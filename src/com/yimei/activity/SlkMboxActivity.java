package com.yimei.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.scrollview.GeneralCHScrollView;
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
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * 辅助：
 *  FiveHourMbox //查询料盒号大于5小时
 *  SLKMBOXCOUNT //查询是新增还是修改
 *  MBOX_SLKID //查询工单号 （mes_cp_lot）的批次数量（tabNum）
 *  
 * @author Administrator
 *
 */
public class SlkMboxActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private EditText yimei_slkmbox_op,yimei_slkmbox_slkid,yimei_slkmbox_mbox;
	private Integer tabNum = 0;
	private String slkid;
	private int cid = 0;
	private String sys_stated ="1";
	private String sid;
	
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
				if (tag.equals("工单料盒作业员")) { // 作业员
					yimei_slkmbox_op.setText(barcodeData.toString()
							.toUpperCase().trim());
					if (yimei_slkmbox_op.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_slkmbox_op.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(SlkMboxActivity.this,"作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_op);
						return;
					}
					MyApplication.nextEditFocus(yimei_slkmbox_mbox);
				}
				if (tag.equals("工单绑定工单")) { // 作业员
					yimei_slkmbox_slkid.setText(barcodeData.toString()
							.toUpperCase().trim());
					if (yimei_slkmbox_op.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_slkmbox_op.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(SlkMboxActivity.this,"作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_op);
						return;
					}
					if (yimei_slkmbox_slkid.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_slkmbox_slkid.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(SlkMboxActivity.this,"工单号不能为空", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_slkid);
						return;
					}
					Map<String, String> map = MyApplication.QueryBatNo("MBOX_SLKID", "~sid='" + yimei_slkmbox_slkid.getText().toString() + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "QuerySLKID");
				}
				if (tag.equals("工单绑定料盒号")) {
					yimei_slkmbox_mbox.setText(barcodeData.toString().toUpperCase().trim());
					if (yimei_slkmbox_op.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_slkmbox_op.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(SlkMboxActivity.this,"作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_op);
						return;
					}
					if (yimei_slkmbox_slkid.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_slkmbox_slkid.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(SlkMboxActivity.this,"工单号不能为空", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_slkid);
						return;
					}
					if (yimei_slkmbox_mbox.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_slkmbox_mbox.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(SlkMboxActivity.this,"料盒号不能为空", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_slkid);
						return;
					}
					Map<String, String> map = MyApplication.QueryBatNo("SLKMBOXCOUNT", "~slkid='" + yimei_slkmbox_slkid.getText().toString() + "'");
					OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null, map, null, mHander,
							true, "QueryMBoxCount1"); //查询绑定工单料盒表是否存在记录，是新增还是修改
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slkmbox);
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.slkmbox_scroll_title);
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.slkmbox_scroll_list);
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
		
		yimei_slkmbox_op = (EditText) findViewById(R.id.yimei_slkmbox_op);
		yimei_slkmbox_slkid = (EditText) findViewById(R.id.yimei_slkmbox_slkid);
		yimei_slkmbox_mbox = (EditText) findViewById(R.id.yimei_slkmbox_mbox);
		
		yimei_slkmbox_op.setOnFocusChangeListener(FoucusChange);
		yimei_slkmbox_slkid.setOnFocusChangeListener(FoucusChange);
		yimei_slkmbox_mbox.setOnFocusChangeListener(FoucusChange);
		
		yimei_slkmbox_mbox.setOnEditorActionListener(EditEnter);
		yimei_slkmbox_op.setOnEditorActionListener(EditEnter);
		yimei_slkmbox_slkid.setOnEditorActionListener(EditEnter);
	}
	
	OnEditorActionListener EditEnter = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(v.getId() == R.id.yimei_slkmbox_op){
				if (actionId == 0) { 
					if(yimei_slkmbox_op.getText().toString().trim().equals("")){
						ToastUtil.showToast(getApplicationContext(), "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_op);
						return false;
					}
					MyApplication.nextEditFocus(yimei_slkmbox_slkid);
				}
			}
			if(v.getId() == R.id.yimei_slkmbox_slkid){
				if (actionId == 0) { 
					if (yimei_slkmbox_op.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_slkmbox_op.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(SlkMboxActivity.this,"作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_op);
						return false;
					}
					if (yimei_slkmbox_slkid.getText().toString().toUpperCase()
							.trim().equals("")
							|| yimei_slkmbox_slkid.getText().toString()
									.toUpperCase().trim() == null) {
						ToastUtil.showToast(SlkMboxActivity.this,"工单号不能为空", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_slkid);
						return false;
					}
					Map<String, String> map = MyApplication.QueryBatNo("MBOX_SLKID", "~sid='" + yimei_slkmbox_slkid.getText().toString() + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "QuerySLKID");
				}
			}
			if(v.getId() == R.id.yimei_slkmbox_mbox){
				if (actionId == 0) { 
					if(yimei_slkmbox_op.getText().toString().trim().equals("")){
						ToastUtil.showToast(getApplicationContext(), "作业员不能为空！", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_op);
						return false;
					}
					if(yimei_slkmbox_slkid.getText().toString().trim().equals("")){
						ToastUtil.showToast(getApplicationContext(), "工单号不能为空！", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_slkid);
						return false;
					}
					if(yimei_slkmbox_mbox.getText().toString().trim().equals("")){
						ToastUtil.showToast(getApplicationContext(), "料盒号不能为空！", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_mbox);
						return false;
					}
					Map<String, String> map = MyApplication.QueryBatNo("SLKMBOXCOUNT", "~slkid='" + yimei_slkmbox_slkid.getText().toString() + "'");
					OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null, map, null, mHander,
							true, "QueryMBoxCount1"); //查询绑定工单料盒表是否存在记录，是新增还是修改
					/*Map<String, String> map = MyApplication.QueryBatNo("MBOXQUERY", "~id='"+yimei_slkmbox_mbox.getText().toString().toUpperCase()+"'");
					OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null, map, null, mHander,
							true, "QueryMBox");*/
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
						if(string.equals("QueryMESOP")){ //检查人是否存在
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (jsonObject.getInteger("code") == 0) {
								MyApplication.nextEditFocus(yimei_slkmbox_op);
								ToastUtil.showToast(getApplicationContext(),"请输入正确的作业员工号!",0);
								InputHidden();
							}
						}
						if (string.equals("QuerySLKID")) { // 查询工单号
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (jsonObject.getInteger("code") == 0) {
								ToastUtil.showToast(getApplicationContext(),"没有查询到"+yimei_slkmbox_slkid.getText().toString()+"工单号!",0);
								MyApplication.nextEditFocus(yimei_slkmbox_slkid);
								yimei_slkmbox_slkid.selectAll();
								InputHidden();
								return;
							}else{
								JSONObject jsonValue = (JSONObject) ((JSONArray) jsonObject.get("values")).get(0);
								tabNum = jsonValue.getInteger("tabnum");
								ToastUtil.showToast(getApplicationContext(),"可绑定"+tabNum.toString()+"个!",0);
								slkid = jsonValue.getString("sid");
								Map<String, String> map = MyApplication.QueryBatNo("SLKMBOXCOUNT", "~slkid='" + yimei_slkmbox_slkid.getText().toString() + "'");
								OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null, map, null, mHander,
										true, "QueryMBoxCount"); //查询绑定工单料盒表是否存在记录，是新增还是修改
							}
						}
						if(string.equals("QueryMBoxCount")){ //查询绑定工单料盒表是否存在记录，是新增还是修改
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (jsonObject.getInteger("code") == 0) {
								sys_stated = "3";
								cid = 0;
							}else{ //修改
								sys_stated = "2";
								JSONObject jsonValue = (JSONObject) ((JSONArray) jsonObject.get("values")).get(0);
								cid = jsonValue.getIntValue("cid");
							}
							MyApplication.nextEditFocus(yimei_slkmbox_mbox);
						}
						if(string.equals("QueryMBoxCount1")){ //查询绑定工单料盒表是否存在记录，是新增还是修改
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (jsonObject.getInteger("code") == 0) {
								sys_stated = "3";
								cid = 0;
							}else{
								sys_stated = "2";
								JSONObject jsonValue = (JSONObject) ((JSONArray) jsonObject.get("values")).get(0);
								sid = jsonValue.getString("sid");
								cid = jsonValue.getIntValue("cid"); //count
							}
							if(cid == tabNum){
								ToastUtil.showToast(getApplicationContext(),yimei_slkmbox_slkid.getText().toString().toUpperCase()+"工单料盒已经帮够！", 0);
								yimei_slkmbox_mbox.setText("");
								yimei_slkmbox_slkid.setText("");
								MyApplication.nextEditFocus(yimei_slkmbox_slkid);
								cid = 0;
								return;
							}
							//查询5小时外并且可用的料盒
							Map<String, String> map = MyApplication.QueryBatNo("FIVEHOURMBOX", "~id='"+yimei_slkmbox_mbox.getText().toString().toUpperCase()+"'");
							OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null, map, null, mHander,
									true, "QueryMBox");
						}
						if (string.equals("QueryMBox")) { // 查询料盒号
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (jsonObject.getInteger("code") == 0) {
								ToastUtil.showToast(getApplicationContext(),"【"+yimei_slkmbox_mbox.getText().toString()+"】料盒不存在或在使用!",0);
								yimei_slkmbox_mbox.selectAll();
								InputHidden();
								return;
							}else{
								JSONObject jsonValue = (JSONObject) ((JSONArray) jsonObject.get("values")).get(0);
								if(jsonValue.getInteger("bok") == 1){ 
									ToastUtil.showToast(getApplicationContext(),"该【"+yimei_slkmbox_mbox.getText().toString()+"】被别的工单已绑定!",0);
									yimei_slkmbox_mbox.selectAll();
									InputHidden();
								}else{
									JSONObject jsonFatcher = new JSONObject();
									jsonFatcher.put("slkid",yimei_slkmbox_slkid.getText().toString().toUpperCase());
									jsonFatcher.put("op",yimei_slkmbox_op.getText().toString().toUpperCase());
									jsonFatcher.put("mkdate",MyApplication.GetServerNowTime());
									jsonFatcher.put("sbuid","D0097");
									jsonFatcher.put("sys_stated",sys_stated);
									jsonFatcher.put("sorg",MyApplication.sorg);
									JSONObject jsonSon = new JSONObject();
									jsonSon.put("mbox",yimei_slkmbox_mbox.getText().toString().toUpperCase());
									jsonSon.put("slkid",yimei_slkmbox_slkid.getText().toString().toUpperCase());
									jsonSon.put("mkdate",MyApplication.GetServerNowTime());
									jsonSon.put("qty","1");
									jsonSon.put("cid",cid + 1);
									if(sys_stated.equals("2")){
										jsonSon.put("sid", sid);
										jsonFatcher.put("sid",sid);
									}
									jsonSon.put("sys_stated","3");
									jsonSon.put("op",yimei_slkmbox_op.getText().toString().toUpperCase());
									jsonFatcher.put("D0097A", new Object[] { jsonSon });
									Map<String, String> httpMapKeyValueMethod = MyApplication
											.httpMapKeyValueMethod(MyApplication.DBID, "savedata",
													MyApplication.user,jsonFatcher.toJSONString(),
													"D0097(D0097A)", "1");
									OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null, httpMapKeyValueMethod, null, mHander,
											true, "savedata");
									yimei_slkmbox_mbox.selectAll();
									InputHidden();
								}
							}
						}
						if(string.equals("savedata")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(jsonObject.getInteger("id") == 0){
								yimei_slkmbox_mbox.setText("");
								MyApplication.nextEditFocus(yimei_slkmbox_mbox);
							}else{
								JSONObject jsonFatcher = new JSONObject();
								jsonFatcher.put("slkid",yimei_slkmbox_slkid.getText().toString().toUpperCase());
								jsonFatcher.put("op",yimei_slkmbox_op.getText().toString().toUpperCase());
								jsonFatcher.put("mkdate",MyApplication.GetServerNowTime());
								jsonFatcher.put("sbuid","D0097");
								jsonFatcher.put("sys_stated",sys_stated);
								jsonFatcher.put("sorg",MyApplication.sorg);
								JSONObject jsonSon = new JSONObject();
								jsonSon.put("mbox",yimei_slkmbox_mbox.getText().toString().toUpperCase());
								jsonSon.put("slkid",yimei_slkmbox_slkid.getText().toString().toUpperCase());
								jsonSon.put("mkdate",MyApplication.GetServerNowTime());
								jsonSon.put("qty","1");
								jsonSon.put("cid",cid + 2);
								if(sys_stated.equals("2")){
									jsonSon.put("sid", sid);
									jsonFatcher.put("sid",sid);
								}
								jsonSon.put("sys_stated","3");
								jsonSon.put("op",yimei_slkmbox_op.getText().toString().toUpperCase());
								jsonFatcher.put("D0097A", new Object[] { jsonSon });
								Map<String, String> httpMapKeyValueMethod = MyApplication
										.httpMapKeyValueMethod(MyApplication.DBID, "savedata",
												MyApplication.user,jsonFatcher.toJSONString(),
												"D0097(D0097A)", "1");
								OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null, httpMapKeyValueMethod, null, mHander,
										true, "savedata");
								yimei_slkmbox_mbox.selectAll();
								InputHidden();
							}
							System.out.println(jsonObject);
						}
					} catch (Exception e) {
						ToastUtil.showToastLocation(getApplicationContext(),"逻辑:【" + e.toString() + "】", 0);
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
				SlkMboxActivity.this);
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
	 * 键盘隐藏
	 */
	private void InputHidden(){
		//调用键盘类
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 如果软键盘已经显示，则隐藏，反之则显示
		imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	OnFocusChangeListener FoucusChange = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(v.getId()== R.id.yimei_slkmbox_op){
				if (!hasFocus) { // 失去焦点
					Map<String, String> map = MyApplication.QueryBatNo("MESOPWEB", "~sal_no='" + yimei_slkmbox_op.getText().toString() + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "QueryMESOP");
				} 
			}
			if(v.getId() == R.id.yimei_slkmbox_slkid){
				/*if (!hasFocus) { // 失去焦点
					if(yimei_slkmbox_op.getText().toString().trim().equals("")){
						ToastUtil.showToast(getApplicationContext(), "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_op);
						return;
					}
					if(yimei_slkmbox_slkid.getText().toString().trim().equals("")){
						ToastUtil.showToast(getApplicationContext(), "工单号不能为空", 0);
						MyApplication.nextEditFocus(yimei_slkmbox_slkid);
						return;
					}
					Map<String, String> map = MyApplication.QueryBatNo("MBOX_SLKID", "~sid='" + yimei_slkmbox_slkid.getText().toString() + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "QuerySLKID");
				}*/
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
