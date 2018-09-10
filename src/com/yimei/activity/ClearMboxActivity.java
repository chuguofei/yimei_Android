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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
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
import com.yimei.adapter.ClearMboxAdapter;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

/**
 * 清洗料盒
 * 
 * @author 16332
 * 
 */
public class ClearMboxActivity extends Activity {

	private EditText op, mbox, rem;
	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private ClearMboxAdapter clearmboxApapter;
	private Button clearmbox_submit,clearmbox_save,clearmbox_new;
	private String submit_sid;
	private Map<Integer,String> AuditorMap = new HashMap<>();
	private int chooseIndex = 0; 
	
	private final String sbuid = "D009A";
		
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
				if (tag.equals("清洗作业员")) {
					op.setText(barcodeData);
					if (op.getText().toString().trim().equals("")
							|| op.getText().toString().trim() == null) {
						ToastUtil.showToast(ClearMboxActivity.this, "作业员不能为空", 0);
						MyApplication.nextEditFocus(op);
						return;
					}
					MyApplication.nextEditFocus(rem);
				}
				if (tag.equals("清洗料盒号")) {
					mbox.setText(barcodeData);
					if (op.getText().toString().trim().equals("")
							|| op.getText().toString().trim() == null) {
						ToastUtil.showToast(ClearMboxActivity.this, "作业员不能为空", 0);
						MyApplication.nextEditFocus(op);
						return;
					}
					if (mbox.getText().toString().trim().equals("")
							|| mbox.getText().toString().trim() == null) {
						ToastUtil.showToast(ClearMboxActivity.this, "料盒号不能为空", 0);
						MyApplication.nextEditFocus(mbox);
						return;
					}
					//查询料盒号
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, MyApplication.QueryBatNo("MBOXQUERY","~id='"+mbox.getText().toString().trim()+"'"), null, mHandler, true, "QueryMbox");
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clearmbox);
		
		
		mListView = (ListView) findViewById(R.id.clearmbox_scroll_list);
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.clearmbox_scroll_title);
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
		op = (EditText) findViewById(R.id.yimei_clearmbox_op);
		mbox = (EditText) findViewById(R.id.yimei_clearmbox_mbox);
		rem = (EditText) findViewById(R.id.yimei_clearmbox_rem);
		
		clearmbox_save = (Button) findViewById(R.id.clearmbox_save);
		clearmbox_submit = (Button) findViewById(R.id.clearmbox_submit);
		clearmbox_new = (Button) findViewById(R.id.clearmbox_new);
		clearmbox_new.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(clearmboxApapter == null){
					ToastUtil.showToast(ClearMboxActivity.this,"列表中没有数据，请扫描料盒!", 0);
				}else{
					clearmboxApapter = null;
					mListView.setAdapter(clearmboxApapter);
					submit_sid = null;
					clearmbox_save.setEnabled(true);
				}
			}
		});
		clearmbox_submit.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					if(submit_sid.equals("") || submit_sid == null){
						ToastUtil.showToast(ClearMboxActivity.this,"请扫描料盒号并保存！", 0);
					}else{
						Map<String, String> map = new HashMap<>();
						map.put("dbid", MyApplication.DBID);
						map.put("usercode", MyApplication.user);
						map.put("apiId", "chkup");
						map.put("chkid", "33");
						JSONObject ceaJson = new JSONObject();
						ceaJson.put("sid",submit_sid);
						ceaJson.put("sbuid", sbuid);
						ceaJson.put("statefr", "0");
						ceaJson.put("stateto", "0");
						map.put("cea", ceaJson.toString());
						OkHttpUtils.getInstance().getServerExecute(
								MyApplication.MESURL, null, map, null,
								mHandler, true, "Approval_33");
						
						Intent intent = new Intent();
						intent.setClass(ClearMboxActivity.this, Loading1Activity.class);// 跳转到加载界面
						startActivity(intent);
					}
				} catch (Exception e) {
					ToastUtil.showToast(ClearMboxActivity.this,"请扫描料盒号并保存！", 0);
				}
			}
		});
		
		clearmbox_save.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					if(clearmboxApapter == null){
						ToastUtil.showToast(ClearMboxActivity.this,"列表中没有数据，请扫描料盒!", 0);
						return;
					}else{
						if(clearmboxApapter.getCount() == 0){
							ToastUtil.showToast(ClearMboxActivity.this,"列表中没有数据，请扫描料盒!", 0);
							return;
						}
					    List<Map<String, String>> list = clearmboxApapter.listData;
					    //主对象
					    JSONObject jsonFatcher = new JSONObject();
					    jsonFatcher.put("sbuid", sbuid);
					    jsonFatcher.put("sys_stated", "3"); // 新增
					    jsonFatcher.put("op", op.getText().toString().toUpperCase());
					    jsonFatcher.put("mkdate", MyApplication.GetServerNowTime());
					    jsonFatcher.put("sorg", MyApplication.sorg);
					    jsonFatcher.put("state", "0");
					    jsonFatcher.put("dcid", GetAndroidMacUtil.getMac());
					    jsonFatcher.put("smake",MyApplication.user);
					    //子对象
						JSONArray JsonSonArr = new JSONArray();
					    
					    for (int i = 0; i < list.size(); i++) {
							Map<String, String> map = list.get(i);
							JSONObject JsonSon = new JSONObject();
							JsonSon.put("sys_stated", "3"); // 新增
							JsonSon.put("cid", i+1); // 项次 
							JsonSon.put("mbox",map.get("mbox")==null?"":map.get("mbox")); 
							JsonSon.put("rem", map.get("rem")==null?"":map.get("rem")); 
							JsonSonArr.add(JsonSon);
						}
					    jsonFatcher.put("D009BWEB", JsonSonArr);
					    Map<String, String> mesIdMap = MyApplication.httpMapKeyValueMethod(
								MyApplication.DBID, "savedata", MyApplication.user,
								jsonFatcher.toString(), "D009AWEB(D009BWEB)", "1"); // 批次号添加（服务器提交）
						OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,
								null, mesIdMap, null, mHandler, true, "save_D009A"); // 批次扫描添加
					}
				} catch (Exception e) {
					ToastUtil.showToast(ClearMboxActivity.this,"列表中没有数据，请扫描料盒!", 0);
				}
			}
		});
		mbox.setOnEditorActionListener(mboxEnter);
		op.setOnFocusChangeListener(EditGetFocus);
	}
	
	/**
	 * 隐藏键盘
	 */
	private void InputHidden() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 如果软键盘已经显示，则隐藏，反之则显示
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_clearmbox_op) {
				if (!hasFocus) {
					
				} else {
					op.setSelectAllOnFocus(true);
					InputHidden(); // 隐藏键盘
				}
			}
		}
	};
	/**
	 * 料盒号回车
	 */
	OnEditorActionListener mboxEnter = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(v.getId() == R.id.yimei_clearmbox_mbox){
				if(actionId >= 0){
					if (op.getText().toString().trim().equals("")
							|| op.getText().toString().trim() == null) {
						ToastUtil.showToast(ClearMboxActivity.this, "作业员不能为空", 0);
						MyApplication.nextEditFocus(op);
						return false;
					}
					if (mbox.getText().toString().trim().equals("")
							|| mbox.getText().toString().trim() == null) {
						ToastUtil.showToast(ClearMboxActivity.this, "料盒号不能为空", 0);
						MyApplication.nextEditFocus(mbox);
						return false;
					}
					//查询料盒号
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, MyApplication.QueryBatNo("MBOXQUERY","~id='"+mbox.getText().toString().trim()+"'"), null, mHandler, true, "QueryMbox");
				}
			}
			return false;
		}
	};
	
	/**
	 * 逻辑判断
	 */
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@SuppressLint("DefaultLocale") @Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				if (msg.arg1 == 0) {
					Bundle b = msg.getData();
					String string = b.getString("type");
					try {
						if (string.equals("QueryMbox")) { // 查询料盒号是否存在
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { // 没有料盒号
								ToastUtil.showToast(ClearMboxActivity.this,"系统中不存在"+mbox.getText().toString().toUpperCase(), 0);
								mbox.setText("");
								MyApplication.nextEditFocus(mbox);
								InputHidden();
								return;
							} else {
								List<Map<String,String>> mList = new ArrayList<>();
								Map<String, String> map = new HashMap<>();
								map.put("op",op.getText().toString().toUpperCase());
								map.put("mbox",mbox.getText().toString().toUpperCase());
								map.put("rem",rem.getText().toString().toUpperCase());
								mList.add(map);
								if(clearmboxApapter == null){									
									clearmboxApapter = new ClearMboxAdapter(ClearMboxActivity.this, mList);
									mListView.setAdapter(clearmboxApapter);
								}else{
									clearmboxApapter.listData.add(map);
									clearmboxApapter.notifyDataSetChanged();
								}
								mbox.setText("");
								MyApplication.nextEditFocus(mbox);
								InputHidden();
							}
						}
						if(string.equals("save_D009A")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(jsonObject.getInteger("id") == 0){ //操作成功
								//{"id":0,"message":"操作成功！","data":{"sid":"BC[YMD]0001"}}
								ToastUtil.showToast(getApplicationContext(),jsonObject.getString("message"),0);
								JSONObject object = (JSONObject) jsonObject.get("data");
								submit_sid = object.getString("sid");
								clearmbox_save.setEnabled(false);
							}else{
								showNormalDialog("服务器响应ERR",jsonObject.get("message").toString());
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
									
									AlertDialog.Builder builder = new AlertDialog.Builder(ClearMboxActivity.this);  
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
											ceaJson.put("sid", submit_sid);
											ceaJson.put("sbuid", sbuid); 
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
													mHandler, true, "Approval_34");
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
									ToastUtil.showToast(ClearMboxActivity.this,"没有审核人",0);
								}
							}
						}
						if(string.equals("Approval_34")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(jsonObject.getInteger("id") == 0){
								ToastUtil.showToast(ClearMboxActivity.this,jsonObject.getString("message"),0);
								if(clearmboxApapter == null){
									ToastUtil.showToast(ClearMboxActivity.this,"列表中没有数据，请扫描料盒!", 0);
								}else{
									clearmboxApapter = null;
									mListView.setAdapter(clearmboxApapter);
									submit_sid = null;
									clearmbox_save.setEnabled(true);
								}
							}else{
								ToastUtil.showToast(ClearMboxActivity.this,"34:"+jsonObject.getString("message"),0);
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
	
	private void showNormalDialog(String Title, String msg) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				ClearMboxActivity.this);
		normalDialog.setTitle(Title);
		normalDialog.setMessage(Html.fromHtml("<font color='red'>" + msg
				+ "</font>"));
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
