package com.yimei.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yimei.activity.BianDaiActivity;
import com.yimei.activity.R;
import com.yimei.scrollview.BianDaiCHScrollView;
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

public class BianDaiAdapter extends BaseAdapter {

	Context context;
	public static List<Map<String, Object>> listData;
	// 记录checkbox的状态
	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	// 构造函数
	public BianDaiAdapter(Context context, List<Map<String, Object>> datas) {
		this.context = context;
		this.listData = datas;
		initCheck(false);
	}

	public HashMap<Integer, Boolean> Getstate() {
		if (state.size() == 0 || state == null) {
			return null;
		}
		return state;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return listData.size();
	}

	public Object getItem(int position) {
		return listData.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public int initCheck(boolean flag) {
		// map集合的数量和list的数量是一致的
		if (listData == null || listData.size() == 0) {
			return -1;
		} else {
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
			convertView = mInflater.inflate(R.layout.biandai_itemlist, null);
						
			BianDaiActivity.addHViews((BianDaiCHScrollView) convertView
					.findViewById(R.id.biandai_item_scroll));
			viewHolder.biandai_lotno = (TextView) convertView
					.findViewById(R.id.biandai_lotno);
			viewHolder.biandai_sid = (TextView) convertView
					.findViewById(R.id.biandai_sid);
			viewHolder.biandai_sid1 = (TextView) convertView
					.findViewById(R.id.biandai_sid1);
			viewHolder.biandai_prd_name = (TextView) convertView
					.findViewById(R.id.biandai_prd_name);
			viewHolder.biandai_qty = (TextView) convertView
					.findViewById(R.id.biandai_qty);
			viewHolder.biandai_bincode = (TextView) convertView
					.findViewById(R.id.biandai_bincode);
			viewHolder.biandai_item_title = (CheckBox) convertView
					.findViewById(R.id.biandai_item_title);
			viewHolder.biandai_state1 = (TextView) convertView
					.findViewById(R.id.biandai_state1);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Map<String, Object> map = listData.get(position);
		viewHolder.biandai_lotno.setText((String) map.get("lotno"));
		viewHolder.biandai_sid.setText((String) map.get("sid"));
		viewHolder.biandai_sid1.setText((String) map.get("sid1"));
		viewHolder.biandai_prd_name.setText((String) map.get("prd_name"));
		viewHolder.biandai_qty.setText((String) map.get("qty"));
		viewHolder.biandai_bincode.setText((String) map.get("bincode"));
		viewHolder.biandai_state1.setText((String) map.get("state"));
		viewHolder.biandai_item_title
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						state.put(position, isChecked);
					}
				});
		viewHolder.biandai_item_title.setChecked(state.get(position));
		viewHolder.biandai_item_title.setTag(map);
		return convertView;
	}

	class ViewHolder {
		public CheckBox biandai_item_title;
		public TextView biandai_lotno;
		public TextView biandai_sid;
		public TextView biandai_sid1;
		public TextView biandai_prd_name;
		public TextView biandai_qty;
		public TextView biandai_bincode;
		public TextView biandai_state1;
	}
}
