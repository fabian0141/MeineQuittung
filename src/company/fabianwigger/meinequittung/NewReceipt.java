package company.fabianwigger.meinequittung;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class NewReceipt extends Activity {

	EditText et;
	DatePicker dp;
	LinearLayout ll, ll2;
	RelativeLayout rl;
	File sdCard;
	Bitmap bitmap, originBitmap;
	boolean fullSize = true;
	String path;
	InputMethodManager in;
	boolean writing;
	Button button;
	Drawable bgrImage;
	ImageView image;
	int height;
	int width;
	ImageButton button2;
	int size = 100;
	int bmWidth, bmHeight, imgHeight, imgWidth;
	int rotation;
	boolean createBM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_receipt);
		Intent mIntent = getIntent();
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		width = displaymetrics.widthPixels;

		Spinner spinner = (Spinner) findViewById(R.id.spinner1);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				switch (position) {
				case 0:
					size = 100;
					break;
				case 1:
					size = 90;
					break;
				case 2:
					size = 80;
					break;
				case 3:
					size = 70;
					break;
				case 4:
					size = 60;
					break;
				case 5:
					size = 50;
					break;
				case 6:
					size = 40;
					break;
				case 7:
					size = 30;
					break;
				case 8:
					size = 20;
					break;
				case 9:
					size = 10;
					break;
				}

				if (createBM) {

					bmWidth = originBitmap.getWidth() * size / 100;
					bmHeight = originBitmap.getHeight() * size / 100;
					Log.i("wichtig", "geht");
					bitmap = Bitmap.createScaledBitmap(originBitmap, bmWidth, bmHeight, true);
					bgrImage = new BitmapDrawable(bitmap);
					image.setImageDrawable(bgrImage);
				} else {
					createBM = true;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		ll = (LinearLayout) findViewById(R.id.linearLayout1);
		ll2 = (LinearLayout) findViewById(R.id.linearLayout2);
		rl = (RelativeLayout) findViewById(R.id.relativeLayout1);

		et = (EditText) findViewById(R.id.editText2);
		et.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				et.setText("");
				writing = true;

			}
		});
		et.setImeActionLabel("Ok", KeyEvent.KEYCODE_ENTER);
		et.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					// NOTE: In the author's example, he uses an identifier
					// called searchBar. If setting this code on your EditText
					// then use v.getWindowToken() as a reference to your
					// EditText is passed into this callback as a TextView

					in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

					return true;
				}
				return false;
			}
		});
		dp = (DatePicker) findViewById(R.id.datePicker1);
		if (mIntent.getStringExtra("image").equals("newimage") || mIntent.getStringExtra("image").equals("imageuri")) {

			dp.updateDate(dp.getYear() + 1, dp.getMonth(), dp.getDayOfMonth());

		} else {
			String[] date = mIntent.getStringExtra("fileDate").split("\\#");
			Log.i("wichtig", "Ablaufdatum: " + date[0] + "." + date[1] + "." + date[2]);
			et.setText(mIntent.getStringExtra("image"));
			dp.updateDate(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
			Log.i("wichtig", "geht");
		}

		sdCard = Environment.getExternalStorageDirectory();

		image = (ImageView) findViewById(R.id.imageView1);

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();

		if (mIntent.getStringExtra("image").equals("newimage")) {
			path = sdCard.getAbsolutePath() + "/.MeineQuittungDaten/cacheimage.jpg";
			originBitmap = BitmapFactory.decodeFile(path, bmOptions);
		} else if (mIntent.getStringExtra("image").equals("imageuri")) {
			Uri uri = Uri.parse(mIntent.getStringExtra("imageUri"));
			try {

				originBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			path = sdCard.getAbsolutePath() + "/.MeineQuittungDaten/" + mIntent.getStringExtra("image") + ".jpg";
			originBitmap = BitmapFactory.decodeFile(path, bmOptions);
		}

		if (mIntent.getStringExtra("image").equals("newimage")) {
			Log.i("wichtig", "geht");
			bmWidth = originBitmap.getWidth();
			bmHeight = originBitmap.getHeight();
			bitmap = Bitmap.createScaledBitmap(originBitmap, bmWidth, bmHeight, true);
			rotation = +90;
			bitmap = RotateBitmap(bitmap, 90);
			originBitmap = RotateBitmap(originBitmap, 90);
		} else if (mIntent.getStringExtra("image").equals("imageuri")) {
			Log.i("wichtig", "geht");
			bmWidth = originBitmap.getWidth();
			bmHeight = originBitmap.getHeight();
			bitmap = Bitmap.createScaledBitmap(originBitmap, bmWidth, bmHeight, true);

		} else {
			Log.i("wichtig", "geht");
			bmWidth = originBitmap.getWidth();
			bmHeight = originBitmap.getHeight();
			Log.i("wichtig", "" + bmWidth);
			Log.i("wichtig", "" + bmHeight);
			bitmap = Bitmap.createScaledBitmap(originBitmap, bmWidth, bmHeight, true);
		}

		bgrImage = new BitmapDrawable(bitmap);
		image.setImageDrawable(bgrImage);

		image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (fullSize) {
					ll2.setVisibility(View.GONE);
					rl.setVisibility(View.GONE);
					button2.setVisibility(View.GONE);
					fullSize = false;
				} else {
					ll2.setVisibility(View.VISIBLE);
					rl.setVisibility(View.VISIBLE);
					button2.setVisibility(View.VISIBLE);
					fullSize = true;
				}
			}
		});

		button2 = (ImageButton) findViewById(R.id.imageButton1);
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (bmWidth < bmHeight) {
					bitmap = Bitmap.createScaledBitmap(bitmap, bmHeight, bmWidth, true);
				} else {
					bitmap = Bitmap.createScaledBitmap(bitmap, bmWidth, bmHeight, true);
				}

				bitmap = RotateBitmap(bitmap, 90);
				originBitmap = RotateBitmap(originBitmap, 90);
				bgrImage = new BitmapDrawable(bitmap);
				image.setImageDrawable(bgrImage);
			}
		});

		// image.setRotation(90);
		// image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT));
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				for (int i = 0; i < et.getText().length(); i++) {
					if (et.getText().charAt(i) == '#') {
						showToast("'#' ist nicht erlaubt.");
						return;
					}

				}
				if (writing) {
					in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					showToast("Kontrolliere das Ablaufdatum und drücke auf Ok.");

					writing = false;
					return;
				}

				Intent mIntent = getIntent();
				if (!et.getText().toString().equals("")) {
					OutputStream fOut = null;
					File dir = new File(sdCard.getAbsolutePath() + "/.MeineQuittungDaten/");

					dir.mkdir();

					File file = new File(dir, et.getText() + ".jpg");
					if (!file.exists()) {
						button.setEnabled(false);
						long startMillis = 0;
						long endMillis = 0;

						Calendar beginTime = Calendar.getInstance();
						beginTime.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), 8, 0);
						startMillis = beginTime.getTimeInMillis();
						Calendar endTime = Calendar.getInstance();
						endTime.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), 24, 0);
						endMillis = endTime.getTimeInMillis();

						ContentResolver cr = getContentResolver();
						ContentValues values = new ContentValues();
						values.put(Events.CALENDAR_ID, 1);
						values.put(Events.TITLE, "Ihre Quittung '" + et.getText() + "' ist dann Abgelaufen");
						values.put(Events.EVENT_TIMEZONE, "Germany/Berlin");
						values.put(Events.DTSTART, startMillis);
						values.put(Events.DTEND, endMillis);

						// values.put("description", "Ihre Quittung '" +
						// et.getText() + "' ist bald Abgelaufen.");

						// values.put("visibility", true);
						// values.put("hasAlarm", this.hasAlarm);
						long eventID;

						if (mIntent.getStringExtra("image").equals("newimage")
								|| mIntent.getStringExtra("image").equals("imageuri")) {
							Uri uri = cr.insert(Events.CONTENT_URI, values);
							eventID = Long.parseLong(uri.getLastPathSegment());

							ContentValues reminders = new ContentValues();
							reminders.put(Reminders.EVENT_ID, eventID);
							reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
							reminders.put(Reminders.MINUTES, 10080);

							cr.insert(Reminders.CONTENT_URI, reminders);
						} else {
							eventID = Long.parseLong(mIntent.getStringExtra("fileID"));
							Uri updateUri = null;
							updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
							getContentResolver().update(updateUri, values, null, null);
						}
						try {

							fOut = new FileOutputStream(file);
							bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
							// saving the Bitmap to a file compressed as a JPEG
							// with 85% compression rate
							fOut.flush();
							fOut.close(); // do not forget to close the stream

							MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(),
									file.getName(), file.getName());

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						bitmap.recycle();

						mIntent.putExtra("fileDate", dp.getDayOfMonth() + "#" + dp.getMonth() + "#" + dp.getYear());

						mIntent.putExtra("fileName", "" + et.getText());

						mIntent.putExtra("fileID", "" + eventID);

						setResult(RESULT_OK, mIntent);

						finish();
					} else if (et.getText().toString().equals(mIntent.getStringExtra("image"))
							&& !mIntent.getStringExtra("image").equals("newimage")) {
						button.setEnabled(false);
						bitmap.recycle();

						long eventID = Long.parseLong(mIntent.getStringExtra("fileID"));

						long startMillis = 0;
						long endMillis = 0;

						Calendar beginTime = Calendar.getInstance();
						beginTime.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), 8, 0);
						startMillis = beginTime.getTimeInMillis();
						Calendar endTime = Calendar.getInstance();
						endTime.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), 24, 0);
						endMillis = endTime.getTimeInMillis();

						Uri updateUri = null;
						ContentValues values = new ContentValues();
						values.put(Events.TITLE, "Ihre Quittung '" + et.getText() + "' ist dann Abgelaufen");
						values.put(Events.EVENT_TIMEZONE, "Germany/Berlin");
						values.put(Events.DTSTART, startMillis);
						values.put(Events.DTEND, endMillis);

						updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
						getContentResolver().update(updateUri, values, null, null);

						mIntent.putExtra("fileDate", dp.getDayOfMonth() + "#" + dp.getMonth() + "#" + dp.getYear());

						mIntent.putExtra("fileName", "" + et.getText());

						mIntent.putExtra("fileID", "" + eventID);

						setResult(RESULT_OK, mIntent);

						finish();
					} else {
						showToast("Name wird bereits verwendet.");
					}
				}
			}

		});
	}

	protected void showToast(String hint) {
		Toast.makeText(getBaseContext(), hint, Toast.LENGTH_LONG).show();
	}

	public static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}

}
