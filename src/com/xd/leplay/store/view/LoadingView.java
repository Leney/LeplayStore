package com.xd.leplay.store.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.leplay.store.R;

/**
 * 界面Loading视图
 * 
 * @author lilijun
 *
 */
public class LoadingView extends LinearLayout
{
	private CircleProgressView progressView = null;

	private TextView loadingText = null;

	public LoadingView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public LoadingView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public LoadingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public LoadingView(Context context)
	{
		super(context);
		init(context);
	}

	private void init(Context context)
	{
		LayoutParams params = new LayoutParams((int) context.getResources()
				.getDimension(R.dimen.dp31), (int) context.getResources()
				.getDimension(R.dimen.dp31));
		LayoutParams params2 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params2.topMargin = (int) context.getResources().getDimension(
				R.dimen.dp5);

		setGravity(Gravity.CENTER);

		progressView = new CircleProgressView(context);
		progressView.setLayoutParams(params);

		loadingText = new TextView(context);
		loadingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		loadingText.setText(context.getResources().getString(
				R.string.footter_loading));
		loadingText.setTextColor(Color.parseColor("#666666"));
		loadingText.setGravity(Gravity.CENTER);
		loadingText.setVisibility(View.VISIBLE);
		loadingText.setLayoutParams(params2);
		setOrientation(LinearLayout.VERTICAL);

		addView(progressView);
		addView(loadingText);
	}

	/**
	 * 设置Loading视图是否显示
	 * 
	 * @param visibily
	 */
	public void setVisibilyView(boolean visibily)
	{
		progressView.setVisibilyView(visibily);
		if (visibily)
		{
			setVisibility(View.VISIBLE);
		} else
		{
			setVisibility(View.GONE);
		}
	}
}
