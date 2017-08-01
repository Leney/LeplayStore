package com.xd.leplay.store.gui.treasure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ConstantManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.gui.recommend.GetCoinActivity;
import com.xd.leplay.store.gui.webview.WebViewActivity;
import com.xd.leplay.store.gui.webview.WeixinPublicAccountWebViewActivity;
import com.xd.leplay.store.model.LoginedUserInfo;

/**
 * 开始赚钱界面
 * 
 * @author lilijun
 *
 */
public class GainCoinsActivity extends BaseActivity implements OnClickListener
{
	/** 今日收入 */
	private TextView todayGainCoins;

	/** 每日任务、邀请好友、关注爱玩、下载奖励 */
	private LinearLayout downloadReward, invitedFriendsLay, attentionAiWanLay,
			downloadAppsLay;
	/** 提现奖励 、赚取记录、赚钱秘籍 */
	private FrameLayout friendsBackLay, gainCoinsRecordLay, getMoneyTypeLay;

	/** 好友返利描述、邀请好友描述 */
	private TextView inviteFriendsDes;

	// private View firstSplitLine;

	private LoginUserInfoManager loginUserInfoManager = null;

	private LoginedUserInfo userInfo = null;

	private ConstantManager constantManager = null;

	@Override
	protected void initView()
	{
		action = DataCollectionManager
				.getAction(
						DataCollectionManager
								.getIntentDataCollectionAction(getIntent()),
						DataCollectionConstant.DATA_COLLECTION_TREASURE_GAIN_COINS_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		titleView.setTitleName(getResources().getString(
				R.string.treasure_begin_get_coins));
		titleView.setRightLayVisible(false);
		titleView.setBottomLineVisible(true);

		loadingView.setVisibilyView(false);
		loginUserInfoManager = LoginUserInfoManager.getInstance();
		userInfo = loginUserInfoManager.getLoginedUserInfo();
		constantManager = ConstantManager.getInstance();

		setCenterView(R.layout.gain_coins_activity);

		todayGainCoins = (TextView) findViewById(R.id.gain_coins_today_coins);

		// firstSplitLine = findViewById(R.id.gain_coins_top_spilt_line);
		friendsBackLay = (FrameLayout) findViewById(R.id.gain_coins_friend_get_back_lay);
		friendsBackLay.setOnClickListener(this);
		gainCoinsRecordLay = (FrameLayout) findViewById(R.id.gain_coins_get_record_lay);
		gainCoinsRecordLay.setOnClickListener(this);
		getMoneyTypeLay = (FrameLayout) findViewById(R.id.gain_coins_get_sercireate_lay);
		getMoneyTypeLay.setOnClickListener(this);
		invitedFriendsLay = (LinearLayout) findViewById(R.id.gain_coins_invite_friends);
		invitedFriendsLay.setOnClickListener(this);
		attentionAiWanLay = (LinearLayout) findViewById(R.id.gain_coins_attention_aiwan);
		attentionAiWanLay.setOnClickListener(this);
		downloadAppsLay = (LinearLayout) findViewById(R.id.gain_coins_download_app);
		downloadAppsLay.setOnClickListener(this);
		downloadReward = (LinearLayout) findViewById(R.id.gain_coins_download_reward);
		downloadReward.setOnClickListener(this);

		inviteFriendsDes = (TextView) findViewById(R.id.gain_coins_invite_friends_des);
		// if (constantManager.isHaveConstantInfo())
		// {
		inviteFriendsDes.setText(String.format(
				getResources().getString(R.string.invite_friends_des), " "
						+ constantManager.getConstantInfo()
								.getInviteFriendCoin() + " "));
		// } else
		// {
		// inviteFriendsDes.setText(String.format(
		// getResources().getString(R.string.invite_friends_des), " "
		// + ConstantManager.INVITE_FRIEND_COIN + " "));
		// registerDes.setText(String.format(
		// getResources().getString(R.string.register_and_login_des),
		// " " + ConstantManager.FIRST_REGISTER_COIN + " "));
		// }

		if (loginUserInfoManager.isHaveUserLogin())
		{
			// registerLay.setVisibility(View.GONE);
			// firstSplitLine.setVisibility(View.GONE);
			// if (userInfo.getTreasureInfo() != null)
			// {
			todayGainCoins.setText(String.format(
					getResources().getString(R.string.format_coins), userInfo
							.getTreasureInfo().getTodayCoins() + ""));
			// }
		} else
		{
			// registerLay.setVisibility(View.VISIBLE);
			// firstSplitLine.setVisibility(View.VISIBLE);
			todayGainCoins.setText(String.format(
					getResources().getString(R.string.format_coins), "0"));
		}

		IntentFilter filter = new IntentFilter(
				Constants.ACTION_ACCOUNT_HAVE_MODIFY);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.gain_coins_friend_get_back_lay:
			// 提现奖励
			WebViewActivity
					.startWebViewActivity(
							GainCoinsActivity.this,
							getResources().getString(R.string.cash_have),
							Constants.FRIEND_BACK_MONEY_URL,
							DataCollectionManager
									.getAction(
											action,
											DataCollectionConstant.DATA_COLLECTION_CLICK_CASH_BACK));
			break;
		case R.id.gain_coins_get_record_lay:
			// 赚取记录
			if (LoginUserInfoManager.getInstance().isHaveUserLogin())
			{
				GainCoinsRecordActivity.startGainCoinsRecordActivity(
						GainCoinsActivity.this, action);
			} else
			{
				LoginActivity
						.startLoginActivity(GainCoinsActivity.this, action);
			}
			break;
		case R.id.gain_coins_get_sercireate_lay:
			// 赚钱秘籍
			// 跳转到金币秘籍界面
			WebViewActivity
					.startWebViewActivity(
							GainCoinsActivity.this,
							getResources().getString(R.string.coins_details),
							Constants.UAC_API_URL + "/corn",
							DataCollectionManager
									.getAction(
											action,
											DataCollectionConstant.DATA_COLLECTION_TREASURE_GAIN_COINS_CLICK_CHEATS_VALUE));
			break;
		case R.id.gain_coins_invite_friends:
			// 邀请好友
			if (LoginUserInfoManager.getInstance().isHaveUserLogin())
			{
				InviteFriendsActivity.startInviteFriendsActivity(
						GainCoinsActivity.this, action);
			} else
			{
				LoginActivity
						.startLoginActivity(GainCoinsActivity.this, action);
			}
			break;
		case R.id.gain_coins_attention_aiwan:
			// 关注爱玩
			WeixinPublicAccountWebViewActivity.startActivity(this,
					getResources().getString(R.string.attention_aiwan),
					Constants.APP_API_URL + "/concern", action);
			break;
		case R.id.gain_coins_download_app:
			// 下载奖励
			GetCoinActivity
					.startGetCoinActivity(GainCoinsActivity.this, action);
			break;
		case R.id.gain_coins_download_reward:
			// 每日任务
			if (LoginUserInfoManager.getInstance().isHaveUserLogin())
			{
				DownloadRewardActivity.startDownloadRewardActivity(
						GainCoinsActivity.this, action);
			} else
			{
				LoginActivity
						.startLoginActivity(GainCoinsActivity.this, action);
			}

			break;
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (Constants.ACTION_ACCOUNT_HAVE_MODIFY.equals(intent.getAction()))
			{
				if (loginUserInfoManager.isHaveUserLogin())
				{
					// registerLay.setVisibility(View.GONE);
					// firstSplitLine.setVisibility(View.GONE);
					userInfo = loginUserInfoManager.getLoginedUserInfo();
					// if (userInfo.getTreasureInfo() != null)
					// {
					todayGainCoins.setText(String.format(getResources()
							.getString(R.string.format_coins), userInfo
							.getTreasureInfo().getTodayCoins() + ""));
					// }
				} else
				{
					// registerLay.setVisibility(View.VISIBLE);
					// firstSplitLine.setVisibility(View.VISIBLE);
					todayGainCoins.setText(String.format(getResources()
							.getString(R.string.format_coins), "0"));
				}
			}
		}
	};

	@Override
	protected void onDestroy()
	{
		unregisterReceiver(receiver);
		super.onDestroy();
	};

	public static void startGainCoinsActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				GainCoinsActivity.class), action);
	}

}
