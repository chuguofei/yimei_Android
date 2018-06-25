package com.yimei.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.ipqc.IPQC_shoujian;
import com.yimei.activity.ipqc.PinZhiGuanLi_Activity;
import com.yimei.entity.Main_map;
import com.yimei.sqlliteUtil.mesAllMethod;
import com.yimei.util.HttpUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class LoadingActivity extends Activity {

	static LoadingActivity loadingactivity;
	private String user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		
		//删除数据
		new mesAllMethod(LoadingActivity.this).delteAll();
		
		// 这里Handler的postDelayed方法，等待10000毫秒在执行run方法。
		// 在Activity中我们经常需要使用Handler方法更新UI或者执行一些耗时事件，
		// 并且Handler中post方法既可以执行耗时事件也可以做一些UI更新的事情，比较好用，推荐使用
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// 等待10000毫秒后销毁此页面，并提示登陆成功
				LoadingActivity.this.finish();

				Intent intent = getIntent();
				user = intent.getStringExtra("user");
				// user = "admin";
				String pwd = intent.getStringExtra("pwd");

//				ToastUtil.showToast(LoadingActivity.this,"url:"+MyApplication.MESURL+"dbid:"+MyApplication.DBID, 0);
				Map<String, String> map = new HashMap<String, String>();
				map.put("dbid", MyApplication.DBID);
				map.put("usercode", user);
				map.put("apiId", "login");
				map.put("pwd", MyApplication.Base64pwd(pwd));
				httpRequest(MyApplication.MESURL, map);

				//拿服务器时间
				Map<String, String> map1 = new HashMap<String, String>();
				map1.put("msclass", "$TIME");
				httpRequestQueryRecord(MyApplication.MESServerTime, map1,
						"GetServerTime");
			}
		}, 1400);
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
			if (string.equals("GetServerTime")) {
				String jsonObj = b.getString("jsonObj");
				long ServetTime = Long.parseLong(jsonObj, 16);
				String a = MyApplication.df.format(new Date(ServetTime));
				Calendar c = Calendar.getInstance();
				long nowTime = c.getTimeInMillis();
				String b1 = MyApplication.df.format(new Date(nowTime));
				MyApplication.ServerTimeCha = ServetTime - nowTime;
			}
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
				b.putString("type", type);
				Message m = new Message();
				m.setData(b);
				handler.sendMessage(m);
			}
		}).start();
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
					if (string.equals("GetServerTime")) { // 设备号查询
						JSONObject jsonObject = JSON.parseObject(b.getString(
								"jsonObj").toString());
						System.out.println(jsonObject);
					}
				}
			}
		}
	};

	/**
	 * http请求
	 * 
	 * @param baseUrl
	 * @param map
	 */
	public void httpRequest(final String baseUrl, final Map<String, String> map) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String httpPost = HttpUtil.httpPost(baseUrl, map);
				Log.i("httpPost", httpPost);
				if (httpPost.equals("-1")) {
					JumpServerError();
				} else {
					ProcessData(httpPost);
				}
			}
		}).start();
	}

	public void JumpServerError() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 连接服务器异常
				Intent intent = new Intent();
				intent.setClass(LoadingActivity.this, ServerError.class);// 跳转到加载界面
				Bundle bundle = new Bundle();
				bundle.putString("ServerError", "服务器异常,请联系管理员！");
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}).start();
	}

	/**
	 * 处理结果
	 * 
	 * @param response
	 */
	private void ProcessData(final String response) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("null")
			@Override
			public void run() {
 				JSONObject json = JSON.parseObject(response.toString());
				Log.i("id", json.toString());

				if (json.getInteger("id") == 0) {
					JSONObject jsonsorg = (JSONObject) json.get("data");
					MyApplication.sorg = ((JSONObject)((JSONObject)(JSONObject)jsonsorg.get("user")).get("deptInfo")).get("deptCode").toString();
					MyApplication.user = user;
					JSONObject json_data = JSON.parseObject(json.get("data")
							.toString());
					JSONArray json_menulist = new JSONArray(
							(List<Object>) json_data.get("menulist"));
					// 判断操作员的权限
					switch (json_menulist.size()) {
					case 1:
						JSONObject quanxian = JSON.parseObject(json_menulist
								.get(0).toString());
						String caidan = quanxian.get("menuId").toString();
						if (caidan.equals("D0001")) { // 通用工站
							Intent intent = new Intent(LoadingActivity.this,
									TongYongActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("D0002")) { // 通用工站
							Intent intent = new Intent(LoadingActivity.this,
									GuJingActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("D2010")) { // 加胶登记
							Intent intent = new Intent(LoadingActivity.this,
									JiaJiaoActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("D0020")) { // 编带管理
							Intent intent = new Intent(LoadingActivity.this,
									BianDaiActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("D5001")) { // 模组登记
							Intent intent = new Intent(LoadingActivity.this,
									MoZuActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("E0004")) { // 生产入库登记
							Intent intent = new Intent(LoadingActivity.this,
									RuKuActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("H0003")) { // 装箱作业
							Intent intent = new Intent(LoadingActivity.this,
									ZhuangXiangActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("D2009")) { // 混胶
							Intent intent = new Intent(LoadingActivity.this,
									HunJiaoActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("E5005")) { // 制具领出
							Intent intent = new Intent(LoadingActivity.this,
									ZhiJuLingChuActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("E5006")) { // 制具清洗
							Intent intent = new Intent(LoadingActivity.this,
									ZhiJuQingXiActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("E5004")) { // 制具入库
							Intent intent = new Intent(LoadingActivity.this,
									ZhiJuRukKuActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("D6004")) { // //加锡膏登记
							Intent intent = new Intent(LoadingActivity.this,
									JiaXiGaoActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("E0001")) { // //加锡膏登记
							Intent intent = new Intent(LoadingActivity.this,
									SCFLActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("D0030")) { //转出
							Intent intent = new Intent(LoadingActivity.this,
									ZhuanChuActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("D0031")) { //接收
							Intent intent = new Intent(LoadingActivity.this,
									JieShouActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("D5030")) { //快速过站
							Intent intent = new Intent(LoadingActivity.this,
									KuaiSuGuoZhanActivity.class);
							startActivity(intent);
						}
						if (caidan.equals("Q00101")) { //首检
							Intent intent = new Intent(LoadingActivity.this,
									IPQC_shoujian.class);
							startActivity(intent);
						}
						if (caidan.equals("Q0")) { //首检
							Intent intent = new Intent(LoadingActivity.this,PinZhiGuanLi_Activity.class);
							startActivity(intent);
						}
						break;
					case 0:
						Toast.makeText(LoadingActivity.this, "你没有权限登录", 0)
								.show();
						break;
					default:
						ArrayList<Main_map> list = new ArrayList<Main_map>();
						for (int i = 0; i < json_menulist.size(); i++) {
							Main_map m = new Main_map();
							JSONObject quanxian1 = JSON
									.parseObject(json_menulist.get(i)
											.toString());
							m.setKey(quanxian1.get("menuId").toString());
							m.setValue(quanxian1.get("menuName").toString());

							list.add(m);
							/*{"childMenu":[
 {"haveChild":false,"command":"pbuid=D0001&pmenuid=D0001","menuId":"D0001","menuName":"通用工站","top":false}
,{"haveChild":false,"command":"pbuid=D0020&pmenuid=D0020","menuId":"D0020","menuName":"编带管理","top":false},
{"haveChild":false,"command":"pbuid=D0030&pmenuid=D0030","menuId":"D0030","menuName":"器件转出登记","top":false},
{"haveChild":false,"command":"pbuid=D0031&pmenuid=D0031","menuId":"D0031","menuName":"器件接收登记","top":false}]
		,"haveChild":true,"command":"","menuId":"D0","menuName":"器件生产","top":true}*/
							/*if(quanxian1.get("menuId").toString().length()==2){
								HashMap<String,JSONObject> map = new HashMap<>();
								JSONObject mainjson = new JSONObject();
								JSONArray childMenu = (JSONArray) quanxian1.get("childMenu");
								for (int j = 0; j < childMenu.size(); j++) {
									JSONObject childMenuSon =  (JSONObject) childMenu.get(j);
									mainjson.put(childMenuSon.getString("menuName"), childMenuSon.get("menuId"));
								}
								map.put(quanxian1.get("menuName").toString(),mainjson);
								m.setMainJSON(map);
								list.add(m);
							}*/
						}
						/*for (int i = 0; i < list.size(); i++) {	
							Main_map m = list.get(i);
							Log.i("mainList",m.getMainJSON().toString());
						}*/
						Intent intent = new Intent(LoadingActivity.this,
								MainActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("list", list);
						intent.putExtras(bundle);
						startActivity(intent);
						break;

					}
				} else if (json.getInteger("id") == -1
						&& json.getString("message").equals("用户名或密码错误")) {
					Toast.makeText(LoadingActivity.this,
							json.getString("message"), 0).show();
				} else {
					Toast.makeText(LoadingActivity.this, "该用户没有授权", 0).show();
				}
			}
		});
	}
}