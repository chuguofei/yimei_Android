package com.yimei.adapter;

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yimei.activity.R;
import com.yimei.activity.RuKuActivity;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.scrollview.JiaJiaoCHScrollView;

public class RuKuAdapter extends BaseAdapter {

	public Context context;
	public List<Map<String, String>> listData;

	public RuKuAdapter(Context context, List<Map<String, String>> datas) {
		this.context = context;
		this.listData = datas;
	}

	@Override
	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		// 如果view未被实例化过，缓存池中没有对应的缓存
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.ruku_itemlist, null);
			RuKuActivity.addHViews((GeneralCHScrollView) convertView
					.findViewById(R.id.ruku_item_scroll));
			viewHolder.ruku_cid = (TextView) convertView
					.findViewById(R.id.ruku_cid);
			viewHolder.ruku_checkid = (TextView) convertView
					.findViewById(R.id.ruku_checkid);
			viewHolder.ruku_bat_no = (TextView) convertView
					.findViewById(R.id.ruku_bat_no);
			viewHolder.ruku_prd_no = (TextView) convertView
					.findViewById(R.id.ruku_prd_no);
			viewHolder.ruku_prd_mark = (TextView) convertView
					.findViewById(R.id.ruku_prd_mark);
			viewHolder.ruku_prd_name = (TextView) convertView
					.findViewById(R.id.ruku_prd_name);
			viewHolder.ruku_wh = (TextView) convertView
					.findViewById(R.id.ruku_wh);
			viewHolder.ruku_qty = (TextView) convertView
					.findViewById(R.id.ruku_qty);
			viewHolder.ruku_info = (TextView) convertView
					.findViewById(R.id.ruku_info);

			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> map = listData.get(position);
		viewHolder.ruku_cid.setText(map.get("ruku_cid"));
		viewHolder.ruku_checkid.setText(map.get("ruku_checkid"));
		viewHolder.ruku_bat_no.setText(map.get("ruku_bat_no"));
		viewHolder.ruku_prd_no.setText(map.get("ruku_prd_no"));
		viewHolder.ruku_prd_mark.setText(map.get("ruku_prd_mark"));
		viewHolder.ruku_prd_name.setText(map.get("ruku_prd_name"));
		viewHolder.ruku_wh.setText(map.get("ruku_wh"));
		viewHolder.ruku_qty.setText(map.get("ruku_qty"));
		viewHolder.ruku_info.setText(map.get("ruku_info"));
		return convertView;
	}

	class ViewHolder {
		public TextView ruku_cid;
		public TextView ruku_checkid;
		public TextView ruku_bat_no;
		public TextView ruku_prd_no;
		public TextView ruku_prd_mark;
		public TextView ruku_prd_name;
		public TextView ruku_wh;
		public TextView ruku_qty;
		public TextView ruku_info;
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
