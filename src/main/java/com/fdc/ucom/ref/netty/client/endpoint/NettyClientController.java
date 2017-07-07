package com.fdc.ucom.ref.netty.client.endpoint;

import com.fdc.ucom.ref.netty.client.service.NettyGateway;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.Future;

/**
 * Created by fbqgez6  - Omar on 7/6/17.
 */
@RestController
@RequestMapping("/netty/client")
public class NettyClientController {

    @Autowired private NettyGateway gateway;

    @PostMapping
    @ResponseStatus(code = HttpStatus.OK)
    public Future<ChannelFuture> handleRequest(@RequestBody String body) {

        return gateway.sendMessageToClientSocket(body);
    }
}
