package com.bestbaan.moonbox.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestbaan.moonbox.util.CustomAppInfo;
import com.timetv.launch.R;

public class AppAdminAdapter extends BaseAdapter<CustomAppInfo> {

	private int mCurrentClickPosition=-1;
	private boolean mIsToDesked=false;
	
	public AppAdminAdapter(Context context, List<CustomAppInfo> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if(null == convertView){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.app_manager_item, null);
			initHolder(holder,convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		initHolderData(holder,position);
		return convertView;
	}
	
	private void initHolderData(Holder holder, int position) {
		CustomAppInfo appInfo = mList.get(position);
		int toDeskBtnText = appInfo.isDesktop ? R.string.cancel_to_desktp : R.string.to_desktp;
		
		holder.appIcon.setImageDrawable(appInfo.icon);
		holder.appName.setText(appInfo.title);
		holder.version.setText(appInfo.versionName);
		if(mCurrentClickPosition!=-1){
			if(mCurrentClickPosition==position){
				if(mIsToDesked==true){
					holder.toDeskTop.setText(R.string.cancel_to_desktp);
				}else{
					holder.toDeskTop.setText(R.string.to_desktp);
				}
			}
		}else{
			holder.toDeskTop.setText(toDeskBtnText);
		}
		
		holder.toDeskTop.setTag(appInfo);
	}

	private void initHolder(Holder holder, View convertView) {
		holder.appIcon = (ImageView) convertView.findViewById(R.id.app_item_imageview);
		holder.appName = (TextView) convertView.findViewById(R.id.app_item_name);
		holder.toDeskTop = (Button) convertView.findViewById(R.id.app_mamager_item_btn_toindex);
		holder.version = (TextView) convertView.findViewById(R.id.app_item_version);
	}

	public void notifyDataSetChanged(int position,boolean isInDesk) {
		mCurrentClickPosition=position;
		mIsToDesked=isInDesk;
		super.notifyDataSetChanged();
	}



	static class Holder {
		ImageView appIcon;
		TextView appName;
		Button toDeskTop;
		TextView version;
	}
}
