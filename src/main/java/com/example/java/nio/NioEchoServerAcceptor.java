package com.example.java.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Administrator on 2017/7/18 0018.
 */
@Slf4j
@AllArgsConstructor
public class NioEchoServerAcceptor implements Runnable {

    private SocketChannel socketChannel;
    private ExecutorService handlerPool;

    @Override
    public void run() {
        try (Selector selector = Selector.open()
        ) {
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);

            while (selector.select() > 0) {
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        log.info("submit handler");
                        handlerPool.submit(new NioEchoServerHandler((SocketChannel) key.channel()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
