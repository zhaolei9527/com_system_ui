package com.system.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.system.ui.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 用于录音、拍照和录像
 */
public class MediaController {

	SurfaceView mSurfaceView;// 用来预览的控件
	SurfaceHolder mSurfaceHolder;
	Camera mCamera;
	MediaRecorder mMediaRecorder;
	private boolean mIsPreview;// 是否正在预览
	private AudioManager mAudioManager;
	int volumn;// 音量
	private String mSavePath = "";
	private Context mContext;
	private OnRecordFinishListener mOnRecordFinishListener;
	private int mQuality = 50;
	private boolean mIsJpeg = true;

	public MediaController(Context context) {
		this.mContext = context;
	}

	public void setSurfaceView(SurfaceView surfaceView) {
		mSurfaceView = surfaceView;
		initSurfaceHolder();
	}

	private void initMediaRecorder() {
		if (mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder();
		}
		mMediaRecorder.reset();
	}

	public interface OnRecordFinishListener {
		void onRecordFinish(String savePath);
	}

	public void setOnRecordFinishListener(OnRecordFinishListener l) {
		this.mOnRecordFinishListener = l;
	}

	/**
	 * 开始录音
	 */
	public void startRecordSound(String savePath) throws IllegalStateException, IOException {
		initMediaRecorder();
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mMediaRecorder.setOutputFile(savePath);
		mMediaRecorder.prepare();
		mMediaRecorder.start();
	}

	/**
	 * 开始录像
	 */
	public void startRecordVideo(String savePath) throws IllegalStateException, IOException {
		if (mSurfaceView == null) {
			throw new IllegalStateException("没有设置预览视图");
		}
		initMediaRecorder();
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		mMediaRecorder.setOutputFile(savePath);
		mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
		mMediaRecorder.setVideoFrameRate(60);// 每秒60帧高速绘制
		mMediaRecorder.prepare();
		mMediaRecorder.start();
	}

	/**
	 * 结束录音/录像
	 */
	public void stopRecord() {
		if (mMediaRecorder != null) {
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
			if (mOnRecordFinishListener != null) {
				mOnRecordFinishListener.onRecordFinish(mSavePath);
			}
		}
	}

	private void initSurfaceHolder() {
		if (mSurfaceView == null) {
			throw new IllegalStateException("SurfaceView do not exists.");
		}
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(new Callback() {
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}

			public void surfaceCreated(SurfaceHolder holder) {
				// surface被创建时打开摄像头
				initCamera(0, 0);
			}

			// surface摧毁时释放摄像头
			public void surfaceDestroyed(SurfaceHolder holder) {
				// 如果camera不为null ,释放摄像头
				if (mCamera != null) {
					if (mIsPreview)
						mCamera.stopPreview();
					mCamera.release();
					mCamera = null;
				}
			}
		});
		// 设置该SurfaceView自己不维护缓冲
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/**
	 * 静音
	 */
	public void clearVolumn(Context context) {
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
		//volumn = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);		
		//mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		//mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		
		
	}

	public void reVolumn(Context context) {
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
		//volumn = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);		
		//mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 100, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		//mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		
		
	}
	/**
	 * 初始化相机,拍照前如果没有初始化，就会用默认的
	 */
	public void initCamera(int index, int angle) {
		int cameraCount = 0;
		cameraCount = Camera.getNumberOfCameras(); // 得到相机的数量
		if (cameraCount == 0) {
			throw new UnsupportedOperationException("手机不支持摄像头功能");
		} else if (cameraCount == 1) {
			mCamera = Camera.open();// 调用Camera的open()方法打开相机。
		} else {
			try {
				mCamera = Camera.open(index);
			} catch (Exception e) {
				// TODO: handle exception
				//mCamera.
			}
				
			
		}
		mCamera.setDisplayOrientation(angle);
		startPreview();
	}

	/**
	 * 开始预览
	 */
	private void startPreview() {
		if (mCamera != null && !mIsPreview) {
			try {
				mCamera.setPreviewDisplay(mSurfaceHolder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mCamera.startPreview();
			mIsPreview = true;
		}
	}

	/**
	 * 拍照
	 */
	public void takePhotos(boolean isJpeg, int quality) {
		this.mIsJpeg = isJpeg;
		this.mQuality = quality;
		clearVolumn(mContext);
		mCamera.takePicture(mShutter, mRawCallback, mPictureCallback);
		reVolumn(mContext);
		
	}

	ShutterCallback mShutter = new ShutterCallback() {

		@Override
		public void onShutter() {
		}
	};

	PictureCallback mRawCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
		}
	};

	/**
	 * 保存照片
	 */
	public void savePicture(Bitmap bitmap) throws IOException {
		if (mIsJpeg) {
			mSavePath = MainActivity.APP_FOLDER + File.separator + "photo.jpg";
		} else {
			mSavePath = MainActivity.APP_FOLDER + File.separator + "photo.png";
		}
		// 如果SD卡已经准备就绪
		if (SDCard.isPrepared(mContext)) {
			File file = new File(mSavePath);
			FileOutputStream fos = new FileOutputStream(file);
			Matrix matrix = new Matrix();
			Bitmap bmp = Bitmap.createBitmap(1200, 1200 * bitmap.getHeight() / bitmap.getWidth(), Config.ARGB_8888);
			matrix.setScale((float) bmp.getWidth() / (float) bitmap.getWidth(),
					(float) bmp.getHeight() / (float) bitmap.getHeight());
			Canvas canvas = new Canvas(bmp);
			canvas.drawBitmap(bitmap, matrix, null);
			if (mIsJpeg) {
				bmp.compress(CompressFormat.JPEG, mQuality, fos);
			} else {
				bmp.compress(CompressFormat.PNG, mQuality, fos);
			}
			fos.flush();
			fos.close();
			mCamera.stopPreview();
			mCamera.startPreview();
			mIsPreview = true;
			if (mOnRecordFinishListener != null) {
				mOnRecordFinishListener.onRecordFinish(mSavePath);
			}
		}
	}

	public PictureCallback mPictureCallback = new PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {
			// 重置声音
			mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volumn, AudioManager.FLAG_ALLOW_RINGER_MODES);
			// 根据拍照所得的数据创建位图
			final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			try {
				savePicture(bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
}
