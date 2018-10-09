package com.third.zoom.guanjia.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.third.zoom.R;

/**
 * Created by Alienware on 2018/8/30.
 */

public class ErrorView extends RelativeLayout {

    private static final int WHAT_CHANGE = 1;

    private Context mContext;
    private ImageView imgError;
    private TextView txtMessage;

    private boolean flag = true;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_CHANGE:
                    change();
                    break;
            }
        }
    };

    public ErrorView(Context context) {
        this(context, null);
    }

    public ErrorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
        initData();
    }

    private void initView(){
        View view = View.inflate(mContext, R.layout.gj_widget_error,this);
        imgError = (ImageView) view.findViewById(R.id.img_waring);
        txtMessage = (TextView) view.findViewById(R.id.txt_error);
    }

    private void initData(){
        mHandler.sendEmptyMessageDelayed(WHAT_CHANGE,1000);
    }

    private void change(){
        if(imgError != null){
            if(flag){
                flag = false;
                imgError.setVisibility(GONE);
                txtMessage.setVisibility(GONE);
            }else{
                flag = true;
                imgError.setVisibility(VISIBLE);
                txtMessage.setVisibility(VISIBLE);
            }
        }
        mHandler.sendEmptyMessageDelayed(WHAT_CHANGE,800);
    }

    public void setErrorMessage(String msg){
        txtMessage.setText(msg);
    }

}
