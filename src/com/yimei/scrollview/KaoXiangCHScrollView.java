package com.yimei.scrollview;

import com.yimei.activity.KaoXiangActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class KaoXiangCHScrollView extends HorizontalScrollView{
	
	KaoXiangActivity activity;
	
	public KaoXiangCHScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		activity = (KaoXiangActivity) context;
	}

	
	public KaoXiangCHScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (KaoXiangActivity) context;
	}

	public KaoXiangCHScrollView(Context context) {
		super(context);
		activity = (KaoXiangActivity) context;
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
