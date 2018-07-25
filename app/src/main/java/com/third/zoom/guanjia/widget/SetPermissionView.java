package com.third.zoom.guanjia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.third.zoom.R;
import com.third.zoom.guanjia.utils.Contans;
import com.third.zoom.guanjia.utils.IntentUtils;

/**
 * 作者：Sky on 2018/7/16.
 * 用途：设置权限view
 */

public class SetPermissionView extends RelativeLayout{

    private Context context;
    private PasswordView passwordView;
    private LinearLayout permissionRoot;
    private Button btnTime;
    private Button btnPassword;


    public SetPermissionView(Context context) {
        this(context, null);
    }

    public SetPermissionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SetPermissionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        initData();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.gj_widget_permission,this);
        passwordView = (PasswordView) view.findViewById(R.id.permission_password);
        permissionRoot = (LinearLayout) view.findViewById(R.id.permission_root);
        btnTime = (Button) view.findViewById(R.id.btn_time);
        btnPassword = (Button) view.findViewById(R.id.btn_password);
    }

    private void initData() {
        btnPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.sendBroadcast(context, Contans.INTENT_GJ_ACTION_ACTIVE);
                permissionRoot.setVisibility(GONE);
                passwordView.setVisibility(VISIBLE);
            }
        });

        passwordView.setCancelOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.sendBroadcast(context, Contans.INTENT_GJ_ACTION_ACTIVE);
                permissionRoot.setVisibility(VISIBLE);
                passwordView.setVisibility(GONE);
            }
        });
    }
}
