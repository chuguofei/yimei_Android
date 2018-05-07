package com.yimei.adapter;

import java.util.List;
import java.util.Map;

import com.yimei.activity.R;
import com.yimei.activity.ShangLiaoActivity;
import com.yimei.scrollview.ScanAreaCHScrollView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScanArealAdapter extends BaseAdapter {

	public Context context;
	public List<Map<String, String>> listData;

	public ScanArealAdapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.scanarea_itemlist, null);
			ShangLiaoActivity.addHViews1((ScanAreaCHScrollView) convertView
					.findViewById(R.id.ScannArea_item_scroll));
			viewHolder.ScanArea_xiangci = (TextView) convertView
					.findViewById(R.id.ScannArea_xiangci);
			viewHolder.ScannArea_cailiaodaima = (TextView) convertView
					.findViewById(R.id.ScannArea_cailiaodaima);
			viewHolder.ScannArea_cailiaopici = (TextView) convertView
					.findViewById(R.id.ScannArea_cailiaopici);
			viewHolder.ScannArea_cailiaoName = (TextView) convertView
					.findViewById(R.id.ScannArea_cailiaoName);
			viewHolder.ScannArea_BinCode = (TextView) convertView
					.findViewById(R.id.ScannArea_BinCode);
			viewHolder.ScannArea_cailiaoshuNum = (TextView) convertView
					.findViewById(R.id.ScannArea_cailiaoshuNum);
			viewHolder.ScannArea_verification = (TextView) convertView
					.findViewById(R.id.ScannArea_verification);
			viewHolder.ScannArea_unit = (TextView) convertView
					.findViewById(R.id.ScannArea_unit);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> map = listData.get(position);
		viewHolder.ScanArea_xiangci.setText(map.get("ScanArea_xiangci"));
		viewHolder.ScannArea_cailiaodaima.setText(map
				.get("ScannArea_cailiaodaima"));
		viewHolder.ScannArea_cailiaopici.setText(map
				.get("ScannArea_cailiaopici"));
		viewHolder.ScannArea_cailiaoName.setText(map
				.get("ScannArea_cailiaoName"));
		viewHolder.ScannArea_BinCode.setText(map.get("ScannArea_BinCode"));
		viewHolder.ScannArea_cailiaoshuNum.setText(map
				.get("ScannArea_cailiaoshuNum"));
		viewHolder.ScannArea_verification.setText(map
				.get("ScannArea_verification"));
		viewHolder.ScannArea_unit.setText(map.get("ScannArea_unit"));
		return convertView;
	}

	class ViewHolder {
		public TextView ScanArea_xiangci;
		public TextView ScannArea_cailiaodaima;
		public TextView ScannArea_cailiaopici;
		public TextView ScannArea_cailiaoName;
		public TextView ScannArea_BinCode;
		public TextView ScannArea_cailiaoshuNum;
		public TextView ScannArea_verification;
		public TextView ScannArea_unit;
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
