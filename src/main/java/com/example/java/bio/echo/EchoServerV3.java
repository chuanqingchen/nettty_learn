package com.example.java.bio.echo;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/7/18 0018.
 *
 * version 2: a server can handler multiple clients at the same time by pulling a thread from thread
 * pool
 */
@Slf4j
public class EchoServerV3 {

    public static final int PORT = 5400;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(PORT));

            log.info("server started");
            boolean exit = false;
            while (!exit) {
                Socket socket = serverSocket.accept();
                log.info("waiting input");
                executorService.submit(new EchoServerHandlerThread(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
