package com.yimei.activity;

import com.yimei.util.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends Activity {

	private Button btn;
	public static LoginActivity loginActivity;
	static MyApplication myapp;
	private EditText login_edit_account;
	private EditText login_edit_pwd;
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Application application = getApplication();
		myapp = (MyApplication) application;
		myapp.removeALLActivity_();
		myapp.addActivity_(LoginActivity.this);
	    loginActivity = LoginActivity.this;
		if (!isWiFi(LoginActivity.this)) {
			// 进入手机设置界面
			// startActivity(new Intent(Settings.ACTION_SETTINGS)); //
			Toast.makeText(getApplicationContext(), "亲，请检查网络是否开启~", 0).show();
		}
		btn = (Button) findViewById(R.id.login_btn_login);
		btn.setOnLongClickListener(new Button.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				ToastUtil.showToastLocation(loginActivity,"进入更新系统窗口",0);
				showNormalDialog();
				return true;
			}
		});
		btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isWiFi(LoginActivity.this)) {
					// 进入手机设置界面
					// startActivity(new Intent(Settings.ACTION_SETTINGS)); //
					// 直接进入手机中设置界面
					Toast.makeText(getApplicationContext(), "亲，请检查网络是否开启~", 0).show();
				} else {
					if(login_edit_account.getText().toString().trim().equals("")||login_edit_account.getText().toString().trim()==null){
						Toast.makeText(getApplicationContext(), "用户名不能为空", 0)
						.show();
						return ;
					}
				    if(login_edit_pwd.getText().toString().trim().equals("")||login_edit_pwd.getText().toString().trim()==null){
						Toast.makeText(getApplicationContext(), "密码不能为空", 0)
						.show();
						return ;
					}
					
				    Intent intent = new Intent();
					intent.setClass(LoginActivity.this, LoadingActivity.class);// 跳转到加载界面
					
					//利用bundle来存取数据
	                Bundle bundle=new Bundle();
	                bundle.putString("user",login_edit_account.getText().toString());
	                bundle.putString("pwd",login_edit_pwd.getText().toString());
	                //再把bundle中的数据传给intent，以传输过去
	                intent.putExtras(bundle);
					startActivity(intent);					
				}
			}
		});

	}
	
	/**
	 * 弹出提示框
	 * 
	 * @param mes
	 */
	private void showNormalDialog() {
		LayoutInflater inflater = getLayoutInflater();
		 View  dialog = inflater.inflate(R.layout.activity_newversion_dig,(ViewGroup) findViewById(R.id.dialog));
		 final EditText editText = (EditText) dialog.findViewById(R.id.dig_et);

		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				LoginActivity.this);
		normalDialog.setTitle("版本更新");
		normalDialog.setView(dialog);
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(editText.getText().toString().equals("shineonadmin")){
							ToastUtil.showToastLocation(loginActivity,"下载新的版本",0);
							try {
								ProgressDialog progressDialog = new ProgressDialog(loginActivity);
								progressDialog.setTitle("提示");
								progressDialog.setMessage("正在下载...");
								progressDialog.setIndeterminate(false);
								progressDialog.setMax(100);
								progressDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
								progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // 进度条类型
								progressDialog.show();
								new DownloadAPK(progressDialog,LoginActivity.this).execute(MyApplication.MESDOWNLOADAPKURL);
							} catch (Exception e) {
								ToastUtil.showToastLocation(loginActivity,"服务器异常,请联系管理员!",0);
							}
						}else{
							ToastUtil.showToastLocation(loginActivity,"密码错误",0);
							showNormalDialog();
							return;
						}
					}
				});
		normalDialog.setNegativeButton("取消", null);
		// 显示
		normalDialog.show();
	}
	
	protected void onResume() {
		super.onResume();
		login_edit_account = (EditText) findViewById(R.id.login_edit_account);
		login_edit_pwd = (EditText) findViewById(R.id.login_edit_pwd);
		login_edit_account.setOnEditorActionListener(editEnter); //用户的回车监听事件
		login_edit_pwd.setOnEditorActionListener(editEnter); //密码回车监听事件
	};
	
	OnEditorActionListener editEnter = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (v.getId() == R.id.login_edit_account) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					EditText nextEditText = (EditText) findViewById(R.id.login_edit_pwd);
					nextEditText.setFocusable(true);
					nextEditText.setFocusableInTouchMode(true);  
					nextEditText.requestFocus();  
					nextEditText.findFocus();  
					return true;
				}
			}
			return false;
		}
	};

	// 判断WiFi是否打开
	public boolean isWiFi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null
				&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
}
