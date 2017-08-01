package com.xd.leplay.store.gui.download.adapter;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.widget.BaseExpandableListAdapter;

import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.download.DownloadListener;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.view.download.AppStateConstants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 可展开的下载管理列表基础适配器类
 * 
 * @author lung
 * 
 * @param <T>
 */
public abstract class DownloadExpandableBaseAdapter<T extends DownloadViewHolder>
		extends BaseExpandableListAdapter
{
	/** 装载试图的列表 */
	protected List<T> holdersList = null;

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

	protected static Context context = null;

	// /** 广播接收类 */
	// protected AutoInstallBroadCastReceive receiver = null;

	/** 软件管理初始化完成广播接收器 */
	protected SoftManagerInitFinishBroadCastReceive softManagerInitFinishBroadCastReceiver = null;

	/** 软件管理 */
	protected SoftwareManager softwareManager = null;

	/** 打印日志的标识 */
	private final String Tag = "DownloadExpandableBaseAdapter";

	/** 是否刷新进度 */
	protected boolean isUpdateProgress = false;

	/** 是否进行排序 */
	private boolean isSort = false;

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

	/**
	 * 设置是否�?��排序
	 * 
	 * @param isSort
	 */
	public void setSort(boolean isSort)
	{
		this.isSort = isSort;
	}

	/**
	 * 注销监听
	 */
	public void unregisterListener()
	{
		if (listener != null && manager != null)
		{
			manager.unregisterDownloadListener(listener);
			try
			{
				// unRegisterAutoStallBroadCast();
				unRegisterSoftManagerFinishBroadCast();
			} catch (Exception e)
			{
				DLog.e(Tag, "broadcase not register");
			}

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
	// receiver = new AutoInstallBroadCastReceive();
	// // 实例化过滤器并设置要过滤的广播
	// IntentFilter intentFilter = new IntentFilter(
	// SoftwareManager.AUTOINSTALL_OR_UNINSTALL_ACTION);
	// // 注册广播
	// context.registerReceiver(receiver, intentFilter);
	// }

	/**
	 * 注册广播
	 */
	private void registerSoftManagerFinisgBroadCast(Context context)
	{
		softManagerInitFinishBroadCastReceiver = new SoftManagerInitFinishBroadCastReceive();
		// 实例化过滤器并设置要过滤的广�?
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
	 * 构�?方法
	 */
	public DownloadExpandableBaseAdapter(Context context)
	{
		this.context = context;
		initHandler();
		initListener();
		manager = DownloadManager.shareInstance();
		softwareManager = SoftwareManager.getInstance();
		registerListener();
		imageLoader = ImageLoaderManager.getInstance();
		options = DisplayUtil.getListIconImageLoaderOptions();
		holdersList = getHoldersList();
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
				sentMessage(info, HANDLER_REFRESH_VIEW_DELETE);
			}

			@Override
			public void onStateChange(int state, DownloadInfo info)
			{
				DLog.v(Tag, "----onStateChange---" + info.getPackageName()
						+ "下载状态改变 " + info.getState() + "   now state ="
						+ state);
				if (state != DownloadInfo.STATE_FINISH)
				{
					sentMessage(info, HANDLER_REFRESH_VIEW);
				} else
				{
					sentMessage(info, HANDLER_REFRESH_VIEW_DELETE);
				}
			}

			@Override
			public void onProgress(int percent, DownloadInfo info)
			{
				DLog.v(Tag, "----onProgress---" + info.getPackageName()
						+ "下载进度 " + info.getPercent() + "  now percent ="
						+ percent);
				if (isUpdateProgress)
				{
					sentMessage(info, HANDLER_REFRESH_VIEW);
				}
			}
		};

	}

	/**
	 * 发�?更新界面的消�?
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
					refreshData(info);
				} else if (msg.what == HANDLER_REFRESH_VIEW_DELETE)
				{
					refreshData(info);
					holdersList.clear();
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
	 * 设置key并添加到视图集合
	 * 
	 * @param key
	 *            唯一标识
	 * @param holder
	 */
	protected void addToViewHolder(String key, T holder)
	{
		holder.setKey(key);
		if (!isContainHolder(holder))
		{
			holdersList.add(holder);
			// DLog.d(Tag,
			// "---addToViewHolder ---" + "addHolder  " + holder.getKey());
		}
	}

	/**
	 * 判断是否已经包含了holder
	 * 
	 * @param holder
	 * @return
	 */
	private boolean isContainHolder(T holder)
	{
		if (holdersList.size() != 0)
		{
			for (T nowHolder : holdersList)
			{
				// DLog.d(Tag,
				// "---isContainHolder ---" + "holder key =  " +
				// holder.getKey()+"now key = "+nowHolder.getKey());
				if (nowHolder.getKey().equals(holder.getKey()))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 移除删除的holder
	 * 
	 * @param key
	 *            唯一标识
	 */
	public void removeHolder(String key)
	{
		for (int i = 0; i < holdersList.size(); i++)
		{
			T holder = holdersList.get(i);
			if (holder.getKey().equals(key))
			{
				holdersList.remove(i);
				return;
			}
		}
	}

	/**
	 * 外部更新状态
	 * 
	 * @param info
	 *            下载任务
	 * 
	 */
	private void refreshData(DownloadInfo info)
	{
		T holder = matchingTheRightHolder(info);
		if (holder != null)
		{
			setDownloadData(holder, info);
		}
	}

	/**
	 * 匹配正确的holder
	 * 
	 * @param info
	 * @return
	 */
	protected T matchingTheRightHolder(DownloadInfo info)
	{
		if (info == null)
		{
			return null;
		}
		if (holdersList == null)
		{
			DLog.d(Tag, "---matchingTheRightHolder ---" + "holdersList = null");
			return null;
		} else if (holdersList.size() == 0)
		{
			DLog.d(Tag, "---matchingTheRightHolder ---"
					+ "holdersList.size() == 0");
			return null;
		}
		for (T holder : holdersList)
		{
			if (holder.getKey().equals(info.getPackageName()))
			{
				return holder;
			}
		}
		return null;
	}

	/**
	 * 得到holderList
	 * 
	 * @return
	 */
	protected abstract List<T> getHoldersList();

	/**
	 * 设置下载过程中的数据
	 * 
	 * @param holder
	 *            具体哪一个更新
	 * @param info
	 *            下载任务
	 */
	protected abstract void setDownloadData(T holder, DownloadInfo info);

	// /**
	// * 广播接收安装或�?卸载状�?
	// *
	// * @author lung
	// *
	// */
	// class AutoInstallBroadCastReceive extends BroadcastReceiver
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
	// // 如果是卸�?
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
	// refreshData(info);
	// notifyDataSetChanged();
	// } else
	// {
	// refreshData(info);
	// }
	//
	// }
	// }
	//
	// }
	// }

	/**
	 * 广播接收软件管理初始化完成广播
	 * 
	 * @author lilijun
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
		return appState;
	}

	public void onDestroy()
	{
		try
		{
			unregisterListener();
			// unRegisterAutoStallBroadCast();
			unRegisterSoftManagerFinishBroadCast();
		} catch (Exception e)
		{
			DLog.d(Tag, "DownloadExpandableBaseAdapter.onDestroy", e);
		}
	}
}
