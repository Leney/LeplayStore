package com.xd.leplay.store.gui.treasure;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqWithdrawingList;
import com.xd.leplay.store.model.proto.Uac.RspWithdrawingList;
import com.xd.leplay.store.model.proto.Uac.WithdrawingInfo;
import com.xd.leplay.store.view.LoadMoreScrollListener;
import com.xd.leplay.store.view.LoadMoreScrollListener.setOnScrollToEndListener;
import com.xd.leplay.store.view.LoadMoreView;
import com.xd.leplay.store.view.MarketListView;
import com.google.protobuf.ByteString;

/**
 * 提现记录界面
 * 
 * @author lilijun
 *
 */
public class GotCashRewardActivity extends BaseActivity
{
	private static final String TAG = "GotCashRewardActivity";

	private MarketListView listView;

	private GotCashRewardAdapter adapter;

	private List<WithdrawingInfo> rewardList;

	private LoadMoreView loadMoreView = null;

	private boolean isHaveNextPageData = true;

	private static final String GOT_CASH_REWARD_REQUEST_TAG = "ReqWithdrawingList";

	private static final String GOT_CASH_REWARD_RESPONSE_TAG = "RspWithdrawingList";

	private static final int LOAD_SIZE = 20;

	@Override
	protected void initView()
	{
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_GOT_CASH_REWARDS_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		titleView.setTitleName(getResources().getString(R.string.got_reward));
		centerViewLayout.setBackgroundColor(Color.parseColor("#f8f8f8"));
		listView = new MarketListView(this);
		listView.setDividerHeight(0);
		setCenterView(listView);
		rewardList = new ArrayList<WithdrawingInfo>();
		adapter = new GotCashRewardAdapter(this, rewardList);

		loadMoreView = new LoadMoreView(this);
		loadMoreView.setNetErrorViewOnClickLinstener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				loadMoreView.setLoadingVisible(true);
				loadMoreData();
			}
		});

		listView.setOnScrollListener(new LoadMoreScrollListener(null, true,
				true, new setOnScrollToEndListener()
				{
					@Override
					public void loadMoreWhenScrollToEnd()
					{
						loadMoreData();
					}
				}));
		listView.addFooterView(loadMoreView);

		doLoadData(Constants.UAC_API_URL, new String[]
		{ GOT_CASH_REWARD_REQUEST_TAG }, new ByteString[]
		{ getRequestData(0, LOAD_SIZE) }, "");
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<ByteString> byteList = rspPacket.getParamsList();
		for (int i = 0; i < byteList.size(); i++)
		{
			String action = rspPacket.getAction(i);
			if (action.equals(GOT_CASH_REWARD_RESPONSE_TAG))
			{
				parseListResult(byteList.get(i));
				if (!isHaveNextPageData)
				{
					listView.setOnScrollListener(null);
					listView.removeFooterView(loadMoreView);
					listView.showEndView();
				}
			}
		}

		if (!rewardList.isEmpty())
		{
			if (listView.getAdapter() == null)
			{
				listView.setAdapter(adapter);
			} else
			{
				adapter.notifyDataSetChanged();
			}
		} else
		{
			// 没有提现记录
			DLog.i("lilijun", "没有提现记录");
			errorViewLayout.setErrorView(R.drawable.no_update_and_no_ignore,
					getResources().getString(R.string.no_reward), "");
			errorViewLayout.setRefrushOnClickListener(null);
			showErrorView();
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		if (!rewardList.isEmpty())
		{
			loadMoreView.setNetErrorVisible(true);
		} else
		{
			loadingView.setVisibilyView(false);
			centerViewLayout.setVisibility(View.GONE);
			errorViewLayout.setVisibility(View.VISIBLE);
			errorViewLayout.showLoadFailedLay();
		}
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		if (!rewardList.isEmpty())
		{
			loadMoreView.setNetErrorVisible(true);
		} else
		{
			loadingView.setVisibilyView(false);
			centerViewLayout.setVisibility(View.GONE);
			errorViewLayout.setVisibility(View.VISIBLE);
			errorViewLayout.showLoadFailedLay();
		}
	}

	@Override
	protected void tryAgain()
	{
		super.tryAgain();
		doLoadData(Constants.UAC_API_URL, new String[]
		{ GOT_CASH_REWARD_REQUEST_TAG }, new ByteString[]
		{ getRequestData(0, LOAD_SIZE) }, "");
	}

	/**
	 * 加载列表下一页数据
	 */
	private void loadMoreData()
	{
		doLoadData(Constants.UAC_API_URL, new String[]
		{ GOT_CASH_REWARD_REQUEST_TAG }, new ByteString[]
		{ getRequestData(rewardList.size() / LOAD_SIZE, LOAD_SIZE) }, "");
	}

	private ByteString getRequestData(int index, int size)
	{
		ReqWithdrawingList.Builder builder = ReqWithdrawingList.newBuilder();
		builder.setUid(LoginUserInfoManager.getInstance().getLoginedUserInfo()
				.getUserId());
		builder.setUserToken(LoginUserInfoManager.getInstance()
				.getLoginedUserInfo().getUserToken());
		builder.setPageIndex(index);
		builder.setPageSize(size);
		return builder.build().toByteString();
	}

	/**
	 * 解析列表返回结果
	 * 
	 * @param rspPacket
	 */
	private void parseListResult(ByteString byteString)
	{
		try
		{
			RspWithdrawingList rspWithdrawingList = RspWithdrawingList
					.parseFrom(byteString);
			// 0=成功,1=失败,2=用户不存在或已经被禁用,3=token错误
			if (rspWithdrawingList.getRescode() == 0)
			{
				// 成功
				List<WithdrawingInfo> rewards = rspWithdrawingList
						.getWithdrawingInfoList();
				if (rewards.size() == 0 || rewards.isEmpty()
						|| rewards.size() < LOAD_SIZE)
				{
					// 没有下一页数据了
					isHaveNextPageData = false;
				}
				rewardList.addAll(rewards);
			} else if (rspWithdrawingList.getRescode() == 3)
			{
				// userToken错误
				// 退出登录
				LoginUserInfoManager.getInstance().exitLogin();
				Toast.makeText(GotCashRewardActivity.this,
						getResources().getString(R.string.re_login),
						Toast.LENGTH_SHORT).show();
				LoginActivity.startLoginActivity(GotCashRewardActivity.this,
						action);
				finish();
			} else
			{
				Toast.makeText(GotCashRewardActivity.this,
						rspWithdrawingList.getResmsg(), Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "parseListResult()#Excepton:", e);
		}
	}

	public static void startGotCashRewardActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				GotCashRewardActivity.class), action);
	}

}
