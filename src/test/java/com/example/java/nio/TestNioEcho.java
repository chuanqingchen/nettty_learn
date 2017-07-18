package com.example.java.nio;

import com.example.java.bio.EchoServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Created by Administrator on 2017/7/18 0018.
 */
@RunWith(JUnit4.class)
@Slf4j
public class TestNioEcho {

    @Test
    public void testOneNioEchoClient() throws InterruptedException {
        new Thread(new NioEchoClient("127.0.0.1", NioEchoServer.PORT)).start();
        Thread.sleep(2000L);
    }

    @Test
    public void testSeveralNioEchoClients() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        IntStream.range(0, 10)
                .forEach(i -> executorService.submit(new NioEchoClient("127.0.0.1", EchoServer.PORT)));

        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }


}
