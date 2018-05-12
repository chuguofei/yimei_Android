package com.yimei.util;

import com.yimei.activity.BianDaiActivity;
import com.yimei.activity.TongYongActivity;
import com.yimei.activity.HanXianActivity;

import android.app.Activity;
import android.content.Intent;

public class JumpPage extends Activity{

	public void jump(String caidan,Activity activity){
		if (caidan.equals("F0001")) {
			// 跳到用户所属权限的页面
			Intent intent = new Intent(activity,
					BianDaiActivity.class);
			startActivity(intent);
		} else if (caidan.equals("D0002")) {
			// 跳到用户所属权限的页面
			Intent intent = new Intent(activity,
					TongYongActivity.class);
			startActivity(intent);
		}else if(caidan.equals("D0003")){
			Intent intent = new Intent(activity,
					HanXianActivity.class);
			startActivity(intent);
		}
	}
}
