package com.third.zoom.ytbus.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.third.zoom.R;
import com.third.zoom.common.base.ActivityFragmentInject;
import com.third.zoom.common.base.BaseActivity;
import com.third.zoom.common.serial.SerialInterface;
import com.third.zoom.common.utils.KeyEventUtils;
import com.third.zoom.common.utils.PreferenceUtils;
import com.third.zoom.common.widget.YTVideoView;
import com.third.zoom.ytbus.bean.PlayDataBean;
import com.third.zoom.ytbus.utils.Contans;

import java.io.File;

import static com.third.zoom.ytbus.utils.Contans.COM_00;
import static com.third.zoom.ytbus.utils.Contans.COM_01;
import static com.third.zoom.ytbus.utils.Contans.COM_02;
import static com.third.zoom.ytbus.utils.Contans.COM_03;
import static com.third.zoom.ytbus.utils.Contans.COM_04;
import static com.third.zoom.ytbus.utils.Contans.COM_05;
import static com.third.zoom.ytbus.utils.Contans.COM_06;
import static com.third.zoom.ytbus.utils.Contans.COM_12;

@ActivityFragmentInject(
        contentViewId = R.layout.yt_activity_main,
        hasNavigationView = false,
        hasToolbar = false
)
public class MainActivity extends BaseActivity {

    private static final int WHAT_OPEN_SERIAL = 10;

    //文件夹名
    private static final String YT_FILE_DIR = "YTBus";
    //配置数据
    private PlayDataBean playDataBean;
    //配置文件跟路径
    private String ytFileRootPath;

    private YTVideoView ytVideoView;
    private TextView ytAdTextView;
    private ImageView imgError;

    //播放广告时先保存电影的位置
    private int tempPlayPosition = 0;
    private String tempPlayPath = "";

    @Override
    protected void toHandleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_OPEN_SERIAL:
                openSerial();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void findViewAfterViewCreate() {
        ytVideoView = (YTVideoView) findViewById(R.id.ytVideoView);
        ytAdTextView = (TextView) findViewById(R.id.txt_ad_content);
        imgError = (ImageView) findViewById(R.id.img_error);
    }

    @Override
    protected void initDataAfterFindView() {
        PreferenceUtils.init(this);
        SerialInterface.serialInit(this);

        configFileInit();

        registerYTProReceiver();
        mHandler.sendEmptyMessageDelayed(WHAT_OPEN_SERIAL,2000);
    }


    /**
     * 配置文件初始化
     */
    private void configFileInit(){
        ytFileRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(ytFileRootPath,YT_FILE_DIR);
        if(file == null || !file.exists()){
            file.mkdirs();
        }
    }

    /**
     * 打开串口
     */
    private void openSerial(){
        try {
            if(playDataBean != null){
                String serialPort = playDataBean.getDefaultSerialPort();
                String serialRate = playDataBean.getDefaultSerialRate();
                SerialInterface.USEING_PORT = serialPort;
                SerialInterface.USEING_RATE = Integer.valueOf(serialRate);
                SerialInterface.openSerialPort(SerialInterface.USEING_PORT,SerialInterface.USEING_RATE);
                SerialInterface.changeActionReceiver(SerialInterface.getActions(SerialInterface.USEING_PORT));
            }else{
                SerialInterface.openSerialPort(SerialInterface.USEING_PORT,SerialInterface.USEING_RATE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private YTProReceiver ytProReceiver;
    private void registerYTProReceiver(){
        ytProReceiver = new YTProReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Contans.INTENT_YT_COM);
        registerReceiver(ytProReceiver,intentFilter);
    }

    private void unRegisterYTProReceiver(){
        if(ytProReceiver != null){
            unregisterReceiver(ytProReceiver);
        }
    }

    private class YTProReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(Contans.INTENT_YT_COM.equals(action)){
                int comValue = intent.getIntExtra("comValue",-1);
                handleCom(comValue);
            }
        }
    }


    /**
     * 处理协议
     * @param comValue
     */
    private void handleCom(int comValue){
        switch (comValue){
            case COM_00:
                KeyEventUtils.sendKeyEvent(KeyEvent.KEYCODE_DPAD_UP);
                break;
            case COM_01:
                KeyEventUtils.sendKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN);
                break;
            case COM_02:
                KeyEventUtils.sendKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT);
                break;
            case COM_03:
                KeyEventUtils.sendKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT);
                break;
            case COM_04:
                KeyEventUtils.sendKeyEvent(KeyEvent.KEYCODE_ENTER);
                break;
            case COM_05:
                doHandle05();
                break;
            case COM_06:
                KeyEventUtils.sendKeyEvent(KeyEvent.KEYCODE_BACK);
                break;
            case COM_12:
                toFileSystem();
                break;
        }
    }

    /**
     * 暂停/播放
     */
    private void doHandle05(){

    }

    /**
     * 调到文件系统
     */
    private void toFileSystem() {
        //TODO 如果文件管理已经打开，不处理
//        Intent toDetail = new Intent(this,SelectSpaceActivity.class);
//        startActivityForResult(toDetail,1);
    }

    private long timeLimit = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - timeLimit < 1500) {
                finish();
            } else {
                timeLimit = System.currentTimeMillis();
                Toast.makeText(this,"再按一次退出应用",Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
