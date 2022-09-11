package company.fabianwigger.meinequittung;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract.Events;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class MainActivity extends Activity {

	LinearLayout llFolders;
	ScrollView svFiles;
	List<Button> folderButtons = new ArrayList<Button>();
	List<Button> fileButtons = new ArrayList<Button>();

	Button button1, button2, addHere;
	int sdk = android.os.Build.VERSION.SDK_INT;
	int buttonCount = -1, chosenButton = 0, maxFilesButton = 0;
	ImageButton imgButton;
	LinearLayout llFiles;

	float x1, x2;
	float y1, y2;
	String fileDate = "1#1#1", fileName = "Datei Name", fileData, fileID;

	BufferedWriter writeFiles, writeFolders, cacheWriteFolders;
	OutputStreamWriter osw;
	boolean firstInstallation = true;
	boolean newFile;
	boolean[] marked;
	boolean marking;
	boolean moveFile;
	String[] stayingFiles;
	boolean addFile;
	int filesToMove;
	Intent intent; // set Intents
	Intent intent2;
	Intent intent3;
	Intent intent4;
	Intent intent5;
	Intent intent6;
	Intent intent7;
	Intent intent8;
	int chosenFile;
	float dpi;
	
	List<Integer> folderFiles = new ArrayList<Integer>();

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dpi = getApplicationContext().getResources().getDisplayMetrics().density;
		
		
		llFolders = (LinearLayout) findViewById(R.id.linearlayout1); // get
																		// Layouts
		llFiles = (LinearLayout) findViewById(R.id.linearLayout2);
		svFiles = (ScrollView) findViewById(R.id.scrollView1);

		svFiles.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				int MIN_DISTANCE = 250;

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					x1 = event.getX();
					y1 = event.getY();
					Log.i("wichtig", "1: " + y1);
					break;
				case MotionEvent.ACTION_UP:
					x2 = event.getX();
					y2 = event.getY();
					float deltaX = x2 - x1;
					Log.i("wichtig", "1: " + y2 + ":" + y1);
					if (Math.abs(deltaX) > MIN_DISTANCE && Math.abs(y2 - y1) < 200) {
						if (x2 > x1) {
							if (chosenButton > 0)
								changeFolder(chosenButton - 1);
						}

						else {
							if (chosenButton < buttonCount)
								changeFolder(chosenButton + 1);
						}

					}
					break;
				}
				return false;
			}
		});

		intent = new Intent(this, NewFolder.class); // set Intents
		intent2 = new Intent(this, RenameFolder.class);
		intent3 = new Intent(this, CameraPreviewActivity.class);
		intent4 = new Intent(this, ToolsActivity.class);
		intent5 = new Intent(this, FilePreview.class);
		intent6 = new Intent(this, SureActivity.class);
		intent7 = new Intent(this, Option.class);
		intent8 = new Intent(this, NewReceipt.class);

		// --------------------------------------------Buttons-------------------------------------------------------

		addHere = (Button) findViewById(R.id.button1); // + hier hinzufügen
														// Button
		addHere.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				llFiles.removeView(addHere);
				if (moveFile) {
					createButton(stayingFiles);
				} else {
					newFile = false;
					createButton();
				}
			}
		});
		
		llFiles.removeView(addHere);

		button1 = (Button) findViewById(R.id.button4); // newFolder Button
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!marking) {
					startActivityForResult(intent, 1);
				}
			}
		});

		imgButton = (ImageButton) findViewById(R.id.imageButton2); // changeFolder
		imgButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!marking) {
					intent2.putExtra("folderName", folderButtons.get(chosenButton).getText());
					startActivityForResult(intent2, 2);
				}
			}
		});

		ImageButton button3 = (ImageButton) findViewById(R.id.imagebutton3); // cameraButton
		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/*
				 * if(!marking && !addFile){ addFile = true;
				 * llFiles.addView(addHere); newFile = true; }
				 */
				if (!marking && !addFile) {
					startActivityForResult(intent3, 3);
				}
			}
		});

		ImageButton button4 = (ImageButton) findViewById(R.id.imageButton4); // toolsButton
		button4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!addFile) {
					startActivityForResult(intent4, 4);
				}
			}
		});

		ImageButton button5 = (ImageButton) findViewById(R.id.imageButton1); // optionsButton
		button5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!addFile) {
					startActivity(intent7);
				}
			}
		});

		createFileFolder();

		if (firstInstallation) { // on first app run
			SharedPreferences settings = getApplicationContext().getSharedPreferences("MQPrefs", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("loggedIn", 0);
			editor.apply();
			File camTest = new File(
					Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten");

			try {
				camTest.mkdirs();
				String filename2 = "foldername.txt";
				File fileFolder = new File(
						Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten",
						filename2);

				writeFolders = new BufferedWriter(new FileWriter(fileFolder, true));
				writeFolders.write("ALLGEMEIN");
				writeFolders.newLine();
				writeFolders.flush();
				createFileFolder();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		Intent getIntent = getIntent();
		String action = getIntent.getAction();
		String type = getIntent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {

			Log.i("wichtig", "geht");

			Uri imageUri = (Uri) getIntent.getParcelableExtra(Intent.EXTRA_STREAM);
			if (imageUri != null) {
				Log.i("wichtig", imageUri.toString());
				intent8.putExtra("image", "imageuri");
				intent8.putExtra("imageUri", imageUri.toString());
				startActivityForResult(intent8, 3);
			}
		}

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void createButton(String readLine) {
		final int count2;
		// -------------------------- create new Button from
		// file----------------
		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		buttonCount++;
		button.setText(readLine);
		button.setBackground(null);
		button.setTextColor(Color.parseColor("#FFFFFF"));
		if (buttonCount == 0) {
			button.setBackgroundResource(R.drawable.underbar2);
		}

		count2 = buttonCount;
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				changeFolder(count2);
			}
		});
		llFolders.removeView(button1); // remove folder-add-button
		llFolders.addView(button); // add folder-button
		llFolders.addView(button1); // add folder-add-button

		folderButtons.add(button);

		folderFiles.add(0);
	}

	private void createFileFolder() {
		// ---------------------------------------------------get Folders and
		// Buttons-------------------------

		String filename2 = "foldername.txt"; // create file
		String filename = "filename0.txt";
		File fileFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten",
				filename2);
		File fileFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten",
				filename);

		String readLine = null;
		folderFiles.add(0);

		try {
			writeFolders = new BufferedWriter(new FileWriter(fileFolder, true)); // create
																					// writer
			writeFiles = new BufferedWriter(new FileWriter(fileFile, true));

			BufferedReader folderReader = new BufferedReader(new FileReader(fileFolder)); // create
																							// Reader
			BufferedReader fileReader = new BufferedReader(new FileReader(fileFile));
			while ((readLine = folderReader.readLine()) != null) { // while
																	// something
																	// is read

				createButton(readLine);
				firstInstallation = false;
			}

			while ((readLine = fileReader.readLine()) != null) {
				final String[] line;
				line = readLine.split("\\#");

				final int filesButtonInt = maxFilesButton;
				maxFilesButton++;
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				params.gravity = Gravity.START;
				Button button = new Button(this);
				button.setText(line[0] + "  -  " + line[1] + "." + line[2] + "." + line[3]);
				button.setBackgroundResource(R.drawable.file);
				button.setLayoutParams(params);
				button.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (marking) {
							if (marked[filesButtonInt] == false) {
								marked[filesButtonInt] = true;
								llFiles.getChildAt(filesButtonInt).setBackgroundResource(R.drawable.underbarmarked);
							} else {
								marked[filesButtonInt] = false;
								llFiles.getChildAt(filesButtonInt).setBackgroundResource(R.drawable.underbarmarking);
							}
						} else {
							chosenFile = filesButtonInt;
							intent5.putExtra("fileName", line[0]);
							intent5.putExtra("fileDate", line[1] + "#" + line[2] + "#" + line[3]);
							intent5.putExtra("fileID", line[4]);
							startActivityForResult(intent5, 5);
						}
					}
				});
				button.setOnLongClickListener(new View.OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						Log.i("wichtig", "geht");
						if (marking) {
							marking = false;
							changeFolder(chosenButton);
						} else {
							Log.i("wichtig", "geht");
							changeFolder(chosenButton);
							for (int i = 0; i < llFiles.getChildCount(); i++) {
								// changeFolder(chosenButton);
								llFiles.getChildAt(i).setBackgroundResource(R.drawable.underbarmarking);
								marked[i] = false;
								marking = true;
							}
						}
						Log.i("wichtig", "geht");
						return true;
					}
				});
				button.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {

						int MIN_DISTANCE = 250;

						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							x1 = event.getX();
							y1 = event.getY() + filesButtonInt * 50 * dpi;
							break;
						case MotionEvent.ACTION_UP:
							x2 = event.getX();
							y2 = event.getY() + filesButtonInt * 50 * dpi;
							float deltaX = x2 - x1;
							Log.i("wichtig", "2: " + y2 + ":" + y1);
							if (Math.abs(deltaX) > MIN_DISTANCE && Math.abs(y2 - y1) < 200) {
								if (x2 > x1) {
									if (chosenButton > 0)
										changeFolder(chosenButton - 1);
								}

								else {
									if (chosenButton < buttonCount)
										changeFolder(chosenButton + 1);
								}

							}
							break;
						}
						return false;
					}
				});

				llFiles.addView(button);
				folderFiles.set(chosenButton, folderFiles.get(chosenButton) + 1);
				fileButtons.add(button);
			}

			marked = new boolean[llFiles.getChildCount()];

			folderReader.close();
			fileReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) // create a Button
	private void createButton() {

		final int filesButtonInt = maxFilesButton;
		maxFilesButton++;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.START;
		Button button = new Button(this);
		
		button.setBackgroundResource(R.drawable.file);
		button.setLayoutParams(params);

		String[] fileDates = fileDate.split("\\#");

		final String line[] = { fileName, fileDates[0], fileDates[1], fileDates[2], fileID };
		button.setText(fileName + " - " + fileDates[0] + "." + fileDates[1] + "." + fileDates[2]);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (marking) {
					if (marked[filesButtonInt] == false) {
						marked[filesButtonInt] = true;
						llFiles.getChildAt(filesButtonInt).setBackgroundResource(R.drawable.underbarmarked);
					} else {
						marked[filesButtonInt] = false;
						llFiles.getChildAt(filesButtonInt).setBackgroundResource(R.drawable.underbarmarking);
					}
				} else {
					chosenFile = filesButtonInt;
					intent5.putExtra("fileName", line[0]);
					intent5.putExtra("fileDate", line[1] + "#" + line[2] + "#" + line[3]);
					intent5.putExtra("fileID", line[4]);
					startActivityForResult(intent5, 5);
				}

			}
		});
		button.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Log.i("wichtig", "geht");
				if (marking) {
					marking = false;
					changeFolder(chosenButton);
				} else {
					Log.i("wichtig", "geht");
					changeFolder(chosenButton);
					for (int i = 0; i < llFiles.getChildCount(); i++) {
						// changeFolder(chosenButton);
						llFiles.getChildAt(i).setBackgroundResource(R.drawable.underbarmarking);
						marked[i] = false;
						marking = true;
					}
				}
				Log.i("wichtig", "geht");
				return true;
			}
		});
		button.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				int MIN_DISTANCE = 250;

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					x1 = event.getX();
					y1 = event.getY() + filesButtonInt * 50 * dpi;
					break;
				case MotionEvent.ACTION_UP:
					x2 = event.getX();
					y2 = event.getY() + filesButtonInt * 50 * dpi;
					float deltaX = x2 - x1;
					Log.i("wichtig", "2: " + y2 + ":" + y1);
					if (Math.abs(deltaX) > MIN_DISTANCE && Math.abs(y2 - y1) < 200) {
						if (x2 > x1) {
							if (chosenButton > 0)
								changeFolder(chosenButton - 1);
						}

						else {
							if (chosenButton < buttonCount)
								changeFolder(chosenButton + 1);
						}

					}
					break;
				}
				return false;
			}
		});
		llFiles.addView(button);
		fileButtons.add(button);

		fileData = fileName + "#" + fileDate + "#" + fileID;
		folderFiles.set(chosenButton, folderFiles.get(chosenButton) + 1);

		marked = new boolean[llFiles.getChildCount()];

		try {

			File fileFile = new File(
					Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten",
					"filename" + chosenButton + ".txt");
			writeFiles = new BufferedWriter(new FileWriter(fileFile, true));
			writeFiles.write(fileData);
			writeFiles.newLine();
			writeFiles.flush();
		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addFile = false;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) // create a Button
	private void createButton(String[] filesToStay) {

		try {

			File fileFile = new File(
					Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten",
					"filename" + chosenButton + ".txt");
			writeFiles = new BufferedWriter(new FileWriter(fileFile, true));
			for (int i = 0; i < filesToMove; i++) {
				writeFiles.write(filesToStay[i]);
				writeFiles.newLine();
				writeFiles.flush();
			}

		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		moveFile = false;
		changeFolder(chosenButton);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) // return of intents
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1) { // create new Folder
			if (resultCode == RESULT_OK) {
				String foldername = data.getStringExtra("edittextvalue");

				createButton(foldername);
				String filename = "filename" + buttonCount + ".txt";
				File fileFolder = new File(
						Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten", filename);
				try {

					fileFolder.createNewFile();

					writeFolders.write(foldername.toUpperCase());

					writeFolders.newLine();

					writeFolders.flush();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} else if (requestCode == 2) { // remove Folder
			if (resultCode == RESULT_OK) {
				String foldername = data.getStringExtra("edittextvalue");

				String filename = "foldername.txt";

				File fileFolder = new File(
						Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten", filename);
				String readLine = null;

				String[] foldersToStay = new String[buttonCount + 1];
				int counter = 0, folderCounter = 0;

				if (foldername.equals("destroy") && (buttonCount == 0 || folderFiles.get(chosenButton) != 0)) { // if
																												// destroy
																												// but
																												// in
																												// use
																												// return
					showToast("Ordner wird momenatn benutzt oder ist der einzig vorhandene Ordner.");
					return;
				}
				try {
					// read file check if rename or delete
					BufferedReader folderReader = new BufferedReader(new FileReader(fileFolder));
					while ((readLine = folderReader.readLine()) != null) {

						if (counter++ != chosenButton) {

							foldersToStay[folderCounter++] = readLine;
						} else if (!foldername.equals("destroy")) {

							foldersToStay[folderCounter++] = foldername;

						}
					}
					fileFolder.delete();
					fileFolder = new File(
							Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten",
							filename);
					writeFolders = new BufferedWriter(new FileWriter(fileFolder, true));

					for (int i = 0; i < folderCounter; i++) { // write in
																// textfile back
						writeFolders.write(foldersToStay[i]);
						writeFolders.newLine();
						writeFolders.flush();
					}

					folderReader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (foldername.equals("destroy")) { // update buttons

					llFolders.removeView(folderButtons.get(chosenButton));
					folderButtons.remove(chosenButton);

					for (int i = 0; i < buttonCount; i++) {
						final int a = i;
						folderButtons.get(i).setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {

								changeFolder(a);

							}

						});
					}
					File fromFile = new File(
							Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten",
							"filename" + chosenButton + ".txt");
					File toFile = null;

					fromFile.delete();
					Log.i("wichtig", "geht");
					for (int i = chosenButton; i < folderCounter; i++) {
						Log.i("wichtig", "" + i);
						fromFile = new File(
								Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten",
								"filename" + (i + 1) + ".txt");
						toFile = new File(
								Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten",
								"filename" + i + ".txt");

						fromFile.renameTo(toFile);

					}

					buttonCount--;
					chosenButton = 0;
					folderButtons.get(chosenButton).setBackgroundResource(R.drawable.underbar2);
					changeFolder(0);

				} else {
					folderButtons.get(chosenButton).setText(foldername); // rename
																			// button
				}

			}
		} else if (requestCode == 3) { // new File
			if (resultCode == RESULT_OK) {
				llFiles.addView(addHere);
				svFiles.post(new Runnable() {
					@Override
					public void run() {
						svFiles.fullScroll(View.FOCUS_DOWN);
					}
				});
				fileDate = data.getStringExtra("fileDate");
				fileName = data.getStringExtra("fileName");
				fileID = data.getStringExtra("fileID");
				Log.i("wichtig", "FileID: " + fileID);
				newFile = true;
				addFile = true;

			}
		} else if (requestCode == 4) { // change files
			if (resultCode == RESULT_OK) {

				int buttonTool = data.getIntExtra("toolsButton", 0);

				switch (buttonTool) {
				case 1: // change Files Directory
					if (marking) {
						moveFile = true;
						moveDeleteFiles(true, false);
					} else {
						showToast("Erst markieren.");
					}
					break;
				case 2: // delete Files
					if (marking) {
						startActivityForResult(intent6, 6);

					} else {
						showToast("Erst markieren.");
					}
					break;
				case 3:
					if (marking) {
						marking = false;
						changeFolder(chosenButton);
					} else {
						changeFolder(chosenButton);
						for (int i = 0; i < llFiles.getChildCount(); i++) {
							// changeFolder(chosenButton);
							llFiles.getChildAt(i).setBackgroundResource(R.drawable.underbarmarking);
							marked[i] = false;
							marking = true;
						}
					}
					break;
				case 4:
					changeFolder(chosenButton);
					if (!marking) {
						for (int i = 0; i < llFiles.getChildCount(); i++) {
							llFiles.getChildAt(i).setBackgroundResource(R.drawable.underbarmarked);
							marked[i] = true;
							marking = true;
						}
					}
					break;

				}
			}
		} else if (requestCode == 5) { // change files
			if (resultCode == RESULT_OK) {

				int changeFile = data.getIntExtra("changeFile", 0);

				switch (changeFile) {
				case 1:
					marking = true;
					moveFile = true;
					marked[chosenFile] = true;
					moveDeleteFiles(true, false);
					changeFolder(chosenButton);
					break;
				case 2:
					marked[chosenFile] = true;
					marking = true;
					moveDeleteFiles(false, false);
					break;
				case 3:
					marked[chosenFile] = true;
					marking = true;
					moveDeleteFiles(false, true);

					llFiles.addView(addHere);
					svFiles.post(new Runnable() {
						@Override
						public void run() {
							svFiles.fullScroll(View.FOCUS_DOWN);
						}
					});
					fileDate = data.getStringExtra("fileDate");
					fileName = data.getStringExtra("fileName");
					fileID = data.getStringExtra("fileID");
					newFile = true;
					break;

				}
			}
		} else if (requestCode == 6) { // change files
			if (resultCode == RESULT_OK) {
				moveDeleteFiles(false, false);
			}
		} else if (requestCode == 7) { // change files
			if (resultCode == RESULT_OK) {
				moveDeleteFiles(false, false);
			}
		}

	}

	private void moveDeleteFiles(boolean move, boolean refresh) {
		filesToMove = 0;
		String filename = "filename" + chosenButton + ".txt";

		File fileFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten",
				filename);
		String readLine = null;
		Log.i("wichtig", "" + maxFilesButton);
		String[] filesToStay = new String[maxFilesButton + 1];

		stayingFiles = new String[maxFilesButton + 1];
		int fileCounter = 0;

		try {
			cacheWriteFolders = new BufferedWriter(new FileWriter(fileFolder, true)); // read
																						// file
																						// check
																						// if
																						// rename
																						// or
																						// delete
			BufferedReader folderReader = new BufferedReader(new FileReader(fileFolder));
			File file;
			File sdCard = Environment.getExternalStorageDirectory();
			String[] getNameAndID;
			for (int i = 0; i < marked.length; i++) {
				if ((readLine = folderReader.readLine()) != null) {
					if (marked[i] == false) {
						filesToStay[fileCounter++] = readLine;
					} else {
						folderFiles.set(chosenButton, folderFiles.get(chosenButton) - 1);
						if (move) {
							stayingFiles[filesToMove++] = readLine;
						} else {
							getNameAndID = readLine.split("\\#");
							file = new File(
									sdCard.getAbsolutePath() + "/.MeineQuittungDaten/" + getNameAndID[0] + ".jpg");
							if (!refresh) {
								file.delete();
								Uri deleteUri = null;
								deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI,
										Long.parseLong(getNameAndID[4]));
								getContentResolver().delete(deleteUri, null, null);
							}

						}

					}
				}
			}

			fileFolder.delete();
			fileFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten",
					filename);
			cacheWriteFolders = new BufferedWriter(new FileWriter(fileFolder, true));

			for (int i = 0; i < fileCounter; i++) { // write in textfile back
				cacheWriteFolders.write(filesToStay[i]);
				cacheWriteFolders.newLine();
				cacheWriteFolders.flush();
			}
			cacheWriteFolders.close();
			folderReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		marking = false;
		changeFolder(chosenButton);

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) // changes the chosen folder
	public void changeFolder(int button) {
		if (!marking) {
			maxFilesButton = 0;
			folderButtons.get(chosenButton).setBackground(null);
			chosenButton = button;
			folderButtons.get(chosenButton).setBackgroundResource(R.drawable.underbar2);

			llFiles.removeAllViews();

			String filename = "filename" + chosenButton + ".txt";
			File fileFolder = new File(
					Environment.getExternalStorageDirectory().getAbsolutePath() + "/.MeineQuittungDaten", filename);
			String readLine = null;

			folderFiles.set(chosenButton, 0);
			try {
				BufferedReader folderReader = new BufferedReader(new FileReader(fileFolder));
				while ((readLine = folderReader.readLine()) != null) {
					final String[] line;
					final int filesButtonInt = maxFilesButton;
					maxFilesButton++;
					line = readLine.split("\\#");

					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT);
					params.gravity = Gravity.START;
					Button fileButton = new Button(this);
					fileButton.setText(line[0] + "  -  " + line[1] + "." + line[2] + "." + line[3]);
					fileButton.setBackgroundResource(R.drawable.file);
					fileButton.setLayoutParams(params);
					fileButton.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							if (marking) {
								if (marked[filesButtonInt] == false) {
									marked[filesButtonInt] = true;
									llFiles.getChildAt(filesButtonInt).setBackgroundResource(R.drawable.underbarmarked);
								} else {
									marked[filesButtonInt] = false;
									llFiles.getChildAt(filesButtonInt)
											.setBackgroundResource(R.drawable.underbarmarking);
								}
							} else {
								chosenFile = filesButtonInt;
								intent5.putExtra("fileName", line[0]);
								intent5.putExtra("fileDate", line[1] + "#" + line[2] + "#" + line[3]);
								intent5.putExtra("fileID", line[4]);
								startActivityForResult(intent5, 5);
							}
						}
					});
					fileButton.setOnLongClickListener(new View.OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
							Log.i("wichtig", "geht");
							if (marking) {
								marking = false;
								changeFolder(chosenButton);
							} else {
								Log.i("wichtig", "geht");
								changeFolder(chosenButton);
								for (int i = 0; i < llFiles.getChildCount(); i++) {
									// changeFolder(chosenButton);
									llFiles.getChildAt(i).setBackgroundResource(R.drawable.underbarmarking);
									marked[i] = false;
									marking = true;
								}
							}
							Log.i("wichtig", "geht");
							return true;
						}
					});
					fileButton.setOnTouchListener(new View.OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {

							int MIN_DISTANCE = 250;
							
							switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								x1 = event.getX();
								y1 = event.getY() + filesButtonInt * 50 * dpi;
								Log.i("wichtig", "3: " + y1);
								break;
							case MotionEvent.ACTION_UP:
								x2 = event.getX();
								y2 = event.getY() + filesButtonInt * 50 * dpi;
								float deltaX = x2 - x1;
								Log.i("wichtig", "3: " + y2 + ":" + y1);
								if (Math.abs(deltaX) > MIN_DISTANCE && Math.abs(y2 - y1) < 200) {
									if (x2 > x1) {
										if (chosenButton > 0)
											changeFolder(chosenButton - 1);
									}

									else {
										if (chosenButton < buttonCount)
											changeFolder(chosenButton + 1);
									}

								}
								break;
							}
							return false;
						}
					});
					llFiles.addView(fileButton);

					folderFiles.set(chosenButton, folderFiles.get(chosenButton) + 1);
				}
				marked = new boolean[llFiles.getChildCount()];
				if (newFile || moveFile) {
					llFiles.addView(addHere);
					svFiles.post(new Runnable() {
						@Override
						public void run() {
							svFiles.fullScroll(View.FOCUS_DOWN);
						}
					});
				}
				folderReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void showToast(String hint) {
		Toast.makeText(getBaseContext(), hint, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onBackPressed() {
		if (!marking && !moveFile && !newFile) {
			finish();
		} else if (!moveFile && !newFile) {
			marking = false;
			changeFolder(chosenButton);

		}
	}

}
