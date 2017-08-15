package cf.mirror.com.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirror.libzingbar.CaptureActivity;
import com.mirror.libzingbar.CaptureInfo;
import com.mirror.libzingbar.zbar.QREncode;

public class MainActivity extends AppCompatActivity{

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView img = (ImageView) findViewById(R.id.img3);
        textView = (TextView) findViewById(R.id.text3);
        Button btn1 = (Button) findViewById(R.id.btn1);
        Button btn2 = (Button) findViewById(R.id.btn2);
        Button btn3 = (Button) findViewById(R.id.btn3);
        Button btn4 = (Button) findViewById(R.id.btn4);
        //直接跳转CaptureActivity，扫描结果则在onActivityResult返回
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,
                        CaptureActivity.class),0x111);
            }
        });

        final float f = DisplayUtils.getDensity(this);//获取屏幕密度，用于适配

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = QREncode.createImage("sss",
                        (int)f * 200,null);
                img.setImageBitmap(bitmap);

            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = QREncode.createImage("sss",
                        (int)f * 200, BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
                img.setImageBitmap(bitmap);
            }
        });

        //继承CaptureActivity，重写OnResult方法
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,
                        ScannerActivity.class),0x111);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CaptureInfo.decode_succeeded ){
            String result = data.getStringExtra(CaptureInfo.scanner_result);
            textView.setText(result);
        }
    }
}
