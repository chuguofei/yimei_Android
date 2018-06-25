package com.yimei.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.ipqc.IPQC_shoujian;
import com.yimei.adapter.MoZuAdapter;
import com.yimei.entity.Pair;
import com.yimei.entity.mesPrecord;
import com.yimei.scrollview.MoZuCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.HttpUtil;
import com.yimei.util.ToastUtil;

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
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;

public class MoZuActivity extends Activity {

	static MyApplication myapp;
	public static MoZuActivity mozuActivity;
	private static ListView mListView; // listview
	public HorizontalScrollView mTouchView;
	private static List<MoZuCHScrollView> MoZuCHScrollViews = new ArrayList<MoZuCHScrollView>(); // 行+标题滚动
	private static MoZuAdapter MoZuAdapter; // 适配器

	private EditText yimei_mozu_user_edt;
	private EditText yimei_mozu_sbid_edt;
	private EditText yimei_mozu_proNum_edt;
	private String sid1;
	private String currSlkid;
	private static JSONObject newJson; // 拿新sid存在json
	
	private String EQIRP_prdno; //判断是否做首检的机型号
	private String EQIRP_firstchk; //判断是否做首检的标示
	private String shebeihao;
	private String zuoyeyuan;
	private String picihao;
	private CheckBox quanxuan; // 全选按钮
	private Button mozu_kaigong;// 开工按钮
	private Button mozu_chuzhan;// 出站按钮
	private Button mozu_shangliao;// 上料按钮
	private String zcno;
	private Spinner selectValue; // 下拉框
	private List<mesPrecord> updateListState; // 修改服务器的2张表的状态（出站，开工）,更改本地库的状态
	private ArrayList<String> SlkidMapJudge = new ArrayList<String>(); // 不同工单不能入站
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
				if(tag==null){
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
				if (tag.equals("模组作业员")) { // 作业员
					Log.i("id", "作业员");
					yimei_mozu_user_edt.setText(barcodeData);
					if (yimei_mozu_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_mozu_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplication(), "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_mozu_user_edt);
						return;
					}
					MyApplication.nextEditFocus(yimei_mozu_sbid_edt);
				}
				if (tag.equals("模组设备号")) { // 设备号
					Log.i("id", "设备");
					yimei_mozu_sbid_edt.setText(barcodeData);
					if (yimei_mozu_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_mozu_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplication(), "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_mozu_user_edt);
						return;
					}
					if (yimei_mozu_sbid_edt.getText().toString().trim()
							.equals("")
							|| yimei_mozu_sbid_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplication(), "设备号不能为空", 0);
						MyApplication.nextEditFocus(yimei_mozu_sbid_edt);
						return;
					}
					shebeihao = yimei_mozu_sbid_edt.getText().toString().trim();
					Map<String, String> IsSbidQuery = MyApplication
							.IsSbidQuery_mozu(shebeihao,zcno);
					httpRequestQueryRecord(MyApplication.MESURL, IsSbidQuery,
							"IsSbidQuery");
					MyApplication.nextEditFocus(yimei_mozu_proNum_edt);
				}
				if (tag.equals("模组批次号")) { // 批次号
					Log.i("id", "批号");
					yimei_mozu_proNum_edt.setText(barcodeData);
					if (yimei_mozu_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_mozu_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplication(), "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_mozu_user_edt);
						return;
					}
					if (yimei_mozu_sbid_edt.getText().toString().trim()
							.equals("")
							|| yimei_mozu_sbid_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplication(), "设备号不能为空", 0);
						MyApplication.nextEditFocus(yimei_mozu_sbid_edt);
						return;
					}
					if (yimei_mozu_proNum_edt.getText().toString().trim()
							.equals("")
							|| yimei_mozu_proNum_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(getApplication(), "批次号为空~", 0);
						MyApplication.nextEditFocus(yimei_mozu_proNum_edt);
						Log.i("foucus", "批次号回车");
						return;
					}
					picihao = yimei_mozu_proNum_edt.getText().toString().trim();
					Map<String, String> IsSbidQuery = MyApplication
							.IsSbidQuery_mozu(shebeihao,zcno);
					httpRequestQueryRecord(MyApplication.MESURL, IsSbidQuery,
							"IsSbidQuery1");
					yimei_mozu_proNum_edt.selectAll();

				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		setContentView(R.layout.activity_mozu);
		Application app = getApplication();
		myapp = (MyApplication) app;
		myapp.addActivity_(this);
		mozuActivity = this;
		myapp.removeActivity_(LoginActivity.loginActivity);// 销毁登录

		String cont;
		if(MyApplication.user.equals("admin")){
			cont="";
		}else{
			cont ="~sorg='"+MyApplication.sorg+"'";
		}
		httpRequestQueryRecord(MyApplication.MESURL,
				MyApplication.QueryBatNo("M_PROCESS",cont),"SpinnerValue");
		selectValue = (Spinner) findViewById(R.id.mozu_selectValue); // 下拉框id

		quanxuan = (CheckBox) findViewById(R.id.mozu_quanxuan); // 全选按钮
		listenerQuanXuan();
		mozu_shangliao = (Button) findViewById(R.id.mozu_shangliao); // 获取开工id
		mozu_shangliao.setVisibility(View.GONE); //隐藏上料按钮
		mozu_kaigong = (Button) findViewById(R.id.mozu_kaigong); // 获取开工id
		mozu_chuzhan = (Button) findViewById(R.id.mozu_chuzhan); // 获取开工id
		mozu_kaigong.setOnClickListener(kaigongClick); // 开工点击事件
		mozu_chuzhan.setOnClickListener(chuzhanClick); // 出站点击事件
		mozu_shangliao.setOnClickListener(shangliaoClick); // 上料点击事件
		if (mListView == null) {
			mozu_shangliao.setEnabled(false);
			mozu_kaigong.setEnabled(false);
			mozu_chuzhan.setEnabled(false);
		} else {
			mozu_shangliao.setEnabled(true);
			mozu_kaigong.setEnabled(true);
			mozu_chuzhan.setEnabled(true);
		}
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
	 * 全选方法
	 */
	protected void listenerQuanXuan() {
		quanxuan.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (MoZuAdapter == null) {
					ToastUtil.showToast(getApplicationContext(), "没有数据", 0);
					return;
				}
				if (isChecked) {
					int initCheck = MoZuAdapter.initCheck(true);
					if (initCheck == -1) {
						ToastUtil.showToast(getApplicationContext(), "没有数据", 0);
						quanxuan.setEnabled(false);
					}
					MoZuAdapter.notifyDataSetChanged();
				} else {
					int initCheck = MoZuAdapter.initCheck(false);
					if (initCheck == -1) {
						ToastUtil.showToast(getApplicationContext(), "没有数据", 0);
						quanxuan.setEnabled(false);
					}
					MoZuAdapter.notifyDataSetChanged();
				}
			}
		});

	}

	/**
	 * 开工的点击事件
	 */
	OnClickListener kaigongClick = new OnClickListener() {

		@SuppressWarnings("null")
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.mozu_kaigong) {
				UpdateServerData("kaigongUpdata");
			}
		}
	};

	/**
	 * 点击出站事件
	 */
	OnClickListener chuzhanClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.mozu_chuzhan) {
				UpdateServerData("chuzhanUpdata");
			}
		}
	};
	
	/**
	 * 上料点击事件
	 */
	OnClickListener shangliaoClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 查看有没有选中的数据
			HashMap<Integer, Boolean> state = MoZuAdapter.Getstate();
			if (state == null) {
				ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
			} else {
				int count = 0;
				mesPrecord mesObj = null;
				for (int j = 0; j < MoZuAdapter.getCount(); j++) {
					if (state.get(j)) {
						if (state.get(j) != null) {
							@SuppressWarnings("unchecked")
							// 取listview中的数据
							HashMap<String, Object> map = (HashMap<String, Object>) MoZuAdapter
									.getItem(j);
							mesPrecord m = (mesPrecord) map.get("mozu_item_title"); // 取选中工单号
							mesObj = m;
							count++;
						}
					}
				}
				switch (count) {
				case 0:
					ToastUtil.showToast(mozuActivity, "请选中一条记录!", 0);
					break;
				case 1:
					showPopupMenu(v, mesObj);
					break;
				default:
					// 多选
					ToastUtil.showToast(mozuActivity, "不可以多选!", 0);
					break;
				}
			}
		}
	};
	
	/**
	 * 选择上料验证方式
	 * 
	 * @param view
	 */
	private void showPopupMenu(View view, final mesPrecord m) {
		// View当前PopupMenu显示的相对View的位置
		PopupMenu popupMenu = new PopupMenu(this, view);

		// menu布局
		popupMenu.getMenuInflater().inflate(R.menu.shangliaoitem,
				popupMenu.getMenu());

		// menu的item点击事件
		popupMenu
				.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						String a = item.getTitle().toString();
						if (item.getTitle().equals("料号")) { // 料号
							JumpShangliao("料号", m);
						} else if (item.getTitle().equals("料号+批号")) { // 料号+批号
							JumpShangliao("料号+批号", m);
						} else if (item.getTitle().equals("料号+BinCode")) { // 料号+bincode
							JumpShangliao("料号+bincode", m);
						}
						return false;
					}
				});

		// PopupMenu关闭事件
		popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
			@Override
			public void onDismiss(PopupMenu menu) {
				// ToastUtil.showToast(gujingActivity, "关闭PopupMenu", 0);
			}
		});

		popupMenu.show();
	}
	
	/**
	 * 选择上料的类型需传的类型
	 */
	public void JumpShangliao(String jumpMes, mesPrecord m) {
		Intent intent = new Intent();
		intent.setClass(mozuActivity, ShangLiaoActivity.class);// 跳转到上料页面
		Bundle bundle = new Bundle();
		bundle.putString("activity", "Tongyong_mozu");
		bundle.putString("type", jumpMes); // 要传的类型
		bundle.putSerializable("object", m);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 获取选中的数据并且请求服务器
	 * 
	 * @param publicState
	 */
	public void UpdateServerData(String publicState) {
		HashMap<Integer, Boolean> state = MoZuAdapter.Getstate();

		if (state == null || state.equals(null)) {
			ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
		} else {
			int count = 0;
			for (int j = 0; j < MoZuAdapter.getCount(); j++) {
				if (state.get(j)) {
					if (state.get(j) != null) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> map = (HashMap<String, Object>) MoZuAdapter
								.getItem(j);

						if (updateListState == null) {
							updateListState = new ArrayList<mesPrecord>();
						}
						mesPrecord m = (mesPrecord) map.get("mozu_item_title");
						updateListState.add(m);
						count++;
					}
				}
			}
			switch (count) {
			case 0:
				ToastUtil.showToast(getApplicationContext(), "请选中一条数据", 0);
				break;
			default:
				for (int i = 0; i < updateListState.size(); i++) {
					mesPrecord mes_precord = updateListState.get(i);
					JSONObject json = (JSONObject) JSON.toJSON(mes_precord);
					// 如果是入站状态改变
					if (publicState.equals("kaigongUpdata")) {
						// 如果状态是入站和上料都可以开工
						if (json.get("state1").toString().equals("01")) {
							httpRequestQueryRecord(
									MyApplication.MESURL,     
									MyApplication.QueryBatNo("FIRCHCKQUERY", "~id='"
											+ shebeihao + "'"), "fircheckQuery");// 查询是否做过首检
							
							//===========================判断是否做了首检===============================================
							// 打了首检标示（fircheck：首件检）
							if (json.get("fircheck").toString().equals("1")) {
								String a = json.get("prd_no").toString();
								String a1 = EQIRP_prdno;
								if(!json.get("prd_no").toString().equals(EQIRP_prdno)){ //判断当前机型和设备中的机型是否匹配
									ToastUtil.showToast(mozuActivity,"【"+json.get("prd_no")+"】机型没有做首检（prdno）!",0);
									updateListState.clear();
									return;
								}
								if(EQIRP_firstchk.equals("0")||EQIRP_firstchk.equals("")){ //是否做过首检
									ToastUtil.showToast(mozuActivity,"【"+json.get("prd_no")+"】机型没有做首检（fircheck）!",0);
									updateListState.clear();
									return;
								}
							}
							//===========================判断是否做了首检===============================================
							
							
							Map<String, String> updateTimeMethod = MyApplication
									.updateServerTimeMethod(MyApplication.DBID,
											MyApplication.user,json.get("state1").toString(), "03",
											mes_precord.getSid(), zuoyeyuan,
											zcno, "202");

							httpRequestQueryRecord(MyApplication.MESURL,
									updateTimeMethod, publicState);
							
							// 修改本表
							Map<String, String> updateServerTable = MyApplication
									.UpdateServerTableMethod(
											MyApplication.DBID,
											MyApplication.user,json.get("state1").toString(), "03",
											mes_precord.getSid1(), currSlkid, zcno,
											"200");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateServerTable, "updateServerTable");
						} else if (json.get("state1").toString().equals("02")) {
							Map<String, String> updateTimeMethod = MyApplication
									.updateServerTimeMethod(MyApplication.DBID,
											MyApplication.user, json.get("state1").toString(), "03",
											mes_precord.getSid(), zuoyeyuan,
											zcno, "202");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateTimeMethod, publicState);
							
							// 修改本表
							Map<String, String> updateServerTable = MyApplication
									.UpdateServerTableMethod(
											MyApplication.DBID,
											MyApplication.user,json.get("state1").toString(), "03",
											mes_precord.getSid1(),json.get("slkid").toString(), zcno,
											"200");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateServerTable, "updateServerTable");
						} else if (json.get("state1").toString().equals("03")) {
							ToastUtil.showToast(getApplicationContext(),
									"选中的批次已是开工状态！", 0);
						}
					} else if (publicState.equals("chuzhanUpdata")) {
						if (json.get("state1").toString().equals("03")) {
							Map<String, String> updateTimeMethod = MyApplication
									.updateServerTimeMethod(MyApplication.DBID,
											MyApplication.user,json.get("state1").toString(), "04",
											mes_precord.getSid(), zuoyeyuan,
											zcno, "202");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateTimeMethod, publicState);
							
							// 修改本表
							Map<String, String> updateServerTable = MyApplication
									.UpdateServerTableMethod(
											MyApplication.DBID,
											MyApplication.user,json.get("state1").toString(), "04",
											mes_precord.getSid1(),json.get("slkid").toString(), zcno,
											"200");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateServerTable, "updateServerTable");
						} else if (json.get("state1").toString().equals("02")) {
							ToastUtil.showToast(getApplicationContext(),
									"选中的批次不能出站！", 0);
						} else if (json.get("state1").toString().equals("01")) {
							ToastUtil.showToast(getApplicationContext(),
									"选中的批次不能出站！", 0);
						}
					}

				}
				break;
			}
		}
	}

	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_mozu_user_edt) {
				if (!hasFocus) {
					zuoyeyuan = yimei_mozu_user_edt.getText().toString().trim();
				} else {
					yimei_mozu_user_edt.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_mozu_sbid_edt) {
				if (hasFocus) {
					Log.i("foucus", "设备号获取焦点");
					yimei_mozu_sbid_edt.setSelectAllOnFocus(true);
				} else {
					shebeihao = yimei_mozu_sbid_edt.getText().toString()
							.toUpperCase().trim();
					yimei_mozu_sbid_edt.setText(shebeihao);
				}

			}
			if (v.getId() == R.id.yimei_mozu_proNum_edt) {
				if (hasFocus) {
					yimei_mozu_proNum_edt.setSelectAllOnFocus(true);
				}
			}
		}
	};

	/**
	 * 回车事件
	 */
	OnEditorActionListener editEnter = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean flag = false;
			if (v.getId() == R.id.yimei_mozu_user_edt) { // 作业员
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_mozu_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_mozu_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplication(), "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_mozu_user_edt);
						return false;
					}
					zuoyeyuan = yimei_mozu_user_edt.getText().toString().trim();
					MyApplication.nextEditFocus(yimei_mozu_sbid_edt);
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_mozu_sbid_edt) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					shebeihao = yimei_mozu_sbid_edt.getText().toString()
							.toUpperCase().trim();
					yimei_mozu_sbid_edt.setText(shebeihao);
					if (yimei_mozu_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_mozu_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplication(), "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_mozu_user_edt);
						return false;
					}
					if (yimei_mozu_sbid_edt.getText().toString().trim()
							.equals("")
							|| yimei_mozu_sbid_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplication(), "设备号不能为空", 0);
						MyApplication.nextEditFocus(yimei_mozu_sbid_edt);
						return false;
					}
					Map<String, String> IsSbidQuery = MyApplication
							.IsSbidQuery_mozu(shebeihao,zcno);
					httpRequestQueryRecord(MyApplication.MESURL, IsSbidQuery,
							"IsSbidQuery");
					MyApplication.nextEditFocus(yimei_mozu_proNum_edt);
					flag = true;
				}
			}
			// 生产批号的回车事件

			if (v.getId() == R.id.yimei_mozu_proNum_edt) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					picihao = yimei_mozu_proNum_edt.getText().toString().trim();
					if (yimei_mozu_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_mozu_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplication(), "作业员不能为空", 0);
						MyApplication.nextEditFocus(yimei_mozu_user_edt);
						return false;
					}
					if (yimei_mozu_sbid_edt.getText().toString().trim()
							.equals("")
							|| yimei_mozu_sbid_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(getApplication(), "设备号不能为空", 0);
						MyApplication.nextEditFocus(yimei_mozu_sbid_edt);
						return false;
					}
					if (yimei_mozu_proNum_edt.getText().toString().trim()
							.equals("")
							|| yimei_mozu_proNum_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(getApplication(), "批次号为空~", 0);
						MyApplication.nextEditFocus(yimei_mozu_proNum_edt);
						Log.i("foucus", "批次号回车");
						return false;
					}
					Map<String, String> IsSbidQuery = MyApplication
							.IsSbidQuery_mozu(shebeihao,zcno);
					httpRequestQueryRecord(MyApplication.MESURL, IsSbidQuery,
							"IsSbidQuery1");
					MyApplication.nextEditFocus(yimei_mozu_proNum_edt);
					flag = true;
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

	/**
	 * 接收http请求返回值
	 */
	@SuppressLint("HandlerLeak")
	final android.os.Handler handler = new android.os.Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle b = msg.getData();
			String string = b.getString("type");
			try {
				if (string.equals("SpinnerValue")) { // 给下拉框赋值
					JSONObject jsonObject = JSON.parseObject(b
							.getString("jsonObj").toString());
					if(Integer.parseInt(jsonObject.get("code").toString()) == 0){
						ToastUtil.showToast(getApplicationContext(),"没有查到制程号~",0);
						return;
					}else{
						SetSelectValue(jsonObject);
						System.out.println(jsonObject);
					}
				}
				if (string.equals("IsSbidQuery")) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { // 没有该设备号
						Log.i("code", jsonObject.get("code").toString());
						if (mListView != null) {
							mListView.setAdapter(null);
							ToastUtil.showToast(getApplicationContext(),
									"请检查设备号和制程是否正确!", 0);
							MyApplication.nextEditFocus(yimei_mozu_sbid_edt);
						} else {
							ToastUtil.showToast(getApplicationContext(),
									"请检查设备号和制程是否正确!", 0);

							MyApplication.nextEditFocus(yimei_mozu_sbid_edt);
						}
					} else {
						// 去服务器中拿设备号
						Map<String, String> map = new HashMap<String, String>();
						map.put("dbid", MyApplication.DBID);
						map.put("usercode", MyApplication.user);
						map.put("apiId", "assist");
						map.put("assistid", "{MSBMOLIST}");
						map.put("cont", "~sbid='" + shebeihao + "' and zcno='"
								+ zcno + "'");
						httpRequestQueryRecord(MyApplication.MESURL, map,
								"shebeiQuery");
						
						httpRequestQueryRecord(
								MyApplication.MESURL,     
								MyApplication.QueryBatNo("FIRCHCKQUERY", "~id='"
										+ shebeihao + "'"), "fircheckQuery");// 查询是否做过首检
					}
				}
				if (string.equals("fircheckQuery")) { // 首检
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject.get("values")).get(0));
						EQIRP_prdno = jsonValue.get("prdno").toString();
						EQIRP_firstchk =  jsonValue.get("firstchk").toString();
					} else {
						ToastUtil.showToast(mozuActivity, "没有【" + shebeihao+ "】", 0);
					}
				}
				if (string.equals("IsSbidQuery1")) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());

					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { // 没有该设备号
						Log.i("code", jsonObject.get("code").toString());
						if (mListView != null) {
							mListView.setAdapter(null);
							ToastUtil.showToast(getApplicationContext(), "没有该设备号!",0);
							InputHidden(); //隐藏键盘
							MyApplication.nextEditFocus(yimei_mozu_sbid_edt);
						} else {
							ToastUtil.showToast(getApplicationContext(), "没有该设备号!",0);
							InputHidden(); //隐藏键盘
							MyApplication.nextEditFocus(yimei_mozu_sbid_edt);
						}
						return;
					} else {
						// 查询批次号表是否存在批次
						Map<String, String> map = new HashMap<String, String>();
						map.put("dbid", MyApplication.DBID);
						map.put("usercode", MyApplication.user);
						map.put("apiId", "assist");
						map.put("assistid", "{MOZCLISTWEB}"); // 查模组辅助 
						map.put("cont", "~sid1='" + picihao + "' and zcno='" + zcno
								+ "'");
						httpRequestQueryRecord(MyApplication.MESURL, map,
								"ListViewIsLotNo");
						
						
					}
				}
				if ("ListViewIsLotNo".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
						// 如果有批号
						if (MoZuAdapter != null) {
							// 循环列表
							int count = 0;
							List<Map<String, Object>> ListMesPitch = MoZuAdapter.listData;
							for (int i = 0; i < ListMesPitch.size(); i++) {
								Map<String, Object> map = ListMesPitch.get(i);
								if (map.get("sid1").equals(picihao)) {
									count++;
									MoZuAdapter.state.put(i, true);
								}
							}
							HashMap<Integer, Boolean> a = MoZuAdapter.Getstate();
							if (count > 0) {
								MoZuAdapter.notifyDataSetChanged();
								ToastUtil.showToast(getApplicationContext(), "《"
										+ picihao + "》测试号存在,已经帮你选中", 0);
								InputHidden(); //隐藏键盘
							} else {
								// 去服务器拿
								// 查询制程和批次是否存在
								Map<String, String> map = new HashMap<String, String>();
								map.put("dbid", MyApplication.DBID);
								map.put("usercode", MyApplication.user);
								map.put("apiId", "assist");
								map.put("assistid", "{MSBMOLIST}"); // 模组辅助
								map.put("cont", "~sid1='" + picihao
										+ "' and zcno='" + zcno + "'");
								httpRequestQueryRecord(MyApplication.MESURL, map,
										"ServerIsZcnoAndLotNo");
							}
						} else {
							// 去服务器拿
							// 查询制程和批次是否存在
							Map<String, String> map = new HashMap<String, String>();
							map.put("dbid", MyApplication.DBID);
							map.put("usercode", MyApplication.user);
							map.put("apiId", "assist");
							map.put("assistid", "{MSBMOLIST}"); // 查任务
							map.put("cont", "~sid1='" + picihao + "' and zcno='"
									+ zcno + "'");
							String a = picihao;
							String b2 = zcno;
							httpRequestQueryRecord(MyApplication.MESURL, map,
									"ServerIsZcnoAndLotNo");
						}
					} else {
						// 提示没有该批号
						ToastUtil.showToast(getApplication(), "没有该批次号！", 0);
						MyApplication.nextEditFocus(yimei_mozu_proNum_edt);
						InputHidden(); //隐藏键盘
					}
				}
				if ("ServerIsZcnoAndLotNo".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					// 如果有设备号 就绑定过 （提示：已经绑定过设备）
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
						ToastUtil.showToast(getApplication(), "《" + picihao
								+ "》测试号已绑定过设备号！", 0);
						InputHidden(); //隐藏键盘
					} else {
						// （json） 拿数据
						Map<String, String> map = new HashMap<String, String>();
						map.put("dbid", MyApplication.DBID);
						map.put("usercode", MyApplication.user);
						map.put("apiId", "assist");
						map.put("assistid", "{MOZCLISTWEB}"); 
						map.put("cont", "~sid1='" + picihao + "' and zcno='" + zcno
								+ "'");
						httpRequestQueryRecord(MyApplication.MESURL, map, "json");
					}
				}
				if ("json".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					// 判断设备号+制程在服务器中是否有数据
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
								.get("values")).get(0));
						if(SlkidMapJudge!=null){
							if (SlkidMapJudge.size() != 0) {
								for (int i = 0; i < SlkidMapJudge.size(); i++) {
									String slkid = SlkidMapJudge.get(i);
									if (!slkid.toUpperCase().equals(
											jsonValue.get("sid").toString()
													.toUpperCase())) {
										showNormalDialog(
												"（工单不符）",
												"\n当前工单为：【"
														+ SlkidMapJudge.get(i)
														+ "】\n"
														+ "扫描工单为:【"
														+ jsonValue.get("sid")
														+ "】\n不能入站，请先将当前批次出站后再入站生产!");
										return;
									}
								}
							}
						}
						
						if (Integer.parseInt(jsonValue.get("bok").toString()) == 0) {
							ToastUtil.showToast(MoZuActivity.this, "该批号不具备入站条件!",
									0);
							yimei_mozu_proNum_edt.selectAll();
							InputHidden(); //隐藏键盘
							return;
						} else if (jsonValue.get("state").toString()
								.equals("02")
								|| jsonValue.get("state").toString()
										.equals("03")) {
							ToastUtil.showToast(MoZuActivity.this, "该批号已经入站!", 0);
							yimei_mozu_proNum_edt.selectAll();
							InputHidden(); //隐藏键盘
							return;
						} else if (jsonValue.get("state").toString()
								.equals("04")) {
							ToastUtil.showToast(MoZuActivity.this, "该批号已经出站!", 0);
							yimei_mozu_proNum_edt.selectAll();
							InputHidden(); //隐藏键盘
							return;
						} else {
						// 如果有首检标示
						if (jsonValue.get("fircheck").toString().equals("1")) { // 打了首检标示（fircheck：首件检）
							if(!jsonValue.get("prd_no").toString().equals(EQIRP_prdno)){
								JumShouJianlDialog("首件检验", "【"+jsonValue.get("prd_no").toString()+"】机型没有做首检,请做首检!");
							}else{
								if(EQIRP_firstchk.equals("0")||EQIRP_firstchk.equals("")){
									JumShouJianlDialog("首件检验", "【"+jsonValue.get("prd_no").toString()+"】机型没有做首检,请做首检!");
								}
							}
						}
						SlkidMapJudge.add(jsonValue.get("sid").toString()); //不同工单禁止入站
						Log.i("SlkidMapJudge","入站"+SlkidMapJudge.toString());
						currSlkid = jsonValue.get("sid").toString(); // 修改服务器表的slkid
						sid1 = jsonValue.get("sid1").toString(); // 修改服务器表的sid1
						jsonValue.put("slkid", jsonValue.get("sid"));
						jsonValue.put("sid", "");
						jsonValue.put("state1", "01");
						jsonValue.put("firstchk",jsonValue.get("fircheck"));
						jsonValue.put("state", "0");
						jsonValue.put("prd_name", jsonValue.containsKey("prd_name")?jsonValue.get("prd_name"):"");
						jsonValue.put("dcid", GetAndroidMacUtil.getMac());
						jsonValue.put("op", zuoyeyuan);
						jsonValue.put("sys_stated", "3");
						jsonValue.put("sbid", shebeihao);
						jsonValue.put("zcno", zcno);
						jsonValue.put("zcno1",jsonValue.get("zcno1"));
						jsonValue.put("smake", MyApplication.user);
						jsonValue.put("mkdate",MyApplication.GetServerNowTime());
						jsonValue.put("sbuid", "D0001");
						newJson = jsonValue;
						Map<String, String> mesIdMap = MyApplication
								.httpMapKeyValueMethod(MyApplication.DBID,
										"savedata", MyApplication.user,
										jsonValue.toJSONString(), "D0001WEB", "1");
						httpRequestQueryRecord(MyApplication.MESURL, mesIdMap, "id");
						}
					}
				}
				if (string.equals("id")) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					JSONObject jsondata = (JSONObject) jsonObject.get("data");
					String newsid = jsondata.get("sid").toString(); // 拿到返回的sib1
					if (newsid != "") {
						
						// ----------------------------------------入站
						Map<String, String> updateServerTable = MyApplication
								.UpdateServerTableMethod(MyApplication.DBID,
										MyApplication.user, "00", "01", sid1,
										currSlkid, zcno, "200");
						httpRequestQueryRecord(MyApplication.MESURL,
								updateServerTable, "updateServerTable");
						// ----------------------------------------入站

						// 去服务器中拿设备号
						Map<String, String> map = new HashMap<String, String>();
						map.put("dbid", MyApplication.DBID);
						map.put("usercode", MyApplication.user);
						map.put("apiId", "assist");
						map.put("assistid", "{MSBMOLIST}"); // 设备任务列表
						map.put("cont", "~sbid='" + shebeihao + "' and zcno='"
								+ zcno + "'");
						httpRequestQueryRecord(MyApplication.MESURL, map,
								"shebeiQuery1");
					}
				}

				if ("updateServerTable".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					String j = jsonObject.toString();
				}
				if ("shebeiQuery1".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					// 判断设备号+制程在服务器中是否有数据
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {

						List<Map<String, Object>> mesList = QueryList(jsonObject); // 刷新列表
						MoZuAdapter = new MoZuAdapter(MoZuActivity.this, mesList);
						mListView.setAdapter(MoZuAdapter);
						ToastUtil.showToast(getApplicationContext(), "《" + picihao
								+ "》测试号加载到列表中~", 0);
						if (mListView == null) {
							mozu_shangliao.setEnabled(false);
							mozu_kaigong.setEnabled(false);
							mozu_chuzhan.setEnabled(false);
						} else {
							mozu_shangliao.setEnabled(true);
							mozu_kaigong.setEnabled(true);
							mozu_chuzhan.setEnabled(true);
						}
					} else {
						if (mListView != null) {
							mListView.setAdapter(null);
							mListView = null;
							MoZuAdapter.notifyDataSetChanged();
							MyApplication.nextEditFocus(yimei_mozu_proNum_edt);
						}
						if (mListView == null) {
							mozu_shangliao.setEnabled(false);
							mozu_kaigong.setEnabled(false);
							mozu_chuzhan.setEnabled(false);
						} else {
							mozu_shangliao.setEnabled(true);
							mozu_kaigong.setEnabled(true);
							mozu_chuzhan.setEnabled(true);
						}
					}
				}
				if ("shebeiQuery".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					if (SlkidMapJudge != null) { // 清空存在工单的集合
						if (SlkidMapJudge.size() != 0) {
							SlkidMapJudge.clear();
						}
					}
					// 判断设备号+制程在服务器中是否有数据
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {

						List<Map<String, Object>> mesList = QueryList(jsonObject); // 刷新列表
						MoZuAdapter = new MoZuAdapter(MoZuActivity.this, mesList);
						mListView.setAdapter(MoZuAdapter);
						ToastUtil.showToast(getApplicationContext(), "《"
								+ shebeihao + "》设备号已加载到列表中", 0);
						if (mListView == null) {
							mozu_shangliao.setEnabled(false);
							mozu_kaigong.setEnabled(false);
							mozu_chuzhan.setEnabled(false);
						} else {
							mozu_shangliao.setEnabled(true);
							mozu_kaigong.setEnabled(true);
							mozu_chuzhan.setEnabled(true);
						}
					} else {
						// 清空列表
						// ToastUtil.showToast(getApplicationContext(), "该设备没有记录",
						// 0);
						if (mListView != null) {
							mozu_shangliao.setEnabled(true);
							mozu_kaigong.setEnabled(true);
							mozu_chuzhan.setEnabled(true);
							mListView.setAdapter(null);
							mListView = null;

							MyApplication.nextEditFocus(yimei_mozu_proNum_edt);
						} else {
							mozu_shangliao.setEnabled(false);
							mozu_kaigong.setEnabled(false);
							mozu_chuzhan.setEnabled(false);
						}
					}
				}
				if ("kaigongUpdata".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					if (Integer.parseInt(jsonObject.get("id").toString()) == 0
							|| Integer.parseInt(jsonObject.get("id").toString()) == 1) {
						updateListState.clear();
						// 刷新列表
						// 去服务器中拿设备号
						Map<String, String> map = new HashMap<String, String>();
						map.put("dbid", MyApplication.DBID);
						map.put("usercode", MyApplication.user);
						map.put("apiId", "assist");
						map.put("assistid", "{MSBMOLIST}"); // 设备任务列表
						map.put("cont", "~sbid='" + shebeihao + "' and zcno='"
								+ zcno + "'");
						httpRequestQueryRecord(MyApplication.MESURL, map,
								"updateRefresh");
					} else {
						ToastUtil.showToast(mozuActivity,
								String.valueOf(jsonObject.get("message")), 0);
					}
				}
				if (string.equals("chuzhanUpdata")) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					if (Integer.parseInt(jsonObject.get("id").toString()) == 0
							|| Integer.parseInt(jsonObject.get("id").toString()) == 1) {
						if (SlkidMapJudge != null) {
							if (SlkidMapJudge.size() != 0) { // 如果集合中还有值就删除第一个下标，所有的批次号出站后也就为空了
								try {
									SlkidMapJudge.remove(0);
								} catch (Exception e) {
									SlkidMapJudge.clear();
								}
							}
						}
						updateListState.clear();
						// 刷新列表
						// 去服务器中拿设备号
						Map<String, String> map = new HashMap<String, String>();
						map.put("dbid", MyApplication.DBID);
						map.put("usercode", MyApplication.user);
						map.put("apiId", "assist");
						map.put("assistid", "{MSBMOLIST}"); // 设备任务列表
						map.put("cont", "~sbid='" + shebeihao + "' and zcno='"
								+ zcno + "'");
						httpRequestQueryRecord(MyApplication.MESURL, map,
								"updateRefresh");
					}
				}
				if ("updateRefresh".equals(string)) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj")
							.toString());
					// 判断设备号+制程在服务器中是否有数据
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1
							|| Integer.parseInt(jsonObject.get("code").toString()) == 0) {

						List<Map<String, Object>> mesList = QueryList(jsonObject); // 刷新列表
					if (mesList != null) {
							MoZuAdapter = new MoZuAdapter(mozuActivity, mesList);
							mListView.setAdapter(MoZuAdapter);
						return;
					} else {
						MoZuAdapter = null;
						mListView.setAdapter(null);
//						MoZuAdapter.notifyDataSetChanged();
					}
					}
				}
			} catch (Exception e) {
				ToastUtil.showToast(MoZuActivity.this,e.toString(),0);
			}
		}
	};
	
	/**
	 * 跳转首检界面
	 * @param msg
	 */
	private void JumShouJianlDialog(String title,String msg) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				MoZuActivity.this);
		normalDialog.setTitle(title);
		normalDialog.setMessage(Html.fromHtml("<font color='red'>" + msg
				+ "</font>"));
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClass(MoZuActivity.this,IPQC_shoujian.class);// 跳转到首检
						// 利用bundle来存取数据
						Bundle bundle = new Bundle();
						bundle.putString("json",newJson.toString());
						// 再把bundle中的数据传给intent，以传输过去
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
//		normalDialog.setNegativeButton("取消", null);		// 显示
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
				MoZuActivity.this,
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

	
	/**
	 * 刷新列表（设备和批号）
	 */
	private List<Map<String, Object>> QueryList(JSONObject jsonobject) {
		List<Map<String, Object>> mesList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mesMap = null;
		MoZuCHScrollView headerScroll = (MoZuCHScrollView) findViewById(R.id.mozu_scroll_title);
		MoZuCHScrollViews.add(headerScroll);
		mListView = (ListView) findViewById(R.id.mozu_scroll_list);
		Map<String, String> stateName = MyApplication.getStateName();
		if (SlkidMapJudge != null) { // 清空存在工单的集合
			if (SlkidMapJudge.size() != 0) {
				SlkidMapJudge.clear();
			}
		}
		if ((JSONArray) jsonobject.get("values") == null) {
			return null;
		} else {

			for (int i = 0; i < ((JSONArray) jsonobject.get("values")).size(); i++) {
				JSONObject jsonValue = (JSONObject) (((JSONArray) jsonobject
						.get("values")).get(i));
				SlkidMapJudge.add(jsonValue.get("slkid").toString());
				mesMap = new HashMap<String, Object>();
				mesPrecord new_mes = jsonValue.toJavaObject(mesPrecord.class);
				Log.i("newmes", new_mes.toString());
				mesMap.put("mozu_item_title", new_mes);
				mesMap.put("sid1", new_mes.getSid1());
				mesMap.put("sid", new_mes.getSlkid());
				mesMap.put("prd_name",jsonValue.get("prd_no"));
				mesMap.put("qty", new_mes.getQty());
				mesMap.put("state", stateName.get(new_mes.getState1()));
				mesMap.put("xianbie", new_mes.getFiles());
				mesList.add(mesMap);
			}
			
			Log.i("SlkidMapJudge","刷新"+SlkidMapJudge.toString());
		}
		return mesList;
	}

	/**
	 * http请求
	 * 
	 * @param baseUrl
	 * @param map
	 */
	public void httpRequestQueryRecord(final String baseUrl,
			final Map<String, String> map, final String type) {
		new Thread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				String httpPost = HttpUtil.httpPost(baseUrl, map);
				Bundle b = new Bundle();
				b.putString("jsonObj", httpPost);
				b.putString("type", type);
				Message m = new Message();
				m.setData(b);
				handler.sendMessage(m);
			}
		}).start();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		
		if(SlkidMapJudge.size()!=0){
			MyApplication.nextEditFocus(yimei_mozu_proNum_edt);
			InputHidden();
			yimei_mozu_proNum_edt.selectAll();
		}
		
		yimei_mozu_user_edt = (EditText) findViewById(R.id.yimei_mozu_user_edt);
		yimei_mozu_sbid_edt = (EditText) findViewById(R.id.yimei_mozu_sbid_edt);
		yimei_mozu_proNum_edt = (EditText) findViewById(R.id.yimei_mozu_proNum_edt);

		yimei_mozu_user_edt.setOnEditorActionListener(editEnter);
		yimei_mozu_sbid_edt.setOnEditorActionListener(editEnter);
		yimei_mozu_proNum_edt.setOnEditorActionListener(editEnter);

		yimei_mozu_user_edt.setOnFocusChangeListener(EditGetFocus);
		yimei_mozu_sbid_edt.setOnFocusChangeListener(EditGetFocus);
		yimei_mozu_proNum_edt.setOnFocusChangeListener(EditGetFocus);

	}
	
	private void showNormalDialog(String Title, String msg) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				MoZuActivity.this);
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

	public static void addHViews(final MoZuCHScrollView hScrollView) {
		if (!MoZuCHScrollViews.isEmpty()) {
			int size = MoZuCHScrollViews.size();
			MoZuCHScrollView scrollView = MoZuCHScrollViews.get(size - 1);
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
		MoZuCHScrollViews.add(hScrollView);
	}

	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		for (MoZuCHScrollView scrollView : MoZuCHScrollViews) {
			if (mTouchView != scrollView)
				scrollView.smoothScrollTo(l, t);
		}
	}

	/**
	 * 获取下框的值
	 */
	

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
