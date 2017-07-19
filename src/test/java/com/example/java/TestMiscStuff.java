package com.example.java;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by ycwu on 2017/7/19.
 */
@RunWith(JUnit4.class)
@Slf4j
public class TestMiscStuff {

    @Test
    public void testByteArrayAndLongValueConvert() {
        long a = 63L;
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(a);
        buffer.flip();
        byte[] array = buffer.array();
        log.info("byte array of {}={}", a, Arrays.toString(array));
        Assert.assertEquals(8, array.length);
        Assert.assertEquals(a, buffer.getLong());
        Assert.assertEquals(a, new BigInteger(array).longValueExact());

    }
}
