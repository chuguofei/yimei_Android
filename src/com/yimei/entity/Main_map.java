package com.yimei.entity;

import java.io.Serializable;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;

public class Main_map implements Serializable{

	private String key;
	private String value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	private Map<String,JSONObject> mainJSON;
	public Map<String, JSONObject> getMainJSON() {
		
		return mainJSON;
	}
	public void setMainJSON(Map<String, JSONObject> mainJSON) {
		this.mainJSON = mainJSON;
	}
}
