package com.yimei.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class ServerError extends Activity {
	
	public ServerError ServerErrorActivity;
	static MyApplication myapp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Application application = getApplication();
		myapp = (MyApplication) application;
		myapp.removeALLActivity_();
		Intent ServerError = getIntent();
		String shuchu = ServerError.getStringExtra("ServerError");
		Toast.makeText(ServerError.this,shuchu, 0).show();
		Intent intent = new Intent();
		intent.setClass(ServerError.this,LoginActivity.class);// 跳转到加载界面
		startActivity(intent);
		
	}
}
