package com.third.zoom.common.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Sky on 2017/12/19
 *
 * 用途：适配器基类
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    protected List<T> mDatas;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected int mLayoutId;


    public BaseRecyclerAdapter(Context context,int layoutId, List<T> data) {
        mContext = context;
        mLayoutId = layoutId;
        mDatas = data == null ? new ArrayList<T>() : data;
        mInflater = LayoutInflater.from(context);
    }

    public void updateDatas(List<T> data){
        mDatas = data == null ? new ArrayList<T>() : data;
        int size = mDatas.size();
        notifyDataSetChanged();
    }


    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder viewHolder = BaseRecyclerViewHolder.get(mContext, parent, mLayoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        convert(holder, mDatas.get(position), position);
    }

    @Override
    public int getItemCount()
    {
        if(mDatas != null && mDatas.size() > 0){
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public abstract void convert(BaseRecyclerViewHolder holder, T t, int position);


}
