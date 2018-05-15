package com.yimei.sqlliteUtil;

import java.util.ArrayList;
import java.util.List;

import com.yimei.entity.Bean;
import com.yimei.entity.mesPrecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class mesAllMethod {

	private DBHelper dbhelp;
	private SQLiteDatabase sqlitedb;

	public mesAllMethod(Context context) {
		dbhelp = new DBHelper(context);
	}

	/**
	 * 二次返回id添加到本地库
	 * 
	 * @param mes
	 * @return
	 */
	public boolean addNewIdData(mesPrecord mes) {
		sqlitedb = dbhelp.getWritableDatabase();
		try {
			sqlitedb.execSQL(
					"insert into mes_precord (dcid,sbid,op,mkdate,smake,sbuid,prd_name,zcno,sid1,slkid,sid,prd_no,qty,zcno1,state,remark,bfirst,state1"
							+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] {mes.getDcid(),mes.getSbid(),mes.getOp(),mes.getMkdate(),mes.getSmake(),mes.getSbuid(),mes.getPrd_name(), mes.getZcno(),
							mes.getSid1(), mes.getSlkid(), mes.getSid(),
							mes.getPrd_no(), mes.getQty(), mes.getZcno1(),
							mes.getState(),mes.getRemark(),
							mes.getBfirst(),mes.getState1()});
		} catch (SQLException e) {
			// e.printStackTrace();
			Log.e("addNewIdData", e.toString());
			return false;
		}
		return true;
	}

	/**
	 * 查询设备+制程号在库中是否存在
	 */
	
	
	/**
	 * 设备号添加
	 * 
	 * @param mes
	 * @return
	 */
	public boolean addSbidData(mesPrecord mes) {
		sqlitedb = dbhelp.getWritableDatabase();
		try {
			sqlitedb.execSQL(
					"insert into mes_precord (sid,sid1,sbuid,zcno,op,sbid,slkid,"
							+ "prd_no,qty,bok,zcno1,state1,state,hpdate,dcid,smake,mkdate,remark,bfirst) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] {mes.getSid(),mes.getSid1(), mes.getSbuid(),
							mes.getZcno(), mes.getOp(), mes.getSbid(),
							mes.getSlkid(), mes.getPrd_no(), mes.getQty(),
							mes.getBok(), mes.getZcno1(), mes.getState1(),
							mes.getState(), mes.getHpdate(), mes.getDcid(),
							mes.getSmake(), mes.getRemark(),
							mes.getBfirst() });
		} catch (SQLException e) {
			Log.e("addNewIdData", e.toString());
			return false;
		}
		return true;
	}

	public boolean addData(mesPrecord mes) {
		sqlitedb = dbhelp.getWritableDatabase();
		try {
			sqlitedb.execSQL(
					"insert into mes_precord (prd_name,zcno,op,sbid,sid1,aa,ab,ac,slkid,sid,sbuid,prd_no,qty,bok,clid,unit"
							+ ",zcno1,state1,state,creftimes,erid,hpdate,dcid,smake,mkdate,iid,remark,bfirst"
							+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] { mes.getPrd_name(), mes.getZcno(),
							mes.getOp(), mes.getSbid(), mes.getSid1(),
							mes.getAa(), mes.getAb(), mes.getAc(),
							mes.getSlkid(), mes.getSid(), mes.getSbuid(),
							mes.getPrd_no(), mes.getQty(), mes.getBok(),
							mes.getClid(), mes.getUnit(), mes.getZcno1(),
							mes.getState1(), mes.getState(),
							mes.getCreftimes(), mes.getErid(), mes.getHpdate(),
							mes.getDcid(), mes.getSmake(), mes.getMkdate(),
							mes.getIid(), mes.getRemark(), mes.getBfirst() });
		} catch (SQLException e) {
			// e.printStackTrace();
			Log.i("Add", e.toString());
			return false;
		}
		return true;
	}

	/**
	 * 到本地库查找批次号(当前的设备号与当前制程)
	 * 
	 * @param prd_no
	 * @return
	 */
	public boolean sid1_Select(String prd_no, String shebeihao,String zcno) {
		sqlitedb = dbhelp.getWritableDatabase();
		Cursor cursor = sqlitedb.query("mes_precord", null,
				"sid1=? and sbid=? and zcno=?", new String[] { prd_no, shebeihao,zcno }, null,
				null, null);
		int count = cursor.getCount();
		boolean flag;
		switch (count) {
		case -1:
			flag = false;
			break;
		case 0:
			flag = false;
			break;
		default:
			flag = true;
		}
		return flag;
	}
	
	
	/**
	 * 到本地库查找批次号(当前的批次号与当前制程)
	 * 
	 * @param prd_no,zcno
	 * @return
	 */
	public boolean IsSid1AndZnco(String prd_no,String zcno) {
		sqlitedb = dbhelp.getWritableDatabase();
		Cursor cursor = sqlitedb.query("mes_precord", null,
				"sid1=? and zcno=?", new String[] { prd_no,zcno }, null,
				null, null);
		int count = cursor.getCount();
		boolean flag;
		switch (count) {
		case -1:
			flag = false;
			break;
		case 0:
			flag = false;
			break;
		default:
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 到本地库查找批次号(当前的批次号与当前制程+设备号) 拿服务器全部数据
	 * 
	 * @param prd_no,zcno
	 * @return
	 */
	public boolean IsSid1AndZncoAndSbid(String prd_no,String zcno,String sbid) {
		sqlitedb = dbhelp.getWritableDatabase();
		Cursor cursor = sqlitedb.query("mes_precord", null,
				"sid1=? and zcno=? and sbid=?", new String[] { prd_no,zcno,sbid }, null,
				null, null);
		int count = cursor.getCount();
		boolean flag;
		switch (count) {
		case -1:
			flag = false;
			break;
		case 0:
			flag = false;
			break;
		default:
			flag = true;
		}
		return flag;
	}

	
	/**
	 * 到本地库查找设备号
	 * 
	 * @param prd_no
	 * @return
	 */
	public boolean sbid_Select(String sbid) {
		sqlitedb = dbhelp.getWritableDatabase();
		Cursor cursor = sqlitedb.rawQuery(
				"select sbid from mes_precord where sbid=?",new String[]{sbid});
		int count = cursor.getCount();
		boolean flag;
		switch (count) {
		case -1:
			flag = false;
			break;
		case 0:
			flag = false;
			break;
		default:
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 按条件查设备号
	 * 
	 * @param sbid
	 * @return
	 */
	@SuppressWarnings("null")
	public List<mesPrecord> findList(String sbid,String zcno) {

		sqlitedb = dbhelp.getWritableDatabase();
		Cursor cursor = sqlitedb
				.rawQuery(
						"select prd_name,zcno,op,sbid,sid1,aa,ab,ac,slkid,sid,sbuid,prd_no,qty,bok,clid,unit,"
								+ "zcno1,state1,state,creftimes,erid,hpdate,dcid,smake,mkdate,iid,"
								+ "remark,bfirst from mes_precord where sbid=? and zcno=?",
						new String[]{sbid,zcno});
//		List<mes_precord> list = new ArrayList<mes_precord>();
		List<mesPrecord> list = null;
		if (cursor != null) {
			list = new ArrayList<mesPrecord>();
			while (cursor.moveToNext()) {
				mesPrecord m = new mesPrecord();
				m.setPrd_name(cursor.getString(cursor
						.getColumnIndex("prd_name")));
				m.setZcno(cursor.getString(cursor.getColumnIndex("zcno")));
				m.setOp(cursor.getString(cursor.getColumnIndex("op")));
				m.setSbid(cursor.getString(cursor.getColumnIndex("sbid")));
				m.setSid1(cursor.getString(cursor.getColumnIndex("sid1")));
				m.setAa(cursor.getString(cursor.getColumnIndex("aa")));
				m.setAb(cursor.getString(cursor.getColumnIndex("ab")));
				m.setAc(cursor.getString(cursor.getColumnIndex("ac")));
				m.setSlkid(cursor.getString(cursor.getColumnIndex("slkid")));
				m.setSid(cursor.getString(cursor.getColumnIndex("sid")));
				m.setSbuid(cursor.getString(cursor.getColumnIndex("sbuid")));
				m.setPrd_no(cursor.getString(cursor.getColumnIndex("prd_no")));
				m.setQty(cursor.getString(cursor.getColumnIndex("qty")));
				m.setBok(cursor.getString(cursor.getColumnIndex("bok")));
				m.setClid(cursor.getString(cursor.getColumnIndex("clid")));
				m.setUnit(cursor.getInt(cursor.getColumnIndex("unit")));
				m.setZcno1(cursor.getString(cursor.getColumnIndex("zcno1")));
				m.setState1(cursor.getString(cursor.getColumnIndex("state1")));
				m.setState(cursor.getString(cursor.getColumnIndex("state")));
				m.setCreftimes(cursor.getString(cursor.getColumnIndex("creftimes")));
				m.setErid(cursor.getString(cursor.getColumnIndex("erid")));
				m.setHpdate(cursor.getString(cursor.getColumnIndex("hpdate")));
				m.setDcid(cursor.getString(cursor.getColumnIndex("dcid")));
				m.setSmake(cursor.getString(cursor.getColumnIndex("smake")));
				m.setMkdate(cursor.getString(cursor.getColumnIndex("mkdate")));
				m.setIid(cursor.getInt(cursor.getColumnIndex("iid")));
				m.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
				m.setBfirst(cursor.getString(cursor.getColumnIndex("bfirst")));
				list.add(m);
			}
			
		}
		return list;
	}
	
	
	
	/**
	 * 查看所有记录
	 * 
	 * @param sbid
	 * @return
	 */
	@SuppressWarnings("null")
	public List<mesPrecord> findAll() {

		sqlitedb = dbhelp.getWritableDatabase();
		Cursor cursor = sqlitedb
				.rawQuery(
						"select prd_name,zcno,op,sbid,sid1,aa,ab,ac,slkid,sid,sbuid,prd_no,qty,bok,clid,unit,"
								+ "zcno1,state1,state,creftimes,erid,hpdate,dcid,smake,mkdate,iid,"
								+ "remark,bfirst from mes_precord",
						null);
//		List<mes_precord> list = new ArrayList<mes_precord>();
		List<mesPrecord> list = null;
		if (cursor != null) {
			list = new ArrayList<mesPrecord>();
			while (cursor.moveToNext()) {
				mesPrecord m = new mesPrecord();
				m.setPrd_name(cursor.getString(cursor
						.getColumnIndex("prd_name")));
				m.setZcno(cursor.getString(cursor.getColumnIndex("zcno")));
				m.setOp(cursor.getString(cursor.getColumnIndex("op")));
				m.setSbid(cursor.getString(cursor.getColumnIndex("sbid")));
				m.setSid1(cursor.getString(cursor.getColumnIndex("sid1")));
				m.setAa(cursor.getString(cursor.getColumnIndex("aa")));
				m.setAb(cursor.getString(cursor.getColumnIndex("ab")));
				m.setAc(cursor.getString(cursor.getColumnIndex("ac")));
				m.setSlkid(cursor.getString(cursor.getColumnIndex("slkid")));
				m.setSid(cursor.getString(cursor.getColumnIndex("sid")));
				m.setSbuid(cursor.getString(cursor.getColumnIndex("sbuid")));
				m.setPrd_no(cursor.getString(cursor.getColumnIndex("prd_no")));
				m.setQty(cursor.getString(cursor.getColumnIndex("qty")));
				m.setBok(cursor.getString(cursor.getColumnIndex("bok")));
				m.setClid(cursor.getString(cursor.getColumnIndex("clid")));
				m.setUnit(cursor.getInt(cursor.getColumnIndex("unit")));
				m.setZcno1(cursor.getString(cursor.getColumnIndex("zcno1")));
				m.setState1(cursor.getString(cursor.getColumnIndex("state1")));
				m.setState(cursor.getString(cursor.getColumnIndex("state")));
				m.setCreftimes(cursor.getString(cursor.getColumnIndex("creftimes")));
				m.setErid(cursor.getString(cursor.getColumnIndex("erid")));
				m.setHpdate(cursor.getString(cursor.getColumnIndex("hpdate")));
				m.setDcid(cursor.getString(cursor.getColumnIndex("dcid")));
				m.setSmake(cursor.getString(cursor.getColumnIndex("smake")));
				m.setMkdate(cursor.getString(cursor.getColumnIndex("mkdate")));
				m.setIid(cursor.getInt(cursor.getColumnIndex("iid")));
				m.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
				m.setBfirst(cursor.getString(cursor.getColumnIndex("bfirst")));
				list.add(m);
			}
			
		}
		return list;
	}

	/**
	 * 上料状态修改
	 * 
	 * @param sbid
	 * @return
	 */
	public boolean shanliaoState1Update(String sid1) {
		sqlitedb = dbhelp.getWritableDatabase();
		try {
			sqlitedb.execSQL("update mes_precord set state1='02' where sid1=?",
					new Object[] { sid1 });
		} catch (SQLException e) {
			Log.e("Tag", e.toString());
			return false;
		}
		return true;
	}
	/**
	 * 开工状态修改
	 * 
	 * @param sbid
	 * @return
	 */
	public boolean kaigongState1Update(String sid1,String hpdate) {
		sqlitedb = dbhelp.getWritableDatabase();
		try {
			sqlitedb.execSQL("update mes_precord set state1='03',hpdate='"+hpdate+"' where sid1=?",
					new Object[] { sid1 });
		} catch (SQLException e) {
			Log.e("Tag", e.toString());
			return false;
		}
		return true;
	}

	/**
	 * 出站状态修改
	 * 
	 * @param sbid
	 * @return
	 */
	public boolean chuzhanState1Update(String sid1) {
		sqlitedb = dbhelp.getWritableDatabase();
		try {
			sqlitedb.execSQL("update mes_precord set state1='04' where sid1=?",
					new Object[] { sid1 });
		} catch (SQLException e) {
			Log.e("Tag", e.toString());
			return false;
		}
		return true;
	}

	/**
	 * 删除表
	 */
	public void drop() {
		sqlitedb = dbhelp.getWritableDatabase();
		sqlitedb.execSQL("drop table mes_precord");
	}

	/**
	 * 删除所有数据
	 */
	public void delteAll() {
		sqlitedb = dbhelp.getWritableDatabase();
		sqlitedb.execSQL("delete from mes_precord");
	}

	/**
	 * 查库总数量
	 * 
	 * @return
	 */
	public int mesDataCount() {
		sqlitedb = dbhelp.getWritableDatabase();
		Cursor cursor = sqlitedb.rawQuery(
				"select count(*) from mes_precord ", null);
		if (cursor.moveToFirst()) {
			int count = cursor.getInt(0);
			cursor.close();
			return count;
		} else {
			return -1;
		}

	}


	

	public mesPrecord findAllOne() {
		sqlitedb = dbhelp.getWritableDatabase();
		Cursor cursor = sqlitedb
				.rawQuery(
						"select zcno,op,sbid,sid1,aa,ab,ac,slkid,sid,sbuid,prd_no,qty,bok,clid,unit,"
								+ "zcno1,state1,state,creftimes,erid,hpdate,dcid,smake,mkdate,iid,"
								+ "remark,bfirst from mes_precord", null);
		if (cursor.moveToFirst()) {
			mesPrecord m = new mesPrecord();
			m.setZcno(cursor.getString(cursor.getColumnIndex("zcno")));
			m.setOp(cursor.getString(cursor.getColumnIndex("op")));
			m.setSbid(cursor.getString(cursor.getColumnIndex("sbid")));
			m.setSid1(cursor.getString(cursor.getColumnIndex("sid1")));
			m.setAa(cursor.getString(cursor.getColumnIndex("aa")));
			m.setAb(cursor.getString(cursor.getColumnIndex("ab")));
			m.setAc(cursor.getString(cursor.getColumnIndex("ac")));
			m.setSlkid(cursor.getString(cursor.getColumnIndex("slkid")));
			m.setSid(cursor.getString(cursor.getColumnIndex("sid")));
			m.setSbuid(cursor.getString(cursor.getColumnIndex("sbuid")));
			m.setPrd_no(cursor.getString(cursor.getColumnIndex("prd_no")));
			m.setQty(cursor.getString(cursor.getColumnIndex("qty")));
			m.setBok(cursor.getString(cursor.getColumnIndex("bok")));
			m.setClid(cursor.getString(cursor.getColumnIndex("clid")));
			m.setUnit(cursor.getInt(cursor.getColumnIndex("unit")));
			m.setZcno1(cursor.getString(cursor.getColumnIndex("zcno1")));
			m.setState1(cursor.getString(cursor.getColumnIndex("state1")));
			m.setState(cursor.getString(cursor.getColumnIndex("state")));
			m.setCreftimes(cursor.getString(cursor.getColumnIndex("creftimes")));
			m.setErid(cursor.getString(cursor.getColumnIndex("erid")));
			m.setHpdate(cursor.getString(cursor.getColumnIndex("hpdate")));
			m.setDcid(cursor.getString(cursor.getColumnIndex("dcid")));
			m.setSmake(cursor.getString(cursor.getColumnIndex("smake")));
			m.setMkdate(cursor.getString(cursor.getColumnIndex("mkdate")));
			m.setIid(cursor.getInt(cursor.getColumnIndex("iid")));
			m.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
			m.setBfirst(cursor.getString(cursor.getColumnIndex("bfirst")));
			return m;
		}
		return null;
	}

	public boolean delete_id(String id) {
		sqlitedb = dbhelp.getWritableDatabase();
		int delete = sqlitedb.delete("yimei_gujing", "pihao=?",
				new String[] { id });
		if (delete != 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean delSid(String chuzhansid1) {
		sqlitedb = dbhelp.getWritableDatabase();
		int delete = sqlitedb.delete("mes_precord", "sid=?",
				new String[] { chuzhansid1 });
		if (delete != 0) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * public Bean find(){ sqlitedb = dbhelp.getWritableDatabase();
	 * sqlitedb.rawQuery Cursor cursor = Cursor cursor = sqlitedb.rawQuery(
	 * "select pihao,gongdan,jizhong,shuliang,info from yimei_gujing",null);
	 * if(cursor.moveToNext()){ return new
	 * Bean(cursor.getString(cursor.getColumnIndex
	 * ("pihao")),cursor.getString(cursor
	 * .getColumnIndex("gongdan")),cursor.getString
	 * (cursor.getColumnIndex("jizhong"))
	 * ,cursor.getString(cursor.getColumnIndex
	 * ("shuliang")),cursor.getString(cursor.getColumnIndex("info"))); } return
	 * null; }
	 */
}
