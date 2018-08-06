package com.yimei.shebei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.MyApplication;
import com.yimei.activity.R;
import com.yimei.adapter.WeiXiuOkAdapter;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author 16332 
 * WEIXIUOK //待维修列表
 * WEIXIUOK_OP //查询维修人员的账号密码
 */
public class WeiXiuOkActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private Button weixiu_start, weixiu_stop, weixui_shengchanOK;
	private WeiXiuOkAdapter weixiuokAdapter;
	public static WeiXiuOkActivity ip;
	private JSONObject ChooseJson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weixiuok);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		ip = WeiXiuOkActivity.this;
		
		
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.weixiuok_scroll_title);
		// 添加头滑动事件
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.weixiuok_scroll_list);
		String cont;
		if (MyApplication.user.equals("admin")) {
			cont = "";
		} else {
			cont = "~sorg='" + MyApplication.sorg + "'";
		}
		//维修列表
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				MyApplication.QueryBatNo("WEIXIUOK", cont), null, mHander,
				true, "WEIXIUOK"); 
	}

	@Override
	protected void onResume() {
		super.onResume();

		weixiu_start = (Button) findViewById(R.id.weixiu_start);
		weixiu_stop = (Button) findViewById(R.id.weixiu_stop);
		weixui_shengchanOK = (Button) findViewById(R.id.weixui_shengchanOK);
		
		weixiu_start.setOnClickListener(Onclick);
		weixiu_stop.setOnClickListener(Onclick);
		weixui_shengchanOK.setOnClickListener(Onclick);
	}

	
	/**
	 * 点击事件
	 */
	OnClickListener Onclick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.weixiu_start){
				ShowDig("start");
			}
			
			if(v.getId() == R.id.weixiu_stop){
				ShowDig("stop");
			}
			
			if(v.getId() == R.id.weixui_shengchanOK){
				ShowDig("shengchan");
			}
		}
	};
	
	/**
	 * 弹出对话框
	 * @param publicState
	 */
	public void ShowDig(String publicState) {
		HashMap<Integer, Boolean> state = weixiuokAdapter.Getstate();
		if (state == null || state.equals(null)) {
			ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
		} else {
			int count = 0;
			JSONObject jsonObj = null;
			for (int j = 0; j < weixiuokAdapter.getCount(); j++) {
				if (state.get(j)) {
					if (state.get(j) != null) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> map = (HashMap<String, Object>) weixiuokAdapter.getItem(j);
						jsonObj = JSONObject.parseObject(map.get("checkMap").toString());
						count++;
					}
				} 
			}
			if(count==0){
				ToastUtil.showToast(getApplicationContext(),"请选中一条数据!",0);
				return;
			}
			if(count>1){
				ToastUtil.showToast(getApplicationContext(),"只能选中一条!",0);
			}else{
//				ToastUtil.showToast(getApplicationContext(),jsonObj.toString(),0);
				if(publicState.equals("start")){ //开始维修
					if(jsonObj.getInteger("wxstate") == 1){
						ToastUtil.showToast(getApplicationContext(),"状态为【开始维修】!",0);
						return;
					}else if(jsonObj.getInteger("wxstate") == 2){
						ToastUtil.showToast(getApplicationContext(),"状态为【待确认】!",0);
						return;
					}else if(jsonObj.getInteger("wxstate") == 3){
						ToastUtil.showToast(getApplicationContext(),"状态为【已确认】!",0);
						return;
					}
					UpdateWeiXiu_dig("开始维修",jsonObj,publicState);  
				}else if(publicState.equals("stop")){  //结束维修
					if(jsonObj.getInteger("wxstate") == 0){
						ToastUtil.showToast(getApplicationContext(),"状态为【待维修】!",0);
						return;
					}else if(jsonObj.getInteger("wxstate") == 2){
						ToastUtil.showToast(getApplicationContext(),"状态为【待确认】!",0);
						return;
					}else if(jsonObj.getInteger("wxstate") == 3){
						ToastUtil.showToast(getApplicationContext(),"状态为【已确认】!",0);
						return;
					}
					UpdateWeiXiu_dig("结束维修",jsonObj,publicState);  
				}else if(publicState.equals("shengchan")){ //生产确认
					if(jsonObj.getInteger("wxstate") != 2){
						ToastUtil.showToast(getApplicationContext(),"没有维修或结束!",0);
						return;
					}
					UpdateWeiXiu_dig("生产确认",jsonObj,publicState);  
				}
			}
		}
	}
	
	/**
	 * 超时登录
	 * 
	 * @param Title
	 * @param msg
	 */
	private void UpdateWeiXiu_dig(String Title, final JSONObject msg,final String type) {
		LayoutInflater inflater = getLayoutInflater();
		View dialog = inflater.inflate(R.layout.activity_mesurl_dig,
				(ViewGroup) findViewById(R.id.dialogurl));
		final EditText usertext = (EditText) dialog.findViewById(R.id.mesurl);
		final EditText userpwd = (EditText) dialog.findViewById(R.id.mesdbid);
		final TextView logintime_usertext = (TextView) dialog
				.findViewById(R.id.logintime_user);
		final TextView logintime_pwdtext = (TextView) dialog
				.findViewById(R.id.logintime_pwd);
		if(type.equals("start")){ //开始维修
			logintime_usertext.setText("维修人");
			logintime_pwdtext.setText("密码");
			userpwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}else if(type.equals("stop")){  //结束维修
			usertext.setVisibility(View.GONE);
			logintime_usertext.setVisibility(View.GONE);
			logintime_pwdtext.setText("密码");
			userpwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}else if(type.equals("shengchan")){ //生产确认
			userpwd.setVisibility(View.GONE);
			logintime_usertext.setText("生产确认");
			logintime_pwdtext.setVisibility(View.GONE);
		}
		

		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				WeiXiuOkActivity.this);
		normalDialog.setTitle(Title);
		normalDialog.setView(dialog);
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ChooseJson = msg ;
						if(type.equals("start")){ //开始维修 
							OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
									MyApplication.QueryBatNo(
											"WEIXIUOK_OP","~usrcode='"+usertext.getText()+"'"
													+ " and pwd='"+userpwd.getText().toString().trim()+"'"), null, mHander,
									true, "WEIXIUOK_start_OP");  //查询维修人员账号密码
						}else if(type.equals("stop")){  //结束维修
							OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
									MyApplication.QueryBatNo(
											"WEIXIUOK_OP","~usrcode='"+msg.getString("schk1")+"'"
													+ " and pwd='"+userpwd.getText().toString().trim()+"'"), null, mHander,
									true, "WEIXIUOK_stop_OP");  //查询维修人员账号密码
						}else if(type.equals("shengchan")){ //生产确认
							OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
									MyApplication.QueryBatNo(
											"MESOPWEB","~sal_no='"+usertext.getText().toString().toUpperCase().trim()+"'"), null, mHander,
									true, "MESOPWEB");  //查询生产人员
						}
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
						JSONObject LoginTimeMess = JSON.parseObject(b
								.getString("jsonObj").toString());
						if (LoginTimeMess.containsKey("message")) {
							if (LoginTimeMess.get("message").equals("请重新登录")) { // 超时登录
								LoginTimeout_dig("超时登录", "请重新登录!");
								return;
							}
						}
						if (string.equals("TimeOutLogin")) { // 超时登录
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (jsonObject.getInteger("id") != 0) {
								LoginTimeout_dig("密码错误", "");
							}
						}
						if(string.equals("WEIXIUOK")){ //待维修列表
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(jsonObject.getInteger("code") == 1){
								JSONArray JSONArray = (JSONArray) jsonObject.get("values");
								ArrayList<Map<String,String>> mList = new ArrayList<>();
								for (int i = 0; i < JSONArray.size(); i++) {
									JSONObject json = (JSONObject) JSONArray.get(i);
									Map<String,String> map = new HashMap<String, String>();
									int wxstate = json.getInteger("wxstate");
									//0:待维修;1:开始维修;2:待确认;3:已确认
									String wxstateName = null;
									if(wxstate == 0){
										wxstateName = "待维修";
									}else if(wxstate == 1){
										wxstateName = "开始维修";
									}else if(wxstate == 2){
										wxstateName = "待确认";
									}else if(wxstate == 3){
										wxstateName = "已确认";
									}
									json.put("state",wxstateName);
									map.put("checkMap",json.toJSONString());
									map.put("sbid", json.getString("sbid"));
									map.put("sopr", json.getString("sopr"));
									map.put("state",wxstateName);
									mList.add(map);
								}
								weixiuokAdapter = new WeiXiuOkAdapter(WeiXiuOkActivity.this, mList);
								mListView.setAdapter(weixiuokAdapter);
							}
							System.out.println(jsonObject);
						}
						if(string.equals("WEIXIUOK_start_OP")){ //维修人员的账号密码
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(jsonObject.getInteger("code") == 1){
								JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject.get("values")).get(0));
								//700  params[单号,设备号,人员,维修状态,备注] 
								
								Map<String, String> Update_700 = new HashMap<>(); // 修改记录表
								Update_700.put("dbid", MyApplication.DBID);
								Update_700.put("usercode",MyApplication.user);
								Update_700.put("apiId", "mesudp");
								Update_700.put("id", "700");
								Update_700.put("sid", ChooseJson.getString("sid"));
								Update_700.put("sbid",  ChooseJson.getString("sbid"));
								Update_700.put("op", jsonValue.getString("usrcode"));
								Update_700.put("state", "1"); // 开始维修
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, Update_700 , null, mHander,
										true, "WEIXIUOK_Server");
							}else{
								ToastUtil.showToastLocation(getApplicationContext(),"用户名或者密码错误", 0);
							}
							System.out.println(jsonObject);
						}
						if(string.equals("WEIXIUOK_stop_OP")){ //维修人员的账号密码
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(jsonObject.getInteger("code") == 1){
								JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject.get("values")).get(0));
								//700  params[单号,设备号,人员,维修状态,备注] 
								
								Map<String, String> Update_700 = new HashMap<>(); // 修改记录表
								Update_700.put("dbid", MyApplication.DBID);
								Update_700.put("usercode",MyApplication.user);
								Update_700.put("apiId", "mesudp");
								Update_700.put("id", "700");
								Update_700.put("sid", ChooseJson.getString("sid"));
								Update_700.put("sbid",  ChooseJson.getString("sbid"));
								Update_700.put("op", jsonValue.getString("usrcode"));
								Update_700.put("state", "2"); // 结束维修
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, Update_700 , null, mHander,
										true, "WEIXIUOK_Server");
							}else{
								ToastUtil.showToastLocation(getApplicationContext(),"密码错误", 0);
							}
							System.out.println(jsonObject);
						}
						if(string.equals("MESOPWEB")){ //查询产线人员
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(jsonObject.getInteger("code") == 1){
								JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject.get("values")).get(0));
								//700  params[单号,设备号,人员,维修状态,备注] 
								Map<String, String> Update_700 = new HashMap<>(); // 修改记录表
								Update_700.put("dbid", MyApplication.DBID);
								Update_700.put("usercode",MyApplication.user);
								Update_700.put("apiId", "mesudp");
								Update_700.put("id", "700");
								Update_700.put("sid", ChooseJson.getString("sid"));
								Update_700.put("sbid",  ChooseJson.getString("sbid"));
								Update_700.put("op", jsonValue.getString("sal_no"));
								Update_700.put("state", "3"); // 生产确认
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, Update_700 , null, mHander,
										true, "WEIXIUOK_Server");
							}else{
								ToastUtil.showToastLocation(getApplicationContext(),"没有该人员!", 0);
							}
						}
						if(string.equals("WEIXIUOK_Server")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(jsonObject.getInteger("id") == 1){
								String cont;
								if (MyApplication.user.equals("admin")) {
									cont = "";
								} else {
									cont = "~sorg='" + MyApplication.sorg + "'";
								}
								//维修列表
								OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
										MyApplication.QueryBatNo("WEIXIUOK", cont), null, mHander,
										true, "WEIXIUOK");
							}
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
		// usertext.setKeyListener(null);

		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				WeiXiuOkActivity.this);
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
						OkHttpUtils.getInstance().getServerExecute(
								MyApplication.MESURL, null, map, null, mHander,
								true, "TimeOutLogin");
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
