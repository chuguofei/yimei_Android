package com.yimei.adapter;

import java.util.List;
import java.util.Map;

import com.yimei.activity.DownloadAPK;
import com.yimei.activity.LoginActivity;
import com.yimei.activity.MyApplication;
import com.yimei.activity.R;
import com.yimei.activity.ipqc.IPQC_xunjian;
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

public class IPQC_XunJian_Adapter extends BaseAdapter {

	Context context;
	public static List<Map<String, String>> listData;

	// 构造函数
	public IPQC_XunJian_Adapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.ipqc_xunjian_itemlist,null);
			IPQC_xunjian.addHViews((GeneralCHScrollView) convertView.findViewById(R.id.xunjian_item_scroll));
			viewHolder.yimei_xunjian_cid = (TextView) convertView
					.findViewById(R.id.yimei_xunjian_cid);
			viewHolder.yimei_xunjian_xmbm = (TextView) convertView
					.findViewById(R.id.yimei_xunjian_xmbm);
			viewHolder.yimei_xunjian_xmmc = (TextView) convertView
					.findViewById(R.id.yimei_xunjian_xmmc);
			viewHolder.yimei_xunjian_bok = (Button) convertView
					.findViewById(R.id.yimei_xunjian_bok);
			viewHolder.yimei_xunjian_remark = (EditText) convertView
					.findViewById(R.id.yimei_xunjian_remark);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final Map<String, String> map = listData.get(position);
		viewHolder.yimei_xunjian_cid.setText((String) map.get("cid"));
		viewHolder.yimei_xunjian_xmbm.setText((String) map.get("xmbm"));
		viewHolder.yimei_xunjian_xmmc.setText((String) map.get("xmmc"));
		viewHolder.yimei_xunjian_bok.setText((String) map.get("bok"));
		viewHolder.yimei_xunjian_bok.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (viewHolder.yimei_xunjian_bok.getText().equals("OK")) {
							viewHolder.yimei_xunjian_bok.setText("NG");
						} else {
							viewHolder.yimei_xunjian_bok.setText("OK");
						}
					Map<String, String> map2 = listData.get(position);
					map2.put("bok", viewHolder.yimei_xunjian_bok.getText().toString());
				}
		});
		viewHolder.yimei_xunjian_remark.setText((String) map.get("remark"));
		viewHolder.yimei_xunjian_remark.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start,int before, int count) {}
					@Override
					public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
					@Override
					public void afterTextChanged(Editable s) {
						Map<String, String> map2 = listData.get(position);
						map2.put("remark", viewHolder.yimei_xunjian_remark
								.getText().toString());
					}
				});
		return convertView;
	}

	class ViewHolder {
		public TextView yimei_xunjian_cid;
		public TextView yimei_xunjian_xmbm;
		public TextView yimei_xunjian_xmmc;
		public Button yimei_xunjian_bok;
		public EditText yimei_xunjian_remark;
	}
}
