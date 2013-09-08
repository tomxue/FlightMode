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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity {

	RadioButton rb1, rb2, rb3;
	WifiManager wifiManager;
	TextView textview1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textview1 = (TextView) this.findViewById(R.id.textView1);
		// 根据ID找到RadioGroup实例
		RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup1);
		// set the default selected RadioButton
		group.check(R.id.radio3);
		// 绑定一个匿名监听器
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				// 获取变更后的选中项的ID
//				int radioButtonId = radioGroup.getCheckedRadioButtonId();
				// 根据ID获取RadioButton的实例
//				RadioButton rb = (RadioButton) MainActivity.this
//						.findViewById(radioButtonId);

				switch (checkedId) {
				case R.id.radio0: // Flight mode on
					setAirplaneModeOn(true);
					break;
				case R.id.radio1: // WLAN on, while flight mode can be on
					setAirplaneModeOn(false);
					// add sleep to make the state change stable
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					setWlanGPRSModeOn(true);
					break;
				case R.id.radio2: // GPRS on, while flight mode cannot be on
					setAirplaneModeOn(false);
					// add sleep to make the state change stable
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					setWlanGPRSModeOn(false);
					break;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void setAirplaneModeOn(boolean enabling) {
		// Change the system setting
		Settings.System.putInt(this.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, enabling ? 1 : 0);

		// Post the intent
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
//		intent.putExtra("state2", enabling);
		this.sendBroadcast(intent);
	}

	private void setWlanGPRSModeOn(boolean enabled) {
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(enabled);

		ConnectivityManager conMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		Class<?> conMgrClass = null; // ConnectivityManager类
		Field iConMgrField = null; // ConnectivityManager类中的字段
		Object iConMgrFieldValue = null; // IConnectivityManager类的引用
		Class<?> iConMgrClass = null; // IConnectivityManager类
		Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法

		try {
			// 取得ConnectivityManager类
			conMgrClass = Class.forName(conMgr.getClass().getName());
			// textview1.setText(conMgr.getClass().getName()); is android.net.ConnectivityManager
			// textview1.setText(conMgrClass.getClass().getName()); is java.lang.Class
//			textview1.setText(conMgrClass.getName()); // is android.net.ConnectivityManager
//			textview1.setText(conMgrClass.toString()); // is class android.net.ConnectivityManager
			// 取得ConnectivityManager类中的对象mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
//			textview1.setText(iConMgrField.getName().toString()); is mService
			// 设置mService可访问
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			// get(): Returns the value of the field in the specified object.
			iConMgrFieldValue = iConMgrField.get(conMgr);
//			textview1.setText(iConMgrFieldValue.toString()); is android.net.IConnectivityManager$Stud$Proxy@41ad1498
			// 取得IConnectivityManager类
			iConMgrClass = Class.forName(iConMgrFieldValue.getClass().getName());
//			textview1.setText(iConMgrFieldValue.getClass().getName()); is android.net.IConnectivityManager$Stud$Proxy
//			textview1.setText(iConMgrClass.getName()); // is android.net.IConnectivityManager$Stud$Proxy
//			textview1.setText(iConMgrClass.toString()); // is class android.net.IConnectivityManager$Stud$Proxy
			// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
					"setMobileDataEnabled", Boolean.TYPE);
//			textview1.setText(setMobileDataEnabledMethod.getName()); is setMobileDataEnabled
			// 设置setMobileDataEnabled方法可访问
			setMobileDataEnabledMethod.setAccessible(true);
			// 调用setMobileDataEnabled方法
			// receiver: the object on which to call this method (or null for static methods)
			setMobileDataEnabledMethod.invoke(iConMgrFieldValue, !enabled);
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
