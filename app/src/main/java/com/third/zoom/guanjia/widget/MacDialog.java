package com.third.zoom.guanjia.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.third.zoom.R;
import com.third.zoom.common.listener.HttpListener;
import com.third.zoom.common.listener.NormalListener;
import com.third.zoom.common.utils.PreferenceUtils;
import com.third.zoom.guanjia.bean.XWBack;
import com.third.zoom.guanjia.utils.FileUtil;
import com.third.zoom.guanjia.utils.QrCodeUtil;
import com.third.zoom.guanjia.utils.WifiSupport;


/**
 * Created by John on 2017/4/7.
 */

public class MacDialog extends Dialog implements View.OnClickListener{

    private EditText password_edit;
    private Button cancel_button;

    private Button cofirm_button;
    private Context mContext;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                Toast.makeText(getContext(),"正在获取中，请稍后...",Toast.LENGTH_LONG).show();
            }else if(msg.what == 2){
                Toast.makeText(getContext(),(String)msg.obj,Toast.LENGTH_LONG).show();
            }else if(msg.what == 3){
                Toast.makeText(getContext(),"获取成功",Toast.LENGTH_LONG).show();
                FileUtil.saveBitmap(getContext(),mBitmap);
                if(isShowing()){
                    dismiss();
                }
            }
        }
    };

    private  Bitmap mBitmap;
    public MacDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceUtils.init(getContext());
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_setting_mac, null);
        setContentView(view);
        initView(view);
        initListener();
    }


    private void initListener() {
        cancel_button.setOnClickListener(this);
        cofirm_button.setOnClickListener(this);
    }

    private void initView(View view) {
        password_edit = (EditText) view.findViewById(R.id.password_edit);
        cancel_button = (Button) view.findViewById(R.id.cancel_button);
        cofirm_button = (Button)view.findViewById(R.id.cofirm_button);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cofirm_button:{
                String mac = password_edit.getText().toString();
                getQrCode(mac);
                break;
            }
            case R.id.cancel_button:{
                dismiss();
                break;
            }
        }
    }

    private void getQrCode(String mac){
        if(TextUtils.isEmpty(mac)){
            Toast.makeText(getContext(),"MAC不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        QrCodeUtil.setMac(mac);
        QrCodeUtil.getQrString(new HttpListener() {
            @Override
            public void onSuccess(Object object) {
                XWBack xwBack = (XWBack) object;
                String urCode = xwBack.getData().getQrcode();
                if(TextUtils.isEmpty(urCode)){
                    Message msg = Message.obtain();
                    msg.what = 2;
                    msg.obj = "获取二维码数据失败";
                    mHandler.sendMessage(msg);
                    return;
                }
                Log.e("ZM","urCode = " +urCode);
                mBitmap = QrCodeUtil.createQRCodeBitmap(urCode, 480, 480);
                PreferenceUtils.commitString("qrCode",urCode);
                mHandler.sendEmptyMessage(3);
            }

            @Override
            public void onFail(String error) {
                Message msg = Message.obtain();
                msg.what = 2;
                msg.obj = error;
                mHandler.sendMessage(msg);
            }

        });
    }

}
