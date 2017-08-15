package com.mirror.libzingbar;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @ package_name   com.example.libzingbar
 * @ effect
 * @ auther         mirror
 * @ date           2017/8/15 0015
 */

public class AlbumActivity extends Activity{


    private GridView mGV;
    private List<String> mList = new ArrayList<>();
    private AlbumAdapter mAdapter;
    private TextView mTV;//返回

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_album);
        mGV = (GridView) findViewById(R.id.gridview);
        mTV = (TextView)findViewById(R.id.tv_back_album);
        mTV.setOnClickListener(new View.OnClickListener() {
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
                    mList.add(path);
                } while (mCursor.moveToNext());
            }
            mCursor.close();
        }catch (Throwable e){
            Log.d("zxingbar","请在工程中添加SD卡的读权限！！！");
        }
        mAdapter = new AlbumAdapter(this,mList);
        mGV.setAdapter(mAdapter);
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
