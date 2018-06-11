package com.yimei.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.yimei.adapter.MainMyAdapter;
import com.yimei.entity.Main_map;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private GridView gv;
//	private ListView lv;
	static MyApplication myapp;
	public static MainActivity mainActivity;
	private MainMyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
		setContentView(R.layout.activity_gridmain);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		Application application = getApplication();
		myapp = (MyApplication) application;
		mainActivity = MainActivity.this;
		myapp.addActivity_(mainActivity);
		myapp.removeActivity_(LoginActivity.loginActivity);
		ArrayList<Main_map> list = new ArrayList<Main_map>();
		list = (ArrayList<Main_map>) getIntent().getSerializableExtra("list");
	/*	Main_map m = new Main_map();
		m.setKey("F0001");
		m.setValue("编带");
		Main_map m1 = new Main_map();
		m1.setKey("D0002");
		m1.setValue("固晶");
		Main_map m2 = new Main_map();
		m2.setKey("D0003");
		m2.setValue("焊线");
		Main_map m3 = new Main_map();
		m3.setKey("D0050");
		m3.setValue("烤箱");
		list.add(m);
		list.add(m1);
		list.add(m2);
		list.add(m3);*/
		gv = (GridView) findViewById(R.id.Main_gridlist_view);
		adapter = new MainMyAdapter(list, MainActivity.this);
		gv.setAdapter(adapter);
		/*lv = (ListView) findViewById(R.id.Main_gridlist_view);
		adapter = new MainMyAdapter(list, MainActivity.this);
		gv.setAdapter(adapter);*/
		Log.i("ServerTime",MyApplication.GetServerNowTime());
		Log.i("ServerTime",MyApplication.df.format(new Date()));
		Log.i("ServerTime","加3小时："+MyApplication.GetHunJiaoAdd_3(3));
	}
	
	private boolean mIsExit;
	@Override
	/**
	 * 双击返回键退出
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//如果只有一个Activity在运行就退出
		if (myapp.getListCount() > 1 || myapp.getListCount() == 1) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (mIsExit) {
					this.finish();
				} else {
					Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
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
	
}
