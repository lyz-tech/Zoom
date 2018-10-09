package com.third.zoom.guanjia.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.third.zoom.R;
import com.third.zoom.common.base.ActivityFragmentInject;
import com.third.zoom.common.base.BaseActivity;
import com.third.zoom.common.listener.BmvSelectListener;
import com.third.zoom.common.listener.NormalListener;
import com.third.zoom.common.serial.SerialInterface;
import com.third.zoom.common.utils.PreferenceUtils;
import com.third.zoom.common.utils.SystemUtil;
import com.third.zoom.guanjia.bean.DeviceDetailStatus;
import com.third.zoom.guanjia.handler.GJProHandler;
import com.third.zoom.guanjia.utils.Contans;
import com.third.zoom.guanjia.utils.GJProUtil;
import com.third.zoom.guanjia.utils.GJProV2Util;
import com.third.zoom.guanjia.utils.IntentUtils;
import com.third.zoom.guanjia.widget.AboutGJView;
import com.third.zoom.guanjia.widget.ErrorView;
import com.third.zoom.guanjia.widget.MainView;
import com.third.zoom.guanjia.widget.NavTopView;
import com.third.zoom.guanjia.widget.SelectHotWaterView;
import com.third.zoom.guanjia.widget.SelectWaterDeviceView;
import com.third.zoom.guanjia.widget.WaitingView;

import java.util.Timer;
import java.util.TimerTask;

import static com.third.zoom.guanjia.utils.Contans.INTENT_GJ_ACTION_ACTIVE;

/**
 * 作者：Sky on 2018/7/13.
 * 用途：//TODO
 */
@ActivityFragmentInject(
        contentViewId = R.layout.gh_activity_main,
        hasNavigationView = false,
        hasToolbar = false
)
/**
 * 1、获取数据、获取成功2，获取不成功，重复获取
 * 2、同步本地状态
 * 3、点击发送协议、如果返回CC，表示发送成功、如果没返回，100ms后继续发送、重复3次，3次不行，提示错误
 *
 */
public class MainActivity extends BaseActivity {

    private static final int WHAT_OPEN_SERIAL = 9;
    private static final int WHAT_NOT_OPERATION = 10;
    private static final int WHAT_NOT_OPERATION_1 = 11;
    private static final int WHAT_NOT_OPERATION_2 = 12;
    private static final int WHAT_DATA_REPEAT = 15;
    private static final int WHAT_DATA_TEST= 16;
    private static final int WHAT_DATA_SEND_NORMAL= 17;

    private static final long DEFAULT_TIME = 3 * 60 * 1000;
    private static final long DEFAULT_TIME_1 = 1 * 60 * 1000;
    private static final long DEFAULT_TIME_2 = 2 * 60 * 1000;

    private MainView mainView;
    private SelectHotWaterView selectHotWaterView;
    private AboutGJView aboutGJView;
    private WaitingView waitingView;
    private SelectWaterDeviceView waterDeviceView;
    private NavTopView navTopView;
    private ErrorView errorView;

    private boolean isSending = false;  //是否正在发送协议
    private String proTempString = "";

    @Override
    protected void toHandleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_OPEN_SERIAL:
                openSerial();
                break;
            case WHAT_NOT_OPERATION:
                operation(false);
                break;
            case WHAT_NOT_OPERATION_1:
                operation1();
                break;
            case WHAT_NOT_OPERATION_2:
                operation2();
                break;
            case WHAT_DATA_REPEAT:
                sendWaterData();
                break;
            case WHAT_DATA_TEST:
                if(errorView.getVisibility() != View.VISIBLE){
                    errorView.setVisibility(View.VISIBLE);
                }else{
                    errorView.setVisibility(View.GONE);
                }
                mHandler.sendEmptyMessageDelayed(WHAT_DATA_TEST,10000);
                break;
            case WHAT_DATA_SEND_NORMAL:
                SerialInterface.sendHexMsg2SerialPort(SerialInterface.USEING_PORT,GJProV2Util.getNormalPro());
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(WHAT_DATA_REPEAT);
        unRegisterGJProReceiver();
        unRegistTimeReceiver();
        SerialInterface.closeAllSerialPort();
    }

    @Override
    protected void findViewAfterViewCreate() {
        mainView = (MainView) findViewById(R.id.mainView);
        selectHotWaterView = (SelectHotWaterView) findViewById(R.id.selectHotWaterView);
        aboutGJView = (AboutGJView) findViewById(R.id.aboutView);
        waitingView = (WaitingView) findViewById(R.id.waitingView);
        waterDeviceView = (SelectWaterDeviceView) findViewById(R.id.waterDeviceView);
        navTopView = (NavTopView) findViewById(R.id.topView);
        errorView = (ErrorView) findViewById(R.id.errorView);
    }

    @Override
    protected void initDataAfterFindView() {
        registerGJProReceiver();
        registTimeReceiver();

        SerialInterface.serialInit(this);
        mHandler.sendEmptyMessageDelayed(WHAT_OPEN_SERIAL,1500);

//        proTempString = GJProV2Util.getNormalPro();
//        mHandler.sendEmptyMessageDelayed(WHAT_DATA_REPEAT,6000);

        initLVDialogView();

        init1();

        changeTime();

    }

    private void init1(){
        PreferenceUtils.init(this);
        boolean isBootFirst = PreferenceUtils.getBoolean("isBootFirst",true);
        if(isBootFirst){
            navTopView.setVisibility(View.GONE);
            waterDeviceView.setVisibility(View.VISIBLE);
            waterDeviceView.setOnListener(new NormalListener() {
                @Override
                public void onActive(Object object) {
                    int type = (int) object;
                    PreferenceUtils.commitBoolean("isBootFirst",false);
                    PreferenceUtils.commitInt("waterType",type);
                    PreferenceUtils.commitLong("waterTime",System.currentTimeMillis());
                    saveTime = System.currentTimeMillis();
                    init2();
                }
            });
        }else{
            init2();
        }
    }

    private void init2(){
        waterDeviceView.setVisibility(View.GONE);
        navTopView.setVisibility(View.VISIBLE);
        mainView.setBmvListener(new BmvSelectListener() {
            @Override
            public void itemSelectOpen(int position) {
                Log.e("ZM", "itemSelectOpen = " + position);
                sendActiveAction();
                mainView.setPositionShow(position);
                if (position == 1) {
                    sendPro(true, GJProUtil.DEFAULT_NORMAL_WATER_TH,
                            GJProUtil.DEFAULT_WATER_ML);
                } else if (position == 2) {
                    mainView.setVisibility(View.GONE);
                    aboutGJView.setVisibility(View.VISIBLE);
                    mainView.getBmvItem(2).performClick();
                }
            }

            @Override
            public void itemSelectClose(int position) {
                sendActiveAction();
                if(position == 0 ){
                    mainView.setCurClickIndex(-1);
                }
                Log.e("ZM", "itemSelectClose = " + position);
                mainView.updateShow(0);
                if (position == 1) {
                    mainView.setCurClickIndex(-1);
                    sendPro(false, GJProUtil.DEFAULT_NORMAL_WATER_TH,
                            GJProUtil.DEFAULT_WATER_ML);
                }
            }
        });

        mainView.setHotWaterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActiveAction();
                mainView.setVisibility(View.GONE);
                selectHotWaterView.setVisibility(View.VISIBLE);
                mainView.getBmvItem(0).performClick();
            }
        });

        selectHotWaterView.setBmvSelectListener(new BmvSelectListener() {
            @Override
            public void itemSelectOpen(int position) {
                sendActiveAction();
                Log.e("ZM", "itemSelectOpen = " + position);
                sendPro(true, GJProUtil.getWaterThByPosition(position),
                        GJProUtil.DEFAULT_WATER_ML);
            }

            @Override
            public void itemSelectClose(int position) {
                sendActiveAction();
                Log.e("ZM", "itemSelectClose = " + position);
                sendPro(false, GJProUtil.getWaterThByPosition(position),
                        GJProUtil.DEFAULT_WATER_ML);
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
                changeTime();
            }
        });

        runOperationTimer();

        normalDialog();

        changeTime();
    }

    private synchronized boolean sendPro(boolean isOpen, int waterTh, int waterMl) {
        if(exitDay <= 15){
            showLVDialog(exitDay);
            return false;
        }
        String pro = GJProV2Util.getWaterPro(isOpen,waterTh,waterMl);
        mHandler.removeMessages(WHAT_DATA_REPEAT);
        proTempString = pro;
        if(!TextUtils.isEmpty(pro)){
            SerialInterface.sendHexMsg2SerialPort(SerialInterface.USEING_PORT,pro);
            mHandler.sendEmptyMessageDelayed(WHAT_DATA_REPEAT,500);
        }
        return isSending;
    }

    private OperationReceiver operationReceiver;
    private void registerGJProReceiver() {
        operationReceiver = new OperationReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Contans.INTENT_GJ_ACTION_ACTIVE);
        intentFilter.addAction(Contans.INTENT_GJ_ACTION_PRO_COME);
        registerReceiver(operationReceiver, intentFilter);
    }

    private void unRegisterGJProReceiver() {
        if (operationReceiver != null) {
            unregisterReceiver(operationReceiver);
        }
    }

    private class OperationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Contans.INTENT_GJ_ACTION_ACTIVE)){
                operation(true);
                runOperationTimer();
            }else if(action.equals(Contans.INTENT_GJ_ACTION_PRO_COME)){
                String comValue = intent.getStringExtra("comValue");
                if(TextUtils.isEmpty(comValue)){
                    return;
                }

                //CC 01 10 22 32 00 00 65
                if(comValue.length() > 6){
                    String error = comValue.substring(4,6);
//                    errorHandle(error);
                }
            }
        }
    }

    /**
     * 3分钟没有操作，亮度设置为30%
     */
    private void operation(boolean isActive) {
        if (isActive) {
            SystemUtil.setScreenLight(this, 200);
            waitingView.setVisibility(View.GONE);
        } else {
            Log.e("ZM", "3分钟没有操作");
            SystemUtil.setScreenLight(this, 80);
            waitingView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 1分钟没有操作，亮度设置为70%
     */
    private void operation1() {
        Log.e("ZM", "1分钟没有操作");
        SystemUtil.setScreenLight(this, 160);
    }

    /**
     * 2分钟没有操作，亮度设置为50%
     */
    private void operation2() {
        Log.e("ZM", "2分钟没有操作");
        SystemUtil.setScreenLight(this, 120);
    }

    /**
     * 操作倒计时
     *
     * @param flag
     */
    private Timer operationTimer;
    private Timer operationTimer1;
    private Timer operationTimer2;

    private void runOperationTimer() {
        if (operationTimer != null) {
            operationTimer.cancel();
            operationTimer = null;
        }
        if (operationTimer1 != null) {
            operationTimer1.cancel();
            operationTimer1 = null;
        }
        if (operationTimer2 != null) {
            operationTimer2.cancel();
            operationTimer2 = null;
        }
        Log.e("ZM", "开始操作定时");
        operationTimer = new Timer();
        operationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(WHAT_NOT_OPERATION);
            }
        }, DEFAULT_TIME);

        operationTimer1 = new Timer();
        operationTimer1.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(WHAT_NOT_OPERATION_1);
            }
        }, DEFAULT_TIME_1);

        operationTimer2 = new Timer();
        operationTimer2.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(WHAT_NOT_OPERATION_2);
            }
        }, DEFAULT_TIME_2);
    }

    private void sendActiveAction() {
        IntentUtils.sendBroadcast(MainActivity.this, INTENT_GJ_ACTION_ACTIVE);
    }

    /**
     * 打开串口
     */
    private void openSerial(){
        try {
            SerialInterface.openSerialPort(SerialInterface.USEING_PORT,SerialInterface.USEING_RATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long timeLimit = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("ZM", "onKeyDown = " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - timeLimit < 1500) {
                finish();
            } else {
                timeLimit = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private AlertDialog normalDialog;
    private void normalDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
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

    /**
     * 轮询获取状态数据
     */
    private void sendWaterData(){
        SerialInterface.sendHexMsg2SerialPort(SerialInterface.USEING_PORT,proTempString);
        mHandler.sendEmptyMessageDelayed(WHAT_DATA_REPEAT,600);
    }

    /**
     * 数据处理
     * @param data
     */
    private DeviceDetailStatus deviceDetailStatus;
    private void handleWaterData(String data){
        deviceDetailStatus = GJProUtil.parseData(data);
    }

    private TimeChangeReceiver timeChangeReceiver;
    private void registTimeReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);//设置了系统时区
        intentFilter.addAction(Contans.INTENT_GJ_ACTION_LV_SET);//设置了系统时区
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);
    }

    private void unRegistTimeReceiver(){
        unregisterReceiver(timeChangeReceiver);
    }

    private long saveTime = 0;
    private int currentType = 2;
    private class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            changeTime();
        }
    }

    private static final int DEFAULT_HOME_DAY = 180;
    private static final int DEFAULT_SHARE_DAY = 120;
    private static final int DEFAULT_EXIT_DAY = 15;
    private int exitDay = 100;
    private void changeTime(){
        saveTime = PreferenceUtils.getLong("waterTime",0);
        currentType = PreferenceUtils.getInt("waterType",1);
        long curnTime = System.currentTimeMillis();
        long indexTime = curnTime - saveTime;
        int last = (int) (indexTime / (24 * 60 * 60 * 1000));
        if(last >= 0){
            if(currentType == 1){
                navTopView.setWaterTime(DEFAULT_SHARE_DAY - last);
                aboutGJView.setLVTime(currentType,DEFAULT_SHARE_DAY - last );
                exitDay = DEFAULT_SHARE_DAY - last;
                if(DEFAULT_SHARE_DAY - last <= DEFAULT_EXIT_DAY && saveTime != 0){
                    showLVDialog(DEFAULT_SHARE_DAY - last);
                }
            }else{
                navTopView.setWaterTime(DEFAULT_HOME_DAY - last );
                aboutGJView.setLVTime(currentType,DEFAULT_HOME_DAY - last);
                exitDay = DEFAULT_HOME_DAY - last;
                if(DEFAULT_HOME_DAY - last <= DEFAULT_EXIT_DAY && saveTime != 0){
                    showLVDialog(DEFAULT_HOME_DAY - last);
                }
            }
        }
    }

    private  AlertDialog LVDialog;
    private String LV_HEAD = "滤芯将在 ";
    private String LV_END = " 天后到期，到期后水机将停止工作，请尽快联系我们的工作人员对产氢设备进行更换！";
    private TextView lvTextView;
    private void initLVDialogView(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View capView = View.inflate(this, R.layout.gj_widget_lv_notice,null);
        lvTextView = (TextView) capView.findViewById(R.id.txt_message);
        lvTextView.setText(LV_HEAD + DEFAULT_EXIT_DAY + LV_END);
        Button btnCancel = (Button) capView.findViewById(R.id.btn_cancel);
        btnCancel.setVisibility(View.GONE);
        Button btnOk = (Button) capView.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lvDialogDismiss();
            }
        });
        builder.setView(capView);
        LVDialog = builder.create();
    }

    public void showLVDialog(int day){
        if(!LVDialog.isShowing()){
            if(day < 0){
                day = 0;
            }
            lvTextView.setText(LV_HEAD + day + LV_END);
            LVDialog.show();
            Window dialogWindow = LVDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = getResources().getDisplayMetrics(); // 获取屏幕宽、高用
            lp.width = (int) (d.widthPixels * 0.5); // 宽度设置为屏幕的0.8
//            lp.height = (int) (d.heightPixels * 0.5); // 宽度设置为屏幕的0.8
            dialogWindow.setAttributes(lp);
        }
    }

    public void lvDialogDismiss(){
        if(LVDialog.isShowing()){
            LVDialog.dismiss();
        }
    }


    /**
     * 错误处理
     * @param error
     */
    private void errorHandle(String error){
        Log.e("ZM","PRO = " + error);
        String result  = "";
        if(error.equals(GJProHandler.ERROR_IN_OPEN)){
            result = "进水温度传感器开路(-20℃以下)";
            errorView.setVisibility(View.VISIBLE);
            errorView.setErrorMessage(result);
        }else if(error.equals(GJProHandler.ERROR_IN_CLOSE)){
            result = "进水温度传感器短路(50℃以上)";
            errorView.setErrorMessage(result);
            errorView.setVisibility(View.VISIBLE);
        }else if(error.equals(GJProHandler.ERROR_OUT_OPEN)){
            result = "出水温度传感器开路(-20℃以下)";
            errorView.setVisibility(View.VISIBLE);
            errorView.setErrorMessage(result);
        }else if(error.equals(GJProHandler.ERROR_OUT_CLOSE)){
            result = "出水温度传感器短路(50℃以上)";
            errorView.setVisibility(View.VISIBLE);
            errorView.setErrorMessage(result);
        }else if(error.equals(GJProHandler.ERROR_FLOW_METER)){
            result = "设定温度不为 0℃时，1 秒之内无流量或流量超过 1200cc/min";
            errorView.setVisibility(View.VISIBLE);
            errorView.setErrorMessage(result);
        }else if(error.equals(GJProHandler.ERROR_NONE)){
            //没有错误
            errorView.setVisibility(View.GONE);
        }

    }

}
