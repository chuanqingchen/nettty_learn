package com.example.java.bio.transferobject;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by ycwu on 2017/7/19.
 */
@Slf4j
@AllArgsConstructor
public class TransferObjectClient implements Runnable {

    private String serverHost;
    private int serverPort;
    private SomeEntity data;

    @Override
    public void run() {
        try (Socket socket = new Socket(serverHost, serverPort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            out.writeObject(data);
            out.flush();
            log.info("sending object...");
            socket.shutdownOutput();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
