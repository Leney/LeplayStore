package com.xd.leplay.store.gui.treasure;

import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.main.BaseTabFragment;

/**
 * 下载奖励 Fragment
 * 
 * @author luoxingxing
 *
 */
public class DownloadRewardFragment extends BaseTabFragment
{

	@Override
	public void addFragment()
	{
		DownloadRecodFragment downloadRecodFragment = DownloadRecodFragment
				.getInstance(DataCollectionManager
						.getIntentDataCollectionAction(getActivity().getIntent()));
		DownloadRankFragment downloadRankFragment = DownloadRankFragment
				.getInstance(DataCollectionManager
						.getIntentDataCollectionAction(getActivity().getIntent()));
		
		fragments.add(downloadRecodFragment);
		fragments.add(downloadRankFragment);
	
		
	}

	@Override
	public void addTitle()
	{
		//下载记录
		titles.add(getActivity().getResources().getString(R.string.download_reward_record));
		// 下载排行榜
		titles.add(getActivity().getResources().getString(R.string.download_reward_rank));
		
	}

}
