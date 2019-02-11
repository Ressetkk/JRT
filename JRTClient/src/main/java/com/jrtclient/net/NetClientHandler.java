package com.jrtclient.net;

import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

import java.net.InetSocketAddress;


public class NetClientHandler extends ChannelInboundHandlerAdapter {

    private StringProperty hostID;
    private BooleanProperty remoteAuth;

    public NetClientHandler(StringProperty hostID, BooleanProperty remoteAuth) {
        this.hostID = hostID;
        this.remoteAuth = remoteAuth;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String[] out = ((String) msg).split("\\|");
        if (out[0].equals("yourid")) {
            AuthHelper.setId(out[1]);
            hostID.setValue(out[1]);
        }
        else if (out[0].equals("connect")) {
            boolean auth = AuthHelper.Authenticate(out[2], out[3]);
            ctx.writeAndFlush("auth|"+ out[1] + "|" + out[2] + "|" + auth);
            System.out.println(auth);
            if (auth) {
                // logic after succesful authentication. Start local shell and forward all IO to remote


            }

        }
        else if (out[0].equals("auth")) {
            if (out[3].equals("true")) {
                System.out.println("authenticated");
                remoteAuth.setValue(true);
                // logic to send commands that are parsed from terminal IO

            }
            else {
                System.out.println("not authenticated");
                remoteAuth.setValue(false);
            }
        }
        else if (out[0].equals("terminalReady")) {
            System.out.println("terminalReady");
            ctx.channel().pipeline().addBefore("mainLogic", "HostShellHandler", new HostShellHandler(ctx));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
