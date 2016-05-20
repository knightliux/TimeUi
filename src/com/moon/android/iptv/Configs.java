

package com.moon.android.iptv;

import java.util.ArrayList;
import java.util.List;

import com.bestbaan.moonbox.util.MACUtils;

public class Configs {
	
	public static boolean DEBUG_MODE = false;
	
	public static final int BOX_TYPE_M2S = 0;
	public static final int BOX_TYPE_M3 = 1;//M3
	public static final int BOX_TYPE_M4 = 2;//S805
	public static final int getType(){
		return BOX_TYPE_M4;
	}
	
	/**
	 * 是否验证区域限制，其余的盒子不需要
	 */
	public static final boolean isVerifyRegionRestrictions = true;
	
	
	public static String THIS_APP_PACKAGE_NAME = "com.timetv.launch";
	public static final String APK_NAME = "MBUI3.0.apk";
	public static final String PARAM_1 = "intent_extra_1_param";
	public static final String PARAM_2 = "intent_extra_2_param";
	
	//PLAYER VERSION  file in /ASSETS/MoonPlayer_xx.apk
	public static final String PLAYER_VERSION = "1.0";
	public static final String PLAYER_PKG = "com.moon.android.moonplayer";
	
	public static class UserMsgVar{
		public static final String MSG_NOT_READ = "1";
		public static final String MSG_HAS_READ = "0";
		
	}
	
	public static class BroadCastConstant{
		public static final String GET_AD_PICTURE= "com.bestbaan.launcher.moonboxlauncher2.getAdPicture";
		public static final String GET_USER_MSG= "com.bestbaan.launcher.moonboxlauncher2.getUserMsg";
		public static final String GET_LAUNCHER_MSG= "com.bestbaan.launcher.moonboxlauncher2.getLauncherMsg";
		public static final String NETWORK_CONNECTION_CHANGE = "com.bestbaan.launcher.moonboxlauncher2.networkConnectChange";
		public static final String ACTION_NEW_USER_MSG = "com.bestbaan.launcher.moonboxlauncher2.view.StatusBar.hasnewmsg";
		public static final String ACTION_NEW_USER_NO_MSG = "com.bestbaan.launcher.moonboxlauncher2.view.StatusBar.hasnonewmsg";
		public static final String ACTION_TO_DESKTOP = "com.bestbann.launcher.moonboxlauncher2.todesktop";
		public static final String ACTION_UN_TO_DESKTOP = "com.bestbann.launcher.moonboxlauncher2.untodesktop";
		public static final String ACTION_UPGRADE = "com.bestbaan.launcher.moonboxlauncher2.upgrade";
		public static final String ACTION_UPDATE_DESKTOP = "com.bestbaan.launcher.moonboxlauncher2.updatedesktop";
		public static final String ACTION_GET_HARDWARE = "com.bestbaan.launcher.moonboxlauncher2.gethardware";
	}
	//
	public static class RegionLimit{
		public static final String ACTION_REGION_LIMIT = "com.bestbaan.launcher.moonboxlauncher2.limitRegionBroad";
		public static final String STATUS_AUTH_SUCCESS = "0";
		public static final String STATUS_AUTH_FAIL = "1";
		public static final String STATUS_AUTH_REGION_LIMIT = "2";
	}
	//
	public class ServerInterface{
		public  final String SERVER_ADDRESS = getServer();
		public  final String SOFTWARE_MSG = SERVER_ADDRESS + "stbinfo.php?";
		public  final String AD_PICTURE = SERVER_ADDRESS + "stbadpic.php?";
		public  final String MSG_CENTER = SERVER_ADDRESS + "msgcenter.php?";
		public  final String MSG_STATUS_CHANGE = SERVER_ADDRESS + "upumsg.php?";
		public  final String MSG_LAUNCHER = SERVER_ADDRESS +"stbmsg.php?";
		public  final String UPGRADE_LAUNCHER = SERVER_ADDRESS +"firmwareup.php?";
		public  final String REGION_LIMIT = SERVER_ADDRESS + "stbauth.php?";
	}
	
	/**
	 * 获取系统所有应用还是获取安装的应用
	 */
//	public static final boolean isShowSystemApp = false;
	public static boolean showSystemApp(){
		if(getType() == BOX_TYPE_M4 || getType() == BOX_TYPE_M3)  
			return true;
		return false;
	}
	
	public static int TOAST_TEXT_SIZE = 24;
	/**
	 * 预留的可以隐藏的几个应用的包名
	 * @return
	 */
	public static List<String> getHiddenAppPkg(){
		List<String> list = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		if(getType()==BOX_TYPE_M4){
			list.add("com.moon.android.moonplayer");
			list.add("com.moon.android.jodoone");
			list.add("com.moon.android.jodotwo");
			list.add("com.moon.android.jodothree");
		//	list.add("com.google.android.gms");//goole plays service
			list.add("com.google.android.gm");
			list.add("com.google.android.plus");
			list.add("com.google.android.apps.maps");
			//list.add("com.google.android.apps.geniewidget");
			list.add("com.google.android.apps.magazines");//报亭
			list.add("com.google.android.videos");//电影
			list.add("com.google.android.apps.books");//图书
			list.add("com.google.android.music");//音乐
			list.add("com.google.android.play.games");//游戏
			list.add("com.google.android.apps.genie.geniewidget");
			list.add("com.google.android.talk");
			list.add("com.google.android.apps.plus");//Google+
			list.add("com.android.settings");//sys setting
			list.add("com.fb.FileBrower");//sys file browser
//			list.add("com.meson.videoplayer");//sys video player
//			list.add("com.android.gallery3d");//sys picture player
			list.add("org.geometerplus.zlibrary.ui.android");//FBReader
//			list.add("com.android.music");//sys music
//			list.add("com.android.browser");//sys browser
//			list.add("com.android.providers.downloads.ui");//sys download
			list.add("com.android.vending");//google play
			list.add("com.moon.appstore");
			//list.add("com.amlogic.mediacenter");//media center
			//list.add("com.amlogic.miracast");//green logo wifi
			list.add("com.android.quicksearchbox");//sys quick search
			list.add("com.android.deskclock");//sys desk clock
			list.add("com.android.email");//sys email
			list.add("com.android.contacts");//sys contacts
			//list.add("com.gsoft.appinstall");//sys app install
			list.add("com.android.calculator2");//sys calculator2
			list.add("com.amlogic.PPPoE");//sys PPPoE
			list.add("com.mbx.settingsmbox");//sys screen(and *) setting
		}else if(getType()==BOX_TYPE_M3){
			list.add("com.amlogic.PPPoE");
			list.add("com.adobe.flashplayer");
			list.add("com.google.android.gm");
			list.add("com.amlogic.miracast");
			list.add("com.android.browser");//sys browser
			list.add("com.meraki.sm");
			list.add("com.android.vending");//google play
			list.add("com.teamviewer.quicksupport.market");
			list.add("com.android.service.remotecontrol");
			list.add("com.waynaktv.android");
			list.add("com.zaaptv.android");
			list.add("com.android.providers.downloads.ui");//sys download
//			list.add("com.google.android.gms");
			list.add("com.android.email");
			list.add("com.android.deskclock");
			list.add("com.android.contacts");
			list.add("com.android.calculator2");
			list.add("com.gsoft.appinstall");//sys app install
			list.add("com.moon.appstore");
			list.add("com.android.settings");//sys setting
			list.add("com.android.music");//sys music
			list.add("com.android.gallery3d");//sys picture player
			list.add("com.fb.FileBrower");//sys file browser
			list.add("com.amlogic.PicturePlayer");
			list.add("com.amlogic.netfilebrowser");
			list.add("com.moon.android.moonplayer");
			list.add("com.farcore.videoplayer");
			list.add("com.example.Upgrade");
		}
		return list;
	}
	/*
	 * 添加桌面隐藏APK包名*/
	public static List<String> getHiddendestopPkg(){
		List<String> list = new ArrayList<String>();
		list.add("com.mooncloud.android.hktvmoon");
		list.add("com.mooncloud.android.hktvlive");
		return list;
	}
	public static String getLoginUrl(){
		return "http://launchlog.tvboxpad.com:8863/Box/Login/?mac="+MACUtils.getMac()+"&type=1";
	}
	private String getServer(){
		return "http://timelaunch.tvboxpad.com:8864/";
	}
	
	/*static{
		System.loadLibrary("MBUI");
	}*/
}
