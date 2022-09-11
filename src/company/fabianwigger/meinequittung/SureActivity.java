package company.fabianwigger.meinequittung;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SureActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sure);
		
		Button button1, button2;
		button1 = (Button) findViewById(R.id.button5);							
		button1.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				Intent mIntent = getIntent();
				setResult(RESULT_OK, mIntent);
				finish();
		}});
		button2 = (Button) findViewById(R.id.button1);							
		button2.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				finish();
		}});
	}
}
