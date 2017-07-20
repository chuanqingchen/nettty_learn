package com.example.java.nio;

import com.example.java.entity.SomeEntity;
import com.example.java.nio.transferobject.NioTransferClient;
import com.example.java.nio.transferobject.NioTransferServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.stream.IntStream;

/**
 * Created by ycwu on 2017/7/20.
 */
@RunWith(JUnit4.class)
public class TestNioTransferObject {

    @Test
    public void testTransferOneObject() throws InterruptedException {
        SomeEntity someEntity = new SomeEntity();
        someEntity.setA(111);
        someEntity.setB("bbbbb");
        someEntity.setC(IntStream.range(0, 90000).toArray());
        new Thread(new NioTransferClient("127.0.0.1", NioTransferServer.PORT, someEntity)).start();

        Thread.sleep(3000L);
    }
}
