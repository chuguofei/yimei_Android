package com.yimei.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.ipqc.IPQC_shoujian;
import com.yimei.activity.kuaiguozhan.KanDaiActivity;
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
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;

/**
 * @author Administrator
 * 辅助:TESTLOTQUERY //查询lot号
 * 对象: D0001WEB //savedata添加
 */
public class BianDaiActivity extends Activity {

	static MyApplication myapp;
	public static BianDaiActivity biandaiActivity;
	public HorizontalScrollView mTouchView;
	private static List<BianDaiCHScrollView> BianDaiScrollViews = new ArrayList<BianDaiCHScrollView>();
	private static ListView mListView;
	private static BianDaiAdapter BianDaiAdapter; // 适配器

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
	private Map<String, String> ptime = new HashMap<String, String>();

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
		
		BianDaiCHScrollView headerScroll = (BianDaiCHScrollView) findViewById(R.id.biandai_scroll_title);
		BianDaiScrollViews.add(headerScroll);
		mListView = (ListView) findViewById(R.id.biandai_scroll_list);
		
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

		// 取出站的时间
		Map<String, String> map = MyApplication.QueryBatNo("M_PROCESS5", "");
		httpRequestQueryRecord(MyApplication.MESURL, map, "biandai_ptime");
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
					shebeihao = yimei_biandai_sbid_edt.getText().toString()
							.trim().toUpperCase();
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

					Map<String, String> IsSbidQuery = MyApplication
							.IsSbidQuery_biandai(shebeihao,
									MyApplication.BIANDAI_ZCNO);
					httpRequestQueryRecord(MyApplication.MESURL, IsSbidQuery,
							"IsSbidQuery");
					MyApplication.nextEditFocus(yimei_biandai_proNum_edt);
				}
				if (tag.equals("编带批次号")) { // 批次号
					Log.i("id", "批号");
					yimei_biandai_proNum_edt.setText(barcodeData);
					lot_no = yimei_biandai_proNum_edt.getText().toString()
							.trim();
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
					Map<String, String> IsSbidQuery = MyApplication
							.IsSbidQuery_biandai(shebeihao,
									MyApplication.BIANDAI_ZCNO);
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
		try {
			HashMap<Integer, Boolean> state = BianDaiAdapter.Getstate();

			if (state == null || state.equals(null)) {
				ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
			} else {
				int count = 0;
				for (int j = 0; j < BianDaiAdapter.getCount(); j++) {
					if (state.get(j)) {
						if (state.get(j) != null) {
							@SuppressWarnings("unchecked")
							HashMap<String, Object> map = (HashMap<String, Object>) BianDaiAdapter.getItem(j);

							if (updateListState == null) {
								updateListState = new ArrayList<mesPrecord>();
							}
							mesPrecord m = (mesPrecord) map.get("biandai_item_title");
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
							mes_precord.setHpdate(MyApplication.GetServerNowTime());
							// 如果状态是入站和上料都可以开工
							if (json.get("state1").toString().equals("01")) {
								Map<String, String> updateTimeMethod = MyApplication
										.updateServerTimeMethod(MyApplication.DBID,
												MyApplication.user, "01", "03",
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
								int chooseTime = MyApplication
										.ChooseTime(mes_precord.getHpdate());
								int a = Integer.parseInt(ptime
										.get(MyApplication.BIANDAI_ZCNO));
								if (chooseTime > Integer.parseInt(ptime
										.get(MyApplication.BIANDAI_ZCNO))
										|| ptime == null) {
									Map<String, String> updateTimeMethod = MyApplication
											.updateServerTimeMethod(
													MyApplication.DBID,
													MyApplication.user, "03", "04",
													mes_precord.getSid(),
													zuoyeyuan,
													MyApplication.BIANDAI_ZCNO,
													"202");
									httpRequestQueryRecord(MyApplication.MESURL,
											updateTimeMethod, publicState);
								} else {
									ToastUtil.showToast(BianDaiActivity.this,"选中的批次不能出站,已开工"+chooseTime+"分！", 0);
								}
							} else if (json.get("state1").toString().equals("02")) {
								ToastUtil.showToast(getApplicationContext(),
										"选中的批次不能出站！", 0);
							} else if (json.get("state1").toString().equals("01")) {
								ToastUtil.showToast(getApplicationContext(),
										"选中的批次不能出站！", 0);
							}
						}
//						Thread.sleep(400); //可能会出现连接池不够睡眠半秒
					}
					break;
				}
			}
		} catch (Exception e) {
			
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
					return;
				}
				if (isChecked) {
					int initCheck = BianDaiAdapter.initCheck(true);
					if(BianDaiAdapter!=null){						
						BianDaiAdapter.notifyDataSetChanged();
					}
					if (initCheck == -1) {
						ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
						return;
					}
				} else {
					int initCheck = BianDaiAdapter.initCheck(false);
					if(BianDaiAdapter!=null){
						BianDaiAdapter.notifyDataSetChanged();
					}
					if (initCheck == -1) {
						ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
						return;
					}
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
				if (actionId >= 0) {
					if (yimei_biandai_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(BianDaiActivity.this, "作业员不能为空", 0);
						return false;
					}
					zuoyeyuan = yimei_biandai_user_edt.getText().toString()
							.trim();
					MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_biandai_sbid_edt) {
				if (actionId >= 0) {
					shebeihao = yimei_biandai_sbid_edt.getText().toString()
							.toUpperCase().trim();
					yimei_biandai_sbid_edt.setText(shebeihao);
					if (yimei_biandai_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(BianDaiActivity.this, "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_biandai_user_edt);
						return false;
					}
					if (yimei_biandai_sbid_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_sbid_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(BianDaiActivity.this, "设备号不能为空", 0);
						MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
						return false;
					}
					Log.i("sbid", "设备号回车");
					Map<String, String> IsSbidQuery = MyApplication
							.IsSbidQuery_biandai(shebeihao,
									MyApplication.BIANDAI_ZCNO);
					httpRequestQueryRecord(MyApplication.MESURL, IsSbidQuery,
							"IsSbidQuery");
					MyApplication.nextEditFocus(yimei_biandai_proNum_edt);
					flag = true;
				}
			}
			// 生产批号的回车事件
			if (v.getId() == R.id.yimei_biandai_proNum_edt) {
				if (actionId >= 0) {
					lot_no = yimei_biandai_proNum_edt.getText().toString()
							.trim();
					if (yimei_biandai_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(BianDaiActivity.this, "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_biandai_user_edt);
						return false;
					}
					if (yimei_biandai_sbid_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_sbid_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(BianDaiActivity.this, "设备号不能为空", 0);
						MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
						return false;
					}
					if (yimei_biandai_proNum_edt.getText().toString().trim()
							.equals("")
							|| yimei_biandai_proNum_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(BianDaiActivity.this, "批次号为空~", 0);
						MyApplication.nextEditFocus(yimei_biandai_proNum_edt);
						Log.i("foucus", "批次号回车");
						return false;
					}
					Log.i("sbid", "批次号回车");
					Map<String, String> IsSbidQuery = MyApplication
							.IsSbidQuery_biandai(shebeihao,
									MyApplication.BIANDAI_ZCNO);
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
				String string = b.getString("type");
				if (string.equals("TimeOutLogin")) { // 超时登录
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (jsonObject.getInteger("id") != 0) {
						LoginTimeout_dig("密码错误", "");
					}
				}
				if (string.equals("IsSbidQuery")) {  //设备号查询
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { // 没有该设备号
						Log.i("code", jsonObject.get("code").toString());
						if (mListView != null) {
							mListView.setAdapter(null);
							if(BianDaiAdapter!=null){								
								BianDaiAdapter.notifyDataSetChanged();
							}
							ToastUtil.showToast(getApplicationContext(),
									"设备与制程不匹或该设备处于维修状态!", 0);
							MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
						} else {
							ToastUtil.showToast(getApplicationContext(),
									"设备与制程不匹或该设备处于维修状态!", 0);
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
				if (string.equals("IsSbidQuery1")) { //批次号回车查询设备号
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());

					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { // 没有该设备号
						Log.i("code", jsonObject.get("code").toString());
						if (mListView != null) {
							mListView.setAdapter(null);
							if(BianDaiAdapter!=null){
								BianDaiAdapter.notifyDataSetChanged();
							}
							ToastUtil.showToast(getApplicationContext(),
									"设备与制程不匹或该设备处于维修状态!", 0);
							MyApplication.nextEditFocus(yimei_biandai_sbid_edt);
						} else {
							ToastUtil.showToast(getApplicationContext(),
									"设备与制程不匹或该设备处于维修状态!", 0);
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
				if ("shebeiQuery".equals(string)) {  //设备号记录查询
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					// 判断设备号+制程在服务器中是否有数据
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {

						List<Map<String, Object>> mesList = QueryList(jsonObject); // 刷新列表
						if (biandaiPrdNocomparison != null) {
							if(biandaiPrdNocomparison.size()!=0){								
								biandaiPrdNocomparison.clear();
							}
						}
						if (mesList != null) {
							biandaiPrdNocomparison = mesList;
						}
						BianDaiAdapter = new BianDaiAdapter(
								BianDaiActivity.this, mesList);
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
//						if (mListView != null) {
						if (biandaiPrdNocomparison != null) {
							if(biandaiPrdNocomparison.size()!=0){								
								biandaiPrdNocomparison.clear();
							}
						}
							biandai_kaigong.setEnabled(true);
							biandai_chuzhan.setEnabled(true);
							BianDaiAdapter = null;
							mListView.setAdapter(null);
//							mListView = null;
							if(BianDaiAdapter!=null){								
								BianDaiAdapter.notifyDataSetChanged();
							}
							MyApplication
									.nextEditFocus(yimei_biandai_proNum_edt);
//						} else {
							biandai_kaigong.setEnabled(false);
							biandai_chuzhan.setEnabled(false);
//						}
					}
				}
				if ("shebeiQuery1".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					// 判断设备号+制程在服务器中是否有数据
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {

						List<Map<String, Object>> mesList = QueryList(jsonObject); // 刷新列表
						BianDaiAdapter = new BianDaiAdapter(
								BianDaiActivity.this, mesList);
						mListView.setAdapter(BianDaiAdapter);
						ToastUtil.showToast(getApplicationContext(), "《"
								+ lot_no + "》测试号加载到列表中~", 0);
						yimei_biandai_proNum_edt.selectAll();
						if (mListView == null) {
							biandai_kaigong.setEnabled(false);
							biandai_chuzhan.setEnabled(false);
						} else {
							biandai_kaigong.setEnabled(true);
							biandai_chuzhan.setEnabled(true);
						}
					} else {
						// 清空列表
						if (biandaiPrdNocomparison != null) {
							if(biandaiPrdNocomparison.size()!=0){								
								biandaiPrdNocomparison.clear();
							}
						}
							BianDaiAdapter = null;
							mListView.setAdapter(null);
//							mListView = null;
							if(BianDaiAdapter!=null){								
								BianDaiAdapter.notifyDataSetChanged();
							}
							MyApplication
									.nextEditFocus(yimei_biandai_proNum_edt);
							biandai_kaigong.setEnabled(false);
							biandai_chuzhan.setEnabled(false);
					}
				}
				if ("updateRefresh".equals(string)) { //刷新列表
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					// 判断设备号+制程在服务器中是否有数据
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1
							|| Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {

						List<Map<String, Object>> mesList = QueryList(jsonObject); // 刷新列表
						if (mesList != null) {
							BianDaiAdapter = new BianDaiAdapter(
									BianDaiActivity.this, mesList);
							mListView.setAdapter(BianDaiAdapter);
							if(BianDaiAdapter!=null){								
								BianDaiAdapter.notifyDataSetChanged();
							}
							return;
						} else {
							BianDaiAdapter = null;
							mListView.setAdapter(null);
							
							/*mListView.setAdapter(null);
							BianDaiAdapter.notifyDataSetChanged();*/
							// 如果等于空可以扫新的批号
							if (biandaiPrdNocomparison != null) {
								if(biandaiPrdNocomparison.size()!=0){								
									biandaiPrdNocomparison.clear();
								}
							}
						}

					}
				}
				if ("ListViewIsLotNo".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject.get("values")).get(0));
						if (Integer.parseInt(jsonValue.get("holdid").toString()) == 1) {
							ToastUtil.showToast(BianDaiActivity.this,"该lot号已被【HOLD】", 0);
							yimei_biandai_proNum_edt.setText("");
							InputHidden();
						}
						// 0.测试站 1.编带站 2.看带站
						if (Integer.parseInt(jsonValue.get("lotstate").toString()) == 1) {
							ToastUtil.showToast(BianDaiActivity.this,"该lot号已编带", 0);
							InputHidden();
							return;
						} else if (Integer.parseInt(jsonValue.get("lotstate").toString()) == 2) {
							ToastUtil.showToast(BianDaiActivity.this,"该lot号已看带", 0);
							yimei_biandai_proNum_edt.setText("");
							InputHidden();
							return;
						}
						// 如果有批号
						if (BianDaiAdapter != null) {
							// 循环列表
							int count = 0;
							for (int i = 0; i < BianDaiAdapter.getCount(); i++) {
								Map<String, Object> map = (Map<String, Object>) BianDaiAdapter
										.getItem(i);
								if (map.get("lotno").equals(lot_no)) {
									count++;
									BianDaiAdapter.state.put(i, true);
								}
							}
							HashMap<Integer, Boolean> a = BianDaiAdapter
									.Getstate();
							if (count > 0) {
								BianDaiAdapter.notifyDataSetChanged();
								ToastUtil.showToast(getApplicationContext(),
										"《" + lot_no + "》测试号存在,已经帮你选中", 0);
								yimei_biandai_proNum_edt.selectAll();
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
								httpRequestQueryRecord(MyApplication.MESURL,
										map, "ServerIsZcnoAndLotNo");
							}
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
						// 循环列表
						if (BianDaiAdapter != null) {
							int count = 0;
							for (int i = 0; i < BianDaiAdapter.getCount(); i++) {
								Map<String, Object> map = (Map<String, Object>) BianDaiAdapter
										.getItem(i);
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
								yimei_biandai_proNum_edt.selectAll();
							} else {
								// 提示没有该批号
								ToastUtil.showToast(getApplication(), "没有该批次号！", 0);
								MyApplication
										.nextEditFocus(yimei_biandai_proNum_edt);
								yimei_biandai_proNum_edt.selectAll();
							}
						}else {
							// 提示没有该批号
							ToastUtil.showToast(getApplication(), "没有该批次号！", 0);
							MyApplication
									.nextEditFocus(yimei_biandai_proNum_edt);
							yimei_biandai_proNum_edt.selectAll();
						}
					}
				}
				if ("ServerIsZcnoAndLotNo".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
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
						httpRequestQueryRecord(MyApplication.MESURL, map,
								"json");
					}
				}
				if ("json".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					// 判断设备号+制程在服务器中是否有数据
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
								.get("values")).get(0));
						if(Integer.parseInt(jsonValue.get("holdid").toString()) == 1){
							ToastUtil.showToast(biandaiActivity,"该lot号已被【HOLD】",0);
						}
						if (Integer.parseInt(jsonValue.get("bok").toString()) == 0) {
							ToastUtil.showToast(BianDaiActivity.this, "该批号不具备入站条件!",0);
							MyApplication.nextEditFocus(yimei_biandai_proNum_edt);
							yimei_biandai_proNum_edt.selectAll();
							return;
						} else if (jsonValue.get("lotstate").toString().equals("1")) {
							MyApplication.nextEditFocus(yimei_biandai_proNum_edt);
							yimei_biandai_proNum_edt.selectAll();
							ToastUtil.showToast(BianDaiActivity.this, "该批号已经出站!", 0);
							return;
						} else {
							//如果有首检标示
							/*if(jsonValue.get("fircheck").toString().equals("1")){
								if(currSlkid!=null&&!(jsonValue.get("sid").toString().equals(currSlkid))){
									JumShouJianlDialog("现工单为:【"+currSlkid+"】,扫描的工单为【"+jsonValue.get("sid").toString()+"】,是否进行首检？");
									return;
								}
							}*/
							if (biandaiPrdNocomparison != null) {

								if (biandaiPrdNocomparison.size() != 0) {
									for (int i = 0; i < biandaiPrdNocomparison
											.size(); i++) {
										Map<String, Object> map = biandaiPrdNocomparison
												.get(i);
										mesPrecord mespre = (mesPrecord) map
												.get("biandai_item_title");
										if (mListView != null) {
											if(mespre.getPrd_no()!=null){
												if (!mespre.getPrd_no().equals(
														jsonValue.get("prd_no"))) {
													showNormalDialog("（机型不符）\n当前机型：【"+mespre.getPrd_no()+"】\n扫描机型:【"+jsonValue.get("prd_no")+"】\n不能入站，请先将当前批次出站后再入站生产!");
													yimei_biandai_proNum_edt
															.selectAll();
													return;
												}
											}
											if(mespre.getBincode()!=null){												
												if (!mespre.getBincode().equals(
														jsonValue.get("bincode"))) {
													showNormalDialog("（bincode不符）\n当前bincode【"+mespre.getBincode()+"】\n扫描bincode【"+jsonValue.get("bincode")+"】\n不能入站，请先将当前批次出站后再入站生产!");
													yimei_biandai_proNum_edt
													.selectAll();
													return;
												}
											}
										}
									}
								} else {
									List<Map<String, Object>> queryList = QueryList(jsonObject);
									biandaiPrdNocomparison = queryList;
								}
							} else {
								List<Map<String, Object>> queryList = QueryList(jsonObject);
								biandaiPrdNocomparison = queryList;
							}

							currSlkid = jsonValue.get("sid").toString(); // 修改服务器表的slkid
							sid1 = jsonValue.get("sid1").toString(); // 修改服务器表的sid1
							qtyv = jsonValue.get("qty").toString(); // (201)批次数量
							lot_no1 = jsonValue.get("lotno").toString();
							jsonValue.put("slkid", jsonValue.get("sid"));
							jsonValue.put("sid", "");
							jsonValue.put("state1", "01");
							jsonValue.put("state", "0");
							jsonValue.put("prd_name", jsonValue.containsKey("prd_name")?jsonValue.get("prd_name"):"");
							jsonValue.put("prd_no", jsonValue.containsKey("prd_no")?jsonValue.get("prd_no"):"");
							jsonValue.put("dcid", GetAndroidMacUtil.getMac());
							jsonValue.put("op", zuoyeyuan);
							jsonValue.put("sys_stated", "3");
							jsonValue.put("sbid", shebeihao);
							jsonValue.put("zcno", MyApplication.BIANDAI_ZCNO);
							jsonValue.put("smake", MyApplication.user);
							jsonValue.put("mkdate",MyApplication.GetServerNowTime());
							jsonValue.put("sbuid", "D0001");
							newJson = jsonValue;
							Map<String, String> mesIdMap = MyApplication
									.httpMapKeyValueMethod(MyApplication.DBID,
											"savedata", MyApplication.user,
											jsonValue.toJSONString(),
											"D0001WEB", "1");
							httpRequestQueryRecord(MyApplication.MESURL,
									mesIdMap, "id");
						}
					} else {
						ToastUtil.showToast(getApplicationContext(), "没有该批次号!",
								0);
						return;
					}
				}
				if ("updateServerTable".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					String a = jsonObject.toString();
				}
				if (string.equals("id")) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
					JSONObject jsondata = (JSONObject) jsonObject.get("data");
					String newsid = jsondata.get("sid").toString(); // 拿到返回的sib1
					if (newsid != "") {

						// ----------------------------------------入站
						// 修改服务器俩张表
						String sidAndlotno = sid1 + ";" + lot_no1+";"+shebeihao;
						Map<String, String> updateServerTable = MyApplication
								.UpdateServerTableMethod(MyApplication.DBID,
										MyApplication.user, "00", "01",
										sidAndlotno, currSlkid,
										MyApplication.BIANDAI_ZCNO, "200");
						httpRequestQueryRecord(MyApplication.MESURL,
								updateServerTable, "updateServerTable_200");
						// ----------------------------------------入站
						// 修改服务器俩张表

						// ----------------------------------------上料准备
						Map<String, String> ShangLiaoReadyMethod = MyApplication
								.ShangLiaoReadyMethod(MyApplication.DBID,
										MyApplication.user, sid1,
										MyApplication.BIANDAI_ZCNO, zuoyeyuan,
										shebeihao, currSlkid, qtyv, "0", "0",
										"201");
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
				if("updateServerTable_200".equals(string)){
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					System.out.println(jsonObject);
				}
				if ("kaigongUpdata".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("id").toString()) == 0
							|| Integer
									.parseInt(jsonObject.get("id").toString()) == 1) {
						for (int i = 0; i < updateListState.size(); i++) {
							mesPrecord m = updateListState.get(i);
							Log.i("mes", m.toString());
							if (m.getState1().equals("01")
									|| m.getState1().equals("02")) {
								// ------------------------修改服务器的俩张表（开工）
								String sidAndlotno = m.getSid1() + ";"
										+ m.getLotno()+";"+shebeihao;
								Map<String, String> updateServerTable = MyApplication
										.UpdateServerTableMethod(
												MyApplication.DBID,
												MyApplication.user, "01", "03",
												sidAndlotno, m.getSlkid(),
												MyApplication.BIANDAI_ZCNO,
												"200");
								httpRequestQueryRecord(MyApplication.MESURL,
										updateServerTable, "updateServerTable_200");
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
						ToastUtil.showToast(BianDaiActivity.this,
								String.valueOf(jsonObject.get("message")), 0);
					}
				}
				if (string.equals("chuzhanUpdata")) {  
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("id").toString()) == 0
							|| Integer
									.parseInt(jsonObject.get("id").toString()) == 1) {
						for (int i = 0; i < updateListState.size(); i++) {
							mesPrecord m = updateListState.get(i);
							Log.i("mes", m.toString());
							if (m.getState1().equals("03")) {
								// ------------------------修改服务器的俩张表（出站）
								String sidAndlotno = m.getSid1() + ";"+ m.getLotno()+";"+shebeihao;
								Map<String, String> updateServerTable = MyApplication
										.UpdateServerTableMethod(
												MyApplication.DBID,
												MyApplication.user, "03", "04",
												sidAndlotno, m.getSlkid(),
												MyApplication.BIANDAI_ZCNO,
												"200");
								httpRequestQueryRecord(MyApplication.MESURL,
										updateServerTable, "updateServerTable1_200");
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
				if (string.equals("biandai_ptime")) {  //拿卡出站时间
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					for (int i = 0; i < ((JSONArray) jsonObject.get("values"))
							.size(); i++) {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
								.get("values")).get(i));
						if (jsonValue.containsKey("ptime")) {
							ptime.put(jsonValue.get("id").toString(), jsonValue
									.get("ptime").toString());
						}
					}
				}
			} catch (Exception e) {
				ToastUtil.showToast(BianDaiActivity.this,"编带（服务器处理）--err:"+e.toString(), 0);
			}
		}
	};
	
	
	/**
	 * 键盘隐藏
	 */
	private void InputHidden(){
		//调用键盘类
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 如果软键盘已经显示，则隐藏，反之则显示
		imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	/**
	 * 跳转首检界面
	 * @param msg
	 */
	private void JumShouJianlDialog(String msg) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				BianDaiActivity.this);
		normalDialog.setTitle("提示");
		normalDialog.setMessage(Html.fromHtml("<font color='red'>" + msg
				+ "</font>"));
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClass(BianDaiActivity.this,IPQC_shoujian.class);// 跳转到首检
						// 利用bundle来存取数据
						Bundle bundle = new Bundle();
						bundle.putString("json",newJson.toString());
						// 再把bundle中的数据传给intent，以传输过去
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
		normalDialog.setNegativeButton("取消", null);		// 显示
		normalDialog.show();
	}

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
		normalDialog.setCancelable(false);
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
				mesMap.put("prd_name", new_mes.getPrd_no());
				mesMap.put("qty", new_mes.getQty());
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
		usertext.setKeyListener(null);

		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				BianDaiActivity.this);
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
						httpRequestQueryRecord(MyApplication.MESURL, map,
								"TimeOutLogin");
					}
				});
		// 显示
		normalDialog.show();
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
			showNormalDialog1("1.新增不同bincode不能入站");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showNormalDialog1(String msg) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(BianDaiActivity.this);
		normalDialog.setTitle("提示");
		normalDialog.setMessage(msg);
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
		// 显示
		normalDialog.show();
	}
}
