package com.xd.leplay.store.view;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class BannerScroller extends Scroller
{
	private int mDuration = 5000;

	public BannerScroller(Context context, Interpolator interpolator,
			boolean flywheel)
	{
		super(context, interpolator, flywheel);
	}

	public BannerScroller(Context context, Interpolator interpolator)
	{
		super(context, interpolator);
	}

	public BannerScroller(Context context)
	{
		super(context);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration)
	{
		super.startScroll(startX, startY, dx, dy, mDuration);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy)
	{
		super.startScroll(startX, startY, dx, dy, mDuration);
	}

	public void setmDuration(int time)
	{
		mDuration = time;
	}

	public int getmDuration()
	{
		return mDuration;
	}
}
