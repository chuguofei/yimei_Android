package com.yimei.entity;

import java.io.Serializable;

public class Pair implements Serializable {

	public String key;
    public String value;
    
    
    
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
	public Pair(String key, String value) {
            this.key = key;
            this.value = value;
    }
    public String toString() {
            return key;
    }
}
