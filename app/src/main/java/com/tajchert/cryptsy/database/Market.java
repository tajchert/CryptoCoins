package com.tajchert.cryptsy.database;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Market implements Parcelable, Comparable<Market>, Serializable{
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
	
	public String name;
	public boolean isSubscribed;
	public com.tajchert.cryptsy.database.PriceRecord last = new com.tajchert.cryptsy.database.PriceRecord();
	public long time;
	
	public double amount;
	
	public double priceUSD;
	
	public boolean useDollars;
	public double pricePoint;
	public double pricePointDol;
	
	public double priceLTC;
	public double priceBTC;
	public double priceXPM;
	
	public Calendar date;
	
	
	
	@SerializedName("primarycode")
	public String primarycode;
	
	@SerializedName("primaryname")
	public String primaryname;
	
	@SerializedName("secondarycode")
	public String secondarycode;
	
	@SerializedName("secondaryname")
	public String secondaryname;
	
	
	@SerializedName("marketid")
	public int marketid;
	
	@SerializedName("lasttradeprice")
	public double lasttradeprice = 0;
	
	
	
	
	public Calendar getDate() {
		return date;
	}

	public void setDate(String date) {
		try {
			this.date = Calendar.getInstance();
			//this.date.setTime(iso8601Format.parse("2013-12-30 04:06:39"));
			this.date.setTime(iso8601Format.parse(date));
		} catch (ParseException e) {
			Log.e("CryptoCoins", "Error while parsing string to date (SpeedRecord class)");
		}
	}
	
	public void setValues(Market in){
		
		name = in.name;
		primarycode = in.primarycode;
		secondarycode = in.secondarycode;
		
		marketid = in.marketid;
		lasttradeprice = in.lasttradeprice;
		
		amount = in.amount;
		
		priceUSD = in.priceUSD;
		priceLTC = in.priceLTC;
		priceBTC = in.priceBTC;
		priceXPM = in.priceXPM;
		
		date = in.date;
	}
	
	
	public Market(Parcel in) {
		readFromParcel(in);
	}
	public Market(){
		Date date = null;
		/*speedHistory = new ArrayList<Float> ();
		datesHistory = new ArrayList<Calendar> ();
		chartDays = new ArrayList<String>();
		staleHistory = new ArrayList<Float> ();
		sharesHistory = new ArrayList<Long>();*/
	}

	private void readFromParcel(Parcel in) {
		
		/*speedHistory = new ArrayList<Float>();
		in.readList(speedHistory,null);*/
		
		name = in.readString();
		primarycode = in.readString();
		secondarycode = in.readString();
		
		marketid = in.readInt();
		lasttradeprice = in.readDouble();
		
		amount = in.readDouble();
		
		priceUSD = in.readDouble();
		priceLTC = in.readDouble();
		priceBTC = in.readDouble();
		priceXPM = in.readDouble();
		
		date = (Calendar) in.readSerializable();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(name);
		dest.writeString(primarycode);
		dest.writeString(secondarycode);
		
		dest.writeInt(marketid);
		dest.writeDouble(lasttradeprice);
		dest.writeDouble(amount);
		dest.writeDouble(priceUSD);
		dest.writeDouble(priceLTC);
		dest.writeDouble(priceBTC);
		dest.writeDouble(priceXPM);
		
		dest.writeSerializable(date);
	}
	
	public static final Creator CREATOR = new Creator() {
		public Market createFromParcel(Parcel in) {
			return new Market(in);
		}

		public Market[] newArray(int size) {
			return new Market[size];
		}
	};
	

	@Override
	public int compareTo(Market arg) {
		return this.primarycode.compareTo(arg.primarycode);
	}

}
