package company.fabianwigger.meinequittung;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

@SuppressLint("InflateParams")
public class CameraPreviewActivity extends Activity {

	private CameraPreview mPreview;
	RelativeLayout rl;
	LayoutInflater controlInflater = null;
	ImageButton button;
	boolean flashLight;
	ImageButton button2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rl = (RelativeLayout) findViewById(R.id.layout);
		mPreview = new CameraPreview(this); 
		setContentView(mPreview);
		 
		
		
		controlInflater = LayoutInflater.from(getBaseContext());
		View viewControl = controlInflater.inflate(R.layout.activity_camera_preview, null);
	     @SuppressWarnings("deprecation")
	     
		LayoutParams layoutParamsControl
	      = new LayoutParams(LayoutParams.FILL_PARENT,
	      LayoutParams.FILL_PARENT);
	     this.addContentView(viewControl, layoutParamsControl);
	     ImageButton button = (ImageButton) findViewById(R.id.imageButton1);
			
		 button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					mPreview.takePhoto();				//saveBitmap(takeScreenshot());
					//finish();
				}
			});
		 
		 button2 = (ImageButton) findViewById(R.id.imageButton2);
		 button2.setBackgroundResource(R.drawable.flashlight);
		 button2.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					flashLight = flashLight ? false : true;
					
					flashLight = mPreview.flash(flashLight);
					
					if(flashLight){
						button2.setBackgroundResource(R.drawable.flashlight2);
					}else{
						button2.setBackgroundResource(R.drawable.flashlight);
					}
					
					mPreview.flash(flashLight);
					
				}
			});
		 
		 
	}

	
	public void background(){
		button.setBackgroundResource(R.drawable.cameraicon);
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        //this.addContentView(mPreview, new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
        
    }
	
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
        //this.removeContentView(mPreview); // This is necessary.
        mPreview = null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Intent mIntent = getIntent();
				mIntent.putExtra("fileDate", data.getStringExtra("fileDate"));
				mIntent.putExtra("fileName", data.getStringExtra("fileName"));
				mIntent.putExtra("fileID", data.getStringExtra("fileID"));
				Log.i("wichtig", "1: " + data.getStringExtra("fileName"));
				setResult(RESULT_OK, mIntent);
				
			}
			finish();
		}
    }
    
}

