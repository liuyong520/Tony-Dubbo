package com.nnk.rpc.server.protocl.dubbo.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyClientChannelHanlder extends ChannelInboundHandlerAdapter {
    private NettyClientListener listener;

    public NettyClientChannelHanlder(NettyClientListener listener) {
        this.listener = listener;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        listener.channelRead(ctx,msg);
    }
}
