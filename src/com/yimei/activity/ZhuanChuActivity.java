package com.yimei.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.adapter.ZhuanChuAdapter;
import com.yimei.entity.Pair;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;

public class ZhuanChuActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private EditText yimei_zhuanchu_user,yimei_zhuanchu_sid;
	private ZhuanChuAdapter zhuanchuAdapter;
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
				if(tag.equals("转出作业员")){
					yimei_zhuanchu_user.setText(barcodeData.toUpperCase().trim());
					if(yimei_zhuanchu_user.getText().toString().toUpperCase().trim()==null
							||yimei_zhuanchu_user.getText().toString().toUpperCase().trim().equals("")){
						ToastUtil.showToast(ZhuanChuActivity.this,"作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_zhuanchu_user);
						return;
					}
					zuoyeyuan = yimei_zhuanchu_user.getText().toString().toUpperCase().trim();
					MyApplication.nextEditFocus(yimei_zhuanchu_sid);
				}
				if(tag.equals("转出批次号")){
					yimei_zhuanchu_sid.setText(barcodeData.toUpperCase().trim());
					if(yimei_zhuanchu_user.getText().toString().toUpperCase().trim()==null
							||yimei_zhuanchu_user.getText().toString().toUpperCase().trim().equals("")){
						ToastUtil.showToast(ZhuanChuActivity.this,"作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_zhuanchu_user);
						return;
					}
					if(yimei_zhuanchu_sid.getText().toString().toUpperCase().trim()==null
							||yimei_zhuanchu_sid.getText().toString().toUpperCase().trim().equals("")){
						ToastUtil.showToast(ZhuanChuActivity.this,"批次号不能为空",0);
						MyApplication.nextEditFocus(yimei_zhuanchu_sid);
						return;
					}	
					sid = yimei_zhuanchu_sid.getText().toString().toUpperCase().trim();
					OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
							MyApplication.QueryBatNo("ZHUANXULIST", "~sbuid='D0030' and sid1='"+sid+"'"), null, mHander, true,
							"QueryJiLu");
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhuanchu);
		String cont;
		if(MyApplication.user.equals("admin")){
			cont="";
		}else{
			cont ="~sorg='"+MyApplication.sorg+"'";
		}
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				MyApplication.QueryBatNo("M_PROCESS",cont), null, mHander, true,
				"SpinnerValue");
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.zhuanchu_scroll_title);
		// 添加头滑动事件
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.zhuanchu_scroll_list);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		yimei_zhuanchu_user = (EditText) findViewById(R.id.yimei_zhuanchu_user);
		yimei_zhuanchu_sid = (EditText) findViewById(R.id.yimei_zhuanchu_sid);
		
		yimei_zhuanchu_sid.setOnEditorActionListener(editEnter);
		yimei_zhuanchu_user.setOnEditorActionListener(editEnter);
//		selectValue = (Spinner) findViewById(R.id.zhuanchu_selectValue);
		
		yimei_zhuanchu_user.setOnFocusChangeListener(EditGetFocus);
	}
	
	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_zhuanchu_user) {
				if (!hasFocus) {
					zuoyeyuan = yimei_zhuanchu_user.getText().toString().trim();
				} else {
					yimei_zhuanchu_user.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_zhuanchu_sid) {
				if (hasFocus) {
					yimei_zhuanchu_sid.getText().toString().trim();
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
			if (v.getId() == R.id.yimei_zhuanchu_user) {
				if (actionId >= 0) {
					if(yimei_zhuanchu_user.getText().toString().toUpperCase().trim()==null
							||yimei_zhuanchu_user.getText().toString().toUpperCase().trim().equals("")){
						ToastUtil.showToast(ZhuanChuActivity.this,"作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_zhuanchu_user);
						return false;
					}
					zuoyeyuan = yimei_zhuanchu_user.getText().toString().toUpperCase().trim();
					MyApplication.nextEditFocus(yimei_zhuanchu_sid);
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_zhuanchu_sid) {
				if (actionId >= 0) {
					if(yimei_zhuanchu_user.getText().toString().toUpperCase().trim()==null
							||yimei_zhuanchu_user.getText().toString().toUpperCase().trim().equals("")){
						ToastUtil.showToast(ZhuanChuActivity.this,"作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_zhuanchu_user);
						return false;
					}
					if(yimei_zhuanchu_sid.getText().toString().toUpperCase().trim()==null
							||yimei_zhuanchu_sid.getText().toString().toUpperCase().trim().equals("")){
						ToastUtil.showToast(ZhuanChuActivity.this,"批次号不能为空",0);
						MyApplication.nextEditFocus(yimei_zhuanchu_sid);
						return false;
					}
					sid = yimei_zhuanchu_sid.getText().toString().toUpperCase().trim();
					OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
							MyApplication.QueryBatNo("ZHUANXULIST", "~sbuid='D0030' and sid1='"+sid+"'"), null, mHander, true,
							"QueryJiLu");
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
		// TODO Auto-generated method stub
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
						/*if (string.equals("SpinnerValue")) { // 给下拉框赋值
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(),
										"没有查到制程号~", 0);
								return;
							} else {
								SetSelectValue(jsonObject);
							}
						}*/
						if(string.equals("QueryJiLu")){ //查询记录表中有没有该记录
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
								OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
										MyApplication.QueryBatNo("MOZCLISTWEB", "~zcno='"+zcno+"' and sid1='"+sid+"' and  bok='1' "), null, mHander, true,
										"QuerySid");
							}else{
								ToastUtil.showToast(ZhuanChuActivity.this, "该批次已经做过转序!",0);
								yimei_zhuanchu_sid.selectAll();
//								InputHidden();
							}
						}
						if(string.equals("QuerySid")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(), "没有该批次号!",0);
								yimei_zhuanchu_sid.selectAll();
								InputHidden();
								return;
							} else {
								for (int i = 0; i <((JSONArray) jsonObject
										.get("values")).size(); i++) {
									JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
											.get("values")).get(i));
									
								if (Integer.parseInt(jsonValue.get("bok").toString()) == 0) {
									ToastUtil.showToast(ZhuanChuActivity.this, "该批次不具备转序条件!",0);
									yimei_zhuanchu_sid.selectAll();
									InputHidden();
									return;
								} else if (jsonValue.get("state").toString().equals("01")
										||jsonValue.get("state").toString().equals("02")
										||jsonValue.get("state").toString().equals("03")
										||jsonValue.get("state").toString().equals("00")
										||jsonValue.get("state").toString().equals("07")) {
									ToastUtil.showToast(ZhuanChuActivity.this, "该批次号状态不是【出站】,不能转出!", 0);
									InputHidden();
									yimei_zhuanchu_sid.selectAll();
									return;
								}
								else{
									showJson = jsonValue;
									zcno1 = jsonValue.get("zcno1").toString();
									showJson.put("op","A0209");
									showJson.put("slkid",showJson.get("sid"));
									showJson.put("sbuid","D0030");
									showJson.put("sid", "");
									showJson.put("sorg",MyApplication.sorg);
									showJson.put("erid","0");
									showJson.put("state1","04");
									showJson.put("op_c",yimei_zhuanchu_user.getText().toString().toUpperCase().trim());
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
													"D0030WEB", "1");
									OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,mesIdMap,null, mHander, true,"savedata");
									
									Map<String, String> updateServerTable = MyApplication
											.UpdateServerTableMethod(
													MyApplication.DBID,
													MyApplication.user,
													showJson.get("state").toString(), "04",
													showJson.get("sid1").toString(),showJson.get("slkid").toString(),
													zcno, "200");
									OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,updateServerTable,null, mHander, true,"updateTableState");
								}
								}
							}
						}
						if(string.equals("updateTableState")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(!(Integer.parseInt(jsonObject.get("id").toString()) == 1)){
								ToastUtil.showToast(ZhuanChuActivity.this,"转序修改状态失败（200）",0);
							}else{
								List<Map<String,String>> mList = new ArrayList<Map<String,String>>();								
								Map<String,String> map = new HashMap<String,String>();
								map.put("zcno","外观");
								map.put("zcno1","测试");
								map.put("sid",sid);
								map.put("slkid",showJson.get("sid").toString());
								map.put("prd_name",showJson.get("prd_name").toString());
								map.put("qty",showJson.get("qty").toString());
								mList.add(map);
								
								if(zhuanchuAdapter==null){
									zhuanchuAdapter = new ZhuanChuAdapter(ZhuanChuActivity.this,mList);
									mListView.setAdapter(zhuanchuAdapter);
								}else{
									zhuanchuAdapter.listData.add(map);
									zhuanchuAdapter.notifyDataSetChanged();
								}
							}
						}
						if(string.equals("savedata")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(!(Integer.parseInt(jsonObject.get("id").toString()) == 0)){
//								ToastUtil.showToastLocation(getApplicationContext(),"（savedata）失败", 0);
							}
							InputHidden();
							yimei_zhuanchu_sid.selectAll();
						}
						if(string.equals("updateServer_200")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(!(Integer.parseInt(jsonObject.get("id").toString()) == 1)){
//								ToastUtil.showToastLocation(getApplicationContext(),"（cref3）失败", 0);
							}
						}
						if(string.equals("updateTableState")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(!(Integer.parseInt(jsonObject.get("id").toString()) == 1)){
//								ToastUtil.showToastLocation(getApplicationContext(),"（cref3）失败", 0);
							}
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
	
	/**
	 * 弹出提示框
	 * 
	 * @param mes
	 */
	private void showNormalDialog(String mes) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				ZhuanChuActivity.this);
		normalDialog.setTitle("提示");
		normalDialog.setMessage(mes);
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showJson.put("op",zuoyeyuan);
						showJson.put("slkid",showJson.get("sid"));
						showJson.put("sbuid","D0030");
						showJson.put("sid", "");
						showJson.put("sorg",MyApplication.sorg);
						showJson.put("erid","0");
						showJson.put("state","04");
						showJson.put("op_c",yimei_zhuanchu_user.getText().toString().toUpperCase().trim());
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
										"D0030WEB", "1");
						OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,mesIdMap,null, mHander, true,"savedata");
						
						Map<String, String> updateServerTable = MyApplication
								.UpdateServerTableMethod(
										MyApplication.DBID,
										MyApplication.user,
										showJson.get("state").toString(), "04",
										showJson.get("sid1").toString(),showJson.get("slkid").toString(),
										zcno, "200");
						OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,updateServerTable,null, mHander, true,"updateTableState");
					
					}
				});
		normalDialog.setNegativeButton("取消", null);
		// 显示
		normalDialog.show();
	}
	
	
	/**
	 * 给下拉框赋值
	 * 
	 * @param hScrollView
	 */
	/*private void SetSelectValue(JSONObject jsonObject) {
		List<Pair> dicts = new ArrayList<Pair>();
		for (int i = 0; i < ((JSONArray) jsonObject.get("values")).size(); i++) {
			JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
					.get("values")).get(i));
			dicts.add(new Pair(jsonValue.getString("name").toString(),
					jsonValue.getString("id").toString()));
			zcnoMap.put(jsonValue.getString("id").toString(),jsonValue.getString("name").toString());
		}
		ArrayAdapter<Pair> adapter = new ArrayAdapter<Pair>(
				ZhuanChuActivity.this,
				android.R.layout.simple_spinner_item, dicts);
		selectValue.setAdapter(adapter);
		selectValue.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				zcno = ((Pair) selectValue.getSelectedItem()).getValue();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}*/
	
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
