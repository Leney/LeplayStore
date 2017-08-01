package com.xd.leplay.store.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.xd.leplay.store.widget.SlidingTabLayout;

/**
 * This class represents a tab to be displayed by {@link ViewPager} and it's
 * associated {@link SlidingTabLayout}.
 */

public class PagerTabItem {
	private final CharSequence mTitle;
	private final int mIndicatorColor;
	private final int mDividerColor;

	public PagerTabItem(CharSequence title, int indicatorColor, int dividerColor) {
		mTitle = title;
		mIndicatorColor = indicatorColor;
		mDividerColor = dividerColor;
	}

	/**
	 * @return A new {@link Fragment} to be displayed by a {@link ViewPager}
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public <T extends Fragment> T createFragment(T t)
			throws NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Class<T> c = (Class<T>) t.getClass();
		Method method = c.getMethod("newInstance", new Class[] {
				CharSequence.class, int.class, int.class });
		if (method != null) {
			return (T) method.invoke(t, mTitle, mIndicatorColor, mDividerColor);
		} else {
			return null;
		}
	}

	/**
	 * @return the title which represents this tab. In this sample this is used
	 *         directly by
	 *         {@link android.support.v4.view.PagerAdapter#getPageTitle(int)}
	 */
	public CharSequence getTitle() {
		return mTitle;
	}

	/**
	 * @return the color to be used for indicator on the
	 *         {@link SlidingTabLayout}
	 */
	public int getIndicatorColor() {
		return mIndicatorColor;
	}

	/**
	 * @return the color to be used for right divider on the
	 *         {@link SlidingTabLayout}
	 */
	public int getDividerColor() {
		return mDividerColor;
	}
}
