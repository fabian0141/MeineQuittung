package company.fabianwigger.meinequittung;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * This class assumes the parent layout is RelativeLayout.LayoutParams.
 */

@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	protected Activity mActivity;
	private SurfaceHolder mHolder;
	protected Camera mCamera;
	private boolean tookPhoto;

	protected boolean mSurfaceConfiguring = false;

	public CameraPreview(Activity activity) {
		super(activity); // Always necessary
		mActivity = activity;
		mHolder = getHolder();
		mHolder.addCallback(this);

		// mCamera = Camera.open();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera = Camera.open();
			mCamera.setDisplayOrientation(90);
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (IOException e) {
			mCamera.release();
			mCamera = null;
		}
	}

	Camera.Parameters params;

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		params = mCamera.getParameters();

		List<Size> sizes = params.getSupportedPictureSizes();

		// Iterate through all available resolutions and choose one.
		// The chosen resolution will be stored in mSize.
		Size mSize = null;
		for (Size size : sizes) {
			Log.i("wichtig", "Available resolution: " + size.width + " " + size.height);
			mSize = size;
			return;
		}

		Log.i("wichtig", "Chosen resolution: " + mSize.width + " " + mSize.height);
		params.setPictureSize(mSize.width, mSize.height);

		List<String> focusModes = params.getSupportedFocusModes();
		if (mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS))

		{
			if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
				// set the focus mode

				params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

				mCamera.setParameters(params);
			}
		}

	}

	public void takePhoto() {
		if (!tookPhoto) {
			mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
			tookPhoto = true;
		}
	}

	public void stop() {
		if (null == mCamera) {
			return;
		}

		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Log.d(TAG, "onShutter'd");
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// Log.d(TAG, "onPictureTaken - raw");
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			new SaveImageTask().execute(data);

		}
	};

	private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

		@Override
		protected Void doInBackground(byte[]... data) {
			FileOutputStream outStream = null;

			// Write to SD Card
			try {
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File(sdCard.getAbsolutePath() + "/.MeineQuittungDaten");
				dir.mkdirs();

				// String fileName = String.format("%d.jpg",
				// System.currentTimeMillis());
				File outFile = new File(dir, "cacheimage.jpg");

				outStream = new FileOutputStream(outFile);
				outStream.write(data[0]);
				outStream.flush();
				outStream.close();

				refreshGallery(outFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				final Intent intent = new Intent(mActivity, NewReceipt.class);
				intent.putExtra("image", "newimage");
				mActivity.startActivityForResult(intent, 1);

			}
			return null;
		}

	}

	private void refreshGallery(File file) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(file));
		mActivity.sendBroadcast(mediaScanIntent);
	}

	public boolean flash(boolean flashBoolean) {
		if (mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			if (flashBoolean) {
				params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
			} else {
				params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			}

			mCamera.setParameters(params);
		} else {
			flashBoolean = false;
			showToast("Kein Blitzlicht vorhanden.");
		}
		return flashBoolean;
	}

	private void showToast(String hint) {
		Toast.makeText(mActivity.getBaseContext(), hint, Toast.LENGTH_LONG).show();
	}

}
