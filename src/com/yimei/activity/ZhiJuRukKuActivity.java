package com.yimei.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.adapter.ZhiJuRuKuAdapter;
import com.yimei.entity.Pair;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;

public class ZhiJuRukKuActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private static ZhiJuRuKuAdapter ZhiJuRuKuAdapter;
	private EditText yimei_zhijuruku_user_edt, yimei_zhijuruku_mojuId;
	private Spinner selectValue; // 下拉框
	private String mojuId;
	private String zcno;
	private String zuoyeyuan;

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
				if(tag.equals("制具入库作业员")){
					yimei_zhijuruku_user_edt.setText(barcodeData.toString().toUpperCase());
					if (yimei_zhijuruku_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_zhijuruku_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuRukKuActivity.this, "作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_zhijuruku_user_edt);
						return;
					}
					zuoyeyuan = yimei_zhijuruku_user_edt.getText().toString().trim();
					MyApplication.nextEditFocus(yimei_zhijuruku_mojuId);
				}
				if(tag.equals("制具入库模具编号")){
					yimei_zhijuruku_mojuId.setText(barcodeData.toString().toUpperCase());
					if (yimei_zhijuruku_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_zhijuruku_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuRukKuActivity.this, "作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_zhijuruku_user_edt);
						return;
					}	
					if (yimei_zhijuruku_mojuId.getText().toString().trim()
							.equals("")
							|| yimei_zhijuruku_mojuId.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuRukKuActivity.this, "模具编码不能为空",0);
						MyApplication.nextEditFocus(yimei_zhijuruku_mojuId);
						return;
					}
					mojuId = yimei_zhijuruku_mojuId.getText().toString()
							.toUpperCase().trim();
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZREGISTER", "~ zc_id='" + zcno + "'and id='"
									+ mojuId + "'");
					OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null, map, null, mHander,
							true, "MoJuIdQuery"); // 模具清洗查询编号
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhijuruku);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				MyApplication.QueryBatNo("M_PROCESS","~sorg='"+MyApplication.sorg+"'"), null, mHander, true,
				"SpinnerValue");
		selectValue = (Spinner) findViewById(R.id.zhijuruku_selectValue);
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.zhijuruku_scroll_title);
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.zhijuruku_scroll_list);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播

		yimei_zhijuruku_user_edt = (EditText) findViewById(R.id.yimei_zhijuruku_user_edt);
		yimei_zhijuruku_mojuId = (EditText) findViewById(R.id.yimei_zhijuruku_mojuId);

		yimei_zhijuruku_user_edt.setOnEditorActionListener(editEnter);
		yimei_zhijuruku_mojuId.setOnEditorActionListener(editEnter);
		
		yimei_zhijuruku_user_edt.setOnFocusChangeListener(EditGetFocus);
		yimei_zhijuruku_mojuId.setOnFocusChangeListener(EditGetFocus);
	}
	
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
			if (v.getId() == R.id.yimei_zhijuruku_user_edt) {
				if (!hasFocus) {
					zuoyeyuan = yimei_zhijuruku_user_edt.getText().toString()
							.trim().toUpperCase();
				} else {
					yimei_zhijuruku_user_edt.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_zhijuruku_mojuId) {
				if (hasFocus) {
					yimei_zhijuruku_mojuId.setSelectAllOnFocus(true);
				} else {
					mojuId = yimei_zhijuruku_mojuId.getText().toString()
							.trim().toUpperCase();
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
			if (v.getId() == R.id.yimei_zhijuruku_user_edt) {
				if (actionId >= 0) {
					if (yimei_zhijuruku_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_zhijuruku_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuRukKuActivity.this, "作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_zhijuruku_user_edt);
						return false;
					}
					zuoyeyuan = yimei_zhijuruku_user_edt.getText().toString()
							.trim();
					MyApplication.nextEditFocus(yimei_zhijuruku_mojuId);
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_zhijuruku_mojuId) {
				if (actionId >= 0) {
					if (yimei_zhijuruku_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_zhijuruku_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuRukKuActivity.this, "作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_zhijuruku_mojuId);
						return false;
					}
					if (yimei_zhijuruku_mojuId.getText().toString().trim()
							.equals("")
							|| yimei_zhijuruku_mojuId.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuRukKuActivity.this,
								"模具编号不能为空", 0);
						MyApplication.nextEditFocus(yimei_zhijuruku_mojuId);
						return false;
					}
					mojuId = yimei_zhijuruku_mojuId.getText().toString()
							.toUpperCase().trim();
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZREGISTER", "~ zc_id='" + zcno + "'and id='"
									+ mojuId + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "MoJuIdQuery"); // 模具清洗查询编号
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
						if (string.equals("SpinnerValue")) { // 给下拉框赋值
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							SetSelectValue(jsonObject);
							System.out.println(jsonObject);
						}
						if (string.equals("MoJuIdQuery")) { // 模具编号回车
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.getString("code")
									.toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(),
										"没有该模具编号或制程！", 0);

								if (mListView != null) {
									mListView.setAdapter(null);
									if (ZhiJuRuKuAdapter != null) {
										ZhiJuRuKuAdapter.notifyDataSetChanged();
									}
								}
								yimei_zhijuruku_mojuId.selectAll();
								return;
							} else {
								JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
										.get("values")).get(0));
								if(jsonValue.get("zt1").toString().equals("0")){
									ToastUtil.showToast(getApplicationContext(), "请先清洗治具!",0);
									return;
								}else if(jsonValue.get("zt2").toString().equals("0")) {
									ToastUtil.showToast(getApplicationContext(), "该模具已在库！",0);
									return;
								} else {
									jsonValue.put("op", zuoyeyuan);
									jsonValue.put("sorg",MyApplication.sorg);
									jsonValue.put("zcno","S03");
									jsonValue.put("mkdate",
											MyApplication.GetServerNowTime());
									jsonValue.put("dcid",
											GetAndroidMacUtil.getMac());
									jsonValue.put("sbuid", "E5004");
									jsonValue.put("smake", MyApplication.user);
									jsonValue.put("zcno", zcno);
									jsonValue.put("sys_stated", "3");
									// 添加数据到清洗的表中
									Map<String, String> addServerQingXiData = MyApplication
											.httpMapKeyValueMethod(
													MyApplication.DBID,
													"savedata",
													MyApplication.user,
													jsonValue.toJSONString(),
													"E5004", "1");
									OkHttpUtils.getInstance().getServerExecute(
											MyApplication.MESURL, null,
											addServerQingXiData, null, mHander,
											true, "addServerChuKuData");

									// 400请求 （修改zct）
									Map<String, String> updateServerMoJuZct = MyApplication
											.updateServerMoJu(
													MyApplication.DBID,
													MyApplication.user, "0",
													mojuId, "400");
									OkHttpUtils.getInstance().getServerExecute(
											MyApplication.MESURL, null,
											updateServerMoJuZct, null, mHander,
											true, "updateServerMoJuZct");

									// 给适配器添加数据
									List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
									Map<String, String> map = new HashMap<String, String>();
									map.put("op", zuoyeyuan == null ? ""
											: zuoyeyuan);
									map.put("sbid", mojuId == null ? ""
											: mojuId);
									map.put("mkdate",
											MyApplication.GetServerNowTime());
									mList.add(map);
									if (ZhiJuRuKuAdapter == null) {
										ZhiJuRuKuAdapter = new ZhiJuRuKuAdapter(
												ZhiJuRukKuActivity.this, mList);
										mListView.setAdapter(ZhiJuRuKuAdapter);
									} else {
										ZhiJuRuKuAdapter.listData.add(map);
										mListView.setAdapter(ZhiJuRuKuAdapter);
										ZhiJuRuKuAdapter.notifyDataSetChanged();
									}
								}
							}
							System.out.println(jsonObject);
						}
						if (string.equals("addServerChuKuData")) { // 添加数据
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							System.out.println(jsonObject);
						}
						if (string.equals("updateServerMoJuZct")) { // 400请求
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.getString("id")) == -1) {
								ToastUtil.showToast(ZhiJuRukKuActivity.this,
										"400请求错误!", 0);
							}
							System.out.println(jsonObject);
						}
					} catch (Exception e) {
						ToastUtil.showToastLocation(getApplicationContext(),
								e.toString(), 0);
					}
				}
			}
		}
	};

	/**
	 * 给下拉框赋值
	 * 
	 * @param hScrollView
	 */
	private void SetSelectValue(JSONObject jsonObject) {
		List<Pair> dicts = new ArrayList<Pair>();
		for (int i = 0; i < ((JSONArray) jsonObject.get("values")).size(); i++) {
			JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
					.get("values")).get(i));
			dicts.add(new Pair(jsonValue.getString("name").toString(),
					jsonValue.getString("id").toString()));
		}
		ArrayAdapter<Pair> adapter = new ArrayAdapter<Pair>(
				ZhiJuRukKuActivity.this, android.R.layout.simple_spinner_item,
				dicts);
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
