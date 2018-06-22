package com.yimei.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yimei.activity.R.color;
import com.yimei.activity.TongYongActivity;
import com.yimei.activity.R;
import com.yimei.scrollview.CHScrollView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class ScrollAdapter extends BaseAdapter {
	Context context;
	public List<Map<String, Object>> listData;
	// 记录checkbox的状态
	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	// 构造函数
	public ScrollAdapter(Context context, List<Map<String, Object>> datas) {
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
		convertView = mInflater.inflate(R.layout.item, null);
		TongYongActivity.addHViews((CHScrollView) convertView
				.findViewById(R.id.item_scroll));
		TextView sid1 = (TextView) convertView.findViewById(R.id.sid1);
		sid1.setText((String) listData.get(position).get("sid1"));

		TextView slkid = (TextView) convertView.findViewById(R.id.slkid);
		slkid.setText((String) listData.get(position).get("slkid"));

		TextView prd_no = (TextView) convertView.findViewById(R.id.prd_no);
		prd_no.setText((String) listData.get(position).get("prd_no"));

		TextView qty = (TextView) convertView.findViewById(R.id.qty);
		qty.setText((String) listData.get(position).get("qty"));

		TextView remark = (TextView) convertView.findViewById(R.id.remark);
		remark.setText((String) listData.get(position).get("remark"));

		TextView fircheck = (TextView) convertView.findViewById(R.id.fircheck);
		fircheck.setText((String) listData.get(position).get("fircheck"));
		if(((String) listData.get(position).get("fircheck")).toString().equals("2")){
			sid1.setTextColor(color.darkgoldenrod);
			slkid.setTextColor(color.darkgoldenrod);
			prd_no.setTextColor(color.darkgoldenrod);
			qty.setTextColor(color.darkgoldenrod);
			remark.setTextColor(color.darkgoldenrod);
			fircheck.setTextColor(color.darkgoldenrod);
		}
		CheckBox check = (CheckBox) convertView.findViewById(R.id.item_title);
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
