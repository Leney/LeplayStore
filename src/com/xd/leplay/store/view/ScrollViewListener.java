package com.xd.leplay.store.view;

/**
 * ScrollView监听滚动的自定义接口
 * 
 * @author lilijun
 *
 */
public interface ScrollViewListener
{
	void onScrollChanged(ObservableScrollView scrollView, int x, int y,
			int oldx, int oldy);
}
