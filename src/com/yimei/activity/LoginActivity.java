package com.yimei.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.adapter.ZhiJuQingXiAdapter;
import com.yimei.util.GetAndroidMacUtil;
import com.yimei.util.OkHttpUtils;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends Activity {

	private Button btn;
	public static LoginActivity loginActivity;
	static MyApplication myapp;
	private EditText login_edit_account;
	private EditText login_edit_pwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Application application = getApplication();
		myapp = (MyApplication) application;
		myapp.removeALLActivity_();
		myapp.addActivity_(LoginActivity.this);
		loginActivity = LoginActivity.this;
		
 		TextView show_text =  (TextView) findViewById(R.id.show_version);
		show_text.setText(MyApplication.SHOW_VERSION);
		TextView show_ip =  (TextView) findViewById(R.id.show_ip);
		show_ip.setText(MyApplication.MESIP);
		if (!isWiFi(LoginActivity.this)) {
			// 进入手机设置界面
			// startActivity(new Intent(Settings.ACTION_SETTINGS)); //
			Toast.makeText(getApplicationContext(), "亲，请检查网络是否开启~", 0).show();
		}
		btn = (Button) findViewById(R.id.login_btn_login);
		btn.setOnLongClickListener(new Button.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				ToastUtil.showToastLocation(loginActivity, "进入更新系统窗口", 0);
				showNormalDialog();
				return true;
			}
		});
		btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isWiFi(LoginActivity.this)) {
					// 进入手机设置界面
					// startActivity(new Intent(Settings.ACTION_SETTINGS)); //
					// 直接进入手机中设置界面
					Toast.makeText(getApplicationContext(), "亲，请检查网络是否开启~", 0)
							.show();
				} else {
					if (login_edit_account.getText().toString().trim()
							.equals("")
							|| login_edit_account.getText().toString().trim() == null) {
						Toast.makeText(getApplicationContext(), "用户名不能为空", 0)
								.show();
						return;
					}
					if (login_edit_pwd.getText().toString().trim().equals("")
							|| login_edit_pwd.getText().toString().trim() == null) {
						Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
						return;
					}

					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, LoadingActivity.class);// 跳转到加载界面

					// 利用bundle来存取数据
					Bundle bundle = new Bundle();
					bundle.putString("user", login_edit_account.getText().toString());
					bundle.putString("pwd", login_edit_pwd.getText().toString());
					// 再把bundle中的数据传给intent，以传输过去
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});

	}
	
	/**
	 * 弹出提示框
	 * 
	 * @param mes
	 */
	private void showNormalDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View dialog = inflater.inflate(R.layout.activity_newversion_dig,
				(ViewGroup) findViewById(R.id.dialog));
		final EditText editText = (EditText) dialog.findViewById(R.id.dig_et);

		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				LoginActivity.this);
		normalDialog.setTitle("版本更新");
		normalDialog.setView(dialog);
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (editText.getText().toString()
								.equals("adminadmin")) {
							ToastUtil.showToastLocation(loginActivity,
									"下载新的版本", 0);
							try {
								ProgressDialog progressDialog = new ProgressDialog(
										loginActivity);
								progressDialog.setTitle("提示");
								progressDialog.setMessage("正在下载...");
								progressDialog.setIndeterminate(false);
								progressDialog.setMax(100);
								progressDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
								progressDialog
										.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // 进度条类型
								progressDialog.show();
								new DownloadAPK(progressDialog,
										LoginActivity.this)
										.execute(MyApplication.MESDOWNLOADAPKURL);
							} catch (Exception e) {
								ToastUtil.showToastLocation(loginActivity,
										"服务器异常,请联系管理员!", 0);
							}
						} else if (editText.getText().toString()
								.equals("MESURL")) {
							choseMesURL();
						} else {
							ToastUtil.showToastLocation(loginActivity, "密码错误",
									0);
							showNormalDialog();
							return;
						}
					}
				});
		normalDialog.setNegativeButton("取消", null);
		// 显示
		normalDialog.show();
	}

	private void choseMesURL() {
		LayoutInflater inflater = getLayoutInflater();
		View dialog = inflater.inflate(R.layout.activity_mesurl_dig,
				(ViewGroup) findViewById(R.id.dialogurl));
		final EditText url = (EditText) dialog.findViewById(R.id.mesurl);
		final EditText dbid = (EditText) dialog.findViewById(R.id.mesdbid);

		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				LoginActivity.this);
		normalDialog.setTitle("切换URL");
		normalDialog.setView(dialog);
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!url.getText().toString().trim().equals("")
								&& !dbid.getText().toString().trim().equals("")) {
							MyApplication.MESURL = url.getText().toString()
									.trim();
							MyApplication.DBID = dbid.getText().toString()
									.trim();
							TextView show_ip =  (TextView) findViewById(R.id.show_ip);
							try {
								show_ip.setText("IP:"+MyApplication.MESURL.substring(7,MyApplication.MESURL.lastIndexOf(":")));
							} catch (Exception e) {
//								e.printStackTrace();
								Log.i("log","请输入有效的地址!");
								ToastUtil.showToastLocation(loginActivity,"请输入有效的地址!", 0);
								show_ip.setText("IP:"+MyApplication.MESURL);
							}
						}
					}
				});
		normalDialog.setNegativeButton("取消", null);
		// 显示
		normalDialog.show();
	}

	protected void onResume() {
		super.onResume();
		login_edit_account = (EditText) findViewById(R.id.login_edit_account);
		login_edit_pwd = (EditText) findViewById(R.id.login_edit_pwd);
		login_edit_account.setOnEditorActionListener(editEnter); // 用户的回车监听事件
		login_edit_pwd.setOnEditorActionListener(editEnter); // 密码回车监听事件
	};

	OnEditorActionListener editEnter = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (v.getId() == R.id.login_edit_account) {
				if (actionId >= 0) {
					EditText nextEditText = (EditText) findViewById(R.id.login_edit_pwd);
					nextEditText.setFocusable(true);
					nextEditText.setFocusableInTouchMode(true);
					nextEditText.requestFocus();
					nextEditText.findFocus();
					return true;
				}
			}
			return false;
		}
	};

	// 判断WiFi是否打开
	public boolean isWiFi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null
				&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
}
