package com.third.zoom.guanjia.widget;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.third.zoom.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alienware on 2018/7/24.
 */

public class SetTimeView extends RelativeLayout {

    private Context context;
    private Button btnSelectTime;
    private Button btnCancel;

    public SetTimeView(Context context) {
        this(context, null);
    }

    public SetTimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SetTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        initData();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.gj_widget_set_time, this);
        btnSelectTime = (Button) view.findViewById(R.id.btn_select_time);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
    }

    private void initData() {
        initTimePicker();
        btnSelectTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show();
            }
        });
    }

    public void setCancelOnClickListener(OnClickListener onClickListener){
        btnCancel.setOnClickListener(onClickListener);
    }

    private TimePickerView pvTime;
    private void initTimePicker() {//Dialog 模式下，在底部弹出
        pvTime = new TimePickerBuilder(context, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Log.e("pvTime", "onTimeSelect = " + getTime(date));
            }
        }).setType(new boolean[]{true, true, true, true, true, true})
                .isDialog(true)
                .build();

        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

}
