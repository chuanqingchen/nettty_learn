package com.example.java.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by ycwu on 2017/7/19.
 */
@Data
@ToString(exclude = "c")
public class SomeEntity implements Serializable {
    private int a;
    private String b;
    private int[] c;
}
