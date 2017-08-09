package com.example.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by ycwu on 2017/8/8.
 */
@Slf4j
public class HeartBeatHandler extends ChannelDuplexHandler {

    private static final ByteBuf HEART_BEAT_MSG = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("heartbeat".getBytes()));

    private int counter = 0;
    private int maxIdleCounter = 5;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // reset counter if has read event
        counter = 0;
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // reset counter if has write event
        counter = 0;
        super.write(ctx, msg, promise);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {

            if (counter > maxIdleCounter) {
                log.error("max heart beat counter reached, disconnect from channel id={}", ctx.channel().id());
                ctx.channel().close().sync();
            }

            counter++;

            IdleStateEvent e = (IdleStateEvent) evt;
            IdleState state = e.state();
            log.info("channel id={}, idle type:{}", ctx.channel().id(), state);

            ctx.writeAndFlush(HEART_BEAT_MSG);
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }
}
