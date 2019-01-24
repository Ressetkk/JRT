package com.jrtclient.net;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
import netscape.javascript.JSObject;

public class TerminalIncomingMessagesHandler extends ChannelInboundHandlerAdapter {
    private JSObject terminalIO;
    public TerminalIncomingMessagesHandler(JSObject terminalIO) {
        this.terminalIO = terminalIO;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Platform.runLater(() -> terminalIO.call("print", msg.toString()));
    }
}
