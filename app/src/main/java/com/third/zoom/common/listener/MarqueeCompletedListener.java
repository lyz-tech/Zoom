package com.third.zoom.common.listener;

import android.view.View;

/**
 * 作者：Sky on 2018/7/14.
 * 用途：滚动字幕监听
 */

public interface MarqueeCompletedListener {

    /**
     * Text滚动结束
     *
     * @param mTextView
     */
    void onCompleted(View mTextView);
}
