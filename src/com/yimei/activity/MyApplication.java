package com.yimei.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MyApplication extends Application {
	public static String MESURL = "http://192.168.7.15:8088/mes/api";
//	public static String MESURL = "http://59.53.182.251:8088/mes/api";
	
	public static String DBID = "01";
//	public static String DBID = "mes";

//	public static String MESURL = "http://192.168.0.117:9999/jd/api";
//	public static String MESURL = "http://192.168.6.80:9999/jd/api";
//	public static String MESURL = "http://192.168.1.86:9999/jd/api";

	
	public static final String MESServerTime = "http://192.168.7.15:8088/mes/mservlet";  //服务器时间
	public static final String MESDOWNLOADAPKURL = "http://192.168.7.15:8088/mes/yimei.apk"; //下载apk
	public static String user = ""; //登录的人
	public static String sorg = ""; //部门
	public static final String MESIP = "IP:"+MESURL.substring(7,MESURL.lastIndexOf(":"));

	public static final String INTENT_ACTION_SCAN_RESULT = "com.android.server.scannerservice.broadcast"; // 广播接收Action值
	public static final String SCN_CUST_EX_SCODE = "scannerdata";
	public static final String TONGYONG_VTEXT = "1.增加换机型进行首检审核"
												+ "2.修改首检工单错误"
												+ "3.增加支架绑定";
	public static final String SHOW_VERSION = "v18-08-24 13:30";
	public static final int LOGOUT = 1; // 切换用户
	public static final int ABOUTUS = 2; // 关于我们
	public static final int VERSION = 3; // 版本信息
	public static final int ERRORNOTICE = 4; // 异常通知单
	public static final String LOGOUTText = "切换用户"; // 切换用户
	public static final String ABOUTUSText = "关于我们"; // 关于我们
	public static final String VERSIONText = "版本信息"; // 版本信息
	public static final String ERRORNOTICETEXT = "异常通知单"; // 异常通知单
	
	
	public static final String QIJIANSORG="05010000"; //器件部门
	public static final String MOZUSORG="05040000"; //模组部门
	public static final String SHOUJIAN_BUID = "Q00201"; //首检业务号
	public static final String SHEBEISBUID = "E6001"; //维修设备业务号
	
	
	public static String Base64pwd(String pwd) {
		return Base64.encodeToString(pwd.getBytes(), Base64.DEFAULT);
	}
	

	/**
	 * 获取服务器时间
	 * 
	 * @return
	 */
	public static String GetServerNowTime() {
		//GregorianCalendar g = new GregorianCalendar();
		Calendar c = Calendar.getInstance();
		String aa = df.format(new Date(c.getTimeInMillis()));
		String bb = df.format(new Date(ServerTimeCha));
		Date d = new Date(c.getTimeInMillis() + ServerTimeCha); //当前毫秒数 + 服务器时间戳
		String a = df.format(d.getTime());
		// g.setTime(d);
		return df.format(d.getTime());
	}

	/**
	 * 胶杯加3小时
	 * 
	 * @return
	 */
	public static String GetHunJiaoAdd_3(int num) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, num);
		if (ServerTimeCha < 0) {
			Date d = new Date(c.getTimeInMillis() - Math.abs(ServerTimeCha));
			String a = df.format(d.getTime());
			return df.format(d.getTime());
		} else {
			Date d = new Date(c.getTimeInMillis() + ServerTimeCha);
			String a = df.format(d.getTime());
			return df.format(d.getTime());
		}
	}

	/**
	 * 出站时间
	 * 
	 * @param kaigongTime
	 * @return
	 */
	public static int ChooseTime(String kaigongTime) {
		if (kaigongTime == null) {
			return 999;
		}
		if (kaigongTime != null && kaigongTime.length() == 16) {
			kaigongTime += ":00";
		}
		long xiangchaTime = 0, nowTime = 0;
		try {
			Calendar c = Calendar.getInstance();
			Date d = new Date(c.getTimeInMillis() + ServerTimeCha);
			String a = df.format(d.getTime());
			nowTime = d.getTime();

			xiangchaTime = Math.abs(nowTime - df.parse(kaigongTime).getTime());
		} catch (Exception e) {
			return 1;
		}
		return (int) xiangchaTime / 1000 / 60;
	}

	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");// 设置日期格式
	public static long ServerTimeCha = 0; //手机和服务

	public static final String GUJING_ZCNO = "11"; //固晶
	public static final String HANJIE_ZCNO = "21"; //焊接
	public static final String DIANJIAO_ZCNO = "31"; //点胶
	public static final String KAOXIANG_ZCNO = "41"; //烤箱
	public static final String BIANDAI_ZCNO = "71"; //编带
	public static final String KANDAI_ZCNO = "81"; //看带
	//快速过站
	public static final String GAOWENDIANLIANG_ZCNO = "S06"; //高温点->外观
	public static final String WaiGuan_ZCNO = "S07"; //外观->贴背胶
	public static final String TieBieJiao_ZCNO = "S08"; //贴背胶->低电流点亮
	public static final String DiDianLiu_ZCNO = "S09"; //低电流->内装
	/**
	 * 通用
	 * 
	 * @param cont
	 *            辅助名
	 * @param assistid
	 *            参数
	 * @return
	 */
	public static Map<String, String> QueryBatNo(String assistid, String cont) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("dbid", MyApplication.DBID);
		map.put("usercode", MyApplication.user);
		map.put("apiId", "assist");
		map.put("assistid", "{" + assistid + "}");
		map.put("cont", cont);
		return map;
	}

	/**
	 * 混胶301
	 * 
	 */
	public static Map<String, String> hunjiao_301(String prtno,
			String effective_time) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("dbid", MyApplication.DBID);
		map.put("usercode", MyApplication.user);
		map.put("prtno", prtno);
		map.put("qty", effective_time);
		map.put("apiId", "mesudp");
		map.put("id", "301");
		return map;
	}

	/**
	 * 查询是否有设备号和制程 （编带）
	 * 
	 * @param sbid
	 * @param zcno
	 * @return
	 */
	public static Map<String, String> IsSbidQuery_biandai(String sbid,
			String zcno) {
		Map<String, String> mapSbid = new HashMap<String, String>();
		mapSbid.put("dbid", MyApplication.DBID);
		mapSbid.put("usercode", MyApplication.user);
		mapSbid.put("apiId", "assist");
		mapSbid.put("assistid", "{MESEQUTM}");
		mapSbid.put("cont", "~id='" + sbid + "' and zc_id='71' ");
		return mapSbid;
	}

	/**
	 * 查询是否有设备号和制程 （模组）
	 * 
	 * @param sbid
	 * @param zcno
	 * @return
	 */
	public static Map<String, String> IsSbidQuery_mozu(String sbid, String zcno) {
		Map<String, String> mapSbid = new HashMap<String, String>();
		mapSbid.put("dbid", MyApplication.DBID);
		mapSbid.put("usercode", MyApplication.user);
		mapSbid.put("apiId", "assist");
		mapSbid.put("assistid", "{MESEQUTM}");
		mapSbid.put("cont", "~id='" + sbid + "' and zc_id='" + zcno + "' ");
		return mapSbid;
	}

	/**
	 * 焦点跳转
	 * 
	 * @param Nextid
	 */
	public static void nextEditFocus(EditText Nextid) {
		Nextid.setFocusable(true);
		Nextid.setFocusableInTouchMode(true);
		Nextid.requestFocus();
		Nextid.findFocus();
	}

	/**
	 * 服务器(增删改查)
	 * 
	 * @param dbid
	 * @param apiId
	 *            （savedata）
	 * @param usercode
	 * @param jsonstr
	 *            （json串）
	 * @param pcell
	 *            （对象名）
	 * @param datatype
	 *            （1）
	 * @return
	 */
	public static Map<String, String> httpMapKeyValueMethod(String dbid,
			String apiId, String usercode, String jsonstr, String pcell,
			String datatype) {
		HashMap<String, String> httpMapKeyValue = new HashMap<String, String>();
		httpMapKeyValue.put("dbid", dbid);
		httpMapKeyValue.put("apiId", apiId);
		httpMapKeyValue.put("usercode", usercode);
		httpMapKeyValue.put("jsonstr", jsonstr);
		httpMapKeyValue.put("pcell", pcell);
		httpMapKeyValue.put("datatype", datatype);
		return httpMapKeyValue;
	}

	/**
	 * 修改服务器俩张表（id区分上料|（开工，出站）） 200
	 * 
	 * @param dbid
	 * @param usercode
	 * @param oldstate
	 * @param newstate
	 * @param datefld
	 * @param sid1
	 * @param slkid
	 * @param zcno
	 * @param id
	 * @return
	 */
	public static Map<String, String> UpdateServerTableMethod(String dbid,
			String usercode, String oldstate, String newstate, String sid1,
			String slkid, String zcno, String id) {
		Map<String, String> UpdateServerTable = new HashMap<String, String>();
		UpdateServerTable.put("dbid", dbid);
		UpdateServerTable.put("usercode", usercode);
		UpdateServerTable.put("oldstate", oldstate);
		UpdateServerTable.put("newstate", newstate);
		UpdateServerTable.put("sid", sid1);
		UpdateServerTable.put("slkid", slkid);
		UpdateServerTable.put("zcno", zcno);
		UpdateServerTable.put("apiId", "mesudp");
		UpdateServerTable.put("id", id);
		return UpdateServerTable;
	}
	
	/**
	 * 
	 * @param dbid 
	 * @param usercode
	 * @param sid1 批次
	 * @param zcno 工序
	 * @param mbox 料盒
	 * @param bind 状态
	 * @param id 650
	 * @return
	 */
	public static Map<String, String> MBoxServerUpdate(String dbid,
			String usercode, String sid1, String zcno, String mbox,
			String bind,String id) {
		Map<String, String> UpdateServerTable = new HashMap<String, String>();
		UpdateServerTable.put("dbid", dbid);
		UpdateServerTable.put("usercode", usercode);
		UpdateServerTable.put("sid1", sid1);
		UpdateServerTable.put("zcno", zcno);
		UpdateServerTable.put("mbox", mbox);
		UpdateServerTable.put("bind", bind);
		UpdateServerTable.put("apiId", "mesudp");
		UpdateServerTable.put("id", id);
		return UpdateServerTable;
	}

	/**
	 * 上料前的准备（id 201）
	 * 
	 * @param dbid
	 * @param usercode
	 * @param sid1
	 * @param zcno
	 * @param op
	 * @param sbid
	 * @param slkid
	 * @param qtyv
	 * @param checkidv
	 * @param checkqtyv
	 * @param id
	 * @return
	 */
	public static Map<String, String> ShangLiaoReadyMethod(String dbid,
			String usercode, String sid1, String zcno, String op, String sbid,
			String slkid, String qtyv, String checkidv, String checkqtyv,
			String id) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("dbid", dbid);
		map.put("usercode", usercode);
		map.put("sidv", sid1);
		map.put("zcnov", zcno);
		map.put("opv", op);
		map.put("sbidv", sbid);
		map.put("slkidv", slkid);
		map.put("qtyv", qtyv); // 批次数量
		map.put("checkidv", checkidv); // 验证方式
		map.put("checkqtyv", checkqtyv); // 验证数量
		map.put("apiId", "mesudp");
		map.put("id", id);
		return map;
	}

	/**
	 * 修改时间（入站，开工，出站） id （202）
	 * 
	 * @param dbid
	 * @param usercode
	 * @param oldstate
	 * @param newstate
	 * @param sid
	 * @param op
	 * @param zcno
	 * @param id
	 *            （202）
	 * @return
	 */
	public static Map<String, String> updateServerTimeMethod(String dbid,
			String usercode, String oldstate, String newstate, String sid,
			String op, String zcno, String id) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("dbid", dbid);
		map.put("usercode", usercode);
		map.put("oldstate", oldstate);
		map.put("newstate", newstate);
		map.put("sid", sid);
		map.put("op", op);
		map.put("zcno", zcno);
		map.put("id", id);
		map.put("apiId", "mesudp");
		return map;
	}

	/**
	 * 加胶300
	 * 
	 */
	public static Map<String, String> updateServerJiaJiao(String dbid,
			String usercode, String sbidv, String prtnov, String id) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("dbid", dbid);
		map.put("usercode", usercode);
		map.put("sbid", sbidv);
		map.put("prtno", prtnov);
		map.put("apiId", "mesudp");
		map.put("id", id);
		return map;
	}

	/**
	 * 模具 400
	 * 
	 */
	public static Map<String, String> updateServerMoJu(String dbid,
			String usercode, String zct, String mojuid, String id) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("dbid", dbid);
		map.put("usercode", usercode);
		map.put("newstate", zct);
		map.put("sbid", mojuid);
		map.put("apiId", "mesudp");
		map.put("id", id);
		return map;
	}
	
	/**
	 * 锡膏 410
	 * 
	 */
	public static Map<String, String> updateServerJiaXiaoGao(String dbid,
			String usercode, String sbid, String sph, String id) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("dbid", dbid);
		map.put("usercode", usercode);
		map.put("sbid", sbid);
		map.put("sph", sph);
		map.put("apiId", "mesudp");
		map.put("id", id);
		return map;
	}
	
	/**
	 * 锡膏 410 prtno
	 * 
	 */
	public static Map<String, String> updateServerJiaXiaoGao1(String dbid,
			String usercode, String sbid, String sph, String id) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("dbid", dbid);
		map.put("usercode", usercode);
		map.put("sbid", sbid);
		map.put("prtno", sph);
		map.put("apiId", "mesudp");
		map.put("id", id);
		return map;
	}

	/**
	 * 状态
	 * 
	 * @return
	 */
	public static Map<String, String> getStateName() {
		Map<String, String> stateName = new HashMap<String, String>();
		stateName.put("00", "准备");
		stateName.put("01", "已入站");
		stateName.put("02", "已上料");
		stateName.put("03", "生产中");
		stateName.put("04", "已出站");
		stateName.put("0A", "异常");
		stateName.put("0B", "暂停");
		stateName.put("0C", "中止");
		stateName.put("0D", "受控");
		return stateName;
	}

	/*
	 * 公共变量
	 */
	private List<Activity> aList;// 用于存放所有启动的Activity的集合

	public int getListCount() {
		return aList.size();
	}

	public void onCreate() {
		super.onCreate();
		aList = new ArrayList<Activity>();
	}

	/**
	 * 添加Activity
	 */
	public void addActivity_(Activity activity) {
		// 判断当前集合中不存在该Activity
		if (!aList.contains(activity)) {
			aList.add(activity);// 把当前Activity添加到集合中
		}
	}

	/**
	 * 销毁单个Activity
	 */
	public void removeActivity_(Activity activity) {
		// 判断当前集合中存在该Activity
		if (aList.contains(activity)) {
			aList.remove(activity);// 从集合中移除
			activity.finish();// 销毁当前Activity
		}
	}

	/**
	 * 销毁所有的Activity
	 */
	public void removeALLActivity_() {
		// 通过循环，把集合中的所有Activity销毁
		for (Activity activity : aList) {
			activity.finish();
		}
	}
}
