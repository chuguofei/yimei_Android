package com.yimei.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yimei.activity.JieShouActivity;
import com.yimei.activity.R;
import com.yimei.scrollview.GeneralCHScrollView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class JieShouAdapter extends BaseAdapter {

	Context context;
	public static List<Map<String, Object>> listData;
	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();
	// 构造函数
	public JieShouAdapter(Context context, List<Map<String, Object>> datas) {
		this.context = context;
		this.listData = datas;
		initCheck(false);
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
	
	public HashMap<Integer, Boolean> Getstate(){
		if(state.size()==0||state==null){
			return null;
		}
		return state;
	}
	
	public int initCheck(boolean flag) {
		// map集合的数量和list的数量是一致的
		if(listData==null||listData.size()==0){
			return -1;
		}else{
			for (int i = 0; i < listData.size(); i++) {
				// 设置默认的显示
				state.put(i, flag);
			}
		}
		return 1;
	}
	
	@Override
	@SuppressLint("ViewHolder")
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		// 如果view未被实例化过，缓存池中没有对应的缓存
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.jieshou_itemlist, null);
						
			JieShouActivity.addHViews((GeneralCHScrollView) convertView
					.findViewById(R.id.jieshou_item_scroll));
			viewHolder.yimei_jieshou_op = (TextView) convertView
					.findViewById(R.id.yimei_jieshou_op);
			viewHolder.yimei_jieshou_state = (TextView) convertView
					.findViewById(R.id.yimei_jieshou_state);
			viewHolder.yimei_jieshou_time = (TextView) convertView
					.findViewById(R.id.yimei_jieshou_time);
			viewHolder.yimei_jieshou_sid1 = (TextView) convertView
					.findViewById(R.id.yimei_jieshou_sid1);
			viewHolder.yimei_jieshou_zcno1 = (TextView) convertView
					.findViewById(R.id.yimei_jieshou_zcno1);
			viewHolder.yimei_jieshou_slkid = (TextView) convertView
					.findViewById(R.id.yimei_jieshou_slkid);
			viewHolder.yimei_jieshou_prd_name = (TextView) convertView
					.findViewById(R.id.yimei_jieshou_prd_name);
			viewHolder.yimei_jieshou_qty = (TextView) convertView
					.findViewById(R.id.yimei_jieshou_qty);
			viewHolder.jieshou_item_title = (CheckBox) convertView
					.findViewById(R.id.jieshou_item_title);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Map<String, Object> map = listData.get(position);
		viewHolder.yimei_jieshou_op.setText((String) map.get("op"));
		viewHolder.yimei_jieshou_state.setText((String) map.get("state"));
		viewHolder.yimei_jieshou_time.setText((String) map.get("time"));
		viewHolder.yimei_jieshou_sid1.setText((String) map.get("sid1"));
		viewHolder.yimei_jieshou_zcno1.setText((String) map.get("zcno1"));
		viewHolder.yimei_jieshou_slkid.setText((String) map.get("slkid"));
		viewHolder.yimei_jieshou_prd_name.setText((String) map.get("prd_name"));
		viewHolder.yimei_jieshou_qty.setText((String) map.get("qty"));
		viewHolder.jieshou_item_title.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				state.put(position,isChecked);
			}
		});
		viewHolder.jieshou_item_title.setChecked(state.get(position));
		return convertView;
	}

	class ViewHolder {
		public TextView yimei_jieshou_op;
		public TextView yimei_jieshou_state;
		public TextView yimei_jieshou_time;
		public TextView yimei_jieshou_sid1;
		public TextView yimei_jieshou_zcno1;
		public TextView yimei_jieshou_slkid;
		public TextView yimei_jieshou_prd_name;
		public TextView yimei_jieshou_qty;
		public CheckBox jieshou_item_title;
	}
}
