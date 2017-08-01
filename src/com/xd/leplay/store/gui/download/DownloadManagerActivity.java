package com.xd.leplay.store.gui.download;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.download.DownloadListener;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.view.MarketExpandableListView;

/**
 * 下载任务管理
 * 
 * @author lilijun
 *
 */
public class DownloadManagerActivity extends BaseActivity
{
	private MarketExpandableListView listView = null;
	private DownloadManagerAdapter adapter = null;

	private DownloadManager downloadManager = null;
	private List<String> groups = null;
	private List<List<DownloadInfo>> childs = null;

	/** 下载任务监听器 */
	private DownloadListener listener = null;

	/** 刷新列表 */
	private static final int REFRESH_LIST_VIEW = 1;

	/** 重新获取列表数据，并刷新 */
	public static final int REGET_LIST_DATA = 2;

	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what == REFRESH_LIST_VIEW)
			{
				adapter.notifyDataSetChanged();
				for (int i = 0; i < adapter.getGroupCount(); i++)
				{
					listView.expandGroup(i);
				}
			} else if (msg.what == REGET_LIST_DATA)
			{
				getListData();
				adapter.notifyDataSetChanged();
			}
			if (childs.isEmpty())
			{
				errorViewLayout
						.setErrorView(
								R.drawable.no_download_task,
								getResources().getString(
										R.string.no_download_task), "");
				errorViewLayout.setBtnVisible(false);
				errorViewLayout.setVisibility(View.VISIBLE);
				errorViewLayout.setRefrushOnClickListener(null);
				centerViewLayout.setVisibility(View.GONE);
			}
		};
	};

	@Override
	protected void initView()
	{
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_DWONLOAD_LIST_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		loadingView.setVisibilyView(false);
		titleView.setTitleName(getResources().getString(R.string.download_app));
		titleView.setBottomLineVisible(true);
		titleView.setRightLayVisible(false);

		downloadManager = DownloadManager.shareInstance();
		listView = new MarketExpandableListView(DownloadManagerActivity.this);
		setCenterView(listView);
		// 是否是从通知栏跳转过来 且是更新爱玩商店本身
		boolean isUpgradeFromNotification = getIntent().getBooleanExtra(
				"isUpgradeFromNotification", false);
		DLog.i("lilijun", "isUpgradeFromNotification------->>>"
				+ isUpgradeFromNotification);
		if (isUpgradeFromNotification)
		{
			DownloadInfo upgradeInfo = (DownloadInfo) getIntent()
					.getSerializableExtra("downloadInfo");
			DLog.i("lilijun", "upgradeInfo==null------->>>"
					+ (upgradeInfo == null));
			downloadManager.addDownload(upgradeInfo);
		}

		groups = new ArrayList<String>();
		childs = new ArrayList<List<DownloadInfo>>();

		adapter = new DownloadManagerAdapter(DownloadManagerActivity.this,
				groups, childs, handler, action);
		getListData();
		if (childs.isEmpty())
		{
			errorViewLayout.setErrorView(R.drawable.no_download_task,
					getResources().getString(R.string.no_download_task), "");
			errorViewLayout.setBtnVisible(false);
			errorViewLayout.setVisibility(View.VISIBLE);
			errorViewLayout.setRefrushOnClickListener(null);
			centerViewLayout.setVisibility(View.GONE);
		}
		listView.setAdapter(adapter);

		for (int i = 0; i < adapter.getGroupCount(); i++)
		{
			listView.expandGroup(i);
		}

		initListener();
		downloadManager.registerDownloadListener(listener);
	}

	private void initListener()
	{
		listener = new DownloadListener()
		{
			@Override
			public void onTaskCountChanged(int state, DownloadInfo info)
			{
				getListData();
				handler.sendEmptyMessage(REFRESH_LIST_VIEW);
			}

			@Override
			public void onStateChange(int state, DownloadInfo info)
			{
				if (state == DownloadInfo.STATE_FINISH)
				{
					getListData();
					handler.sendEmptyMessage(REFRESH_LIST_VIEW);
				}
			}

			@Override
			public void onProgress(int percent, DownloadInfo info)
			{
			}
		};
	}

	/**
	 * 获取列表显示数据
	 */
	private void getListData()
	{
		List<DownloadInfo> downloadingList = new ArrayList<DownloadInfo>();
		List<DownloadInfo> finishList = new ArrayList<DownloadInfo>();
		for (DownloadInfo downloadInfo : downloadManager.getAllTaskInfo())
		{
			if (downloadInfo.getState() != DownloadInfo.STATE_FINISH)
			{
				downloadingList.add(downloadInfo);
			} else
			{
				finishList.add(downloadInfo);
			}
		}
		childs.clear();
		groups.clear();
		if (!downloadingList.isEmpty())
		{
			childs.add(downloadingList);
			groups.add(getResources().getString(R.string.downloading) + "( "
					+ downloadingList.size() + " )");
		}
		if (!finishList.isEmpty())
		{
			childs.add(finishList);
			groups.add(getResources().getString(R.string.finished) + "( "
					+ finishList.size() + " )");
		}
	}

	@Override
	protected void onDestroy()
	{
		adapter.unregisterListener();
		downloadManager.unregisterDownloadListener(listener);
		super.onDestroy();
	}

	public static void startDownloadManagerActivity(Context context,
			String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				DownloadManagerActivity.class), action);
	}

}
