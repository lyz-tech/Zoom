package com.third.zoom.ytbus.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.third.zoom.R;
import com.third.zoom.common.base.BaseRecyclerAdapter;
import com.third.zoom.common.base.BaseRecyclerViewHolder;
import com.third.zoom.common.utils.MToolBox;
import com.third.zoom.common.utils.SpaceFileUtil;

import java.util.List;

/**
 * Created by Administrator on 2018/3/25.
 */

/**
 * 文件详情
 */
public class FileDetailAdapter extends BaseRecyclerAdapter<String> {

    public FileDetailAdapter(Context context, int layoutId, List<String> data) {
        super(context, layoutId, data);
    }

    @Override
    public void convert(BaseRecyclerViewHolder holder, final String fileName, int position) {
        TextView textView = holder.getTextView(R.id.txt_file_name);
        textView.setText(fileName);

        ImageView imgType = holder.getImageView(R.id.img_type);

        RelativeLayout root = (RelativeLayout) holder.getView(R.id.rl_root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClickListener != null){
                    itemClickListener.onClick(fileName);
                }
            }
        });
        int l_FileType = SpaceFileUtil.getFileType(fileName);
        int l_ResourceID = MToolBox.getFileImage(l_FileType);
        imgType.setImageResource(l_ResourceID);
    }

    private ItemClickListener itemClickListener;
    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }


    public interface ItemClickListener{
        void onClick(String path);
    }
}
