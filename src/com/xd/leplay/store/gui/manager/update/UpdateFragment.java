package com.xd.leplay.store.gui.manager.update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xd.download.DownloadInfo;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.AutoUpdateManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.gui.main.BaseTabChildFragment;
import com.xd.leplay.store.model.UpdateAppInfo;
import com.xd.leplay.store.view.MarketListView;

/**
 * 
 * 更新列表的Fragment
 * 
 * @author lilijun
 *
 */
public class UpdateFragment extends BaseTabChildFragment
{
	private MarketListView listView = null;

	private UpdateAdapter adapter = null;

	private Button updateAllBtn = null;

	private ArrayList<UpdateAppInfo> updateAppInfos = null;

	private SoftwareManager softwareManager = null;

	/** 重新获取列表数据，并刷新 */
	public static final int REGET_LIST_DATA = 1;

	private OnPromptUpgreadeChangeListener changeListener = null;

	private UpdateAppActivity parentActivity = null;

	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what == REGET_LIST_DATA)
			{
				refresh();
				changeListener.onPromptUpgreadeChange(0);
			}
		};
	};

	/** 判断是否是第一次加载数据 */
	private boolean isFirstLoadData = true;

	private static String thisAction = "";

	public static UpdateFragment getInstance(String beforeAction)
	{
		thisAction = DataCollectionManager
				.getAction(
						beforeAction,
						DataCollectionConstant.DATA_COLLECTION_UPDATE_LIST_UPDATE_VALUE);
		return new UpdateFragment();
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
		loadingView.setVisibilyView(false);

		softwareManager = SoftwareManager.getInstance();

		changeListener = (UpdateAppActivity) getActivity();
		parentActivity = (UpdateAppActivity) getActivity();

		setCenterView(R.layout.update_layout);
		listView = (MarketListView) view.findViewById(R.id.update_listview);
		updateAllBtn = (Button) view.findViewById(R.id.update_all_btn);
		updateAllBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// 全部更新
				for (UpdateAppInfo updateAppInfo : updateAppInfos)
				{
					DownloadInfo autoUpdateDownloadInfo = AutoUpdateManager
							.getInstance().downloadManager
							.queryDownload(updateAppInfo.getPackageName());
					if (autoUpdateDownloadInfo != null
							&& autoUpdateDownloadInfo.getState() == DownloadInfo.STATE_FINISH)
					{
						// 如果自动下载列表中有下载任务 并且已经下载完成 则直接安装自动下载列表中的任务
						softwareManager
								.installApkByDownloadInfo(autoUpdateDownloadInfo);
					} else
					{
						DownloadInfo downloadInfo = DownloadManager
								.shareInstance().queryDownload(
										updateAppInfo.getPackageName());
						if (downloadInfo != null
								&& downloadInfo.getState() == DownloadInfo.STATE_FINISH)
						{
							// 如果下载已完成 则直接安装
							softwareManager
									.installApkByDownloadInfo(downloadInfo);
						} else
						{
							// 添加新的下载任务
							DownloadManager.shareInstance().addDownload(
									updateAppInfo.toDownloadInfo());
						}
					}
				}
			}
		});

		updateAppInfos = new ArrayList<UpdateAppInfo>();

		adapter = new UpdateAdapter(getActivity(), listView, updateAppInfos,
				handler, action);
		listView.setAdapter(adapter);
		refresh();

		IntentFilter filter = new IntentFilter(
				Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS);
		getActivity().registerReceiver(receiver, filter);
	}

	/**
	 * 获取列表显示数据
	 */
	private void getListData()
	{
		List<UpdateAppInfo> list = new ArrayList<UpdateAppInfo>();

		for (Entry<String, UpdateAppInfo> entry : softwareManager
				.getUpdateAppInfos().entrySet())
		{
			if (entry.getValue().isPromptUpgreade())
			{
				list.add(entry.getValue());
			}
		}
		updateAppInfos.clear();
		updateAppInfos.addAll(list);
	}

	/**
	 * 重新刷新界面
	 */
	public void refresh()
	{
		getListData();
		if (updateAppInfos.isEmpty())
		{
			parentActivity.setCurPage(1);
			errorViewLayout.setErrorView(R.drawable.no_update_and_no_ignore,
					getResources().getString(R.string.no_update_apps), "");
			errorViewLayout.setBtnVisible(false);
			errorViewLayout.setVisibility(View.VISIBLE);
			errorViewLayout.setRefrushOnClickListener(null);
			centerViewLayout.setVisibility(View.GONE);
		} else
		{
			errorViewLayout.setVisibility(View.GONE);
			centerViewLayout.setVisibility(View.VISIBLE);
			adapter.notifyDataSetChanged();
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(
					Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS))
			{
				refresh();
			}
		}
	};

	@Override
	public void onDestroy()
	{
		if (adapter != null)
		{
			adapter.onDestory();
		}
		if (getActivity() != null)
		{
			getActivity().unregisterReceiver(receiver);
		}
		super.onDestroy();
	}
}
