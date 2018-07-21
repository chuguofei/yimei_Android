package com.yimei.activity.ipqc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.MyApplication;
import com.yimei.activity.R;
import com.yimei.adapter.IPQC_XunJian_Adapter;
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
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;

/**
 * 
 * @author Administrator
 *  辅助：
 * PROCESSAQ //查询制程检验列表
 * M_PROCESS //根据部门查看部门 
 *  MESEQUTM //查询设备号
 */
public class IPQC_xunjian extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private IPQC_XunJian_Adapter xunjianAdapter;
	private Map<String, String> zcnoMap = new HashMap<String, String>();
	private Spinner selectValue; // 下拉框
	private String zcno;
	private EditText yimei_xunjian_user, yimei_xunjian_sbid,
			yimei_xunjian_sid1, yimei_xunjian_prd_no;
	private String chtype = "02"; // 查询制程检验列表
	private String op, sbid, sid1;
	private Button yimei_xunjian_save, yimei_xunjian_ps; // 保存按钮|结果判定
	private HashMap<String, String> IsSorg = new HashMap<String, String>(); // 判断部门
	public static IPQC_xunjian ip ;
	private JSONObject Sid1Json;
	private String lotno;
	private JSONObject errorJSON; //传到异常通知单的json串
	private boolean errJumpOK = false; // 是否保存数据
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
				if (tag.equals("巡检作业员")) { // 作业员
					yimei_xunjian_user.setText(barcodeData);
					if (yimei_xunjian_user.getText().toString().trim()
							.equals("")
							|| yimei_xunjian_user.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(), "作业员不能为空",
								0);
						yimei_xunjian_user.selectAll();
						return;
					}
					op = yimei_xunjian_user.getText().toString().toUpperCase();
					MyApplication.nextEditFocus(yimei_xunjian_sbid);
				}
				if (tag.equals("巡检设备号")) { // 设备号
					yimei_xunjian_sbid.setText(barcodeData);
					if (yimei_xunjian_user.getText().toString().trim()
							.equals("")
							|| yimei_xunjian_user.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(), "作业员不能为空",
								0);
						yimei_xunjian_user.selectAll();
						return;
					}
					if (yimei_xunjian_sbid.getText().toString().trim()
							.equals("")
							|| yimei_xunjian_sbid.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(), "设备号不能为空",
								0);
						yimei_xunjian_sbid.selectAll();
						return;
					}
					sbid = yimei_xunjian_sbid.getText().toString()
							.toUpperCase().trim();
					// 查询设备号
					AccessServer("MESEQUTM", "~id='" + sbid + "' and zc_id='"
							+ zcno + "' ", "QuerySbid");
				}
				if (tag.equals("巡检批次号")) { // 批次号
					yimei_xunjian_sid1.setText(barcodeData);
					if (yimei_xunjian_user.getText().toString().trim()
							.equals("")
							|| yimei_xunjian_user.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(), "作业员不能为空",
								0);
						yimei_xunjian_user.selectAll();
						return;
					}
					if (yimei_xunjian_sbid.getText().toString().trim()
							.equals("")
							|| yimei_xunjian_sbid.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(), "设备号不能为空",
								0);
						yimei_xunjian_sbid.selectAll();
						return;
					}
					if (yimei_xunjian_sid1.getText().toString().trim()
							.equals("")
							|| yimei_xunjian_sid1.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplicationContext(), "批次号不能为空",
								0);
						yimei_xunjian_sid1.selectAll();
						return;
					}
					sid1 = yimei_xunjian_sid1.getText().toString()
							.toUpperCase().trim();
					lotno = null;
					if (IsSorg.containsKey(zcno)) { // 查测试表
						AccessServer("TESTQUERY", "~lotno='" + sid1 + "'",
								"QuerylotNo");
					} else { // 查lot表
						AccessServer("MOZCLISTWEB", "~zcno='" + zcno
								+ "' and sid1='" + sid1 + "'", "QuerySid1");
					}
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ipqc_xunjian);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// 静止进入页面弹出键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		String cont; // 参数
		ip = IPQC_xunjian.this;
		if (MyApplication.user.equals("admin")) {
			cont = "";
		} else {
			cont = "~sorg='" + MyApplication.sorg + "'";
		}
		// 获取制程
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				MyApplication.QueryBatNo("M_PROCESS", cont), null, mHander,
				true, "SpinnerValue");
		// 获取发起原因
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.xunjian_scroll_title);
		// 添加头滑动事件
		GeneralCHScrollView.add(headerScroll);
		final ScrollView s  = (ScrollView) findViewById(R.id.scrollView_xunjian);
		mListView = (ListView) findViewById(R.id.xunjian_scroll_list);
		mListView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				s.requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		selectValue = (Spinner) findViewById(R.id.ipqc_xunjian_selectValue);
		yimei_xunjian_user = (EditText) findViewById(R.id.yimei_xunjian_user);
		yimei_xunjian_sbid = (EditText) findViewById(R.id.yimei_xunjian_sbid);
		yimei_xunjian_sid1 = (EditText) findViewById(R.id.yimei_xunjian_sid1);
		yimei_xunjian_prd_no = (EditText) findViewById(R.id.yimei_xunjian_prd_no);
		yimei_xunjian_save = (Button) findViewById(R.id.yimei_xunjian_save);
		yimei_xunjian_ps = (Button) findViewById(R.id.yimei_xunjian_ps);
		
		IsSorg.put("61", "61");
		IsSorg.put("71", "71");
		IsSorg.put("81", "81");

		yimei_xunjian_user.setOnEditorActionListener(EnterListen);
		yimei_xunjian_sbid.setOnEditorActionListener(EnterListen);
		yimei_xunjian_sid1.setOnEditorActionListener(EnterListen);
		yimei_xunjian_ps.setOnClickListener(BtnOnClick);
		yimei_xunjian_save.setOnClickListener(BtnOnClick);
		
		yimei_xunjian_user.setOnFocusChangeListener(focusChange);
		yimei_xunjian_sbid.setOnFocusChangeListener(focusChange);
		yimei_xunjian_sid1.setOnFocusChangeListener(focusChange);
		yimei_xunjian_prd_no.setKeyListener(null);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(barcodeReceiver); // 取消广播注册
	}
	/**
	 * 按钮点击事件
	 */
	OnClickListener BtnOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.yimei_xunjian_ps){ //结果判定
				if(yimei_xunjian_ps.getText().equals("")){
					yimei_xunjian_ps.setText("OK");
				}else if(yimei_xunjian_ps.getText().equals("OK")){
					yimei_xunjian_ps.setText("NG");
				}else if(yimei_xunjian_ps.getText().equals("NG")){
					yimei_xunjian_ps.setText("OK");
				}
			}
			if(v.getId()==R.id.yimei_xunjian_save){
				if (yimei_xunjian_user.getText().toString().trim()
						.equals("")
						|| yimei_xunjian_user.getText().toString().trim() == null) {
					showNormalDialog("请选择作业员！");
					return ;
				}
				if (yimei_xunjian_sbid.getText().toString().trim()
						.equals("")
						|| yimei_xunjian_sbid.getText().toString().trim() == null) {
					showNormalDialog("请选择设备号！");
					return ;
				}
				if (yimei_xunjian_sid1.getText().toString().trim()
						.equals("")
						|| yimei_xunjian_sid1.getText().toString().trim() == null) {
					showNormalDialog("批次号不能为空！");
					return;
				}
				if(yimei_xunjian_ps.getText().equals("")){
					showNormalDialog("请确定结果判定！");
					return;
				}
				if(xunjianAdapter!=null){
					//====================父对象==========================
					JSONObject FatherJson = new JSONObject();
					FatherJson.put("sbuid","Q00102");
					FatherJson.put("chtype",chtype);
					FatherJson.put("sbid",sbid);
					FatherJson.put("slkid",Sid1Json.get("sid"));
					FatherJson.put("sid1",sid1);
					FatherJson.put("lotno",lotno!=null?lotno:"");
					FatherJson.put("prd_no",Sid1Json.containsKey("prd_no")?Sid1Json.get("prd_no"):"");
					FatherJson.put("prd_name",Sid1Json.containsKey("prd_name")?Sid1Json.get("prd_name"):"");
					FatherJson.put("op",op);
					FatherJson.put("bok",yimei_xunjian_ps.getText().equals("OK")?"0":"1");
					FatherJson.put("qty",Sid1Json.containsKey("qty")?Sid1Json.get("qty"):"");
					FatherJson.put("ngqty",Sid1Json.containsKey("ngqty")?Sid1Json.get("ngqty"):"");
					FatherJson.put("qty",Sid1Json.containsKey("qty")?Sid1Json.get("qty"):"");
					FatherJson.put("ngqty",Sid1Json.containsKey("ngqty")?Sid1Json.get("ngqty"):"");
					FatherJson.put("ps",yimei_xunjian_ps.getText().equals("OK")?"0":"1");
					FatherJson.put("state","0");
					FatherJson.put("mkdate",MyApplication.GetServerNowTime());
					FatherJson.put("op_c",MyApplication.user);
					FatherJson.put("dcid",GetAndroidMacUtil.getMac());
					FatherJson.put("bat_no",Sid1Json.containsKey("bat_no")?Sid1Json.get("bat_no"):"");
					FatherJson.put("smake",MyApplication.user);
					FatherJson.put("sorg",MyApplication.sorg);
					FatherJson.put("state1","07");
					FatherJson.put("zcno", zcno);
					FatherJson.put("sys_stated", "3");
					//====================父对象==========================
					JSONArray jsonarr = new JSONArray();
					//============子对象======================
					for (int i = 0; i < xunjianAdapter.getCount(); i++) {
						JSONObject SonJson = new JSONObject();
						Map<String,String> map = (Map<String, String>) xunjianAdapter.getItem(i);
						SonJson.put("itm",map.get("cid"));
						SonJson.put("cid","1");
						SonJson.put("xmbm",map.get("xmbm"));
						SonJson.put("xmmc",map.get("xmmc"));
						SonJson.put("bok",map.get("bok").equals("OK")?"0":"1");
						SonJson.put("remark",map.get("remark"));
						SonJson.put("sys_stated", "3");
						jsonarr.add(SonJson);
					}
					FatherJson.put("Q00102A", jsonarr );
					//============子对象======================
					Map<String, String> httpMapKeyValueMethod = MyApplication
							.httpMapKeyValueMethod(MyApplication.DBID,
									"savedata", MyApplication.user,
									FatherJson.toString(), "Q00102(Q00102A)",
									"1");
					OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,httpMapKeyValueMethod,null,mHander,true,"Save_Q00102");
				}else{
					showNormalDialog("列表为空");
				}
			}
		}
	};
	
	/**
	 * 提示
	 * @param msg
	 */
	private void showNormalDialog(String msg) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(IPQC_xunjian.this);
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
	
	/**
	 * 焦点
	 */
	OnFocusChangeListener focusChange = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_xunjian_user) {
				if (!hasFocus) {
					op = yimei_xunjian_user.getText().toString()
							.trim().toUpperCase();
				} else {
					yimei_xunjian_user.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_xunjian_sbid) {
				if (hasFocus) {
					yimei_xunjian_sbid.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_xunjian_sid1) {
				if (hasFocus) {
					yimei_xunjian_sid1.setSelectAllOnFocus(true);
				}
			}
		}
	};
	
	/**
	 * 键盘回车
	 */
	OnEditorActionListener EnterListen = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean flag = false;
			if (v.getId() == R.id.yimei_xunjian_user) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_xunjian_user.getText().toString().trim()
							.equals("")
							|| yimei_xunjian_user.getText().toString().trim() == null) {
						ToastUtil.showToast(IPQC_xunjian.this, "作业员不能为空", 0);
						return false;
					}
					op = yimei_xunjian_user.getText().toString().toUpperCase()
							.trim();
					MyApplication.nextEditFocus(yimei_xunjian_sbid);
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_xunjian_sbid) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_xunjian_user.getText().toString().trim()
							.equals("")
							|| yimei_xunjian_user.getText().toString().trim() == null) {
						ToastUtil.showToast(IPQC_xunjian.this, "作业员不能为空", 0);
						return false;
					}
					if (yimei_xunjian_sbid.getText().toString().trim()
							.equals("")
							|| yimei_xunjian_sbid.getText().toString().trim() == null) {
						ToastUtil.showToast(IPQC_xunjian.this, "设备号不能为空", 0);
						return false;
					}
					sbid = yimei_xunjian_sbid.getText().toString()
							.toUpperCase().trim();
					// 查询设备号
					AccessServer("MESEQUTM", "~id='" + sbid + "' and zc_id='"
							+ zcno + "' ", "QuerySbid");
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_xunjian_sid1) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_xunjian_user.getText().toString().trim()
							.equals("")
							|| yimei_xunjian_user.getText().toString().trim() == null) {
						ToastUtil.showToast(IPQC_xunjian.this, "作业员不能为空", 0);
						return false;
					}
					if (yimei_xunjian_sbid.getText().toString().trim()
							.equals("")
							|| yimei_xunjian_sbid.getText().toString().trim() == null) {
						ToastUtil.showToast(IPQC_xunjian.this, "设备号不能为空", 0);
						return false;
					}
					if (yimei_xunjian_sid1.getText().toString().trim()
							.equals("")
							|| yimei_xunjian_sid1.getText().toString().trim() == null) {
						ToastUtil.showToast(IPQC_xunjian.this, "批次号不能为空", 0);
						return false;
					}
					sid1 = yimei_xunjian_sid1.getText().toString()
							.toUpperCase().trim();
					lotno = null;
					if (IsSorg.containsKey(zcno)) { // 查测试表
						AccessServer("TESTQUERY", "~lotno='" + sid1 + "'",
								"QuerylotNo");
					} else { // 查lot表
						AccessServer("MOZCLISTWEB", "~zcno='" + zcno
								+ "' and sid1='" + sid1 + "'", "QuerySid1");
					}
					flag = true;
				}
			}
			return flag;
		}
	};

	/**
	 * 
	 * @param assistid
	 *            辅助
	 * @param cont
	 *            参数
	 * @param id
	 */
	private void AccessServer(String assistid, String cont, String id) {
		Map<String, String> queryBatNo = MyApplication.QueryBatNo(assistid,
				cont);
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				queryBatNo, null, mHander, true, id);
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
						JSONObject LoginTimeMess = JSON.parseObject(b.getString(
								"jsonObj").toString());
						if (LoginTimeMess.containsKey("message")) {
							if (LoginTimeMess.get("message").equals("请重新登录")) { // 超时登录
								LoginTimeout_dig("超时登录", "请重新登录!");
								return;
							}
						}
						if (string.equals("TimeOutLogin")) { // 超时登录
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (jsonObject.getInteger("id") != 0) {
								LoginTimeout_dig("密码错误", "");
							}
						}
						if(string.equals("Save_Q00102")){ //点击保存按钮
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id")
									.toString()) == -1) {
								ToastUtil.showToast(IPQC_xunjian.this,
										"（SaveData）错误", 0);
							} else {
								errJumpOK = true;
								errorJSON.put("sid", ((JSONObject) jsonObject
										.get("data")).get("sid"));
								errorJSON.put("checkType",chtype);
								ToastUtil.showToast(IPQC_xunjian.this,"保存成功",0);
								yimei_xunjian_sbid.setText("");
								yimei_xunjian_sid1.setText("");
								MyApplication.nextEditFocus(yimei_xunjian_sbid);
							}
						}
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
							}
						}
						if (string.equals("PROCESSA_QUERY")) { // 查询制程检验列表
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(),
										"没有该制程检验列表", 0);
								return;
							} else {
								List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
								for (int i = 0; i < ((JSONArray) jsonObject
										.get("values")).size(); i++) {
									JSONObject jsonObj = (JSONObject) ((JSONArray) jsonObject
											.get("values")).get(i);
									Map<String, String> map = new HashMap<>();
									map.put("cid",jsonObj.containsKey("cid") ? jsonObj.get("cid").toString() : "");
									map.put("xmbm",jsonObj.containsKey("xmbm") ? jsonObj.get("xmbm").toString(): "");
									map.put("xmmc",jsonObj.containsKey("xmmc") ? jsonObj.get("xmmc").toString(): "");
									map.put("bok", "OK");
									map.put("remark","");
									mList.add(map);
								}
								xunjianAdapter = new IPQC_XunJian_Adapter(IPQC_xunjian.this, mList);
								mListView.setAdapter(xunjianAdapter);
							}
						}
						if (string.equals("QuerySbid")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(IPQC_xunjian.this,
										"在该制程中没有该设备号,请检查！", 0);
								yimei_xunjian_sbid.selectAll();
							} else {
								MyApplication
										.nextEditFocus(yimei_xunjian_sid1);
							}
						}
						if (string.equals("QuerylotNo")) { // 查询lotNo号
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(IPQC_xunjian.this, "没有【"
										+ sid1 + "】lot号!", 0);
								yimei_xunjian_sid1.selectAll();
								return;
							} else {
								JSONObject jsonObj = (JSONObject) ((JSONArray) jsonObject
										.get("values")).get(0);
								sid1 = jsonObj.get("sid1").toString();
								lotno = jsonObj.get("sid1").toString();
								// 查询批次号
								AccessServer("MOZCLISTWEB", "~zcno='" + zcno
										+ "' and sid1='" + sid1 + "'",
										"QuerySid1");
							}
						}
						if (string.equals("QuerySid1")) { // 查询批次号
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(IPQC_xunjian.this, "没有【"
										+ sid1 + "】批次号!", 0);
								yimei_xunjian_sid1.selectAll();
								xunjianAdapter = null;
								mListView.setAdapter(xunjianAdapter);
								return;
							} else {
								JSONObject jsonObj = (JSONObject) ((JSONArray) jsonObject
										.get("values")).get(0);
							/*	if (jsonObj.get("state").toString()
										.equals("04")) {
									ToastUtil.showToast(IPQC_xunjian.this,
											"该【" + sid1 + "】批号已经出站!", 0);
									yimei_xunjian_sid1.selectAll();
									return;
								} else {*/
									Sid1Json = jsonObj;
									errorJSON = Sid1Json; // 审批流的json
									errorJSON.put("slkid", Sid1Json.get("sid").toString());
									errorJSON.put("sbid", sbid);
									errorJSON.put("op", op);
									errorJSON.put("chtype",chtype);
									// 查询制程检验列表
									OkHttpUtils.getInstance().getServerExecute(
											MyApplication.MESURL,
											null,
											MyApplication.QueryBatNo(
													"PROCESSAQ", "~id='" + zcno
															+ "' and chtype='"
															+ chtype + "'"),
											null, mHander, true,
											"PROCESSA_QUERY");
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									// 如果软键盘已经显示，则隐藏，反之则显示
									imm.toggleSoftInput(0,
											InputMethodManager.HIDE_NOT_ALWAYS);
									yimei_xunjian_prd_no.setText(jsonObj
											.containsKey("prd_no") ? jsonObj
											.get("prd_no").toString() : "");
//								}
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
//		usertext.setKeyListener(null);

		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				IPQC_xunjian.this);
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
						OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,
								null, map, null, mHander, true, "TimeOutLogin");
					}
				});
		// 显示
		normalDialog.show();
	}
	
	/**
	 * 给制程下拉框赋值
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
			zcnoMap.put(jsonValue.getString("id").toString(), jsonValue
					.getString("name").toString());
		}
		ArrayAdapter<Pair> adapter = new ArrayAdapter<Pair>(IPQC_xunjian.this,
				android.R.layout.simple_spinner_item, dicts);
		selectValue.setAdapter(adapter);
		selectValue.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				zcno = ((Pair) selectValue.getSelectedItem()).getValue();
				// 查询发起原因
				AccessServer("CAUSEDQ", "~id='" + zcno + "'", "QueryCaused");
				// ToastUtil.showToast(IPQC_xunjian.this,zcno,0);
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
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, MyApplication.ERRORNOTICE, 4, MyApplication.ERRORNOTICETEXT);
		return true;
	}*/

	/**
	 * 异常通知单
	 */
	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case MyApplication.ERRORNOTICE:
			if (errJumpOK) {
				if(yimei_xunjian_ps.getText().equals("OK")){
					Intent intent = new Intent();
					intent.setClass(IPQC_xunjian.this, ErrorNotice_shoujian.class);// 跳转到加载界面
					// 利用bundle来存取数据
					Bundle bundle = new Bundle();
					bundle.putString("json", errorJSON.toJSONString());
					// 再把bundle中的数据传给intent，以传输过去
					intent.putExtras(bundle);
					startActivity(intent);
				}
			} else {
				showNormalDialog("请先填写首检记录");
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}*/
}
