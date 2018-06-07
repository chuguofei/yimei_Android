package com.yimei.adapter;

import java.util.List;
import java.util.Map;

import com.yimei.activity.R;
import com.yimei.activity.ZhiJuQingXiActivity;
import com.yimei.activity.ipqc.ORT_quyang;
import com.yimei.scrollview.GeneralCHScrollView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ORTQuYangAdapter extends BaseAdapter {

	Context context;
	public static List<Map<String, String>> listData;
	// 构造函数
	public ORTQuYangAdapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.ortquyang_itemlist, null);
						
			ORT_quyang.addHViews((GeneralCHScrollView) convertView
					.findViewById(R.id.ort_quyang_item_scroll));
			viewHolder.ort_quyang_op = (TextView) convertView.findViewById(R.id.ort_quyang_op);
			viewHolder.ort_quyang_sbid = (TextView) convertView.findViewById(R.id.ort_quyang_sbid);
			viewHolder.ort_quyang_sid1 = (TextView) convertView.findViewById(R.id.ort_quyang_sid1);
			viewHolder.ort_quyang_zcno = (TextView) convertView.findViewById(R.id.ort_quyang_zcno);
			viewHolder.ort_quyang_slkid = (TextView) convertView.findViewById(R.id.ort_quyang_slkid);
			viewHolder.ort_quyang_prd_no = (TextView) convertView.findViewById(R.id.ort_quyang_prd_no);
			viewHolder.ort_quyang_prd_name = (TextView) convertView.findViewById(R.id.ort_quyang_prd_name);
			viewHolder.ort_quyang_qty = (TextView) convertView.findViewById(R.id.ort_quyang_qty);
			viewHolder.ort_quyang_mkdate = (TextView) convertView.findViewById(R.id.ort_quyang_mkdate);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Map<String, String> map = listData.get(position);
		viewHolder.ort_quyang_op.setText((String) map.get("op"));
		viewHolder.ort_quyang_sbid.setText((String) map.get("sbid"));
		viewHolder.ort_quyang_sid1.setText((String) map.get("sid1"));
		viewHolder.ort_quyang_zcno.setText((String) map.get("zcno"));
		viewHolder.ort_quyang_slkid.setText((String) map.get("slkid"));
		viewHolder.ort_quyang_prd_no.setText((String) map.get("prd_no"));
		viewHolder.ort_quyang_prd_name.setText((String) map.get("prd_name"));
		viewHolder.ort_quyang_qty.setText((String) map.get("qty"));
		viewHolder.ort_quyang_mkdate.setText((String) map.get("mkdate"));
		return convertView;
	}

	class ViewHolder {
		public TextView ort_quyang_op;
		public TextView ort_quyang_sbid;
		public TextView ort_quyang_sid1;
		public TextView ort_quyang_zcno;
		public TextView ort_quyang_slkid;
		public TextView ort_quyang_prd_no;
		public TextView ort_quyang_prd_name;
		public TextView ort_quyang_qty;
		public TextView ort_quyang_mkdate;
	}
}
