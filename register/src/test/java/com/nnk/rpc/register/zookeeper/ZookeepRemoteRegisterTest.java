package com.nnk.rpc.register.zookeeper;

import com.nnk.rpc.api.HelloService;
import com.nnk.rpc.api.entity.URL;
import org.junit.Before;

import static org.junit.Assert.*;

public class ZookeepRemoteRegisterTest {
    ZookeepRemoteRegister remoteRegister;
    @Before
    public void setUp() throws Exception {
        remoteRegister = new ZookeepRemoteRegister(new ZookeeperClient());
    }

    @org.junit.Test
    public void register() {

        remoteRegister.register(HelloService.class.getName(),new URL("localhost",8021));
    }

    @org.junit.Test
    public void getRadomURL() {
        register();
        remoteRegister.getRadomURL(HelloService.class.getName());
    }
}