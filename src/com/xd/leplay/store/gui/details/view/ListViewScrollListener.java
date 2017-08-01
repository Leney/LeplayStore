package com.xd.leplay.store.gui.details.view;

import android.widget.ListView;

/**
 * ScrollView监听滚动的自定义接口
 * 
 * @author lilijun
 *
 */
public interface ListViewScrollListener
{
	void onScrollChanged(ListView listView, int x, int y, int oldx, int oldy);
}
