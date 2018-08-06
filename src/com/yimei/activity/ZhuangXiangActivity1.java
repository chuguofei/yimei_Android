package com.yimei.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.app.TabActivity;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.ipqc.IPQC_shoujian;
import com.yimei.adapter.ZhuangXiangAdapter;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

/**
 * 辅助：
 *  PKLIST：装箱批次号查询
 *  ERPCKNO:装箱CK单查询
 *  TTM0： //查询入库批次号 checkid=1
 * @author Administrator
 *
 */
public class ZhuangXiangActivity1 extends TabActivity {

	private EditText yimei_zhuangxiang_jinbanren,
			yimei_zhuangxiang_canpindaihao,yimei_zhuangxiang_canpinxinghao,yimei_zhuangxiang_chukuNum,
			yimei_zhuangxiang_shoudingdanhao, yimei_zhuangxiang_chukushenqing,
			yimei_zhuangxiang_manxiangNum, yimei_zhuangxiang_bat_no,
			yimei_zhuangxiang_Num, yimei_zhuangxiang_bincode;
	private Button zhuangxiang_add;

	static MyApplication myapp;
	public static ZhuangXiangActivity1 zhuangxiangActivity;
	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private String chukushenqing; // 出库申请的文本 （查询当前出货申请单号的子表的所有数量是否大于出库数量）
	private JSONObject chukushenqingJson; // 出库申请的json串
	private String zuoyeyuan;
	private String zhuangxiang_bat_no; // 装箱批次号
	private Integer manxiangshuliang = 0; // 满箱数量 判断是否超出
	private int cidgagarin = 1; // 项次号自增
	private JSONObject cus_pnJsonObject; // 往适配器添加数据
	private static ListView mListView;
	private ZhuangXiangAdapter ZhuangXiangAdapter;
	private Integer AllQty = 0; // 子表所数量的和
	private String canpinmingcheng; // 产品名称
	private JSONObject cus_pnQueryIspackingJson; // 没有包装过的json提交到服务器
	private String sid; // sid
 	private int checkNum = 0; //取showdialog选中的下标
 	Map<Integer,JSONObject> dig_map = new HashMap<Integer,JSONObject>(); //存放选择的机型 （ 判断选择的下标）
	private JSONObject jsonObjEdit= new JSONObject(); //给界面文本框赋值 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_zhuangxiang);
		Application app = getApplication();
		myapp = (MyApplication) app;
		myapp.addActivity_(this);
		zhuangxiangActivity = this;
		myapp.removeActivity_(LoginActivity.loginActivity);// 销毁登录

		mListView = (ListView) findViewById(R.id.zhuangxiang_scroll_list);
		TabHost tabHost = this.getTabHost();

		TabSpec tab1 = tabHost.newTabSpec("tab1").setIndicator("扫描区")
				.setContent(R.id.zhuangxiang_tab1);
		tabHost.addTab(tab1);
		TabSpec tab2 = tabHost.newTabSpec("tab2").setIndicator("明细")
				.setContent(R.id.zhuangxiang_tab2);
		tabHost.addTab(tab2);

		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.zhuangxiang_scroll_title);
		// 添加头滑动事件
		GeneralCHScrollView.add(headerScroll);
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
				int focusId = rootview.findFocus().getId();
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
				if (tag.equals("装箱入库申请")) { // 批次号
					if (yimei_zhuangxiang_jinbanren.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(), "请输入经办人~", 0);
						MyApplication.nextEditFocus(yimei_zhuangxiang_jinbanren); // 跳转到经办人
						return;
					}
					// 换申请号清空适配器
					if (chukushenqingJson != null
							&& !yimei_zhuangxiang_chukushenqing.getText()
									.toString().trim().equals(chukushenqingJson)) {
						if (ZhuangXiangAdapter != null) {
							ZhuangXiangAdapter = null;
							mListView.setAdapter(ZhuangXiangAdapter);
						}
					}
					yimei_zhuangxiang_chukushenqing.setText(barcodeData);
					chukushenqing = yimei_zhuangxiang_chukushenqing.getText()
							.toString().trim();
					QuerySpno(chukushenqing); // 查询申请单号
				}
				
				if (tag.equals("装箱作业批次号")) { // 批次号
					if (yimei_zhuangxiang_jinbanren.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(), "请输入经办人~",
								0);
						MyApplication
								.nextEditFocus(yimei_zhuangxiang_jinbanren);
						return;
					}
					if (yimei_zhuangxiang_manxiangNum.getText().toString()
							.trim().equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"请输入满箱数量~", 0);
						MyApplication
								.nextEditFocus(yimei_zhuangxiang_manxiangNum);
						return;
					}
					if (yimei_zhuangxiang_chukushenqing.getText().toString()
							.trim().equals("")) {
						ToastUtil.showToast(getApplicationContext(),
								"请选中出货单号~", 0);
						MyApplication
								.nextEditFocus(yimei_zhuangxiang_chukushenqing);
						return;
					}
					yimei_zhuangxiang_bat_no.setText(barcodeData);
					zhuangxiang_bat_no = yimei_zhuangxiang_bat_no.getText()
							.toString().trim();
					Map<String, String> queryBatNo = MyApplication.QueryBatNo(
							"TTM0", "~bat_no='" + zhuangxiang_bat_no + "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, queryBatNo, null,
							mHander, true, "cus_pnQuery"); // 批次号查询
					MyApplication.nextEditFocus(yimei_zhuangxiang_bincode);
				}
				if(tag.equals("装箱作业bin")){
					yimei_zhuangxiang_bincode.setText(barcodeData);
					if (yimei_zhuangxiang_chukushenqing.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(), "请输入出库申请单~", 0);
						MyApplication
								.nextEditFocus(yimei_zhuangxiang_chukushenqing);
						return ;
					}
					if (yimei_zhuangxiang_manxiangNum.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(), "请输入满箱数量~", 0);
						MyApplication.nextEditFocus(yimei_zhuangxiang_manxiangNum);
						return ;
					}
					if (yimei_zhuangxiang_bat_no.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(), "请输入批次号~", 0);
						MyApplication.nextEditFocus(yimei_zhuangxiang_bat_no);
						return ;
					}
					if (yimei_zhuangxiang_bincode.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(), "请输入bincode~",
								0);
						MyApplication.nextEditFocus(yimei_zhuangxiang_bincode);
						return ;
					}
					boolean isNum_manxiang = IsNum_manxiang(); // 判断满箱数量是否正确
					if (!isNum_manxiang) {
						return ;
					}
					MyApplication.nextEditFocus(yimei_zhuangxiang_Num); // 跳转到数量
				}
				if(tag.equals("装箱作业qty")){
					yimei_zhuangxiang_Num.setText(barcodeData);
					if (yimei_zhuangxiang_chukushenqing.getText().toString().trim()
							.equals("")) {
						ToastUtil
								.showToast(getApplicationContext(), "请输入出库申请单~", 0);
						MyApplication.nextEditFocus(yimei_zhuangxiang_chukushenqing);
						return ;
					}
					if (yimei_zhuangxiang_manxiangNum.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(), "请输入满箱数量~", 0);
						MyApplication.nextEditFocus(yimei_zhuangxiang_manxiangNum);
						return ;
					}
					if (yimei_zhuangxiang_bat_no.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(), "请输入批次号~", 0);
						MyApplication.nextEditFocus(yimei_zhuangxiang_bat_no);
						return ;
					}
					if (yimei_zhuangxiang_Num.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(), "请输入数量~", 0);
						MyApplication.nextEditFocus(yimei_zhuangxiang_Num);
						return ;
					}
					if (yimei_zhuangxiang_bincode.getText().toString().trim()
							.equals("")) {
						ToastUtil.showToast(getApplicationContext(), "请输入bincode~",
								0);
						MyApplication.nextEditFocus(yimei_zhuangxiang_bincode);
						return ;
					}
					if (chukushenqingJson == null) {
						ToastUtil.showToast(getApplicationContext(), "没有该单号,请核对~",
								0);
						MyApplication
								.nextEditFocus(yimei_zhuangxiang_chukushenqing);
						return ;
					} else {
						boolean isNum_manxiang = IsNum_manxiang(); // 判断满箱数量是否正确
						if (!isNum_manxiang) {
							return ;
						}
						Map<String, String> queryBatNo = MyApplication.QueryBatNo(
								"MESPACKINGA", "~bat_no='" + zhuangxiang_bat_no
										+ "'");
						OkHttpUtils.getInstance().getServerExecute(
								MyApplication.MESURL, null, queryBatNo, null,
								mHander, true, "Num_cus_pnQueryIspacking"); // 批次号查询（是否包装过）|
																			// 数量
					}
				}
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(barcodeReceiver, new IntentFilter(
				MyApplication.INTENT_ACTION_SCAN_RESULT)); // 注册广播
		yimei_zhuangxiang_jinbanren = (EditText) findViewById(R.id.yimei_zhuangxiang_jinbanren);
		yimei_zhuangxiang_canpindaihao = (EditText) findViewById(R.id.yimei_zhuangxiang_canpindaihao);
		yimei_zhuangxiang_canpinxinghao = (EditText) findViewById(R.id.yimei_zhuangxiang_canpinxinghao);
		yimei_zhuangxiang_chukuNum = (EditText) findViewById(R.id.yimei_zhuangxiang_chukuNum);
//		yimei_zhuangxiang_shoudingdanhao = (EditText) findViewById(R.id.yimei_zhuangxiang_shoudingdanhao);
		yimei_zhuangxiang_manxiangNum = (EditText) findViewById(R.id.yimei_zhuangxiang_manxiangNum);
		yimei_zhuangxiang_bat_no = (EditText) findViewById(R.id.yimei_zhuangxiang_bat_no);
		yimei_zhuangxiang_Num = (EditText) findViewById(R.id.yimei_zhuangxiang_Num);
		yimei_zhuangxiang_bincode = (EditText) findViewById(R.id.yimei_zhuangxiang_bincode);
		yimei_zhuangxiang_chukushenqing = (EditText) findViewById(R.id.yimei_zhuangxiang_chukushenqing);
		zhuangxiang_add = (Button) findViewById(R.id.zhuangxiang_add);
		yimei_zhuangxiang_canpinxinghao.setKeyListener(null);
		yimei_zhuangxiang_canpindaihao.setKeyListener(null);
		yimei_zhuangxiang_chukuNum.setKeyListener(null);
//		yimei_zhuangxiang_shoudingdanhao.setKeyListener(null);

		yimei_zhuangxiang_jinbanren.setOnEditorActionListener(editEnter);
		yimei_zhuangxiang_chukuNum.setOnEditorActionListener(editEnter);
		yimei_zhuangxiang_chukushenqing.setOnEditorActionListener(editEnter);
		yimei_zhuangxiang_bat_no.setOnEditorActionListener(editEnter);
		yimei_zhuangxiang_manxiangNum.setOnEditorActionListener(editEnter);
		yimei_zhuangxiang_Num.setOnEditorActionListener(editEnter);
		yimei_zhuangxiang_bincode.setOnEditorActionListener(editEnter);

		yimei_zhuangxiang_jinbanren.setOnFocusChangeListener(EditGetFocus);
		yimei_zhuangxiang_chukushenqing.setOnFocusChangeListener(EditGetFocus);
		yimei_zhuangxiang_manxiangNum.setOnFocusChangeListener(EditGetFocus);
		yimei_zhuangxiang_bat_no.setOnFocusChangeListener(EditGetFocus);
		yimei_zhuangxiang_bincode.setOnFocusChangeListener(EditGetFocus);
		yimei_zhuangxiang_Num.setOnFocusChangeListener(EditGetFocus);

		// 新增单号
		zhuangxiang_add.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
//				chukushenqingJson = null;
//				QuerySpno(chukushen);
				if(chukushenqingJson==null){
					ToastUtil.showToastLocation(getApplicationContext(),"请选择出库号~",0);
					return ;
				}
				AddDanHao("提交","确定要换新的单号吗？");
//				show(chukushenqingJson);
			}
		});
	}

	/**
	 * 换单号
	 * @param msg
	 */
	private void AddDanHao(String title, String msg) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				ZhuangXiangActivity1.this);
		normalDialog.setTitle(title);
		normalDialog.setMessage(Html.fromHtml("<font color='red'>" + msg
				+ "</font>"));
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						yimei_zhuangxiang_chukushenqing.setText("");
						yimei_zhuangxiang_manxiangNum.setText("");
						yimei_zhuangxiang_bat_no.setText("");
						yimei_zhuangxiang_bincode.setText("");
						yimei_zhuangxiang_Num.setText("");
						yimei_zhuangxiang_canpindaihao.setText("");
						yimei_zhuangxiang_canpinxinghao.setText("");
						yimei_zhuangxiang_chukuNum.setText("");
						cidgagarin = 1;
						if(ZhuangXiangAdapter!=null){
							mListView.setAdapter(null);
							ZhuangXiangAdapter.notifyDataSetChanged();
						}
						chukushenqingJson = null;
						MyApplication.nextEditFocus(yimei_zhuangxiang_chukushenqing);
					}
				});
		normalDialog.setNegativeButton("取消",null);
		normalDialog.show();
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
	 * 清空文本框
	 */
	private void clearEditText() {
		yimei_zhuangxiang_bat_no.setText("");
		yimei_zhuangxiang_Num.setText("");
		yimei_zhuangxiang_bincode.setText("");
	}

	/**
	 * 查询申请单号（用于新增箱码和第一次查询）
	 */
	private void QuerySpno(String chukushenqing) {
		Map<String, String> map = MyApplication.QueryBatNo("ERPCKNO",
				"~ck_no='" + chukushenqing + "'");
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				map, null, mHander, true, "Querychukushenqing"); // 出库申请查询
	}

	/**
	 * 判断文本框失去|获取焦点
	 */
	OnFocusChangeListener EditGetFocus = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.yimei_zhuangxiang_jinbanren) {
				if (!hasFocus) {
					zuoyeyuan = yimei_zhuangxiang_jinbanren.getText()
							.toString().trim();
				} else {
					yimei_zhuangxiang_jinbanren.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_zhuangxiang_chukushenqing) {
				if (hasFocus) {
					yimei_zhuangxiang_chukushenqing.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_zhuangxiang_manxiangNum) {
				if (hasFocus) {
					yimei_zhuangxiang_manxiangNum.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_zhuangxiang_bat_no) {
				if (hasFocus) {
					yimei_zhuangxiang_bat_no.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_zhuangxiang_bincode) {
				if (hasFocus) {
					yimei_zhuangxiang_bincode.setSelectAllOnFocus(true);
				}
			}
			if (v.getId() == R.id.yimei_zhuangxiang_Num) {
				if (hasFocus) {
					yimei_zhuangxiang_Num.setSelectAllOnFocus(true);
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
			if (v.getId() == R.id.yimei_zhuangxiang_jinbanren) { // 经办人
				if (yimei_zhuangxiang_jinbanren.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入经办人~", 0);
					return false;
				}
				zuoyeyuan = yimei_zhuangxiang_jinbanren.getText().toString()
						.trim();
				MyApplication.nextEditFocus(yimei_zhuangxiang_chukushenqing);
			}
			if (v.getId() == R.id.yimei_zhuangxiang_chukushenqing) { // 申请号
				if (yimei_zhuangxiang_jinbanren.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入经办人~", 0);
					MyApplication.nextEditFocus(yimei_zhuangxiang_jinbanren); // 跳转到经办人
					return false;
				}
				dig_map.clear();
				jsonObjEdit.clear();
				// 换申请号清空适配器
				if (chukushenqing != null
						&& !yimei_zhuangxiang_chukushenqing.getText()
								.toString().trim().equals(chukushenqing)) {
					if (ZhuangXiangAdapter != null) {
						ZhuangXiangAdapter = null;
						mListView.setAdapter(ZhuangXiangAdapter);
					}
				}
				chukushenqing = yimei_zhuangxiang_chukushenqing.getText()
						.toString().trim();
				QuerySpno(chukushenqing); // 查询申请单号
				flag = true;
			}
			if (v.getId() == R.id.yimei_zhuangxiang_manxiangNum) { // 满箱数量
				if (yimei_zhuangxiang_jinbanren.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入经办人~", 0);
					MyApplication.nextEditFocus(yimei_zhuangxiang_jinbanren);
					return false;
				}
				if (yimei_zhuangxiang_manxiangNum.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入满箱数量~", 0);
					MyApplication.nextEditFocus(yimei_zhuangxiang_manxiangNum);
					return false;
				}
				if (chukushenqingJson == null) {
					ToastUtil.showToast(getApplicationContext(), "没有该单号,请核对~",
							0);
					MyApplication
							.nextEditFocus(yimei_zhuangxiang_chukushenqing);
					return false;
				} else {
					boolean isNum_manxiang = IsNum_manxiang(); // 判断满箱数量是否正确
					if (!isNum_manxiang) {
						return false;
					}
//					chukushenqingJson.put("plqty", manxiangshuliang);
					chukushenqingJson.put("sys_stated", "3"); // 新增
					chukushenqingJson.put("sopr", zuoyeyuan);
					chukushenqingJson.put("sbuid","H0003");
					chukushenqingJson.put("state","0");
					chukushenqingJson.put("mkdate",MyApplication.GetServerNowTime());
					chukushenqingJson.put("cprn","1");
					chukushenqingJson.put("wh","01");
					chukushenqingJson.put("unit","1");
					chukushenqingJson.put("prtqty","1");
					chukushenqingJson.put("sorg",MyApplication.sorg);
					chukushenqingJson.put("pkid","0");
					chukushenqingJson.put("ckqty",chukushenqingJson.getInteger("qty")==null?0:chukushenqingJson.getInteger("qty")); //出货数量
					chukushenqingJson.put("pkqty",manxiangshuliang); //满箱数量
					chukushenqingJson.put("spno",yimei_zhuangxiang_chukushenqing.getText().toString().toUpperCase().trim()); // 出库单
					Map<String, String> mesIdMap = MyApplication
							.httpMapKeyValueMethod(MyApplication.DBID,
									"savedata", MyApplication.user,
									chukushenqingJson.toString(), "H0003WEB",
									"1"); // 出库申请添加
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, mesIdMap, null,
							mHander, true, "AddChukushenqing"); // 出库申请添加
					MyApplication.nextEditFocus(yimei_zhuangxiang_bat_no);
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_zhuangxiang_bat_no) { // 批次号
				if (yimei_zhuangxiang_chukushenqing.getText().toString().trim()
						.equals("")) {
					ToastUtil
							.showToast(getApplicationContext(), "请输入出库申请单~", 0);
					MyApplication
							.nextEditFocus(yimei_zhuangxiang_chukushenqing);
					return false;
				}
				if (yimei_zhuangxiang_manxiangNum.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入满箱数量~", 0);
					MyApplication.nextEditFocus(yimei_zhuangxiang_manxiangNum);
					return false;
				}
				if (yimei_zhuangxiang_bat_no.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入批次号~", 0);
					return false;
				}
				boolean isNum_manxiang = IsNum_manxiang(); // 判断满箱数量是否正确
				if (!isNum_manxiang) {
					return false;
				}
				zhuangxiang_bat_no = yimei_zhuangxiang_bat_no.getText()
						.toString().trim();
				Map<String, String> queryBatNo = MyApplication.QueryBatNo(
						"TTM0", "~bat_no='" + zhuangxiang_bat_no + "'");
				OkHttpUtils.getInstance().getServerExecute(
						MyApplication.MESURL, null, queryBatNo, null, mHander,
						true, "cus_pnQuery"); // 批次号查询
			}
			if (v.getId() == R.id.yimei_zhuangxiang_Num) { // 数量
				if (yimei_zhuangxiang_chukushenqing.getText().toString().trim()
						.equals("")) {
					ToastUtil
							.showToast(getApplicationContext(), "请输入出库申请单~", 0);
					MyApplication
							.nextEditFocus(yimei_zhuangxiang_chukushenqing);
					return false;
				}
				if (yimei_zhuangxiang_manxiangNum.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入满箱数量~", 0);
					MyApplication.nextEditFocus(yimei_zhuangxiang_manxiangNum);
					return false;
				}
				if (yimei_zhuangxiang_bat_no.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入批次号~", 0);
					MyApplication.nextEditFocus(yimei_zhuangxiang_bat_no);
					return false;
				}
				if (yimei_zhuangxiang_Num.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入数量~", 0);
					MyApplication.nextEditFocus(yimei_zhuangxiang_Num);
					return false;
				}
				if (yimei_zhuangxiang_bincode.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入bincode~",
							0);
					MyApplication.nextEditFocus(yimei_zhuangxiang_bincode);
					return false;
				}
				if (chukushenqingJson == null) {
					ToastUtil.showToast(getApplicationContext(), "没有该单号,请核对~",
							0);
					MyApplication
							.nextEditFocus(yimei_zhuangxiang_chukushenqing);
					return false;
				} else {
					boolean isNum_manxiang = IsNum_manxiang(); // 判断满箱数量是否正确
					if (!isNum_manxiang) {
						return false;
					}
					Map<String, String> queryBatNo = MyApplication.QueryBatNo(
							"MESPACKINGA", "~bat_no='" + zhuangxiang_bat_no
									+ "'");
					OkHttpUtils.getInstance().getServerExecute(
							MyApplication.MESURL, null, queryBatNo, null,
							mHander, true, "Num_cus_pnQueryIspacking"); // 批次号查询（是否包装过）|
																		// 数量
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_zhuangxiang_bincode) { // bincode
				if (yimei_zhuangxiang_chukushenqing.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入出库申请单~", 0);
					MyApplication
							.nextEditFocus(yimei_zhuangxiang_chukushenqing);
					return false;
				}
				if (yimei_zhuangxiang_manxiangNum.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入满箱数量~", 0);
					MyApplication.nextEditFocus(yimei_zhuangxiang_manxiangNum);
					return false;
				}
				if (yimei_zhuangxiang_bat_no.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入批次号~", 0);
					MyApplication.nextEditFocus(yimei_zhuangxiang_bat_no);
					return false;
				}
				if (yimei_zhuangxiang_bincode.getText().toString().trim()
						.equals("")) {
					ToastUtil.showToast(getApplicationContext(), "请输入bincode~",
							0);
					MyApplication.nextEditFocus(yimei_zhuangxiang_bincode);
					return false;
				}
				boolean isNum_manxiang = IsNum_manxiang(); // 判断满箱数量是否正确
				if (!isNum_manxiang) {
					return false;
				}
				MyApplication.nextEditFocus(yimei_zhuangxiang_Num); // 跳转到数量
				flag = true;
			}
			return flag;
		}
	};

	/**
	 * 判断满箱数量输入是否是数字
	 */
	private boolean IsNum_manxiang() {
		boolean flag = true;
		String manxiangNum = yimei_zhuangxiang_manxiangNum.getText().toString()
				.trim();
		if (!manxiangNum.equals("")) {
			try {
				Integer num = Integer.parseInt(manxiangNum);
				manxiangshuliang = Integer.parseInt(manxiangNum); // 全局满箱数量
			} catch (Exception e) {
				ToastUtil.showToast(getApplicationContext(), "请输入正确的满箱数量~", 0);
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * 数量回车
	 * 
	 * @return
	 */
	private boolean NumEnter() {
		boolean flag = true;
		try {
			Integer.parseInt(yimei_zhuangxiang_Num.getText().toString().trim());
		} catch (Exception e) {
			ToastUtil.showToast(getApplicationContext(), "请输入正确的数字~", 0);
			flag = false;
			return false;
		}
		if (Integer.parseInt(yimei_zhuangxiang_Num.getText().toString().trim()) > manxiangshuliang) {
			ToastUtil.showToast(getApplicationContext(), "满箱数量已超出,最多可以扫："
					+ manxiangshuliang + "", 0);
			flag = false;
			return false;
		}
		// 判断当前输入的数量是否超过满箱数量
		if(!panduan_manxiangNum(Integer.parseInt(yimei_zhuangxiang_Num.getText().toString().trim()))){
			ToastUtil.showToast(getApplicationContext(), "已超出满箱数量!", 0);
			return false;
		};
		// 判断所有的数量加本次的数量是否超过出库数量
		if (Integer.parseInt(yimei_zhuangxiang_Num.getText().toString().trim())
				+ AllQty > Integer.parseInt(yimei_zhuangxiang_chukuNum
				.getText().toString().trim())) {
			ToastUtil.showToast(getApplicationContext(), "出库数量已超出~", 0);
			flag = false;
			return false;
		}

		if (flag == true) {
			// 批次号没有查到添加
			JSONObject jsonobj = new JSONObject();
			jsonobj.put("cid", cidgagarin); // 项次
			jsonobj.put("bat_no", yimei_zhuangxiang_bat_no.getText().toString().trim());// 批次号
			jsonobj.put("prd_mark", yimei_zhuangxiang_bincode.getText().toString().trim()); // bincode
			jsonobj.put("qty", yimei_zhuangxiang_Num.getText().toString().trim()); // 新增
			jsonobj.put("prd_name", yimei_zhuangxiang_canpinxinghao.getText().toString().trim()); // 产品名称
			jsonobj.put("prd_no", yimei_zhuangxiang_canpindaihao.getText().toString().trim()); // 产品代号
			jsonobj.put("sys_stated", "3"); // 新增
			jsonobj.put("sc_dd", MyApplication.GetServerNowTime()); // sc_dd
			jsonobj.put("unit", "1"); // unit
			jsonobj.put("sid", sid); // 主表的sid
			cidgagarin++;
			Map<String, String> mesIdMap = MyApplication.httpMapKeyValueMethod(
					MyApplication.DBID, "savedata", MyApplication.user,
					jsonobj.toString(), "H0003AWEB", "1"); // 批次号添加
			OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,
					null, mesIdMap, null, mHander, true, "Nocus_pnAddAdapter"); // 批次扫描添加

			List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
			Map<String, String> map = new HashMap<String, String>();
			map.put("zhuangxiang_cid", jsonobj.get("cid").toString());
			map.put("zhuangxiang_bat_no", jsonobj.get("bat_no").toString());
			map.put("zhuangxiang_bincode", jsonobj.get("prd_mark").toString());
			map.put("zhuangxiang_qty", jsonobj.get("qty").toString());
			map.put("zhuangxiang_prd_no", jsonobj.getString("prd_no"));
			mList.add(map);
			if (ZhuangXiangAdapter == null) {
				ZhuangXiangAdapter = new ZhuangXiangAdapter(zhuangxiangActivity, mList);
				mListView.setAdapter(ZhuangXiangAdapter);
				ZhuangXiangAdapter.notifyDataSetChanged();
				MyApplication.nextEditFocus(yimei_zhuangxiang_bat_no);
				yimei_zhuangxiang_bat_no.setText("");
				yimei_zhuangxiang_bincode.setText("");
				yimei_zhuangxiang_Num.setText("");
			} else {
				ZhuangXiangAdapter.listData.add(map);
				ZhuangXiangAdapter.notifyDataSetChanged();
				MyApplication.nextEditFocus(yimei_zhuangxiang_bat_no);
				yimei_zhuangxiang_bat_no.setText("");
				yimei_zhuangxiang_bincode.setText("");
				yimei_zhuangxiang_Num.setText("");
			}
		}
		return flag;
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
						if (string.equals("Querychukushenqing")) {
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 0) { // 申请单没有数据
								ToastUtil.showToast(getApplicationContext(),
										"没有该单号,请核对~", 0);
								chukushenqing = null;
								chukushenqingJson = null;
								clearEditText(); 
								yimei_zhuangxiang_canpinxinghao.setText("");
								yimei_zhuangxiang_canpindaihao.setText("");
								yimei_zhuangxiang_chukuNum.setText("");
								MyApplication
										.nextEditFocus(yimei_zhuangxiang_chukushenqing);
								yimei_zhuangxiang_chukushenqing.selectAll();
								return;
							} else {
								//弹框
//								ListView shenqinghaoQueryShowDig = shenqinghaoQueryShowDig(jsonObject);
								showNormalDialog((JSONArray) jsonObject.get("values"));
							}
						}
						if (string.equals("QuerychukushenqingSum")) { // 查询子表所有的数量
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if (!(Integer.parseInt(jsonObject.get("code")
									.toString()) == 0)) { // 申请单没有数据的和
								// 子表所有的数量和
								String num = ((JSONObject) ((JSONArray) jsonObject
										.get("values")).get(0)).get("qty")
										.toString();
								if(num.indexOf(".")!= -1){									
									num = num.substring(0, num.indexOf("."));
								}
								AllQty = Integer.parseInt(num);
							}
						}
						if (string.equals("AddChukushenqing")) {
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id").toString()) == 0) {
								sid = ((JSONObject) jsonObject.get("data")).get(
										"sid").toString();
							} else {
								ToastUtil.showToast(getApplicationContext(),
										jsonObject.getString("message"), 0);
							}
						}
						if ("cus_pnQuery".equals(string)) { // 批次号查询回来的数据
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if (!(Integer.parseInt(jsonObject.get("code").toString()) == 0)) { // 找到了批次号
								String a1 = null;
								try {
									a1 = ((JSONObject)((JSONArray) jsonObject.get("values")).get(0)).get("prd_no").toString();
								} catch (Exception e) {
									ToastUtil.showToast(getApplicationContext(),"该批次没有机型号，无法匹配！",0);
									return;
								}
								if(!a1.equals(chukushenqingJson.get("prd_no"))){
									ToastUtil.showToast(getApplicationContext(),"该批次的产品出货机型不符~",0);
									MyApplication.nextEditFocus(yimei_zhuangxiang_bat_no);
									yimei_zhuangxiang_bat_no.selectAll();
									return;
								}
								// 拿回来的数量
								Integer num = (Integer) ((JSONObject) ((JSONArray) jsonObject
												.get("values")).get(0)).get("qty");
								// 批次号是否包装
								if (num + AllQty > Integer
										.parseInt(yimei_zhuangxiang_chukuNum
												.getText().toString().trim())) {
									ToastUtil.showToast(getApplicationContext(),
											"出库数量已超出~", 0);
									return;
								}

								Map<String, String> queryBatNo = MyApplication
										.QueryBatNo("MESPACKINGA", "~bat_no='"
												+ zhuangxiang_bat_no + "'");
								OkHttpUtils.getInstance()
										.getServerExecute(MyApplication.MESURL,
												null, queryBatNo, null, mHander,
												true, "cus_pnQueryIspacking"); // 批次号查询（是否包装过）
																				// （在mes存在）
								cus_pnQueryIspackingJson = jsonObject; // 没有包装过的json提交到服务器
							} else {
								// 没找到
								// 批次号是否包装
								Map<String, String> queryBatNo = MyApplication
										.QueryBatNo("MESPACKINGA", "~bat_no='"
												+ zhuangxiang_bat_no + "'");
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, queryBatNo,
										null, mHander, true,
										"cus_pnQueryIspackingAndNoLot_No"); // 批次号查询（在mes不存在）
							}
						}
						if (string.equals("cus_pnQueryIspacking")) { // 包装批次号是否包装过
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
								// 包装过
								ToastUtil.showToast(getApplicationContext(), "《"
										+ zhuangxiang_bat_no + "》已包装过~", 0);
								MyApplication.nextEditFocus(yimei_zhuangxiang_bat_no);
								yimei_zhuangxiang_bat_no.selectAll();
								return;
							} else {
								// 没有包装
								bat_noExist(cus_pnQueryIspackingJson);
							}
						}
						if (string.equals("Num_cus_pnQueryIspacking")) { // 数量查询批次号是否包装过
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
								// 包装过
								ToastUtil.showToast(getApplicationContext(), "《"
										+ zhuangxiang_bat_no + "》已包装过~", 0);
								return;
							} else {
								// 没有包装
								boolean numEnter = NumEnter(); // 添加数据
								if (!numEnter) {
									return;
								}
							}
						}
						if (string.equals("cus_pnQueryIspackingAndNoLot_No")) { // 没有找到批次号
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code").toString()) == 1) {
								// 包装过
								ToastUtil.showToast(getApplicationContext(), "《"
										+ zhuangxiang_bat_no + "》已包装过~", 0);
								return;
							} else {
								// 没有包装
								MyApplication.nextEditFocus(yimei_zhuangxiang_bincode);
							}
						}
						if (string.equals("cus_pnAddAdapter")) { // 往适配器写数据
							JSONObject jsonObject = JSON.parseObject(b.getString(
									"jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id").toString()) == 0) {
								List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
								Map<String, String> map = new HashMap<String, String>();
								map.put("zhuangxiang_cid",
										cus_pnJsonObject.getString("cid"));
								map.put("zhuangxiang_bat_no",
										cus_pnJsonObject.getString("bat_no"));
								map.put("zhuangxiang_bincode",
										cus_pnJsonObject.getString("prd_mark"));
								map.put("zhuangxiang_qty",
										cus_pnJsonObject.getString("qty"));
								map.put("zhuangxiang_prd_no",
										cus_pnJsonObject.getString("prd_no"));
								map.put("zhuangxiang_prd_name",
										cus_pnJsonObject.getString("prd_name"));
								mList.add(map);
								if (ZhuangXiangAdapter == null) {
									ZhuangXiangAdapter = new ZhuangXiangAdapter(
											zhuangxiangActivity, mList);

									mListView.setAdapter(ZhuangXiangAdapter);
									ZhuangXiangAdapter.notifyDataSetChanged();
								} else {
									ZhuangXiangAdapter.listData.add(map);
									ZhuangXiangAdapter.notifyDataSetChanged();
								}
							} else {
								ToastUtil.showToast(getApplicationContext(),
										jsonObject.getString("message"), 0);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						ToastUtil.showToast(getApplicationContext(),e.toString(),0);
					}
				}
			} else {
				Log.i("err", msg.obj.toString());
			}
		}
	};

	private void show(JSONObject jsonObj){
		if(chukushenqingJson==null){
			ToastUtil.showToast(getApplicationContext(),"请选择出货号~",0);
		}else{
			List<Map<String, String>> mList = new ArrayList<Map<String,String>>();
			Map<String,String> map = new HashMap<String,String>();
			map.put("zhuangxiang_dig_chukudan",jsonObj.get("ck_no").toString());
			map.put("zhuangxiang_dig_shoudandan",jsonObj.get("os_no").toString());
			map.put("zhuangxiang_dig_kuhudingdan",jsonObj.get("cus_os_no").toString());
			map.put("zhuangxiang_dig_chuhuobeizhu","");
			map.put("zhuangxiang_dig_prdName",jsonObj.get("prd_name").toString());
			mList.add(map);
//			chukushenqingJson = jsonObj; // 全局
												// 满箱数量回车后需要的json
												// （服务器添加箱码号）
			// 给界面添加数据================================
			canpinmingcheng = jsonObj.get("prd_name").toString();
			yimei_zhuangxiang_canpindaihao.setText(jsonObj.get("prd_no").toString());
			yimei_zhuangxiang_canpinxinghao.setText(jsonObj.get("prd_name").toString());
			if(jsonObj.get("qty").toString().indexOf(".") != -1){
				yimei_zhuangxiang_chukuNum.setText(jsonObj.get("qty").toString().substring(0,jsonObj.get("qty").toString().indexOf(".")));
			}else{
				yimei_zhuangxiang_chukuNum.setText(jsonObj.get("qty").toString());
			}
			/*yimei_zhuangxiang_shoudingdanhao
					.setText(jsonObj.get("os_no")==null?"":jsonObj.get("os_no").toString());*/
			// 给界面添加数据================================
	
			MyApplication.nextEditFocus(yimei_zhuangxiang_manxiangNum);
	
			Map<String, String> map1 = MyApplication.QueryBatNo(
					"SHENQINGSUM", "~spno='" + chukushenqing
							+ "'");
			OkHttpUtils.getInstance().getServerExecute(
					MyApplication.MESURL, null, map1, null,
					mHander, true, "QuerychukushenqingSum"); // 查询当前出货申请单号的子表的所有数量是否大于出库数量
	
			Log.i("jsonObj", jsonObj.toString());
		}
	}
	
	
	
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
			String qty = jsonobj.get("qty").toString();
			String strShow ="出库号："+jsonobj.get("ck_no")+"\n"
					+"机型："+jsonobj.get("prd_name")+"\n"
					+"数量："+qty.substring(0,qty.indexOf("."))+"\n";
			str[i] = strShow;
		}
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				zhuangxiangActivity);
		normalDialog.setTitle("选择CK单");
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setSingleChoiceItems(
			    str, checkNum,
			     new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
//			       dialog.dismiss();
			    	  checkNum = which;
			      }});
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						jsonObjEdit = dig_map.get(checkNum); //取选中的下标
						chukushenqingJson = jsonObjEdit; // 全局
						show(jsonObjEdit); //界面填值
					}
				});
		// 显示
		normalDialog.show();
	}
	
	
	/**
	 * 批次号存在提交服务器
	 * 
	 * @param jsonObject
	 */
	private void bat_noExist(JSONObject jsonObject) { // 批次号存在
		cus_pnJsonObject = (JSONObject) ((JSONArray) jsonObject.get("values")).get(0); // 全局适配器添加数据
		if (Integer.parseInt(cus_pnJsonObject.get("qty").toString()) > manxiangshuliang) {
			ToastUtil.showToast(getApplicationContext(), "满箱数量已超出,最多可以扫："
					+ manxiangshuliang + "", 0);
			return;
		}
		if(!panduan_manxiangNum(Integer.parseInt(cus_pnJsonObject.get("qty").toString()))){
			ToastUtil.showToast(getApplicationContext(), "已超出满箱数量!", 0);
			return;
		}; // 判断出库数据有没有超出
		if (cus_pnJsonObject.containsKey("qty")
				|| cus_pnJsonObject.containsKey("prd_no")
				|| cus_pnJsonObject.containsKey("prd_mark")) {
			cus_pnJsonObject.put("sys_stated", "3"); // 新增
			cus_pnJsonObject.put("cid", cidgagarin); // 项次
			cus_pnJsonObject.put("sid", sid); // 主表的sid
			cus_pnJsonObject.put("unit", "1"); // unit
//			cus_pnJsonObject.put("spno",yimei_zhuangxiang_chukushenqing.getText().toString().toUpperCase().trim()); // unit
			cus_pnJsonObject.put("bat_no", yimei_zhuangxiang_bat_no.getText().toString().trim()); // 主表的sid
			cidgagarin++;
			Map<String, String> mesIdMap = MyApplication.httpMapKeyValueMethod(
					MyApplication.DBID, "savedata", MyApplication.user,
					cus_pnJsonObject.toString(), "H0003AWEB", "1"); // 批次号添加（服务器提交）
			OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,
					null, mesIdMap, null, mHander, true, "cus_pnAddAdapter"); // 批次扫描添加
			MyApplication.nextEditFocus(yimei_zhuangxiang_bat_no);
			yimei_zhuangxiang_bat_no.setText("");
			yimei_zhuangxiang_bat_no.selectAll();
		}
	}

	/**
	 * 判断列表中加起来的数量是否超出满箱数量
	 */
	private boolean panduan_manxiangNum(Integer num) {
		int listNum = 0;
		if (ZhuangXiangAdapter != null) {
			for (int i = 0; i < ZhuangXiangAdapter.getCount(); i++) {
				Map<String, String> map = (Map<String, String>) ZhuangXiangAdapter
						.getItem(i);
				listNum += Integer.parseInt(map.get("zhuangxiang_qty"));
			}
			if (listNum + num > manxiangshuliang) {
				ToastUtil.showToast(getApplicationContext(), "满箱数量已超出,最多可以扫："
						+ manxiangshuliang + "", 0);
				return false;
			}
		}
		return true;
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
