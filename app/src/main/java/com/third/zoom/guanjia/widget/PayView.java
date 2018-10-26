package com.third.zoom.guanjia.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.third.zoom.R;
import com.third.zoom.common.utils.PreferenceUtils;
import com.third.zoom.guanjia.utils.Contans;
import com.third.zoom.guanjia.utils.FileUtil;

import java.io.File;

import static com.third.zoom.guanjia.activity.MainActivity.DEFAULT_SHARE_DAY_12;
import static com.third.zoom.guanjia.activity.MainActivity.DEFAULT_SHARE_DAY_3;
import static com.third.zoom.guanjia.activity.MainActivity.DEFAULT_SHARE_DAY_6;

/**
 * Created by Alienware on 2018/10/22.
 */

public class PayView extends RelativeLayout {

    private Context context;

    private ImageView imgChangeBack;
    private ImageView imgTypeSelect;
    private RelativeLayout rlPay;
    private RelativeLayout rlTc;

    private ImageView imgTypeA;
    private ImageView imgTypeB;
    private ImageView imgTypeC;
    private int selectFlag = 1;

    private TextView txtMoney;

    public PayView(Context context) {
        this(context, null);
    }

    public PayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        initData();
    }
    private ImageView imgMoney;
    private void initView() {
        View view = View.inflate(context, R.layout.gj_widget_pay,this);
        imgChangeBack = (ImageView) view.findViewById(R.id.img_change_back);
        rlPay = (RelativeLayout) view.findViewById(R.id.rl_pay);
        imgTypeSelect = (ImageView) view.findViewById(R.id.img_tc_select);
        rlTc = (RelativeLayout) view.findViewById(R.id.rl_tc);
        imgTypeA = (ImageView) view.findViewById(R.id.img_tc_a);
        imgTypeB = (ImageView) view.findViewById(R.id.img_tc_b);
        imgTypeC = (ImageView) view.findViewById(R.id.img_tc_c);
        txtMoney = (TextView) view.findViewById(R.id.txt_money);
        imgMoney = (ImageView) view.findViewById(R.id.img_money);
    }

    private void initData(){
        PreferenceUtils.init(context);
        imgTypeA.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFlag = 1;
                imgTypeA.setSelected(true);
                imgTypeB.setSelected(false);
                imgTypeC.setSelected(false);
            }
        });

        imgTypeB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFlag = 2;
                imgTypeA.setSelected(false);
                imgTypeB.setSelected(true);
                imgTypeC.setSelected(false);
            }
        });

        imgTypeC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFlag = 3;
                imgTypeA.setSelected(false);
                imgTypeB.setSelected(false);
                imgTypeC.setSelected(true);
            }
        });

        imgTypeSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rlPay.setVisibility(VISIBLE);
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), FileUtil.QR_PATH);
                if(file != null && file.exists()){
                    Glide.with(context).load(file).into(imgMoney);
                }
                rlTc.setVisibility(GONE);
                if(selectFlag == 1){
                    txtMoney.setText("需要付2000元(点击跳过,测试用)");
                }else if(selectFlag == 2){
                    txtMoney.setText("需要付4000元(点击跳过,测试用)");
                }else if(selectFlag == 3){
                    txtMoney.setText("需要付6000元(点击跳过,测试用)");
                }
            }
        });

        rlPay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rlTc.setVisibility(VISIBLE);
                rlPay.setVisibility(GONE);
                int leftDay = PreferenceUtils.getInt("waterPay",DEFAULT_SHARE_DAY_3);
                if(selectFlag == 1){
                    PreferenceUtils.commitInt("waterPay",DEFAULT_SHARE_DAY_3 + leftDay);
                }else if(selectFlag == 2){
                    PreferenceUtils.commitInt("waterPay",DEFAULT_SHARE_DAY_6 + leftDay);
                }else if(selectFlag == 3){
                    PreferenceUtils.commitInt("waterPay",DEFAULT_SHARE_DAY_12 + leftDay);
                }
                Intent toLv = new Intent(Contans.INTENT_GJ_ACTION_LV_SET);
                context.sendBroadcast(toLv);
            }
        });

//        imgChangeBack.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rlTc.setVisibility(VISIBLE);
//                rlPay.setVisibility(GONE);
//                rlChangeDevice.setVisibility(GONE);
//            }
//        });

    }

    public void setBackListener(OnClickListener onClickListener){
        imgChangeBack.setOnClickListener(onClickListener);
    }

}
