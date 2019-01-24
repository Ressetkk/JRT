package com.jrtserver.net;

import com.jrtserver.util.IDGenerator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.concurrent.ConcurrentHashMap;

public class JRTServerHandler extends ChannelInboundHandlerAdapter {

    private final ConcurrentHashMap<String, Channel> clientsMap;

    public JRTServerHandler(ConcurrentHashMap<String, Channel> clientsMap) {
        this.clientsMap = clientsMap;
    }

    public void channelActive(ChannelHandlerContext ctx) {
        Integer id = IDGenerator.nextId();      // Get next usable ID
        System.out.println(id);
        clientsMap.put(id.toString(), ctx.channel());  // add the channel to the Hash Map of connected channels

        // write ID to the client
//        final ByteBuf msg = ctx.alloc().buffer();
        String commandId = "yourid|"+id.toString();
//        msg.write();
        ctx.writeAndFlush(commandId);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        System.out.println("JRTServerHandler");
        System.out.println((String) msg);
//        for (byte m : ((String) msg).getBytes()) {
//            System.out.printf("%02X | ",m);
//        }
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
}
