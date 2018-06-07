package com.yimei.activity.ipqc;

import java.util.HashMap;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.MyApplication;
import com.yimei.activity.R;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ErrorNotice_shoujian extends Activity {

	private EditText yimei_shoujian_err_user, yimei_shoujian_err_sidf,
			yimei_shoujian_err_hpdate, yimei_shoujian_err_zcno,
			yimei_shoujian_err_sbid, yimei_shoujian_err_sid1,
			yimei_shoujian_err_slkid, yimei_shoujian_err_prd_no,
			yimei_shoujian_err_qty, yimei_shoujian_err_op_c,
			yimei_shoujian_err_risklvl, yimei_shoujian_err_remark,
			yimei_shoujian_err_whys;
	// 下拉框
	private Spinner yimei_shoujian_err_bbid, yimei_shoujian_err_ptype,
			yimei_shoujian_err_clid, yimei_shoujian_err_determine;
	private Button yimei_shoujian_err_save, yimei_shoujian_err_capaid;
	private JSONObject Errjson; // 上个界面传来的值
	private String errnoticeSid; // 异常提交sid
	private String bbid, ptype, clid, determine; // 下拉框的值
	private String capaid = "0";// 是否发起异常
	private int chooseIndex = 0;
	private Map<Integer,String> AuditorMap = new HashMap<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoujian_errornotice);
		AuditorMap.clear();
	};

	@Override
	protected void onResume() {
		super.onResume();
		// 拿到首检记录的json串
		Intent intent = getIntent();
		String json = intent.getStringExtra("json");
		Errjson = JSONObject.parseObject(json);
		yimei_shoujian_err_user = (EditText) findViewById(R.id.yimei_shoujian_err_user);
		yimei_shoujian_err_sidf = (EditText) findViewById(R.id.yimei_shoujian_err_sidf);
		yimei_shoujian_err_hpdate = (EditText) findViewById(R.id.yimei_shoujian_err_hpdate);
		yimei_shoujian_err_zcno = (EditText) findViewById(R.id.yimei_shoujian_err_zcno);
		yimei_shoujian_err_sbid = (EditText) findViewById(R.id.yimei_shoujian_err_sbid);
		yimei_shoujian_err_sid1 = (EditText) findViewById(R.id.yimei_shoujian_err_sid1);
		yimei_shoujian_err_slkid = (EditText) findViewById(R.id.yimei_shoujian_err_slkid);
		yimei_shoujian_err_prd_no = (EditText) findViewById(R.id.yimei_shoujian_err_prd_no);
		yimei_shoujian_err_qty = (EditText) findViewById(R.id.yimei_shoujian_err_qty);
		yimei_shoujian_err_op_c = (EditText) findViewById(R.id.yimei_shoujian_err_op_c);
		yimei_shoujian_err_risklvl = (EditText) findViewById(R.id.yimei_shoujian_err_risklvl);
		yimei_shoujian_err_remark = (EditText) findViewById(R.id.yimei_shoujian_err_remark);
		yimei_shoujian_err_whys = (EditText) findViewById(R.id.yimei_shoujian_err_whys);

		yimei_shoujian_err_capaid = (Button) findViewById(R.id.yimei_shoujian_err_capaid); // 是否发起异常报告
		yimei_shoujian_err_capaid
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (yimei_shoujian_err_capaid.getText().equals("√")) {
							yimei_shoujian_err_capaid.setText("");
							capaid = "0";
						} else {
							yimei_shoujian_err_capaid.setText("√");
							capaid = "1";
						}
					}
				});
		yimei_shoujian_err_determine = (Spinner) findViewById(R.id.yimei_shoujian_err_determine);
		yimei_shoujian_err_bbid = (Spinner) findViewById(R.id.yimei_shoujian_err_bbid); // 班别
		yimei_shoujian_err_ptype = (Spinner) findViewById(R.id.yimei_shoujian_err_ptype); // 异常类型
		yimei_shoujian_err_clid = (Spinner) findViewById(R.id.yimei_shoujian_err_clid); // 临时措施

		yimei_shoujian_err_determine.setOnItemSelectedListener(determine_SelectValue); // 下拉框改变更新值
		yimei_shoujian_err_bbid.setOnItemSelectedListener(bbid_SelectValue); // 下拉框改变更新值
		yimei_shoujian_err_ptype.setOnItemSelectedListener(ptype_SelectValue); // 下拉框改变更新值
		yimei_shoujian_err_clid.setOnItemSelectedListener(clid_SelectValue); // 下拉框改变更新值

		yimei_shoujian_err_user.setText(Errjson.containsKey("op") ? Errjson
				.get("op").toString() : "");
		yimei_shoujian_err_sidf.setText(Errjson.containsKey("sid") ? Errjson
				.get("sid").toString() : "");
		yimei_shoujian_err_hpdate.setText(MyApplication.GetServerNowTime());
		yimei_shoujian_err_zcno.setText(Errjson.containsKey("zcno") ? Errjson
				.get("zcno").toString() : "");
		yimei_shoujian_err_sbid.setText(Errjson.containsKey("sbid") ? Errjson
				.get("sbid").toString() : "");
		yimei_shoujian_err_sid1.setText(Errjson.containsKey("sid1") ? Errjson
				.get("sid1").toString() : "");
		yimei_shoujian_err_slkid.setText(Errjson.containsKey("slkid") ? Errjson
				.get("slkid").toString() : "");
		yimei_shoujian_err_prd_no
				.setText(Errjson.containsKey("prd_name") ? Errjson.get(
						"prd_name").toString() : "");
		yimei_shoujian_err_qty.setText(Errjson.containsKey("qty") ? Errjson
				.get("qty").toString() : "");

		yimei_shoujian_err_user.setKeyListener(null);
		yimei_shoujian_err_sidf.setKeyListener(null);
		yimei_shoujian_err_hpdate.setKeyListener(null);
		yimei_shoujian_err_zcno.setKeyListener(null);
		yimei_shoujian_err_sbid.setKeyListener(null);
		yimei_shoujian_err_sid1.setKeyListener(null);
		yimei_shoujian_err_prd_no.setKeyListener(null);
		yimei_shoujian_err_qty.setKeyListener(null);
		yimei_shoujian_err_slkid.setKeyListener(null);
		yimei_shoujian_err_user.setKeyListener(null);

		yimei_shoujian_err_save = (Button) findViewById(R.id.yimei_shoujian_err_save);
		yimei_shoujian_err_save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				JSONObject json = new JSONObject();
				json.put("sbuid", "Q00201");
				json.put("state", "0");
				json.put("sorg", MyApplication.sorg);
				json.put("op", yimei_shoujian_err_user.getText());
				json.put("sidf", yimei_shoujian_err_sidf.getText());
				json.put("hpdate", yimei_shoujian_err_hpdate.getText());
				json.put("zcno", yimei_shoujian_err_zcno.getText());
				json.put("sbid", yimei_shoujian_err_sbid.getText());
				json.put("sid1", yimei_shoujian_err_sid1.getText());
				json.put("slkid", yimei_shoujian_err_slkid.getText());
				json.put("prd_no",
						Errjson.containsKey("prd_no") ? Errjson.get("prd_no")
								: "");
				json.put(
						"prd_name",
						Errjson.containsKey("prd_name") ? Errjson
								.get("prd_name") : "");
				json.put("qty", yimei_shoujian_err_qty.getText());
				json.put("bbid", bbid);
				json.put("risklvl", yimei_shoujian_err_risklvl.getText());
				json.put("clid", clid);
				json.put("capaid", capaid);
				json.put("determine", determine);
				json.put("remark", yimei_shoujian_err_remark.getText());
				json.put("op_c", yimei_shoujian_err_op_c.getText());
				json.put("ptype", ptype);
				json.put("whys", yimei_shoujian_err_whys.getText());
				json.put("state", "0");
				json.put("smake", MyApplication.user);
				json.put("mkdate", MyApplication.GetServerNowTime());
				json.put("state", "0");
				json.put("chtype",Errjson.containsKey("chtype")?Errjson.get("chtype"):"");
				Map<String, String> httpMapKeyValueMethod = MyApplication
						.httpMapKeyValueMethod(MyApplication.DBID, "savedata",
								MyApplication.user, json.toString(), "Q00201S",
								"1");
				OkHttpUtils.getInstance().getServerExecute(
						MyApplication.MESURL, null, httpMapKeyValueMethod,
						null, mHander, true, "Save_Q00201S");
			}
		});
	}

	OnItemSelectedListener bbid_SelectValue = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			switch (position) {
			case 0:
				bbid = "";
				break;
			case 1:
				bbid = "0";
				break;
			case 2:
				bbid = "1";
				break;
			default:
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}

	};

	OnItemSelectedListener ptype_SelectValue = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			switch (position) {
			case 0:
				ptype = "";
				break;
			case 1:
				ptype = "0";
				break;
			case 2:
				ptype = "1";
				break;
			case 3:
				ptype = "2";
				break;
			case 4:
				ptype = "3";
				break;
			default:
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}

	};

	OnItemSelectedListener determine_SelectValue = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			switch (position) {
			case 0:
				determine = "";
				break;
			case 1:
				determine = "00";
				break;
			case 2:
				determine = "01";
				break;
			case 3:
				determine = "02";
				break;
			case 4:
				determine = "03";
				break;
			case 5:
				determine = "04";
				break;
			default:
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}

	};

	OnItemSelectedListener clid_SelectValue = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			switch (position) {
			case 0:
				clid = "";
				break;
			case 1:
				clid = "OA";
				break;
			case 2:
				clid = "OB";
				break;
			case 3:
				clid = "OC";
				break;
			case 4:
				clid = "OD";
				break;
			case 5:
				clid = "OE";
				break;
			case 6:
				clid = "OF";
				break;
			case 7:
				clid = "OG";
				break;
			default:
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

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
					try {
						Bundle b = msg.getData();
						String string = b.getString("type");
						if (string.equals("Save_Q00201S")) {
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id")
									.toString()) == -1) {
								ToastUtil.showToast(ErrorNotice_shoujian.this,
										"（异常通知单save）错误", 0);
							} else {
								errnoticeSid = ((JSONObject) jsonObject
										.get("data")).get("sbuid").toString();
								Map<String, String> map = new HashMap<>();
								map.put("dbid", MyApplication.DBID);
								map.put("usercode", MyApplication.user);
								map.put("apiId", "chkup");
								map.put("chkid", "33");
								JSONObject ceaJson = new JSONObject();
								ceaJson.put("sid", errnoticeSid);
								ceaJson.put("sbuid", "Q00201");
								ceaJson.put("statefr", "0");
								ceaJson.put("stateto", "0");
								map.put("cea", ceaJson.toString());
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, map, null,
										mHander, true, "Approval_33");
							}
						}
						if (string.equals("Approval_33")) {  //调用提交
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
									
									AlertDialog.Builder builder = new AlertDialog.Builder(ErrorNotice_shoujian.this);  
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
											ceaJson.put("sid", errnoticeSid);
											ceaJson.put("sbuid", "Q00201");
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
									ToastUtil.showToast(ErrorNotice_shoujian.this,"没有审核人",0);
								}
							/*	
								JSONObject ceaJson = new JSONObject();
								ceaJson.put("stateto", listJson.get("stateId"));
								ceaJson.put("sid", errnoticeSid);
								ceaJson.put("sbuid", "Q00201");
								ceaJson.put("ckd", info.get("checked"));
								ceaJson.put("yjcontext", "");
								ceaJson.put("bup", "1");
								ceaJson.put("statefr", "0");
								ceaJson.put("tousr", MyApplication.user);
								Map<String, String> map = new HashMap<>();
								map.put("dbid", MyApplication.DBID);
								map.put("usercode", MyApplication.user);
								map.put("apiId", "chkup");
								map.put("chkid", "34");
								map.put("cea", ceaJson.toString());
								OkHttpUtils.getInstance().getServerExecute(
										MyApplication.MESURL, null, map, null,
										mHander, true, "Approval_34");*/
							} else {
								ToastUtil.showToast(ErrorNotice_shoujian.this,"（异常通知单）33-Error", 0);
							}
						}
						if (string.equals("Approval_34")) {  //调用审核
							JSONObject jsonObject = JSON.parseObject(b
									.getString("jsonObj").toString());
							if (Integer.parseInt(jsonObject.get("id")
									.toString()) == 0) {
								ToastUtil.showToast(ErrorNotice_shoujian.this,
										"审批提交成功", 0);
								ErrorNotice_shoujian.this.finish();
							} else {
								ToastUtil.showToast(ErrorNotice_shoujian.this,
										"审批提交失败", 0);
							}
						}
					} catch (Exception e) {
						ToastUtil.showToast(ErrorNotice_shoujian.this,
								e.toString(), 0);
					}
				}
			}
		}
	};

}
