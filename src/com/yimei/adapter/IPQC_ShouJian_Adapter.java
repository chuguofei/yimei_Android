package com.yimei.adapter;

import java.util.List;
import java.util.Map;

import com.yimei.activity.DownloadAPK;
import com.yimei.activity.LoginActivity;
import com.yimei.activity.MyApplication;
import com.yimei.activity.R;
import com.yimei.activity.ipqc.IPQC_shoujian;
import com.yimei.scrollview.GeneralCHScrollView;
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
import android.widget.EditText;
import android.widget.TextView;

public class IPQC_ShouJian_Adapter extends BaseAdapter {

	Context context;
	public static List<Map<String, String>> listData;
	// 构造函数
	public IPQC_ShouJian_Adapter(Context context, List<Map<String, String>> datas) {
		this.context = context;
		this.listData = datas;
	}
	
	public int getCount() {
		return listData.size();
	}

	public Object getItem(int position) {
		return listData.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	
	
	@Override
	@SuppressLint("ViewHolder")
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		// 如果view未被实例化过，缓存池中没有对应的缓存
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.ipqc_shoujian_itemlist, null);
						
			IPQC_shoujian.addHViews((GeneralCHScrollView) convertView.findViewById(R.id.shoujian_item_scroll));
			viewHolder.yimei_shoujian_cid = (TextView) convertView.findViewById(R.id.yimei_shoujian_cid);
			viewHolder.yimei_shoujian_xmbm = (TextView) convertView.findViewById(R.id.yimei_shoujian_xmbm);
			viewHolder.yimei_shoujian_xmmc = (TextView) convertView.findViewById(R.id.yimei_shoujian_xmmc);
			/*viewHolder.yimei_shoujian_standard = (EditText) convertView.findViewById(R.id.yimei_shoujian_standard);
			viewHolder.yimei_shoujian_value1 = (EditText) convertView.findViewById(R.id.yimei_shoujian_value1);
			viewHolder.yimei_shoujian_value2 = (EditText) convertView.findViewById(R.id.yimei_shoujian_value2);
			viewHolder.yimei_shoujian_value3 = (EditText) convertView.findViewById(R.id.yimei_shoujian_value3);
			viewHolder.yimei_shoujian_value4 = (EditText) convertView.findViewById(R.id.yimei_shoujian_value4);
			viewHolder.yimei_shoujian_value5 = (EditText) convertView.findViewById(R.id.yimei_shoujian_value5);*/
			viewHolder.yimei_shoujian_bok = (Button) convertView.findViewById(R.id.yimei_shoujian_bok);
			viewHolder.yimei_shoujian_chakan = (Button) convertView.findViewById(R.id.yimei_shoujian_chakan);
//			viewHolder.yimei_shoujian_remark = (EditText) convertView.findViewById(R.id.yimei_shoujian_remark);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final Map<String, String> map = listData.get(position);
		viewHolder.yimei_shoujian_cid.setText((String) map.get("cid"));
		viewHolder.yimei_shoujian_xmbm.setText((String) map.get("xmbm"));
		viewHolder.yimei_shoujian_xmmc.setText((String) map.get("xmmc"));
		viewHolder.yimei_shoujian_chakan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				ToastUtil.showToast(IPQC_shoujian.ip,map.get("cid"), 0);
				showNormalDialog(map,position);
			}
		});
		/*viewHolder.yimei_shoujian_standard.setText((String) map.get("standard"));
		viewHolder.yimei_shoujian_standard.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				Map<String, String> map2 = listData.get(position);
				map2.put("standard",viewHolder.yimei_shoujian_standard.getText().toString());
			}
		});
		viewHolder.yimei_shoujian_value1.setText((String) map.get("val1"));
		viewHolder.yimei_shoujian_value2.setText((String) map.get("val2"));
		viewHolder.yimei_shoujian_value3.setText((String) map.get("val3"));
		viewHolder.yimei_shoujian_value4.setText((String) map.get("val4"));
		viewHolder.yimei_shoujian_value5.setText((String) map.get("val5"));*/
		viewHolder.yimei_shoujian_bok.setText((String) map.get("bok"));
		viewHolder.yimei_shoujian_bok.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(viewHolder.yimei_shoujian_bok.getText().equals("OK")){
					viewHolder.yimei_shoujian_bok.setText("NG");
				}else{
					viewHolder.yimei_shoujian_bok.setText("OK");
				}
				Map<String, String> map2 = listData.get(position);
				map2.put("bok",viewHolder.yimei_shoujian_bok.getText().toString());
			}
		});
//		viewHolder.yimei_shoujian_remark.setText((String) map.get("remark"));
		return convertView;
	}

	/**
	 * 弹出提示框
	 * 
	 * @param mes
	 */
	private void showNormalDialog(Map<String,String> map,final int position) {
		LayoutInflater inflater = IPQC_shoujian.ip.getLayoutInflater();
		final View dialog = inflater.inflate(R.layout.activity_shoujian_dig,
				(ViewGroup) IPQC_shoujian.ip.findViewById(R.id.shoujian_dialog));
		((EditText) dialog.findViewById(R.id.shoujian_name)).setText(map.get("xmmc")+"（"+map.get("xmbm")+"）");
		((EditText)dialog.findViewById(R.id.shoujian_name)).setKeyListener(null);
		
		((EditText) dialog.findViewById(R.id.standard)).setText(map.get("standard"));
		((EditText) dialog.findViewById(R.id.value1)).setText(map.get("value1"));
		((EditText) dialog.findViewById(R.id.value2)).setText(map.get("value2"));
		((EditText) dialog.findViewById(R.id.value3)).setText(map.get("value3"));
		((EditText) dialog.findViewById(R.id.value4)).setText(map.get("value4"));
		((EditText) dialog.findViewById(R.id.value5)).setText(map.get("value5"));
		((EditText) dialog.findViewById(R.id.value6)).setText(map.get("value6"));
		((EditText) dialog.findViewById(R.id.remark)).setText(map.get("remark"));
		
		
		((EditText) dialog.findViewById(R.id.standard)).addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				Map<String, String> map2 = listData.get(position);
				map2.put("standard",((EditText) dialog.findViewById(R.id.standard)).getText().toString());
			}
		});
			((EditText) dialog.findViewById(R.id.value1)).addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				Map<String, String> map2 = listData.get(position);
				map2.put("value1",((EditText) dialog.findViewById(R.id.value1)).getText().toString());
			}
		});
		((EditText) dialog.findViewById(R.id.value2)).addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				Map<String, String> map2 = listData.get(position);
				map2.put("value2",((EditText) dialog.findViewById(R.id.value2)).getText().toString());
			}
		});
		((EditText) dialog.findViewById(R.id.value3)).addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				Map<String, String> map2 = listData.get(position);
				map2.put("value3",((EditText) dialog.findViewById(R.id.value3)).getText().toString());
			}
		});
		((EditText) dialog.findViewById(R.id.value4)).addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {}
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
					@Override
					public void afterTextChanged(Editable s) {
						Map<String, String> map2 = listData.get(position);
						map2.put("value4",((EditText) dialog.findViewById(R.id.value4)).getText().toString());
					}
		});
		((EditText) dialog.findViewById(R.id.value5)).addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				Map<String, String> map2 = listData.get(position);
				map2.put("value5",((EditText) dialog.findViewById(R.id.value5)).getText().toString());
			}
		});
		((EditText) dialog.findViewById(R.id.value6)).addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				Map<String, String> map2 = listData.get(position);
				map2.put("value6",((EditText) dialog.findViewById(R.id.value6)).getText().toString());
			}
		});
		((EditText) dialog.findViewById(R.id.remark)).addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				Map<String, String> map2 = listData.get(position);
				map2.put("remark",((EditText) dialog.findViewById(R.id.remark)).getText().toString());
			}
		});
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				IPQC_shoujian.ip);
		normalDialog.setTitle("首检记录");
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
		public TextView yimei_shoujian_cid;
		public TextView yimei_shoujian_xmbm;
		public TextView yimei_shoujian_xmmc;
		public EditText yimei_shoujian_standard;
		public EditText yimei_shoujian_value1;
		public EditText yimei_shoujian_value2;
		public EditText yimei_shoujian_value3;
		public EditText yimei_shoujian_value4;
		public EditText yimei_shoujian_value5;
		public Button yimei_shoujian_bok;
		public EditText yimei_shoujian_remark;
		public Button yimei_shoujian_chakan;
	}
}
