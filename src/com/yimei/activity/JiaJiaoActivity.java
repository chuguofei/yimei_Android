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
import com.yimei.adapter.JiaJiaoAdapter;
import com.yimei.adapter.MaterialAdapter;
import com.yimei.adapter.ScanArealAdapter;
import com.yimei.scrollview.JiaJiaoCHScrollView;
import com.yimei.scrollview.ScanAreaCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.HttpUtil;
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
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * 
 * @author Administrator
 * 辅助:
 *   MESGLUEJOB //查询胶杯号
 *   MESEQUTM // 查询设备号
 *   MSBMOLIST_JJ //加胶前查询的辅助 （ID:Query_Precord_slkid_1 //往设备上加胶）
 *   
 */
public class JiaJiaoActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<JiaJiaoCHScrollView> JiaJiaoCHScrollView = new ArrayList<JiaJiaoCHScrollView>();
	private static ListView mListView;
	private static JiaJiaoAdapter JiaJiaoAdapter; // 加胶适配器

	private EditText yimei_jiajiao_user;
	private EditText yimei_jiajiao_jidiaojinumber;
	private EditText yimei_jiajiao_jiaobeipihao;

	private String jiaobeipihao;
	private String zuoyeyuan;
	private String sbid;
	private String Production_slkid = ""; //正在生产的工单 
	
	/**
	 * 获取pda扫描（广播）
	 */
	private BroadcastReceiver barcodeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (MyApplication.INTENT_ACTION_SCAN_RESULT.equals(intent
					.getAction())) {
				View rootview = getCurrentFocus();
				Object tag =rootview.findFocus().getTag();
				if(tag==null){
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
				if (tag.equals("加胶作业员")) { // 作业员  
					Log.i("id", "作业员");
					yimei_jiajiao_user.setText(barcodeData);
					zuoyeyuan = yimei_jiajiao_user.getText().toString().trim();
					if (yimei_jiajiao_user.getText().toString().trim()
							.equals("")
							|| yimei_jiajiao_user.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(), "作业员不能为空",
								0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_user));
						return;
					}
					nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber));
				}
				if (tag.equals("加胶胶机编号")) { // 胶机编号 
					yimei_jiajiao_jidiaojinumber.setText(barcodeData);
					sbid = yimei_jiajiao_jidiaojinumber.getText().toString()
							.trim();
					if (yimei_jiajiao_user.getText().toString().trim()
							.equals("")
							|| yimei_jiajiao_user.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(), "作业员不能为空",
								0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_user));
						return;
					}
					if (yimei_jiajiao_jidiaojinumber.getText().toString()
							.trim().equals("")
							|| yimei_jiajiao_jidiaojinumber.getText()
									.toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(),
								"胶机编号不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber));
						return;
					}
					nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jiaobeipihao));
					Map<String, String> mapSbid = new HashMap<String, String>();
					mapSbid.put("dbid", MyApplication.DBID);
					mapSbid.put("usercode", MyApplication.user);
					mapSbid.put("apiId", "assist");
					mapSbid.put("assistid", "{MESEQUTM}");
					mapSbid.put("cont", "~id='" + sbid + "' and zc_id='31'");
					httpRequestQueryRecord(MyApplication.MESURL, mapSbid,
							"Isshebei");
				}
				if (tag.equals("加胶胶杯批号")) { // 胶杯批号
					yimei_jiajiao_jiaobeipihao.setText(barcodeData);
					if (yimei_jiajiao_user.getText().toString().trim()
							.equals("")
							|| yimei_jiajiao_user.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(), "作业员不能为空",
								0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_user));
						return;
					}
					if (yimei_jiajiao_jidiaojinumber.getText().toString()
							.trim().equals("")
							|| yimei_jiajiao_jidiaojinumber.getText()
									.toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(),
								"胶机编号不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber));
						return;
					}
					if (yimei_jiajiao_jiaobeipihao.getText().toString().trim()
							.equals("")
							|| yimei_jiajiao_jiaobeipihao.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(getApplicationContext(),
								"胶杯批号不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jiaobeipihao));
						return;
					}
					jiaobeipihao = yimei_jiajiao_jiaobeipihao.getText().toString().trim();
					nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jiaobeipihao));
					Map<String, String> mapSbid = new HashMap<String, String>();
					mapSbid.put("dbid", MyApplication.DBID);
					mapSbid.put("usercode", MyApplication.user);
					mapSbid.put("apiId", "assist");
					mapSbid.put("assistid", "{MESEQUTM}");
					mapSbid.put("cont", "~id='" + sbid + "' and zc_id='31'");
					httpRequestQueryRecord(MyApplication.MESURL, mapSbid,
							"Isshebei1");
				}

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jiajiao);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		String t_op = getIntent().getStringExtra("op");
		if(t_op!=null){
			zuoyeyuan = t_op;
		}
		String t_sbid = getIntent().getStringExtra("sbid");
		if(t_sbid!=null){
			sbid = t_sbid;
		}
	}

	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		yimei_jiajiao_user = (EditText) findViewById(R.id.yimei_jiajiao_user);
		yimei_jiajiao_jidiaojinumber = (EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber);
		yimei_jiajiao_jiaobeipihao = (EditText) findViewById(R.id.yimei_jiajiao_jiaobeipihao);
		
		if(zuoyeyuan!=null){
			yimei_jiajiao_user.setText(zuoyeyuan);
		}
		if(yimei_jiajiao_jidiaojinumber!=null){
			yimei_jiajiao_jidiaojinumber.setText(sbid);
		}
		
		yimei_jiajiao_user.setOnEditorActionListener(userEnter);
		yimei_jiajiao_jidiaojinumber.setOnEditorActionListener(sbidEnter);
		yimei_jiajiao_jiaobeipihao.setOnEditorActionListener(prtnoEnter);

		yimei_jiajiao_user.setOnFocusChangeListener(EditGetFocus);
		yimei_jiajiao_jidiaojinumber.setOnFocusChangeListener(EditGetFocus);
		yimei_jiajiao_jiaobeipihao.setOnFocusChangeListener(EditGetFocus);

	};

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(barcodeReceiver); // 取消广播注册
	}
	
	/**
	 * 胶杯号
	 */
	OnEditorActionListener prtnoEnter = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (v.getId() == R.id.yimei_jiajiao_jiaobeipihao) {
				if (actionId >= 0) { // 胶杯批号
					jiaobeipihao = yimei_jiajiao_jiaobeipihao.getText()
							.toString().trim();
					if (yimei_jiajiao_user.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"作业员不能为空!", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_user));
						return false;
					}
					if (yimei_jiajiao_jidiaojinumber.getText().toString()
							.trim().equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"点胶机编号不能为空!", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber));
						return false;
					}
					if (yimei_jiajiao_jiaobeipihao.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"胶杯批号不能为空!", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jiaobeipihao));
						return false;
					}
					Map<String, String> mapSbid = new HashMap<String, String>();
					mapSbid.put("dbid", MyApplication.DBID);
					mapSbid.put("usercode", MyApplication.user);
					mapSbid.put("apiId", "assist");
					mapSbid.put("assistid", "{MESEQUTM}");
					mapSbid.put("cont", "~id='" + sbid + "' and zc_id='31'");
					httpRequestQueryRecord(MyApplication.MESURL, mapSbid,
							"Isshebei1");
					nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jiaobeipihao));
					yimei_jiajiao_jiaobeipihao.selectAll();
				}
			}
			return false;
		}
	};

	/**
	 * 作业员
	 */
	OnEditorActionListener userEnter = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (v.getId() == R.id.yimei_jiajiao_user) { // 作业员
				if (actionId >= 0) {
					if (yimei_jiajiao_user.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"作业员不能为空!", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_user));
						return false;
					}
					zuoyeyuan = yimei_jiajiao_user.getText().toString().trim();
					nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber));
				}
			}
			return false;
		}
	}; 
	
	
	/**
	 * 设备号
	 */
	OnEditorActionListener sbidEnter = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (v.getId() == R.id.yimei_jiajiao_jidiaojinumber) { // 设备号
				if (actionId >= 0) {
					sbid = yimei_jiajiao_jidiaojinumber.getText().toString()
							.toUpperCase().trim();
					if (zuoyeyuan.equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"作业员不能为空!", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_user));
						return false;
					}
					if (sbid.equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"点胶机编号不能为空!", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber));
						return false;
					}
					Map<String, String> mapSbid = new HashMap<String, String>();
					mapSbid.put("dbid", MyApplication.DBID);
					mapSbid.put("usercode", MyApplication.user);
					mapSbid.put("apiId", "assist");
					mapSbid.put("assistid", "{MESEQUTM}");
					mapSbid.put("cont", "~id='" + sbid + "' and zc_id='31'");
					httpRequestQueryRecord(MyApplication.MESURL, mapSbid,
							"Isshebei");
				}
			}
			return false;
		}
	};
	
	/**
	 * 回车事件（通用）
	 */
	OnEditorActionListener editEnter = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean flag = false;
			if (v.getId() == R.id.yimei_jiajiao_jiaobeipihao) {
				if (actionId >= 0) { // 胶杯批号
					jiaobeipihao = yimei_jiajiao_jiaobeipihao.getText()
							.toString().trim();
					if (yimei_jiajiao_user.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"作业员不能为空!", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_user));
						return false;
					}
					if (yimei_jiajiao_jidiaojinumber.getText().toString()
							.trim().equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"点胶机编号不能为空!", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber));
						return false;
					}
					if (yimei_jiajiao_jiaobeipihao.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"胶杯批号不能为空!", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jiaobeipihao));
						return false;
					}
					Map<String, String> mapSbid = new HashMap<String, String>();
					mapSbid.put("dbid", MyApplication.DBID);
					mapSbid.put("usercode", MyApplication.user);
					mapSbid.put("apiId", "assist");
					mapSbid.put("assistid", "{MESEQUTM}");
					mapSbid.put("cont", "~id='" + sbid + "' and zc_id='31'");
					httpRequestQueryRecord(MyApplication.MESURL, mapSbid,
							"Isshebei1");
					nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jiaobeipihao));
					yimei_jiajiao_jiaobeipihao.selectAll();
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_jiajiao_user) { // 作业员
				if (actionId >= 0) {
					if (yimei_jiajiao_user.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"作业员不能为空!", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_user));
						return false;
					}
					zuoyeyuan = yimei_jiajiao_user.getText().toString().trim();
					nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber));
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_jiajiao_jidiaojinumber) { // 设备号
				if (actionId >= 0) {
					sbid = yimei_jiajiao_jidiaojinumber.getText().toString()
							.toUpperCase().trim();
					if (zuoyeyuan.equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"作业员不能为空!", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_user));
						return false;
					}
					if (sbid.equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"点胶机编号不能为空!", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber));
						return false;
					}
					Map<String, String> mapSbid = new HashMap<String, String>();
					mapSbid.put("dbid", MyApplication.DBID);
					mapSbid.put("usercode", MyApplication.user);
					mapSbid.put("apiId", "assist");
					mapSbid.put("assistid", "{MESEQUTM}");
					mapSbid.put("cont", "~id='" + sbid + "' and zc_id='31'");
					httpRequestQueryRecord(MyApplication.MESURL, mapSbid,
							"Isshebei");
					flag = true;
				}
			}
			return flag;
		}
	};

	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_jiajiao_user) {
				if (!hasFocus) { // 失去焦点
					zuoyeyuan = yimei_jiajiao_user.getText().toString().trim();
				} else { // 获取焦点
					yimei_jiajiao_user.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_jiajiao_jidiaojinumber) {
				if (hasFocus) {
					yimei_jiajiao_jidiaojinumber.setSelectAllOnFocus(true);
				} else {
					sbid = yimei_jiajiao_jidiaojinumber.getText().toString()
							.toUpperCase().trim();
					yimei_jiajiao_jidiaojinumber.setText(sbid);
				}
			}
			if (v.getId() == R.id.yimei_jiajiao_jiaobeipihao) {
				if (hasFocus) {
					yimei_jiajiao_jiaobeipihao.setSelectAllOnFocus(true);
				}
			}
		}
	};

	@SuppressLint("HandlerLeak")
	final android.os.Handler handler = new android.os.Handler() {
		public void handleMessage(Message msg) {
			try {
				super.handleMessage(msg);
				Bundle b = msg.getData();
				String string = b.getString("type");
				if (string.equals("Isshebei1")) { //胶杯批次回车 
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {  //如没有这个机台
						Log.i("code", jsonObject.get("code").toString());
						if (mListView != null) {
							mListView.setAdapter(null);
							JiaJiaoAdapter.notifyDataSetChanged();
							nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber));
							ToastUtil.showToast(getApplicationContext(),
									"没有该胶机编号!", 0);
						} else {
							nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber));
							ToastUtil.showToast(getApplicationContext(),
									"没有该胶机编号!", 0);
						}
					} else {
						auxiliaryQuery("MSBMOLIST_JJ","~sbid='"+sbid+"' ","Query_Precord_slkid_1"); //查询任务表中正在生产的工单  and state1='03'
					}
				}
				if(string.equals("Query_Precord_slkid_1")){ //胶杯回车去查设备号是否正确
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
					if(Integer.parseInt(jsonObject.get("code").toString()) == 1){
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject.get("values")).get(0));
						if(jsonValue.containsKey("slkid")){							
							Production_slkid = jsonValue.get("slkid").toString();
							ToastUtil.showToast(JiaJiaoActivity.this,jsonValue.get("slkid").toString(),0);
							
							//查询胶杯批次号
							Map<String, String> mapSbid = new HashMap<String, String>();
							mapSbid.put("dbid", MyApplication.DBID);
							mapSbid.put("usercode", MyApplication.user);
							mapSbid.put("apiId", "assist");
							mapSbid.put("assistid", "{MESGLUEJOB}");
							mapSbid.put("cont", "~prtno='" + jiaobeipihao + "'");
							httpRequestQueryRecord(MyApplication.MESURL, mapSbid,
									"QueryMESgluejob");
							nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jiaobeipihao));
						}else{
							ToastUtil.showToast(JiaJiaoActivity.this,"当前正在生产的【"+jsonValue.get("sid1")+"】批次号没有绑定工单号,请联系管理员!",0);
							yimei_jiajiao_jidiaojinumber.selectAll();
						}
					}else{
						showSlkid("当前机台没有入站的批次,无法加胶!"); //当前没有正在生产的批次，请先将批次入站开工！
						yimei_jiajiao_jidiaojinumber.selectAll();
					}
				}
				if (string.equals("Isshebei")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { //如果没有这个机台
						Log.i("code", jsonObject.get("code").toString());
						if (mListView != null) {
							mListView.setAdapter(null);
							JiaJiaoAdapter.notifyDataSetChanged();
							nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber));
							yimei_jiajiao_jidiaojinumber.selectAll();
							ToastUtil.showToast(getApplicationContext(),
									"没有该胶机编号!", 0);
						} else {
							nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jidiaojinumber));
							yimei_jiajiao_jidiaojinumber.selectAll();
							ToastUtil.showToast(getApplicationContext(),
									"没有该胶机编号!", 0);
						}
					} else { //有这个机台
						auxiliaryQuery("MSBMOLIST_JJ","~sbid='"+sbid+"' ","Query_Precord_slkid"); //查询任务表中正在生产的工单 and state1='03'
//						nextEditFocus((EditText) findViewById(R.id.yimei_jiajiao_jiaobeipihao));
					}
				}
				if(string.equals("Query_Precord_slkid")){ //查询任务列表中正在生产的工单号
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
					if(Integer.parseInt(jsonObject.get("code").toString()) == 1){
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject.get("values")).get(0));
						if(jsonValue.containsKey("slkid")){							
							Production_slkid = jsonValue.get("slkid").toString();
//							ToastUtil.showToast(JiaJiaoActivity.this,jsonValue.get("slkid").toString(),0);
							MyApplication.nextEditFocus(yimei_jiajiao_jiaobeipihao);
						}else{
							ToastUtil.showToast(JiaJiaoActivity.this,"当前正在生产的【"+jsonValue.get("sid1")+"】批次号没有绑定工单号,请联系管理员!",0);
							yimei_jiajiao_jidiaojinumber.selectAll();
						}
					}else{
						showSlkid("当前机台没有入站的批次,无法加胶!"); 
						yimei_jiajiao_jidiaojinumber.selectAll();
					}
				}
				if (string.equals("QueryMESgluejob")) { // 查胶杯批号
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
								.get("values")).get(0));
						if(!Production_slkid.equals("")){
							//判断工单正在生产的工单和扫描胶杯的工单是否匹配
							if(Production_slkid.equals(jsonValue.get("mo_no").toString())){
								if(jsonValue.containsKey("newly_time")){
									//混胶的新时间 
									if (!jsonValue.get("newly_time").toString().equals("")) {
			
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy-MM-dd HH:mm");
										//加3小时间后的时间
										String newly_time =  jsonValue.get("newly_time").toString();
										if(newly_time.length() == 16){
											newly_time += ":00";
										}
										//最晚到期时间
										long vdate = sdf.parse(newly_time).getTime()- MyApplication.df.parse(MyApplication.GetServerNowTime()).getTime();
										if (vdate < 0) {
											ToastUtil.showToast(getApplicationContext(),
													"胶杯已过有效期", 0);
											return;
										}
									}
									if(jsonValue.containsKey("mixing_time")){ //判断混胶后20分钟内需要加胶
										if(!jsonValue.get("mixing_time").toString().equals("")){
											//最近混胶时间
											String mixing_time = jsonValue.get("mixing_time").toString();
											if(mixing_time.length() == 16){
												mixing_time+=":00";
											}
											//判断混胶后20分钟内需要加胶
											long Time = MyApplication.df.parse(MyApplication.GetServerNowTime()).getTime()  - MyApplication.df.parse(mixing_time).getTime();
											long Time1 = Time/1000/60;
											//如果混胶时间大于20分钟(!混交时间<20)
											if(!((Time/1000/60) < Integer.parseInt(jsonValue.get("fr_add_time").toString()))&&(Time/1000/60)>0){
												ToastUtil.showToast(JiaJiaoActivity.this,"请联系工程人员！",0);
												return;
											}
										}else{
											ToastUtil.showToast(JiaJiaoActivity.this,"请先做混胶~",0);
											return;
										}
									}else{
										ToastUtil.showToast(JiaJiaoActivity.this,"请先做混胶~",0);
										return;
									}
								}else{
									ToastUtil.showToast(JiaJiaoActivity.this,"请先做混胶~",0);
									return;
								}
								
								jsonValue.put("op", zuoyeyuan);
								jsonValue.put("sbid", sbid);
								jsonValue.put("prtno", jsonValue.get("prtno"));
								jsonValue.put("indate",MyApplication.GetServerNowTime());
								jsonValue.put("edate", jsonValue.get("vdate"));// 到期时间
								jsonValue.put("mkdate", jsonValue.get("tprn"));// 胶杯打印时间
								jsonValue.put("slkid", jsonValue.get("mo_no"));// 工单号（制令单号）
								// jsonValue.put("prd_name",jsonValue.get("prdno"));//货品名称
								jsonValue.put("dcid", GetAndroidMacUtil.getMac());
								jsonValue.put("sys_stated", "3");
								jsonValue.put("zcno", "31");
								jsonValue.put("sbuid", "D2010");
								jsonValue.put("qty", "1");
								Map<String, String> mesIdMap = MyApplication
										.httpMapKeyValueMethod(MyApplication.DBID,
												"savedata", MyApplication.user,
												jsonValue.toJSONString(), "D2010", "1");
								httpRequestQueryRecord(MyApplication.MESURL, mesIdMap,
										"AddMESgluejob");
							}else{
								showSlkid("正在生产的工单为【"+Production_slkid+"】\n扫描的加胶为【"+jsonValue.get("mo_no").toString()+"】,请检查!");
							}
						}else{
							ToastUtil.showToast(JiaJiaoActivity.this,"请先扫机台号",0);
						}
					} else {
						ToastUtil.showToast(getApplicationContext(), "没有"
								+ jiaobeipihao + "胶杯批号！", 0);
					}
				}
				if (string.equals("AddMESgluejob")) { // 300请求修改状态（state）
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("id").toString()) == 0) {
						Map<String, String> mesIdMap = MyApplication
								.updateServerJiaJiao(MyApplication.DBID,
										MyApplication.user, sbid, jiaobeipihao,
										"300");   
						String a = jiaobeipihao;
						httpRequestQueryRecord(MyApplication.MESURL, mesIdMap,
								"updateServerJiaJiao");

					} else {
						ToastUtil.showToast(getApplicationContext(),
								"服务器处理异（savedata）)！", 0);
					}
				}
				if (string.equals("updateServerJiaJiao")) { // 查询列表
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("id").toString()) == 1) {
						Map<String, String> mapSbid = new HashMap<String, String>();
						mapSbid.put("dbid", MyApplication.DBID);
						mapSbid.put("usercode", MyApplication.user);
						mapSbid.put("apiId", "assist");
						mapSbid.put("assistid", "{JIAJAOSELECT}");
						mapSbid.put("cont", "~prtno='" + jiaobeipihao + "'");
						httpRequestQueryRecord(MyApplication.MESURL, mapSbid,
								"QueryMESgrecord");
					} else {
						ToastUtil.showToast(getApplicationContext(),
								"服务器处理异常300！", 0);
					}
				}
				if (string.equals("QueryMESgrecord")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (!(Integer.parseInt(jsonObject.get("code").toString()) == 0)) { // 扫描区添加值
						JSONArray Values = (JSONArray) jsonObject.get("values");
						List<Map<String, String>> listMap = new ArrayList<>();
						Map<String, String> map = null;
						for (int i = 0; i < Values.size(); i++) {
							@SuppressWarnings("unchecked")
							Map<String, String> Valuesmap = (Map<String, String>) Values
									.get(i);
							map = new HashMap<String, String>();
							map.put("prtno",
									(Valuesmap.get("prtno")) == null ? ""
											: Valuesmap.get("prtno"));
							map.put("prd_no",
									(Valuesmap.get("prd_no")) == null ? ""
											: Valuesmap.get("prd_no"));
							map.put("qty",
									(String.valueOf(Valuesmap.get("qty"))) == null ? ""
											: "1.0000");
							map.put("indate",
									(Valuesmap.get("indate")) == null ? ""
											: Valuesmap.get("indate"));
							map.put("mkdate",
									(Valuesmap.get("mkdate")) == null ? ""
											: Valuesmap.get("mkdate"));
							map.put("edate",
									(Valuesmap.get("edate")) == null ? ""
											: Valuesmap.get("edate"));
							map.put("prd_name",
									(Valuesmap.get("prd_name")) == null ? ""
											: Valuesmap.get("prd_name"));
							listMap.add(map);
						}
						JiaJiaoCHScrollView headerScroll = (JiaJiaoCHScrollView) findViewById(R.id.jiajiao_scroll_title);
						// 添加头滑动事件
						JiaJiaoCHScrollView.add(headerScroll);
						mListView = (ListView) findViewById(R.id.jiaJiao_scroll_list);
						JiaJiaoAdapter = new JiaJiaoAdapter(
								JiaJiaoActivity.this, listMap);
						mListView.setAdapter(JiaJiaoAdapter);
						JiaJiaoAdapter.notifyDataSetChanged();

					}
				}
			} catch (Exception e) {
				Log.i("err", e.toString());
				ToastUtil.showToast(getApplicationContext(),e.toString(), 0);
			}
		}
	};
	
	/**
	 * 辅助查询
	 */
	private void auxiliaryQuery(String assistid,String count,String id){
		Map<String, String> mapSbid = new HashMap<String, String>();
		mapSbid.put("dbid", MyApplication.DBID);
		mapSbid.put("usercode", MyApplication.user);
		mapSbid.put("apiId", "assist");
		mapSbid.put("assistid", "{"+assistid+"}");
		mapSbid.put("cont",count);
		httpRequestQueryRecord(MyApplication.MESURL, mapSbid,
				id);
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

	public static void addHViews(final JiaJiaoCHScrollView hScrollView) {
		if (!JiaJiaoCHScrollView.isEmpty()) {
			int size = JiaJiaoCHScrollView.size();
			JiaJiaoCHScrollView scrollView = JiaJiaoCHScrollView.get(size - 1);
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
		JiaJiaoCHScrollView.add(hScrollView);
	}

	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		for (JiaJiaoCHScrollView scrollView : JiaJiaoCHScrollView) {
			if (mTouchView != scrollView)
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
	
	/**
	 * 弹出提示框
	 * 
	 * @param mes
	 */
	private void showSlkid(String mes) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(JiaJiaoActivity.this);
		normalDialog.setTitle("提示");
		normalDialog.setMessage(mes);
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

	/**
	 * 弹出提示框
	 * 
	 * @param mes
	 */
	private void showNormalDialog(String mes) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				JiaJiaoActivity.this);
		normalDialog.setTitle("输入密码:");
		normalDialog.setIcon(android.R.drawable.ic_dialog_info).setView(
				new EditText(this));
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
