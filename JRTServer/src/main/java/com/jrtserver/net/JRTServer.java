package com.jrtserver.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.concurrent.ConcurrentHashMap;

public class JRTServer {

    private final int port;
    private final ConcurrentHashMap<String, Channel> clientsMap;
    private final SslContext sslCtx;

    public JRTServer(int port) {
        SslContext sslCtx1 = null;
        this.port = port;
        this.clientsMap = new ConcurrentHashMap<>();

        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx1 = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                    .build();
        } catch (CertificateException e) {
            sslCtx1 = null;
        } catch (SSLException e) {
            e.printStackTrace();
        }
        sslCtx = sslCtx1;
    }

    public void startServer() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new JRTChannelInitializer(clientsMap, sslCtx))
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
            ;
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            channelFuture.channel().closeFuture().sync();
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
