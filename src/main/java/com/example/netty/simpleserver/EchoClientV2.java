package com.example.netty.simpleserver;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * Created by ycwu on 2017/8/8.
 */
@Slf4j
public class EchoClientV2 {

    private static final String COMMAND_EXIT = "exit";
    private static final int MAX_LENGTH = 1024;

    public static void main(String[] args) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Scanner scanner = new Scanner(System.in);
        try {
            Bootstrap b = new Bootstrap();
            b.channel(NioSocketChannel.class).group(workerGroup).handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    pipeline.addLast(new LineBasedFrameDecoder(1024));
                    pipeline.addLast(new StringDecoder());

                    pipeline.addLast(new EchoClientHandler());
                }
            }).option(ChannelOption.SO_KEEPALIVE, true);

            Channel channel = b.connect(new InetSocketAddress("localhost", EchoServer.PORT)).sync().channel();

            log.info("client starts ok");
            String line;
            while (true) {
                line = scanner.nextLine();
                if (line == null || "".equals(line)) {
                    continue;
                }

                if (COMMAND_EXIT.equals(line)) {
                    log.info("exit command found");
                    break;
                }

                if (line.length() >= MAX_LENGTH) {
                    log.error("message too large, discard");
                }

                channel.writeAndFlush(Unpooled.wrappedBuffer((line + "\r").getBytes()));
                log.info("send: {}", line);

            }

            channel.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
            workerGroup.shutdownGracefully();
        }
    }

    static class EchoClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            log.info("receive: {}", msg);
        }
    }

}
