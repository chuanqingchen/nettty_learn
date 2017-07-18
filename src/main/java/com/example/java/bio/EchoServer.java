package com.example.java.bio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Administrator on 2017/7/18 0018.
 *
 * version 1: a server can only handler one client at a time
 */
@Slf4j
public class EchoServer {

    public static final int PORT = 5400;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(PORT));

            boolean exit = false;
            while (!exit) {
                log.info("waiting input");
                try (Socket socket = serverSocket.accept();
                    BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))
                ) {
                    log.info("accepted client:{}", socket.toString());

                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("server read {}", line);

                        writer.write(line + "\n");
                        writer.flush();

                        if (EchoProtocolConstants.EXIT.equals(line)) {
                            log.info("client exit");
                            break;
                        }
                    }
                } finally {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
