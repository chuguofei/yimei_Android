package com.yimei.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class HttpUtil {

	public static String httpPost(String baseUrl, Map<String, String> map) {
		String responseJson = null;
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		try {
			URL url = new URL(baseUrl);

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(8000);
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			StringBuffer tempParams = new StringBuffer();
			int pos = 0;
			for (String key : map.keySet()) {
				if (pos > 0) {
					tempParams.append("&");
				}
				tempParams.append(String.format("%s=%s", key,
						URLEncoder.encode(map.get(key), "utf-8")));
				pos++;
			}
			out.writeBytes(tempParams.toString());
			InputStream in = connection.getInputStream();
			// 下面对获取到的输入流进行读取
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			Log.i("response",response.toString());
			responseJson = response.toString();
		} catch (Exception e) {
//			e.printStackTrace();
			responseJson = "-1";
			Log.i("httpPost",responseJson);
//			throw new RuntimeException("服务器连接失败");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					responseJson = "-1";
					e.printStackTrace();
				}
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
		return responseJson;
	}
}
