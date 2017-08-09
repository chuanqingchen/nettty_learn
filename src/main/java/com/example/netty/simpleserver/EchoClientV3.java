package com.example.netty.simpleserver;


import com.example.netty.handler.ReconnectHandler;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by ycwu on 2017/8/8.
 */
@Slf4j
public class EchoClientV3 {

    private static final String COMMAND_EXIT = "exit";
    private static final int MAX_LENGTH = 1024;

    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;

    private ChannelFutureListener reconnectListener = new ChannelFutureListener() {

        private int count = 0;

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                count++;
                log.error("try {} times reconnecting....", count);
                final EventLoopGroup eventLoopGroup = future.channel().eventLoop();
                eventLoopGroup.schedule(() -> {
                    doConnect(new Bootstrap(), eventLoopGroup);
                }, 2L, TimeUnit.SECONDS);
            } else {
                log.info("connect successfully");
                count = 0;
                channel = future.channel();
            }
        }
    };


    public void doConnect(Bootstrap b, EventLoopGroup workerGroup) {
        EchoClientV3 client = this;
        if (b != null) {
            b.channel(NioSocketChannel.class).group(workerGroup).handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ReconnectHandler(client));
                    pipeline.addLast(new LineBasedFrameDecoder(1024));
                    pipeline.addLast(new StringDecoder());

                    pipeline.addLast(new EchoClientHandler());
                }
            }).option(ChannelOption.SO_KEEPALIVE, true);
        }

        b.connect(new InetSocketAddress("localhost", EchoServer.PORT)).addListener(reconnectListener);
    }

    private boolean start() {
        int count = 0;
        doConnect(new Bootstrap(), workerGroup);
        while (channel == null || !channel.isActive()) {
            if (count >= 10) {
                log.error("failed to connect server");
                return false;
            }
            count++;

            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void processInput(Scanner scanner) {
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

            try {
                channel.writeAndFlush(Unpooled.wrappedBuffer((line + "\n").getBytes())).sync();
                log.info("send: {}", line);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void run() {
        Scanner scanner = new Scanner(System.in);
        try {
            if (!start()) {
                return;
            }

            log.info("client starts ok");
            processInput(scanner);

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new EchoClientV3().run();
    }

    static class EchoClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            log.info("receive: {}", msg);
        }
    }

}
