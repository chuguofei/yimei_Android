package com.yimei.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.adapter.GuJingScrollAdapter;
import com.yimei.adapter.ScrollAdapter;
import com.yimei.entity.mesPrecord;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.sqlliteUtil.mesAllMethod;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.HttpUtil;
import com.yimei.util.ToastUtil;

public class GuJingActivity extends Activity {

	static MyApplication myapp;
	public static GuJingActivity gujingActivity;
	// 操作sqliet
	private mesAllMethod gujing_list = new mesAllMethod(GuJingActivity.this); // 调用操作本地库的方法
	private static GuJingScrollAdapter scrollAdapter; // 适配器
	private static ListView mListView; // listview
	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>(); // 行+标题滚动
	private CheckBox gujing_quanxuan; // 全选按钮
	private EditText yimei_gujingequipment_edt;
	private EditText yimei_gujinguser_edt;
	private EditText yimei_gujingproNum_edt;
	private String zcno = "11";// 界面上的制程号
	private Button kaigong;// 开工按钮
	private Button chuzhan;// 出站按钮
	private Button shangliao; // 上料按钮

	// 获取4个文本框的文本
	private String zuoyeyuan; // 界面上的作业员(全局)
	private String shebeihao; // 界面上的设备号(全局)
	private String yimei_pro_edt; // 界面上的批次号（全局）
	private String qtyv; // 批次数量

	private static JSONObject newJson; // 拿新sid存在json
	private static String currSlkid;
	private List<mesPrecord> updatekaigongSid1; // 修改服务器的2张表的状态（出站，开工）,更改本地库的状态
	private Map<String, String> ptime = new HashMap<String, String>();

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
				if (tag.equals("固晶作业员")) { // 2131296268 2131361805
					Log.i("id", "作业员");
					yimei_gujinguser_edt.setText(barcodeData);
					if (yimei_gujinguser_edt.getText().toString().trim()
							.equals("")
							|| yimei_gujinguser_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_gujinguser_edt));
						return;
					}
					nextEditFocus((EditText) findViewById(R.id.yimei_gujingequipment_edt));
				}
				if (tag.equals("固晶设备号")) {
					yimei_gujingequipment_edt.setText(barcodeData);
					if (yimei_gujinguser_edt.getText().toString().trim()
							.equals("")
							|| yimei_gujinguser_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_gujinguser_edt));
						return;
					}
					if (yimei_gujingequipment_edt.getText().toString().trim()
							.equals("")
							|| yimei_gujingequipment_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(gujingActivity, "设备号不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_gujingequipment_edt));
						return;
					}
					sbidGetData(); // 设备号的回车键
					nextEditFocus((EditText) findViewById(R.id.yimei_gujingproNum_edt));
				}
				if (tag.equals("固晶批次号")) {
					yimei_gujingproNum_edt.setText(barcodeData);
					if (yimei_gujinguser_edt.getText().toString().trim()
							.equals("")
							|| yimei_gujinguser_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_gujinguser_edt));
						return;
					}
					if (yimei_gujingequipment_edt.getText().toString().trim()
							.equals("")
							|| yimei_gujingequipment_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(gujingActivity, "设备号不能为空~", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_gujingequipment_edt));
						return;
					}
					if (yimei_gujingproNum_edt.getText().toString().trim()
							.equals("")
							|| yimei_gujingproNum_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(gujingActivity, "批次号为空~", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_gujingproNum_edt));
						return;
					}
					sid1GetData(); // 生产批号回车事件
					nextEditFocus((EditText) findViewById(R.id.yimei_gujingproNum_edt));
					yimei_gujingproNum_edt.selectAll();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gujing);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		Application app = getApplication();
		myapp = (MyApplication) app;
		myapp.addActivity_(this);
		gujingActivity = this;
		myapp.removeActivity_(LoginActivity.loginActivity);// 销毁登录
		gujing_quanxuan = (CheckBox) findViewById(R.id.gujing_quanxuan); // 全选按钮
		listenerQuanXuan(); // 全选事件
		shangliao = (Button) findViewById(R.id.gujing_ruliao); // 获取上料id
		kaigong = (Button) findViewById(R.id.gujing_kaigong); // 获取开工id
		chuzhan = (Button) findViewById(R.id.gujing_chuzhan); // 获取出站id
		if (gujing_list.mesDataCount() == 0) { // 是否禁用(上料，开工，出站)按钮
			shangliao.setEnabled(false);
			kaigong.setEnabled(false);
			chuzhan.setEnabled(false);
		}
		if (mListView == null) {
			shangliao.setEnabled(false);
			kaigong.setEnabled(false);
			chuzhan.setEnabled(false);
		} else {
			shangliao.setEnabled(true);
			kaigong.setEnabled(true);
			chuzhan.setEnabled(true);
		}
		kaigong.setOnClickListener(kaigongClick); // 开工点击事件
		chuzhan.setOnClickListener(chuzhanClick); // 出站点击事件
		shangliao.setOnClickListener(shangliaoClick); // 上料点击事件

		// 取出站的时间
		Map<String, String> map = MyApplication.QueryBatNo("M_PROCESS5", "");
		httpRequestQueryRecord(MyApplication.MESURL, map, "gujing_ptime");
	};

	/**
	 * 点击出站事件
	 */
	OnClickListener chuzhanClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.gujing_chuzhan) {
				UpdateServerData("chuzhanUpdata");
			}
		}
	};

	/**
	 * 开工的点击事件
	 */
	OnClickListener kaigongClick = new OnClickListener() {

		@SuppressWarnings("null")
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.gujing_kaigong) {
				UpdateServerData("kaigongUpdata");
			}
		}
	};

	/**
	 * 上料点击事件
	 */
	OnClickListener shangliaoClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 查看有没有选中的数据
			HashMap<Integer, Boolean> state = scrollAdapter.Getstate();
			if (state == null) {
				ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
			} else {
				int count = 0;
				mesPrecord mesObj = null;
				for (int j = 0; j < scrollAdapter.getCount(); j++) {
					if (state.get(j)) {
						if (state.get(j) != null) {
							@SuppressWarnings("unchecked")
							// 取listview中的数据
							HashMap<String, Object> map = (HashMap<String, Object>) scrollAdapter
									.getItem(j);
							mesPrecord m = (mesPrecord) map.get("checkMap"); // 取选中工单号
							mesObj = m;
							count++;
						}
					}
				}
				switch (count) {
				case 0:
					ToastUtil.showToast(gujingActivity, "请选中一条记录!", 0);
					break;
				case 1:
					Intent intent = new Intent();
					intent.setClass(gujingActivity, ShangLiaoActivity.class);// 跳转到上料页面
					Bundle bundle = new Bundle();
					bundle.putString("activity", "gujing");
					bundle.putString("type", "料号+批号"); // 显示的文本框
					bundle.putSerializable("object", mesObj);
					intent.putExtras(bundle);
					startActivity(intent);
					break;
				default:
					// 多选
					ToastUtil.showToast(gujingActivity, "不可以多选!", 0);
					break;
				}
			}
		}
	};

	/**
	 * 与界面交互
	 */
	protected void onResume() {
		super.onResume();

		List<Map<String, Object>> getListMes_Procord = GetListMes_Procord(
				shebeihao, zcno);
		if (getListMes_Procord != null) {
			yimei_gujingequipment_edt.setFocusable(true);
			yimei_gujingequipment_edt.setFocusableInTouchMode(true);
			yimei_gujingequipment_edt.selectAll();
			scrollAdapter = new GuJingScrollAdapter(GuJingActivity.this,
					getListMes_Procord);
			mListView.setAdapter(scrollAdapter);
			scrollAdapter.notifyDataSetChanged();
		}

		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		yimei_gujinguser_edt = (EditText) findViewById(R.id.yimei_gujinguser_edt);
		yimei_gujingproNum_edt = (EditText) findViewById(R.id.yimei_gujingproNum_edt);
		yimei_gujingequipment_edt = (EditText) findViewById(R.id.yimei_gujingequipment_edt);
		// yimei_gujinguser_edt.setTag("固晶作业员");

		yimei_gujinguser_edt.setOnEditorActionListener(editEnter); // 操作员的回车监听事件
		yimei_gujingequipment_edt.setOnEditorActionListener(editEnter); // 设备的回车监听事件
		yimei_gujingproNum_edt.setOnEditorActionListener(editEnter);// 生产批号的回车监听事件

		yimei_gujinguser_edt.setOnFocusChangeListener(EditGetFocus);// 操作员失去焦点
		yimei_gujingequipment_edt.setOnFocusChangeListener(EditGetFocus);// 设备号失去焦点
		yimei_gujingproNum_edt.setOnFocusChangeListener(EditGetFocus);// 批号失去焦点

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
	 * 按设备号获取记录
	 */
	public void sbidGetData() {
		// 获取设备号的文本
		shebeihao = yimei_gujingequipment_edt.getText().toString().trim();
		Map<String, String> mapSbid = new HashMap<String, String>();
		mapSbid.put("dbid", MyApplication.DBID);
		mapSbid.put("usercode", MyApplication.user);
		mapSbid.put("apiId", "assist");
		mapSbid.put("assistid", "{MESEQUTM}");
		mapSbid.put("cont", "~id='" + shebeihao + "' and zc_id='" + zcno + "' ");
		httpRequestQueryRecord(MyApplication.MESURL, mapSbid, "Isshebei");
	}

	/***
	 * 生产批号pda|失去焦点获取值
	 */
	public void sid1GetData() {

		Map<String, String> mapSbid = new HashMap<String, String>();
		mapSbid.put("dbid", MyApplication.DBID);
		mapSbid.put("usercode", MyApplication.user);
		mapSbid.put("apiId", "assist");
		mapSbid.put("assistid", "{MESEQUTM}");
		mapSbid.put("cont", "~id='" + shebeihao + "' and zc_id='" + zcno + "' ");
		httpRequestQueryRecord(MyApplication.MESURL, mapSbid, "Isshebei1");
	}

	/**
	 * 接收http请求返回值
	 */
	@SuppressLint("HandlerLeak")
	final android.os.Handler handler = new android.os.Handler() {
		public void handleMessage(Message msg) {
			try {
				super.handleMessage(msg);
				Bundle b = msg.getData();
				String string = b.getString("type");
				if (string.equals("Isshebei1")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
						Log.i("code", jsonObject.get("code").toString());
						if (mListView != null) {
							mListView.setAdapter(null);
							if (scrollAdapter != null) {
								scrollAdapter.notifyDataSetChanged();
							}
							nextEditFocus((EditText) findViewById(R.id.yimei_gujingequipment_edt));
							ToastUtil.showToast(getApplicationContext(),
									"没有该设备或制程!", 0);
						} else {
							nextEditFocus((EditText) findViewById(R.id.yimei_gujingequipment_edt));
							ToastUtil.showToast(getApplicationContext(),
									"没有该设备或制程!", 0);
						}
					} else {
						yimei_pro_edt = yimei_gujingproNum_edt.getText()
								.toString().trim();
						// 如果批次号+制程在库中存在
						if (gujing_list.IsSid1AndZnco(yimei_pro_edt, zcno)) {
							// 如果批次+设备+制程 在库中存在
							if (gujing_list.sid1_Select(yimei_pro_edt,
									shebeihao, zcno)) {
								Log.i("Tag", yimei_pro_edt + "批次号存在,已经帮你选中");
								// 查找当前选着的设备号
								List<Map<String, Object>> mesList = GetListMes_Procord(
										shebeihao, zcno);
								if (mesList != null) {
									scrollAdapter = new GuJingScrollAdapter(
											gujingActivity, mesList);
									mListView.setAdapter(scrollAdapter);
									// 如果存在复选框选中
									List<Map<String, Object>> ListMesPitch = scrollAdapter.listData;
									for (int i = 0; i < ListMesPitch.size(); i++) {
										Map<String, Object> map = ListMesPitch
												.get(i);
										if (map.get("sid1").equals(
												yimei_pro_edt)) {
											scrollAdapter.state.put(i, true);
										}
									}
									scrollAdapter.notifyDataSetChanged();
									ToastUtil.showToast(
											getApplicationContext(), "《"
													+ yimei_pro_edt
													+ "》批次号存在,已经帮你选中", 0);
								}
								// 去服务器拿数据
							} else {
								// 批号已经绑定设备
								ToastUtil.showToast(gujingActivity, "《"
										+ yimei_gujingproNum_edt.getText()
												.toString()
										+ "》批次号已绑定过设备号不能重复绑定~", 0);
								yimei_gujingproNum_edt
										.setSelectAllOnFocus(true);
							}
						} else {
							// 去服务器拿值 并绑定设备号
							Map<String, String> map = new HashMap<String, String>();
							map.put("dbid", MyApplication.DBID);
							map.put("usercode", MyApplication.user);
							map.put("apiId", "assist");
							map.put("assistid", "{MOZCLISTWEB}");
							map.put("cont", "~sid1='" + yimei_pro_edt
									+ "' and zcno='" + zcno + "'");
							httpRequestQueryRecord(MyApplication.MESURL, map,
									"json");
						}
					}
				}
				if (string.equals("json")) { // 没有批次号
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
						ToastUtil.showToast(getApplicationContext(), "没有该批次号!",0);
						return;
					} else {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
								.get("values")).get(0));
						if (Integer.parseInt(jsonValue.get("bok").toString()) == 0) {
							ToastUtil.showToast(gujingActivity, "该批号不具备入站条件!",0);
							yimei_gujingproNum_edt.selectAll();
							return;
						} else if (jsonValue.get("state").toString().equals("02")
								|| jsonValue.get("state").toString().equals("03")) {
							ToastUtil.showToast(gujingActivity, "该批号已经入站!", 0);
							yimei_gujingproNum_edt.selectAll();
							return;
						} else if (jsonValue.get("state").toString().equals("04")) {
							ToastUtil.showToast(gujingActivity, "该批号已经出站!", 0);
							yimei_gujingproNum_edt.selectAll();
							return;
						} else {
							currSlkid = jsonValue.get("sid").toString(); // 修改服务器表的slkid
							qtyv = jsonValue.get("qty").toString(); // (201)批次数量
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
							jsonValue.put("smake", MyApplication.user);
							jsonValue.put("mkdate",
									MyApplication.GetServerNowTime());
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
					}
				}
				if (string.equals("id")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					JSONObject jsondata = (JSONObject) jsonObject.get("data");
					String newsid = jsondata.get("sid").toString(); // 拿到返回的sib1
					if (newsid != "") {
						if ((!(zuoyeyuan == null) || !zuoyeyuan.equals(""))
								&& !(shebeihao == null)
								|| !shebeihao.equals("")) {

							// ----------------------------------------入站
							// 修改服务器俩张表
							Map<String, String> updateServerTable = MyApplication
									.UpdateServerTableMethod(
											MyApplication.DBID,
											MyApplication.user, "00", "01",
											yimei_pro_edt, currSlkid, zcno,
											"200");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateServerTable, "updateServerTable");
							// ----------------------------------------入站
							// 修改服务器俩张表

							// ----------------------------------------上料准备
							Map<String, String> ShangLiaoReadyMethod = MyApplication
									.ShangLiaoReadyMethod(MyApplication.DBID,
											MyApplication.user, yimei_pro_edt,
											zcno, zuoyeyuan, shebeihao,
											currSlkid, qtyv, "0", "0", "201");
							httpRequestQueryRecord(MyApplication.MESURL,
									ShangLiaoReadyMethod,
									"ShangLiaoReadyMethod");
							// ----------------------------------------上料准备

							newJson.put("sid", newsid);
							Log.i("static", newJson.toString());
							mesPrecord new_mes = newJson
									.toJavaObject(mesPrecord.class);
							if (gujing_list.addNewIdData(new_mes)) {

								kaigong.setEnabled(true);
								chuzhan.setEnabled(true);
								List<Map<String, Object>> getListMes_Procord = GetListMes_Procord(
										shebeihao, zcno);
								if (getListMes_Procord != null) {
									scrollAdapter = new GuJingScrollAdapter(
											GuJingActivity.this,
											getListMes_Procord);
									mListView.setAdapter(scrollAdapter);
									shangliao.setEnabled(true);
									ToastUtil.showToast(
											getApplicationContext(), "《"
													+ yimei_gujingproNum_edt
															.getText()
															.toString()
													+ "》批次号已加载到列表中", 0);

								} else {
									// ToastUtil.showToast(getApplicationContext(),
									// "没有记录", 0);
									Log.i("Tag", "没有记录");
								}
							}
						}

					} else {
						ToastUtil.showToast(getApplicationContext(),
								"作业员或设备号为空", 0);
					}

				}
				if (string.equals("updateServerTable")) { // 修改服务器俩张表
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					Log.i("updateServerTable", jsonObject.toString());
				}
				if (string.equals("ShangLiaoReadyMethod")) { // 上料准备
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					Log.i("ShangLiaoReadyMethod", jsonObject.toString());
				}
				if (string.equals("Isshebei")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
						Log.i("code", jsonObject.get("code").toString());
						if (mListView != null) {
							mListView.setAdapter(null);
							if (scrollAdapter != null) {
								scrollAdapter.notifyDataSetChanged();
							}
							nextEditFocus((EditText) findViewById(R.id.yimei_gujingequipment_edt));
							ToastUtil.showToast(getApplicationContext(),
									"没有该设备,请核对~!", 0);
						} else {
							nextEditFocus((EditText) findViewById(R.id.yimei_gujingequipment_edt));
							ToastUtil.showToast(getApplicationContext(),
									"没有该设备编号!", 0);
						}
					} else {
						// 去服务器中拿设备号
						Map<String, String> map = new HashMap<String, String>();
						map.put("dbid", MyApplication.DBID);
						map.put("usercode", MyApplication.user);
						map.put("apiId", "assist");
						map.put("assistid", "{MSBMOLIST}");
						map.put("cont", "~sbid='" + shebeihao + "' and zcno='"
								+ zcno + "'");
						httpRequestQueryRecord(MyApplication.MESURL, map,
								"shebei");
					}
				}
				if (string.equals("shebei")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					// 判断设备号在服务器中是否存在
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
						for (int i = 0; i < ((JSONArray) jsonObject
								.get("values")).size(); i++) {
							JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
									.get("values")).get(i));
							jsonValue.put("sbid", jsonValue.get("sbid")
									.toString().toUpperCase());
							mesPrecord new_mes = jsonValue
									.toJavaObject(mesPrecord.class);
							if (!gujing_list.IsSid1AndZncoAndSbid(
									new_mes.getSid1(), new_mes.getZcno(),
									new_mes.getSbid())) {
								// 向本地库添加批次号（多条）
								if (gujing_list.addSbidData(new_mes)) {
									shangliao.setEnabled(true);
									kaigong.setEnabled(true);
									chuzhan.setEnabled(true);
									// 将当前的文本框的设备号传入,查当前文本框的设备的批号
									List<Map<String, Object>> getListMes_Procord = GetListMes_Procord(
											shebeihao, zcno);
									if (getListMes_Procord != null) {
										scrollAdapter = new GuJingScrollAdapter(
												GuJingActivity.this,
												getListMes_Procord);
										mListView.setAdapter(scrollAdapter);
										ToastUtil
												.showToast(
														getApplicationContext(),
														"《"
																+ yimei_gujingequipment_edt
																		.getText()
																		.toString()
																+ "》设备号已加载到列表中",
														0);

									} else {
										shangliao.setEnabled(false);
										chuzhan.setEnabled(false);
										kaigong.setEnabled(false);
										mListView.setAdapter(null);
										scrollAdapter.notifyDataSetChanged();
									}

								} else {
									shangliao.setEnabled(false);
									chuzhan.setEnabled(false);
									kaigong.setEnabled(false);
								}
							}
						}
						// 将当前的文本框的设备号传入,查当前文本框的设备的批号
						List<Map<String, Object>> getListMes_Procord = GetListMes_Procord(
								shebeihao, zcno);
						if (getListMes_Procord != null) {
							scrollAdapter = new GuJingScrollAdapter(
									GuJingActivity.this, getListMes_Procord);
							mListView.setAdapter(scrollAdapter);
							ToastUtil.showToast(getApplicationContext(), "《"
									+ yimei_gujingequipment_edt.getText()
											.toString() + "》设备号已加载到列表中", 0);
						} else {
							shangliao.setEnabled(false);
							chuzhan.setEnabled(false);
							kaigong.setEnabled(false);
							mListView.setAdapter(null);
							scrollAdapter.notifyDataSetChanged();
						}
						if (mListView == null) {
							shangliao.setEnabled(false);
							kaigong.setEnabled(false);
							chuzhan.setEnabled(false);
						} else {
							shangliao.setEnabled(true);
							kaigong.setEnabled(true);
							chuzhan.setEnabled(true);
						}
					} else {
						if (mListView != null) {
							mListView.setAdapter(null);
							if (scrollAdapter != null) {
								scrollAdapter.notifyDataSetChanged();
							}
						}
					}
				}
				if (string.equals("kaigongUpdata")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("id").toString()) == 0
							|| Integer
									.parseInt(jsonObject.get("id").toString()) == 1) {
						for (int i = 0; i < updatekaigongSid1.size(); i++) {
							mesPrecord m = updatekaigongSid1.get(i);
							Log.i("mes", m.toString());
							if (m.getState1().equals("01")
									|| m.getState1().equals("02")) {
								// ------------------------修改服务器的俩张表（开工）
								Map<String, String> updateServerTable = MyApplication
										.UpdateServerTableMethod(
												MyApplication.DBID,
												MyApplication.user,
												m.getState1(), "03",
												m.getSid1(), m.getSlkid(),
												zcno, "200");
								httpRequestQueryRecord(MyApplication.MESURL,
										updateServerTable, "updateServerTable");
								// ------------------------修改服务器的俩张表（开工）

								if (gujing_list.kaigongState1Update(
										m.getSid1(),
										MyApplication.GetServerNowTime())) {
									List<Map<String, Object>> getListMes_Procord = GetListMes_Procord(
											shebeihao, zcno);
									scrollAdapter = new GuJingScrollAdapter(
											GuJingActivity.this,
											getListMes_Procord);
									mListView.setAdapter(scrollAdapter);

									ToastUtil.showToast(gujingActivity,
											"状态修改成功", 0);

								} else {
									ToastUtil.showToast(gujingActivity,
											"状态修改失败", 0);
								}
							}
						}
						updatekaigongSid1.clear();

					} else {
						/* Log.i("kaigongUpdata", "服务器修改开工状态失败"); */
						ToastUtil.showToast(gujingActivity,
								String.valueOf(jsonObject.get("message")), 0);
					}
				}
				if (string.equals("chuzhanUpdata")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("id").toString()) == 0
							|| Integer
									.parseInt(jsonObject.get("id").toString()) == 1) {
						for (int i = 0; i < updatekaigongSid1.size(); i++) {
							mesPrecord m = updatekaigongSid1.get(i);
							if (m.getState1().equals("03")) {
								// ------------------------修改服务器的俩张表（出站）
								Map<String, String> updateServerTable = MyApplication
										.UpdateServerTableMethod(
												MyApplication.DBID,
												MyApplication.user, "03", "04",
												m.getSid1(), m.getSlkid(),
												zcno, "200");
								httpRequestQueryRecord(MyApplication.MESURL,
										updateServerTable, "updateServerTable");

								if (gujing_list.delSid(m.getSid())) {
									Log.i("kaigongUpdata", "本地库已删除~");
									List<Map<String, Object>> getListMes_Procord = GetListMes_Procord(
											shebeihao, zcno);
									scrollAdapter = new GuJingScrollAdapter(
											GuJingActivity.this,
											getListMes_Procord);
									mListView.setAdapter(scrollAdapter);

								} else {
									Log.i("kaigongUpdata", "本地库删除失败");
								}
							}
						}
						updatekaigongSid1.clear();
					} else {
						ToastUtil.showToast(gujingActivity,
								String.valueOf(jsonObject.get("message")), 0);
						// Log.i("kaigongUpdata", "服务器修改开工状态失败");
					}
				}
				if (string.equals("gujing_ptime")) {
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
				ToastUtil.showToastLocation(gujingActivity, e.toString(), 0);
			}
		}
	};

	/**
	 * 设备号|页面刷新数据
	 * 
	 * @return
	 */
	@SuppressWarnings("null")
	public List<Map<String, Object>> GetListMes_Procord(String sbid, String zcno) {
		List<Map<String, Object>> mesList = null;
		if (sbid == null || sbid.equals("")) {
			Log.i("tag", "设备号为空为空，请输入设备号~");
			return null;
		} else {
			// 查批次号和制程
			List<mesPrecord> findList = gujing_list.findList(sbid, zcno);
			if (findList != null) {
				mesList = new ArrayList<Map<String, Object>>();
				Map<String, Object> mesMap = null;
				GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.gujing_item_scroll_title);
				// 添加头滑动事件
				GeneralCHScrollView.add(headerScroll);
				mListView = (ListView) findViewById(R.id.gujing_scroll_list);
				Map<String, String> stateName = MyApplication.getStateName();
				for (int i = 0; i < findList.size(); i++) {
					mesPrecord mesItem = findList.get(i);
					mesMap = new HashMap<String, Object>();
					mesMap.put("checkMap", mesItem);
					mesMap.put("sid1", mesItem.getSid1());
					mesMap.put("slkid", stateName.get(mesItem.getState1()));
					mesMap.put("prd_no", mesItem.getPrd_no());
					mesMap.put("qty", mesItem.getQty());
					mesMap.put("remark", mesItem.getRemark());
					mesList.add(mesMap);
				}
			}
			return mesList;
		}
	}

	/**
	 * 全选方法
	 */
	protected void listenerQuanXuan() {
		gujing_quanxuan.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (scrollAdapter == null) {
					ToastUtil.showToast(getApplicationContext(), "没有数据", 0);
					return;
				}
				if (isChecked) {
					int initCheck = scrollAdapter.initCheck(true);
					scrollAdapter.notifyDataSetChanged();
					if (initCheck == -1) {
						ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
						return;
					}
				} else {
					int initCheck = scrollAdapter.initCheck(false);
					scrollAdapter.notifyDataSetChanged();
					if (initCheck == -1) {
						ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
						return;
					}
				}
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

	OnClickListener RLlistener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mListView.setAdapter(null);
			scrollAdapter.notifyDataSetChanged();
		}
	};

	/**
	 * 获取选中的数据并且请求服务器
	 * 
	 * @param publicState
	 */
	public void UpdateServerData(String publicState) {
		HashMap<Integer, Boolean> state = scrollAdapter.Getstate();

		if (state == null || state.equals(null)) {
			ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
		} else {
			int count = 0;
			for (int j = 0; j < scrollAdapter.getCount(); j++) {
				if (state.get(j)) {
					if (state.get(j) != null) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> map = (HashMap<String, Object>) scrollAdapter
								.getItem(j);

						if (updatekaigongSid1 == null) {
							updatekaigongSid1 = new ArrayList<mesPrecord>();
						}
						mesPrecord m = (mesPrecord) map.get("checkMap");
						updatekaigongSid1.add(m);
						count++;
					}
				}
			}

			String ShowstateName = null;
			if ("kaigongUpdata".equals(publicState)) {
				ShowstateName = "开工";
			} else if ("chuzhanUpdata".equals(publicState)) {
				ShowstateName = "出站";
			}
			if (zcno.equals("11")) {
				if (count > 2) {
					ToastUtil.showToast(GuJingActivity.this, "固晶"
							+ ShowstateName + "最多是两批物料~", 0);
					return;
				}
			}
			switch (count) {
			case 0:
				ToastUtil.showToast(getApplicationContext(), "请选中一条数据", 0);
				break;
			default:
				/*
				 * ToastUtil.showToast(getApplicationContext(), "不可以选多条数据", 0) ;
				 */
				for (int i = 0; i < updatekaigongSid1.size(); i++) {
					mesPrecord mes_precord = updatekaigongSid1.get(i);
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
											zcno, "202");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateTimeMethod, publicState);
						} else if (json.get("state1").toString().equals("02")) {
							Map<String, String> updateTimeMethod = MyApplication
									.updateServerTimeMethod(MyApplication.DBID,
											MyApplication.user, "02", "03",
											mes_precord.getSid(), zuoyeyuan,
											zcno, "202");
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
							if (chooseTime >= Integer.parseInt(ptime
									.get(MyApplication.GUJING_ZCNO))
									|| ptime == null) {
								Map<String, String> updateTimeMethod = MyApplication
										.updateServerTimeMethod(
												MyApplication.DBID,
												MyApplication.user, "03", "04",
												mes_precord.getSid(),
												zuoyeyuan, zcno, "202");
								httpRequestQueryRecord(MyApplication.MESURL,
										updateTimeMethod, publicState);
							} else {
								ToastUtil.showToast(getApplicationContext(),"选中的批次不能出站,已开工"+chooseTime+"分！", 0);
							}
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

	// 返回键判断（在按一次退出）
	private boolean mIsExit;

	@Override
	/**
	 * 双击返回键退出
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果只有一个Activity在运行就退出
		if (myapp.getListCount() == 1) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (mIsExit) {
					this.finish();

				} else {
					ToastUtil.showToast(this, "再按一次退出", Toast.LENGTH_SHORT);
					mIsExit = true;
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							mIsExit = false;
						}
					}, 2000);
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_gujinguser_edt) {
				if (!hasFocus) {
					zuoyeyuan = yimei_gujinguser_edt.getText().toString()
							.trim();
				} else {
					yimei_gujinguser_edt.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_gujingequipment_edt) {
				if (hasFocus) {
					Log.i("foucus", "设备号获取焦点");
					yimei_gujingequipment_edt.setSelectAllOnFocus(true);
				} else {
					shebeihao = yimei_gujingequipment_edt.getText().toString()
							.trim(); // 失去焦点给设备号赋值
					shebeihao = shebeihao.toUpperCase().trim();
					yimei_gujingequipment_edt.setText(shebeihao);
				}
			}
			if (v.getId() == R.id.yimei_gujingproNum_edt) {
				if (hasFocus) {
					yimei_gujingproNum_edt.setSelectAllOnFocus(true);
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
			if (v.getId() == R.id.yimei_gujinguser_edt) {
				if (actionId >= 0) {
					if (yimei_gujinguser_edt.getText().toString().trim()
							.equals("")
							|| yimei_gujinguser_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						return false;
					}
					zuoyeyuan = yimei_gujinguser_edt.getText().toString()
							.trim();
					nextEditFocus((EditText) findViewById(R.id.yimei_gujingequipment_edt));
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_gujingequipment_edt) {
				if (actionId >= 0) {
					shebeihao = yimei_gujingequipment_edt.getText().toString()
							.trim();
					shebeihao = shebeihao.toUpperCase().trim();
					yimei_gujingequipment_edt.setText(shebeihao);
					if (yimei_gujinguser_edt.getText().toString().trim()
							.equals("")
							|| yimei_gujinguser_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_gujinguser_edt));
						return false;
					}
					if (yimei_gujingequipment_edt.getText().toString().trim()
							.equals("")
							|| yimei_gujingequipment_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(gujingActivity, "设备号不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_gujingequipment_edt));
						return false;
					} else {
						sbidGetData(); // 设备号的回车键
						nextEditFocus((EditText) findViewById(R.id.yimei_gujingproNum_edt));
						return true;
					}
				}
			}
			// 生产批号的回车事件
			if (v.getId() == R.id.yimei_gujingproNum_edt) {
				if (actionId >= 0) {
					if (yimei_gujinguser_edt.getText().toString().trim()
							.equals("")
							|| yimei_gujinguser_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_gujinguser_edt));
						return false;
					}
					if (yimei_gujingequipment_edt.getText().toString().trim()
							.equals("")
							|| yimei_gujingequipment_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(gujingActivity, "设备号不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_gujingequipment_edt));
						return false;
					}
					if (yimei_gujingproNum_edt.getText().toString().trim()
							.equals("")
							|| yimei_gujingproNum_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(gujingActivity, "批次号为空~", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_gujingproNum_edt));
						return false;
					} else {
						sid1GetData(); // 生产批号回车事件
						nextEditFocus((EditText) findViewById(R.id.yimei_gujingproNum_edt));
						yimei_gujingproNum_edt.selectAll();
						flag = true;
					}
				}
			}
			return flag;
		}
	};

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
				response(httpPost);
				b.putString("type", type);
				Message m = new Message();
				m.setData(b);
				handler.sendMessage(m);
			}
		}).start();
	}

	public void response(String http) {
		Log.i("jsonjson", http);
	}

	/**
	 * 焦点跳转
	 * 
	 * @param Nextid
	 */
	private void nextEditFocus(EditText Nextid) {
		Nextid.setFocusable(true);
		Nextid.setFocusableInTouchMode(true);
		Nextid.requestFocus();
		Nextid.findFocus();
	}

}
