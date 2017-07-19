package com.example.java.nio.transferobject;

import com.example.java.entity.SomeEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

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
        try (SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(serverHost, serverPort));
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        ) {
            objectOutputStream.writeObject(someEntity);
            objectOutputStream.flush();
            byte[] array = byteArrayOutputStream.toByteArray();
            log.info("target send object size={}", array.length);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // first round
            buffer.putInt(array.length);

            int i = 0;
            while (i < array.length) {
                while (buffer.hasRemaining() && i < array.length) {
                    buffer.put(array[i++]);
                }
                buffer.flip();
                socketChannel.write(buffer);
                log.info("send object size={}", i);

                buffer.clear();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
