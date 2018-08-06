package com.yimei.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.yimei.activity.DownloadAPK;
import com.yimei.activity.LoginActivity;
import com.yimei.activity.MyApplication;
import com.yimei.activity.R;
import com.yimei.activity.ipqc.IPQC_shoujian;
import com.yimei.adapter.IPQC_ShouJian_Adapter.ViewHolder;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.shebei.WeiXiuOkActivity;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class WeiXiuOkAdapter extends BaseAdapter {

	Context context;
	public List<Map<String, String>> listData;
	// 记录checkbox的状态
	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	// 构造函数
	public WeiXiuOkAdapter(Context context, List<Map<String, String>> datas) {
		this.context = context;
		this.listData = datas;
		initCheck(false);
	}
	
	public HashMap<Integer, Boolean> Getstate(){
		if(state.size()==0||state==null){
			return null;
		}
		return state;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return listData.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listData.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public int initCheck(boolean flag) {
		// map集合的数量和list的数量是一致的
		if(listData==null||listData.size()==0){
			return -1;
		}else{
			for (int i = 0; i < listData.size(); i++) {
				// 设置默认的显示
				state.put(i, flag);
			}
		}
		return 1;
	}
	
	
	
	@Override
	@SuppressLint("ViewHolder")
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		// 如果view未被实例化过，缓存池中没有对应的缓存
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.weixiuok_itemlist, null);
						
			WeiXiuOkActivity.addHViews((GeneralCHScrollView) convertView.findViewById(R.id.weixiuok_item_scroll));
			viewHolder.sbid = (TextView) convertView.findViewById(R.id.yimei_weixiuok_sbid);
			viewHolder.sopr = (TextView) convertView.findViewById(R.id.yimei_weixiuok_sopr);
			viewHolder.mingxi = (Button) convertView.findViewById(R.id.yimei_weixiuok_xiangxi);
			viewHolder.state = (TextView) convertView.findViewById(R.id.yimei_weixiuok_state);
			CheckBox check = (CheckBox) convertView.findViewById(R.id.yimei_weixiuok_quanxuan); //单选框
			check.setChecked(state.get(position));
			check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					state.put(position,isChecked);
				}
			});
			
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final Map<String, String> map = listData.get(position);
		viewHolder.sbid.setText((String) map.get("sbid"));
		viewHolder.sopr.setText((String) map.get("sopr"));
		viewHolder.state.setText((String) map.get("state"));
		viewHolder.mingxi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				ToastUtil.showToast(context,map.get("checkMap").toString(),0);
				showNormalDialog(map,position);
			}
		});
		return convertView;
	}

	/**
	 * 弹出提示框
	 * 
	 * @param mes
	 */
	private void showNormalDialog(Map<String,String> map,final int position) {
		LayoutInflater inflater = WeiXiuOkActivity.ip.getLayoutInflater();
		final View dialog = inflater.inflate(R.layout.activity_weixiuok_dig,
				(ViewGroup) WeiXiuOkActivity.ip.findViewById(R.id.weixiuok_dialog));
		JSONObject json = JSONObject.parseObject(map.get("checkMap"));
		((EditText) dialog.findViewById(R.id.weixiuok_sid)).setText(json.getString("sid"));
		((EditText) dialog.findViewById(R.id.weixiuok_sopr)).setText(json.getString("sopr"));
		((EditText) dialog.findViewById(R.id.weixiuok_sbid)).setText(json.getString("sbid"));
		((EditText) dialog.findViewById(R.id.weixiuok_zcno)).setText(json.getString("zcno"));
		((EditText) dialog.findViewById(R.id.weixiuok_reason)).setText(json.getString("reason"));
		((EditText) dialog.findViewById(R.id.weixiuok_madate)).setText(json.getString("mkdate"));
		((EditText) dialog.findViewById(R.id.weixiuok_wxdate)).setText(json.getString("state"));
		
		((EditText)dialog.findViewById(R.id.weixiuok_sid)).setKeyListener(null);
		((EditText)dialog.findViewById(R.id.weixiuok_sopr)).setKeyListener(null);
		((EditText)dialog.findViewById(R.id.weixiuok_sbid)).setKeyListener(null);
		((EditText)dialog.findViewById(R.id.weixiuok_zcno)).setKeyListener(null);
		((EditText)dialog.findViewById(R.id.weixiuok_reason)).setKeyListener(null);
		((EditText)dialog.findViewById(R.id.weixiuok_madate)).setKeyListener(null);
		((EditText)dialog.findViewById(R.id.weixiuok_wxdate)).setKeyListener(null);
		
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				WeiXiuOkActivity.ip);
		normalDialog.setTitle("维修明细");
		normalDialog.setView(dialog);
		normalDialog.setCancelable(false); // 设置不可点击界面之外的区域让对话框消失
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
		// 显示
		normalDialog.show();
	}
	
	class ViewHolder {
		public TextView sbid;
		public TextView sopr;
		public Button mingxi;
		public TextView state;
	}
}
