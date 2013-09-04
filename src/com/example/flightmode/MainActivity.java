package com.example.flightmode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
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

	CheckBox cb1, cb2;
	Context mContext = this;
	String FlightModeOn, FlightModeOff, ToggleWlanGPRSOn, ToggleWlanGPRSOff;
	WifiManager wifiManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FlightModeOn = getResources().getString(R.string.FlightModeOn);
		FlightModeOff = getResources().getString(R.string.FlightModeOff);
		ToggleWlanGPRSOn = getResources().getString(R.string.ToggleWlanGPRSOn);
		ToggleWlanGPRSOff = getResources().getString(R.string.ToggleWlanGPRSOff);

		cb1 = (CheckBox) findViewById(R.id.checkBox1);
		cb2 = (CheckBox) findViewById(R.id.checkBox2);

		cb1.setOnCheckedChangeListener((OnCheckedChangeListener) this);
		cb2.setOnCheckedChangeListener((OnCheckedChangeListener) this);
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.findViewById(R.id.checkBox1) == cb1) {
			if (isChecked) {
				cb1.setText(FlightModeOn);
				setAirplaneModeOn(true);
			} else {
				cb1.setText(FlightModeOff);
				setAirplaneModeOn(false);
			}
		} else if (buttonView.findViewById(R.id.checkBox2) == cb2) {
			if (isChecked) {
				cb2.setText(ToggleWlanGPRSOn);
				setWlanGPRSModeOn(true);
			} else {
				cb2.setText(ToggleWlanGPRSOff);
				setWlanGPRSModeOn(false);
			}
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
	
	private void setWlanGPRSModeOn(boolean enabled) {
		// Change the system setting
//		Settings.System.putInt(mContext.getContentResolver(),
//				Settings.System.AIRPLANE_MODE_ON, enabling ? 1 : 0);
//
//		// Post the intent
//		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
//		intent.putExtra("state", enabling);
//		mContext.sendBroadcast(intent);
		
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(enabled);
		
		ConnectivityManager conMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

		  Class<?> conMgrClass = null; // ConnectivityManager类
		  Field iConMgrField = null; // ConnectivityManager类中的字段
		  Object iConMgr = null; // IConnectivityManager类的引用
		  Class<?> iConMgrClass = null; // IConnectivityManager类
		  Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法

		  try {
		   // 取得ConnectivityManager类
		   conMgrClass = Class.forName(conMgr.getClass().getName());
		   // 取得ConnectivityManager类中的对象mService
		   iConMgrField = conMgrClass.getDeclaredField("mService");
		   // 设置mService可访问
		   iConMgrField.setAccessible(true);
		   // 取得mService的实例化类IConnectivityManager
		   iConMgr = iConMgrField.get(conMgr);
		   // 取得IConnectivityManager类
		   iConMgrClass = Class.forName(iConMgr.getClass().getName());
		   // 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
		   setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		   // 设置setMobileDataEnabled方法可访问
		   setMobileDataEnabledMethod.setAccessible(true);
		   // 调用setMobileDataEnabled方法
		   setMobileDataEnabledMethod.invoke(iConMgr, !enabled);
		  } catch (ClassNotFoundException e) {
		   e.printStackTrace();
		  } catch (NoSuchFieldException e) {
		   e.printStackTrace();
		  } catch (SecurityException e) {
		   e.printStackTrace();
		  } catch (NoSuchMethodException e) {
		   e.printStackTrace();
		  } catch (IllegalArgumentException e) {
		   e.printStackTrace();
		  } catch (IllegalAccessException e) {
		   e.printStackTrace();
		  } catch (InvocationTargetException e) {
		   e.printStackTrace();
		  }
	}
}
