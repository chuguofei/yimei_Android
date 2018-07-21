package com.yimei.entity;

import java.io.Serializable;

public class mesPrecord implements Serializable {

	public mesPrecord() {
	}

	public mesPrecord(String zcno1, String zcno, String bfirst, String prd_no,
			String remark, String prd_name, String sbid, String sid1,
			String qty, String state, String bok, String slkid) {
		super();
		this.zcno = zcno;
		this.sbid = sbid;
		this.sid1 = sid1;
		this.prd_name = prd_name;
		this.prd_no = prd_no;
		this.qty = qty;
		this.bok = bok;
		this.zcno1 = zcno1;
		this.state = state;
		this.remark = remark;
		this.bfirst = bfirst;
		this.slkid = slkid;
	}

	public String getPrd_name() {
		return prd_name;
	}

	public void setPrd_name(String prd_name) {
		this.prd_name = prd_name;
	}

	private String prd_name;
	private String zcno;
	private String op;
	private String sbid;
	private String sid1;
	private String aa;
	private String ab;
	private String ac;
	private String slkid;
	private String sid;
	private String sbuid;
	private String prd_no;
	private String qty;
	private String bok;
	private String clid;
	private int unit;
	private String zcno1;
	private String state1;
	private String state;
	private String creftimes;
	private String erid;
	private String hpdate;
	private String dcid;
	private String smake;
	private String mkdate;
	private int iid;
	private String remark;
	private String bfirst;
	private int cref3;
	private String fircheck; //首检检验
	private String textcolor; //是否进行首检
	private String mbox;

	public String getMbox() {
		return mbox;
	}

	public void setMbox(String mbox) {
		this.mbox = mbox;
	}

	public String getTextcolor() {
		return textcolor;
	}

	public void setTextcolor(String textcolor) {
		this.textcolor = textcolor;
	}

	public String getFircheck() {
		return fircheck;
	}

	public void setFircheck(String fircheck) {
		this.fircheck = fircheck;
	}

	public String getCkdate() {
		return ckdate;
	}

	public void setCkdate(String ckdate) {
		this.ckdate = ckdate;
	}

	private String ckdate;
	
	public int getCref3() {
		return cref3;
	}

	public void setCref3(int cref3) {
		this.cref3 = cref3;
	}

	// 测试字段
	private String totalqty;
	private String cid;
	private String fj_root;
	private String files;
	private String lotno;
	private String bincode;

	public String getBincode() {
		return bincode;
	}

	public void setBincode(String bincode) {
		this.bincode = bincode;
	}

	public String getTotalqty() {
		return totalqty;
	}

	public void setTotalqty(String totalqty) {
		this.totalqty = totalqty;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getFj_root() {
		return fj_root;
	}

	public void setFj_root(String fj_root) {
		this.fj_root = fj_root;
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	public String getLotno() {
		return lotno;
	}

	public void setLotno(String lotno) {
		this.lotno = lotno;
	}

	public String getZcno() {
		return zcno;
	}

	public void setZcno(String zcno) {
		this.zcno = zcno;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getSbid() {
		return sbid;
	}

	public void setSbid(String sbid) {
		this.sbid = sbid;
	}

	public String getSid1() {
		return sid1;
	}

	public void setSid1(String sid1) {
		this.sid1 = sid1;
	}

	public String getAa() {
		return aa;
	}

	public void setAa(String aa) {
		this.aa = aa;
	}

	public String getAb() {
		return ab;
	}

	public void setAb(String ab) {
		this.ab = ab;
	}

	public String getAc() {
		return ac;
	}

	public void setAc(String ac) {
		this.ac = ac;
	}

	public String getSlkid() {
		return slkid;
	}

	public void setSlkid(String slkid) {
		this.slkid = slkid;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getSbuid() {
		return sbuid;
	}

	public void setSbuid(String sbuid) {
		this.sbuid = sbuid;
	}

	public String getPrd_no() {
		return prd_no;
	}

	public void setPrd_no(String prd_no) {
		this.prd_no = prd_no;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getBok() {
		return bok;
	}

	public void setBok(String bok) {
		this.bok = bok;
	}

	public String getClid() {
		return clid;
	}

	public void setClid(String clid) {
		this.clid = clid;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public String getZcno1() {
		return zcno1;
	}

	public void setZcno1(String zcno1) {
		this.zcno1 = zcno1;
	}

	public String getCreftimes() {
		return creftimes;
	}

	public void setCreftimes(String creftimes) {
		this.creftimes = creftimes;
	}

	public String getErid() {
		return erid;
	}

	public void setErid(String erid) {
		this.erid = erid;
	}

	public String getHpdate() {
		return hpdate;
	}

	public void setHpdate(String hpdate) {
		this.hpdate = hpdate;
	}

	public String getDcid() {
		return dcid;
	}

	public void setDcid(String dcid) {
		this.dcid = dcid;
	}

	public String getSmake() {
		return smake;
	}

	public void setSmake(String smake) {
		this.smake = smake;
	}

	public String getMkdate() {
		return mkdate;
	}

	public void setMkdate(String mkdate) {
		this.mkdate = mkdate;
	}

	public int getIid() {
		return iid;
	}

	public void setIid(int iid) {
		this.iid = iid;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getBfirst() {
		return bfirst;
	}

	public void setBfirst(String bfirst) {
		this.bfirst = bfirst;
	}

	@Override
	public String toString() {
		return "mesPrecord [prd_name=" + prd_name + ", zcno=" + zcno + ", op="
				+ op + ", sbid=" + sbid + ", sid1=" + sid1 + ", aa=" + aa
				+ ", ab=" + ab + ", ac=" + ac + ", slkid=" + slkid + ", sid="
				+ sid + ", sbuid=" + sbuid + ", prd_no=" + prd_no + ", qty="
				+ qty + ", bok=" + bok + ", clid=" + clid + ", unit=" + unit
				+ ", zcno1=" + zcno1 + ", state1=" + state1 + ", state="
				+ state + ", creftimes=" + creftimes + ", erid=" + erid
				+ ", hpdate=" + hpdate + ", dcid=" + dcid + ", smake=" + smake
				+ ", mkdate=" + mkdate + ", iid=" + iid + ", remark=" + remark
				+ ", bfirst=" + bfirst + ", totalqty=" + totalqty + ", cid="
				+ cid + ", fj_root=" + fj_root + ", files=" + files
				+ ", lotno=" + lotno + " bincode=" + bincode + "]";
	}

	public mesPrecord(String prd_name, String zcno, String op, String sbid,
			String sid1, String aa, String ab, String ac, String slkid,
			String sid, String sbuid, String prd_no, String qty, String bok,
			String clid, int unit, String zcno1, String state1, String state,
			String creftimes, String erid, String hpdate, String dcid,
			String smake, String mkdate, int iid, String remark, String bfirst) {
		super();
		this.zcno = zcno;
		this.op = op;
		this.sbid = sbid;
		this.sid1 = sid1;
		this.aa = aa;
		this.prd_name = prd_name;
		this.ab = ab;
		this.ac = ac;
		this.slkid = slkid;
		this.sid = sid;
		this.sbuid = sbuid;
		this.prd_no = prd_no;
		this.qty = qty;
		this.bok = bok;
		this.clid = clid;
		this.unit = unit;
		this.zcno1 = zcno1;
		this.state1 = state1;
		this.state = state;
		this.creftimes = creftimes;
		this.erid = erid;
		this.hpdate = hpdate;
		this.dcid = dcid;
		this.smake = smake;
		this.mkdate = mkdate;
		this.iid = iid;
		this.remark = remark;
		this.bfirst = bfirst;
	}

	public String getState1() {
		return state1;
	}

	public void setState1(String state1) {
		this.state1 = state1;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
