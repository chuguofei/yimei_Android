package com.yimei.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.entity.Main_map;
import com.yimei.util.HttpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class LoadingActivity extends Activity {

	static LoadingActivity loadingactivity;
	private String user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		// 这里Handler的postDelayed方法，等待10000毫秒在执行run方法。
		// 在Activity中我们经常需要使用Handler方法更新UI或者执行一些耗时事件，
		// 并且Handler中post方法既可以执行耗时事件也可以做一些UI更新的事情，比较好用，推荐使用
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// 等待10000毫秒后销毁此页面，并提示登陆成功
				LoadingActivity.this.finish();

				Intent intent = getIntent();
				user= intent.getStringExtra("user");
//				user = "admin";
				String pwd = intent.getStringExtra("pwd");
				

				Map<String, String> map = new HashMap<String, String>();
				map.put("dbid", MyApplication.DBID);
				map.put("usercode", user);
				map.put("apiId", "login");
				map.put("pwd", MyApplication.Base64pwd(pwd));
				httpRequest(MyApplication.MESURL, map);
			}
		}, 1400);
	}

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

	public void JumpServerError(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				//连接服务器异常
				Intent intent = new Intent();
				intent.setClass(LoadingActivity.this,ServerError.class);// 跳转到加载界面
				Bundle bundle = new Bundle();
				bundle.putString("ServerError","服务器异常,请联系管理员！");
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
				Log.i("id",json.toString());
				
					if (json.getInteger("id") == 0) {
						
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
							if (caidan.equals("D0001")) { //通用工站
								Intent intent = new Intent(LoadingActivity.this,
										TongYongActivity.class);
								startActivity(intent);
							}
							if (caidan.equals("D0002")) { //通用工站
								Intent intent = new Intent(LoadingActivity.this,
										GuJingActivity.class);
								startActivity(intent);
							}
							if (caidan.equals("D2010")) { //加胶登记
								Intent intent = new Intent(LoadingActivity.this,
										JiaJiaoActivity.class);
								startActivity(intent);
							}
							if (caidan.equals("D0020")) { //编带管理
								Intent intent = new Intent(LoadingActivity.this,
										BianDaiActivity.class);
								startActivity(intent);
							}
							if (caidan.equals("D5001")) { //模组登记
								Intent intent = new Intent(LoadingActivity.this,
										MoZuActivity.class);
								startActivity(intent);
							}
							if (caidan.equals("E0004")) { //生产入库登记
								Intent intent = new Intent(LoadingActivity.this,
									  RuKuActivity.class);
								startActivity(intent);
							}
							if (caidan.equals("H0003")) { //装箱作业
								Intent intent = new Intent(LoadingActivity.this,
									  ZhuangXiangActivity.class);
								startActivity(intent);
							}
							if (caidan.equals("D2009")) { //装箱作业
								Intent intent = new Intent(LoadingActivity.this,
									  HunJiaoActivity.class);
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
							}

							Intent intent = new Intent(LoadingActivity.this,
									MainActivity.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable("list", list);
							intent.putExtras(bundle);
							startActivity(intent);
							break;

						}
					} else if (json.getInteger("id") == -1 && json.getString("message").equals("用户名或密码错误")) {
						Toast.makeText(LoadingActivity.this,
								json.getString("message"), 0).show();
					}else{
						Toast.makeText(LoadingActivity.this,"该用户没有授权", 0).show();
					}
			}
		});
	}
}