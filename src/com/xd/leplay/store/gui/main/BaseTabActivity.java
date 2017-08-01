package com.xd.leplay.store.gui.main;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;

import com.xd.leplay.store.R;
import com.xd.leplay.store.model.PagerTabItem;
import com.xd.leplay.store.view.ChildTitleView;
import com.xd.leplay.store.widget.SimpleStaticTabPagerAdapter;
import com.xd.leplay.store.widget.SlidingTabLayout;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseTabActivity extends AppCompatActivity implements
		OnPageChangeListener
{
	/** 整个页面标题 */
	protected ChildTitleView titleView = null;
	protected ViewPager viewPager;
	private ArrayList<PagerTabItem> pagerTabItems = new ArrayList<PagerTabItem>();
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	/** 标题 */
	private ArrayList<String> titles = new ArrayList<String>();

	private Intent dataIntent;
	/** tab标签页码 */
	private int index = 0;

	/** 数据采集的界面所对应的action */
	protected String action = "";

	// private TextView titleTextView ,rightTextView;
	// private ImageView title_right_img;
	// protected TitleView titleView = null;

	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_tab);
		initView();
		addFragment(fragments);
		initIntent();
		addTitle(titles);
		init();

	}

	private void initIntent()
	{
		dataIntent = getIntent();
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
	public abstract void addFragment(List<Fragment> fragments);

	/** 修改标题 */
	public abstract void addTitle(List<String> titles);

	private void initView()
	{
		// titleView = (TitleView)
		// findViewById(R.id.base_tab_activity_title_view);
		// titleTextView = (TextView) findViewById(R.id.title_center_name);
		// rightTextView = (TextView)findViewById(R.id.title_right_text);
		// title_right_img = (ImageView)findViewById(R.id.title_right_img);
		//
		// View goBackView = findViewById(R.id.goback_layout);
		// goBackView.setOnClickListener(clickListener);
		titleView = (ChildTitleView) findViewById(R.id.base_tab_activity_title_view);
	}

	private void init()
	{

		SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		viewPager = (ViewPager) findViewById(R.id.viewpager);

		for (String title : titles)
		{
			pagerTabItems.add(new PagerTabItem(title, getResources().getColor(
					R.color.indicator_color), getResources().getColor(
					R.color.white_color)));
		}

		int screenX = getResources().getDisplayMetrics().widthPixels;
		viewPager.setAdapter(new SimpleStaticTabPagerAdapter(
				getSupportFragmentManager(), fragments, pagerTabItems));

		slidingTabLayout.setViewPager(viewPager, screenX);
		slidingTabLayout.setBackgroundColor(getResources().getColor(
				R.color.title_bg2));
		slidingTabLayout.setTabTextColor(Color.BLACK);
		slidingTabLayout.setSelectedIndicatorColors(R.color.indicator_color);
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

	// OnClickListener clickListener = new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// switch (v.getId())
	// {
	// case R.id.goback_layout:
	// finish();
	// break;
	//
	// default:
	// break;
	// }
	//
	// }
	// };

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

	// /**
	// * 设置页面主标题
	// */
	// protected void setTitle(String title)
	// {
	// titleTextView.setText(title);
	// }

	// /**
	// * 隐藏标题栏右侧
	// */
	// protected void hideRightTitleView()
	// {
	// rightTextView.setVisibility(View.GONE);
	// title_right_img.setVisibility(View.GONE);
	// }

	// /**
	// *
	// * @param rightText
	// * 右侧标题
	// * @param isShowImg
	// * 是否显示图片提示
	// */
	// protected void showRightTitleView(String rightText, boolean isShowImg)
	// {
	// rightTextView.setVisibility(View.VISIBLE);
	// if (isShowImg)
	// {
	// title_right_img.setVisibility(View.VISIBLE);
	// } else
	// {
	// title_right_img.setVisibility(View.GONE);
	// }
	// }

}
