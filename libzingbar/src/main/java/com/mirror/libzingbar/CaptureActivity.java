package com.mirror.libzingbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mirror.libzingbar.statusbar.StatusBarCompat;
import com.mirror.libzingbar.zbar.camera.CameraManager;
import com.mirror.libzingbar.zbar.decode.CaptureActivityHandler;
import com.mirror.libzingbar.zbar.decode.InactivityTimer;
import com.mirror.libzingbar.zbar.decode.RGBLuminanceSource;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.util.Hashtable;

/**
 * 扫描界面
 */
public class CaptureActivity extends Activity implements SurfaceHolder.Callback {

    private CaptureActivityHandler captureHandler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    private RelativeLayout ContainerRlayout = null;
    private RelativeLayout CropRlayout = null;
    private LinearLayout layoutFinish;
    private TextView tvFlash, tvAlbum;
    private RelativeLayout rlayoutTitle;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    boolean flag = true;

    //闪光灯
    protected void light() {
        if (flag == true) {
            flag = false;
            // 开闪光灯
            CameraManager.get().openLight();
        } else {
            flag = true;
            // 关闪光灯
            CameraManager.get().offLight();
        }
    }


    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (captureHandler != null) {
            captureHandler.quitSynchronously();
            captureHandler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * @return true   finish Activity
     * false  onResume
     */
    public boolean OnResult(String result) {
        return true;
    }

    /**
     * 解析信息处理
     */
    public void handleDecode(String result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        if (OnResult(result)) {
            Intent intent = new Intent();
            intent.putExtra(CaptureInfo.scanner_result, result == null ? "" : result);
            setResult(CaptureInfo.decode_succeeded, intent);
            CaptureActivity.this.finish();
        }
    }


    //初始化相机
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (Throwable e) {
            showDialog();
            return;
        }
        Point point = CameraManager.get().getCameraResolution();
        int width = point.y;
        int height = point.x;

        int x = CropRlayout.getLeft() * width / ContainerRlayout.getWidth();
        int y = CropRlayout.getTop() * height / ContainerRlayout.getHeight();

        int cropWidth = CropRlayout.getWidth() * width / ContainerRlayout.getWidth();
        int cropHeight = CropRlayout.getHeight() * height / ContainerRlayout.getHeight();

        setX(x);
        setY(y);
        setCropWidth(cropWidth);
        setCropHeight(cropHeight);

        if (captureHandler == null) {
            captureHandler = new CaptureActivityHandler(CaptureActivity.this);
        }
    }

    //没有权限时弹窗
    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("警告！！！")
                .setMessage("相机权限没有打开,清先打开相机权限！")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setResult(CaptureInfo.quit);
                        finish();
                    }
                })
                .show();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public Handler getHandler() {
        return captureHandler;
    }

    //声音设置
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                        file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            try {
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(VIBRATE_DURATION);
            } catch (Throwable e) {
                Log.d("zxingbar", "请在工程中添加震动权限！！！");
            }

        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(savedInstanceState);
    }

    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_qr_scan);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK, false);
        // 初始化 CameraManager
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        ContainerRlayout = (RelativeLayout) findViewById(R.id.rlayout_capture_containter);
        CropRlayout = (RelativeLayout) findViewById(R.id.rlayout_capture_crop);
        rlayoutTitle = (RelativeLayout) findViewById(R.id.rlayout_title);
        layoutFinish = (LinearLayout) findViewById(R.id.llayout_left_title);
        tvAlbum = (TextView) findViewById(R.id.txt_right_titleL);
        tvFlash = (TextView) findViewById(R.id.txt_right_titleR);

        ImageView mQrLineView = (ImageView) findViewById(R.id.img_capture_scan_line);

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mQrLineView.measure(w, h);
        int height = mQrLineView.getMeasuredHeight();
        mQrLineView.getMeasuredWidth();

        TranslateAnimation animation = new TranslateAnimation(0, 0, -height, height);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);// 设置重复次数
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(3000);// 设置动画持续时间
        mQrLineView.startAnimation(animation);
        onListener();
    }

    //事件监听
    private void onListener() {
        layoutFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(CaptureInfo.quit);
                finish();
            }
        });
        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CaptureActivity.this, AlbumActivity.class);
                startActivityForResult(intent, CaptureInfo.open_album);
            }
        });
        tvFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                light();
            }
        });
    }

    /**
     * 接收返回状态
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CaptureInfo.picture_path) {
            String photo_path = data.getExtras().getString(CaptureInfo.SELECT_PICTURE);
            Result result = scanningImage(photo_path);
            if (result != null) {
                handleDecode(result.getText());
            } else {
                Toast.makeText(this, "无法解析", Toast.LENGTH_SHORT).show();
                if (null != getHandler()) {
                    getHandler().sendEmptyMessage(CaptureInfo.decode_failed);
                }
            }
        }
    }

    //图片路径解析
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap, hints);
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            //Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(CaptureInfo.quit);
        finish();
    }
}
