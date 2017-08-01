package com.xd.leplay.store.gui.application;

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
import com.xd.leplay.store.gui.adapter.NecessaryAdapter;
import com.xd.leplay.store.gui.main.BaseTabChildFragment;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.model.proto.App;
import com.xd.leplay.store.model.proto.App.NecessaryContent;
import com.xd.leplay.store.model.proto.App.ReqNecessaryApp;
import com.xd.leplay.store.model.proto.App.RspNecessaryApp;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.LoadMoreScrollListener;
import com.xd.leplay.store.view.LoadMoreScrollListener.setOnScrollToEndListener;
import com.xd.leplay.store.view.LoadMoreView;
import com.xd.leplay.store.view.MarketExpandableListView;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 应用Tab中 必备子Fragment
 * 
 * @author lilijun
 *
 */
public class ApplicationNecessaryFragment extends BaseTabChildFragment
{
	private static final String TAG = "ApplicationNecessaryFragment";

	private MarketExpandableListView listView = null;

	private NecessaryAdapter adapter = null;

	private List<String> groups = null;

	private List<List<ListAppInfo>> childs = null;

	/** 必备对象的集合 */
	private List<NecessaryContent> necessaryContents = null;

	/** 列表一次加载的长度 */
	private static final int LOAD_DATA_SIZE = 10;

	/** 标识是否还有下一页列表数据 */
	private boolean isHaveNextPageData = true;

	private LoadMoreView loadMoreView = null;

	/** 请求列表数据的TAG */
	private final String REQUEST_LIST_TAG = "ReqNecessaryApp";

	/** 响应列表数据的TAG */
	private final String RSPONSE_LIST_TAG = "RspNecessaryApp";

	/** 判断是否是第一次加载数据 */
	private boolean isFirstLoadData = true;

	private static String thisAction = "";

	/** 应用分类列表的etag标识值 */
	private String etagMark = "applicationNecessaryList";

	public static ApplicationNecessaryFragment getInstance(String beforeAction)
	{
		thisAction = DataCollectionManager.getAction(beforeAction,
				DataCollectionConstant.DATA_COLLECTION_APP_NECESSARY_VALUE);
		return new ApplicationNecessaryFragment();
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
		listView = new MarketExpandableListView(getActivity());
		listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		setCenterView(listView);

		necessaryContents = new ArrayList<App.NecessaryContent>();
		groups = new ArrayList<String>();
		childs = new ArrayList<List<ListAppInfo>>();

		adapter = new NecessaryAdapter(getActivity(), groups, childs, action);
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
				{ getNecessaryList(
						((necessaryContents.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, etagMark);
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<String> actions = rspPacket.getActionList();
		for (String action : actions)
		{
			if (RSPONSE_LIST_TAG.equals(action))
			{
				// 必备列表数据返回
				parseNecessaryListResult(rspPacket);
				if (!isHaveNextPageData)
				{
					listView.setOnScrollListener(null);
					listView.removeFooterView(loadMoreView);
					listView.showEndView();
				}
				if (!necessaryContents.isEmpty())
				{
					if (listView.getAdapter() == null)
					{
						// 表明是第一次加载列表数据
						listView.setAdapter(adapter);
					} else
					{
						// 表明是加载下一页返回了数据
						adapter.notifyDataSetChanged();
					}
					// if (necessaryContents.size() <= LOAD_DATA_SIZE)
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
				}
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		if (!necessaryContents.isEmpty())
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
		if (!necessaryContents.isEmpty())
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
				{ getNecessaryList(
						((necessaryContents.size() / LOAD_DATA_SIZE) + 1),
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
				{ getNecessaryList(
						((necessaryContents.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, etagMark
						+ (necessaryContents.size() / LOAD_DATA_SIZE) + 1);
	}

	/**
	 * 获取必备列表的请求数据
	 * 
	 * @param type
	 * @param index
	 * @param size
	 * @return
	 */
	private ByteString getNecessaryList(int index, int size)
	{
		ReqNecessaryApp.Builder builder = ReqNecessaryApp.newBuilder();
		builder.setPageIndex(index);
		builder.setPageSize(size);
		return builder.build().toByteString();
	}

	/**
	 * 解析必备列表返回结果
	 * 
	 * @param rspPacket
	 * @throws Exception
	 */
	private void parseNecessaryListResult(RspPacket rspPacket)
	{
		try
		{
			RspNecessaryApp rspNecessaryApp = RspNecessaryApp
					.parseFrom(rspPacket.getParams(0));

			// 返回码：0=成功,1=失败,2=暂无数据
			if (rspNecessaryApp.getRescode() == 0)
			{
				// 成功
				List<NecessaryContent> infos = rspNecessaryApp
						.getNecessaryContentList();

				if (infos == null || infos.isEmpty()
						|| infos.size() < LOAD_DATA_SIZE)
				{
					// 没有下一页数据了
					isHaveNextPageData = false;
				}
				if (infos != null)
				{
					necessaryContents.addAll(infos);
					for (NecessaryContent necessaryContent : infos)
					{
						groups.add(necessaryContent.getTitle());

						childs.add(ToolsUtil
								.getListAppInfosByAppInfoList(necessaryContent
										.getAppInfosList()));
					}
				}
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "parseNecessaryListResult()#Excepton:", e);
		}
	}

	@Override
	public void onDestroy()
	{
		// if (adapter != null)
		// {
		// adapter.unRegisterListener();
		// }
		super.onDestroy();
	}

}
