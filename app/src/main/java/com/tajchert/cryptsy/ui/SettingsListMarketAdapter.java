package com.tajchert.cryptsy.ui;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tajchert.cryptsy.R;
import com.tajchert.cryptsy.database.Market;
import com.tajchert.cryptsy.database.MarketDataSource;


public class SettingsListMarketAdapter extends BaseAdapter {
	public ArrayList<Market> data;
	MarketDataSource datasource;
	public ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
	Context c;
	//SimpleDateFormat  format = new SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault());

	SettingsListMarketAdapter(ArrayList<Market> data, Context c, MarketDataSource datasource) {
		this.data = data;
		this.c = c;
		this.datasource = datasource;
		/*for(CryptsyMarket mark: data){
			itemChecked.add(mark.isSubscribed);
		}*/
	}
	

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_market_item, null);
		}
		TextView NameText = (TextView) v.findViewById(R.id.NameText);
		CheckBox check = (CheckBox) v.findViewById(R.id.checkBoxSub);
		Market obj = data.get(position);
		//NameText.setText(obj.primarycode +"-"+obj.secondarycode);
		NameText.setText(obj.primaryname+"-"+obj.secondarycode);
		/*if(obj.isSubscribed){
			Log.d("Cryptsy", "isSubscribed");
			check.setChecked(true);
		}*/
		check.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	            CheckBox cb = (CheckBox) v.findViewById(R.id.checkBoxSub);
	            if (cb.isChecked()) {
	                //itemChecked.set(position, true);
	                data.get(position).isSubscribed = true;
	                datasource.updateListRow(data.get(position).marketid, true);
	            } else if (!cb.isChecked()) {
	                //itemChecked.set(position, false);
	                data.get(position).isSubscribed = false;
	                datasource.updateListRow(data.get(position).marketid, false);
	            }
	        }
	    });
		check.setChecked(data.get(position).isSubscribed);
		return v;
	}
}