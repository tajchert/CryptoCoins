package com.tajchert.cryptsy.database;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());

	public static final String TABLE_MARKETS = "MARKET";
	public static final String TABLE_PRICES = "PRICE";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MARKET_ID = "marketid";
	
	public static final String COLUMN_SUBSCRIBED = "sub";
	public static final String COLUMN_DATE = "date";
	
	public static final String COLUMN_PRICE = "price";
	public static final String COLUMN_PRICE_DOL = "pricedol";
	public static final String COLUMN_PRICE_POINT = "pricepoint";
	public static final String COLUMN_PRICE_POINT_DOL = "pricepointdol";
	
	public static final String COLUMN_CODE_PRIMARY = "primarycode";
	public static final String COLUMN_CODE_SECONDARY = "secondary";
	public static final String COLUMN_NAME_FULL = "fullname";
	

	private static final String DATABASE_NAME = "markets.db";
	private static final int DATABASE_VERSION = 1;//parzysta usuwa Markets przy upgradzie
	private static final String DATABASE_CREATE_MARKETS = "create table "
			+ TABLE_MARKETS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_MARKET_ID + " INTEGER, "
			+ COLUMN_CODE_PRIMARY + " text, "
			+ COLUMN_CODE_SECONDARY + " text, "
			+ COLUMN_NAME_FULL + " text, "
			+ COLUMN_PRICE_POINT + " REAL, "
			+ COLUMN_PRICE_POINT_DOL + " REAL, "
			+ COLUMN_SUBSCRIBED + " INTEGER);";
	
	private static final String DATABASE_CREATE_PRICES = "create table "
			+ TABLE_PRICES + "(" + COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_DATE + " text not null, "
			+ COLUMN_MARKET_ID + " INTEGER, "
			+ COLUMN_PRICE + " REAL, "
			+ COLUMN_PRICE_DOL + " REAL);";
	/*
	 * { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_MARKET_ID, MySQLiteHelper.COLUMN_DATE ,
			MySQLiteHelper.COLUMN_PRICE, MySQLiteHelper.COLUMN_SUBSCRIBED , MySQLiteHelper.COLUMN_PRICE_POINT, MySQLiteHelper.COLUMN_PRICE_DOL
			, MySQLiteHelper.COLUMN_CODE_PRIMARY, MySQLiteHelper.COLUMN_CODE_SECONDARY, MySQLiteHelper.COLUMN_NAME_FULL};
	 */
	/*private static final String DATABASE_CREATE = "create table "
			+ TABLE_MARKETS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_DATE + " text not null, "
			+ COLUMN_PRICE + " REAL, "
			+ COLUMN_MARKET_ID + " text not null, "
			+ COLUMN_SUBSCRIBED + " text not null);";*/

	public MySQLiteHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_MARKETS);
		db.execSQL(DATABASE_CREATE_PRICES);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		if ( (newVersion & 1) == 0 ) {
			//jesli nowa wersja jest parzysta - drop table markets
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKETS);
		}
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRICES);
		onCreate(db);
		
	}

}
