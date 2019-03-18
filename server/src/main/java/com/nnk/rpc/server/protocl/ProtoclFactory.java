package com.nnk.rpc.server.protocl;

import com.nnk.rpc.server.protocl.dubbo.NettyProtocl;
import com.nnk.rpc.server.protocl.http.HttpProtocl;

public class ProtoclFactory {
    private static NettyProtocl nettyProtocl = new NettyProtocl();
    private static HttpProtocl httpProtocl = new HttpProtocl();
    public static Protocl getProtocl(ProtoclType protoclType){
        switch (protoclType){
            case HTTP: return httpProtocl;
            case NETTY: return nettyProtocl;
            default:return null;
        }
    }
}
