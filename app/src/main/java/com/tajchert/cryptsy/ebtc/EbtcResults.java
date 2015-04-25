package com.tajchert.cryptsy.ebtc;

import com.google.gson.annotations.SerializedName;

public class EbtcResults {
	@SerializedName("ticker")
	public com.tajchert.cryptsy.ebtc.EbtcTicker ticker;
	

}

//{"ticker":{"high":23.5,"low":21.05,"avg":22.275,"vol":16590774.05272,"vol_cur":744078.88647,"last":22.13501,"buy":22.21009,"sell":22.136011,"updated":1388225718,"server_time":1388225719}}