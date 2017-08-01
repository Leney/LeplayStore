package com.xd.leplay.store.gui.recommend;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.FrameLayout;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.adapter.SoftListAdapter;
import com.xd.leplay.store.gui.main.BaseTabChildFragment;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.model.proto.App;
import com.xd.leplay.store.model.proto.App.ReqRecommAppList;
import com.xd.leplay.store.model.proto.App.RspRecommAppList;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.LoadMoreScrollListener;
import com.xd.leplay.store.view.LoadMoreScrollListener.setOnScrollToEndListener;
import com.xd.leplay.store.view.LoadMoreView;
import com.xd.leplay.store.view.MarketListView;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 玩赚金币游戏Tab
 * 
 * @author lilijun
 *
 */
public class GetCoinGameFragment extends BaseTabChildFragment
{
	private static final String TAG = "GetCoinGameFragment";

	private MarketListView listView = null;

	private SoftListAdapter adapter = null;

	private ArrayList<ListAppInfo> appInfos = null;

	/** 列表一次加载的长度 */
	private static final int LOAD_DATA_SIZE = 10;

	/** 标识是否还有下一页列表数据 */
	private boolean isHaveNextPageData = true;

	private LoadMoreView loadMoreView = null;

	/**
	 * 获取列表的类型的标识值
	 * 1=首页推荐,2=首页新品应用首发,3=首页新品游戏首发,4=首页玩赚金币(应用),5=首页玩赚金币(游戏)，6=应用精品，
	 * 7=应用排行,8=应用必备,9=游戏精品,10=游戏排行
	 */
	private final int LIST_TYPE = 5;

	/** 请求列表的TAG值 */
	private final String REQUEST_LIST_TAG = "ReqRecommAppList";

	/** 获取响应列表时 返回的TAG值 */
	private final String RESPONSE_LIST_TAG = "RspRecommAppList";

	/** 判断是否是第一次加载数据 */
	private boolean isFirstLoadData = true;

	private static String thisAction = "";

	/** 玩赚金币游戏列表的etag标识值 */
	private String etagMark = "getCoinGameList";

	public static GetCoinGameFragment getInstance(String beforeAction)
	{
		thisAction = DataCollectionManager.getAction(beforeAction,
				DataCollectionConstant.DATA_COLLECTION_GAIN_COIN_GAME_VALUE);
		return new GetCoinGameFragment();
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
		listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		setCenterView(listView);

		appInfos = new ArrayList<ListAppInfo>();
		adapter = new SoftListAdapter(getActivity(), listView, appInfos, action);
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
				Constants.APP_API_URL,
				new String[]
				{ REQUEST_LIST_TAG },
				new ByteString[]
				{ getReqRecommAppList(((appInfos.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, etagMark);
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<String> actions = rspPacket.getActionList();
		for (String action : actions)
		{
			if (RESPONSE_LIST_TAG.equals(action))
			{
				// 列表数据返回
				parseListResult(rspPacket);
				if (!isHaveNextPageData)
				{
					listView.setOnScrollListener(null);
					listView.removeFooterView(loadMoreView);
					listView.showEndView();
				}
				if (!appInfos.isEmpty())
				{
					if (listView.getAdapter() == null)
					{
						listView.setAdapter(adapter);
					} else
					{
						adapter.notifyDataSetChanged();
					}
					// if (appInfos.size() <= LOAD_DATA_SIZE)
					// {
					// // 表明是第一次加载列表数据
					// listView.setAdapter(adapter);
					// } else
					// {
					// // 表明是加载下一页返回了数据
					// adapter.notifyDataSetChanged();
					// }
				}
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		if (!appInfos.isEmpty())
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
		if (!appInfos.isEmpty())
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
				Constants.APP_API_URL,
				new String[]
				{ REQUEST_LIST_TAG },
				new ByteString[]
				{ getReqRecommAppList(((appInfos.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, etagMark);
	}

	/**
	 * 加载列表下一页数据
	 */
	private void loadMoreData()
	{
		doLoadData(
				Constants.APP_API_URL,
				new String[]
				{ REQUEST_LIST_TAG, },
				new ByteString[]
				{ getReqRecommAppList(((appInfos.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, etagMark
						+ (appInfos.size() / LOAD_DATA_SIZE) + 1);
	}

	/**
	 * 获取ReqRecommAppList的ByteString
	 * 
	 * @param type
	 * @param index
	 * @param size
	 * @return
	 */
	private ByteString getReqRecommAppList(int index, int size)
	{
		ReqRecommAppList.Builder builder = ReqRecommAppList.newBuilder();
		builder.setRecommType(LIST_TYPE);
		builder.setPageIndex(index);
		builder.setPageSize(size);
		ReqRecommAppList reqRecommAppList = builder.build();
		ByteString byteString = reqRecommAppList.toByteString();
		return byteString;
	}

	/**
	 * 解析列表返回结果
	 * 
	 * @param rspPacket
	 */
	private void parseListResult(RspPacket rspPacket)
	{
		try
		{
			RspRecommAppList rspRecommAppList = RspRecommAppList
					.parseFrom(rspPacket.getParams(0));
			List<App.AppInfo> infos = rspRecommAppList.getAppInfosList();
			if (infos.size() == 0 || infos.isEmpty()
					|| infos.size() < LOAD_DATA_SIZE)
			{
				// 没有下一页数据了
				isHaveNextPageData = false;
			}

			for (App.AppInfo info : infos)
			{
				appInfos.add(ToolsUtil.getListAppInfo(info));
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "parseListResult()#Excepton:", e);
		}
	}

	@Override
	public void onDestroy()
	{
		if (adapter != null)
		{
			adapter.unRegisterListener();
		}
		super.onDestroy();
	}

}
