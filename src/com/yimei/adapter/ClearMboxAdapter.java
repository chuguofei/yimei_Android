package com.yimei.adapter;

import java.util.List;
import java.util.Map;

import com.yimei.activity.ClearMboxActivity;
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

public class ClearMboxAdapter extends BaseAdapter {

	public Context context;
	public List<Map<String, String>> listData;

	public ClearMboxAdapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.clearmbox_itemlist, null);
			ClearMboxActivity.addHViews((GeneralCHScrollView) convertView
					.findViewById(R.id.clearmbox_item_scroll));
			viewHolder.clearmbox_op = (TextView) convertView
					.findViewById(R.id.clearmbox_op);
			viewHolder.clearmbox_mbox = (TextView) convertView
					.findViewById(R.id.clearmbox_mbox);
			viewHolder.clearmbox_rem = (TextView) convertView
					.findViewById(R.id.clearmbox_rem);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> map = listData.get(position);
		viewHolder.clearmbox_op.setText(map.get("op"));
		viewHolder.clearmbox_mbox.setText(map.get("mbox"));
		viewHolder.clearmbox_rem.setText(map.get("rem"));
		return convertView;
	}

	class ViewHolder {
		public TextView clearmbox_op;
		public TextView clearmbox_mbox;
		public TextView clearmbox_rem;
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
