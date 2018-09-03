package com.third.zoom.ytbus.bean;

/**
 * 作者：Sky on 2018/3/6.
 * 用途：配置相关信息
 */

public class PlayDataBean {

    //串口端口
    private String defaultSerialPort;

    //串口波特率
    private String defaultSerialRate;

    //视频间隔
    private String AdDuration;

    //字幕间隔
    private String textDuration;

    //字幕内容
    private String textContent;

    //字幕内容
    private String playVideoPath;

    public String getPlayVideoPath() {
        return playVideoPath;
    }

    public void setPlayVideoPath(String playVideoPath) {
        this.playVideoPath = playVideoPath;
    }

    public String getDefaultSerialPort() {
        return defaultSerialPort;
    }

    public void setDefaultSerialPort(String defaultSerialPort) {
        this.defaultSerialPort = defaultSerialPort;
    }

    public String getDefaultSerialRate() {
        return defaultSerialRate;
    }

    public void setDefaultSerialRate(String defaultSerialRate) {
        this.defaultSerialRate = defaultSerialRate;
    }

    public String getAdDuration() {
        return AdDuration;
    }

    public void setAdDuration(String adDuration) {
        AdDuration = adDuration;
    }

    public String getTextDuration() {
        return textDuration;
    }

    public void setTextDuration(String textDuration) {
        this.textDuration = textDuration;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}
