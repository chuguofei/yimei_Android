package com.yimei.activity;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.ipqc.IPQC_shoujian;
import com.yimei.adapter.ScrollAdapter;
import com.yimei.entity.Pair;
import com.yimei.entity.mesPrecord;
import com.yimei.scrollview.CHScrollView;
import com.yimei.sqlliteUtil.mesAllMethod;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.HttpUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

/**
 * 200修改mes_precord
 * 202修改mes_lot_plana
 * QJ_ZCNO（器件的制程）: 查询器件的制程
 * @author Administrator
 * PPA和EMC3030
 * MES再添加一个3030制程的流程，具体流程：plasma、固晶、烘烤、plasma、焊线、plasma、点胶、烘烤、外观、测试、编带、看带、包装
 */
public class TongYongActivity extends Activity {

	static MyApplication myapp;
	public static TongYongActivity gujingActivity;
	// 操作sqliet
	private mesAllMethod gujing_list = new mesAllMethod(TongYongActivity.this); //调用操作本地库的方法
	private static ScrollAdapter scrollAdapter; // 适配器
	private static ListView mListView; // listview
	public HorizontalScrollView mTouchView;
	private static List<CHScrollView> mHScrollViews = new ArrayList<CHScrollView>(); // 行+标题滚动
	private CheckBox quanxuan; // 全选按钮
	private EditText yimei_equipment_edt;
	private EditText yimei_user_edt;
	private EditText yimei_proNum_edt;
	private EditText yimei_tongyong_oldMbox;
	private EditText yimei_tongyong_newMbox;
	private TextView oldMboxText, newMboxText;
	private LinearLayout mboxTab;
	private Spinner selectValue; // 下拉框
	private String zcno = "11";// 界面上的制程号
	private Button kaigong;// 开工按钮
	private Button chuzhan;// 出站按钮
	private String EQIRP_prdno; // 判断是否做首检的机型号
	private String EQIRP_firstchk; // 判断是否做首检的标示
	private Button shangliao; // 上料按钮

	// 获取4个文本框的文本
	private String zuoyeyuan; // 界面上的作业员(全局)
	private String shebeihao; // 界面上的设备号(全局)
	private String yimei_pro_edt; // 界面上的批次号（全局）
	private String qtyv; // 批次数量

	private static JSONObject newJson; // 拿新sid存在json
	private static String currSlkid;
	private static String fircheck_PRDNO = ""; // 首检的机种名称（换机种要做首检）
	private List<mesPrecord> updatekaigongSid1; // 修改服务器的2张表的状态（出站，开工）,更改本地库的状态
	private Map<String, String> ptime = new HashMap<String, String>();  //获取出站时间
	// private HashMap<Integer, String> SlkidMapJudge = new HashMap<>(); //
	// 不同工单不能入站
	private ArrayList<String> SlkidMapJudge = new ArrayList<String>(); // 不同工单不能入站
	private boolean IsMbox_old = true;
	private boolean IsMbox_new = true;
	private String MboxFocur_old = "";
	private String MboxFocur_new = "";
	private String IsMboxMess_old = "";
	private String IsMboxMess_new = "";
	private String [] glue;  //烤箱需要绑定的胶
	private String [] stents; //烤箱需要绑定的支架
	
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
				if (tag.equals("通用作业员")) {
					Log.i("id", "作业员");
					yimei_user_edt.setText(barcodeData);
					if (yimei_user_edt.getText().toString().trim().equals("")
							|| yimei_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_user_edt));
						return;
					}
					nextEditFocus((EditText) findViewById(R.id.yimei_equipment_edt));
				}
				if (tag.equals("通用设备号")) {
					Log.i("id", "设备");
					yimei_equipment_edt.setText(barcodeData);
					if (yimei_user_edt.getText().toString().trim().equals("")
							|| yimei_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_user_edt));
						return;
					}
					if (yimei_equipment_edt.getText().toString().trim()
							.equals("")
							|| yimei_equipment_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "设备号不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_equipment_edt));
						return;
					}
					sbidGetData(); // 设备号的回车键
//					nextEditFocus((EditText) findViewById(R.id.yimei_proNum_edt));
				}
				if (tag.equals("旧料盒号")) {
					yimei_tongyong_oldMbox.setText(barcodeData);
					if (yimei_user_edt.getText().toString().trim().equals("")
							|| yimei_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_user_edt));
						return;
					}
					if (yimei_equipment_edt.getText().toString().trim()
							.equals("")
							|| yimei_equipment_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "设备号不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_equipment_edt));
						return;
					}
					if (yimei_tongyong_oldMbox.getText().toString().trim()
							.equals("")
							|| yimei_tongyong_oldMbox.getText().toString()
									.trim() == null) {
						if (zcno.equals("11")) {
							ToastUtil
									.showToast(gujingActivity, "固晶料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return;
						} else if (zcno.equals("21")) {
							ToastUtil
									.showToast(gujingActivity, "固晶料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return;
						} else if (zcno.equals("31")) {
							ToastUtil
									.showToast(gujingActivity, "焊接料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return;
						}
					}
					Map<String, String> queryMBox = MyApplication.QueryBatNo(
							"MBOXQUERY", "~id='"
									+ yimei_tongyong_oldMbox.getText()
											.toString().toUpperCase().trim()
									+ "'");
					httpRequestQueryRecord(MyApplication.MESURL, queryMBox,
							"Old_QueryMboX");
				}
				if (tag.equals("新料盒号")) {
					yimei_tongyong_newMbox.setText(barcodeData);
					if (yimei_user_edt.getText().toString().trim().equals("")
							|| yimei_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_user_edt));
						return;
					}
					if (yimei_equipment_edt.getText().toString().trim()
							.equals("")
							|| yimei_equipment_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "设备号不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_equipment_edt));
						return;
					}
					// 旧料盒
					if (yimei_tongyong_oldMbox.getText().toString().trim()
							.equals("")
							|| yimei_tongyong_oldMbox.getText().toString()
									.trim() == null) {
						if (zcno.equals("11")) {
							ToastUtil
									.showToast(gujingActivity, "固晶料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return;
						} else if (zcno.equals("21")) {
							ToastUtil
									.showToast(gujingActivity, "固晶料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return;
						} else if (zcno.equals("31")) {
							ToastUtil
									.showToast(gujingActivity, "焊接料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return;
						}
					}
					// 新料盒
					if (yimei_tongyong_newMbox.getText().toString().trim()
							.equals("")
							|| yimei_tongyong_newMbox.getText().toString()
									.trim() == null) {
						if (zcno.equals("21")) {
							ToastUtil
									.showToast(gujingActivity, "焊接料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_newMbox);
							return;
						} else if (zcno.equals("31")) {
							ToastUtil
									.showToast(gujingActivity, "点胶料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_newMbox);
							return;
						}
					}
					Map<String, String> queryMBox = MyApplication.QueryBatNo(
							"MBOXQUERY", "~id='"
									+ yimei_tongyong_newMbox.getText()
											.toString().toUpperCase().trim()
									+ "'");
					httpRequestQueryRecord(MyApplication.MESURL, queryMBox,
							"New_QueryMboX");
				}
				if (tag.equals("通用批次号")) {
					yimei_proNum_edt.setText(barcodeData);
					if (yimei_user_edt.getText().toString().trim().equals("")
							|| yimei_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_user_edt));
						return;
					}
					if (yimei_equipment_edt.getText().toString().trim()
							.equals("")
							|| yimei_equipment_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "设备号不能为空~", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_equipment_edt));
						return;
					}
					if (yimei_proNum_edt.getText().toString().trim().equals("")
							|| yimei_proNum_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "批次号为空~", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_proNum_edt));
						return;
					}
					sid1GetData(); // 生产批号回车事件
//					nextEditFocus((EditText) findViewById(R.id.yimei_proNum_edt));
//					yimei_proNum_edt.selectAll();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tongyong);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		Application app = getApplication();
		myapp = (MyApplication) app;
		myapp.addActivity_(this);
		gujingActivity = this;
		myapp.removeActivity_(LoginActivity.loginActivity);// 销毁登录
		selectValue = (Spinner) findViewById(R.id.selectValue); // 下拉框id
//		selectValue.setOnItemSelectedListener(SelectValueListener); // 下拉框改变更新值
		quanxuan = (CheckBox) findViewById(R.id.quanxuan); // 全选按钮
		
		Map<String, String> map1 = MyApplication.QueryBatNo("QJ_ZCNO", "");
		httpRequestQueryRecord(MyApplication.MESURL, map1, "SpinnerValue");
		
		
		listenerQuanXuan(); // 全选事件
		mListView = (ListView) findViewById(R.id.scroll_list);
		shangliao = (Button) findViewById(R.id.ruliao); // 获取上料id
		kaigong = (Button) findViewById(R.id.kaigong); // 获取开工id
		chuzhan = (Button) findViewById(R.id.chuzhan); // 获取出站id
		if (gujing_list.mesDataCount() == 0) { // 是否禁用(上料，开工，出站)按钮
			shangliao.setEnabled(false);
			kaigong.setEnabled(false);
			chuzhan.setEnabled(false);
		}
		kaigong.setOnClickListener(kaigongClick); // 开工点击事件
		chuzhan.setOnClickListener(chuzhanClick); // 出站点击事件
		shangliao.setOnClickListener(shangliaoClick); // 上料点击事件

		// 取出站的时间
		Map<String, String> map = MyApplication.QueryBatNo("M_PROCESS5", "");
		httpRequestQueryRecord(MyApplication.MESURL, map, "Tongyong_ptime");
	};

	/**
	 * 点击出站事件
	 */
	OnClickListener chuzhanClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.chuzhan) {
				UpdateServerData("chuzhanUpdata");
			}
		}
	};

	/**
	 * 开工的点击事件
	 */
	OnClickListener kaigongClick = new OnClickListener() {

		@SuppressWarnings("null")
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.kaigong) {
				UpdateServerData("kaigongUpdata");
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
			HashMap<Integer, Boolean> state = scrollAdapter.Getstate();
			if (state == null) {
				ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
			} else {
				int count = 0;
				mesPrecord mesObj = null;
				for (int j = 0; j < scrollAdapter.getCount(); j++) {
					if (state.get(j)) {
						if (state.get(j) != null) {
							@SuppressWarnings("unchecked")
							// 取listview中的数据
							HashMap<String, Object> map = (HashMap<String, Object>) scrollAdapter
									.getItem(j);
							mesPrecord m = (mesPrecord) map.get("checkMap"); // 取选中工单号
							mesObj = m;
							count++;
						}
					}
				}
				switch (count) {
				case 0:
					ToastUtil.showToast(gujingActivity, "请选中一条记录!", 0);
					break;
				case 1:
					showPopupMenu(v, mesObj);
					break;
				default:
					// 多选
					ToastUtil.showToast(gujingActivity, "不可以多选!", 0);
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
		intent.setClass(gujingActivity, ShangLiaoActivity.class);// 跳转到上料页面
		Bundle bundle = new Bundle();
		bundle.putString("activity", "tongyong");
		bundle.putString("type", jumpMes); // 要传的类型
		bundle.putSerializable("object", m);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 与界面交互
	 */
	protected void onResume() {
		super.onResume();

		List<Map<String, Object>> getListMes_Procord = GetListMes_Procord(
				shebeihao, zcno);
		if (getListMes_Procord != null) {
			yimei_equipment_edt.setFocusable(true);
			yimei_equipment_edt.setFocusableInTouchMode(true);
			yimei_equipment_edt.selectAll();
			scrollAdapter = new ScrollAdapter(TongYongActivity.this,
					getListMes_Procord);
			mListView.setAdapter(scrollAdapter);
			scrollAdapter.notifyDataSetChanged();
		}
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		yimei_user_edt = (EditText) findViewById(R.id.yimei_user_edt);
		yimei_proNum_edt = (EditText) findViewById(R.id.yimei_proNum_edt);
		yimei_equipment_edt = (EditText) findViewById(R.id.yimei_equipment_edt);
		yimei_tongyong_oldMbox = (EditText) findViewById(R.id.yimei_tongyong_oldMbox);
		yimei_tongyong_newMbox = (EditText) findViewById(R.id.yimei_tongyong_newMbox);
		oldMboxText = (TextView) findViewById(R.id.oldMboxText);
		newMboxText = (TextView) findViewById(R.id.newMboxText);
		mboxTab = (LinearLayout) findViewById(R.id.mboxTab);

		if(!yimei_user_edt.getText().equals("")){
			if(zcno.equals("11") || zcno.equals("21") || zcno.equals("31")){
				MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
			}else{
				MyApplication.nextEditFocus(yimei_proNum_edt);
			}
		}
		InputHidden();
		yimei_proNum_edt.selectAll();
		
		
		yimei_user_edt.setOnEditorActionListener(editEnter); // 操作员的回车监听事件
		yimei_equipment_edt.setOnEditorActionListener(editEnter); // 设备的回车监听事件
		yimei_proNum_edt.setOnEditorActionListener(editEnter);// 生产批号的回车监听事件
		yimei_tongyong_oldMbox.setOnEditorActionListener(editEnter);// 生产批号的回车监听事件
		yimei_tongyong_newMbox.setOnEditorActionListener(editEnter);// 生产批号的回车监听事件

		yimei_user_edt.setOnFocusChangeListener(EditGetFocus);// 操作员失去焦点
		yimei_equipment_edt.setOnFocusChangeListener(EditGetFocus);// 设备号失去焦点
		yimei_proNum_edt.setOnFocusChangeListener(EditGetFocus);// 批号失去焦点
		yimei_tongyong_newMbox.setOnFocusChangeListener(EditGetFocus);// 批号失去焦点
		yimei_tongyong_oldMbox.setOnFocusChangeListener(EditGetFocus);// 批号失去焦点
	};

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
	 * 按设备号获取记录
	 */
	public void sbidGetData() {
		// 获取设备号的文本
		shebeihao = yimei_equipment_edt.getText().toString().trim();
		Map<String, String> mapSbid = new HashMap<String, String>();
		mapSbid.put("dbid", MyApplication.DBID);
		mapSbid.put("usercode", MyApplication.user);
		mapSbid.put("apiId", "assist");
		mapSbid.put("assistid", "{MESEQUTM}");
		String zzcno = zcno;
		if(zcno.equals("12")||zcno.equals("13")){
			zzcno = "11";  
		}
		if(zcno.equals("1A")||zcno.equals("1B")){
			zzcno= "1A";
		}
		if(zcno.equals("41")){
			zzcno= "41";
		}
		mapSbid.put("cont", "~id='" + shebeihao + "' and zc_id='" + zzcno + "'"); //
		httpRequestQueryRecord(MyApplication.MESURL, mapSbid, "Isshebei");
	}

	/***
	 * 生产批号pda|失去焦点获取值
	 */
	public void sid1GetData() {

		Map<String, String> mapSbid = new HashMap<String, String>();
		mapSbid.put("dbid", MyApplication.DBID);
		mapSbid.put("usercode", MyApplication.user);
		mapSbid.put("apiId", "assist");
		mapSbid.put("assistid", "{MESEQUTM}");
		String zzcno = zcno;
		if(zcno.equals("12")||zcno.equals("13")){
			zzcno = "11";  
		}
		if(zcno.equals("1A")||zcno.equals("1B")){
			zzcno= "1A";
		}
		if(zcno.equals("41")){
			zzcno= "41";
		}
		mapSbid.put("cont", "~id='" + shebeihao + "' and zc_id='" + zzcno + "'"); //and zc_id='" + zcno + "' 
		httpRequestQueryRecord(MyApplication.MESURL, mapSbid, "Isshebei1");

		if (zcno.equals("11")) {
			Map<String, String> queryMBox = MyApplication.QueryBatNo(
					"MBOXQUERY", "~id='"
							+ yimei_tongyong_oldMbox.getText().toString()
									.toUpperCase().trim() + "'");
			httpRequestQueryRecord(MyApplication.MESURL, queryMBox,
					"Old_QueryMboX");
		} else if(zcno.equals("31") || zcno.equals("21")){
			Map<String, String> queryMBox1 = MyApplication.QueryBatNo(
					"MBOXQUERY", "~id='"
							+ yimei_tongyong_oldMbox.getText().toString()
									.toUpperCase().trim() + "'");
			httpRequestQueryRecord(MyApplication.MESURL, queryMBox1,
					"Old_QueryMboX");

			Map<String, String> queryMBox = MyApplication.QueryBatNo(
					"MBOXQUERY", "~id='"
							+ yimei_tongyong_newMbox.getText().toString()
									.toUpperCase().trim() + "'");
			httpRequestQueryRecord(MyApplication.MESURL, queryMBox,
					"New_QueryMboX");
		}
	}

	/**
	 * 接收http请求返回值
	 */
	@SuppressLint("HandlerLeak")
	final android.os.Handler handler = new android.os.Handler() {
		public void handleMessage(Message msg) {
			try {
				super.handleMessage(msg);
				Bundle b = msg.getData();
				JSONObject LoginTimeMess = JSON.parseObject(b.getString(
						"jsonObj").toString());
				if (LoginTimeMess.containsKey("message")) {
					if (LoginTimeMess.get("message").equals("请重新登录")) { // 超时登录
						LoginTimeout_dig("超时登录", "请重新登录!");
						return;
					}
				}
				String string = b.getString("type");
				if (string.equals("SpinnerValue")) { // 给下拉框赋值
					JSONObject jsonObject = JSON.parseObject(b
							.getString("jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
						ToastUtil.showToast(getApplicationContext(),
								"没有查到制程号~", 0);
						return;
					} else {
						SetSelectValue(jsonObject);
						System.out.println(jsonObject);
					}
				}
				if(string.equals("Query_Precord_chuzhan")){ //出站查询批次是否异常OA
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { // 没有料盒号
						ToastUtil.showToast(gujingActivity,"查询异常没有该批次",0);
					}else{
						JSONObject json = (JSONObject) (((JSONArray) jsonObject
								.get("values")).get(0));
						if (json.get("state1").toString().equals("03")) {
							if (!zcno.equals("41")&&!zcno.equals("1A")&&!zcno.equals("1B")) { // 烘烤站批量提交不走该线程
								Map<String, String> updateTimeMethod = MyApplication.updateServerTimeMethod(
												MyApplication.DBID,MyApplication.user,
												"03", "04",json.get("sid").toString(),
												zuoyeyuan, zcno, "202");  //传主键
								httpRequestQueryRecord(MyApplication.MESURL,updateTimeMethod,"chuzhanUpdata"); //出站修改状态
							}
						} else if (json.get("state1").toString().equals("02")) {
							ToastUtil.showToast(getApplicationContext(),"选中的【"+json.get("sid1").toString()+"】批次【上料】不能出站！", 0);
						} else if (json.get("state1").toString().equals("01")) {
							ToastUtil.showToast(getApplicationContext(),"选中的【"+json.get("sid1").toString()+"】批次【入站】不能出站！", 0);
						}else if (json.get("state1").toString().equals("0A")) {
							ToastUtil.showToast(getApplicationContext(),"选中的【"+json.get("sid1").toString()+"】批次状态【异常】不能出站！", 0);
						}else if (json.get("state1").toString().equals("0B")) {
							ToastUtil.showToast(getApplicationContext(),"选中的【"+json.get("sid1").toString()+"】批次状态【暂停】不能出站！", 0);
						}else if (json.get("state1").toString().equals("0C")) {
							ToastUtil.showToast(getApplicationContext(),"选中的【"+json.get("sid1").toString()+"】批次状态【中止】不能出站！", 0);
						}else if (json.get("state1").toString().equals("0D")) {
							ToastUtil.showToast(getApplicationContext(),"选中的【"+json.get("sid1").toString()+"】批次状态【受控】不能出站！", 0);
						}
					}
				}
				if (string.equals("TimeOutLogin")) { // 超时登录
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (jsonObject.getInteger("id") != 0) {
						LoginTimeout_dig("密码错误", "");
					}
				}
				if (string.equals("Old_QueryMboX")) { // 查询旧料盒号
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { // 没有料盒号
						IsMbox_old = false;
						if(!yimei_tongyong_oldMbox.getText().toString()
								.toUpperCase().trim().equals("")){
							IsMboxMess_old = "没有【"
									+ yimei_tongyong_oldMbox.getText().toString()
											.toUpperCase().trim() + "】料盒号";
							ToastUtil.showToast(gujingActivity, "没有【"
									+ yimei_tongyong_oldMbox.getText().toString()
											.toUpperCase().trim() + "】料盒号", 0);
						}
						MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
						yimei_tongyong_oldMbox.selectAll();
						InputHidden();
						return;
					} else {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
								.get("values")).get(0));
						if (Integer.parseInt(jsonValue.get("state").toString()) == 0) {
							if (zcno.equals("11")) {
								IsMbox_old = true;
								IsMboxMess_old = "";
								nextEditFocus((EditText) findViewById(R.id.yimei_proNum_edt));
							} else {
								IsMbox_old = true;
								IsMboxMess_old = "";
								nextEditFocus((EditText) findViewById(R.id.yimei_tongyong_newMbox));
							}
						} else {
							if (!zcno.equals("11")) {
								IsMbox_old = true;
								IsMboxMess_old = "";
								nextEditFocus((EditText) findViewById(R.id.yimei_tongyong_newMbox));
							} else {
								IsMbox_old = false;
								MboxFocur_old = "old";
								IsMboxMess_old = "【"
										+ yimei_tongyong_oldMbox.getText()
												.toString().toUpperCase()
												.trim() + "】料盒号没有解绑！";
								ToastUtil.showToast(gujingActivity, "【"
										+ yimei_tongyong_oldMbox.getText()
												.toString().toUpperCase()
												.trim() + "】料盒号没有解绑！", 0);
								MyApplication
										.nextEditFocus(yimei_tongyong_oldMbox);
								InputHidden();
								yimei_tongyong_oldMbox.selectAll();
							}
						}
					}
				}
				if (string.equals("New_QueryMboX")) { // 查询新料盒号
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { // 没有料盒号
						IsMbox_new = false;
						MboxFocur_new = "new";
						if(!yimei_tongyong_newMbox.getText().toString()
								.toUpperCase().trim().equals("")){
							IsMboxMess_new = "没有【"
									+ yimei_tongyong_newMbox.getText().toString()
											.toUpperCase().trim() + "】料盒号";
							ToastUtil.showToast(gujingActivity, "没有【"
									+ yimei_tongyong_newMbox.getText().toString()
											.toUpperCase().trim() + "】料盒号", 0);
						}
						MyApplication.nextEditFocus(yimei_tongyong_newMbox);
						yimei_tongyong_newMbox.selectAll();
						InputHidden();
						return;
					} else {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
								.get("values")).get(0));
						if (Integer.parseInt(jsonValue.get("state").toString()) == 0) {
							IsMbox_new = true;
							IsMboxMess_new = "";
							MyApplication.nextEditFocus(yimei_proNum_edt);
						} else {
							IsMbox_new = false;
							MboxFocur_new = "new";
							IsMboxMess_new = "【"
									+ yimei_tongyong_newMbox.getText()
											.toString().toUpperCase().trim()
									+ "】料盒号没有解绑！";
							ToastUtil.showToast(gujingActivity, "【"
									+ yimei_tongyong_newMbox.getText()
											.toString().toUpperCase().trim()
									+ "】料盒号没有解绑！", 0);
							InputHidden();
							MyApplication.nextEditFocus(yimei_tongyong_newMbox);
							yimei_tongyong_newMbox.selectAll();
						}
					}
				}
				if (string.equals("Isshebei1")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
						Log.i("code", jsonObject.get("code").toString());
						if (mListView != null) {
							mListView.setAdapter(null);
							if (scrollAdapter != null) {
								scrollAdapter.notifyDataSetChanged();
							}
							MyApplication.nextEditFocus(yimei_equipment_edt);
							yimei_equipment_edt.selectAll();
							ToastUtil.showToast(getApplicationContext(),"系统中没有维护该设备号!", 0);
							InputHidden();
						} else {
							MyApplication.nextEditFocus(yimei_equipment_edt);
							yimei_equipment_edt.selectAll();
							ToastUtil.showToast(getApplicationContext(),"系统中没有维护该设备号!", 0);
							InputHidden();
						}
					} else {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject.get("values")).get(0));
						if(Integer.parseInt(jsonValue.get("sbstate").toString()) != 0){
							ToastUtil.showToast(getApplicationContext(),"该设备处于报修状态，请联系工程!", 0);
							MyApplication.nextEditFocus(yimei_equipment_edt);
							yimei_equipment_edt.selectAll();
							InputHidden();
							return ;
						}
						//查询一个设备是否绑定多个制程
						if(jsonValue.get("zc_id").toString().indexOf(";") != -1){
							String[] split = jsonValue.get("zc_id").toString().split(";");
							boolean contains = Arrays.asList(split).contains(zcno);
							if(!contains){
								ToastUtil.showToast(getApplicationContext(),"设备号和工序不匹!", 0);
								MyApplication.nextEditFocus(yimei_equipment_edt);
								yimei_equipment_edt.selectAll();
								InputHidden();
								return;
							}
						}
						/*else{
							if(!jsonValue.get("zc_id").toString().equals(zcno)){
								ToastUtil.showToast(getApplicationContext(),"设备号和工序不匹!", 0);
								MyApplication.nextEditFocus(yimei_equipment_edt);
								yimei_equipment_edt.selectAll();
								InputHidden();
								return;
							}
						}*/
						if (!zcno.equals("41")) {
							if (!IsMbox_old) {
								ToastUtil.showToast(gujingActivity,
										IsMboxMess_old, 0);
								MyApplication
										.nextEditFocus(yimei_tongyong_oldMbox);
								yimei_tongyong_oldMbox.selectAll();
								InputHidden();
								return;
							}
							if (!IsMbox_new) {
								ToastUtil.showToast(gujingActivity,
										IsMboxMess_new, 0);
								MyApplication
										.nextEditFocus(yimei_tongyong_newMbox);
								yimei_tongyong_newMbox.selectAll();
								InputHidden();
								return;
							}
						}
						yimei_pro_edt = yimei_proNum_edt.getText().toString().trim();
						// 如果批次号+制程在库中存在
						if (gujing_list.IsSid1AndZnco(yimei_pro_edt, zcno)) {
							// 如果批次+设备+制程 在库中存在
							if (gujing_list.sid1_Select(yimei_pro_edt,
									shebeihao, zcno)) {
								Log.i("Tag", yimei_pro_edt + "批次号存在,已经帮你选中");
								// 查找当前选着的设备号
								List<Map<String, Object>> mesList = GetListMes_Procord(
										shebeihao, zcno);
								if (mesList != null) {
									scrollAdapter = new ScrollAdapter(
											gujingActivity, mesList);
									mListView.setAdapter(scrollAdapter);
									// 如果存在复选框选中
									List<Map<String, Object>> ListMesPitch = scrollAdapter.listData;
									for (int i = 0; i < ListMesPitch.size(); i++) {
										Map<String, Object> map = ListMesPitch
												.get(i);
										if (map.get("sid1").equals(
												yimei_pro_edt)) {
											scrollAdapter.state.put(i, true);
										}
									}
									scrollAdapter.notifyDataSetChanged();
									ToastUtil.showToast(
											getApplicationContext(), "《"
													+ yimei_pro_edt
													+ "》批次号存在,已经帮你选中", 0);
									InputHidden(); // 隐藏键盘
								}
								// 去服务器拿数据
							} else {
								// 批号已经绑定设备
								ToastUtil.showToast(gujingActivity, "《"
										+ yimei_proNum_edt.getText().toString()
										+ "》批次号已绑定过设备号不能重复绑定~", 0);
								yimei_proNum_edt.setSelectAllOnFocus(true);
								InputHidden(); // 隐藏键盘
							}
						} else {
							// 去服务器拿值 并绑定设备号
							Map<String, String> map = new HashMap<String, String>();
							map.put("dbid", MyApplication.DBID);
							map.put("usercode", MyApplication.user);
							map.put("apiId", "assist");
							map.put("assistid", "{QJBOXWEB}"); // 连表查料盒(原（MOZCLISTWEB）) 连接材料表
							if(zcno.equals("1B")){
								map.put("cont", "~sid1='" + yimei_pro_edt
										+ "' and zcno='" + zcno + "' and stype='1'");
							}else{
								map.put("cont", "~sid1='" + yimei_pro_edt
										+ "' and zcno='" + zcno + "' and stype='0'");
							}
							httpRequestQueryRecord(MyApplication.MESURL, map,
									"json");

							httpRequestQueryRecord(MyApplication.MESURL,
									MyApplication.QueryBatNo("FIRCHCKQUERY",
											"~id='" + shebeihao + "'"),
									"fircheckQuery");// 查询是否做过首检
						}
					}
				}
				if (string.equals("json")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					// 没有批次号
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
						ToastUtil.showToast(getApplicationContext(), "没有该批次号!",0);
						MyApplication.nextEditFocus(yimei_proNum_edt);
						yimei_proNum_edt.selectAll();
						InputHidden(); // 隐藏键盘
						return;
					} else {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
								.get("values")).get(0));
						if (!zcno.equals("41") && !zcno.equals("1A") && !zcno.equals("1B")) { // 烤箱只做记录
							if (SlkidMapJudge != null) {
								if (SlkidMapJudge.size() != 0) {
									for (int i = 0; i < SlkidMapJudge.size(); i++) {
										String slkid = SlkidMapJudge.get(i);
										if (!slkid.toUpperCase().equals(
												jsonValue.get("sid").toString()
														.toUpperCase())) {
											showNormalDialog(
													"（工单不符）",
													"\n当前工单为：【"
															+ SlkidMapJudge
																	.get(i)
															+ "】\n"
															+ "扫描工单为:【"
															+ jsonValue
																	.get("sid")
															+ "】\n不能入站，请先将当前批次出站后再入站生产!");
											return;
										}
									}
								}
							}
						}
						
						if (Integer.parseInt(jsonValue.get("bok").toString()) == 0) {
							ToastUtil.showToast(gujingActivity, "该批号不具备入站条件!",0);
							MyApplication.nextEditFocus(yimei_proNum_edt);
							yimei_proNum_edt.selectAll();
							InputHidden(); // 隐藏键盘
							return;
						} else if (jsonValue.get("state").toString().equals("02")
								|| jsonValue.get("state").toString().equals("03")) {
							ToastUtil.showToast(gujingActivity, "该批号状态为【入站】!",
									0);
							MyApplication.nextEditFocus(yimei_proNum_edt);
							yimei_proNum_edt.selectAll();
							InputHidden(); // 隐藏键盘
							return;
						} else if (jsonValue.get("state").toString().equals("04")) {
							ToastUtil.showToast(gujingActivity, "该批号状态为【出站】!",0);
							MyApplication.nextEditFocus(yimei_proNum_edt);
							yimei_proNum_edt.selectAll();
							InputHidden(); // 隐藏键盘
							return;
						} else if (jsonValue.get("state").toString().equals("07")) {
							ToastUtil.showToast(gujingActivity,
									"该批号状态为【出站待检】!", 0);
							MyApplication.nextEditFocus(yimei_proNum_edt);
							yimei_proNum_edt.selectAll();
							InputHidden(); // 隐藏键盘
							return;
						} else if (jsonValue.get("state").toString().equals("0A")) {
							ToastUtil.showToast(gujingActivity,"该批号状态为【异常状态】!", 0);
							MyApplication.nextEditFocus(yimei_proNum_edt);
							yimei_proNum_edt.selectAll();
							InputHidden(); // 隐藏键盘
							return;
						} else {
							// 判断料盒
							if (zcno.equals("21") || zcno.equals("31")) {
								if(!yimei_tongyong_oldMbox.getText().toString().toUpperCase().trim().equals("A0000")){
									if (!jsonValue.get("mbox").toString().equals(yimei_tongyong_oldMbox.getText().toString().toUpperCase().trim())) {
										if (zcno.equals("21")) {
											MboxIsNot("料盒不符", "固晶绑定料盒为【"
													+ jsonValue.get("mbox")
															.toString()
													+ "】，现要解绑的固晶绑料盒为【"
													+ yimei_tongyong_oldMbox
															.getText().toString()
															.toUpperCase().trim()
													+ "】");
											MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
											yimei_tongyong_oldMbox.selectAll();
											InputHidden();
											return;
										} else if (zcno.equals("31")) {
											MboxIsNot("料盒不符", "焊接绑定料盒为【"
													+ jsonValue.get("mbox")
															.toString()
													+ "】，现要解绑料的焊接盒为【"
													+ yimei_tongyong_oldMbox
															.getText().toString()
															.toUpperCase().trim()
													+ "】");
											MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
											yimei_tongyong_oldMbox.selectAll();
											InputHidden();
											return;
										}
									}
								}
								
								
								if (zcno.equals("31")) { // 点胶提示
									if (currSlkid != null) {
										if (!currSlkid.toUpperCase().equals(
												jsonValue.get("sid").toString()
														.toUpperCase())) {
											showNormalDialog("（更换工单）", "\n上批工单为：【"
													+ currSlkid.toUpperCase() + "】\n"
													+ "扫描工单为:【" + jsonValue.get("sid")
													+ "】!");
										}
									}
								}
							}
							/*else { // 固晶
								if (!jsonValue.get("mbox").toString()
										.equals("")) {
									ToastUtil.showToast(gujingActivity, "该批次【"
											+ yimei_proNum_edt.getText()
													.toString().trim()
													.toUpperCase() + "】已绑定料盒【"
											+ jsonValue.get("mbox").toString()
											+ "】", 0);
									MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
									yimei_tongyong_oldMbox.selectAll();
									InputHidden();
									return;
								}
							}*/

							if (!zcno.equals("41") && !zcno.equals("31")) { // 烤箱只做记录
								// 如果有首检标示
								if (jsonValue.get("fircheck").toString()
										.equals("1")) { // 打了首检标示（fircheck：首件检）
									if (!jsonValue.get("prd_no").toString()
											.equals(EQIRP_prdno)) {
										JumShouJianlDialog("首件检验", "【"
												+ jsonValue.get("prd_no")
														.toString()
												+ "】机型没有做首检,请做首检!");
									} else {
										if (EQIRP_firstchk.equals("0")
												|| EQIRP_firstchk.equals("")) {
											JumShouJianlDialog("首件检验", "【"
													+ jsonValue.get("prd_no")
															.toString()
													+ "】机型没有做首检,请做首检!");
										}
									}
								}
							}
							if(zcno.equals("1A")||zcno.equals("1B") || zcno.equals("41")){ //固晶烘烤								
								if(glue!=null){
									if(glue.length != 0){
										if(!jsonValue.getString("mjiao").trim().equals("")){
											if(!Arrays.asList(glue).contains(jsonValue.getString("mjiao"))){
												showNormalDialog("胶不符","该【"+shebeihao+"】烤箱需要绑定"+"【"+GetGlue()+"】胶"
														+ "扫描的批次号的胶为【"+jsonValue.getString("mjiao")+"】请检查!");
												return;
											}
										}
									}
								}
								if(stents!=null){
									if(stents.length != 0){
										if(!jsonValue.getString("mzhij").trim().equals("")){
											if(!Arrays.asList(stents).contains(jsonValue.getString("mzhij"))){
												showNormalDialog("支架不符","该【"+shebeihao+"】烤箱需要绑定【"+GetStents()+"】支架，"
														+ "扫描的批次号的支架为【"+jsonValue.getString("mzhij")+"】请检查!");
												return;
											}
										}
									}
								}
							}
							Intent intent = new Intent();
							intent.setClass(TongYongActivity.this, Loading1Activity.class);// 跳转到加载界面
							startActivity(intent);
							
							SlkidMapJudge.add(jsonValue.get("sid").toString());
							Log.i("SlkidMapJudge",
									"入站:" + SlkidMapJudge.toString());
							currSlkid = jsonValue.get("sid").toString(); // 修改服务器表的slkid
							fircheck_PRDNO = jsonValue.get("prd_no").toString(); // 进行首检的机种
							qtyv = jsonValue.get("qty").toString(); // (201)批次数量
							jsonValue.put("slkid", jsonValue.get("sid"));
							jsonValue.put("sid", "");
							jsonValue
									.put("firstchk", jsonValue.get("fircheck"));
							if (zcno.equals("41")||zcno.equals("1A")||zcno.equals("1B")) {
								jsonValue.put("state1", "03");
							} else {
								jsonValue.put("state1", "01");
							}
							jsonValue.put("state", "0");
							jsonValue.put("prd_name",jsonValue.containsKey("prd_name") ? jsonValue.get("prd_name") : "");
							jsonValue.put("prd_no",jsonValue.containsKey("prd_no") ? jsonValue.get("prd_no") : "");
							jsonValue.put("dcid", GetAndroidMacUtil.getMac());
							if (zcno.equals("11")) {
								jsonValue.put("mbox", yimei_tongyong_oldMbox.getText().toString().trim().toUpperCase());
							} else if (zcno.equals("21") || zcno.equals("31")) {
								jsonValue.put("mbox", yimei_tongyong_newMbox.getText().toString().trim().toUpperCase());
							} else if (zcno.equals("41")) {
								jsonValue.put("mbox", jsonValue.get("mbox"));
							} else if(zcno.equals("12")){ //第二次固晶
								jsonValue.put("mbox", jsonValue.get("mbox"));
							} else if(zcno.equals("13")){ //第一次固晶
								jsonValue.put("mbox", jsonValue.get("mbox"));
							} else if(zcno.equals("1A")){ //第一次固晶
								jsonValue.put("mbox", jsonValue.get("mbox"));
							} else if(zcno.equals("1B")){ //第一次固晶
								jsonValue.put("mbox", jsonValue.get("mbox"));
							} 
							jsonValue.put("op", zuoyeyuan);
							jsonValue.put("sys_stated", "3");
							jsonValue.put("sbid", shebeihao);
							jsonValue.put("smake", MyApplication.user);
							jsonValue.put("mkdate",
									MyApplication.GetServerNowTime());
							jsonValue.put("sbuid", "D0001");
							newJson = jsonValue;
							Map<String, String> mesIdMap = MyApplication
									.httpMapKeyValueMethod(MyApplication.DBID,
											"savedata", MyApplication.user,
											jsonValue.toJSONString(),
											"D0001WEB", "1");
							httpRequestQueryRecord(MyApplication.MESURL,
									mesIdMap, "id");
						}
					}
				}
				if (string.equals("id")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					JSONObject jsondata = (JSONObject) jsonObject.get("data");
					String newsid = jsondata.get("sid").toString(); // 拿到返回的sib1
					if (newsid != "") {
						if ((!(zuoyeyuan == null) || !zuoyeyuan.equals(""))
								&& !(shebeihao == null)
								|| !shebeihao.equals("")) {

							// ----------------------------------------入站
							String pla_zcno;
							if(zcno.equals("41") || zcno.equals("1A") || zcno.equals("1B")){
								pla_zcno = "03";
							}else{
								pla_zcno = "01";
							}
							// 修改本表
							Map<String, String> updateServerTable = MyApplication
									.UpdateServerTableMethod(
											MyApplication.DBID,
											MyApplication.user, "00",
											pla_zcno,yimei_pro_edt, currSlkid, zcno,
											"200");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateServerTable, "updateServerTable");
							// ----------------------------------------入站
							// 修改本表
							// 料盒修改==============================================================
							String mbox = null;
							if (zcno.equals("11")) {
								mbox = yimei_tongyong_oldMbox.getText()
										.toString().toUpperCase().trim();
							} else if (zcno.equals("21") || zcno.equals("31")) {
								mbox = yimei_tongyong_newMbox.getText()
										.toString().toUpperCase().trim();
							} else if (zcno.equals("41")) {
								mbox = newJson.getString("mbox");
							} else if(zcno.equals("12")){ //固晶2
								mbox = newJson.getString("mbox");
							} else if(zcno.equals("13")){ //固晶3
								mbox = newJson.getString("mbox");
							} else if(zcno.equals("1A")){ //固晶烘烤1
								mbox = newJson.getString("mbox");
							} else if(zcno.equals("1B")){ //固晶烘烤1
								mbox = newJson.getString("mbox");
							}
							if(!zcno.equals("41")&&!zcno.equals("12")&&!zcno.equals("13")&&!zcno.equals("1A")&&!zcno.equals("1B")){
								JSONArray jsonArr_650 = new JSONArray();
								JSONObject jsonobj_650 = new JSONObject();
								jsonobj_650.put("sid1", newJson.get("sid1").toString().toUpperCase());
								jsonobj_650.put("zcno", zcno);
								jsonobj_650.put("mbox", mbox);
								jsonobj_650.put("bind", "1"); // 绑定料盒
								jsonArr_650.add(jsonobj_650);
	
								Map<String, String> MboxServerUpdate_650 = new HashMap<>(); // 修改记录表
								MboxServerUpdate_650
										.put("dbid", MyApplication.DBID);
								MboxServerUpdate_650.put("usercode",
										MyApplication.user);
								MboxServerUpdate_650.put("apiId", "mesudp");
								MboxServerUpdate_650.put("jsondata",
										jsonArr_650.toString());
								MboxServerUpdate_650.put("id", "650");
								httpRequestQueryRecord(MyApplication.MESURL,
									MboxServerUpdate_650, "MboxServerUpdate");
							}
							// 料盒修改==============================================================
							// ----------------------------------------上料准备
							Map<String, String> ShangLiaoReadyMethod = MyApplication
									.ShangLiaoReadyMethod(MyApplication.DBID,
											MyApplication.user, yimei_pro_edt,
											zcno, zuoyeyuan, shebeihao,
											currSlkid, qtyv, "0", "0", "201");
							httpRequestQueryRecord(MyApplication.MESURL,
									ShangLiaoReadyMethod,
									"ShangLiaoReadyMethod");
							// ----------------------------------------上料准备

							newJson.put("sid", newsid);
							Log.i("static", newJson.toString());
							mesPrecord new_mes = newJson
									.toJavaObject(mesPrecord.class);
							if (gujing_list.addNewIdData(new_mes)) {
								if (zcno.equals("41") || zcno.equals("1A") || zcno.equals("1B")) { // 烘烤修改入站时间 ，后面出站需要卡控时间
									gujing_list.kaigongState1Update(newJson
											.get("sid1").toString(),
											MyApplication.GetServerNowTime());
								}
								kaigong.setEnabled(true);
								chuzhan.setEnabled(true);
								List<Map<String, Object>> getListMes_Procord = GetListMes_Procord(
										shebeihao, zcno);
								if (getListMes_Procord != null) {
									scrollAdapter = new ScrollAdapter(
											TongYongActivity.this,
											getListMes_Procord);
									mListView.setAdapter(scrollAdapter);
									shangliao.setEnabled(true);
									ToastUtil.showToast(
											getApplicationContext(), "《"
													+ yimei_proNum_edt
															.getText()
															.toString()
													+ "》批次号已加载到列表中", 0);
									yimei_tongyong_oldMbox.setText("");
									InputHidden(); // 隐藏键盘
									if (!zcno.equals("11")) {
										yimei_tongyong_newMbox.setText("");
										IsMbox_new = true;									
									}
									yimei_proNum_edt.setText("");
									if(zcno.equals("41")){
										MyApplication.nextEditFocus(yimei_proNum_edt);
									}else if(zcno.equals("31")||zcno.equals("21")){
										MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
										IsMbox_old = true;
									}else if(zcno.equals("1A")||zcno.equals("1B")||zcno.equals("12")||zcno.equals("13")){
										IsMbox_old = true;
										MyApplication.nextEditFocus(yimei_proNum_edt);
									}else{
										IsMbox_old = true;
										MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
										yimei_tongyong_oldMbox.selectAll();
										// yimei_tongyong_oldMbox.selectAll();
									}
								} else {
									// ToastUtil.showToast(getApplicationContext(),
									// "没有记录", 0);
									Log.i("Tag", "没有记录");
								}
							}
						}

					} else {
						ToastUtil.showToast(getApplicationContext(),
								"作业员或设备号为空", 0);
					}

				}
				if (string.equals("MboxServerUpdate")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					Log.i("updateServerTable", jsonObject.toString());
				}
				if (string.equals("updateServerTable")) { // 修改服务器俩张表(200)
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					Log.i("updateServerTable", jsonObject.toString());
					if (updatekaigongSid1 != null) {
						updatekaigongSid1.clear();
					}
				}
				if (string.equals("ShangLiaoReadyMethod")) { // 上料准备
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					Log.i("ShangLiaoReadyMethod", jsonObject.toString());
				}
				if (string.equals("Isshebei")) {
					JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 0) {
						Log.i("code", jsonObject.get("code").toString());
						if (mListView != null) {
							mListView.setAdapter(null);
							if (scrollAdapter != null) {
								scrollAdapter.notifyDataSetChanged();
							}
							MyApplication.nextEditFocus(yimei_equipment_edt);
							yimei_equipment_edt.selectAll();
							ToastUtil.showToast(getApplicationContext(),"系统中没有注册该设备!", 0);
							InputHidden();
						} else {
							MyApplication.nextEditFocus(yimei_equipment_edt);
							yimei_equipment_edt.selectAll();
							ToastUtil.showToast(getApplicationContext(),"系统中没有注册该设备!", 0);
							InputHidden();
						}
					} else {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject.get("values")).get(0));
						if(Integer.parseInt(jsonValue.get("sbstate").toString()) != 0){
							ToastUtil.showToast(getApplicationContext(),"该设备处于报修状态，请联系工程!", 0);
							MyApplication.nextEditFocus(yimei_equipment_edt);
							yimei_equipment_edt.selectAll();
							InputHidden();
							return ;
						}
						//查询一个设备是否绑定多个制程
						if(jsonValue.get("zc_id").toString().indexOf(";") != -1){
							String[] split = jsonValue.get("zc_id").toString().split(";");
							boolean contains = Arrays.asList(split).contains(zcno);
							if(!contains){
								ToastUtil.showToast(getApplicationContext(),"设备号和工序不匹!", 0);
								MyApplication.nextEditFocus(yimei_equipment_edt);
								yimei_equipment_edt.selectAll();
								InputHidden();
								return ;
							}
						}
						/*else{
							if(!jsonValue.get("zc_id").toString().equals(zcno)){
								ToastUtil.showToast(getApplicationContext(),"设备号和工序不匹!", 0);
								MyApplication.nextEditFocus(yimei_equipment_edt);
								yimei_equipment_edt.selectAll();
								InputHidden();
								return ;
							}
						}*/
						glue = null;
						stents = null;
						if(!jsonValue.getString("glue").equals("")){							
							if(jsonValue.getString("glue").indexOf(";") != -1){					
								String glue1[] = jsonValue.getString("glue").split(";");
								glue = new String[glue1.length];
								glue = glue1;
							}else{
								glue = new String[1];
								glue[0] = jsonValue.getString("glue");
							}
						}
						if(!jsonValue.getString("stents").equals("")){
							if(jsonValue.getString("stents").indexOf(";") != -1){							
								String stents1[] = jsonValue.getString("stents").split(";");
								stents = new String[stents1.length];
								stents = stents1;
							}else{
								stents = new String[1];
								stents[0] = jsonValue.getString("stents");
							}
						}
						//除了 点胶烘烤 固晶2 固晶3  固晶烘烤1 固晶烘烤2 都往批次号上跳转 
						if(!zcno.equals("41")&&!zcno.equals("12")&&!zcno.equals("13")
								&&!zcno.equals("1A")&&!zcno.equals("1B")){
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
						}else{
							MyApplication.nextEditFocus(yimei_proNum_edt);
						}
						// 去服务器中拿设备号
						Map<String, String> map = new HashMap<String, String>();
						map.put("dbid", MyApplication.DBID);
						map.put("usercode", MyApplication.user);
						map.put("apiId", "assist");
						map.put("assistid", "{MSBMOLIST}");
						map.put("cont", "~sbid='" + shebeihao + "' and zcno='"
								+ zcno + "'");
						httpRequestQueryRecord(MyApplication.MESURL, map,
								"shebei");

						httpRequestQueryRecord(MyApplication.MESURL,
								MyApplication.QueryBatNo("FIRCHCKQUERY",
										"~id='" + shebeihao + "'"),
								"fircheckQuery");// 查询是否做过首检
					}
				}
				if (string.equals("fircheckQuery")) { // 首检
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
								.get("values")).get(0));
						EQIRP_prdno = jsonValue.get("prdno").toString();
						EQIRP_firstchk = jsonValue.get("firstchk").toString();
					} else {
						ToastUtil.showToast(gujingActivity, "没有【" + shebeihao + "】", 0);
					}
				}
				if (string.equals("shebei")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (SlkidMapJudge != null) { // 清空存在工单的集合
						if (SlkidMapJudge.size() != 0) {
							SlkidMapJudge.clear();
						}
					}
					//删除数据
					new mesAllMethod(TongYongActivity.this).delteAll();
					currSlkid = null;
					// 判断设备号在服务器中是否存在
					if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
						for (int i = 0; i < ((JSONArray) jsonObject
								.get("values")).size(); i++) {
							JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
									.get("values")).get(i));
							fircheck_PRDNO = jsonValue.get("prd_no").toString(); // 进行首检的机种
							jsonValue.put("sbid", jsonValue.get("sbid").toString().toUpperCase());
							jsonValue.put("mbox",jsonValue.get("mbox")); //将料盒号加到备注上
							
							// 获取mes_precord表中的工单号,为下次批次入站做不同工单禁止入站功能
							SlkidMapJudge.add(jsonValue.get("slkid").toString());
							currSlkid = jsonValue.get("slkid").toString();
							
							mesPrecord new_mes = jsonValue.toJavaObject(mesPrecord.class);
							new_mes.setFircheck(jsonValue.get("fircheck").toString());
							
							if (!gujing_list.IsSid1AndZncoAndSbid(
									new_mes.getSid1(), new_mes.getZcno(),
									new_mes.getSbid())) {
								// 向本地库添加批次号（多条）
								if (gujing_list.addSbidData(new_mes)) {
									shangliao.setEnabled(true);
									kaigong.setEnabled(true);
									chuzhan.setEnabled(true);
									// 将当前的文本框的设备号传入,查当前文本框的设备的批号
									List<Map<String, Object>> getListMes_Procord = GetListMes_Procord(
											shebeihao, zcno);
									if (getListMes_Procord != null) {
										scrollAdapter = new ScrollAdapter(
												TongYongActivity.this,
												getListMes_Procord);
										mListView.setAdapter(scrollAdapter);
										ToastUtil.showToast(
												getApplicationContext(), "《"
														+ yimei_equipment_edt
																.getText()
																.toString()
														+ "》设备号已加载到列表中", 0);

									} else {
										shangliao.setEnabled(false);
										chuzhan.setEnabled(false);
										kaigong.setEnabled(false);
										mListView.setAdapter(null);
										scrollAdapter.notifyDataSetChanged();
									}

								} else {
									shangliao.setEnabled(false);
									chuzhan.setEnabled(false);
									kaigong.setEnabled(false);
								}
							}
						}
						Log.i("SlkidMapJudge", "设备" + SlkidMapJudge.toString());
						// 将当前的文本框的设备号传入,查当前文本框的设备的批号
						List<Map<String, Object>> getListMes_Procord = GetListMes_Procord(
								shebeihao, zcno);
						if (getListMes_Procord != null) {
							scrollAdapter = new ScrollAdapter(
									TongYongActivity.this, getListMes_Procord);
							mListView.setAdapter(scrollAdapter);
							ToastUtil.showToast(getApplicationContext(), "《"
									+ yimei_equipment_edt.getText().toString()
									+ "》设备号已加载到列表中", 0);
						} else {
							shangliao.setEnabled(false);
							chuzhan.setEnabled(false);
							kaigong.setEnabled(false);
							mListView.setAdapter(null);
							scrollAdapter.notifyDataSetChanged();
						}
						if (mListView == null) {
							shangliao.setEnabled(false);
							kaigong.setEnabled(false);
							chuzhan.setEnabled(false);
						} else {
							shangliao.setEnabled(true);
							kaigong.setEnabled(true);
							chuzhan.setEnabled(true);
						}
					} else {
						if (mListView != null) {
							mListView.setAdapter(null);
							if (scrollAdapter != null) {
								scrollAdapter.notifyDataSetChanged();
							}
						}
					}
				}
				if (string.equals("kaigongUpdata")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("id").toString()) == 0
							|| Integer
									.parseInt(jsonObject.get("id").toString()) == 1) {
						for (int i = 0; i < updatekaigongSid1.size(); i++) {
							mesPrecord m = updatekaigongSid1.get(i);
							Log.i("mes", m.toString());
							if (m.getState1().equals("01")
									|| m.getState1().equals("02")) {
								// ------------------------修改服务器的俩张表（开工）
								Map<String, String> updateServerTable = MyApplication
										.UpdateServerTableMethod(
												MyApplication.DBID,
												MyApplication.user,
												m.getState1(), "03",
												m.getSid1(), m.getSlkid(),
												zcno, "200");
								httpRequestQueryRecord(MyApplication.MESURL,
										updateServerTable, "updateServerTable");
								// ------------------------修改服务器的俩张表（开工）
								Thread.sleep(400);
								if (gujing_list.kaigongState1Update(
										m.getSid1(),
										MyApplication.GetServerNowTime())) { // 修改本地入站时间和状态，出站需要开工的卡控时间
									List<Map<String, Object>> getListMes_Procord = GetListMes_Procord(
											shebeihao, zcno);
									scrollAdapter = new ScrollAdapter(
											TongYongActivity.this,
											getListMes_Procord);
									mListView.setAdapter(scrollAdapter);

									ToastUtil.showToast(gujingActivity,
											"状态修改成功", 0);

								} else {
									ToastUtil.showToast(gujingActivity,
											"状态修改失败", 0);
								}
							}
						}
						updatekaigongSid1.clear();

					} else {
						/* Log.i("kaigongUpdata", "服务器修改开工状态失败"); */
						ToastUtil.showToast(
								gujingActivity,
								String.valueOf("（通用）服务器修改开工状态失败--err:"
										+ jsonObject.get("message")), 0);
					}
				}
				if (string.equals("chuzhanUpdata")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					if (Integer.parseInt(jsonObject.get("id").toString()) == 0
							|| Integer.parseInt(jsonObject.get("id").toString()) == 1) {
						for (int i = 0; i < updatekaigongSid1.size(); i++) {
							mesPrecord m = updatekaigongSid1.get(i);
							Log.i("mes", m.toString());
							if (m.getState1().equals("03")) {
								// ------------------------修改服务器的俩张表（出站）
								if (!m.getZcno().equals("41") && !m.getZcno().equals("1A") && !m.getZcno().equals("1B")) { // 烘烤站数组提交
									Map<String, String> updateServerTable = MyApplication
											.UpdateServerTableMethod(
													MyApplication.DBID,
													MyApplication.user, "03",
													"04", m.getSid1(),
													m.getSlkid(), zcno, "200");
									httpRequestQueryRecord(
											MyApplication.MESURL,
											updateServerTable,
											"updateServerTable");
								}
								Thread.sleep(200);
								if (gujing_list.delSid(m.getSid())) {
									if (SlkidMapJudge != null) {
										if (SlkidMapJudge.size() != 0) { // 如果集合中还有值就删除第一个下标，所有的批次号出站后也就为空了
											try {
												SlkidMapJudge.remove(0);
											} catch (Exception e) {
												SlkidMapJudge.clear();
											}
										}
									}
									Log.i("kaigongUpdata", "本地库已删除~");
									List<Map<String, Object>> getListMes_Procord = GetListMes_Procord(
											shebeihao, zcno);
									scrollAdapter = new ScrollAdapter(
											TongYongActivity.this,
											getListMes_Procord);
									mListView.setAdapter(scrollAdapter);

								} else {
									Log.i("kaigongUpdata", "本地库删除失败");
								}
							}
						}
						Log.i("SlkidMapJudge", "出站：" + SlkidMapJudge.toString());
						updatekaigongSid1.clear();
					} else {
						ToastUtil.showToast(gujingActivity,
								String.valueOf(jsonObject.get("message")), 0);
					}
				}
				if (string.equals("Tongyong_ptime")) {
					JSONObject jsonObject = JSON.parseObject(b.getString(
							"jsonObj").toString());
					for (int i = 0; i < ((JSONArray) jsonObject.get("values"))
							.size(); i++) {
						JSONObject jsonValue = (JSONObject) (((JSONArray) jsonObject
								.get("values")).get(i));
						if (jsonValue.containsKey("ptime")) {
							ptime.put(jsonValue.get("id").toString(), jsonValue
									.get("ptime").toString());
						}
					}
				}
			} catch (Exception e) {
				Log.i("err", "（通用）" + e.toString());
				ToastUtil.showToast(gujingActivity,
						"（通用1）请求服务器:" + e.toString(), 0);
			}
		}
	};

	/**
	 * 获取胶的数组元素
	 */
	private String GetGlue(){
		if(glue.length != 0){
			String str = "";
			for (int i = 0; i < glue.length; i++) {
				str += glue[i]+"/";
			}
			str = str.substring(0,str.lastIndexOf("/"));
			return str;
		}
		return "";
	}
	
	/**
	 * 获取支架的数组元素
	 */
	private String GetStents(){
		if(stents.length != 0){
			String str = "";
			for (int i = 0; i < stents.length; i++) {
				str += stents[i]+"/";
			}
			str = str.substring(0,str.lastIndexOf("/"));
			return str;
		}
		return "";
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
		ArrayAdapter<Pair> adapter = new ArrayAdapter<Pair>(TongYongActivity.this,
				android.R.layout.simple_spinner_item, dicts);
			selectValue.setAdapter(adapter);
			//下拉框的样式为默认
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			selectValue.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						zcno = ((Pair) selectValue.getSelectedItem()).getValue();
						switch (zcno) {
						case "11": //固晶
							zcno = "11";
							if (newMboxText.getVisibility() == View.VISIBLE) { // 新料盒文本
								newMboxText.setVisibility(View.GONE);
							}
							mboxTab.setVisibility(View.VISIBLE);
							oldMboxText.setText("固晶料盒"); // 旧料盒文本
							if (yimei_tongyong_newMbox.getVisibility() == View.VISIBLE) { // 新料盒文本框
								yimei_tongyong_newMbox.setVisibility(View.GONE);
							}
							shangliao.setVisibility(View.VISIBLE);
							kaigong.setVisibility(View.VISIBLE);
							clearEditText();
							break;
						case "12":  //固2
							zcno = "12";
							shangliao.setVisibility(View.VISIBLE);
							kaigong.setVisibility(View.VISIBLE);
							mboxTab.setVisibility(View.GONE);
							nextEditFocus(yimei_user_edt);
							clearEditText();
							break;
						case "13":  //固3
							zcno = "13";
							shangliao.setVisibility(View.VISIBLE);
							kaigong.setVisibility(View.VISIBLE);
							mboxTab.setVisibility(View.GONE);
							nextEditFocus(yimei_user_edt);
							clearEditText();
							break;
						case "1A":  //固晶烘烤1
							zcno = "1A";
							mboxTab.setVisibility(View.GONE);
							shangliao.setVisibility(View.GONE);
							nextEditFocus(yimei_user_edt);
							clearEditText();
							break;
						case "1B":  //固晶烘烤2
							zcno = "1B";
							mboxTab.setVisibility(View.GONE);
							shangliao.setVisibility(View.GONE);
							nextEditFocus(yimei_user_edt);
							clearEditText();
							break;
						case "21": //焊接
							zcno = "21";
							mboxTab.setVisibility(View.VISIBLE);
							oldMboxText.setText("固晶料盒");
							newMboxText.setText("焊接料盒");
							if (yimei_tongyong_newMbox.getVisibility() == View.GONE) {
								yimei_tongyong_newMbox.setVisibility(View.VISIBLE);
							}
							if (newMboxText.getVisibility() == View.GONE) {
								newMboxText.setVisibility(View.VISIBLE);
							}

							yimei_tongyong_oldMbox.setVisibility(View.VISIBLE);
							yimei_tongyong_newMbox.setVisibility(View.VISIBLE);
							shangliao.setVisibility(View.VISIBLE);
							kaigong.setVisibility(View.VISIBLE);
							clearEditText();
							break;
						case "31": //点胶
							zcno = "31";
							mboxTab.setVisibility(View.VISIBLE);
							oldMboxText.setText("焊接料盒");
							newMboxText.setText("点胶料盒");
							if (yimei_tongyong_newMbox.getVisibility() == View.GONE) {
								yimei_tongyong_newMbox.setVisibility(View.VISIBLE);
							}
							if (newMboxText.getVisibility() == View.GONE) {
								newMboxText.setVisibility(View.VISIBLE);
							}
							shangliao.setVisibility(View.GONE);
							if (kaigong.getVisibility() == View.GONE) {
								kaigong.setVisibility(View.VISIBLE);
							}
							clearEditText();
							break;
						case "41":  //点胶后烘烤
							zcno = "41";
							mboxTab.setVisibility(View.GONE);
							shangliao.setVisibility(View.GONE);
							nextEditFocus(yimei_user_edt);
							clearEditText();
							break;
						}
//						ToastUtil.showToast(getApplicationContext(), zcno, 0);
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
	 * 设备号|页面刷新数据
	 * 
	 * @return
	 */
	@SuppressWarnings("null")
	public List<Map<String, Object>> GetListMes_Procord(String sbid, String zcno) {
		List<Map<String, Object>> mesList = null;
		if (sbid == null || sbid.equals("")) {
			Log.i("tag", "设备号为空为空，请输入设备号~");
			return null;
		} else {
			// 查批次号和制程
			List<mesPrecord> findList = gujing_list.findList(sbid, zcno);
			if (findList != null) {
				mesList = new ArrayList<Map<String, Object>>();
				Map<String, Object> mesMap = null;
				CHScrollView headerScroll = (CHScrollView) findViewById(R.id.item_scroll_title);
				// 添加头滑动事件
				mHScrollViews.add(headerScroll);

				Map<String, String> stateName = MyApplication.getStateName();
				for (int i = 0; i < findList.size(); i++) {
					mesPrecord mesItem = findList.get(i);
					mesMap = new HashMap<String, Object>();
					mesMap.put("checkMap", mesItem);
					mesMap.put("sid1", mesItem.getSid1());
					mesMap.put("slkid", stateName.get(mesItem.getState1()));
					mesMap.put("prd_no", mesItem.getPrd_no());
					mesMap.put("qty", mesItem.getQty());
					mesMap.put("remark", mesItem.getMbox());
					mesMap.put("fircheck", mesItem.getFircheck());
					mesList.add(mesMap);
				}
			}
			return mesList;
		}
	}

	/**
	 * 全选方法
	 */
	protected void listenerQuanXuan() {
		quanxuan.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (scrollAdapter == null) {
					ToastUtil.showToast(getApplicationContext(), "没有数据", 0);
					return;
				}
				if (isChecked) {
					int initCheck = scrollAdapter.initCheck(true);
					scrollAdapter.notifyDataSetChanged();
					if (initCheck == -1) {
						ToastUtil
								.showToast(getApplicationContext(), "列表为空~", 0);
						return;
					}
				} else {
					int initCheck = scrollAdapter.initCheck(false);
					scrollAdapter.notifyDataSetChanged();
					if (initCheck == -1) {
						ToastUtil
								.showToast(getApplicationContext(), "列表为空~", 0);
						return;
					}
				}
			}
		});

	}

	public static void addHViews(final CHScrollView hScrollView) {
		if (!mHScrollViews.isEmpty()) {
			int size = mHScrollViews.size();
			CHScrollView scrollView = mHScrollViews.get(size - 1);
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
		mHScrollViews.add(hScrollView);
	}

	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		for (CHScrollView scrollView : mHScrollViews) {
			if (mTouchView != scrollView)
				scrollView.smoothScrollTo(l, t);
		}
	}

	OnClickListener RLlistener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mListView.setAdapter(null);
			scrollAdapter.notifyDataSetChanged();
		}
	};

	/**
	 * 获取选中的数据并且请求服务器 （开工，出站）
	 * 
	 * @param publicState
	 */
	public void UpdateServerData(String publicState) {
		try {
			HashMap<Integer, Boolean> state = scrollAdapter.Getstate();

			if (state == null || state.equals(null)) {
				ToastUtil.showToast(getApplicationContext(), "列表为空~", 0);
			} else {
				int count = 0;
				if (updatekaigongSid1 != null) {
					updatekaigongSid1.clear();
				}
				for (int j = 0; j < scrollAdapter.getCount(); j++) {
					if (state.get(j)) {
						if (state.get(j) != null) {
							@SuppressWarnings("unchecked")
							HashMap<String, Object> map = (HashMap<String, Object>) scrollAdapter
									.getItem(j);

							if (updatekaigongSid1 == null) {
								updatekaigongSid1 = new ArrayList<mesPrecord>();
							}
							mesPrecord m = (mesPrecord) map.get("checkMap");
							updatekaigongSid1.add(m);
							count++;
						}
					} 
				}
				String ShowstateName = null;
				if ("kaigongUpdata".equals(publicState)) {
					ShowstateName = "开工";
				} else if ("chuzhanUpdata".equals(publicState)) {
					ShowstateName = "出站";
				}
				if (ShowstateName.equals("开工") && zcno.equals("11")) {
					if (count > 2) {
						ToastUtil.showToast(TongYongActivity.this, "固晶"
								+ ShowstateName + "最多是两批物料~", 0);
						updatekaigongSid1.clear(); // 清空需要修改的列表
													// ，如果第一次选择3个，第二次选择2个，list里的值为3个
						return;
					}
				}
				if (ShowstateName.equals("开工") && zcno.equals("12")) {
					if (count > 2) {
						ToastUtil.showToast(TongYongActivity.this, "固晶"
								+ ShowstateName + "最多是两批物料~", 0);
						updatekaigongSid1.clear(); // 清空需要修改的列表
													// ，如果第一次选择3个，第二次选择2个，list里的值为3个
						return;
					}
				}
				if (ShowstateName.equals("开工") && zcno.equals("13")) {
					if (count > 2) {
						ToastUtil.showToast(TongYongActivity.this, "固晶"
								+ ShowstateName + "最多是两批物料~", 0);
						updatekaigongSid1.clear(); // 清空需要修改的列表
													// ，如果第一次选择3个，第二次选择2个，list里的值为3个
						return;
					}
				}
				if (ShowstateName.equals("开工") && zcno.equals("21")) {
					if (count > 1) {
						ToastUtil.showToast(TongYongActivity.this, "焊接"
								+ ShowstateName + "最多是一批物料~", 0);
						updatekaigongSid1.clear(); // 清空需要修改的列表
													// ，如果第一次选择3个，第二次选择2个，list里的值为3个
						return;
					}
				}
				if (ShowstateName.equals("开工") && zcno.equals("31")) {
					if (count > 1) {
						ToastUtil.showToast(TongYongActivity.this,
								"只可选择一个批次开工。", 0);
						updatekaigongSid1.clear(); // 清空需要修改的列表
													// ，如果第一次选择3个，第二次选择2个，list里的值为3个
						return;
					}
				}
				switch (count) {
				case 0:
					ToastUtil.showToast(getApplicationContext(), "请选中一条数据", 0);
					break;
				default:
					/*
					 * ToastUtil.showToast(getApplicationContext(), "不可以选多条数据",
					 * 0) ;
					 */
					JSONArray jsonArr_200 = new JSONArray();
					JSONArray jsonArr_202 = new JSONArray();
					for (int i = 0; i < updatekaigongSid1.size(); i++) {
						mesPrecord mes_precord = updatekaigongSid1.get(i);
						JSONObject json = (JSONObject) JSON.toJSON(mes_precord);
						// 如果是入站状态改变
						if (publicState.equals("kaigongUpdata")) {
							mes_precord.setHpdate(MyApplication
									.GetServerNowTime());
							// 如果状态是入站和上料都可以开工
							if (json.get("state1").toString().equals("01")) {
								httpRequestQueryRecord(MyApplication.MESURL,
										MyApplication.QueryBatNo(
												"FIRCHCKQUERY", "~id='"
														+ shebeihao + "'"),
										"fircheckQuery");// 查询是否做过首检
								Thread.sleep(100);
								// ===========================判断是否做了首检===============================================
								// 打了首检标示（fircheck：首件检）
								if(!zcno.equals("31") && !zcno.equals("41")){
									if (json.get("fircheck").toString().equals("1")) {
										if (!json.get("prd_no").toString()
												.equals(EQIRP_prdno)) { // 判断当前机型和设备中的机型是否匹配
											ToastUtil.showToast(gujingActivity, "【"
													+ json.get("prd_no")
													+ "】机型没有做首检（prdno）!", 0);
											updatekaigongSid1.clear();
											Log.i("check", String
													.valueOf(updatekaigongSid1
															.size()));
											return;
										}
										if (EQIRP_firstchk.equals("0")
												|| EQIRP_firstchk.equals("")) { // 是否做过首检
											ToastUtil.showToast(gujingActivity, "【"
													+ json.get("prd_no")
													+ "】机型没有做首检（fircheck）!", 0);
											updatekaigongSid1.clear();
											Log.i("check", String
													.valueOf(updatekaigongSid1
															.size()));
											return;
										}
									}
								}
								// ===========================判断是否做了首检===============================================
								// 点胶站和焊接站只许一个批次开工
								if (zcno.equals("31") || zcno.equals("21")) {
									int state02 = 0;
									for (int j = 0; j < scrollAdapter
											.getCount(); j++) {
										Map<String, Object> map = (Map<String, Object>) scrollAdapter
												.getItem(j);
										if (map.get("slkid").equals("生产中")) {
											state02++;
										}
									}
									if (state02 >= 1) {
										ToastUtil.showToast(
												TongYongActivity.this,
												"请将上一个批次【出站】后再【开工】。", 0);
										updatekaigongSid1.clear();
										return;
									} else {
										Map<String, String> updateTimeMethod = MyApplication
												.updateServerTimeMethod(
														MyApplication.DBID,
														MyApplication.user,
														"01", "03",
														mes_precord.getSid(),
														zuoyeyuan, zcno, "202");
										httpRequestQueryRecord(
												MyApplication.MESURL,
												updateTimeMethod, publicState);
									}
								} else if (zcno.equals("11") || zcno.equals("1A") || zcno.equals("1B")) { // 固晶可开俩批
									int state02 = 0;
									for (int j = 0; j < scrollAdapter
											.getCount(); j++) {
										Map<String, Object> map = (Map<String, Object>) scrollAdapter
												.getItem(j);
										if (map.get("slkid").equals("生产中")) {
											state02++;
										}
									}
									if (state02 >= 1) {
										ToastUtil.showToast(
												TongYongActivity.this,
												"请将上一个批次【出站】后再【开工】。", 0);
										updatekaigongSid1.clear();
										return;
									} else {
										Map<String, String> updateTimeMethod = MyApplication
												.updateServerTimeMethod(
														MyApplication.DBID,
														MyApplication.user,
														"01", "03",
														mes_precord.getSid(),
														zuoyeyuan, zcno, "202");
										httpRequestQueryRecord(
												MyApplication.MESURL,
												updateTimeMethod, publicState);
									}
								} else {
									Map<String, String> updateTimeMethod = MyApplication
											.updateServerTimeMethod(
													MyApplication.DBID,
													MyApplication.user, "01",
													"03", mes_precord.getSid(),
													zuoyeyuan, zcno, "202");
									httpRequestQueryRecord(
											MyApplication.MESURL,
											updateTimeMethod, publicState);
								}
							} else if (json.get("state1").toString().equals("02")) {
								if (zcno.equals("31") || zcno.equals("21")) {
									int state02 = 0;
									for (int j = 0; j < scrollAdapter
											.getCount(); j++) {
										Map<String, Object> map = (Map<String, Object>) scrollAdapter
												.getItem(j);
										if (map.get("slkid").equals("生产中")) {
											state02++;
										}
									}
									if (state02 >= 1) {
										ToastUtil.showToast(
												TongYongActivity.this,
												"请将上一个批次【出站】后再【开工】。", 0);
										updatekaigongSid1.clear();
										return;
									} else {
										Map<String, String> updateTimeMethod = MyApplication
												.updateServerTimeMethod(
														MyApplication.DBID,
														MyApplication.user,
														"02", "03",
														mes_precord.getSid(),
														zuoyeyuan, zcno, "202");
										httpRequestQueryRecord(
												MyApplication.MESURL,
												updateTimeMethod, publicState);
									}
								} else if (zcno.equals("11") || zcno.equals("1A") || zcno.equals("1B")) {
									int state02 = 0;
									for (int j = 0; j < scrollAdapter
											.getCount(); j++) {
										Map<String, Object> map = (Map<String, Object>) scrollAdapter
												.getItem(j);
										if (map.get("slkid").equals("生产中")) {
											state02++;
										}
									}
									if (state02 >= 2) {
										ToastUtil.showToast(
												TongYongActivity.this,
												"请将上一个批次【出站】后再【开工】。", 0);
										updatekaigongSid1.clear();
										return;
									} else {
										Map<String, String> updateTimeMethod = MyApplication
												.updateServerTimeMethod(
														MyApplication.DBID,
														MyApplication.user,
														"02", "03",
														mes_precord.getSid(),
														zuoyeyuan, zcno, "202");
										httpRequestQueryRecord(
												MyApplication.MESURL,
												updateTimeMethod, publicState);
									}
								} else {
									Map<String, String> updateTimeMethod = MyApplication
											.updateServerTimeMethod(
													MyApplication.DBID,
													MyApplication.user, "02",
													"03", mes_precord.getSid(),
													zuoyeyuan, zcno, "202");
									httpRequestQueryRecord(
											MyApplication.MESURL,
											updateTimeMethod, publicState);
								}
							} else if (json.get("state1").toString().equals("03")) {
								ToastUtil.showToast(getApplicationContext(),
										"选中的【"+json.get("sid1").toString()+"】批次已是开工状态！", 0);
								updatekaigongSid1.clear(); // 清空需要修改的列表
							}else if (json.get("state1").toString().equals("0A")) {
								ToastUtil.showToast(getApplicationContext(),
										"选中的【"+json.get("sid1").toString()+"】批次状态【异常】不能开工！", 0);
								updatekaigongSid1.clear(); // 清空需要修改的列表
							}else if (json.get("state1").toString().equals("0B")) {
								ToastUtil.showToast(getApplicationContext(),
										"选中的【"+json.get("sid1").toString()+"】批次状态【暂停】不能开工！", 0);
								updatekaigongSid1.clear(); // 清空需要修改的列表
							}else if (json.get("state1").toString().equals("0C")) {
								ToastUtil.showToast(getApplicationContext(),
										"选中的【"+json.get("sid1").toString()+"】批次状态【中止】不能开工！", 0);
								updatekaigongSid1.clear(); // 清空需要修改的列表
							}else if (json.get("state1").toString().equals("0D")) {
								ToastUtil.showToast(getApplicationContext(),
										"选中的【"+json.get("sid1").toString()+"】批次状态【受控】不能开工！", 0);
								updatekaigongSid1.clear(); // 清空需要修改的列表
							}
						} else if (publicState.equals("chuzhanUpdata")) {
							int chooseTime = MyApplication.ChooseTime(mes_precord.getHpdate());
							// int a = Integer.parseInt(ptime.get(zcno));
							if (chooseTime >= Integer.parseInt(ptime
									.get(zcno)) || ptime == null || Integer.parseInt(ptime.get(zcno)) <=0) {
								if(zcno.equals("41")||zcno.equals("1A")||zcno.equals("1B")) {
									JSONObject jsonobj_200 = new JSONObject(); // 200批量提交
									jsonobj_200.put("oldstate",
											json.get("state1").toString());
									jsonobj_200.put("newstate", "04");
									jsonobj_200.put("sid", json.get("sid1")
											.toString());
									jsonobj_200.put("slkid",
											json.get("slkid").toString());
									jsonobj_200.put("zcno", zcno);
									jsonArr_200.add(jsonobj_200);
	
									JSONObject jsonobj_202 = new JSONObject();
									jsonobj_202.put("oldstate",
											json.get("state1").toString());
									jsonobj_202.put("newstate", "04");
									jsonobj_202.put("sid",
											mes_precord.getSid());
									jsonobj_202.put("op", zuoyeyuan);
									jsonobj_202.put("zcno", zcno);
									jsonArr_202.add(jsonobj_202);
	
									gujing_list.delSid(mes_precord.getSid());
								}else{								
									Map<String, String> Query_Precord = MyApplication.QueryBatNo("MSBMOLIST","~sid1='"+json.get("sid1")+"'"
											+ " and sbid='"+shebeihao+"' and zcno='"+zcno+"'");
									httpRequestQueryRecord(MyApplication.MESURL, Query_Precord,"Query_Precord_chuzhan");
								}
							}else {
								ToastUtil.showToast(getApplicationContext(),"选中的批次不能出站,已开工"+ chooseTime + "分！",0);
							}
						}
					}
					if (jsonArr_200.size() != 0) { // 烘烤站批量提交
						if (zcno.equals("41") || zcno.equals("1A") || zcno.equals("1B")) { // 烘烤站数组提交出站
							Map<String, String> updateServer_04_202 = new HashMap<>(); // 修改记录表
							updateServer_04_202.put("dbid", MyApplication.DBID);
							updateServer_04_202.put("usercode",
									MyApplication.user);
							updateServer_04_202.put("apiId", "mesudp");
							updateServer_04_202.put("jsondata",
									jsonArr_202.toString());
							updateServer_04_202.put("id", "290");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateServer_04_202, "updateServerTable");

							Map<String, String> updateServer_04_200 = new HashMap<>(); // 修改lot_plana
							updateServer_04_200.put("dbid", MyApplication.DBID);
							updateServer_04_200.put("usercode",
									MyApplication.user);
							updateServer_04_200.put("apiId", "mesudp");
							updateServer_04_200.put("jsondata",
									jsonArr_200.toString());
							updateServer_04_200.put("id", "280");
							httpRequestQueryRecord(MyApplication.MESURL,
									updateServer_04_200, "updateServerTable");

							List<Map<String, Object>> getListMes_Procord = GetListMes_Procord(
									shebeihao, zcno);
							scrollAdapter = new ScrollAdapter(
									TongYongActivity.this, getListMes_Procord);
							mListView.setAdapter(scrollAdapter);
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.showToast(TongYongActivity.this,
					"（通用）请求服务器:" + e.toString(), 0);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, 0, 0, "");
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
			showNormalDialog("版本", MyApplication.TONGYONG_VTEXT);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 跳转首检界面
	 * 
	 * @param msg
	 */
	private void JumShouJianlDialog(String title, String msg) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				TongYongActivity.this);
		normalDialog.setTitle(title);
		normalDialog.setMessage(Html.fromHtml("<font color='red'>" + msg
				+ "</font>"));
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClass(TongYongActivity.this,
								IPQC_shoujian.class);// 跳转到首检
						// 利用bundle来存取数据
						Bundle bundle = new Bundle();
						bundle.putString("json", newJson.toString());
						// 再把bundle中的数据传给intent，以传输过去
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
		normalDialog.show();
	}

	/**
	 * 料盒绑定不符提示
	 * 
	 * @param msg
	 */
	private void MboxIsNot(String title, String msg) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				TongYongActivity.this);
		normalDialog.setTitle(title);
		normalDialog.setMessage(Html.fromHtml("<font color='red'>" + msg
				+ "</font>"));
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		normalDialog.show();
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
//		usertext.setKeyListener(null);

		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				TongYongActivity.this);
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
						httpRequestQueryRecord(MyApplication.MESURL, map,
								"TimeOutLogin");
					}
				});
		// 显示
		normalDialog.show();
	}

	private void showNormalDialog(String Title, String msg) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				TongYongActivity.this);
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

	// 返回键判断（在按一次退出）
	private boolean mIsExit;

	@Override
	/**
	 * 双击返回键退出
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果只有一个Activity在运行就退出
		if (myapp.getListCount() == 1) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (mIsExit) {
					this.finish();

				} else {
					ToastUtil.showToast(this, "再按一次退出", Toast.LENGTH_SHORT);
					mIsExit = true;
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							mIsExit = false;
						}
					}, 2000);
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 判断制程和设备号
	 * @param jsonObject
	 */
	private boolean ChooseZcnoAndSbid(JSONObject jsonObject){
		
		return true;
	}
	
	/**
	 * 获取下框的值
	 */
	OnItemSelectedListener SelectValueListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			switch (position) {
			case 0: //固晶
				zcno = "11";
				mboxTab.setVisibility(View.VISIBLE);
				oldMboxText.setText("固晶料盒"); // 旧料盒文本
				if (newMboxText.getVisibility() == View.VISIBLE) { // 新料盒文本
					newMboxText.setVisibility(View.GONE);
				}
				if (yimei_tongyong_newMbox.getVisibility() == View.VISIBLE) { // 新料盒文本框
					yimei_tongyong_newMbox.setVisibility(View.GONE);
				}
				shangliao.setVisibility(View.VISIBLE);
				kaigong.setVisibility(View.VISIBLE);
				clearEditText();
				break;
			case 1:  //固2
				zcno = "12";
				shangliao.setVisibility(View.VISIBLE);
				kaigong.setVisibility(View.VISIBLE);
				mboxTab.setVisibility(View.GONE);
				nextEditFocus(yimei_user_edt);
				clearEditText();
				break;
			case 2:  //固3
				zcno = "13";
				shangliao.setVisibility(View.VISIBLE);
				kaigong.setVisibility(View.VISIBLE);
				mboxTab.setVisibility(View.GONE);
				nextEditFocus(yimei_user_edt);
				clearEditText();
				break;
			case 3:  //固晶烘烤1
				zcno = "1A";
				mboxTab.setVisibility(View.GONE);
				shangliao.setVisibility(View.GONE);
				nextEditFocus(yimei_user_edt);
				clearEditText();
				break;
			case 4:  //固晶烘烤2
				zcno = "1B";
				mboxTab.setVisibility(View.GONE);
				shangliao.setVisibility(View.GONE);
				nextEditFocus(yimei_user_edt);
				clearEditText();
				break;
			case 5: //焊接
				zcno = "21";
				mboxTab.setVisibility(View.VISIBLE);
				oldMboxText.setText("固晶料盒");
				newMboxText.setText("焊接料盒");
				if (yimei_tongyong_newMbox.getVisibility() == View.GONE) {
					yimei_tongyong_newMbox.setVisibility(View.VISIBLE);
				}
				if (newMboxText.getVisibility() == View.GONE) {
					newMboxText.setVisibility(View.VISIBLE);
				}

				yimei_tongyong_oldMbox.setVisibility(View.VISIBLE);
				yimei_tongyong_newMbox.setVisibility(View.VISIBLE);
				shangliao.setVisibility(View.VISIBLE);
				kaigong.setVisibility(View.VISIBLE);
				clearEditText();
				break;
			case 6: //点胶
				zcno = "31";
				mboxTab.setVisibility(View.VISIBLE);
				oldMboxText.setText("焊接料盒");
				newMboxText.setText("点胶料盒");
				if (yimei_tongyong_newMbox.getVisibility() == View.GONE) {
					yimei_tongyong_newMbox.setVisibility(View.VISIBLE);
				}
				if (newMboxText.getVisibility() == View.GONE) {
					newMboxText.setVisibility(View.VISIBLE);
				}
				shangliao.setVisibility(View.GONE);
				if (kaigong.getVisibility() == View.GONE) {
					kaigong.setVisibility(View.VISIBLE);
				}
				clearEditText();
				break;
			case 7:  //点胶后烘烤
				zcno = "41";
				mboxTab.setVisibility(View.GONE);
				shangliao.setVisibility(View.GONE);
				nextEditFocus(yimei_user_edt);
				clearEditText();
				break;
			}
//			ToastUtil.showToast(gujingActivity,zcno,0);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 切换工序清除文本
	 */
	private void clearEditText() {
		yimei_user_edt.setText("");
		yimei_equipment_edt.setText("");
		yimei_proNum_edt.setText("");
		yimei_tongyong_newMbox.setText("");
		yimei_tongyong_oldMbox.setText("");
		IsMbox_old = true;
		IsMbox_new = true;
		if(!zcno.equals("31")){
			currSlkid = null;
		}
		MyApplication.nextEditFocus(yimei_user_edt);
		yimei_user_edt.selectAll();
	}

	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_tongyong_oldMbox) {
				if (!hasFocus) {
					if(!yimei_tongyong_oldMbox.getText().toString().toUpperCase().trim().equals("")){
						 Map<String, String> queryMBox1 =
						 MyApplication.QueryBatNo("MBOXQUERY","~id='"+yimei_tongyong_oldMbox.getText().toString().toUpperCase().trim()+"'");
						 httpRequestQueryRecord(MyApplication.MESURL, queryMBox1,
						 "Old_QueryMboX");
					}
				} else {
					yimei_tongyong_oldMbox.setSelectAllOnFocus(true);
					InputHidden(); // 隐藏键盘
				}
			}
			if (v.getId() == R.id.yimei_tongyong_newMbox) {
				if (!hasFocus) {
					 if(!yimei_tongyong_newMbox.getText().toString().toUpperCase().trim().equals("")){						 
						 Map<String, String> queryMBox =
								 MyApplication.QueryBatNo("MBOXQUERY","~id='"+yimei_tongyong_newMbox.getText().toString().toUpperCase().trim()+"'");
						 httpRequestQueryRecord(MyApplication.MESURL, queryMBox,
								 "New_QueryMboX");
					 }
				} else {
					yimei_tongyong_newMbox.setSelectAllOnFocus(true);
					InputHidden(); // 隐藏键盘
				}
			}
			if (v.getId() == R.id.yimei_user_edt) {
				if (!hasFocus) {
					zuoyeyuan = yimei_user_edt.getText().toString().trim();
				} else {
					yimei_tongyong_oldMbox.setSelectAllOnFocus(true);
					InputHidden(); // 隐藏键盘
				}
			}
			if (v.getId() == R.id.yimei_equipment_edt) {
				if (hasFocus) {
					Log.i("foucus", "设备号获取焦点");
					yimei_equipment_edt.setSelectAllOnFocus(true);
					InputHidden(); // 隐藏键盘
				} else {
					shebeihao = yimei_equipment_edt.getText().toString().trim(); // 失去焦点给设备号赋值
					shebeihao = shebeihao.toUpperCase().trim();
					yimei_equipment_edt.setText(shebeihao);
				}
			}
			if (v.getId() == R.id.yimei_proNum_edt) {
				if (hasFocus) {
					yimei_proNum_edt.setSelectAllOnFocus(true);
					InputHidden(); // 隐藏键盘
				}
			}
		}
	};

	/**
	 * 键盘回车事件
	 */
	OnEditorActionListener editEnter = new OnEditorActionListener() {

		@SuppressLint("DefaultLocale")
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean flag = false;
			if (v.getId() == R.id.yimei_user_edt) { // 作业员回车
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_user_edt.getText().toString().trim().equals("")
							|| yimei_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						return false;
					}
					zuoyeyuan = yimei_user_edt.getText().toString().trim();
					nextEditFocus((EditText) findViewById(R.id.yimei_equipment_edt));
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_equipment_edt) { // 设备号回车
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					shebeihao = yimei_equipment_edt.getText().toString().trim();
					shebeihao = shebeihao.toUpperCase().trim();
					yimei_equipment_edt.setText(shebeihao);
					if (yimei_user_edt.getText().toString().trim().equals("")
							|| yimei_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_user_edt));
						return false;
					}
					if (yimei_equipment_edt.getText().toString().trim()
							.equals("")
							|| yimei_equipment_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "设备号不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_equipment_edt));
						return false;
					} else {
						Log.i("foucus", "设备号回车");
						sbidGetData(); // 设备号的回车键
						/*if (!zcno.equals("41")) {
							nextEditFocus((EditText) findViewById(R.id.yimei_tongyong_oldMbox));
						} else {
							nextEditFocus((EditText) findViewById(R.id.yimei_proNum_edt));
						}*/
						return true;
					}
				}
			}
			if (v.getId() == R.id.yimei_tongyong_oldMbox) { // 旧料盒
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_user_edt.getText().toString().trim().equals("")
							|| yimei_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_user_edt));
						return false;
					}
					if (yimei_equipment_edt.getText().toString().trim()
							.equals("")
							|| yimei_equipment_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "设备号不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_equipment_edt));
						return false;
					}
					if (yimei_tongyong_oldMbox.getText().toString().trim()
							.equals("")
							|| yimei_tongyong_oldMbox.getText().toString()
									.trim() == null) {
						if (zcno.equals("11")) {
							ToastUtil
									.showToast(gujingActivity, "固晶料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return false;
						} else if (zcno.equals("21")) {
							ToastUtil
									.showToast(gujingActivity, "固晶料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return false;
						} else if (zcno.equals("31")) {
							ToastUtil
									.showToast(gujingActivity, "焊接料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return false;
						}
					}
					Map<String, String> queryMBox = MyApplication.QueryBatNo(
							"MBOXQUERY", "~id='"
									+ yimei_tongyong_oldMbox.getText()
											.toString().toUpperCase().trim()
									+ "'");
					httpRequestQueryRecord(MyApplication.MESURL, queryMBox,
							"Old_QueryMboX");
				}
			}
			if (v.getId() == R.id.yimei_tongyong_newMbox) { // 新料盒
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_user_edt.getText().toString().trim().equals("")
							|| yimei_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_user_edt));
						return false;
					}
					if (yimei_equipment_edt.getText().toString().trim()
							.equals("")
							|| yimei_equipment_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "设备号不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_equipment_edt));
						return false;
					}
					// 旧料盒
					if (yimei_tongyong_oldMbox.getText().toString().trim()
							.equals("")
							|| yimei_tongyong_oldMbox.getText().toString()
									.trim() == null) {
						if (zcno.equals("11")) {
							ToastUtil
									.showToast(gujingActivity, "固晶料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return false;
						} else if (zcno.equals("21")) {
							ToastUtil
									.showToast(gujingActivity, "固晶料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return false;
						} else if (zcno.equals("31")) {
							ToastUtil
									.showToast(gujingActivity, "焊接料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return false;
						}
					}
					// 新料盒
					if (yimei_tongyong_newMbox.getText().toString().trim()
							.equals("")
							|| yimei_tongyong_newMbox.getText().toString()
									.trim() == null) {
						if (zcno.equals("21")) {
							ToastUtil
									.showToast(gujingActivity, "焊接料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_newMbox);
							return false;
						} else if (zcno.equals("31")) {
							ToastUtil
									.showToast(gujingActivity, "点胶料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_newMbox);
							return false;
						}
					}
					Map<String, String> queryMBox = MyApplication.QueryBatNo(
							"MBOXQUERY", "~id='"
									+ yimei_tongyong_newMbox.getText()
											.toString().toUpperCase().trim()
									+ "'");
					httpRequestQueryRecord(MyApplication.MESURL, queryMBox,
							"New_QueryMboX");
				}
			}
			if (v.getId() == R.id.yimei_proNum_edt) { // 生产批号的回车事件
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_user_edt.getText().toString().trim().equals("")
							|| yimei_user_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "作业员不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_user_edt));
						return false;
					}
					if (yimei_equipment_edt.getText().toString().trim()
							.equals("")
							|| yimei_equipment_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "设备号不能为空", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_equipment_edt));
						return false;
					}
					// 旧料盒
					if (yimei_tongyong_oldMbox.getText().toString().trim()
							.equals("")
							|| yimei_tongyong_oldMbox.getText().toString()
									.trim() == null) {
						if (zcno.equals("11")) {
							ToastUtil
									.showToast(gujingActivity, "固晶料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return false;
						} else if (zcno.equals("21")) {
							ToastUtil
									.showToast(gujingActivity, "固晶料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return false;
						} else if (zcno.equals("31")) {
							ToastUtil
									.showToast(gujingActivity, "焊接料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_oldMbox);
							return false;
						}
					}
					// 新料盒
					if (yimei_tongyong_newMbox.getText().toString().trim()
							.equals("")
							|| yimei_tongyong_newMbox.getText().toString()
									.trim() == null) {
						if (zcno.equals("21")) {
							ToastUtil
									.showToast(gujingActivity, "焊接料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_newMbox);
							return false;
						} else if (zcno.equals("31")) {
							ToastUtil
									.showToast(gujingActivity, "点胶料盒号不能为空~", 0);
							MyApplication.nextEditFocus(yimei_tongyong_newMbox);
							return false;
						}
					}
					if (yimei_proNum_edt.getText().toString().trim().equals("")
							|| yimei_proNum_edt.getText().toString().trim() == null) {
						ToastUtil.showToast(gujingActivity, "批次号为空~", 0);
						nextEditFocus((EditText) findViewById(R.id.yimei_proNum_edt));
						Log.i("foucus", "批次号回车");
						return false;
					} else {
						sid1GetData(); // 生产批号回车事件
//						nextEditFocus((EditText) findViewById(R.id.yimei_proNum_edt));
//						yimei_proNum_edt.selectAll();
						flag = true;
					}
				}
			}
			return flag;
		}
	};

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
				response(httpPost);
				b.putString("type", type);
				Message m = new Message();
				m.setData(b);
				handler.sendMessage(m);
			}
		}).start();
	}

	public void response(String http) {
		Log.i("jsonjson", http);
	}

	/**
	 * 焦点跳转
	 * 
	 * @param Nextid
	 */
	private void nextEditFocus(EditText Nextid) {
		Nextid.setFocusable(true);
		Nextid.setFocusableInTouchMode(true);
		Nextid.requestFocus();
		Nextid.findFocus();
	}

}
