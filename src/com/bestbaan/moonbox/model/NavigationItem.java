package com.bestbaan.moonbox.model;

import android.view.View;

public class NavigationItem {

	private int id;
	private int nameRes;
	public View view;
    public int imgRes;
    public int imgFocusRes;
	public int getImgFocusRes() {
		return imgFocusRes;
	}

	public void setImgFocusRes(int imgFocusRes) {
		this.imgFocusRes = imgFocusRes;
	}

	public int getImgRes() {
		return imgRes;
	}

	public void setImgRes(int imgRes) {
		this.imgRes = imgRes;
	}

	public NavigationItem(int id, int nameRes, View view,int imgRes,int imgFocusRes) {
		this.id = id;
		this.nameRes = nameRes;
		this.view = view;
		this.imgRes=imgRes;
		this.imgFocusRes=imgFocusRes;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNameRes() {
		return nameRes;
	}

	public void setNameRes(int nameRes) {
		this.nameRes = nameRes;
	}

	public View getFragment() {
		return view;
	}

	public void setFragment(View fragment) {
		this.view = fragment;
	}
}
