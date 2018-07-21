package com.yimei.activity.kuaiguozhan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.color;
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
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.MyApplication;
import com.yimei.activity.R;
import com.yimei.adapter.GaoWenDianLiangAdapter;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

/**
 * 
 * @author Administrator 对象：D50301 辅助：TESTQUERY //lot号查询 MOZCLISTWEB //查询批次号
 *         功能：快速过站
 */
public class KanDaiActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private EditText gaowen_user, gaowen_sid1;
	private String op, sid1;
	private GaoWenDianLiangAdapter gaowendianliangApapter;
	private JSONObject BadJson;
	private EditText bad_fracture,bad_crushed,bad_poinrem,bad_broken,bad_pressing,bad_overflow;
	private String bad_sid;
	private Button BadRecord;
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
				if (tag.equals("高温点亮作业员")) { // 作业员
					gaowen_user.setText(barcodeData);
					if (gaowen_user.getText().toString().toUpperCase().trim()
							.equals("")
							|| gaowen_user.getText().toString().toUpperCase()
									.trim() == null) {
						ToastUtil.showToast(KanDaiActivity.this, "作业员不能为空", 0);
						MyApplication.nextEditFocus(gaowen_user);
						return;
					}
					op = barcodeData.toString().toUpperCase().trim();
					MyApplication.nextEditFocus(gaowen_sid1);
				}
				if (tag.equals("高温点亮批次号")) { // 作业员
					gaowen_sid1.setText(barcodeData);
					if (gaowen_user.getText().toString().toUpperCase().trim()
							.equals("")
							|| gaowen_user.getText().toString().toUpperCase()
									.trim() == null) {
						ToastUtil.showToast(KanDaiActivity.this, "作业员不能为空", 0);
						MyApplication.nextEditFocus(gaowen_user);
						return;
					}
					if (gaowen_sid1.getText().toString().toUpperCase()
							.equals("")
							|| gaowen_sid1.getText().toString().toUpperCase() == null) {
						ToastUtil.showToast(KanDaiActivity.this, "批次号不能为空", 0);
						MyApplication.nextEditFocus(gaowen_sid1);
						return;
					}
					sid1 = gaowen_sid1.getText().toString().toUpperCase()
							.trim();
					Map<String, String> map = MyApplication.QueryBatNo(
							"TESTQUERY", "~lotno='" + sid1 + "' ");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "QuertSid1");
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gaowendianliang);

		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.gaowendianliang_scroll_title);
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.gaowendianliang_scroll_list);
		BadRecord = (Button) findViewById(R.id.BadRecord);
		BadRecord.setEnabled(false);
		BadRecord.setTextColor(Color.GRAY);
	}

	@Override
	protected void onResume() {
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		super.onResume();
		gaowen_user = (EditText) findViewById(R.id.gaowen_user);
		gaowen_sid1 = (EditText) findViewById(R.id.gaowen_sid1);
		
		BadRecord.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showNormalDialog();
			}
		});
		gaowen_user.setOnEditorActionListener(EditListener);
		gaowen_sid1.setOnEditorActionListener(EditListener);

		gaowen_user.setOnFocusChangeListener(EditGetFocus);
		gaowen_sid1.setOnFocusChangeListener(EditGetFocus);
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
			if (v.getId() == R.id.gaowen_user) {
				if (!hasFocus) { // 失去焦点
					op = gaowen_user.getText().toString().trim().toUpperCase();
				} else { // 获取焦点
					gaowen_user.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.gaowen_sid1) {
				if (!hasFocus) { // 失去焦点
					sid1 = gaowen_sid1.getText().toString().trim()
							.toUpperCase();
				} else { // 获取焦点
					gaowen_sid1.setSelectAllOnFocus(true);
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
			if (v.getId() == R.id.gaowen_user) {
				if (actionId == EditorInfo.IME_ACTION_DONE) { // 用户名
					if (gaowen_user.getText().toString().toUpperCase()
							.equals("")
							|| gaowen_user.getText() == null) {
						ToastUtil.showToast(KanDaiActivity.this, "用户名不能为空！", 0);
						return false;
					}
					op = gaowen_user.getText().toString().toUpperCase();
					MyApplication.nextEditFocus(gaowen_sid1);
				}
			}
			if (v.getId() == R.id.gaowen_sid1) {
				if (actionId == EditorInfo.IME_ACTION_DONE) { // 用户名
					if (gaowen_user.getText().toString().toUpperCase()
							.equals("")
							|| gaowen_user.getText() == null) {
						ToastUtil.showToast(KanDaiActivity.this, "用户名不能为空！", 0);
						return false;
					}
					if (gaowen_sid1.getText().toString().toUpperCase()
							.equals("")
							|| gaowen_user.getText() == null) {
						ToastUtil.showToast(KanDaiActivity.this, "批次号不能为空！", 0);
						return false;
					}
					sid1 = gaowen_sid1.getText().toString().toUpperCase()
							.trim();
					Map<String, String> map = MyApplication.QueryBatNo(
							"TESTQUERY", "~lotno='" + sid1 + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "QuertSid1");
				}
			}
			return false;
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
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if (jsonObject.getInteger("id") != 0) {
								LoginTimeout_dig("密码错误", "");
							}
						}
						if (string.equals("QuertSid1")) { 
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(KanDaiActivity.this, "在该制程"
										+ MyApplication.KANDAI_ZCNO + "，没有该批次【"
										+ sid1 + "】或该批次已入站!", 0);
								gaowen_sid1.selectAll();
								return;
							} else {
								JSONObject jsonValue = (JSONObject) ((JSONArray) jsonObject
										.get("values")).get(0);
								if(Integer.parseInt(jsonValue.get("holdid").toString()) == 1){
									ToastUtil.showToast(KanDaiActivity.this,"该lot号已被【HOLD】",0);
								}
								// 0.测试站 1.编带站 2.看带站 3.已检验
								if (Integer.parseInt(jsonValue.get("lotstate")
										.toString()) == 0) {
									ToastUtil.showToast(KanDaiActivity.this,
											"该批号不具备入站条件,上个工序未出站!", 0);
									MyApplication.nextEditFocus(gaowen_sid1);
									gaowen_sid1.selectAll();
									return;
								} else if (Integer.parseInt(jsonValue.get(
										"lotstate").toString()) == 2) {
									ToastUtil.showToast(KanDaiActivity.this,
											"该批号已经出站!", 0);
									MyApplication.nextEditFocus(gaowen_sid1);
									gaowen_sid1.selectAll();
									return;
								} else {

									Map<String, String> map = MyApplication
											.QueryBatNo(
													"MOZCLISTWEB",
													"~sid1='"
															+ jsonValue
																	.get("sid1")
															+ "' and zcno='81' ");
									OkHttpUtils.getInstance().getServerExecute(
											MyApplication.MESURL, null, map,
											null, mHander, true,
											"QuertPlanaSid1");
								}
							}
						}
						if (string.equals("QuertPlanaSid1")) { // 查询批次号(mes_lot_plana)
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(KanDaiActivity.this,
										"没有查到批次（mes_lot_plana）", 0);
								MyApplication.nextEditFocus(gaowen_sid1);
								gaowen_sid1.selectAll();
								return;
							} else {
								JSONObject jsonValue = (JSONObject) ((JSONArray) jsonObject.get("values")).get(0);
								jsonValue.put("sbuid", "D0001");
								jsonValue.put("dcid",GetAndroidMacUtil.getMac());
								jsonValue.put("hpdate",MyApplication.GetServerNowTime());
								jsonValue.put("smake", MyApplication.user);
								jsonValue.put("lotno", sid1);
								jsonValue.put("bok","1");
								jsonValue.put("mkdate",MyApplication.GetServerNowTime());
								jsonValue.put("op", op);
								jsonValue.put("op_b", op);
								jsonValue.put("op_o", op);
								jsonValue.put("state1", "04");
								jsonValue.put("prd_name",jsonValue.get("prd_name"));
								jsonValue.put("outdate",MyApplication.GetServerNowTime());
								jsonValue.put("slkid", jsonValue.get("sid"));
								jsonValue.put("zcno", MyApplication.KANDAI_ZCNO);
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
								String sidAndlotno = jsonValue.get("sid1")
										+ ";" + gaowen_sid1.getText();
								Map<String, String> updateServerTable = MyApplication
										.UpdateServerTableMethod(
												MyApplication.DBID,
												MyApplication.user, jsonValue
														.get("state")
														.toString(), "04",
												sidAndlotno,
												jsonValue.get("slkid")
														.toString(),
												MyApplication.KANDAI_ZCNO,
												"200");
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null,
										updateServerTable, null, mHander, true,
										"updateTableState");

								// 200请求--------------------------------------------------

								// 适配器填值
								List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
								Map<String, String> map = new HashMap<String, String>();
								map.put("op", op);
								map.put("sid1", sid1);
								map.put("slkid", jsonValue.get("slkid")
										.toString());
								map.put("prd_no", jsonValue.getString("prd_no")
										.toString());
								map.put("qty", jsonValue.get("qty").toString());
								map.put("zcno", "看带");
								map.put("zcno1", "包装");
								mList.add(map);
								if (gaowendianliangApapter == null) {
									gaowendianliangApapter = new GaoWenDianLiangAdapter(
											KanDaiActivity.this, mList);
									mListView
											.setAdapter(gaowendianliangApapter);
								} else {
									gaowendianliangApapter.listData.add(map);
									gaowendianliangApapter
											.notifyDataSetChanged();
								}
								gaowen_sid1.selectAll();
								//调用键盘类
								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								// 如果软键盘已经显示，则隐藏，反之则显示
								imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
							}
							System.out.println(jsonObject);
						}
						if (string.equals("savedata")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if(Integer.parseInt(jsonObject.get("id").toString())==0){
								BadRecord.setEnabled(true);
								BadRecord.setTextColor(Color.RED);
 								JSONObject data = (JSONObject) jsonObject.get("data");
 								bad_sid = data.get("sid").toString();
							}
							System.out.println(jsonObject);
						}
						if (string.equals("bad_savedata")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if(Integer.parseInt(jsonObject.get("id").toString())==0){
								BadRecord.setEnabled(false);
								BadRecord.setTextColor(Color.GRAY);
							}
							System.out.println(jsonObject);
						}
						if (string.equals("updateTableState")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							System.out.println(jsonObject);
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
					KanDaiActivity.this);
			normalDialog.setTitle("不良记录");
			normalDialog.setView(dialog);
			normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
			normalDialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							JSONObject json_bad = new JSONObject();
							json_bad.put("sid",bad_sid);
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
			ToastUtil.showToast(KanDaiActivity.this, e1.toString(), 0);
		}
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
				KanDaiActivity.this);
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
