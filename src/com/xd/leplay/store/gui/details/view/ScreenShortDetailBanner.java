package com.xd.leplay.store.gui.details.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.util.DisplayUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ScreenShortDetailBanner extends RelativeLayout
{
	private ViewPager vp;
	/** 底部指示器显示区域 */
	private LinearLayout indicatorLay;
	private LayoutParams lp;
	private List<String> showResults;
	/** 底部圆形指示点 */
	private View[] circlePointViews = null;

	private ImageLoaderManager imageLoaderManager = null;

	public void initData(List<String> pResult)
	{
		showResults = pResult;
	}

	@SuppressLint("NewApi")
	public ScreenShortDetailBanner(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public ScreenShortDetailBanner(Context context, AttributeSet attrs,
			int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	public ScreenShortDetailBanner(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ScreenShortDetailBanner(Context context)
	{
		super(context);
	}

	public void initView(Context context)
	{
		imageLoaderManager = ImageLoaderManager.getInstance();

		// WindowManager wm = (WindowManager) getContext().getSystemService(
		// Context.WINDOW_SERVICE);
		// int height = wm.getDefaultDisplay().getHeight();
		// 整个显示banner的Viewpager
		// lp = new LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.px2dip(
		// context, 300));
		// lp = new LayoutParams(LayoutParams.MATCH_PARENT,
		// (int) (0.15625 * height));
		vp = new ViewPager(context);
		// vp.setLayoutParams(lp);

		// 指示器
		lp = new LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(
				context, 30));
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lp.bottomMargin = (int) context.getResources().getDimension(
				R.dimen.banner_proint_bottom_margin);
		indicatorLay = new LinearLayout(context);
		indicatorLay.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		indicatorLay.setLayoutParams(lp);
		// 真正的整个指示器部分
		LinearLayout realIndicatorLay = new LinearLayout(context);
		LinearLayout.LayoutParams indicatorLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		realIndicatorLay.setLayoutParams(indicatorLayoutParams);
		circlePointViews = new View[showResults.size()];

		// 指示器大小
		LinearLayout.LayoutParams indicatorItemParams = new LinearLayout.LayoutParams(
				(int) context.getResources().getDimension(
						R.dimen.banner_proint_width), (int) context
						.getResources().getDimension(
								R.dimen.banner_proint_height));
		indicatorItemParams.leftMargin = DisplayUtil.dip2px(context, 2);
		indicatorItemParams.rightMargin = DisplayUtil.dip2px(context, 2);
		for (int i = 0; i < circlePointViews.length; i++)
		{
			circlePointViews[i] = new View(context);
			// view.setLayoutParams(circleLayoutParams);
			if (i == 0)
			{
				// 初始化时 将第一个设置为选择状态
				circlePointViews[i]
						.setBackgroundResource(R.drawable.banner_proint_select_bg_shape);
			} else
			{
				circlePointViews[i]
						.setBackgroundResource(R.drawable.banner_proint_normal_bg_shape);
			}
			circlePointViews[i].setLayoutParams(indicatorItemParams);
			realIndicatorLay.addView(circlePointViews[i]);
		}
		indicatorLay.addView(realIndicatorLay);
		adapter = new ViewPagerAdapter(context, showResults);
		vp.setAdapter(adapter);
		vp.setOnPageChangeListener(new MyPageChangeListener());
		addView(vp);
		// addView(centerLayout);
		addView(indicatorLay);

	}

	/**
	 * 设置显示第几页界面
	 * 
	 * @param position
	 */
	public void setCurrentItem(int position)
	{
		vp.setCurrentItem(position);
	}

	public ViewPagerAdapter adapter;

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 */
	private class MyPageChangeListener implements OnPageChangeListener
	{
		public void onPageSelected(int position)
		{
			// 获取新的位置
			for (int i = 0; i < circlePointViews.length; i++)
			{
				if (i == position)
				{
					circlePointViews[i]
							.setBackgroundResource(R.drawable.banner_proint_select_bg_shape);
				} else
				{
					circlePointViews[i]
							.setBackgroundResource(R.drawable.banner_proint_normal_bg_shape);
				}
			}
		}

		public void onPageScrollStateChanged(int arg0)
		{
		}

		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
		}
	}

	private class ViewPagerAdapter extends PagerAdapter
	{

		List<String> lists;
		Context ct;
		private ImageView imgView;
		private int currentPosition = 0;
		private DisplayImageOptions options;

		private RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		public ViewPagerAdapter(Context ct, List<String> mList)
		{
			lists = mList;
			this.ct = ct;
			options = DisplayUtil.getBannerImageLoaderOptions();
		}

		/**
		 * 获得页面的总数
		 */
		public int getCount()
		{
			return lists.size();
		}

		/**
		 * 获得相应位置上的view container view的容器
		 */
		public Object instantiateItem(ViewGroup container, final int position)
		{
			imgView = new ImageView(ct);
			imgView.setScaleType(ImageView.ScaleType.FIT_XY);
			// imgView.setLayoutParams(params);
			if (lists.size() == 0)
			{

			} else
			{
				// 写自己的逻辑
				currentPosition = position % lists.size();

				imageLoaderManager.displayImage(lists.get(currentPosition),
						imgView, options);
			}

			// 给 container 添加一个view
			container.addView(imgView);
			container.setLayoutParams(params);
			imgView.setOnClickListener(itemOnClickListener);
			// 返回一个和该view相对的object
			return imgView;
		}

		/**
		 * 判断 view和object的对应关系
		 */
		public boolean isViewFromObject(View view, Object object)
		{
			if (view == object)
			{
				return true;
			} else
			{
				return false;
			}
		}

		/**
		 * 销毁对应位置上的object
		 */
		public void destroyItem(ViewGroup container, int position, Object object)
		{

			container.removeView((View) object);
			object = null;
		}

		@Override
		public int getItemPosition(Object object)
		{
			return super.getItemPosition(object);
		}

	}

	private OnClickListener itemOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			((Activity) v.getContext()).finish();
		}
	};
}
