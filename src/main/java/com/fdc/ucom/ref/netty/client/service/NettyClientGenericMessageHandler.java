package com.fdc.ucom.ref.netty.client.service;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Created by Omar.Gaye - fbqgez6 on 7/2/17.
 */
@Component
@ChannelHandler.Sharable
public class NettyClientGenericMessageHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger LOGGER = Logger.getLogger(NettyClientGenericMessageHandler.class);
    private ChannelHandlerContext context;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        LOGGER.info("Message Received :-     " + message);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.context = ctx;
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void sendMessage(String msgToSend) {
        if (context != null) {
            // ChannelFuture cf = context.writeAndFlush(Unpooled.copiedBuffer(msgToSend, CharsetUtil.UTF_8));
            ChannelFuture cf = context.writeAndFlush(Unpooled.copiedBuffer(msgToSend.getBytes()));
            if (!cf.isSuccess()) {
                LOGGER.error("*** Send message failed: " + cf.cause());
            }
        } else {
            LOGGER.error("*** Channel Handler Context not yet initialized!");
            //context not initialized yet. We are a bit to fast, sleep some time
        }
    }

    public ChannelFuture sendSyncMessage(String msgToSend) {
        ChannelFuture cf = null;
        if (context != null) {
  /*          ChannelFuture cf2 = context.writeAndFlush(Unpooled.copiedBuffer(msgToSend+ "\r\n", CharsetUtil.UTF_8));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignore) {
            }

            cf = context.writeAndFlush(Unpooled.copiedBuffer(msgToSend.getBytes())).addListener(genericChannelFutureListener);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignore) {
            }*/

            cf = context.channel().writeAndFlush(Unpooled.copiedBuffer(msgToSend.getBytes()))
                    .addListener(genericChannelFutureListener);
            if (!cf.isSuccess()) {
                LOGGER.error("*** Send message failed: " + cf.cause());
            }
        } else {
            LOGGER.error("*** Channel Handler Context not yet initialized!");
            //context not initialized yet. We are a bit to fast, sleep some time
        }
        return cf;

    }


    private final ChannelFutureListener genericChannelFutureListener = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
            if (future.isSuccess()) {
                //Do whatever you want is case of success
                LOGGER.error("*** From Listener Call was successfull");
                LOGGER.error(context.read());
            } else {
                future.cause().printStackTrace();
                future.channel().close();
            }
        }
    };


}