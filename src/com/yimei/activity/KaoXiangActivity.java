package com.yimei.activity;

import java.util.ArrayList;
import java.util.List;

import com.yimei.scrollview.KaoXiangCHScrollView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.HorizontalScrollView;
import android.widget.ListView;

public class KaoXiangActivity extends Activity {
	
	public HorizontalScrollView mTouchView;
	private static List<KaoXiangCHScrollView> KaoXiangScrollViews = new ArrayList<KaoXiangCHScrollView>();
	private static ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kaoxiang);
	}
	

	public static void addHViews(final KaoXiangCHScrollView hScrollView) {
		if (!KaoXiangScrollViews.isEmpty()) {
			int size = KaoXiangScrollViews.size();
			KaoXiangCHScrollView scrollView = KaoXiangScrollViews.get(size - 1);
			final int scrollX = scrollView.getScrollX();
			if (scrollX != 0) {
				mListView.post(new Runnable() {
					@Override
					public void run() {
						hScrollView.scrollTo(scrollX, 0);
					}
				});
			}
		}
		KaoXiangScrollViews.add(hScrollView);
	}

	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		for (KaoXiangCHScrollView scrollView : KaoXiangScrollViews) {
			if (mTouchView != scrollView)
				scrollView.smoothScrollTo(l, t);
		}
	}
}
