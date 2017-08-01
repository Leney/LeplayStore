package com.xd.leplay.store.util;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.xd.download.DownloadInfo;
import com.xd.download.DownloadListener;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.AutoUpdateManager;
import com.xd.leplay.store.control.ConstantManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LeplayPreferences;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.control.UpgradAppManager;
import com.xd.leplay.store.gui.details.DetailsActivity;
import com.xd.leplay.store.gui.download.DownloadManagerActivity;
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.webview.WebViewActivity;
import com.xd.leplay.store.model.UpdateAppInfo;
import com.xd.leplay.store.view.BottomDialog;
import com.xd.leplay.store.view.CenterDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * dp、sp 转换为 px 的工具类
 *
 * @author fxsky 2012.11.12
 *
 */
public class DisplayUtil
{
	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 *
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 *
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 *
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(Context context, float pxValue)
	{
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param spValue
	 * @return
	 */
	public static int sp2px(Context context, float spValue)
	{
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/** 获取状态栏的高度 */
	public static int getStatusBarHeight(Context context)
	{
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try
		{
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	/**
	 * 获取列表图标的图片加载配置
	 * 
	 * @return
	 */
	public static DisplayImageOptions getListIconImageLoaderOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_icon)// 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.default_icon)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.default_icon)// 设置图片加载或解码过程中发生错误显示的图片
				.displayer(new RoundedBitmapDisplayer(20))// 设置成显示角度为20的圆角
				.cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.build();
		return options;
	}

	/**
	 * 获取滚动Banner的图片加载配置
	 * 
	 * @return
	 */
	public static DisplayImageOptions getBannerImageLoaderOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_rec_icon)
				.showImageForEmptyUri(R.drawable.default_rec_icon)
				.showImageOnFail(R.drawable.default_rec_icon).cacheOnDisk(true)
				.cacheInMemory(true).build();
		return options;
	}

	/**
	 * 获取图片广告的图片加载配置
	 * 
	 * @return
	 */
	public static DisplayImageOptions getAdImgImageLoaderOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_rec_icon)
				.showImageForEmptyUri(R.drawable.default_rec_icon)
				.showImageOnFail(R.drawable.default_rec_icon).cacheOnDisk(true)
				.cacheInMemory(true).build();
		return options;
	}

	/**
	 * 获取详情中截图的图片加载配置
	 * 
	 * @return
	 */
	public static DisplayImageOptions getScreenShortImageLoaderOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_rec_icon)
				.showImageForEmptyUri(R.drawable.default_rec_icon)
				.showImageOnFail(R.drawable.default_rec_icon).cacheOnDisk(true)
				.cacheInMemory(true).build();
		return options;
	}

	/**
	 * 获取用户头像的图片加载配置
	 * 
	 * @return
	 */
	public static DisplayImageOptions getUserIconImageLoaderOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.user_icon_default)
				.showImageForEmptyUri(R.drawable.user_icon_default)
				.showImageOnFail(R.drawable.user_icon_default)
				.cacheOnDisk(true).cacheInMemory(true).build();
		return options;
	}

	/**
	 * 获取图片广告的图片加载配置
	 * 
	 * @return
	 */
	public static DisplayImageOptions getSplashImageLoaderOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.splash_screen_img)
				.showImageForEmptyUri(R.drawable.splash_screen_img)
				.showImageOnFail(R.drawable.splash_screen_img)
				.cacheOnDisk(true).cacheInMemory(true).build();
		return options;
	}

	/**
	 * 切换软键盘的状态 如当前为收起变为弹出,若当前为弹出变为收起
	 */
	public static void toggleInput(Context context)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 强制隐藏输入法键盘
	 */
	public static void hideInput(Context context, View view)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * 显示跳转到应用详情的通知栏
	 * 
	 * @param context
	 * @param iconUrl
	 *            通知栏图标路径
	 * @param title
	 *            通知栏title
	 * @param appId
	 *            appId
	 */
	public static void showAppDetailsNotification(final Context context,
			final String iconUrl, final String title, final long appId)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context);
				mBuilder.setSmallIcon(R.drawable.app_icon_small);
				mBuilder.setAutoCancel(true);
				mBuilder.setWhen(System.currentTimeMillis());
				RemoteViews contentView = new RemoteViews(context
						.getPackageName(), R.layout.app_details_notifiy_layout);
				contentView.setTextViewText(
						R.id.app_details_notify_content_text, title);
				Bitmap iconBitmap = ImageLoaderManager.getInstance()
						.loadImageSync(iconUrl);
				contentView.setImageViewBitmap(R.id.app_details_notify_img,
						iconBitmap);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String date = sdf.format(new Date(System.currentTimeMillis()));
				contentView.setTextViewText(R.id.app_details_notify_time_text,
						date);

				mBuilder.setContent(contentView);
				// 通知栏滚动消息
				mBuilder.setTicker(title);

				// Intent intent = new Intent(UPDATE_ACTION);
				Intent intent = new Intent();
				intent.setClass(context, DetailsActivity.class);
				intent.putExtra("softId", appId);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent contentIntent = PendingIntent.getActivity(
						context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setContentIntent(contentIntent);
				NotificationManager mnotiManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification mNotification = mBuilder.build();
				mNotification.contentView = contentView;

				mnotiManager.notify(Constants.APP_DETAILS_NOTIFY_ID,
						mNotification);
			}
		}).start();
	}

	/**
	 * 显示跳转到网页的通知栏
	 * 
	 * @param context
	 * @param iconUrl
	 *            通知栏图标路径
	 * @param title
	 *            通知栏title
	 * @param skipUrl
	 *            跳转的网络路径
	 * @param webName
	 *            网页界面标题名称
	 */
	public static void showWebAcitivtyNotification(final Context context,
			final String iconUrl, final String title, final String skipUrl,
			final String webName)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context);
				mBuilder.setSmallIcon(R.drawable.app_icon_small);
				mBuilder.setAutoCancel(true);
				mBuilder.setWhen(System.currentTimeMillis());
				RemoteViews contentView = new RemoteViews(context
						.getPackageName(), R.layout.app_details_notifiy_layout);
				contentView.setTextViewText(
						R.id.app_details_notify_content_text, title);
				Bitmap iconBitmap = ImageLoaderManager.getInstance()
						.loadImageSync(iconUrl);
				contentView.setImageViewBitmap(R.id.app_details_notify_img,
						iconBitmap);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String date = sdf.format(new Date(System.currentTimeMillis()));
				contentView.setTextViewText(R.id.app_details_notify_time_text,
						date);

				mBuilder.setContent(contentView);
				// 通知栏滚动消息
				mBuilder.setTicker(title);

				// Intent intent = new Intent(UPDATE_ACTION);
				Intent intent = new Intent();
				intent.setClass(context, WebViewActivity.class);
				intent.putExtra("loadUrl", skipUrl);
				intent.putExtra("title", webName);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent contentIntent = PendingIntent.getActivity(
						context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setContentIntent(contentIntent);
				NotificationManager mnotiManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification mNotification = mBuilder.build();
				mNotification.contentView = contentView;

				mnotiManager
						.notify(Constants.WEB_VIEW_NOTIFY_ID, mNotification);
			}
		}).start();
	}

	/**
	 * 显示爱玩商店本身有更新的通知栏
	 * 
	 * @param context
	 * @param downloadInfo
	 */
	public static void showUpgradeNotification(final Context context,
			final String title, final String discrible,
			final DownloadInfo downloadInfo)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context);
				mBuilder.setSmallIcon(R.drawable.app_icon_small);
				mBuilder.setAutoCancel(true);
				mBuilder.setWhen(System.currentTimeMillis());
				RemoteViews contentView = new RemoteViews(context
						.getPackageName(), R.layout.upgrade_notification_lay);
				contentView.setTextViewText(
						R.id.upgrade_notification_content_title, title);
				contentView.setTextViewText(
						R.id.upgrade_notification_content_discrible, discrible);
				Bitmap iconBitmap = ImageLoaderManager.getInstance()
						.loadImageSync(downloadInfo.getIconUrl());
				contentView.setImageViewBitmap(
						R.id.upgrade_notification_content_icon, iconBitmap);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String date = sdf.format(new Date(System.currentTimeMillis()));
				contentView.setTextViewText(
						R.id.upgrade_notification_content_date, date);

				mBuilder.setContent(contentView);
				// 通知栏滚动消息
				mBuilder.setTicker(title);

				// Intent intent = new Intent(UPDATE_ACTION);
				Intent intent = new Intent();
				intent.setClass(context, DownloadManagerActivity.class);
				intent.putExtra("isUpgradeFromNotification", true);
				intent.putExtra("downloadInfo", downloadInfo);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent contentIntent = PendingIntent.getActivity(
						context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setContentIntent(contentIntent);
				NotificationManager mnotiManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification mNotification = mBuilder.build();
				mNotification.contentView = contentView;

				mnotiManager.notify(Constants.UPGREAD_NOTIFY_ID, mNotification);
			}
		}).start();
	}

	/**
	 * 显示爱玩商店升级提示框
	 */
	public static void showUpgradeDialog(final Context context)
	{

		if (!SoftwareManager.getInstance().getUpdateAppInfos()
				.containsKey(context.getPackageName())
				|| LeplayPreferences.getInstance(context).isShowUpgradeDialog())
		{
			return;
		}
		final UpdateAppInfo updateAppInfo = SoftwareManager.getInstance()
				.getUpdateAppInfos().get(context.getPackageName());
		LeplayPreferences.getInstance(context).setShowUpgradeDialog(true);
		final BottomDialog upgradDialog = new BottomDialog(context);
		upgradDialog.show();
		upgradDialog.setTitleName(context.getResources().getString(
				R.string.upgrade_aiwan));
		upgradDialog.setCenterMsg(String.format(context.getResources()
				.getString(R.string.upgrade_aiwan_msg), updateAppInfo
				.getUpdateVersionName())
				+ "\n\n" + updateAppInfo.getUpdateDescribe());
		upgradDialog.setRightBtnText(context.getResources().getString(
				R.string.upgrade_aiwan_now));
		upgradDialog.setRightBtnVisible(true);
		upgradDialog.setLeftBtnVisible(false);
		upgradDialog.setRightOnclickLinstener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				DownloadInfo autoDownloadInfo = AutoUpdateManager.getInstance().downloadManager
						.queryDownload(context.getPackageName());
				if (autoDownloadInfo != null)
				{
					if (autoDownloadInfo.getState() == DownloadInfo.STATE_FINISH)
					{
						// 如果自动下载列表中有已经下载并完成了的任务 则直接安装
						SoftwareManager.getInstance().installApkByDownloadInfo(
								autoDownloadInfo);
					}
				} else
				{
					DownloadManager.shareInstance().addDownload(
							updateAppInfo.toDownloadInfo());
				}
				upgradDialog.dismiss();
			}
		});
	}

	/**
	 * 显示下载领金币Dialog
	 */
	public static void showDownloadGetCoinDialog(final Context context)
	{
		LeplayPreferences.getInstance(context).setFirstDownloadWithUnlogin(
				false);
		final BottomDialog downloadGetCoinDialog = new BottomDialog(context);
		downloadGetCoinDialog.show();
		downloadGetCoinDialog.setTitleName(context.getResources().getString(
				R.string.download_get_coins));
		downloadGetCoinDialog.setCenterMsg(ToolsUtil.getFormatTextColor(context
				.getResources().getString(R.string.download_get_coins_msg), 0,
				12, "#fb5c00"));
		downloadGetCoinDialog.setLeftBtnText(context.getResources().getString(
				R.string.register));
		downloadGetCoinDialog.setLeftBtnOnclickLinstener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				LoginActivity.startLoginActivity(context, "");
				downloadGetCoinDialog.dismiss();
			}
		});

		downloadGetCoinDialog.setRightBtnText(context.getResources().getString(
				R.string.login));
		downloadGetCoinDialog.setRightOnclickLinstener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				LoginActivity.startLoginActivity(context, "");
				downloadGetCoinDialog.dismiss();
			}
		});
	}

	/**
	 * 重新下载的Dialog
	 * 
	 * @param context
	 * @param downloadInfo
	 *            下载任务对象
	 * @param isAutoDownloadManager
	 *            是否是自动下载任务管理器中的任务
	 */
	public static void showReDownloadDialog(final Context context,
			final DownloadInfo downloadInfo, final boolean isAutoDownloadManager)
	{
		final BottomDialog reDownloadDialog = new BottomDialog(context);
		reDownloadDialog.show();
		reDownloadDialog.setCenterMsg(context.getResources().getString(
				R.string.redownload_msg));
		reDownloadDialog.setLeftBtnText(context.getResources().getString(
				R.string.cancle));
		reDownloadDialog.setLeftBtnOnclickLinstener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// if (isAutoDownloadManager)
				// {
				// // 删除自动更新下载管理器中的下载任务
				// AutoUpdateManager.getInstance().downloadManager
				// .deleteDownload(downloadInfo);
				// } else
				// {
				// // 删除前端可见的下载任务管理器中的任务
				// DownloadManager.shareInstance()
				// .deleteDownload(downloadInfo);
				// }
				reDownloadDialog.dismiss();
			}
		});

		reDownloadDialog.setRightBtnText(context.getResources().getString(
				R.string.redownload));
		reDownloadDialog.setRightOnclickLinstener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				DownloadInfo newDownloadInfo = new DownloadInfo();
				newDownloadInfo = downloadInfo;
				// 获取到apk所在的文件对象
				File fileApk = new File(downloadInfo.getPath());
				// 获取到donson文件夹的对象
				File file = fileApk.getParentFile().getParentFile();
				newDownloadInfo.setPath(file.getAbsolutePath()
						+ "/"
						+ DownloadManager.shareInstance()
								.getDefault_download_dir() + "/"
						+ fileApk.getName());

				if (isAutoDownloadManager)
				{
					// 删除自动更新下载管理器中的下载任务
					AutoUpdateManager.getInstance().downloadManager
							.deleteDownload(downloadInfo);
				} else
				{
					// 删除前端可见的下载任务管理器中的任务
					DownloadManager.shareInstance()
							.deleteDownload(downloadInfo);
				}
				DownloadManager.shareInstance().addDownload(newDownloadInfo);
				reDownloadDialog.dismiss();
			}
		});
	}

	/**
	 * 显示第一次进来的登录提示Dialog
	 */
	public static void showFirstInDialog(final Context context,
			int firstLoginCoin)
	{
		final CenterDialog firstDialog = new CenterDialog(context);
		firstDialog.show();
		View view = View.inflate(context, R.layout.first_in_dialog, null);
		view.setBackgroundColor(context.getResources().getColor(
				R.color.transparency));
		firstDialog.setCenterView(view, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		firstDialog.setTitleVisible(false);
		TextView topCoin = (TextView) view.findViewById(R.id.first_in_top_coin);
		TextView contentMsg = (TextView) view
				.findViewById(R.id.first_in_content_msg);
		Button loginBtn = (Button) view.findViewById(R.id.first_in_login_get);

		topCoin.setText(firstLoginCoin + "");
		// contentMsg.setText(ToolsUtil.getFormatTextColor(
		// String.format(
		// context.getResources().getString(
		// R.string.first_login_get), firstLoginCoin), 4,
		// 6 + (firstLoginCoin + "").length(), "#fff719"));
		contentMsg.setText(String.format(
				context.getResources().getString(R.string.first_login_get3),
				firstLoginCoin * ConstantManager.EXCHANGE_RATE + ""));
		loginBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				LoginActivity.startLoginActivity(context, "");
				firstDialog.dismiss();
			}
		});
	}

	/**
	 * 显示网络异常的Dialog
	 * 
	 * @param context
	 */
	public static void showNetErrorDialog(final Context context)
	{
		final BottomDialog netErrorDialog = new BottomDialog(context);
		netErrorDialog.show();
		netErrorDialog.setCenterMsg(context.getResources().getString(
				R.string.net_error));
		netErrorDialog.setLeftBtnText(context.getResources().getString(
				R.string.cancle));
		netErrorDialog.setLeftBtnOnclickLinstener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				netErrorDialog.dismiss();
			}
		});

		netErrorDialog.setRightBtnText(context.getResources().getString(
				R.string.set_net));
		netErrorDialog.setRightOnclickLinstener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				ToolsUtil.startSettingActivity(context);
				netErrorDialog.dismiss();
			}
		});
	}

	/**
	 * 显示分享的Dialog
	 * 
	 * @param context
	 */
	public static void showShareDialog(final Context context,
			final String shareTitle, final String shareContent,
			final String weixinTitle, final String weixinContent,
			final String weixinUrl, final String action)
	{
		final BottomDialog shareDialog = new BottomDialog(context);
		shareDialog.show();
		shareDialog.setTitleName(shareTitle);
		shareDialog.setCenterView(R.layout.share_dialog);
		/*
		 * shareDialog.setLeftBtnText(context.getResources().getString(
		 * R.string.cancle)); shareDialog.setRightBtnVisible(false);
		 */
		shareDialog.setBottomBtnsLayVisible(false);
		shareDialog.setBottomBtnsLay1Visible(true);
		shareDialog.setBottomBtnText(context.getResources().getString(
				R.string.cancle));
		/*
		 * shareDialog.setLeftBtnOnclickLinstener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { shareDialog.dismiss(); } });
		 */
		shareDialog.setBottomBtnOnclickLinstener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				shareDialog.dismiss();
			}
		});
		TextView QQFriendsBtn = (TextView) shareDialog
				.findViewById(R.id.share_qq_item);
		QQFriendsBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				shareDialog.dismiss();
				ToolsUtil.share2Spec(context, "com.tencent.mobileqq",
						shareTitle, shareContent);
				String actionValue = action
						+ "-"
						+ DataCollectionConstant.DATA_COLLECTION_SHARE_TO_QQ_VALUE;
				// 添加数据采集
				DataCollectionManager.getInstance().addRecord(actionValue);
				// 添加友盟数据统计
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								context,
								actionValue,
								DataCollectionConstant.EVENT_ID_CLICK_SHARE_TO_QQ,
								null);
			}
		});
		TextView WeixinFriendBtn = (TextView) shareDialog
				.findViewById(R.id.share_weixin_friends_item);
		WeixinFriendBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				shareDialog.dismiss();
				// ToolsUtil.share2Spec(context, "com.tencent.mm", shareTitle,
				// shareContent);
				ToolsUtil.share2weixin(context, 0, weixinTitle, weixinContent,
						weixinUrl);
				String actionValue = action
						+ "-"
						+ DataCollectionConstant.DATA_COLLECTION_SHARE_TO_WEIXIN_VALUE;
				// 添加数据采集
				DataCollectionManager.getInstance().addRecord(actionValue);
				// 添加友盟数据统计
				DataCollectionManager.getInstance().addYouMengEventRecord(
						context, actionValue,
						DataCollectionConstant.EVENT_ID_CLICK_SHARE_TO_WEIXIN,
						null);
			}
		});
		TextView weixinFriendCircle = (TextView) shareDialog
				.findViewById(R.id.share_weixin_friends_circle_item);
		weixinFriendCircle.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				shareDialog.dismiss();
				ToolsUtil.share2weixin(context, 1, weixinTitle, weixinContent,
						weixinUrl);
				String actionValue = action
						+ "-"
						+ DataCollectionConstant.DATA_COLLECTION_SHARE_TO_WEIXIN_FRIENDS_CIRCLE_VALUE;
				// 添加数据采集
				DataCollectionManager.getInstance().addRecord(actionValue);
				// 添加友盟数据统计
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								context,
								actionValue,
								DataCollectionConstant.EVENT_ID_CLICK_SHARE_TO_WEIXIN_FRIEND_CIRCLE,
								null);
			}
		});
		TextView sinaWeiboBtn = (TextView) shareDialog
				.findViewById(R.id.share_sina_weibo_item);
		sinaWeiboBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				shareDialog.dismiss();
				ToolsUtil.share2Spec(context, "com.qzone", shareTitle,
						shareContent);
				String actionValue = action
						+ "-"
						+ DataCollectionConstant.DATA_COLLECTION_SHARE_TO_SINA_WEIBO_VALUE;
				// 添加数据采集
				DataCollectionManager.getInstance().addRecord(actionValue);
				// 添加友盟数据统计
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								context,
								actionValue,
								DataCollectionConstant.EVENT_ID_CLICK_SHARE_TO_SINA_WEIBO,
								null);
			}
		});
		TextView shareMoreBtn = (TextView) shareDialog
				.findViewById(R.id.share_more_item);
		shareMoreBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				shareDialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				// intent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
				intent.putExtra(Intent.EXTRA_TEXT, shareContent);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(Intent.createChooser(intent, shareTitle));

				String actionValue = action
						+ "-"
						+ DataCollectionConstant.DATA_COLLECTION_SHARE_CLICK_MORE_VALUE;
				// 添加数据采集
				DataCollectionManager.getInstance().addRecord(actionValue);
				// 添加友盟数据统计
				DataCollectionManager.getInstance().addYouMengEventRecord(
						context, actionValue,
						DataCollectionConstant.EVENT_ID_CLICK_SHARE_MORE_BTN,
						null);
			}
		});
	}

	/**
	 * 显示强制升级的Dialog
	 */
	public static void showForceUpgreadDialog(final DownloadInfo downloadInfo,
			final Context context)
	{
		UpgradAppManager upgradAppManager = UpgradAppManager.getInstance();
		CenterDialog forceDialog = new CenterDialog(context);
		forceDialog.show();
		forceDialog.setTitleVisible(false);
		forceDialog.setCancelable(false);
		forceDialog.setCanceledOnTouchOutside(false);
		forceDialog.setCenterView(R.layout.force_dialog_layout);
		final TextView msgText = (TextView) forceDialog
				.findViewById(R.id.force_dialog_msg);
		final ProgressBar downloadProgress = (ProgressBar) forceDialog
				.findViewById(R.id.force_dialog_download_progress);
		final Button reDownloadBtn = (Button) forceDialog
				.findViewById(R.id.force_dialog_redownload_btn);
		final TextView progressText = (TextView) forceDialog
				.findViewById(R.id.force_dialog_progress_text);
		reDownloadBtn.setVisibility(View.GONE);
		reDownloadBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				DownloadInfo alreadyDownloadInfo = UpgradAppManager
						.getInstance().downloadManager.queryDownload(context
						.getPackageName());
				if (alreadyDownloadInfo != null)
				{
					if (DownloadInfo.STATE_FINISH != alreadyDownloadInfo
							.getState())
					{
						// 重新下载
						UpgradAppManager.getInstance().downloadManager
								.reDownloadLostedFinishedTask(alreadyDownloadInfo);
					}
				} else
				{
					// 添加新的下载任务
					UpgradAppManager.getInstance().downloadManager
							.addDownload(downloadInfo);
				}
			}
		});
		final Button installBtn = (Button) forceDialog
				.findViewById(R.id.force_dialog_install_btn);
		installBtn.setVisibility(View.GONE);
		installBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				DownloadInfo alreadyDownloadInfo = UpgradAppManager
						.getInstance().downloadManager.queryDownload(context
						.getPackageName());
				if (alreadyDownloadInfo != null)
				{
					if (new File(alreadyDownloadInfo.getPath()).exists())
					{
						SoftwareManager.getInstance().installApkByDownloadInfo(
								alreadyDownloadInfo);
					} else
					{
						UpgradAppManager.getInstance().downloadManager
								.deleteDownload(alreadyDownloadInfo);
						// 添加新的下载任务
						UpgradAppManager.getInstance().downloadManager
								.addDownload(downloadInfo);
					}
				} else
				{
					// 添加新的下载任务
					UpgradAppManager.getInstance().downloadManager
							.addDownload(downloadInfo);
				}
			}
		});

		DownloadListener listener = new DownloadListener()
		{
			@Override
			public void onTaskCountChanged(int state, DownloadInfo info)
			{
			}

			@Override
			public void onStateChange(int state, final DownloadInfo info)
			{
				((Activity) context).runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						if (DownloadInfo.STATE_FINISH == info.getState())
						{
							// 下载完成
							installBtn.setVisibility(View.VISIBLE);
							reDownloadBtn.setVisibility(View.GONE);
							msgText.setText(context.getResources().getString(
									R.string.upgread_install_text));
							SoftwareManager.getInstance()
									.installApkByDownloadInfo(info);
						} else if (DownloadInfo.STATE_ERROR == info.getState())
						{
							// 下载失败
							reDownloadBtn.setVisibility(View.VISIBLE);
							installBtn.setVisibility(View.GONE);
							msgText.setText(context.getResources().getString(
									R.string.upgread_error_text));
						} else if (DownloadInfo.STATE_DOWNLOADING == info
								.getState())
						{
							// 正在下载
							reDownloadBtn.setVisibility(View.GONE);
							installBtn.setVisibility(View.GONE);
							msgText.setText(context.getResources().getString(
									R.string.upgreading_text));
						}
					}
				});
			}

			@Override
			public void onProgress(final int percent, DownloadInfo info)
			{
				((Activity) context).runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						downloadProgress.setProgress(percent);
						progressText.setText(percent + "%");
					}
				});
			}
		};
		upgradAppManager.downloadManager.registerDownloadListener(listener);
		final DownloadInfo queryDownloadInfo = upgradAppManager.downloadManager
				.queryDownload(context.getPackageName());
		if (queryDownloadInfo != null)
		{
			if (queryDownloadInfo.getUpdateVersionCode() != downloadInfo
					.getUpdateVersionCode())
			{
				// 只要自动下载任务中的下载任务版本号 和 必须强制升级的目标版本号不相等 就直接删除任务
				upgradAppManager.downloadManager
						.deleteDownload(queryDownloadInfo);
				// 添加新的下载任务
				upgradAppManager.downloadManager.addDownload(downloadInfo);
			} else
			{
				if (DownloadInfo.STATE_FINISH == queryDownloadInfo.getState())
				{
					// 如果已经完成，则显示安装按钮
					installBtn.setVisibility(View.VISIBLE);
					reDownloadBtn.setVisibility(View.GONE);
					downloadProgress.setProgress(100);
					progressText.setText("100%");
					msgText.setText(context.getResources().getString(
							R.string.upgread_install_text));
				} else
				{
					// 重新下载
					upgradAppManager.downloadManager
							.reDownloadLostedFinishedTask(queryDownloadInfo);
				}
			}
		} else
		{
			// 自动下载任务中没有该下载任务
			// 添加新的下载任务
			upgradAppManager.downloadManager.addDownload(downloadInfo);
		}
		// // 再去查找一次自动下载管理中是否有下载任务(这里查找出来的下载任务就肯定是和强制升级的目标版本一致)
		// DownloadInfo queryDownloadInfo2 = autoUpdateManager.downloadManager
		// .queryDownload(context.getPackageName());
		// if (queryDownloadInfo2 != null)
		// {
		// // 自动下载任务中已经有该下载任务
		// if (DownloadInfo.STATE_FINISH == queryDownloadInfo2.getState())
		// {
		// // 如果已经完成，则显示安装按钮
		// installBtn.setVisibility(View.VISIBLE);
		// reDownloadBtn.setVisibility(View.GONE);
		// downloadProgress.setProgress(100);
		// msgText.setText("点击安装新版本");
		// } else
		// {
		// // 重新下载
		// autoUpdateManager.downloadManager
		// .reDownloadLostedFinishedTask(queryDownloadInfo2);
		// }
		// } else
		// {
		// // 自动下载任务中没有该下载任务
		// // 添加新的下载任务
		// autoUpdateManager.downloadManager.addDownload(downloadInfo);
		// }

	}
}