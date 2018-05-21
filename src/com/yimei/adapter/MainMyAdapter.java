package com.yimei.adapter;

import java.util.List;

import com.yimei.activity.BianDaiActivity;
import com.yimei.activity.GuJingActivity;
import com.yimei.activity.HunJiaoActivity;
import com.yimei.activity.JiaXiGaoActivity;
import com.yimei.activity.SCFLActivity;
import com.yimei.activity.TongYongActivity;
import com.yimei.activity.HanXianActivity;
import com.yimei.activity.JiaJiaoActivity;
import com.yimei.activity.KaoXiangActivity;
import com.yimei.activity.LoadingActivity;
import com.yimei.activity.LoginActivity;
import com.yimei.activity.MoZuActivity;
import com.yimei.activity.R;
import com.yimei.activity.ZhiJuLingChuActivity;
import com.yimei.activity.ZhiJuQingXiActivity;
import com.yimei.activity.ZhiJuRukKuActivity;
import com.yimei.activity.ZhuangXiangActivity;
import com.yimei.activity.R.id;
import com.yimei.activity.R.layout;
import com.yimei.activity.RuKuActivity;
import com.yimei.entity.Main_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMyAdapter extends BaseAdapter {

    private List<Main_map> list;
    private LayoutInflater inflater;
    private Context context;
    public MainMyAdapter() {}

    public MainMyAdapter(List<Main_map> stuList,Context context) {
        this.list = stuList;
        this.context= context;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public Main_map getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @SuppressLint({ "ViewHolder", "InflateParams" })
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        View view=inflater.inflate(R.layout.main_griditem,null);
        final Main_map mainMap=getItem(position);
        
        TextView tv = (TextView) view.findViewById(R.id.main_text);
        ImageView img = (ImageView) view.findViewById(R.id.main_image);
        img.setImageResource(R.drawable.wenhao);
        setImg(mainMap,img);
        if(mainMap.getKey().equals("D0050")){        	
        	tv.setText("烤箱管理");
        }else{
        	tv.setText(mainMap.getValue());
        }
        img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//包装作业
				if(mainMap.getKey().equals("F0001")){
					/*Intent intent = new Intent(context,
							BianDaiActivity.class);
					v.getContext().startActivity(intent);*/
				}else if(mainMap.getKey().equals("D0002")){
					//固晶管理
					Intent intent = new Intent(context,
							GuJingActivity.class);
					v.getContext().startActivity(intent);
				}else if(mainMap.getKey().equals("D0003")){
					//焊线管理
					Intent intent = new Intent(context,
							HanXianActivity.class);
					v.getContext().startActivity(intent);
				}else if(mainMap.getKey().equals("D0050")){
					//烤箱管理
					Intent intent = new Intent(context,
							KaoXiangActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("D0001")) { //通用工站
					Intent intent = new Intent(context,
							TongYongActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("D2010")) { //加胶登记
					Intent intent = new Intent(context,
							JiaJiaoActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("D0020")) { //编带管理
					Intent intent = new Intent(context,
							BianDaiActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("D5001")) { //模组登记
					Intent intent = new Intent(context,
							MoZuActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("E0004")) { //生产入库登记
					Intent intent = new Intent(context,
							RuKuActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("H0003")) { //装箱作业
					Intent intent = new Intent(context,
							ZhuangXiangActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("D2009")) { //混胶作业
					Intent intent = new Intent(context,
							HunJiaoActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("E5005")) { //制具领出
					Intent intent = new Intent(context,
							ZhiJuLingChuActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("E5006")) { //制具清洗
					Intent intent = new Intent(context,
							ZhiJuQingXiActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("E5004")) { //制具清洗
					Intent intent = new Intent(context,
							ZhiJuRukKuActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("D6004")) { //加锡膏登记
					Intent intent = new Intent(context,
							JiaXiGaoActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("E0001")) { //生产发料
					Intent intent = new Intent(context,
							SCFLActivity.class);
					v.getContext().startActivity(intent);
				}
			}
		});
        return view;
    }

    private void setImg(Main_map mainMap,ImageView img){
    	if(mainMap.getKey().equals("F0001")){
			
		}else if(mainMap.getKey().equals("D0002")){ //固晶管理
			img.setImageResource(R.drawable.gujing);
		}else if(mainMap.getKey().equals("D0003")){ //焊线管理
			img.setImageResource(R.drawable.hanjie);
		}else if(mainMap.getKey().equals("D0050")){ //烤箱管理
			
		}
		if (mainMap.getKey().equals("D0001")) { //通用工站
			img.setImageResource(R.drawable.tongyong);
		}
		if (mainMap.getKey().equals("D2010")) { //加胶登记
			img.setImageResource(R.drawable.jiajiao);
		}
		if (mainMap.getKey().equals("D0020")) { //编带管理
			img.setImageResource(R.drawable.biandai);
		}
		if (mainMap.getKey().equals("D5001")) { //模组登记
			img.setImageResource(R.drawable.mozu);
		}
		if (mainMap.getKey().equals("E0004")) { //生产入库登记
			img.setImageResource(R.drawable.shengcanruku);
		}
		if (mainMap.getKey().equals("H0003")) { //装箱作业
			img.setImageResource(R.drawable.zhuangxiang);
		}
		if (mainMap.getKey().equals("D2009")) { //混胶作业
			img.setImageResource(R.drawable.hunjiao);
		}
    }
    
	/*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        View view=inflater.inflate(R.layout.main_item,null);
        final Main_map mainMap=getItem(position);
        //在view视图中查找id为image_photo的控件
        Button btn= (Button) view.findViewById(R.id.Main_btn_name);
        if(mainMap.getKey().equals("D0050")){        	
        	btn.setText("烤箱管理");
        }else{
        	btn.setText(mainMap.getValue());
        }
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//编带包装
				if(mainMap.getKey().equals("F0001")){
					Intent intent = new Intent(context,
							BianDaiActivity.class);
					v.getContext().startActivity(intent);
				}else if(mainMap.getKey().equals("D0002")){
					//固晶管理
					Intent intent = new Intent(context,
							GuJingActivity.class);
					v.getContext().startActivity(intent);
				}else if(mainMap.getKey().equals("D0003")){
					//焊线管理
					Intent intent = new Intent(context,
							HanXianActivity.class);
					v.getContext().startActivity(intent);
				}else if(mainMap.getKey().equals("D0050")){
					//烤箱管理
					Intent intent = new Intent(context,
							KaoXiangActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("D0001")) { //通用工站
					Intent intent = new Intent(context,
							GuJingActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("D2010")) { //加胶登记
					Intent intent = new Intent(context,
							JiaJiaoActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("D0020")) { //编带管理
					Intent intent = new Intent(context,
							BianDaiActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("D5001")) { //模组登记
					Intent intent = new Intent(context,
							MoZuActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("E0004")) { //生产入库登记
					Intent intent = new Intent(context,
							RuKuActivity.class);
					v.getContext().startActivity(intent);
				}
				if (mainMap.getKey().equals("H0003")) { //装箱作业
					Intent intent = new Intent(context,
							ZhuangXiangActivity.class);
					v.getContext().startActivity(intent);
				}
			}
		});
        return view;
    }*/
}