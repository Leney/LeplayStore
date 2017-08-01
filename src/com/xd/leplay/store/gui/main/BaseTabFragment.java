package com.xd.leplay.store.gui.main;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xd.leplay.store.R;
import com.xd.leplay.store.model.PagerTabItem;
import com.xd.leplay.store.widget.SimpleStaticTabPagerAdapter;
import com.xd.leplay.store.widget.SlidingTabLayout;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("NewApi")
public abstract class BaseTabFragment extends Fragment implements
		OnPageChangeListener
{
	protected ViewPager viewPager;
	private ArrayList<PagerTabItem> pagerTabItems = new ArrayList<PagerTabItem>();
	@SuppressLint("NewApi")
	protected ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	/** 标题 */
	protected ArrayList<String> titles = new ArrayList<String>();

	private Intent dataIntent;
	/** tab标签页码 */
	private int index = 0;

	/** 数据采集的界面所对应的action */
	protected String action = "";

	// private TextView titleTextView ,rightTextView;
	// private ImageView title_right_img;
	// protected TitleView titleView = null;

	/** 当前页面的标识值 */
	private String currentAction = "-1";

	@Override
	public void onResume()
	{
		super.onResume();
		int lastIndex = action.lastIndexOf("-");
		if (lastIndex != -1)
		{
			// 不是一级界面
			currentAction = action.substring(lastIndex + 1);
		} else
		{
			// 只是一级界面
			currentAction = action;
		}
		MobclickAgent.onPageStart(currentAction);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		MobclickAgent.onPageEnd(currentAction);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_multi_tab, container,
				false);
		initView(view);
		addFragment();
		initIntent();
		addTitle();
		init(view);
		// TODO Auto-generated method stub
		return view;

	}

	private void initIntent()
	{
		dataIntent = new Intent();
		if (dataIntent.hasExtra("titles"))
		{
			titles = dataIntent.getStringArrayListExtra("titles");
		}
		if (dataIntent.hasExtra("index"))
		{
			index = dataIntent.getIntExtra("index", 0);
		}
	}

	/** 将Fragment添加到fragments中 */
	public abstract void addFragment();

	/** 添加标题 */
	public abstract void addTitle();

	private void initView(View v)
	{
		// titleView = (TitleView)
		// v.findViewById(R.id.base_tab_activity_title_view);
	}

	private void init(View v)
	{

		SlidingTabLayout slidingTabLayout = (SlidingTabLayout) v
				.findViewById(R.id.sliding_tabs);
		viewPager = (ViewPager) v.findViewById(R.id.viewpager);

		for (String title : titles)
		{
			pagerTabItems.add(new PagerTabItem(title, getResources().getColor(
					R.color.indicator_color), getResources().getColor(
					R.color.white_color)));
		}
		int screenX = getResources().getDisplayMetrics().widthPixels;
		viewPager.setAdapter(new SimpleStaticTabPagerAdapter(
				getChildFragmentManager(), fragments, pagerTabItems));

		slidingTabLayout.setViewPager(viewPager, screenX);
		//设置滑动指示器的背景色
		slidingTabLayout.setBackgroundColor(getResources().getColor(
				R.color.title_bg2));
		slidingTabLayout.setTabTextColor(Color.BLACK);
		//设置滑动指示器的横条颜色
		slidingTabLayout.setSelectedIndicatorColors(R.color.indicator_color);
		// slidingTabLayout.setTabBackground(R.drawable.tab_indicator_bg);
		slidingTabLayout.setOnPageChangeListener(this);
		slidingTabLayout
				.setCustomTabColorizer(new SlidingTabLayout.TabColorizer()
				{

					public int getIndicatorColor(int position)
					{
						return pagerTabItems.get(position).getIndicatorColor();
					}

					public int getDividerColor(int position)
					{
						return pagerTabItems.get(position).getDividerColor();
					}
				});

		viewPager.setCurrentItem(index);

	}

	@Override
	public void onPageScrollStateChanged(int arg0)
	{
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{

	}

	@Override
	public void onPageSelected(int index)
	{

	}

}
