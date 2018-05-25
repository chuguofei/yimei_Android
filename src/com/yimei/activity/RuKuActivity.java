package com.yimei.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.os.Handler;
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

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONPObject;
import com.yimei.adapter.BianDaiAdapter;
import com.yimei.adapter.RuKuAdapter;
import com.yimei.entity.mesPrecord;
import com.yimei.entity.mesTmm0;
import com.yimei.scrollview.CHScrollView;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

public class RuKuActivity extends Activity {

	static MyApplication myapp;
	public static RuKuActivity rukuActivity;
	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private RuKuAdapter RuKuAdapter;

	private String zuoyeyuan;
	private String bat_no;

	private EditText yimei_ruku_user_edt;
	private EditText yimei_ruku_proNum_edt;

	private Map<String, Object> map1 = new HashMap<String, Object>(); // 判断是否是第二次或一扫描
	private Map<String, String> map2 = new HashMap<String, String>(); // 查询已检测的批次号

	/**
	 * 获取pda扫描（广播）
	 */
	private BroadcastReceiver barcodeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (MyApplication.INTENT_ACTION_SCAN_RESULT.equals(intent
					.getAction())) {
				View rootview = getCurrentFocus();
				int focusId = rootview.findFocus().getId();
				Object tag = rootview.findFocus().getTag();
				if(tag == null){
					return ;
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
				if(tag.equals("生产入库作业员")){
					yimei_ruku_user_edt.setText(barcodeData);
					if (yimei_ruku_user_edt.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(rukuActivity, "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_ruku_user_edt);
						return;
					}
					zuoyeyuan = yimei_ruku_user_edt.getText().toString().trim();
					MyApplication.nextEditFocus(yimei_ruku_proNum_edt);
				}
				if(tag.equals("生产入库批次号")){
					yimei_ruku_proNum_edt.setText(barcodeData);
					if (yimei_ruku_user_edt.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(rukuActivity, "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_ruku_user_edt);
						return;
					}
					if (yimei_ruku_proNum_edt.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(rukuActivity, "批次号不能为空", 0);
						MyApplication.nextEditFocus(yimei_ruku_proNum_edt);
						return;
					}
					bat_no = yimei_ruku_proNum_edt.getText().toString().trim();
					InitDate();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_ruku);
		Application app = getApplication();
		myapp = (MyApplication) app;
		myapp.addActivity_(this);
		rukuActivity = this;
		myapp.removeActivity_(LoginActivity.loginActivity);// 销毁登录
		
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.ruku_scroll_title);
		// 添加头滑动事件
		GeneralCHScrollView.add(headerScroll);
	}

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
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播

		yimei_ruku_user_edt = (EditText) findViewById(R.id.yimei_ruku_user_edt);
		yimei_ruku_proNum_edt = (EditText) findViewById(R.id.yimei_ruku_proNum_edt);

		yimei_ruku_user_edt.setOnEditorActionListener(editEnter);
		yimei_ruku_proNum_edt.setOnEditorActionListener(editEnter);

		yimei_ruku_user_edt.setOnFocusChangeListener(EditGetFocus);
		yimei_ruku_proNum_edt.setOnFocusChangeListener(EditGetFocus);

	}

	/**
	 * 回车事件
	 */
	OnEditorActionListener editEnter = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean flag = false;
			if (v.getId() == R.id.yimei_ruku_user_edt) { // 作业员
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_ruku_user_edt.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(rukuActivity, "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_ruku_user_edt);
						return false;
					}
					zuoyeyuan = yimei_ruku_user_edt.getText().toString().trim();
					MyApplication.nextEditFocus(yimei_ruku_proNum_edt);
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_ruku_proNum_edt) { //批次号
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_ruku_user_edt.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(rukuActivity, "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_ruku_user_edt);
						return false;
					}
					if (yimei_ruku_proNum_edt.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(rukuActivity, "批次号不能为空", 0);
						MyApplication.nextEditFocus(yimei_ruku_proNum_edt);
						return false;
					}
					bat_no = yimei_ruku_proNum_edt.getText().toString().trim();
					InitDate();
					flag = true;
				}
			}
			return flag;
		}
	};

	/**
	 * 初始化数据
	 */
	private void InitDate(){
		if (map1.size() == 0) { // 第一次扫描
			InitMM0Data();
		} else { // 第二次扫描
			if (map2.containsKey(bat_no)) {
				// 提示扫描的批次号已经检测
				ToastUtil.showToast(rukuActivity, "《" + bat_no
						+ "》批次号已扫描过~", 0);
				MyApplication.nextEditFocus(yimei_ruku_proNum_edt);
			} else {
				if (map1.containsKey(bat_no)) {
					// 修改为检测
					// 修改服务器状态
					JSONObject jsonValue = (JSONObject) JSONObject
							.toJSON(map1.get(bat_no));
					jsonValue.put("sys_stated", "2");
					jsonValue.put("checkid", "1");
					Map<String, String> httpMapKeyValueMethod = MyApplication
							.httpMapKeyValueMethod(
									MyApplication.DBID, "savedata",
									MyApplication.user,
									jsonValue.toString(),
									"E0004AWEB", "1");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null,
							httpMapKeyValueMethod, null, mHander,
							true, "UpdateCheckid");

					List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
					for (int i = 0; i < RuKuAdapter.getCount(); i++) {
						Map<String, String> map = (Map<String, String>) RuKuAdapter
								.getItem(i);
						if (map.get("ruku_bat_no").equals(bat_no)) {
							map.put("ruku_checkid", "√");
							map2.put(bat_no, bat_no);
						}
						mapList.add(map);
					}
					RuKuAdapter = new RuKuAdapter(rukuActivity,
							mapList);
					mListView.setAdapter(RuKuAdapter);
					RuKuAdapter.notifyDataSetChanged();

				} else {
					if (map1.size() == map2.size()) {
						// 提示换缴库单号
						showNormalDialog("该缴库单号已扫完，是否切换新的单号~");
					} else {
						// 混批
						ToastUtil.showToast(rukuActivity,
								"请将当前的缴库单号扫完~", 0);
						MyApplication.nextEditFocus(yimei_ruku_proNum_edt);
					}
				}
			}
		}
	}
	
	/**
	 * 拿值，修改状态，换申请表
	 */
	private void InitMM0Data() {
		Map<String, String> map = MyApplication.QueryBatNo("MESTMM0",
				"~bat_no='" + bat_no + "'");
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				map, null, mHander, true, "QueryBatNo"); // 查询批次号
		MyApplication.nextEditFocus(yimei_ruku_proNum_edt);
	}

	/**
	 * 弹出提示框
	 * 
	 * @param mes
	 */
	private void showNormalDialog(String mes) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				rukuActivity);
		normalDialog.setTitle("提示");
		normalDialog.setMessage(mes);
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListView.setAdapter(null);
						RuKuAdapter.notifyDataSetChanged();
						InitMM0Data();
						MyApplication.nextEditFocus(yimei_ruku_proNum_edt);
					}
				});
		normalDialog.setNegativeButton("取消", null);
		// 显示
		normalDialog.show();
	}

	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_ruku_user_edt) {
				if (!hasFocus) {
					zuoyeyuan = yimei_ruku_user_edt.getText().toString().trim();
				} else {
					yimei_ruku_user_edt.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_ruku_proNum_edt) {
				if (hasFocus) {
					yimei_ruku_proNum_edt.setSelectAllOnFocus(true);
				}
			}
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
					if (string.equals("QueryBatNo")) {
						map1.clear();
						map2.clear();
						JSONObject jsonObject = JSON.parseObject(b.getString(
								"jsonObj").toString());
						if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
							ToastUtil.showToast(rukuActivity, "没有该批次号!", 0);
							yimei_ruku_proNum_edt.selectAll();
						} else {
							mesTmm0 mesTmm0 = new mesTmm0();

							for (int i = 0; i < ((JSONArray) jsonObject
									.get("values")).size(); i++) {
								JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
										.get("values")).get(i));
								mesTmm0 = jsonValue.toJavaObject(mesTmm0.class);
							}

							Map<String, String> map = MyApplication.QueryBatNo(
									"MESTMM0", "~mm_no='" + mesTmm0.getMm_no()
											+ "'");
							OkHttpUtils.getInstance().getServerExecute(
									MyApplication.MESURL, null, map, null,
									mHander, true, "QueryMmNo"); // 查询缴库单号

						}
					}
					if (string.equals("QueryMmNo")) {
						JSONObject jsonObject = JSON.parseObject(b.getString(
								"jsonObj").toString());
						if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
							ToastUtil.showToast(rukuActivity, "该缴库单号没有数据!", 0);
						} else {
							List<Map<String, String>> displatTmm = DisplatTmm(jsonObject);
							RuKuAdapter = new RuKuAdapter(rukuActivity,
									displatTmm);
							mListView.setAdapter(RuKuAdapter);
							/*ToastUtil.showToast(getApplicationContext(),
									"数据已加载~", 0);*/
						}
					}
				}
			} else {
				Log.i("err", msg.obj.toString());
			}

		}
	};

	/**
	 * 填充适配器之前
	 * 
	 * @param jsonObject
	 * @return
	 */
	private List<Map<String, String>> DisplatTmm(JSONObject jsonObject) {
		List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
		mListView = (ListView) findViewById(R.id.ruku_scroll_list);
		Map<String, String> map = null;
		for (int i = 0; i < ((JSONArray) jsonObject.get("values")).size(); i++) {
			JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
					.get("values")).get(i));
			mesTmm0 mesTmm0 = jsonValue.toJavaObject(mesTmm0.class);
			map = new HashMap<String, String>();
			map.put("ruku_cid", mesTmm0.getItm());

			map1.put(mesTmm0.getBat_no(), mesTmm0);
			if(mesTmm0.getCheckid()==null){
				mesTmm0.setCheckid("0");
			}
			if (Integer.parseInt(mesTmm0.getCheckid()) == 0
					&& mesTmm0.getBat_no().equals(bat_no)) {
				mesTmm0.setCheckid("1");
				// 修改服务器状态
				jsonValue.put("sys_stated", "2");
				Map<String, String> httpMapKeyValueMethod = MyApplication
						.httpMapKeyValueMethod(MyApplication.DBID, "savedata",
								MyApplication.user, jsonValue.toString(),
								"E0004AWEB", "1");
				OkHttpUtils.getInstance().getServerExecute(
						MyApplication.MESURL, null, httpMapKeyValueMethod,
						null, mHander, true, "UpdateCheckid");
			}
			if (Integer.parseInt(mesTmm0.getCheckid()) == 1) {
				map2.put(mesTmm0.getBat_no(), mesTmm0.getBat_no());
			}
			map.put("ruku_checkid",
					Integer.parseInt(mesTmm0.getCheckid()) == 1 ? "√" : "");
			map.put("ruku_bat_no", mesTmm0.getBat_no());
			map.put("ruku_prd_no", mesTmm0.getPrd_no());
			map.put("ruku_prd_mark", mesTmm0.getPrd_mark());
			map.put("ruku_prd_name", mesTmm0.getPrd_name());
			map.put("ruku_wh", mesTmm0.getWh());
			map.put("ruku_qty", mesTmm0.getQty());
			map.put("ruku_info", mesTmm0.getRem());
			mList.add(map);
		}
		return mList;
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
}
