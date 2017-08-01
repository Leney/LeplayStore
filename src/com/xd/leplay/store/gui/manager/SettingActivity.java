package com.xd.leplay.store.gui.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.download.DownloadInfo;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.AutoUpdateManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.EtagManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LeplayPreferences;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.LoadingDialog;

import java.io.File;

/**
 * 设置界面
 * 
 * @author lilijun
 *
 */
public class SettingActivity extends BaseActivity
{

	private LeplayPreferences preferences = null;

	/** 当前网络 */
	private TextView curNetwork;

	/** 无图省流量模式开关、删除安装包开关、自动更新开关、接收推荐内容提醒开关 */
	private CheckBox noPicCheck, deleteApkCheck, autoUpdateCheck,
			receiverPushCheck;

	/** 清除本地缓存 */
	private LinearLayout clearCancleLay;

	@Override
	protected void initView()
	{
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_SETTING_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		titleView.setRightLayVisible(false);
		titleView.setTitleName(getResources().getString(R.string.setting));
		titleView.setBottomLineVisible(true);

		loadingView.setVisibilyView(false);

		preferences = LeplayPreferences.getInstance(SettingActivity.this);

		setCenterView(R.layout.setting_activity);

		curNetwork = (TextView) findViewById(R.id.setting_cur_net_status);

		noPicCheck = (CheckBox) findViewById(R.id.setting_no_pic_on_off_checkbox);
		noPicCheck.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked)
			{
				if (isChecked)
				{
					DataCollectionManager
							.getInstance()
							.addRecord(
									DataCollectionManager
											.getAction(
													action,
													DataCollectionConstant.DATA_COLLECTION_SETTING_OPEN_NO_PIC_VALUE));
					// 添加友盟的自定义事件的数据采集
					DataCollectionManager
							.getInstance()
							.addYouMengEventRecord(
									SettingActivity.this,
									action,
									DataCollectionConstant.EVENT_ID_SETTING_OPEN_NO_PIC,
									null);
				} else
				{
					DataCollectionManager
							.getInstance()
							.addRecord(
									DataCollectionManager
											.getAction(
													action,
													DataCollectionConstant.DATA_COLLECTION_SETTING_CLOSE_NO_PIC_VALUE));
					// 添加友盟的自定义事件的数据采集
					DataCollectionManager
							.getInstance()
							.addYouMengEventRecord(
									SettingActivity.this,
									action,
									DataCollectionConstant.EVENT_ID_SETTING_CLOSE_NO_PIC,
									null);
				}
				preferences.setNoPicModel(isChecked);
				// 发送无图省流量模式改变的广播
				sendBroadcast(new Intent(Constants.ACTION_NO_PIC_MODEL_CHANGE));
			}
		});
		deleteApkCheck = (CheckBox) findViewById(R.id.setting_delete_apk_on_off_checkbox);
		deleteApkCheck.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked)
			{
				if (isChecked)
				{
					DataCollectionManager
							.getInstance()
							.addRecord(
									DataCollectionManager
											.getAction(
													action,
													DataCollectionConstant.DATA_COLLECTION_SETTING_OPEN_DELETE_APK_FILE_VALUE));
					// 添加友盟的自定义事件的数据采集
					DataCollectionManager
							.getInstance()
							.addYouMengEventRecord(
									SettingActivity.this,
									action,
									DataCollectionConstant.EVENT_ID_SETTING_OPEN_DELETE_APK_FILE,
									null);
				} else
				{
					DataCollectionManager
							.getInstance()
							.addRecord(
									DataCollectionManager
											.getAction(
													action,
													DataCollectionConstant.DATA_COLLECTION_SETTING_CLOSE_DELETE_APK_FILE_VALUE));
					// 添加友盟的自定义事件的数据采集
					DataCollectionManager
							.getInstance()
							.addYouMengEventRecord(
									SettingActivity.this,
									action,
									DataCollectionConstant.EVENT_ID_SETTING_CLOSE_DELETE_APK_FILE,
									null);
				}
				preferences.setDeleteApk(isChecked);
			}
		});
		autoUpdateCheck = (CheckBox) findViewById(R.id.setting_auto_update_on_off_checkbox);
		autoUpdateCheck
				.setOnCheckedChangeListener(new OnCheckedChangeListener()
				{
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked)
					{
						if (isChecked)
						{
							DataCollectionManager
									.getInstance()
									.addRecord(
											DataCollectionManager
													.getAction(
															action,
															DataCollectionConstant.DATA_COLLECTION_SETTING_OPEN_AUTO_UPDATE_VALUE));
							// 添加友盟的自定义事件的数据采集
							DataCollectionManager
									.getInstance()
									.addYouMengEventRecord(
											SettingActivity.this,
											action,
											DataCollectionConstant.EVENT_ID_SETTING_OPEN_AUTO_UPDATE,
											null);
						} else
						{
							DataCollectionManager
									.getInstance()
									.addRecord(
											DataCollectionManager
													.getAction(
															action,
															DataCollectionConstant.DATA_COLLECTION_SETTING_CLOSE_AUTO_UPDATE_VALUE));
							// 添加友盟的自定义事件的数据采集
							DataCollectionManager
									.getInstance()
									.addYouMengEventRecord(
											SettingActivity.this,
											action,
											DataCollectionConstant.EVENT_ID_SETTING_CLOSE_AUTO_UPDATE,
											null);
						}
						preferences.setAutoUpdate(isChecked);
						if (LeplayPreferences.getInstance(SettingActivity.this)
								.isAutoUpdate())
						{
							// 开始自动下载更新
							AutoUpdateManager.getInstance().autoUpdate();
						}
					}
				});
		receiverPushCheck = (CheckBox) findViewById(R.id.setting_push_on_off_checkbox);
		receiverPushCheck
				.setOnCheckedChangeListener(new OnCheckedChangeListener()
				{
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked)
					{
						if (isChecked)
						{
							DataCollectionManager
									.getInstance()
									.addRecord(
											DataCollectionManager
													.getAction(
															action,
															DataCollectionConstant.DATA_COLLECTION_SETTING_OPEN_PUSH_MSG_VALUE));
							// 添加友盟的自定义事件的数据采集
							DataCollectionManager
									.getInstance()
									.addYouMengEventRecord(
											SettingActivity.this,
											action,
											DataCollectionConstant.EVENT_ID_SETTING_OPEN_PUSH_MSG,
											null);
						} else
						{
							DataCollectionManager
									.getInstance()
									.addRecord(
											DataCollectionManager
													.getAction(
															action,
															DataCollectionConstant.DATA_COLLECTION_SETTING_CLOSE_PUSH_MSG_VALUE));
							// 添加友盟的自定义事件的数据采集
							DataCollectionManager
									.getInstance()
									.addYouMengEventRecord(
											SettingActivity.this,
											action,
											DataCollectionConstant.EVENT_ID_SETTING_CLOSE_PUSH_MSG,
											null);
						}

						preferences.setReceiverPushMsg(isChecked);
					}
				});

		if (ToolsUtil.isWifiConnection(SettingActivity.this))
		{
			curNetwork.setText(getResources().getString(R.string.wifi));
		} else
		{
			curNetwork.setText(getResources().getString(R.string.not_wifi));
		}

		clearCancleLay = (LinearLayout) findViewById(R.id.setting_clear_local_cancle_lay);
		clearCancleLay.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				LoadingDialog loadingDialog = new LoadingDialog(
						SettingActivity.this, getResources().getString(
								R.string.clearing));
				loadingDialog.show();
				ImageLoaderManager.getInstance().clearMemoryCache();
				DownloadManager downloadManager = DownloadManager
						.shareInstance();
				AutoUpdateManager autoUpdateManager = AutoUpdateManager
						.getInstance();
				File donsonFile = new File(Environment
						.getExternalStorageDirectory(), "donson");
				if (donsonFile.exists())
				{
					if (downloadManager.getAllTaskInfo().isEmpty())
					{
						// 如果当前没有下载任务，删除下载文件夹下的所有文件
						// File downloadApkFile = new File(donsonFile,
						// "downloadApk");
						// if (downloadApkFile.exists())
						// {
						// ToolsUtil.deleteAllFile(downloadApkFile);
						// }
						ToolsUtil.deleteAllFile(donsonFile);
					} else
					{
						for (DownloadInfo autoDownloadInfo : autoUpdateManager.downloadManager
								.getAllTaskInfo())
						{
							autoUpdateManager.downloadManager
									.deleteDownload(autoDownloadInfo);
						}
						File updateApkFile = new File(donsonFile, "updateApk");
						if (updateApkFile.exists())
						{
							ToolsUtil.deleteAllFile(updateApkFile);
						}
						File logFile = new File(donsonFile, "log");
						if (logFile.exists())
						{
							ToolsUtil.deleteAllFile(logFile);
						}
					}
					// for (DownloadInfo downloadInfo : downloadManager
					// .getAllTaskInfo())
					// {
					// downloadManager.deleteDownload(downloadInfo);
					// }

					// 清除etag缓存
					EtagManager.getInstance().getEtagMap().clear();
					ToolsUtil.clearCacheDataToFile(SettingActivity.this,
							Constants.ETAG_CANCLE_FILE_NAME);
				}
				loadingDialog.dismiss();
				Toast.makeText(
						SettingActivity.this,
						getResources().getString(R.string.clear_cancle_success),
						Toast.LENGTH_SHORT).show();
			}
		});

		noPicCheck.setChecked(preferences.isNoPicModel());
		deleteApkCheck.setChecked(preferences.isDeleteApk());
		autoUpdateCheck.setChecked(preferences.isAutoUpdate());
		receiverPushCheck.setChecked(preferences.isReceiverPushMsg());

	}

	public static void startSettingActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				SettingActivity.class), action);
	}

}
