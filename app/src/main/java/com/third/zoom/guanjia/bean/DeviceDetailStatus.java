package com.third.zoom.guanjia.bean;

/**
 * 作者：Sky on 2018/8/27.
 * 用途：饮水机状态
 */

public class DeviceDetailStatus {

    //工作模式
    private int workMode;

    //出水状态 bit0,1代表出水、0代表停水
    private int status;

    //出水温度（0-100）
    private int th;

    //当次出水量（80~65535）
    private int cap;

    //总出水量（80~65535）
    private long capTotal;

    //故障(1超温，2除垢11V，8可控硅短路，16除垢10V,128加热板现在只能出常温水)
    private int error0;

    //故障(1出水传感器故障，2缺水故障，8发热膜回路故障，16温度低于0°、水道结冰,
    // 32进水温度传感器故障，64电压传感器故障，128超温报警)
    private int error1;

    //水位（1低水位，2中水位，4高水位）
    private int waterCap;


    public int getWorkMode() {
        return workMode;
    }

    public void setWorkMode(int workMode) {
        this.workMode = workMode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTh() {
        return th;
    }

    public void setTh(int th) {
        this.th = th;
    }

    public int getCap() {
        return cap;
    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    public long getCapTotal() {
        return capTotal;
    }

    public void setCapTotal(long capTotal) {
        this.capTotal = capTotal;
    }

    public int getError0() {
        return error0;
    }

    public void setError0(int error0) {
        this.error0 = error0;
    }

    public int getError1() {
        return error1;
    }

    public void setError1(int error1) {
        this.error1 = error1;
    }

    public int getWaterCap() {
        return waterCap;
    }

    public void setWaterCap(int waterCap) {
        this.waterCap = waterCap;
    }
}
