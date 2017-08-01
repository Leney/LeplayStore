package com.xd.leplay.store.widget;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.xd.leplay.store.model.PagerTabItem;


/**带Tab功能的ViewPager适配器*/
public class SimpleStaticTabPagerAdapter extends StaticFragmentPagerAdapter {
	private ArrayList<Fragment> fragments;
	private ArrayList<PagerTabItem> pagerTabItems;

	public SimpleStaticTabPagerAdapter(FragmentManager fm,
			ArrayList<Fragment> fragments, ArrayList<PagerTabItem> pagerTabItems) {
		super(fm);
		this.fragments = fragments;
		this.pagerTabItems = pagerTabItems;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return pagerTabItems.size();
	}

	// BEGIN_INCLUDE (pageradapter_getpagetitle)
	/**
	 * Return the title of the item at {@code position}. This is important as
	 * what this method returns is what is displayed in the
	 * {@link SlidingTabLayout}.
	 * <p>
	 * Here we return the value returned from {@link SamplePagerItem#getTitle()}.
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		return pagerTabItems.get(position).getTitle();
	}

}
