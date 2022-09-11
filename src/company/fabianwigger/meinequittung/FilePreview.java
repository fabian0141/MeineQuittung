package company.fabianwigger.meinequittung;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;


import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.print.PrintHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FilePreview extends Activity {

	Intent intent,intent2, intent3;
	String dir;
	boolean fullSize = true;
	Intent mIntent;
	LinearLayout ll;
	RelativeLayout rl;
	File sdCard;
	File file2;
	int width,height;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_preview);
		
		ll = (LinearLayout) findViewById(R.id.linearLayout1);
		rl = (RelativeLayout) findViewById(R.id.relativeLayout1);
		
		intent = new Intent(this, PreviewTool.class);
		intent2 = new Intent(this, NewReceipt.class);
		intent3 = new Intent(this, SureActivity.class);
		
		ImageButton button = (ImageButton) findViewById(R.id.imageButton1); // cameraButton
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(intent, 1);
			}
		});
		
		ImageButton button2 = (ImageButton) findViewById(R.id.imageButton2); // cameraButton
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
					finish();
			}
		});
		
		mIntent = getIntent();
		
		TextView text = (TextView) findViewById(R.id.textView1); // cameraButton
		text.setText(mIntent.getStringExtra("fileName"));
		
		TextView text2 = (TextView) findViewById(R.id.textView2); // cameraButton
		
		String[] fileDate = mIntent.getStringExtra("fileDate").split("\\#");
		
		
		text2.setText("Ablaufdatum: " + fileDate[0] + "." + (Integer.parseInt(fileDate[1]) + 1) + "." + fileDate[2]);
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		width = displaymetrics.widthPixels;
		
		sdCard = Environment.getExternalStorageDirectory();

		ImageView image = (ImageView) findViewById(R.id.imageView1);
		
		dir = sdCard.getAbsolutePath() + "/.MeineQuittungDaten/" + mIntent.getStringExtra("fileName") + ".jpg";
		
		
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bitmap = BitmapFactory.decodeFile(dir, bmOptions);
		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
		
		
		Drawable bgrImage = new BitmapDrawable(bitmap);
		image.setImageDrawable(bgrImage);
		image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(fullSize){
					//dp.setEnabled(false);
					//ll.setEnabled(false);
					rl.setVisibility(View.GONE);
					ll.setVisibility(View.GONE);
					fullSize = false;
				}else{
					//dp.setEnabled(true);
					//ll.setEnabled(true);
					rl.setVisibility(View.VISIBLE);
					ll.setVisibility(View.VISIBLE);
					fullSize = true;
				}
			}
		});
	}
	Bitmap bitmap;
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) { // create new Folder
			if (resultCode == RESULT_OK) {
				int switchTool = data.getIntExtra("intTool", 0);
				Intent intent;
				switch (switchTool){
				case 1:
					intent2.putExtra("image", mIntent.getStringExtra("fileName"));
					intent2.putExtra("fileDate", mIntent.getStringExtra("fileDate"));
					intent2.putExtra("fileID", mIntent.getStringExtra("fileID"));
					startActivityForResult(intent2, 2);
					break;
				case 2:
					intent = getIntent();
					intent.putExtra("changeFile", 1);
					setResult(RESULT_OK, intent);
					finish();
					break;
				case 3:
					File file = new File(dir);
					Intent shareIntent = new Intent();
					shareIntent.setAction(Intent.ACTION_SEND);
					shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
					shareIntent.setType("image/jpeg");
					startActivity(Intent.createChooser(shareIntent, "Sende Bild an:"));
					break;
				case 4:
					startActivityForResult(intent3, 3);
					break;
				case 5:
			        try {
			        	ByteArrayOutputStream stream = new ByteArrayOutputStream();
					    bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
			        	 Image img = Image.getInstance(stream.toByteArray());
			             img.scaleToFit(width, height);
			             Log.i("wichtig", "" + width);
			             img.setAbsolutePosition(0, 0);
			            
			             // We create a new document with the correct size
			             Document document2 = new Document(new Rectangle(img.getScaledWidth(), img.getScaledHeight()));
			             PdfWriter.getInstance(document2, new FileOutputStream(sdCard.getAbsolutePath() + "/.MeineQuittungDaten/" + mIntent.getStringExtra("fileName") + ".pdf"));
			             document2.open();
			             document2.add(img);
			             document2.close();
			        	
			        	
			        	
			        	/*
						PdfWriter.getInstance(document,new FileOutputStream(sdCard.getAbsolutePath() + "/.MeineQuittungDaten/" + mIntent.getStringExtra("fileName") + ".pdf"));
						document.open();
					    //Image image = Image.getInstance(sdCard.getAbsolutePath() + "/.MeineQuittungDaten/" + mIntent.getStringExtra("fileName") + ".jpg");
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
					    bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
					    Image image = Image.getInstance(stream.toByteArray());
					    image.setAlignment(Image.MIDDLE);
					    Rectangle ra = new Rectangle(width, height);
					    document.setPageSize(ra);
					    document.setMargins(2, 2, 2, 2);
					    document.add(image);               
					    document.close();
					    */
					    file2 = new File(sdCard.getAbsolutePath() + "/.MeineQuittungDaten/" + mIntent.getStringExtra("fileName") + ".pdf");
					    Intent shareIntent2 = new Intent();
						shareIntent2.setAction(Intent.ACTION_SEND);
						shareIntent2.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file2));
						//shareIntent2.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "" });
						shareIntent2.setType("application/pdf");
						startActivityForResult(Intent.createChooser(shareIntent2, "Sende PDF an:"), 4);
						
						
					}catch (Exception e) {
						e.printStackTrace();
					}
			       
			        break;
				case 6:
					doPhotoPrint();
					//finish();
					break;
				}
			}
		}else if(requestCode == 2){
			if (resultCode == RESULT_OK) {
				Intent mIntent = getIntent();
					mIntent.putExtra("changeFile", 3);
				
				mIntent.putExtra("fileName", data.getStringExtra("fileName"));
				mIntent.putExtra("fileDate", data.getStringExtra("fileDate"));
				mIntent.putExtra("fileID", data.getStringExtra("fileID"));
				setResult(RESULT_OK, mIntent);
				finish();
			}
		}else if(requestCode == 3){
			if (resultCode == RESULT_OK) {
				Intent intent = getIntent();
				intent.putExtra("changeFile", 2);
				setResult(RESULT_OK, intent);
				finish();
			}
		}else if(requestCode == 4){
			file2.delete();
		}
	}
	
	private void doPhotoPrint() {
	    PrintHelper photoPrinter = new PrintHelper(this);
	    photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
	    photoPrinter.printBitmap("droids.jpg - test print", bitmap);
	}
	
}
