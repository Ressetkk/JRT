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
        String[] out = ((String) msg).split("\\|");
        // command for this is: resizeWindow|cols|rows|
        if (out[0].equals("resizeWindow")) {
            process.resizeShell(Integer.parseInt(out[1]), Integer.parseInt(out[2]));
        }

        else if (out[0].equals("disconnect")) {
            // destroy PTY process
            System.out.println("killing process");
            process.disconnect();

            // commit suicide
            ctx.channel().pipeline().remove(this);
        }
        else {
            process.command((String) msg);
        }
    }
}
