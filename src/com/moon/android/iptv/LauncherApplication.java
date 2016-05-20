package com.moon.android.iptv;

import com.bestbaan.moonbox.model.UpdateData;

import android.app.Application;

public class LauncherApplication extends Application{
	public static final String TAG = "LauncherApplication";
	private static LauncherApplication application;
	public static UpdateData updateData;

	/**
	 * current position of AppInfo witch remove from desk 
	 */
	public static int position=0;
	
	public static LauncherApplication getApplication() {
		return application;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
	}
	
	
	
}
