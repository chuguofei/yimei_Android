package com.yimei.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.adapter.BianDaiAdapter;
import com.yimei.entity.mesPrecord;
import com.yimei.scrollview.BianDaiCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.HttpUtil;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;

public class BianDaiActivity extends Activity {

	static MyApplication myapp;
	public static BianDaiActivity biandaiActivity;
	public HorizontalScrollView mTouchView;
	private static List<BianDaiCHScrollView> BianDaiScrollViews = new ArrayList<BianDaiCHScrollView>();
	private static ListView mListView;
	private static BianDaiAdapter BianDaiAdapter; // 适配器
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

	private EditText yimei_biandai_user_edt;
	private EditText yimei_biandai_sbid_edt;
	private EditText yimei_biandai_proNum_edt;

	private String zuoyeyuan;
	private String shebeihao;
	private String lot_no;

	private String sid1;
	private String currSlkid;
	private String qtyv;
	private String lot_no1;
	private CheckBox quanxuan; // 全选按钮
	private Button biandai_kaigong;// 开工按钮
	private Button biandai_chuzhan;// 出站按钮
	private List<mesPrecord> updateListState; // 修改服务器的2张表的状态（出站，开工）,更改本地库的状态
	private List<Map<String, Object>> biandaiPrdNocomparison; //
	private static JSONObject newJson; // 拿新sid存在json

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		setContentView(R.layout.activity_biandai);
		Application app = getApplication();
		myapp = (MyApplication) app;
		myapp.addActivity_(this);
		biandaiActivity = this;
		myapp.removeActivity_(LoginActivity.loginActivity);// 销毁登录
		quanxuan = (CheckBox) findViewById(R.id.biandai_quanxuan); // 全选按钮
		listenerQuanXuan();
		biandai_kaigong = (Button) findViewById(R.id.biandai_kaigong); // 获取开工id
		biandai_chuzhan = (Button) findViewById(R.id.biandai_chuzhan); // 获取开工id
		biandai_kaigong.setOnClickListener(kaigongClick); // 开工点击事件
		biandai_chuzhan.setOnClickListener(chuzhanClick); // 出站点击事件
		if (mListView == null) {
			biandai_kaigong.setEnabled(false);
			biandai_chuzhan.setEnabled(false);
		} else {
			biandai_kaigong.setEnabled(true);
			biandai_chuzhan.setEnabled(true);
		}
	}

	/**
	 * 获取pda扫描（广播）
	 */
	private BroadcastReceiver barcodeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (MyApplication.INTENT_ACTION_SCAN_RESULT.equals(intent
					.getAction())) {
				View rootview = getCurrentFocus();
				int focusId = rootview.findFocus().getId();
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
				if (tag.equals("编带作业员")) { // 作业员
					yimei_biandai_user_edt.setText(barcodeData);
					if (yimei_biandai_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(getApplication(), "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_biandai_user_edt);
						return;
					}
					MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
				}
				if (tag.equals("编带设备号")) { // 设备号
					Log.i("id", "设备");
					yimei_biandai_sbid_edt.setText(barcodeData);
					if (yimei_biandai_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(getApplication(), "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_biandai_user_edt);
						return;
					}
					if (yimei_biandai_sbid_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_sbid_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(getApplication(), "设备号不能为空", 0);
						MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
						return;
					}
					shebeihao = yimei_biandai_user_edt.getText().toString()
							.trim();
					Map<String, String> IsSbidQuery = MyApplication
							.IsSbidQuery(shebeihao, MyApplication.BIANDAI_ZCNO);
					httpRequestQueryRecord(MyApplication.MESURL, IsSbidQuery,
							"IsSbidQuery");
					MyApplication.nextEditFocus(yimei_biandai_proNum_edt);
				}
				if (tag.equals("编带批次号")) { // 批次号
					Log.i("id", "批号");
					yimei_biandai_proNum_edt.setText(barcodeData);
					if (yimei_biandai_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(getApplication(), "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_biandai_user_edt);
						return;
					}
					if (yimei_biandai_sbid_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_sbid_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(getApplication(), "设备号不能为空", 0);
						MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
						return;
					}
					if (yimei_biandai_proNum_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_proNum_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(getApplication(), "批次号为空~", 0);
						MyApplication.nextEditFocus(yimei_biandai_proNum_edt);
						Log.i("foucus", "批次号回车");
						return;
					}
					lot_no = yimei_biandai_proNum_edt.getText().toString()
							.trim();
					Map<String, String> IsSbidQuery = MyApplication
							.IsSbidQuery(shebeihao, MyApplication.BIANDAI_ZCNO);
					httpRequestQueryRecord(MyApplication.MESURL, IsSbidQuery,
							"IsSbidQuery1");
					yimei_biandai_proNum_edt.selectAll();

				}
			}
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		yimei_biandai_user_edt = (EditText) findViewById(R.id.yimei_biandai_user_edt);
		yimei_biandai_sbid_edt = (EditText) findViewById(R.id.yimei_biandai_sbid_edt);
		yimei_biandai_proNum_edt = (EditText) findViewById(R.id.yimei_biandai_proNum_edt);

		yimei_biandai_user_edt.setOnEditorActionListener(editEnter);
		yimei_biandai_sbid_edt.setOnEditorActionListener(editEnter);
		yimei_biandai_proNum_edt.setOnEditorActionListener(editEnter);

		yimei_biandai_user_edt.setOnFocusChangeListener(EditGetFocus);
		yimei_biandai_sbid_edt.setOnFocusChangeListener(EditGetFocus);
		yimei_biandai_proNum_edt.setOnFocusChangeListener(EditGetFocus);

	}

	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_biandai_user_edt) {
				if (!hasFocus) {
					zuoyeyuan = yimei_biandai_user_edt.getText().toString()
							.trim();
				} else {
					yimei_biandai_user_edt.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_biandai_sbid_edt) {
				if (hasFocus) {
					Log.i("foucus", "设备号获取焦点");
					yimei_biandai_sbid_edt.setSelectAllOnFocus(true);
				} else {
					shebeihao = yimei_biandai_sbid_edt.getText().toString()
							.toUpperCase().trim(); // 失去焦点给设备号赋值;
					yimei_biandai_sbid_edt.setText(shebeihao);
				}
			}
			if (v.getId() == R.id.yimei_biandai_proNum_edt) {
				if (hasFocus) {
					yimei_biandai_proNum_edt.setSelectAllOnFocus(true);
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

	/**
	 * 开工的点击事件
	 */
	OnClickListener kaigongClick = new OnClickListener() {

		@SuppressWarnings("null")
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.biandai_kaigong) {
				UpdateServerData("kaigongUpdata");
			}
		}
	};

	/**
	 * 点击出站事件
	 */
	OnClickListener chuzhanClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.biandai_chuzhan) {
				UpdateServerData("chuzhanUpdata");
			}
		}
	};

	/**
	 * 获取选中的数据并且请求服务器
	 * 
	 * @param publicState
	 */
	public void UpdateServerData(String publicState) {
		HashMap<Integer, Boolean> state = BianDaiAdapter.Getstate();

		if (state == null || state.equals(null)) {
			ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
		} else {
			int count = 0;
			for (int j = 0; j < BianDaiAdapter.getCount(); j++) {
				if (state.get(j)) {
					if (state.get(j) != null) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> map = (HashMap<String, Object>) BianDaiAdapter
								.getItem(j);

						if (updateListState == null) {
							updateListState = new ArrayList<mesPrecord>();
						}
						mesPrecord m = (mesPrecord) map
								.get("biandai_item_title");
						updateListState.add(m);
						count++;
					}
				}
			}
			switch (count) {
			case 0:
				ToastUtil.showToast(getApplicationContext(), "请选中一条数据", 0);
				break;
			default:
				for (int i = 0; i < updateListState.size(); i++) {
					mesPrecord mes_precord = updateListState.get(i);
					JSONObject json = (JSONObject) JSON.toJSON(mes_precord);
					// 如果是入站状态改变
					if (publicState.equals("kaigongUpdata")) {
						// 如果状态是入站和上料都可以开工
						if (json.get("state1").toString().equals("01")) {
							Map<String, String> updateTimeMethod = MyApplication
									.updateServerTimeMethod(MyApplication.DBID,
											MyApplication.user, "01", "03",
											mes_precord.getSid(), zuoyeyuan,
											MyApplication.BIANDAI_ZCNO, "202");

							httpRequestQueryRecord(MyApplication.MESURL,
									updateTimeMethod, publicState);
						} else if (json.get("state1").toString().equals("02")) {
							Map<String, String> updateTimeMethod = MyApplication
									.updateServerTimeMethod(MyApplication.DBID,
											MyApplication.user, "02", "03",
											mes_precord.getSid(), zuoyeyuan,
											MyApplication.BIANDAI_ZCNO, "202");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateTimeMethod, publicState);
						} else if (json.get("state1").toString().equals("03")) {
							ToastUtil.showToast(getApplicationContext(),
									"选中的批次已是开工状态！", 0);
						}
					} else if (publicState.equals("chuzhanUpdata")) {
						if (json.get("state1").toString().equals("03")) {
							Map<String, String> updateTimeMethod = MyApplication
									.updateServerTimeMethod(MyApplication.DBID,
											MyApplication.user, "03", "04",
											mes_precord.getSid(), zuoyeyuan,
											MyApplication.BIANDAI_ZCNO, "202");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateTimeMethod, publicState);
						} else if (json.get("state1").toString().equals("02")) {
							ToastUtil.showToast(getApplicationContext(),
									"选中的批次不能出站！", 0);
						} else if (json.get("state1").toString().equals("01")) {
							ToastUtil.showToast(getApplicationContext(),
									"选中的批次不能出站！", 0);
						}
					}

				}
				break;
			}
		}
	}

	/**
	 * 全选方法
	 */
	protected void listenerQuanXuan() {
		quanxuan.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (BianDaiAdapter == null) {
					ToastUtil.showToast(getApplicationContext(), "没有数据", 0);
					quanxuan.setEnabled(false);
					return;
				}
				if (isChecked) {
					int initCheck = BianDaiAdapter.initCheck(true);
					if (initCheck == -1) {
						ToastUtil.showToast(getApplicationContext(), "没有数据", 0);
						quanxuan.setEnabled(false);
					}
					BianDaiAdapter.notifyDataSetChanged();
				} else {
					int initCheck = BianDaiAdapter.initCheck(false);
					if (initCheck == -1) {
						ToastUtil.showToast(getApplicationContext(), "没有数据", 0);
						quanxuan.setEnabled(false);
					}
					BianDaiAdapter.notifyDataSetChanged();
				}
			}
		});

	}

	/**
	 * 回车事件
	 */
	OnEditorActionListener editEnter = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean flag = false;
			if (v.getId() == R.id.yimei_biandai_user_edt) { // 作业员
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_biandai_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(biandaiActivity, "作业员不能为空", 0);
						return false;
					}
					zuoyeyuan = yimei_biandai_user_edt.getText().toString()
							.trim();
					MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_biandai_sbid_edt) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					shebeihao = yimei_biandai_sbid_edt.getText().toString()
							.toUpperCase().trim();
					yimei_biandai_sbid_edt.setText(shebeihao);
					if (yimei_biandai_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(biandaiActivity, "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_biandai_user_edt);
						return false;
					}
					if (yimei_biandai_sbid_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_sbid_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(biandaiActivity, "设备号不能为空", 0);
						MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
						return false;
					}
					Log.i("sbid", "设备号回车");
					Map<String, String> IsSbidQuery = MyApplication
							.IsSbidQuery(shebeihao, MyApplication.BIANDAI_ZCNO);
					httpRequestQueryRecord(MyApplication.MESURL, IsSbidQuery,
							"IsSbidQuery");
					MyApplication.nextEditFocus(yimei_biandai_proNum_edt);
					flag = true;
				}
			}
			// 生产批号的回车事件
			if (v.getId() == R.id.yimei_biandai_proNum_edt) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					lot_no = yimei_biandai_proNum_edt.getText().toString()
							.trim();
					if (yimei_biandai_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(biandaiActivity, "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_biandai_user_edt);
						return false;
					}
					if (yimei_biandai_sbid_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_sbid_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(biandaiActivity, "设备号不能为空", 0);
						MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
						return false;
					}
					if (yimei_biandai_proNum_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_proNum_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(biandaiActivity, "批次号为空~", 0);
						MyApplication.nextEditFocus(yimei_biandai_proNum_edt);
						Log.i("foucus", "批次号回车");
						return false;
					}
					/*
					 * // 查询lot表是否存在批次 Map<String, String> map = new
					 * HashMap<String, String>(); map.put("dbid",
					 * MyApplication.DBID); map.put("usercode",
					 * MyApplication.user); map.put("apiId", "assist");
					 * map.put("assistid", "{TESTLOTQUERY}"); map.put("cont",
					 * "~lotno='" + lot_no + "'");
					 * httpRequestQueryRecord(MyApplication.MESURL, map,
					 * "ListViewIsLotNo");
					 */
					Log.i("sbid", "批次号回车");
					Map<String, String> IsSbidQuery = MyApplication
							.IsSbidQuery(shebeihao, MyApplication.BIANDAI_ZCNO);
					httpRequestQueryRecord(MyApplication.MESURL, IsSbidQuery,
							"IsSbidQuery1");
					MyApplication.nextEditFocus(yimei_biandai_proNum_edt);
					flag = true;
				}
			}
			return flag;
		}
	};

	/**
	 * 接收http请求返回值
	 */
	@SuppressLint("HandlerLeak")
	final android.os.Handler handler = new android.os.Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle b = msg.getData();
			String string = b.getString("type");
			if (string.equals("IsSbidQuery")) {
				JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
						.toString());
				if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { // 没有该设备号
					Log.i("code", jsonObject.get("code").toString());
					if (mListView != null) {
						mListView.setAdapter(null);
						BianDaiAdapter.notifyDataSetChanged();
						ToastUtil.showToast(getApplicationContext(), "没有该设备号!",
								0);
						MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
					} else {
						ToastUtil.showToast(getApplicationContext(), "没有该设备号!",
								0);
						MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
					}
				} else {
					// 去服务器中拿设备号
					Map<String, String> map = new HashMap<String, String>();
					map.put("dbid", MyApplication.DBID);
					map.put("usercode", MyApplication.user);
					map.put("apiId", "assist");
					map.put("assistid", "{MSBMOLIST}");
					map.put("cont", "~sbid='" + shebeihao + "' and zcno='"
							+ MyApplication.BIANDAI_ZCNO + "'");
					httpRequestQueryRecord(MyApplication.MESURL, map,
							"shebeiQuery");
				}
			}
			if (string.equals("IsSbidQuery1")) {
				JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
						.toString());

				if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { // 没有该设备号
					Log.i("code", jsonObject.get("code").toString());
					if (mListView != null) {
						mListView.setAdapter(null);
						BianDaiAdapter.notifyDataSetChanged();
						ToastUtil.showToast(getApplicationContext(), "没有该设备号!",
								0);
						MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
					} else {
						ToastUtil.showToast(getApplicationContext(), "没有该设备号!",
								0);
						MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
					}
					return;
				} else {
					// 查询lot表是否存在批次
					Map<String, String> map = new HashMap<String, String>();
					map.put("dbid", MyApplication.DBID);
					map.put("usercode", MyApplication.user);
					map.put("apiId", "assist");
					map.put("assistid", "{TESTLOTQUERY}");
					map.put("cont", "~lotno='" + lot_no + "'");
					httpRequestQueryRecord(MyApplication.MESURL, map,
							"ListViewIsLotNo");
				}
			}
			if ("shebeiQuery".equals(string)) {
				JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
						.toString());
				// 判断设备号+制程在服务器中是否有数据
				if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {

					List<Map<String, Object>> mesList = QueryList(jsonObject); // 刷新列表
					biandaiPrdNocomparison = mesList;
					BianDaiAdapter = new BianDaiAdapter(BianDaiActivity.this,
							mesList);
					mListView.setAdapter(BianDaiAdapter);
					ToastUtil.showToast(getApplicationContext(), "《"
							+ shebeihao + "》设备号已加载到列表中", 0);
					if (mListView == null) {
						biandai_kaigong.setEnabled(false);
						biandai_chuzhan.setEnabled(false);
					} else {
						biandai_kaigong.setEnabled(true);
						biandai_chuzhan.setEnabled(true);
					}
				} else {
					// 清空列表
					// ToastUtil.showToast(getApplicationContext(), "该设备没有记录",
					// 0);
					if (mListView != null) {
						biandai_kaigong.setEnabled(true);
						biandai_chuzhan.setEnabled(true);
						mListView.setAdapter(null);
						mListView = null;
						BianDaiAdapter.notifyDataSetChanged();
						MyApplication.nextEditFocus(yimei_biandai_proNum_edt);
					} else {
						biandai_kaigong.setEnabled(false);
						biandai_chuzhan.setEnabled(false);
					}
				}
			}
			if ("shebeiQuery1".equals(string)) {
				JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
						.toString());
				// 判断设备号+制程在服务器中是否有数据
				if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {

					List<Map<String, Object>> mesList = QueryList(jsonObject); // 刷新列表
					BianDaiAdapter = new BianDaiAdapter(BianDaiActivity.this,
							mesList);
					mListView.setAdapter(BianDaiAdapter);
					ToastUtil.showToast(getApplicationContext(), "《" + lot_no
							+ "》测试号加载到列表中~", 0);
					if (mListView == null) {
						biandai_kaigong.setEnabled(false);
						biandai_chuzhan.setEnabled(false);
					} else {
						biandai_kaigong.setEnabled(true);
						biandai_chuzhan.setEnabled(true);
					}
				} else {
					// 清空列表
					// ToastUtil.showToast(getApplicationContext(),
					// "该设备没有记录111",0);
					if (mListView != null) {
						mListView.setAdapter(null);
						mListView = null;
						BianDaiAdapter.notifyDataSetChanged();
						MyApplication.nextEditFocus(yimei_biandai_proNum_edt);
					}
					if (mListView == null) {
						biandai_kaigong.setEnabled(false);
						biandai_chuzhan.setEnabled(false);
					} else {
						biandai_kaigong.setEnabled(true);
						biandai_chuzhan.setEnabled(true);
					}
				}
			}
			if ("updateRefresh".equals(string)) {
				JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
						.toString());
				// 判断设备号+制程在服务器中是否有数据
				if (Integer.parseInt(jsonObject.get("code").toString()) == 1
						|| Integer.parseInt(jsonObject.get("code").toString()) == 0) {

					List<Map<String, Object>> mesList = QueryList(jsonObject); // 刷新列表
					if (mesList != null) {
						BianDaiAdapter = new BianDaiAdapter(
								BianDaiActivity.this, mesList);
						mListView.setAdapter(BianDaiAdapter);
						BianDaiAdapter.notifyDataSetChanged();
						return;
					} else {
						mListView.setAdapter(null);
						BianDaiAdapter.notifyDataSetChanged();
					}

				}
			}
			if ("ListViewIsLotNo".equals(string)) {
				JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
						.toString());
				if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
					// 如果有批号
					if (mListView != null) {
						// 循环列表
						int count = 0;
						List<Map<String, Object>> ListMesPitch = BianDaiAdapter.listData;
						for (int i = 0; i < ListMesPitch.size(); i++) {
							Map<String, Object> map = ListMesPitch.get(i);
							if (map.get("lotno").equals(lot_no)) {
								count++;
								BianDaiAdapter.state.put(i, true);
							}
						}
						HashMap<Integer, Boolean> a = BianDaiAdapter.Getstate();
						if (count > 0) {
							BianDaiAdapter.notifyDataSetChanged();
							ToastUtil.showToast(getApplicationContext(), "《"
									+ lot_no + "》测试号存在,已经帮你选中", 0);
						} else {
							// 去服务器拿
							// 查询制程和批次是否存在
							Map<String, String> map = new HashMap<String, String>();
							map.put("dbid", MyApplication.DBID);
							map.put("usercode", MyApplication.user);
							map.put("apiId", "assist");
							map.put("assistid", "{MSBMOLIST}");
							map.put("cont", "~lotno='" + lot_no
									+ "' and zcno='"
									+ MyApplication.BIANDAI_ZCNO + "'");
							httpRequestQueryRecord(MyApplication.MESURL, map,
									"ServerIsZcnoAndLotNo");
						}
					} else {
						// 去服务器拿
						// 查询制程和批次是否存在
						Map<String, String> map = new HashMap<String, String>();
						map.put("dbid", MyApplication.DBID);
						map.put("usercode", MyApplication.user);
						map.put("apiId", "assist");
						map.put("assistid", "{MSBMOLIST}");
						map.put("cont", "~lotno='" + lot_no + "' and zcno='"
								+ MyApplication.BIANDAI_ZCNO + "'");
						httpRequestQueryRecord(MyApplication.MESURL, map,
								"ServerIsZcnoAndLotNo");
					}
				} else {
					// 提示没有该批号
					ToastUtil.showToast(getApplication(), "没有该批次号！", 0);
					MyApplication.nextEditFocus(yimei_biandai_proNum_edt);
				}
			}
			if ("ServerIsZcnoAndLotNo".equals(string)) {
				JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
						.toString());
				// 如果有设备号 就绑定过 （提示：已经绑定过设备）
				if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
					ToastUtil.showToast(getApplication(), "《" + lot_no
							+ "》测试号已绑定过设备号！", 0);
				} else {
					// （json） 拿数据
					Map<String, String> map = new HashMap<String, String>();
					map.put("dbid", MyApplication.DBID);
					map.put("usercode", MyApplication.user);
					map.put("apiId", "assist");
					map.put("assistid", "{TESTLOTQUERY}");
					map.put("cont", "~lotno='" + lot_no + "'");
					httpRequestQueryRecord(MyApplication.MESURL, map, "json");
				}
			}
			if ("json".equals(string)) {
				JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
						.toString());
				// 判断设备号+制程在服务器中是否有数据
				if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
					JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
							.get("values")).get(0));

					if (biandaiPrdNocomparison != null) {
						for (int i = 0; i < biandaiPrdNocomparison.size(); i++) {
							Map<String, Object> map = biandaiPrdNocomparison
									.get(i);
							mesPrecord mespre = (mesPrecord) map
									.get("biandai_item_title");
							if (mListView != null) {
								if (mespre.getPrd_no() != jsonValue
										.get("prd_no")) {
									showNormalDialog("扫入的测试批次机型与设备当前批次的机型不符！\n不能入站，请先将当前批次出站后再入站生产!");
									return;
								}
							}
						}
					}

					currSlkid = jsonValue.get("sid").toString(); // 修改服务器表的slkid
					sid1 = jsonValue.get("sid1").toString(); // 修改服务器表的sid1
					qtyv = jsonValue.get("qty").toString(); // (201)批次数量
					lot_no1 = jsonValue.get("lotno").toString();
					jsonValue.put("slkid", jsonValue.get("sid"));
					jsonValue.put("sid", "");
					jsonValue.put("state1", "01");
					jsonValue.put("state", "0");
					jsonValue.put("prd_name", jsonValue.get("prd_name"));
					jsonValue.put("dcid", GetAndroidMacUtil.getMac());
					jsonValue.put("op", zuoyeyuan);
					jsonValue.put("sys_stated", "3");
					jsonValue.put("sbid", shebeihao);
					jsonValue.put("zcno", MyApplication.BIANDAI_ZCNO);
					jsonValue.put("smake", MyApplication.user);
					jsonValue.put("mkdate", df.format(new Date()));
					jsonValue.put("sbuid", "D0001");
					newJson = jsonValue;
					Map<String, String> mesIdMap = MyApplication
							.httpMapKeyValueMethod(MyApplication.DBID,
									"savedata", MyApplication.user,
									jsonValue.toJSONString(), "D0001WEB", "1");
					httpRequestQueryRecord(MyApplication.MESURL, mesIdMap, "id");
				}
			}
			if ("updateServerTable".equals(string)) {
				JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
						.toString());
				String a = jsonObject.toString();
			}
			if (string.equals("id")) {
				JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
						.toString());
				JSONObject jsondata = (JSONObject) jsonObject.get("data");
				String newsid = jsondata.get("sid").toString(); // 拿到返回的sib1
				if (newsid != "") {

					// ----------------------------------------入站
					// 修改服务器俩张表
					String sidAndlotno = sid1 + ";" + lot_no1;
					Map<String, String> updateServerTable = MyApplication
							.UpdateServerTableMethod(MyApplication.DBID,
									MyApplication.user, "00", "01",
									sidAndlotno, currSlkid,
									MyApplication.BIANDAI_ZCNO, "200");
					httpRequestQueryRecord(MyApplication.MESURL,
							updateServerTable, "updateServerTable");
					// ----------------------------------------入站
					// 修改服务器俩张表

					// ----------------------------------------上料准备
					Map<String, String> ShangLiaoReadyMethod = MyApplication
							.ShangLiaoReadyMethod(MyApplication.DBID,
									MyApplication.user, sid1,
									MyApplication.BIANDAI_ZCNO, zuoyeyuan,
									shebeihao, currSlkid, qtyv, "0", "0", "201");
					httpRequestQueryRecord(MyApplication.MESURL,
							ShangLiaoReadyMethod, "ShangLiaoReadyMethod");
					// ----------------------------------------上料准备

					// 去服务器中拿设备号
					Map<String, String> map = new HashMap<String, String>();
					map.put("dbid", MyApplication.DBID);
					map.put("usercode", MyApplication.user);
					map.put("apiId", "assist");
					map.put("assistid", "{MSBMOLIST}"); // 设备任务列表
					map.put("cont", "~sbid='" + shebeihao + "' and zcno='"
							+ MyApplication.BIANDAI_ZCNO + "'");
					httpRequestQueryRecord(MyApplication.MESURL, map,
							"shebeiQuery1");

				}
			}
			if ("kaigongUpdata".equals(string)) {
				JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
						.toString());
				if (Integer.parseInt(jsonObject.get("id").toString()) == 0
						|| Integer.parseInt(jsonObject.get("id").toString()) == 1) {
					for (int i = 0; i < updateListState.size(); i++) {
						mesPrecord m = updateListState.get(i);
						Log.i("mes", m.toString());
						if (m.getState1().equals("01")
								|| m.getState1().equals("02")) {
							// ------------------------修改服务器的俩张表（开工）
							String sidAndlotno = m.getSid1() + ";"
									+ m.getLotno();
							Map<String, String> updateServerTable = MyApplication
									.UpdateServerTableMethod(
											MyApplication.DBID,
											MyApplication.user, "01", "03",
											sidAndlotno, m.getSlkid(),
											MyApplication.BIANDAI_ZCNO, "200");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateServerTable, "updateServerTable");
							// ------------------------修改服务器的俩张表（开工）
						}
					}
					updateListState.clear();
					// 刷新列表
					// 去服务器中拿设备号
					Map<String, String> map = new HashMap<String, String>();
					map.put("dbid", MyApplication.DBID);
					map.put("usercode", MyApplication.user);
					map.put("apiId", "assist");
					map.put("assistid", "{MSBMOLIST}"); // 设备任务列表
					map.put("cont", "~sbid='" + shebeihao + "' and zcno='"
							+ MyApplication.BIANDAI_ZCNO + "'");
					httpRequestQueryRecord(MyApplication.MESURL, map,
							"updateRefresh");
				} else {
					ToastUtil.showToast(biandaiActivity,
							String.valueOf(jsonObject.get("message")), 0);
				}
			}
			if (string.equals("chuzhanUpdata")) {
				JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
						.toString());
				if (Integer.parseInt(jsonObject.get("id").toString()) == 0
						|| Integer.parseInt(jsonObject.get("id").toString()) == 1) {
					for (int i = 0; i < updateListState.size(); i++) {
						mesPrecord m = updateListState.get(i);
						Log.i("mes", m.toString());
						if (m.getState1().equals("03")) {
							// ------------------------修改服务器的俩张表（出站）
							String sidAndlotno = m.getSid1() + ";"
									+ m.getLotno();
							Map<String, String> updateServerTable = MyApplication
									.UpdateServerTableMethod(
											MyApplication.DBID,
											MyApplication.user, "03", "04",
											sidAndlotno, m.getSlkid(),
											MyApplication.BIANDAI_ZCNO, "200");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateServerTable, "updateServerTable1");
						}
					}
					updateListState.clear();
					// 刷新列表
					// 去服务器中拿设备号
					Map<String, String> map = new HashMap<String, String>();
					map.put("dbid", MyApplication.DBID);
					map.put("usercode", MyApplication.user);
					map.put("apiId", "assist");
					map.put("assistid", "{MSBMOLIST}"); // 设备任务列表
					map.put("cont", "~sbid='" + shebeihao + "' and zcno='"
							+ MyApplication.BIANDAI_ZCNO + "'");
					httpRequestQueryRecord(MyApplication.MESURL, map,
							"updateRefresh");
				}
			}
		}
	};

	/*
	 * 弹出提示框
	 * 
	 * @param mes
	 */
	private void showNormalDialog(String mes) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				BianDaiActivity.this);
		normalDialog.setTitle("提示");
		normalDialog.setMessage(mes);
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// ...To-do
					}
				});
		// 显示
		normalDialog.show();
	}

	/**
	 * 刷新列表（设备和批号）
	 */
	private List<Map<String, Object>> QueryList(JSONObject jsonobject) {
		List<Map<String, Object>> mesList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mesMap = null;
		BianDaiCHScrollView headerScroll = (BianDaiCHScrollView) findViewById(R.id.biandai_scroll_title);
		BianDaiScrollViews.add(headerScroll);
		mListView = (ListView) findViewById(R.id.biandai_scroll_list);
		Map<String, String> stateName = MyApplication.getStateName();
		if ((JSONArray) jsonobject.get("values") == null) {
			return null;
		} else {
			for (int i = 0; i < ((JSONArray) jsonobject.get("values")).size(); i++) {
				JSONObject jsonValue = (JSONObject) (((JSONArray) jsonobject
						.get("values")).get(i));
				mesMap = new HashMap<String, Object>();
				mesPrecord new_mes = jsonValue.toJavaObject(mesPrecord.class);
				Log.i("newmes", new_mes.toString());
				mesMap.put("biandai_item_title", new_mes);
				mesMap.put("lotno", new_mes.getLotno());
				mesMap.put("state", stateName.get(new_mes.getState1()));
				mesMap.put("sid", new_mes.getSlkid());
				mesMap.put("sid1", new_mes.getSid1());
				mesMap.put("prd_name", new_mes.getPrd_name());
				mesMap.put("qty", new_mes.getQty());
				mesMap.put("bincode", new_mes.getBincode());
				mesList.add(mesMap);
			}
		}
		return mesList;
	}

	/**
	 * http请求
	 * 
	 * @param baseUrl
	 * @param map
	 */
	public void httpRequestQueryRecord(final String baseUrl,
			final Map<String, String> map, final String type) {
		new Thread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				String httpPost = HttpUtil.httpPost(baseUrl, map);
				Bundle b = new Bundle();
				b.putString("jsonObj", httpPost);
				b.putString("type", type);
				Message m = new Message();
				m.setData(b);
				handler.sendMessage(m);
			}
		}).start();
	}

	public static void addHViews(final BianDaiCHScrollView hScrollView) {
		if (!BianDaiScrollViews.isEmpty()) {
			int size = BianDaiScrollViews.size();
			BianDaiCHScrollView scrollView = BianDaiScrollViews.get(size - 1);
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
		BianDaiScrollViews.add(hScrollView);
	}

	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		for (BianDaiCHScrollView scrollView : BianDaiScrollViews) {
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