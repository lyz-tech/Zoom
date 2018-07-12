package com.third.zoom.ytbus.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.third.zoom.R;
import com.third.zoom.common.base.ActivityFragmentInject;
import com.third.zoom.common.base.BaseActivity;
import com.third.zoom.common.serial.SerialInterface;
import com.third.zoom.common.utils.KeyEventUtils;
import com.third.zoom.ytbus.bean.PlayDataBean;
import com.third.zoom.ytbus.utils.Contans;

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

    /**
     * 配置数据
     */
    private PlayDataBean playDataBean;

    @Override
    protected void toHandleMessage(Message msg) {

    }

    @Override
    protected void findViewAfterViewCreate() {

    }

    @Override
    protected void initDataAfterFindView() {

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
//                doHandle05();
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
