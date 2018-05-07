package com.yimei.entity;

import java.io.Serializable;

public class mesPackinga implements Serializable {

	private String cid;
	private String bat_no;
	private String prd_no;
	private String prd_mark;
	private String prd_name;
	private String unit;
	private String wh;
	private String qty;
	private String rem;
	private String mm_dd;
	private String sc_dd;
	
	
	
	@Override
	public String toString() {
		return "mesPackinga [cid=" + cid + ", bat_no=" + bat_no + ", prd_no="
				+ prd_no + ", prd_mark=" + prd_mark + ", prd_name=" + prd_name
				+ ", unit=" + unit + ", wh=" + wh + ", qty=" + qty + ", rem="
				+ rem + ", mm_dd=" + mm_dd + ", sc_dd=" + sc_dd + "]";
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getBat_no() {
		return bat_no;
	}
	public void setBat_no(String bat_no) {
		this.bat_no = bat_no;
	}
	public String getPrd_no() {
		return prd_no;
	}
	public void setPrd_no(String prd_no) {
		this.prd_no = prd_no;
	}
	public String getPrd_mark() {
		return prd_mark;
	}
	public void setPrd_mark(String prd_mark) {
		this.prd_mark = prd_mark;
	}
	public String getPrd_name() {
		return prd_name;
	}
	public void setPrd_name(String prd_name) {
		this.prd_name = prd_name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getWh() {
		return wh;
	}
	public void setWh(String wh) {
		this.wh = wh;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getRem() {
		return rem;
	}
	public void setRem(String rem) {
		this.rem = rem;
	}
	public String getMm_dd() {
		return mm_dd;
	}
	public void setMm_dd(String mm_dd) {
		this.mm_dd = mm_dd;
	}
	public String getSc_dd() {
		return sc_dd;
	}
	public void setSc_dd(String sc_dd) {
		this.sc_dd = sc_dd;
	}
	
	
}
