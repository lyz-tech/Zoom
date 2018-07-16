package com.third.zoom.guanjia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.third.zoom.R;

/**
 * 作者：Sky on 2018/7/16.
 * 用途：密码view
 */

public class PasswordView extends LinearLayout{

    private Context context;
    private Button btnCancel;

    public PasswordView(Context context) {
        this(context, null);
    }

    public PasswordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        initData();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.gj_widget_password,this);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
    }

    private void initData() {

    }

    public void setCancelOnClickListener(OnClickListener onClickListener){
        btnCancel.setOnClickListener(onClickListener);
    }

}
