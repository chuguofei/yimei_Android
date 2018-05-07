package com.yimei.scrollview;

import com.yimei.activity.ShangLiaoActivity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class ScanAreaCHScrollView extends HorizontalScrollView{
	
	ShangLiaoActivity activity;
	
	public ScanAreaCHScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		activity = (ShangLiaoActivity) context;
	}

	
	public ScanAreaCHScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (ShangLiaoActivity) context;
	}

	public ScanAreaCHScrollView(Context context) {
		super(context);
		activity = (ShangLiaoActivity) context;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		activity.mTouchView1 = this;
		return super.onTouchEvent(ev);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if(activity.mTouchView1 == this) {
			activity.onScrollChanged1(l, t, oldl, oldt);
		}else{
			super.onScrollChanged(l, t, oldl, oldt);
		}
	}
}
