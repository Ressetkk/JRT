package com.jrtserver.net;

import com.jrtserver.util.IDGenerator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.concurrent.ConcurrentHashMap;

public class JRTServerHandler extends ChannelInboundHandlerAdapter {

    private final ConcurrentHashMap<String, Channel> clientsMap;
    private Integer id;

    public JRTServerHandler(ConcurrentHashMap<String, Channel> clientsMap) {
        this.clientsMap = clientsMap;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        id = IDGenerator.nextId();      // Get next usable ID
        System.out.println(id);
        clientsMap.put(id.toString(), ctx.channel());  // add the channel to the Hash Map of connected channels

        // write ID to the client
        String commandId = "yourid|"+id.toString();
        ctx.writeAndFlush(commandId);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (((String) msg).contains("connect|")) {
            String[] out = ((String)msg).split("\\|");
            Channel redirectChannel = clientsMap.get(out[2]);
            if (redirectChannel == null) {
//                ctx.writeAndFlush("auth|" + out[1] + "|false");
            } else {
                redirectChannel.writeAndFlush(msg);
            }
        } else if (((String) msg).contains("auth|")) {
            String[] out = ((String)msg).split("\\|");
            Channel redirectChannel = clientsMap.get(out[1]);
            if (out[3].equals("true")) {
                ctx.channel().pipeline().addBefore("mainLogic", "io", new IORedirect(redirectChannel));
                redirectChannel.pipeline().addBefore("mainLogic", "io", new IORedirect(ctx.channel()));
            }
            redirectChannel.writeAndFlush(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        clientsMap.remove(id);
        IDGenerator.addReusableId(id);
    }
}
