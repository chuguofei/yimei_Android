package com.yimei.scrollview;

import com.yimei.activity.TongYongActivity;
import com.yimei.activity.MoZuActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class MoZuCHScrollView extends HorizontalScrollView{
	
	MoZuActivity activity;
	
	public MoZuCHScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		activity = (MoZuActivity) context;
	}

	
	public MoZuCHScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (MoZuActivity) context;
	}

	public MoZuCHScrollView(Context context) {
		super(context);
		activity = (MoZuActivity) context;
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
