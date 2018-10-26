package com.third.zoom.common.listener;

/**
 * Created by Alienware on 2018/10/26.
 */

public interface HttpListener {

    void onSuccess(Object object);

    void onFail(String error);

}
