package com.example.java.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Administrator on 2017/7/18 0018.
 *
 * version 2: a server can handler multiple clients at the same time by starting a new thread to handle
 */
@Slf4j
public class EchoServerV2 {

    public static final int PORT = 5400;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(PORT));

            log.info("server started");
            boolean exit = false;
            while (!exit) {
                Socket socket = serverSocket.accept();
                log.info("waiting input");
                new Thread(new ServerHandlerThread(socket)).start();

//                try-with-resource will close the socket too early, cause the client get exception:
//                     java.net.SocketException: Software caused connection abort: socket write error
//
//                try (Socket socket = serverSocket.accept()) {
//                    log.info("waiting input");
//                    new Thread(new ServerHandlerThread(socket)).start();
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
