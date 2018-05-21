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
import com.yimei.activity.SCFLActivity;
import com.yimei.activity.ZhiJuQingXiActivity;
import com.yimei.adapter.ZhiJuRuKuAdapter.ViewHolder;
import com.yimei.scrollview.GeneralCHScrollView;

public class SCFL_ScanAdapter extends BaseAdapter {

	Context context;
	public static List<Map<String, String>> listData;
	// 构造函数
	public SCFL_ScanAdapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.scfl_scan_itemlist, null);
						
			SCFLActivity.addHViews((GeneralCHScrollView) convertView
					.findViewById(R.id.scfl_scan_item_scroll));
			viewHolder.yimei_scfl_scan_itm = (TextView) convertView
					.findViewById(R.id.yimei_scfl_scan_itm);
			viewHolder.yimei_scfl_scan_gdic = (TextView) convertView
					.findViewById(R.id.yimei_scfl_scan_gdic);
			viewHolder.yimei_scfl_scan_name = (TextView) convertView
					.findViewById(R.id.yimei_scfl_scan_name);
			viewHolder.yimei_scfl_scan_qty = (TextView) convertView
					.findViewById(R.id.yimei_scfl_scan_qty);
			viewHolder.yimei_scfl_scan_sph = (TextView) convertView
					.findViewById(R.id.yimei_scfl_scan_sph);
			viewHolder.yimei_scfl_scan_prd_mark = (TextView) convertView
					.findViewById(R.id.yimei_scfl_scan_prd_mark);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Map<String, String> map = listData.get(position);
		viewHolder.yimei_scfl_scan_itm.setText((String) map.get("itm"));
		viewHolder.yimei_scfl_scan_gdic.setText((String) map.get("gdic"));
		viewHolder.yimei_scfl_scan_name.setText((String) map.get("name"));
		viewHolder.yimei_scfl_scan_qty.setText((String) map.get("qty"));
		viewHolder.yimei_scfl_scan_sph.setText((String) map.get("sph"));
		viewHolder.yimei_scfl_scan_prd_mark.setText((String) map.get("prd_mark"));
		return convertView;
	}

	class ViewHolder {
		public TextView yimei_scfl_scan_itm;
		public TextView yimei_scfl_scan_gdic;
		public TextView yimei_scfl_scan_name;
		public TextView yimei_scfl_scan_qty;
		public TextView yimei_scfl_scan_sph;
		public TextView yimei_scfl_scan_prd_mark;
	}

}
