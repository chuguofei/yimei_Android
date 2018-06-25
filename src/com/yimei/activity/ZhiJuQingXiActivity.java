package com.yimei.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.ipqc.IPQC_shoujian;
import com.yimei.adapter.ZhiJuQingXiAdapter;
import com.yimei.entity.Pair;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;

public class ZhiJuQingXiActivity extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private static ZhiJuQingXiAdapter ZhiJuQingXiAdapter; // 适配器
	private EditText yimei_zhijuqingxi_user, yimei_zhijuqingxi_mojuId;
	private Button yimei_zhijuqingxi_zhangliceshi;
	private String mojuId;
	private String zuoyeyuan;
	private EditText A,B,C,D,E,F,G;
	private String a,b1,c,d,e,f,g;
	private View dialog; //张力测试的view
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
				if(tag.equals("制具清洗作业员")){
					yimei_zhijuqingxi_user.setText(barcodeData.toString().toUpperCase());
					if (yimei_zhijuqingxi_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijuqingxi_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuQingXiActivity.this, "作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_zhijuqingxi_user);
						return;
					}
					zuoyeyuan = yimei_zhijuqingxi_user.getText().toString().trim();
					MyApplication.nextEditFocus((EditText) findViewById(R.id.yimei_zhijuqingxi_mojuId));
				}
				if(tag.equals("制具清洗模具编号")){
					yimei_zhijuqingxi_mojuId.setText(barcodeData.toString().toUpperCase());
					if (yimei_zhijuqingxi_user.getText().toString().trim()
							.equals("")
							|| yimei_zhijuqingxi_user.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuQingXiActivity.this, "作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_zhijuqingxi_user);
						return;
					}
					if (yimei_zhijuqingxi_mojuId.getText().toString().trim()
							.equals("")
							|| yimei_zhijuqingxi_mojuId.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ZhiJuQingXiActivity.this, "作业员不能为空",0);
						MyApplication.nextEditFocus(yimei_zhijuqingxi_mojuId);
						return;
					}
					mojuId = yimei_zhijuqingxi_mojuId.getText().toString().toUpperCase().trim();
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZREGISTER", "~id='"
									+ mojuId + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "MoJuIdQuery"); //模具清洗查询编号
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhijuqingxi);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				MyApplication.QueryBatNo("M_PROCESS","~sorg='"+MyApplication.sorg+"'"), null, mHander, true,
				"SpinnerValue");

		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.zhijuqingxi_scroll_title);
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.zhijuqingxi_scroll_list);
	
	
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		
		yimei_zhijuqingxi_user = (EditText) findViewById(R.id.yimei_zhijuqingxi_user_edt);
		yimei_zhijuqingxi_mojuId = (EditText) findViewById(R.id.yimei_zhijuqingxi_mojuId);
		yimei_zhijuqingxi_zhangliceshi = (Button) findViewById(R.id.yimei_zhijuqingxi_zhangliceshi);
		yimei_zhijuqingxi_user.setOnEditorActionListener(editEnter);
		yimei_zhijuqingxi_mojuId.setOnEditorActionListener(editEnter);
		
		yimei_zhijuqingxi_user.setOnFocusChangeListener(EditGetFocus);
		yimei_zhijuqingxi_mojuId.setOnFocusChangeListener(EditGetFocus);
		
		
		yimei_zhijuqingxi_zhangliceshi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showNormalDialog();
			}
		});
	}
	
	/**
	 * 张力测试
	 * 
	 * @param mes
	 */
	private void showNormalDialog() {
		LayoutInflater inflater = getLayoutInflater();
		dialog = inflater.inflate(R.layout.activity_zhangliceshi_dig,
				(ViewGroup) findViewById(R.id.zhangliceshi_dialog));
		A = (EditText) dialog.findViewById(R.id.A);
		B = (EditText) dialog.findViewById(R.id.B);
		C = (EditText) dialog.findViewById(R.id.C);
		D = (EditText) dialog.findViewById(R.id.D);
		E = (EditText) dialog.findViewById(R.id.E);
		F = (EditText) dialog.findViewById(R.id.F);
		G = (EditText) dialog.findViewById(R.id.G);
		
		A.setOnFocusChangeListener(EditGetFocus);
		B.setOnFocusChangeListener(EditGetFocus);
		C.setOnFocusChangeListener(EditGetFocus);
		D.setOnFocusChangeListener(EditGetFocus);
		E.setOnFocusChangeListener(EditGetFocus);
		F.setOnFocusChangeListener(EditGetFocus);
		G.setOnFocusChangeListener(EditGetFocus);
		
		A.setText(a==null?"":a);
		B.setText(b1==null?"":b1);
		C.setText(c==null?"":c);
		D.setText(d==null?"":d);
		E.setText(e==null?"":e);
		F.setText(f==null?"":f);
		G.setText(g==null?"":g);
		
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				ZhiJuQingXiActivity.this);
		normalDialog.setTitle("张力测试");
		normalDialog.setView(dialog);
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						a = A.getText().toString();
						b1 = B.getText().toString();
						c = C.getText().toString();
						d = D.getText().toString();
						e = E.getText().toString();
						f = F.getText().toString();
						g = G.getText().toString();
					}
				});
		// 显示
		normalDialog.show();
	}	@Override
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
			if (v.getId() == R.id.yimei_zhijuqingxi_user_edt) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_zhijuqingxi_user.getText().toString().trim().equals("")
							|| yimei_zhijuqingxi_user.getText().toString().trim() == null) {
						ToastUtil.showToast(ZhiJuQingXiActivity.this, "作业员不能为空", 0);
						return false;
					}
					zuoyeyuan = yimei_zhijuqingxi_user.getText().toString().trim();
					MyApplication.nextEditFocus((EditText) findViewById(R.id.yimei_zhijuqingxi_mojuId));
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_zhijuqingxi_mojuId) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_zhijuqingxi_user.getText().toString().trim().equals("")
							|| yimei_zhijuqingxi_user.getText().toString().trim() == null) {
						ToastUtil.showToast(ZhiJuQingXiActivity.this, "作业员不能为空", 0);
						return false;
					}
					if (yimei_zhijuqingxi_mojuId.getText().toString().trim().equals("")
							|| yimei_zhijuqingxi_mojuId.getText().toString().trim() == null) {
						ToastUtil.showToast(ZhiJuQingXiActivity.this, "模具编号不能为空", 0);
						return false;
					}
					mojuId = yimei_zhijuqingxi_mojuId.getText().toString().toUpperCase().trim();
					Map<String, String> map = MyApplication.QueryBatNo(
							"MOZREGISTER", "~id='"+ mojuId + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, map, null, mHander,
							true, "MoJuIdQuery"); //模具清洗查询编号
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
			if (v.getId() == R.id.yimei_zhijuqingxi_user_edt) {
				if (!hasFocus) {
					zuoyeyuan = yimei_zhijuqingxi_user.getText().toString().trim();
				} else {
					yimei_zhijuqingxi_user.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_zhijuqingxi_mojuId) {
				if (hasFocus) {
					yimei_zhijuqingxi_mojuId.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.A) {
				if (!hasFocus) {
					a = A.getText().toString();
				}else{
					A.setText(a==null?"":a);
				}
			}
			if (v.getId() == R.id.B) {
				if (!hasFocus) {
					b1 = B.getText().toString();
				}else{
					B.setText(b1==null?"":b1);
				}
			}
			if (v.getId() == R.id.C) {
				if (!hasFocus) {
					c = C.getText().toString();
				}else{
					C.setText(c==null?"":c);
				}
			}
			if (v.getId() == R.id.C) {
				if (!hasFocus) {
					c = C.getText().toString();
				}else{
					C.setText(c==null?"":c);
				}
			}
			if (v.getId() == R.id.D) {
				if (!hasFocus) {
					d = D.getText().toString();
				}else{
					D.setText(d==null?"":d);
				}
			}
			if (v.getId() == R.id.E) {
				if (!hasFocus) {
					e = E.getText().toString();
				}else{
					E.setText(e==null?"":e);
				}
			}
			if (v.getId() == R.id.F) {
				if (!hasFocus) {
					f = F.getText().toString();
				}else{
					F.setText(f==null?"":f);
				}
			}
			if (v.getId() == R.id.G) {
				if (!hasFocus) {
					g = G.getText().toString();
				}else{
					G.setText(g==null?"":g);
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
						if(string.equals("MoJuIdQuery")){ //模具编号回车
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if(Integer.parseInt(jsonObject.getString("code").toString()) == 0){
								ToastUtil.showToast(getApplicationContext(),"没有该模具编号或制程！",0);
								InputHidden(); 
								zhangliEditClear();
								if (mListView != null) {
									mListView.setAdapter(null);
									if (ZhiJuQingXiAdapter != null) {
										ZhiJuQingXiAdapter.notifyDataSetChanged();
									}
								}
								yimei_zhijuqingxi_mojuId.selectAll();
								return;
							}else{								
								JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject.get("values")).get(0));
								jsonValue.put("op",yimei_zhijuqingxi_user.getText().toString().toUpperCase().trim());
								jsonValue.put("mkdate",MyApplication.GetServerNowTime());
								jsonValue.put("dcid",GetAndroidMacUtil.getMac());
								jsonValue.put("sbuid","E5006");
								jsonValue.put("sbid",mojuId);
								jsonValue.put("zcno","S");
								jsonValue.put("qty","1");
								jsonValue.put("state","0");
								jsonValue.put("zt","0");
								jsonValue.put("smake",MyApplication.user);
								jsonValue.put("sys_stated", "3");
								jsonValue.put("strain_a", a==null?"":a);
								jsonValue.put("strain_b", b1==null?"":b1);
								jsonValue.put("strain_c", c==null?"":c);
								jsonValue.put("strain_d", d==null?"":d);
								jsonValue.put("strain_e", e==null?"":e);
								jsonValue.put("strain_f", f==null?"":f);
								jsonValue.put("strain_g", g==null?"":g);
								//添加数据到清洗的表中
								Map<String, String> addServerQingXiData = MyApplication
										.httpMapKeyValueMethod(MyApplication.DBID,
												"savedata", MyApplication.user,
												jsonValue.toJSONString(), "E5006", "1");
								OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,addServerQingXiData,
											null, mHander,true,"addServerQingXiData");
								
								//400请求 （修改zct）
								Map<String, String> updateServerMoJuZct = MyApplication
										.updateServerMoJu(MyApplication.DBID,
												MyApplication.user,"AA",mojuId,
												"400");
								OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,
										null,updateServerMoJuZct,null,mHander,true,"updateServerMoJuZct");
								
								//给适配器添加数据
								List<Map<String,String>> mList = new ArrayList<Map<String,String>>();
								Map<String,String> map = new HashMap<String, String>();
								map.put("op",zuoyeyuan==null?"":zuoyeyuan);
								map.put("sbid",mojuId==null?"":mojuId);
								map.put("mkdate",MyApplication.GetServerNowTime());
								mList.add(map);
								if(ZhiJuQingXiAdapter == null){							
									ZhiJuQingXiAdapter = new ZhiJuQingXiAdapter(ZhiJuQingXiActivity.this,mList);
									mListView.setAdapter(ZhiJuQingXiAdapter);
									InputHidden();
									yimei_zhijuqingxi_mojuId.selectAll();
									zhangliEditClear();
								}else{
									ZhiJuQingXiAdapter.listData.add(map);
									mListView.setAdapter(ZhiJuQingXiAdapter);
									ZhiJuQingXiAdapter.notifyDataSetChanged();
								}
							}
							System.out.println(jsonObject);
						}
						if(string.equals("addServerQingXiData")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							System.out.println(jsonObject);
						}
						if(string.equals("updateServerMoJuZct")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
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
	
	private void zhangliEditClear(){
		A.setText("");
		B.setText("");
		C.setText("");
		D.setText("");
		E.setText("");
		F.setText("");
		G.setText("");
		a = "";
		b1 = "";
		c = "";
		d = "";
		e = "";
		f = "";
		g = "";
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
