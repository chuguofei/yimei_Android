package com.yimei.entity;

public class mesMrecord {

	
	private String checkid;
	private String checkqty;
	private String sbid;
	private String op;
	private String sid;
	private String zcno;
	private String sid1;
	private String slkid;
	private String slkbuid;
	private String qty;
	private String prd_name;
	private int unit;
	private String hpdate;
	private String erid;
	private String sbuid;
	private String state;
	private int creftimes;
	private String dcid;
	private String smake;
	private String mkdate;
	private String clid;
	private String bbno;
	private Double totalqty;
	
	
	
	public mesMrecord(String checkid, String checkqty, String sbid, String op,
			String sid, String zcno, String sid1, String slkid, String slkbuid,
			String qty, String prd_name, int unit, String hpdate, String erid,
			String sbuid, String state, int creftimes, String dcid,
			String smake, String mkdate, String clid, String bbno,
			Double totalqty) {
		super();
		this.checkid = checkid;
		this.checkqty = checkqty;
		this.sbid = sbid;
		this.op = op;
		this.sid = sid;
		this.zcno = zcno;
		this.sid1 = sid1;
		this.slkid = slkid;
		this.slkbuid = slkbuid;
		this.qty = qty;
		this.prd_name = prd_name;
		this.unit = unit;
		this.hpdate = hpdate;
		this.erid = erid;
		this.sbuid = sbuid;
		this.state = state;
		this.creftimes = creftimes;
		this.dcid = dcid;
		this.smake = smake;
		this.mkdate = mkdate;
		this.clid = clid;
		this.bbno = bbno;
		this.totalqty = totalqty;
	}
	@Override
	public String toString() {
		return "mesMrecord [checkid=" + checkid + ", checkqty=" + checkqty
				+ ", sbid=" + sbid + ", op=" + op + ", sid=" + sid + ", zcno="
				+ zcno + ", sid1=" + sid1 + ", slkid=" + slkid + ", slkbuid="
				+ slkbuid + ", qty=" + qty + ", prd_name=" + prd_name
				+ ", unit=" + unit + ", hpdate=" + hpdate + ", erid=" + erid
				+ ", sbuid=" + sbuid + ", state=" + state + ", creftimes="
				+ creftimes + ", dcid=" + dcid + ", smake=" + smake
				+ ", mkdate=" + mkdate + ", clid=" + clid + ", bbno=" + bbno
				+ ", totalqty=" + totalqty + "]";
	}
	public String getCheckid() {
		return checkid;
	}
	public void setCheckid(String checkid) {
		this.checkid = checkid;
	}
	public String getCheckqty() {
		return checkqty;
	}
	public void setCheckqty(String checkqty) {
		this.checkqty = checkqty;
	}
	public String getSbid() {
		return sbid;
	}
	public void setSbid(String sbid) {
		this.sbid = sbid;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getZcno() {
		return zcno;
	}
	public void setZcno(String zcno) {
		this.zcno = zcno;
	}
	public String getSid1() {
		return sid1;
	}
	public void setSid1(String sid1) {
		this.sid1 = sid1;
	}
	public String getSlkid() {
		return slkid;
	}
	public void setSlkid(String slkid) {
		this.slkid = slkid;
	}
	public String getSlkbuid() {
		return slkbuid;
	}
	public void setSlkbuid(String slkbuid) {
		this.slkbuid = slkbuid;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getPrd_name() {
		return prd_name;
	}
	public void setPrd_name(String prd_name) {
		this.prd_name = prd_name;
	}
	public int getUnit() {
		return unit;
	}
	public void setUnit(int unit) {
		this.unit = unit;
	}
	public String getHpdate() {
		return hpdate;
	}
	public void setHpdate(String hpdate) {
		this.hpdate = hpdate;
	}
	public String getErid() {
		return erid;
	}
	public void setErid(String erid) {
		this.erid = erid;
	}
	public String getSbuid() {
		return sbuid;
	}
	public void setSbuid(String sbuid) {
		this.sbuid = sbuid;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getCreftimes() {
		return creftimes;
	}
	public void setCreftimes(int creftimes) {
		this.creftimes = creftimes;
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
	public String getClid() {
		return clid;
	}
	public void setClid(String clid) {
		this.clid = clid;
	}
	public String getBbno() {
		return bbno;
	}
	public void setBbno(String bbno) {
		this.bbno = bbno;
	}
	public Double getTotalqty() {
		return totalqty;
	}
	public void setTotalqty(Double totalqty) {
		this.totalqty = totalqty;
	}

}
