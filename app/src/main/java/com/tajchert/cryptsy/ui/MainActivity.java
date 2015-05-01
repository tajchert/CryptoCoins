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
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tajchert.cryptsy.R;
import com.tajchert.cryptsy.database.Market;
import com.tajchert.cryptsy.database.MarketDataSource;
import com.tajchert.cryptsy.ebtc.EbtcTicker;
import com.tajchert.cryptsy.ebtc.GetEbtc;
import com.tajchert.cryptsy.json.GetCryptsyMarkets;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends Activity {
	
	private final static String PREF_NAME = "com.tajchert.cryptsy";
	private final static String DATE_TIME_UPDATE = "com.tajchert.cryptsy.lastrundate";
	private final static String DATE_TIME_KEY = "com.tajchert.cryptsy.firstrundate";
	
	private int positionOfClick;
	
	ProgressDialog progress;
	TextView btcPriceText;
	TextView ltcPriceText;
	TextView xpmPriceText;
	
	Button buttonRefresh;
	
	private com.tajchert.cryptsy.ui.MainListMarketAdapter marketListAdapter;
	private ArrayList <Market> adapterList;
	private MarketDataSource datasource;
	
	private int numberOfStarts = 0;
	
	ListView marketlist;

	EbtcTicker tickerBTC;
	EbtcTicker tickerLTC;
	double tickerXPM;
	ArrayList<Market> allCryptsyMarkets;
	SparseArray<Market> favMarkets;
	DecimalFormat df = new DecimalFormat("0.#");
	DecimalFormat prices = new DecimalFormat("0.###");

	
	SharedPreferences prefs;
	//https://btc-e.com/api/2/ltc_usd/ticker
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//AppRater.app_launched(this);
		prefs = this.getSharedPreferences(PREF_NAME, 0);
		
		
		long todayDate = Calendar.getInstance().getTimeInMillis();
		long firstRunDate = prefs.getLong(DATE_TIME_KEY, todayDate); 
		
		/*if(firstRunDate == todayDate){
			startActivity(new Intent(MainActivity.this, Settings.class));
		}*/
		df.setMaximumFractionDigits(8);
		
		
		datasource = new MarketDataSource(this);
		
		btcPriceText = (TextView) findViewById(R.id.textViewBTCprice);
		ltcPriceText = (TextView) findViewById(R.id.textViewLTCprice);
		xpmPriceText = (TextView) findViewById(R.id.textViewXPMprice);
		
		buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
		
		adapterList = new ArrayList<Market>();
		marketlist = (ListView) findViewById(R.id.listViewMarketsSubscribed);
		marketlist.setDividerHeight(5);
		
		
		buttonRefresh.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(favMarkets != null && favMarkets.size()>0){
					EbtcStock MyTask= new EbtcStock();
					MyTask.execute();
				}else{
					dialog("Database with markets not set properly", "To solve it, go to app settings and refresh markets, or clean app data in system settings");
				}
				
			}
		});
		
		
		//BTCStock MyTask= new BTCStock();
        //MyTask.execute();
		
		
	}
	@Override
	protected void onResume() {
		numberOfStarts = 0;
		datasource.open();
		//readFile();
		long todayDate = Calendar.getInstance().getTimeInMillis();
		if(isNetworkAvailable()){
			
				//readFile();
				favMarkets = (SparseArray<Market>) datasource.getAllSubscibedMarkets();
				if(favMarkets.size()>0){
					for(int i = 0; i < favMarkets.size(); i++) {
						   Market mark = favMarkets.get(favMarkets.keyAt(i));
						   Log.d("CryptoCoins", "market: " + mark.name +" - "+mark.lasttradeprice);
						}
					if( todayDate - prefs.getLong(DATE_TIME_UPDATE, 0) > 15000){
						EbtcStock MyTask= new EbtcStock();
						MyTask.execute();
					}else{
						Toast.makeText(getApplicationContext(), "Wait 15 sec. before next update.", Toast.LENGTH_SHORT).show();
					}
				}else{
					startActivity(new Intent(MainActivity.this, Settings.class));
				}
			
		}else{
			//No internet connection
			dialog("CryptoCoins", "No Internet connection. Try again later.");
		}
		super.onResume();
	}
	@Override
	protected void onPause() {
		try {
			progress.dismiss();
			progress = null;
		} catch (Exception e) {
		}
		datasource.close();
		super.onPause();
	}
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	
	/*private void setList() {
		
		marketListAdapter = new MainListMarketAdapter((ArrayList<Market>) adapterList, MainActivity.this);
		marketlist.setAdapter(marketListAdapter);

		marketListAdapter.notifyDataSetChanged();
		
		marketlist.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				  positionOfClick = position;
				  AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				  builder.setMessage("Do you want to set up a price point to calculate change in % ?").setPositiveButton("Yes", dialogClickListener)
				      .setNegativeButton("No/Delete", dialogClickListener).show();
			  }
			}); 
		
	}*/
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
	        	datasource.updateMarketPricePoint(favMarkets.get(favMarkets.keyAt(positionOfClick)).marketid, favMarkets.get(favMarkets.keyAt(positionOfClick)).lasttradeprice, favMarkets.get(favMarkets.keyAt(positionOfClick)).priceUSD);
	        	
	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	        	datasource.updateMarketPricePoint(favMarkets.get(favMarkets.keyAt(positionOfClick)).marketid, 0, 0);
	            break;
	        }
	    }
	};
	DialogInterface.OnClickListener innerDialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
	        	datasource.updateMarketPricePoint(favMarkets.get(favMarkets.keyAt(positionOfClick)).marketid, 0, favMarkets.get(favMarkets.keyAt(positionOfClick)).priceUSD);
	            break;

	        case DialogInterface.BUTTON_NEUTRAL:
	        	datasource.updateMarketPricePoint(favMarkets.get(favMarkets.keyAt(positionOfClick)).marketid, favMarkets.get(favMarkets.keyAt(positionOfClick)).lasttradeprice, 0);
	            break;
	        }
	    }
	};
	private Runnable returnRes = new Runnable() {
        public void run() {
            if(adapterList != null && adapterList.size() > 0){
            	marketListAdapter = new com.tajchert.cryptsy.ui.MainListMarketAdapter((ArrayList<Market>) adapterList, MainActivity.this);
        		marketlist.setAdapter(marketListAdapter);

        		marketListAdapter.notifyDataSetChanged();
        		
        		marketlist.setOnItemClickListener(new OnItemClickListener() {
        			  @Override
        			  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        				  positionOfClick = position;
        				  AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        				  builder.setMessage("Do you want to set up a price point to calculate change in % ?").setPositiveButton("Yes", dialogClickListener)
        				      .setNegativeButton("No/Delete", dialogClickListener).show();
        			  }
        			}); 
            }
            try {
				progress.dismiss();
				progress = null;
			} catch (Exception e) {
			}
            marketListAdapter.notifyDataSetChanged();
        }
      };

	//Update Single Markets
	private class UpdateSingleMarkets extends AsyncTask<String, Void, String> {
		int successes = 0;
		@Override
		protected String doInBackground(String... params) {
			/*if(cryptsyMarkets == null || cryptsyMarkets.size()==0){
				return "Executed";
			}*/
			favMarkets = (SparseArray<Market>) datasource.getAllSubscibedMarkets();
			if(favMarkets != null && favMarkets.size() >0){
				GetCryptsyMarkets tmpJson = new GetCryptsyMarkets();
				//SparseArray<Market> tmpArr = new SparseArray<Market>();
				adapterList = new ArrayList<Market>();
				Market tmpMarket = new Market();
				Log.d("CryptoCoins", "favMarkets size: " + favMarkets.size());
				long timeNow = Calendar.getInstance().getTimeInMillis();
				int key = 0;
				for(int i = 0; i < favMarkets.size(); i++) {
				   key = favMarkets.keyAt(i);
				   // get the object by the key.
				   Market market = favMarkets.get(key);
						try {
							//Log.d("CryptoCoins", "market: " + market.primarycode +" - "  + market.secondarycode +" @ " +market.marketid);
							tmpJson.url = "http://pubapi.cryptsy.com/api.php?method=singlemarketdata&marketid=" + market.marketid;
							tmpMarket = tmpJson.getSingle(MainActivity.this);
							if(tmpMarket.secondarycode.equals("BTC")){
								tmpMarket.priceUSD = tmpMarket.lasttradeprice * tickerBTC.avg;
							}
							if(tmpMarket.secondarycode.equals("LTC")){
								tmpMarket.priceUSD = tmpMarket.lasttradeprice * tickerLTC.avg;
							}
							if(tmpMarket.secondarycode.equals("XPM")){
								tmpMarket.priceUSD = tmpMarket.lasttradeprice * tickerXPM;
							}
							tmpMarket.last = datasource.getMarketLastPrice(tmpMarket.marketid);
							//datasource.countLastPrices(market.marketid);
							datasource.createMarketPrice(tmpMarket.lasttradeprice, tmpMarket.priceUSD, Calendar.getInstance(), tmpMarket.marketid);
							//datasource.addMarket(tmpMarket.lasttradeprice, Calendar.getInstance(), tmpMarket.marketid,  true);
							//datasource.updateRow(tmpMarket.lasttradeprice, tmpMarket.priceUSD, Calendar.getInstance(), tmpMarket.marketid);
							market.time = timeNow;
							market.lasttradeprice = tmpMarket.lasttradeprice;
							market.priceUSD =  tmpMarket.priceUSD;
							market.last.date = tmpMarket.last.date;
							market.last.price = tmpMarket.last.price;
							market.last.priceDol = tmpMarket.last.priceDol;
							market.secondarycode = tmpMarket.secondarycode;
							market.primarycode = tmpMarket.primarycode;
							
							//Log.d("CryptoCoins", "market: " + df.format(market.lasttradeprice) +" - "  + df.format(tmpMarket.lasttradeprice));
							//market = tmpMarket;
							//Log.d("CryptoCoins", "market code: " + favMarkets.get(key).primarycode);
							//tmpArr.put(tmpMarket.marketid, tmpMarket);
							adapterList.add(market);
							runOnUiThread(returnRes);
							successes +=1;
						} catch (Exception e) {
							try {
								progress.dismiss();
								progress = null;
							} catch (Exception e4) {
							}
							//dialog("Temporary problem (cryptsy.com)", "Problem with access to some or all markets.\nSorry. Please try again later.");
						}
				} 
				//favMarkets = tmpArr;
			}else {
				try {
					successes=favMarkets.size();
				} catch (Exception e) {
				}
				try {
					progress.dismiss();
					progress = null;
				} catch (Exception e4) {
				}
			}
			
			Log.d("CryptoCoins", "Done");
			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {
			if(favMarkets != null && favMarkets.size() >0){
				Log.d("CryptoCoins", "Post");
				numberOfStarts = 0;
				Log.d("CryptoCoins", "successes: " + successes +"-" + favMarkets.size());
				if(successes < favMarkets.size()){
					dialog("Some fails", "Due to problems with Crypsty.com we were able to get only "+ successes +" out of " + favMarkets.size() +" markets.\nTry again in a second");
				}
			}
			/*try {
				progress.dismiss();
				progress = null;
			} catch (Exception e) {
			}*/
			
		}

		@Override
		protected void onPreExecute() {
			
			try {
				progress.dismiss();
				progress = null;
			} catch (Exception e) {
			}
        	progress = new ProgressDialog(MainActivity.this);
			progress.setTitle("CryptoCoins");
			progress.setMessage("Updating selected markets.");
			progress.show();
			
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}
	
	//EBTC price
		private class EbtcStock extends AsyncTask<String, Void, String> {
			
			Market xpmBtcMarket = new Market();
	        @Override
	        protected String doInBackground(String... params) {
	        	GetEbtc btcJson = new GetEbtc();
	        	tickerBTC = btcJson.makeAndGet(0);
	        	GetEbtc ltcJson = new GetEbtc();
	        	tickerLTC = ltcJson.makeAndGet(1);

	        	//XPM TODO
	        	GetCryptsyMarkets tmpJson = new GetCryptsyMarkets();
	        	tmpJson.url = "http://pubapi.cryptsy.com/api.php?method=singlemarketdata&marketid=63";
	        	try {
					xpmBtcMarket = tmpJson.getSingle(MainActivity.this);
				} catch (Exception e) {
					try {
						xpmBtcMarket = tmpJson.getSingle(MainActivity.this);
					} catch (Exception e1) {
						//Toast.makeText(getApplicationContext(), "Temporary problem with cryptsy.com, try again later.", Toast.LENGTH_SHORT).show();
						//dialog("Temporary problem (cryptsy.com)", "Problem with access to XPM market...");
					}
					//TODO repeat
				}
	        	
	        	
	            return "Executed";
	        }

	        @Override
	        protected void onPostExecute(String result) {

	        	btcPriceText.setText(prices.format(tickerBTC.avg)+"$");
	        	ltcPriceText.setText(prices.format(tickerLTC.avg)+"$");
	        	//XPM TODO
	        	if(tickerBTC.avg!=0 && xpmBtcMarket.lasttradeprice!= 0){
	        		tickerXPM = xpmBtcMarket.lasttradeprice * tickerBTC.avg;
		        	xpmPriceText.setText(prices.format(tickerXPM)+"$");
	        	}
	        	try {
					progress.dismiss();
					progress = null;
					
				} catch (Exception e) {
				}
	        	if(progress == null && numberOfStarts == 0){
	        		numberOfStarts +=1;
	        		long todayDate = Calendar.getInstance().getTimeInMillis();
	        		if(todayDate - prefs.getLong(DATE_TIME_UPDATE, 0) > 15000){
	        			prefs.edit().putLong(DATE_TIME_UPDATE, todayDate).commit();
			        	UpdateSingleMarkets MyTask= new UpdateSingleMarkets();
			            MyTask.execute();
	        		}else{
	        			
	        		}
	        	}
	        	/*CryptsyStock MyCryptsyStockTask= new CryptsyStock();
	            MyCryptsyStockTask.execute();*/
	        	//Log.d("Cryptsy", "tickerBTC:" + tickerBTC.avg);
	        	//Log.d("Cryptsy", "tickerLTC:" + tickerLTC.avg);
	            
	        }

	        @Override
	        protected void onPreExecute() {
	        	try {
					progress.dismiss();
					progress = null;
				} catch (Exception e) {
				}
	        	progress = new ProgressDialog(MainActivity.this);
				progress.setTitle("CryptoCoins");
				progress.setMessage("Updating USD prices (BTC and LTC).");
				progress.show();
	        }

	        @Override
	        protected void onProgressUpdate(Void... values) {}
	    }
		private void dialog(String title, String content){
			AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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
