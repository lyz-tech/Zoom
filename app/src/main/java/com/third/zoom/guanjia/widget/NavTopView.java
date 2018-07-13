package com.third.zoom.guanjia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.third.zoom.R;

/**
 * 作者：Sky on 2018/7/13.
 * 用途：//TODO
 */

public class NavTopView extends RelativeLayout{

    private Context mContext;

    public NavTopView(Context context) {
        this(context,null);
    }

    public NavTopView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NavTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
        initData();
    }

    private void initView(){
        View view = View.inflate(mContext, R.layout.gh_widget_top,this);
    }

    private void initData(){

    }

}
