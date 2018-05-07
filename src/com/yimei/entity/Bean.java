
package com.yimei.entity;

public class Bean {
	
	private String pihao;
	private String gongdan;
	private String jizhong;
	private String shuliang;
	private String info;
	
	public Bean(String pihao, String gongdan, String jizhong, String shuliang,String info) {
		this.pihao = pihao;
		this.gongdan = gongdan;
		this.jizhong = jizhong;
		this.shuliang = shuliang;
		this.info = info;
	}
	
	public String getPihao() {
		return pihao;
	}
	public void setPihao(String pihao) {
		this.pihao = pihao;
	}
	public String getGongdan() {
		return gongdan;
	}
	public void setGongdan(String gongdan) {
		this.gongdan = gongdan;
	}
	public String getJizhong() {
		return jizhong;
	}
	public void setJizhong(String jizhong) {
		this.jizhong = jizhong;
	}
	public String getShuliang() {
		return shuliang;
	}
	public void setShuliang(String shuliang) {
		this.shuliang = shuliang;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
}