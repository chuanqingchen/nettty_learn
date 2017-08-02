package com.example.netty.simpleserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Administrator on 2017/8/2 0002.
 */
@Slf4j
public class EchoClient {

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("127.0.0.1", 7000);
            BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream outputStream = socket.getOutputStream();
            Scanner scanner = new Scanner(System.in);
        ) {
            String line;
            for (; ; ) {
                line = scanner.nextLine();

                if (line == null || "".equals(line)) {
                    continue;
                }
                if ("exit".equals(line)) {
                    log.info("found exit command!!!");
                    break;
                }

                outputStream.write((line + "\n").getBytes());
                outputStream.flush();

                log.info("echo from server:{}", reader.readLine());
            }

        }


    }


}
