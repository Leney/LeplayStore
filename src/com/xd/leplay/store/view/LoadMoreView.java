package com.xd.leplay.store.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.leplay.store.R;

/**
 * 列表加载更多视图
 * 
 * @author Administrator
 *
 */
public class LoadMoreView extends LinearLayout
{

	private FrameLayout loadMoreLay;

	private TextView loading, netError;

	private AbsListView.LayoutParams params = null;

	public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public LoadMoreView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public LoadMoreView(Context context)
	{
		super(context);
		init(context);
	}

	private void init(Context context)
	{
		loadMoreLay = new FrameLayout(context);

		params = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT,
				AbsListView.LayoutParams.MATCH_PARENT);
		params.height = (int) context.getResources().getDimension(R.dimen.dp46);

		loading = new TextView(context);
		netError = new TextView(context);

		loading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		loading.setText(context.getResources().getString(
				R.string.footter_loading));
		loading.setTextColor(Color.parseColor("#666666"));
		loading.setGravity(Gravity.CENTER);
		loading.setVisibility(View.VISIBLE);

		netError.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		netError.setText(context.getResources().getString(
				R.string.footter_net_error));
		netError.setTextColor(Color.parseColor("#999999"));
		netError.setGravity(Gravity.CENTER);
		netError.setVisibility(View.GONE);

		loadMoreLay.addView(loading);
		loadMoreLay.addView(netError);
		loadMoreLay.setLayoutParams(params);
		loadMoreLay.setBackgroundColor(context.getResources().getColor(
				R.color.main_color));
		addView(loadMoreLay);
	}

	/**
	 * 设置显示加载视图
	 * 
	 * @param isVisible
	 */
	public void setLoadingVisible(boolean isVisible)
	{
		if (isVisible)
		{
			loading.setVisibility(View.VISIBLE);
			netError.setVisibility(View.GONE);
		} else
		{
			loading.setVisibility(View.GONE);
			netError.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置显示加载错误视图
	 * 
	 * @param isVisible
	 */
	public void setNetErrorVisible(boolean isVisible)
	{
		if (isVisible)
		{
			netError.setVisibility(View.VISIBLE);
			loading.setVisibility(View.GONE);
		} else
		{
			netError.setVisibility(View.GONE);
			loading.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置加载失败视图的点击事件
	 * 
	 * @param listener
	 */
	public void setNetErrorViewOnClickLinstener(OnClickListener listener)
	{
		netError.setOnClickListener(listener);
	}

	/**
	 * 设置加载视图是否显示
	 * 
	 * @param isVisible
	 */
	public void setVisibleView(boolean isVisible)
	{
		if (isVisible)
		{
			loadMoreLay.setVisibility(View.VISIBLE);
		} else
		{
			loadMoreLay.setVisibility(View.GONE);
		}
	}
}
