package com.third.zoom.guanjia.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.third.zoom.R;
import com.third.zoom.common.listener.NormalListener;

/**
 * Created by Alienware on 2018/8/30.
 */

public class SelectWaterDeviceView extends RelativeLayout {

    private Context mContext;
    private ImageView imgBg;
    private ImageView imgStart;
    private ImageView imgA;
    private ImageView imgB;
    private RelativeLayout rlTypeSelect;
    private RelativeLayout rlZbxt;
    private ImageView imgTypeA;
    private ImageView imgTypeB;
    private ImageView imgTypeC;
    private Button btnSelect;
    private ImageView tcSelect;
    private RelativeLayout rlPay;
    private RelativeLayout rlPayAfter;

    private int selectFlag = 1;

    public SelectWaterDeviceView(Context context) {
        this(context, null);
    }

    public SelectWaterDeviceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectWaterDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
        initData();
    }

    private void initView(){
        View view = View.inflate(mContext, R.layout.gj_widget_water_device,this);
        imgBg = (ImageView) view.findViewById(R.id.img_bg);
        imgStart = (ImageView) view.findViewById(R.id.img_start);
        imgA = (ImageView) view.findViewById(R.id.img_a);
        imgB = (ImageView) view.findViewById(R.id.img_b);
        rlZbxt = (RelativeLayout) view.findViewById(R.id.rl_zbms);
        rlTypeSelect = (RelativeLayout) view.findViewById(R.id.rl_type_select);
        imgTypeA = (ImageView) view.findViewById(R.id.img_tc_a);
        imgTypeB = (ImageView) view.findViewById(R.id.img_tc_b);
        imgTypeC = (ImageView) view.findViewById(R.id.img_tc_c);
        btnSelect = (Button) view.findViewById(R.id.btn_type_select);
        tcSelect = (ImageView) view.findViewById(R.id.img_tc_select);
        rlPay = (RelativeLayout) view.findViewById(R.id.rl_pay);
        rlPayAfter = (RelativeLayout) view.findViewById(R.id.rl_pay_after);
    }

    private void initData(){
        normalDialog();

        imgStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                imgStart.setVisibility(View.GONE);
                rlZbxt.setVisibility(VISIBLE);

//                imgBg.setImageResource(R.drawable.gj_device_bg_3);
//                Glide.with(mContext).load(R.drawable.gj_device_bg_3).into(imgBg);
//                imgA.setVisibility(VISIBLE);
//                imgB.setVisibility(VISIBLE);
            }
        });

        btnSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rlZbxt.setVisibility(GONE);
                rlTypeSelect.setVisibility(VISIBLE);
            }
        });

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

        imgTypeA.performClick();

        tcSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rlTypeSelect.setVisibility(GONE);
                rlPay.setVisibility(VISIBLE);
            }
        });

        rlPay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rlPay.setVisibility(GONE);
                rlPayAfter.setVisibility(VISIBLE);
            }
        });

        rlPayAfter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rlPayAfter.setVisibility(GONE);
                if(listener != null){
                    listener.onActive(selectFlag);
                }
            }
        });

        imgA.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogShow("您选择的是共享机");
                type = 1;
            }
        });

        imgB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogShow("您选择的是家庭机");
                type = 2;
            }
        });
    }

    private int type;
    private AlertDialog normalDialog;
    private void normalDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("温馨提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(listener != null){
                    listener.onActive(type);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                imgStart.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(R.drawable.gj_device_bg_2).into(imgBg);
//                imgBg.setImageResource(R.drawable.gj_device_bg_2);
                imgA.setVisibility(GONE);
                imgB.setVisibility(GONE);
                dialogDismiss();
            }
        });
        normalDialog = builder.create();
    }

    private void dialogShow(String message){
        if(!normalDialog.isShowing()){
            normalDialog.setMessage(message);
            normalDialog.show();
        }else{
            normalDialog.setMessage(message);
        }
    }

    private void dialogDismiss(){
        if(normalDialog.isShowing()){
            normalDialog.dismiss();
        }
    }

    private NormalListener listener;
    public void setOnListener(NormalListener onListener){
        listener = onListener;
    }


}
