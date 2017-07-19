package com.example.java.bio.transferobject;

import com.example.java.entity.SomeEntity;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ycwu on 2017/7/19.
 */
@Slf4j
public class TransferObjectServer {

    public static final int PORT = 5500;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             Socket socket = serverSocket.accept();
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {
            log.info("start receiving object...");
            SomeEntity someEntity = (SomeEntity) in.readObject();
            log.info("read object[{}]", someEntity);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
