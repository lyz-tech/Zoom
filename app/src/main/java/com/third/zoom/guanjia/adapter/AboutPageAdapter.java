package com.third.zoom.guanjia.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * 作者：Sky on 2018/7/16.
 * 用途：关于page
 */

public class AboutPageAdapter extends PagerAdapter {

    private Context context;
    private int imageUrls[];

    public AboutPageAdapter(Context context,int[] imageUrls) {
        this.imageUrls = imageUrls;
        this.context = context;
    }

    public void updateData(int[] imageUrls){
        this.imageUrls = imageUrls;
        notifyDataSetChanged();
    }

    private int resMode = 0;
    public void updateData(int[] imageUrls,int mode){
        this.resMode = mode;
        this.imageUrls = imageUrls;
        notifyDataSetChanged();
    }

    public int getResMode(){
        return resMode;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
//        imageView.setImageResource(imageUrls[position]);
        Glide.with(context).load(imageUrls[position]).into(imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public int getCount() {
        return imageUrls != null ? imageUrls.length : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    private int mChildCount = 0;
    @Override public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override public int getItemPosition(Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }


}
