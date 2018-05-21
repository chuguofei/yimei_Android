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

public class SCFL_MaterialAdapter extends BaseAdapter {

	Context context;
	public static List<Map<String, String>> listData;
	// 构造函数
	public SCFL_MaterialAdapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.scfl_material_itemlist, null);
						
			SCFLActivity.addHViews1((GeneralCHScrollView) convertView
					.findViewById(R.id.scfl_material_item_scroll));
			viewHolder.yimei_scfl_material_itm = (TextView) convertView
					.findViewById(R.id.yimei_scfl_material_itm);
			viewHolder.yimei_scfl_material_prd_no = (TextView) convertView
					.findViewById(R.id.yimei_scfl_material_prd_no);
			viewHolder.yimei_scfl_material_prd_name = (TextView) convertView
					.findViewById(R.id.yimei_scfl_material_prd_name);
			viewHolder.yimei_scfl_material_prd_mark = (TextView) convertView
					.findViewById(R.id.yimei_scfl_material_prd_mark);
			viewHolder.yimei_scfl_material_wh = (TextView) convertView
					.findViewById(R.id.yimei_scfl_material_wh);
			viewHolder.yimei_scfl_material_unit = (TextView) convertView
					.findViewById(R.id.yimei_scfl_material_unit);
			viewHolder.yimei_scfl_material_qty_lost = (TextView) convertView
					.findViewById(R.id.yimei_scfl_material_qty_lost);
			viewHolder.yimei_scfl_material_qty_rsv = (TextView) convertView
					.findViewById(R.id.yimei_scfl_material_qty_rsv);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Map<String, String> map = listData.get(position);
		viewHolder.yimei_scfl_material_itm.setText((String) map.get("itm"));
		viewHolder.yimei_scfl_material_prd_no.setText((String) map.get("prd_no"));
		viewHolder.yimei_scfl_material_prd_name.setText((String) map.get("prd_name"));
		viewHolder.yimei_scfl_material_prd_mark.setText((String) map.get("prd_mark"));
		viewHolder.yimei_scfl_material_wh.setText((String) map.get("wh"));
		viewHolder.yimei_scfl_material_unit.setText((String) map.get("unit"));
		viewHolder.yimei_scfl_material_qty_rsv.setText((String) map.get("qty_rsv"));
		viewHolder.yimei_scfl_material_qty_lost.setText((String) map.get("qty_lost"));
		return convertView;
	}

	class ViewHolder {
		public TextView yimei_scfl_material_itm;
		public TextView yimei_scfl_material_prd_no;
		public TextView yimei_scfl_material_prd_name;
		public TextView yimei_scfl_material_prd_mark;
		public TextView yimei_scfl_material_wh;
		public TextView yimei_scfl_material_unit;
		public TextView yimei_scfl_material_qty_rsv;
		public TextView yimei_scfl_material_qty_lost;
	}

}
