package com.tajchert.cryptsy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MarketDataSource {
	private SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
	
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	
	List<Market> markets;
	private String[] marketColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_MARKET_ID,
			 MySQLiteHelper.COLUMN_SUBSCRIBED , MySQLiteHelper.COLUMN_PRICE_POINT, MySQLiteHelper.COLUMN_PRICE_POINT_DOL
			, MySQLiteHelper.COLUMN_CODE_PRIMARY, MySQLiteHelper.COLUMN_CODE_SECONDARY, MySQLiteHelper.COLUMN_NAME_FULL};
	private String[] priceColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_MARKET_ID,
			 MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_PRICE, MySQLiteHelper.COLUMN_PRICE_DOL};
	/*
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_MARKET_ID, MySQLiteHelper.COLUMN_DATE ,
			MySQLiteHelper.COLUMN_PRICE, MySQLiteHelper.COLUMN_SUBSCRIBED , MySQLiteHelper.COLUMN_PRICE_POINT, MySQLiteHelper.COLUMN_PRICE_DOL
			, MySQLiteHelper.COLUMN_CODE_PRIMARY, MySQLiteHelper.COLUMN_CODE_SECONDARY, MySQLiteHelper.COLUMN_NAME_FULL};*/

	public MarketDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 7));//DELETE older then 7 days
		database.execSQL("DELETE FROM " + MySQLiteHelper.TABLE_PRICES + " WHERE " + MySQLiteHelper.COLUMN_DATE + " < '"+iso8601Format.format(date.getTime())+"'");
		database.execSQL("VACUUM");
	}

	public void close() {
		dbHelper.close();
	}
	public boolean exists(int marketid) {
		Cursor cursor = database.rawQuery("Select 1 from "
				+ MySQLiteHelper.TABLE_MARKETS + " where "
				+ MySQLiteHelper.COLUMN_MARKET_ID + " = ?",
				new String[] { marketid+"" });
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	}
	public void addMarket(double price, double pricedol,  Calendar date, int marketid, boolean subscribed){
		if(exists(marketid)){
			updateRow( price, pricedol,  date,  marketid,  subscribed);
			Log.d("CryptoCoins", "UPDATE");
		}else{
			//createMarket( price,  date,  marketid,  subscribed);
			Log.d("CryptoCoins", "CREATE");
		}
	}
	public void updateListRow(int marketid, boolean subscribed) {
		
		ContentValues values = new ContentValues();
		if(subscribed){
			Log.d("CryptoCoins", "1");
			values.put(MySQLiteHelper.COLUMN_SUBSCRIBED, 1);
		}else{
			Log.d("CryptoCoins", "0");
			values.put(MySQLiteHelper.COLUMN_SUBSCRIBED, 0);
		}
		database.update(MySQLiteHelper.TABLE_MARKETS, values, MySQLiteHelper.COLUMN_MARKET_ID +" = "+ marketid , null);
		/*
		Cursor cursor = database.query(MySQLiteHelper.TABLE_MARKETS, allColumns, MySQLiteHelper.COLUMN_MARKET_ID +" = "+ marketid, null,null, null, null);
		cursor.moveToFirst();
		Market newSpeed = cursorToMarket(cursor);
		Log.d("CryptoCoins", "newSpeed: " +newSpeed.marketid +":"+ newSpeed.isSubscribed);
		
		cursor.close();
		return newSpeed;*/
	}
	public void updateRow(double price, double pricedol, Calendar date, int marketid, boolean subscribed) {
		String dateS = iso8601Format.format(date.getTime());
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PRICE, price);
		values.put(MySQLiteHelper.COLUMN_PRICE_DOL, price);
		values.put(MySQLiteHelper.COLUMN_DATE, dateS);
		if(subscribed){
			values.put(MySQLiteHelper.COLUMN_SUBSCRIBED, 1);
		}else{
			values.put(MySQLiteHelper.COLUMN_SUBSCRIBED, 0);
		}
		database.update(MySQLiteHelper.TABLE_MARKETS, values, MySQLiteHelper.COLUMN_MARKET_ID +" = "+ marketid , null);
		
	}
	
	public void createMarketPrice(double price, double pricedol, Calendar date, int marketid) {
		String dateS = iso8601Format.format(date.getTime());
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_MARKET_ID, marketid);
		values.put(MySQLiteHelper.COLUMN_PRICE, price);
		values.put(MySQLiteHelper.COLUMN_PRICE_DOL, pricedol);
		values.put(MySQLiteHelper.COLUMN_DATE, dateS);
		database.insert(MySQLiteHelper.TABLE_PRICES, null, values);
	}
	public void updateMarketPricePoint(int marketid, double pricepoint, double pricepointdol) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PRICE_POINT, pricepoint);
		values.put(MySQLiteHelper.COLUMN_PRICE_POINT_DOL, pricepointdol);
		database.update(MySQLiteHelper.TABLE_MARKETS, values, MySQLiteHelper.COLUMN_MARKET_ID +" = "+ marketid , null);
	}
	/*public Market createMarket(String fullName, String primarycode, String secondarycode, double price, double priceDol,
			double pricePoint, double dolPoint, Calendar date, int marketid, boolean subscribed) {
		if(exists(marketid)){
			return null;
		}else{
			String dateS = iso8601Format.format(date.getTime());
			Log.d("CryptoCoins", "Creating new row.");
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.COLUMN_PRICE, price);
			values.put(MySQLiteHelper.COLUMN_DATE, dateS);
			values.put(MySQLiteHelper.COLUMN_PRICE_DOL, priceDol);
			values.put(MySQLiteHelper.COLUMN_PRICE_POINT, pricePoint);
			values.put(MySQLiteHelper.COLUMN_PRICE_POINT_DOL, dolPoint);
			values.put(MySQLiteHelper.COLUMN_CODE_PRIMARY, primarycode);
			values.put(MySQLiteHelper.COLUMN_CODE_SECONDARY, secondarycode);
			values.put(MySQLiteHelper.COLUMN_NAME_FULL, fullName);
			if(subscribed){
				values.put(MySQLiteHelper.COLUMN_SUBSCRIBED, 1);
			}else{
				values.put(MySQLiteHelper.COLUMN_SUBSCRIBED, 0);
			}
			values.put(MySQLiteHelper.COLUMN_MARKET_ID, marketid);
			long insertId = database.insert(MySQLiteHelper.TABLE_MARKETS, null, values);
			Cursor cursor = database.query(MySQLiteHelper.TABLE_MARKETS, allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,null, null, null);
			cursor.moveToFirst();
			Market newSpeed = cursorToMarket(cursor);
			cursor.close();
			return newSpeed;
		}
	}*/
	public Market createMarket(int marketid, boolean subscribed, String primaryCode, String secondaryCode, String fullName) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_MARKET_ID, marketid);
		if(subscribed){
			values.put(MySQLiteHelper.COLUMN_SUBSCRIBED, 1);
		}else{
			values.put(MySQLiteHelper.COLUMN_SUBSCRIBED, 0);
		}
		values.put(MySQLiteHelper.COLUMN_CODE_PRIMARY, primaryCode);
		values.put(MySQLiteHelper.COLUMN_CODE_SECONDARY, secondaryCode);
		values.put(MySQLiteHelper.COLUMN_NAME_FULL, fullName);
		
		
		long insertId = database.insert(MySQLiteHelper.TABLE_MARKETS, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_MARKETS, marketColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,null, null, null);
		cursor.moveToFirst();
		Market newSpeed = cursorToMarket(cursor);
		cursor.close();
		return newSpeed;
	}

	public void deleteMarket(Market market) {
		long id = market.marketid;
		database.delete(MySQLiteHelper.TABLE_MARKETS, MySQLiteHelper.COLUMN_MARKET_ID + " = " + id, null);
	}
	public List<Market> getAllMarkets() {
		markets = new ArrayList<Market>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_MARKETS, marketColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Market mark = cursorToMarket(cursor);
			markets.add(mark);
			cursor.moveToNext();
		}
		cursor.close();
		return markets;
	}
	public SparseArray<Market> getAllSubscibedMarkets() {
		SparseArray tmpmarkets = new SparseArray<Market>();
		try {
			Cursor cursor = database.query(MySQLiteHelper.TABLE_MARKETS, marketColumns, MySQLiteHelper.COLUMN_SUBSCRIBED + " = 1", null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Market speed = cursorToMarket(cursor);
				tmpmarkets.put(speed.marketid, speed);
				cursor.moveToNext();
			}
			cursor.close();
			return tmpmarkets;
		} catch (Exception e) {
			return null;
		}
	}
	public int countLastPrices(int marketId) {
		PriceRecord pRecord = new PriceRecord();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_PRICES, priceColumns, MySQLiteHelper.COLUMN_MARKET_ID+" = " +  marketId , null, null, null, null);
		
		//Cursor cursor = database.rawQuery("SELECT "+priceColumns +" FROM " + MySQLiteHelper.TABLE_PRICES +" WHERE " + MySQLiteHelper.COLUMN_MARKET_ID+" = " +  marketId , null);
		cursor.moveToFirst();
		Log.d("CryptoCoins", "countLastPrices size: " + cursor.getCount());
		return cursor.getCount();
	}
	public PriceRecord getMarketLastPrice(int marketId) {
		PriceRecord pRecord = new PriceRecord();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_PRICES, priceColumns, MySQLiteHelper.COLUMN_MARKET_ID+" = " +  marketId + " order by date DESC limit 1", null, null, null, null);
		
		//Cursor cursor = database.rawQuery("SELECT "+priceColumns +" FROM " + MySQLiteHelper.TABLE_PRICES +" WHERE " + MySQLiteHelper.COLUMN_MARKET_ID+" = " +  marketId , null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			//Log.d("CryptoCoins", "got date: " + cursor.getString(2));
			/*Market speed = cursorToMarket(cursor);
			prices[0] = cursor.getDouble(columnIndex);
			tmpmarkets.put(speed.marketid, speed);*/
			pRecord.price = cursor.getDouble(3);
			pRecord.priceDol = cursor.getDouble(4);
			pRecord.setDate(cursor.getString(2));
			cursor.moveToNext();
		}
		cursor.close();
		return pRecord;
	}
	/*public SparseArray<Market> getAllSubscibedMarketsOld() {
		SparseArray tmpmarkets = new SparseArray<Market>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_MARKETS, allColumns, MySQLiteHelper.COLUMN_SUBSCRIBED + " = 1", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Market speed = cursorToMarket(cursor);
			tmpmarkets.put(speed.marketid, speed);
			Log.d("CryptoCoins", "getAllSubscibedMarkets put: " + speed.marketid);
			cursor.moveToNext();
		}
		cursor.close();
		Log.d("CryptoCoins", "getAllSubscibedMarkets size: " + tmpmarkets.size());
		return tmpmarkets;
	}*/

	private Market cursorToMarket(Cursor cursor) {
		/*{ MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_MARKET_ID,
			 MySQLiteHelper.COLUMN_SUBSCRIBED , MySQLiteHelper.COLUMN_PRICE_POINT, MySQLiteHelper.COLUMN_PRICE_POINT_DOL
			, MySQLiteHelper.COLUMN_CODE_PRIMARY, MySQLiteHelper.COLUMN_CODE_SECONDARY, MySQLiteHelper.COLUMN_NAME_FULL};*/
		Market market = new Market();
		market.marketid = cursor.getInt(1);
		//market.setDate(cursor.getString(2));
		//market.lasttradeprice = cursor.getFloat(3);
		if(cursor.getInt(2) == 1){
			market.isSubscribed = true;
		}else{
			market.isSubscribed = false;
		}
		market.pricePoint = cursor.getFloat(3);
		market.pricePointDol = cursor.getFloat(4);
		//Log.d("CryptoCoins", "cursor.getFloat(3): " + cursor.getFloat(3));
		//Log.d("CryptoCoins", "cursor.getFloat(4): " + cursor.getFloat(4));
		market.primarycode = cursor.getString(5);
		market.secondarycode = cursor.getString(6);
		market.name = cursor.getString(7);
		return market;
	}
}
