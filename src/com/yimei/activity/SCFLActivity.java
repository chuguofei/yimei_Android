package com.yimei.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONWriter;
import com.yimei.adapter.SCFL_MaterialAdapter;
import com.yimei.adapter.SCFL_ScanAdapter;
import com.yimei.adapter.ZhiJuQingXiAdapter;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView.OnEditorActionListener;

/**
 * FL_COUNT //查询批次号是否扫描过
 * @author 16332
 *
 */
public class SCFLActivity extends TabActivity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static List<GeneralCHScrollView> GeneralCHScrollView1 = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView; // 发料信息的列表
	private static ListView mListView1; // 工单所需物料列表
	private static SCFL_MaterialAdapter scfl_materialAdapter;
	private static SCFL_ScanAdapter scfl_scanAdapter;
	private static EditText yimei_SCFL_user, yimei_scfl_mo_no,
			yimei_scfl_prd_no, yimei_scfl_bat_no, yimei_scfl_bincode,
			yimei_scfl_qty, yimei__scfl_yingfa, yimei_scfl_yifa;

	private String mo_no; // 制令号
	private String zuoyeyuan;
	private String prd_no; // 材料号
	private String cus_pn; // 批次号
	private String bincode;// bincode;
	private String prd_mark; // 不同的料号，已发清空
	private long qty = 0; // 数量
	private long yinfa = 0; // 应发
	private long yifa = 0; // 已发
	private List<Integer> MaxCid = new ArrayList<Integer>();
	private Map<String, JSONObject> tf_noMap = new HashMap<String, JSONObject>(); // 材料号查询
	private Map<String, String> tf_moBinCodeMap = new HashMap<String, String>(); // bincode查询
	private int state = 0; // 是否第一次提交
	private String sid; // 新增修改判断
	private boolean NoBinCode = false;

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
				try {
					if (tag.equals("生产发料作业员")) {
						yimei_SCFL_user.setText(barcodeData.toUpperCase());
						if (yimei_SCFL_user.getText().toString().equals("")
								|| yimei_SCFL_user.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_SCFL_user);
							return;
						}
						zuoyeyuan = yimei_SCFL_user.getText().toString();
						MyApplication.nextEditFocus(yimei_scfl_mo_no);
					}
					if (tag.equals("生产发料制令号")) {
						yimei_scfl_mo_no.setText(barcodeData.toUpperCase());
						if (yimei_SCFL_user.getText().toString().equals("")
								|| yimei_SCFL_user.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_SCFL_user);
							return;
						}
						if (yimei_scfl_mo_no.getText().toString().equals("")
								|| yimei_scfl_mo_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "制令单号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_mo_no);
							return;
						}
						// 如果换了制令单号清空
						if (scfl_scanAdapter != null) {
							state = 0;
							MaxCid.clear();
							prd_mark = null; // 同料号不同bin的已发数
							if (tf_noMap.size() != 0) {
								tf_noMap.clear();
							}
							if (tf_moBinCodeMap.size() != 0) {
								tf_moBinCodeMap.clear();
							}
							scfl_scanAdapter = null;
							mListView.setAdapter(null);
							if (scfl_materialAdapter != null) {
								scfl_materialAdapter = null;
								mListView1.setAdapter(null);
							}
							if (sid != null || !sid.equals("")) {
								sid = null;
							}
						}
						mo_no = yimei_scfl_mo_no.getText().toString().trim()
								.toUpperCase();
						// 查询制令单号
						OkHttpUtils.getInstance().getServerExecute(
								MyApplication.MESURL,
								null,
								MyApplication.QueryBatNo("SCFLMM_NO",
										"~mo_no='" + mo_no + "'"), null,
								mHander, true, "QueryMo_no");
					}
					if (tag.equals("生产发料材料号")) {
						yimei_scfl_prd_no.setText(barcodeData.toUpperCase());
						if (yimei_SCFL_user.getText().toString().equals("")
								|| yimei_SCFL_user.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_SCFL_user);
							return;
						}
						if (yimei_scfl_mo_no.getText().toString().equals("")
								|| yimei_scfl_mo_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "制令号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_mo_no);
							return;
						}
						if (yimei_scfl_prd_no.getText().toString().equals("")
								|| yimei_scfl_prd_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "材料号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_prd_no);
							return;
						}
						NoBinCode = false;
						if (prd_no != null
								&& !yimei_scfl_prd_no.getText().toString()
										.trim().toUpperCase().equals(prd_no)) {
							yifa = 0;
							yimei_scfl_yifa.setText("0");
							mListView.setAdapter(null);
						}
						prd_no = yimei_scfl_prd_no.getText().toString().trim()
								.toUpperCase();
						System.out.println(prd_no);
						Map<String, JSONObject> a = tf_noMap;
						if (tf_noMap.containsKey(prd_no)) { // 判断材料号是否存在
							String yingfa = ((JSONObject) tf_noMap.get(prd_no))
									.get("qty_rsv").toString();
							yimei__scfl_yingfa.setText(yingfa.substring(0,
									yingfa.indexOf(".")));
							if (scfl_scanAdapter != null) {
								long yifa = 0;
								for (int i = 0; i < scfl_scanAdapter.getCount(); i++) {
									Map<String, String> map = (Map<String, String>) scfl_scanAdapter
											.getItem(i);
									//如果有bin和批次数量就加，不然如果没有bin会报null
									if(map.containsKey("prd_mark")&&map.containsKey("gdic")){
										if (map.get("gdic").equals(prd_no)
												&& map.get("prd_mark").equals(
														prd_mark)) {
											yifa += Integer.parseInt(map.get("qty")
													.toString());
										}
									}
								}
								yimei_scfl_yifa.setText(String.valueOf(yifa));
							}
							yinfa = Integer.parseInt(yingfa.substring(0,
									yingfa.indexOf(".")));

							MyApplication.nextEditFocus(yimei_scfl_bat_no);
						} else {
							ToastUtil
									.showToast(SCFLActivity.this, "没有该材料号！", 0);
							yimei_scfl_prd_no.selectAll();
							return;
						}
					}
					if (tag.equals("生产发料批次号")) {
						yimei_scfl_bat_no.setText(barcodeData);
						if (yimei_SCFL_user.getText().toString().equals("")
								|| yimei_SCFL_user.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_SCFL_user);
							return;
						}
						if (yimei_scfl_mo_no.getText().toString().equals("")
								|| yimei_scfl_mo_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "制令号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_mo_no);
							return;
						}
						if (yimei_scfl_prd_no.getText().toString().equals("")
								|| yimei_scfl_prd_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "材料号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_prd_no);
							return;
						}
						if (yimei_scfl_bat_no.getText().toString().equals("")
								|| yimei_scfl_bat_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "批次号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_bat_no);
							return;
						}
						// ************判断材料号***********************
						boolean check_Prd_no = Check_Prd_no();
						if (check_Prd_no == false) {
							ToastUtil.showToast(SCFLActivity.this, "没有【"
									+ prd_no + "】材料号！", 0);
							yimei_scfl_prd_no.selectAll();
							return;
						}
						// ************判断材料号***********************
						cus_pn = yimei_scfl_bat_no.getText().toString().trim()
								.toUpperCase();
						
						// 查询批次号是否在记录中存在
						OkHttpUtils.getInstance().getServerExecute(
								MyApplication.MESURL,
								null,
								MyApplication.QueryBatNo("FL_COUNT",
										"~gdic='"+prd_no+"' and sph='"+cus_pn+"' and mono='"+mo_no+"'"),
								null, mHander, true, "Query_mo_fla_Count");
					}
					if (tag.equals("生产发料bincode")) {
						yimei_scfl_bincode.setText(barcodeData.toUpperCase());
						if (yimei_SCFL_user.getText().toString().equals("")
								|| yimei_SCFL_user.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_SCFL_user);
							return;
						}
						if (yimei_scfl_mo_no.getText().toString().equals("")
								|| yimei_scfl_mo_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "制令号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_mo_no);
							return;
						}
						if (yimei_scfl_prd_no.getText().toString().equals("")
								|| yimei_scfl_prd_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "材料号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_prd_no);
							return;
						}
						if (NoBinCode == true) {
							if (yimei_scfl_bincode.getText().toString()
									.equals("")
									|| yimei_scfl_bincode.getText().toString() == null) {
								ToastUtil.showToast(SCFLActivity.this,
										"bincode不能为空！", 0);
								MyApplication.nextEditFocus(yimei_scfl_bincode);
								return;
							}
						}
						bincode = yimei_scfl_bincode.getText().toString()
								.trim().toUpperCase();
						if (tf_moBinCodeMap.containsKey(bincode)) {
							MyApplication.nextEditFocus(yimei_scfl_qty);
						} else {
							ToastUtil.showToast(SCFLActivity.this, "该bincode【"
									+ bincode + "】不属于该工单！", 0);
							return;
						}
					}
					if (tag.equals("生产发料数量")) {
						yimei_scfl_qty.setText(barcodeData);
						if (yimei_SCFL_user.getText().toString().equals("")
								|| yimei_SCFL_user.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_SCFL_user);
							return;
						}
						if (yimei_scfl_mo_no.getText().toString().equals("")
								|| yimei_scfl_mo_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "指令号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_mo_no);
							return;
						}
						if (yimei_scfl_prd_no.getText().toString().equals("")
								|| yimei_scfl_prd_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "材料号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_prd_no);
							return;
						}
						if (yimei_scfl_bat_no.getText().toString().equals("")
								|| yimei_scfl_bat_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "批次号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_bat_no);
							return;
						}
						if (NoBinCode == true) {
							if (yimei_scfl_bincode.getText().toString()
									.equals("")
									|| yimei_scfl_bincode.getText().toString() == null) {
								ToastUtil.showToast(SCFLActivity.this,
										"bincode不能为空！", 0);
								MyApplication.nextEditFocus(yimei_scfl_bincode);
								return;
							}
						}
						if (yimei_scfl_qty.getText().toString().equals("")
								|| yimei_scfl_qty.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "数量不能为空！", 0);
							MyApplication.nextEditFocus(yimei_scfl_qty);
							return;
						}
						try {
							int qty = Integer.parseInt(yimei_scfl_qty.getText()
									.toString().trim().toUpperCase());
						} catch (Exception e) {
							showNormalDialog("请输入正确数字!");
							yimei_scfl_qty.selectAll();
							return;
						}
						qty = Integer.parseInt(yimei_scfl_qty.getText().toString().trim().toUpperCase());
						if (yifa > yinfa) {
							showNormalDialog("该binCode【" + bincode + "】数量够了！!");
							return;
						}
						String sys_stated = "3"; // 新增还是修改
						if (state == 0) {
							sys_stated = "3";
						} else if (state == 1) {
							sys_stated = "2";
						}
						// ================主对象
						JSONObject fatherJSON = new JSONObject();
						fatherJSON.put("sbuid", "D0001");
						fatherJSON.put("mono", mo_no);
						fatherJSON.put("sopr", MyApplication.user);
						fatherJSON.put("smake", zuoyeyuan);
						fatherJSON.put("mkdate",
								MyApplication.GetServerNowTime());
						fatherJSON.put("smoid", zuoyeyuan);
						fatherJSON.put("state", "0");
						fatherJSON.put("sorg", "08030000");
						fatherJSON.put("sys_stated", sys_stated);
						fatherJSON.put("moditime",
								MyApplication.GetServerNowTime());
						if (sys_stated.equals("2")) {
							fatherJSON.put("sid", sid);
						}
						// ================子对象
						JSONObject sonJson = new JSONObject();
						if (MaxCid.size() == 0) {
							sonJson.put("cid", 1);
							MaxCid.add(1);
						} else {
							sonJson.put("cid", Collections.max(MaxCid) + 1);
							MaxCid.add(Integer.parseInt(sonJson.get("cid")
									.toString()));
						}
						sonJson.put("gdic", prd_no);
						sonJson.put("mono", mo_no);
						sonJson.put("name", ((JSONObject) tf_noMap.get(prd_no))
								.get("prd_name"));
						sonJson.put("qty", qty);
						sonJson.put("sph", cus_pn);
						sonJson.put("sys_stated", "3");
						sonJson.put("prd_mark", bincode == null ? "" : bincode);
						fatherJSON.put("E0001AWEB", new Object[] { sonJson });
						Map<String, String> httpMapKeyValueMethod = MyApplication
								.httpMapKeyValueMethod(MyApplication.DBID,
										"savedata", MyApplication.user,
										fatherJSON.toString(),
										"E0001WEB(E0001AWEB)", "1");
						OkHttpUtils.getInstance().getServerExecute(
								MyApplication.MESURL, null,
								httpMapKeyValueMethod, null, mHander, true,
								"AddData");

						List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
						Map<String, String> map = new HashMap<String, String>();
						map.put("itm", sonJson.get("cid").toString());
						map.put("gdic", prd_no);
						map.put("name",
								((JSONObject) tf_noMap.get(prd_no)).get(
										"prd_name").toString() == null ? ""
										: ((JSONObject) tf_noMap.get(prd_no))
												.get("prd_name").toString());
						map.put("qty", String.valueOf(qty));
						String a1 = cus_pn;
						map.put("sph", cus_pn);
						map.put("prd_mark", bincode);
						mList.add(map);
						if (scfl_scanAdapter == null) {
							scfl_scanAdapter = new SCFL_ScanAdapter(
									SCFLActivity.this, mList);
							mListView.setAdapter(scfl_scanAdapter);
						} else {
							scfl_scanAdapter.listData.add(map);
							mListView.setAdapter(scfl_scanAdapter);
							scfl_scanAdapter.notifyDataSetChanged();
						}
					}
				} catch (Exception e) {
					ToastUtil.showToast(SCFLActivity.this,
							"扫描：" + e.toString(), 0);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shengchanshangliaoscan);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		TabHost tabHost = this.getTabHost();

		TabSpec tab1 = tabHost.newTabSpec("tab1").setIndicator("扫描区")
				.setContent(R.id.scfl_tab1);
		tabHost.addTab(tab1);
		TabSpec tab2 = tabHost.newTabSpec("tab2").setIndicator("工单所需物料")
				.setContent(R.id.scfl_tab2);
		tabHost.addTab(tab2);

		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.scfl_scan_scroll_title);
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.scfl_scan_scroll_list);
		GeneralCHScrollView headerScroll1 = (GeneralCHScrollView) findViewById(R.id.scfl_materials_scroll_title);
		GeneralCHScrollView1.add(headerScroll1);
		mListView1 = (ListView) findViewById(R.id.scfl_materials_scroll_list);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		yimei_SCFL_user = (EditText) findViewById(R.id.yimei_SCFL_user);
		yimei_scfl_mo_no = (EditText) findViewById(R.id.yimei_scfl_mo_no); // 指令号
		yimei_scfl_prd_no = (EditText) findViewById(R.id.yimei_scfl_prd_no); // 材料号
		yimei_scfl_bat_no = (EditText) findViewById(R.id.yimei_scfl_bat_no); // 批次号
		yimei_scfl_bincode = (EditText) findViewById(R.id.yimei_scfl_bincode);
		yimei_scfl_qty = (EditText) findViewById(R.id.yimei_scfl_qty);
		yimei__scfl_yingfa = (EditText) findViewById(R.id.yimei__scfl_yingfa);
		yimei_scfl_yifa = (EditText) findViewById(R.id.yimei_scfl_yifa);

		yimei__scfl_yingfa.setKeyListener(null); // 禁用应发文本
		yimei_scfl_yifa.setKeyListener(null); // 禁用已发文本

		yimei_SCFL_user.setOnEditorActionListener(editEnter);
		yimei_scfl_mo_no.setOnEditorActionListener(editEnter);
		yimei_scfl_prd_no.setOnEditorActionListener(editEnter);
		yimei_scfl_bat_no.setOnEditorActionListener(editEnter);
		yimei_scfl_bincode.setOnEditorActionListener(editEnter);
		yimei_scfl_qty.setOnEditorActionListener(editEnter);

		yimei_SCFL_user.setOnFocusChangeListener(EditGetFocus);
		yimei_scfl_mo_no.setOnFocusChangeListener(EditGetFocus);
		yimei_scfl_prd_no.setOnFocusChangeListener(EditGetFocus);
		yimei_scfl_bat_no.setOnFocusChangeListener(EditGetFocus);
		yimei_scfl_bincode.setOnFocusChangeListener(EditGetFocus);
		yimei_scfl_qty.setOnFocusChangeListener(EditGetFocus);
	}

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
	 * 判断材料号
	 */
	private boolean Check_Prd_no() {
		boolean flag = true;
		NoBinCode = false;
		if (prd_no != null
				&& !yimei_scfl_prd_no.getText().toString().trim().toUpperCase()
						.equals(prd_no)) {
			yifa = 0;
			yimei_scfl_yifa.setText("0");
			mListView.setAdapter(null);
		}
		prd_no = yimei_scfl_prd_no.getText().toString().trim().toUpperCase();
		System.out.println(prd_no);
		Map<String, JSONObject> a = tf_noMap;
		if (tf_noMap.containsKey(prd_no)) { // 判断材料号是否存在
			String yingfa = ((JSONObject) tf_noMap.get(prd_no)).get("qty_rsv")
					.toString();
			yimei__scfl_yingfa
					.setText(yingfa.substring(0, yingfa.indexOf(".")));
			if (scfl_scanAdapter != null) {
				long yifa = 0;
				for (int i = 0; i < scfl_scanAdapter.getCount(); i++) {
					Map<String, String> map = (Map<String, String>) scfl_scanAdapter
							.getItem(i);
					if (map.get("gdic").equals(prd_no)) {
						yifa += Integer.parseInt(map.get("qty").toString());
					}
				}
				yimei_scfl_yifa.setText(String.valueOf(yifa));
			}
			yinfa = Integer.parseInt(yingfa.substring(0, yingfa.indexOf(".")));
			MyApplication.nextEditFocus(yimei_scfl_bat_no);
			flag = true;
		} else {
			ToastUtil.showToast(SCFLActivity.this, "没有该材料号！", 0);
			yimei_scfl_prd_no.selectAll();
			flag = false;
		}
		return flag;
	}

	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			try {
				if (v.getId() == R.id.yimei_SCFL_user) {
					if (hasFocus) {
						yimei_SCFL_user.setSelectAllOnFocus(true);
					} else {
						zuoyeyuan = yimei_SCFL_user.getText().toString().trim()
								.toUpperCase();
					}
				}
				if (v.getId() == R.id.yimei_scfl_mo_no) {
					if (hasFocus) {
						yimei_scfl_mo_no.setSelectAllOnFocus(true);
					} else {
						mo_no = yimei_scfl_mo_no.getText().toString().trim()
								.toUpperCase();
					}
				}
				if (v.getId() == R.id.yimei_scfl_prd_no) {
					if (hasFocus) {
						yimei_scfl_prd_no.setSelectAllOnFocus(true);
					} else {
						prd_no = yimei_scfl_prd_no.getText().toString().trim()
								.toUpperCase();
					}
				}
				if (v.getId() == R.id.yimei_scfl_bat_no) {
					if (hasFocus) {
						yimei_scfl_bat_no.setSelectAllOnFocus(true);
					} else {
						cus_pn = yimei_scfl_bat_no.getText().toString().trim()
								.toUpperCase();
					}
				}
				if (v.getId() == R.id.yimei_scfl_bincode) {
					if (hasFocus) {
						yimei_scfl_bincode.setSelectAllOnFocus(true);
					} else {
						bincode = yimei_scfl_bincode.getText().toString()
								.trim().toUpperCase();
					}
				}
				if (v.getId() == R.id.yimei_scfl_qty) {
					if (hasFocus) {
						yimei_scfl_qty.setSelectAllOnFocus(true);
					}
				}
			} catch (Exception e) {
				ToastUtil.showToast(SCFLActivity.this, "焦点：" + e.toString(), 0);
			}
		}
	};

	/**
	 * 键盘回车
	 */
	OnEditorActionListener editEnter = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean flag = false;
			try {
				if (v.getId() == R.id.yimei_SCFL_user) { // 作业员
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						if (yimei_SCFL_user.getText().toString().equals("")
								|| yimei_SCFL_user.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_SCFL_user);
							return false;
						}
						zuoyeyuan = yimei_SCFL_user.getText().toString();
						MyApplication.nextEditFocus(yimei_scfl_mo_no);
					}
				}
				if (v.getId() == R.id.yimei_scfl_mo_no) { // 制令号
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						if (yimei_SCFL_user.getText().toString().equals("")
								|| yimei_SCFL_user.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_SCFL_user);
							return false;
						}
						if (yimei_scfl_mo_no.getText().toString().equals("")
								|| yimei_scfl_mo_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "制令号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_mo_no);
							return false;
						}
						if (scfl_scanAdapter != null) {
							state = 0;
							MaxCid.clear();
							prd_mark = null; // 同料号不同bin的已发数
							if (tf_noMap.size() != 0) {
								tf_noMap.clear();
							}
							if (tf_moBinCodeMap.size() != 0) {
								tf_moBinCodeMap.clear();
							}
							scfl_scanAdapter = null;
							mListView.setAdapter(null);
							if (scfl_materialAdapter != null) {
								scfl_materialAdapter = null;
								mListView1.setAdapter(null);
							}
							if (sid != null || !sid.equals("")) {
								sid = null;
							}
						}
						mo_no = yimei_scfl_mo_no.getText().toString().trim()
								.toUpperCase();
						// 查询制令单号
						OkHttpUtils.getInstance().getServerExecute(
								MyApplication.MESURL,
								null,
								MyApplication.QueryBatNo("SCFLMM_NO",
										"~mo_no='" + mo_no + "'"), null,
								mHander, true, "QueryMo_no");
					}
				}
				if (v.getId() == R.id.yimei_scfl_prd_no) { // 材料号
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						if (yimei_SCFL_user.getText().toString().equals("")
								|| yimei_SCFL_user.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_SCFL_user);
							return false;
						}
						if (yimei_scfl_mo_no.getText().toString().equals("")
								|| yimei_scfl_mo_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "制令号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_mo_no);
							return false;
						}
						if (yimei_scfl_prd_no.getText().toString().equals("")
								|| yimei_scfl_prd_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "材料号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_prd_no);
							return false;
						}
						NoBinCode = false;
						if (prd_no != null
								&& !yimei_scfl_prd_no.getText().toString()
										.trim().toUpperCase().equals(prd_no)) {
							yifa = 0;
							yimei_scfl_yifa.setText("0");
							mListView.setAdapter(null);
						}
						prd_no = yimei_scfl_prd_no.getText().toString().trim()
								.toUpperCase();
						System.out.println(prd_no);
						Map<String, JSONObject> a = tf_noMap;
						if (tf_noMap.containsKey(prd_no)) { // 判断材料号是否存在
							String yingfa = ((JSONObject) tf_noMap.get(prd_no))
									.get("qty_rsv").toString();
							yimei__scfl_yingfa.setText(yingfa.substring(0,
									yingfa.indexOf(".")));
							if (scfl_scanAdapter != null) {
								long yifa = 0;
								for (int i = 0; i < scfl_scanAdapter.getCount(); i++) {
									Map<String, String> map = (Map<String, String>) scfl_scanAdapter
											.getItem(i);
									if (map.get("gdic").equals(prd_no)
											&& map.get("prd_mark").equals(
													prd_mark)) {
										yifa += Integer.parseInt(map.get("qty")
												.toString());
									}
								}
								yimei_scfl_yifa.setText(String.valueOf(yifa));
							}
							yinfa = Integer.parseInt(yingfa.substring(0,
									yingfa.indexOf(".")));

							MyApplication.nextEditFocus(yimei_scfl_bat_no);
						} else {
							ToastUtil
									.showToast(SCFLActivity.this, "没有该材料号！", 0);
							yimei_scfl_prd_no.selectAll();
							return false;
						}
					}
				}
				if (v.getId() == R.id.yimei_scfl_bat_no) { // 批次号
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						if (yimei_SCFL_user.getText().toString().equals("")
								|| yimei_SCFL_user.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_SCFL_user);
							return false;
						}
						if (yimei_scfl_mo_no.getText().toString().equals("")
								|| yimei_scfl_mo_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_mo_no);
							return false;
						}
						if (yimei_scfl_prd_no.getText().toString().equals("")
								|| yimei_scfl_prd_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "材料号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_prd_no);
							return false;
						}
						if (yimei_scfl_bat_no.getText().toString().equals("")
								|| yimei_scfl_bat_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "批次号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_bat_no);
							return false;
						}
						boolean check_Prd_no = Check_Prd_no();
						if (check_Prd_no == false) {
							ToastUtil.showToast(SCFLActivity.this, "没有【"
									+ prd_no + "】材料号！", 0);
							yimei_scfl_prd_no.selectAll();
							return false;
						}
						cus_pn = yimei_scfl_bat_no.getText().toString().trim()
								.toUpperCase();
						// 查询批次号是否在记录中存在
						OkHttpUtils.getInstance().getServerExecute(
								MyApplication.MESURL,
								null,
								MyApplication.QueryBatNo("FL_COUNT",
										"~gdic='"+prd_no+"' and sph='"+cus_pn+"' and mono='"+mo_no+"'"),
								null, mHander, true, "Query_mo_fla_Count");
					}
				}
				if (v.getId() == R.id.yimei_scfl_bincode) { // bincode
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						if (yimei_SCFL_user.getText().toString().equals("")
								|| yimei_SCFL_user.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_SCFL_user);
							return false;
						}
						if (yimei_scfl_mo_no.getText().toString().equals("")
								|| yimei_scfl_mo_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_mo_no);
							return false;
						}
						if (yimei_scfl_prd_no.getText().toString().equals("")
								|| yimei_scfl_prd_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "材料号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_prd_no);
							return false;
						}
						if (yimei_scfl_bat_no.getText().toString().equals("")
								|| yimei_scfl_bat_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "批次号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_bat_no);
							return false;
						}
						if (NoBinCode == true) {
							if (yimei_scfl_bincode.getText().toString()
									.equals("")
									|| yimei_scfl_bincode.getText().toString() == null) {
								ToastUtil.showToast(SCFLActivity.this,
										"bincode不能为空！", 0);
								MyApplication.nextEditFocus(yimei_scfl_bincode);
								return false;
							}
						}
						bincode = yimei_scfl_bincode.getText().toString()
								.trim().toUpperCase();
						if (tf_moBinCodeMap.containsKey(bincode)) {
							MyApplication.nextEditFocus(yimei_scfl_qty);
						} else {
							ToastUtil.showToast(SCFLActivity.this, "该bincode【"
									+ bincode + "】不属于该工单！", 0);
							return false;
						}
					}
				}
				if (v.getId() == R.id.yimei_scfl_qty) { // 数量
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						if (yimei_SCFL_user.getText().toString().equals("")
								|| yimei_SCFL_user.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "作业员不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_SCFL_user);
							return false;
						}
						if (yimei_scfl_mo_no.getText().toString().equals("")
								|| yimei_scfl_mo_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "指令号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_mo_no);
							return false;
						}
						if (yimei_scfl_prd_no.getText().toString().equals("")
								|| yimei_scfl_prd_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "材料号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_prd_no);
							return false;
						}
						if (yimei_scfl_bat_no.getText().toString().equals("")
								|| yimei_scfl_bat_no.getText().toString() == null) {
							ToastUtil.showToast(SCFLActivity.this, "批次号不能为空！",
									0);
							MyApplication.nextEditFocus(yimei_scfl_bat_no);
							return false;
						}
						if (NoBinCode == true) {
							if (yimei_scfl_bincode.getText().toString()
									.equals("")
									|| yimei_scfl_bincode.getText().toString() == null) {
								ToastUtil.showToast(SCFLActivity.this,
										"bincode不能为空！", 0);
								MyApplication.nextEditFocus(yimei_scfl_bincode);
								return false;
							}
						}
						if (yimei_scfl_qty.getText().toString().equals("")
								|| yimei_scfl_qty.getText().toString() == null) {
							ToastUtil
									.showToast(SCFLActivity.this, "数量不能为空！", 0);
							MyApplication.nextEditFocus(yimei_scfl_qty);
							return false;
						}
						try {
							int qty = Integer.parseInt(yimei_scfl_qty.getText()
									.toString().trim().toUpperCase());
						} catch (Exception e) {
							showNormalDialog("请输入正确数字!");
							yimei_scfl_qty.selectAll();
							return false;
						}
						qty = Integer.parseInt(yimei_scfl_qty.getText()
								.toString().trim().toUpperCase());
						if (yifa > yinfa) {
							showNormalDialog("该binCode【" + bincode + "】数量够了！!");
							return false;
						}
						String sys_stated = "3"; // 新增还是修改
						if (state == 0) {
							sys_stated = "3";
						} else if (state == 1) {
							sys_stated = "2";
						}
						// ================主对象
						JSONObject fatherJSON = new JSONObject();
						fatherJSON.put("sbuid", "E0001");
						fatherJSON.put("mono", mo_no);
						fatherJSON.put("sopr", MyApplication.user);
						fatherJSON.put("smake", zuoyeyuan);
						fatherJSON.put("mkdate",
								MyApplication.GetServerNowTime());
						fatherJSON.put("smoid", zuoyeyuan);
						fatherJSON.put("state", "0");
						fatherJSON.put("sorg", "08030000");
						fatherJSON.put("sys_stated", sys_stated);
						fatherJSON.put("moditime",
								MyApplication.GetServerNowTime());
						if (sys_stated.equals("2")) {
							fatherJSON.put("sid", sid);
						}
						// ================子对象
						JSONObject sonJson = new JSONObject();
						if (MaxCid.size() == 0) {
							sonJson.put("cid", 1);
							MaxCid.add(1);
						} else {
							sonJson.put("cid", Collections.max(MaxCid) + 1);
							MaxCid.add(Integer.parseInt(sonJson.get("cid")
									.toString()));
						}
						sonJson.put("gdic", prd_no);
						sonJson.put("mono", mo_no);
						sonJson.put("name", ((JSONObject) tf_noMap.get(prd_no))
								.get("prd_name"));
						sonJson.put("qty", qty);
						sonJson.put("sph", cus_pn);
						sonJson.put("sys_stated", "3");
						sonJson.put("prd_mark", bincode == null ? "" : bincode);
						fatherJSON.put("E0001AWEB", new Object[] { sonJson });
						Map<String, String> httpMapKeyValueMethod = MyApplication
								.httpMapKeyValueMethod(MyApplication.DBID,
										"savedata", MyApplication.user,
										fatherJSON.toString(),
										"E0001WEB(E0001AWEB)", "1");
						OkHttpUtils.getInstance().getServerExecute(
								MyApplication.MESURL, null,
								httpMapKeyValueMethod, null, mHander, true,
								"AddData");

						List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
						Map<String, String> map = new HashMap<String, String>();
						map.put("itm", sonJson.get("cid").toString());
						map.put("gdic", prd_no);
						map.put("name",
								((JSONObject) tf_noMap.get(prd_no)).get(
										"prd_name").toString() == null ? ""
										: ((JSONObject) tf_noMap.get(prd_no))
												.get("prd_name").toString());
						map.put("qty", String.valueOf(qty));
						String a1 = cus_pn;
						map.put("sph", cus_pn);
						map.put("prd_mark", bincode);
						mList.add(map);
						if (scfl_scanAdapter == null) {
							scfl_scanAdapter = new SCFL_ScanAdapter(
									SCFLActivity.this, mList);
							mListView.setAdapter(scfl_scanAdapter);
						} else {
							scfl_scanAdapter.listData.add(map);
							mListView.setAdapter(scfl_scanAdapter);
							scfl_scanAdapter.notifyDataSetChanged();
						}
					}
				}
			} catch (Exception e) {
				ToastUtil.showToast(SCFLActivity.this, "回车：" + e.toString(), 0);
			}
			return flag;
		}
	};

	/**
	 * 弹出提示框
	 * 
	 * @param mes
	 */
	private void showNormalDialog(String mes) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				SCFLActivity.this);
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
						if(string.equals("Query_mo_fla_Count")){  //查询批次号在记录中是否存在
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 1) { //存在
										new AlertDialog.Builder(SCFLActivity.this).setTitle("记录重复，是否继续扫描？")
										.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
										    @Override  
										    public void onClick(DialogInterface dialog, int which) {  
										    	//查询批次号
												if (scfl_scanAdapter != null) {
													boolean flag1 = true;
													for (int i = 0; i < scfl_scanAdapter.getCount(); i++) {
														Map<String, String> map = (Map<String, String>) scfl_scanAdapter
																.getItem(i);
														try {
															if ((map.get("sph").equals(cus_pn) && map.get(
																	"gdic").equals(prd_no))
																	|| map.get("sph").equals(cus_pn)) {
																flag1 = false;
															}
														} catch (Exception e) {
															continue;
														}
													}
													if (flag1 == false) {
														showNormalDialog("该批次已绑定");
														yimei_scfl_bat_no.selectAll();
													} else {
														// 查询批号
														OkHttpUtils.getInstance().getServerExecute(
																MyApplication.MESURL,
																null,
																MyApplication.QueryBatNo("SCFLCUS_PN",
																		"~cus_pn='" + cus_pn + "'"),
																null, mHander, true, "Querybat_no");
													}
												} else {
													String a = cus_pn;
													System.out.println(a);
													// 查询批号
													OkHttpUtils.getInstance().getServerExecute(
															MyApplication.MESURL,
															null,
															MyApplication.QueryBatNo("SCFLCUS_PN",
																	"~cus_pn='" + cus_pn + "'"), null,
															mHander, true, "Querybat_no");
												}
										    }  
										}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												yimei_scfl_bat_no.setText("");
												MyApplication.nextEditFocus(yimei_scfl_bat_no);
											}
										}).show();
							}else{
								//查询批次号
								if (scfl_scanAdapter != null) {
									boolean flag1 = true;
									for (int i = 0; i < scfl_scanAdapter.getCount(); i++) {
										Map<String, String> map = (Map<String, String>) scfl_scanAdapter
												.getItem(i);
										try {
											if ((map.get("sph").equals(cus_pn) && map.get(
													"gdic").equals(prd_no))
													|| map.get("sph").equals(cus_pn)) {
												flag1 = false;
											}
										} catch (Exception e) {
											continue;
										}
									}
									if (flag1 == false) {
										showNormalDialog("该批次已绑定");
										yimei_scfl_bat_no.selectAll();
									} else {
										// 查询批号
										OkHttpUtils.getInstance().getServerExecute(
												MyApplication.MESURL,
												null,
												MyApplication.QueryBatNo("SCFLCUS_PN",
														"~cus_pn='" + cus_pn + "'"),
												null, mHander, true, "Querybat_no");
									}
								} else {
									String a = cus_pn;
									System.out.println(a);
									// 查询批号
									OkHttpUtils.getInstance().getServerExecute(
											MyApplication.MESURL,
											null,
											MyApplication.QueryBatNo("SCFLCUS_PN",
													"~cus_pn='" + cus_pn + "'"), null,
											mHander, true, "Querybat_no");
								}
							}
						}
						if (string.equals("QueryMo_no")) { // 制令单号查询
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(SCFLActivity.this,
										"没有该制令单号的数据", 0);
								yimei_scfl_mo_no.selectAll();
								return;
							} else {
								List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
								for (int i = 0; i < ((JSONArray) jsonObject
										.get("values")).size(); i++) {
									JSONObject jsonObj = (JSONObject) ((JSONArray) jsonObject
											.get("values")).get(i);
									// 材料号
									tf_noMap.put(jsonObj.get("prd_no")
											.toString(), jsonObj); // 材料号
									// bincode
									tf_moBinCodeMap.put(
											jsonObj.getString("prd_mark"),
											jsonObj.getString("prd_mark"));
									Map<String, String> map = new HashMap<String, String>();
									map.put("itm", jsonObj.get("itm")
											.toString());
									map.put("prd_no", jsonObj
											.containsKey("prd_no") ? jsonObj
											.get("prd_no").toString() : "");
									map.put("prd_name", jsonObj
											.containsKey("prd_name") ? jsonObj
											.get("prd_name").toString() : "");
									map.put("prd_mark", jsonObj
											.containsKey("prd_mark") ? jsonObj
											.get("prd_mark").toString() : "");
									map.put("wh",
											jsonObj.containsKey("wh") ? jsonObj
													.get("wh").toString() : "");
									map.put("unit",
											jsonObj.containsKey("unit") ? jsonObj
													.get("unit").toString()
													: "");
									map.put("qty_rsv", jsonObj
											.containsKey("qty_rsv") ? jsonObj
											.get("qty_rsv").toString() : "");
									map.put("qty_lost", jsonObj
											.containsKey("qty_lost") ? jsonObj
											.get("qty_lost").toString() : "");
									mList.add(map);
								}
								scfl_materialAdapter = new SCFL_MaterialAdapter(
										SCFLActivity.this, mList);
								mListView1.setAdapter(scfl_materialAdapter);
								MyApplication.nextEditFocus(yimei_scfl_prd_no);
							}
							System.out.println(jsonObject);
						}
						if (string.equals("Querybat_no")) { // 批次号查询  
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) { // 没有查到批次号
								if (tf_noMap.size() != 0) {
									if (((JSONObject) tf_noMap.get(prd_no))
											.containsKey("prd_mark")) {
										if ((((JSONObject) tf_noMap.get(prd_no))
												.get("prd_mark").equals("") || ((JSONObject) tf_noMap
												.get(prd_no)).get("prd_mark") == null)) { // 如果没有bincode
											MyApplication
													.nextEditFocus(yimei_scfl_qty);
										} else {
											MyApplication
													.nextEditFocus(yimei_scfl_bincode);
											NoBinCode = true;
										}
									} else {
										MyApplication
												.nextEditFocus(yimei_scfl_qty);
									}
								} else {
									MyApplication
											.nextEditFocus(yimei_scfl_mo_no);
									showNormalDialog("请输入正确的制令单");
									return;
								}
							} else { // 查到了批次号
								JSONObject jsonObj = (JSONObject) ((JSONArray) jsonObject
										.get("values")).get(0);
								String pklistPrd_no = jsonObj.get("prd_no")
										.toString();
								if (!pklistPrd_no.equals(prd_no)) {
									showNormalDialog("该批次物料号是：【" + pklistPrd_no
											+ "】,与当前料号不符，请检查~");
									yimei_scfl_bat_no.selectAll();
									return;
								}
								String pklistBinCode = jsonObj.get("bincode")
										.toString();
								if (prd_mark != null) {
									if (!pklistBinCode.equals(prd_mark)) {
										yifa = 0;
									}
								}
								prd_mark = pklistBinCode;
								if (!tf_moBinCodeMap.containsKey(pklistBinCode)) {
									showNormalDialog("当前BinCode是【"
											+ pklistBinCode + "】，与指定料号不符，请检查~");
									yimei_scfl_bat_no.selectAll();
									return;
								}
								long pklistQty = Integer.parseInt(jsonObj.get(
										"qty").toString());
								if (pklistQty > yinfa) {
									showNormalDialog("该binCode数量够了！!");
									yimei_scfl_bat_no.selectAll();
									return;
								}
								if (yinfa < yifa) {
									showNormalDialog("该binCode数量够了！!");
									yimei_scfl_bat_no.selectAll();
									return;
								}
								JSONObject cus_pnJSON = (JSONObject) (tf_noMap
										.get(prd_no));
								String sys_stated = "3"; // 新增还是修改
								if (state == 0) {
									sys_stated = "3";
								} else if (state == 1) {
									sys_stated = "2";
								}
								// ================主对象
								JSONObject fatherJSON = new JSONObject();
								fatherJSON.put("sbuid", "E0001");
								fatherJSON.put("mono", mo_no);
								fatherJSON.put("sopr", MyApplication.user);
								fatherJSON.put("smake", zuoyeyuan);
								fatherJSON.put("mkdate",
										MyApplication.GetServerNowTime());
								fatherJSON.put("smoid", zuoyeyuan);
								fatherJSON.put("sys_stated", sys_stated);
								fatherJSON.put("moditime",
										MyApplication.GetServerNowTime());
								if (sys_stated.equals("2")) {
									fatherJSON.put("sid", sid);
								}
								// ================子对象
								JSONObject sonJson = new JSONObject();
								if (MaxCid.size() == 0) {
									sonJson.put("cid", 1);
									MaxCid.add(1);
								} else {
									sonJson.put("cid",
											Collections.max(MaxCid) + 1);
									MaxCid.add(Integer.parseInt(sonJson.get(
											"cid").toString()));
								}
								sonJson.put("gdic", prd_no); // 料号
								sonJson.put("mono", mo_no); // 制令号
								sonJson.put("name", jsonObj.get("prd_name"));
								sonJson.put("qty", jsonObj.get("qty"));
								sonJson.put("sph", cus_pn);
								sonJson.put("prd_mark", pklistBinCode);
								sonJson.put("sys_stated", "3");
								fatherJSON.put("E0001AWEB",
										new Object[] { sonJson });
								Map<String, String> httpMapKeyValueMethod = MyApplication
										.httpMapKeyValueMethod(
												MyApplication.DBID, "savedata",
												MyApplication.user,
												fatherJSON.toString(),
												"E0001WEB(E0001AWEB)", "1");
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null,
										httpMapKeyValueMethod, null, mHander,
										true, "AddData");

								List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
								Map<String, String> map = new HashMap<String, String>();
								map.put("itm", sonJson.get("cid").toString());
								map.put("gdic", pklistPrd_no);
								map.put("name",
										jsonObj.containsKey("prd_name") ? jsonObj
												.getString("prd_name") : "");
								map.put("qty", String.valueOf(pklistQty));
								map.put("sph", cus_pn);
								map.put("prd_mark", pklistBinCode);
								mList.add(map);
								if (scfl_scanAdapter == null) {
									scfl_scanAdapter = new SCFL_ScanAdapter(
											SCFLActivity.this, mList);
									mListView.setAdapter(scfl_scanAdapter);
								} else {
									scfl_scanAdapter.listData.add(map);
									mListView.setAdapter(scfl_scanAdapter);
									scfl_scanAdapter.notifyDataSetChanged();
								}
							}
						}
						if (string.equals("QueryBincode")) { // bincode
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
						}
						if (string.equals("AddData")) { // 主子表添加数据
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id")
									.toString()) == 0) {
								state = 1; // 修改
								if (((JSONObject) jsonObject.get("data"))
										.containsKey("sid")) {
									sid = ((JSONObject) jsonObject.get("data"))
											.get("sid").toString();
								}
								clearText(); // 清空文本框
								MyApplication.nextEditFocus(yimei_scfl_bat_no); // 跳到批次号
								if (scfl_scanAdapter != null) {
									yifa = 0;
									for (int i = 0; i < scfl_scanAdapter
											.getCount(); i++) {
										Map<String, String> item = (Map<String, String>) scfl_scanAdapter
												.getItem(i);
										//如果有bin和批次数量就加，不然如果没有bin会报null
										if(item.containsKey("prd_mark")&&item.containsKey("gdic")){
											if (item.get("gdic").equals(prd_no)
													&& item.get("gdic").equals(
															prd_mark)) {
												yifa += Long.parseLong(item.get(
														"qty").toString());
											}
										}
									}
								} else {
									yifa = 0;
								}
								yimei_scfl_yifa.setText(String.valueOf(yifa));
							} else {
								ToastUtil.showToast(SCFLActivity.this,
										"（savedate）失败", 0);
							}
							System.out.println(jsonObject);
						}
					} catch (Exception e) {
						ToastUtil.showToastLocation(SCFLActivity.this, "服务器请求："
								+ e.toString(), 0);
					}
				}
			}
		}
	};

	/**
	 * 清空文本框
	 */
	private static void clearText() {
		// yimei_scfl_prd_no.setText("");
		yimei_scfl_bat_no.setText("");
		yimei_scfl_bincode.setText("");
		yimei_scfl_qty.setText("");
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

	public static void addHViews1(final GeneralCHScrollView hScrollView) {
		if (!GeneralCHScrollView1.isEmpty()) {
			int size = GeneralCHScrollView1.size();
			GeneralCHScrollView scrollView = GeneralCHScrollView1.get(size - 1);
			final int scrollX = scrollView.getScrollX();
			if (scrollX != 0) {
				mListView1.post(new Runnable() {
					@Override
					public void run() {
						hScrollView.scrollTo(scrollX, 0);
					}
				});
			}
		}
		GeneralCHScrollView1.add(hScrollView);
	}

	public void onScrollChanged1(int l, int t, int oldl, int oldt) {
		for (GeneralCHScrollView scrollView : GeneralCHScrollView1) {
			if (mTouchView != scrollView)
				scrollView.smoothScrollTo(l, t);
		}
	}
}
