package com.example.java.nio.transferobject;

import com.example.java.entity.SomeEntity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by ycwu on 2017/7/20.
 */
@Slf4j
@AllArgsConstructor
public class NioTransferClient implements Runnable {

    private String serverHost;
    private int serverPort;
    private SomeEntity someEntity;

    @Override
    public void run() {
        try (SocketChannel socketChannel = SocketChannel
            .open(new InetSocketAddress(serverHost, serverPort));
            Selector selector = Selector.open();
        ) {
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_WRITE);

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            byte[] array = objectToByteArray(someEntity);

            // first put target object size
            buffer.putInt(array.length);

            int totalRead = 0;
            int count;
            while ((count = selector.select()) > -1) {
                if (count == 0) {
                    continue;
                }

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isWritable()) {
                        totalRead = doTransfer(socketChannel, buffer, array, totalRead);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] objectToByteArray(SomeEntity someEntity) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        ) {
            objectOutputStream.writeObject(someEntity);
            objectOutputStream.flush();
            byte[] array = byteArrayOutputStream.toByteArray();
            log.info("target send object size={}", array.length);
            return array;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int doTransfer(SocketChannel socketChannel, ByteBuffer buffer, byte[] array,
        int totalRead)
        throws IOException {
        while (totalRead < array.length) {
            while (buffer.hasRemaining() && totalRead < array.length) {
                buffer.put(array[totalRead++]);
            }
            buffer.flip();
            int actualWrite;
            // since NIO, SocketChannel.write(ByteBuffer) returns right away !!!
            do {
                actualWrite = socketChannel.write(buffer);
            } while (actualWrite == 0);

            log.info("send object size={}", totalRead);
            buffer.clear();
        }
        return totalRead;
    }

}