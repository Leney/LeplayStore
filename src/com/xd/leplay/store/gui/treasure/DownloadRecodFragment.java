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
import com.xd.leplay.store.model.proto.Uac.DownloadDetail;
import com.xd.leplay.store.model.proto.Uac.ReqWealthDetail;
import com.xd.leplay.store.model.proto.Uac.RspDownloadList;
import com.xd.leplay.store.view.LoadMoreScrollListener;
import com.xd.leplay.store.view.LoadMoreScrollListener.setOnScrollToEndListener;
import com.xd.leplay.store.view.LoadMoreView;
import com.xd.leplay.store.view.MarketListView;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 下载记录
 * 
 * @author luoxingxing
 *
 */
public class DownloadRecodFragment extends BaseTabChildFragment
{
	private static final String TAG = "DownloadRecodFragment";

	private MarketListView listView = null;

	private DownloadRewardRecordAdapter adapter = null;

	/** 按天排列的下载应用的记录集合 */
	private List<DownloadDetail> downloadDetailList = null;

	/** 列表一次加载的长度 */
	private static final int LOAD_DATA_SIZE = 10;

	private LoadMoreView loadMoreView = null;

	/** 获取下载应用列表请求的TAG */
	private final String GAIN_DOWNLOAD_LIST_REQUEST_TAG = "ReqDownloadList";

	/** 获取下载应用列表响应的TAG */
	private final String GAIN_DOWNLOAD_LIST_RSPONSE_TAG = "RspDownloadList";

	/** 判断是否是第一次加载数据 */
	private boolean isFirstLoadData = true;

	private static String thisAction = "";

	public static DownloadRecodFragment getInstance(String beforeAction)
	{
		thisAction = DataCollectionManager
				.getAction(
						beforeAction,
						DataCollectionConstant.DATA_COLLECTION_TREASURE_DOWNLOAD_REWARD_DOWNLOAD_LIST_VALUE);
		return new DownloadRecodFragment();
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
		listView.setDividerHeight(0);
		setCenterView(listView);

		downloadDetailList = new ArrayList<DownloadDetail>();

		adapter = new DownloadRewardRecordAdapter(getActivity(),
				downloadDetailList);
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
				{ GAIN_DOWNLOAD_LIST_REQUEST_TAG },
				new ByteString[]
				{ getCoinsRecordRequestData(LoginUserInfoManager.getInstance()
						.getLoginedUserInfo().getUserId(), LoginUserInfoManager
						.getInstance().getLoginedUserInfo().getUserToken(),
						((downloadDetailList.size() / LOAD_DATA_SIZE) + 1),
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
				{ GAIN_DOWNLOAD_LIST_REQUEST_TAG },
				new ByteString[]
				{ getCoinsRecordRequestData(LoginUserInfoManager.getInstance()
						.getLoginedUserInfo().getUserId(), LoginUserInfoManager
						.getInstance().getLoginedUserInfo().getUserToken(),
						((downloadDetailList.size() / LOAD_DATA_SIZE)),
						LOAD_DATA_SIZE) }, "");
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
			int index, int size)
	{
		ReqWealthDetail.Builder builder = ReqWealthDetail.newBuilder();
		builder.setUid(userId);
		builder.setUserToken(userToken);
		builder.setPageIndex(index);
		builder.setPageSize(size);
		return builder.build().toByteString();
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<String> actions = rspPacket.getActionList();
		for (String actionNet : actions)
		{
			if (GAIN_DOWNLOAD_LIST_RSPONSE_TAG.equals(actionNet))
			{
				// 列表数据返回
				try
				{
					RspDownloadList rspDownloadInfo = RspDownloadList
							.parseFrom(rspPacket.getParams(0));
					// 0=成功,1=系统响应错误,2=userToken错误
					if (rspDownloadInfo.getRescode() == 0)
					{
						List<DownloadDetail> emptyDetails = rspDownloadInfo
								.getDetailList();
						if (emptyDetails != null && !emptyDetails.isEmpty())
						{
							downloadDetailList.addAll(rspDownloadInfo
									.getDetailList());
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
								"downloadDetailList.size()--------->>>"
										+ rspDownloadInfo.getDetailList()
												.size());

						if (!downloadDetailList.isEmpty())
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
							// 当前没有下载应用的记录
							errorViewLayout.setErrorView(
									R.drawable.no_gain_coin_record_img,
									getResources().getString(
											R.string.no_gain_download_record),
									"");
							errorViewLayout.setRefrushOnClickListener(null);
						}
					} else if (rspDownloadInfo.getRescode() == 2)
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
							LoginActivity.startLoginActivity(getActivity(),
									action);
							getActivity().finish();
						}
					} else
					{
						DLog.e("luoxingxing", rspDownloadInfo.getResmsg()
								+ "--->>" + rspDownloadInfo.getRescode());
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
		if (!downloadDetailList.isEmpty())
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
		if (!downloadDetailList.isEmpty())
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
				{ GAIN_DOWNLOAD_LIST_REQUEST_TAG },
				new ByteString[]
				{ getCoinsRecordRequestData(LoginUserInfoManager.getInstance()
						.getLoginedUserInfo().getUserId(), LoginUserInfoManager
						.getInstance().getLoginedUserInfo().getUserToken(),
						((downloadDetailList.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, "");
	}

}
