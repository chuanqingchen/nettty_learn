package com.example.java.bio.transferobject;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by ycwu on 2017/7/19.
 */
@Data
@ToString
public class SomeEntity implements Serializable {
    private int a;
    private String b;

}
