package com.xd.leplay.store.control;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.xd.download.DownloadInfo;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.model.UpdateAppInfo;

/**
 * 通知栏按钮点击事件的接收广播类
 * 
 * @author lilijun
 *
 */
public class NotifyBtnClickReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (Constants.ACTION_NOTIFY_UPDATE_ALL_BTN_CLICK.equals(intent
				.getAction()))
		{
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(112);
			collapseStatusBar(context);
			List<UpdateAppInfo> updateAppInfos = new ArrayList<UpdateAppInfo>();
			updateAppInfos.addAll(getUpdateInfos());
			// 在更新通知栏点击了 "一键更新" 按钮
			for (UpdateAppInfo updateAppInfo : updateAppInfos)
			{
				DownloadInfo autoUpdateDownloadInfo = AutoUpdateManager
						.getInstance().downloadManager
						.queryDownload(updateAppInfo.getPackageName());
				if (autoUpdateDownloadInfo != null
						&& autoUpdateDownloadInfo.getState() == DownloadInfo.STATE_FINISH)
				{
					// 如果自动下载列表中有下载任务 并且已经下载完成 则直接安装自动下载列表中的任务
					SoftwareManager.getInstance().installApkByDownloadInfo(
							autoUpdateDownloadInfo);
				} else
				{
					DownloadInfo downloadInfo = DownloadManager.shareInstance()
							.queryDownload(updateAppInfo.getPackageName());
					if (downloadInfo != null)
					{
						if (downloadInfo.getState() == DownloadInfo.STATE_FINISH)
						{
							// 如果下载已完成 则直接安装
							SoftwareManager.getInstance()
									.installApkByDownloadInfo(downloadInfo);
						} else
						{
							DownloadManager.shareInstance().addDownload(
									updateAppInfo.toDownloadInfo());
						}
					}
				}
			}
		}
	}

	/**
	 * 获取列表显示数据
	 */
	private List<UpdateAppInfo> getUpdateInfos()
	{
		List<UpdateAppInfo> list = new ArrayList<UpdateAppInfo>();

		for (Entry<String, UpdateAppInfo> entry : SoftwareManager.getInstance()
				.getUpdateAppInfos().entrySet())
		{
			if (entry.getValue().isPromptUpgreade())
			{
				list.add(entry.getValue());
			}
		}
		return list;
	}

	/**
	 * 收起通知栏方法
	 * 
	 * @param context
	 */
	private void collapseStatusBar(Context context)
	{
		try
		{
			Object statusBarManager = context.getSystemService("statusbar");
			Method collapse;
			if (Build.VERSION.SDK_INT <= 16)
			{
				collapse = statusBarManager.getClass().getMethod("collapse");
			} else
			{
				collapse = statusBarManager.getClass().getMethod(
						"collapsePanels");
			}
			collapse.invoke(statusBarManager);
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
	}

}
