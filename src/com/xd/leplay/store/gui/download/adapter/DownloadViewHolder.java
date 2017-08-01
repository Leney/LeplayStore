package com.xd.leplay.store.gui.download.adapter;

import android.view.View;

public abstract class DownloadViewHolder
{
	protected View baseView;

	protected String key;

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public DownloadViewHolder(View baseView)
	{
		this.baseView = baseView;
	}

	@Override
	public boolean equals(Object o)
	{
		return super.equals(o);
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

}
