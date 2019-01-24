package com.jrtserver.app;

import com.jrtserver.net.JRTChannelInitializer;
import com.jrtserver.net.JRTServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Main {

    public static void main(String[] args) {

        System.out.println("Start Server Socket");
        try {
            new JRTServer(2137).startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
