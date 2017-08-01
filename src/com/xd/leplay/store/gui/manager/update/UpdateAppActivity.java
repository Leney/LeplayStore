package com.xd.leplay.store.gui.manager.update;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.main.BaseTabActivity;

public class UpdateAppActivity extends BaseTabActivity implements
		OnPromptUpgreadeChangeListener
{

	private UpdateFragment updateFragment;

	private IgnoreFragment ignoreFragment;

	@Override
	public void addFragment(List<Fragment> fragments)
	{
		action = DataCollectionManager
				.getIntentDataCollectionAction(getIntent());

		titleView.setTitleName(getResources().getString(R.string.update_app));
		titleView.setRightLayVisible(false);
		updateFragment = UpdateFragment.getInstance(action);
		ignoreFragment = IgnoreFragment.getInstance(action);
		fragments.add(updateFragment);
		fragments.add(ignoreFragment);
	}

	@Override
	public void addTitle(List<String> titles)
	{
		// 更新
		titles.add(getResources().getString(R.string.update));
		// 忽略
		titles.add(getResources().getString(R.string.ignore));
	}

	public static void startUpdateAppActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				UpdateAppActivity.class), action);
	}

	@Override
	public void onPromptUpgreadeChange(int flag)
	{
		if (flag == 0)
		{
			// 是从更新的Fragment中进行回调回来的
			ignoreFragment.refresh();
		} else if (flag == 1)
		{
			// 是从忽略的Fragmnet中进行回调回来的
			updateFragment.refresh();
		}
	}

	/**
	 * 滑动到指定页
	 * 
	 * @param postion
	 */
	public void setCurPage(int postion)
	{
		if (postion < viewPager.getChildCount())
		{
			viewPager.setCurrentItem(postion);
		}
	}
}
