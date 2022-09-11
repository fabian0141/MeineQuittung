package company.fabianwigger.meinequittung;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class PreviewTool extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview_tool);
		
		Button button1, button2, button3,button4,button5,button6;
		button1 = (Button) findViewById(R.id.button1);							
		button1.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				Intent mIntent = getIntent();
				mIntent.putExtra("intTool", 1);
				setResult(RESULT_OK, mIntent);
				finish();
		}});
		button2 = (Button) findViewById(R.id.button2);							
		button2.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				Intent mIntent = getIntent();
				mIntent.putExtra("intTool", 2);
				setResult(RESULT_OK, mIntent);
				finish();
		}});
		button3 = (Button) findViewById(R.id.button3);							
		button3.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				Intent mIntent = getIntent();
				mIntent.putExtra("intTool", 3);
				setResult(RESULT_OK, mIntent);
				finish();
		}});
		button4 = (Button) findViewById(R.id.button4);							
		button4.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				Intent mIntent = getIntent();
				mIntent.putExtra("intTool", 4);
				setResult(RESULT_OK, mIntent);
				finish();
		}});
		button5 = (Button) findViewById(R.id.button5);							
		button5.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				Intent mIntent = getIntent();
				mIntent.putExtra("intTool", 5);
				setResult(RESULT_OK, mIntent);
				finish();
		}});
		button6 = (Button) findViewById(R.id.Button01);							
		button6.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				Intent mIntent = getIntent();
				mIntent.putExtra("intTool", 6);
				setResult(RESULT_OK, mIntent);
				finish();
		}});
		
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout);
		rl.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {				
				finish();
		}});
		
	}

	
}
