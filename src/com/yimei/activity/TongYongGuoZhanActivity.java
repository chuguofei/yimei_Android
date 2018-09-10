package com.yimei.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.kuaiguozhan.KanDaiActivity;
import com.yimei.adapter.GaoWenDianLiangAdapter;
import com.yimei.entity.Pair;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

public class TongYongGuoZhanActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private Spinner tongyongguozhan_selectValue;
	private EditText tongyongguozhan_user, tongyongguozhan_sid1;
	private String op, sid1;
	private GaoWenDianLiangAdapter gaowendianliangApapter;
	private EditText bad_fracture,bad_crushed,bad_poinrem,bad_broken,bad_pressing,bad_overflow;
	private String zcno ="81";
	private JSONObject BadJson;
	private String lotno;
	private Button BadRecord;
	private String bad_sid;
	private String getServerNowTime = null;
	private int testqty;
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
				if (tag.equals("通用过站作业员")) { // 作业员
					tongyongguozhan_user.setText(barcodeData.toString()
							.toUpperCase().trim());
					if (tongyongguozhan_user.getText().toString().toUpperCase()
							.trim().equals("")
							|| tongyongguozhan_user.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(TongYongGuoZhanActivity.this,
								"作业员不能为空", 0);
						MyApplication.nextEditFocus(tongyongguozhan_user);
						return;
					}
					op = barcodeData.toString().toUpperCase().trim();
					MyApplication.nextEditFocus(tongyongguozhan_sid1);
				}
				if (tag.equals("通用过站批次号")) { // 作业员
					tongyongguozhan_sid1.setText(barcodeData.toString().trim());
					if (tongyongguozhan_user.getText().toString().toUpperCase()
							.trim().equals("")
							|| tongyongguozhan_user.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(TongYongGuoZhanActivity.this,
								"作业员不能为空", 0);
						MyApplication.nextEditFocus(tongyongguozhan_user);
						return;
					}
					if (tongyongguozhan_sid1.getText().toString().toUpperCase()
							.trim().equals("")
							|| tongyongguozhan_sid1.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(TongYongGuoZhanActivity.this,
								"批次号不能为空", 0);
						MyApplication.nextEditFocus(tongyongguozhan_sid1);
						return;
					}
					sid1 = tongyongguozhan_sid1.getText().toString()
							.toUpperCase().trim();
					if (zcno.equals("81")) {
						Map<String, String> map = MyApplication.QueryBatNo(
								"TESTQUERY", "~lotno='" + sid1 + "' ");
						OkHttpUtils.getInstance().getServerExecute(
								MyApplication.MESURL, null, map, null, mHander,
								true, "QuertLotNo");
					} else {
						Map<String, String> map = MyApplication.QueryBatNo(
								"MOZCLISTWEB", "~sid1='" + sid1
										+ "' and zcno='" + zcno + "' ");
						OkHttpUtils.getInstance().getServerExecute(
								MyApplication.MESURL, null, map, null, mHander,
								true, "QuertPlanaSid1");
					}
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tongyongguozhan);
		tongyongguozhan_selectValue = (Spinner) findViewById(R.id.tongyongguozhan_selectValue);
		String cont;
		if (MyApplication.user.equals("admin")) {
			cont = "";
		} else {
			cont = "~sorg='" + MyApplication.sorg + "'";
		}
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				MyApplication.QueryBatNo("GUOZHANZCNO", cont), null, mHander,
				true, "SpinnerValue");
		BadRecord = (Button) findViewById(R.id.tongyongguozhan_BadRecord);
		BadRecord.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
//				showNormalDialog();
				Intent intent = new Intent(TongYongGuoZhanActivity.this,KanDaiBadActivity.class);
				startActivity(intent);
			}
		});
//		BadRecord.setEnabled(false);
//		BadRecord.setTextColor(Color.GRAY);
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.tongyongguozhan_scroll_title);
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.tongyongguozhan_scroll_list);
	}

	@Override
	protected void onResume() {
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		super.onResume();
		/*BadRecord1 = (Button) findViewById(R.id.tongyongguozhan_BadRecord1);
		BadRecord1.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TongYongGuoZhanActivity.this,KanDaiBadActivity.class);
				startActivity(intent);
			}
		});*/
		tongyongguozhan_user = (EditText) findViewById(R.id.tongyongguozhan_user);
		tongyongguozhan_sid1 = (EditText) findViewById(R.id.tongyongguozhan_sid1);
		if(!zcno.equals("81")){
			BadRecord.setVisibility(View.GONE);
//			BadRecord1.setVisibility(View.GONE);
		}else{
			BadRecord.setVisibility(View.VISIBLE);
//			BadRecord1.setVisibility(View.VISIBLE);
		}
		tongyongguozhan_user.setOnEditorActionListener(EditListener);
		tongyongguozhan_sid1.setOnEditorActionListener(EditListener);

		tongyongguozhan_user.setOnFocusChangeListener(EditGetFocus);
		tongyongguozhan_sid1.setOnFocusChangeListener(EditGetFocus);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(barcodeReceiver); // 取消广播注册
	} 

	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.tongyongguozhan_user) {
				if (!hasFocus) { // 失去焦点
					op = tongyongguozhan_user.getText().toString().trim().toUpperCase();
					Map<String, String> map = MyApplication.QueryBatNo("MESOPWEB", "~sal_no='" + op + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "QueryMESOP");
				} else { // 获取焦点
					tongyongguozhan_user.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.tongyongguozhan_sid1) {
				if (!hasFocus) { // 失去焦点
					sid1 = tongyongguozhan_sid1.getText().toString().trim()
							.toUpperCase();
				} else { // 获取焦点
					tongyongguozhan_sid1.setSelectAllOnFocus(true);
				}
			}
		}
	};

	/**
	 * 回车事件
	 */
	OnEditorActionListener EditListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (v.getId() == R.id.tongyongguozhan_user) {
				if (actionId >= 0) { // 用户名
					if (tongyongguozhan_user.getText().toString().toUpperCase()
							.equals("")
							|| tongyongguozhan_user.getText() == null) {
						ToastUtil.showToast(TongYongGuoZhanActivity.this,
								"用户名不能为空！", 0);
						return false;
					}
					op = tongyongguozhan_user.getText().toString()
							.toUpperCase();
					MyApplication.nextEditFocus(tongyongguozhan_sid1);
				}
			}
			if (v.getId() == R.id.tongyongguozhan_sid1) {
				if (actionId >= 0) { // 用户名
					if (tongyongguozhan_user.getText().toString().toUpperCase()
							.equals("")
							|| tongyongguozhan_user.getText() == null) {
						ToastUtil.showToast(TongYongGuoZhanActivity.this,
								"用户名不能为空！", 0);
						return false;
					}
					if (tongyongguozhan_sid1.getText().toString().toUpperCase()
							.equals("")
							|| tongyongguozhan_user.getText() == null) {
						ToastUtil.showToast(TongYongGuoZhanActivity.this,
								"批次号不能为空！", 0);
						return false;
					}
					sid1 = tongyongguozhan_sid1.getText().toString()
							.toUpperCase().trim();
					if (zcno.equals("81")) {
						Map<String, String> map = MyApplication.QueryBatNo(
								"TESTQUERY", "~lotno='" + sid1 + "' ");
						OkHttpUtils.getInstance().getServerExecute(
								MyApplication.MESURL, null, map, null, mHander,
								true, "QuertLotNo");
					} else {
						Map<String, String> map = MyApplication.QueryBatNo(
								"MOZCLISTWEB", "~sid1='" + sid1
										+ "' and zcno='" + zcno + "' ");
						OkHttpUtils.getInstance().getServerExecute(
								MyApplication.MESURL, null, map, null, mHander,
								true, "QuertPlanaSid1");
					}
				}
			}
			return false;
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
				TongYongGuoZhanActivity.this);
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
						// {"message":"请重新登录","id":-1}
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
						if(string.equals("QueryMESOP")){ //检查人是否存在
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (jsonObject.getInteger("code") == 0) {
								MyApplication.nextEditFocus(tongyongguozhan_user);
								tongyongguozhan_user.selectAll();
								ToastUtil.showToast(getApplicationContext(),"请输入正确的作业员工号!",0);
								InputHidden();
							}else{
								if(BadJson!=null){
									if(op!=null && !op.equals(BadJson.getString("op"))){
										mListView.setAdapter(null);
										gaowendianliangApapter = null;
									}
								}
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
								System.out.println(jsonObject);
							}
						}
						if (string.equals("QuertLotNo")) { // 查询测试表
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(),"没有该lot号!", 0);
								tongyongguozhan_sid1.selectAll();
								InputHidden();
							} else {
								JSONObject jsonValue = (JSONObject) ((JSONArray) jsonObject
										.get("values")).get(0);
								if (Integer.parseInt(jsonValue.get("holdid")
										.toString()) == 1) {
									ToastUtil.showToast(
											TongYongGuoZhanActivity.this,
											"该lot号已被【HOLD】", 0);
									tongyongguozhan_sid1.selectAll();
									InputHidden();
								}
								// 0.测试站 1.编带站 2.看带站
								if (Integer.parseInt(jsonValue.get("lotstate")
										.toString()) == 0) {
									ToastUtil.showToast(
											TongYongGuoZhanActivity.this,
											"该批号不具备入站条件,上个工序未出站!", 0);
									MyApplication.nextEditFocus(tongyongguozhan_sid1);
									tongyongguozhan_sid1.selectAll();
									InputHidden();
									return;
								} else if (Integer.parseInt(jsonValue.get(
										"lotstate").toString()) == 2) {
									ToastUtil.showToast(
											TongYongGuoZhanActivity.this,
											"该批号已经出站!", 0);
									MyApplication
											.nextEditFocus(tongyongguozhan_sid1);
									tongyongguozhan_sid1.selectAll();
									InputHidden();
									return;
								} else {
									lotno = sid1; //lot号
									testqty = jsonValue.getIntValue("qty"); //qty
									Map<String, String> map = MyApplication
											.QueryBatNo("MOZCLISTWEB",
													"~sid1='"+ jsonValue.get("sid1")
															+ "' and zcno='"
															+ zcno + "' ");
									OkHttpUtils.getInstance().getServerExecute(
											MyApplication.MESURL, null, map,
											null, mHander, true,
											"QuertPlanaSid1");
								}
							}
						}
						if (string.equals("QuertPlanaSid1")) { // 查询记录中是否有该记录
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(TongYongGuoZhanActivity.this,
										"没有查到批次（mes_lot_plana）", 0);
								MyApplication.nextEditFocus(tongyongguozhan_sid1);
								tongyongguozhan_sid1.selectAll();
								InputHidden();
								return;
							} else {
								
								JSONObject jsonValue = (JSONObject) ((JSONArray) jsonObject.get("values")).get(0);
								if(!zcno.equals("81")){
									if(Integer.parseInt(jsonValue.get("bok").toString()) == 0){
										ToastUtil.showToast(getApplicationContext(),"选中的【"+jsonValue.get("sid1").toString()+"】不具备过站条件！", 0);
										return;
									}
									if(Integer.parseInt(jsonValue.get("holdid").toString()) == 1){
										ToastUtil.showToast(getApplicationContext(),"该批号状态为【HOLD】，请找QC!", 0);
										tongyongguozhan_sid1.selectAll();
										InputHidden();
										return;
									}
									if (jsonValue.get("state").toString().equals("04")) {
										ToastUtil.showToast(getApplicationContext(),"选中的【"+jsonValue.get("sid1").toString()+"】批次已经【出站】！", 0);
										tongyongguozhan_sid1.selectAll();
										InputHidden();
										return;
									}else if (jsonValue.get("state").toString().equals("01")) {
										ToastUtil.showToast(getApplicationContext(),"选中的【"+jsonValue.get("sid1").toString()+"】批次【入站】不能出站！", 0);
										tongyongguozhan_sid1.selectAll();
										InputHidden();
										return;
									}else if (jsonValue.get("state").toString().equals("07")) {
										ToastUtil.showToast(getApplicationContext(),"选中的【"+jsonValue.get("sid1").toString()+"】批次【待检】不能出站！", 0);
										tongyongguozhan_sid1.selectAll();
										InputHidden();
										return;
									}
									else if (jsonValue.get("state").toString().equals("0A")) {
										ToastUtil.showToast(getApplicationContext(),"选中的【"+jsonValue.get("sid1").toString()+"】批次状态【异常】不能出站！", 0);
										tongyongguozhan_sid1.selectAll();
										InputHidden();
										return;
									}else if (jsonValue.get("state").toString().equals("0B")) {
										ToastUtil.showToast(getApplicationContext(),"选中的【"+jsonValue.get("sid1").toString()+"】批次状态【暂停】不能出站！", 0);
										tongyongguozhan_sid1.selectAll();
										InputHidden();
										return;
									}else if (jsonValue.get("state").toString().equals("0C")) {
										ToastUtil.showToast(getApplicationContext(),"选中的【"+jsonValue.get("sid1").toString()+"】批次状态【中止】不能出站！", 0);
										tongyongguozhan_sid1.selectAll();
										InputHidden();
										return;
									}else if (jsonValue.get("state").toString().equals("0D")) {
										ToastUtil.showToast(getApplicationContext(),"选中的【"+jsonValue.get("sid1").toString()+"】批次状态【受控】不能出站！", 0);
										tongyongguozhan_sid1.selectAll();
										InputHidden();
										return;
									}
								}
								
								getServerNowTime = tongyongguozhan_sid1.getText().toString().toUpperCase();
								jsonValue.put("sbuid", "D0001");
								jsonValue.put("dcid",GetAndroidMacUtil.getMac());
								jsonValue.put("hpdate",MyApplication.GetServerNowTime());
								jsonValue.put("smake", MyApplication.user);
								if(zcno.equals("81")){
									jsonValue.put("lotno", sid1);
								}
								jsonValue.put("bok", "1");
								jsonValue.put("mkdate",MyApplication.GetServerNowTime());
								jsonValue.put("op", op);
								jsonValue.put("sorg",MyApplication.sorg);
								jsonValue.put("op_b", op);
								jsonValue.put("op_o", op);
								jsonValue.put("state1", "04");
								if(zcno.equals("81")){									
									jsonValue.put("qty", testqty);
								}
								jsonValue.put("outdate",MyApplication.GetServerNowTime());
								jsonValue.put("slkid", jsonValue.get("sid"));
								if(zcno.equals("81")){
									jsonValue.put("prtno", getServerNowTime);
								}
								BadJson = jsonValue;
								// savedate--------------------------------------------
								Map<String, String> mesIdMap = MyApplication
										.httpMapKeyValueMethod(
												MyApplication.DBID, "savedata",
												MyApplication.user,
												jsonValue.toJSONString(),
												"D0071", "1");
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, mesIdMap,
										null, mHander, true, "savedata");
								// savedate--------------------------------------------
								// 200请求--------------------------------------------------
								if(zcno.equals("81")){
									String sidAndlotno = jsonValue.get("sid1")+";" + tongyongguozhan_sid1.getText();
									Map<String, String> updateServerTable = MyApplication
											.UpdateServerTableMethod(
													MyApplication.DBID,
													MyApplication.user, jsonValue
															.get("state")
															.toString(), "04",
													sidAndlotno,
													jsonValue.get("slkid").toString(),
													MyApplication.KANDAI_ZCNO,
													"200");
									OkHttpUtils.getInstance().getServerExecute(
											MyApplication.MESURL, null,
											updateServerTable, null, mHander, true,
											"updateTableState");
								}else{
									Map<String, String> updateServerTable = MyApplication
											.UpdateServerTableMethod(
													MyApplication.DBID,
													MyApplication.user,jsonValue.get("state").toString(),"04",
													sid1,jsonValue.get("sid").toString(), zcno,
													"200");
									OkHttpUtils.getInstance().getServerExecute(
											MyApplication.MESURL, null,
											updateServerTable, null, mHander, true,
											"updateTableState");
								}
								// 适配器填值
								List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
								Map<String, String> map = new HashMap<String, String>();
								map.put("op", op);
								map.put("sid1", sid1);
								map.put("slkid", jsonValue.get("slkid").toString());
								map.put("prd_no", jsonValue.getString("prd_no").toString());
								map.put("qty", jsonValue.get("qty").toString());
								map.put("zcno",jsonValue.get("zcname").toString());
								mList.add(map);
								if (gaowendianliangApapter == null) {
									gaowendianliangApapter = new GaoWenDianLiangAdapter(
											TongYongGuoZhanActivity.this, mList);
									mListView
											.setAdapter(gaowendianliangApapter);
								} else {
									gaowendianliangApapter.listData.add(map);
									gaowendianliangApapter.notifyDataSetChanged();
								}
								tongyongguozhan_sid1.selectAll();
							}
						}
						if (string.equals("savedata")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if(Integer.parseInt(jsonObject.get("id").toString())==0){
								if(zcno.equals("81")){		
//									BadRecord.setEnabled(true);
//									BadRecord.setTextColor(Color.RED);
									JSONObject data = (JSONObject) jsonObject.get("data");
	 								bad_sid = data.get("sid").toString();
								}
								tongyongguozhan_sid1.selectAll();
								InputHidden();
							}
							System.out.println(jsonObject);
						}
						if (string.equals("bad_savedata")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if(Integer.parseInt(jsonObject.get("id").toString())==0){
//								BadRecord.setEnabled(false);
//								BadRecord.setTextColor(Color.GRAY);
								getServerNowTime=null;
//								tongyongguozhan_user.setText("");
								tongyongguozhan_sid1.setText("");
								MyApplication.nextEditFocus(tongyongguozhan_sid1);
							}
							System.out.println(jsonObject);
						}
						if(string.equals("updateTableState")){ //器件后端修改lot_no状态（04）
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
						}
					} catch (Exception e) {
						ToastUtil.showToastLocation(getApplicationContext(),
								"逻辑:【" + e.toString() + "】", 0);
					}
				}
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
	 * 不良记录
	 */
	private void showNormalDialog() {

		LayoutInflater inflater = getLayoutInflater();
		View dialog = inflater.inflate(R.layout.activity_badprecord_dig,
				(ViewGroup) findViewById(R.id.kandai_bad));
		bad_fracture = (EditText) dialog.findViewById(R.id.bad_fracture);
		bad_crushed = (EditText) dialog.findViewById(R.id.bad_crushed);
		bad_poinrem = (EditText) dialog.findViewById(R.id.bad_poinrem);
		bad_broken = (EditText) dialog.findViewById(R.id.bad_broken);
		bad_pressing = (EditText) dialog.findViewById(R.id.bad_pressing);
		bad_overflow = (EditText) dialog.findViewById(R.id.bad_overflow);
		EditText bad_lotno = (EditText) dialog.findViewById(R.id.bad_lotno);
		bad_lotno.setKeyListener(null);
		bad_lotno.setText(BadJson.get("lotno").toString());
		try {
			final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
					TongYongGuoZhanActivity.this);
			normalDialog.setTitle("不良记录");
			normalDialog.setView(dialog);
			normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
			normalDialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							JSONObject json_bad = new JSONObject();
//							json_bad.put("sid",bad_sid);
							json_bad.put("slkid",BadJson.get("slkid")==null?"":BadJson.get("slkid"));
							json_bad.put("sid1",BadJson.get("sid1")==null?"":BadJson.get("sid1"));
							json_bad.put("lotno",BadJson.get("lotno").toString());
							json_bad.put("prd_no",BadJson.get("prd_no")==null?"":BadJson.get("prd_no"));
							json_bad.put("mkdate",MyApplication.GetServerNowTime());
							json_bad.put("fracture",bad_fracture.getText().toString().trim().equals("")?0:bad_fracture.getText().toString().trim());
							json_bad.put("crushed",bad_crushed.getText().toString().trim().equals("")?0:bad_crushed.getText().toString().trim());
							json_bad.put("poinrem",bad_poinrem.getText().toString().trim().equals("")?0:bad_poinrem.getText().toString().trim());
							json_bad.put("broken",bad_broken.getText().toString().trim().equals("")?0:bad_broken.getText().toString().trim());
							json_bad.put("pressing",bad_pressing.getText().toString().trim().equals("")?0:bad_pressing.getText().toString().trim());
							json_bad.put("overflow",bad_overflow.getText().toString().trim().equals("")?0:bad_overflow.getText().toString().trim());
//							json_bad.put("sid", getServerNowTime);
							json_bad.put("sid", tongyongguozhan_sid1.getText().toString().toUpperCase());
							// savedate--------------------------------------------
							Map<String, String> mesIdMap = MyApplication
									.httpMapKeyValueMethod(
											MyApplication.DBID, "savedata",
											MyApplication.user,
											json_bad.toJSONString(),
											"D0071A", "1");
							OkHttpUtils.getInstance().getServerExecute(
									MyApplication.MESURL, null, mesIdMap,
									null, mHander, true, "bad_savedata");
						}
					});
			normalDialog.setNegativeButton("取消", null);
			// 显示
			normalDialog.show();
		} catch (Exception e1) {
			ToastUtil.showToast(TongYongGuoZhanActivity.this,"【过站不良弹窗】"+e1.toString(), 0);
		}
	}

	
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
				TongYongGuoZhanActivity.this,
				android.R.layout.simple_spinner_item, dicts);
		tongyongguozhan_selectValue.setAdapter(adapter);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tongyongguozhan_selectValue
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						zcno = ((Pair) tongyongguozhan_selectValue
								.getSelectedItem()).getValue();
						if(!zcno.equals("81")){
							BadRecord.setVisibility(View.GONE);
						}else{
							BadRecord.setVisibility(View.VISIBLE);
						}
//						ToastUtil.showToast(getApplicationContext(), zcno, 0);
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
}
