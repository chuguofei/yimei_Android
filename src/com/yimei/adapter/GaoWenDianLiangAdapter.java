package com.yimei.adapter;

import java.util.List;
import java.util.Map;

import com.yimei.activity.R;
import com.yimei.activity.TongYongGuoZhanActivity;
import com.yimei.activity.kuaiguozhan.DiDianLiuActivity;
import com.yimei.activity.kuaiguozhan.GaoWenDianLiangActivity;
import com.yimei.activity.kuaiguozhan.KanDaiActivity;
import com.yimei.activity.kuaiguozhan.TieBeiJiaoActivity;
import com.yimei.activity.kuaiguozhan.WaiGuanActivity;
import com.yimei.activity.kuaiguozhan.plasmaActivity;
import com.yimei.scrollview.GeneralCHScrollView;
import com.yimei.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GaoWenDianLiangAdapter extends BaseAdapter {

	Context context;
	public static List<Map<String, String>> listData;
	// 构造函数
	public GaoWenDianLiangAdapter(Context context, List<Map<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.gaowendianliang_itemlist, null);
			
			CharSequence title = ((Activity) context).getTitle();
			if(title.equals("高温点亮过站")){
				GaoWenDianLiangActivity.addHViews((GeneralCHScrollView) convertView
						.findViewById(R.id.gaowendianliang_item_scroll));
			}
			if(title.equals("外观过站")){
				WaiGuanActivity.addHViews((GeneralCHScrollView) convertView
						.findViewById(R.id.gaowendianliang_item_scroll));
			}
			if(title.equals("贴背胶过站")){
				TieBeiJiaoActivity.addHViews((GeneralCHScrollView) convertView
						.findViewById(R.id.gaowendianliang_item_scroll));
			}
			if(title.equals("低电流过站")){
				DiDianLiuActivity.addHViews((GeneralCHScrollView) convertView
						.findViewById(R.id.gaowendianliang_item_scroll));
			}
			if(title.equals("看带过站")){
				KanDaiActivity.addHViews((GeneralCHScrollView) convertView
						.findViewById(R.id.gaowendianliang_item_scroll));
			}
			if(title.equals("plasma过站")){
				plasmaActivity.addHViews((GeneralCHScrollView) convertView
						.findViewById(R.id.gaowendianliang_item_scroll));
			}
			if(title.equals("通用过站")){
				TongYongGuoZhanActivity.addHViews((GeneralCHScrollView) convertView
						.findViewById(R.id.gaowendianliang_item_scroll));
			}
			viewHolder.gaowendianliang_op  = (TextView) convertView
					.findViewById(R.id.gaowendianliang_op);
			viewHolder.gaowendianliang_sid1 = (TextView) convertView
					.findViewById(R.id.gaowendianliang_sid1);
			viewHolder.gaowendianliang_slkid = (TextView) convertView
					.findViewById(R.id.gaowendianliang_slkid);
			viewHolder.gaowendianliang_prd_no = (TextView) convertView
					.findViewById(R.id.gaowendianliang_prd_no);
			viewHolder.gaowendianliang_qty = (TextView) convertView
					.findViewById(R.id.gaowendianliang_qty);
			viewHolder.gaowendianliang_zcno = (TextView) convertView
					.findViewById(R.id.gaowendianliang_zcno);
			convertView.setTag(viewHolder);
		} else {// 如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Map<String, String> map = listData.get(position);
		viewHolder.gaowendianliang_op.setText((String) map.get("op"));
		viewHolder.gaowendianliang_sid1.setText((String) map.get("sid1"));
		viewHolder.gaowendianliang_slkid.setText((String) map.get("slkid"));
		viewHolder.gaowendianliang_prd_no.setText((String) map.get("prd_no"));
		viewHolder.gaowendianliang_qty.setText((String) map.get("qty"));
		viewHolder.gaowendianliang_zcno.setText((String) map.get("zcno"));
		return convertView;
	}

	class ViewHolder {
		public TextView gaowendianliang_op;
		public TextView gaowendianliang_sid1;
		public TextView gaowendianliang_slkid;
		public TextView gaowendianliang_prd_no;
		public TextView gaowendianliang_qty;
		public TextView gaowendianliang_zcno;
	}
}
