# dubbo 简单介绍
dubbo 是阿里巴巴开源的一款分布式rpc框架。

## 为什么手写实现一下bubbo？

很简单，最近从公司离职了，为了复习一下dubbo原理相关的知识，决定自己手写实现一个tony的dubbo，然后再结合dubbo的源码已达到复习的目的。

## 什么是RPC？
rpc 简单的说就是远程调用，以API的方式调用远程的服务器上的方法，像调本地方法一样！

创建一个api的包模块，供服务端和消费者端共同使用。

## 接口抽象
```java
package com.nnk.rpc.api;

public interface HelloService {
    /**
     * 接口服务
     * @param name
     * @return
     */
    String sayHello(String name);
}
```

## 服务端实现

服务端server端要实现这个接口。同时要发布这个接口，何谓发布这个接口？其实就是要像注册中心注册一下这个服务。这样，消费者在远程调用的时候可以通过注册中心注册的信息能够感知到服务。
服务的实现：
```java
package com.nnk.rpc.server.provide;

import com.nnk.rpc.api.HelloService;

public class HelloServiceImpl implements HelloService {

    public String sayHello(String name) {
        System.out.println("hello," + name);
        return "hello " + name;
    }
}
```

服务端抽象：
```
package com.nnk.rpc.server.protocl;

/**
 * 服务端server
 */
public interface RpcServer {
    /**
     * 开启服务 监听hostName：port
     * @param hostName
     * @param port
     */
    public void start(String hostName,int port);

}
```
http协议的RPCServer实现
```java
package com.nnk.rpc.server.protocl.http;

import com.nnk.rpc.server.protocl.RpcServer;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;

public class HttpServer implements RpcServer {

    public void start(String hostName,int port){
        Tomcat tomcat = new Tomcat();
        Server server = tomcat.getServer();
        Service service = server.findService("Tomcat");
        Connector connector = new Connector();
        connector.setPort(port);
        Engine engine = new StandardEngine();
        engine.setDefaultHost(hostName);

        Host host = new StandardHost();
        host.setName(hostName);
        //设置上下文
        String contextPath="";
        Context context = new StandardContext();
        context.setPath(contextPath);
        context.addLifecycleListener(new Tomcat.FixContextListener());

        host.addChild(context);
        engine.addChild(host);

        service.setContainer(engine);
        service.addConnector(connector);
        //设置拦截servlet
        tomcat.addServlet(contextPath,"dispather",new DispatcherServlet());
        context.addServletMappingDecoded("/*","dispather");
        try {
            //启动tomcat
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}

```
启动了tomcat并用到DispatcherServlet来拦截我们的请求。

```java
package com.nnk.rpc.server.protocl.http;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 这个代码大家应该很熟悉吧，这个是sevlet的基本知识。
 * 任何请求被进来都会被这个sevlet处理
 */
public class DispatcherServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //把所有的请求交给HttpHandler接口处理
        new HttpHandler().handler(req,resp);
    }
}

```
再看一下HttpHandler类：
```
package com.nnk.rpc.server.protocl.http;

import com.nnk.rpc.api.entity.Invocation;

import com.nnk.rpc.register.RegisterType;
import com.nnk.rpc.register.factory.LocalRegisterFactory;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HttpHandler {

    public void handler(HttpServletRequest req, HttpServletResponse resp){
        // 获取对象
        try {
            //从流里面获取数据
            InputStream is = req.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(is);
            //从流中读取数据反序列话成实体类。
            Invocation invocation = (Invocation) objectInputStream.readObject();
            //拿到服务的名字
            String interfaceName = invocation.getInterfaceName();
            //从注册中心里面拿到接口的实现类
            Class interfaceImplClass = LocalRegisterFactory.getLocalRegister(RegisterType.LOCAL).get(interfaceName);
            //获取类的方法
            Method method = interfaceImplClass.getMethod(invocation.getMethodName(),invocation.getParamtypes());
            //反射调用方法
            String result = (String) method.invoke(interfaceImplClass.newInstance(),invocation.getObjects());
            //把结果返回给调用者
            IOUtils.write(result,resp.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
```
我们看看Invocation的实现：
```java
package com.nnk.rpc.api.entity;

import java.io.Serializable;

public class Invocation implements Serializable {

    private String interfaceName;
    private String methodName;
    private Class[] paramtypes;
    private Object[] objects;

    /**
     * 
     * @param interfaceName 接口名字
     * @param methodName 方法名字
     * @param paramtypes 参数类型列表
     * @param objects 参数列表
     */
    public Invocation(String interfaceName, String methodName, Class[] paramtypes, Object[] objects) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.paramtypes = paramtypes;
        this.objects = objects;
    }

   .... get set 方法省略掉
}
```
到这里服务端先告一段落下面实现一下注册中心
## 注册中心
接口抽象：
```java
package com.nnk.rpc.register;

public interface LocalRegister {
    /**
     *
     * @param interfaceName 接口名称
     * @param interfaceImplClass 接口实现类
     */
    void register(String interfaceName,Class interfaceImplClass);

    /**
     * 获取实现类
     * @param interfaceName
     * @return
     */
    Class get(String interfaceName);
}
```
LocalRegister 这个主要是供服务端自己在反射调用的时候根据服务名称找到对应的实现。
```java
package com.nnk.rpc.register;

import com.nnk.rpc.api.entity.URL;

public interface RemoteRegister {
    /**
     * 注册到远程注册中心
     * @param interfaceName
     * @param host
     */
    void register(String interfaceName, URL host);

    /**
     * 根据服务名称获取调用者的地址信息
     * @param interfaceName
     * @return
     */
    URL getRadomURL(String interfaceName);
}
```
这个主要是供消费者端根据服务名字找对应的地址发起远程调用用的。

我们分别来看看这两个接口的实现：

```java

package com.nnk.rpc.register.local;



import com.nnk.rpc.register.LocalRegister;

import java.util.HashMap;
import java.util.Map;

public class LocalMapRegister implements LocalRegister {
    private Map<String, Class> registerMap = new HashMap<String,Class>(1024);
    public void register(String interfaceName, Class interfaceImplClass) {
        registerMap.put(interfaceName,interfaceImplClass);
    }

    public Class get(String interfaceName) {
        return registerMap.get(interfaceName);
    }
}

```

很简单就是写在缓存里，map存储。
```java
package com.nnk.rpc.register.local;

import com.nnk.rpc.api.entity.URL;
import com.nnk.rpc.register.RemoteRegister;

import java.io.*;
import java.util.*;

public class RemoterMapRegister implements RemoteRegister {
    private Map<String, List<URL>> registerMap = new HashMap<String,List<URL>>(1024);
    public static final String path = "/data/register";
    public void register(String interfaceName, URL host) {
        if(registerMap.containsKey(interfaceName)){
            List<URL> list = registerMap.get(interfaceName);
            list.add(host);
        }else {
            List<URL> list = new LinkedList<URL>();
            list.add(host);
            registerMap.put(interfaceName,list);
        }
        try {
            saveFile(path,registerMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public URL getRadomURL(String interfaceName) {
        try {
            registerMap = (Map<String, List<URL>>) readFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        List<URL> list = registerMap.get(interfaceName);
        Random random = new Random();
        int i = random.nextInt(list.size());
        return list.get(i);
    }

    /**
     * 写入文件
     * @param path
     * @param object
     * @throws IOException
     */
    private void saveFile(String path,Object object) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
        ObjectOutputStream objectOutputStream =new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(object);
    }

    /**
     * 从文件中读取
     * @param path
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Object readFile(String path) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
        return inputStream.readObject();
    }
}
```
这里为什么要写入文件呢？这是因为如果只存在内存中话，消费者和服务者不是同一个程序，消费者不额能感知到服务者程序内存的变化的。所以只能服务端写入文件，消费者从文件里取才能取得到。
dubbo注册中心怎么干的呢，dubbo只是把这些信息写到了zookeeper，或者redis.或者其他地方。
这里我就不再实现zookeeper的注册中心了。

接下来我们开启服务
```
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


public class Provider {
    public static void main(String[] args) {
            URL url = new URL("localhost",8021);
            //远程服务注册地址
            RemoteRegister register = RemoteRegisterFactory.getRemoteRegister(RegisterType.ZOOKEEPER);
            register.register(HelloService.class.getName(),url);

            //本地注册服务的实现类
            LocalRegister localRegister = LocalRegisterFactory.getLocalRegister(RegisterType.LOCAL);
            localRegister.register(HelloService.class.getName(),HelloServiceImpl.class);
            //这里我又封装了一层协议层，我们都知道dubbo有基于netty的dubbo协议，有基于http的http协议，还有基于redis的redis协议等等。    
            Protocl protocl = ProtoclFactory.getProtocl(ProtoclType.HTTP);
            protocl.start(url);
    }
}

```

## 消费者端：

消费者端其实很简单，就是根据注册中心里的信息远程调用对应服务器上的方法。
```java

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
```

```java
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

```
至此Dubbo的RPC调用核心框架就已经基本实现了。
涉及到的东西其实挺多的，有tomcat的知识（http协议实现），协议的序列化和反序列化（远程调用消息的传递），netty的知识（dubbo协议的实现），动态代理的知识（消费者端实现）。反射（远程调用的核心）。再深入点就是负载均衡算法（在远程获取服务者的地址时可以抽象）。

如果有什么不清楚的地方，欢迎大家留言，咱们可以一起交流讨论。


