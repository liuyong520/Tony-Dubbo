package com.nnk.rpc.server.provide;

import com.nnk.rpc.api.HelloService;

public class HelloServiceImpl implements HelloService {

    public String sayHello(String name) {
        System.out.println("hello," + name);
        return "hello " + name;
    }
}
