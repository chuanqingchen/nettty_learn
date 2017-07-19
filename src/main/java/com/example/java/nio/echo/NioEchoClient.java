package com.example.java.nio.echo;

import com.example.java.bio.echo.EchoProtocolConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count;
            for (String word : words) {
                buffer.clear();
                buffer.put(word.getBytes());
                buffer.flip();
                socketChannel.write(buffer);
                log.info("send {}", word);

                buffer.clear();
                do {
                    count = socketChannel.read(buffer);
                    if (count < 0) {
                        // error
                        break;
                    }
                } while (count == 0);

                buffer.flip();

                StringBuilder sb = new StringBuilder();
                while (buffer.hasRemaining()) {
                    sb.append((char) buffer.get());
                }
                log.info("echo {}", sb.toString());

                if (EchoProtocolConstants.EXIT.equals(sb.toString())) {
                    log.info("got exit mark");
                    break;
                }

                // TODO define the protocol instead of sleep
                Thread.sleep(200L);
            }

            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }


    }
}
