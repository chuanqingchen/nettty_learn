package com.example.java.nio.transferobject;

import com.example.java.entity.SomeEntity;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by ycwu on 2017/7/19.
 */
@Slf4j
public class NioTransferServer {

    public static final int PORT = 5500;

    public static void main(String[] args) {

        ExecutorService acceptorPool = Executors.newCachedThreadPool();
        ExecutorService handlerPool = Executors.newCachedThreadPool();

        try (Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ) {
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            int count;
            while ((count = selector.select()) > -1) {
                if (count == 0) {
                    continue;
                }

                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeySet.iterator();

                while (iterator.hasNext()) {
                    SelectionKey selectedKey = iterator.next();
                    iterator.remove();

                    if (selectedKey.isAcceptable()) {
                        ServerSocketChannel acceptor = (ServerSocketChannel) selectedKey
                            .channel();
                        acceptorPool
                            .submit(new TransferAcceptorThread(acceptor.accept(), handlerPool));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Slf4j
    @AllArgsConstructor
    static class TransferEventHandlerThread implements Runnable {

        private SocketChannel socketChannel;
        private SelectionKey key;

        @Override
        public void run() {

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                1024);) {
                int totalRead = 0;
                int targetSize = -1;
                ByteBuffer buffer = ByteBuffer.allocate(1024);

                if (targetSize == -1) {
                    log.info("start receiving object...");

                    // first round, get total size first
                    socketChannel.read(buffer);
                    buffer.flip();

                    // first round
                    targetSize = buffer.getInt();
                    log.info("target object size={}", targetSize);

                    while (buffer.hasRemaining()) {
                        byteArrayOutputStream.write(buffer.get());
                        totalRead++;
                    }
                    log.info("read object size={}", totalRead);

                    buffer.clear();
                }

                while (true) {
                    int read = socketChannel.read(buffer);
                    if (read == 0) {
                        continue;
                    }
                    if (read < 0) {
                        key.cancel();
                        break;
                    }

                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        byteArrayOutputStream.write(buffer.get());
                        totalRead++;
                    }
                    log.info("read object size={}", totalRead);
                    buffer.clear();

                    if (totalRead == targetSize) {
                        key.cancel();
                        break;
                    }
                }

                if (totalRead == targetSize) {
                    byteArrayOutputStream.flush();
                    try (ObjectInputStream objectInputStream = new ObjectInputStream(
                        new ByteArrayInputStream(
                            byteArrayOutputStream.toByteArray()))) {
                        SomeEntity someEntity = (SomeEntity) objectInputStream
                            .readObject();
                        log.info("the object is {}", someEntity);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    log.info("waiting more..");
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Slf4j
    @AllArgsConstructor
    static class TransferAcceptorThread implements Runnable {

        private SocketChannel socketChannel;
        private ExecutorService handlerPool;

        @Override
        public void run() {
            try (Selector selector = Selector.open();) {
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);

                int count;
                while ((count = selector.select()) > -1) {
                    if (count == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> iterator = selector.selectedKeys()
                        .iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if (key.isReadable() && key.isValid()) {
                            key.interestOps(0);
                            handlerPool.submit(
                                new TransferEventHandlerThread((SocketChannel) key.channel(), key));
                        }
                    }
                }
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
