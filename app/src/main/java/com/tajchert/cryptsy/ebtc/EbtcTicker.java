package com.tajchert.cryptsy.ebtc;

import com.google.gson.annotations.SerializedName;

public class EbtcTicker {

	@SerializedName("high")
	public double high;
	
	@SerializedName("low")
	public double low;
	
	
	@SerializedName("avg")
	public double avg;
	
	@SerializedName("last")
	public double last;
	
	
}
