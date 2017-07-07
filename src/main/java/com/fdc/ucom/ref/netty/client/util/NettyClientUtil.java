package com.fdc.ucom.ref.netty.client.util;

import com.fdc.ucom.ref.netty.client.config.NettyClientPropertyConfiguration;
import com.fdc.ucom.ref.netty.client.service.NettyClientGenericMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;


/**
 * Created by fbqgez6 on 7/6/17.
 */
@Component
public class NettyClientUtil {

    private static final Logger LOGGER = Logger.getLogger(NettyClientUtil.class);

    public static final ChannelGroup DEFAULT_CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Autowired
    private NettyClientPropertyConfiguration config;

    @Autowired
    private NettyClientGenericMessageHandler messageHandler;

    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;

    public void initializeNettyClient() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                StringEncoder stringEncoder = new StringEncoder();

                LengthFieldPrepender lengthFieldPrepender = new LengthFieldPrepender(4);
                LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder = new LengthFieldBasedFrameDecoder(
                        Integer.MAX_VALUE, 0, 4, 0, 4);
                StringDecoder stringDecoder = new StringDecoder();

                channel.pipeline().addLast(stringEncoder).addLast(lengthFieldPrepender).addLast(lengthFieldBasedFrameDecoder)
                        .addLast(stringDecoder).addLast(messageHandler);
            }
        });
    }

    public Channel createNettyChannel() {

        String host = config.getClient().getHost();
        int port = config.getClient().getPort();

        InetSocketAddress address = new InetSocketAddress(host, port);

        ChannelFuture channelFuture = bootstrap.connect(address);

        Channel channel = channelFuture.awaitUninterruptibly().channel();

        LOGGER.info("Successfully connected to host : [" + host + "] on port [" + port + "] with success: [" + channelFuture.isSuccess() + "]");
        LOGGER.info("Created a new channel with isActive = [" + channel.isActive() + "]");

        getClientChannelGroup().add(channel);

        return channel;
    }


    public static ChannelGroup getClientChannelGroup() {
        return DEFAULT_CHANNEL_GROUP;
    }



    /*public void initialize() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            p.addLast(new NettyClientMessageHandler());
                        }
                    });

            String host = config.getClient().getHost();
            int port = config.getClient().getPort();
            InetSocketAddress address = new InetSocketAddress(host, port);
            ChannelFuture connection = b.connect(address);

            System.out.println("Successfully connected to: " + address + " with success: " + connection.isSuccess());

            ChannelFuture f = connection.sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }*/
}
