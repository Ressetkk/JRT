package com.jrtserver.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class IORedirect extends ChannelInboundHandlerAdapter {
    private Channel redirectChannel;

    public IORedirect(Channel redirectChannel) {
        this.redirectChannel = redirectChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        redirectChannel.writeAndFlush(msg);
    }
}
