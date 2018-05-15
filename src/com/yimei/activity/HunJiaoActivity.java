package com.yimei.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.adapter.HunJiaoAdapter;
import com.yimei.adapter.ZhuangXiangAdapter;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class HunJiaoActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;

	private EditText yimei_hunjiao_user_edt, yimei_hunjiao_sbid_edt,
			yimei_hunjiao_proNum_edt;

	private HunJiaoAdapter hunJiaoAdapter;
	private String zuoyeyuan;
	private String sbid; // 设备号
	private String prtno; // 胶杯批号
	
	private Calendar nowTime = Calendar.getInstance();
	
	static MyApplication myapp;
	public static HunJiaoActivity hunjiaoActivity;

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
				if (tag == null) {
					return;
				}
				// 拿到pda扫描后的值
				String barcodeData;
				if (intent.getStringExtra("data") == null) {
					barcodeData = intent.getStringExtra(
							MyApplication.SCN_CUST_EX_SCODE)// 拿到销邦终端的值
							.toString();
				} else {
					barcodeData = intent.getStringExtra("data").toString(); // 拿到HoneyWell终端的值
				}
				if (tag.equals("混胶作业员")) { // 设备号
					yimei_hunjiao_user_edt.setText(barcodeData);
					if (yimei_hunjiao_user_edt.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(), "请输入作业员~", 0);
						MyApplication.nextEditFocus(yimei_hunjiao_user_edt); // 跳转到作业员
						return;
					}
					zuoyeyuan = yimei_hunjiao_user_edt.getText().toString().trim();
					MyApplication.nextEditFocus(yimei_hunjiao_sbid_edt); // 跳转到作业员
				}
				if (tag.equals("混胶设备号")) { //设备号
					yimei_hunjiao_sbid_edt.setText(barcodeData);
					if (yimei_hunjiao_user_edt.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(), "请输入作业员~", 0);
						MyApplication.nextEditFocus(yimei_hunjiao_user_edt); // 跳转到作业员
						return;
					}
					if (yimei_hunjiao_sbid_edt.getText().toString().trim()
							.equals("")||yimei_hunjiao_sbid_edt.getText().toString().trim()==null) {
						ToastUtil.showToast(getApplicationContext(), "请输入设备号~",0);
						MyApplication.nextEditFocus(yimei_hunjiao_sbid_edt);
						return;
					}
					sbid = yimei_hunjiao_sbid_edt.getText().toString().toUpperCase().trim();
					Map<String, String> map = MyApplication.QueryBatNo("MESEQUTM", "~id='" + sbid
							+ "' and zc_id='31'");
					OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,map,null, mHander, true,"hunjiao_Isshebei");
				}
				if(tag.equals("混胶批次号")){
					yimei_hunjiao_proNum_edt.setText(barcodeData);
					if (yimei_hunjiao_user_edt.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(), "请输入作业员~", 0);
						MyApplication.nextEditFocus(yimei_hunjiao_user_edt); // 跳转到作业员
						return;
					}
					if (yimei_hunjiao_sbid_edt.getText().toString().trim()
							.equals("")||yimei_hunjiao_sbid_edt.getText().toString().trim()==null) {
						ToastUtil.showToast(getApplicationContext(), "请输入设备号~",0);
						MyApplication.nextEditFocus(yimei_hunjiao_sbid_edt);
						return;
					}
					if (yimei_hunjiao_proNum_edt.getText().toString().trim()
							.equals("")||yimei_hunjiao_proNum_edt.getText().toString().trim()==null) {
						ToastUtil.showToast(getApplicationContext(), "请输入批次号~",0);
						
						MyApplication.nextEditFocus(yimei_hunjiao_proNum_edt);
						return;
					}
					prtno = yimei_hunjiao_proNum_edt.getText().toString().trim();
					Map<String, String> map = MyApplication.QueryBatNo("MESEQUTM", "~id='" + sbid
							+ "' and zc_id='31'");
					OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,map,null, mHander, true,"hunjiao_Isshebei");
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hunjiao);
		
		Application app = getApplication();
		myapp = (MyApplication) app;
		myapp.addActivity_(this);
		hunjiaoActivity = this;
		myapp.removeActivity_(LoginActivity.loginActivity);// 销毁登录

		
		mListView = (ListView) findViewById(R.id.hunjiao_scroll_list);
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.hunjiao_scroll_title);
		GeneralCHScrollView.add(headerScroll);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		yimei_hunjiao_user_edt = (EditText) findViewById(R.id.yimei_hunjiao_user_edt);
		yimei_hunjiao_sbid_edt = (EditText) findViewById(R.id.yimei_hunjiao_sbid_edt);
		yimei_hunjiao_proNum_edt = (EditText) findViewById(R.id.yimei_hunjiao_proNum_edt);

		yimei_hunjiao_user_edt.setOnEditorActionListener(editEnter);
		yimei_hunjiao_sbid_edt.setOnEditorActionListener(editEnter);
		yimei_hunjiao_proNum_edt.setOnEditorActionListener(editEnter);
		
		yimei_hunjiao_user_edt.setOnFocusChangeListener(EditGetFocus);
		yimei_hunjiao_sbid_edt.setOnFocusChangeListener(EditGetFocus);
		yimei_hunjiao_proNum_edt.setOnFocusChangeListener(EditGetFocus);
	};

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(barcodeReceiver); // 取消广播注册
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}
	
	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_hunjiao_user_edt) {
				if (!hasFocus) { // 失去焦点
					zuoyeyuan = yimei_hunjiao_user_edt.getText().toString().trim();
				} else { // 获取焦点
					yimei_hunjiao_user_edt.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_hunjiao_sbid_edt) {
				if (hasFocus) {
					yimei_hunjiao_sbid_edt.setSelectAllOnFocus(true);
				} else {
					sbid = yimei_hunjiao_sbid_edt.getText().toString()
							.toUpperCase().trim();
					yimei_hunjiao_sbid_edt.setText(sbid);
				}
			}
			if (v.getId() == R.id.yimei_hunjiao_proNum_edt) {
				if (hasFocus) {
					yimei_hunjiao_proNum_edt.setSelectAllOnFocus(true);
				}
			}
		}
	};
	
	
	/**
	 * 回车事件
	 */
	OnEditorActionListener editEnter = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean flag = false;
			if (v.getId() == R.id.yimei_hunjiao_user_edt) { // 经办人
				if (yimei_hunjiao_user_edt.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入作业员~", 0);
					return false;
				}
				zuoyeyuan = yimei_hunjiao_sbid_edt.getText().toString().trim();
				MyApplication.nextEditFocus(yimei_hunjiao_sbid_edt);
			}
			if (v.getId() == R.id.yimei_hunjiao_sbid_edt) { // 设备号
				if (yimei_hunjiao_user_edt.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入作业员~", 0);
					return false;
				}
				if (yimei_hunjiao_sbid_edt.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入设备号~", 0);
					return false;
				}
				sbid = yimei_hunjiao_sbid_edt.getText().toString().toUpperCase().trim();
				Map<String, String> map = MyApplication.QueryBatNo("MESEQUTM", "~id='" + sbid
						+ "' and zc_id='31'");
				OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,map,null, mHander, true,"hunjiao_Isshebei");
			}
			if (v.getId() == R.id.yimei_hunjiao_proNum_edt) { // 批次号
				if (yimei_hunjiao_user_edt.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入作业员~", 0);
					return false;
				}
				if (yimei_hunjiao_sbid_edt.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入设备号~", 0);
					return false;
				}
				if(yimei_hunjiao_proNum_edt.getText().toString().trim().equals("")){
					ToastUtil.showToast(getApplicationContext(), "请输入批次号~", 0);
					return false;
				}
				prtno = yimei_hunjiao_proNum_edt.getText().toString().trim();
				Map<String, String> map = MyApplication.QueryBatNo("MESEQUTM", "~id='" + sbid
						+ "' and zc_id='31'");
				OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,map,null, mHander, true,"hunjiao_Isshebei");
			}
			return flag;
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
						if (string.equals("hunjiao_Isshebei")) { //设备号查询
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
								MyApplication.nextEditFocus((EditText) findViewById(R.id.yimei_hunjiao_sbid_edt));
								yimei_hunjiao_sbid_edt.selectAll();
								ToastUtil.showToast(getApplicationContext(),
										"没有该设备编号!", 0);
							}else{
								View rootview = getCurrentFocus();
								Object tag = rootview.findFocus().getTag();
								if(tag.equals("混胶设备号")){									
									MyApplication.nextEditFocus(yimei_hunjiao_proNum_edt); 
								}else if(tag.equals("混胶批次号")){
									Map<String, String> map = MyApplication.QueryBatNo("MESGLUEJOB","~prtno='" + prtno + "'");
									OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,map,null, mHander, true,"hunjiao_prdnoQuery");
								}
							}
						}
						if(string.equals("hunjiao_prdnoQuery")){ //批次号查询
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if(Integer.parseInt(jsonObject.get("code").toString())==0){
								MyApplication.nextEditFocus((EditText) findViewById(R.id.yimei_hunjiao_proNum_edt));
								yimei_hunjiao_proNum_edt.selectAll();
								ToastUtil.showToast(getApplicationContext(),
										"没有该批次号,请核对~", 0);
								return;
							}else{
								JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
										.get("values")).get(0));
								jsonValue.put("qty",jsonValue.get("effective_time")==null?"":jsonValue.get("effective_time"));
								jsonValue.put("op",zuoyeyuan);
								jsonValue.put("dcid",GetAndroidMacUtil.getMac());
								//加3小时
								jsonValue.put("newly_time",MyApplication.GetHunJiaoAdd_3(Integer.parseInt(jsonValue.get("effective_time").toString()))); //加3小时后的时间
								jsonValue.put("sbuid", "D2009");
								jsonValue.put("zcno", "31");
								jsonValue.put("sbid",sbid);
								jsonValue.put("sys_stated", "3"); // 新增
								jsonValue.put("slkid",jsonValue.get("mo_no")); 
								jsonValue.put("mkdate",jsonValue.get("tprn")==null?null:jsonValue.get("tprn"));
								jsonValue.put("prd_no",jsonValue.get("prdno")); 
								jsonValue.put("prd_name",jsonValue.get("name"));
								jsonValue.put("edate",jsonValue.get("vdate"));
								jsonValue.put("indate",MyApplication.GetServerNowTime());
								
								//************批次号添加
								Map<String, String> mesIdMap = MyApplication.httpMapKeyValueMethod(
										MyApplication.DBID, "savedata", MyApplication.user,
										jsonValue.toString(), "D2009", "1"); // 批次号添加（服务器提交）
								OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null, mesIdMap, null, mHander, true, "hunjiao_Addpici"); // 批次扫描添加
								
								//*********************301请求
								Map<String, String> hunjiao_301 = MyApplication.hunjiao_301(prtno,jsonValue.getString("effective_time"));
								OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,hunjiao_301,null, mHander, true,"Server_301");
								
								List<Map<String,String>> mList = new ArrayList<Map<String,String>>();
								Map<String,String> map = new HashMap<String,String>();
								map.put("prtno",jsonValue.get("prtno").toString());
								map.put("indate",jsonValue.get("indate").toString());
								map.put("newly_time",jsonValue.get("newly_time").toString());
								mList.add(map);
								if(hunJiaoAdapter == null){
									hunJiaoAdapter = new HunJiaoAdapter(HunJiaoActivity.this,mList);
									mListView.setAdapter(hunJiaoAdapter);
								}else{
									hunJiaoAdapter.listData.add(map);
									hunJiaoAdapter.notifyDataSetChanged();
								}
								
								
								yimei_hunjiao_proNum_edt.selectAll();
							}
							System.out.println(jsonObject);
						}
						if(string.equals("Server_301")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(Integer.parseInt(jsonObject.getString("id"))==-1){
								ToastUtil.showToast(hunjiaoActivity,"混胶301请求错误~",0);
							}
							System.out.println(jsonObject);
						}
					} catch (Exception e) {
						e.printStackTrace();
						ToastUtil.showToast(getApplicationContext(),
								e.toString(), 0);
					}
				}
			} else {
				Log.i("err", msg.obj.toString());
			}
		}
	};

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
