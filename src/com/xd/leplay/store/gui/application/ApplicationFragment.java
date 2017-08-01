package com.xd.leplay.store.gui.application;

import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.gui.game.GameBoutiqueFragment;
import com.xd.leplay.store.gui.main.BaseTabFragment;

/**
 * 应用 主TAB
 * 
 * @author lilijun
 *
 */
public class ApplicationFragment extends BaseTabFragment
{

	@Override
	public void addFragment()
	{
		action = DataCollectionConstant.DATA_COLLECTION_APPLICATION_VALUE;

		ApplicationBoutiqueFragment boutiqueFragment = ApplicationBoutiqueFragment
				.getInstance(action);
		GameBoutiqueFragment gameFragment = GameBoutiqueFragment
				.getInstance(action);
		ApplicationRankFragment rankFragment = ApplicationRankFragment
				.getInstance(action);
		// ApplicationNecessaryFragment necessaryFragment =
		// ApplicationNecessaryFragment
		// .getInstance(action);
		// ApplicationClassifyFragment sortFragment =
		// ApplicationClassifyFragment
		// .getInstance(action);
		fragments.add(boutiqueFragment);
		fragments.add(gameFragment);
		// fragments.add(necessaryFragment);
		fragments.add(rankFragment);
	}

	@Override
	public void addTitle()
	{
		// 应用
		titles.add(getActivity().getResources().getString(R.string.application));
		// 游戏
		titles.add(getActivity().getResources().getString(R.string.game));
		// // 必备
		// titles.add(getActivity().getResources().getString(R.string.necessary));
		// 排行
		titles.add(getActivity().getResources().getString(R.string.rank));
	}

}
