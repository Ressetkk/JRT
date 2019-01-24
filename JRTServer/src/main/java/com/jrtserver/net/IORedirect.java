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
        String[] out = ((String) msg).split("\\|");
        if (out[0].equals("disconnect")) {
            ctx.channel().pipeline().remove(this);
            redirectChannel.pipeline().remove("io");
            System.out.println(ctx.channel().pipeline().names());
            System.out.println(redirectChannel.pipeline().names());
        }
        redirectChannel.writeAndFlush(msg);
    }
}
