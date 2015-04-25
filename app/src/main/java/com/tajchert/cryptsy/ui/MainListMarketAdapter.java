package com.tajchert.cryptsy.ui;


import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tajchert.cryptsy.R;
import com.tajchert.cryptsy.database.Market;


public class MainListMarketAdapter extends BaseAdapter {
	private ArrayList<Market> data;
	protected AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ; 
	private DecimalFormat df = new DecimalFormat("##0.00000000");
	private DecimalFormat sf = new DecimalFormat("##0.#");
	Context c;
	//SimpleDateFormat  format = new SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault());

	MainListMarketAdapter(ArrayList<Market> data, Context c) {
		this.data = data;
		this.c = c;
		//df.setMaximumFractionDigits(8);
		fadeIn.setDuration(1200);
		fadeIn.setFillAfter(true);
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_market_detail_item, null);
		}
		TextView NameText = (TextView) v.findViewById(R.id.textName);
		TextView textBTC = (TextView) v.findViewById(R.id.textBTC);
		TextView textUSD = (TextView) v.findViewById(R.id.textUSD);
		TextView textCurrency = (TextView) v.findViewById(R.id.textCurrency);
		TextView textViewPercentage = (TextView) v.findViewById(R.id.textViewPercentage);
		TextView textViewPercentageBTC = (TextView) v.findViewById(R.id.textViewPercentageBTC);
		TextView textViewPercentagePricePoint = (TextView) v.findViewById(R.id.textViewPercentagePricePoint);
		TextView textViewPercentagePricePointBTC = (TextView) v.findViewById(R.id.textViewPercentagePricePointBTC);
		
		
		TextView textBTCPoint = (TextView) v.findViewById(R.id.textBTCPoint);
		TextView textUSDPoint = (TextView) v.findViewById(R.id.textUSDPoint);
		
		TextView textViewAgo = (TextView) v.findViewById(R.id.textViewAgo);
		
		
		
		//TextView DateText = (TextView) v.findViewById(R.id.DateText);
		Market obj = data.get(position);
		//Market obj = data.get(data.keyAt(position));
		//Log.d("CryptoCoins", "List: " + obj.marketid);
		//String fullname = obj.primaryname +"-"+obj.secondarycode;
		NameText.setText(obj.name); 
		//NameText.setText(obj.primaryname +"-"+obj.secondarycode);
		textBTC.setText(df.format(obj.lasttradeprice)+"");
		textCurrency.setText(obj.secondarycode +":");
		textUSD.setText(df.format(obj.priceUSD)+"");
		double diff = 0;
		
		
		double pricePoint = (double)Math.round(obj.pricePoint * 100000000) / 100000000;
		double pricePointDol =(double)Math.round(obj.pricePointDol * 100000000) / 100000000;
		double lastTradePrice =(double)Math.round(obj.lasttradeprice * 100000000) / 100000000;
		double priceDol =(double)Math.round(obj.priceUSD * 100000000) / 100000000;
		double prevPrice =(double)Math.round(obj.last.price * 100000000) / 100000000;
		double prevPriceDol =(double)Math.round(obj.last.priceDol * 100000000) / 100000000;
		
		textBTCPoint.setText(df.format(pricePoint));
		textUSDPoint.setText(df.format(pricePointDol));
		
		/*long timeDiff = Math.abs(obj.time - obj.last.date.getTimeInMillis());
		long hours = timeDiff / (60 * 60 * 1000);
	    long minutes = timeDiff / (60 * 1000);
	    minutes = minutes - 60 * hours;
	    long seconds = timeDiff / (1000);

	    if (hours > 0) {
	    	textViewAgo.setText(hours + " h " + minutes + " min");
	    } else {
	        if (minutes > 0)
	        	textViewAgo.setText(minutes + " min");
	        else {
	        	textViewAgo.setText(seconds + " min");
	        }
	    }*/
		
		if(pricePoint != 0 && pricePointDol != 0){
			if (pricePoint > lastTradePrice) {
				diff = pricePoint - lastTradePrice;
				diff = (diff / pricePoint) * 100;
				textViewPercentagePricePointBTC.setText("-" + sf.format(diff)+ "%");
				textViewPercentagePricePointBTC.setTextColor(Color.RED);
			} else if (pricePoint < lastTradePrice) {
				diff = lastTradePrice - pricePoint;
				diff = (diff / pricePoint) * 100;
				textViewPercentagePricePointBTC.setText("+" + sf.format(diff)+ "%");
				textViewPercentagePricePointBTC.setTextColor(Color.GREEN);
			} else if (pricePoint == lastTradePrice){
				textViewPercentagePricePointBTC.setText("0%");
				textViewPercentagePricePointBTC.setTextColor(Color.WHITE);
			}

			if (pricePointDol > priceDol) {
				diff = pricePointDol - priceDol;
				diff = (diff / pricePointDol) * 100;
				textViewPercentagePricePoint.setText("-" + sf.format(diff)+ "%");
				textViewPercentagePricePoint.setTextColor(Color.RED);
			} else if (pricePointDol < priceDol) {
				diff = priceDol - pricePointDol;
				diff = (diff / pricePointDol) * 100;
				textViewPercentagePricePoint.setText("+"+sf.format(diff)+ "%");
				textViewPercentagePricePoint.setTextColor(Color.GREEN);
			} else if (pricePointDol == priceDol){
				textViewPercentagePricePoint.setText("0%");
				textViewPercentagePricePoint.setTextColor(Color.WHITE);
			}
		}else{
			textViewPercentagePricePoint.setText("X%");
			textViewPercentagePricePoint.setTextColor(Color.WHITE);
			textViewPercentagePricePointBTC.setText("X%");
			textViewPercentagePricePointBTC.setTextColor(Color.WHITE);
		}
		if (prevPrice > lastTradePrice) {
			diff = prevPrice - lastTradePrice;
			diff = (diff / prevPrice) * 100;
			textViewPercentageBTC.setText("-" + sf.format(diff) + "%");
			textViewPercentageBTC.setTextColor(Color.RED);
		} else if (prevPrice < lastTradePrice) {
			diff = lastTradePrice - prevPrice;
			diff = (diff / prevPrice) * 100;
			textViewPercentageBTC.setText("+" + sf.format(diff) + "%");
			textViewPercentageBTC.setTextColor(Color.GREEN);
		} else if (prevPrice == lastTradePrice){
			textViewPercentageBTC.setText("0%");
			textViewPercentageBTC.setTextColor(Color.WHITE);
		}
		if (prevPriceDol > priceDol) {
			diff = prevPriceDol - priceDol;
			diff = (diff / prevPriceDol) * 100;
			textViewPercentage.setText("-" + sf.format(diff) + "%");
			textViewPercentage.setTextColor(Color.RED);
		} else if (prevPriceDol < priceDol) {
			diff = priceDol - prevPriceDol;
			diff = (diff / prevPriceDol) * 100;
			textViewPercentage.setText("+" + sf.format(diff) + "%");
			textViewPercentage.setTextColor(Color.GREEN);
		} else if(prevPriceDol == priceDol){
			textViewPercentage.setText("0%");
			textViewPercentage.setTextColor(Color.WHITE);
		}
		
		textViewPercentage.startAnimation(fadeIn);
		textViewPercentageBTC.startAnimation(fadeIn);
		textViewPercentagePricePoint.startAnimation(fadeIn);
		textViewPercentagePricePointBTC.startAnimation(fadeIn);
		
		textBTC.startAnimation(fadeIn);
		textUSD.startAnimation(fadeIn);
		
		textBTCPoint.startAnimation(fadeIn);
		textUSDPoint.startAnimation(fadeIn);
		
		return v;
	}
}