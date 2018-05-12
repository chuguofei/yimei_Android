package com.yimei.adapter;

import java.util.List;
import java.util.Map;

import com.yimei.activity.HunJiaoActivity;
import com.yimei.activity.JiaJiaoActivity;
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

public class HunJiaoAdapter extends BaseAdapter {

	public Context context;
	public List<Map<String, String>> listData;

	public HunJiaoAdapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.hunjiao_itemlist, null);
			HunJiaoActivity.addHViews((GeneralCHScrollView) convertView
					.findViewById(R.id.hunjiao_item_scroll));
			viewHolder.yimei_hunjiao_prtno = (TextView) convertView
					.findViewById(R.id.hunjiao_prtno);
			viewHolder.yimei_hunjiao_indate = (TextView) convertView
					.findViewById(R.id.hunjiao_indate);
			viewHolder.yimei_hunjiao_newly_time = (TextView) convertView
					.findViewById(R.id.hunjiao_newly_time);
			
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> map = listData.get(position);
		viewHolder.yimei_hunjiao_prtno.setText(map.get("prtno"));
		viewHolder.yimei_hunjiao_indate.setText(map.get("indate"));
		viewHolder.yimei_hunjiao_newly_time.setText(map.get("newly_time"));
		return convertView;
	}

	class ViewHolder {
		public TextView yimei_hunjiao_prtno;
		public TextView yimei_hunjiao_indate;
		public TextView yimei_hunjiao_newly_time;
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
