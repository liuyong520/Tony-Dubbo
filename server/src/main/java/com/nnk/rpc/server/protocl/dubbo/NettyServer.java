package com.nnk.rpc.server.protocl.dubbo;

import com.nnk.rpc.server.protocl.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer implements RpcServer {

    public void start(String hostName, int port) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup wrokeGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,wrokeGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new NettyChannelInitializer());
        try {
            System.out.println("Netty Server start listering host:" + hostName + "port:" + port);
            ChannelFuture channelFuture = serverBootstrap.bind(hostName,port).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            wrokeGroup.shutdownGracefully();
        }

    }
}
