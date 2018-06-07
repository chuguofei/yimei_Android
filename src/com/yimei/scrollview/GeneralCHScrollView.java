package com.yimei.scrollview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.yimei.activity.GuJingActivity;
import com.yimei.activity.HunJiaoActivity;
import com.yimei.activity.JiaXiGaoActivity;
import com.yimei.activity.JieShouActivity;
import com.yimei.activity.RuKuActivity;
import com.yimei.activity.SCFLActivity;
import com.yimei.activity.ZhiJuLingChuActivity;
import com.yimei.activity.ZhiJuQingXiActivity;
import com.yimei.activity.ZhiJuRukKuActivity;
import com.yimei.activity.ZhuanChuActivity;
import com.yimei.activity.ZhuangXiangActivity;
import com.yimei.activity.ipqc.IPQC_shoujian;
import com.yimei.activity.ipqc.IPQC_xunjian;
import com.yimei.activity.ipqc.ORT_quyang;
import com.yimei.activity.kuaiguozhan.DiDianLiuActivity;
import com.yimei.activity.kuaiguozhan.GaoWenDianLiangActivity;
import com.yimei.activity.kuaiguozhan.KanDaiActivity;
import com.yimei.activity.kuaiguozhan.TieBeiJiaoActivity;
import com.yimei.activity.kuaiguozhan.WaiGuanActivity;

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
		if (title.equals("转出登记")) {
			activity = (ZhuanChuActivity) context;
		}
		if (title.equals("接收登记")) {
			activity = (JieShouActivity) context;
		}
		if (title.equals("高温点亮过站")) {
			activity = (GaoWenDianLiangActivity) context;
		}
		if (title.equals("外观过站")) {
			activity = (WaiGuanActivity) context;
		}
		if (title.equals("贴背胶过站")) {
			activity = (TieBeiJiaoActivity) context;
		}
		if (title.equals("低电流过站")) {
			activity = (DiDianLiuActivity) context;
		}
		if (title.equals("看带过站")) {
			activity = (KanDaiActivity) context;
		}
		if (title.equals("首件检验记录")) {
			activity = (IPQC_shoujian) context;
		}
		if (title.equals("巡检记录")) {
			activity = (IPQC_xunjian) context;
		}
		if (title.equals("ORT取样")) {
			activity = (ORT_quyang) context;
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
		if (activity instanceof ZhuanChuActivity) {
			((ZhuanChuActivity) activity).mTouchView = this;
		}
		if (activity instanceof JieShouActivity) {
			((JieShouActivity) activity).mTouchView = this;
		}
		if (activity instanceof GaoWenDianLiangActivity) {
			((GaoWenDianLiangActivity) activity).mTouchView = this;
		}
		if (activity instanceof WaiGuanActivity) {
			((WaiGuanActivity) activity).mTouchView = this;
		}
		if (activity instanceof TieBeiJiaoActivity) {
			((TieBeiJiaoActivity) activity).mTouchView = this;
		}
		if (activity instanceof DiDianLiuActivity) {
			((DiDianLiuActivity) activity).mTouchView = this;
		}
		if (activity instanceof KanDaiActivity) {
			((KanDaiActivity) activity).mTouchView = this;
		}
		if (activity instanceof IPQC_shoujian) {
			((IPQC_shoujian) activity).mTouchView = this;
		}
		if (activity instanceof IPQC_xunjian) {
			((IPQC_xunjian) activity).mTouchView = this;
		}
		if (activity instanceof ORT_quyang) {
			((ORT_quyang) activity).mTouchView = this;
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
		if (activity instanceof ZhuanChuActivity) {
			if (((ZhuanChuActivity) activity).mTouchView == this) {
				((ZhuanChuActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof JieShouActivity) {
			if (((JieShouActivity) activity).mTouchView == this) {
				((JieShouActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof GaoWenDianLiangActivity) {
			if (((GaoWenDianLiangActivity) activity).mTouchView == this) {
				((GaoWenDianLiangActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof WaiGuanActivity) {
			if (((WaiGuanActivity) activity).mTouchView == this) {
				((WaiGuanActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof TieBeiJiaoActivity) {
			if (((TieBeiJiaoActivity) activity).mTouchView == this) {
				((TieBeiJiaoActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof DiDianLiuActivity) {
			if (((DiDianLiuActivity) activity).mTouchView == this) {
				((DiDianLiuActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof KanDaiActivity) {
			if (((KanDaiActivity) activity).mTouchView == this) {
				((KanDaiActivity) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof IPQC_shoujian) {
			if (((IPQC_shoujian) activity).mTouchView == this) {
				((IPQC_shoujian) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof IPQC_xunjian) {
			if (((IPQC_xunjian) activity).mTouchView == this) {
				((IPQC_xunjian) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
		if (activity instanceof ORT_quyang) {
			if (((ORT_quyang) activity).mTouchView == this) {
				((ORT_quyang) activity).onScrollChanged(l, t, oldl, oldt);
			} else {
				super.onScrollChanged(l, t, oldl, oldt);
			}
		}
	}
}
