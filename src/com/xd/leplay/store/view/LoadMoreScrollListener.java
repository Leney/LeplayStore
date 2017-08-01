package com.xd.leplay.store.view;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.xd.base.util.DLog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * 加载更多的监听器
 * 
 * @author lilijun
 * 
 */
public class LoadMoreScrollListener extends PauseOnScrollListener
{
	private setOnScrollToEndListener listener;

	private int size = 10;

	public void setSize(int size)
	{
		this.size = size;
	}

	public interface setOnScrollToEndListener
	{

		void loadMoreWhenScrollToEnd();

	}

	public LoadMoreScrollListener(ImageLoader imageLoader,
			boolean pauseOnScroll, boolean pauseOnFling,
			setOnScrollToEndListener setOnScrollToEndListener)
	{
		super(imageLoader, pauseOnScroll, pauseOnFling);
		this.listener = setOnScrollToEndListener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		super.onScrollStateChanged(view, scrollState);
		// 当不滚动时
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE)
		{
			// 判断滚动到底部
			if (view.getCount() - view.getLastVisiblePosition() < size)
			{
				DLog.d("denglh", "滑动到底");
				listener.loadMoreWhenScrollToEnd();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount)
	{
		
	}
}