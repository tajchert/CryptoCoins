package com.tajchert.cryptsy.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.tajchert.cryptsy.R;
import com.tajchert.cryptsy.database.Market;
import com.tajchert.cryptsy.database.MarketDataSource;
import com.tajchert.cryptsy.json.CryptsyResults;
import com.tajchert.cryptsy.json.GetCryptsyMarkets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Settings extends AppCompatActivity {
	private MarketCheckAdapter marketListAdapter;
	ArrayList<Market> cryptsyMarkets;

	private final static String PREF_NAME = "com.tajchert.cryptsy";
	private final static String MARKET_LIST = "com.tajchert.cryptsy.marketlist";
	private final static String DATE_TIME_KEY = "com.tajchert.cryptsy.firstrundate";

	ProgressDialog progress;
	
	private MarketDataSource datasource;
	private static boolean isRefreshing;
	SharedPreferences prefs;
	ListView marketlist;
	Button saveButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		prefs = this.getSharedPreferences(PREF_NAME, 0);

		marketlist = (ListView) findViewById(R.id.listViewMarkets);
		marketlist.setDividerHeight(1);
		saveButton = (Button) findViewById(R.id.buttonSave);

		//this.deleteDatabase("markets.db");
		datasource = new MarketDataSource(this);

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
			cryptsyMarkets = (ArrayList<Market>) readFromFile();
			setList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(isNetworkAvailable() && (cryptsyMarkets == null || cryptsyMarkets.size() == 0)) {
			//Internet connection
			if(!isRefreshing) {
				CryptsyMarkets MyTask = new CryptsyMarkets();
				MyTask.execute();
			}
		} else if (cryptsyMarkets == null || cryptsyMarkets.size() == 0){
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
		cryptsyMarkets = marketListAdapter.marketList;
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
				Collections.sort(cryptsyMarkets);
				prefs.edit().putBoolean(MARKET_LIST, true).apply();
				prefs.edit().putLong(DATE_TIME_KEY, System.currentTimeMillis()).apply();
			} catch (Exception e) {
			}
			Log.d("CryptoCoins", "doInBackground - Executed");
			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("CryptoCoins", "onPostExecute");
			for(Market market: cryptsyMarkets){
				datasource.createMarket(market.marketid, market.isSubscribed, market.primarycode, market.secondarycode, market.primaryname + "-" + market.secondarycode);
				datasource.createMarketPrice(market.lasttradeprice, 0, Calendar.getInstance(), market.marketid);
				//datasource.createMarket(market.primaryname + " - " + market.secondarycode, market.primarycode, market.secondarycode,market.lasttradeprice, market.priceUSD, market.pricePoint, market.pricePointDol, Calendar.getInstance(), market.marketid, market.isSubscribed);
			}
			setList();
			isRefreshing = false;
			progress.dismiss();
		}

		@Override
		protected void onPreExecute() {
			isRefreshing = true;
			cryptsyMarkets = new ArrayList<Market>();
			progress = new ProgressDialog(Settings.this);
			progress.setTitle("CryptoCoins");
			progress.setMessage("Wait while downloading list of all currencies...");
			progress.setIndeterminate(true);
			progress.setCancelable(false);
			progress.setCanceledOnTouchOutside(false);
			progress.show();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	private void setList() {

		SparseArray<Market> favMarkets = (SparseArray<Market>) datasource.getAllSubscibedMarkets();
		marketListAdapter = new MarketCheckAdapter(Settings.this, R.layout.list_market_item, cryptsyMarkets, datasource, asList(favMarkets));
		marketlist.setAdapter(marketListAdapter);
		marketListAdapter.notifyDataSetChanged();
	}



	private List<Market> readFromFile() {
		String ret = "";
		try {
			InputStream inputStream = openFileInput("cryptsyMarkets.txt");
			if ( inputStream != null ) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ( (receiveString = bufferedReader.readLine()) != null ) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();

				Gson gson = new Gson();
				CryptsyResults results = gson.fromJson(ret, CryptsyResults.class);
				return results.markets;
			}
		}
		catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}

		return null;
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


	public static <C> ArrayList<C> asList(SparseArray<C> sparseArray) {
		if (sparseArray == null) return null;
		ArrayList<C> arrayList = new ArrayList<C>(sparseArray.size());
		for (int i = 0; i < sparseArray.size(); i++)
			arrayList.add(sparseArray.valueAt(i));
		return arrayList;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item.getItemId() == R.id.action_refresh) {
			try {
				if(!isRefreshing) {
					dialogRefresh("Refresh", "You are about to refresh whole list of currencies, which is around 5MB.\n\n Do you want to proceed? ");
				}
			} catch (Exception e) {
				dialog("CryptoCoins", "Some very unexpected error during downloading data.\nPlease try again later.");
			}
		}
		return false;
	}

	private void dialogRefresh(String title, String content){
		AlertDialog alertDialog = new AlertDialog.Builder(Settings.this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(content);
		alertDialog.setCancelable(true);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				CryptsyMarkets MyTask = new CryptsyMarkets();
				MyTask.execute();
			}
		});
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alertDialog.show();
	}
}
