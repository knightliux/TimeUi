package com.interactive.send;

import android.util.Log;

import com.bestbaan.moonbox.util.RequestUtil;
import com.moon.android.iptv.Configs;

public class LoginHome {
	public LoginHome(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
			//	Log.d("-------------","----------");
				RequestUtil.getInstance().request(Configs.getLoginUrl());
			}
		}).start();
		
	}
}
