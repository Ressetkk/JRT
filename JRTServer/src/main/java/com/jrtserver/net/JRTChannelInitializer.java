package com.jrtserver.net;

import com.jrtserver.util.IDGenerator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class JRTChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ConcurrentHashMap<String, Channel> clientsMap;

    public JRTChannelInitializer(ConcurrentHashMap<String, Channel> clientsMap) {
        this.clientsMap = clientsMap;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new StringDecoder())
                .addLast(new StringEncoder())
                .addLast("mainLogic", new JRTServerHandler(clientsMap));
    }

}
