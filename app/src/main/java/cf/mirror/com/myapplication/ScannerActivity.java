package cf.mirror.com.myapplication;

import android.widget.Toast;

import com.example.libzingbar.CaptureActivity;
import com.example.libzingbar.CaptureInfo;

/**
 * Created by Administrator on 2017/7/17 0017.
 */

public class ScannerActivity extends CaptureActivity {

    @Override
    public boolean OnResult(String result) {
        Toast.makeText(this, "result" + result, Toast.LENGTH_SHORT).show();
        //重新扫描
//        getHandler().sendEmptyMessage(CaptureInfo.restart_preview);
        return false;
    }
}
