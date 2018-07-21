package com.yimei.activity;

import com.yimei.activity.kuaiguozhan.DiDianLiuActivity;
import com.yimei.activity.kuaiguozhan.GaoWenDianLiangActivity;
import com.yimei.activity.kuaiguozhan.KanDaiActivity;
import com.yimei.activity.kuaiguozhan.TieBeiJiaoActivity;
import com.yimei.activity.kuaiguozhan.WaiGuanActivity;
import com.yimei.activity.kuaiguozhan.plasmaActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class KuaiSuGuoZhanActivity extends Activity {
	
	private Button gaowen,waiguan,tiebeijiao,didianliu,kandai,plasma;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kuaisuguozhan);
	    gaowen = (Button) findViewById(R.id.gaowendianliang);
	    waiguan = (Button) findViewById(R.id.waiguan);
	    tiebeijiao = (Button) findViewById(R.id.tiebeijiao);
	    didianliu = (Button) findViewById(R.id.didianliu);
	    kandai = (Button) findViewById(R.id.kandai);
	    plasma= (Button) findViewById(R.id.plasma);
	    gaowen.setOnClickListener(OnClick);
	    waiguan.setOnClickListener(OnClick);
	    tiebeijiao.setOnClickListener(OnClick);
	    didianliu.setOnClickListener(OnClick);
	    kandai.setOnClickListener(OnClick);
	    plasma.setOnClickListener(OnClick);
		if(MyApplication.sorg.equals(MyApplication.QIJIANSORG)){ //器件隐藏模组
			gaowen.setVisibility(View.GONE);
			waiguan.setVisibility(View.GONE);
			tiebeijiao.setVisibility(View.GONE);
			didianliu.setVisibility(View.GONE);
		}else if(MyApplication.sorg.equals(MyApplication.MOZUSORG)){ //模组隐藏器件
			kandai.setVisibility(View.GONE);
			plasma.setVisibility(View.GONE);
		}
	}
	
	OnClickListener OnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.gaowendianliang) {
				startActivity(new Intent(KuaiSuGuoZhanActivity.this,GaoWenDianLiangActivity.class));
			}
			if (v.getId() == R.id.waiguan) {
				startActivity(new Intent(KuaiSuGuoZhanActivity.this,WaiGuanActivity.class));
			}
			if (v.getId() == R.id.tiebeijiao) {
				startActivity(new Intent(KuaiSuGuoZhanActivity.this,TieBeiJiaoActivity.class));
			}
			if (v.getId() == R.id.didianliu) {
				startActivity(new Intent(KuaiSuGuoZhanActivity.this,DiDianLiuActivity.class));
			}
			if (v.getId() == R.id.kandai) {
				startActivity(new Intent(KuaiSuGuoZhanActivity.this,KanDaiActivity.class));
			}
			if (v.getId() == R.id.plasma) {
				startActivity(new Intent(KuaiSuGuoZhanActivity.this,plasmaActivity.class));
			}
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, MyApplication.LOGOUT, 1, MyApplication.LOGOUTText);
		menu.add(1, MyApplication.ABOUTUS, 2, MyApplication.ABOUTUSText);
		menu.add(1, MyApplication.VERSION, 3, MyApplication.VERSIONText);
		return true;
	}

	/**
	 * 切换用户
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case MyApplication.LOGOUT:
			startActivity(new Intent(getApplicationContext(),
					LoginActivity.class));
			break;
		case MyApplication.ABOUTUS:

			break;
		case MyApplication.VERSION:

			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
