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
import com.yimei.activity.ZhuangXiangActivity;
import com.yimei.scrollview.GeneralCHScrollView;

public class ZhuangXiangAdapter extends BaseAdapter {

	public Context context;
	public List<Map<String, String>> listData;

	public ZhuangXiangAdapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.zhuangxiang_itemlist, null);
			ZhuangXiangActivity.addHViews((GeneralCHScrollView) convertView
					.findViewById(R.id.zhuangxiang_item_scroll));
			viewHolder.zhuangxiang_cid = (TextView) convertView
					.findViewById(R.id.zhuangxiang_cid);
			viewHolder.zhuangxiang_bat_no = (TextView) convertView
					.findViewById(R.id.zhuangxiang_bat_no);
			viewHolder.zhuangxiang_bincode = (TextView) convertView
					.findViewById(R.id.zhuangxiang_bincode);
			viewHolder.zhuangxiang_qty = (TextView) convertView
					.findViewById(R.id.zhuangxiang_qty);

			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> map = listData.get(position);
		viewHolder.zhuangxiang_cid.setText(map.get("zhuangxiang_cid"));
		viewHolder.zhuangxiang_bat_no.setText(map.get("zhuangxiang_bat_no"));
		viewHolder.zhuangxiang_bincode.setText(map.get("zhuangxiang_bincode"));
		viewHolder.zhuangxiang_qty.setText(map.get("zhuangxiang_qty"));
		return convertView;
	}

	class ViewHolder {
		public TextView zhuangxiang_cid;
		public TextView zhuangxiang_bat_no;
		public TextView zhuangxiang_bincode;
		public TextView zhuangxiang_qty;
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
