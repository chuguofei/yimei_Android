package com.yimei.entity;

import java.io.Serializable;

public class mesGrecord implements Serializable{

	private String sid;
	private String zcno;
	private String op;
	private String sbid;
	private String prtno;
	private String slkid;
	private String sbuid;
	private String prd_no;
	private String qty;
	private String state;
	private String indate;
	private String dcid;
	private String mkdate;
	private String remark;
	private String edate;
	private String prd_name;
	
	
	
	public mesGrecord(String sid, String zcno, String op, String sbid,
			String prtno, String slkid, String sbuid, String prd_no,
			String qty, String state, String indate, String dcid,
			String mkdate, String remark, String edate, String prd_name) {
		super();
		this.sid = sid;
		this.zcno = zcno;
		this.op = op;
		this.sbid = sbid;
		this.prtno = prtno;
		this.slkid = slkid;
		this.sbuid = sbuid;
		this.prd_no = prd_no;
		this.qty = qty;
		this.state = state;
		this.indate = indate;
		this.dcid = dcid;
		this.mkdate = mkdate;
		this.remark = remark;
		this.edate = edate;
		this.prd_name = prd_name;
	}
	@Override
	public String toString() {
		return "mesGrecord [sid=" + sid + ", zcno=" + zcno + ", op=" + op
				+ ", sbid=" + sbid + ", prtno=" + prtno + ", slkid=" + slkid
				+ ", sbuid=" + sbuid + ", prd_no=" + prd_no + ", qty=" + qty
				+ ", state=" + state + ", indate=" + indate + ", dcid=" + dcid
				+ ", mkdate=" + mkdate + ", remark=" + remark + ", edate="
				+ edate + ", prd_name=" + prd_name + "]";
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
	public String getPrtno() {
		return prtno;
	}
	public void setPrtno(String prtno) {
		this.prtno = prtno;
	}
	public String getSlkid() {
		return slkid;
	}
	public void setSlkid(String slkid) {
		this.slkid = slkid;
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
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getIndate() {
		return indate;
	}
	public void setIndate(String indate) {
		this.indate = indate;
	}
	public String getDcid() {
		return dcid;
	}
	public void setDcid(String dcid) {
		this.dcid = dcid;
	}
	public String getMkdate() {
		return mkdate;
	}
	public void setMkdate(String mkdate) {
		this.mkdate = mkdate;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getEdate() {
		return edate;
	}
	public void setEdate(String edate) {
		this.edate = edate;
	}
	public String getPrd_name() {
		return prd_name;
	}
	public void setPrd_name(String prd_name) {
		this.prd_name = prd_name;
	}


}
