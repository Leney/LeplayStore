package com.xd.leplay.store.gui.details.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.xd.leplay.store.R;

public class DetailCommentListView extends ListView
{
	/** ~到底了~ 视图 */
	// private TextView endText = null;

	// private Context mContext = null;

	private ListViewScrollListener scrollViewListener = null;

	public DetailCommentListView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		// mContext = context;
		init(context);
		// initEndLay(context);
	}

	public DetailCommentListView(Context context, AttributeSet attrs,
			int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		// mContext = context;
		init(context);
		// initEndLay(context);
	}

	public DetailCommentListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// mContext = context;
		init(context);
		// initEndLay(context);
	}

	public DetailCommentListView(Context context)
	{
		super(context);
		// mContext = context;
		init(context);
		// initEndLay(context);
	}

	private void init(Context context)
	{
		// 禁止在顶部或者到底之后能拉动
		setOverScrollMode(View.OVER_SCROLL_NEVER);
		setDivider(context.getResources().getDrawable(R.drawable.divider_color));
		setDividerHeight(1);
		setCacheColorHint(Color.parseColor("#00000000"));
		setFadingEdgeLength(0);
	}

	// private void initEndLay(Context context)
	// {
	// FrameLayout endLay = new FrameLayout(context);
	// endLay.setBackgroundColor(context.getResources().getColor(
	// R.color.main_color));
	// FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
	// FrameLayout.LayoutParams.MATCH_PARENT,
	// FrameLayout.LayoutParams.MATCH_PARENT);
	// params.height = (int) mContext.getResources()
	// .getDimension(R.dimen.dp46);
	// endText = new TextView(mContext);
	// endText.setTextSize(DisplayUtil.px2sp(mContext, 36));
	// endText.setText(mContext.getResources().getString(R.string.footter_end));
	// endText.setTextColor(Color.parseColor("#999999"));
	// endText.setGravity(Gravity.CENTER);
	// endText.setLayoutParams(params);
	// endLay.addView(endText);
	// addFooterView(endLay);
	// endText.setVisibility(View.GONE);
	// }

	public void setScrollViewListener(ListViewScrollListener scrollViewListener)
	{
		this.scrollViewListener = scrollViewListener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		super.onScrollChanged(l, t, oldl, oldt);
		if (scrollViewListener != null)
		{
			scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
		}
	}

	/**
	 * 显示"~到底了~" 视图
	 */
	public void showEndView()
	{
		// endText.setVisibility(View.VISIBLE);
	}

	/** 隐藏"~到底了~"视图 */
	public void hideEndView()
	{
		// endText.setVisibility(View.GONE);
	}
}
