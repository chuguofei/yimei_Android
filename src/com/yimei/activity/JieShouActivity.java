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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.adapter.JieShouAdapter;
import com.yimei.entity.mesPrecord;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

public class JieShouActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private EditText yimei_jieshou_user;
	private Button yimei_jieshou, yimei_jieshou_tuihui;
	private String op;
	private JieShouAdapter jieshouAdapter;
	private mesPrecord m;
	private JSONObject updateCref3;

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
				if (tag.equals("接收作业员")) { // 作业员
					yimei_jieshou_user.setText(barcodeData.toUpperCase().trim());
					if(yimei_jieshou_user.getText().toString().trim().equals("")
							||yimei_jieshou_user.getText().toString().trim()==null){
						ToastUtil.showToast(JieShouActivity.this,"作业员不能为空",0);
						return;
					}
					op = yimei_jieshou_user.getText().toString().trim()
							.toUpperCase();
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL,
							null,
							MyApplication.QueryBatNo("MSBMOLISTWEB",
									"~zcno1='61' and sbuid='D0030'"), null,
							mHander, true, "QueryRenWu");
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jieshou);
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.jieshou_scroll_title);
		// 添加头滑动事件
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.jieshou_scroll_list);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		yimei_jieshou_user = (EditText) findViewById(R.id.yimei_jieshou_user);
		yimei_jieshou = (Button) findViewById(R.id.yimei_jieshou);
		yimei_jieshou_tuihui = (Button) findViewById(R.id.yimei_jieshou_tuihui);

		yimei_jieshou.setOnClickListener(Click);
		yimei_jieshou_tuihui.setOnClickListener(Click);

		if(jieshouAdapter==null){
			yimei_jieshou.setEnabled(false);
			yimei_jieshou_tuihui.setEnabled(false);
		}else{
			yimei_jieshou.setEnabled(true);
			yimei_jieshou_tuihui.setEnabled(true);
		}
		yimei_jieshou_user.setOnEditorActionListener(editEnter);
	}
	
	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_jieshou_user) {
				if (!hasFocus) {
					op = yimei_jieshou_user.getText().toString().trim();
				} else {
					yimei_jieshou_user.setSelectAllOnFocus(true);
				}
			}
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

	OnClickListener Click = new OnClickListener() {

		@SuppressWarnings("null")
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.yimei_jieshou) {
				IssChoose("jieshou");
			}
			if (v.getId() == R.id.yimei_jieshou_tuihui) {
				IssChoose("tuihui");
			}
		}
	};

	/**
	 * 获取选中的数据并且请求服务器
	 * 
	 * @param publicState
	 */
	public void IssChoose(String mes) {
		HashMap<Integer, Boolean> state = jieshouAdapter.Getstate();
		try {
			if (state == null || state.equals(null)) {
				ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
			} else {
				int count = 0;
				for (int j = 0; j < jieshouAdapter.getCount(); j++) {
					if (state.get(j)) {
						if (state.get(j) != null) {
							@SuppressWarnings("unchecked")
							HashMap<String, Object> map = (HashMap<String, Object>) jieshouAdapter
									.getItem(j);
							updateCref3 = (JSONObject) map.get("jieshou_item_title");
							m = new mesPrecord();
							m.setCref3(1);
							m.setSlkid(map.get("slkid").toString());
							m.setMkdate(MyApplication.GetServerNowTime());
							m.setSid1(map.get("sid1").toString());
							m.setZcno1(map.get("zcno1").toString());
							count++;
						}
					}
				}
				switch (count) {
				case 0:
					ToastUtil.showToast(JieShouActivity.this, "请选中一条数据", 0);
					break;
				case 1:
					
					for (int i = 0; i < jieshouAdapter.getCount(); i++) {
						@SuppressWarnings("unchecked")
						Map<String,Object> map = (Map<String, Object>) jieshouAdapter.getItem(i);
						if(mes.equals("jieshou")){
							if(map.get("state").equals("已接收")&&map.get("sid1").equals(m.getSid1())){
	 							ToastUtil.showToast(JieShouActivity.this,"该记录已接收",0);
	 							return;
	 						}else if(map.get("state").equals("待接收")&&map.get("sid1").equals(m.getSid1())){
	 							if(map.get("sid1").equals(m.getSid1())){
		 							map.put("state","已接收");
		 							map.put("time",MyApplication.GetServerNowTime());
		 							jieshouAdapter.notifyDataSetChanged();
		 							
		 							Map<String, String> updateServerTable = MyApplication
											.UpdateServerTableMethod(
													MyApplication.DBID,
													MyApplication.user, "00", "01",
													m.getSid1(),m.getSlkid(),m.getZcno1(),"200");
									OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,
											null,updateServerTable,null, mHander, true,"updateServer_200");
									
									updateCref3.put("ckdate",MyApplication.GetServerNowTime());
		 							updateCref3.put("op_c",op);
		 							updateCref3.put("cref3","1");
		 							updateCref3.put("sbuid","D0031");
		 							updateCref3.put("sys_stated", "2");
		 							Map<String, String> mesIdMap = MyApplication
											.httpMapKeyValueMethod(MyApplication.DBID,
													"savedata", MyApplication.user,
													updateCref3.toJSONString(),
													"D0031WEB", "1");
		 							OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,mesIdMap,
		 									null, mHander, true,"UpdateCref3");
		 						}
	 							return;
	 						}else if(map.get("state").equals("退回")&&map.get("sid1").equals(m.getSid1())){
	 							ToastUtil.showToast(JieShouActivity.this,"该记录已被退回",0);
	 							return;
	 						}
						}
						if(mes.equals("tuihui")){
							if(map.get("state").equals("已接收")&&map.get("sid1").equals(m.getSid1())){
	 							ToastUtil.showToast(JieShouActivity.this,"该记录已被接受！",0);
	 							return;
	 						}else if(map.get("state").equals("待接收")&&map.get("sid1").equals(m.getSid1())){
	 							if(map.get("sid1").equals(m.getSid1())){
		 							map.put("state","退回");
		 							map.put("time",MyApplication.GetServerNowTime());
		 							jieshouAdapter.notifyDataSetChanged();
		 							
		 							updateCref3.put("ckdate",MyApplication.GetServerNowTime());
		 							updateCref3.put("op_c",op);
		 							updateCref3.put("cref3","2");
		 							updateCref3.put("sys_stated", "2");
		 							Map<String, String> mesIdMap = MyApplication
											.httpMapKeyValueMethod(MyApplication.DBID,
													"savedata", MyApplication.user,
													updateCref3.toJSONString(),
													"D0031WEB", "1");
		 							OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,mesIdMap,
		 									null, mHander, true,"UpdateCref3");
		 						}
	 							return;
	 						}else if(map.get("state").equals("退回")&&map.get("sid1").equals(m.getSid1())){
	 							ToastUtil.showToast(JieShouActivity.this,"该记录已被退回",0);
	 							return;
	 						}
						}
					}
					
					break;
				default:
					ToastUtil.showToast(JieShouActivity.this, "不可多选！", 0);
					break;
				}
			}
		} catch (Exception e) {
			ToastUtil.showToast(JieShouActivity.this,e.toString(),0);
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
			if (v.getId() == R.id.yimei_jieshou_user) {
				if (actionId >= 0) {
					if(yimei_jieshou_user.getText().toString().trim().equals("")
							||yimei_jieshou_user.getText().toString().trim()==null){
						ToastUtil.showToast(JieShouActivity.this,"作业员不能为空",0);
						return false;
					}
					op = yimei_jieshou_user.getText().toString().trim()
							.toUpperCase();
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL,
							null,
							MyApplication.QueryBatNo("MSBMOLISTWEB",
									"~zcno1='61' and sbuid='D0030'"), null,
							mHander, true, "QueryRenWu");
					flag = true;
				}
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
						if (string.equals("QueryRenWu")) { // 给下拉框赋值
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
								List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
								for (int i = 0; i < ((JSONArray) jsonObject
										.get("values")).size(); i++) {
									JSONObject jsonObj = (JSONObject) ((JSONArray) jsonObject
											.get("values")).get(i);
									
									Map<String, Object> map = new HashMap<String, Object>();
									map.put("op", jsonObj.get("op"));
									String cref3 = null;
									if (jsonObj.containsKey("cref3")) {
										int state = Integer.parseInt(jsonObj
												.get("cref3").toString());
										switch (state) {
										case 0:
											cref3 = "待接收";
											break;
										case 1:
											cref3 = "已接收";
											break;
										case 2:
											cref3 = "退回";
											break;
										}
									} else {
										cref3 = "待接收";
									}
									map.put("state", cref3);
									map.put("time", jsonObj.containsKey("ckdate")?jsonObj.get("ckdate"):MyApplication.GetServerNowTime());
									map.put("sid1", jsonObj.get("sid1")
											.toString());
									map.put("zcno1", jsonObj.get("zcno1")
											.toString());
									map.put("slkid", jsonObj.get("slkid")
											.toString());
									map.put("prd_name",
											jsonObj.containsKey("prd_name") ? jsonObj
													.get("prd_name") : "");
									map.put("qty", jsonObj.get("qty")
											.toString());
									map.put("jieshou_item_title", jsonObj);
									mList.add(map);
								}
								jieshouAdapter = new JieShouAdapter(
										JieShouActivity.this, mList);
								mListView.setAdapter(jieshouAdapter);
								yimei_jieshou.setEnabled(true);
								yimei_jieshou_tuihui.setEnabled(true);
							}else{
								ToastUtil.showToast(JieShouActivity.this,"没有转序批号！",0);
							}
						}
						if(string.equals("updateServer_200")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(!(Integer.parseInt(jsonObject.get("id").toString()) == 1)){
								ToastUtil.showToast(JieShouActivity.this,"（200）失败",0);
							}
						}
						if(string.equals("UpdateCref3")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(!(Integer.parseInt(jsonObject.get("id").toString()) == 0)){
								ToastUtil.showToast(JieShouActivity.this,"（cref3）失败",0);
							}
						}
					} catch (Exception e) {
						ToastUtil.showToastLocation(JieShouActivity.this,
								e.toString(), 0);
					}
				}
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
