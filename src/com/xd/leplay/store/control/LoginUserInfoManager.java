package com.xd.leplay.store.control;

import android.content.Context;
import android.content.Intent;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.util.ToolsUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 登录的用户信息管理类
 * 
 * @author lilijun
 *
 */
public class LoginUserInfoManager
{

	private Context mContext = null;

	private static LoginUserInfoManager instance = null;

	/** 登录的用户信息类对象 */
	private LoginedUserInfo loginedUserInfo = null;

	private LoginUserInfoManager()
	{
	}

	public static LoginUserInfoManager getInstance()
	{
		if (instance == null)
		{
			synchronized (LoginUserInfoManager.class)
			{
				instance = new LoginUserInfoManager();
			}
		}
		return instance;
	}

	/**
	 * 初始化 登录的用户信息管理类(得到LoginedUserInfo对象)
	 * 
	 * @param context
	 */
	public void init(Context context)
	{
		this.mContext = context;
		loginedUserInfo = (LoginedUserInfo) ToolsUtil.getCacheDataFromFile(
				context, Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME);
		if (loginedUserInfo != null)
		{
			DLog.i("lilijun", "帐号：" + loginedUserInfo.getAccount());
			DLog.i("lilijun", "userId:" + loginedUserInfo.getUserId());
			DLog.i("lilijun", "登录Token:" + loginedUserInfo.getUserToken());
		}
	}

	/**
	 * 判断当前是否有用户登录
	 * 
	 * @return
	 */
	public boolean isHaveUserLogin()
	{
		if (loginedUserInfo != null)
		{
			if (loginedUserInfo.getTreasureInfo().getCoinNum() != -1)
			{
				return true;
			} else
			{
				return false;
			}
		} else
		{
			return false;
		}
		// return loginedUserInfo != null ? true : false;
	}

	/**
	 * 退出(注销登录)
	 */
	public void exitLogin()
	{
		DLog.i("lilijun", "退出登录！！！！");
		// 删除登录用户信息的缓存文件
		ToolsUtil.clearCacheDataToFile(mContext,
				Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME);
		// 清除赞的数据
		PraiseManager.getInstance().clearAllPraise();
		// 清除所有未上报的数据
		ReportDownLoadDataManager.getInstance().clearAllData();
		if (loginedUserInfo != null)
		{
			loginedUserInfo.getGiftList().clear();
			loginedUserInfo.getGiftIdList().clear();
		}
		// 将用户的登录信息对象为空
		loginedUserInfo = null;

//		// 置空大头鸟积分墙的客户端用户数据
//		DTNIntegralManager.getInstance().reSetClientUserId("");
//
//		// 置空点乐积分墙的客户端用户数据
//		DevInit.setCurrentUserID(mContext, "");
//
//		// 置空有米积分墙的客户端用户数据
//		OffersManager.getInstance(mContext).setCustomUserId("");
//
//		// 置空贝多积分墙的客户端用户数据
//		BeiduoPlatform.setUserId("");
//
//		// 置空多盟积分墙的客户端用户数据
//		DOW.getInstance(mContext).setUserId("");
//
//		// 置空点入积分墙的客户端用户数据
//		DRSdk.setUserId("");

		// 友盟统计 账户登出
		MobclickAgent.onProfileSignOff();

//		// 置空爱玩积分墙账户
//		AWApi.setClientUserId("");

		// 发送用户信息改变的广播
		mContext.sendBroadcast(new Intent(Constants.ACTION_ACCOUNT_HAVE_MODIFY));
	}

	public LoginedUserInfo getLoginedUserInfo()
	{
		return loginedUserInfo;
	}

	public void setLoginedUserInfo(LoginedUserInfo loginedUserInfo)
	{
		this.loginedUserInfo = loginedUserInfo;
	}

	/**
	 * 做旧版本数据兼容，将旧LoginedUserInfo数据设置到新的LoginedUserInfo对象中去
	 */
	public void setOldInfoToNewInfo(LoginedUserInfo newUserInfo)
	{
		newUserInfo.setUserId(loginedUserInfo.getUserId());
		newUserInfo.setAccount(loginedUserInfo.getAccount());
		newUserInfo.setSex(loginedUserInfo.getSex());
		newUserInfo.setEmail(loginedUserInfo.getEmail());
		newUserInfo.setPhone(loginedUserInfo.getPhone());
		newUserInfo.setIconUrl(loginedUserInfo.getIconUrl());
		newUserInfo.setLastLoginTime(loginedUserInfo.getLastLoginTime());
		newUserInfo.setRegisterTime(loginedUserInfo.getRegisterTime());
		newUserInfo.setUserStatus(loginedUserInfo.getUserStatus());
		newUserInfo.setRecommenderId(loginedUserInfo.getRecommenderId());
		newUserInfo.setNickName(loginedUserInfo.getNickName());
		newUserInfo.setUserToken(loginedUserInfo.getUserToken());
		newUserInfo.getGiftList().clear();
		newUserInfo.getGiftList().putAll(loginedUserInfo.getGiftList());
		newUserInfo.getGiftIdList().clear();
		newUserInfo.getGiftIdList().addAll(loginedUserInfo.getGiftIdList());

		// newUserInfo.getTreasureInfo().setCoinNum(
		// loginedUserInfo.getTreasureInfo().getCoinNum());
		newUserInfo.getTreasureInfo().setSignIn(
				loginedUserInfo.getTreasureInfo().isSignIn());
		newUserInfo.getTreasureInfo().setContinuSignDays(
				loginedUserInfo.getTreasureInfo().getContinuSignDays());
		// newUserInfo.getTreasureInfo().setTodayCoins(
		// loginedUserInfo.getTreasureInfo().getTodayCoins());
		// newUserInfo.getTreasureInfo().setInviteCoins(
		// loginedUserInfo.getTreasureInfo().getInviteCoins());
		// newUserInfo.getTreasureInfo().setInviteFriendCount(
		// loginedUserInfo.getTreasureInfo().getInviteFriendCount());
		// newUserInfo.getTreasureInfo().setInviteCode(
		// loginedUserInfo.getTreasureInfo().getInviteCode());
		// newUserInfo.getTreasureInfo().setLevalName(
		// loginedUserInfo.getTreasureInfo().getLevalName());
		// newUserInfo.getTreasureInfo().setLevalNo(
		// loginedUserInfo.getTreasureInfo().getLevalNo());
		// newUserInfo.getTreasureInfo().setWithdrawMoney(
		// loginedUserInfo.getTreasureInfo().getWithdrawMoney());
		try
		{
			newUserInfo.getTreasureInfo().setDownloadRewardApps(
					loginedUserInfo.getTreasureInfo().getDownloadRewardApps());
			newUserInfo.getTreasureInfo().setDownloadRewardCoins(
					loginedUserInfo.getTreasureInfo().getDownloadRewardCoins());
			// newUserInfo.getTreasureInfo().setFriendBackMoney(
			// loginedUserInfo.getTreasureInfo().getFriendBackMoney());
			// newUserInfo.getTreasureInfo()
			// .setDownloadMoney(
			// loginedUserInfo.getTreasureInfo()
			// .getDownloadAndTaskMoney());
			// newUserInfo.getTreasureInfo().setSignAndLotteryMoney(
			// loginedUserInfo.getTreasureInfo().getSignAndLotteryMoney());
			// newUserInfo.getTreasureInfo().setInviteMoney(
			// loginedUserInfo.getTreasureInfo().getInviteMoney());
			// newUserInfo.getTreasureInfo().setOtherMoney(
			// loginedUserInfo.getTreasureInfo().getOtherMoney());

			// newUserInfo.setOpenId(loginedUserInfo.getOpenId());
			// newUserInfo.setWxUnionId(loginedUserInfo.getWxUnionId());
			// newUserInfo.setUnreadMsgNum(loginedUserInfo.getUnreadMsgNum());
			// newUserInfo.getUserMsgList().clear();
			// newUserInfo.getUserMsgList().addAll(
			// loginedUserInfo.getUserMsgList());
		} catch (Exception e)
		{
			DLog.e("LoginUserInfoManager", "setOldInfoToNewInfo()#exception:",
					e);
		}
	}
}
