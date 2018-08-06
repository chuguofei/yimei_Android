package com.yimei.adapter;

import java.util.List;
import java.util.Map;

import com.yimei.activity.JieShou1Activity;
import com.yimei.activity.R;
import com.yimei.activity.ZhiJuQingXiActivity;
import com.yimei.activity.ZhuanChuActivity;
import com.yimei.adapter.ZhuanChuAdapter.ViewHolder;
import com.yimei.scrollview.GeneralCHScrollView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class JieShou1Adapter extends BaseAdapter {

	Context context;
	public static List<Map<String, String>> listData;
	// 构造函数
	public JieShou1Adapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.zhuanchu_itemlist, null);
						
			JieShou1Activity.addHViews((GeneralCHScrollView) convertView
					.findViewById(R.id.zhuanchu_item_scroll));
			viewHolder.yimei_zhuanchu_zcno = (TextView) convertView
					.findViewById(R.id.yimei_zhuanchu_zcno);
			viewHolder.yimei_zhuanchu_sid = (TextView) convertView
					.findViewById(R.id.yimei_zhuanchu_sid);
			viewHolder.yimei_zhuanchu_zcno1 = (TextView) convertView
					.findViewById(R.id.yimei_zhuanchu_zcno1);
			viewHolder.yimei_zhuanchu_prd_name = (TextView) convertView
					.findViewById(R.id.yimei_zhuanchu_prd_name);
			viewHolder.yimei_zhuanchu_qty = (TextView) convertView
					.findViewById(R.id.yimei_zhuanchu_qty);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Map<String, String> map = listData.get(position);
		viewHolder.yimei_zhuanchu_zcno.setText((String) map.get("zcno"));
		viewHolder.yimei_zhuanchu_sid.setText((String) map.get("sid"));
		viewHolder.yimei_zhuanchu_zcno1.setText((String) map.get("zcno1"));
		viewHolder.yimei_zhuanchu_prd_name.setText((String) map.get("prd_name"));
		viewHolder.yimei_zhuanchu_qty.setText((String) map.get("qty"));
		return convertView;
	}

	class ViewHolder {
		public TextView yimei_zhuanchu_zcno;
		public TextView yimei_zhuanchu_sid;
		public TextView yimei_zhuanchu_zcno1;
		public TextView yimei_zhuanchu_slkid;
		public TextView yimei_zhuanchu_prd_name;
		public TextView yimei_zhuanchu_qty;
	}
}
