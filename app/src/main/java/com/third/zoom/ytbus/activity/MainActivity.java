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
import com.third.zoom.common.serial.SerialUtils;
import com.third.zoom.common.utils.KeyEventUtils;
import com.third.zoom.common.utils.MountUtils;
import com.third.zoom.common.utils.PreferenceUtils;
import com.third.zoom.common.utils.SpaceFileUtil;
import com.third.zoom.common.widget.MarqueeTextViewV2;
import com.third.zoom.common.widget.YTVideoView;
import com.third.zoom.ytbus.bean.PlayDataBean;
import com.third.zoom.ytbus.utils.Contans;
import com.third.zoom.ytbus.utils.ParseFileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import static com.third.zoom.ytbus.utils.Contans.COM_20;
import static com.third.zoom.ytbus.utils.Contans.COM_21;
import static com.third.zoom.ytbus.utils.Contans.COM_22;
import static com.third.zoom.ytbus.utils.Contans.COM_23;
import static com.third.zoom.ytbus.utils.Contans.COM_24;
import static com.third.zoom.ytbus.utils.Contans.COM_25;
import static com.third.zoom.ytbus.utils.Contans.COM_26;
import static com.third.zoom.ytbus.utils.Contans.COM_27;
import static com.third.zoom.ytbus.utils.Contans.COM_28;
import static com.third.zoom.ytbus.utils.Contans.COM_29;
import static com.third.zoom.ytbus.utils.Contans.COM_30;

@ActivityFragmentInject(
        contentViewId = R.layout.yt_activity_main,
        hasNavigationView = false,
        hasToolbar = false
)
public class MainActivity extends BaseActivity {

    private static final int WHAT_OPEN_SERIAL = 10;
    private static final int WHAT_PLAY_AD = 11;
    private static final int WHAT_PLAY_TEXT = 12;

    private static final int WHAT_CURRENT_PLAY_TIME = 20;

    //记忆播放
    private static final String SP_KEY_PLAY_DIR = "playDir";
    private static final String SP_KEY_PLAY_PATH = "playPath";
    private static final String SP_KEY_PLAY_TIME = "playTime";
    //文件夹名
    private String CURRENT_VIDEO_FILE_DIR = "VIDEO1";
    private static final String[] VIDEO_FILE_DIRS = {"VIDEO1","VIDEO2",
            "VIDEO3","VIDEO4",
            "VIDEO5","VIDEO6",
            "VIDEO7","VIDEO8",
            "VIDEO9","VIDEO10",
            "VIDEO11","VIDEO12"};
    private static final String DEFAULT_VIDEO_FILE_DIR = "VIDEO1";
    private static final String YT_AD_FILE_DIR = "AD";
    private static final String FIRST_PLAY_AD = "AD001";
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
    private int AD_TIME = 20 * 1000;
    private int TEXT_TIME = 20 * 1000;
    //字幕内容
    private String textContent;

    private YTVideoView ytVideoView;
    private MarqueeTextViewV2 ytAdTextView;
    private ImageView imgError;

    //mnt/media

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
            case WHAT_CURRENT_PLAY_TIME:
                sendCurrentTime();
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
        SerialInterface.closeAllSerialPort();
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

        SerialInterface.serialInit(this);
        mHandler.sendEmptyMessageDelayed(WHAT_OPEN_SERIAL,2000);

        ytFileRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        configFileInit(ytFileRootPath);

        parseFile();

        ytVideoViewOnCompletionListener();
        ytTextViewOnCompletionListener();
        setOnErrorListener();
        registerYTProReceiver();

        mHandler.sendEmptyMessageDelayed(WHAT_CURRENT_PLAY_TIME,5000);
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
                tempPlayPath = ytVideoView.getVideoPath();
                if(TextUtils.isEmpty(tempPlayPath) || isADVideo(tempPlayPath)){
                    return;
                }
                tempPlayPosition = ytVideoView.getCurrentPosition();
                PreferenceUtils.commitString(SP_KEY_PLAY_PATH, tempPlayPath);
                if (tempPlayPosition > 0) {
                    PreferenceUtils.commitInt(SP_KEY_PLAY_TIME, tempPlayPosition);
                }
            }
        }
    }

    private void doOnResumeThings() {
        tempPlayPath = PreferenceUtils.getString(SP_KEY_PLAY_PATH, "");
        tempPlayPosition = PreferenceUtils.getInt(SP_KEY_PLAY_TIME, 0);
        runTextTimer(true);
        if(isFirst){
            isFirst = false;
            playFirstAD();
        }else{
            runADTimer(true);
            String urlPath = getIntent().getStringExtra("selectPath");
            if(TextUtils.isEmpty(urlPath)){
                if (TextUtils.isEmpty(tempPlayPath)) {
                    String next = getRandomVideoPath(CURRENT_VIDEO_FILE_DIR);
                    if(TextUtils.isEmpty(next)){
                        someError("未找到视频文件，请检查！");
                        return;
                    }else{
                        ytVideoView.setVideoPath(next);
                    }
                }else{
                    ytVideoView.setVideoPath(tempPlayPath);
                    if (tempPlayPosition > 0) {
                        ytVideoView.seekTo(tempPlayPosition);
                    }
                }
            }
        }
    }

    /**
     * 配置文件初始化
     */
    private void configFileInit(String rootPath){

        File videoFile = new File(rootPath,CURRENT_VIDEO_FILE_DIR);
        if(videoFile == null || !videoFile.exists()){
            videoFile.mkdirs();
        }
        File adFile = new File(rootPath,YT_AD_FILE_DIR);
        if(adFile == null || !adFile.exists()){
            adFile.mkdirs();
        }
        File ytFile = new File(rootPath,"YTBus");
        if(ytFile == null || !ytFile.exists()){
            ytFile.mkdirs();
        }
    }

    /**
     * 解析配置文件
     */
    private String YTBusConfigFilePath = "/YTBus/ytConfig.xml";
    private String PLAY_VIDEO_PATH = "";
    private void parseFile(){
        String lastDir = PreferenceUtils.getString(SP_KEY_PLAY_DIR,"");
        if(TextUtils.isEmpty(lastDir)){
            CURRENT_VIDEO_FILE_DIR = DEFAULT_VIDEO_FILE_DIR;
        }else{
            CURRENT_VIDEO_FILE_DIR = lastDir;
        }
        try {
            File configFile = new File(ytFileRootPath, YTBusConfigFilePath);
            playDataBean = ParseFileUtil.parsePlayData(new FileInputStream(configFile));
            AD_TIME = Integer.valueOf(playDataBean.getAdDuration()) * 1000;
            TEXT_TIME = Integer.valueOf(playDataBean.getTextDuration()) * 1000;
            textContent = playDataBean.getTextContent();
            PLAY_VIDEO_PATH = playDataBean.getPlayVideoPath();

            //判断盘是否存在,不存在默认为内部
            if(!TextUtils.isEmpty(PLAY_VIDEO_PATH)){
                String[] mountPaths = MountUtils.getStorageList(this);
                if(mountPaths != null && mountPaths.length > 1){
                    String mountPath = mountPaths[1];
                    ytFileRootPath = mountPath;
                    configFileInit(ytFileRootPath);
                    tempPlayPath = PreferenceUtils.getString(SP_KEY_PLAY_PATH, "");
                    tempPlayPosition = PreferenceUtils.getInt(SP_KEY_PLAY_TIME, 0);
                    if(!TextUtils.isEmpty(tempPlayPath)){
                        if(!tempPlayPath.contains(PLAY_VIDEO_PATH)){
                            CURRENT_VIDEO_FILE_DIR = DEFAULT_VIDEO_FILE_DIR;
                            tempPlayPath = "";
                            tempPlayPosition = 0;
                            PreferenceUtils.commitString(SP_KEY_PLAY_DIR,"");
                            PreferenceUtils.commitString(SP_KEY_PLAY_PATH,"");
                            PreferenceUtils.commitInt(SP_KEY_PLAY_TIME,0);
                        }
                    }
                }
            }

        }catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"配置文件有问题，请检查！",Toast.LENGTH_LONG).show();
        }
    }


    //开机播放AD001
    private void playFirstAD(){
        String[] temps = {".mp4", ".rmvb", ".avi", ".flv", ".mkv"};
        String exist = "";
        for (String temp : temps) {
            File ad001 = new File(ytFileRootPath + "/" + YT_AD_FILE_DIR + "/" + FIRST_PLAY_AD + temp);
            if(ad001 != null && ad001.exists()){
                exist = temp;
                break;
            }
        }
        File ad001 = new File(ytFileRootPath + "/" + YT_AD_FILE_DIR + "/" + FIRST_PLAY_AD + exist);
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
        imgError.setVisibility(View.GONE);
        tempPlayPosition = ytVideoView.getCurrentPosition();
        Log.e("ZM","当前保存位置 = " + tempPlayPosition);
        if(tempPlayPosition < 10000){
            tempPlayPosition = 0;
        }
        tempPlayPath = ytVideoView.getVideoPath();
        if (!TextUtils.isEmpty(tempPlayPath)) {
            PreferenceUtils.commitString(SP_KEY_PLAY_PATH, tempPlayPath);
        }
        if (tempPlayPosition > 0) {
            PreferenceUtils.commitInt(SP_KEY_PLAY_TIME, tempPlayPosition);
        }
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
            textContent = "未找到字幕内容，请检查！";
        }
        ytAdTextView.setVisibility(View.VISIBLE);
        ytAdTextView.setContent(textContent);
        runTextTimer(false);
    }

    private void setOnErrorListener(){
//        ytVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                Log.e("ZM","onError = " + i);
//                String next = getRandomVideoPath(CURRENT_VIDEO_FILE_DIR);
//                if(TextUtils.isEmpty(next)){
//                    someError("setOnErrorListener 2 未找到视频文件，请检查！");
//                }else{
//                    ytVideoView.setVideoPath(next);
//                }
//                return true;
//            }
//        });
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
                        String next = getRandomVideoPath(CURRENT_VIDEO_FILE_DIR);
                        if(TextUtils.isEmpty(next)){
                            someError("未找到视频文件，请检查！");
                        }else{
                            ytVideoView.setVideoPath(next);
                        }
                    }else{
                        int type = SpaceFileUtil.getFileType(tempPlayPath);
                        if(!isADVideo(tempPlayPath) && type >= 300 && type < 400 && new File(tempPlayPath).exists()){
                            ytVideoView.setVideoPath(tempPlayPath);
                            if (tempPlayPosition > 0) {
                                ytVideoView.seekTo(tempPlayPosition);
                            }
                        }else{
                            String next = getRandomVideoPath(CURRENT_VIDEO_FILE_DIR);
                            if(TextUtils.isEmpty(next)){
                                someError("未找到视频文件，请检查！");
                            }else{
                                ytVideoView.setVideoPath(next);
                            }
                        }
                    }
                }else{
                    Log.e("ZM","播放默认视频");
                    String next = getRandomVideoPath(CURRENT_VIDEO_FILE_DIR);
                    if(TextUtils.isEmpty(next)){
                        someError("未找到视频文件，请检查！");
                        ytVideoView.changePath("");
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
//                handleCom(comValue);
            }
        }
    }


    /**
     * 处理协议
     * @param comValue
     */
    private void handleCom(int comValue){
        if(isADVideo(ytVideoView.getVideoPath())){
            Toast.makeText(this,"当前正在播放广告，不允许操作！",Toast.LENGTH_LONG).show();
            return;
        }
        switch (comValue){
            case COM_00:
                if(isFileActivity){
                    KeyEventUtils.sendKeyEvent(KeyEvent.KEYCODE_DPAD_UP);
                }else{
                   playNextOrPre(false);
                }
                break;
            case COM_01:
                if(isFileActivity){
                    KeyEventUtils.sendKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN);
                }else{
                    playNextOrPre(true);
                }
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
            case COM_20:
            case COM_21:
            case COM_22:
            case COM_23:
            case COM_24:
            case COM_25:
            case COM_26:
            case COM_27:
            case COM_28:
            case COM_29:
            case COM_30:
                selectVideoDir(comValue);
                break;
        }
    }


    /**
     * 播放上一首、下一首
     */
    private void playRandomNext(){
        runADTimer(true);
        String next = getRandomVideoPath(CURRENT_VIDEO_FILE_DIR);
        if(TextUtils.isEmpty(next)){
            someError("未找到视频文件，请检查！");
            return;
        }else{
            ytVideoView.setVideoPath(next);
        }
    }

    /**
     * 播放上一首\下一首
     */
    private void playNextOrPre(boolean isNext){
        runADTimer(true);
        File videoFile = new File(ytFileRootPath, CURRENT_VIDEO_FILE_DIR);
        File[] videoFiles = videoFile.listFiles();
        ArrayList<String> fileList = new ArrayList<>();
        if(videoFiles != null && videoFiles.length > 0){
            for (int i = 0; i < videoFiles.length; i++) {
                int type = SpaceFileUtil.getFileType(videoFiles[i].getAbsolutePath());
                if (type >= 300 && type < 400){
                    fileList.add(videoFiles[i].getAbsolutePath());
                }
            }
            int index = -1;
            for (int i = 0; i < fileList.size(); i++) {
                String currentPath = ytVideoView.getVideoPath();
                if(!TextUtils.isEmpty(currentPath)){
                    if(currentPath.equals(fileList.get(i))){
                        index = i;
                        break;
                    }
                }
            }
            if(isNext){
                index = (index + 1 + fileList.size()) % fileList.size();
            }else{
                index = (index - 1 + fileList.size()) % fileList.size();
            }

            String next = fileList.get(index);
            ytVideoView.setVideoPath(next);
        }else{
            someError("未找到视频文件，请检查！");
        }
    }




    /**
     * 暂停/播放
     */
    private void doHandle05(){
        if(ytVideoView.isPlaying()) {
            ytVideoView.pause();
        }else{
            ytVideoView.start();
        }
    }

    /**
     * 调到文件系统
     */
    private boolean isFileActivity = false;
    private void toFileSystem() {
        if(isFileActivity){
            return;
        }
        isFileActivity = true;
        Intent toDetail = new Intent(this,FileSystemActivity.class);
        toDetail.putExtra("urlPath",PreferenceUtils.getString("selectPath",""));
        startActivityForResult(toDetail,1);
    }

    //返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            isFileActivity = false;
        }
        if(data == null){
            return;
        }
        String urlPath = data.getStringExtra("selectPath");
        if(requestCode == 1){
            isFileActivity = false;
            String[] paths = urlPath.split("/");
            String dirName = paths[paths.length - 2];
            Log.e("ZM","选择的DIRNAME = " + dirName);
            CURRENT_VIDEO_FILE_DIR = dirName;
            PreferenceUtils.commitString(SP_KEY_PLAY_DIR,dirName);
            ytVideoView.setVideoPath(urlPath);
            tempPlayPath = urlPath;
            tempPlayPosition = 0;
            PreferenceUtils.commitString(SP_KEY_PLAY_PATH, tempPlayPath);
            PreferenceUtils.commitInt(SP_KEY_PLAY_TIME, tempPlayPosition);
        }
    }

    private void selectVideoDir(int index){
        if(isADVideo(ytVideoView.getVideoPath())){
            Toast.makeText(this,"当前正在播放广告，不允许操作！",Toast.LENGTH_LONG).show();
            return;
        }
        try {
            if(index >= 20 && index <= 31){
                index = index - 20;
                String dirName = VIDEO_FILE_DIRS[index];
                File videoFile = new File(ytFileRootPath, dirName);
                File[] videoFiles = videoFile.listFiles();
                if(videoFiles == null || videoFiles.length == 0){
                    Toast.makeText(this,"当前文件夹没有视频文件,请重新选择",Toast.LENGTH_LONG).show();
                }else{
                    String urlPath = getRandomVideoPath(dirName);
                    if(TextUtils.isEmpty(urlPath)){
                        Toast.makeText(this,"当前文件夹没有视频文件,请重新选择",Toast.LENGTH_LONG).show();
                        return;
                    }
                    CURRENT_VIDEO_FILE_DIR = dirName;
                    PreferenceUtils.commitString(SP_KEY_PLAY_DIR,dirName);
                    ytVideoView.setVideoPath(urlPath);
                    tempPlayPath = urlPath;
                    tempPlayPosition = 0;
                    PreferenceUtils.commitString(SP_KEY_PLAY_PATH, tempPlayPath);
                    PreferenceUtils.commitInt(SP_KEY_PLAY_TIME, tempPlayPosition);
                    Toast.makeText(this,"切换到文件夹 " + CURRENT_VIDEO_FILE_DIR,Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e){
            Log.e("ZM",e.getMessage());
        }

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
        if(TextUtils.isEmpty(path)){
            return false;
        }
        if(path.contains("AD0") || path.contains("ad0")){
            return true;
        }
        return false;
    }

    /**
     * 获取随机video视频
     * @return
     */
    private String getRandomVideoPath(String dirPath){
        File videoFile = new File(ytFileRootPath, dirPath);
        File[] videoFiles = videoFile.listFiles();
        ArrayList<String> fileList = new ArrayList<>();
        if(videoFiles != null && videoFiles.length > 0){
            for (int i = 0; i < videoFiles.length; i++) {
                int type = SpaceFileUtil.getFileType(videoFiles[i].getAbsolutePath());
                if (type >= 300 && type < 400){
                    fileList.add(videoFiles[i].getAbsolutePath());
                }
            }

            if(fileList.size() == 0){
                return "";
            }
            int index = 0;
            try{
                Random random = new Random();
                index = random.nextInt(fileList.size()) ;
            }catch (Exception e){
                index = 0;
            }
            return fileList.get(index);
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
        Log.e("ZM","onKeyDown = " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - timeLimit < 1500) {
                finish();
            } else {
                timeLimit = System.currentTimeMillis();
                Toast.makeText(this,"再按一次退出应用",Toast.LENGTH_LONG).show();
            }
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if(isADVideo(ytVideoView.getVideoPath())){
                Toast.makeText(this,"当前正在播放广告，不允许操作！",Toast.LENGTH_LONG).show();
                return true;
            }
            if(!isFileActivity){
                playNextOrPre(false);
            }
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if(isADVideo(ytVideoView.getVideoPath())){
                Toast.makeText(this,"当前正在播放广告，不允许操作！",Toast.LENGTH_LONG).show();
                return true;
            }
            if(!isFileActivity){
                playNextOrPre(true);
            }
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_1) {
            selectVideoDir(20);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_2) {
            selectVideoDir(21);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_3) {
            selectVideoDir(22);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_4) {
            selectVideoDir(23);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_5) {
            selectVideoDir(24);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_6) {
            selectVideoDir(25);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_7) {
            selectVideoDir(26);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_8) {
            selectVideoDir(27);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_9) {
            selectVideoDir(28);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_0) {
            selectVideoDir(29);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_STAR) {
            selectVideoDir(30);
            return true;
        }else if (keyCode == 10002) {
            selectVideoDir(31);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            if(isADVideo(ytVideoView.getVideoPath())){
                Toast.makeText(this,"当前正在播放广告，不允许操作！",Toast.LENGTH_LONG).show();
                return true;
            }
            doHandle05();
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_MENU) {
            if(isADVideo(ytVideoView.getVideoPath())){
                Toast.makeText(this,"当前正在播放广告，不允许操作！",Toast.LENGTH_LONG).show();
                return true;
            }
            toFileSystem();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void sendCurrentTime(){
        if(ytVideoView != null && ytVideoView.isPlaying()){
            int time = ytVideoView.getCurrentPosition();
            byte[] timeBytes = SerialUtils.putInt(time);
            String hexStirng = SerialUtils.bytes2HexString(timeBytes);

//            SerialInterface.sendHexMsg2SerialPort(SerialInterface.USEING_PORT,"ff");
        }
        mHandler.sendEmptyMessageDelayed(WHAT_CURRENT_PLAY_TIME,1000);
    }


}
