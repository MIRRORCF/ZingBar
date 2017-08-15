package com.mirror.libzingbar.zbar.decode;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.mirror.libzingbar.CaptureActivity;
import com.mirror.libzingbar.CaptureInfo;
import com.mr.zbar.build.ZBarDecoder;


/**
 * 接受消息后解码
 */
final class DecodeHandler extends Handler {

	CaptureActivity activity = null;

	DecodeHandler(CaptureActivity activity) {
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case CaptureInfo.decode:
			decode((byte[]) message.obj, message.arg1, message.arg2);
			break;
		case CaptureInfo.quit:
			Looper.myLooper().quit();
			break;
		}
	}

	private void decode(byte[] data, int width, int height) {

		byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++)
				rotatedData[x * height + height - y - 1] = data[x + y * width];
		}
		int tmp = width;// Here we are swapping, that's the difference to #11
		width = height;
		height = tmp;

		// 解码
		ZBarDecoder manager = new ZBarDecoder();
		//全屏扫描
//		String result = manager.decodeRaw(rotatedData, width, height);
		//区域扫描
		String result = manager.decodeCrop(rotatedData, width, height, activity.getX(), activity.getY(), activity.getCropWidth(), activity.getCropHeight());


		if (result != null) {
			if (null != activity.getHandler()) {
				Message msg = new Message();
				msg.obj = result;
				msg.what = CaptureInfo.decode_succeeded;
				activity.getHandler().sendMessage(msg);
			}
		} else {
			if (null != activity.getHandler()) {
				activity.getHandler().sendEmptyMessage(CaptureInfo.decode_failed);
			}
		}
	}

}
