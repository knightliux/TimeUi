package com.moon.android.iptv;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalBitmap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbaan.moonbox.ayncpicture.ImageLoader;
import com.bestbaan.moonbox.database.AppDAO;
import com.bestbaan.moonbox.model.AdMsg;
import com.bestbaan.moonbox.util.ActivityUtils;
import com.bestbaan.moonbox.util.AppUtils;
import com.bestbaan.moonbox.util.AppUtils.AppInfo;
import com.bestbaan.moonbox.util.Logger;
import com.bestbaan.moonbox.util.NetworkUtil;
import com.bestbaan.moonbox.util.RequestUtil;
import com.bestbaan.moonbox.view.AppGrid.AppsAdapter2;
import com.bestbaan.moonbox.view.CustomToast;
import com.bestbaan.moonbox.view.DeskAppMgr;
import com.bestbaan.moonbox.view.PageIndicatorView;
import com.bestbaan.moonbox.view.VideoView;
import com.forcetech.android.ForceTV;
import com.timetv.launch.R;

public class PageIndexView extends LinearLayout {

	public static final Logger log = Logger.getInstance();
	private GridView mGridApp;
	private ImageView mGalleryAd;
	private AppDAO mAppDAO;
	private List<AppInfo> mListAppInfo = new ArrayList<AppInfo>();
	private AppsAdapter2 mAppsAdapter;
	private TextView mTextPrompt;
	private PageIndicatorView mPageView;
	private LinearLayout mAdContainer;
	private Timer mTimerAdChange = new Timer(true);
	private ImageLoader imageLoader;
	private boolean isNeedUpdate = false;
	public static final int NUM_COLUMNS = 6;
	protected static final int CHANGE_AD_PICTURE = 0X00012;
	private Context mContext;

	public PageIndexView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		View view = LayoutInflater.from(context).inflate(R.layout.page_index,this);
		
		startScrollAd();
		initData();
		initWidget(view);
		regBroadCast();
		regToDeskTopReceiver();
		log.i("isNeedUpdate = " + isNeedUpdate);
		//
	}
    public void UpView(){
//    	mListAppInfo.clear();
//	    mAppsAdapter = new AppsAdapter2(mContext, mListAppInfo);
//		mGridApp.setAdapter(mAppsAdapter);
//		updateGridView(true);
    	
    }
	public PageIndexView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PageIndexView(Context context) {
		this(context, null);
	}

	private void regToDeskTopReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Configs.BroadCastConstant.ACTION_TO_DESKTOP);
		filter.addAction(Configs.BroadCastConstant.ACTION_UN_TO_DESKTOP);
		filter.addAction(Configs.BroadCastConstant.ACTION_UPDATE_DESKTOP);
		mContext.registerReceiver(mDeskTopReceiver, filter);
	}

	private BroadcastReceiver mDeskTopReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			log.i("PageIndex  get Broadcast [ ACTION_TO_DESKTOP or ACTION_UN_TO_DESKTOP "+ "or ACTION_UPDATE_DESKTOP]");
			isNeedUpdate = true;
			log.i("isNeedUpdate = " + isNeedUpdate);
			updateGridView(false);
		}
	};

	private void regBroadCast() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Configs.BroadCastConstant.GET_AD_PICTURE);
		mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context context, Intent intent) {
			log.i("接收到获取到广告图片的广播");
			Declare.listAdMsg = (List<AdMsg>) intent.getSerializableExtra(Configs.PARAM_1);
			mAdImageSelect = 0;
			fillGallery(false);
		}
	};

	private int mAdImageSelect = 0;

	private void fillGallery(boolean isBack) {
		if (Declare.listAdMsg.size() > 0) {
			int totalCount = Declare.listAdMsg.size();
			if (isBack) {
				if (mAdImageSelect - 1 < 0)
					mAdImageSelect = Declare.listAdMsg.size() - 1;
				else
					mAdImageSelect--;
			} else {
				if (mAdImageSelect + 1 > Declare.listAdMsg.size() - 1)
					mAdImageSelect = 0;
				else
					mAdImageSelect++;
			}
			String imageUrl = Declare.listAdMsg.get(mAdImageSelect).getPicurl();
			try {
				imageLoader.DisplayImage(imageUrl, mContext, mGalleryAd, false);
				mPageView.setDirect(true);
				mPageView.setTotalPage(totalCount);
				mPageView.setCurrentPage(mAdImageSelect);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param isInit 目前不知道这个参数最初的含义，因为函数体中不再使用
	 */
	private void updateGridView(boolean isInit) {
		
		List<AppInfo> apps = AppUtils.getAppsByPackage(mContext, mAppDAO.getAppInfo(true));
		mListAppInfo.clear();
		mListAppInfo.addAll(apps);
		if (mListAppInfo.size() > 0) {
			mGridApp.setNumColumns(NUM_COLUMNS);
			mTextPrompt.setVisibility(View.GONE);
			mGridApp.setVisibility(View.VISIBLE);
			log.i("首页APP数量" + mListAppInfo.size());
			if (null != mContext) {
				if (null == mAppsAdapter) {
					log.i("PageIndex app adapter is null, load");
					mAppsAdapter = new AppsAdapter2(mContext, mListAppInfo);
					mGridApp.setAdapter(mAppsAdapter);
				} else {
					log.i("PageIndex app adapter is not null, notifyDataSetChanged");
					mAppsAdapter.notifyDataSetChanged();
				}
			}
		} else {
			mTextPrompt.setVisibility(View.VISIBLE);
			mGridApp.setVisibility(View.GONE);
		}
	}

	private void initData() {
		mAppDAO = new AppDAO(mContext);
		mListAppInfo=mAppDAO.getAppInfo(true);
		imageLoader = new ImageLoader(mContext);
		
	}

	private void initWidget(View view) {
		mGridApp = (GridView) view.findViewById(R.id.index_grid);
		mTextPrompt = (TextView) view.findViewById(R.id.index_prompt);
		mGalleryAd = (ImageView) view.findViewById(R.id.index_gallery);
		mAdContainer = (LinearLayout) view.findViewById(R.id.ad_container);
		mPageView = (PageIndicatorView) view.findViewById(R.id.ad_page_num);
		mGridApp.setOnItemClickListener(mGridItemClickListener);
		//mGridApp.setOnItemLongClickListener(mGridItemLongClickListener);
		mAdContainer.setOnClickListener(mAdClickListener);
		mGalleryAd.setOnClickListener(mAdClickListener);
		mGridApp.setNextFocusLeftId(0);
		updateGridView(true);
		fillGallery(false);
		mAdContainer.setOnKeyListener(mAdKeyListener);
		mAdContainer.setNextFocusLeftId(0);
	}

	private void startScrollAd() {
		mTimerAdChange.schedule(new TimerTask() {
			public void run() {
				try {
					mHandler.sendEmptyMessage(CHANGE_AD_PICTURE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 30000L, 30000L);
	}

	private OnKeyListener mAdKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View arg0, int keyCode, KeyEvent event) {
			log.i("ad key");
			if (keyCode == KeyEvent.KEYCODE_DPAD_UP
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
			//	fillGallery(true);
				return false;
			}
			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				//fillGallery(false);
				return false;
			}
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				   Log.d("---","right");
				   fillGallery(false);
				return true;
				// 翻到下一页
				// return false;
			}
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				   Log.d("---","left");
				   fillGallery(true);
				return true;
				// 翻到下一页
				// return false;
			}
			return false;
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CHANGE_AD_PICTURE:
				fillGallery(false);
				break;
			default:
				break;
			}
		};
	};

	public static final int AD_TYPE_PICTURE = 1;
	public static final int AD_TYPE_VIDEO = 2;
	public static final int AD_TYPE_WEB = 3;
	String url;
	String key;
	String type;
	String[] picUrl;
	int adPicPos;
	private OnClickListener mAdClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			if (null == Declare.listAdMsg || Declare.listAdMsg.size() <= 0) {
				log.i("Declare.listAdMsg 为空，点击无效");
				return;
			}

			AdMsg adMsg = Declare.listAdMsg.get(mAdImageSelect);

			url = adMsg.getAdurl();
			log.i("click ad url = " + url);
			log.i("click ad type = " + type);
			type = adMsg.getType();
			key = adMsg.getKey();

			adPicPos = 0;

			if (null == type)
				return;
			if (null == url)
				return;
			int adType = 0;
			try {
				adType = Integer.valueOf(type);
			} catch (Exception e) {
				log.e(e.toString());
			}

			switch (adType) {
			case AD_TYPE_PICTURE:
				showDialog(adType);
				break;
			case AD_TYPE_VIDEO:
				log.i("顯示視頻");
				showDialog(adType);
				break;
			case AD_TYPE_WEB:
				if (url.startsWith("http://") || url.startsWith("https://")) {
					// 链接
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					mContext.startActivity(intent);
				}
				break;
			}
		}

		private void showDialog(int type) {

			mAdDialog = new Dialog(mContext, R.style.ad_dialog);
			mAdDialog.setContentView(R.layout.activity_show_ad);
			mAdDialog.show();

			mAdVideoview = (VideoView) mAdDialog.findViewById(R.id.videoview);
			mAdImageView = (TextView) mAdDialog.findViewById(R.id.ad_image);
			mAdArrLeft = (TextView) mAdDialog.findViewById(R.id.ad_arrow_left);
			mAdArrRight = (TextView) mAdDialog
					.findViewById(R.id.ad_arrow_right);
			mAdPage = (TextView) mAdDialog.findViewById(R.id.ad_page);
			mImageContainer = (RelativeLayout) mAdDialog
					.findViewById(R.id.image_container);
			mVideoviewContainer = (RelativeLayout) mAdDialog
					.findViewById(R.id.videoview_container);
			Button btn = (Button) mAdDialog.findViewById(R.id.btn_focus);
			btn.setOnKeyListener(mAdBtbKeyListener);
			btn.requestFocus();
			btn.requestFocusFromTouch();
			if (AD_TYPE_PICTURE == type) {
				mImageContainer.setVisibility(View.VISIBLE);
				mVideoviewContainer.setVisibility(View.GONE);
				picUrl = url.split("\\|\\|");
				log.i("picture url = " + picUrl[0]);
				FinalBitmap fb = FinalBitmap.create(mContext);
				fb.display(mAdImageView, picUrl[0]);
				setArrPage();
			} else if (AD_TYPE_VIDEO == type) {
				new CustomToast(mContext, mContext.getText(R.string.ready_play)
						.toString(), 24).show();
				mImageContainer.setVisibility(View.GONE);
				mVideoviewContainer.setVisibility(View.VISIBLE);
				if (url.startsWith("force")) {
					new ForceTV().initForceClient();
					startPlayForce();
				} else {
					mAdVideoview.setVideoPath(url);
					mAdVideoview
							.setOnCompletionListener(new OnCompletionListener() {
								@Override
								public void onCompletion(MediaPlayer mp) {
									hiddenDialog();
								}
							});
					mAdVideoview.setOnErrorListener(new OnErrorListener() {
						@Override
						public boolean onError(MediaPlayer mp, int what,
								int extra) {
							hiddenDialog();
							return false;
						}
					});
					mAdVideoview.start();
				}
			}
		}
	};

	private void setArrPage() {
		if (null != mAdPage && null != picUrl)
			mAdPage.setText((adPicPos + 1) + "/" + picUrl.length);
	}

	private OnKeyListener mAdBtbKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN && null != type
					&& String.valueOf(AD_TYPE_PICTURE).equals(type)) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
					if (adPicPos > 0) {
						adPicPos--;
						if (null != picUrl && picUrl.length > adPicPos
								&& null != mAdImageView) {
							setArrPage();
							FinalBitmap.create(mContext).display(mAdImageView,
									picUrl[adPicPos]);
							Animation anim = AnimationUtils.loadAnimation(
									mContext, R.anim.dialog_enter);
							mAdImageView.setAnimation(anim);
						}
					}
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					if (null != picUrl && adPicPos < picUrl.length - 1) {
						adPicPos++;
						if (null != picUrl && picUrl.length > adPicPos
								&& null != mAdImageView) {
							setArrPage();
							FinalBitmap.create(mContext).display(mAdImageView,
									picUrl[adPicPos]);
							Animation anim = AnimationUtils.loadAnimation(
									mContext, R.anim.dialog_out);
							mAdImageView.setAnimation(anim);
						}
					}

				}
			}

			if (event.getAction() == KeyEvent.ACTION_DOWN && null != type
					&& String.valueOf(AD_TYPE_VIDEO).equals(type)) {

			}

			return false;
		}
	};

	Dialog mAdDialog;
	VideoView mAdVideoview;
	TextView mAdImageView;
	TextView mAdArrLeft, mAdArrRight, mAdPage;
	RelativeLayout mImageContainer;
	RelativeLayout mVideoviewContainer;

	public static final int NETWORK_CONNECT = 0;
	public static final int NETWORK_NOT_CONNECT = 1;
	public static final int PLAY = 2;

	private void startPlayForce() {
		if (NetworkUtil.isConnectingToInternet(mContext)) {
			mAdHandler.sendEmptyMessage(NETWORK_CONNECT);
		} else {
			mAdHandler.sendEmptyMessage(NETWORK_NOT_CONNECT);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mAdHandler = new Handler() {
		String[] arrayOfString = null;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case NETWORK_CONNECT:
				arrayOfString = url.split("://")[1].split("/");
				final String forceLocal = getVideoHttp(arrayOfString[1],
						arrayOfString[0], FORCE_CMD, key);
				new Thread(new Runnable() {
					@Override
					public void run() {
						String request = RequestUtil.getInstance().request(
								forceLocal);
						int status = checkVodCanPlay(request);
						if (status == 0) {
							log.i("请求成功，准备播放");
							mAdHandler.sendEmptyMessage(PLAY);
						} else {
							log.i("请求失败，无法播放");
							// hiddenDialog(R.string.agree_disclaimer);
						}
					}
				}).start();
				break;
			case NETWORK_NOT_CONNECT:
				new CustomToast(mContext, mContext.getText(
						R.string.network_not_connect).toString(), 24).show();
				break;
			case PLAY:
				if (null == arrayOfString)
					return;
				String videoPath = new StringBuilder(PLAY_SERVER)
						.append(arrayOfString[1]).append(".").append("ts")
						.toString();
				log.i(videoPath);
				if (null != mAdVideoview && null != mAdImageView) {
					mImageContainer.setVisibility(View.GONE);
					mVideoviewContainer.setVisibility(View.VISIBLE);
					// Integer[] i = ScreenUtils.getScreenSize(mContext);
					mAdVideoview.setVideoPath(videoPath);
					log.i("---- videoview start ---- ");
					mAdVideoview.start();
				}
				break;
			}
		};
	};

	private void hiddenDialog() {
		if (null != mAdDialog && mAdDialog.isShowing()) {
			mAdDialog.dismiss();
		}
	}

	public static int checkVodCanPlay(String xml) {
		Document doc = null;
		try {
			String parseXml = xml.replace(
					"<?xml version=\"1.0\" encoding=\"utf-8\"?>", "").replace(
					"ver=1.0", "");
			doc = DocumentHelper.parseText(parseXml);
			Element root = doc.getRootElement();
			Element element = (Element) root
					.selectSingleNode("/forcetv/result");
			int flag = Integer.valueOf(element.attribute("ret").getValue());
			return flag;
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return -1;
	}

	public static final String PLAY_SERVER = "http://127.0.0.1:9906/";
	public static final String FORCE_CMD = "switch_chan";

	private String getVideoHttp(String channelId, String streamIp, String cmd,
			String link) {
		StringBuilder s4 = new StringBuilder(PLAY_SERVER + "cmd.xml?cmd=");
		s4.append(cmd).append("&id=").append(channelId).append("&server=")
				.append(streamIp);
		if (link != null && !link.equals("")) {
			s4.append("&link=").append(link);
		}
		return s4.toString();
	}

	private OnItemClickListener mGridItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			String pkgname = mListAppInfo.get(position).getPackageName();
			ActivityUtils.startActivity(mContext, pkgname);
		}
	};

	private OnItemLongClickListener mGridItemLongClickListener = new OnItemLongClickListener() {// GridView的Item长按事件

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			DeskAppMgr deskAppMgr=new DeskAppMgr(mContext);
			deskAppMgr.setData(mAppsAdapter,mListAppInfo);
			deskAppMgr.show();
			return true;
		}
	};

	public void clearGridFocus() {
		mGridApp.clearFocus();
	}

	
}
