package com.yimei.shebei;

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
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.JieBangMboxActivity;
import com.yimei.activity.MyApplication;
import com.yimei.activity.R;
import com.yimei.activity.ipqc.IPQC_shoujian;
import com.yimei.entity.Pair;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

public class shebeiweixiuActivity extends Activity{
 
	private EditText yimei_weixiu_user,yimei_weixiu_sbid;
	private Spinner weixiu_yuanyin_selectValue,weixiu_zcno_selectValue;
	private Button yimei_weixiu_save;
	private String zcno;
	private String yuanyinId;
	private Map<String, String> zcnoMap = new HashMap<String, String>();
	private boolean IsSbid = false; //true时候有该设备号
	private JSONObject baoxiuJSON;
	private Map<Integer,String> AuditorMap = new HashMap<>();
	private int chooseIndex = 0;
	
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
				if (tag.equals("维修作业员")) {
					yimei_weixiu_user.setText(barcodeData);
					if(yimei_weixiu_user.getText().toString().toUpperCase().trim().equals("")
							||yimei_weixiu_user.getText().toString().toUpperCase().trim()==null){
						ToastUtil.showToast(shebeiweixiuActivity.this,"作业员不能为空", 0);
						return;
					}
					MyApplication.nextEditFocus(yimei_weixiu_sbid);
				}
				if (tag.equals("维修设备")) {
					yimei_weixiu_sbid.setText(barcodeData);
					if(yimei_weixiu_user.getText().toString().toUpperCase().trim().equals("")
							||yimei_weixiu_user.getText().toString().toUpperCase().trim()==null){
						ToastUtil.showToast(shebeiweixiuActivity.this,"作业员不能为空", 0);
						return ;
					}
					if(yimei_weixiu_sbid.getText().toString().toUpperCase().trim().equals("")
							||yimei_weixiu_sbid.getText().toString().toUpperCase().trim()==null){
						ToastUtil.showToast(shebeiweixiuActivity.this,"设备号不能为空", 0);
						return ;
					}
					if(zcno!=null){
						AccessServer("MESEQUTM","~id='" + 
								yimei_weixiu_sbid.getText().toString().toUpperCase().trim() + "' and zc_id='" + zcno + "'","QuerySbid");
					}else{
						ToastUtil.showToast(shebeiweixiuActivity.this,"请选择制程", 0);
						return ;
					}
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_shebeiweixiu);
		String cont; // 参数
		if (MyApplication.user.equals("admin")) {
			cont = "";
		} else {
			cont = "~sorg='" + MyApplication.sorg + "'";
		}
		// 获取制程
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				MyApplication.QueryBatNo("M_PROCESS", cont), null, mHander,
				true, "SpinnerValue");
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		yimei_weixiu_user = (EditText) findViewById(R.id.yimei_weixiu_user);
		yimei_weixiu_sbid = (EditText) findViewById(R.id.yimei_weixiu_sbid);
		weixiu_zcno_selectValue = (Spinner)findViewById(R.id.weixiu_zcno_selectValue);
		weixiu_yuanyin_selectValue = (Spinner)findViewById(R.id.weixiu_yuanyin_selectValue);
		yimei_weixiu_save = (Button) findViewById(R.id.yimei_weixiu_save);
		yimei_weixiu_save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AccessServer("MESEQUTM","~id='" + 
						yimei_weixiu_sbid.getText().toString().toUpperCase().trim() + "' and zc_id='" + zcno + "'","QuerySbid");
				if(yimei_weixiu_user.getText().toString().toUpperCase().trim().equals("")
						||yimei_weixiu_user.getText().toString().toUpperCase().trim()==null){
					ToastUtil.showToast(shebeiweixiuActivity.this,"作业员不能为空", 0);
					MyApplication.nextEditFocus(yimei_weixiu_user);
					return ;
				}
				if(yimei_weixiu_sbid.getText().toString().toUpperCase().trim().equals("")
						||yimei_weixiu_sbid.getText().toString().toUpperCase().trim()==null){
					ToastUtil.showToast(shebeiweixiuActivity.this,"设备号不能为空", 0);
					MyApplication.nextEditFocus(yimei_weixiu_sbid);
					return ;
				}
				if(!IsSbid){
					ToastUtil.showToast(getApplicationContext(),"设备与制程不匹或该设备处于维修状态!", 0);
					MyApplication.nextEditFocus(yimei_weixiu_sbid);
					yimei_weixiu_sbid.selectAll();
					return;
				}
				JSONObject json = new JSONObject();
				json.put("sbuid","D0001");
				json.put("sorg",MyApplication.sorg);
				json.put("sopr",yimei_weixiu_user.getText().toString().toUpperCase().trim());
				json.put("hpdate",MyApplication.GetServerNowTime());
				json.put("sbid",yimei_weixiu_sbid.getText().toString().toUpperCase().trim());
				json.put("zcno",zcno);
				json.put("mkdate",MyApplication.GetServerNowTime());
				json.put("state","0");
				json.put("wxstate","0");
				json.put("qtype","0");
				json.put("smake",MyApplication.user);
				json.put("reason",yuanyinId);
				Map<String, String> httpMapKeyValueMethod = MyApplication
						.httpMapKeyValueMethod(MyApplication.DBID,
								"savedata", MyApplication.user,
								json.toString(), "E6001",
								"1");
				OkHttpUtils.getInstance().getServerExecute(
						MyApplication.MESURL, null, httpMapKeyValueMethod,
						null, mHander, true, "Save_E6001");
			}
		});
		
		yimei_weixiu_user.setOnEditorActionListener(EditEnter);
		yimei_weixiu_sbid.setOnEditorActionListener(EditEnter);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(barcodeReceiver); // 取消广播注册
	}
	
	OnEditorActionListener EditEnter = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(v.getId() == R.id.yimei_weixiu_user){
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if(yimei_weixiu_user.getText().toString().toUpperCase().trim().equals("")
							||yimei_weixiu_user.getText().toString().toUpperCase().trim()==null){
						ToastUtil.showToast(shebeiweixiuActivity.this,"作业员不能为空", 0);
						return false;
					}
					MyApplication.nextEditFocus(yimei_weixiu_sbid);
				}
			}
			if(v.getId() == R.id.yimei_weixiu_sbid){
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if(yimei_weixiu_user.getText().toString().toUpperCase().trim().equals("")
							||yimei_weixiu_user.getText().toString().toUpperCase().trim()==null){
						ToastUtil.showToast(shebeiweixiuActivity.this,"作业员不能为空", 0);
						return false;
					}
					if(yimei_weixiu_sbid.getText().toString().toUpperCase().trim().equals("")
							||yimei_weixiu_sbid.getText().toString().toUpperCase().trim()==null){
						ToastUtil.showToast(shebeiweixiuActivity.this,"设备号不能为空", 0);
						return false;
					}
					if(zcno!=null){
						AccessServer("MESEQUTM","~id='" + 
								yimei_weixiu_sbid.getText().toString().toUpperCase().trim() + "' and zc_id='" + zcno + "'","QuerySbid");
					}else{
						ToastUtil.showToast(shebeiweixiuActivity.this,"请选择制程", 0);
						return false;
					}
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
		Map<String, String> queryBatNo = MyApplication.QueryBatNo(assistid,cont);
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
						if(string.equals("SpinnerValue")){ //制程
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(),
										"没有查到制程号~", 0);
								return;
							} else {
								Set_zcno_SelectValue(jsonObject);
							}
						}
						if(string.equals("SBIDWEIXIUQuery")){ //维修原因
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(),
										"该制程没有维修原因", 0);
								return;
							} else {
								Set_yuanyin_SelectValue(jsonObject);
							}
						}
						if(string.equals("QuerySbid")){ //设备查询
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
								MyApplication.nextEditFocus(yimei_weixiu_sbid);
								yimei_weixiu_sbid.selectAll();
								ToastUtil.showToast(getApplicationContext(),"设备与制程不匹或该设备处于维修状态!", 0);
								IsSbid = false;
							}else{
								IsSbid = true;
							}
						}
						if(string.equals("Save_E6001")){ //savedata
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id")
									.toString()) == -1) {
								ToastUtil.showToast(shebeiweixiuActivity.this,
										"（SaveData错误）保修失败", 0);
							} else {
								baoxiuJSON = new JSONObject();
								JSONObject a = (JSONObject) jsonObject.get("data");
								String bb = a.get("sid").toString();
								baoxiuJSON.put("sid", bb);
								//33请求=====================================
								Map<String, String> map = new HashMap<>();
								map.put("dbid", MyApplication.DBID);
								map.put("usercode", MyApplication.user);
								map.put("apiId", "chkup");
								map.put("chkid", "33");
								JSONObject ceaJson = new JSONObject();
								ceaJson.put("sid", baoxiuJSON.get("sid"));
								ceaJson.put("sbuid", MyApplication.SHEBEISBUID); 
								ceaJson.put("statefr", "0");
								ceaJson.put("stateto", "0");
								map.put("cea", ceaJson.toString());
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, map, null,
										mHander, true, "Approval_33");
								//33请求=====================================
								
							}
						}
						if(string.equals("Approval_33")){
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id")
									.toString()) == 0) {
								JSONObject data = (JSONObject) jsonObject
									 	.get("data");
								final JSONObject info = (JSONObject) data.get("info");
								JSONArray list = (JSONArray)info.get("list");
								final JSONObject listJson = (JSONObject) list.get(0);
								//判断是否有用户
								if(listJson.containsKey("users")){
									JSONArray jsonarr =  (JSONArray) listJson.get("users");
									for (int i = 0; i < jsonarr.size(); i++) {
										JSONObject jsonOp = (JSONObject) jsonarr.get(i);
										jsonOp.get("userCode");
										jsonOp.get("userName");
										AuditorMap.put(i,jsonOp.get("userCode")+"-"+jsonOp.get("userName"));
									}
									
									AlertDialog.Builder builder = new AlertDialog.Builder(shebeiweixiuActivity.this);  
					                builder.setTitle("请选择审核人");  
					                builder.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
					                final String[] Auditor = new String[AuditorMap.size()];
					                for (int j = 0; j < AuditorMap.size(); j++) {
					                	Auditor[j] = AuditorMap.get(j); 
									}
					                builder.setSingleChoiceItems(Auditor,chooseIndex, new DialogInterface.OnClickListener()  
					                {  
					                    @Override  
					                    public void onClick(DialogInterface dialog, int which)  
					                    {  
					                    	chooseIndex = which;
					                    }  
					                });  
					                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()  
					                {  
					                    @Override  
					                    public void onClick(DialogInterface dialog, int which)  
					                    {  
					                    	JSONObject ceaJson = new JSONObject();
											ceaJson.put("stateto", listJson.get("stateId"));
											ceaJson.put("sid", baoxiuJSON.get("sid"));
											ceaJson.put("sbuid", MyApplication.SHEBEISBUID); 
											ceaJson.put("ckd", info.get("checked"));
											ceaJson.put("yjcontext", "");
											ceaJson.put("bup", "1");
											ceaJson.put("statefr", "0");
											ceaJson.put("tousr",AuditorMap.get(chooseIndex).substring(0,AuditorMap.get(chooseIndex).indexOf("-")));
											Map<String, String> map = new HashMap<>();
											map.put("dbid", MyApplication.DBID);
											map.put("usercode", MyApplication.user);
											map.put("apiId", "chkup");
											map.put("chkid", "34");
											map.put("cea", ceaJson.toString());
											OkHttpUtils.getInstance().getServerExecute(
													MyApplication.MESURL, null, map, null,
													mHander, true, "Approval_34");
					                    }  
					                });
					                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()  
					                {  
					                    @Override  
					                    public void onClick(DialogInterface dialog, int which)  
					                    {  
					                          
					                    }  
					                });  
					                builder.show();  
								}else{
									ToastUtil.showToast(shebeiweixiuActivity.this,"没有审核人",0);
								}
							}
						}
						if(string.equals("Approval_34")){
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id").toString()) == 0) {
								ToastUtil.showToast(shebeiweixiuActivity.this,"审批提交成功", 0);
								MyApplication.nextEditFocus(yimei_weixiu_user);
								yimei_weixiu_user.setText("");
								yimei_weixiu_sbid.setText("");
							} else {
								ToastUtil.showToast(shebeiweixiuActivity.this,
										"审批提交失败", 0);
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
	 * 给维修原因下拉框赋值
	 * 
	 * @param hScrollView
	 */
	private void Set_yuanyin_SelectValue(JSONObject jsonObject) {
		List<Pair> dicts = new ArrayList<Pair>();
		for (int i = 0; i < ((JSONArray) jsonObject.get("values")).size(); i++) {
			JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
					.get("values")).get(i));
			dicts.add(new Pair(jsonValue.getString("name").toString(),
					jsonValue.getString("fid").toString()));
		}
		ArrayAdapter<Pair> adapter = new ArrayAdapter<Pair>(shebeiweixiuActivity.this,
				android.R.layout.simple_spinner_item, dicts);
		weixiu_yuanyin_selectValue.setAdapter(adapter);
		weixiu_yuanyin_selectValue.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				yuanyinId = ((Pair) weixiu_yuanyin_selectValue.getSelectedItem()).getValue();
//			    ToastUtil.showToast(shebeiweixiuActivity.this,yuanyinId,0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	/**
	 * 给制程下拉框赋值
	 * 
	 * @param hScrollView
	 */
	private void Set_zcno_SelectValue(JSONObject jsonObject) {
		List<Pair> dicts = new ArrayList<Pair>();
		for (int i = 0; i < ((JSONArray) jsonObject.get("values")).size(); i++) {
			JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
					.get("values")).get(i));
			dicts.add(new Pair(jsonValue.getString("name").toString(),
					jsonValue.getString("id").toString()));
			zcnoMap.put(jsonValue.getString("id").toString(), jsonValue
					.getString("name").toString());
		}
		ArrayAdapter<Pair> adapter = new ArrayAdapter<Pair>(shebeiweixiuActivity.this,
				android.R.layout.simple_spinner_item, dicts);
		weixiu_zcno_selectValue.setAdapter(adapter);
		weixiu_zcno_selectValue.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				zcno = ((Pair) weixiu_zcno_selectValue.getSelectedItem()).getValue();
				// 查询维修原因
				AccessServer("CAUSEDQ", "~id='" + zcno + "'", "SBIDWEIXIUQuery");
//			    ToastUtil.showToast(shebeiweixiuActivity.this,zcno,0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	/**
	 * 隐藏键盘
	 */
	private void InputHidden() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 如果软键盘已经显示，则隐藏，反之则显示
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
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
				shebeiweixiuActivity.this);
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
}
