package com.yimei.adapter;

import java.util.List;
import java.util.Map;

import com.yimei.activity.R;
import com.yimei.activity.ShangLiaoActivity;
import com.yimei.scrollview.MaterialsBreakdownCHScrollView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MaterialAdapter extends BaseAdapter {

	public Context context;
	public List<Map<String, String>> listData;

	public MaterialAdapter(Context context, List<Map<String, String>> datas) {
		this.context = context;
		this.listData = datas;
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

	@Override
	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		// 如果view未被实例化过，缓存池中没有对应的缓存
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.activity_material, null);
			ShangLiaoActivity
					.addHViews((MaterialsBreakdownCHScrollView) convertView
							.findViewById(R.id.Material_item_scroll));
			viewHolder.Material_xiangci = (TextView) convertView
					.findViewById(R.id.Material_xiangci);
			viewHolder.Material_cailiaoNum = (TextView) convertView
					.findViewById(R.id.Material_cailiaoNum);
			viewHolder.Material_cailiaoName = (TextView) convertView
					.findViewById(R.id.Material_cailiaoName);
			viewHolder.Material_BinCode = (TextView) convertView
					.findViewById(R.id.Material_BinCode);
			viewHolder.Material_cailiaopici = (TextView) convertView
					.findViewById(R.id.Material_cailiaopici);
			viewHolder.Material_SumNum = (TextView) convertView
					.findViewById(R.id.Material_SumNum);
			viewHolder.Material_tidaipin = (TextView) convertView
					.findViewById(R.id.Material_tidaipin);
			// 通过setTag将convertView与viewHolder关联
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		  // 取出bean对象
	    Map<String, String> map = listData.get(position);
	    viewHolder.Material_xiangci.setText(map.get("Material_xiangci"));
	    viewHolder.Material_cailiaoNum.setText(map.get("Material_cailiaoNum"));
	    viewHolder.Material_cailiaoName.setText(map.get("Material_cailiaoName"));
	    viewHolder.Material_BinCode.setText(map.get("Material_BinCode"));
	    viewHolder.Material_cailiaopici.setText(map.get("Material_cailiaopici"));
	    viewHolder.Material_SumNum.setText(map.get("Material_SumNum"));
	    viewHolder.Material_tidaipin.setText(map.get("Material_tidaipin"));
	    return convertView;
	}

	class ViewHolder {
		public TextView Material_xiangci;
		public TextView Material_cailiaoNum;
		public TextView Material_cailiaoName;
		public TextView Material_BinCode;
		public TextView Material_cailiaopici;
		public TextView Material_SumNum;
		public TextView Material_tidaipin;
	}
}
