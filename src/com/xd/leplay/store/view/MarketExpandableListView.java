package com.xd.leplay.store.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;

/**
 * ExpandableListView 自定义封装类
 * 
 * @author lilijun
 */
public class MarketExpandableListView extends ExpandableListView
{

	// /** ~到底了~ 视图 */
	// private FrameLayout endLay = null;

	private Context mContext = null;

	public MarketExpandableListView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		mContext = context;
		init(context);
		// initEndLay(context);
	}

	public MarketExpandableListView(Context context, AttributeSet attrs,
			int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		mContext = context;
		init(context);
		// initEndLay(context);
	}

	public MarketExpandableListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		init(context);
		// initEndLay(context);
	}

	public MarketExpandableListView(Context context)
	{
		super(context);
		mContext = context;
		init(context);
		// initEndLay(context);
	}

	private void init(Context context)
	{
		// 禁止列表到底或者在顶部能继续拉动
		setOverScrollMode(View.OVER_SCROLL_NEVER);
		setDivider(null);
		setChildDivider(null);
		setDividerHeight(0);
		setCacheColorHint(Color.parseColor("#00000000"));
		setFadingEdgeLength(0);
		// 去掉箭头
		setGroupIndicator(null);
		// 设置点击不可收缩
		setOnGroupClickListener(new OnGroupClickListener()
		{
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id)
			{
				return true;
			}
		});
	}

	// private void initEndLay(Context context)
	// {
	// endLay = new FrameLayout(context);
	// endLay.setBackgroundColor(context.getResources().getColor(
	// R.color.main_color));
	// addFooterView(endLay);
	// }

	/**
	 * 显示"~到底了~" 视图
	 */
	public void showEndView()
	{
		// LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT);
		// params.height = (int) mContext.getResources()
		// .getDimension(R.dimen.dp46);
		// TextView endText = new TextView(mContext);
		// endText.setTextSize(DisplayUtil.px2sp(mContext, 36));
		// endText.setText(mContext.getResources().getString(R.string.footter_end));
		// endText.setTextColor(Color.parseColor("#999999"));
		// endText.setGravity(Gravity.CENTER);
		// endLay.addView(endText);
		// endLay.setLayoutParams(params);
	}

}
