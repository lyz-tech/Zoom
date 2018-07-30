package com.third.zoom.guanjia.widget;

import android.app.Dialog;
import android.content.Context;

/**
 * 作者：Sky on 2018/7/30.
 * 用途：常用dialog
 */

public class NormalDialog extends Dialog{


    public NormalDialog(Context context) {
        super(context);
    }

    public NormalDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected NormalDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


}
