package com.yimei.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yimei.entity.Main_map;

public class SerMap  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public List<Main_map> map;
	
	public List<Main_map> getMap() {
		return map;
	}
	public void setMap(List<Main_map> map) {
		this.map = map;
	}
    
}