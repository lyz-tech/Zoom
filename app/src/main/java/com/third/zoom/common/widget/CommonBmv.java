package com.third.zoom.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.third.zoom.R;
import com.third.zoom.common.listener.BmvChangeListener;
import com.third.zoom.common.listener.BmvSelectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Sky on 2018/7/13.
 * 用途：//TODO
 */

public class CommonBmv extends LinearLayout implements View.OnClickListener {

    protected static String TAG = "CommonBmv";

    private Context mContext;

    CommonBmvItem deviceBar;
    CommonBmvItem shoppingBar;
    CommonBmvItem communityBar;
    CommonBmvItem mineBar;

    List<CommonBmvItem> tabs = new ArrayList<CommonBmvItem>();

    BmvChangeListener changeListener;
    BmvSelectListener bmvSelectListener;

    public CommonBmv(Context context) {
        this(context,null);
    }

    public CommonBmv(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CommonBmv(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
        initData();
    }

    private void initView(){
        View view = View.inflate(mContext, R.layout.common_widget_bmv,this);
        deviceBar = (CommonBmvItem) view.findViewById(R.id.bottombar_item_0);
        shoppingBar = (CommonBmvItem) view.findViewById(R.id.bottombar_item_1);
        communityBar = (CommonBmvItem) view.findViewById(R.id.bottombar_item_2);
        mineBar = (CommonBmvItem) view.findViewById(R.id.bottombar_item_3);
    }

    private void initData(){
        deviceBar.setTabIcon(R.drawable.icon_tab_test,R.drawable.icon_tab_test_press);
        deviceBar.setTitleColor(getResources().getColor(R.color.tab_normal),getResources().getColor(R.color.tab_focus));
        shoppingBar.setTabIcon(R.drawable.icon_tab_test,R.drawable.icon_tab_test_press);
        shoppingBar.setTitleColor(getResources().getColor(R.color.tab_normal),getResources().getColor(R.color.tab_focus));
        communityBar.setTabIcon(R.drawable.icon_tab_test,R.drawable.icon_tab_test_press);
        communityBar.setTitleColor(getResources().getColor(R.color.tab_normal),getResources().getColor(R.color.tab_focus));
        mineBar.setTabIcon(R.drawable.icon_tab_test,R.drawable.icon_tab_test_press);
        mineBar.setTitleColor(getResources().getColor(R.color.tab_normal),getResources().getColor(R.color.tab_focus));

        deviceBar.setTitle(getResources().getString(R.string.main_tab_title_drum));
        shoppingBar.setTitle(getResources().getString(R.string.main_tab_title_hot));
        communityBar.setTitle(getResources().getString(R.string.main_tab_title_normal));
        mineBar.setTitle(getResources().getString(R.string.main_tab_title_about));

        tabs.add(deviceBar);
        tabs.add(shoppingBar);
        tabs.add(communityBar);
        tabs.add(mineBar);

        for (int i = 0; i < tabs.size(); i++) {
            tabs.get(i).setTag(i);
            tabs.get(i).setOnClickListener(this);
        }
    }

    public CommonBmvItem getBmvItem(int position){
        return tabs.get(position);
    }

    /**
     * 如果第一次点击，select哪个，如果再次点击相同，
     * @param v
     */
    @Override
    public void onClick(View v) {
        for (int i = 0; i < tabs.size(); i++) {
            CommonBmvItem item = tabs.get(i);
            if(item.isSelect()){
                item.setTabSelected(false);
                if(bmvSelectListener != null){
                    bmvSelectListener.itemSelectClose(i);
                }
                if((Integer)v.getTag() == i){
                    return;
                }
            }
        }
        for (int i = 0; i < tabs.size(); i++) {
            CommonBmvItem item = tabs.get(i);
            if((Integer)v.getTag() == i){
                item.setTabSelected(true);
                if(bmvSelectListener != null){
                    bmvSelectListener.itemSelectOpen(i);
                }
            }else{
                item.setTabSelected(false);
            }
        }
    }

    public void setBmvChangeListener(BmvChangeListener changeListener){
        this.changeListener = changeListener;
    }

    public void setBmvSelectListener(BmvSelectListener bmvSelectListener){
        this.bmvSelectListener = bmvSelectListener;
    }

    public void setCheck(int position){
        for (int i = 0; i < tabs.size(); i++) {
            CommonBmvItem item = tabs.get(i);
            if(position == i){
                item.setTabSelected(true);
                if(changeListener != null){
                    changeListener.itemChange(i);
                }
            }else{
                item.setTabSelected(false);
            }
        }
    }

}
