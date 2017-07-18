package com.example.java.bio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Administrator on 2017/7/18 0018.
 */
@Slf4j
@AllArgsConstructor
public class ServerHandlerThread implements Runnable {

    private Socket socket;

    @Override
    public void run() {
        try (BufferedWriter writer = new BufferedWriter(
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
