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
import com.yimei.shebei.shebeiweixiuActivity;
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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Loading1Activity extends Activity {

	static Loading1Activity loadingactivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		TextView text = (TextView) findViewById(R.id.loadingText);
		text.setText("处理中...");
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// 等待10000毫秒后销毁此页面，并提示登陆成功
				Loading1Activity.this.finish();
			}
		}, 1100);
	}

}