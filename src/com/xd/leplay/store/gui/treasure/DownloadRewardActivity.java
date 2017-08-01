package com.xd.leplay.store.gui.treasure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.gui.personal.PersonalCenterActivity;
import com.xd.leplay.store.gui.search.SearchActivity;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqDownloadInfo;
import com.xd.leplay.store.model.proto.Uac.RspDownloadInfo;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.TextUtil;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.CustomImageView;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.umeng.analytics.MobclickAgent;

/**
 * 每日任务界面
 * 
 * @author luoxingxing
 *
 */
public class DownloadRewardActivity extends BaseActivity implements
		OnClickListener
{

	private final String TAG = "DownloadRewardActivity";

	private FragmentManager fragmentManager = null;

	private LoginUserInfoManager userInfoManager = null;

	/** 登录用户信息 */
	private LoginedUserInfo userInfo = null;

	/** 用户头像 */
	private CustomImageView userIcon = null;

	/** 朋友圈、QQ好友、微信好友、更多分享图片 */
	private ImageView wxCirlcelShare = null, qqShaer = null,
			wxFriendShare = null, moreShare = null;

	/** 玩家级别 */
	private TextView userLevel = null;

	/** 下载应用总数 */
	private TextView downloadAppsCount = null;

	/** 下载应用总数 */
	private int downLoadAppCount = 0;

	/** 下载金币总数 */
	private TextView downloadCoinCount = null;

	/** 下载金币总数 */
	private int downloadCoinTotal = 0;

	/** 再下载相应应用个数，获得额外奖励金币数 */
	private TextView needDownLoadCounts = null;

	/** 下载列表加载图片的配置 */
	private DisplayImageOptions options = null;

	/** 图片加载器 */
	private ImageLoaderManager imageLoader = null;

	/** 用户下载信息请求TAG */
	private static String GET_USER_DOWNLOAD_INFO_REQUEST_TAG = "ReqDownloadInfo";

	/** 用户下载信息相应TAG */
	private static String GET_USER_DOWNLOAD_INFO_RSPONSE_TAG = "RspDownloadInfo";

	/** 搜索编辑框 */
	private LinearLayout titleSearchEditLay;

	/** 标题 */
	private LinearLayout titleBackLay;

	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void initView()
	{
		if (!LoginUserInfoManager.getInstance().isHaveUserLogin())
		{
			finish();
			return;
		}
		// 不需要父类进行友盟的数据采集
		isNeedCollection = false;
		action = DataCollectionManager
				.getAction(
						DataCollectionManager
								.getIntentDataCollectionAction(getIntent()),
						DataCollectionConstant.DATA_COLLECTION_TREASURE_DOWNLOAD_REWARD_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		/*
		 * titleView.setTitleName((getResources()
		 * .getString(R.string.download_reward_title)));
		 * titleView.setRightLayVisible(false);
		 * titleView.setBottomLineVisible(false);
		 */
		titleView.setVisibility(View.GONE);

		loadingView.setVisibilyView(true);
		setCenterView(R.layout.download_reward_activity);
		centerViewLayout.setVisibility(View.INVISIBLE);
		fragmentManager = getSupportFragmentManager();
		userInfoManager = LoginUserInfoManager.getInstance();

		imageLoader = ImageLoaderManager.getInstance();
		options = DisplayUtil.getUserIconImageLoaderOptions();

		userIcon = (CustomImageView) findViewById(R.id.download_reward_user_icon);
		userIcon.setOnClickListener(this);
		userInfo = LoginUserInfoManager.getInstance().getLoginedUserInfo();
		imageLoader.displayImage(userInfo.getIconUrl(), userIcon, options);
		titleSearchEditLay = (LinearLayout) findViewById(R.id.mydownload_search_edit_lay);
		titleSearchEditLay.setOnClickListener(this);
		titleBackLay = (LinearLayout) findViewById(R.id.mydownload__title_lay);
		titleBackLay.setOnClickListener(this);
		userLevel = (TextView) findViewById(R.id.download_reward_user_level);
		userLevel.setText(userInfo.getTreasureInfo().getLevalName());
		downloadAppsCount = (TextView) findViewById(R.id.download_reward_app_num);
		downloadCoinCount = (TextView) findViewById(R.id.already_gain_coin_sum);
		needDownLoadCounts = (TextView) findViewById(R.id.download_app_gain_extra_reward);

		wxCirlcelShare = (ImageView) findViewById(R.id.download_reward_share_wx_circle);
		wxCirlcelShare.setOnClickListener(this);
		qqShaer = (ImageView) findViewById(R.id.download_reward_share_qq);
		qqShaer.setOnClickListener(this);
		wxFriendShare = (ImageView) findViewById(R.id.download_reward_share_wx_friend);
		wxFriendShare.setOnClickListener(this);
		moreShare = (ImageView) findViewById(R.id.download_reward_share_more);
		moreShare.setOnClickListener(this);

		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.add(R.id.download_reward_fragment_lay,
				new DownloadRewardFragment());
		transaction.commitAllowingStateLoss();

		IntentFilter filter = new IntentFilter(
				Constants.ACTION_ACCOUNT_HAVE_MODIFY);
		registerReceiver(receiver, filter);

		doLoadData(Constants.UAC_API_URL, new String[]
		{ GET_USER_DOWNLOAD_INFO_REQUEST_TAG }, new ByteString[]
		{ getUserDownLoadInfoRequestData() }, "");
	}

	@Override
	public void onClick(View v)
	{

		if (LoginUserInfoManager.getInstance().isHaveUserLogin())
		{
			String shareUrl = Constants.AIWAN_APK_DOWNLOAD_URL;

			String shareTitle = getResources().getString(
					R.string.download_reward_share_title);

			String shareContent = String.format(
					getResources().getString(
							R.string.download_reward_share_content),
					ToolsUtil.getFormatPraiseCount(userInfoManager
							.getLoginedUserInfo().getTreasureInfo()
							.getDownloadRewardApps())
							+ "",
					ToolsUtil.getFormatPraiseCount(userInfoManager
							.getLoginedUserInfo().getTreasureInfo()
							.getDownloadRewardCoins())
							+ "", getDownLoadCoins(userInfoManager
							.getLoginedUserInfo().getTreasureInfo()
							.getDownloadRewardCoins())
							+ "", shareUrl);

			String wxContent = String.format(
					getResources().getString(
							R.string.download_reward_share_content),
					ToolsUtil.getFormatPraiseCount(userInfoManager
							.getLoginedUserInfo().getTreasureInfo()
							.getDownloadRewardApps())
							+ "",
					ToolsUtil.getFormatPraiseCount(userInfoManager
							.getLoginedUserInfo().getTreasureInfo()
							.getDownloadRewardCoins())
							+ "", getDownLoadCoins(userInfoManager
							.getLoginedUserInfo().getTreasureInfo()
							.getDownloadRewardCoins())
							+ "", "");
			if (downloadCoinTotal == 0)
			{
				shareContent = String.format(
						getResources().getString(
								R.string.show_off_share_content), shareUrl);
				wxContent = String.format(
						getResources().getString(
								R.string.show_off_share_content), "");
			}
			switch (v.getId())
			{
			/** 分享到微信朋友圈 */
			case R.id.download_reward_share_wx_circle:

				ToolsUtil.share2weixin(DownloadRewardActivity.this, 1,
						wxContent, "", shareUrl);

				String wxCircleAction = DataCollectionManager
						.getAction(
								action,
								DataCollectionConstant.DATA_COLLECTION_TREASURE_DOWNLOAD_REWARD_WXCIRCLE_SHARE_VALUE);
				DataCollectionManager.getInstance().addRecord(wxCircleAction);
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								DownloadRewardActivity.this,
								action,
								DataCollectionConstant.EVENT_ID_CLICK_SHARE_TO_WEIXIN_FRIEND_CIRCLE,
								null);
				break;
			/** 分享到qq好友 */
			case R.id.download_reward_share_qq:
				ToolsUtil.share2Spec(DownloadRewardActivity.this,
						"com.tencent.mobileqq", shareTitle, shareContent);

				String qqFriendAction = DataCollectionManager
						.getAction(
								action,
								DataCollectionConstant.DATA_COLLECTION_TREASURE_DOWNLOAD_REWARD_QQFRIEND_SHARE_VALUE);
				DataCollectionManager.getInstance().addRecord(qqFriendAction);
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								DownloadRewardActivity.this,
								action,
								DataCollectionConstant.EVENT_ID_CLICK_SHARE_TO_QQ,
								null);
				break;
			/** 分享到微信好友 */
			case R.id.download_reward_share_wx_friend:
				ToolsUtil.share2weixin(DownloadRewardActivity.this, 0, "",
						wxContent, shareUrl);
				String wxFriendAction = DataCollectionManager
						.getAction(
								action,
								DataCollectionConstant.DATA_COLLECTION_TREASURE_DOWNLOAD_REWARD_WXFRIEND_SHARE_VALUE);
				DataCollectionManager.getInstance().addRecord(wxFriendAction);
				DataCollectionManager.getInstance().addYouMengEventRecord(
						DownloadRewardActivity.this, action,
						DataCollectionConstant.EVENT_ID_CLICK_SHARE_TO_WEIXIN,
						null);
				break;
			/** 分享到更多 */
			case R.id.download_reward_share_more:
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, shareContent);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				DownloadRewardActivity.this.startActivity(Intent.createChooser(
						intent, shareTitle));
				String moreShareAction = DataCollectionManager
						.getAction(
								action,
								DataCollectionConstant.DATA_COLLECTION_SHARE_TO_OTHER_VALUE);
				DataCollectionManager.getInstance().addRecord(moreShareAction);
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								DownloadRewardActivity.this,
								action,
								DataCollectionConstant.EVENT_ID_CLICK_SHARE_TO_OTHER_VALUE,
								null);
				break;
			/** 跳转到个人中心界面 */
			case R.id.download_reward_user_icon:
				PersonalCenterActivity.startPersonalActivity(
						DownloadRewardActivity.this, action);
				String personalAction = DataCollectionManager.getAction(action,
						DataCollectionConstant.DATA_COLLECTION_PERSIONAL_VALUE);
				DataCollectionManager.getInstance().addRecord(personalAction);
				break;
			case R.id.mydownload_search_edit_lay:
				// 搜索编辑框
				SearchActivity.startSearchActivity(DownloadRewardActivity.this,
						"");
				String searchAction = DataCollectionManager.getAction(action,
						DataCollectionConstant.DATA_COLLECTION_SEARCH_VALUE);
				DataCollectionManager.getInstance().addRecord(searchAction);
				break;
			case R.id.mydownload__title_lay:
				DownloadRewardActivity.this.finish();
				break;
			}
		} else
			LoginActivity.startLoginActivity(DownloadRewardActivity.this,
					action);
	}

	/**
	 * 获取用户下载信息请求数据
	 * 
	 * @param userId
	 * @return
	 */
	private ByteString getUserDownLoadInfoRequestData()
	{
		ReqDownloadInfo.Builder builder = ReqDownloadInfo.newBuilder();
		builder.setUid(userInfo.getUserId());
		builder.setUserToken(userInfo.getUserToken());
		return builder.build().toByteString();
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		try
		{

			for (String action : rspPacket.getActionList())
			{

				if (action.equals(GET_USER_DOWNLOAD_INFO_RSPONSE_TAG))
				{
					RspDownloadInfo rspDownLoad = RspDownloadInfo
							.parseFrom(rspPacket.getParams(0));
					if (rspDownLoad.getRescode() == 0)
					{
						// 获取用户下载信息成功
						downLoadAppCount = rspDownLoad.getDownloadAppCount();

						int needDownloadCount = rspDownLoad
								.getNeedDownloadCount();
						int getExtraCoins = rspDownLoad.getRewardCoin();

						downloadAppsCount
								.setText(String
										.format(getResources()
												.getString(
														R.string.download_reward_app_num),
												" "
														+ ToolsUtil
																.getFormatPraiseCount(needDownloadCount),
												" "
														+ ToolsUtil
																.getFormatPraiseCount(getExtraCoins)));

						userInfoManager.getLoginedUserInfo().getTreasureInfo()
								.setDownloadRewardApps(downLoadAppCount);

						downloadCoinTotal = rspDownLoad.getDownloadCoinTotal();

						userInfoManager.getLoginedUserInfo().getTreasureInfo()
								.setDownloadRewardCoins(downloadCoinTotal);
						;

						downloadCoinCount
								.setText(String
										.format(getResources()
												.getString(
														R.string.download_reward_record_already_gain),
												" "
														+ ToolsUtil
																.getFormatPraiseCount(downLoadAppCount),
												" "
														+ ToolsUtil
																.getFormatPraiseCount(downloadCoinTotal)
														+ " "));

						downloadCoinCount.setText(TextUtil.setTextPartialColor(
								downloadCoinCount.getText().toString(),
								downloadCoinCount.getText().toString()
										.indexOf("取") + 1,
								downloadCoinCount.getText().toString()
										.indexOf("币"),
								getResources().getColor(
										R.color.red_self_text_color)));

						needDownLoadCounts
								.setText(String
										.format(getResources()
												.getString(
														R.string.download_app_gain_extra_reward),
												rspDownLoad.getMaxDailyAddUp()
														+ ""));

					} else if (rspDownLoad.getRescode() == 2)
					{
						// userToken错误
						// 退出登录
						LoginUserInfoManager.getInstance().exitLogin();
						Toast.makeText(DownloadRewardActivity.this,
								getResources().getString(R.string.re_login),
								Toast.LENGTH_SHORT).show();
						LoginActivity.startLoginActivity(
								DownloadRewardActivity.this, action);
						if (this != null)
						{
							finish();
						}
					} else
					{
						DLog.e("luoxingxing", rspDownLoad.getResmsg() + "--->>"
								+ rspDownLoad.getRescode());
						showErrorView();
					}
				}

			}
		} catch (InvalidProtocolBufferException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	protected void tryAgain()
	{
		super.tryAgain();
		// 获取用户下载信息
		doLoadData(Constants.UAC_API_URL, new String[]
		{ GET_USER_DOWNLOAD_INFO_REQUEST_TAG }, new ByteString[]
		{ getUserDownLoadInfoRequestData() }, "");
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		showErrorView();
		errorViewLayout.showLoadFailedLay();
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		showErrorView();
		errorViewLayout.showLoadFailedLay();
	}

	public static void startDownloadRewardActivity(Context context,
			String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				DownloadRewardActivity.class), action);
	}

	/**
	 * 格式化分享金币下载钱数eg:(5.12元)
	 */
	public String getDownLoadCoins(int gainCoins)
	{
		int interNum = gainCoins / 100;
		int pointNum = gainCoins % 100;
		if (gainCoins < 500)
		{
			return " ";
		} else
		{
			if (gainCoins % 100 == 0)
			{
				return "(" + ToolsUtil.getFormatPraiseCount(interNum) + "元"
						+ ")";
			} else
			{
				if (pointNum < 10)
				{
					return "(" + ToolsUtil.getFormatPraiseCount(interNum)
							+ ".0" + pointNum + "元" + ")";
				} else
					return "(" + ToolsUtil.getFormatPraiseCount(interNum) + "."
							+ pointNum + "元" + ")";
			}
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (!LoginUserInfoManager.getInstance().isHaveUserLogin())
			{
				DownloadRewardActivity.this.finish();
			}
		}
	};
}
