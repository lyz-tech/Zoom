package com.gruio.utils;

/**
 * Created by gzy on 2017/7/31.
 */

public class GpioK2Manager {
    private static GpioK2Manager instance;

    private GpioK2Manager(){
    }
    public static synchronized GpioK2Manager getInstance(){
        if (instance==null){
            synchronized (GpioK2Manager.class){
                if (instance==null){
                    instance=new GpioK2Manager();
                }
            }
        }
        return instance;
    }





    public synchronized int getGpio3(){ //GPIO0_A6
        //如果后期出现之前状态不对的，再用getGpio接口校验之前状态
        //打开gpio设备开发接口
        int state;
        gpioGruio.openGpioDev();
        gpioGruio.requestGpio(gpiodefinegruio.GPIO0_A6);
        state= gpioGruio.getGpio(gpiodefinegruio.GPIO0_A6);
        gpioGruio.releaseGpio(gpiodefinegruio.GPIO0_A6);
        //关闭开发接口
        gpioGruio.closeGpioDev();
        return state;
    }


}
