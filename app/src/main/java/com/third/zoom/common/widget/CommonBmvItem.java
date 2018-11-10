package com.third.zoom.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.third.zoom.R;

/**
 * 作者：Sky on 2018/7/13.
 * 用途：//TODO
 */

public class CommonBmvItem  extends LinearLayout {

    private static String TAG = "CommonBmvItem";

    private View rootView;
    private TextView tvTitle;
    private ImageView ivIcon;

    private boolean   mIconSelected;//是否选中
    private int       mNormalIConResId;//默认时的图片资源
    private int       mSelectedIconResId;//选中时的图片资源

    private boolean   mTitleSelected;//是否选中
    private int       mNormalTitleResId;//默认时
    private int       mSelectedTitleResId;//选中时

    private float normalSize;
    private float selectSize;

    public CommonBmvItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    public CommonBmvItem(Context context) {
        super(context);
        initView();
        normalSize = context.getResources().getDimension(R.dimen.DIMEN_10sp);
        selectSize = context.getResources().getDimension(R.dimen.DIMEN_13sp);
        Log.e("ZM","normalSize = " + normalSize);
        Log.e("ZM","selectSize = " + selectSize);
    }

    private void initView() {
        rootView = View.inflate(getContext(), R.layout.common_widget_bmv_item, this);
        tvTitle = (TextView) rootView.findViewById(R.id.tab_title);
        ivIcon = (ImageView) rootView.findViewById(R.id.tab_icon);
    }

    public void setTitle(String title){
        tvTitle.setText(title);
    }


    public void setTabIcon(int normalResId,
                           int selectedResId){
        this.mNormalIConResId = normalResId;
        this.mSelectedIconResId = selectedResId;

        ivIcon.setImageResource(normalResId);
    }

    public void setTitleColor(int normalTitleResId,
                              int selectedTitleResId){
        this.mNormalTitleResId = normalTitleResId;
        this.mSelectedTitleResId = selectedTitleResId;

        tvTitle.setTextColor(normalTitleResId);
    }

    private void setTitleSelected(boolean selected){
        this.mTitleSelected = selected;
        tvTitle.setTextColor(mTitleSelected?mSelectedTitleResId:mNormalTitleResId);
        if(mTitleSelected){
            tvTitle.setTextSize(20);
        }else {
            tvTitle.setTextSize(15);
        }
    }

    public void setTabSelected(boolean selected){
        this.mIconSelected = selected;
        ivIcon.setImageResource(mIconSelected ? mSelectedIconResId : mNormalIConResId);

        setTitleSelected(selected);
    }

    public boolean isSelect(){
        return mIconSelected;
    }


}
