package com.gruio.utils;

/**
 * Created by wwwga on 2017/7/28.
 */

public class gpioGruio {

    public native static int getGpio(int num);

    public native static int requestGpio(int num);

    public native static int setGpioState(int num, int state);

    public native static int closeGpioDev();

    public native static int openGpioDev();

    public native static int releaseGpio(int num);

    static{
        System.loadLibrary("rockchip_gpio");
    }
}
