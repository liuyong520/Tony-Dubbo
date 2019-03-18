package com.nnk.rpc.client.comsummer;

import com.nnk.rpc.api.HelloService;
import com.nnk.rpc.client.proxy.ProxyFactory;
import com.nnk.rpc.register.RegisterType;
import com.nnk.rpc.server.protocl.ProtoclType;

public class Consumer {
    public static void main(String[] args) {
        HelloService helloService = ProxyFactory.getProxy(ProtoclType.HTTP, RegisterType.ZOOKEEPER,HelloService.class);
        String result = helloService.sayHello("liuy");
        System.out.println(result);
    }
}
