package com.tajchert.cryptsy.database;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.util.Log;

public class PriceRecord implements  Serializable {
	
	private static final long serialVersionUID = 1L;
	public int marketid;
	public double price;
	public double priceDol;
	public Calendar date;
	private SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
	
	public void setDate(String date) {
		try {
			this.date = Calendar.getInstance();
			//this.date.setTime(iso8601Format.parse("2013-12-30 04:06:39"));
			this.date.setTime(iso8601Format.parse(date));
		} catch (ParseException e) {
			Log.e("CryptoCoins", "Error while parsing string to date (SpeedRecord class)");
		}
	}
}
