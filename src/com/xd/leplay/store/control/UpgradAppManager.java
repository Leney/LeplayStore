package com.xd.leplay.store.control;

import android.content.Context;

import com.xd.download.DownloadManager;

/**
 * 强制升级下载管理类
 * 
 * @author lilijun
 *
 */
public class UpgradAppManager
{
	private static UpgradAppManager instance = null;

	public DownloadManager downloadManager = null;

	// private DownloadListener listener = null;

	// private SoftwareManager softwareManager = null;

	public static UpgradAppManager getInstance()
	{
		if (instance == null)
		{
			synchronized (UpgradAppManager.class)
			{
				instance = new UpgradAppManager();
			}
		}
		return instance;
	}

	public void init(Context context)
	{
		downloadManager = new DownloadManager();
		downloadManager.init(context, "upgreadApk.db", "upgreadApk");
		// softwareManager = SoftwareManager.getInstance();
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

	// /**
	// * 开始自动更新
	// */
	// public void autoUpdate()
	// {
	// for (Entry<String, UpdateAppInfo> entry : softwareManager
	// .getUpdateAppInfos().entrySet())
	// {
	// DownloadInfo downloadInfo = entry.getValue().toDownloadInfo();
	// downloadInfo
	// .setAction(DataCollectionConstant.DATA_COLLECTION_AUTO_UPDATE_DOWNLOAD);
	// // 添加自动更新任务
	// downloadManager.addDownload(downloadInfo);
	// }
	// }
}
