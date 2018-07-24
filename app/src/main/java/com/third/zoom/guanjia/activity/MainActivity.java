package com.third.zoom.guanjia.activity;

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
import com.third.zoom.guanjia.widget.AboutGJView;
import com.third.zoom.guanjia.widget.MainView;
import com.third.zoom.guanjia.widget.SelectHotWaterView;

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

    private MainView mainView;
    private SelectHotWaterView selectHotWaterView;
    private AboutGJView aboutGJView;

    @Override
    protected void toHandleMessage(Message msg) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void findViewAfterViewCreate() {
        mainView = (MainView) findViewById(R.id.mainView);
        selectHotWaterView = (SelectHotWaterView) findViewById(R.id.selectHotWaterView);
        aboutGJView = (AboutGJView) findViewById(R.id.aboutView);
    }

    @Override
    protected void initDataAfterFindView() {
        mainView.setBmvListener(new BmvSelectListener() {
            @Override
            public void itemSelectOpen(int position) {
                Log.e("ZM","itemSelectOpen = " + position);
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
                mainView.setVisibility(View.GONE);
                selectHotWaterView.setVisibility(View.VISIBLE);
                mainView.getBmvItem(1).performClick();
            }
        });

        selectHotWaterView.setBmvSelectListener(new BmvSelectListener() {
            @Override
            public void itemSelectOpen(int position) {
                Log.e("ZM","itemSelectOpen = " + position);
            }

            @Override
            public void itemSelectClose(int position) {
                Log.e("ZM","itemSelectClose = " + position);
            }
        });

        selectHotWaterView.setImageBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectHotWaterView.resetView();
                mainView.setVisibility(View.VISIBLE);
                selectHotWaterView.setVisibility(View.GONE);
                mainView.updateShow(0);
            }
        });

        aboutGJView.setImageBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.setVisibility(View.VISIBLE);
                aboutGJView.setVisibility(View.GONE);
                mainView.updateShow(0);
            }
        });
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

}
