package com.yimei.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.adapter.PlasmaTwoAdapter;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class PlasmeTwoActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private EditText yimei_plasma_user,yimei_plasma_sid1;
	private PlasmaTwoAdapter plasetwoAdapter;
	private String op;
	
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
				if(tag.equals("二次清洗作业员")){
					yimei_plasma_user.setText(barcodeData.toUpperCase().trim());
					if(yimei_plasma_user.getText().toString().toUpperCase().trim()==null
							||yimei_plasma_user.getText().toString().toUpperCase().trim().equals("")){
						ToastUtil.showToast(PlasmeTwoActivity.this,"作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_plasma_user);
						return;
					}
					op = yimei_plasma_user.getText().toString().toUpperCase().trim();
					MyApplication.nextEditFocus(yimei_plasma_sid1);
				}
				if(tag.equals("二次清洗批次号")){
					yimei_plasma_sid1.setText(barcodeData.toUpperCase().trim());
					if (yimei_plasma_user.getText().toString().trim().equals("")
							|| yimei_plasma_user.getText().toString().trim() == null) {
						ToastUtil.showToast(PlasmeTwoActivity.this, "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_plasma_user);
						return ;
					}
					if (yimei_plasma_sid1.getText().toString().trim().equals("")
							|| yimei_plasma_sid1.getText().toString().trim() == null) {
						ToastUtil.showToast(PlasmeTwoActivity.this, "批次号不能为空", 0);
						MyApplication.nextEditFocus(yimei_plasma_sid1);
						return ;
					}
					OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
							MyApplication.QueryBatNo("ZHUANXULIST", "~sbuid='D0073' and sid1='"+yimei_plasma_sid1.getText().toString().toUpperCase().trim()+"'"), null, mHander, true,
							"QueryJiLu");
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plasmatwo);
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.plasma_scroll_title);
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.plasma_scroll_list);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		yimei_plasma_user = (EditText) findViewById(R.id.yimei_plasma_user);
		yimei_plasma_sid1 = (EditText) findViewById(R.id.yimei_plasma_sid1);
		
		yimei_plasma_sid1.setOnEditorActionListener(TextEdit);
		yimei_plasma_user.setOnEditorActionListener(TextEdit);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(barcodeReceiver); // 取消广播注册
	} 
	
	OnFocusChangeListener TextFocus = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(v.getId() == R.id.yimei_plasma_user){
				if(!hasFocus){
					if (yimei_plasma_user.getText().toString().trim().equals("")
							|| yimei_plasma_user.getText().toString().trim() == null) {
						ToastUtil.showToast(PlasmeTwoActivity.this, "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_plasma_user);
						return;
					}
					op = yimei_plasma_user.getText().toString().toUpperCase().trim();
					MyApplication.nextEditFocus(yimei_plasma_sid1);
				}
			}	
		}
	};
	
	/**
	 * 回车
	 */
	OnEditorActionListener TextEdit = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(v.getId() == R.id.yimei_plasma_user){
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_plasma_user.getText().toString().trim().equals("")
							|| yimei_plasma_user.getText().toString().trim() == null) {
						ToastUtil.showToast(PlasmeTwoActivity.this, "作业员不能为空1", 0);
						MyApplication.nextEditFocus(yimei_plasma_user);
						yimei_plasma_user.selectAll();
						return false;
					}
					op = yimei_plasma_user.getText().toString().toUpperCase().trim();
					MyApplication.nextEditFocus(yimei_plasma_sid1);
				}
			}
			if(v.getId() == R.id.yimei_plasma_sid1){
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_plasma_user.getText().toString().trim().equals("")
							|| yimei_plasma_user.getText().toString().trim() == null) {
						ToastUtil.showToast(PlasmeTwoActivity.this, "作业员不能为空2", 0);
						MyApplication.nextEditFocus(yimei_plasma_user);
						return false;
					}
					if (yimei_plasma_sid1.getText().toString().trim().equals("")
							|| yimei_plasma_sid1.getText().toString().trim() == null) {
						ToastUtil.showToast(PlasmeTwoActivity.this, "批次号不能为空", 0);
						MyApplication.nextEditFocus(yimei_plasma_sid1);
						return false;
					}
					OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
							MyApplication.QueryBatNo("ZHUANXULIST", "~sbuid='D0073' and sid1='"+yimei_plasma_sid1.getText().toString().toUpperCase().trim()+"'"), null, mHander, true,
							"QueryJiLu");
				}
			}
			return false;
		}
	};
	
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
						// {"message":"请重新登录","id":-1}
						JSONObject LoginTimeMess = JSON.parseObject(b.getString(
								"jsonObj").toString());
						if (LoginTimeMess.containsKey("message")) {
							if (LoginTimeMess.get("message").equals("请重新登录")) { // 超时登录
								LoginTimeout_dig("超时登录", "请重新登录!");
								return;
							}
						}
						if (string.equals("TimeOutLogin")) { // 超时登录
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (jsonObject.getInteger("id") != 0) {
								LoginTimeout_dig("密码错误", "");
							}
						}
						if(string.equals("QueryJiLu")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
								Map<String, String> mapQuery= new HashMap<String, String>();
								mapQuery.put("dbid", MyApplication.DBID);
								mapQuery.put("usercode", MyApplication.user);
								mapQuery.put("apiId", "assist");
								mapQuery.put("assistid", "{QJBOXWEB}");
								mapQuery.put("cont", "~zcno='1P' and sid1='"+yimei_plasma_sid1.getText().toString().toUpperCase().trim()+"'");
								OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL
											, null, mapQuery, null, mHander, true, "QuerySid1");
							}else{
								ToastUtil.showToast(PlasmeTwoActivity.this, "该批次已经做过二次清洗!",0);
								yimei_plasma_sid1.selectAll();
								MyApplication.nextEditFocus(yimei_plasma_sid1);
							}
						}
						if(string.equals("QuerySid1")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (jsonObject.getInteger("code") == 0) {
								ToastUtil.showToast(PlasmeTwoActivity.this,"没有"+yimei_plasma_sid1.getText()+"记录!", 0);
								yimei_plasma_sid1.setText("");
								MyApplication.nextEditFocus(yimei_plasma_sid1);
								return;
							}else{
								JSONObject jsonValues = (JSONObject) (((JSONArray) jsonObject.get("values")).get(0));
								if(jsonValues.getString("state").equals("00")){
									ToastUtil.showToast(PlasmeTwoActivity.this,"请先进行第一次清洗!", 0);
									yimei_plasma_sid1.setText("");
									MyApplication.nextEditFocus(yimei_plasma_sid1);
									return;
								}else{
									jsonValues.put("sbuid", "D0073");
									jsonValues.put("sorg", MyApplication.sorg);
									jsonValues.put("slkid", jsonValues.getString("sid1").substring(0, 11));
									jsonValues.put("sid1", jsonValues.getString("sid1"));
									jsonValues.put("hpdate",MyApplication.GetServerNowTime());
									jsonValues.put("dcid",GetAndroidMacUtil.getMac());
									jsonValues.put("smake", MyApplication.user);
									jsonValues.put("mkdate",MyApplication.GetServerNowTime());
									jsonValues.put("outdate", MyApplication.GetServerNowTime());
									jsonValues.put("op_o", yimei_plasma_user.getText());
									jsonValues.put("op",yimei_plasma_user.getText());
									jsonValues.put("state1","04");
									jsonValues.put("state","0");
									//插入清洗表
									Map<String, String> mesIdMap = MyApplication
											.httpMapKeyValueMethod(MyApplication.DBID,
													"savedata", MyApplication.user,
													jsonValues.toJSONString(),
													"D0073W", "1");
									OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null, mesIdMap, null, mHander, true, "savedata");
								
									Map<String, String> updateServerTable = MyApplication
											.UpdateServerTableMethod(
													MyApplication.DBID,
													MyApplication.user,
													jsonValues.getString("state"), "04",
													jsonValues.getString("sid1"), jsonValues.getString("slkid"),
													"1P", "200");
									OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, 
											null, updateServerTable, null, mHander, true, "updateServer");
								}
							}
							System.out.println(jsonObject);
						}
						if(string.equals("updateServer")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							System.out.println(jsonObject);
						}
						if(string.equals("savedata")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(jsonObject.getInteger("id") == 1){
								ToastUtil.showToastLocation(getApplicationContext(),"二次清洗失败", 0);
								yimei_plasma_sid1.setText("");
								MyApplication.nextEditFocus(yimei_plasma_sid1);
							}else{
								Map<String, String> map = new HashMap<>();
								map.put("op", yimei_plasma_user.getText().toString());
								map.put("sid1", yimei_plasma_sid1.getText().toString());
								
								List<Map<String, String>> mList = new ArrayList<>();
								mList.add(map);
								
								if(plasetwoAdapter != null){
									plasetwoAdapter.listData.add(map);
									plasetwoAdapter.notifyDataSetChanged();
								}else{
									plasetwoAdapter = new PlasmaTwoAdapter(PlasmeTwoActivity.this, mList);
									mListView.setAdapter(plasetwoAdapter);
								}
								yimei_plasma_sid1.setText("");
								MyApplication.nextEditFocus(yimei_plasma_sid1);
							}
							System.out.println(jsonObject);
						}
					} catch (Exception e) {
						ToastUtil.showToastLocation(getApplicationContext(),e.toString(), 0);
					}
				}
			}
		}
	};
	
	
	/**
	 * 超时登录
	 * 
	 * @param Title
	 * @param msg
	 */
	private void LoginTimeout_dig(String Title, String msg) {
		LayoutInflater inflater = getLayoutInflater();
		View dialog = inflater.inflate(R.layout.activity_mesurl_dig,
				(ViewGroup) findViewById(R.id.dialogurl));
		final EditText usertext = (EditText) dialog.findViewById(R.id.mesurl);
		final EditText userpwd = (EditText) dialog.findViewById(R.id.mesdbid);
		final TextView logintime_usertext = (TextView) dialog
				.findViewById(R.id.logintime_user);
		final TextView logintime_pwdtext = (TextView) dialog
				.findViewById(R.id.logintime_pwd);
		logintime_usertext.setText("用户名");
		logintime_pwdtext.setText("密码");
		userpwd.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		usertext.setText(MyApplication.user);
//		usertext.setKeyListener(null);

		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				PlasmeTwoActivity.this);
		normalDialog.setTitle(Title);
		normalDialog.setView(dialog);
		// normalDialog.setMessage(Html.fromHtml("<font color='red'>" + msg
		// + "</font>"));
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("dbid", MyApplication.DBID);
						map.put("usercode", usertext.getText().toString());
						map.put("apiId", "login");
						map.put("pwd", MyApplication.Base64pwd(userpwd
								.getText().toString()));
						OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,
								null, map, null, mHander, true, "TimeOutLogin");
					}
				});
		// 显示
		normalDialog.show();
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
	
}
