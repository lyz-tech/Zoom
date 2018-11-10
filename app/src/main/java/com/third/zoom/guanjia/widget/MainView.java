package com.third.zoom.guanjia.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.third.zoom.R;
import com.third.zoom.common.listener.BmvSelectListener;
import com.third.zoom.common.widget.CommonBmv;
import com.third.zoom.common.widget.CommonBmvItem;
import com.third.zoom.guanjia.adapter.AboutPageAdapter;
import com.third.zoom.guanjia.utils.Contans;
import com.third.zoom.guanjia.utils.IntentUtils;

/**
 * 作者：Sky on 2018/7/13.
 * 用途：主界面
 */

public class MainView extends LinearLayout{

    private static final int UPDATE_SHOW_TIME  = 10 * 1000;
    private static final int WHAT_UPDATE_SHOW = 1;
    private static final int WHAT_UPDATE_NORMAL_SHOW = 2;

    private Context mContext;
//    private ImageView imgShow;
    private ImageView imgShow2;
    private ImageView imgPre;
    private ImageView imgNext;
    private RelativeLayout rlPre;
    private RelativeLayout rlNext;
    private RelativeLayout rlLock;
    private CommonBmv commonBmv;

    private ViewPager viewPager;

    /**
     * 主页轮播图
     */
    private int[] showRes = {R.drawable.gj_wait_frame3, R.drawable.gj_wait_frame3,R.drawable.gj_main_show_14,R.drawable.gj_main_show_16,R.drawable.gj_main_show_0,R.drawable.gj_main_show_00,R.drawable.gj_main_show_1,R.drawable.gj_main_show_2,R.drawable.gj_main_show_3,
            R.drawable.gj_main_show_4,R.drawable.gj_main_show_5,R.drawable.gj_main_show_6,R.drawable.gj_main_show_7,
            R.drawable.gj_main_show_8,R.drawable.gj_main_show_9,R.drawable.gj_main_show_10,R.drawable.gj_main_show_11,
            R.drawable.gj_main_show_15,R.drawable.gj_main_show_23,
            R.drawable.gj_main_show_17,R.drawable.gj_main_show_18,R.drawable.gj_main_show_19,
            R.drawable.gj_main_show_20,R.drawable.gj_main_show_21,R.drawable.gj_main_show_22};
    private int[] positionRes = {R.drawable.gj_about_tab_6,R.drawable.gj_main_show_29,R.drawable.gj_icon_bom_notice};
    private int[] normalRes = {R.drawable.gj_main_show_29,R.drawable.gj_main_show_30};
    private int showIndex = 0;
    private int normalShowIndex = 0;

    //当前点击index
    private int curClickIndex = -1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_UPDATE_SHOW:
                    updateShow(1);
                    break;
                case WHAT_UPDATE_NORMAL_SHOW:
                    updateNormalShow(1);
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
//        imgShow = (ImageView) view.findViewById(R.id.img_main_show);
        imgShow2 = (ImageView) view.findViewById(R.id.img_main_show2);
        commonBmv = (CommonBmv) view.findViewById(R.id.commonbmv);
        imgPre = (ImageView) view.findViewById(R.id.gj_main_img_pre);
        imgNext = (ImageView) view.findViewById(R.id.gj_main_img_next);
        rlPre = (RelativeLayout) view.findViewById(R.id.rl_pre);
        rlNext = (RelativeLayout) view.findViewById(R.id.rl_next);
        rlLock = (RelativeLayout) view.findViewById(R.id.rl_lock);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
    }

    private AboutPageAdapter aboutPageAdapter;
    private void initViewPager(){
        viewPager.setOffscreenPageLimit(0);
        aboutPageAdapter = new AboutPageAdapter(mContext, null);
        viewPager.setAdapter(aboutPageAdapter);
    }

    private void initData(){
        initViewPager();
        updateShow(0);
        rlPre.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPre.performClick();
            }
        });
        rlNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                imgNext.performClick();
            }
        });
        imgPre.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curClickIndex == 1 ){
                    updateNormalShow(-1);
                }else{
                    updateShow(-1);
                }
            }
        });

        imgNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curClickIndex == 1 ){
                    updateNormalShow(1);
                }else{
                    updateShow(1);
                }
            }
        });

//        imgShow.setClickable(false);
    }

    public void setBmvListener(BmvSelectListener bmvListener){
        commonBmv.setBmvSelectListener(bmvListener);
    }

    public CommonBmvItem getBmvItem(int position){
        return commonBmv.getBmvItem(position);
    }

    public void setHotWaterClickListener(OnClickListener onClickListener){
        imgShow2.setOnClickListener(onClickListener);
    }

    public void setPositionShow(int position){
        Log.e("ZM","setPositionShow + " + position);
        mHandler.removeMessages(WHAT_UPDATE_SHOW);
        mHandler.removeMessages(WHAT_UPDATE_NORMAL_SHOW);
//        Glide.with(mContext).load(positionRes[position]).into(imgShow);
        curClickIndex = position;
        if(curClickIndex == 0){
            rlLock.setVisibility(VISIBLE);
        }else{
            rlLock.setVisibility(GONE);
        }
        if(curClickIndex == 1){
            updateNormalShow(0);
        }
    }

    public void setCurClickIndex(int index){
        rlLock.setVisibility(GONE);
//        imgShow.setVisibility(VISIBLE);
        viewPager.setVisibility(VISIBLE);
        aboutPageAdapter.updateData(showRes,1);
        curClickIndex = index;
    }

    public int getCurClickIndex(){
        return curClickIndex;
    }

    public void updateNormalShow(int index){
        mHandler.removeMessages(WHAT_UPDATE_SHOW);
        mHandler.removeMessages(WHAT_UPDATE_NORMAL_SHOW);
        normalShowIndex = normalShowIndex + index;
        if(normalShowIndex >= normalRes.length){
            normalShowIndex = 0;
        }
        if(normalShowIndex < 0){
            normalShowIndex = normalRes.length - 1;
        }

        if(aboutPageAdapter.getResMode() != 2){
            aboutPageAdapter.updateData(normalRes,2);
        }else{
            viewPager.setCurrentItem(normalShowIndex);
        }

//        Glide.with(mContext).load(normalRes[normalShowIndex]).into(imgShow);
        mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_NORMAL_SHOW,UPDATE_SHOW_TIME);
    }

    public void updateShow(int index){
        mHandler.removeMessages(WHAT_UPDATE_SHOW);
        mHandler.removeMessages(WHAT_UPDATE_NORMAL_SHOW);
        showIndex = viewPager.getCurrentItem() + index;
        if(showIndex >= showRes.length){
            showIndex = 0;
        }
        if(showIndex < 0){
            showIndex = showRes.length - 1;
        }
        if(aboutPageAdapter.getResMode() != 1){
            aboutPageAdapter.updateData(showRes,1);
        }else{
            viewPager.setCurrentItem(showIndex);
        }
//        Glide.with(mContext).load(showRes[showIndex]).into(imgShow);
        mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_SHOW,UPDATE_SHOW_TIME);
    }

    public void stopShow(){
        mHandler.removeMessages(WHAT_UPDATE_SHOW);
        mHandler.removeMessages(WHAT_UPDATE_NORMAL_SHOW);
    }

}
