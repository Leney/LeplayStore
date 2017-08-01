package com.xd.leplay.store.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 监听ScrollView的滚动事件封装类
 * 
 * @author lilijun
 *
 */
public class ObservableScrollView extends ScrollView
{
	private ScrollViewListener scrollViewListener = null;

	public ObservableScrollView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		// TODO Auto-generated constructor stub
	}

	public ObservableScrollView(Context context, AttributeSet attrs,
			int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public ObservableScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ObservableScrollView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setScrollViewListener(ScrollViewListener scrollViewListener)
	{
		this.scrollViewListener = scrollViewListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy)
	{
		super.onScrollChanged(x, y, oldx, oldy);
		if (scrollViewListener != null)
		{
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}

}
