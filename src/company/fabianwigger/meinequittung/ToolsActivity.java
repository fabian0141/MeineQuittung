package company.fabianwigger.meinequittung;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ToolsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tools);
		
		
		Button button1, button2, button3,button4;
		button1 = (Button) findViewById(R.id.button1);							
		button1.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				Intent mIntent = getIntent();
				mIntent.putExtra("toolsButton", 1);
				setResult(RESULT_OK, mIntent);
				finish();
		}});
		button2 = (Button) findViewById(R.id.button2);							
		button2.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				Intent mIntent = getIntent();
				mIntent.putExtra("toolsButton", 2);
				setResult(RESULT_OK, mIntent);
				finish();
		}});
		button3 = (Button) findViewById(R.id.button3);							
		button3.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				Intent mIntent = getIntent();
				mIntent.putExtra("toolsButton", 3);
				setResult(RESULT_OK, mIntent);
				finish();
		}});
		button4 = (Button) findViewById(R.id.button4);							
		button4.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				Intent mIntent = getIntent();
				mIntent.putExtra("toolsButton", 4);
				setResult(RESULT_OK, mIntent);
				finish();
		}});
		
		
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout);
		rl.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {				
				finish();
		}});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tools, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
