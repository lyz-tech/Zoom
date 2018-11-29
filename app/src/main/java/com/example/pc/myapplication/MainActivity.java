package com.example.pc.myapplication;

/**
 * Created by Alienware on 2018/11/12.
 */

public class MainActivity {

    public static native String stringFromJNI();

    static{
        System.loadLibrary("sumjni");
    }

}
