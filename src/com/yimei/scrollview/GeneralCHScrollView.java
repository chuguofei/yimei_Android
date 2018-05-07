package com.yimei.scrollview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.yimei.activity.RuKuActivity;
import com.yimei.activity.ZhuangXiangActivity;

public class GeneralCHScrollView extends HorizontalScrollView {

	Activity activity;

	public GeneralCHScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		GeneralJump(context);
	}

	public GeneralCHScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		GeneralJump(context);
	}

	private void GeneralJump(Context context) {
	    CharSequence title = ((Activity) context).getTitle();
		if (title.equals("生产入库")) {
			activity = (RuKuActivity) context;
		}
		if (title.equals("装箱作业")) {
			activity = (ZhuangXiangActivity) context;
		}
	}

	public GeneralCHScrollView(Context context) {
		super(context);
		GeneralJump(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (activity instanceof RuKuActivity) {
			((RuKuActivity) activity).mTouchView = this;
		}
		if (activity instanceof ZhuangXiangActivity) {
			((ZhuangXiangActivity) activity).mTouchView = this;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (activity instanceof RuKuActivity) {
			if (((RuKuActivity) activity).mTouchView == this) {
				((RuKuActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof ZhuangXiangActivity) {
			if (((ZhuangXiangActivity) activity).mTouchView == this) {
				((ZhuangXiangActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
	}
}
