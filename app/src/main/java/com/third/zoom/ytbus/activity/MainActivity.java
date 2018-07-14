package com.third.zoom.ytbus.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.third.zoom.R;
import com.third.zoom.common.base.ActivityFragmentInject;
import com.third.zoom.common.base.BaseActivity;
import com.third.zoom.common.listener.MarqueeCompletedListener;
import com.third.zoom.common.serial.SerialInterface;
import com.third.zoom.common.utils.KeyEventUtils;
import com.third.zoom.common.utils.PreferenceUtils;
import com.third.zoom.common.widget.MarqueeTextViewV2;
import com.third.zoom.common.widget.YTVideoView;
import com.third.zoom.ytbus.bean.PlayDataBean;
import com.third.zoom.ytbus.utils.Contans;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
    private static final int WHAT_PLAY_AD = 11;
    private static final int WHAT_PLAY_TEXT = 12;

    //记忆播放
    private static final String SP_KEY_PLAY_PATH = "playPath";
    private static final String SP_KEY_PLAY_TIME = "playTime";
    //文件夹名
    private static final String YT_VIDEO_FILE_DIR = "VIDEO";
    private static final String YT_AD_FILE_DIR = "AD";
    private static final String FIRST_PLAY_AD = "AD001.mp4";
    //配置数据
    private PlayDataBean playDataBean;
    //配置文件跟路径
    private String ytFileRootPath;
    //播放广告时先保存电影的位置
    private int tempPlayPosition = 0;
    private String tempPlayPath = "";
    //首次flag
    private boolean isFirst = true;
    //广告位置
    private int adIndex = 0;
    //定时器
    private Timer adTimer;
    private Timer textTimer;
    //广告时间
    private long AD_TIME = 20 * 1000;
    private long TEXT_TIME = 20 * 1000;
    //字幕内容
    private String textContent;

    private YTVideoView ytVideoView;
    private MarqueeTextViewV2 ytAdTextView;
    private ImageView imgError;

    @Override
    protected void toHandleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_OPEN_SERIAL:
                openSerial();
                break;
            case WHAT_PLAY_AD:
                playAD();
                break;
            case WHAT_PLAY_TEXT:
                playText();
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
        unRegisterYTProReceiver();
//        SerialInterface.closeAllSerialPort();
    }

    @Override
    protected void findViewAfterViewCreate() {
        ytVideoView = (YTVideoView) findViewById(R.id.ytVideoView);
        ytAdTextView = (MarqueeTextViewV2) findViewById(R.id.txt_ad_content);
        imgError = (ImageView) findViewById(R.id.img_error);
    }

    @Override
    protected void initDataAfterFindView() {
        PreferenceUtils.init(this);

//        SerialInterface.serialInit(this);
//        mHandler.sendEmptyMessageDelayed(WHAT_OPEN_SERIAL,2000);

        configFileInit();

        ytVideoViewOnCompletionListener();
        ytTextViewOnCompletionListener();

        registerYTProReceiver();
    }


    @Override
    protected void onPause() {
        super.onPause();
        doOnPauseThings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doOnResumeThings();
    }

    /**
     * 取消message，记录播放路径跟位置
     */
    private void doOnPauseThings() {
        runADTimer(false);
        runTextTimer(false);
        if (ytVideoView != null) {
            if (ytVideoView.isPlaying()) {
                tempPlayPosition = ytVideoView.getCurrentPosition();
                tempPlayPath = ytVideoView.getVideoPath();
                if (!TextUtils.isEmpty(tempPlayPath)) {
                    return;
                }
                PreferenceUtils.commitString(SP_KEY_PLAY_PATH, tempPlayPath);
                if (tempPlayPosition > 0) {
                    PreferenceUtils.commitInt(SP_KEY_PLAY_TIME, tempPlayPosition);
                }
            }
        }
    }

    private void doOnResumeThings() {
        runTextTimer(true);
        if(isFirst){
            isFirst = false;
            playFirstAD();
        }else{
            tempPlayPath = PreferenceUtils.getString(SP_KEY_PLAY_PATH, "");
            tempPlayPosition = PreferenceUtils.getInt(SP_KEY_PLAY_TIME, 0);
            if (TextUtils.isEmpty(tempPlayPath)) {
                String next = getRandomVideoPath();
                if(TextUtils.isEmpty(next)){
                    someError("未找到视频文件，请检查！");
                    return;
                }else{
                    ytVideoView.setVideoPath(next);
                }
            }
            ytVideoView.setVideoPath(tempPlayPath);
            if (tempPlayPosition > 0) {
                ytVideoView.seekTo(tempPlayPosition);
            }
        }
    }

    /**
     * 配置文件初始化
     */
    private void configFileInit(){
        ytFileRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File videoFile = new File(ytFileRootPath,YT_VIDEO_FILE_DIR);
        if(videoFile == null || !videoFile.exists()){
            videoFile.mkdirs();
        }
        File adFile = new File(ytFileRootPath,YT_AD_FILE_DIR);
        if(adFile == null || !adFile.exists()){
            adFile.mkdirs();
        }
    }

    //开机播放AD001
    private void playFirstAD(){
        File ad001 = new File(ytFileRootPath + "/" + YT_AD_FILE_DIR + "/" + FIRST_PLAY_AD);
        if(ad001 != null && ad001.exists()){
            ytVideoView.setVideoPath(ad001.getAbsolutePath());
        }else {
            someError("未找到AD001视频文件,请检查！");
        }
    }

    /**
     * 播放广告
     */
    private void playAD(){
        Log.e("ZM","播放广告");
        tempPlayPosition = ytVideoView.getCurrentPosition();
        tempPlayPath = ytVideoView.getVideoPath();
        File videoFile = new File(ytFileRootPath, YT_AD_FILE_DIR);
        File[] adFiles = videoFile.listFiles();
        ArrayList<String> adList = new ArrayList<>();
        if(adFiles != null && adFiles.length > 0){
            //过滤掉AD001
            for (int i = 0; i < adFiles.length; i++) {
                if(!adFiles[i].getName().contains(FIRST_PLAY_AD)){
                    adList.add(adFiles[i].getAbsolutePath());
                }
            }
            if(adIndex > (adList.size() - 1)){
                adIndex = 0;
            }
            ytVideoView.setVideoPath(adList.get(adIndex));
            runADTimer(false);
        }
        adIndex++;
    }

    /**
     * 播放字幕
     */
    private void playText(){
        Log.e("ZM","播放字幕");
        if(TextUtils.isEmpty(textContent)){
            textContent = "这是测试文本。这是测试文本。这是测试文本。这是测试文本。";
        }
        ytAdTextView.setVisibility(View.VISIBLE);
        ytAdTextView.setContent(textContent);
        runTextTimer(false);
    }

    //先播完ad001，播放之后播默认，30秒后播广告，广告播完在回来，30秒后播广告
    private void ytVideoViewOnCompletionListener() {
        ytVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("ZM","播放完成");
                String videoPath = ytVideoView.getVideoPath();
                //广告播完回到默认播放,如果是默认播完，随机播放VIDEO文件夹下的某个视频
                if(isADVideo(videoPath)){
                    runADTimer(true);
                    Log.e("ZM","播放默认视频");
                    if(TextUtils.isEmpty(tempPlayPath)){
                        String next = getRandomVideoPath();
                        if(TextUtils.isEmpty(next)){
                            someError("未找到视频文件，请检查！");
                        }else{
                            ytVideoView.setVideoPath(next);
                        }
                    }else{
                        ytVideoView.setVideoPath(tempPlayPath);
                        if (tempPlayPosition > 0) {
                            ytVideoView.seekTo(tempPlayPosition);
                        }
                    }
                }else{
                    Log.e("ZM","播放默认视频");
                    String next = getRandomVideoPath();
                    if(TextUtils.isEmpty(next)){
                        someError("未找到视频文件，请检查！");
                    }else{
                        ytVideoView.setVideoPath(next);
                    }
                }
            }
        });
    }

    private void ytTextViewOnCompletionListener(){
        ytAdTextView.setMarqueeCompletedListener(new MarqueeCompletedListener() {
            @Override
            public void onCompleted(View mTextView) {
                ytAdTextView.setVisibility(View.INVISIBLE);
                runTextTimer(true);
            }
        });
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

    /**
     * 找不到资源
     */
    private void someError(String error){
        imgError.setVisibility(View.VISIBLE);

        Toast.makeText(this,error,Toast.LENGTH_LONG).show();
    }

    /**
     * 判断是否为广告视频
     * @param path
     * @return
     */
    private boolean isADVideo(String path){
        if(path.contains("AD0") || path.contains("ad0")){
            return true;
        }
        return false;
    }

    /**
     * 获取随机video视频
     * @return
     */
    private String getRandomVideoPath(){
        File videoFile = new File(ytFileRootPath, YT_VIDEO_FILE_DIR);
        File[] videoFiles = videoFile.listFiles();
        if(videoFiles != null && videoFiles.length > 0){
            Random random = new Random();
            int index = random.nextInt(videoFiles.length - 1);
            return videoFiles[index].getAbsolutePath();
        }else{
            return "";
        }
    }

    /**
     * 广告定时器
     * @param flag
     */
    private void runADTimer(boolean flag){
        if(adTimer != null){
            adTimer.cancel();
            adTimer = null;
        }
        if(!flag){
            Log.e("ZM","取消广告定时");
            return;
        }
        Log.e("ZM","开始广告定时");
        adTimer = new Timer();
        adTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(WHAT_PLAY_AD);
            }
        },AD_TIME);
    }

    /**
     * 字幕定时器
     * @param flag
     */
    private void runTextTimer(boolean flag){
        if(textTimer != null){
            textTimer.cancel();
            textTimer = null;
        }
        if(!flag){
            Log.e("ZM","取消字幕定时");
            return;
        }
        Log.e("ZM","开始字幕定时");
        textTimer = new Timer();
        textTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(WHAT_PLAY_TEXT);
            }
        },TEXT_TIME);
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
