package com.xd.leplay.store.gui.recommend;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.main.BaseTabActivity;

/**
 * 新品首发 Tab Activity
 * 
 * @author lilijun
 *
 */
public class NewFirstActivity extends BaseTabActivity
{

	@Override
	public void addFragment(List<Fragment> fragments)
	{
		action = DataCollectionManager
				.getIntentDataCollectionAction(getIntent());

		NewFirstAppFragment applicationFragment = NewFirstAppFragment
				.getInstance(action);
		NewFirstGameFragment gameFragment = NewFirstGameFragment
				.getInstance(action);
		fragments.add(applicationFragment);
		fragments.add(gameFragment);
	}

	@Override
	public void addTitle(List<String> titles)
	{
		// 应用
		titles.add(getResources().getString(R.string.application));
		// 游戏
		titles.add(getResources().getString(R.string.game));

		// 设置主标题
		titleView.setTitleName(getResources().getString(R.string.new_first));
	}

	public static void startNewFirstActivity(Context context,
			String intentAction)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				NewFirstActivity.class), intentAction);
	}
}
