package com.example.java.bio.echo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/7/18 0018.
 */
@Slf4j
@AllArgsConstructor
public class EchoClient implements Runnable {

    private String serverHost;
    private int serverPort;

    private static String[] wordList = new String[]{EchoProtocolConstants.EXIT, "hello", "world",
        "the USA", "silly", "xxx"};

    @Override
    public void run() {

        List<String> words = Arrays.asList(wordList);

        Collections.shuffle(words);
        try (Socket socket = new Socket(serverHost, serverPort);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()))
        ) {
            for (String word : words) {
                writer.write(word + "\n");
                writer.flush();
                log.info("client sent {}", word);

                String response = reader.readLine();
                log.info("echo: {}", response);
                if (EchoProtocolConstants.EXIT.equals(response)) {
                    log.info("got exit signal");
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
