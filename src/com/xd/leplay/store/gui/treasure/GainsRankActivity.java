package com.xd.leplay.store.gui.treasure;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.RankInfo;
import com.xd.leplay.store.model.proto.Uac.ReqRank;
import com.xd.leplay.store.model.proto.Uac.RspRank;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.view.CustomImageView;
import com.xd.leplay.store.view.MarketListView;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 收入排行界面
 * 
 * @author lilijun
 *
 */
public class GainsRankActivity extends BaseActivity
{

	private final String TAG = "GainsRankActivity";

	private MarketListView listView;

	/** 顶在上面不动的部分 */
	private LinearLayout topTitleLay;

	private GainsRankAdapter adapter;

	/** 排行数据源 */
	private List<RankInfo> rankInfos;

	/** 前3名的RankInfo */
	private List<RankInfo> top3RankInfos;

	/** 用户自己的RankInfo对象 */
	private RankInfo userRankInfo = null;

	private View headerView;

	/** 前三的用户头像显示控件 */
	private CustomImageView[] headerTop3UserIcons = new CustomImageView[3];

	/** 前三的用户名称 */
	private TextView[] headerTop3UserName = new TextView[3];

	/** 前三的用户提现金额 */
	private TextView[] headerTop3UserMoney = new TextView[3];

	/** 用户自己的排名(我的排名)、用户自己的排名称号(我的称号) */
	private TextView userRankNum, userRankName;

	/** 用户自己的排名信息显示部分 */
	private LinearLayout userInfoLay;

	/** 收入排行的请求TAG */
	private static String GET_INCOME_RANK_REQUEST_TAG = "ReqRank";

	/** 收入排行的响应TAG */
	private static String GET_INCOME_RANK_RSPONSE_TAG = "RspRank";

	private LoginUserInfoManager userInfoManager = null;

	private LoginedUserInfo userInfo = null;

	private ImageLoaderManager imageLoaderManager = null;

	private DisplayImageOptions options = null;

	@Override
	protected void initView()
	{
		action = DataCollectionManager
				.getAction(
						DataCollectionManager
								.getIntentDataCollectionAction(getIntent()),
						DataCollectionConstant.DATA_COLLECTION_TREASURE_IN_COME_RANK_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		titleView.setTitleName(getResources().getString(
				R.string.treasure_income_rank));
		titleView.setRightLayVisible(false);
		titleView.setBottomLineVisible(false);
		loadingView.setVisibilyView(true);
		centerViewLayout.setVisibility(View.GONE);
		errorViewLayout.setVisibility(View.GONE);

		userInfoManager = LoginUserInfoManager.getInstance();
		imageLoaderManager = ImageLoaderManager.getInstance();
		options = DisplayUtil.getUserIconImageLoaderOptions();

		setCenterView(R.layout.gains_rank_activity);
		listView = (MarketListView) findViewById(R.id.gains_rank_listview);
		topTitleLay = (LinearLayout) findViewById(R.id.gains_rank_top_lay);
		topTitleLay.setVisibility(View.GONE);

		top3RankInfos = new ArrayList<RankInfo>();
		rankInfos = new ArrayList<RankInfo>();
		adapter = new GainsRankAdapter(GainsRankActivity.this, rankInfos);

		headerView = View.inflate(GainsRankActivity.this,
				R.layout.gain_rank_header, null);
		headerTop3UserIcons[0] = (CustomImageView) headerView
				.findViewById(R.id.gain_rank_first_user_icon);
		headerTop3UserIcons[1] = (CustomImageView) headerView
				.findViewById(R.id.gain_rank_second_user_icon);
		headerTop3UserIcons[2] = (CustomImageView) headerView
				.findViewById(R.id.gain_rank_third_user_icon);
		// headerTop3UserIcons[0].setOuterRingData("#dddddd", 5);
		// headerTop3UserIcons[1].setOuterRingData("#dddddd", 5);
		// headerTop3UserIcons[2].setOuterRingData("#dddddd", 5);

		headerTop3UserName[0] = (TextView) headerView
				.findViewById(R.id.gain_rank_first_user_name);
		headerTop3UserName[1] = (TextView) headerView
				.findViewById(R.id.gain_rank_second_user_name);
		headerTop3UserName[2] = (TextView) headerView
				.findViewById(R.id.gain_rank_third_user_name);

		headerTop3UserMoney[0] = (TextView) headerView
				.findViewById(R.id.gain_rank_first_user_money);
		headerTop3UserMoney[1] = (TextView) headerView
				.findViewById(R.id.gain_rank_second_user_money);
		headerTop3UserMoney[2] = (TextView) headerView
				.findViewById(R.id.gain_rank_third_user_money);

		userRankNum = (TextView) headerView
				.findViewById(R.id.gain_rank_my_rank);
		userRankName = (TextView) headerView
				.findViewById(R.id.gain_rank_my_level_name);

		userInfoLay = (LinearLayout) headerView
				.findViewById(R.id.gain_rank_user_info_lay);

		listView.addHeaderView(headerView);
		listView.addHeaderView(View.inflate(this,
				R.layout.gain_rank_tiitle_header, null));
		listView.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount)
			{
				if (firstVisibleItem >= 1)
				{
					topTitleLay.setVisibility(View.VISIBLE);
				} else
				{
					topTitleLay.setVisibility(View.GONE);
				}
			}
		});

		// 获取收入排行榜名单
		doLoadData(Constants.UAC_API_URL, new String[]
		{ GET_INCOME_RANK_REQUEST_TAG }, new ByteString[]
		{ getInComeRankRequestData() }, "");

		listView.setAdapter(adapter);

	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		for (String action : rspPacket.getActionList())
		{
			if (action.equals(GET_INCOME_RANK_RSPONSE_TAG))
			{
				// 获取到排行数据
				try
				{
					RspRank rspRank = RspRank.parseFrom(rspPacket.getParams(0));
					if (rspRank.getRescode() == 0)
					{
						// 获取排行数据成功
						DLog.i("lilijun", "获取排行数据成功");
						List<RankInfo> list = rspRank.getRankInfoList();
						if (list != null)
						{
							rankInfos.addAll(list);
						}
						DLog.i("lilijun", "rankInfos.size()----->>>"
								+ rankInfos.size());
						userInfo = userInfoManager.getLoginedUserInfo();
						for (RankInfo rankInfo : rankInfos)
						{
							DLog.i("lilijun", "rankInfo.getRankNo()----->>"
									+ rankInfo.getRankNo());
							DLog.i("lilijun", "rankInfo.getUserName()----->>"
									+ rankInfo.getUserName());
							if (userInfoManager.isHaveUserLogin())
							{
								if (rankInfo.getUid() == userInfo.getUserId())
								{
									// 得到用户自己的RankInfo对象
									userRankInfo = rankInfo;
									if (rankInfo.getRankNo() == 1
											|| rankInfo.getRankNo() == 2
											|| rankInfo.getRankNo() == 3)
									{
										// 如果是排名前3的
										// 则将RankInfo信息加入到top3RankInfos集合中去
										top3RankInfos.add(rankInfo);
									}
								} else
								{
									if (rankInfo.getRankNo() == 1
											|| rankInfo.getRankNo() == 2
											|| rankInfo.getRankNo() == 3)
									{
										// 如果是排名前3的
										// 则将RankInfo信息加入到top3RankInfos集合中去
										top3RankInfos.add(rankInfo);
									}
								}
							} else
							{
								if (rankInfo.getRankNo() == 1
										|| rankInfo.getRankNo() == 2
										|| rankInfo.getRankNo() == 3)
								{
									// 如果是排名前3的
									// 则将RankInfo信息加入到top3RankInfos集合中去
									top3RankInfos.add(rankInfo);
								}
							}
						}

						if (userRankInfo != null)
						{
							// 设置用户自己的排名信息
							userInfoLay.setVisibility(View.VISIBLE);
							userRankName.setText(String.format(getResources()
									.getString(R.string.my_rank_level_name),
									userRankInfo.getRankName()));
							if (userRankInfo.getRankNo() > 100)
							{
								rankInfos.remove(userRankInfo);
								userRankNum.setText(String.format(
										getResources().getString(
												R.string.my_rank_num),
										getResources().getString(
												R.string.no_rank)));
							} else
							{
								userRankNum.setText(String.format(
										getResources().getString(
												R.string.my_rank_num),
										userRankInfo.getRankNo() + ""));
							}
						} else
						{
							userInfoLay.setVisibility(View.GONE);
						}
						adapter.setUserRankInfo(userRankInfo);

						DLog.i("lilijun",
								"添加到top3List top3RankInfos.size()---->>>"
										+ top3RankInfos.size());
						// 遍历前三的RankInfo信息
						for (RankInfo rankInfo : top3RankInfos)
						{
							// 将前三的RankInfo移除普通排行信息列表
							rankInfos.remove(rankInfo);
							DLog.i("lilijun", "用户排名" + rankInfo.getRankNo());
							DLog.i("lilijun", "用户名称：" + rankInfo.getUserName());
							if (rankInfo.getRankNo() == 1)
							{
								// 设置第一名的排名信息
								headerTop3UserName[0].setText(rankInfo
										.getUserName());
								headerTop3UserMoney[0].setText(rankInfo
										.getWithdrawing() + "");
								imageLoaderManager.displayImage(
										rankInfo.getUserHeadPic(),
										headerTop3UserIcons[0], options);
							}
							if (rankInfo.getRankNo() == 2)
							{
								// 设置第二名的排名信息
								headerTop3UserName[1].setText(rankInfo
										.getUserName());
								headerTop3UserMoney[1].setText(rankInfo
										.getWithdrawing() + "");
								imageLoaderManager.displayImage(
										rankInfo.getUserHeadPic(),
										headerTop3UserIcons[1], options);
							}
							if (rankInfo.getRankNo() == 3)
							{
								// 设置第三名的排名信息
								headerTop3UserName[2].setText(rankInfo
										.getUserName());
								headerTop3UserMoney[2].setText(rankInfo
										.getWithdrawing() + "");
								imageLoaderManager.displayImage(
										rankInfo.getUserHeadPic(),
										headerTop3UserIcons[2], options);
							}
						}

						adapter.notifyDataSetChanged();
						centerViewLayout.setVisibility(View.VISIBLE);
						loadingView.setVisibilyView(false);
						errorViewLayout.setVisibility(View.GONE);
					} else
					{
						DLog.e("lilijun",
								"获取排行数据失败   code------->>>"
										+ rspRank.getRescode());
						showErrorView();
						errorViewLayout.showLoadFailedLay();
					}
				} catch (Exception e)
				{
					DLog.e(TAG, "解析排行数据时发生异常()#Excepton:", e);
					showErrorView();
					errorViewLayout.showLoadFailedLay();
				}
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		showErrorView();
		errorViewLayout.showLoadFailedLay();
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		showErrorView();
		errorViewLayout.showLoadFailedLay();
	}

	@Override
	protected void tryAgain()
	{
		super.tryAgain();
		// 获取收入排行榜名单
		doLoadData(Constants.UAC_API_URL, new String[]
		{ GET_INCOME_RANK_REQUEST_TAG }, new ByteString[]
		{ getInComeRankRequestData() }, "");
	}

	/**
	 * 获取收入排行榜的请求数据
	 * 
	 * @param userId
	 * @return
	 */
	private ByteString getInComeRankRequestData()
	{
		ReqRank.Builder builder = ReqRank.newBuilder();
		if (userInfoManager.isHaveUserLogin())
		{
			userInfo = userInfoManager.getLoginedUserInfo();
			builder.setUid(userInfo.getUserId());
		}
		return builder.build().toByteString();
	}

	public static void startGainsRankActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				GainsRankActivity.class), action);
	}

}
