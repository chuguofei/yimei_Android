package com.yimei.util;

import java.io.File;
import java.io.FileInputStream;

import android.util.Log;

public class GetAndroidMacUtil {

	/**
	 * 得到终端的Mac地址
	 * @return
	 */
	public static String getMac(){
		String Mac = null;
		try {

			String path = "sys/class/net/wlan0/address";
			if ((new File(path)).exists()) {
				FileInputStream fis = new FileInputStream(path);
				byte[] buffer = new byte[8192];
				int byteCount = fis.read(buffer);
				if (byteCount > 0) {
					Mac = new String(buffer, 0, byteCount, "utf-8");
				}
			}
			if (Mac == null || Mac.length() == 0) {
				path = "sys/class/net/eth0/address";
				FileInputStream fis_name = new FileInputStream(path);
				byte[] buffer_name = new byte[8192];
				int byteCount_name = fis_name.read(buffer_name);
				if (byteCount_name > 0) {
					Mac = new String(buffer_name, 0, byteCount_name, "utf-8");
				}
			}
			if (Mac.length() == 0 || Mac == null) {
				return "";
			}
		} catch (Exception io) {
			Log.v("daming.zou**exception*", "" + io.toString());
		}

		Log.v("xulongheng*Mac", Mac);
		return Mac.trim();
	}
}
