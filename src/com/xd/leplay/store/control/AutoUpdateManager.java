package com.xd.leplay.store.control;

import android.content.Context;

import com.xd.download.DownloadInfo;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.model.UpdateAppInfo;

import java.util.Map.Entry;

/**
 * 自动下载更新的管理类
 * 
 * @author lilijun
 *
 */
public class AutoUpdateManager
{
	private static AutoUpdateManager instance = null;

	public DownloadManager downloadManager = null;

	// private DownloadListener listener = null;

	private SoftwareManager softwareManager = null;

	public static AutoUpdateManager getInstance()
	{
		if (instance == null)
		{
			synchronized (AutoUpdateManager.class)
			{
				instance = new AutoUpdateManager();
			}
		}
		return instance;
	}

	public void init(Context context)
	{
		downloadManager = new DownloadManager();
		downloadManager.init(context, "updateApk.db", "updateApk");
		softwareManager = SoftwareManager.getInstance();
		// initDownloadLinstener();
		// downloadManager.registerDownloadListener(listener);
	}

	// private void initDownloadLinstener()
	// {
	// listener = new DownloadListener()
	// {
	// @Override
	// public void onTaskCountChanged(int state, DownloadInfo info)
	// {
	// }
	//
	// @Override
	// public void onStateChange(int state, DownloadInfo info)
	// {
	// }
	//
	// @Override
	// public void onProgress(int percent, DownloadInfo info)
	// {
	// }
	// };
	// }

	/**
	 * 开始自动更新
	 */
	public void autoUpdate()
	{
		for (Entry<String, UpdateAppInfo> entry : softwareManager
				.getUpdateAppInfos().entrySet())
		{
			DownloadInfo queryDownloadInfo = downloadManager
					.queryDownload(entry.getValue().getPackageName());
			if (queryDownloadInfo != null)
			{
				if (queryDownloadInfo.getUpdateVersionCode() >= entry
						.getValue().getUpdateVersionCode())
				{
					// 本地已有下载任务的更新版本号和刚从服务器得到的更新信息的版本号相同
					// 之前有下载任务
					if (DownloadInfo.STATE_FINISH != queryDownloadInfo
							.getState())
					{
						// 之前未完成,继续下载
						downloadManager
								.reDownloadLostedFinishedTask(queryDownloadInfo);
					}
				} else
				{
					// 本地已有下载任务已经是旧版本，则更新新版本
					// 删除已下载的版本
					downloadManager.deleteDownload(queryDownloadInfo);
					DownloadInfo downloadInfo = entry.getValue()
							.toDownloadInfo();
					downloadInfo
							.setAction(DataCollectionConstant.DATA_COLLECTION_AUTO_UPDATE_DOWNLOAD);
					// 添加自动更新任务
					downloadManager.addDownload(downloadInfo);
				}
			} else
			{
				DownloadInfo downloadInfo = entry.getValue().toDownloadInfo();
				downloadInfo
						.setAction(DataCollectionConstant.DATA_COLLECTION_AUTO_UPDATE_DOWNLOAD);
				// 添加自动更新任务
				downloadManager.addDownload(downloadInfo);
			}
		}
	}
}
