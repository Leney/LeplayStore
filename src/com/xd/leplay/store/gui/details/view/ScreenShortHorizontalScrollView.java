package com.xd.leplay.store.gui.details.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.gui.details.ScreenShortDetailActivity;
import com.xd.leplay.store.util.DisplayUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ScreenShortHorizontalScrollView extends HorizontalScrollView
{
	private ImageLoaderManager imageLoaderManager;

	private ViewGroup[] viewGroups;

	private Context context;

	private ImageView icon;

	private DisplayImageOptions options;

	private ArrayList<String> urls;
	
	private String action="";
	
	
	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public ScreenShortHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle)
	{
		super(context, attrs, defStyle);
		this.context = context;
		init(context);
	}

	public ScreenShortHorizontalScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		init(context);
	}

	public ScreenShortHorizontalScrollView(Context context)
	{
		super(context);
		this.context = context;
		init(context);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		if (viewGroups != null)
		{
			for (int i = 0; i < viewGroups.length; i++)
			{
				viewGroups[i].requestDisallowInterceptTouchEvent(true);
			}

		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if (viewGroups != null)
		{
			for (int i = 0; i < viewGroups.length; i++)
			{
				viewGroups[i].requestDisallowInterceptTouchEvent(true);
			}

		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (viewGroups != null)
		{
			for (int i = 0; i < viewGroups.length; i++)
			{
				viewGroups[i].requestDisallowInterceptTouchEvent(true);
			}

		}
		return super.onTouchEvent(event);
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	private void init(Context context)
	{
		setHorizontalScrollBarEnabled(false);
		urls = new ArrayList<String>();
		imageLoaderManager = ImageLoaderManager.getInstance();
		options = DisplayUtil.getScreenShortImageLoaderOptions();
		isInEditMode();
	}

	/**
	 * 设置你可能喜欢的数据
	 * 
	 * @param context
	 * @param appInfos
	 * @param father
	 *            父类viewpager
	 */
	public void setUrls(List<String> urls, ViewGroup... viewGroups)
	{
		if (urls == null)
		{
			return;
		} else if (urls.isEmpty())
		{
			return;
		}
		this.urls.addAll(urls);

		this.viewGroups = viewGroups;
		int length = urls.size();
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setGravity(Gravity.CENTER_VERTICAL);

		final int leftSpace = (int) getResources().getDimension(R.dimen.dp5);
		final int leftSpace2 = (int) getResources().getDimension(R.dimen.dp8);

		for (int i = 0; i < length; i++)
		{
			View view = View.inflate(context,
					R.layout.details_screenshort_adapter, null);
			icon = (ImageView) view
					.findViewById(R.id.details_screenshort_adapter_img);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			if (i == 0)
			{
				params.leftMargin = leftSpace;
			} else
			{
				params.leftMargin = leftSpace2;
			}

			if (i == length - 1)
			{
				params.rightMargin = leftSpace;
			}

			view.setLayoutParams(params);
			linearLayout.addView(view);

			view.setTag(i);
			view.setOnClickListener(itemOnClickListener);
			imageLoaderManager.displayImage(urls.get(i), icon, options);
		}

		removeAllViews();
		addView(linearLayout);
	}

	private OnClickListener itemOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			int position = (Integer) v.getTag();
			Intent intent = new Intent(context, ScreenShortDetailActivity.class);
			intent.putExtra("position", position);
			intent.putStringArrayListExtra("urls", (ArrayList<String>) urls);
			ScreenShortDetailActivity.startScreenShortDetailActivity(context, position, urls, action);
		}
	};

	public void onDestroy()
	{
		// if (download != null)
		// {
		// download.onDestory();
		// }
	}

}
