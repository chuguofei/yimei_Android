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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.adapter.ZhiJuLingChuAdapter;
import com.yimei.entity.Pair;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

public class ZhiJuLingChuActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private static ZhiJuLingChuAdapter ZhiJuLingChuAdapter;
	private Spinner selectValue; // 下拉框
	private EditText yimei_zhijulingchu_user, yimei_zhijulingchu_proNum_edt,
			yimei_zhijulingchu_mojuId;
	private String zcno;
	private String prd_no;
	private String mojuId;
	private String zuoyeyuan;
	private JSONObject piciJsonObject;

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
				if (tag.equals("制具领出作业员")) {
					yimei_zhijulingchu_user.setText(barcodeData.toString()
							.toUpperCase());
					if (yimei_zhijulingchu_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_user);
						return;
					}
					zuoyeyuan = yimei_zhijulingchu_user.getText().toString()
							.trim();
					MyApplication.nextEditFocus(yimei_zhijulingchu_proNum_edt);
				}
				if (tag.equals("制具领出批次号")) {
					yimei_zhijulingchu_proNum_edt.setText(barcodeData
							.toString().toUpperCase());
					if (yimei_zhijulingchu_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_user);
						return;
					}
					if (yimei_zhijulingchu_proNum_edt.getText().toString()
							.trim().equals("")
							|| yimei_zhijulingchu_proNum_edt.getText()
									.toString().trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"生产批次不能为空", 0);
						MyApplication
								.nextEditFocus(yimei_zhijulingchu_proNum_edt);
						return;
					}
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZCLISTWEB", "~sid1='"
									+ yimei_zhijulingchu_proNum_edt.getText()
											.toString() + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "picihaoQuery");
				}
				if (tag.equals("制具领出模具编号")) {
					yimei_zhijulingchu_mojuId.setText(barcodeData.toString()
							.toUpperCase());
					if (yimei_zhijulingchu_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_user);
						return;
					}
					if (yimei_zhijulingchu_proNum_edt.getText().toString()
							.trim().equals("")
							|| yimei_zhijulingchu_proNum_edt.getText()
									.toString().trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"生产批次不能为空", 0);
						MyApplication
								.nextEditFocus(yimei_zhijulingchu_proNum_edt);
						return;
					}
					if (yimei_zhijulingchu_mojuId.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_mojuId.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"模具编号不能为空", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_mojuId);
						return;
					}
					mojuId = yimei_zhijulingchu_mojuId.getText().toString()
							.toUpperCase().trim();
					// 模具领出主子表
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZREGISTER", "~prd_no='" + prd_no + "' and id='"
									+ mojuId + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "MOZRegisterQuery");
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhijulingchu);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				MyApplication.QueryBatNo("M_PROCESS", "~sorg='"+MyApplication.sorg+"'"), null, mHander, true,
				"SpinnerValue");

		selectValue = (Spinner) findViewById(R.id.zhijulingchu_selectValue);

		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.zhijulingchu_scroll_title);
		// 添加头滑动事件
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.zhijulingchu_scroll_list);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播

		yimei_zhijulingchu_user = (EditText) findViewById(R.id.yimei_zhijulingchu_user_edt);
		yimei_zhijulingchu_proNum_edt = (EditText) findViewById(R.id.yimei_zhijulingchu_proNum_edt);
		yimei_zhijulingchu_mojuId = (EditText) findViewById(R.id.yimei_zhijulingchu_mojuId);

		yimei_zhijulingchu_user.setOnEditorActionListener(editEnter);
		yimei_zhijulingchu_proNum_edt.setOnEditorActionListener(editEnter);
		yimei_zhijulingchu_mojuId.setOnEditorActionListener(editEnter);

		yimei_zhijulingchu_user.setOnFocusChangeListener(EditGetFocus);
		yimei_zhijulingchu_proNum_edt.setOnFocusChangeListener(EditGetFocus);
		yimei_zhijulingchu_mojuId.setOnFocusChangeListener(EditGetFocus);
	}

	/**
	 * 键盘回车事件
	 */
	OnEditorActionListener editEnter = new OnEditorActionListener() {

		@SuppressLint("DefaultLocale")
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean flag = false;
			if (v.getId() == R.id.yimei_zhijulingchu_user_edt) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_zhijulingchu_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"作业员不能为空!", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_user);
						return false;
					}
					zuoyeyuan = yimei_zhijulingchu_user.getText().toString()
							.trim();
					MyApplication.nextEditFocus(yimei_zhijulingchu_proNum_edt);
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_zhijulingchu_proNum_edt) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_zhijulingchu_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"作业员不能为空!", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_user);
						return false;
					}
					if (yimei_zhijulingchu_proNum_edt.getText().toString()
							.trim().equals("")
							|| yimei_zhijulingchu_proNum_edt.getText()
									.toString().trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"生产批次不能为空!", 0);
						MyApplication
								.nextEditFocus(yimei_zhijulingchu_proNum_edt);
						return false;
					}
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZCLISTWEB", "~sid1='"
									+ yimei_zhijulingchu_proNum_edt.getText()
											.toString() + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "picihaoQuery");
				}
			}
			if (v.getId() == R.id.yimei_zhijulingchu_mojuId) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_zhijulingchu_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"作业员不能为空!", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_user);
						return false;
					}
					if (yimei_zhijulingchu_proNum_edt.getText().toString()
							.trim().equals("")
							|| yimei_zhijulingchu_proNum_edt.getText()
									.toString().trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"生产批次不能为空!", 0);
						MyApplication
								.nextEditFocus(yimei_zhijulingchu_proNum_edt);
						return false;
					}
					if (yimei_zhijulingchu_mojuId.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_mojuId.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"模具编号不能为空!", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_mojuId);
						return false;
					}
					mojuId = yimei_zhijulingchu_mojuId.getText().toString()
							.toUpperCase().trim();
					// 模具领出主子表
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZREGISTER", "~prd_no='" + prd_no + "' and id='"
									+ mojuId + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "MOZRegisterQuery");
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
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_zhijulingchu_user_edt) {
				if (!hasFocus) {
					zuoyeyuan = yimei_zhijulingchu_user.getText().toString()
							.trim();
				} else {
					yimei_zhijulingchu_user.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_zhijulingchu_proNum_edt) {
				if (hasFocus) {
					yimei_zhijulingchu_proNum_edt.setSelectAllOnFocus(true);
				} else {
					
				}
			}
			if (v.getId() == R.id.yimei_zhijulingchu_mojuId) {
				if (hasFocus) {
					yimei_zhijulingchu_mojuId.setSelectAllOnFocus(true);
				}
			}
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
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(),
										"没有查到制程号~", 0);
								return;
							} else {
								SetSelectValue(jsonObject);
								System.out.println(jsonObject);
							}
						}
						if (string.equals("picihaoQuery")) { // 批次号查询
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								MyApplication
										.nextEditFocus(yimei_zhijulingchu_mojuId);
								return;
							} else {
								JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
										.get("values")).get(0));
								prd_no = jsonValue.get("prd_no").toString();
								piciJsonObject = jsonValue;
								MyApplication
										.nextEditFocus(yimei_zhijulingchu_mojuId);
							}
						}
						if (string.equals("MOZRegisterQuery")) { // 模具登记查询（prd_no+id）
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(ZhiJuLingChuActivity.this,
										"该治具不能在当前设备使用", 0);
								yimei_zhijulingchu_mojuId.selectAll();
								return;
							} else {
								Map<String, String> map = MyApplication
										.QueryBatNo("MOZREGISTER", "~prd_no='"
												+ prd_no + "' and id='"
												+ mojuId + "'");
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, map, null,
										mHander, true, "MOZzt1Andzt2Querey");
							}
							System.out.println(jsonObject);
						}
						if (string.equals("MOZzt1Andzt2Querey")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
									.get("values")).get(0));
							if (jsonValue.get("zt2").toString().equals("1")) {
								ToastUtil.showToast(ZhiJuLingChuActivity.this,
										"该制具已被使用~", 0);
								yimei_zhijulingchu_mojuId.selectAll();
								return;
							} else if (jsonValue.get("zt1").toString()
									.equals("0")) {
								ToastUtil.showToast(ZhiJuLingChuActivity.this,
										"请先清洗制具~", 0);
								yimei_zhijulingchu_mojuId.selectAll();
								return;
							} else {
								// 400请求 （修改zct）
								Map<String, String> updateServerMoJu = MyApplication
										.updateServerMoJu(MyApplication.DBID,
												MyApplication.user, "1",
												mojuId, "400");
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null,
										updateServerMoJu, null, mHander, true,
										"UpdateServerZct");

								if (piciJsonObject != null) {

									piciJsonObject.put("op",
											zuoyeyuan.toUpperCase());
									piciJsonObject.put("mkdate",
											MyApplication.GetServerNowTime());
									piciJsonObject.put("dcid",
											GetAndroidMacUtil.getMac());
									piciJsonObject.put("sbuid", "E5005");
									piciJsonObject.put("smake",
											MyApplication.user);
									piciJsonObject.put("zcno", zcno);
									piciJsonObject.put("sys_stated", "3");
									// 添加数据到清洗的表中
									Map<String, String> addServerQingXiData = MyApplication
											.httpMapKeyValueMethod(
													MyApplication.DBID,
													"savedata",
													MyApplication.user,
													piciJsonObject.toJSONString(),
													"E5005", "1");
									OkHttpUtils.getInstance().getServerExecute(
											MyApplication.MESURL, null,
											addServerQingXiData, null, mHander,
											true, "addServerLingChuData");

									List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
									// 给适配器添加数据
									Map<String, String> map = new HashMap<String, String>();
									map.put("sid1", piciJsonObject.get("sid1")
											.toString());
									map.put("sbid", mojuId);
									map.put("slkid", piciJsonObject
											.getString("slkid") == null ? ""
											: jsonValue.getString("slkid"));
									map.put("prd_no",
											piciJsonObject.getString("prd_no"));
									map.put("mkdate",
											MyApplication.GetServerNowTime());
									mList.add(map);
									if (ZhiJuLingChuAdapter == null) {
										ZhiJuLingChuAdapter = new ZhiJuLingChuAdapter(
												ZhiJuLingChuActivity.this,
												mList);
										mListView
												.setAdapter(ZhiJuLingChuAdapter);
									} else {
										ZhiJuLingChuAdapter.listData.add(map);
										mListView
												.setAdapter(ZhiJuLingChuAdapter);
										ZhiJuLingChuAdapter
												.notifyDataSetChanged();
									}
									yimei_zhijulingchu_mojuId
											.setSelectAllOnFocus(true);
								}
							}
						}
						if (string.equals("UpdateServerZct")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id")
									.toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(),
										"400请求错误!", 0);
							}
						}
						if (string.equals("addServerLingChuData")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id")
									.toString()) != 0) {
								ToastUtil.showToast(getApplicationContext(),
										"（savadate）添加失败!", 0);
							}
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
				ZhiJuLingChuActivity.this,
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
