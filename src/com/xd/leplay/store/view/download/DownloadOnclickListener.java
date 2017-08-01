package com.xd.leplay.store.view.download;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.control.AutoUpdateManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LeplayPreferences;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.model.DetailAppInfo;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.model.UpdateAppInfo;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.NetUtil;
import com.xd.leplay.store.util.ToolsUtil;

public class DownloadOnclickListener implements OnClickListener
{
	private DownloadInfo downloadInfo = null;

	// private ListAppInfo appInfo;

	private Context context;

	private DownloadManager manager;

	private SoftwareManager softwareManager;

	private String action = "";

	public DownloadOnclickListener(Context context, String action)
	{
		this.context = context;
		this.action = action;
		manager = DownloadManager.shareInstance();
		softwareManager = SoftwareManager.getInstance();
	}

	public DownloadOnclickListener(Context context, DownloadInfo info,
			String action)
	{
		// admin/admin-apps3
		this.downloadInfo = info;
		this.context = context;
		this.action = action;
		manager = DownloadManager.shareInstance();
		softwareManager = SoftwareManager.getInstance();
	}

	public DownloadOnclickListener(Context context, ListAppInfo appInfo,
			String action)
	{
		this.context = context;
		this.action = action;
		manager = DownloadManager.shareInstance();
		softwareManager = SoftwareManager.getInstance();
		this.downloadInfo = manager.queryDownload(appInfo.getPackageName());
		// this.appInfo = appInfo;
	}

	public DownloadOnclickListener(Context context,
			DetailAppInfo detailAppInfo, String action)
	{
		this.context = context;
		this.action = action;
		manager = DownloadManager.shareInstance();
		softwareManager = SoftwareManager.getInstance();
		this.downloadInfo = detailAppInfo.toDownloadInfo();
		// this.appInfo = appInfo;
	}

	/**
	 * 监听器通过下载任务设置下载信息
	 * 
	 * @param info
	 *            下载信息
	 */
	public void setDownloadListenerInfo(DownloadInfo info)
	{
		this.downloadInfo = info;
	}

	/**
	 * 监听器通过下载任务设置下载信息
	 * 
	 * @param info
	 *            app信息
	 */
	public void setDownloadListenerInfo(ListAppInfo appInfo)
	{
		this.downloadInfo = appInfo.toDownloadInfo();
	}

	/**
	 * 监听器通过下载任务设置下载信息
	 * 
	 * @param info
	 *            app信息
	 */
	public void setDownloadListenerInfo(UpdateAppInfo appInfo)
	{
		this.downloadInfo = appInfo.toDownloadInfo();
	}

	@Override
	public void onClick(View v)
	{
		DLog.i("lilijun", "点击下载按钮。。。。。");
		if (downloadInfo == null)
		{
			return;
		}
		DownloadInfo info = manager
				.queryDownload(downloadInfo.getPackageName());
		if (info != null)
		{
			DLog.i("lilijun", "当前有下载任务....");
			/* 如果存在下载任务 */
			switch (info.getState())
			{
			// 下载 等待
			case DownloadInfo.STATE_WAIT:
				// // 设置为不可点击
				// downloadImg.setClickable(false);
				// downloadText.setClickable(false);
				// setClickable(false);

				// 等待，点击暂停
				manager.stopDownload(info);

				break;
			case DownloadInfo.STATE_DOWNLOADING:
				// 正在下载中,点击则暂停
				manager.stopDownload(info);
				break;
			case DownloadInfo.STATE_STOP:
				// 下载停止状态，点击则继续下载
			case DownloadInfo.STATE_ERROR:
				// 下载出错状态，点击则继续下载
				// if (ToolsUtil.checkNetworkValid(context))
				// {
				// DownloadInfo newDownloadInfo = new DownloadInfo();
				// newDownloadInfo = info;
				// manager.deleteDownload(info);
				// manager.addDownload(newDownloadInfo);
				// }
				if (ToolsUtil.checkNetworkValid(context))
				{
					if (ToolsUtil.checkMemorySize(context, info))
					{
						manager.addDownload(info);
					}
				}
				break;
			case DownloadInfo.STATE_FINISH:
				// 下载完成状态，点击则安装应用
				// softwareManager.installLocalApk(DownloadManager.shareInstance(),
				// info);
				File file = new File(info.getPath());
				if (file.exists())
				{
					softwareManager.installApkByDownloadInfo(info);
				} else
				{
					// 显示重新下载的弹出框
					DisplayUtil.showReDownloadDialog(context, info, false);
				}
				break;
			}
		} else
		{
			DLog.i("lilijun", "当前没有下载任务....");
			DownloadInfo updateDownloadInfo = AutoUpdateManager.getInstance().downloadManager
					.queryDownload(downloadInfo.getPackageName());
			if (updateDownloadInfo != null
					&& updateDownloadInfo.getState() == DownloadInfo.STATE_FINISH)
			{
				DLog.i("lilijun", "自动更新下载列表中有下载任务，并且已经下载完成....");
				/* 如果存在下载任务 */
				// 下载完成状态，点击则安装应用
				// softwareManager.installLocalApk(DownloadManager.shareInstance(),
				// info);
				File file = new File(updateDownloadInfo.getPath());
				if (file.exists())
				{
					softwareManager
							.installApkByDownloadInfo(updateDownloadInfo);
				} else
				{
					// 显示重新下载的弹出框
					DisplayUtil.showReDownloadDialog(context,
							updateDownloadInfo, true);
				}
			} else
			{
				if (updateDownloadInfo != null)
				{
					AutoUpdateManager.getInstance().downloadManager
							.deleteDownload(updateDownloadInfo);
					// if (ToolsUtil.checkNetworkValid(context))
					// {
					// if (ToolsUtil.checkMemorySize(context, downloadInfo))
					// {
					// manager.addDownload(downloadInfo);
					// }
					// }
				}
				/* 如果不存在存在下载任务 */
				int state = softwareManager.getStateByPackageName(downloadInfo
						.getPackageName());
				switch (state)
				{
				// 已安装
				case SoftwareManager.STATE_INSTALLED:
					DLog.i("lilijun", "已安装...");
					// 打开应用
					// softwareManager.openSoftware(context, packageName);
					ToolsUtil.openSoftware(context,
							downloadInfo.getPackageName());
					break;
				// 未安装
				case SoftwareManager.STATE_NO_INSTALLED:
					DLog.i("lilijun", "未安装...");
					// 无该下载任务，且未安装，点击则下载
					// if (ToolsUtil.checkNetworkValid(context)
					// && Util.checkGprslimt(context, downloadInfo.getSize(),
					// downloadInfo))
					if (ToolsUtil.checkNetworkValid(context))
					{
						if (ToolsUtil.checkMemorySize(context, downloadInfo))
						{
							downloadInfo
									.setAction(DataCollectionManager
											.getAction(
													action,
													DataCollectionConstant.DATA_COLLECTION_LIST_CLICK_DOWNLOAD_BTN_VALUE));
							long softId = Long.parseLong(downloadInfo
									.getSoftId().trim());
							if (softId > 0)
							{
								DLog.i("lilijun", "开始更新下载次数！！！ softId--->>>"
										+ downloadInfo.getSoftId());
								NetUtil.updateDownloadCount(Long
										.parseLong(downloadInfo.getSoftId()
												.trim()));
							}
							manager.addDownload(downloadInfo);
							DataCollectionManager.getInstance().addRecord(
									downloadInfo.getAction(),
									DataCollectionManager.SOFT_ID,
									downloadInfo.getSoftId(),
									DataCollectionManager.PACK_ID,
									downloadInfo.getPackageId() + "");
							// 添加友盟数据统计
							HashMap<String, String> values = new HashMap<String, String>();
							values.put(DataCollectionManager.SOFT_ID,
									downloadInfo.getSoftId());
							values.put(DataCollectionManager.PACK_ID,
									downloadInfo.getPackageId() + "");
							DataCollectionManager
									.getInstance()
									.addYouMengEventRecord(
											context,
											downloadInfo.getAction(),
											DataCollectionConstant.EVENT_ID_ADD_NEW_DOWNLOAD_TASK,
											values);
						}
					}

					DLog.i("lilijun",
							"下载送的金币数-------->>" + downloadInfo.getIntegral());
					if (downloadInfo.getIntegral() > 0
							&& LeplayPreferences.getInstance(context)
									.isFirstDownloadWithUnlogin()
							&& !LoginUserInfoManager.getInstance()
									.isHaveUserLogin())
					{
						// 有送金币 并且之前未显示过这个Dialog 并且未登录
						// 显示下载送金币的弹出框
						DisplayUtil.showDownloadGetCoinDialog(context);
					}
					break;
				// 有更新
				case SoftwareManager.STATE_UPDATE:
					DLog.i("lilijun", "有更新...");
					// if (ToolsUtil.checkNetworkValid(context)
					// && Util.checkGprslimt(context, downloadInfo.getSize(),
					// downloadInfo))
					if (ToolsUtil.checkNetworkValid(context))
					{
						if (ToolsUtil.checkMemorySize(context, downloadInfo))
						{
							// // 如果签名不同，則弹出提示
							// if (appInfo != null && !appInfo.isSameSign())
							// {
							// showDialog();
							// } else
							// {
							// manager.addDownload(softwareManager
							// .getUpdateDownloadInfoByPackageName(downloadInfo
							// .getPackageName()));
							downloadInfo
									.setAction(DataCollectionManager
											.getAction(
													action,
													DataCollectionConstant.DATA_COLLECTION_LIST_CLICK_UPDATE_BTN_VALUE));
							manager.addDownload(downloadInfo);
							DataCollectionManager.getInstance().addRecord(
									downloadInfo.getAction(),
									DataCollectionManager.SOFT_ID,
									downloadInfo.getSoftId(),
									DataCollectionManager.PACK_ID,
									downloadInfo.getPackageId() + "");
							// 添加友盟数据统计
							HashMap<String, String> values = new HashMap<String, String>();
							values.put(DataCollectionManager.SOFT_ID,
									downloadInfo.getSoftId());
							values.put(DataCollectionManager.PACK_ID,
									downloadInfo.getPackageId() + "");
							DataCollectionManager
									.getInstance()
									.addYouMengEventRecord(
											context,
											downloadInfo.getAction(),
											DataCollectionConstant.EVENT_ID_ADD_NEW_DOWNLOAD_TASK,
											values);
							// }
						}
					}

					DLog.i("lilijun",
							"更新送的金币数-------->>" + downloadInfo.getIntegral());
					if (downloadInfo.getIntegral() > 0
							&& LeplayPreferences.getInstance(context)
									.isFirstDownloadWithUnlogin()
							&& !LoginUserInfoManager.getInstance()
									.isHaveUserLogin())
					{
						// 有送金币 并且之前未显示过这个Dialog 并且未登录
						// 显示下载送金币的弹出框
						DisplayUtil.showDownloadGetCoinDialog(context);
					}

					break;
				}
			}
		}

	}
}
