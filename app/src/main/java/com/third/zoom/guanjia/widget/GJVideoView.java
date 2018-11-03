package com.third.zoom.guanjia.widget;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.third.zoom.R;

/**
 * Created by Alienware on 2018/10/21.
 */

public class GJVideoView extends RelativeLayout {

    private Context mContext;

    private VideoView videoView;

    public GJVideoView(Context context) {
        this(context,null);
    }

    public GJVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GJVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
        initData();
    }

    private void initView() {
        View view = View.inflate(mContext, R.layout.gj_widget_video_view,this);
        videoView = (VideoView) view.findViewById(R.id.videoView);
    }

    public void initData(){
        Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.gj_video);
        videoView.setVideoURI(uri);
        videoView.start();
    }

    public void setOnCompleteListener(MediaPlayer.OnCompletionListener listener){
        videoView.setOnCompletionListener(listener);
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener listener){
        videoView.setOnErrorListener(listener);
    }

    public void stop(){
        videoView.stopPlayback();
    }
}
