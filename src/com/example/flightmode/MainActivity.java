package com.example.flightmode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity {

	RadioButton rb1, rb2, rb3;
	WifiManager wifiManager;
	TextView textview1;
	CheckBox cb1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		cb1 = (CheckBox) this.findViewById(R.id.checkBox1);

		cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.findViewById(R.id.checkBox1) == cb1) {
					if (isChecked) {
						cb1.setText("GPS on");
						toggleGPS();
					} else {
						cb1.setText("GPS off");
						toggleGPS();
					}
				}
			}
		});

		// 根据ID找到RadioGroup实例
		RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup1);
		// set the default selected RadioButton
		group.check(R.id.radio3);
		// 绑定一个匿名监听器
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				// 获取变更后的选中项的ID
				// int radioButtonId = radioGroup.getCheckedRadioButtonId();
				// 根据ID获取RadioButton的实例
				// RadioButton rb = (RadioButton) MainActivity.this
				// .findViewById(radioButtonId);

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

	// TODO don't work?!
	private void toggleGPS() {
		Intent gpsIntent = new Intent();
		gpsIntent.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
		gpsIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
			// this.sendBroadcast(gpsIntent);
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}

	private void setAirplaneModeOn(boolean enabling) {
		// Change the system setting
		Settings.System.putInt(this.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, enabling ? 1 : 0);

		// Post the intent
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		// intent.putExtra("state2", enabling);
		this.sendBroadcast(intent);
	}

	private void setWlanGPRSModeOn(boolean enabled) {
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(enabled);

		ConnectivityManager conMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// 打开移动网络比较麻烦，系统没有直接提供开放的方法，只在ConnectivityManager类中有一个不可见的setMobileDataEnabled方法，
		// 查看源代码发现，它是调用IConnectivityManager类中的setMobileDataEnabled(boolean)方法。
		// 由于方法不可见，只能采用反射来调用
		// 先得到ConnectivityManager类名 -> mService字段 -> 该字段值/对象 -> IConnectivityManager类名 -> setMobileDataEnabled方法 -> 调用之
		Class<?> conMgrClass = null; // ConnectivityManager类
		Field iConMgrField = null; // ConnectivityManager类中的字段
		Object iConMgrFieldObject = null; // IConnectivityManager类的引用
		Class<?> iConMgrClass = null; // IConnectivityManager类
		Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法

		try {
			// 取得ConnectivityManager类
			conMgrClass = Class.forName(conMgr.getClass().getName());
			// textview1.setText(conMgr.getClass().getName()); is
			// android.net.ConnectivityManager
			// textview1.setText(conMgrClass.getClass().getName()); is
			// java.lang.Class
			// textview1.setText(conMgrClass.getName()); // is
			// android.net.ConnectivityManager
			// textview1.setText(conMgrClass.toString()); // is class
			// android.net.ConnectivityManager
			// 取得ConnectivityManager类中的对象mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// textview1.setText(iConMgrField.getName().toString()); is mService
			// 设置mService可访问
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			// get(): Returns the value of the field in the specified object.
			iConMgrFieldObject = iConMgrField.get(conMgr);
			// textview1.setText(iConMgrFieldValue.toString()); is
			// android.net.IConnectivityManager$Stud$Proxy@41ad1498
			// 取得IConnectivityManager类
			iConMgrClass = Class.forName(iConMgrFieldObject.getClass()
					.getName());
			// textview1.setText(iConMgrFieldValue.getClass().getName()); is
			// android.net.IConnectivityManager$Stud$Proxy
			// textview1.setText(iConMgrClass.getName()); // is
			// android.net.IConnectivityManager$Stud$Proxy
			// textview1.setText(iConMgrClass.toString()); // is class
			// android.net.IConnectivityManager$Stud$Proxy
			// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
					"setMobileDataEnabled", Boolean.TYPE);
			// textview1.setText(setMobileDataEnabledMethod.getName()); is
			// setMobileDataEnabled
			// 设置setMobileDataEnabled方法可访问
			setMobileDataEnabledMethod.setAccessible(true);
			// 调用setMobileDataEnabled方法
			// receiver: the object on which to call this method (or null for
			// static methods)
			setMobileDataEnabledMethod.invoke(iConMgrFieldObject, !enabled);
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
