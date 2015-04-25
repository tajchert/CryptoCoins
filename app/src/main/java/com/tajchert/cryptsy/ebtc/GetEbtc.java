package com.tajchert.cryptsy.ebtc;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.methods.HttpGet;

import com.google.gson.Gson;

public class GetEbtc {
	HttpGet httppost;
	private static String [] urls = { "https://btc-e.com/api/2/btc_usd/ticker", "https://btc-e.com/api/2/ltc_usd/ticker"};
	Gson gson = new Gson();
	
	 EbtcResults results;
	
	public EbtcTicker makeAndGet(int currency){
		//InputStream source = retrieveStream(MtGoxResults.url);
		httppost =  new HttpGet(urls[currency]);
		httppost.setHeader("Content-type", "application/json");
    	
    	
    	InputStream inputStream = null;
    	try {
			URL urll = new URL(urls[currency]);
			URLConnection urlConnection = urll.openConnection();
			inputStream = urlConnection.getInputStream();
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
    	
    	BufferedReader reader = null;
    	if(inputStream == null){
    		//set values to 0 + no connection TODO
    		return new EbtcTicker();
    	}
    	try {
			reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	String line = null;
		StringBuilder sb = new StringBuilder();
		try {
			while ((line = reader.readLine()) != null)
			{
			    sb.append(line + "\n");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch(RuntimeException e3){
			e3.printStackTrace();
		}
		String output = sb.toString();
		results = gson.fromJson(output, EbtcResults.class);
		return results.ticker;
	}
	
}
/*Correct output for MtGox:
{"result":"success","data":[{"type":"last_local","value":"783.99999","value_int":"78399999","display":"$784.00","display_short":"$784.00","currency":"USD"},{"type":"last","value":"783.99999","value_int":"78399999","display":"$784.00","display_short":"$784.00","currency":"USD"},{"type":"last_orig","value":"783.99999","value_int":"78399999","display":"$784.00","display_short":"$784.00","currency":"USD"},{"type":"last_all","value":"783.99999","value_int":"78399999","display":"$784.00","display_short":"$784.00","currency":"USD"},{"type":"buy","value":"777.00500","value_int":"77700500","display":"$777.01","display_short":"$777.01","currency":"USD"},{"type":"sell","value":"783.99499","value_int":"78399499","display":"$783.99","display_short":"$783.99","currency":"USD"}],"now":"1388170886254588"}
*/