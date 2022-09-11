package company.fabianwigger.meinequittung;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class RenameFolder extends Activity {

	EditText et;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rename_folder);
		
		Intent mIntent = getIntent();
		
		et = (EditText) findViewById(R.id.editText2);
		et.setText(mIntent.getStringExtra("folderName"));
		et.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	et.setText("");
            }
        });
		et.setOnKeyListener(new OnKeyListener() {

	        public boolean onKey(View v, int keyCode, KeyEvent event) {
	                if (event.getAction() == KeyEvent.ACTION_DOWN
	                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
	                    return true;
	                }
	            return false;
	        }
		});
		
		Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(!et.getText().toString().equals("")){
            	Intent intent = new Intent();
        		intent.putExtra("edittextvalue","" + et.getText());
        		setResult(RESULT_OK, intent);        
        		finish();
            	}
            }
        });
        
        
        ImageButton imgButton = (ImageButton) findViewById(R.id.imageButton3);
        imgButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	Intent intent = new Intent();
        		intent.putExtra("edittextvalue","destroy");
        		setResult(RESULT_OK, intent);        
        		finish();
            }
        });	
        
		
	}
	
	@Override
	public void onBackPressed()
	{     
		finish();
	}
	
}
