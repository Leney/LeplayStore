package com.xd.leplay.store.gui.treasure;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.main.BaseTabChildFragment;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.DownloadRank;
import com.xd.leplay.store.model.proto.Uac.ReqDownloadRank;
import com.xd.leplay.store.model.proto.Uac.RspDownloadRank;
import com.xd.leplay.store.view.LoadMoreScrollListener;
import com.xd.leplay.store.view.LoadMoreScrollListener.setOnScrollToEndListener;
import com.xd.leplay.store.view.LoadMoreView;
import com.xd.leplay.store.view.MarketListView;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 下载排行榜Fragment
 * 
 * @author luoxingxing
 *
 */
public class DownloadRankFragment extends BaseTabChildFragment
{
	private static final String TAG = "DownloadRankFragment";

	private MarketListView listView = null;

	private DownLoadRewardRankAdapter adapter = null;

	private ArrayList<DownloadRank> downloadRankInfos = null;

	/** 列表一次加载的长度 */
	private static final int LOAD_DATA_SIZE = 10;

	private LoadMoreView loadMoreView = null;

	/** 请求列表数据的TAG值 */
	private final String REQUEST_LIST_TAG = "ReqDownloadRank";

	/** 响应列表数据的TAG值 */
	private final String RSPONSE_LIST_TAG = "RspDownloadRank";

	/** 判断是否是第一次加载数据 */
	private boolean isFirstLoadData = true;

	private static String thisAction = "";

	public static DownloadRankFragment getInstance(String beforeAction)
	{
		thisAction = DataCollectionManager
				.getAction(
						beforeAction,
						DataCollectionConstant.DATA_COLLECTION_TREASURE_DOWNLOAD_REWARD_DOWNLOAD_RANK_VALUE);
		return new DownloadRankFragment();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isFirstLoadData)
		{
			action = thisAction;
			DataCollectionManager.getInstance().addRecord(action);
			isFirstLoadData = false;
		}
	}

	@Override
	protected void initView(FrameLayout view)
	{
		action = thisAction;
		listView = new MarketListView(getActivity());
		setCenterView(listView);

		downloadRankInfos = new ArrayList<DownloadRank>();
		adapter = new DownLoadRewardRankAdapter(getActivity(), listView,
				downloadRankInfos, action);
		listView.setOnScrollListener(new LoadMoreScrollListener(ImageLoader
				.getInstance(), true, true, new setOnScrollToEndListener()
		{
			@Override
			public void loadMoreWhenScrollToEnd()
			{
				loadMoreData();
			}
		}));

		loadMoreView = new LoadMoreView(getActivity());
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
				{ REQUEST_LIST_TAG },
				new ByteString[]
				{ getDownLoadRankRequestData(
						((downloadRankInfos.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, "");

	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);

		List<String> actions = rspPacket.getActionList();
		for (String actionNet : actions)
		{
			if (RSPONSE_LIST_TAG.equals(actionNet))
			{
				// 列表数据返回
				try
				{
					RspDownloadRank rspDownloadRankInfo = RspDownloadRank
							.parseFrom(rspPacket.getParams(0));
					// 0=成功,1=系统响应错误,2=userToken错误
					if (rspDownloadRankInfo.getRescode() == 0)
					{
						List<DownloadRank> emptyDetails = rspDownloadRankInfo
								.getDownloadRankList();
						if (emptyDetails != null && !emptyDetails.isEmpty())
						{
							downloadRankInfos.addAll(rspDownloadRankInfo
									.getDownloadRankList());
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
						DLog.i("luoxingxing",
								"downloadRankInfosList.size()--------->>>"
										+ downloadRankInfos.size());

						if (!downloadRankInfos.isEmpty())
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
							showErrorView();
							// 当前没有下载排行的记录
							errorViewLayout
									.setErrorView(
											R.drawable.no_gain_coin_record_img,
											getResources()
													.getString(
															R.string.no_gain_download_rank_record),
											"");
							errorViewLayout.setRefrushOnClickListener(null);
						}
					} else if (rspDownloadRankInfo.getRescode() == 2)
					{
						// userToken错误
						if (getActivity() != null)
						{
							// 退出登录
							LoginUserInfoManager.getInstance().exitLogin();
							Toast.makeText(
									getActivity(),
									getResources().getString(R.string.re_login),
									Toast.LENGTH_SHORT).show();
							LoginActivity.startLoginActivity(
									getActivity(), action);
							getActivity().finish();
						}
					} else
					{
						DLog.e("luoxingxing", rspDownloadRankInfo.getResmsg()
								+ "--->>" + rspDownloadRankInfo.getRescode());
						showErrorView();
					}
				} catch (Exception e)
				{
					DLog.e(TAG, "解析下载应用列表记录发生异常#exception：", e);
				}
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		if (!downloadRankInfos.isEmpty())
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
		if (!downloadRankInfos.isEmpty())
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
		doLoadData(
				Constants.UAC_API_URL,
				new String[]
				{ REQUEST_LIST_TAG },
				new ByteString[]
				{ getDownLoadRankRequestData(
						((downloadRankInfos.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, "");
	}

	/**
	 * 加载列表下一页数据
	 */
	private void loadMoreData()
	{
		doLoadData(
				Constants.UAC_API_URL,
				new String[]
				{ REQUEST_LIST_TAG },
				new ByteString[]
				{ getDownLoadRankRequestData(
						((downloadRankInfos.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, "");
	}

	/**
	 * 获取下载排行榜的请求数据
	 * 
	 * @param userId
	 * @return
	 */
	private ByteString getDownLoadRankRequestData(int index, int size)
	{
		ReqDownloadRank.Builder builder = ReqDownloadRank.newBuilder();
		builder.setPageIndex(index);
		builder.setPageSize(size);
		return builder.build().toByteString();
	}

}
