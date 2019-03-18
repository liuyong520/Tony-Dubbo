package com.nnk.rpc.client.proxy;

import com.nnk.rpc.api.entity.Invocation;
import com.nnk.rpc.api.entity.URL;
import com.nnk.rpc.register.RegisterType;
import com.nnk.rpc.register.RemoteRegister;
import com.nnk.rpc.register.factory.RemoteRegisterFactory;
import com.nnk.rpc.server.protocl.Protocl;
import com.nnk.rpc.server.protocl.ProtoclFactory;
import com.nnk.rpc.server.protocl.ProtoclType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory {

    public static <T> T getProxy(final ProtoclType protoclType ,final RegisterType registerType, final Class interfaceClass){
       return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
           public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
               Protocl protocl = ProtoclFactory.getProtocl(protoclType);
               Invocation invocation = new Invocation(interfaceClass.getName(),method.getName(),method.getParameterTypes(),args);
               RemoteRegister remoteRegister = RemoteRegisterFactory.getRemoteRegister(registerType);
               URL radomURL = remoteRegister.getRadomURL(interfaceClass.getName());
               System.out.println("调用地址host:"+ radomURL.getHost()+ ",port:"+radomURL.getPort());
               return protocl.invokeProtocl(radomURL,invocation);
           }
       });
    }
}
