package com.tajchert.cryptsy.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tajchert.cryptsy.R;
import com.tajchert.cryptsy.database.Market;
import com.tajchert.cryptsy.database.MarketDataSource;
import com.tajchert.cryptsy.json.GetCryptsyMarkets;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class Settings extends Activity {
	private com.tajchert.cryptsy.ui.SettingsListMarketAdapter marketListAdapter;
	ArrayList<Market> cryptsyMarkets;

	private final static String PREF_NAME = "com.tajchert.cryptsy";
	private final static String MARKET_LIST = "com.tajchert.cryptsy.marketlist";
	private final static String BTC_SOURCE = "com.tajchert.cryptsy.btcsource";
	private final static String DATE_TIME_KEY = "com.tajchert.cryptsy.firstrundate";

	ProgressDialog progress;
	
	private MarketDataSource datasource;

	SharedPreferences prefs;
	ListView marketlist;
	Button saveButton;
	Button buttonRefresh;
	
	private RadioButton radioButtonBTCE;
	private RadioButton radioButtonMtGox;
	private RadioGroup radioSourceGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		prefs = this.getSharedPreferences(PREF_NAME, 0);

		marketlist = (ListView) findViewById(R.id.listViewMarkets);
		marketlist.setDividerHeight(1);
		saveButton = (Button) findViewById(R.id.buttonSave);
		buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
		
		radioButtonBTCE = (RadioButton) findViewById(R.id.radioButtonBTCE);
		radioButtonMtGox = (RadioButton) findViewById(R.id.radioButtonMtGox);
		radioSourceGroup = (RadioGroup) findViewById(R.id.radioSource);
		//this.deleteDatabase("markets.db");
		datasource = new MarketDataSource(this);
		
		
		buttonRefresh.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					CryptsyMarkets MyTask = new CryptsyMarkets();
					MyTask.execute();
				} catch (Exception e) {
					dialog("CryptoCoins", "Some very unexpected error during downloading data - please report to thetajchert@gmail.com");
				}
			}
		});
		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});

	}
	@Override
	protected void onResume() {
		datasource.open();
		
		cryptsyMarkets = new ArrayList<Market>();
		try {
			readFile();
			setList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(isNetworkAvailable()){
			//Internet connection
			if(prefs.getInt(BTC_SOURCE, 2) == 1){
				radioButtonBTCE.setChecked(true);
			}else{
				radioButtonMtGox.setChecked(true);
			}
			if (!prefs.getBoolean(MARKET_LIST, false) && progress == null){
				CryptsyMarkets MyTask = new CryptsyMarkets();
				MyTask.execute();
			}
		}else{
			//No internet connection
			dialog("CryptoCoins", "No Internet connection. Try again later.");
		}
		super.onResume();
	}
	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	@Override
	public void onBackPressed() {
		cryptsyMarkets = marketListAdapter.data;
		try {
			writeFile();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),"Error during save of markets.", Toast.LENGTH_SHORT).show();
		}
		if(radioButtonBTCE.isChecked()){
			prefs.edit().putInt(BTC_SOURCE, 1).commit();
		}
		if(radioButtonMtGox.isChecked()){
			prefs.edit().putInt(BTC_SOURCE, 2).commit();
		}
		super.onBackPressed();
	}

	// Cryptsy markets
	private class CryptsyMarkets extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.d("CryptoCoins", "doInBackground");
			GetCryptsyMarkets tmpJson = new GetCryptsyMarkets();
			try {
				cryptsyMarkets = (ArrayList<Market>) tmpJson.makeAndGet(Settings.this);
			} catch (Exception e) {
			}
			Collections.sort(cryptsyMarkets);
			prefs.edit().putBoolean(MARKET_LIST, true).commit();
			long todayDate = Calendar.getInstance().getTimeInMillis();
			prefs.edit().putLong(DATE_TIME_KEY, todayDate).commit();
			
			Log.d("CryptoCoins", "doInBackground - Executed");
			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("CryptoCoins", "onPostExecute");
			try {
				writeFile();
				prefs.edit().putBoolean(MARKET_LIST, true).commit();
				Log.d("CryptoCoins", "MARKET_LIST = true");
			} catch (IOException e) {
			}
			for(Market market: cryptsyMarkets){
				datasource.createMarket(market.marketid, market.isSubscribed, market.primarycode, market.secondarycode, market.primaryname + "-" + market.secondarycode);
				datasource.createMarketPrice(market.lasttradeprice, 0, Calendar.getInstance(), market.marketid);
				//datasource.createMarket(market.primaryname + " - " + market.secondarycode, market.primarycode, market.secondarycode,market.lasttradeprice, market.priceUSD, market.pricePoint, market.pricePointDol, Calendar.getInstance(), market.marketid, market.isSubscribed);
			}
			//TODO
			setList();
			progress.dismiss();
			/* for(Market market : cryptsyMarkets){ //Log.d("Cryptsy",
			  market.primarycode+"-"+market.secondarycode +" @ " +
			  market.lasttradeprice); if(market.secondarycode.equals("BTC")){
			 market.priceUSD = market.lasttradeprice * tickerBTC.avg; }
			  if(market.secondarycode.equals("LTC")){ market.priceUSD =
			  market.lasttradeprice * tickerLTC.avg; } } for(CryptsyMarket
			  market : cryptsyMarkets){ try { Log.d("Cryptsy",
			  market.primarycode+"-"+market.secondarycode +" @ " +
			  df.format(market.priceUSD) +"$"); } catch (Exception e) { } }*/
			 

		}

		@Override
		protected void onPreExecute() {
			cryptsyMarkets = new ArrayList<Market>();
			progress = new ProgressDialog(Settings.this);
			progress.setTitle("CryptoCoins");
			progress.setMessage("Wait while downloading list of all currencies...");
			progress.show();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	private void setList() {
		marketListAdapter = new com.tajchert.cryptsy.ui.SettingsListMarketAdapter((ArrayList<Market>) cryptsyMarkets, Settings.this, datasource);
		marketlist.setAdapter(marketListAdapter);
		marketListAdapter.notifyDataSetChanged();
		/*marketlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View view, int position,long id) {
				CheckBox cb = (CheckBox) view.findViewById(R.id.checkBoxSub);
				if (cb.isChecked()) {
					cb.setChecked(false);
					cryptsyMarkets.get(position).isSubscribed = false;
				} else {
					cb.setChecked(true);
					cryptsyMarkets.get(position).isSubscribed = true;
				}
			}
		});*/

		

	}

	private void writeFile() throws IOException {
		FileOutputStream fos = openFileOutput("cryptsyMarkets.obj",Context.MODE_PRIVATE);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(cryptsyMarkets);
		out.close();
		fos.close();
	}

	private void readFile() {
		try {
			FileInputStream fin = openFileInput("cryptsyMarkets.obj");
			ObjectInputStream in = new ObjectInputStream(fin);
			cryptsyMarkets = (ArrayList<Market>) in.readObject();
			setList();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (OptionalDataException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void dialog(String title, String content){
		AlertDialog alertDialog = new AlertDialog.Builder(Settings.this).create();
    	alertDialog.setTitle(title);
    	alertDialog.setMessage(content);
    	alertDialog.setCancelable(true);
    	alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
    		   public void onClick(DialogInterface dialog, int which) {
    			   dialog.dismiss();
    			   }
    			});
    	alertDialog.show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.nothome, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_settings) {
	        startActivity(new Intent(this, Settings.class));
	    }
	    if (item.getItemId() == R.id.action_about) {
	        startActivity(new Intent(this, About.class));
	    }
	    if (item.getItemId() == R.id.action_help) {
	        startActivity(new Intent(this, HelpActivity.class));
	    }
        return false;
    }
}
