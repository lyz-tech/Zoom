package com.third.zoom.ytbus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.third.zoom.R;
import com.third.zoom.common.base.ActivityFragmentInject;
import com.third.zoom.common.base.BaseActivity;
import com.third.zoom.common.utils.MountUtils;
import com.third.zoom.common.utils.PreferenceUtils;
import com.third.zoom.common.utils.SpaceFileUtil;
import com.third.zoom.ytbus.adapter.FileDetailAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Sky on 2018/7/12.
 * 用途：简易文件管理器
 */
@ActivityFragmentInject(
        contentViewId = R.layout.yt_activity_filesystem,
        hasNavigationView = false,
        hasToolbar = false
)
public class FileSystemActivity extends BaseActivity{

    private RecyclerView recyclerView;
    private LinearLayoutManager lm;
    private TextView txtTitle;
    private ImageView imgBack;

    private FileDetailAdapter fileDetailAdapter;
    //首层url
    private String urlPath = "";
    //当前
    private String currentPath = "";
    //是否是第一次进去
    private boolean firstFlag = true;

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
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        txtTitle = (TextView) findViewById(R.id.txt_path_name);
        imgBack  = (ImageView) findViewById(R.id.img_back);
    }

    @Override
    protected void initDataAfterFindView() {
        PreferenceUtils.init(this);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(urlPath)){
                    finish();
                }else{
                    if(urlPath.equals(currentPath)){
                        urlPath = "";
                        selectSpace();
                    }else{
                        File file = new File(currentPath);
                        updateData(file.getParent());
                    }
                }

            }
        });

        initAdapter();
        intFiles();
        String path = getIntent().getStringExtra("urlPath");
        if(TextUtils.isEmpty(path)){
            selectSpace();
        }else{
            selectPath(path);
        }

    }

    List<String> spaceFiles = new ArrayList<>();
    private void intFiles(){
        String[] mountPaths = MountUtils.getStorageList(this);
        if(mountPaths != null && mountPaths.length > 0){
            for (int i = 0; i < mountPaths.length; i++) {
                Log.e("ZM","mountPaths = " + mountPaths[i]);
                spaceFiles.add(mountPaths[i]);
            }
        }else{
            spaceFiles.add(Environment.getExternalStorageDirectory().getAbsolutePath());
        }
    }

    private void selectSpace(){
        PreferenceUtils.commitString("selectPath","");
        txtTitle.setText("File manager");
        fileDetailAdapter.updateDatas(spaceFiles);
        recyclerView.scrollToPosition(0);
    }

    private void selectPath(String path){
        firstFlag = false;
        for (String file : spaceFiles) {
            if(path.startsWith(file)){
                urlPath = file;
                break;
            }
        }
        txtTitle.setText(path);
        currentPath = path;
        updateData(path);
    }

    private void initAdapter(){
        fileDetailAdapter = new FileDetailAdapter(this,R.layout.yt_file_item_detail,null);
        lm = new LinearLayoutManager(this, 1, false);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(fileDetailAdapter);
        recyclerView.setFocusable(false);
        fileDetailAdapter.setItemClickListener(new FileDetailAdapter.ItemClickListener() {
            @Override
            public void onClick(String path) {
                File file = new File(path);
                if(file != null && file.isDirectory()){
                    if(firstFlag){
                        firstFlag = false;
                        urlPath = path;
                    }
                    updateData(path);
                }else{
                    int type = SpaceFileUtil.getFileType(path);
                    if(type >= 300 && type < 400){
                        Intent mIntent = new Intent();
                        String relPath = path.replace(urlPath,"");
                        mIntent.putExtra("selectPath", path);
                        setResult(1, mIntent);
                        Toast.makeText(FileSystemActivity.this,"Select:" + relPath,Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(FileSystemActivity.this,"You can only select video files!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void updateData(String path){
        if(TextUtils.isEmpty(path)){
            Toast.makeText(FileSystemActivity.this,"some error,please try...",Toast.LENGTH_LONG).show();
            return;
        }
        PreferenceUtils.commitString("selectPath",path);
        txtTitle.setText(path);
        currentPath = path;
        List<String> files = SpaceFileUtil.getFileByPath(path);
        fileDetailAdapter.updateDatas(files);
        recyclerView.scrollToPosition(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(TextUtils.isEmpty(urlPath)){
                finish();
            }else{
                if(urlPath.equals(currentPath)){
                    urlPath = "";
                    selectSpace();
                }else{
                    File file = new File(currentPath);
                    updateData(file.getParent());
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
