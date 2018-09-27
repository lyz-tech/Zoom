package com.third.zoom.guanjia.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.third.zoom.R;
import com.third.zoom.common.utils.PreferenceUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：Sky on 2018/7/13.
 * 用途：顶部view
 */

public class NavTopView extends RelativeLayout{

    private static final int UPDATE_TIME = 60 * 1000;
    private static final int WHAT_UPDATE_TIME = 1;
    private static final int WHAT_WATER_WARNING = 2;

    private TextView txtTimeDay;
    private TextView txtTimeWeek;
    private TextView txtTimeHour;
    private TextView txtWaterTime;
    private TextView txtWaterCap;

    private Context mContext;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日#HH:mm#EEEE");

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_UPDATE_TIME:
                    updateTime();
                    break;
                case WHAT_WATER_WARNING:
                    updateWaterWarning(true);
                    break;
            }
        }
    };

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
        txtTimeDay = (TextView) view.findViewById(R.id.txt_time_day);
        txtTimeWeek = (TextView) view.findViewById(R.id.txt_time_week);
        txtTimeHour = (TextView) view.findViewById(R.id.txt_time_hour);
        txtWaterTime = (TextView) view.findViewById(R.id.txt_water_time);
        txtWaterCap = (TextView) view.findViewById(R.id.txt_water_cap);
    }

    private void initData(){
        updateTime();
        PreferenceUtils.init(mContext);
        long saveTime = PreferenceUtils.getLong("waterTime",0);
        int currentType = PreferenceUtils.getInt("waterType",1);
        long curnTime = System.currentTimeMillis();
        long indexTime = curnTime - saveTime;
        int last = (int) (indexTime / 24 * 60 * 60 * 1000);
        if(last > 0 && last < 180){
            if(currentType == 1){
                setWaterTime(120 - last );
            }else{
                setWaterTime(180 - last );
            }
        }else{
            if(currentType == 1){
                setWaterTime(120);
            }else{
                setWaterTime(180);
            }
        }
    }

    private void updateTime(){
        String[] times = sdf.format(new Date()).split("#");
        txtTimeDay.setText(times[0]);
        txtTimeWeek.setText(times[2]);
        txtTimeHour.setText(times[1]);
        mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_TIME,UPDATE_TIME);
    }

    public void setWaterTime(int waterTime){
        if(waterTime < 0){
            txtWaterTime.setText("0日");
        }else{
            txtWaterTime.setText(waterTime + "日");
        }
    }

    private void setWaterCap(int waterCap){
        txtWaterCap.setText(waterCap + "");
        if(waterCap < 100){
            txtWaterTime.setTextColor(Color.RED);
            txtWaterCap.setTextColor(Color.RED);
            updateWaterWarning(true);
        }else{
            txtWaterTime.setTextColor(Color.BLACK);
            txtWaterCap.setTextColor(Color.BLACK);
            updateWaterWarning(false);
        }
    }

    private void updateWaterWarning(boolean flag){
        if(!flag){
            mHandler.removeMessages(WHAT_WATER_WARNING);
            return;
        }
        if(txtWaterTime.getVisibility() == View.VISIBLE){
            txtWaterTime.setVisibility(View.INVISIBLE);
            txtWaterCap.setVisibility(View.INVISIBLE);
        }else{
            txtWaterTime.setVisibility(View.VISIBLE);
            txtWaterCap.setVisibility(View.VISIBLE);
        }
        mHandler.sendEmptyMessageDelayed(WHAT_WATER_WARNING,1000);
    }

}
