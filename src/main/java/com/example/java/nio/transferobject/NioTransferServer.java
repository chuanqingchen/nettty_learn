package com.example.java.nio.transferobject;

import com.example.java.entity.SomeEntity;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by ycwu on 2017/7/19.
 */
@Slf4j
public class NioTransferServer {

    public static final int PORT = 5500;

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ) {
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            try (SocketChannel socketChannel = serverSocketChannel.accept();
                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);) {
                log.info("start receiving object...");
                ByteBuffer buffer = ByteBuffer.allocate(1024);

                int totalRead = 0;

                // first round, get total size first
                socketChannel.read(buffer);
                buffer.flip();

                // first round
                int targetSize = buffer.getInt();
                log.info("target object size={}", targetSize);

                while (buffer.hasRemaining()) {
                    byteArrayOutputStream.write(buffer.get());
                    totalRead++;
                }
                log.info("read object size={}", totalRead);

                while (totalRead < targetSize) {
                    buffer.clear();
                    int read = socketChannel.read(buffer);
                    if (read == 0) {
                        continue;
                    }

                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        byteArrayOutputStream.write(buffer.get());
                        totalRead++;
                    }
                    log.info("read object size={}", totalRead);
                }

                byteArrayOutputStream.flush();

                try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()))) {
                    SomeEntity someEntity = (SomeEntity) objectInputStream.readObject();
                    log.info("the object is {}", someEntity);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
