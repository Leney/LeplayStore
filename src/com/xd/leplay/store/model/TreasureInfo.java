package com.xd.leplay.store.model;

import java.io.Serializable;

/**
 * 财富对象
 * 
 * @author lilijun
 *
 */
public class TreasureInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 总金币数量 */
	private int coinNum = -1;

	/** 今天是否已签到 */
	private boolean isSignIn = false;

	/** 连续签到天数 */
	private int continuSignDays = 0;

	/** 今日收入金币数 */
	private int todayCoins = 0;

	/** 邀请好友所获得的金币 */
	private int inviteCoins;

	/** 已经邀请好友总数量 */
	private int inviteFriendCount;

	/** 邀请码 */
	private String inviteCode;

	/** 我的等级名称 */
	private String levalName;

	/** 我的等级名称所对应的等级数字排行编号 */
	private int levalNo;

	/** 已提现的金额 */
	private double withdrawMoney;

	/** 下载奖励下载应用总数 */
	private int downloadRewardApps = 0;

	/** 下载奖励下载金币总数 */
	private int downloadRewardCoins = 0;

	/** 邀请的好友提现奖励的总金额数 */
	private double friendBackMoney = 0d;
	/** 任务下载收入 */
	private double downloadAndTaskMoney = 0d;
	/** 签到抽奖收入 */
	private double signAndLotteryMoney = 0d;
	/** 邀请收入 */
	private double inviteMoney = 0d;
	/** 其他收入 */
	private double otherMoney = 0d;

	public int getCoinNum()
	{
		return coinNum;
	}

	public void setCoinNum(int coinNum)
	{
		this.coinNum = coinNum;
	}

	public boolean isSignIn()
	{
		return isSignIn;
	}

	public void setSignIn(boolean isSignIn)
	{
		this.isSignIn = isSignIn;
	}

	public int getContinuSignDays()
	{
		return continuSignDays;
	}

	public void setContinuSignDays(int continuSignDays)
	{
		this.continuSignDays = continuSignDays;
	}

	public int getTodayCoins()
	{
		return todayCoins;
	}

	public void setTodayCoins(int todayCoins)
	{
		this.todayCoins = todayCoins;
	}

	public int getInviteCoins()
	{
		return inviteCoins;
	}

	public void setInviteCoins(int inviteCoins)
	{
		this.inviteCoins = inviteCoins;
	}

	public int getInviteFriendCount()
	{
		return inviteFriendCount;
	}

	public void setInviteFriendCount(int inviteFriendCount)
	{
		this.inviteFriendCount = inviteFriendCount;
	}

	public String getInviteCode()
	{
		return inviteCode;
	}

	public void setInviteCode(String inviteCode)
	{
		this.inviteCode = inviteCode;
	}

	public String getLevalName()
	{
		return levalName;
	}

	public void setLevalName(String levalName)
	{
		this.levalName = levalName;
	}

	public int getLevalNo()
	{
		return levalNo;
	}

	public void setLevalNo(int levalNo)
	{
		this.levalNo = levalNo;
	}

	public double getWithdrawMoney()
	{
		return withdrawMoney;
	}

	public void setWithdrawMoney(double withdrawMoney)
	{
		this.withdrawMoney = withdrawMoney;
	}

	public int getDownloadRewardApps()
	{
		return downloadRewardApps;
	}

	public void setDownloadRewardApps(int downloadRewardApps)
	{
		this.downloadRewardApps = downloadRewardApps;
	}

	public int getDownloadRewardCoins()
	{
		return downloadRewardCoins;
	}

	public void setDownloadRewardCoins(int downloadRewardCoins)
	{
		this.downloadRewardCoins = downloadRewardCoins;
	}

	public double getDownloadAndTaskMoney()
	{
		return downloadAndTaskMoney;
	}

	public void setDownloadMoney(double downloadAndTaskMoney)
	{
		this.downloadAndTaskMoney = downloadAndTaskMoney;
	}

	public double getSignAndLotteryMoney()
	{
		return signAndLotteryMoney;
	}

	public void setSignAndLotteryMoney(double signAndLotteryMoney)
	{
		this.signAndLotteryMoney = signAndLotteryMoney;
	}

	public double getInviteMoney()
	{
		return inviteMoney;
	}

	public void setInviteMoney(double inviteMoney)
	{
		this.inviteMoney = inviteMoney;
	}

	public double getOtherMoney()
	{
		return otherMoney;
	}

	public void setOtherMoney(double otherMoney)
	{
		this.otherMoney = otherMoney;
	}

	public double getFriendBackMoney()
	{
		return friendBackMoney;
	}

	public void setFriendBackMoney(double friendBackMoney)
	{
		this.friendBackMoney = friendBackMoney;
	}
}
