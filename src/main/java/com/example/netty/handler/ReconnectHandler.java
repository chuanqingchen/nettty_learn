package com.example.netty.handler;

import com.example.netty.simpleserver.EchoClientV3;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by ycwu on 2017/8/9.
 */
@Slf4j
public class ReconnectHandler extends ChannelInboundHandlerAdapter {

    private EchoClientV3 client;

    public ReconnectHandler(EchoClientV3 client) {
        this.client = client;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("reconnecting");
        final EventLoopGroup eventLoopGroup = ctx.channel().eventLoop();

        eventLoopGroup.schedule(() -> {
            client.doConnect(new Bootstrap(), eventLoopGroup);
        }, 2L, TimeUnit.SECONDS);

        super.channelInactive(ctx);
    }
}
