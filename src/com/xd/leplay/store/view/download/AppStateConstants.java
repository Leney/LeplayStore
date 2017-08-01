package com.xd.leplay.store.view.download;

import android.content.Context;

import com.xd.download.DownloadInfo;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.SoftwareManager;

/**
 * 下载按钮状态值常量类
 * 
 * @author lilijun
 *
 */
public class AppStateConstants
{
	private static final String TAG = "AppStateConstant";

	/** 下载类型 */
	public final static int TYPE_DOWNLOAD = 1000;

	/** 软件管理类型 */
	public final static int TYPE_SOFTMANAGER = 1001;

	/** 下载状态：创建一个下载任务的初始状态等待 */
	public static final int APP_DOWNLOAD_STATE_WAIT = 0;

	/** 下载状态：正在下载 */
	public static final int APP_DOWNLOAD_STATE_DOWNLOADING = 1;

	/** 下载状态：停止 */
	public static final int APP_DOWNLOAD_STATE_STOP = 2;

	/** 下载状态：完成 */
	public static final int APP_DOWNLOAD_STATE_FINISH = 3;

	/** 下载状态：下载出错 */
	public static final int APP_DOWNLOAD_STATE_ERROR = 4;

	/** 本地软件状态_未安装 */
	public static final int APP_NO_INSTALL = 5;

	/** 本地软件状态_已安装 */
	public static final int APP_INSTALLED = 6;

	/** 本地软件状态_有更新 */
	public static final int APP_UPDATE = 7;

	/** app状态文本 */
	public static String[] stateTexts;

	/** app状态字体颜色 */
	public static String[] stateTextColors;

	/** 任务状态文本 */
	private static String[] taskStateTexts;

	/** 任务状态文本色值 */
	private static String[] taskStateTextColors;

	// /** 当为任务状态时，已安装的状态(必须保证是从爱玩下载的app并安装成功的软件) */
	// public static final int TASK_APP_INSTALLED = TaskInfo.STATE_UN_DO + 1;

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public static void init(Context context)
	{
		AppStateConstants.stateTexts = context.getResources().getStringArray(
				R.array.app_state_texts);
		AppStateConstants.stateTextColors = context.getResources()
				.getStringArray(R.array.app_state_colors);
		taskStateTexts = context.getResources().getStringArray(
				R.array.task_state_texts);
		taskStateTextColors = context.getResources().getStringArray(
				R.array.task_state_text_colors);
	}

	/**
	 * 得到当前CP的状态
	 * 
	 * @param type
	 *            类型 TYPE_DOWNLOAD 为下载状态， TYPE_SOFTMANAGER 为软件管理状态
	 * @param state
	 *            不同类型对应的状态
	 * @return cp 状态
	 */
	public static int getAppState(int type, int state)
	{
		int appState = APP_NO_INSTALL;
		switch (type)
		{
		case TYPE_DOWNLOAD:
			// 下载类型
			if (state == DownloadInfo.STATE_WAIT)
			{
				appState = APP_DOWNLOAD_STATE_WAIT;
			} else if (state == DownloadInfo.STATE_DOWNLOADING)
			{
				appState = APP_DOWNLOAD_STATE_DOWNLOADING;
			} else if (state == DownloadInfo.STATE_STOP)
			{
				appState = APP_DOWNLOAD_STATE_STOP;
			} else if (state == DownloadInfo.STATE_FINISH)
			{
				appState = APP_DOWNLOAD_STATE_FINISH;
			} else if (state == DownloadInfo.STATE_ERROR)
			{
				appState = APP_DOWNLOAD_STATE_ERROR;
			}
			break;
		case TYPE_SOFTMANAGER:
			// 软件管理类型
			if (state == SoftwareManager.STATE_NO_INSTALLED)
			{
				appState = APP_NO_INSTALL;
			} else if (state == SoftwareManager.STATE_INSTALLED)
			{
				appState = APP_INSTALLED;
			} else if (state == SoftwareManager.STATE_UPDATE)
			{
				appState = APP_UPDATE;
			}
			break;
		}
		return appState;
	}

	/**
	 * 通过app状态得到对应的文字
	 * 
	 * @param appState
	 * @return
	 */
	public static String getAppStateText(int appState)
	{
		return stateTexts[appState];
	}

	/**
	 * 通过app状态得到对应的文字颜色
	 * 
	 * @param appState
	 * @return
	 */
	public static String getAppStateTextColor(int appState)
	{
		return stateTextColors[appState];
	}

	/**
	 * 获取任务状态显示的文本
	 * 
	 * @param taskState
	 * @return
	 */
	public static String getTaskStateText(int taskState)
	{
		return taskStateTexts[taskState];
	}

	/**
	 * 获取任务状态显示文本的颜色
	 * 
	 * @param taskState
	 * @return
	 */
	public static String getTaskStateTextColor(int taskState)
	{
		return taskStateTextColors[taskState];
	}

	/**
	 * 通过app状态得到对应的背景
	 * 
	 * @param appState
	 * @return
	 */
	public static int getAppStateBackgroundResource(int appState)
	{
		int rId = R.drawable.list_download_btn_bg1_shape;
		switch (appState)
		{
		case APP_DOWNLOAD_STATE_DOWNLOADING:
		case APP_NO_INSTALL:
		case APP_UPDATE:
		case APP_DOWNLOAD_STATE_WAIT:
			rId = R.drawable.list_download_btn_bg1_shape;
			break;
		case APP_DOWNLOAD_STATE_STOP:
		case APP_DOWNLOAD_STATE_FINISH:
		case APP_DOWNLOAD_STATE_ERROR:
			rId = R.drawable.list_download_btn_bg2_shape;
			break;
		case APP_INSTALLED:
			rId = R.drawable.list_download_btn_bg3_shape;
			break;
		default:
			rId = R.drawable.list_download_btn_bg1_shape;
			break;
		}
		return rId;
	}

//	/**
//	 * 通过task状态得到对应的背景
//	 *
//	 * @return
//	 */
//	public static int getTaskStateBackgroundResource(int state)
//	{
//		int rId = TaskInfo.STATE_NO_START;
//		switch (state)
//		{
//		// 可执行但未下载(做任务)
//		case TaskInfo.STATE_NO_DOWNLOAD:
//			rId = R.drawable.task_state_btn_bg1_shape;
//			break;
//		// // 之前下载并安装成功过任务应用，但未开始执行任务(打开)
//		// case TASK_APP_INSTALLED:
//		// 任务app初次下载完成、但未打开过
//		case TaskInfo.STATE_NO_START:
//			// 任务正在进行中(打开)
//		case TaskInfo.STATE_DOING:
//			rId = R.drawable.task_state_btn_bg2_shape;
//			break;
//		// 任务已完成(领取奖励)
//		case TaskInfo.STATE_DONE_AND_NOT_GET:
//			rId = R.drawable.task_state_btn_bg3_shape;
//			break;
//		// 任务已完成(已完成)
//		case TaskInfo.STATE_DONE:
//			rId = R.drawable.task_state_btn_bg4_shape;
//			break;
//		// 任务不可执行(未开始)
//		case TaskInfo.STATE_UN_DO:
//			rId = R.drawable.task_state_btn_bg5_shape;
//			break;
//		default:
//			rId = R.drawable.task_state_btn_bg1_shape;
//			break;
//		}
//		return rId;
//	}
}
