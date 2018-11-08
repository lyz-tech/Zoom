package com.third.zoom.guanjia.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gruio.utils.GpioK2Manager;
import com.third.zoom.R;
import com.third.zoom.common.listener.NormalListener;
import com.third.zoom.common.utils.PreferenceUtils;
import com.third.zoom.guanjia.utils.Contans;
import com.third.zoom.guanjia.utils.FileUtil;
import com.third.zoom.guanjia.utils.QrCodeUtil;

import java.io.File;

import static com.third.zoom.guanjia.activity.MainActivity.DEFAULT_SHARE_DAY_12;
import static com.third.zoom.guanjia.activity.MainActivity.DEFAULT_SHARE_DAY_3;
import static com.third.zoom.guanjia.activity.MainActivity.DEFAULT_SHARE_DAY_6;

/**
 * Created by Alienware on 2018/8/30.
 */

public class SelectWaterDeviceV2View extends RelativeLayout {

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
    private TextView txtMoney;
    private TextView txtCount;

    private int countIndex  = 5;

    private int selectFlag = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                rlPayAfter.setVisibility(GONE);
                if(listener != null){
                    listener.onActive(selectFlag);
                }
            }else if(msg.what == 10){
                getGpioStatus();
            }else {
                txtCount.setText(countIndex + "");
                countIndex--;
                if(countIndex > 0){
                    mHandler.sendEmptyMessageDelayed(2,1000);
                }
            }
        }
    };

    public SelectWaterDeviceV2View(Context context) {
        this(context, null);
    }

    public SelectWaterDeviceV2View(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectWaterDeviceV2View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
        initData();
    }

    private void initView(){
        View view = View.inflate(mContext, R.layout.gj_widget_water_device_v2,this);
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
        txtMoney = (TextView) view.findViewById(R.id.txt_money);
        txtCount = (TextView) view.findViewById(R.id.txt_count);
        imgMoney = (ImageView) view.findViewById(R.id.img_money);

    }

    private void initData(){
        PreferenceUtils.init(mContext);
        normalDialog();

//        imgStart.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                imgStart.setVisibility(View.GONE);
////                rlZbxt.setVisibility(VISIBLE);
////                imgBg.setImageResource(R.drawable.gj_device_bg_3);
//                Glide.with(mContext).load(R.drawable.gj_device_bg_3).into(imgBg);
//                imgA.setVisibility(VISIBLE);
//                imgB.setVisibility(VISIBLE);
//            }
//        });
        imgStart.setVisibility(View.GONE);
        Glide.with(mContext).load(R.drawable.gj_device_bg_3).into(imgBg);
        imgA.setVisibility(VISIBLE);
        imgB.setVisibility(VISIBLE);

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
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), FileUtil.QR_PATH);
                if(file != null && file.exists()){
                    Glide.with(mContext).load(file).into(imgMoney);
                }
                Glide.with(mContext).load(R.drawable.gj_device_bg_2).into(imgBg);
                if(selectFlag == 1){
                    txtMoney.setText("请扫码付款");
                }else if(selectFlag == 2){
                    txtMoney.setText("请扫码付款");
                }else if(selectFlag == 3){
                    txtMoney.setText("请扫码付款");
                }
                mHandler.sendEmptyMessageDelayed(10,3000);
            }
        });

        rlPay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                rlPay.setVisibility(GONE);
//                SelectWaterDeviceV2View.this.setVisibility(GONE);
//                updateMode(selectFlag);
//                rlPayAfter.setVisibility(VISIBLE);
//                mHandler.sendEmptyMessageDelayed(2,1);
//                mHandler.sendEmptyMessageDelayed(1,5000);
            }
        });

        rlPayAfter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        imgA.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                imgA.setVisibility(GONE);
                imgB.setVisibility(GONE);
                rlZbxt.setVisibility(VISIBLE);
//                dialogShow("您选择的是共享机");
                type = 1;
            }
        });

        imgB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogShow("您选择的是家庭机");
                type = 10;
            }
        });

        initMoneyImg();
    }
    private ImageView imgMoney;
    private void initMoneyImg(){
        //本地文件
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), FileUtil.QR_PATH);
        if(file != null && file.exists()){
            //加载图片
            Glide.with(mContext).load(file).into(imgMoney);
        }
    }

    private void updateMode(int type){
        if(type == 10){
            Log.e("ZM","家庭机");
            type = 2;
            PreferenceUtils.commitInt("waterMode",2);
        }else{
            PreferenceUtils.commitInt("waterMode",1);
        }
        PreferenceUtils.commitBoolean("isBootFirst",false);
        PreferenceUtils.commitInt("waterType",type);
        PreferenceUtils.commitInt("lvTime",DEFAULT_SHARE_DAY_6);
        PreferenceUtils.commitLong("waterTime",System.currentTimeMillis());
        if(type == 1){
            PreferenceUtils.commitInt("waterPay",DEFAULT_SHARE_DAY_3);
        }else if(type == 2){
            PreferenceUtils.commitInt("waterPay",DEFAULT_SHARE_DAY_6);
        }else if(type == 3){
            PreferenceUtils.commitInt("waterPay",DEFAULT_SHARE_DAY_12);
        }
        Intent toLv = new Intent(Contans.INTENT_GJ_ACTION_MODE_CHANGE);
        mContext.sendBroadcast(toLv);
    }

    public void initViewData(){
        imgStart.setVisibility(View.GONE);
        Glide.with(mContext).load(R.drawable.gj_device_bg_3).into(imgBg);
        imgA.setVisibility(VISIBLE);
        imgB.setVisibility(VISIBLE);
    }

    private int type;
    private AlertDialog normalDialog;
    private void normalDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("温馨提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SelectWaterDeviceV2View.this.setVisibility(GONE);
                updateMode(10);
                if(listener != null){
                    listener.onActive(type);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                rlZbxt.setVisibility(GONE);
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

    /**
     * 读取io状态
     */
    private void getGpioStatus(){
        int status = GpioK2Manager.getInstance().getGpio3();
        if(status == 1){
            paySuccess();
        }else{
            waitingPay();
        }
    }

    /**
     * 支付成功
     */
    private void paySuccess(){
        rlPay.setVisibility(GONE);
        SelectWaterDeviceV2View.this.setVisibility(GONE);
        updateMode(selectFlag);
    }

    /**
     * 等待支付
     */
    private void waitingPay(){
        mHandler.sendEmptyMessageDelayed(10,1500);
    }
}
