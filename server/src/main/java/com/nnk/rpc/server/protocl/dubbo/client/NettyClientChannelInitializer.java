package com.nnk.rpc.server.protocl.dubbo.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private NettyClientListener listener;

    public NettyClientChannelInitializer(NettyClientListener listener) {
        this.listener = listener;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
        pipeline.addLast("encoder",new ObjectEncoder());
        pipeline.addLast("handler",new NettyClientChannelHanlder(listener));
    }
}
