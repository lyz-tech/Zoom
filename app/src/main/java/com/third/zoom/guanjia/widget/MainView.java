package com.third.zoom.guanjia.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.third.zoom.R;
import com.third.zoom.common.listener.BmvSelectListener;
import com.third.zoom.common.widget.CommonBmv;

/**
 * 作者：Sky on 2018/7/13.
 * 用途：主界面
 */

public class MainView extends LinearLayout{

    private static final int UPDATE_SHOW_TIME  = 5 * 1000;
    private static final int WHAT_UPDATE_SHOW = 1;

    private Context mContext;
    private ImageView imgShow;
    private CommonBmv commonBmv;

    /**
     * 主页轮播图
     */
    private int[] showRes = {R.drawable.apk,R.drawable.audio};
    private int showIndex = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_UPDATE_SHOW:
                    updateShow(1);
                    break;
            }
        }
    };


    public MainView(Context context) {
        this(context,null);
    }

    public MainView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
        initData();
    }

    private void initView(){
        View view = View.inflate(mContext, R.layout.gj_widget_main,this);
        imgShow = (ImageView) view.findViewById(R.id.img_main_show);
        commonBmv = (CommonBmv) view.findViewById(R.id.commonbmv);
    }

    private void initData(){
        updateShow(0);
        commonBmv.setBmvSelectListener(new BmvSelectListener() {
            @Override
            public void itemSelectOpen(int position) {
                Log.e("ZM","itemSelectOpen = " + position);
            }

            @Override
            public void itemSelectClose(int position) {
                Log.e("ZM","itemSelectClose = " + position);
            }
        });
    }

    private void updateShow(int index){
        showIndex = showIndex + index;
        if(showIndex >= showRes.length){
            showIndex = 0;
        }
        if(showIndex < 0){
            showIndex = showRes.length - 1;
        }
        imgShow.setImageResource(showRes[showIndex]);
        mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_SHOW,UPDATE_SHOW_TIME);
    }

}
