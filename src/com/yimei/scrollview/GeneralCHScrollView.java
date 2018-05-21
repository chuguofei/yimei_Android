package com.yimei.scrollview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.yimei.activity.GuJingActivity;
import com.yimei.activity.HunJiaoActivity;
import com.yimei.activity.JiaXiGaoActivity;
import com.yimei.activity.RuKuActivity;
import com.yimei.activity.SCFLActivity;
import com.yimei.activity.ZhiJuLingChuActivity;
import com.yimei.activity.ZhiJuQingXiActivity;
import com.yimei.activity.ZhiJuRukKuActivity;
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
		if (title.equals("制具领出")) {
			activity = (ZhiJuLingChuActivity) context;
		}
		if (title.equals("制具清洗")) {
			activity = (ZhiJuQingXiActivity) context;
		}
		if (title.equals("制具入库")) {
			activity = (ZhiJuRukKuActivity) context;
		}
		if (title.equals("加锡膏登记")) {
			activity = (JiaXiGaoActivity) context;
		}
		if (title.equals("生产发料")) {
			activity = (SCFLActivity) context;
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
		if (activity instanceof ZhiJuLingChuActivity) {
			((ZhiJuLingChuActivity) activity).mTouchView = this;
		}
		if (activity instanceof ZhiJuQingXiActivity) {
			((ZhiJuQingXiActivity) activity).mTouchView = this;
		}
		if (activity instanceof ZhiJuRukKuActivity) {
			((ZhiJuRukKuActivity) activity).mTouchView = this;
		}
		if (activity instanceof JiaXiGaoActivity) {
			((JiaXiGaoActivity) activity).mTouchView = this;
		}
		if (activity instanceof SCFLActivity) {
			((SCFLActivity) activity).mTouchView = this;
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
		if (activity instanceof ZhiJuLingChuActivity) {
			if (((ZhiJuLingChuActivity) activity).mTouchView == this) {
				((ZhiJuLingChuActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof ZhiJuQingXiActivity) {
			if (((ZhiJuQingXiActivity) activity).mTouchView == this) {
				((ZhiJuQingXiActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof ZhiJuRukKuActivity) {
			if (((ZhiJuRukKuActivity) activity).mTouchView == this) {
				((ZhiJuRukKuActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof JiaXiGaoActivity) {
			if (((JiaXiGaoActivity) activity).mTouchView == this) {
				((JiaXiGaoActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof SCFLActivity) {
			if (((SCFLActivity) activity).mTouchView == this) {
				((SCFLActivity) activity).onScrollChanged(l, t, oldl, oldt);
				((SCFLActivity) activity).onScrollChanged1(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		
	}
}
