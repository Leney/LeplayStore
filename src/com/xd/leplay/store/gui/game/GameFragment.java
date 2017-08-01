package com.xd.leplay.store.gui.game;

import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.gui.main.BaseTabFragment;

/**
 * 游戏 主TAB
 * 
 * @author lilijun
 *
 */
public class GameFragment extends BaseTabFragment
{

	@Override
	public void addFragment()
	{
		action = DataCollectionConstant.DATA_COLLECTION_GAME_VALUE;

		GameBoutiqueFragment boutiqueFragment = GameBoutiqueFragment
				.getInstance(action);
		GameRankFragment rankFragment = GameRankFragment.getInstance(action);
		// GameGiftFragment giftFragment = GameGiftFragment.getInstance(action);
		GameClassifyFragment sortFragment = GameClassifyFragment
				.getInstance(action);
		fragments.add(boutiqueFragment);
		fragments.add(rankFragment);
		// fragments.add(giftFragment);
		fragments.add(sortFragment);
	}

	@Override
	public void addTitle()
	{
		// 精品
		titles.add(getActivity().getResources().getString(R.string.boutique));
		// 排行
		titles.add(getActivity().getResources().getString(R.string.rank));
		// // 礼包
		// titles.add(getActivity().getResources().getString(R.string.gift));
		// 分类
		titles.add(getActivity().getResources().getString(R.string.sort));
	}

}
