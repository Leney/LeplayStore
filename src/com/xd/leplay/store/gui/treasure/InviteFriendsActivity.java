package com.xd.leplay.store.gui.treasure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ConstantManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.gui.webview.WebViewActivity;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.ToolsUtil;

/**
 * 邀请好友界面
 * 
 * @author lilijun
 *
 */
public class InviteFriendsActivity extends BaseActivity
{

	/** 邀请好友已获得的金币数量、邀请好友的个数、邀请码、成为商店代言人规则1,成为商店代言人规则2 、邀请一位朋友注册所获得金币 */
	private TextView inviteCoins, invideFriendCount, invideCode,
			inviteMaxRule1, inviteMaxRule2, inviteFriendCoin;

	/** 邀请的好友提现返利利率 */
	private TextView inviteRule3;
	/** 提现奖励 */
	private TextView friendBackMoney;

	/** 邀请好友按钮、二维码邀请 */
	private Button inviteFriendBtn, qdInviteBtn;

	private LoginedUserInfo userInfo = null;

	private ConstantManager constantManager = null;

	@SuppressLint("ResourceAsColor")
	@Override
	protected void initView()
	{
		action = DataCollectionManager
				.getAction(
						DataCollectionManager
								.getIntentDataCollectionAction(getIntent()),
						DataCollectionConstant.DATA_COLLECTION_TREASURE_INVITED_FRIENDS_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		userInfo = LoginUserInfoManager.getInstance().getLoginedUserInfo();

		titleView.setTitleName(getResources()
				.getString(R.string.invite_friends));
		titleView.setRightLayVisible(true);

		titleView.setRightFirstImgVisible(true);
		titleView.setRightTextBtnName(getResources().getString(
				R.string.cash_have));
		titleView
				.setRightTextBtnLeftDrawableResource(R.drawable.friend_cash_reward);
		titleView.setRightTextBtnColor(Color.parseColor("#999999"));
		titleView.setRightFirstImgOnclickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// 提现奖励
				WebViewActivity.startWebViewActivity(
						InviteFriendsActivity.this,
						getResources().getString(R.string.cash_have),
						Constants.FRIEND_BACK_MONEY_URL,
						DataCollectionManager
								.getAction(
										action,
										DataCollectionConstant.DATA_COLLECTION_CLICK_CASH_BACK));
			}
		});

		titleView.setRightTextBtnOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// 提现奖励
				WebViewActivity.startWebViewActivity(
						InviteFriendsActivity.this,
						getResources().getString(R.string.cash_have),
						Constants.FRIEND_BACK_MONEY_URL,
						DataCollectionManager
								.getAction(
										action,
										DataCollectionConstant.DATA_COLLECTION_CLICK_CASH_BACK));

			}
		});

		titleView.setBottomLineVisible(false);
		titleView.setRightFirstImgVisible(false);
		titleView.setRightSecondImgVisible(false);
		loadingView.setVisibilyView(false);

		constantManager = ConstantManager.getInstance();
		setCenterView(R.layout.invite_friends_activity);

		inviteCoins = (TextView) findViewById(R.id.invite_friends_already_gain_coins);
		invideFriendCount = (TextView) findViewById(R.id.invite_friends_successed_friends_num);
		invideCode = (TextView) findViewById(R.id.invite_friends_code);
		inviteFriendBtn = (Button) findViewById(R.id.invite_friends_btn);
		inviteFriendBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// 邀请好友 按钮点击事件
				DataCollectionManager
						.getInstance()
						.addRecord(
								DataCollectionManager
										.getAction(
												action,
												DataCollectionConstant.DATA_COLLECTION_TREASURE_CLICK_INVITED_FRIENDS_BTN_VALUE));
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								InviteFriendsActivity.this,
								action,
								DataCollectionConstant.EVENT_ID_TREASURE_CLICK_INVITED_FRIENDS_BTN,
								null);
				// 旧的邀请地址组合
				// String shareUrl = Constants.UAC_API_URL + "/invite?code="
				// + userInfo.getTreasureInfo().getInviteCode();
				// 新的邀请地址组合
				String shareUrl = Constants.UAC_API_URL + "invite/new/"
						+ userInfo.getUserId();
				String shareContent = String.format(
						getResources().getString(
								R.string.share_aiwan_invite_code), shareUrl);
				DisplayUtil
						.showShareDialog(
								InviteFriendsActivity.this,
								getResources().getString(
										R.string.invite_friends),
								shareContent,
								getResources().getString(
										R.string.share_app_title),
								getResources().getString(
										R.string.share_weixin_content),
								shareUrl, action);
			}
		});
		qdInviteBtn = (Button) findViewById(R.id.invite_friends_qd_invite_btn);
		qdInviteBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// 二维码邀请按钮
				InviteQdActivity.startActivity(InviteFriendsActivity.this,
						action);
			}
		});
		// inviteMaxRule1 = (TextView) findViewById(R.id.invite_friend_rule1);
		inviteMaxRule2 = (TextView) findViewById(R.id.invite_friend_rule2);
		inviteRule3 = (TextView) findViewById(R.id.invite_friend_rule3);
		inviteFriendCoin = (TextView) findViewById(R.id.invite_friend_have_coins);
		// inviteMaxRule1.setText(String.format(
		// getResources().getString(R.string.invited_rule1), " "
		// + constantManager.getConstantInfo()
		// .getInviteMaxFriendCount() + " "));

		String rule3Result = constantManager.getConstantInfo()
				.getFriendBackRate() * 100 + "%";
		String rule3 = String.format(
				getResources().getString(R.string.invite_friends_new_rule3),
				rule3Result);
		int start3 = rule3.indexOf(rule3Result);
		inviteRule3.setText(ToolsUtil.getFormatTextColor(rule3, start3, start3
				+ rule3Result.length(), "#e73636"));

		friendBackMoney = (TextView) findViewById(R.id.invite_friends_back_money);

		String rule2Result = constantManager.getConstantInfo()
				.getInviteMaxFriendCoin()
				* constantManager.getConstantInfo().getExchangeRate() + "";
		String rule2 = String.format(
				getResources().getString(R.string.invite_friends_new_rule2),
				rule2Result);
		int start2 = rule2.indexOf(rule2Result);
		inviteMaxRule2.setText(ToolsUtil.getFormatTextColor(rule2, start2,
				start2 + rule2Result.length() + 1, "#e73636"));
		// double money = userInfoManager.getLoginedUserInfo().getTreasureInfo()
		// .getCoinNum()
		// * constantManager.getConstantInfo().getExchangeRate();
		String rule1Result = constantManager.getConstantInfo()
				.getInviteFriendCoin()
				* constantManager.getConstantInfo().getExchangeRate() + "";
		String rule1 = String.format(
				getResources().getString(R.string.invite_friends_new_rule1),
				rule1Result);
		int start1 = rule1.indexOf(rule1Result);
		// ToolsUtil.getFormatTextColor(rule1, start, rule1Result.length()-1,
		// "#e73636");
		inviteFriendCoin.setText(ToolsUtil.getFormatTextColor(rule1, start1,
				start1 + rule1Result.length() + 1, "#e73636"));

		inviteCoins.setText(String.format(
				getResources().getString(R.string.yuan), userInfo
						.getTreasureInfo().getInviteCoins()
						* constantManager.getConstantInfo().getExchangeRate()
						+ ""));
		invideFriendCount.setText(String.format(
				getResources().getString(R.string.ren), userInfo
						.getTreasureInfo().getInviteFriendCount()));
		friendBackMoney.setText(String.format(
				getResources().getString(R.string.yuan), userInfo
						.getTreasureInfo().getFriendBackMoney() + ""));

		if ("".equals(userInfo.getPhone()))
		{
			// 没有绑定手机号码
			invideCode.setText(userInfo.getTreasureInfo().getInviteCode() + "");
		} else
		{
			// 有绑定手机号码
			String result = userInfo.getTreasureInfo().getInviteCode() + " "
					+ getResources().getString(R.string.or) + " "
					+ userInfo.getPhone();
			int start = result.indexOf(getResources().getString(R.string.or));
			invideCode.setText(ToolsUtil.getFormatTextColor(result, start,
					start + 1, "#567792"));
		}
	}

	public static void startInviteFriendsActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				InviteFriendsActivity.class), action);
	}

}
