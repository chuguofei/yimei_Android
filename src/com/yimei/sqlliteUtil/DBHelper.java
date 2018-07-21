package com.yimei.sqlliteUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

	public static final int VERSION = 1;
	private static final String DBNAME ="data.db";
		
	public DBHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}
	
	String mesPrecord = "create table mes_precord("
			+"prd_name VARCHAR(100),"
			+ "zcno VARCHAR(20),"
			+ "op	VARCHAR(20),"
			+ "sbid	VARCHAR(20),"
			+ "sid1	VARCHAR(40),"
			+ "aa	VARCHAR(10),"
			+ "ab	VARCHAR(10),"
			+ "ac	VARCHAR(10),"
			+ "slkid	VARCHAR(20),"
			+ "sid	VARCHAR(20),"
			+ "sbuid	VARCHAR(10),"
			+ "prd_no	VARCHAR(30),"
			+ "qty	DECIMAL(16),"
			+ "bok	CHAR(1),"
			+ "clid	VARCHAR(10),"
			+ "unit	SMALLINT(4),"
			+ "zcno1	VARCHAR(20),"
			+ "state1	VARCHAR(2),"
			+ "state	SMALLINT,"
			+ "creftimes	SMALLINT,"
			+ "erid	CHAR(1),"
			+ "hpdate	TIMESTAMP,"
			+ "dcid	VARCHAR(20),"
			+ "smake	VARCHAR(30),"
			+ "mkdate	TIMESTAMP,"
			+ "iid	INT,"
			+ "remark	VARCHAR(200),"
			+ "fircheck	VARCHAR(20),"
			+ "textcolor char(1),"
			+ "mbox VARCHAR(50),"
			+ "bfirst	CHAR(1))";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(mesPrecord);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
