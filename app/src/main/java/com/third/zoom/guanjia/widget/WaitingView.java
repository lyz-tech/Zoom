package com.third.zoom.guanjia.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.third.zoom.R;
import com.third.zoom.guanjia.utils.Contans;

/**
 * Created by Alienware on 2018/7/24.
 */

public class WaitingView extends LinearLayout {

    private Context context;
    private ImageView imageView;
    private AnimationDrawable animationDrawable;

    public WaitingView(Context context) {
        this(context, null);
    }

    public WaitingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaitingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        initData();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.gj_widget_waiting,this);
        imageView = (ImageView) view.findViewById(R.id.img_bg);
    }

    private void initData() {
        setXml2FrameAnim2();

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toActive = new Intent(Contans.INTENT_GJ_ACTION_ACTIVE);
                context.sendBroadcast(toActive);
            }
        });
    }

    private void setXml2FrameAnim2() {
        animationDrawable = (AnimationDrawable) getResources().getDrawable(
                R.drawable.gj_wait_frame_anim);
        imageView.setBackground(animationDrawable);
    }

    @Override
    public void setVisibility(int visibility) {
        if(visibility == View.VISIBLE){
            if (animationDrawable != null && !animationDrawable.isRunning()) {
                animationDrawable.start();
            }
        }else{
            if (animationDrawable != null && animationDrawable.isRunning()) {
                animationDrawable.stop();
            }
        }
        super.setVisibility(visibility);
    }


}
