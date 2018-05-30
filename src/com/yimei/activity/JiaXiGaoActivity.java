package com.yimei.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.adapter.JiaXiGaoAdapter;
import com.yimei.adapter.ZhiJuRuKuAdapter;
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

public class JiaXiGaoActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private static JiaXiGaoAdapter JiaXiGaoApapter;
	private String sbid;
	private String sph;
	private String zuoyeyuan;
	private JSONObject savadateJson;

	private EditText yimei_jiaxigao_sbid, yimei_jiaxigao_prtno,yimei_jiaxigao_user;

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
				if(tag.equals("加锡膏作业员")){
					yimei_jiaxigao_user.setText(barcodeData.toString().toUpperCase());
					if (yimei_jiaxigao_user.getText().toString().trim().equals("")
							|| yimei_jiaxigao_user.getText().toString().trim() == null) {
						ToastUtil.showToast(JiaXiGaoActivity.this, "作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_jiaxigao_user);
						return;
					}
					zuoyeyuan = yimei_jiaxigao_user.getText().toString().trim();
					MyApplication.nextEditFocus((EditText) findViewById(R.id.yimei_jiaxigao_sbid));
				}
				if(tag.equals("加锡膏印刷机编号")){
					yimei_jiaxigao_sbid.setText(barcodeData.toString().toUpperCase());
					if (yimei_jiaxigao_user.getText().toString().trim().equals("")
							|| yimei_jiaxigao_user.getText().toString().trim() == null) {
						ToastUtil.showToast(JiaXiGaoActivity.this, "作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_jiaxigao_user);
						return ;
					}
					if (yimei_jiaxigao_sbid.getText().toString().trim().equals("")
							|| yimei_jiaxigao_sbid.getText().toString().trim() == null) {
						ToastUtil.showToast(JiaXiGaoActivity.this, "设备编号不能为空",0);
						MyApplication.nextEditFocus(yimei_jiaxigao_sbid);
						return ;
					}
					sbid = yimei_jiaxigao_sbid.getText().toString().toUpperCase().trim();
					Map<String, String> queryBatNo = MyApplication.QueryBatNo(
							"MESEQUTM", "~id='" + sbid + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, queryBatNo, null,
							mHander, true, "QuerySbid");
				}
				if(tag.equals("加锡膏批号")){
					yimei_jiaxigao_prtno.setText(barcodeData.toString().toUpperCase());
					if (yimei_jiaxigao_user.getText().toString().trim().equals("")
							|| yimei_jiaxigao_user.getText().toString().trim() == null) {
						ToastUtil.showToast(JiaXiGaoActivity.this, "作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_jiaxigao_user);
						return ;
					}
					if (yimei_jiaxigao_sbid.getText().toString().trim().equals("")
							|| yimei_jiaxigao_sbid.getText().toString().trim() == null) {
						ToastUtil.showToast(JiaXiGaoActivity.this, "设备编号不能为空",0);
						MyApplication.nextEditFocus(yimei_jiaxigao_sbid);
						return ;
					}
					if (yimei_jiaxigao_prtno.getText().toString().trim().equals("")
							|| yimei_jiaxigao_prtno.getText().toString().trim() == null) {
						ToastUtil.showToast(JiaXiGaoActivity.this, "批次号不能为空",0);
						MyApplication.nextEditFocus(yimei_jiaxigao_prtno);
						return ;
					}
					sph = yimei_jiaxigao_prtno.getText().toString()
							.toUpperCase().trim();
					Map<String, String> queryBatNo = MyApplication.QueryBatNo(
							"JXIAGAOPTRNO", "~sph='" + sph + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, queryBatNo, null,
							mHander, true, "Querysph");
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_jiaxigao);
		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.jiaxigao_scroll_title);
		// 添加头滑动事件
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.jixigao_scroll_list);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		yimei_jiaxigao_user = (EditText) findViewById(R.id.yimei_jiaxigao_user);
		yimei_jiaxigao_sbid = (EditText) findViewById(R.id.yimei_jiaxigao_sbid);
		yimei_jiaxigao_prtno = (EditText) findViewById(R.id.yimei_jiaxigao_prtno);
		
		yimei_jiaxigao_user.setOnEditorActionListener(editEnter);
		yimei_jiaxigao_sbid.setOnEditorActionListener(editEnter);
		yimei_jiaxigao_prtno.setOnEditorActionListener(editEnter);
		
		
		yimei_jiaxigao_user.setOnFocusChangeListener(EditGetFocus);
		yimei_jiaxigao_sbid.setOnFocusChangeListener(EditGetFocus);
		yimei_jiaxigao_prtno.setOnFocusChangeListener(EditGetFocus);
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

	/**
	 * 键盘回车事件
	 */
	OnEditorActionListener editEnter = new OnEditorActionListener() {

		@SuppressLint("DefaultLocale")
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean flag = false;
			if (v.getId() == R.id.yimei_jiaxigao_user) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_jiaxigao_user.getText().toString().trim().equals("")
							|| yimei_jiaxigao_user.getText().toString().trim() == null) {
						ToastUtil.showToast(JiaXiGaoActivity.this, "作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_jiaxigao_user);
						return false;
					}
					zuoyeyuan = yimei_jiaxigao_user.getText().toString().trim();
					MyApplication.nextEditFocus((EditText) findViewById(R.id.yimei_jiaxigao_sbid));
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_jiaxigao_sbid) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_jiaxigao_user.getText().toString().trim().equals("")
							|| yimei_jiaxigao_user.getText().toString().trim() == null) {
						ToastUtil.showToast(JiaXiGaoActivity.this, "作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_jiaxigao_user);
						return false;
					}
					if (yimei_jiaxigao_sbid.getText().toString().trim().equals("")
							|| yimei_jiaxigao_sbid.getText().toString().trim() == null) {
						ToastUtil.showToast(JiaXiGaoActivity.this, "设备编号不能为空",0);
						MyApplication.nextEditFocus(yimei_jiaxigao_sbid);
						return false;
					}
					sbid = yimei_jiaxigao_sbid.getText().toString().toUpperCase().trim();
					yimei_jiaxigao_sbid.setText(sbid);
					Map<String, String> queryBatNo = MyApplication.QueryBatNo(
							"MESEQUTM", "~id='" + sbid + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, queryBatNo, null,
							mHander, true, "QuerySbid");
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_jiaxigao_prtno) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_jiaxigao_user.getText().toString().trim().equals("")
							|| yimei_jiaxigao_user.getText().toString().trim() == null) {
						ToastUtil.showToast(JiaXiGaoActivity.this, "作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_jiaxigao_user);
						return false;
					}
					if (yimei_jiaxigao_sbid.getText().toString().trim().equals("")
							|| yimei_jiaxigao_sbid.getText().toString().trim() == null) {
						ToastUtil.showToast(JiaXiGaoActivity.this, "设备编号不能为空",0);
						MyApplication.nextEditFocus(yimei_jiaxigao_sbid);
						return false;
					}
					if (yimei_jiaxigao_prtno.getText().toString().trim().equals("")
							|| yimei_jiaxigao_prtno.getText().toString().trim() == null) {
						ToastUtil.showToast(JiaXiGaoActivity.this, "批次号不能为空",0);
						MyApplication.nextEditFocus(yimei_jiaxigao_prtno);
						return false;
					}
					sph = yimei_jiaxigao_prtno.getText().toString()
							.toUpperCase().trim();
					Map<String, String> queryBatNo = MyApplication.QueryBatNo(
							"JXIAGAOPTRNO", "~sph='" + sph + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, queryBatNo, null,
							mHander, true, "Querysph");
					flag = true;
				}
			}
			return flag;
		}
	};
	
	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_jiaxigao_user) {
				if (!hasFocus) {
					zuoyeyuan = yimei_jiaxigao_user.getText().toString().trim().toUpperCase();
				} else {
					yimei_jiaxigao_user.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_jiaxigao_sbid) {
				if (hasFocus) {
					yimei_jiaxigao_sbid.setSelectAllOnFocus(true);
				} else {
					sbid = yimei_jiaxigao_sbid.getText().toString()
							.trim().toUpperCase();
				}
			}
			if (v.getId() == R.id.yimei_jiaxigao_prtno) {
				if (hasFocus) {
					yimei_jiaxigao_prtno.setSelectAllOnFocus(true);
				} else {
					sph = yimei_jiaxigao_prtno.getText().toString().trim().toUpperCase();
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
					try {
						if (string.equals("QuerySbid")) { // 给下拉框赋值
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
								ToastUtil.showToast(JiaXiGaoActivity.this,"没有该设备号~",0);
								yimei_jiaxigao_sbid.selectAll();
								return;
							}else{
								//查询设备号的制令单号
								Map<String, String> queryBatNo = MyApplication.QueryBatNo(
										"JIAXIGAOSBID", "~sbid='" + sbid + "'");
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, queryBatNo, null,
										mHander, true, "QuerySbidSlkid");
							}
//							MyApplication.nextEditFocus(yimei_jiaxigao_prtno);
						}
						if(string.equals("QuerySbidSlkid")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
								ToastUtil.showToast(JiaXiGaoActivity.this,"该设备没有入站数据，请检查~",0);
								yimei_jiaxigao_sbid.selectAll();
								return;
							}else{
								//选择指令号
								showNormalDialog((JSONArray) jsonObject.get("values"));
							}
						}
						if(string.equals("Querysph")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
								ToastUtil.showToast(JiaXiGaoActivity.this,"该锡膏批号不存在",0);
								yimei_jiaxigao_prtno.selectAll();
								return;
							}else{
								JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject.get("values")).get(0));
								
								if(Integer.parseInt(jsonValue.get("states").toString())!=1){
									String error= Integer.parseInt(jsonValue.get("states").toString())==0?"该批次锡膏是入库状态":"该批次锡膏已过效期";
									ToastUtil.showToast(JiaXiGaoActivity.this,error,0);
									yimei_jiaxigao_prtno.selectAll();
									return;
								}else{
									boolean isUsed=jsonValue.containsKey("firstdate"); //是否开封
									String latelydate = jsonValue.get("latelydate").toString();
									if(latelydate.length()==16){
										latelydate += ":00";
									}
									Date latelydateData = MyApplication.df.parse(latelydate); //最近出库时间
									String vtime = jsonValue.get("vtime").toString().substring(0,jsonValue.get("vtime").toString().indexOf("."));
									long vt = Integer.parseInt(vtime)*3600*1000;//拆盖需要校验，小时计算
									long mintime = 0; 
									if(jsonValue.containsKey("mintime")){
										mintime = (Integer.parseInt(jsonValue.get("mintime").toString().substring(0,jsonValue.get("mintime").toString().indexOf("."))))*3600*1000;
									}
									long Lastmintime = mintime;//最小放置时间
									if(isUsed){ //是否开封
										String firstdate = jsonValue.get("firstdate").toString();
										if(firstdate.length()==16){
											firstdate += ":00";
										}
										Date tUse= MyApplication.df.parse(firstdate); //首次开封时间
										long m1=MyApplication.df.parse(MyApplication.GetServerNowTime()).getTime()-tUse.getTime();
										if (m1>vt){
											ToastUtil.showToast(JiaXiGaoActivity.this,"该批次已过有效期，不能再使用！",0);
											yimei_jiaxigao_prtno.selectAll();
											return;
										}
										//当前时间减最近出库时间
										m1 = MyApplication.df.parse(MyApplication.GetServerNowTime()).getTime()-latelydateData.getTime();
										if(m1<Lastmintime){
											long minute = (Lastmintime-m1) / 60000;  //回温时间
											ToastUtil.showToast(JiaXiGaoActivity.this,"该批次回温时间还有【"+minute+"】分钟！",0);
											yimei_jiaxigao_prtno.selectAll();
											return;
										}
										savadateJson = jsonValue;
										jsonValue.put("dcid",GetAndroidMacUtil.getMac());
										jsonValue.put("prtno",jsonValue.get("sph"));
										jsonValue.put("sbuid","D6004");
										jsonValue.put("smake",MyApplication.user);
										jsonValue.put("sys_stated", "3");
										jsonValue.put("sbid", sbid);
										jsonValue.put("qty","1");
										jsonValue.put("zcno","31");
										jsonValue.put("op",zuoyeyuan);
										jsonValue.put("state","0");
										jsonValue.put("slkid",ChoseSlkid.containsKey("slkid")?ChoseSlkid.get("slkid"):"");
										jsonValue.put("indate",MyApplication.GetServerNowTime());
										jsonValue.put("mkdate",MyApplication.GetServerNowTime());
										
										//添加数据到清洗的表中
										Map<String, String> addServerQingXiData = MyApplication
												.httpMapKeyValueMethod(MyApplication.DBID,
														"savedata", MyApplication.user,
														jsonValue.toJSONString(), "D6004", "1");
										OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,addServerQingXiData,
													null, mHander,true,"addServerJiaXiGaoData");
										return;
									}else{
										//当前时间减最近出库时间
										long m1 = MyApplication.df.parse(MyApplication.GetServerNowTime()).getTime()-latelydateData.getTime();
										if(Lastmintime!=0){
											if(m1<Lastmintime&&m1>0){
												long minute = (Lastmintime-m1) / 60000; //回温时间
												ToastUtil.showToast(JiaXiGaoActivity.this,"该批次回温时间还有【"+minute+"】分钟！",0);
												yimei_jiaxigao_prtno.selectAll();
												return;
											}
										}
										savadateJson = jsonValue;
										jsonValue.put("dcid",GetAndroidMacUtil.getMac());
										jsonValue.put("prtno",jsonValue.get("sph"));
										jsonValue.put("sbuid","D6004");
										jsonValue.put("smake",MyApplication.user);
										jsonValue.put("sys_stated", "3");
										jsonValue.put("sbid", sbid);
										jsonValue.put("qty","1");
										jsonValue.put("zcno","31");
										jsonValue.put("op",zuoyeyuan);
										jsonValue.put("state","0");
										jsonValue.put("slkid",ChoseSlkid.containsKey("slkid")?ChoseSlkid.get("slkid"):"");
										jsonValue.put("indate",MyApplication.GetServerNowTime());
										jsonValue.put("mkdate",MyApplication.GetServerNowTime());
										
										//添加数据到清洗的表中
										Map<String, String> addServerQingXiData = MyApplication
												.httpMapKeyValueMethod(MyApplication.DBID,
														"savedata", MyApplication.user,
														jsonValue.toJSONString(), "D6004", "1");
										OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,addServerQingXiData,
													null, mHander,true,"addServerJiaXiGaoData");
									}
								}
							}
						}
						if(string.equals("addServerJiaXiGaoData")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(Integer.parseInt(jsonObject.get("id").toString()) == -1){
								ToastUtil.showToast(JiaXiGaoActivity.this,"（savadate）失败",0);
								return;
							}
							//420请求 
							Map<String, String> updateServerMoJuZct = MyApplication
									.updateServerJiaXiaoGao(MyApplication.DBID,
											MyApplication.user,sbid,sph,
											"410");
							OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,
									null,updateServerMoJuZct,null,mHander,true,"updateServerMoJuZct");
							
						}
						if(string.equals("updateServerMoJuZct")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(Integer.parseInt(jsonObject.getString("id")) == -1){
								ToastUtil.showToast(JiaXiGaoActivity.this,"410请求错误",0);
								return;
							}else{
								if(savadateJson!=null){
									// 给适配器添加数据
									List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
									Map<String, String> map = new HashMap<String, String>();
									map.put("op", zuoyeyuan == null ? "": zuoyeyuan);
									map.put("prtno",sph==null?"":sph);
									map.put("sbid",sbid);
									map.put("slkid",savadateJson.getString("mono")==null?"":savadateJson.getString("mono"));
									map.put("prdno",savadateJson.getString("prdno")==null?"":savadateJson.getString("prdno"));
									map.put("qty",savadateJson.getString("qty")==null?"":savadateJson.getString("qty"));
									map.put("indate",savadateJson.getString("indate")==null?"":savadateJson.getString("indate"));
									map.put("mkdate",savadateJson.getString("mkdate")==null?"":savadateJson.getString("mkdate"));
									mList.add(map);
									if (JiaXiGaoApapter == null) {
										JiaXiGaoApapter = new JiaXiGaoAdapter(
												JiaXiGaoActivity.this, mList);
										mListView.setAdapter(JiaXiGaoApapter);
									} else {
										JiaXiGaoApapter.listData.add(map);
										JiaXiGaoApapter.notifyDataSetChanged();
									}
									MyApplication.nextEditFocus(yimei_jiaxigao_prtno);
									yimei_jiaxigao_prtno.selectAll();
								}
							}
							System.out.println(jsonObject);
						}
						if(string.equals("IsJiaJiao")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							String message = jsonObject.get("message").toString();
							if(message.indexOf(",") != -1){
								int msg1 = Integer.parseInt(String.valueOf(message.substring(0,message.indexOf(","))));
								showNormalDialog1(msg1,message);
							}else{
								MyApplication.nextEditFocus(yimei_jiaxigao_prtno);
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
	 * 弹出提示框
	 * 
	 * @param mes
	 */
	private void showNormalDialog1(int num,String msg) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				JiaXiGaoActivity.this);
		normalDialog.setTitle("提示");
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		if(num==1){
			normalDialog.setMessage(msg.substring(msg.indexOf(",")+1,msg.length()));
			normalDialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							MyApplication.nextEditFocus(yimei_jiaxigao_prtno);
						}
				});
			normalDialog.setNegativeButton("取消", null);
		}
		if(num==-1){
			normalDialog.setMessage(msg.substring(msg.indexOf(",")+1,msg.length()));
			normalDialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							yimei_jiaxigao_sbid.selectAll();
						}
				});
		}
		// 显示
		normalDialog.show();
	}
	
	private int checkNum = 0; //取showdialog选中的下标
	static Map<Integer,JSONObject> dig_map = new HashMap<Integer,JSONObject>(); //存放选择的机型 （ 判断选择的下标）
	private JSONObject ChoseSlkid;
	/**
	 * 弹出提示框
	 * 
	 * @param mes
	 */
	private void showNormalDialog(JSONArray jsonarr) {
		String [] str = new String[jsonarr.size()]; //显示的数据
		for (int i = 0; i < jsonarr.size(); i++) {
			JSONObject jsonobj = (JSONObject) jsonarr.get(i);
			dig_map.put(i,jsonobj);
			String sid1 = jsonobj.containsKey("sid1")?jsonobj.getString("sid1"):"";
			String prd_no = jsonobj.containsKey("prd_no")?jsonobj.getString("prd_no"):"";
			String slkid = jsonobj.containsKey("slkid")?jsonobj.getString("slkid"):"";
			String strShow ="批次号："+sid1+"\n"
					+"货品代号："+prd_no+"\n"
					+"制令单号："+slkid;
			str[i] = strShow;
		}
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				JiaXiGaoActivity.this);
		normalDialog.setTitle("选中批次号");
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setSingleChoiceItems(
			    str, checkNum,
			     new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  checkNum = which;
			      }});
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ChoseSlkid = dig_map.get(checkNum); //取选中的下标
						
						Map<String, String> map = new HashMap<String, String>();
						map.put("apiId","mesudp");
						map.put("dbid",MyApplication.DBID);
						map.put("usercode",MyApplication.user);
						map.put("sbid",sbid);
						map.put("id","450");
						OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,map
								, null, mHander,true,"IsJiaJiao");
//						MyApplication.nextEditFocus(yimei_jiaxigao_prtno);
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
