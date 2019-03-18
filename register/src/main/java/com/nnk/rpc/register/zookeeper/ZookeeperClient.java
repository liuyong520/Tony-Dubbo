package com.nnk.rpc.register.zookeeper;

import com.alibaba.fastjson.JSONObject;
import com.nnk.rpc.api.entity.URL;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

public class ZookeeperClient {
    private CuratorFramework client;
    private static String zkServerIps="127.0.0.1:4180,127.0.0.1:4182,127.0.0.1:4181";
    private Builder builder;

    public Builder custom(){
        if(builder==null){
            builder = new Builder();
        }
        return builder;
    }

    public ZookeeperClient(String zkServerIps) {
         client = this.custom().setZkServerIps(zkServerIps).build();
         client.start();
    }

    public ZookeeperClient() {
        this(zkServerIps);
    }

    public void closezkclient(){
        if(client!=null){
            this.client.close();
        }
    }

    /**
     *
     * @return
     */
    public boolean started(){
        return client==null?false:client.isStarted();
    }

    /**
     * 创建节点
     * @param nodePath
     * @param createSubNode 节点不存在是否创建
     * @return
     */
    public void createNodePath(String nodePath,String data) throws Exception {
        //创建谁都能能够访问的临时节点
        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(nodePath,data.getBytes());

    }

    public void deleteNodePath(String nodePath) throws Exception {
        Stat stat = this.client.checkExists().forPath(nodePath);
        if(stat != null){
            client.delete().guaranteed().deletingChildrenIfNeeded().forPath(nodePath);
        }
    }

    public List<URL> getChildNodes(String nodePath) throws Exception {
        List<String> strings = this.client.getChildren().forPath(nodePath);
        ArrayList urls = new ArrayList();
        for (String string : strings) {
            urls.add(JSONObject.parseObject(string,URL.class));
        }
        return urls;
    }


    /**
     * 构造器
     */
    class Builder{
        private String zkServerIps;
        private RetryPolicy retryPolicy;

        public String getZkServerIps() {
            return zkServerIps;
        }

        public Builder setZkServerIps(String zkServerIps) {
            this.zkServerIps = zkServerIps;
            return this;
        }

        public RetryPolicy getRetryPolicy() {
            return retryPolicy;
        }

        public Builder setRetryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }
        public CuratorFramework build(){
            if(builder!=null){
                builder = new Builder();
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
                builder.setRetryPolicy(retryPolicy);
                builder.setZkServerIps(zkServerIps);
            }else{
                if(builder.getRetryPolicy()==null){
                    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
                    builder.setRetryPolicy(retryPolicy);
                }
                if(builder.getZkServerIps()==null){
                    builder.setZkServerIps(zkServerIps);
                }
            }
            CuratorFramework client = CuratorFrameworkFactory.builder()
                    .connectString(builder.getZkServerIps()).connectionTimeoutMs(10000)
                    .sessionTimeoutMs(10000).retryPolicy(builder.getRetryPolicy())
                    .namespace("dubbo").build();
            return client;
        }
    }
}
