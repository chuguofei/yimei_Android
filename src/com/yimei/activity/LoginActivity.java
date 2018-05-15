package com.yimei.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
		if (!isWiFi(LoginActivity.this)) {
			// 进入手机设置界面
			// startActivity(new Intent(Settings.ACTION_SETTINGS)); //
			Toast.makeText(getApplicationContext(), "亲，请检查网络是否开启~", 0).show();
		}
		btn = (Button) findViewById(R.id.login_btn_login);
		btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isWiFi(LoginActivity.this)) {
					// 进入手机设置界面
					// startActivity(new Intent(Settings.ACTION_SETTINGS)); //
					// 直接进入手机中设置界面
					Toast.makeText(getApplicationContext(), "亲，请检查网络是否开启~", 0).show();
				} else {
					if(login_edit_account.getText().toString().trim().equals("")||login_edit_account.getText().toString().trim()==null){
						Toast.makeText(getApplicationContext(), "用户名不能为空", 0)
						.show();
						return ;
					}
				    if(login_edit_pwd.getText().toString().trim().equals("")||login_edit_pwd.getText().toString().trim()==null){
						Toast.makeText(getApplicationContext(), "密码不能为空", 0)
						.show();
						return ;
					}
					
				    Intent intent = new Intent();
					intent.setClass(LoginActivity.this, LoadingActivity.class);// 跳转到加载界面
					
					//利用bundle来存取数据
	                Bundle bundle=new Bundle();
	                bundle.putString("user",login_edit_account.getText().toString());
	                bundle.putString("pwd",login_edit_pwd.getText().toString());
	                //再把bundle中的数据传给intent，以传输过去
	                intent.putExtras(bundle);
					startActivity(intent);					
				}
			}
		});

	}
	
	protected void onResume() {
		super.onResume();
		login_edit_account = (EditText) findViewById(R.id.login_edit_account);
		login_edit_pwd = (EditText) findViewById(R.id.login_edit_pwd);
		login_edit_account.setOnEditorActionListener(editEnter); //用户的回车监听事件
		login_edit_pwd.setOnEditorActionListener(editEnter); //密码回车监听事件
	};
	
	OnEditorActionListener editEnter = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (v.getId() == R.id.login_edit_account) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
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
