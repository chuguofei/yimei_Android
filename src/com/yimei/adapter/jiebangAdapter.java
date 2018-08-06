package com.yimei.adapter;

import java.util.List;
import java.util.Map;

import com.yimei.activity.JiaJiaoActivity;
import com.yimei.activity.JieBangMboxActivity;
import com.yimei.activity.R;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.scrollview.JiaJiaoCHScrollView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class jiebangAdapter extends BaseAdapter {

	public Context context;
	public List<Map<String, String>> listData;

	public jiebangAdapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.mbox_itemlist, null);
			JieBangMboxActivity.addHViews((GeneralCHScrollView) convertView
					.findViewById(R.id.mbox_item_scroll));
			viewHolder.yimei_mbox_op = (TextView) convertView
					.findViewById(R.id.yimei_mbox_op);
			viewHolder.yimei_mbox_sid1 = (TextView) convertView
					.findViewById(R.id.yimei_mbox_sid1);
			viewHolder.yimei_mbox_mbox = (TextView) convertView
					.findViewById(R.id.yimei_mbox_mbox);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> map = listData.get(position);
		viewHolder.yimei_mbox_op.setText(map.get("op"));
		viewHolder.yimei_mbox_sid1.setText(map.get("sid1"));
		viewHolder.yimei_mbox_mbox.setText(map.get("mbox"));
		return convertView;
	}

	class ViewHolder {
		public TextView yimei_mbox_op;
		public TextView yimei_mbox_sid1;
		public TextView yimei_mbox_mbox;
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
