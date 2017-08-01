package com.xd.leplay.store.gui.details.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.download.DownloadListener;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.AutoUpdateManager;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.download.AppStateConstants;

public class DetailDownloadProgressButton extends FrameLayout implements
		OnClickListener
{
	private static final String TAG = "DownloadProgressButton";

	public Button downloadbtn;

	private ProgressBar downloadpb;

	private int appState = DetailAppStateConstants.APP_NO_INSTALL;

	private Context context;

	/** 下载管理器 */
	private DownloadManager downloadManager = null;

	/** 軟件管理器 */
	private SoftwareManager softwareManager = null;

	/** 下载监听器 */
	private DownloadListener listener;

	/** 包名 */
	private String packageName = null;

	// private DataCollectInfo datainfo = null;

	// private String vid = "";

	/** 刷新状态 */
	private final int HANDLER_REFRESH_SATAE = 0;

	/** 刷新添加或者删除任务 */
	private final int HANDLER_REFRESH_ADD_OR_DELETE = 1;

	/** 下载任务 */
	private DownloadInfo downloadInfo = null;

	/**  */
	private ListAppInfo appInfo = null;

	/** 广播接收器 */
	private AutoInstallBroadCastReceive receiver = null;

	/** 软件管理初始化完成广播接收器 */
	protected SoftManagerInitFinishBroadCastReceive softManagerInitFinishBroadCastReceiver = null;

	private Drawable pdrawable;

	public DetailDownloadProgressButton(Context context)
	{
		super(context);
		this.context = context;
		init();
		initLayout();
	}

	public DetailDownloadProgressButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		init();
		initLayout();
	}

	private void init()
	{
		downloadManager = DownloadManager.shareInstance();
		softwareManager = SoftwareManager.getInstance();

		initListener();
		downloadManager.registerDownloadListener(listener);
		registerAutoStallBroadCast(context);

		registerSoftManagerFinisgBroadCast(context);
	}

	/**
	 * 初始化布局
	 */
	private void initLayout()
	{
		downloadbtn = new Button(context);
		downloadpb = new ProgressBar(context, null,
				android.R.attr.progressBarStyleHorizontal);

		downloadbtn.setSingleLine();
		downloadbtn.setTextColor(Color.parseColor(DetailAppStateConstants
				.getAppStateTextColor(appState)));
		downloadbtn.setGravity(Gravity.CENTER);
		downloadbtn.setText(DetailAppStateConstants.getAppStateText(appState));
		downloadbtn.setBackgroundResource(DetailAppStateConstants
				.getAppStateBackgroundResource(appState));
		downloadbtn.setPadding(0, 0, 0, 0);
		downloadbtn.setOnClickListener(this);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		downloadpb.setMax(100);
		downloadpb.setProgress(0);
		pdrawable = context.getResources().getDrawable(
				R.drawable.progress_downloading_bg);
		downloadpb.setProgressDrawable(pdrawable);
		downloadpb.setVisibility(View.GONE);

		addView(downloadpb, params);
		addView(downloadbtn, params);

		isInEditMode();
	}

	/**
	 * 设置按钮的背景图片和文字
	 * 
	 * @param packageName
	 */

	public void setInfo(String packageName)
	{
		this.packageName = packageName;
		DownloadInfo info = downloadManager.queryDownload(packageName);
		appState = DetailAppStateConstants.APP_NO_INSTALL;

		if (info != null)
		{
			// 下载列表中有此应用的下载任务
			appState = DetailAppStateConstants.getAppState(
					DetailAppStateConstants.TYPE_DOWNLOAD, info.getState());
			setProgressInfo(info);

		} else
		{
			// 下载任务列表中没有此应用的下载任务
			appState = DetailAppStateConstants.getAppState(
					DetailAppStateConstants.TYPE_SOFTMANAGER,
					softwareManager.getStateByPackageName(packageName));
			downloadpb.setVisibility(View.GONE);
		}
		DownloadInfo autoDownloadInfo = AutoUpdateManager.getInstance().downloadManager
				.queryDownload(packageName);
		if (autoDownloadInfo != null)
		{
			// 自动更新列表中有下载任务
			if (autoDownloadInfo.getState() == DownloadInfo.STATE_FINISH)
			{
				// 如果自动更新列表中已下载完成 则显示自动更新里面的状态
				appState = AppStateConstants.getAppState(
						AppStateConstants.TYPE_DOWNLOAD,
						autoDownloadInfo.getState());
				setProgressInfo(autoDownloadInfo);
			}
		}

		// 设置对应的文本信息
		downloadbtn.setText(DetailAppStateConstants.getAppStateText(appState));
		downloadbtn.setTextColor(Color.parseColor(DetailAppStateConstants
				.getAppStateTextColor(appState)));
		downloadbtn.setBackgroundResource(DetailAppStateConstants
				.getAppStateBackgroundResource(appState));

	}

	/**
	 * 设置进度条信息
	 * 
	 * @param info
	 */
	private void setProgressInfo(DownloadInfo info)
	{
		if (appState == DetailAppStateConstants.APP_DOWNLOAD_STATE_STOP)
		{
			downloadpb.setVisibility(View.VISIBLE);
			pdrawable = context.getResources().getDrawable(
					R.drawable.progress_stop_bg);
			pdrawable.setBounds(downloadpb.getProgressDrawable().getBounds());
			downloadpb.setProgressDrawable(pdrawable);
			downloadpb.setProgress(info.getPercent());
		} else if (appState == DetailAppStateConstants.APP_DOWNLOAD_STATE_DOWNLOADING)
		{
			downloadpb.setVisibility(View.VISIBLE);
			pdrawable = context.getResources().getDrawable(
					R.drawable.progress_downloading_bg);
			pdrawable.setBounds(downloadpb.getProgressDrawable().getBounds());
			downloadpb.setProgressDrawable(pdrawable);
			downloadpb.setProgress(info.getPercent());
		} else
		{
			downloadpb.setVisibility(View.GONE);
		}
	}

	/**
	 * 初始化下载监听器
	 */
	private void initListener()
	{
		listener = new DownloadListener()
		{

			@Override
			public void onTaskCountChanged(int state, DownloadInfo info)
			{
				if (info.getPackageName().equals(packageName))
				{
					handler.sendEmptyMessage(HANDLER_REFRESH_ADD_OR_DELETE);
				}
			}

			@Override
			public void onStateChange(int state, DownloadInfo info)
			{
				if (info.getPackageName().equals(packageName))
				{
					if (state != DownloadInfo.STATE_FINISH)
					{
						sentMessage(info, HANDLER_REFRESH_SATAE);
					} else
					{
						handler.sendEmptyMessage(HANDLER_REFRESH_ADD_OR_DELETE);
					}
				}

			}

			@Override
			public void onProgress(int percent, DownloadInfo info)
			{
				if (info.getPackageName().equals(packageName))
				{
					sentMessage(info, HANDLER_REFRESH_SATAE);
				}
			}
		};

	}

	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			DownloadInfo downInfo = (DownloadInfo) msg.obj;
			if (msg.what == HANDLER_REFRESH_SATAE)
			{
				setInfo(downInfo.getPackageName());
			} else if (msg.what == HANDLER_REFRESH_ADD_OR_DELETE)
			{
				setInfo(packageName);
			}
		};
	};

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
	 * 广播接收安装或者卸载状态
	 * 
	 * @author denglh
	 * 
	 */
	class AutoInstallBroadCastReceive extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			// if (intent.getAction().equals(
			// SoftwareManager.AUTOINSTALL_OR_UNINSTALL_ACTION))
			// {
			// int state = intent.getIntExtra(SoftwareManager.STATE,
			// ApkInstallUtil.AUTO_INSTALL_FAILED);
			// // 如果不是安装成功
			// if (state != ApkInstallUtil.AUTO_INSTALL_SUCCESS)
			// {
			// setInfo(packageName);
			// }
			// }
			if (Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS
					.equals(intent.getAction())
					|| Constants.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS
							.equals(intent.getAction()))
			{
				String intentPackageName = intent.getStringExtra("packageName");
				if (intentPackageName.equals(packageName))
				{
					setInfo(packageName);
				}
			}

		}
	}

	/**
	 * 广播接收软件管理初始化完成广播
	 * 
	 * @author denglh
	 * 
	 */
	class SoftManagerInitFinishBroadCastReceive extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			// 如果软件管理初始化完成，刷新
			if (intent.getAction().equals(
					Constants.ACTION_SOFTWARE_MANAGER_INIT_DONE)
					|| intent
							.getAction()
							.equals(Constants.ACTION_SOFTWARE_MANAGER_GET_UPDATE_LIST_FROM_NETWORK_FINISH))
			{
				DLog.d(TAG,
						"---SoftManagerInitFinishBroadCastReceive onReceive---"
								+ "初始化完成");
				if (packageName != null)
				{
					setInfo(packageName);
				}
			}
		}
	}

	/**
	 * 注册广播
	 */
	private void registerAutoStallBroadCast(Context context)
	{
		receiver = new AutoInstallBroadCastReceive();
		// 实例化过滤器并设置要过滤的广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction(Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS);
		intentFilter
				.addAction(Constants.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS);
		// 注册广播
		context.registerReceiver(receiver, intentFilter);
	}

	/**
	 * 注册广播
	 */
	private void registerSoftManagerFinisgBroadCast(Context context)
	{
		softManagerInitFinishBroadCastReceiver = new SoftManagerInitFinishBroadCastReceive();
		// 实例化过滤器并设置要过滤的广播
		IntentFilter intentFilter = new IntentFilter(
				Constants.ACTION_SOFTWARE_MANAGER_INIT_DONE);
		intentFilter
				.addAction(Constants.ACTION_SOFTWARE_MANAGER_GET_UPDATE_LIST_FROM_NETWORK_FINISH);
		// 注册广播
		context.registerReceiver(softManagerInitFinishBroadCastReceiver,
				intentFilter);
	}

	/**
	 * 取消注册广播
	 */
	private void unRegisterAutoStallBroadCast()
	{
		if (receiver != null)
		{
			context.unregisterReceiver(receiver);
		}
	}

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
	 * 销毁时调用
	 */
	public void onDestory()
	{
		if (downloadManager != null)
		{
			downloadManager.unregisterDownloadListener(listener);
			try
			{
				unRegisterAutoStallBroadCast();
				unRegisterSoftManagerFinishBroadCast();
				unRegisterSoftManagerFinishBroadCast();
			} catch (Exception e)
			{
				DLog.e(TAG, "broadcase not register");
			}
		}
	}

	public void setDownloadInfo(DownloadInfo downloadinfo)
	{
		this.downloadInfo = downloadinfo;
		// this.datainfo = datainfo;
	}

	public void setAppInfo(ListAppInfo appinfo)
	{
		this.appInfo = appinfo;
		this.downloadInfo = appinfo.toDownloadInfo();
	}

	// public void setVid(String vid)
	// {
	// this.vid = vid;
	// }

	@Override
	public void onClick(View arg0)
	{
		if (downloadInfo == null)
		{
			return;
		}
		DownloadInfo info = downloadManager.queryDownload(packageName);
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
				break;
			case DownloadInfo.STATE_DOWNLOADING:
				// 正在下载中,点击则暂停
				downloadManager.stopDownload(info);
				break;
			case DownloadInfo.STATE_STOP:
				// 下载停止状态，点击则继续下载
			case DownloadInfo.STATE_ERROR:
				// 下载出错状态，点击则继续下载
				if (ToolsUtil.checkNetworkValid(context))
				{
					downloadManager.addDownload(info);
				}
				break;
			case DownloadInfo.STATE_FINISH:
				// 下载完成状态，点击则安装应用
				// softwareManager.installLocalApk(DownloadManager.shareInstance(),
				// info);
				softwareManager.installApkByDownloadInfo(info);
				break;
			}
		} else
		{
			DLog.i("lilijun", "当前没有下载任务....");
			/* 如果不存在存在下载任务 */
			int state = softwareManager.getStateByPackageName(packageName);
			switch (state)
			{
			// 已安装
			case SoftwareManager.STATE_INSTALLED:
				DLog.i("lilijun", "已安装...");
				// 打开应用
				// softwareManager.openSoftware(context, packageName);
				ToolsUtil.openSoftware(context, packageName);
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
						downloadManager.addDownload(downloadInfo);
						// sentDataCollection(); TODO 添加下载任务的数据采集
					}
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
						downloadManager
								.addDownload(softwareManager
										.getUpdateDownloadInfoByPackageName(downloadInfo
												.getPackageName()));
						// sentDataCollection(); TODO 添加下载任务的数据采集
						// }
					}
				}
				break;
			}
		}
	}

	@Override
	protected void onDetachedFromWindow()
	{
		onDestory();
		super.onDetachedFromWindow();
	}

	// /**
	// * 显示签名不同提示
	// */
	// private void showDialog()
	// {
	// MarketFloateDialogBuilder builder = new MarketFloateDialogBuilder(
	// context);
	// builder.setMessage("你原有的【" + appInfo.getName()
	// + "】与新版本不兼容，需卸载后重新安装，是否继续更新？");
	// builder.setCancelable(true);
	//
	// final Dialog dialog = builder.crteate();
	// builder.setLeftButton("忽略更新", new OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	// // softwareManager.updateAppInfoToDatabase(appInfo);
	// softwareManager.changeUpdateToIgnore(appInfo.getPackageName());
	// dialog.dismiss();
	// }
	// });
	//
	// builder.setRightButton("继续更新", new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// dialog.dismiss();
	// if (downloadManager.addDownload(softwareManager
	// .getDownloadInfoByPackageName(appInfo.getPackageName())))
	// {
	// softwareManager.uninstallApk(appInfo.getPackageName());
	// }
	// sentDataCollection();
	// }
	// });
	// dialog.show();
	// }

	// /**
	// * 数据采集
	// */
	// private void sentDataCollection()
	// {
	// if (!vid.equals(""))
	// {
	// datainfo.setAction(DataCollectManager.ACTION_INFOMATION_DOWNLOAD);
	// DataCollectManager.addRecord(datainfo, "setup_flag", "0", "app_id",
	// downloadInfo.getSoftId(), "cpversion",
	// downloadInfo.getUpdateVersionName() + "", "vid", vid);
	// }
	// else
	// {
	// datainfo.setAction(DataCollectManager.ACTION_DOWNLOAD);
	// DataCollectManager.addRecord(datainfo, "setup_flag", "0", "app_id",
	// downloadInfo.getSoftId(), "cpversion",
	// downloadInfo.getUpdateVersionName() + "");
	// }
	// }
}
