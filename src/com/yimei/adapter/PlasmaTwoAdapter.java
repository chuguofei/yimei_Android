package com.yimei.adapter;

import java.util.List;
import java.util.Map;

import com.yimei.activity.R;
import com.yimei.activity.ZhiJuQingXiActivity;
import com.yimei.activity.ZhuanChuActivity;
import com.yimei.activity.kuaiguozhan.plasmaActivity;
import com.yimei.scrollview.GeneralCHScrollView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlasmaTwoAdapter extends BaseAdapter {

	Context context;
	public static List<Map<String, String>> listData;
	// 构造函数
	public PlasmaTwoAdapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.plasmatwo_itemlist, null);
						
			plasmaActivity.addHViews((GeneralCHScrollView) convertView.findViewById(R.id.plasma_item_scroll));
			
			viewHolder.yimei_plasma_op = (TextView) convertView.findViewById(R.id.yimei_plasma_user);
			viewHolder.yimei_plasma_sid1 = (TextView) convertView.findViewById(R.id.yimei_plasma_sid1);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Map<String, String> map = listData.get(position);
		viewHolder.yimei_plasma_op.setText((String) map.get("op"));
		viewHolder.yimei_plasma_sid1.setText((String) map.get("sid1"));
		return convertView;
	}

	class ViewHolder {
		public TextView yimei_plasma_op;
		public TextView yimei_plasma_sid1;
	}
}
