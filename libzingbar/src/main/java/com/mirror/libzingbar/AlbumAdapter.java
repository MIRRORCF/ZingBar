package com.mirror.libzingbar;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * @ package_name   com.example.libzingbar
 * @ effect
 * @ auther         murongtech
 * @ date           2017/8/15 0015
 */

public class AlbumAdapter extends BaseAdapter{

    private List<String> mList;
    private Activity mContext;

    public AlbumAdapter(Activity mContext,List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public String getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_grid,null);
        ImageView imageView = (ImageView)view.findViewById(R.id.iv_itme);
        Glide.with(mContext).load(mList.get(position)).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(CaptureInfo.SELECT_PICTURE, mList.get(position));
                mContext.setResult(CaptureInfo.picture_path, data);
                mContext.finish();
            }
        });
        return view;
    }
}
