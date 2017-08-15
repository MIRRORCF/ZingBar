package com.example.libzingbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import java.util.List;

/**
 * 相册适配器
 *
 * Created by ADMINISTRATOR on 2017/7/2.
 */

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.MyHolder> {

    private List<String> list;
    private Activity activity;

    public PictureAdapter(Activity activity, List<String> list){
        this.activity = activity;
        this.list = list;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder holder = new MyHolder(LayoutInflater.from(
                activity).inflate(R.layout.recycle_item_picture, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        Glide.with(activity).load(list.get(position)).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra("intent_selected_picture", list.get(position));
                activity.setResult(CaptureInfo.picture_path, data);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == list?0:list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public MyHolder(View view){
            super(view);
            imageView = (ImageView) view.findViewById(R.id.iv);
        }
    }
}
