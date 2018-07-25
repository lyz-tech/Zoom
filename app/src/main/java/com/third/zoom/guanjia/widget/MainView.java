package com.third.zoom.guanjia.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.third.zoom.R;
import com.third.zoom.common.listener.BmvSelectListener;
import com.third.zoom.common.widget.CommonBmv;
import com.third.zoom.common.widget.CommonBmvItem;

/**
 * 作者：Sky on 2018/7/13.
 * 用途：主界面
 */

public class MainView extends LinearLayout{

    private static final int UPDATE_SHOW_TIME  = 5 * 1000;
    private static final int WHAT_UPDATE_SHOW = 1;

    private Context mContext;
    private ImageView imgShow;
    private ImageView imgPre;
    private ImageView imgNext;
    private CommonBmv commonBmv;

    /**
     * 主页轮播图
     */
    private int[] showRes = {R.drawable.gj_main_show_1,R.drawable.gj_main_show_2,R.drawable.gj_main_show_3};
    private int[] positionRes = {R.drawable.gj_icon_bom_notice,R.drawable.gj_icon_hot_unlock,R.drawable.gj_icon_bom_notice,R.drawable.gj_icon_bom_notice};
    private int showIndex = 0;

    //当前点击index
    private int curClickIndex = -1;

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
        imgPre = (ImageView) view.findViewById(R.id.gj_main_img_pre);
        imgNext = (ImageView) view.findViewById(R.id.gj_main_img_next);
    }

    private void initData(){
        updateShow(0);
        imgPre.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateShow(-1);

            }
        });

        imgNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateShow(1);
            }
        });
    }

    public void setBmvListener(BmvSelectListener bmvListener){
        commonBmv.setBmvSelectListener(bmvListener);
    }

    public CommonBmvItem getBmvItem(int position){
        return commonBmv.getBmvItem(position);
    }

    public void setHotWaterClickListener(OnClickListener onClickListener){
        imgShow.setOnClickListener(onClickListener);
    }

    public void setPositionShow(int position){
        mHandler.removeMessages(WHAT_UPDATE_SHOW);
        imgShow.setImageResource(positionRes[position]);
        curClickIndex = position;
        if(curClickIndex == 1){
            imgShow.setClickable(true);
        }else{
            imgShow.setClickable(false);
        }
    }

    public void updateShow(int index){
        mHandler.removeMessages(WHAT_UPDATE_SHOW);
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
