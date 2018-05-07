package com.yimei.activity;

import android.app.Activity;
import android.os.Bundle;

import com.yimei.sqlliteUtil.mesAllMethod;

public class HanXianActivity extends Activity {

	static MyApplication myapp;
	public static GuJingActivity gujingActivity;

//	stuDao s = new stuDao(HanXianActivity.this,"");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hanxian);
	}

}
