package com.xd.leplay.store.gui.manager.update;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.download.DownloadListener;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.model.UpdateAppInfo;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.view.download.AppStateConstants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * @类描述：下载管理列表基础适配器
 * @作者：lilijun
 * @日期： 2014-2-12 上午10:11:58
 * 
 */
public abstract class UpdateDownloadBaseAdapter<T extends AbsListView> extends
		BaseAdapter
{

	/** 列表数据 */
	protected ArrayList<? extends UpdateAppInfo> appInfos;

	/** 下载监听器 */
	protected DownloadListener listener = null;

	/** 下载管理器 */
	protected DownloadManager manager = null;

	/** 刷新界面的handler */
	protected Handler handler = null;

	/** 刷新界面 */
	private final int HANDLER_REFRESH_VIEW = 0;

	/** 删除任务 */
	private final int HANDLER_REFRESH_VIEW_DELETE = 1;

	/** 下载列表加载图片的配置 */
	protected DisplayImageOptions options = null;

	/** 图片加载器 */
	protected ImageLoaderManager imageLoader = null;

	/** 上下文 */
	protected Context context = null;

	// /** 广播接收器 */
	// protected InstallBroadCastReceive receiver = null;

	/** 软件管理初始化完成广播接收器 */
	protected SoftManagerInitFinishBroadCastReceive softManagerInitFinishBroadCastReceiver = null;

	/** 软件管理 */
	protected SoftwareManager softwareManager = null;

	/** 打印日志的标签 */
	private final String Tag = "DownloadBaseAdapter";

	/** 是否刷新进度条 */
	private boolean isUpdateProgress = false;

	// /** 是否需要排序 */
	// private boolean isSort = false;

	/** 列表 */
	protected T listView;

	public List<? extends UpdateAppInfo> getAppInfos()
	{
		return appInfos;
	}

	public void setAppInfos(ArrayList<? extends UpdateAppInfo> appInfos)
	{
		this.appInfos = appInfos;
	}

	@Override
	public Object getItem(int position)
	{
		return position;
	}

	@Override
	public int getCount()
	{
		if (appInfos == null)
		{
			return 0;
		}
		return appInfos.size();
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	public boolean isUpdateProgress()
	{
		return isUpdateProgress;
	}

	/**
	 * 设置是否更新下载进度
	 * 
	 * @param isUpdateProgress
	 */
	public void setUpdateProgress(boolean isUpdateProgress)
	{
		this.isUpdateProgress = isUpdateProgress;
	}

	// /**
	// * 设置是否需要排序
	// *
	// * @param isSort
	// */
	// public void setSort(boolean isSort)
	// {
	// this.isSort = isSort;
	// }

	/**
	 * 销毁时调用
	 */
	public void onDestory()
	{
		unRegisterListener();
		listView = null;
		handler = null;
		manager = null;
		context = null;

	}

	/**
	 * 注销监听
	 */
	public void unRegisterListener()
	{
		if (listener != null && manager != null)
		{
			manager.unregisterDownloadListener(listener);

		}
		try
		{
			// unRegisterAutoStallBroadCast();
			unRegisterSoftManagerFinishBroadCast();
		} catch (Exception e)
		{
			DLog.e(Tag, "broadcase not register");
		}
	}

	/**
	 * 添加到监听器
	 */
	public void registerListener()
	{
		if (listener != null && manager != null)
		{
			manager.registerDownloadListener(listener);
			// registerAutoStallBroadCast(context);
			registerSoftManagerFinisgBroadCast(context);
		}
	}

	// /**
	// * 注册广播
	// */
	// private void registerAutoStallBroadCast(Context context)
	// {
	// receiver = new InstallBroadCastReceive();
	// // 实例化过滤器并设置要过滤的广播
	// IntentFilter filter = new IntentFilter();
	// filter.addAction(Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS);
	// filter.addAction(Constants.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS);
	// // 注册广播
	// context.registerReceiver(receiver, filter);
	// }

	/**
	 * 注册广播
	 */
	private void registerSoftManagerFinisgBroadCast(Context context)
	{
		softManagerInitFinishBroadCastReceiver = new SoftManagerInitFinishBroadCastReceive();
		// 实例化过滤器并设置要过滤的广播
		IntentFilter intentFilter = new IntentFilter(
				Constants.ACTION_SOFTWARE_MANAGER_INIT_DONE);
		// 注册广播
		context.registerReceiver(softManagerInitFinishBroadCastReceiver,
				intentFilter);
	}

	// /**
	// * 取消注册广播
	// */
	// private void unRegisterAutoStallBroadCast()
	// {
	// if (receiver != null)
	// {
	// context.unregisterReceiver(receiver);
	// }
	// }

	/**
	 * 取消注册广播
	 */
	private void unRegisterSoftManagerFinishBroadCast()
	{
		if (softManagerInitFinishBroadCastReceiver != null)
		{
			context.unregisterReceiver(softManagerInitFinishBroadCastReceiver);
		}
	}

	/**
	 * 构造方法
	 */
	public UpdateDownloadBaseAdapter(Context context, T listView,
			ArrayList<? extends UpdateAppInfo> appInfos)
	{
		this.context = context;
		this.listView = listView;
		this.appInfos = appInfos;
		initHandler();
		initListener();
		manager = DownloadManager.shareInstance();
		softwareManager = SoftwareManager.getInstance();
		registerListener();
		imageLoader = ImageLoaderManager.getInstance();
		options = DisplayUtil.getListIconImageLoaderOptions();
		// holdersList = getHoldersList();
	}

	/**
	 * 初始化监听器
	 */
	private void initListener()
	{
		listener = new DownloadListener()
		{

			@Override
			public void onTaskCountChanged(int state, DownloadInfo info)
			{
				DLog.v(Tag,
						"----onTaskCountChanged--- " + "name" + info.getName()
								+ " state =" + state);
				sentMessage(info, HANDLER_REFRESH_VIEW_DELETE);
			}

			@Override
			public void onStateChange(int state, DownloadInfo info)
			{
				DLog.v(Tag, "----onStateChange---" + info.getPackageName()
						+ "下载状态为  " + info.getState() + "   now state ="
						+ state);
				if (state != DownloadInfo.STATE_FINISH)
				{
					sentMessage(info, HANDLER_REFRESH_VIEW);
				}
				// else if (!Constants.canAutoInstall)
				// {
				// sentMessage(info, HANDLER_REFRESH_VIEW);
				// }
				else
				{
					sentMessage(info, HANDLER_REFRESH_VIEW_DELETE);
				}
			}

			@Override
			public void onProgress(int percent, DownloadInfo info)
			{
				DLog.v(Tag, "----onProgress---" + info.getPackageName()
						+ "下载进度为  " + info.getPercent() + "  now percent ="
						+ percent);
				DLog.i("lilijun", "进度进度-----》》" + percent);
				if (isUpdateProgress)
				{
					sentMessage(info, HANDLER_REFRESH_VIEW);
				}
			}
		};

	}

	/**
	 * 发送更新界面的消息
	 * 
	 * @param info
	 * @param what
	 * 
	 */
	private void sentMessage(DownloadInfo info, int what)
	{
		Message message = new Message();
		message.what = what;
		message.obj = info;
		handler.sendMessage(message);
	}

	/**
	 * 初始化handler
	 */
	private void initHandler()
	{
		handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				DownloadInfo info = (DownloadInfo) msg.obj;
				if (msg.what == HANDLER_REFRESH_VIEW)
				{
					DLog.i("lilijun", "刷新刷新...");
					matchDownloadInfo(info);
				} else if (msg.what == HANDLER_REFRESH_VIEW_DELETE)
				{

					notifyDataSetChanged();
				}
			}
		};

	}

	// /**
	// * 设置图片加载配置
	// *
	// * @return
	// */
	// protected DisplayImageOptions getImageLoaderOptions()
	// {
	// DisplayImageOptions options = new DisplayImageOptions.Builder()
	// .showImageOnLoading(R.drawable.default_icon)
	// .showImageForEmptyUri(R.drawable.default_icon)
	// .showImageOnFail(R.drawable.default_icon).cacheOnDisk(true)
	// .cacheInMemory(true).build();
	// return options;
	// }

	/**
	 * 刷新数据
	 * 
	 * @param info
	 * @param view
	 */
	protected abstract void refreshData(DownloadInfo info, View view);

	// /**
	// * 广播接收安装或者卸载状态
	// *
	// * @author lung
	// *
	// */
	// class InstallBroadCastReceive extends BroadcastReceiver
	// {
	//
	// @Override
	// public void onReceive(Context context, Intent intent)
	// {
	// if (intent.getAction().equals(
	// SoftwareManager.AUTOINSTALL_OR_UNINSTALL_ACTION))
	// {
	// DownloadInfo info = (DownloadInfo) intent
	// .getSerializableExtra(SoftwareManager.DOWNLOADINFO);
	// int state = intent.getIntExtra(SoftwareManager.STATE,
	// ApkInstallUtil.AUTO_INSTALL_FAILED);
	// // 如果是卸载
	// if (state == SoftwareManager.UNINSTALLED)
	// {
	// sentMessage(info, HANDLER_REFRESH_VIEW_DELETE);
	// } else if (state != ApkInstallUtil.AUTO_INSTALL_SUCCESS)
	// {
	// DLog.d(Tag, "---AutoInstallBroadCastReceive onReceive---"
	// + "info packageName =" + info.getPackageName()
	// + "state = " + state);
	// if (isSort)
	// {
	// matchDownloadInfo(info);
	// notifyDataSetChanged();
	// } else
	// {
	// matchDownloadInfo(info);
	// }
	//
	// }
	// }
	//
	// }
	// }

	/**
	 * @param downloadInfo
	 */
	private void matchDownloadInfo(final DownloadInfo downloadInfo)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				final View view = matchViewToRefreshData(downloadInfo);
				if (view != null)
				{
					handler.post(new Runnable()
					{

						@Override
						public void run()
						{
							refreshData(downloadInfo, view);

						}
					});
				}

			}
		}).start();
	}

	/**
	 * 匹配
	 * 
	 * @param downloadInfo
	 */
	protected View matchViewToRefreshData(DownloadInfo downloadInfo)
	{
		if (listView == null)
		{
			return null;
		}
		int firstPosition = listView.getFirstVisiblePosition();
		int lastPosition = listView.getLastVisiblePosition();

		if (appInfos == null)
		{
			return null;
		}
		@SuppressWarnings("unchecked")
		ArrayList<? extends UpdateAppInfo> list = (ArrayList<? extends UpdateAppInfo>) appInfos
				.clone();
		if (list == null || list.size() == 0)
		{
			return null;
		}

		if (listView instanceof ListView)
		{
			firstPosition = listView.getFirstVisiblePosition()
					- ((ListView) listView).getHeaderViewsCount() <= 0 ? 0
					: listView.getFirstVisiblePosition()
							- ((ListView) listView).getHeaderViewsCount();
			lastPosition = lastPosition
					- ((ListView) listView).getHeaderViewsCount() >= list
					.size() ? list.size() - 1 : lastPosition
					- ((ListView) listView).getHeaderViewsCount();
		}

		for (int i = firstPosition; i <= lastPosition; i++)
		{
			DLog.d("lung", "matchViewToRefreshData i =" + i);
			if (list.get(i).getPackageName()
					.equals(downloadInfo.getPackageName()))
			{
				// refreshData(downloadInfo,
				// listView.getChildAt(i - firstPosition));
				DLog.d("lung", "刷新数据  --- i =" + i);

				if (listView instanceof ListView)
				{
					DLog.d("lung", "刷新数据" + downloadInfo.getState());
					if (listView.getFirstVisiblePosition() <= ((ListView) listView)
							.getHeaderViewsCount())
					{
						return listView.getChildAt(i
								- listView.getFirstVisiblePosition()
								+ ((ListView) listView).getHeaderViewsCount());
					}

				}
				return listView.getChildAt(i - firstPosition);

			}
		}
		return null;
	}

	/**
	 * 广播接收软件管理初始化完成广播
	 * 
	 * @author lung
	 * 
	 */
	class SoftManagerInitFinishBroadCastReceive extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			// 如果软件管理初始化完成，刷新
			if (intent.getAction().equals(
					Constants.ACTION_SOFTWARE_MANAGER_INIT_DONE))
			{
				DLog.d(Tag,
						"---SoftManagerInitFinishBroadCastReceive onReceive---"
								+ "初始化完成");
				notifyDataSetChanged();
			}
		}
	}

	/**
	 * 通过包名获取app的状态
	 * 
	 * @param packageName
	 *            包名
	 * @return
	 */
	protected int getAppState(String packageName)
	{
		DownloadInfo info = manager.queryDownload(packageName);
		int appState = AppStateConstants.APP_NO_INSTALL;
		if (info != null)
		{
			appState = AppStateConstants.getAppState(
					AppStateConstants.TYPE_DOWNLOAD, info.getState());
		} else
		{
			appState = AppStateConstants.getAppState(
					AppStateConstants.TYPE_SOFTMANAGER,
					softwareManager.getStateByPackageName(packageName));
		}
		// DLog.v(Tag, "---getAppState ---" + "appState =  " + appState);
		return appState;
	}
}
