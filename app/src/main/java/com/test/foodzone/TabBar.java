package com.test.foodzone;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class TabBar extends TabActivity implements OnTabChangeListener, OnClickListener {

	/** Called when the activity is first created. */
	TabHost tabHost;

	String restName;
	String requestCat;
	private ImageView goBack;

	TextView textView1;

	String reqLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_bar);

		goBack = (ImageView) findViewById(R.id.goBack);
		goBack.setOnClickListener(this);

		restName = getIntent().getStringExtra("restName");
		requestCat=getIntent().getStringExtra("requestCategory");
		reqLocation=getIntent().getStringExtra("requestLocation");

		textView1= (TextView) findViewById(R.id.note);

		textView1.setTypeface(null,Typeface.ITALIC);

		textView1.setTextSize(15);

		if(requestCat.equals("admin"))
		{
			textView1.setText("*Long press to edit the details of item");
		}
		else
		{
			textView1.setText("*Tap to view item details & long press for feedback");
		}

		// Get TabHost Refference
		tabHost = getTabHost();

		// Set TabChangeListener called when tab changed
		tabHost.setOnTabChangedListener(this);

		TabHost.TabSpec spec;
		Intent intent = null;

		/************* TAB1 ************/
		// Create Intents to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, TabVeg.class);

		intent.putExtra("foodcourtid", getIntent()
				.getStringExtra("foodcourtid"));
		intent.putExtra("restaurantid",
				getIntent().getStringExtra("restaurantid"));
		intent.putExtra("categoryid", getIntent().getStringExtra("categoryid"));
		intent.putExtra("requestCategory",
				getIntent().getStringExtra("requestCategory"));
		intent.putExtra("requestLocation",reqLocation);


		spec = tabHost.newTabSpec("First").setIndicator("Veg")
				.setContent(intent);

		// Add intent to tab
		tabHost.addTab(spec);

		/************* TAB2 ************/
		intent = new Intent().setClass(this, TabNonVeg.class);

		intent.putExtra("foodcourtid", getIntent()
				.getStringExtra("foodcourtid"));
		intent.putExtra("restaurantid",
				getIntent().getStringExtra("restaurantid"));
		intent.putExtra("categoryid", getIntent().getStringExtra("categoryid"));
		intent.putExtra("requestCategory",
				getIntent().getStringExtra("requestCategory"));
		intent.putExtra("requestLocation",reqLocation);

		spec = tabHost.newTabSpec("Second").setIndicator("NonVeg")
				.setContent(intent);
		tabHost.addTab(spec);

		/************* TAB3 ************/
		/*intent = new Intent().setClass(this, Tab3.class);
		intent.putExtra("restName", restName);
		spec = tabHost.newTabSpec("Third").setIndicator("Rating")
				.setContent(intent);
		tabHost.addTab(spec);*/

		// Set drawable images to tab
		tabHost.getTabWidget().getChildAt(1)
				.setBackgroundColor(Color.parseColor("#FF0000"));
		/*tabHost.getTabWidget().getChildAt(2)
				.setBackgroundColor(Color.parseColor("#FFBF00"));*/

		// Set Tab1 as Default tab and change image
		tabHost.getTabWidget().setCurrentTab(0);
		tabHost.getTabWidget().getChildAt(0)
				.setBackgroundColor(Color.parseColor("#04B404"));

	}

	@Override
	public void onTabChanged(String tabId) {

		/************ Called when tab changed *************/

		// ********* Check current selected tab and change according images
		// *******/

		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			if (i == 0)
				tabHost.getTabWidget().getChildAt(i)
						.setBackgroundColor(Color.parseColor("#04B404"));
			else if (i == 1)
				tabHost.getTabWidget().getChildAt(i)
						.setBackgroundColor(Color.parseColor("#FF0000"));
			else if (i == 2)
				tabHost.getTabWidget().getChildAt(i)
						.setBackgroundColor(Color.parseColor("#FFBF00"));
		}

		Log.i("tabs", "CurrentTab: " + tabHost.getCurrentTab());

		if (tabHost.getCurrentTab() == 0)
			tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab())
					.setBackgroundColor(Color.parseColor("#04B404"));
		else if (tabHost.getCurrentTab() == 1)
			tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab())
					.setBackgroundColor(Color.parseColor("#FF0000"));
		else if (tabHost.getCurrentTab() == 2)
			tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab())
					.setBackgroundColor(Color.parseColor("#F7D358"));

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v == goBack) {
			finish();
    	}
		
	}

}
