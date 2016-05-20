package com.moon.android.iptv;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbaan.moonbox.database.UserMsgDAO;
import com.bestbaan.moonbox.model.UserMsg;
import com.bestbaan.moonbox.util.Logger;
import com.bestbaan.moonbox.util.MACUtils;
import com.bestbaan.moonbox.util.VersionUtil;
import com.bestbaan.moonbox.view.CustomToast;
import com.timetv.launch.R;

public class PageMoonBoxView extends RelativeLayout{

	public static Logger logger = Logger.getInstance();
	private TextView mTextVersion;
	private TextView mTextMac;
	private TextView mTextHardware;
	private ListView mListViewUserMsg;
	private LinearLayout mArrowLeft;
	private LinearLayout mArrowRight;
	private UserMsgDAO mMsgDAO;
	private List<UserMsg> mListUserMsg;
	private List<UserMsg> mCurrentPageUserMsg;
	private TextView mTextPage;
	private int mCurrentPage = 1;
	private int mTotalPage;
	private int LIST_PAGE_SIZE = 7;
	private Context mContext;

	public PageMoonBoxView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		View view = LayoutInflater.from(context).inflate(R.layout.page_moonbox, this);
		initData();
		regBroadCast();
		initWidget(view);
	}

	public PageMoonBoxView(Context context, AttributeSet attrs) {
		this(context, attrs, 0 );
	}

	public PageMoonBoxView(Context context) {
		this(context,null );
	}
	
	private void initData() {
		getListContent();
	}
	
	private void getListContent(){
		mMsgDAO = new UserMsgDAO(mContext);
		mListUserMsg = mMsgDAO.queryAll();
		mTotalPage = caculaterPages(mListUserMsg);
	}

	private void regBroadCast() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Configs.BroadCastConstant.GET_USER_MSG);
		myIntentFilter.addAction(Configs.BroadCastConstant.ACTION_GET_HARDWARE);
		mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			logger.i("Moonbox get msg broadcast");
			String action = intent.getAction();
			if(action.equals(Configs.BroadCastConstant.GET_USER_MSG)){
				getListContent();
				fillUserMsg();
				checkIsMsgNotRead();
			} else if(action.equals(Configs.BroadCastConstant.ACTION_GET_HARDWARE)){
				mTextHardware.setText(Declare.HARDWARE_MODEL);
			}
		}
	};
	
	public int getTotalPage(){
		return mTotalPage;
	}
	
	private void fillUserMsg(){
		setPage();
		mCurrentPageUserMsg = new ArrayList<UserMsg>();
		for (int i = (mCurrentPage - 1) * LIST_PAGE_SIZE; i < mCurrentPage
				* LIST_PAGE_SIZE; i++) {
			try {
				mCurrentPageUserMsg.add(mListUserMsg.get(i));
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		if (0 != mCurrentPageUserMsg.size()) {
			MsgAdapter adapter = new MsgAdapter(mContext, mCurrentPageUserMsg);
			mListViewUserMsg.setAdapter(adapter);
		}
	}
	
	private int caculaterPages(List<UserMsg> list){
		int appSize = list.size();
		if (0 == appSize % LIST_PAGE_SIZE)
			return appSize / LIST_PAGE_SIZE;
		else
			return appSize / LIST_PAGE_SIZE + 1;
	}
	
	private OnKeyListener mListKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(v == mListViewUserMsg && event.getAction() == KeyEvent.ACTION_DOWN){
				if(KeyEvent.KEYCODE_DPAD_RIGHT == keyCode){
					toNextPage();
					return true;
				} else if(KeyEvent.KEYCODE_DPAD_LEFT == keyCode){
					toPrePage();
					return true;
				}
			}
			return false;
		}
	};
	
	private void toNextPage(){
		if( mTotalPage > mCurrentPage){
			mCurrentPage ++;
			fillUserMsg();
		} else {
			new CustomToast(mContext, mContext.getString(R.string.has_to_last), 
					Configs.TOAST_TEXT_SIZE).show();
		}
	}
	
	private void toPrePage(){
		if(mCurrentPage > 1){
			mCurrentPage --;
			fillUserMsg();
		} else {
			new CustomToast(mContext, mContext.getString(R.string.has_to_first),
					Configs.TOAST_TEXT_SIZE).show();
		}
	}
	
	private void initWidget(View view) {
		mTextVersion = (TextView) view.findViewById(R.id.page_moonbox_version);
		mTextMac = (TextView) view.findViewById(R.id.page_moonbox_mac);
		mTextHardware = (TextView) view.findViewById(R.id.page_moonbox_hardware);
		mListViewUserMsg = (ListView) view.findViewById(R.id.page_moonbox_listview);
		mArrowLeft = (LinearLayout) view.findViewById(R.id.arrow_left);
		mArrowRight = (LinearLayout) view.findViewById(R.id.arrow_right);
		mTextPage = (TextView) view.findViewById(R.id.page_moonbox_msg_page);
		
		mTextMac.setText(MACUtils.getMac());
		mTextHardware.setText(Declare.HARDWARE_MODEL);
		String version = VersionUtil.getVersionName(mContext);
		mTextVersion.setText(version);
		fillUserMsg();
		mListViewUserMsg.setOnItemClickListener(mListItemLisener);
		mListViewUserMsg.setOnKeyListener(mListKeyListener);
		mArrowLeft.setOnClickListener(mArrowClickListener);
		mArrowRight.setOnClickListener(mArrowClickListener);
		setPage();
	}
	
	
	private void setPage(){
		if(null != mListUserMsg && mListUserMsg.size() >= 1){
			mTextPage.setText(mCurrentPage +"/"+mTotalPage);
			mArrowLeft.setVisibility(View.VISIBLE);
			mArrowRight.setVisibility(View.VISIBLE);}
		else {mTextPage.setText("0/0");
			mArrowLeft.setVisibility(View.INVISIBLE);
			mArrowRight.setVisibility(View.INVISIBLE);
		}
	}
	
	private OnClickListener mArrowClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == mArrowLeft){
				toPrePage();
			} else if(v == mArrowRight){
				toNextPage();
			}
		}
	};		
	
	private OnItemClickListener mListItemLisener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> views, View view, int position,
				long arg3) {
			UserMsg userMsg = mCurrentPageUserMsg.get(position);
			if(Configs.UserMsgVar.MSG_NOT_READ.equals(userMsg.getStatus())){
				View viewImage = ((LinearLayout)((RelativeLayout)view).getChildAt(1)).getChildAt(1);
				viewImage.setVisibility(View.INVISIBLE);
				int msgPos = (mCurrentPage - 1) * LIST_PAGE_SIZE + position;
				userMsg.setStatus(Configs.UserMsgVar.MSG_HAS_READ);
				mListUserMsg.set(msgPos, userMsg);
				mMsgDAO.changeMsgStatus(userMsg);
				checkIsMsgNotRead();
			}
			Intent intent = new Intent();
			intent.setClass(mContext, UserMsgShowActivity.class);
			intent.putExtra(Configs.PARAM_1, userMsg);
			mContext.startActivity(intent);
		}
	};
	
	private void checkIsMsgNotRead(){
		String flag = mMsgDAO.hasNoReadMsg()?Configs.BroadCastConstant.ACTION_NEW_USER_MSG:Configs.BroadCastConstant.ACTION_NEW_USER_NO_MSG;
		Intent intent = new Intent();
		intent.setAction(flag);
		mContext.sendBroadcast(intent);
	}

	public static class MsgAdapter extends BaseAdapter{
		private List<UserMsg> mListUserMsg;
		private LayoutInflater mInflater;
		public MsgAdapter(Context context,List<UserMsg> list){
			mListUserMsg = list;
			mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return mListUserMsg.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mListUserMsg.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			Holder holder = null;
			if(null == convertView){
				holder = new Holder();
				convertView = mInflater.inflate(R.layout.user_msg_item, null);
				holder.textTitle = (TextView) convertView.findViewById(R.id.user_msg_item_text);
				holder.imsgeMsg = (ImageView) convertView.findViewById(R.id.user_msg_item_icon);
				holder.textDate = (TextView)convertView.findViewById(R.id.user_msg_item_date);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			UserMsg msg = mListUserMsg.get(position);
			holder.textTitle.setText(msg.getTitle());
			int isRead = (Configs.UserMsgVar.MSG_HAS_READ.equals(msg.getStatus())) ? View.INVISIBLE : View.VISIBLE;
			holder.imsgeMsg.setVisibility(isRead);
			holder.textDate.setText(msg.getTime());
			if(position % 2 ==  0){
				convertView.setBackgroundResource(R.drawable.user_msg_no_transport);
			}
			return convertView;
		}
		
		class Holder{
			TextView textTitle;
			ImageView imsgeMsg;
			TextView textDate;
		}
	} 
}
