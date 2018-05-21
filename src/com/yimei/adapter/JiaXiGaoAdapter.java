package com.yimei.adapter;

import java.util.List;
import java.util.Map;

import com.yimei.activity.JiaXiGaoActivity;
import com.yimei.activity.R;
import com.yimei.activity.ZhiJuQingXiActivity;
import com.yimei.scrollview.GeneralCHScrollView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class JiaXiGaoAdapter extends BaseAdapter {

	Context context;
	public static List<Map<String, String>> listData;
	// 构造函数
	public JiaXiGaoAdapter(Context context, List<Map<String, String>> datas) {
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
		ViewHolder viewHolder;
		// 如果view未被实例化过，缓存池中没有对应的缓存
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.jiaxigao_itemlist, null);
						
			JiaXiGaoActivity.addHViews((GeneralCHScrollView) convertView
					.findViewById(R.id.jiaxigao_item_scroll));
			viewHolder.yimei_jiaxigao_op = (TextView) convertView
					.findViewById(R.id.yimei_jiaxigao_op);
			viewHolder.yimei_jiaxigao_prtno = (TextView) convertView
					.findViewById(R.id.yimei_jiaxigao_prtno);
			viewHolder.yimei_jiaxigao_sbid = (TextView) convertView
					.findViewById(R.id.yimei_jiaxigao_sbid);
			viewHolder.yimei_jiaxigao_slkid = (TextView) convertView
					.findViewById(R.id.yimei_jiaxigao_slkid);
			viewHolder.yimei_jiaxigao_prdno = (TextView) convertView
					.findViewById(R.id.yimei_jiaxigao_prdno);
			viewHolder.yimei_jiaxigao_qty = (TextView) convertView
					.findViewById(R.id.yimei_jiaxigao_qty);
			viewHolder.yimei_jiaxigao_indate = (TextView) convertView
					.findViewById(R.id.yimei_jiaxigao_indate);
			viewHolder.yimei_jiaxigao_mkdate = (TextView) convertView
					.findViewById(R.id.yimei_jiaxigao_mkdate);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Map<String, String> map = listData.get(position);
		viewHolder.yimei_jiaxigao_op.setText(map.get("op"));
		viewHolder.yimei_jiaxigao_prtno.setText(map.get("prtno"));
		viewHolder.yimei_jiaxigao_sbid.setText(map.get("sbid"));
		viewHolder.yimei_jiaxigao_slkid.setText(map.get("slkid"));
		viewHolder.yimei_jiaxigao_prdno.setText(map.get("prdno"));
		viewHolder.yimei_jiaxigao_qty.setText(map.get("qty"));
		viewHolder.yimei_jiaxigao_indate.setText(map.get("indate"));
		viewHolder.yimei_jiaxigao_mkdate.setText(map.get("mkdate"));
		return convertView;
	}

	class ViewHolder {
		public TextView yimei_jiaxigao_op;
		public TextView yimei_jiaxigao_prtno;
		public TextView yimei_jiaxigao_sbid;
		public TextView yimei_jiaxigao_slkid;
		public TextView yimei_jiaxigao_prdno;
		public TextView yimei_jiaxigao_qty;
		public TextView yimei_jiaxigao_indate;
		public TextView yimei_jiaxigao_mkdate;
	}
}
