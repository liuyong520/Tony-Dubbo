package com.nnk.rpc.server.protocl.dubbo.client;

import com.nnk.rpc.api.entity.Invocation;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CountDownLatch;

public class NettyClient {
    public String send(String hostName, int port , Invocation invocation){
        final CountDownLatch latch = new CountDownLatch(1);
        final Object[] result = {null};
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).handler(new NettyClientChannelInitializer(new NettyClientListener() {
                public void channelRead(ChannelHandlerContext ctx, Object message) {
                    result[0] = message;
                    latch.countDown();

                }
            }));
            Channel channel = bootstrap.connect(hostName,port).sync().channel();
            channel.writeAndFlush(invocation);
            latch.await();
            return (String) result[0];
        }catch (Exception e){

        }finally {
            group.shutdownGracefully();
        }
        return null;
    }

}
