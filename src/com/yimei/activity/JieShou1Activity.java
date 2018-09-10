package com.yimei.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.adapter.JieShou1Adapter;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

public class JieShou1Activity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private EditText yimei_jieshou1_user,yimei_jieshou1_sid;
	private JieShou1Adapter jieshou1Adapter;
//	private Spinner selectValue; // 下拉框
	private String zcno = "51"; //当前制程
	private String zcno1 = "61";
	private String sid; //批次号
	private String zuoyeyuan;
	private Map<String,String> zcnoMap = new HashMap<String,String>();
	private JSONObject showJson;
	
	/**
	 * 获取pda扫描（广播）
	 */
	private BroadcastReceiver barcodeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (MyApplication.INTENT_ACTION_SCAN_RESULT.equals(intent
					.getAction())) {
				View rootview = getCurrentFocus();
				Object tag = rootview.findFocus().getTag();
				// 拿到pda扫描后的值
				String barcodeData;
				if (intent.getStringExtra("data") == null) {
					barcodeData = intent.getStringExtra(
							MyApplication.SCN_CUST_EX_SCODE)// 拿到销邦终端的值
							.toString();
				} else {
					barcodeData = intent.getStringExtra("data").toString(); // 拿到HoneyWell终端的值
				}
				if(tag.equals("接收作业员1")){
					yimei_jieshou1_user.setText(barcodeData.toUpperCase().trim());
					if(yimei_jieshou1_user.getText().toString().toUpperCase().trim()==null
							||yimei_jieshou1_user.getText().toString().toUpperCase().trim().equals("")){
						ToastUtil.showToast(JieShou1Activity.this,"作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_jieshou1_user);
						return;
					}
					zuoyeyuan = yimei_jieshou1_user.getText().toString().toUpperCase().trim();
					MyApplication.nextEditFocus(yimei_jieshou1_sid);
				}
				if(tag.equals("接收批次号1")){
					yimei_jieshou1_sid.setText(barcodeData.toUpperCase().trim());
					if(yimei_jieshou1_user.getText().toString().toUpperCase().trim()==null
							||yimei_jieshou1_user.getText().toString().toUpperCase().trim().equals("")){
						ToastUtil.showToast(JieShou1Activity.this,"作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_jieshou1_user);
						return;
					}
					if(yimei_jieshou1_sid.getText().toString().toUpperCase().trim()==null
							||yimei_jieshou1_sid.getText().toString().toUpperCase().trim().equals("")){
						ToastUtil.showToast(JieShou1Activity.this,"批次号不能为空",0);
						MyApplication.nextEditFocus(yimei_jieshou1_sid);
						return;
					}	
					sid = yimei_jieshou1_sid.getText().toString().toUpperCase().trim();
					OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
							MyApplication.QueryBatNo("ZHUANXULIST", "~sbuid='D0031' and sid1='"+sid+"'"), null, mHander, true,
							"QueryJiLu_D0031");
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jieshou1);
		String cont;
		if(MyApplication.user.equals("admin")){
			cont="";
		}else{
			cont ="~sorg='"+MyApplication.sorg+"'";
		}
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				MyApplication.QueryBatNo("M_PROCESS",cont), null, mHander, true,
				"SpinnerValue");
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.jieshou1_scroll_title);
		// 添加头滑动事件
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.jieshou1_scroll_list);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		yimei_jieshou1_user = (EditText) findViewById(R.id.yimei_jieshou1_user);
		yimei_jieshou1_sid = (EditText) findViewById(R.id.yimei_jieshou1_sid);
		
		yimei_jieshou1_sid.setOnEditorActionListener(editEnter);
		yimei_jieshou1_user.setOnEditorActionListener(editEnter);
		
		yimei_jieshou1_user.setOnFocusChangeListener(EditGetFocus);
	}
	
	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_jieshou1_user) {
				if (!hasFocus) {
					zuoyeyuan = yimei_jieshou1_user.getText().toString().trim();
				} else {
					yimei_jieshou1_user.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_jieshou1_sid) {
				if (hasFocus) {
					yimei_jieshou1_sid.getText().toString().trim();
				}
			}
		}
	};
	
	/**
	 * 键盘回车事件
	 */
	OnEditorActionListener editEnter = new OnEditorActionListener() {

		@SuppressLint("DefaultLocale")
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean flag = false;
			if (v.getId() == R.id.yimei_jieshou1_user) {
				if (actionId >= 0) {
					if(yimei_jieshou1_user.getText().toString().toUpperCase().trim()==null
							||yimei_jieshou1_user.getText().toString().toUpperCase().trim().equals("")){
						ToastUtil.showToast(JieShou1Activity.this,"作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_jieshou1_user);
						return false;
					}
					zuoyeyuan = yimei_jieshou1_user.getText().toString().toUpperCase().trim();
					MyApplication.nextEditFocus(yimei_jieshou1_sid);
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_jieshou1_sid) {
				if (actionId >= 0) {
					if(yimei_jieshou1_user.getText().toString().toUpperCase().trim()==null
							||yimei_jieshou1_user.getText().toString().toUpperCase().trim().equals("")){
						ToastUtil.showToast(JieShou1Activity.this,"作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_jieshou1_user);
						return false;
					}
					if(yimei_jieshou1_sid.getText().toString().toUpperCase().trim()==null
							||yimei_jieshou1_sid.getText().toString().toUpperCase().trim().equals("")){
						ToastUtil.showToast(JieShou1Activity.this,"批次号不能为空",0);
						MyApplication.nextEditFocus(yimei_jieshou1_sid);
						return false;
					}
					sid = yimei_jieshou1_sid.getText().toString().toUpperCase().trim();
					OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
							MyApplication.QueryBatNo("ZHUANXULIST", "~sbuid='D0031' and sid1='"+sid+"'"), null, mHander, true,
							"QueryJiLu_D0031");
					flag = true;
				}
			}
			return flag;
		}
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(barcodeReceiver); // 取消广播注册
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
	
	/**
	 * 逻辑判断
	 */
	@SuppressLint("HandlerLeak")
	private final Handler mHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				if (msg.arg1 == 0) {
					Bundle b = msg.getData();
					String string = b.getString("type");
					try {
						if(string.equals("QueryJiLu_D0031")){ //查询是否接收
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { //有没有接收过
								OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
										MyApplication.QueryBatNo("ZHUANXULIST", "~sbuid='D0030' and sid1='"+sid+"'"), null, mHander, true,
										"QueryJiLu");
							}else{
								ToastUtil.showToast(JieShou1Activity.this, "该批次已经接收!",0);
								yimei_jieshou1_sid.selectAll();
								InputHidden();
							}
						}
						if(string.equals("QueryJiLu")){ //查询记录表中有没有该记录
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { //没有做转序
								ToastUtil.showToast(JieShou1Activity.this, "该批次没有做转出，暂不能接收!",0);
								yimei_jieshou1_sid.selectAll();
								InputHidden();
							}else{
								OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
										MyApplication.QueryBatNo("MOZCLISTWEB", "~zcno='"+zcno+"' and sid1='"+sid+"'"), null, mHander, true,
										"QuerySid");
							}
						}
						if(string.equals("QuerySid")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(), "没有该批次号!",0);
								yimei_jieshou1_sid.selectAll();
								InputHidden();
								return;
							} else {
								JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
										.get("values")).get(0));
								if (Integer.parseInt(jsonValue.get("bok").toString()) == 0) {
									ToastUtil.showToast(JieShou1Activity.this, "该批次不具备转序条件!",0);
									yimei_jieshou1_sid.selectAll();
									InputHidden();
									return;
								} else if (jsonValue.get("state").toString().equals("01")
										||jsonValue.get("state").toString().equals("02")
										||jsonValue.get("state").toString().equals("03")
										||jsonValue.get("state").toString().equals("00")
										||jsonValue.get("state").toString().equals("07")) {
									ToastUtil.showToast(JieShou1Activity.this, "该批次号还没有出站，无法接收!", 0);
									InputHidden();
									yimei_jieshou1_sid.selectAll();
									return;
								} 
								else{
									showJson = jsonValue;
									zcno1 = jsonValue.get("zcno1").toString();
									showJson.put("op",zuoyeyuan);
									showJson.put("slkid",showJson.get("sid"));
									showJson.put("sbuid","D0031");
									showJson.put("sid", "");
									showJson.put("sorg",MyApplication.sorg);
									showJson.put("erid","0");
									showJson.put("state1","04");
									showJson.put("op_c",yimei_jieshou1_user.getText().toString().toUpperCase().trim());
									showJson.put("state","0");
									showJson.put("dcid",GetAndroidMacUtil.getMac());
									showJson.put("smake",MyApplication.user);
									showJson.put("mkdate",MyApplication.GetServerNowTime());
									showJson.put("hpdate",MyApplication.GetServerNowTime());
									showJson.put("outdate",MyApplication.GetServerNowTime());
									showJson.put("cref3","1");
									Map<String, String> mesIdMap = MyApplication
											.httpMapKeyValueMethod(MyApplication.DBID,
													"savedata", MyApplication.user,
													showJson.toJSONString(),
													"D0031WEB", "1");
									OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,mesIdMap,null, mHander, true,"savedata");
								}
							}
						}
						if(string.equals("savedata")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(Integer.parseInt(jsonObject.get("id").toString()) == 0){
								List<Map<String,String>> mList = new ArrayList<Map<String,String>>();								
								Map<String,String> map = new HashMap<String,String>();
								map.put("zcno","外观");
								map.put("zcno1","测试");
								map.put("sid",sid);
								map.put("slkid",showJson.get("sid").toString());
								map.put("prd_name",showJson.get("prd_name").toString());
								map.put("qty",showJson.get("qty").toString());
								mList.add(map);
								
								if(jieshou1Adapter==null){
									jieshou1Adapter = new JieShou1Adapter(JieShou1Activity.this,mList);
									mListView.setAdapter(jieshou1Adapter);
								}else{
									jieshou1Adapter.listData.add(map);
									jieshou1Adapter.notifyDataSetChanged();
								}
								
								ToastUtil.showToastLocation(getApplicationContext(),"接收成功", 0);
							}
							InputHidden();
							yimei_jieshou1_sid.selectAll();
						}
					} catch (Exception e) {
						ToastUtil.showToastLocation(getApplicationContext(),e.toString(), 0);
					}
				}
			}
		}
	};
	
	/**
	 * 隐藏键盘
	 */
	private void InputHidden() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 如果软键盘已经显示，则隐藏，反之则显示
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public static void addHViews(final GeneralCHScrollView hScrollView) {
		if (!GeneralCHScrollView.isEmpty()) {
			int size = GeneralCHScrollView.size();
			GeneralCHScrollView scrollView = GeneralCHScrollView.get(size - 1);
			final int scrollX = scrollView.getScrollX();
			if (scrollX != 0) {
				mListView.post(new Runnable() {
					@Override
					public void run() {
						hScrollView.scrollTo(scrollX, 0);
					}
				});
			}
		}
		GeneralCHScrollView.add(hScrollView);
	}

	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		for (GeneralCHScrollView scrollView : GeneralCHScrollView) {
			if (mTouchView != scrollView)
				scrollView.smoothScrollTo(l, t);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, MyApplication.LOGOUT, 1, MyApplication.LOGOUTText);
		menu.add(1, MyApplication.ABOUTUS, 2, MyApplication.ABOUTUSText);
		menu.add(1, MyApplication.VERSION, 3, MyApplication.VERSIONText);
		return true;
	}

	/**
	 * 切换用户
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case MyApplication.LOGOUT:
			startActivity(new Intent(getApplicationContext(),
					LoginActivity.class));
			break;
		case MyApplication.ABOUTUS:

			break;
		case MyApplication.VERSION:

			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
