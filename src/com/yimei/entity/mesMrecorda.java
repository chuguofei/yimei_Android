package com.yimei.entity;

public class mesMrecorda {

	private String cid;
	private String prd_no;
	private String bat_no;
	private String prd_name;
	private String prd_mark;
	private String qty;
	private String aa;
	private String unit;
	private String yzrem;
	private String iid;
	private String op;
	private String hpdate;
	private String dcid;
	private String sbid;
	private String zcno;
	
	public mesMrecorda(String cid, String prd_no, String bat_no,
			String prd_name, String prd_mark, String qty, String aa,
			String unit, String yzrem, String iid, String op, String hpdate,
			String dcid, String sbid, String zcno) {
		super();
		this.cid = cid;
		this.prd_no = prd_no;
		this.bat_no = bat_no;
		this.prd_name = prd_name;
		this.prd_mark = prd_mark;
		this.qty = qty;
		this.aa = aa;
		this.unit = unit;
		this.yzrem = yzrem;
		this.iid = iid;
		this.op = op;
		this.hpdate = hpdate;
		this.dcid = dcid;
		this.sbid = sbid;
		this.zcno = zcno;
	}
	
	@Override
	public String toString() {
		return "mesMrecorda [cid=" + cid + ", prd_no=" + prd_no + ", bat_no="
				+ bat_no + ", prd_name=" + prd_name + ", prd_mark=" + prd_mark
				+ ", qty=" + qty + ", aa=" + aa + ", unit=" + unit + ", yzrem="
				+ yzrem + ", iid=" + iid + ", op=" + op + ", hpdate=" + hpdate
				+ ", dcid=" + dcid + ", sbid=" + sbid + ", zcno=" + zcno + "]";
	}
	
	
	
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getPrd_no() {
		return prd_no;
	}
	public void setPrd_no(String prd_no) {
		this.prd_no = prd_no;
	}
	public String getBat_no() {
		return bat_no;
	}
	public void setBat_no(String bat_no) {
		this.bat_no = bat_no;
	}
	public String getPrd_name() {
		return prd_name;
	}
	public void setPrd_name(String prd_name) {
		this.prd_name = prd_name;
	}
	public String getPrd_mark() {
		return prd_mark;
	}
	public void setPrd_mark(String prd_mark) {
		this.prd_mark = prd_mark;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getAa() {
		return aa;
	}
	public void setAa(String aa) {
		this.aa = aa;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getYzrem() {
		return yzrem;
	}
	public void setYzrem(String yzrem) {
		this.yzrem = yzrem;
	}
	public String getIid() {
		return iid;
	}
	public void setIid(String iid) {
		this.iid = iid;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
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
	public String getSbid() {
		return sbid;
	}
	public void setSbid(String sbid) {
		this.sbid = sbid;
	}
	public String getZcno() {
		return zcno;
	}
	public void setZcno(String zcno) {
		this.zcno = zcno;
	}
	

}
