package com.mirror.libzingbar.zbar.decode;

import android.os.Handler;
import android.os.Message;

import com.mirror.libzingbar.CaptureActivity;
import com.mirror.libzingbar.CaptureInfo;
import com.mirror.libzingbar.zbar.camera.CameraManager;


/**
 * 扫描消息转发
 */
public final class CaptureActivityHandler extends Handler {

	DecodeThread decodeThread = null;
	CaptureActivity activity = null;
	private State state;
	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	public CaptureActivityHandler(CaptureActivity activity) {
		this.activity = activity;
		decodeThread = new DecodeThread(activity);
		decodeThread.start();
		state = State.SUCCESS;
		CameraManager.get().startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {

		switch (message.what) {
		case CaptureInfo.auto_focus:
			if (state == State.PREVIEW) {
				CameraManager.get().requestAutoFocus(this,CaptureInfo.auto_focus);
			}
			break;
		case CaptureInfo.restart_preview:
			restartPreviewAndDecode();
			break;
		case CaptureInfo.decode_succeeded:
			state = State.SUCCESS;
			activity.handleDecode((String) message.obj);// 解析成功，回调
			break;

		case CaptureInfo.decode_failed:
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),CaptureInfo.decode);
			break;
		}

	}

	public void quitSynchronously() {
		state = State.DONE;
		CameraManager.get().stopPreview();
		removeMessages(CaptureInfo.decode_succeeded);
		removeMessages(CaptureInfo.decode_failed);
		removeMessages(CaptureInfo.decode);
		removeMessages(CaptureInfo.auto_focus);
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),CaptureInfo.decode);
			CameraManager.get().requestAutoFocus(this,CaptureInfo.auto_focus);
		}
	}

}
