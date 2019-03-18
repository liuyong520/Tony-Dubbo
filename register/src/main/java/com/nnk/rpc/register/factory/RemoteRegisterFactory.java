package com.nnk.rpc.register.factory;


import com.nnk.rpc.register.RegisterType;
import com.nnk.rpc.register.RemoteRegister;
import com.nnk.rpc.register.local.RemoterMapRegister;
import com.nnk.rpc.register.zookeeper.ZookeepRemoteRegister;
import com.nnk.rpc.register.zookeeper.ZookeeperClient;

public class RemoteRegisterFactory  {
    private static RemoterMapRegister remoterMapRegister = new RemoterMapRegister();
    private static ZookeepRemoteRegister zookeepRemoteRegister = new ZookeepRemoteRegister(new ZookeeperClient());
    public static RemoteRegister getRemoteRegister(RegisterType registerType){
        switch (registerType){
            case LOCAL:return remoterMapRegister;
            case ZOOKEEPER: return zookeepRemoteRegister;
            default:return null;
        }
    }
}
