package com.yimei.scrollview;

import com.yimei.activity.JiaJiaoActivity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class JiaJiaoCHScrollView extends HorizontalScrollView{
	
	JiaJiaoActivity activity;
	
	public JiaJiaoCHScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		activity = (JiaJiaoActivity) context;
	}

	
	public JiaJiaoCHScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (JiaJiaoActivity) context;
	}

	public JiaJiaoCHScrollView(Context context) {
		super(context);
		activity = (JiaJiaoActivity) context;
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
