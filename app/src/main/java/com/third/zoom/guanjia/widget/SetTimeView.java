package com.third.zoom.guanjia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.third.zoom.R;

/**
 * Created by Alienware on 2018/7/24.
 */

public class SetTimeView extends LinearLayout {

    private Context context;

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
        View view = View.inflate(context, R.layout.gj_widget_set_time,this);

    }

    private void initData() {

    }

}
