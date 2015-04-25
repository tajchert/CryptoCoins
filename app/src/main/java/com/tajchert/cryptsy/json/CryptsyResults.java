package com.tajchert.cryptsy.json;
import com.google.gson.annotations.SerializedName;
import com.tajchert.cryptsy.database.Market;

import java.util.List;

public class CryptsyResults {
	
	public List<Market> markets;
	
	@SerializedName("success")
	public int success;
	
}
