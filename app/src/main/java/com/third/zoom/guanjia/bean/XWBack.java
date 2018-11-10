package com.third.zoom.guanjia.bean;

/**
 * Created by Alienware on 2018/10/26.
 */

public class XWBack {

    private int code;
    private long expires;
    private String error;
    private WXData data;
    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }
    public long getExpires() {
        return expires;
    }

    public void setData(WXData data) {
        this.data = data;
    }
    public WXData getData() {
        return data;
    }




}
