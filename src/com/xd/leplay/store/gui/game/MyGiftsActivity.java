package com.xd.leplay.store.gui.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.GiftInfo;
import com.xd.leplay.store.view.MarketListView;

/**
 * 我的礼包界面
 * 
 * @author lilijun
 *
 */
public class MyGiftsActivity extends BaseActivity
{
	private MarketListView listView;

	private MyGiftAdapter adapter = null;

	private List<GiftInfo> giftInfos = null;

	@Override
	protected void initView()
	{
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_MY_GIFTS_VALUE);
		DataCollectionManager.getInstance().addRecord(action);
		
		titleView.setTitleName(getResources().getString(R.string.my_gift));
		titleView.setRightLayVisible(false);
		titleView.setBottomLineVisible(true);

		listView = new MarketListView(this);
		setCenterView(listView);

		giftInfos = new ArrayList<GiftInfo>();
		adapter = new MyGiftAdapter(this, giftInfos, action);
		listView.setAdapter(adapter);

		getListData();

		IntentFilter filter = new IntentFilter(
				Constants.ACTION_ACCOUNT_HAVE_MODIFY);
		registerReceiver(receiver, filter);
	}

	private void getListData()
	{
		List<GiftInfo> list = new ArrayList<GiftInfo>();
		for (Entry<Integer, GiftInfo> entry : LoginUserInfoManager
				.getInstance().getLoginedUserInfo().getGiftList().entrySet())
		{
			list.add(entry.getValue());
		}
		giftInfos.clear();
		giftInfos.addAll(list);
		adapter.notifyDataSetChanged();
		if (giftInfos.isEmpty())
		{
			centerViewLayout.setVisibility(View.GONE);
			errorViewLayout.setVisibility(View.VISIBLE);
			errorViewLayout.setErrorView(R.drawable.no_gifts_icon,
					getResources().getString(R.string.no_gifts), "");
			errorViewLayout.setRefrushOnClickListener(null);
		} else
		{
			loadingView.setVisibilyView(false);
			centerViewLayout.setVisibility(View.VISIBLE);
			errorViewLayout.setVisibility(View.GONE);
			listView.showEndView();
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (Constants.ACTION_ACCOUNT_HAVE_MODIFY.equals(intent.getAction()))
			{
				getListData();
			}
		}
	};

	protected void onDestroy()
	{
		unregisterReceiver(receiver);
		super.onDestroy();
	};

	public static void startMyGiftsActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				MyGiftsActivity.class), action);
	}
}
