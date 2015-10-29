package com.shizhefei.test;

import com.shizhefei.utils.MyVolley;

import android.app.Application;

public class BaseApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		MyVolley.init(getApplicationContext());
	}
}
