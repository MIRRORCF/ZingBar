package com.example.libzingbar;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.libzingbar.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册
 *
 * Created by ADMINISTRATOR on 2017/7/2.
 */

public class PictureActivity extends Activity {

    private RecyclerView recyclerView;
    private List<String> images;
    private PictureAdapter adapter;
    private TextView tv;//返回

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        StatusBarCompat.setStatusBarColor(this,Color.WHITE,false);
        initView();
        initData();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycle_picture);
        tv = (TextView) findViewById(R.id.tv_back);
        images = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
    }

    private void initData(){
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelSelcet();
            }
        });
        handler.post(runnable);
    }

    /**
     * 获取图片路径
     */
    private void getThumbnail() {
        try{
            Cursor mCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns.DATA }, "", null,
                    MediaStore.MediaColumns.DATE_ADDED + " DESC");
            if (mCursor.moveToFirst()) {
                int _date = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                do {
                    String path = mCursor.getString(_date);
                    images.add(path);
                } while (mCursor.moveToNext());
            }
            mCursor.close();
        }catch (Throwable e){
            Log.d("zxingbar","请在工程中添加SD卡的读权限！！！");
        }
        adapter = new PictureAdapter(this,images);
        recyclerView.setAdapter(adapter);
    }

    Handler handler = new Handler();

    Runnable runnable  = new Runnable() {
        @Override
        public void run() {
            getThumbnail();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelSelcet();
    }

    //取消
    private void cancelSelcet(){
        setResult(CaptureInfo.quit);
        finish();
    }
}
