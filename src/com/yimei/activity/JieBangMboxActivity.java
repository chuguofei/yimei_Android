package com.yimei.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.ipqc.ORT_quyang;
import com.yimei.activity.kuaiguozhan.DiDianLiuActivity;
import com.yimei.adapter.GaoWenDianLiangAdapter;
import com.yimei.adapter.JiaXiGaoAdapter;
import com.yimei.adapter.ORTQuYangAdapter;
import com.yimei.adapter.jiebangAdapter;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * 
 * @author Administrator 辅助: MBOXQUERY（器件料盒号查询） QJBOXWEB（生产批次信息查询料盒号(手持)）
 */
public class JieBangMboxActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private static jiebangAdapter jiebangApapter;
	private EditText yimei_jiebang_user, yimei_jiebang_sid1,
			yimei_jiebang_mbox;
	private String op;

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
				if (tag.equals("解绑料盒用户名")) {
					yimei_jiebang_user.setText(barcodeData.toString()
							.toUpperCase().trim());
					if (yimei_jiebang_user.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_jiebang_user.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(JieBangMboxActivity.this,
								"请输入作业员~", 0);
						MyApplication.nextEditFocus(yimei_jiebang_user);
						return;
					}
					op = yimei_jiebang_user.getText().toString().toUpperCase()
							.trim();
					MyApplication.nextEditFocus(yimei_jiebang_mbox);
				}
				if (tag.equals("解绑料盒")) {
					yimei_jiebang_mbox.setText(barcodeData.toString().trim());
					if (yimei_jiebang_user.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_jiebang_user.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(JieBangMboxActivity.this,
								"请输入作业员~", 0);
						MyApplication.nextEditFocus(yimei_jiebang_user);
						return;
					}
					if (yimei_jiebang_mbox.getText().toString().trim()
							.equals("")
							|| yimei_jiebang_mbox.getText().toString().trim() == null) {
						ToastUtil.showToast(JieBangMboxActivity.this,
								"请输入料盒号~", 0);
						MyApplication.nextEditFocus(yimei_jiebang_mbox);
						return;
					}
					AccessServer("MBOXQUERY", "~id='"
							+ yimei_jiebang_mbox.getText().toString().trim()
							+ "'", "QueryMbox");
				}
				if (tag.equals("解绑料盒批次")) {
					yimei_jiebang_sid1.setText(barcodeData.toString().trim());
					if (yimei_jiebang_user.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_jiebang_user.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(JieBangMboxActivity.this,
								"请输入作业员~", 0);
						MyApplication.nextEditFocus(yimei_jiebang_user);
						return;
					}
					if (yimei_jiebang_mbox.getText().toString().trim()
							.equals("")
							|| yimei_jiebang_mbox.getText().toString().trim() == null) {
						ToastUtil.showToast(JieBangMboxActivity.this,
								"请输入料盒号~", 0);
						MyApplication.nextEditFocus(yimei_jiebang_mbox);
						return;
					}
					if (yimei_jiebang_sid1.getText().toString().trim()
							.equals("")
							|| yimei_jiebang_sid1.getText().toString().trim() == null) {
						ToastUtil.showToast(JieBangMboxActivity.this,
								"请输入批次号~", 0);
						MyApplication.nextEditFocus(yimei_jiebang_sid1);
						return;
					}
					AccessServer("QJBOXWEB", "~sid1='"
							+ yimei_jiebang_sid1.getText().toString().trim()
							+ "' and zcno='31'", "QuerySid1");
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_mbox);
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.jiebang_scroll_title);
		// 添加头滑动事件
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.jiebang_scroll_list);
	}
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(barcodeReceiver); // 取消广播注册
	}

	@Override
	protected void onResume() {
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		super.onResume();
		yimei_jiebang_user = (EditText) findViewById(R.id.yimei_jiebang_user);
		yimei_jiebang_sid1 = (EditText) findViewById(R.id.yimei_jiebang_sid1);
		yimei_jiebang_mbox = (EditText) findViewById(R.id.yimei_jiebang_mbox);

		yimei_jiebang_user.setOnEditorActionListener(EditEnter);
		yimei_jiebang_sid1.setOnEditorActionListener(EditEnter);
		yimei_jiebang_mbox.setOnEditorActionListener(EditEnter);
	}

	/**
	 * 文本框回车事件
	 */
	OnEditorActionListener EditEnter = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (v.getId() == R.id.yimei_jiebang_user) {
				if (actionId >= 0) {
					if (yimei_jiebang_user.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_jiebang_user.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(JieBangMboxActivity.this,
								"请输入作业员~", 0);
						MyApplication.nextEditFocus(yimei_jiebang_user);
						return false;
					}
					op = yimei_jiebang_user.getText().toString().toUpperCase()
							.trim();
					MyApplication.nextEditFocus(yimei_jiebang_mbox);
				}
			}
			if (v.getId() == R.id.yimei_jiebang_sid1) {
				if (actionId >= 0) {
					if (yimei_jiebang_user.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_jiebang_user.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(JieBangMboxActivity.this,
								"请输入作业员~", 0);
						MyApplication.nextEditFocus(yimei_jiebang_user);
						return false;
					}
					if (yimei_jiebang_mbox.getText().toString().trim()
							.equals("")
							|| yimei_jiebang_mbox.getText().toString().trim() == null) {
						ToastUtil.showToast(JieBangMboxActivity.this,
								"请输入料盒号~", 0);
						MyApplication.nextEditFocus(yimei_jiebang_mbox);
						return false;
					}
					if (yimei_jiebang_sid1.getText().toString().trim()
							.equals("")
							|| yimei_jiebang_sid1.getText().toString().trim() == null) {
						ToastUtil.showToast(JieBangMboxActivity.this,
								"请输入批次号~", 0);
						MyApplication.nextEditFocus(yimei_jiebang_sid1);
						return false;
					}
					AccessServer("QJBOXWEB", "~sid1='"
							+ yimei_jiebang_sid1.getText().toString().trim()
							+ "' and zcno='31'", "QuerySid1");
				}
			}
			if (v.getId() == R.id.yimei_jiebang_mbox) {
				if (actionId >= 0) {
					if (yimei_jiebang_user.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_jiebang_user.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(JieBangMboxActivity.this,
								"请输入作业员~", 0);
						MyApplication.nextEditFocus(yimei_jiebang_user);
						return false;
					}
					if (yimei_jiebang_mbox.getText().toString().trim()
							.equals("")
							|| yimei_jiebang_mbox.getText().toString().trim() == null) {
						ToastUtil.showToast(JieBangMboxActivity.this,
								"请输入料盒号~", 0);
						MyApplication.nextEditFocus(yimei_jiebang_mbox);
						return false;
					}
					AccessServer("MBOXQUERY", "~id='"
							+ yimei_jiebang_mbox.getText().toString().trim()
							+ "'", "QueryMbox");
				}
			}
			return false;
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
				JieBangMboxActivity.this);
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
						OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
								map, null, mHander, true,"TimeOutLogin");
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
					try {
						if (string.equals("TimeOutLogin")) { // 超时登录
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if (jsonObject.getInteger("id") != 0) {
								LoginTimeout_dig("密码错误", "");
							}
						}
						if (string.equals("QuerySid1")) { // 查询批次号
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(),
										"没有【"
												+ yimei_jiebang_sid1.getText()
														.toString().trim()
												+ "】批次号!", 0);
								MyApplication.nextEditFocus(yimei_jiebang_sid1);
								yimei_jiebang_sid1.selectAll();
								InputHidden(); // 隐藏键盘
								return;
							} else {
								JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
										.get("values")).get(0));
								if (jsonValue.get("mbox").toString().trim()
										.equals("")) {
									ToastUtil.showToast(
											JieBangMboxActivity.this, "【"
													+ yimei_jiebang_sid1
															.getText()
															.toString()
													+ "】没有绑定料盒,无法解绑!", 0);
									yimei_jiebang_sid1.selectAll();
									return;
								}
								if (!jsonValue
										.get("mbox")
										.toString()
										.trim()
										.toUpperCase()
										.equals(yimei_jiebang_mbox.getText()
												.toString().trim()
												.toUpperCase())) {
									ToastUtil.showToast(
											JieBangMboxActivity.this, "【"
													+ yimei_jiebang_sid1
															.getText()
															.toString()
													+ "】批次绑定是"+jsonValue
													.get("mbox")+"料盒号,扫描的是【"
													+ yimei_jiebang_mbox
															.getText()
															.toString().trim()
															.toUpperCase()
													+ "】料盒号，无法解绑!", 0);
									yimei_jiebang_sid1.selectAll();
									return;
								}
								jsonValue.put("sbuid", "D0090");
								jsonValue.put("mbox", yimei_jiebang_mbox.getText().toString().toUpperCase());
								jsonValue.put("sorg", MyApplication.sorg);
								jsonValue.put("sopr", yimei_jiebang_user.getText().toString().toUpperCase());
								jsonValue.put("hpdate",
										MyApplication.GetServerNowTime());
								jsonValue.put("slkid", jsonValue.get("sid"));
								jsonValue.put("smake", MyApplication.user);
								jsonValue.put("mkdate",
										MyApplication.GetServerNowTime());
								jsonValue.put("dcid",
										GetAndroidMacUtil.getMac());

								Map<String, String> mesIdMap = MyApplication
										.httpMapKeyValueMethod(
												MyApplication.DBID, "savedata",
												MyApplication.user,
												jsonValue.toJSONString(),
												"D0090WEB", "1");
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, mesIdMap,
										null, mHander, true, "savedata_mbox");
							}
						}
						if (string.equals("QueryMbox")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(),
										"没有【"
												+ yimei_jiebang_mbox.getText()
														.toString().trim()
												+ "】料盒号!", 0);
								MyApplication.nextEditFocus(yimei_jiebang_mbox);
								yimei_jiebang_mbox.selectAll();
								InputHidden(); // 隐藏键盘
								return;
							} else {
								MyApplication.nextEditFocus(yimei_jiebang_sid1);
							}
						}
						if (string.equals("MboxServerUpdate")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id").toString()) != 1) {
								ToastUtil.showToast(getApplicationContext(),"（update）修改失败", 0);
							}else{
								ArrayList<Map<String, String>> mList = new ArrayList<>();
								Map<String, String> map = new HashMap<String, String>();
								map.put("op", op);
								map.put("mbox", yimei_jiebang_mbox.getText()
										.toString());
								map.put("sid1", yimei_jiebang_sid1.getText()
										.toString());
								mList.add(map);
								if (jiebangApapter == null) {
									jiebangApapter = new jiebangAdapter(
											JieBangMboxActivity.this, mList);
									mListView.setAdapter(jiebangApapter);
								} else {
									jiebangApapter.listData.add(0,map);
									jiebangApapter.notifyDataSetChanged();
								}
								yimei_jiebang_mbox.setText("");
								yimei_jiebang_sid1.setText("");
								InputHidden();
								MyApplication.nextEditFocus(yimei_jiebang_mbox);
							}
							System.out.println(jsonObject);
						}
						if (string.equals("savedata_mbox")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id").toString()) != 0) {
								ToastUtil.showToast(getApplicationContext(),"（savedata）添加失败", 0);
							}else{
								JSONArray jsonArr_650 = new JSONArray();
								JSONObject jsonobj_650 = new JSONObject();
								jsonobj_650.put("sid1", yimei_jiebang_sid1
										.getText().toString().trim());
								jsonobj_650.put("zcno", "41");
								jsonobj_650.put("mbox", yimei_jiebang_mbox
										.getText().toString().trim());
								jsonobj_650.put("bind", "0"); // 解绑料盒
								jsonArr_650.add(jsonobj_650);

								Map<String, String> MboxServerUpdate_650 = new HashMap<>();
								MboxServerUpdate_650.put("dbid",
										MyApplication.DBID);
								MboxServerUpdate_650.put("usercode",
										MyApplication.user);
								MboxServerUpdate_650.put("apiId", "mesudp");
								MboxServerUpdate_650.put("jsondata",
										jsonArr_650.toString());
								MboxServerUpdate_650.put("id", "650");
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null,
										MboxServerUpdate_650, null, mHander,
										true, "MboxServerUpdate");
							}
							System.out.println(jsonObject);
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
	 * 隐藏键盘
	 */
	private void InputHidden() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 如果软键盘已经显示，则隐藏，反之则显示
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
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
