package com.tajchert.cryptsy.ui;


import com.tajchert.cryptsy.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class About extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		Button buttonDonation = (Button) findViewById(R.id.buttonDonation);
		buttonDonation.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://tajchert.pl/donation/"));
				startActivity(browserIntent);
			}
		});
		
		Button buttonAuthor = (Button) findViewById(R.id.buttonAuthor);
		buttonAuthor.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://tajchert.pl/"));
				startActivity(browserIntent);
			}
		});
		
		Button buttonContact = (Button) findViewById(R.id.buttonContact);
		buttonContact.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final Intent emailIntent = new Intent(Intent.ACTION_SEND);

				emailIntent.setType("text/plain");
				emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"thetajchert@gmail.com"});
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Cryptsy App");
				emailIntent.putExtra(Intent.EXTRA_TEXT, "");

				About.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			}
		});
	}

}
