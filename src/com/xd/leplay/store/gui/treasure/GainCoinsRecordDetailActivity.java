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
import com.xd.leplay.store.model.proto.Uac.Detail;
import com.xd.leplay.store.model.proto.Uac.DetailContent;
import com.xd.leplay.store.model.proto.Uac.ReqWealthDetail;
import com.xd.leplay.store.model.proto.Uac.RspWealthDetail;
import com.xd.leplay.store.view.LoadMoreScrollListener;
import com.xd.leplay.store.view.LoadMoreScrollListener.setOnScrollToEndListener;
import com.xd.leplay.store.view.LoadMoreView;
import com.xd.leplay.store.view.MarketExpandableListView;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 获取金币记录界面
 * 
 * @author luoxingxing
 *
 */
public class GainCoinsRecordDetailActivity extends BaseActivity
{
	private static final String TAG = "GainCoinsRecordDetailActivity";

	private MarketExpandableListView listView = null;

	private GainCoinRecordDetailAdapter adapter = null;

	/** 按天排列的获取金币记录集合 */
	// private List<Detail> recordDetailList = null;

	private List<Detail> groups = null;

	private List<List<DetailContent>> childs = null;

	/** 列表一次加载的长度 */
	private static final int LOAD_DATA_SIZE = 10;

	private LoadMoreView loadMoreView = null;

	/** 获取赚取金币记录请求的TAG */
	private final String GAIN_COINS_RECORD_REQUEST_TAG = "ReqWealthDetail";

	/** 获取赚取金币记录响应的TAG */
	private final String GAIN_COINS_RECORD_RSPONSE_TAG = "RspWealthDetail";

	/** 获取赚取记录的type */
	private String type = "";

	@Override
	protected void initView()
	{
		// 0---任务下载收入详情，1---签到抽奖收入详情，2---邀请奖励收入详情，3---其它奖励收入详情，4----红包口令收入
		String newAction = "";
		int incomeType = getIntent().getIntExtra("incomeDetailType", 0);
		switch (incomeType)
		{
		case 0:
			newAction = DataCollectionConstant.DATA_COLLECTION_DOWNLOAD_AND_TASK_INCOME_VALUE;
			titleView.setTitleName(getResources().getString(
					R.string.download_task_income));
			type = "4,8,9";
			break;
		case 1:
			newAction = DataCollectionConstant.DATA_COLLECTION_SIGN_AND_LOTTERY_INCOME_VALUE;
			titleView.setTitleName(getResources().getString(
					R.string.sign_lottery_income));
			type = "2,10";
			break;
		case 2:
			newAction = DataCollectionConstant.DATA_COLLECTION_INVITE_FRIENDS_INCOME_VALUE;
			titleView.setTitleName(getResources().getString(
					R.string.invite_friend_income));
			type = "3,11,12";
			break;
		case 3:
			newAction = DataCollectionConstant.DATA_COLLECTION_OTHER_INCOME_VALUE;
			titleView.setTitleName(getResources().getString(
					R.string.other_reward_income));
			type = "1,5,6,7,13";
			break;
		case 4:
			titleView.setTitleName(getResources().getString(
					R.string.red_packet_password_income));
			type = "13";
			break;
		}
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()), newAction);
		DataCollectionManager.getInstance().addRecord(action);

		titleView.setRightLayVisible(false);
		titleView.setBottomLineVisible(true);

		listView = new MarketExpandableListView(this);
		listView.setDividerHeight(0);
		listView.setBackgroundColor(Color.parseColor("#f8f8f8"));
		setCenterView(listView);
		groups = new ArrayList<Detail>();
		childs = new ArrayList<List<DetailContent>>();
		// recordDetailList = new ArrayList<Detail>();

		adapter = new GainCoinRecordDetailAdapter(
				GainCoinsRecordDetailActivity.this, groups, childs,type);
		listView.setOnScrollListener(new LoadMoreScrollListener(ImageLoader
				.getInstance(), true, true, new setOnScrollToEndListener()
		{
			@Override
			public void loadMoreWhenScrollToEnd()
			{
				// if (isHaveNextPageData)
				// {
				loadMoreData();
				// } else
				// {
				// listView.setOnScrollListener(null);
				// listView.removeFooterView(loadMoreView);
				// listView.showEndView();
				// }
			}
		}));

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
		listView.addFooterView(loadMoreView);

		doLoadData(
				Constants.UAC_API_URL,
				new String[]
				{ GAIN_COINS_RECORD_REQUEST_TAG },
				new ByteString[]
				{ getCoinsRecordRequestData(LoginUserInfoManager.getInstance()
						.getLoginedUserInfo().getUserId(), LoginUserInfoManager
						.getInstance().getLoginedUserInfo().getUserToken(),
						((groups.size() / LOAD_DATA_SIZE) + 1), LOAD_DATA_SIZE,
						type) }, "");
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<String> actions = rspPacket.getActionList();
		for (String actionNet : actions)
		{
			if (GAIN_COINS_RECORD_RSPONSE_TAG.equals(actionNet))
			{
				// 列表数据返回
				try
				{
					RspWealthDetail rspWealthDetail = RspWealthDetail
							.parseFrom(rspPacket.getParams(0));
					// 0=成功,1=系统响应错误,2=userToken错误
					if (rspWealthDetail.getRescode() == 0)
					{
						List<Detail> emptyDetails = rspWealthDetail
								.getDetailList();
						if (emptyDetails != null && !emptyDetails.isEmpty())
						{
							// recordDetailList.addAll(rspWealthDetail
							// .getDetailList());
							for (Detail detail : emptyDetails)
							{
								groups.add(detail);
								childs.add(detail.getContentList());
							
							}

							if (emptyDetails.size() < LOAD_DATA_SIZE)
							{
								listView.setOnScrollListener(null);
								listView.removeFooterView(loadMoreView);
								listView.showEndView();
							}
						} else
						{
							listView.setOnScrollListener(null);
							listView.removeFooterView(loadMoreView);
							listView.showEndView();
						}
						DLog.i("lilijun", "rewardDetailList.size()--------->>>"
								+ groups.size() + "-------->>>" + childs.size());

						// if (recordDetailList.isEmpty()
						// || recordDetailList.size() < LOAD_DATA_SIZE)
						// {
						// listView.setOnScrollListener(null);
						// listView.removeFooterView(loadMoreView);
						// listView.showEndView();
						// }
						if (!groups.isEmpty())
						{
							if (listView.getAdapter() == null)
							{
								listView.setAdapter(adapter);
							} else
							{
								adapter.notifyDataSetChanged();
							}
							// if (recordDetailList.size() < LOAD_DATA_SIZE)
							// {
							// // 表明是第一次加载列表数据
							// listView.setAdapter(adapter);
							// } else
							// {
							// // 表明是加载下一页返回了数据
							// adapter.notifyDataSetChanged();
							// }
							// 展开所有item
							for (int i = 0; i < adapter.getGroupCount(); i++)
							{
								listView.expandGroup(i);
							}
						} else
						{
							showErrorView();
							// 当前没有赚取金币的记录
							errorViewLayout.setErrorView(
									R.drawable.no_gain_coin_record_img,
									getResources().getString(
											R.string.no_gain_coins_detail), "");
							errorViewLayout.setRefrushOnClickListener(null);
						}
					} else if (rspWealthDetail.getRescode() == 2)
					{
						// userToken错误
						// 退出登录
						LoginUserInfoManager.getInstance().exitLogin();
						Toast.makeText(GainCoinsRecordDetailActivity.this,
								getResources().getString(R.string.re_login),
								Toast.LENGTH_SHORT).show();
						LoginActivity.startLoginActivity(
								GainCoinsRecordDetailActivity.this, action);
						finish();
					} else
					{
						DLog.e("lilijun", rspWealthDetail.getResmsg() + "--->>"
								+ rspWealthDetail.getRescode());
						showErrorView();
					}
				} catch (Exception e)
				{
					DLog.e(TAG, "解析赚取金币记录发生异常#exception：", e);
				}
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		if (!groups.isEmpty())
		{
			loadMoreView.setNetErrorVisible(true);
		} else
		{
			showErrorView();
			errorViewLayout.showLoadFailedLay();
		}
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		if (!groups.isEmpty())
		{
			loadMoreView.setNetErrorVisible(true);
		} else
		{
			showErrorView();
			errorViewLayout.showLoadFailedLay();
		}
	}

	@Override
	protected void tryAgain()
	{
		super.tryAgain();
		doLoadData(
				Constants.UAC_API_URL,
				new String[]
				{ GAIN_COINS_RECORD_REQUEST_TAG },
				new ByteString[]
				{ getCoinsRecordRequestData(LoginUserInfoManager.getInstance()
						.getLoginedUserInfo().getUserId(), LoginUserInfoManager
						.getInstance().getLoginedUserInfo().getUserToken(),
						((groups.size() / LOAD_DATA_SIZE) + 1), LOAD_DATA_SIZE,
						type) }, "");
	}

	/**
	 * 加载列表下一页数据
	 */
	private void loadMoreData()
	{
		doLoadData(
				Constants.UAC_API_URL,
				new String[]
				{ GAIN_COINS_RECORD_REQUEST_TAG },
				new ByteString[]
				{ getCoinsRecordRequestData(LoginUserInfoManager.getInstance()
						.getLoginedUserInfo().getUserId(), LoginUserInfoManager
						.getInstance().getLoginedUserInfo().getUserToken(),
						((groups.size() / LOAD_DATA_SIZE) + 1), LOAD_DATA_SIZE,
						type) }, "");
	}

	/**
	 * 获取赚取金币记录的请求数据
	 * 
	 * @param userId
	 * @param userToken
	 * @param index
	 * @param size
	 * @return
	 */
	private ByteString getCoinsRecordRequestData(int userId, String userToken,
			int index, int size, String type)
	{
		ReqWealthDetail.Builder builder = ReqWealthDetail.newBuilder();
		builder.setUid(userId);
		builder.setUserToken(userToken);
		builder.setPageIndex(index);
		builder.setPageSize(size);
		builder.setType(type);
		return builder.build().toByteString();
	}

	public static void startGainCoinsRecordDetailActivity(Context context,
			String action, int incomeDetailType)
	{
		// 0---任务下载收入详情，1---签到抽奖收入详情，2---邀请奖励收入详情，3---其它奖励收入详情,4---红包口令收入
		Intent intent = new Intent(context, GainCoinsRecordDetailActivity.class);
		intent.putExtra("incomeDetailType", incomeDetailType);
		DataCollectionManager.startActivity(context, intent, action);
	}

}
