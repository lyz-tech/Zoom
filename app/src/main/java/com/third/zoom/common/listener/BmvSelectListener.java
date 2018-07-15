package com.third.zoom.common.listener;

/**
 * Created by Alienware on 2018/7/15.
 */

public interface BmvSelectListener {

    /**
     * 选择item
     * @param position
     */
    void itemSelectOpen(int position);

    /**
     *  item弹起
     * @param position
     */
    void itemSelectClose(int position);
}
