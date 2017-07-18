package com.example.java.nio;

import com.example.java.bio.EchoProtocolConstants;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Administrator on 2017/7/18 0018.
 */
@Slf4j
@AllArgsConstructor
public class NioEchoClient implements Runnable {

    private String serverHost;
    private int serverPort;

    private static String[] wordList = new String[]{EchoProtocolConstants.EXIT, "hello", "world",
        "the USA", "silly", "xxx"};

    @Override
    public void run() {

        List<String> words = Arrays.asList(wordList);
        Collections.shuffle(words);

        try (SocketChannel socketChannel = SocketChannel
            .open(new InetSocketAddress(serverHost, serverPort))) {
            socketChannel.configureBlocking(false);

            ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            for (String word : words) {
                writeBuffer.clear();
                writeBuffer.put(word.getBytes());
                //
                writeBuffer.flip();
                socketChannel.write(writeBuffer);

                log.info("send {}", word);

                socketChannel.read(readBuffer);
                String echo = readBuffer.asCharBuffer().toString();
                log.info("echo {}", echo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
