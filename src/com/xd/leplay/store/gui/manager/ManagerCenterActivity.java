package com.xd.leplay.store.gui.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.AutoUpdateManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.gui.manager.update.ManageListAdapter;
import com.xd.leplay.store.gui.personal.PersonalCenterActivity;
import com.xd.leplay.store.model.InstalledAppInfo;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.model.ManagerAppInfo;
import com.xd.leplay.store.model.UpdateAppInfo;
import com.xd.leplay.store.model.proto.App.LocalAppVer;
import com.xd.leplay.store.model.proto.App.ReqAppsUpdate;
import com.xd.leplay.store.model.proto.App.RspAppsUpdate;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.NetUtil;
import com.xd.leplay.store.util.NetUtil.OnNetResponseLinstener;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.BottomDialog;
import com.xd.leplay.store.view.CustomImageView;
import com.xd.leplay.store.view.LoadingDialog;
import com.xd.leplay.store.view.MarketListView;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 管理中心界面
 * 
 * @author lilijun
 *
 */
public class ManagerCenterActivity extends BaseActivity implements
		OnClickListener
{
	/** 头像 */
	private CustomImageView userIcon;

	/** 用户名 */
	private TextView userName;

	// /** 我的金币显示区域、我的礼包显示区域 */
	// private LinearLayout myCoinsLay, myGiftsLay;

	// /** 我的金币数量、我的礼包数量 */
	// private TextView myCoinsNum, myGiftsNum;

	/** 管理应用、检查更新、关于我们、意见反馈 */
	private TextView aboutUs, feedBack;

	/** 下载应用、更新应用 */
	// private FrameLayout downloadAppLay, updateAppLay;

	/** 下载数字角标、更新数字角标 */
	// private TextView downloadNum, updateNum;

	private LoginUserInfoManager userInfoManager = null;

	private ImageLoaderManager imageLoaderManager = null;

	private DisplayImageOptions options = null;

	private LoadingDialog loadingDialog = null;

	private MarketListView listView = null;

	private SoftwareManager softwareManager = null;

	private List<ManagerAppInfo> allAppInfo = null;

	private List<ManagerAppInfo> updateInfo = null;

	private List<ManagerAppInfo> otherInfo = null;

	private ManageListAdapter adapter = null;

	@Override
	protected void initView()
	{
		action = DataCollectionConstant.DATA_COLLECTION_MANAGER_CENTER_VALUE;
		DataCollectionManager.getInstance().addRecord(action);

		imageLoaderManager = ImageLoaderManager.getInstance();
		options = DisplayUtil.getUserIconImageLoaderOptions();

		loadingView.setVisibilyView(false);
		// titleView.setTitleBackgroundColor(getResources().getColor(
		// R.color.manager_bg_color));
		titleView.setTitleBackground(R.drawable.personal_top_bg);
		titleView.setBackImgRes(R.drawable.back_img);
		titleView.setTitleColor(getResources().getColor(R.color.white));
		titleView.setBottomLineVisible(false);
		titleView.setRightSecondImgVisible(false);
		titleView.setTitleName(getResources()
				.getString(R.string.manager_center));
		titleView.setRightFirstImgRes(R.drawable.settting);
		titleView.setRightFirstImgVisible(true);
		titleView.setRightFirstImgOnclickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// 跳转到设置界面
				SettingActivity.startSettingActivity(
						ManagerCenterActivity.this, action);
			}
		});

		userInfoManager = LoginUserInfoManager.getInstance();

		setCenterView(R.layout.manager_center_activity);
		listView = (MarketListView) findViewById(R.id.managerCenterList);
		userIcon = (CustomImageView) findViewById(R.id.manager_center_user_icon);
		userIcon.setOnClickListener(this);

		userName = (TextView) findViewById(R.id.manager_center_user_name);
		// myCoinsNum = (TextView)
		// findViewById(R.id.manager_center_my_coins_num);
		// myCoinsLay = (LinearLayout)
		// findViewById(R.id.manager_center_my_coins_lay);
		// myCoinsLay.setOnClickListener(this);

		// myGiftsNum = (TextView)
		// findViewById(R.id.manager_center_my_gifts_num);
		// myGiftsLay = (LinearLayout)
		// findViewById(R.id.manager_center_my_gifts_lay);
		// myGiftsLay.setOnClickListener(this);

		// downloadAppLay = (FrameLayout)
		// findViewById(R.id.manager_cener_download_app_lay);
		// downloadAppLay.setOnClickListener(this);
		//
		// updateAppLay = (FrameLayout)
		// findViewById(R.id.manager_cener_update_app_lay);
		// updateAppLay.setOnClickListener(this);
		//
		// managerApp = (TextView) findViewById(R.id.manager_cener_manager_app);
		// managerApp.setOnClickListener(this);
		//
		// checkUpdate = (TextView)
		// findViewById(R.id.manager_cener_check_update);
		// checkUpdate.setOnClickListener(this);

		aboutUs = (TextView) findViewById(R.id.manager_cener_about_us);
		aboutUs.setOnClickListener(this);

		feedBack = (TextView) findViewById(R.id.manager_cener_feedback);
		feedBack.setOnClickListener(this);

		// downloadNum = (TextView)
		// findViewById(R.id.manager_cener_download_num);
		// downloadNum.setVisibility(View.GONE);
		// setDownloadNum(DownloadManager.shareInstance().getAllTaskInfo().size());
		//
		// updateNum = (TextView) findViewById(R.id.manager_cener_update_num);
		// updateNum.setVisibility(View.GONE);
		int number = 0;
		for (Entry<String, UpdateAppInfo> entry : SoftwareManager.getInstance()
				.getUpdateAppInfos().entrySet())
		{
			if (entry.getValue().isPromptUpgreade())
			{
				number++;
			}
		}
		// setUpdateNum(number);

		setUserInfoData();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_SOFTWARE_MANAGER_GET_UPDATE_LIST_FROM_NETWORK_FINISH);
		filter.addAction(Constants.ACTION_UPDATE_ACTIVITY_IGNORE_OR_CANCLE_IGNORE_APP);
		filter.addAction(Constants.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS);
		filter.addAction(Constants.ACTION_DOWNLOAD_TASK_COUNT_CHANGE);
		filter.addAction(Constants.ACTION_ACCOUNT_HAVE_MODIFY);
		filter.addAction(Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY);
		filter.addAction(Constants.ACTION_NO_PIC_MODEL_CHANGE);
		registerReceiver(receiver, filter);

		softwareManager = SoftwareManager.getInstance();

		updateInfo = new ArrayList<ManagerAppInfo>();

		otherInfo = new ArrayList<ManagerAppInfo>();

		allAppInfo = new ArrayList<ManagerAppInfo>();

		getListData();

		adapter = new ManageListAdapter(this, allAppInfo);
		listView.setAdapter(adapter);

	}

	private void getListData()
	{

		for (Entry<String, InstalledAppInfo> entry : softwareManager
				.getInstalledAppInfos().entrySet())
		{
			ManagerAppInfo managerAppInfo = new ManagerAppInfo();
			if (softwareManager.getUpdateAppInfos().containsKey(entry.getKey()))
			{
				managerAppInfo.setUpdateInfo(true);
				managerAppInfo.setUpdateAppInfo(softwareManager
						.getUpdateAppInfos().get(entry.getKey()));
				updateInfo.add(managerAppInfo);
			} else
			{
				if (!entry.getValue().isSystemApp())
				{
					managerAppInfo.setInstalledAppInfo(entry.getValue());
					otherInfo.add(managerAppInfo);
				}
			}
		}
		allAppInfo.addAll(updateInfo);
		allAppInfo.addAll(otherInfo);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.manager_center_user_icon:
			// 用户头像
			if (userInfoManager.isHaveUserLogin())
			{
				// 跳转到个人中心
				PersonalCenterActivity.startPersonalActivity(
						ManagerCenterActivity.this, action);
			} else
			{
				// 跳转到登录界面
				LoginActivity.startLoginActivity(ManagerCenterActivity.this,
						action);
			}
			break;
		// case R.id.manager_center_my_coins_lay:
		// // 我的金币
		// if (userInfoManager.isHaveUserLogin())
		// {
		// MyPurseActivity.startMyPurseActivity(
		// ManagerCenterActivity.this, action);
		// } else
		// {
		// LoginActivity.startLoginActivity(ManagerCenterActivity.this,
		// action);
		// }
		// break;
		// case R.id.manager_center_my_gifts_lay:
		// 我的礼包
		// if (userInfoManager.isHaveUserLogin())
		// {
		// MyGiftsActivity.startMyGiftsActivity(
		// ManagerCenterActivity.this, action);
		// } else
		// {
		// LoginActivity.startLoginActivity(ManagerCenterActivity.this,
		// action);
		// }
		// // 赚取记录
		// if (LoginUserInfoManager.getInstance().isHaveUserLogin())
		// {
		// GainCoinsRecordActivity.startGainCoinsRecordActivity(
		// ManagerCenterActivity.this, action);
		// } else
		// {
		// LoginActivity.startLoginActivity(ManagerCenterActivity.this,
		// action);
		// }
		// 刷新金币
		// if (LoginUserInfoManager.getInstance().isHaveUserLogin())
		// {
		// refreshDialog.show();
		// NetUtil.getLoginUserGiftsAndTreasureData(ManagerCenterActivity.this);
		// } else
		// {
		// LoginActivity.startLoginActivity(ManagerCenterActivity.this,
		// action);
		// }
		// break;
		// case R.id.manager_cener_download_app_lay:
		// // 下载应用
		// DownloadManagerActivity.startDownloadManagerActivity(this,
		// action);
		// break;
		// case R.id.manager_cener_update_app_lay:
		// // 更新应用
		// UpdateAppActivity.startUpdateAppActivity(this, action);
		// break;
		// case R.id.manager_cener_manager_app:
		// // 管理应用
		// ManageAppActivity.startManagerAppActivity(
		// ManagerCenterActivity.this, action);
		// break;
		// case R.id.manager_cener_check_update:
		// // 检查更新(实际上就是去查看更新列表中是否有爱玩商店本身的更新版本)
		// DataCollectionManager
		// .getInstance()
		// .addRecord(
		// DataCollectionManager
		// .getAction(
		// action,
		// DataCollectionConstant.DATA_COLLECTION_MANAGER_CHECK_UPDATE_VALUE));
		// DataCollectionManager.getInstance().addYouMengEventRecord(
		// ManagerCenterActivity.this, action,
		// DataCollectionConstant.EVENT_ID_CLICK_CHECK_UPDATE_BTN,
		// null);
		// if (loadingDialog == null)
		// {
		// loadingDialog = new LoadingDialog(ManagerCenterActivity.this,
		// getResources().getString(R.string.loading));
		// }
		// checkAppUpdateInfo();
		// if (SoftwareManager.getInstance().getUpdateAppInfos()
		// .containsKey(getPackageName()))
		// {
		// // 爱玩商店有更新
		// final UpdateAppInfo updateAppInfo = SoftwareManager
		// .getInstance().getUpdateAppInfos()
		// .get(getPackageName());
		// final BottomDialog updateDialog = new BottomDialog(
		// ManagerCenterActivity.this);
		// updateDialog.show();
		// updateDialog.setTitleName(getResources().getString(
		// R.string.check_update));
		// updateDialog.setCenterMsg(String.format(getResources()
		// .getString(R.string.aiwan_new_update), updateAppInfo
		// .getUpdateVersionName()));
		// updateDialog.setLeftBtnText(getResources().getString(
		// R.string.cancle));
		// updateDialog.setLeftBtnOnclickLinstener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// // 取消按钮的点击事件
		// updateDialog.dismiss();
		// }
		// });
		// updateDialog.setRightBtnText(getResources().getString(
		// R.string.sure));
		// updateDialog.setRightOnclickLinstener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// // 确定按钮的点击事件(添加更新下载任务)
		// DownloadManager.shareInstance().addDownload(
		// updateAppInfo.toDownloadInfo());
		// updateDialog.dismiss();
		// }
		// });
		// } else
		// {
		// // 爱玩商店没有更新
		// final BottomDialog noUpdateDialog = new BottomDialog(
		// ManagerCenterActivity.this);
		// noUpdateDialog.show();
		// noUpdateDialog.setTitleName(getResources().getString(
		// R.string.check_update));
		// noUpdateDialog.setCenterMsg(getResources().getString(
		// R.string.aiwan_already_new_update));
		// noUpdateDialog.setRightBtnVisible(false);
		// noUpdateDialog.setLeftBtnText(getResources().getString(
		// R.string.cancle));
		// noUpdateDialog.setLeftBtnOnclickLinstener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// noUpdateDialog.dismiss();
		// }
		// });
		// }
		// break;
		case R.id.manager_cener_about_us:
			// 关于我们
			AboutUsActivity.startAboutUsActivity(ManagerCenterActivity.this,
					action);
			break;
		case R.id.manager_cener_feedback:
			// 意见反馈
			FeedBackActivivty.startFeedBackActivity(ManagerCenterActivity.this,
					action);
			break;
		}
	}

	/**
	 * 设置下载任务条数的数字角标值
	 * 
	 * @param number
	 */
	// public void setDownloadNum(int number)
	// {
	// if (number <= 99)
	// {
	// downloadNum.setText(number + "");
	// } else
	// {
	// downloadNum.setText("99");
	// }
	// if (k != 0)
	// {
	// downloadNum.setVisibility(View.VISIBLE);
	// } else
	// {
	// downloadNum.setVisibility(View.GONE);
	// }
	// }

	/**
	 * 设置更新应用条数的数字角标值
	 * 
	 * @param number
	 */
	// public void setUpdateNum(int number)
	// {
	// if (number <= 99)
	// {
	// updateNum.setText(number + "");
	// } else
	// {
	// updateNum.setText("99");
	// }
	// if (number != 0)
	// {
	// updateNum.setVisibility(View.VISIBLE);
	// } else
	// {
	// updateNum.setVisibility(View.GONE);
	// }
	// }

	/**
	 * 设置用户信息
	 */
	public void setUserInfoData()
	{
		// refreshDialog.dismiss();
		if (userInfoManager.isHaveUserLogin())
		{
			LoginedUserInfo userInfo = userInfoManager.getLoginedUserInfo();
			if (userInfo.getNickName().trim().equals(""))
			{
				// 昵称为空 显示帐号
				userName.setText(userInfo.getAccount());
			} else
			{
				// 显示昵称
				userName.setText(userInfo.getNickName());
			}
			// myGiftsNum.setText(userInfoManager.getLoginedUserInfo()
			// .getGiftList().size()
			// + ""); TODO
			// myCoinsNum.setText(userInfo.getTreasureInfo().getCoinNum() + "");
			imageLoaderManager.displayImage(userInfo.getIconUrl(), userIcon,
					options);
		} else
		{
			userName.setText(getResources().getString(R.string.please_login));
			// myGiftsNum.setText(0 + "");
			// myCoinsNum.setText(0 + "");
			imageLoaderManager.displayImage("", userIcon, options);
		}
	}

	// private BroadcastReceiver receiver = new BroadcastReceiver()
	// {
	// @Override
	// public void onReceive(Context context, Intent intent)
	// {
	// if (Constants.ACTION_SOFTWARE_MANAGER_GET_UPDATE_LIST_FROM_NETWORK_FINISH
	// .equals(intent.getAction())
	// || Constants.ACTION_UPDATE_ACTIVITY_IGNORE_OR_CANCLE_IGNORE_APP
	// .equals(intent.getAction())
	// || Constants.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS
	// .equals(intent.getAction()))
	// {
	// // 从网络获取可更新应用数据完成
	// int number = 0;
	// for (Entry<String, UpdateAppInfo> entry : SoftwareManager
	// .getInstance().getUpdateAppInfos().entrySet())
	// {
	// if (entry.getValue().isPromptUpgreade())
	// {
	// number++;
	// }
	// }
	// // setUpdateNum(number);
	// } else if (Constants.ACTION_DOWNLOAD_TASK_COUNT_CHANGE
	// .equals(intent.getAction()))
	// {
	// // 下载任务条数发生改变
	// // setDownloadNum(DownloadManager.shareInstance().getAllTaskInfo()
	// // .size());
	// } else if (Constants.ACTION_ACCOUNT_HAVE_MODIFY.equals(intent
	// .getAction()))
	// {
	// // 账户信息发生变化
	// setUserInfoData();
	// } else if (Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY
	// .equals(intent.getAction()))
	// {
	// // 用户信息发生变化
	// setUserInfoData();
	// } else if (Constants.ACTION_NO_PIC_MODEL_CHANGE.equals(intent
	// .getAction()))
	// {
	// if (userInfoManager.isHaveUserLogin())
	// {
	// LoginedUserInfo userInfo = userInfoManager
	// .getLoginedUserInfo();
	// imageLoaderManager.displayImage(userInfo.getIconUrl(),
	// userIcon, options);
	// } else
	// {
	// imageLoaderManager.displayImage("", userIcon, options);
	// }
	// }
	// }
	// };
	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(
					Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS)
					|| intent
							.getAction()
							.equals(Constants.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS))
			{
				DLog.i("lilijun", "--------->>>>>>>unInstall");
				updateInfo.clear();
				otherInfo.clear();
				allAppInfo.clear();
				getListData();
				adapter.notifyDataSetChanged();
			} else if (Constants.ACTION_ACCOUNT_HAVE_MODIFY.equals(intent
					.getAction()))
			{
				// 账户信息发生变化
				setUserInfoData();
			} else if (Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY
					.equals(intent.getAction()))
			{
				// 用户信息发生变化
				setUserInfoData();
			} else if (Constants.ACTION_NO_PIC_MODEL_CHANGE.equals(intent
					.getAction()))
			{
				if (userInfoManager.isHaveUserLogin())
				{
					LoginedUserInfo userInfo = userInfoManager
							.getLoginedUserInfo();
					imageLoaderManager.displayImage(userInfo.getIconUrl(),
							userIcon, options);
				} else
				{
					imageLoaderManager.displayImage("", userIcon, options);
				}
			}
		}
	};

	/**
	 * 检查更新
	 */
	private void checkAppUpdateInfo()
	{
		InstalledAppInfo installedAppInfo = SoftwareManager.getInstance()
				.getInstalledAppInfos().get(getPackageName());
		if (installedAppInfo == null)
		{
			return;
		}
		loadingDialog.show();
		LocalAppVer.Builder localAppVerBuilder = LocalAppVer.newBuilder();
		localAppVerBuilder.setPackName(installedAppInfo.getPackageName());
		localAppVerBuilder.setVerName(installedAppInfo.getVersionName());
		localAppVerBuilder.setVerCode(installedAppInfo.getVersionCode());
		ReqAppsUpdate.Builder builder = ReqAppsUpdate.newBuilder();
		builder.addLocalAppVer(localAppVerBuilder.build());
		NetUtil.doLoadData(Constants.APP_API_URL, new String[]
		{ "ReqAppsUpdate" }, new ByteString[]
		{ builder.build().toByteString() }, new OnNetResponseLinstener()
		{
			@Override
			public void onNetError(String[] tags)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						loadingDialog.dismiss();
						Toast.makeText(ManagerCenterActivity.this,
								getResources().getString(R.string.net_error),
								Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onLoadSuccess(final RspPacket rspPacket)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						loadingDialog.dismiss();
						RspAppsUpdate rspAppsUpdate = null;
						try
						{
							rspAppsUpdate = RspAppsUpdate.parseFrom(rspPacket
									.getParams(0));
						} catch (InvalidProtocolBufferException e)
						{
							DLog.e("lilijun", "检查更新，解析应用更新返回数据异常", e);
							return;
						}
						if (rspAppsUpdate == null)
						{
							return;
						}
						if (rspAppsUpdate.getRescode() == 0)
						{
							if (rspAppsUpdate.getAppInfosList() == null
									|| rspAppsUpdate.getAppInfosList()
											.isEmpty())
							{
								// 当前已是最新版本
								final BottomDialog noUpdateDialog = new BottomDialog(
										ManagerCenterActivity.this);
								noUpdateDialog.show();
								noUpdateDialog.setTitleName(getResources()
										.getString(R.string.check_update));
								noUpdateDialog
										.setCenterMsg(getResources()
												.getString(
														R.string.aiwan_already_new_update));
								noUpdateDialog.setRightBtnVisible(false);
								noUpdateDialog.setLeftBtnText(getResources()
										.getString(R.string.cancle));
								noUpdateDialog
										.setLeftBtnOnclickLinstener(new OnClickListener()
										{
											@Override
											public void onClick(View v)
											{
												noUpdateDialog.dismiss();
											}
										});
								return;
							}
							// 爱玩商店有更新
							final UpdateAppInfo updateAppInfo = ToolsUtil
									.getUpdateAppInfo(rspAppsUpdate
											.getAppInfosList().get(0));
							final BottomDialog updateDialog = new BottomDialog(
									ManagerCenterActivity.this);
							updateDialog.show();
							updateDialog.setTitleName(getResources().getString(
									R.string.check_update));
							updateDialog.setCenterMsg(String.format(
									getResources().getString(
											R.string.aiwan_new_update),
									updateAppInfo.getUpdateVersionName()));
							updateDialog.setLeftBtnText(getResources()
									.getString(R.string.cancle));
							updateDialog
									.setLeftBtnOnclickLinstener(new OnClickListener()
									{
										@Override
										public void onClick(View v)
										{
											// 取消按钮的点击事件
											updateDialog.dismiss();
										}
									});
							updateDialog.setRightBtnText(getResources()
									.getString(R.string.sure));
							updateDialog
									.setRightOnclickLinstener(new OnClickListener()
									{
										@Override
										public void onClick(View v)
										{
											// 确定按钮的点击事件(添加更新下载任务)
											DownloadInfo downloadInfo = AutoUpdateManager
													.getInstance().downloadManager
													.queryDownload(getPackageName());
											DLog.i("lilijun",
													"自动下载任务的版本--------->>"
															+ downloadInfo
																	.getUpdateVersionCode());
											DLog.i("lilijun",
													"有更新任务的版本--------->>"
															+ updateAppInfo
																	.getUpdateVersionCode());
											if (downloadInfo != null
													&& DownloadInfo.STATE_FINISH == downloadInfo
															.getState()
													&& downloadInfo
															.getUpdateVersionCode() == updateAppInfo
															.getUpdateVersionCode())
											{
												// 如果自动下载任务列表中有此任务并且已经下载成功
												// 直接安装
												SoftwareManager
														.getInstance()
														.installApkByDownloadInfo(
																downloadInfo);
											} else
											{
												// 添加新的任务到下载列表中区
												DownloadManager
														.shareInstance()
														.addDownload(
																updateAppInfo
																		.toDownloadInfo());
											}
											updateDialog.dismiss();
										}
									});
						} else
						{
							Toast.makeText(
									ManagerCenterActivity.this,
									getResources().getString(
											R.string.check_update_failed),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}

			@Override
			public void onLoadFailed(RspPacket rspPacket)
			{
				runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						loadingDialog.dismiss();
						Toast.makeText(
								ManagerCenterActivity.this,
								getResources().getString(
										R.string.check_update_failed),
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	@Override
	protected void onDestroy()
	{
		unregisterReceiver(receiver);
		super.onDestroy();
	};

	public static void startManagerCenterActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				ManagerCenterActivity.class), action);
	}

}
