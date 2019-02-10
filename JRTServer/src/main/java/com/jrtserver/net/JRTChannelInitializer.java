package com.jrtserver.net;

import com.jrtserver.util.IDGenerator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class JRTChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ConcurrentHashMap<String, Channel> clientsMap;
    private final SslContext sslCtx;

    public JRTChannelInitializer(ConcurrentHashMap<String, Channel> clientsMap, SslContext sslCtx) {
        this.clientsMap = clientsMap;
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        if (sslCtx != null) {
            ch.pipeline()
                    .addLast(sslCtx.newHandler(ch.alloc()))
                    .addLast(new StringDecoder())
                    .addLast(new StringEncoder())
                    .addLast("mainLogic", new JRTServerHandler(clientsMap));
        } else {
            ch.pipeline()
                    .addLast(new StringDecoder())
                    .addLast(new StringEncoder())
                    .addLast("mainLogic", new JRTServerHandler(clientsMap));
        }
    }

}
