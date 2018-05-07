package com.yimei.activity;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import android.util.Log;

import com.yimei.entity.mesPrecord;
import com.yimei.sqlliteUtil.DBHelper;
import com.yimei.sqlliteUtil.mesAllMethod;
public class Test extends AndroidTestCase{
	
	public void add(){
		mesAllMethod mes = new mesAllMethod(this.getContext());
		mesPrecord m = new mesPrecord("1","1","1","1111", "2","2", "2","1", "1", "1", "1", "1", "1", "1", "1", 1, "1", "1", "1", "1", "1", "1", "1", "1", "1", 1, "1", "1");
		mes.addData(m);
	}
	
	/**
	 * 创建表
	 */
	public void create(){
		DBHelper d = new DBHelper(this.getContext());
		SQLiteDatabase sqlitedb = d.getWritableDatabase();
		d.onCreate(sqlitedb);
	}
	
	/**
	 * 当前的批次号和制程
	 */
	public void prd_noSelect(){
		mesAllMethod mes = new mesAllMethod(this.getContext());
		if(mes.sid1_Select("2","021","11")){
			Log.i("Tag","存在");
		}else{
			Log.i("Tag","不存在");
		}
	}
	
	public void del(){
		mesAllMethod mes = new mesAllMethod(this.getContext());
		if(mes.delSid("SR18041200071")){
			Log.i("Tag","删除成功");
		}else{
			Log.i("Tag","删除失败");
		}
	}
	
	/**
	 * 当前的设备号查询
	 */
	public void prd_noSelect1(){
		mesAllMethod mes = new mesAllMethod(this.getContext());
		boolean sbid_Select = mes.sbid_Select("1111");
		if(sbid_Select){
			Log.i("Tag","存在");
		}else{
			Log.i("Tag","不存在");
		}
	}
	
	
	
	
	public void count(){
		mesAllMethod mes = new mesAllMethod(this.getContext());
		int mesDataCount = mes.mesDataCount();
		if(mesDataCount!=0){
			Log.i("Tag",String.valueOf(mesDataCount));
		}else{
			Log.i("Tag","展示没有数据");
		}
		
	}
	
	public void findList(){
		mesAllMethod mes = new mesAllMethod(this.getContext());
		List<mesPrecord> findList = mes.findAll();
		if(findList!=null){
			Log.i("Tag","--有数据");
		}else{
			Log.i("Tag","--展示没有数据");
		}
	}
	
	
	/*public void find(){
		mesAllMethod mes = new mesAllMethod(this.getContext());
		mes_precord findAll = mes.findAll();
		Log.i("Tag",findAll.toString());
	}*/
	
/*	public void deleteAll(){
		mesAllMethod mes = new mesAllMethod(this.getContext());
		mes.delteAll();
	}*/
	
	/*public void drop(){
		mesAllMethod mes = new mesAllMethod(this.getContext());
		mes.drop();
	}*/

}
