package com.xd.leplay.store.gui.search;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.FrameLayout;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.adapter.SearchResultAdapter;
import com.xd.leplay.store.gui.main.BaseTabChildFragment;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.model.proto.App;
import com.xd.leplay.store.model.proto.App.ReqAppList4SearchKey;
import com.xd.leplay.store.model.proto.App.RspAppList4SearchKey;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.LoadMoreScrollListener;
import com.xd.leplay.store.view.LoadMoreScrollListener.setOnScrollToEndListener;
import com.xd.leplay.store.view.LoadMoreView;
import com.xd.leplay.store.view.MarketListView;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 游戏Tab中 排行子Fragment
 * 
 * @author lilijun
 *
 */
public class SearchResultFragment extends BaseTabChildFragment
{
	private static final String TAG = "SearchResultFragment";

	private MarketListView listView = null;

	private SearchResultAdapter adapter = null;

	private ArrayList<ListAppInfo> appInfos = null;

	/** 当前视图显示的数据的应用包名集合 */
	private List<String> packgeNames = null;

	/** 列表一次加载的长度 */
	private static final int LOAD_DATA_SIZE = 10;

	/** 标识是否还有下一页列表数据 */
	private boolean isHaveNextPageData = true;

	/** 标记 是否是一次新的搜索 */
	private boolean isNewSearch = true;

	private LoadMoreView loadMoreView = null;

	/** 包裹loadMoreView的容器 */
	private FrameLayout loadMoreLayout = null;
	/** 搜索的关键字 */
	private static String searchKey = "";

	/** 请求搜索列表的key值 */
	private final String REQUST_LIST_KEY_NAME = "ReqAppList4SearchKey";

	/** 响应搜索列表的key值 */
	private final String RESPONSE_LIST_KEY_NAME = "RspAppList4SearchKey";

	/** 判断是否是第一次加载数据 */
	private boolean isFirstLoadData = true;

	private static String thisAction = "";

	/** 当前页数 */
	private int curPage = 1;

	/** 百度搜索的数据index值 */
	private int pageIndexBaidu = -1;

	/** 服务器是否从百度查询 */
	private boolean isSearchBaidu = false;

	public static SearchResultFragment getSearchResultFragment(String key,
			String beforeAction)
	{
		searchKey = key.trim();
		thisAction = DataCollectionManager.getAction(beforeAction,
				DataCollectionConstant.DATA_COLLECTION_SEARCH_KEY_VALUE);
		return new SearchResultFragment();
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
	public void onCreate(Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			String saveSearchKey = savedInstanceState
					.getString("save_search_key");
			if (!saveSearchKey.equals("") && saveSearchKey != null)
			{
				searchKey = saveSearchKey;
			} else
			{
				searchKey = "";
			}
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putString("save_search_key", searchKey);
		super.onSaveInstanceState(outState);
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
		packgeNames = new ArrayList<String>();
		adapter = new SearchResultAdapter(getActivity(), listView, appInfos,
				action);
		listView.setOnScrollListener(new LoadMoreScrollListener(ImageLoader
				.getInstance(), true, true, new setOnScrollToEndListener()
		{
			@Override
			public void loadMoreWhenScrollToEnd()
			{
				if (isHaveNextPageData)
				{
					loadMoreData();
					loadMoreView.setVisibility(View.VISIBLE);
				} else
				{
					// listView.setOnScrollListener(null);
					loadMoreView.setVisibility(View.GONE);
					listView.showEndView();
				}
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

		loadMoreLayout = new FrameLayout(getActivity());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		loadMoreLayout.setLayoutParams(params);
		loadMoreLayout.addView(loadMoreView);

		// listView.addFooterView(loadMoreView);
		listView.addFooterView(loadMoreLayout);

		// 是一次新的搜索
		isNewSearch = true;
		// doLoadData(
		// Constants.APP_API_URL,
		// new String[]
		// { REQUST_LIST_KEY_NAME },
		// new ByteString[]
		// { getReqSearchAppList(searchKey,
		// ((appInfos.size() / LOAD_DATA_SIZE) + 1),
		// LOAD_DATA_SIZE) }, "");
		doLoadData(Constants.APP_API_URL, new String[]
		{ REQUST_LIST_KEY_NAME }, new ByteString[]
		{ getReqSearchAppList(searchKey, curPage, LOAD_DATA_SIZE) }, "");
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<String> actions = rspPacket.getActionList();
		for (String action : actions)
		{
			if (RESPONSE_LIST_KEY_NAME.equals(action))
			{
				// 列表数据返回
				parseListResult(rspPacket);
				if (isNewSearch)
				{
					listView.setAdapter(adapter);
				} else
				{
					if (listView.getAdapter() == null)
					{
						listView.setAdapter(adapter);
					} else
					{
						adapter.notifyDataSetChanged();
					}
				}
				if (curPage == 1)
				{
					if (appInfos.size() < LOAD_DATA_SIZE)
					{
						loadMoreView.setVisibility(View.VISIBLE);
						loadMoreData();
					}
				}
				// else
				// {
				// if (appInfos.size() < LOAD_DATA_SIZE)
				// {
				// loadMoreView.setVisibility(View.GONE);
				// listView.showEndView();
				// }
				// }
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
		// 是一次新的搜索
		isNewSearch = true;
		curPage = 1;
		pageIndexBaidu = -1;
		isSearchBaidu = false;
		// doLoadData(
		// Constants.APP_API_URL,
		// new String[]
		// { REQUST_LIST_KEY_NAME },
		// new ByteString[]
		// { getReqSearchAppList(searchKey,
		// ((appInfos.size() / LOAD_DATA_SIZE) + 1),
		// LOAD_DATA_SIZE) }, "");
		doLoadData(Constants.APP_API_URL, new String[]
		{ REQUST_LIST_KEY_NAME }, new ByteString[]
		{ getReqSearchAppList(searchKey, curPage, LOAD_DATA_SIZE) }, "");
	}

	/**
	 * 加载列表下一页数据
	 */
	private void loadMoreData()
	{
		DLog.i("lilijun", "加载下一页！！！");
		// 不是一次新的搜索、仅仅只是加载下一页
		isNewSearch = false;
		if (isSearchBaidu)
		{
			if (pageIndexBaidu == -1)
			{
				// 下一次是第一次从百度搜索
				pageIndexBaidu = 1;
			} else
			{
				pageIndexBaidu++;
			}
			curPage = pageIndexBaidu;
		} else
		{
			curPage++;
		}
		// doLoadData(
		// Constants.APP_API_URL,
		// new String[]
		// { REQUST_LIST_KEY_NAME, },
		// new ByteString[]
		// { getReqSearchAppList(searchKey,
		// ((appInfos.size() / LOAD_DATA_SIZE) + 1),
		// LOAD_DATA_SIZE) }, "");
		doLoadData(Constants.APP_API_URL, new String[]
		{ REQUST_LIST_KEY_NAME, }, new ByteString[]
		{ getReqSearchAppList(searchKey, curPage, LOAD_DATA_SIZE) }, "");
	}

	/**
	 * 开始新的搜索
	 * 
	 * @param key
	 *            搜索关键字
	 */
	public void startSearch(String key)
	{
		DLog.i("lilijun", "是一次新的搜索！！！");
		searchKey = key;
		// 是一次新的搜索
		isNewSearch = true;
		curPage = 1;
		pageIndexBaidu = -1;
		isSearchBaidu = false;
		// doLoadData(Constants.APP_API_URL, new String[]
		// { REQUST_LIST_KEY_NAME, }, new ByteString[]
		// { getReqSearchAppList(searchKey, 1, LOAD_DATA_SIZE) }, "");
		doLoadData(Constants.APP_API_URL, new String[]
		{ REQUST_LIST_KEY_NAME, }, new ByteString[]
		{ getReqSearchAppList(searchKey, curPage, LOAD_DATA_SIZE) }, "");
	}

	/**
	 * 获取ReqRecommAppList的ByteString
	 * 
	 * @param type
	 * @param index
	 * @param size
	 * @return
	 */
	private ByteString getReqSearchAppList(String key, int index, int size)
	{
		ReqAppList4SearchKey.Builder builder = ReqAppList4SearchKey
				.newBuilder();
		builder.setSearchKeyStr(key);
		builder.setPageIndex(index);
		builder.setPageSize(size);
		if (isSearchBaidu)
		{
			builder.setNextSearchFrom(1);
		} else
		{
			builder.setNextSearchFrom(0);
		}
		ReqAppList4SearchKey reqAppList4SearchKey = builder.build();
		ByteString byteString = reqAppList4SearchKey.toByteString();
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
			RspAppList4SearchKey rspAppList = RspAppList4SearchKey
					.parseFrom(rspPacket.getParams(0));
			if (rspAppList.getNextSearchFrom() == 0)
			{
				// 下一次搜索从我们自己的服务器搜索
				isSearchBaidu = false;
			} else
			{
				// 下一次搜索从百度搜索
				isSearchBaidu = true;
			}
			DLog.i("lilijun", "返回Msg------->>>" + rspAppList.getResmsg());
			rspAppList.getNextSearchFrom();
			List<App.AppInfo> infos = rspAppList.getAppInfosList();
			// if (infos.size() == 0 || infos.isEmpty()
			// || infos.size() < LOAD_DATA_SIZE)
			// {
			// // 没有下一页数据了
			// isHaveNextPageData = false;
			// } else
			// {
			// isHaveNextPageData = true;
			// }
			if (infos.size() == 0 || infos.isEmpty())
			{
				// 没有下一页数据了
				isHaveNextPageData = false;
			} else
			{
				isHaveNextPageData = true;
			}
			DLog.i("lilijun", "infos.size()----->>>" + infos.size());
			DLog.i("lilijun", "isHaveNextPageData----->>>" + isHaveNextPageData);

			if (isNewSearch)
			{
				appInfos.clear();
				packgeNames.clear();
			}
			for (App.AppInfo info : infos)
			{
				if (!packgeNames.contains(info.getPackName()))
				{
					// 只有在当前视图没有的应用 才加入视图显示的数据源中去(去重数据)
					packgeNames.add(info.getPackName());
					appInfos.add(ToolsUtil.getListAppInfo(info));
				} else
				{
					DLog.i("lilijun", "重复的应用-------->>>" + info.getShowName());
				}
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
			adapter.onDestory();
		}
		super.onDestroy();
	}

}
