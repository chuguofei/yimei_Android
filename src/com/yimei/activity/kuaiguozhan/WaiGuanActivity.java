package com.yimei.activity.kuaiguozhan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.MyApplication;
import com.yimei.activity.R;
import com.yimei.activity.R.id;
import com.yimei.activity.R.layout;
import com.yimei.adapter.GaoWenDianLiangAdapter;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * 
 * @author Administrator 对象：D50301 辅助：MOZCLISTWEB //查询批次号 功能：快速过站
 */
public class WaiGuanActivity extends Activity {
	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private EditText gaowen_user, gaowen_sid1;
	private String op, sid1;
	private GaoWenDianLiangAdapter gaowendianliangApapter;

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
					gaowen_user.setText(barcodeData.toString().toUpperCase()
							.trim());
					if (gaowen_user.toString().toUpperCase().trim().equals("")
							|| gaowen_user.toString().toUpperCase().trim() == null) {
						ToastUtil.showToast(WaiGuanActivity.this, "作业员不能为空", 0);
						MyApplication.nextEditFocus(gaowen_user);
						return;
					}
					op = barcodeData.toString().toUpperCase().trim();
					MyApplication.nextEditFocus(gaowen_sid1);
				}
				if (tag.equals("高温点亮批次号")) { // 作业员
					gaowen_sid1.setText(barcodeData.toString().trim());
					if (gaowen_user.toString().toUpperCase().trim().equals("")
							|| gaowen_user.toString().toUpperCase().trim() == null) {
						ToastUtil.showToast(WaiGuanActivity.this, "作业员不能为空", 0);
						MyApplication.nextEditFocus(gaowen_user);
						return;
					}
					if (gaowen_sid1.toString().toUpperCase().trim().equals("")
							|| gaowen_sid1.toString().toUpperCase().trim() == null) {
						ToastUtil.showToast(WaiGuanActivity.this, "批次号不能为空", 0);
						MyApplication.nextEditFocus(gaowen_sid1);
						return;
					}
					sid1 = gaowen_sid1.getText().toString().toUpperCase()
							.trim();
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZCLISTWEB", "~sid1='" + sid1 + "' and"
									+ " zcno='" + MyApplication.WaiGuan_ZCNO
									+ "'");
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
	}

	@Override
	protected void onResume() {
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		super.onResume();
		gaowen_user = (EditText) findViewById(R.id.gaowen_user);
		gaowen_sid1 = (EditText) findViewById(R.id.gaowen_sid1);

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
						ToastUtil
								.showToast(WaiGuanActivity.this, "用户名不能为空！", 0);
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
						ToastUtil
								.showToast(WaiGuanActivity.this, "用户名不能为空！", 0);
						return false;
					}
					if (gaowen_sid1.getText().toString().toUpperCase()
							.equals("")
							|| gaowen_user.getText() == null) {
						ToastUtil
								.showToast(WaiGuanActivity.this, "批次号不能为空！", 0);
						return false;
					}
					sid1 = gaowen_sid1.getText().toString().toUpperCase()
							.trim();
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZCLISTWEB", "~sid1='" + sid1 + "' and"
									+ " zcno='" + MyApplication.WaiGuan_ZCNO
									+ "'");
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
						if (string.equals("QuertSid1")) { // 模具编号回车
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(WaiGuanActivity.this,
										"在该制程" + MyApplication.WaiGuan_ZCNO
												+ "，没有该批次【" + sid1 + "】!", 0);
								gaowen_sid1.selectAll();
								return;
							} else {
								JSONObject jsonValue = (JSONObject) ((JSONArray) jsonObject
										.get("values")).get(0);
								if (Integer.parseInt(jsonValue.get("bok")
										.toString()) == 0) {
									ToastUtil.showToast(WaiGuanActivity.this,
											"该批号不具备入站条件,上个工序为出站!", 0);
									gaowen_sid1.selectAll();
									return;
								} else if (jsonValue.get("state").toString()
										.equals("02")
										|| jsonValue.get("state").toString()
												.equals("03")) {
									ToastUtil.showToast(WaiGuanActivity.this,
											"该批号已经入站!", 0);
									gaowen_sid1.selectAll();
									return;
								} else if (jsonValue.get("state").toString()
										.equals("04")) {
									ToastUtil.showToast(WaiGuanActivity.this,
											"该批号已经出站!", 0);
									gaowen_sid1.selectAll();
									return;
								} else {
									jsonValue.put("sbuid", "D0001");
									jsonValue.put("dcid",
											GetAndroidMacUtil.getMac());
									jsonValue.put("smake", MyApplication.user);
									jsonValue.put("mkdate",
											MyApplication.GetServerNowTime());
									jsonValue.put("op", op);
									jsonValue
											.put("slkid", jsonValue.get("sid"));
									jsonValue.put("zcno",
											MyApplication.WaiGuan_ZCNO);
									// savedate--------------------------------------------
									Map<String, String> mesIdMap = MyApplication
											.httpMapKeyValueMethod(
													MyApplication.DBID,
													"savedata",
													MyApplication.user,
													jsonValue.toJSONString(),
													"D50301", "1");
									OkHttpUtils.getInstance().getServerExecute(
											MyApplication.MESURL, null,
											mesIdMap, null, mHander, true,
											"savedata");
									// savedate--------------------------------------------
									// 200请求--------------------------------------------------
									Map<String, String> updateServerTable = MyApplication
											.UpdateServerTableMethod(
													MyApplication.DBID,
													MyApplication.user,
													jsonValue.get("state")
															.toString(), "04",
													jsonValue.get("sid1")
															.toString(),
													jsonValue.get("slkid")
															.toString(),
													MyApplication.WaiGuan_ZCNO,
													"200");
									OkHttpUtils.getInstance().getServerExecute(
											MyApplication.MESURL, null,
											updateServerTable, null, mHander,
											true, "updateTableState");
									// 200请求--------------------------------------------------

									// 适配器填值
									List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
									Map<String, String> map = new HashMap<String, String>();
									map.put("sid1", sid1);
									map.put("slkid", jsonValue.get("slkid")
											.toString());
									map.put("prd_no",
											jsonValue.getString("prd_no")
													.toString());
									map.put("qty", jsonValue.get("qty")
											.toString());
									map.put("zcno", MyApplication.WaiGuan_ZCNO);
									map.put("zcno1",
											jsonValue.containsKey("zcno1") ? jsonValue
													.getString("zcno1")
													: MyApplication.TieBieJiao_ZCNO);
									mList.add(map);
									if (gaowendianliangApapter == null) {
										gaowendianliangApapter = new GaoWenDianLiangAdapter(
												WaiGuanActivity.this, mList);
										mListView
												.setAdapter(gaowendianliangApapter);
									} else {
										gaowendianliangApapter.listData
												.add(map);
										gaowendianliangApapter
												.notifyDataSetChanged();
									}
									gaowen_sid1.selectAll();
								}
							}
						}
						if (string.equals("savedata")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
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
