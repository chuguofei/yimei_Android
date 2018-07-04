package com.yimei.adapter;

import java.util.List;
import java.util.Map;
import com.yimei.activity.R;
import com.yimei.activity.ZhiJuLingChuActivity;
import com.yimei.scrollview.GeneralCHScrollView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ZhiJuLingChuAdapter extends BaseAdapter {

	Context context;
	public static List<Map<String, String>> listData;
	// 构造函数
	public ZhiJuLingChuAdapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.zhijulingchu_itemlist, null);
						
			ZhiJuLingChuActivity.addHViews((GeneralCHScrollView) convertView
					.findViewById(R.id.zhijulingchu_item_scroll));
			viewHolder.zhijulingchu_sid1 = (TextView) convertView
					.findViewById(R.id.zhijulingchu_sid1);
			viewHolder.zhijulingchu_sbid = (TextView) convertView
					.findViewById(R.id.zhijulingchu_sbid);
			viewHolder.zhijulingchu_slkid = (TextView) convertView
					.findViewById(R.id.zhijulingchu_slkid);
			viewHolder.zhijulingchu_prd_no = (TextView) convertView
					.findViewById(R.id.zhijulingchu_prd_no);
			viewHolder.zhijulingchu_mkdate = (TextView) convertView
					.findViewById(R.id.zhijulingchu_mkdate);
			
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Map<String, String> map = listData.get(position);
		String a = map.get("sbid");
		viewHolder.zhijulingchu_sid1.setText(map.get("sid1"));
		viewHolder.zhijulingchu_sbid.setText(map.get("sbid"));
		viewHolder.zhijulingchu_slkid.setText(map.get("slkid"));
		viewHolder.zhijulingchu_prd_no.setText(map.get("prd_no"));
		viewHolder.zhijulingchu_mkdate.setText(map.get("mkdate"));
		return convertView;
	}

	class ViewHolder {
		public TextView zhijulingchu_sid1;
		public TextView zhijulingchu_sbid;
		public TextView zhijulingchu_slkid;
		public TextView zhijulingchu_prd_no;
		public TextView zhijulingchu_mkdate;
	}
}
