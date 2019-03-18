package com.nnk.rpc.server.provide;

import com.nnk.rpc.api.HelloService;
import com.nnk.rpc.api.entity.URL;
import com.nnk.rpc.register.LocalRegister;
import com.nnk.rpc.register.RegisterType;
import com.nnk.rpc.register.RemoteRegister;
import com.nnk.rpc.register.factory.LocalRegisterFactory;
import com.nnk.rpc.register.factory.RemoteRegisterFactory;
import com.nnk.rpc.server.protocl.Protocl;
import com.nnk.rpc.server.protocl.ProtoclFactory;
import com.nnk.rpc.server.protocl.ProtoclType;

public class Provider2 {
    public static void main(String[] args) {
        URL url = new URL("localhost",8022);
        //远程服务注册地址
        RemoteRegister register = RemoteRegisterFactory.getRemoteRegister(RegisterType.ZOOKEEPER);
        register.register(HelloService.class.getName(),url);

        //本地注册服务的实现类
        LocalRegister localRegister = LocalRegisterFactory.getLocalRegister(RegisterType.LOCAL);
        localRegister.register(HelloService.class.getName(),HelloServiceImpl.class);

        Protocl protocl = ProtoclFactory.getProtocl(ProtoclType.NETTY);
        protocl.start(url);
    }
}
