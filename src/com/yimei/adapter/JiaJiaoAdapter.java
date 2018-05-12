package com.yimei.adapter;

import java.util.List;
import java.util.Map;
import com.yimei.activity.JiaJiaoActivity;
import com.yimei.activity.R;
import com.yimei.scrollview.JiaJiaoCHScrollView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class JiaJiaoAdapter extends BaseAdapter {

	public Context context;
	public List<Map<String, String>> listData;

	public JiaJiaoAdapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.jiajiao_itemlist, null);
			JiaJiaoActivity.addHViews((JiaJiaoCHScrollView) convertView
					.findViewById(R.id.jiajiao_item_scroll));
			viewHolder.yimei_jiajiao_prtno = (TextView) convertView
					.findViewById(R.id.yimei_jiajiao_prtno);
			viewHolder.yimei_jiajiao_prd_no = (TextView) convertView
					.findViewById(R.id.yimei_jiajiao_prd_no);
			viewHolder.yimei_jiajiao_qty = (TextView) convertView
					.findViewById(R.id.yimei_jiajiao_qty);
			viewHolder.yimei_jiajiao_indate = (TextView) convertView
					.findViewById(R.id.yimei_jiajiao_indate);
			viewHolder.yimei_jiajiao_mkdate = (TextView) convertView
					.findViewById(R.id.yimei_jiajiao_mkdate);
			viewHolder.yimei_jiajiao_edate = (TextView) convertView
					.findViewById(R.id.yimei_jiajiao_edate);
			viewHolder.yimei_jiajiao_prd_name = (TextView) convertView
					.findViewById(R.id.yimei_jiajiao_prd_name);
			
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> map = listData.get(position);
		viewHolder.yimei_jiajiao_prtno.setText(map.get("prtno"));
		viewHolder.yimei_jiajiao_prd_no.setText(map.get("prd_no"));
		viewHolder.yimei_jiajiao_qty.setText(map.get("qty"));
		viewHolder.yimei_jiajiao_indate.setText(map.get("indate"));
		viewHolder.yimei_jiajiao_mkdate.setText(map.get("mkdate"));
		viewHolder.yimei_jiajiao_edate.setText(map.get("edate"));
		viewHolder.yimei_jiajiao_prd_name.setText(map.get("prd_name"));
		return convertView;
	}

	class ViewHolder {
		public TextView yimei_jiajiao_prtno;
		public TextView yimei_jiajiao_prd_no;
		public TextView yimei_jiajiao_qty;
		public TextView yimei_jiajiao_indate;
		public TextView yimei_jiajiao_mkdate;
		public TextView yimei_jiajiao_edate;
		public TextView yimei_jiajiao_prd_name;
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
