package com.jrtclient.net;

import com.jrtclient.shell.ShellProcess;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HostShellHandler extends ChannelInboundHandlerAdapter {
    private ShellProcess process;
    public HostShellHandler(ChannelHandlerContext ctx) throws Exception {
        this.process = new ShellProcess(ctx);
        process.initProcess();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        process.command((String) msg);
    }
}
