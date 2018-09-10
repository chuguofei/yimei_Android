package com.yimei.activity;

import java.util.ArrayList;
import java.util.Date;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.adapter.ZhiJuLingChuAdapter;
import com.yimei.entity.Pair;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

public class ZhiJuLingChuActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private static ZhiJuLingChuAdapter ZhiJuLingChuAdapter;
	private Spinner selectValue; // 下拉框
	private EditText yimei_zhijulingchu_user, yimei_zhijulingchu_proNum_edt,
			yimei_zhijulingchu_mojuId;
	private String zcno;
	private String prd_no;
	private String mojuId;
	private String zuoyeyuan;
	private JSONObject piciJsonObject;
	private int shiyongNum=0; //使用次数 
	
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
				// 拿到pda扫描后的值
				String barcodeData;
				if (intent.getStringExtra("data") == null) {
					barcodeData = intent.getStringExtra(
							MyApplication.SCN_CUST_EX_SCODE)// 拿到销邦终端的值
							.toString();
				} else {
					barcodeData = intent.getStringExtra("data").toString(); // 拿到HoneyWell终端的值
				}
				if (tag.equals("制具领出作业员")) {
					yimei_zhijulingchu_user.setText(barcodeData.toString()
							.toUpperCase());
					if (yimei_zhijulingchu_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_user);
						return;
					}
					zuoyeyuan = yimei_zhijulingchu_user.getText().toString()
							.trim();
					MyApplication.nextEditFocus(yimei_zhijulingchu_proNum_edt);
				}
				if (tag.equals("制具领出批次号")) {
					yimei_zhijulingchu_proNum_edt.setText(barcodeData
							.toString().toUpperCase());
					if (yimei_zhijulingchu_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_user);
						return;
					}
					if (yimei_zhijulingchu_proNum_edt.getText().toString()
							.trim().equals("")
							|| yimei_zhijulingchu_proNum_edt.getText()
									.toString().trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"生产批次不能为空", 0);
						MyApplication
								.nextEditFocus(yimei_zhijulingchu_proNum_edt);
						return;
					}
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZCLISTWEB", "~sid1='"
									+ yimei_zhijulingchu_proNum_edt.getText()
											.toString() + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "picihaoQuery");
				}
				if (tag.equals("制具领出模具编号")) {
					yimei_zhijulingchu_mojuId.setText(barcodeData.toString()
							.toUpperCase());
					if (yimei_zhijulingchu_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_user);
						return;
					}
					if (yimei_zhijulingchu_proNum_edt.getText().toString()
							.trim().equals("")
							|| yimei_zhijulingchu_proNum_edt.getText()
									.toString().trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"生产批次不能为空", 0);
						MyApplication
								.nextEditFocus(yimei_zhijulingchu_proNum_edt);
						return;
					}
					if (yimei_zhijulingchu_mojuId.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_mojuId.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"模具编号不能为空", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_mojuId);
						return;
					}
					mojuId = yimei_zhijulingchu_mojuId.getText().toString()
							.toUpperCase().trim();
					// 模具领出主子表
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZREGISTER", "~prd_no='" + prd_no + "' and id='"
									+ mojuId + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "MOZRegisterQuery");
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhijulingchu);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		String cont;
		if(MyApplication.user.equals("admin")){
			cont="";
		}else{
			cont =  "~sorg='"+MyApplication.sorg+"'";
		}
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				MyApplication.QueryBatNo("M_PROCESS",cont), null, mHander, true,
				"SpinnerValue");

		selectValue = (Spinner) findViewById(R.id.zhijulingchu_selectValue);

		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.zhijulingchu_scroll_title);
		// 添加头滑动事件
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.zhijulingchu_scroll_list);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播

		yimei_zhijulingchu_user = (EditText) findViewById(R.id.yimei_zhijulingchu_user_edt);
		yimei_zhijulingchu_proNum_edt = (EditText) findViewById(R.id.yimei_zhijulingchu_proNum_edt);
		yimei_zhijulingchu_mojuId = (EditText) findViewById(R.id.yimei_zhijulingchu_mojuId);

		yimei_zhijulingchu_user.setOnEditorActionListener(editEnter);
		yimei_zhijulingchu_proNum_edt.setOnEditorActionListener(editEnter);
		yimei_zhijulingchu_mojuId.setOnEditorActionListener(editEnter);

		yimei_zhijulingchu_user.setOnFocusChangeListener(EditGetFocus);
		yimei_zhijulingchu_proNum_edt.setOnFocusChangeListener(EditGetFocus);
		yimei_zhijulingchu_mojuId.setOnFocusChangeListener(EditGetFocus);
	}

	/**
	 * 键盘回车事件
	 */
	OnEditorActionListener editEnter = new OnEditorActionListener() {

		@SuppressLint("DefaultLocale")
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean flag = false;
			if (v.getId() == R.id.yimei_zhijulingchu_user_edt) {
				if (actionId >= 0) {
					if (yimei_zhijulingchu_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"作业员不能为空!", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_user);
						return false;
					}
					zuoyeyuan = yimei_zhijulingchu_user.getText().toString()
							.trim();
					MyApplication.nextEditFocus(yimei_zhijulingchu_proNum_edt);
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_zhijulingchu_proNum_edt) {
				if (actionId >= 0) {
					if (yimei_zhijulingchu_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"作业员不能为空!", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_user);
						return false;
					}
					if (yimei_zhijulingchu_proNum_edt.getText().toString()
							.trim().equals("")
							|| yimei_zhijulingchu_proNum_edt.getText()
									.toString().trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"生产批次不能为空!", 0);
						MyApplication
								.nextEditFocus(yimei_zhijulingchu_proNum_edt);
						return false;
					}
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZCLISTWEB", "~sid1='"
									+ yimei_zhijulingchu_proNum_edt.getText()
											.toString() + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "picihaoQuery");
				}
			}
			if (v.getId() == R.id.yimei_zhijulingchu_mojuId) {
				if (actionId >= 0) {
					if (yimei_zhijulingchu_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"作业员不能为空!", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_user);
						return false;
					}
					if (yimei_zhijulingchu_proNum_edt.getText().toString()
							.trim().equals("")
							|| yimei_zhijulingchu_proNum_edt.getText()
									.toString().trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"生产批次不能为空!", 0);
						MyApplication
								.nextEditFocus(yimei_zhijulingchu_proNum_edt);
						return false;
					}
					if (yimei_zhijulingchu_mojuId.getText().toString().trim()
							.equals("")
							|| yimei_zhijulingchu_mojuId.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuLingChuActivity.this,
								"模具编号不能为空!", 0);
						MyApplication.nextEditFocus(yimei_zhijulingchu_mojuId);
						return false;
					}
					mojuId = yimei_zhijulingchu_mojuId.getText().toString()
							.toUpperCase().trim();
					// 模具领出主子表
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZREGISTER", "~prd_no='" + prd_no + "' and id='"
									+ mojuId + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "MOZRegisterQuery");
				}
			}
			return flag;
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(barcodeReceiver); // 取消广播注册
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_zhijulingchu_user_edt) {
				if (!hasFocus) {
					zuoyeyuan = yimei_zhijulingchu_user.getText().toString()
							.trim();
				} else {
					yimei_zhijulingchu_user.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_zhijulingchu_proNum_edt) {
				if (hasFocus) {
					yimei_zhijulingchu_proNum_edt.setSelectAllOnFocus(true);
				} else {
					
				}
			}
			if (v.getId() == R.id.yimei_zhijulingchu_mojuId) {
				if (hasFocus) {
					yimei_zhijulingchu_mojuId.setSelectAllOnFocus(true);
				}
			}
		}
	};

	/**
	 * 逻辑判断
	 */
	@SuppressLint("HandlerLeak")
	private final Handler mHander = new Handler() {
		@SuppressLint("DefaultLocale") @Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				if (msg.arg1 == 0) {
					Bundle b = msg.getData();
					String string = b.getString("type");
					try {
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
						if (string.equals("picihaoQuery")) { // 批次号查询
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								/*MyApplication
										.nextEditFocus(yimei_zhijulingchu_mojuId);*/
								ToastUtil.showToast(ZhiJuLingChuActivity.this,"没有【"+yimei_zhijulingchu_proNum_edt.getText().toString()+"】批次号，请检查！",0);
								yimei_zhijulingchu_proNum_edt.selectAll();
							} else {
								JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
										.get("values")).get(0));
								
								//查询治具使用次数
								 Map<String, String> CPNUm = MyApplication.QueryBatNo("CPLOTNUM","~sid='"+yimei_zhijulingchu_proNum_edt.getText().toString().toUpperCase()+"'");
								 OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
										 CPNUm, null, mHander,true,"Query_shiyongNun");
								 
							    prd_no = jsonValue.get("prd_no").toString();
								piciJsonObject = jsonValue;
								MyApplication.nextEditFocus(yimei_zhijulingchu_mojuId);
							}
						}
						if(string.equals("Query_shiyongNun")){ //查询治具使用次数
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if(Integer.parseInt(jsonObject.get("code").toString())==0){
								
							}else{
								JSONObject json =  (JSONObject) ((JSONArray)jsonObject.get("values")).get(0);
								int totalqty = 0;
								int unprtnum = Integer.parseInt(json.get("unprtnum").toString());
								if(!json.containsKey("totalqty")){
									totalqty = 160;
								}else{
									totalqty = Integer.parseInt(json.get("totalqty").toString());
								}
								shiyongNum = totalqty/unprtnum;
								ToastUtil.showToast(ZhiJuLingChuActivity.this,String.valueOf(shiyongNum),0);
							}
						}
						if (string.equals("MOZRegisterQuery")) { // 模具登记查询（prd_no+id）
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(ZhiJuLingChuActivity.this,
										"该治具不能在当前设备使用", 0);
								yimei_zhijulingchu_mojuId.selectAll();
								return;
							} else {
								Map<String, String> map = MyApplication
										.QueryBatNo("MOZREGISTER", "~prd_no='"
												+ prd_no + "' and id='"
												+ mojuId + "'");
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, map, null,
										mHander, true, "MOZzt1Andzt2Querey");
							}
							System.out.println(jsonObject);
						}
						if (string.equals("MOZzt1Andzt2Querey")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
									.get("values")).get(0));
							if (jsonValue.get("zt2").toString().equals("1")) {
								ToastUtil.showToast(ZhiJuLingChuActivity.this,
										"该制具已被使用~", 0);
								yimei_zhijulingchu_mojuId.selectAll();
								return;
							} else if (jsonValue.get("zt1").toString()
									.equals("0")) {
								ToastUtil.showToast(ZhiJuLingChuActivity.this,
										"请先清洗制具~", 0);
								yimei_zhijulingchu_mojuId.selectAll();
								return;
							} else {
								//回写使用次数
								if(!jsonValue.containsKey("type")){
									showNormalDialog("该治具没有填写使用类型，请先填写!");
									yimei_zhijulingchu_mojuId.selectAll();
									return;
								}
								if(!jsonValue.containsKey("shouming")){
									showNormalDialog("该治具没有填写使用寿命，请先填写!");
									yimei_zhijulingchu_mojuId.selectAll();
									return;
								}
								int shouming = Integer.parseInt(jsonValue.get("shouming").toString());
								System.out.println(shouming);
								int yishiyong = Integer.parseInt(jsonValue.get("yishiyong").toString());
								int type = Integer.parseInt(jsonValue.get("type").toString());
								if(type==0){ //次数
									if(yishiyong>shouming){
										showNormalDialog("该治具寿命为:"+shouming+"次,已使用:"+yishiyong+"次，不能继续使用。");
										yimei_zhijulingchu_mojuId.selectAll();
										return;
									}
									ToastUtil.showToast(ZhiJuLingChuActivity.this,"该治具可使用次数为："+String.valueOf(shouming-yishiyong),0);
								}else{ //天数
									if(!jsonValue.containsKey("pur_date")){
										showNormalDialog("该治具没有填写购买时间!");
										yimei_zhijulingchu_mojuId.selectAll();
										return;
									}
									String pur_date = jsonValue.get("pur_date").toString();
									if(pur_date.length()==16){
										pur_date += ":00";
									}
									if(pur_date.length()==10){
										pur_date += " 00:00:00";
									}
									Date pur_DATE = MyApplication.df.parse(pur_date);

									Date NowTime = MyApplication.df.parse(MyApplication.GetServerNowTime());
									long days = (NowTime.getTime() - pur_DATE.getTime())/ (1000 * 3600 * 24);
									if(days>shouming){
										showNormalDialog("该治具使用期限已到达不能在使用！");
										return;
									}else{
										ToastUtil.showToast(ZhiJuLingChuActivity.this,"该治具已使用"+days+"天",1);
									}
								}
								//**************修改次数************************
								Map<String, String> CiShumap = new HashMap<String, String>();
								CiShumap.put("apiId","mesudp");
								CiShumap.put("dbid",MyApplication.DBID);
								CiShumap.put("usercode",MyApplication.user);
								CiShumap.put("sbid",mojuId);
								CiShumap.put("sid1",yimei_zhijulingchu_proNum_edt.getText().toString().toUpperCase());
								CiShumap.put("id","500");
								OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,CiShumap
										, null, mHander,true,"UpdateCiShu");
								//**************修改次数************************
								Map<String, String> mesIdMap = MyApplication
										.httpMapKeyValueMethod(MyApplication.DBID,
												"savedata", MyApplication.user,
												jsonValue.toJSONString(),
												"D0001WEB", "1");
								//**************修改次数************************
								// 400请求 （修改zct）
								Map<String, String> updateServerMoJu = MyApplication
										.updateServerMoJu(MyApplication.DBID,
												MyApplication.user, "1",
												mojuId, "400");
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null,
										updateServerMoJu, null, mHander, true,
										"UpdateServerZct");

								if (piciJsonObject != null) {
									piciJsonObject.put("sbid",mojuId);
									piciJsonObject.put("slkid",piciJsonObject.containsKey("sid")?piciJsonObject.get("sid"):"");
									piciJsonObject.put("op",
											zuoyeyuan.toUpperCase());
									piciJsonObject.put("mkdate",
											MyApplication.GetServerNowTime());
									piciJsonObject.put("dcid",
											GetAndroidMacUtil.getMac());
									piciJsonObject.put("sbuid", "E5005");
									piciJsonObject.put("smake",
											MyApplication.user);
									piciJsonObject.put("zcno", zcno);
									piciJsonObject.put("sys_stated", "3");
									// 添加数据到清洗的表中
									Map<String, String> addServerQingXiData = MyApplication
											.httpMapKeyValueMethod(
													MyApplication.DBID,
													"savedata",
													MyApplication.user,
													piciJsonObject.toJSONString(),
													"E5005", "1");
									OkHttpUtils.getInstance().getServerExecute(
											MyApplication.MESURL, null,
											addServerQingXiData, null, mHander,
											true, "addServerLingChuData");

									List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
									// 给适配器添加数据
									Map<String, String> map = new HashMap<String, String>();
									map.put("sid1",yimei_zhijulingchu_proNum_edt.getText().toString());
									map.put("sbid", mojuId);
									map.put("slkid", piciJsonObject.get("sid").toString());
									map.put("prd_no",piciJsonObject.getString("prd_no"));
									map.put("zcno",zcno);
									map.put("mkdate",MyApplication.GetServerNowTime());
									mList.add(map);
									if (ZhiJuLingChuAdapter == null) {
										ZhiJuLingChuAdapter = new ZhiJuLingChuAdapter(
												ZhiJuLingChuActivity.this,
												mList);
										mListView
												.setAdapter(ZhiJuLingChuAdapter);
									} else {
										ZhiJuLingChuAdapter.listData.add(map);
										mListView
												.setAdapter(ZhiJuLingChuAdapter);
										ZhiJuLingChuAdapter
												.notifyDataSetChanged();
									}
									InputHidden();
									yimei_zhijulingchu_mojuId.selectAll();
								}
							}
						}
						if(string.equals("UpdateCiShu")){ //修改次数
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							System.out.println(jsonObject);
						}
						if (string.equals("UpdateServerZct")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id")
									.toString()) == 0) {
								ToastUtil.showToast(getApplicationContext(),
										"400请求错误!", 0);
							}
						}
						if (string.equals("addServerLingChuData")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id")
									.toString()) != 0) {
								ToastUtil.showToast(getApplicationContext(),
										"（savadate）添加失败!", 0);
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
	 * 隐藏键盘
	 */
	private void InputHidden() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 如果软键盘已经显示，则隐藏，反之则显示
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	private void showNormalDialog(String msg) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(ZhiJuLingChuActivity.this);
		normalDialog.setTitle("提示");
		normalDialog.setMessage(msg);
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
				ZhiJuLingChuActivity.this,
				android.R.layout.simple_spinner_item, dicts);
		selectValue.setAdapter(adapter);
		selectValue.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				zcno = ((Pair) selectValue.getSelectedItem()).getValue();
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
			showNormalDialog("1.新增治具使用次数、使用寿命"
					+ "\n2.修改报表没有模具编码,指令号");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
