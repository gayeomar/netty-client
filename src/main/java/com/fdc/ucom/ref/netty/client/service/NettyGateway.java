package com.fdc.ucom.ref.netty.client.service;

import com.fdc.ucom.ref.netty.client.util.NettyClientUtil;
import io.netty.channel.ChannelFuture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * Created by fbqgez6 on 7/6/17.
 */
@Component
public class NettyGateway {
    private static final Logger LOGGER = Logger.getLogger(NettyGateway.class);


    @Autowired
    private NettyClientUtil nettyUtil;

    @Autowired
    private NettyClientGenericMessageHandler messageHandler;

    public Future<ChannelFuture> sendMessageToClientSocket(String message) {
        ChannelFuture response = null;

        nettyUtil.initializeNettyClient();

        nettyUtil.createNettyChannel();

        LOGGER.info(" About to async call");
        response = messageHandler.sendSyncMessage(message);
        LOGGER.info("returning from async call");

        return new AsyncResult<ChannelFuture>(response);
    }
}
