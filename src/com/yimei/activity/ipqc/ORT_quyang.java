package com.yimei.activity.ipqc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.MyApplication;
import com.yimei.activity.R;
import com.yimei.adapter.IPQC_XunJian_Adapter;
import com.yimei.adapter.ORTQuYangAdapter;
import com.yimei.entity.Pair;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;

public class ORT_quyang extends Activity {

	public HorizontalScrollView mTouchView;
	private static List<GeneralCHScrollView> GeneralCHScrollView = new ArrayList<GeneralCHScrollView>();
	private static ListView mListView;
	private ORTQuYangAdapter ortquyangAdapter;
	private Map<String, String> zcnoMap = new HashMap<String, String>();
	private Spinner selectValue; // 下拉框
	private EditText yimei_ort_quyang_user_edt, yimei_ort_quyang_sbid,
			yimei_ort_quyang_sid1;
	private String op, sbid, sid1,zcno,ORTSid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ort_quyang);
		String cont;
		if (MyApplication.user.equals("admin")) {
			cont = "";
		} else {
			cont = "~sorg='" + MyApplication.sorg + "'";
		}
		// 获取制程
		OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL, null,
				MyApplication.QueryBatNo("M_PROCESS", cont), null, mHander,
				true, "SpinnerValue");

		GeneralCHScrollView headerScroll = (GeneralCHScrollView) findViewById(R.id.ort_quyang_scroll_title);
		GeneralCHScrollView.add(headerScroll);
		mListView = (ListView) findViewById(R.id.ort_quyang_scroll_list);
	}

	@Override
	protected void onResume() {
		super.onResume();

		yimei_ort_quyang_user_edt = (EditText) findViewById(R.id.yimei_ort_quyang_user_edt);
		yimei_ort_quyang_sbid = (EditText) findViewById(R.id.yimei_ort_quyang_sbid);
		yimei_ort_quyang_sid1 = (EditText) findViewById(R.id.yimei_ort_quyang_sid1);
		selectValue = (Spinner) findViewById(R.id.ort_quyang_selectValue);
		
		yimei_ort_quyang_user_edt.setOnEditorActionListener(EnterLister);
		yimei_ort_quyang_sbid.setOnEditorActionListener(EnterLister);
		yimei_ort_quyang_sid1.setOnEditorActionListener(EnterLister);
	}

	/**
	 * 键盘回车
	 */
	OnEditorActionListener EnterLister = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean flag = true;
			if (v.getId() == R.id.yimei_ort_quyang_user_edt) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_ort_quyang_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_ort_quyang_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ORT_quyang.this, "作业员不能为空", 0);
						return false;
					}
					op = yimei_ort_quyang_user_edt.getText().toString()
							.toUpperCase().trim();
					MyApplication.nextEditFocus(yimei_ort_quyang_sbid);
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_ort_quyang_sbid) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_ort_quyang_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_ort_quyang_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ORT_quyang.this, "作业员不能为空", 0);
						return false;
					}
					if (yimei_ort_quyang_sbid.getText().toString().trim()
							.equals("")
							|| yimei_ort_quyang_sbid.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ORT_quyang.this, "设备号不能为空", 0);
						return false;
					}
					sbid = yimei_ort_quyang_sbid.getText().toString()
							.toUpperCase().trim();
					// 查询设备号
					AccessServer("MESEQUTM", "~id='" + sbid + "' and zc_id='"
							+ zcno + "' ", "QuerySbid");
					flag = true;
				}
			}
			if (v.getId() == R.id.yimei_ort_quyang_sid1) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (yimei_ort_quyang_user_edt.getText().toString().trim()
							.equals("")
							|| yimei_ort_quyang_user_edt.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ORT_quyang.this, "作业员不能为空", 0);
						return false;
					}
					if (yimei_ort_quyang_sbid.getText().toString().trim()
							.equals("")
							|| yimei_ort_quyang_sbid.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ORT_quyang.this, "设备号不能为空", 0);
						return false;
					}
					if (yimei_ort_quyang_sid1.getText().toString().trim()
							.equals("")
							|| yimei_ort_quyang_sid1.getText().toString()
									.trim() == null) {
						ToastUtil.showToast(ORT_quyang.this, "批次号不能为空", 0);
						return false;
					}
					sid1 = yimei_ort_quyang_sid1.getText().toString().toUpperCase().trim();
					int count = 0;
					if(ortquyangAdapter!=null){
						for (int i = 0; i < ortquyangAdapter.getCount(); i++) {
 							Map<String,String> map = (Map<String, String>) ortquyangAdapter.getItem(i);
 							if(map.get("sid1").equals(sid1)){
 								count++;
 							}
						}
					}
					if(count>0){
						ToastUtil.showToast(ORT_quyang.this,"【"+sid1+"】批次号已经扫描过！",0);
						return false;
					}
					AccessServer("MOZCLISTWEB", "~zcno='" + zcno
							+ "' and sid1='" + sid1 + "'", "QuerySid1");
					flag = true;
				}
			}
			return flag;
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
		Map<String, String> queryBatNo = MyApplication.QueryBatNo(assistid,
				cont);
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
							}
						}
						if (string.equals("QuerySbid")) { //查询设备号
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(ORT_quyang.this,
										"在该制程中没有该设备号,请检查！", 0);
								yimei_ort_quyang_sbid.selectAll();
							} else {
								MyApplication.nextEditFocus(yimei_ort_quyang_sid1);
							}
						}
						if (string.equals("QuerySid1")) { // 查询批次号
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("code")
									.toString()) == 0) {
								ToastUtil.showToast(ORT_quyang.this, "没有【"
										+ sid1 + "】批次号!", 0);
								yimei_ort_quyang_sid1.selectAll();
								return;
							} else {
								JSONObject jsonObj = (JSONObject) ((JSONArray) jsonObject
										.get("values")).get(0);
								jsonObj.put("op",op);
								jsonObj.put("slkid",jsonObj.containsKey("sid")?jsonObj.get("sid"):"");
								jsonObj.put("sbuid","Q00501");
								jsonObj.put("chtype","09");
								jsonObj.put("caused","ORT");
								jsonObj.put("state","0");
								jsonObj.put("dcid",GetAndroidMacUtil.getMac());
								jsonObj.put("smake",MyApplication.user);
								jsonObj.put("mkdate",MyApplication.GetServerNowTime());
								jsonObj.put("state","0");
								Map<String, String> httpMapKeyValueMethod = MyApplication
										.httpMapKeyValueMethod(MyApplication.DBID,
												"savedata", MyApplication.user,
												jsonObj.toString(), "Q00501",
												"1");
								OkHttpUtils.getInstance().getServerExecute(MyApplication.MESURL,null,httpMapKeyValueMethod,null,mHander,true,"Save_Q00501");
								
								List<Map<String,String>> mList =new ArrayList<Map<String,String>>();
								Map<String,String> map = new HashMap<>();
								map.put("op",op);
								map.put("sbid",sbid);
								map.put("sid1",sid1);
								map.put("zcno",zcnoMap.get("zcno"));
								map.put("slkid",jsonObj.containsKey("slkid")?jsonObj.get("slkid").toString():"");
								map.put("prd_no",jsonObj.containsKey("prd_no")?jsonObj.get("prd_no").toString():"");
								map.put("prd_name",jsonObj.containsKey("prd_name")?jsonObj.get("prd_name").toString():"");
								map.put("qty",jsonObj.containsKey("qty")?jsonObj.getString("qty").toString():"");
								map.put("mkdate",MyApplication.GetServerNowTime());
								mList.add(map);
								if(ortquyangAdapter==null){									
									ortquyangAdapter = new ORTQuYangAdapter(ORT_quyang.this, mList);
									mListView.setAdapter(ortquyangAdapter);
								}else{
									ortquyangAdapter.listData.add(map);
									ortquyangAdapter.notifyDataSetChanged();
								}
							}
						}
						if(string.equals("Save_Q00501")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							ORTSid = ((JSONObject)jsonObject.get("data")).get("sid").toString();
							if(Integer.parseInt(jsonObject.get("id").toString())==0){
								Map<String, String> map = new HashMap<>();
								map.put("dbid", MyApplication.DBID);
								map.put("usercode", MyApplication.user);
								map.put("apiId", "chkup");
								map.put("chkid", "33");
								JSONObject ceaJson = new JSONObject();
								ceaJson.put("sid",((JSONObject)jsonObject.get("data")).get("sid"));
								ceaJson.put("sbuid", "Q00501");
								ceaJson.put("statefr", "0");
								ceaJson.put("stateto", "0");
								map.put("cea", ceaJson.toString());
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, map, null,
										mHander, true, "Approval_33");
							}else{
								ToastUtil.showToast(ORT_quyang.this,jsonObject.getString("message"),0);
							}
						}
						if(string.equals("Approval_33")){
							JSONObject jsonObject = JSON.parseObject(b.getString("jsonObj").toString());
							if(Integer.parseInt(jsonObject.get("id").toString())==0){
								ToastUtil.showToast(ORT_quyang.this,"提交成功",0);
								JSONObject ceaJson = new JSONObject();
								ceaJson.put("stateto","6");
								ceaJson.put("sid",ORTSid);
								ceaJson.put("sbuid", "Q00501");
								ceaJson.put("ckd","true");
								ceaJson.put("yjcontext", "");
								ceaJson.put("bup", "1");
								ceaJson.put("statefr", "0");
								ceaJson.put("tousr","");
								Map<String, String> map = new HashMap<>();
								map.put("dbid", MyApplication.DBID);
								map.put("usercode", MyApplication.user);
								map.put("apiId", "chkup");
								map.put("chkid", "34");
								map.put("cea", ceaJson.toString());
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, map, null,
										mHander, true, "Approval_34");
							}else{
								ToastUtil.showToast(ORT_quyang.this,jsonObject.getString("message"),0);
							}
						}
						if(string.equals("Approval_34")){
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
	/**
	 * 给制程下拉框赋值
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
			zcnoMap.put(jsonValue.getString("id").toString(), jsonValue
					.getString("name").toString());
		}
		ArrayAdapter<Pair> adapter = new ArrayAdapter<Pair>(ORT_quyang.this,
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
}
