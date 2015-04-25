package com.tajchert.cryptsy.mtgox;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class GetMtGox {
	HttpGet httppost;
	private static String url = "http://data.mtgox.com/api/2/BTCUSD/money/ticker_fast";
	Gson gson = new Gson();
	
	 com.tajchert.cryptsy.mtgox.MtGoxResults results;
	
	public List <com.tajchert.cryptsy.mtgox.MtGoxOrderType> makeAndGet(){
		//InputStream source = retrieveStream(MtGoxResults.url);
		httppost =  new HttpGet(url);
		httppost.setHeader("Content-type", "application/json");
    	
    	
    	InputStream inputStream = null;
    	try {
			URL urll = new URL(url);
			URLConnection urlConnection = urll.openConnection();
			inputStream = urlConnection.getInputStream();
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
    	
    	BufferedReader reader = null;
    	reader = new BufferedReader(new InputStreamReader(inputStream), 8);
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
		output = correctChar(output);
		results = gson.fromJson(output, com.tajchert.cryptsy.mtgox.MtGoxResults.class);
		List <com.tajchert.cryptsy.mtgox.MtGoxOrderType> lista = results.data;
		return lista;
	}
	private  String correctChar(String in){
		
		in=in.replace("\"data\":{\"last_local\":{\"", "\"data\":[{\"type\":\"last_local\",\"");
		in=in.replace(",\"last\":{", ",{\"type\":\"last\",");
		in=in.replace(",\"last_orig\":{", ",{\"type\":\"last_orig\",");
		in=in.replace(",\"last_all\":{", ",{\"type\":\"last_all\",");
		in=in.replace(",\"buy\":{", ",{\"type\":\"buy\",");
		in=in.replace(",\"sell\":{", ",{\"type\":\"sell\",");
		in=in.replace("},\"now\"", "}],\"now\"");
		in=in.replace("\"}}", "\"}");
		//Log.d("Cryptsy", in);
		return in;
		
	}
	private  InputStream retrieveStream(String url) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("CryptoCoins", "Error " + statusCode + " for URL " + url);
				return null;
			}
			HttpEntity getResponseEntity = getResponse.getEntity();
			return getResponseEntity.getContent();
		} catch (IOException e) {
			getRequest.abort();
			Log.w("CryptoCoins", "Error for URL " + url, e);
		}
		return null;

	}
	
}
/*Correct output for MtGox:
{"result":"success","data":[{"type":"last_local","value":"783.99999","value_int":"78399999","display":"$784.00","display_short":"$784.00","currency":"USD"},{"type":"last","value":"783.99999","value_int":"78399999","display":"$784.00","display_short":"$784.00","currency":"USD"},{"type":"last_orig","value":"783.99999","value_int":"78399999","display":"$784.00","display_short":"$784.00","currency":"USD"},{"type":"last_all","value":"783.99999","value_int":"78399999","display":"$784.00","display_short":"$784.00","currency":"USD"},{"type":"buy","value":"777.00500","value_int":"77700500","display":"$777.01","display_short":"$777.01","currency":"USD"},{"type":"sell","value":"783.99499","value_int":"78399499","display":"$783.99","display_short":"$783.99","currency":"USD"}],"now":"1388170886254588"}
*/