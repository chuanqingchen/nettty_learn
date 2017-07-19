package com.example.java.bio;

import com.example.java.entity.SomeEntity;
import com.example.java.bio.transferobject.TransferObjectClient;
import com.example.java.bio.transferobject.TransferObjectServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by ycwu on 2017/7/19.
 */
@RunWith(JUnit4.class)
public class TestTransferObject {

    @Test
    public void testSendAnObject() throws InterruptedException {
        SomeEntity someEntity = new SomeEntity();
        someEntity.setA(111);
        someEntity.setB("bbbb");
        new Thread(new TransferObjectClient("127.0.0.1", TransferObjectServer.PORT, someEntity)).start();

        Thread.sleep(2000L);
    }
}
