package com.yimei.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.yimei.activity.GuJingActivity;
import com.yimei.activity.R;
import com.yimei.scrollview.CHScrollView;
import com.yimei.scrollview.GeneralCHScrollView;

public class GuJingScrollAdapter extends BaseAdapter {
	Context context;
	public List<Map<String, Object>> listData;
	// 记录checkbox的状态
	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	// 构造函数
	public GuJingScrollAdapter(Context context, List<Map<String, Object>> datas) {
		this.context = context;
		this.listData = datas;
		initCheck(false);
	}
	
	public HashMap<Integer, Boolean> Getstate(){
		if(state.size()==0||state==null){
			return null;
		}
		return state;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return listData.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listData.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
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

	// 重写View
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater = LayoutInflater.from(context);
		convertView = mInflater.inflate(R.layout.gujing_item, null);
		GuJingActivity.addHViews((GeneralCHScrollView) convertView
				.findViewById(R.id.gujing_item_scroll));
		TextView sid1 = (TextView) convertView.findViewById(R.id.gujing_sid1);
		sid1.setText((String) listData.get(position).get("sid1"));

		TextView slkid = (TextView) convertView.findViewById(R.id.gujing_slkid);
		slkid.setText((String) listData.get(position).get("slkid"));

		TextView prd_no = (TextView) convertView.findViewById(R.id.gujing_prd_no);
		prd_no.setText((String) listData.get(position).get("prd_no"));

		TextView qty = (TextView) convertView.findViewById(R.id.gujing_qty);
		qty.setText((String) listData.get(position).get("qty"));

		TextView remark = (TextView) convertView.findViewById(R.id.gujing_remark);
		remark.setText((String) listData.get(position).get("remark"));

		CheckBox check = (CheckBox) convertView.findViewById(R.id.gujing_item_title);
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				state.put(position,isChecked);
			}
		});
		check.setChecked(state.get(position));
		return convertView;
	}
}
