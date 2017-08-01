package com.xd.leplay.store.gui.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.adapter.SoftListAdapter;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.gui.main.MainActivity;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.model.proto.App;
import com.xd.leplay.store.model.proto.App.ReqAppTypeAppList;
import com.xd.leplay.store.model.proto.App.RspAppTypeAppList;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.LoadMoreScrollListener;
import com.xd.leplay.store.view.LoadMoreScrollListener.setOnScrollToEndListener;
import com.xd.leplay.store.view.LoadMoreView;
import com.xd.leplay.store.view.MarketListView;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 应用单个分类列表界面
 * 
 * @author lilijun
 *
 */
public class ApplicationClassifyDetailActivity extends BaseActivity
{
	private static final String TAG = "ApplicationClassifyDetailActivity";

	private MarketListView listView = null;

	private SoftListAdapter adapter = null;

	private ArrayList<ListAppInfo> appInfos = null;

	/** 列表一次加载的长度 */
	private static final int LOAD_DATA_SIZE = 10;

	/** 标识是否还有下一页列表数据 */
	private boolean isHaveNextPageData = true;

	private LoadMoreView loadMoreView = null;

	/** 请求列表数据的TAG */
	private final String REQUEST_LIST_TAG = "ReqAppTypeAppList";

	/** 响应列表数据的TAG */
	private final String RSPONSE_LIST_TAG = "RspAppTypeAppList";

	/** 类型id */
	private int typeId;

	/** 类型名称 */
	private String typeName;

	/** etag的标识值(传过来的值可能是应用的分类 和 游戏的分类) */
	private String etagMark = "";

	/** 是否从闪屏跳转过来的 */
	private boolean isFromSplash = false;

	@Override
	protected void initView()
	{
		titleView.setRightLayVisible(false);
		titleView.setBottomLineVisible(true);

		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_CP_DETAIL_VALUE);

		listView = new MarketListView(this);
		setCenterView(listView);

		typeId = getIntent().getIntExtra("typeId", -1);
		typeName = getIntent().getStringExtra("typeName");
		etagMark = getIntent().getStringExtra("etagMark");
		isFromSplash = getIntent().getBooleanExtra("isFromSplash", false);
		titleView.setTitleName(typeName);
		action = DataCollectionManager
				.getAction(
						DataCollectionManager
								.getIntentDataCollectionAction(getIntent()),
						DataCollectionConstant.DATA_COLLECTION_CLASSFIY_DETAIL_LIST_VALUE);

		appInfos = new ArrayList<ListAppInfo>();
		adapter = new SoftListAdapter(this, listView, appInfos, action);
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
				Constants.APP_API_URL,
				new String[]
				{ REQUEST_LIST_TAG },
				new ByteString[]
				{ getAppTypeAppListRequestData(typeId,
						((appInfos.size() / LOAD_DATA_SIZE) + 1),
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
				{ getAppTypeAppListRequestData(typeId,
						((appInfos.size() / LOAD_DATA_SIZE) + 1),
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
				{ getAppTypeAppListRequestData(typeId,
						((appInfos.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, etagMark
						+ (appInfos.size() / LOAD_DATA_SIZE) + 1);
	}

	@Override
	public void finish()
	{
		if (isFromSplash)
		{
			// 跳转到主页
			Intent intent = new Intent(ApplicationClassifyDetailActivity.this,
					MainActivity.class);
			startActivity(intent);
		}
		super.finish();
	}

	/**
	 * 获取单个类型列表的请求数据
	 * 
	 * @param type
	 *            列表数据类型
	 * @param index
	 * @param size
	 * @return
	 */
	private ByteString getAppTypeAppListRequestData(int typeId, int index,
			int size)
	{
		ReqAppTypeAppList.Builder builder = ReqAppTypeAppList.newBuilder();
		builder.setTypeId(typeId);
		builder.setPageIndex(index);
		builder.setPageSize(size);
		return builder.build().toByteString();
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
			RspAppTypeAppList rspRecommAppList = RspAppTypeAppList
					.parseFrom(rspPacket.getParams(0));
			// 0=成功，1=失败，2=参数错误...
			if (rspRecommAppList.getRescode() == 0)
			{
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
				DataCollectionManager.getInstance().addRecord(action,
						DataCollectionManager.CLASSFIY_ID, typeId + "");
				// 添加友盟数据统计
				HashMap<String, String> values = new HashMap<String, String>();
				values.put(DataCollectionManager.CLASSFIY_ID, typeId + "");
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								this,
								action,
								DataCollectionConstant.EVENT_ID_GET_CLASSFIY_LIST_SUCCESS,
								values);
			} else
			{
				Toast.makeText(ApplicationClassifyDetailActivity.this,
						rspRecommAppList.getResmsg(), Toast.LENGTH_SHORT)
						.show();
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

	public static void startApplicationClassifyDetailActivity(Context context,
			int typeId, String typeName, String action, String etagMark)
	{
		Intent intent = new Intent(context,
				ApplicationClassifyDetailActivity.class);
		intent.putExtra("typeId", typeId);
		intent.putExtra("typeName", typeName);
		intent.putExtra("etagMark", etagMark);
		DataCollectionManager.startActivity(context, intent, action);
	}

	public static void startApplicationClassifyDetailActivityFromSplash(
			Context context, int typeId, String typeName, String action,
			String etagMark)
	{
		Intent intent = new Intent(context,
				ApplicationClassifyDetailActivity.class);
		intent.putExtra("typeId", typeId);
		intent.putExtra("typeName", typeName);
		intent.putExtra("etagMark", etagMark);
		// 是否是从闪屏处跳转过来
		intent.putExtra("isFromSplash", true);
		DataCollectionManager.startActivity(context, intent, action);
	}
}
