package com.tajchert.cryptsy.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.tajchert.cryptsy.database.Market;

public class GetCryptsyMarkets {
	
	public String url = "http://pubapi.cryptsy.com/api.php?method=marketdatav2";
	HttpGet httppost;
	Gson gson = new Gson();
	Context cont;
	
	CryptsyResults results;
	
	public List <Market> makeAndGet(Context cont){
		this.cont = cont;
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
		writeToFile(output);
		results = gson.fromJson(output, CryptsyResults.class);
		List <Market> lista = results.markets;
		return lista;
	}
	
	public Market getSingle(Context cont){
		this.cont = cont;
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
		output = correctChar(output);
		//writeToFile(output);
		results = gson.fromJson(output, CryptsyResults.class);
		//results.markets.get(0).date = Calendar.getInstance();
		//Log.v("Cryptsy", "End2" + results.markets.get(0).lasttradeprice);
		//Log.d("Cryptsy", "market2: " + results.markets.get(0).primarycode +" - "  + results.markets.get(0).secondarycode);
		//List <CryptsyMarket> lista = results.markets;
		return results.markets.get(0);
	}
	
	private void writeToFile(String data) {
	    try {
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(cont.openFileOutput("result.txt", Context.MODE_PRIVATE));
	        outputStreamWriter.write(data);
	        outputStreamWriter.close();
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}
	private  String correctChar(String in){
		in = in.replace("\"return\":{", "");
		in = in.replace("\"markets\":{\"", "\"markets\":[{\"topic\":\"");
		
		in = in.replace("}]},\"", "}]},{\"title\":\"");
		//or if null (!)
		in = in.replace("null},\"", "null},{\"title\":\"");
		in = in.replace("\":{\"marketid\"", "\",\"marketid\"");
		in = in.replace("]}}}}", "]}]}");
		//in = in.replace(",\"recenttrades\":[{\"*}]},", "},");
		//in = in.replace(",\"recenttrades\":[{\"*}]}]}", "}]}");
		//Log.d("Cryptsy", in);
		//writeToFile(in);
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
