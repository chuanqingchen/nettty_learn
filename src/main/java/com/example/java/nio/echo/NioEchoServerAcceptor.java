package com.example.java.nio.echo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;

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

            int count;
            while ((count = selector.select()) >= 0) {
                // select again if no keys return
                if (count == 0) {
                    continue;
                }

                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (key.isReadable() && key.isValid()) {
                        log.info("submit handler");
                        handlerPool.submit(new NioEchoServerHandler((SocketChannel) key.channel(), key));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
