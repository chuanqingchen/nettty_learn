package com.example.java.nio.echo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by Administrator on 2017/7/18 0018.
 */
@Slf4j
@AllArgsConstructor
public class NioEchoServerHandler implements Runnable {

    private SocketChannel socketChannel;
    private SelectionKey key;

    @Override
    public void run() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count;
            while ((count = socketChannel.read(buffer)) > -1) {
                if (count == 0) {
                    continue;
                }

                buffer.flip();
                StringBuilder sb = new StringBuilder();
                while (buffer.hasRemaining()) {
                    sb.append((char) buffer.get());
                }
                String content = sb.toString();
                log.info("handler read:{}", content);

                buffer.clear();
                buffer.put(content.getBytes());
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
                log.info("handler echo:{}", content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                key.cancel();
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
