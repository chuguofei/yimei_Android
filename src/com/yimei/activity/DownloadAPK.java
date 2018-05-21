package com.yimei.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

public class DownloadAPK extends AsyncTask<String, Integer, String> {
	

	ProgressDialog progressDialog;
	File file;
	Context mContext;

	public DownloadAPK(ProgressDialog progressDialog,Context context) {
		this.progressDialog = progressDialog;
		mContext = context;
	}

	@Override
	protected String doInBackground(String... params) {
		// 根据url获取网络数据生成apk文件
		URL url;
		HttpURLConnection conn;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;

		try {
			url = new URL(params[0]);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);

			int fileLength = conn.getContentLength();
			bis = new BufferedInputStream(conn.getInputStream());
			//文件路径
			String fileName = Environment.getExternalStorageDirectory()
					.getPath() + "/yimeiDown/"+java.lang.System.currentTimeMillis()+"yimei.apk";
			file = new File(fileName);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			byte data[] = new byte[4 * 1024];
			long total = 0;
			int count;
			while ((count = bis.read(data)) != -1) {
				total += count;
				publishProgress((int) (total * 100 / fileLength));
				fos.write(data, 0, count);
				fos.flush();
			}
			fos.flush();

		} catch (IOException e) {
			
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
	}

	@Override
	protected void onPostExecute(String s) {
		// 到这里说明下载完成，判断文件是否存在，如果存在，执行安装apk的操作
		  super.onPostExecute(s);
          openFile(file);                 //打开安装apk文件操作
          progressDialog.dismiss();
	}

	private void openFile(File file) {
		if (file != null) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
			mContext.startActivity(intent);
		}
	}
}
