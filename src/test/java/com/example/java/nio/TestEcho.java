package com.example.java.nio;

import com.example.java.bio.EchoClient;
import com.example.java.bio.EchoServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by Administrator on 2017/7/18 0018.
 */
@RunWith(JUnit4.class)
@Slf4j
public class TestEcho {

    @Test
    public void testOneEchoClient() throws InterruptedException {
        new Thread(new NioEchoClient("127.0.0.1", NioEchoServer.PORT)).start();
        Thread.sleep(5000L);
    }

    @Test
    public void testSeveralEchoClients() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        IntStream.range(0, 10)
            .forEach(i -> executorService.submit(new EchoClient("127.0.0.1", EchoServer.PORT)));

        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void testClientWriteWithoutFlush() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            try (Socket socket = new Socket("127.0.0.1", EchoServer.PORT);
                OutputStream out = socket.getOutputStream();
            ) {
                for (char c : "hello world".toCharArray()) {
                    log.info("" + c);
                    out.write((int) c);
                }

                Thread.sleep(10000);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executorService.awaitTermination(30, TimeUnit.SECONDS);
    }

}
