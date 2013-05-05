package com.example.flightmode;

import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity implements
		CompoundButton.OnCheckedChangeListener {

	CheckBox cb;
	Context mContext = this;
	String FlightModeOn, FlightModeOff;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FlightModeOn = getResources().getString(R.string.FlightModeOn);
		FlightModeOff = getResources().getString(R.string.FlightModeOff);
		cb = (CheckBox) findViewById(R.id.checkBox1);

		cb.setOnCheckedChangeListener((OnCheckedChangeListener) this);
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			cb.setText(FlightModeOn);
			setAirplaneModeOn(true);
		} else {
			cb.setText(FlightModeOff);
			setAirplaneModeOn(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void setAirplaneModeOn(boolean enabling) {
		// Change the system setting
		Settings.System.putInt(mContext.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, enabling ? 1 : 0);

		// Post the intent
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", enabling);
		mContext.sendBroadcast(intent);
	}
}
