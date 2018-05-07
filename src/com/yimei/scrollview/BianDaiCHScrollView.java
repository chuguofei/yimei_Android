package com.yimei.scrollview;

import com.yimei.activity.BianDaiActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class BianDaiCHScrollView extends HorizontalScrollView{
	
	BianDaiActivity activity;
	
	public BianDaiCHScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		activity = (BianDaiActivity) context;
	}

	
	public BianDaiCHScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (BianDaiActivity) context;
	}

	public BianDaiCHScrollView(Context context) {
		super(context);
		activity = (BianDaiActivity) context;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		activity.mTouchView = this;
		return super.onTouchEvent(ev);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if(activity.mTouchView == this) {
			activity.onScrollChanged(l, t, oldl, oldt);
		}else{
			super.onScrollChanged(l, t, oldl, oldt);
		}
	}
}
