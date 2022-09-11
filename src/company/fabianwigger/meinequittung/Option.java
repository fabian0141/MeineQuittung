package company.fabianwigger.meinequittung;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Option extends Activity {
	Intent intent;

	EditText ed, ed2, ed3, ed4;

	boolean registring, progress, login, finished;

	Button button4, button5;
	Thread thread2;
	SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);

		// Get from the SharedPreferences
		settings = getApplicationContext().getSharedPreferences("MQPrefs", 0);
		int loggedIn = settings.getInt("loggedIn", 0);

		intent = new Intent(this, SureActivity.class);

		ImageButton button = (ImageButton) findViewById(R.id.imageButton1);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/MeineQuittung/"));
				startActivity(intent);
			}
		});

		ImageButton button2 = (ImageButton) findViewById(R.id.imageButton2);
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		Button button3 = (Button) findViewById(R.id.button1);
		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(intent, 1);
			}
		});

		ed = (EditText) findViewById(R.id.editText1);

		ed2 = (EditText) findViewById(R.id.editText2);

		ed3 = (EditText) findViewById(R.id.editText3);

		ed4 = (EditText) findViewById(R.id.editText4);

		button4 = (Button) findViewById(R.id.button2);
		button4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (finished) {
					Log.i("wichtig", "geht");
					disable();
				} else {

					if (registring) {
						if (ed2.getText().toString().equals(ed3.getText().toString())) {
							sendData(3);
						}else{
							showToast("Passwort falsch wiederholt.");
						}
					} else {
						if (!login) {
							sendData(2);
							login = true;
						}
					}
				}
			}
		});

		
		button5 = (Button) findViewById(R.id.button3);
		button5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (finished) {
					Log.i("wichtig", "geht");
					disable();
				} else {
				ed3.setVisibility(View.VISIBLE);
				ed4.setVisibility(View.VISIBLE);
				registring = true;
				}
			}
		});
		if (loggedIn == 1) {
			ed.setEnabled(false);
			ed2.setEnabled(false);
			ed3.setEnabled(false);
			ed4.setEnabled(false);
			button4.setEnabled(false);
			button5.setEnabled(false);
		}

	}

	Thread thread;

	protected void sendData(int i) {
		try {

			thread = new Thread(new ClientThread(i));
			thread.start();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				File sdCard = Environment.getExternalStorageDirectory();

				File parentDir = new File(sdCard.getAbsolutePath() + "/.MeineQuittungDaten/");
				File[] files = parentDir.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
				showToast("Alles gelöscht.");
			}
		}
	}

	class ClientThread implements Runnable {

		public ClientThread(int i) {
			this.i = i;

		}

		int i;
		Context context;

		@Override
		public void run() {

			try {
				
				progress = false;
				SSLSocket clientSocket;
				String modifiedSentence;
				BufferedReader inFromServer;
				DataOutputStream outToServer;
				String sentence = "";

				KeyStore trustStore = KeyStore.getInstance("BKS");
				InputStream trustStoreStream = getResources().openRawResource(R.raw.mqclient);
				trustStore.load(trustStoreStream, "niceguy".toCharArray());

				TrustManagerFactory trustManagerFactory = TrustManagerFactory
						.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				trustManagerFactory.init(trustStore);

				// KeyManagerFactory keyManagerFactory = null;
				// keyManagerFactory =
				// KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				// keyManagerFactory.init(trustStore, "niceguy".toCharArray());

				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
				SSLSocketFactory factory = sslContext.getSocketFactory();
				clientSocket = (SSLSocket) factory.createSocket("192.168.1.5", 5000);

				outToServer = new DataOutputStream(clientSocket.getOutputStream());
				inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				for (int j = 0; j < i; j++) {
					switch (j) {
					case 0:
						sentence = sentence + "vtifi7%IDIdri6ciRR&DRIR&d" + ed.getText();
						break;
					case 1:
						sentence = sentence + "vtifi7%IDIdri6ciRR&DRIR&d" + ed2.getText();
						break;
					case 2:
						sentence = sentence + "vtifi7%IDIdri6ciRR&DRIR&d" + ed4.getText();
						break;
					}

				}
				Log.i("wichtig",sentence);
				

					
				outToServer.writeBytes(sentence + '\n');
								
				modifiedSentence = inFromServer.readLine();
				if (modifiedSentence.equals("worked")) {

					SharedPreferences.Editor editor = settings.edit();
					editor.putInt("loggedIn", 1);
					editor.apply();
					showToast("Angemeldet.");
					finished = true;
					login = false;
					Looper.loop();
				} else if (modifiedSentence.equals("used")) {
					showToast("Name wird bereits benutzt.");
					Thread.sleep(500);
					Looper.loop();
				} else if (modifiedSentence.equals("failed")) {
					showToast("Name oder Passwort falsch oder nicht Vorhanden.");
					Thread.sleep(500);
					Looper.loop();
				} else {
					showToast("Konnte nicht verarbeiten.");
					
					Thread.sleep(500);
					Looper.loop();
				}
				
				
				clientSocket.close();
			} catch (Exception e1) {
				showToast("Keine Verbindung hergestellt.");
				e1.printStackTrace();
				Looper.loop();
			}

		}
		private void showToast(String hint) {
			Looper.prepare();
			Toast.makeText(getBaseContext(), hint, Toast.LENGTH_LONG).show();
			
		}
	}

	protected void disable() {
		ed.setEnabled(false);
		ed2.setEnabled(false);
		ed3.setEnabled(false);
		ed4.setEnabled(false);
		button4.setEnabled(false);
		button5.setEnabled(false);
	}

	private void showToast(String hint) {
		
		Toast.makeText(getBaseContext(), hint, Toast.LENGTH_LONG).show();
		
	}
}
