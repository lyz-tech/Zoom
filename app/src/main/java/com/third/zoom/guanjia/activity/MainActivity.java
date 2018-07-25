package com.third.zoom.guanjia.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.third.zoom.R;
import com.third.zoom.common.base.ActivityFragmentInject;
import com.third.zoom.common.base.BaseActivity;
import com.third.zoom.common.listener.BmvSelectListener;
import com.third.zoom.common.utils.SystemUtil;
import com.third.zoom.guanjia.utils.Contans;
import com.third.zoom.guanjia.utils.IntentUtils;
import com.third.zoom.guanjia.widget.AboutGJView;
import com.third.zoom.guanjia.widget.MainView;
import com.third.zoom.guanjia.widget.SelectHotWaterView;
import com.third.zoom.guanjia.widget.WaitingView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者：Sky on 2018/7/13.
 * 用途：//TODO
 */
@ActivityFragmentInject(
        contentViewId = R.layout.gh_activity_main,
        hasNavigationView = false,
        hasToolbar = false
)
public class MainActivity extends BaseActivity {

    private static final int WHAT_NOT_OPERATION = 10;
    private static final int WHAT_NOT_OPERATION_1 = 11;
    private static final int WHAT_NOT_OPERATION_2 = 12;
    private static final long DEFAULT_TIME = 3 * 60 * 1000;
    private static final long DEFAULT_TIME_1 = 1 * 60 * 1000;
    private static final long DEFAULT_TIME_2 = 2 * 60 * 1000;

    private MainView mainView;
    private SelectHotWaterView selectHotWaterView;
    private AboutGJView aboutGJView;
    private WaitingView waitingView;

    @Override
    protected void toHandleMessage(Message msg) {
        switch (msg.what){
            case WHAT_NOT_OPERATION:
                operation(false);
                break;
            case WHAT_NOT_OPERATION_1:
                operation1();
                break;
            case WHAT_NOT_OPERATION_2:
                operation2();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterGJProReceiver();
    }

    @Override
    protected void findViewAfterViewCreate() {
        mainView = (MainView) findViewById(R.id.mainView);
        selectHotWaterView = (SelectHotWaterView) findViewById(R.id.selectHotWaterView);
        aboutGJView = (AboutGJView) findViewById(R.id.aboutView);
        waitingView = (WaitingView) findViewById(R.id.waitingView);
    }

    @Override
    protected void initDataAfterFindView() {
        registerGJProReceiver();
        mainView.setBmvListener(new BmvSelectListener() {
            @Override
            public void itemSelectOpen(int position) {
                Log.e("ZM","itemSelectOpen = " + position);
                sendActiveAction();
                mainView.setPositionShow(position);
                if(position == 0 || position == 2){
                    sendPro(true,"position = " + position);
                }else if(position == 3){
                    mainView.setVisibility(View.GONE);
                    aboutGJView.setVisibility(View.VISIBLE);
                    mainView.getBmvItem(3).performClick();
                }
            }

            @Override
            public void itemSelectClose(int position) {
                sendActiveAction();
                Log.e("ZM","itemSelectClose = " + position);
                mainView.updateShow(0);
                if(position == 0 || position == 2){
                    sendPro(false,"position = " + position);
                }
            }
        });

        mainView.setHotWaterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActiveAction();
                mainView.setVisibility(View.GONE);
                selectHotWaterView.setVisibility(View.VISIBLE);
                mainView.getBmvItem(1).performClick();
            }
        });

        selectHotWaterView.setBmvSelectListener(new BmvSelectListener() {
            @Override
            public void itemSelectOpen(int position) {
                sendActiveAction();
                Log.e("ZM","itemSelectOpen = " + position);
            }

            @Override
            public void itemSelectClose(int position) {
                sendActiveAction();
                Log.e("ZM","itemSelectClose = " + position);
            }
        });

        selectHotWaterView.setImageBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActiveAction();
                selectHotWaterView.resetView();
                mainView.setVisibility(View.VISIBLE);
                selectHotWaterView.setVisibility(View.GONE);
                mainView.updateShow(0);
            }
        });

        aboutGJView.setImageBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActiveAction();
                mainView.setVisibility(View.VISIBLE);
                aboutGJView.setVisibility(View.GONE);
                mainView.updateShow(0);
            }
        });

        runOperationTimer();
    }

    /**
     * 发送协议
     * @param isOpen
     * @param pro
     */
    private synchronized void sendPro(boolean isOpen,String pro){
        if(isOpen){
            Log.e("ZM","打开：" + pro);
        }else{
            Log.e("ZM","关闭：" + pro);
        }

    }

    private OperationReceiver operationReceiver;
    private void registerGJProReceiver(){
        operationReceiver = new OperationReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Contans.INTENT_GJ_ACTION_ACTIVE);
        registerReceiver(operationReceiver,intentFilter);
    }

    private void unRegisterGJProReceiver(){
        if(operationReceiver != null){
            unregisterReceiver(operationReceiver);
        }
    }

    private class OperationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            operation(true);
            runOperationTimer();
        }
    }

    /**
     * 3分钟没有操作，亮度设置为30%
     */
    private void operation(boolean isActive){
        if(isActive){
            SystemUtil.setScreenLight(this,200);
            waitingView.setVisibility(View.GONE);
        }else{
            SystemUtil.setScreenLight(this,80);
            waitingView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 1分钟没有操作，亮度设置为70%
     */
    private void operation1(){
        SystemUtil.setScreenLight(this,160);
    }

    /**
     * 2分钟没有操作，亮度设置为50%
     */
    private void operation2(){
        SystemUtil.setScreenLight(this,120);
    }

    /**
     * 操作倒计时
     * @param flag
     */
    private Timer operationTimer;
    private Timer operationTimer1;
    private Timer operationTimer2;
    private void runOperationTimer(){
        if(operationTimer != null){
            operationTimer.cancel();
            operationTimer = null;
        }
        if(operationTimer1 != null){
            operationTimer1.cancel();
            operationTimer1 = null;
        }
        if(operationTimer2 != null){
            operationTimer2.cancel();
            operationTimer2 = null;
        }
        Log.e("ZM","开始操作定时");
        operationTimer = new Timer();
        operationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(WHAT_NOT_OPERATION);
            }
        },DEFAULT_TIME);

        operationTimer1 = new Timer();
        operationTimer1.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(WHAT_NOT_OPERATION_1);
            }
        },DEFAULT_TIME_1);

        operationTimer2 = new Timer();
        operationTimer2.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(WHAT_NOT_OPERATION_2);
            }
        },DEFAULT_TIME_2);
    }

    private void sendActiveAction(){
        IntentUtils.sendBroadcast(MainActivity.this, Contans.INTENT_GJ_ACTION_ACTIVE);
    }

}
