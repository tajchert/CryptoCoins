package com.tajchert.cryptsy.mtgox;
import com.google.gson.annotations.SerializedName;

public class MtGoxOrderType {
	
	@SerializedName("type")
	public String type;
	@SerializedName("value")
	public double value;
	
	@SerializedName("display")
	public String display;
	
	@SerializedName("currency")
	public String currency;
	
	
}
/*{
"result": "success",
"data": {
    "last_local": {
        "value": "793.00000",
        "value_int": "79300000",
        "display": "$793.00",
        "display_short": "$793.00",
        "currency": "USD"
    },
    "last": {
        "value": "793.00000",
        "value_int": "79300000",
        "display": "$793.00",
        "display_short": "$793.00",
        "currency": "USD"
    },
    "last_orig": {
        "value": "576.00000",
        "value_int": "57600000",
        "display": "576.00\u00a0\u20ac",
        "display_short": "576.00\u00a0\u20ac",
        "currency": "EUR"
    },
    "last_all": {
        "value": "788.19840",
        "value_int": "78819840",
        "display": "$788.20",
        "display_short": "$788.20",
        "currency": "USD"
    },
    "buy": {
        "value": "793.01000",
        "value_int": "79301000",
        "display": "$793.01",
        "display_short": "$793.01",
        "currency": "USD"
    },
    "sell": {
        "value": "799.28999",
        "value_int": "79928999",
        "display": "$799.29",
        "display_short": "$799.29",
        "currency": "USD"
    },
    "now": "1388164438498428"
}
}*/