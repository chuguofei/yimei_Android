package com.yimei.scrollview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.yimei.activity.GuJingActivity;
import com.yimei.activity.HunJiaoActivity;
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
		if (title.equals("固晶工站")) {
			activity = (GuJingActivity) context;
		}
		if (title.equals("混胶登记")) {
			activity = (HunJiaoActivity) context;
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
		if (activity instanceof GuJingActivity) {
			((GuJingActivity) activity).mTouchView = this;
		}
		if (activity instanceof HunJiaoActivity) {
			((HunJiaoActivity) activity).mTouchView = this;
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
		if (activity instanceof GuJingActivity) {
			if (((GuJingActivity) activity).mTouchView == this) {
				((GuJingActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof HunJiaoActivity) {
			if (((HunJiaoActivity) activity).mTouchView == this) {
				((HunJiaoActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
	}
}
