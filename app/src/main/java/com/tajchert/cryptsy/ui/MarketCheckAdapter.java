package com.tajchert.cryptsy.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tajchert.cryptsy.R;
import com.tajchert.cryptsy.database.Market;
import com.tajchert.cryptsy.database.MarketDataSource;

import java.util.ArrayList;

/**
 * Created by Michal Tajchert on 2015-05-02.
 */
public class MarketCheckAdapter extends ArrayAdapter<Market> {

    public ArrayList<Market> marketList;
    public ArrayList<Market> favMarkets;
    private Context context;
    private MarketDataSource datasource;

    public MarketCheckAdapter(Context context, int textViewResourceId, ArrayList<Market> markets, MarketDataSource datasource, ArrayList<Market> subscribed) {
        super(context, textViewResourceId, markets);
        this.marketList = new ArrayList<Market>();
        this.marketList.addAll(markets);
        this.context = context;
        this.datasource = datasource;
        this.favMarkets = subscribed;
        for(Market market : marketList) {
            for(Market sub : favMarkets) {
                if(market.marketid == sub.marketid) {
                    market.isSubscribed = true;
                    Log.d("Crypts", "SettingsListMarketAdapter is Sub!" + market.marketid);
                } else {
                    market.isSubscribed = false;
                }
            }
        }
    }

    private class ViewHolder {
        TextView marketName;
        CheckBox box;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_market_item, null);

            holder = new ViewHolder();
            holder.marketName = (TextView) convertView.findViewById(R.id.NameText);
            holder.box = (CheckBox) convertView.findViewById(R.id.checkBoxSub);
            convertView.setTag(holder);

            holder.box.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    Market country = (Market) cb.getTag();
                    datasource.updateListRow(country.marketid, cb.isChecked());
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Market country = marketList.get(position);
        holder.marketName.setText(country.primaryname + "-" + country.secondarycode);
        for(Market market: favMarkets) {
            if(market.marketid == country.marketid) {
                country.isSubscribed =true;
            }
        }
        holder.box.setChecked(country.isSubscribed);
        //Log.d("cryptsy", "getView " + holder.marketName.getText().toString() +":" + country.isSubscribed);
        holder.box.setTag(country);

        return convertView;

    }
}