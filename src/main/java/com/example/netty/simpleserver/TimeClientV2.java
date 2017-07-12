package com.example.netty.simpleserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Administrator on 2017/7/11 0011.
 */
@Slf4j
public class TimeClientV2 {

    public static void main(String[] args) {
        int port = 7000;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(
                new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new TimeClientHandlerV2());
                    }
                }).option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.connect("localhost", port).sync();
            log.info("client starts");

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    /**
     * let the handler count read bytes
     */
    @Slf4j
    static class TimeClientHandlerV2 extends ChannelInboundHandlerAdapter {

        private ByteBuf buf;

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            buf = ctx.alloc().buffer(4);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            buf.release();
            buf = null;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf m = (ByteBuf) msg;
            buf.writeBytes(m);
            m.release();

            if (buf.readableBytes() >= 4) {
                long currentTimeMillis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
                log.info("{}", new Date(currentTimeMillis));
                ctx.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
