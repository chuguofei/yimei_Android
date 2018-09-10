package com.yimei.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

public class KanDaiBadActivity extends Activity {

	private EditText bad_fracture1,bad_crushed1,bad_poinrem1,bad_broken1,bad_pressing1,bad_overflow1,bad_lotno1; 
	private JSONObject BadJson;
	private boolean ok = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kandaibad);
		
	}
	
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
				if (tag.equals("lotno号")) { // 作业员
					bad_lotno1.setText(barcodeData.toString().toUpperCase().trim());
					Map<String, String> map = MyApplication.QueryBatNo(
							"TESTQUERY", "~lotno='" + bad_lotno1.getText().toString().toUpperCase() + "' ");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "QuertLotNo");
				}
			}
		}
	};
	
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
		bad_fracture1 = (EditText)findViewById(R.id.bad_fracture1);
		bad_crushed1 = (EditText) findViewById(R.id.bad_crushed1);
		bad_poinrem1 = (EditText) findViewById(R.id.bad_poinrem1);
		bad_broken1 = (EditText) findViewById(R.id.bad_broken1);
		bad_pressing1 = (EditText) findViewById(R.id.bad_pressing1);
		bad_overflow1 = (EditText) findViewById(R.id.bad_overflow1);
		bad_lotno1 = (EditText) findViewById(R.id.bad_lotno1);
		
		bad_lotno1.setOnEditorActionListener(EditListener);
		Button bad_save = (Button)findViewById(R.id.bad_save);
		bad_save.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!ok){
					ToastUtil.showToast(getApplicationContext(),"lot号做过不良或没有该lot号",0);
					return;
				}
				JSONObject json_bad = new JSONObject();
				json_bad.put("slkid",BadJson.get("sid")==null?"":BadJson.get("sid"));
				json_bad.put("sid1",BadJson.get("sid1")==null?"":BadJson.get("sid1"));
				json_bad.put("lotno", bad_lotno1.getText().toString().toUpperCase().trim());
				json_bad.put("prd_no",BadJson.get("prd_no")==null?"":BadJson.get("prd_no"));
				json_bad.put("mkdate",MyApplication.GetServerNowTime());
				json_bad.put("fracture",bad_fracture1.getText().toString().trim().equals("")?0:bad_fracture1.getText().toString().trim());
				json_bad.put("crushed",bad_crushed1.getText().toString().trim().equals("")?0:bad_crushed1.getText().toString().trim());
				json_bad.put("poinrem",bad_poinrem1.getText().toString().trim().equals("")?0:bad_poinrem1.getText().toString().trim());
				json_bad.put("broken",bad_broken1.getText().toString().trim().equals("")?0:bad_broken1.getText().toString().trim());
				json_bad.put("pressing",bad_pressing1.getText().toString().trim().equals("")?0:bad_pressing1.getText().toString().trim());
				json_bad.put("overflow",bad_overflow1.getText().toString().trim().equals("")?0:bad_overflow1.getText().toString().trim());
				json_bad.put("sid",bad_lotno1.getText().toString().toUpperCase().trim());
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
	}
	
	/**
	 * 回车事件
	 */
	OnEditorActionListener EditListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (v.getId() == R.id.bad_lotno1) {
				if (actionId >= 0) { // 用户名
					Map<String, String> map = MyApplication.QueryBatNo(
							"TESTQUERY", "~lotno='" + bad_lotno1.getText().toString().toUpperCase() + "' ");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "QuertLotNo");
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
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (jsonObject.getInteger("id") != 0) {
								LoginTimeout_dig("密码错误", "");
							}
						}
						if (string.equals("QuertLotNo")) { // 查询测试表
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(),"没有查到lot号!", 0);
								bad_lotno1.selectAll();
							} else {
								JSONObject jsonValue = (JSONObject) ((JSONArray) jsonObject.get("values")).get(0);
								Map<String, String> map = MyApplication
										.QueryBatNo("MOZCLISTWEB",
												"~sid1='"+ jsonValue.get("sid1")
														+ "' and zcno='"
														+ "81" + "' ");
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, map,
										null, mHander, true,
										"QuertPlanaSid1");
							}
						}
						if(string.equals("QuertPlanaSid1")){ //查询（mes_lot_plana）
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(KanDaiBadActivity.this,
										"没有查到批次（mes_lot_plana）", 0);
								return;
							} else {
								JSONObject jsonValue = (JSONObject) ((JSONArray) jsonObject.get("values")).get(0);
								BadJson = jsonValue;
								Map<String, String> map = MyApplication.QueryBatNo(
										"QUERYBAD", "~lotno='" + bad_lotno1.getText().toString().toUpperCase().trim() + "' ");
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, map, null, mHander,
										true, "QUERYBAD");
							}
						}
						if(string.equals("QUERYBAD")){ //查询lotno号
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (jsonObject.getInteger("code") == 1) {
								ToastUtil.showToast(getApplicationContext(),bad_lotno1.getText().toString()+"已经做过不良记录",0);
							}else{
								ok = true;
								MyApplication.nextEditFocus(bad_fracture1);
							}
						}
						if(string.equals("bad_savedata")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							System.out.println(jsonObject);
							KanDaiBadActivity.this.finish();
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
				KanDaiBadActivity.this);
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
}
