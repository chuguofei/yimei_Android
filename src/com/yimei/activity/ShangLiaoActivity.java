package com.yimei.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.adapter.MaterialAdapter;
import com.yimei.adapter.ScanArealAdapter;
import com.yimei.adapter.ScrollAdapter;
import com.yimei.entity.mesPrecord;
import com.yimei.scrollview.CHScrollView;
import com.yimei.scrollview.MaterialsBreakdownCHScrollView;
import com.yimei.scrollview.ScanAreaCHScrollView;
import com.yimei.sqlliteUtil.mesAllMethod;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.HttpUtil;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ShangLiaoActivity extends TabActivity {

	static MyApplication myapp;
	public static ShangLiaoActivity shangliaoActivity;
	public HorizontalScrollView mTouchView;
	public HorizontalScrollView mTouchView1;
	private static List<MaterialsBreakdownCHScrollView> MaterialsBreakdownCHScrollView = new ArrayList<MaterialsBreakdownCHScrollView>();
	private static List<ScanAreaCHScrollView> ScanAreaCHScrollView = new ArrayList<ScanAreaCHScrollView>();
	private static ListView mListView;
	private static ListView mListView1;
	private static MaterialAdapter materialAdapter; // 材料适配器
	private static ScanArealAdapter scanareaAdapter; // 扫描适配器
	private mesPrecord mesObj; // 别的界面传来的对象
	private String type; // 别的界面传过来的类型
	private Button isBtnCheck; // 是否验证数量
	// 界面上的文本框
	private EditText yimei_shangliao_materialCode; // 材料代码
	private EditText yimei_shangliao_materialPihao; //材料批次
	private EditText yimei_shangliao_materialBinCode; //bincode
	private EditText yimei_shangliao_NumDue; // 应发数量
	private EditText yimei_shangliao_NumIssued; // 已发数量
	private EditText yimei_shangliao_Num; // 数量上料
	private TableRow yimei_shangliao_materialCode_TabRow; // 料号
	private TableRow yimei_shangliao_materialPihao_TabRow; // 批号
	private TableRow yimei_shangliao_materialBinCode_TabRow; // BinCode

	private long yinfa; // 判断是否提示
	private long yifa; // 判断是否提示
	private Map<String, String> shuliangMap;
	private int checkqty;
	private List<Integer> MaxCid;
	private String ActivtyCont; //辅助查询明细带的参数
	private String picihao;
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
				if (tag.equals("上料代码")) { // 材料的文本框
					yimei_shangliao_materialCode.setText(barcodeData);
					if (yimei_shangliao_materialCode.getText().toString()
							.trim().equals("")
							|| yimei_shangliao_materialCode.getText()
									.toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(),
								"材料代码不能为空！", 0);
						return;
					}
					cailiao_EnterMethod();
				}
				if(tag.equals("上料批次号")){ //批次号文本框
					yimei_shangliao_materialPihao.setText(barcodeData);
					if (yimei_shangliao_materialPihao.getText().toString().trim().equals("")
							|| yimei_shangliao_materialPihao.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(), "材料批次不能为空！", 0);
						return;
					}
					boolean flag = false;
					for (int i = 0; i < materialAdapter.getCount(); i++) {
						Map<String, String> map = (Map<String, String>) materialAdapter
								.getItem(i);
						if (map.get("Material_cailiaopici").equals(yimei_shangliao_materialPihao.getText()
										.toString().trim())) {
							flag = true;
						}
					}
					if(type.equals("料号")){
						picihao = yimei_shangliao_materialPihao.getText().toString().trim();
					}else{
						if(flag==false){
							yimei_shangliao_materialPihao.selectAll();
							ToastUtil.showToast(shangliaoActivity,"没有该批次号，请核对~",0);
							return;
						}
					}
					MyApplication.nextEditFocus(yimei_shangliao_Num);
				}
				if(tag.equals("上料bincode")){  //bincode文本框
					yimei_shangliao_materialBinCode.setText(barcodeData);
					if (yimei_shangliao_materialBinCode.getText().toString().trim().equals("")
							|| yimei_shangliao_materialBinCode.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(), "bincode不能为空！", 0);
						return;
					}
					boolean flag = false;
					for (int i = 0; i < materialAdapter.getCount(); i++) {
						Map<String, String> map = (Map<String, String>) materialAdapter
								.getItem(i);
						if (map.get("Material_BinCode").equals(yimei_shangliao_materialBinCode.getText()
										.toString().trim())) {
							flag = true;
						}
					}
					if(flag==false){
						yimei_shangliao_materialBinCode.selectAll();
						ToastUtil.showToast(shangliaoActivity,"没有该批bincode号，请核对~",0);
						return;
					}
					MyApplication.nextEditFocus(yimei_shangliao_Num);
				}
				
				
			}
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shangliao);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		Application app = getApplication();
		myapp = (MyApplication) app;
		myapp.addActivity_(this);
		shangliaoActivity = this;

		mesObj = (mesPrecord) getIntent().getSerializableExtra("object");
		type = getIntent().getSerializableExtra("type").toString();
		String activity = getIntent().getSerializableExtra("activity").toString();
		if(activity.equals("tongyong")){
			ActivtyCont = "~mo_no='" + mesObj.getSlkid()+ "'";
		}else if(activity.equals("gujing")){
			ActivtyCont = "~mo_no='" + mesObj.getSlkid()+ "' and (gzl='M01' OR gzl='M03') and upid>'0'";
		}else if(activity.equals("Tongyong_mozu")){
			ActivtyCont = "~mo_no='" + mesObj.getSlkid()+ "'";
		}
		
		
		TabHost tabHost = this.getTabHost();

		TabSpec tab1 = tabHost.newTabSpec("tab1").setIndicator("扫描区")
				.setContent(R.id.tab1);

		tabHost.addTab(tab1);
		TabSpec tab2 = tabHost.newTabSpec("tab2").setIndicator("材料明细")
				.setContent(R.id.tab2);

		tabHost.addTab(tab2);
		tabHost.setOnTabChangedListener(tabChange);
		isBtnCheck = (Button) findViewById(R.id.IsbtnCheck);

		isBtnCheck.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isBtnCheck.getText().toString().equals("√")) {
					isBtnCheck.setText("");
					checkqty = 0;
				} else {
					isBtnCheck.setText("√");
					checkqty = 1;
				}
			}
		});
		loadingMaterial();
		// 加载扫描区数据
		Map<String, String> map = new HashMap<>();
		map.put("dbid", MyApplication.DBID);
		map.put("usercode", MyApplication.user);
		map.put("apiId", "assist");
		map.put("assistid", "{MSMRECORDA}");
		map.put("cont", "~sid='" + mesObj.getSid1() + "'");
		httpRequestQueryRecord(MyApplication.MESURL, map, "QueryMrecorda");
	}

	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		yimei_shangliao_materialCode_TabRow = (TableRow) findViewById(R.id.yimei_shangliao_materialCode_TabRow); //材料号Tab
		yimei_shangliao_materialPihao_TabRow = (TableRow) findViewById(R.id.yimei_shangliao_materialPihao_TabRow);//批次号Tab
		yimei_shangliao_materialBinCode_TabRow = (TableRow) findViewById(R.id.yimei_shangliao_materialBinCode_TabRow);//BincodeTab
		yimei_shangliao_NumDue = (EditText) findViewById(R.id.yimei_shangliao_NumDue); // 应发Id
		yimei_shangliao_NumIssued = (EditText) findViewById(R.id.yimei_shangliao_NumIssued); // 已发Id
		yimei_shangliao_Num = (EditText) findViewById(R.id.yimei_shangliao_Num); // 数量Id
		yimei_shangliao_materialCode = (EditText) findViewById(R.id.yimei_shangliao_materialCode); // 材料代码Id
		yimei_shangliao_materialPihao = (EditText) findViewById(R.id.yimei_shangliao_materialPihao); // 批次号Id
		yimei_shangliao_materialBinCode = (EditText) findViewById(R.id.yimei_shangliao_materialBinCode); // bincodeId
		
		yimei_shangliao_materialCode.setOnEditorActionListener(editEnter); // 材料代码的回车事件
		yimei_shangliao_Num.setOnEditorActionListener(editEnter); // 数量回车的回车事件
		yimei_shangliao_materialPihao.setOnEditorActionListener(editEnter); // 批次号回车的回车事件
		yimei_shangliao_materialBinCode.setOnEditorActionListener(editEnter); // 批次号回车的回车事件
		

		yimei_shangliao_NumDue.setKeyListener(null); // 禁用应发文本
		yimei_shangliao_NumIssued.setKeyListener(null); // 禁用已发文本
		
		yimei_shangliao_materialCode.setOnFocusChangeListener(EditGetFocus); // 材料批号的焦点事件
		yimei_shangliao_materialPihao.setOnFocusChangeListener(EditGetFocus); // 材料批次的焦点事件
		yimei_shangliao_Num.setOnFocusChangeListener(EditGetFocus); // 数量回车的焦点事件
		yimei_shangliao_materialBinCode.setOnFocusChangeListener(EditGetFocus); //bincode焦点时间
		
		if (type.equals("料号")) { // 隐藏批号和bincode
			yimei_shangliao_materialBinCode_TabRow.setVisibility(View.GONE);
		} else if (type.equals("料号+批号")) { // 隐藏Bincode
			yimei_shangliao_materialBinCode_TabRow.setVisibility(View.GONE);
		} else if (type.equals("料号+bincode")) {   // 隐藏批次号
			yimei_shangliao_materialPihao_TabRow.setVisibility(View.GONE);
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
	 * 回车事件（通用）
	 */
	OnEditorActionListener editEnter = new OnEditorActionListener() {

		@SuppressWarnings("unchecked")
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (v.getId() == R.id.yimei_shangliao_materialCode) { //材料批号
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					cailiao_EnterMethod();
				}
			}
			if (v.getId() == R.id.yimei_shangliao_Num) { // 数量回车
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_shangliao_materialCode.getText().toString()
							.trim().equals("")) {
						nextEditFocus(yimei_shangliao_materialCode);
						yimei_shangliao_Num.setText("");
						ToastUtil.showToast(shangliaoActivity, "材料号不能为空!", 0);
						return false;
					}
					if(type.equals("料号+批号")){
						if (yimei_shangliao_materialPihao.getText().toString().trim().equals("")
								|| yimei_shangliao_materialPihao.getText().toString().trim() == null) {
							ToastUtil.showToast(getApplicationContext(), "材料批次不能为空！", 0);
							MyApplication.nextEditFocus(yimei_shangliao_materialPihao);
							return false;
						}
					}
					// =======================数量直接获取焦点
					// 判断材料号输入是否正确=================
					boolean isFlag = false;
					// 循环材料明细列表
					for (int i = 0; i < materialAdapter.getCount(); i++) {
						Map<String, String> map = (Map<String, String>) materialAdapter
								.getItem(i);
						Log.i("Tag", map.get("Material_cailiaoNum"));
						if(type.equals("料号")){
							if (map.get("Material_cailiaoNum").equals(
									yimei_shangliao_materialCode.getText()
											.toString().trim())) {
								isFlag = true;
							}
						}else if(type.equals("料号+批号")){
							if (map.get("Material_cailiaoNum").equals(
									yimei_shangliao_materialCode.getText()
											.toString().trim())&&map.get("Material_cailiaopici")
											.equals(yimei_shangliao_materialPihao.getText()
											.toString().trim())) {
								
								isFlag = true;
							}
						}else if(type.equals("料号+bincode")){
							if (map.get("Material_cailiaoNum").equals(
									yimei_shangliao_materialCode.getText()
											.toString().trim())&&map.get("Material_cailiaopici")
											.equals(yimei_shangliao_materialBinCode.getText()
											.toString().trim())) {
								isFlag = true;
							}
						}
						
					}
					if(type.equals("料号")){
						// 如果材料代码不存在
						if (isFlag == false) {
							showNormalDialog("没有该材料《"
									+ yimei_shangliao_materialCode.getText()
											.toString().trim() + "》");
							nextEditFocus(yimei_shangliao_materialCode);
							yimei_shangliao_materialCode.selectAll();
							return false;
						}
					}
					if(type.equals("料号+批号")){
						// 如果材料代码不存在
						if (isFlag == false) {
							showNormalDialog("没有料号或批号，请检查~");
							nextEditFocus(yimei_shangliao_materialPihao);
							yimei_shangliao_materialPihao.selectAll();
							return false;
						}
					}
					if(type.equals("料号+bincode")){
						// 如果材料代码不存在
						if (isFlag == false) {
							showNormalDialog("没有料号或bincode，请检查~");
							nextEditFocus(yimei_shangliao_materialBinCode);
							yimei_shangliao_materialBinCode.selectAll();
							return false;
						}
					}
					
					// =======================数量直接获取焦点
					// 判断材料号输入是否正确=================

					try {
						long num = Integer.parseInt(yimei_shangliao_Num
								.getText().toString().trim());
					} catch (NumberFormatException e) {
						ToastUtil.showToast(shangliaoActivity, "请输入数量", 0);
						return false;
					}
					if (isBtnCheck.getText().toString().equals("√")) { // 如果选中验证数量
						if (yinfa < yifa) {
							showNormalDialog("该材料号《"
									+ yimei_shangliao_materialCode.getText()
											.toString().trim() + "》上料数量已超！");
							return false;
						}
						if (Long.parseLong(yimei_shangliao_Num.getText()
								.toString().trim()) > yinfa) {
							showNormalDialog("该材料号《"
									+ yimei_shangliao_materialCode.getText()
											.toString().trim() + "》上料数量已超！");
							return false;
						}
					} else {
						if (yinfa < yifa) {
							showNormalDialog("该材料号《"
									+ yimei_shangliao_materialCode.getText()
											.toString().trim() + "》上料数量已超！");
							return false;
						}
					}

					JSONObject jsonstr = new JSONObject();
					// =======================主对象=======================================
					jsonstr.put("sid", mesObj.getSid1());
					jsonstr.put("sid1", mesObj.getSid1());
					jsonstr.put("slkid", mesObj.getSlkid());
					jsonstr.put("zcno", mesObj.getZcno());
					jsonstr.put("checkid", "0");
					jsonstr.put("checkqty", checkqty);
					jsonstr.put("sbid", mesObj.getSbid());
					jsonstr.put("op", mesObj.getOp());
					jsonstr.put("sys_stated", "2");
					jsonstr.put("dcid", GetAndroidMacUtil.getMac());
					// =======================主对象=======================================
					// =======================子对象=======================================
					JSONObject jsonSon = new JSONObject();
					Log.i("a", shuliangMap.toString());
					if (MaxCid == null) {
						jsonSon.put("cid", 1);
					} else {
						jsonSon.put("cid", Collections.max(MaxCid) + 1);
					}
					jsonSon.put("prd_no",
							shuliangMap.get("Material_cailiaoNum"));
					jsonSon.put("bat_no",picihao==null?shuliangMap.get("Material_cailiaopici"):picihao);
					jsonSon.put("BinCode", shuliangMap.get("Material_BinCode"));
					jsonSon.put("prd_name",shuliangMap.get("Material_cailiaoName"));
					jsonSon.put("qty", yimei_shangliao_Num.getText());
					jsonSon.put("dcid", GetAndroidMacUtil.getMac());
					jsonSon.put("op", mesObj.getOp());
					jsonstr.put("sbid", mesObj.getSbid());
					jsonstr.put("zcno", mesObj.getZcno());
					jsonSon.put("sys_stated", "3");
					// =======================子对象=======================================
					jsonstr.put("D0040AWEB", new Object[] { jsonSon });
					Log.i("jsonstr", jsonstr.toJSONString());
					Map<String, String> httpMapKeyValueMethod = MyApplication
							.httpMapKeyValueMethod(MyApplication.DBID,
									"savedata", MyApplication.user,
									jsonstr.toString(), "D0040WEB(D0040AWEB)",
									"1");
					httpRequestQueryRecord(MyApplication.MESURL,
							httpMapKeyValueMethod, "insertServerMaterials");
				}
			}
			if(v.getId() == R.id.yimei_shangliao_materialPihao){  //料号+批号
				if(type.equals("料号+批号")){
					if (yimei_shangliao_materialPihao.getText().toString().trim().equals("")
							|| yimei_shangliao_materialPihao.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(), "材料批次不能为空！", 0);
						return false;
					}
				}
				boolean flag = false;
				for (int i = 0; i < materialAdapter.getCount(); i++) {
					Map<String, String> map = (Map<String, String>) materialAdapter
							.getItem(i);
					if (map.get("Material_cailiaopici").equals(yimei_shangliao_materialPihao.getText()
									.toString().trim())) {
						flag = true;
					}
				}
				if(type.equals("料号")){
					picihao = yimei_shangliao_materialPihao.getText().toString().trim();
				}else{
					if(flag==false){
						yimei_shangliao_materialPihao.selectAll();
						ToastUtil.showToast(shangliaoActivity,"没有该批次号，请核对~",0);
						return false;
					}
				}
				MyApplication.nextEditFocus(yimei_shangliao_Num);
			}
			if(v.getId() == R.id.yimei_shangliao_materialBinCode){ //料号+bincode
				if(type.equals("料号bincode")){
					if (yimei_shangliao_materialBinCode.getText().toString().trim().equals("")
							|| yimei_shangliao_materialBinCode.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(), "bincode不能为空！", 0);
						return false;
					}
				}
				boolean flag = false;
				for (int i = 0; i < materialAdapter.getCount(); i++) {
					Map<String, String> map = (Map<String, String>) materialAdapter
							.getItem(i);
					if (map.get("Material_BinCode").equals(yimei_shangliao_materialBinCode.getText()
									.toString().trim())) {
						flag = true;
					}
				}
				if(flag==false){
					ToastUtil.showToast(shangliaoActivity,"没有该批bincode号，请核对~",0);
					return false;
				}
				MyApplication.nextEditFocus(yimei_shangliao_Num);
			}
			return false;
		}
	};

	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_shangliao_materialCode) {
				if (hasFocus) {
					yimei_shangliao_materialCode.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_shangliao_materialPihao) {
				if (hasFocus) {
					yimei_shangliao_materialPihao.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_shangliao_materialBinCode) {
				if (hasFocus) {
					yimei_shangliao_materialBinCode.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_shangliao_Num) {
				if (hasFocus) {
					yimei_shangliao_Num.setSelectAllOnFocus(true);
				}
			}
		}
	};
	
	/**
	 * 弹出提示框
	 * 
	 * @param mes
	 */
	private void showNormalDialog(String mes) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				ShangLiaoActivity.this);
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

	OnTabChangeListener tabChange = new OnTabChangeListener() {

		@Override
		public void onTabChanged(String tabId) {
			if (tabId.equals("tab1")) {

			}
			if (tabId.equals("tab2")) {
				loadingMaterial();
			}
			// ToastUtil.showToast(getApplicationContext(), tabId, 0);
		}
	};

	/**
	 * 材料明细
	 */
	private void loadingMaterial() {
		Map<String, String> mapSbid = new HashMap<String, String>();
		mapSbid.put("dbid", MyApplication.DBID);
		mapSbid.put("usercode", MyApplication.user);
		mapSbid.put("apiId", "assist");
		mapSbid.put("assistid", "{MSMLLIST}");
		mapSbid.put("cont", ActivtyCont); //根据不同的页面查询不同的明细
		httpRequestQueryRecord(MyApplication.MESURL, mapSbid,
				"MaterialDetailed");
	}

	/**
	 * 接收http请求返回值
	 */
	@SuppressLint("HandlerLeak")
	final android.os.Handler handler = new android.os.Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle b = msg.getData();
			String type = b.getString("type");
			try {
				if ("MaterialDetailed".equals(type)) { // 材料号
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
						ToastUtil
								.showToast(getApplicationContext(), "该工单号没有材料!", 0);
					} else {

						JSONArray Values = (JSONArray) jsonObject.get("values");
						List<Map<String, String>> listMap = new ArrayList<>();
						Map<String, String> map = null;
						for (int i = 0; i < Values.size(); i++) {
							Map<String, String> Valuesmap = (Map<String, String>) Values
									.get(i);
							map = new HashMap<String, String>();
							map.put("Material_xiangci",
									String.valueOf(Valuesmap.get("itm")));
							map.put("Material_cailiaoNum", Valuesmap.get("prd_no"));
							map.put("Material_cailiaoName",
									Valuesmap.get("prd_name"));
							map.put("Material_BinCode",
									Valuesmap.get("bincode") == null ? ""
											: Valuesmap.get("bincode"));
							map.put("Material_cailiaopici",
									Valuesmap.get("bat_no") == null ? ""
											: Valuesmap.get("bat_no"));
							String Material_SumNum;
							if (String.valueOf(Valuesmap.get("qty_rsv")) != null) {
								Material_SumNum = String.valueOf(
										Valuesmap.get("qty_rsv")).substring(
										0,
										String.valueOf(Valuesmap.get("qty_rsv"))
												.indexOf("."));
							} else {
								Material_SumNum = "";
							}
							map.put("Material_SumNum", Material_SumNum);
							map.put("Material_tidaipin",
									Valuesmap.get("prd_no_chg"));
							map.put("Material_info", "");
							listMap.add(map);
						}
						MaterialsBreakdownCHScrollView headerScroll = (MaterialsBreakdownCHScrollView) findViewById(R.id.MaterialsBreakdowni_scroll_title);
						// 添加头滑动事件
						MaterialsBreakdownCHScrollView.add(headerScroll);
						mListView = (ListView) findViewById(R.id.MaterialsBreakdown_scroll_list);
						materialAdapter = new MaterialAdapter(
								ShangLiaoActivity.this, listMap);
						mListView.setAdapter(materialAdapter);
						materialAdapter.notifyDataSetChanged();
						Log.i("jsonObj", Values.toString());
					}
				}
				if ("QueryMrecorda".equals(type)) { // 扫描区
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == -1) { // 不存在辅助
						showNormalDialog("系统错误，请联系管理员~");
						return;
					}
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) { // 扫描区添加值
						JSONArray Values = (JSONArray) jsonObject.get("values");
						List<Map<String, String>> listMap = new ArrayList<>();
						Map<String, String> map = null;
						MaxCid = new ArrayList<>();
						for (int i = 0; i < Values.size(); i++) {
							Map<String, String> Valuesmap = (Map<String, String>) Values
									.get(i);
							map = new HashMap<String, String>();
							map.put("ScanArea_xiangci",
									String.valueOf(Valuesmap.get("cid")));
							map.put("ScannArea_cailiaodaima",
									Valuesmap.get("prd_no"));
							map.put("ScannArea_cailiaopici",
									Valuesmap.get("bat_no"));
							map.put("ScannArea_cailiaoName",
									Valuesmap.get("prd_name"));
							map.put("ScannArea_BinCode", Valuesmap.get("bincode"));
							map.put("ScannArea_cailiaoshuNum",
									String.valueOf(Valuesmap.get("qty")));
							map.put("ScannArea_verification",
									Valuesmap.get("yzrem"));
							map.put("ScannArea_unit",
									Valuesmap.get("unit") == null ? "" : String
											.valueOf(Valuesmap.get("unit")));

							MaxCid.add(Integer.parseInt(String.valueOf(Valuesmap
									.get("cid")))); // 取最大值的集合
							listMap.add(map);
						}
						Log.i("num", Collections.max(MaxCid).toString());
						ScanAreaCHScrollView headerScroll = (ScanAreaCHScrollView) findViewById(R.id.ScanArea_scroll_title);
						// 添加头滑动事件
						ScanAreaCHScrollView.add(headerScroll);
						mListView1 = (ListView) findViewById(R.id.ScanArea_scroll_list);
						scanareaAdapter = new ScanArealAdapter(
								ShangLiaoActivity.this, listMap);
						mListView1.setAdapter(scanareaAdapter);
						scanareaAdapter.notifyDataSetChanged();

						// 显示已发
						long yifaCount = 0;
						if (scanareaAdapter != null) {
							// 循环扫描的列表（判断数量有没有超出）
							for (int i = 0; i < scanareaAdapter.getCount(); i++) {
								Map<String, String> map1 = (Map<String, String>) scanareaAdapter
										.getItem(i);
								if (map1.get("ScannArea_cailiaodaima").equals(
										yimei_shangliao_materialCode.getText()
												.toString().trim())) {
									yifaCount += Long.parseLong(map1.get(
											"ScannArea_cailiaoshuNum").substring(
											0,
											map1.get("ScannArea_cailiaoshuNum")
													.indexOf(".")));

								}
							}
						}

						// 已发数量
						yimei_shangliao_NumIssued.setText(String.valueOf(yifaCount)
								.equals("0") ? "" : String.valueOf(yifaCount));
					} else {
						if (mListView1 != null) {
							// mListView1.setAdapter(null);
							mListView1 = null;
							scanareaAdapter.notifyDataSetChanged();
						}
					}

					Log.i("jsonObject", jsonObject.toString());
				}
				if ("insertServerMaterials".equals(type)) { // 向服务器添加主子对象成功后
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					if (Integer.parseInt(jsonObject.get("id").toString()) == 0) {
						if (MaxCid != null) {
							MaxCid.clear();
						}
						ToastUtil.showToast(shangliaoActivity, "添加成功", 0);
						nextEditFocus(yimei_shangliao_materialCode);
						yimei_shangliao_materialCode.selectAll();

						// 加载扫描区数据
						Map<String, String> map = new HashMap<>();
						map.put("dbid", MyApplication.DBID);
						map.put("usercode", MyApplication.user);
						map.put("apiId", "assist");
						map.put("assistid", "{MSMRECORDA}");
						map.put("cont", "~sid='" + mesObj.getSid1() + "'");
						httpRequestQueryRecord(MyApplication.MESURL, map,
								"QueryMrecorda");

						if (mesObj.getState1().equals("01")) {
							Map<String, String> updateTimeMethod = MyApplication
									.updateServerTimeMethod(MyApplication.DBID,
											MyApplication.user, "01", "02",
											mesObj.getSid(), mesObj.getOp(),
											mesObj.getZcno(), "202");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateTimeMethod, "UpdataServerShangLiaoState");
						}

					} else {
						ToastUtil.showToast(shangliaoActivity, "服务器处理异常", 0);
					}
				}
				if ("UpdataServerShangLiaoState".equals(type)) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					if (Integer.parseInt(jsonObject.get("id").toString()) == 1) {

						if (mesObj.getState1().equals("01")) {
							// ------------------------修改服务器的俩张表（开工）
							Map<String, String> updateServerTable = MyApplication
									.UpdateServerTableMethod(MyApplication.DBID,
											MyApplication.user, "01", "02",
											mesObj.getSid1(), mesObj.getSlkid(),
											mesObj.getZcno(), "200");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateServerTable, "updateServerTable");
							// ------------------------修改服务器的俩张表（开工）
							mesAllMethod mesUpdatelocal = new mesAllMethod(
									ShangLiaoActivity.this);
							if (mesUpdatelocal.shanliaoState1Update(mesObj
									.getSid1())) { // 修改本地状态
								/*
								 * ToastUtil.showToast(ShangLiaoActivity.this,
								 * "上料页面修改状态成功", 0);
								 */
								mesObj.setState1("02"); // 修改传来的状态
							} else {
								ToastUtil.showToast(ShangLiaoActivity.this,
										"状态修改失败", 0);
							}
						}

						Log.i("mesObj", mesObj.toString());
					}
				}
			} catch (Exception e) {
				ToastUtil.showToast(ShangLiaoActivity.this,e.toString(),0);
			}
		}
	};

	/**
	 * http请求
	 * 
	 * @param baseUrl
	 * @param map
	 */
	@SuppressWarnings("unused")
	private void httpRequestQueryRecord(final String baseUrl,
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

	/**
	 * 材料代码回车
	 * 
	 * @param hScrollView
	 */
	private boolean cailiao_EnterMethod() {
		if (yimei_shangliao_materialCode.getText().toString().trim().equals("")
				|| yimei_shangliao_materialCode.getText().toString().trim() == null) {
			ToastUtil.showToast(shangliaoActivity, "材料代码不能为空！", 0);
			return false;
		}
		boolean isFlag = false;
		Map<String, String> mesShow = null;
		// 循环材料明细列表
		for (int i = 0; i < materialAdapter.getCount(); i++) {
			Map<String, String> map = (Map<String, String>) materialAdapter
					.getItem(i);
			Log.i("Tag", map.get("Material_cailiaoNum"));
			if (map.get("Material_cailiaoNum").equals(
					yimei_shangliao_materialCode.getText().toString().trim())) {
				isFlag = true;
				mesShow = map;
			}
		}
		// 如果材料代码不存在
		if (isFlag == false) {
			showNormalDialog("没有该材料《"
					+ yimei_shangliao_materialCode.getText().toString().trim()
					+ "》");
			yimei_shangliao_materialCode.selectAll();
			return false;
		} else {
			// 材料代码存在
			long yifaCount = 0;
			if (mListView1 != null) {
				if (scanareaAdapter != null) {
					// 循环扫描的列表（判断数量有没有超出）
					for (int i = 0; i < scanareaAdapter.getCount(); i++) {
						@SuppressWarnings("unchecked")
						Map<String, String> map = (Map<String, String>) scanareaAdapter
								.getItem(i);
						if (map.get("ScannArea_cailiaodaima").equals(
								yimei_shangliao_materialCode.getText()
										.toString().trim())) {
							yifaCount += Long.parseLong(map.get(
									"ScannArea_cailiaoshuNum").substring(
									0,
									map.get("ScannArea_cailiaoshuNum").indexOf(
											".")));
						}
					}
				}
			}

			// 应发数量
			yimei_shangliao_NumDue.setText(mesShow.get("Material_SumNum"));
			// 已发数量
			yimei_shangliao_NumIssued.setText(String.valueOf(yifaCount));

			yinfa = Long.parseLong(yimei_shangliao_NumDue.getText().toString());
			yifa = Long.parseLong(yimei_shangliao_NumIssued.getText()
					.toString());

			shuliangMap = mesShow;

			if (yinfa < yifa) {
				showNormalDialog("该材料号《"
						+ yimei_shangliao_materialCode.getText().toString()
								.trim() + "》上料数量已超！");
				return false;
			}
			if(type.equals("料号")){
				nextEditFocus(yimei_shangliao_materialPihao);
			}else if(type.equals("料号+bincode")){
				nextEditFocus(yimei_shangliao_materialBinCode);
			}else{
				nextEditFocus(yimei_shangliao_materialPihao);
			}

		}
		return true;
	}

	
	public static void addHViews(
			final MaterialsBreakdownCHScrollView hScrollView) {
		if (!MaterialsBreakdownCHScrollView.isEmpty()) {
			int size = MaterialsBreakdownCHScrollView.size();
			MaterialsBreakdownCHScrollView scrollView = MaterialsBreakdownCHScrollView
					.get(size - 1);
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
		MaterialsBreakdownCHScrollView.add(hScrollView);
	}

	public static void addHViews1(final ScanAreaCHScrollView hScrollView) {
		if (!ScanAreaCHScrollView.isEmpty()) {
			int size = ScanAreaCHScrollView.size();
			ScanAreaCHScrollView scrollView = ScanAreaCHScrollView
					.get(size - 1);
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
		ScanAreaCHScrollView.add(hScrollView);
	}

	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		for (MaterialsBreakdownCHScrollView scrollView : MaterialsBreakdownCHScrollView) {
			if (mTouchView != scrollView)
				scrollView.smoothScrollTo(l, t);
		}
	}

	public void onScrollChanged1(int l, int t, int oldl, int oldt) {
		for (ScanAreaCHScrollView scrollView : ScanAreaCHScrollView) {
			if (mTouchView1 != scrollView)
				scrollView.smoothScrollTo(l, t);
		}
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

	/**
	 * 监听返回键
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果只有一个Activity在运行就退出
		/* ToastUtil.showToast(getApplicationContext(),"按了返回键",0); */
		return super.onKeyDown(keyCode, event);
	}
}
