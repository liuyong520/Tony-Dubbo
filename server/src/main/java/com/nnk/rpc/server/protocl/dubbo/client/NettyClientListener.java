package com.nnk.rpc.server.protocl.dubbo.client;

import io.netty.channel.ChannelHandlerContext;

public interface NettyClientListener {
    void channelRead(ChannelHandlerContext ctx, Object message);
}
