package com.xd.leplay.store.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.xd.leplay.store.model.proto.Uac.LevelInfo;

/**
 * 整个应用的常量信息类
 * 
 * @author lilijun
 *
 */
public class ConstantInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 首次登录注册所送金币 */
	private int firstRegisterCoin = 0;

	/** 邀请一个好友并成功注册之后所奖励的金币数量 */
	private int inviteFriendCoin = 0;

	/** 成功邀请多少好友 成为商店代言人 */
	private int inviteMaxFriendCount = 0;

	/** 成功邀请一定好友，成为商店代言人之后，所奖励的金币数 */
	private int inviteMaxFriendCoin = 0;

	/** 连续签到7天所额外奖励的金币数 */
	private int sign7daysCoins = 0;

	/** des加密key */
	private String desKey = "";

	/** 客服电话 */
	private String serviceTelephone = "";

	/** 粉丝QQ群号 */
	private String qqGroupNo = "";

	/** 微信公众帐号 */
	private String weixinPublicAccount = "";

	/** 公司邮箱 */
	private String companyEmail = "";

	/** 金币和人民币之间的兑换汇率(金币兑换人民币 例如:0.01 即100金币= 1元RMB) */
	private double exchangeRate = 0d;

	/** 邀请好友提现，返利利率(例:0.1=10%) */
	private double friendBackRate = 0d;

	/** 设备是否被注册过 */
	private int isDeviceRegisted;

	/** 是否存在红包活动 */
	private int isExistRedPacket;

	/** 等级信息对象 */
	private Map<Integer, LevelInfo> levelInfos = new HashMap<Integer, LevelInfo>();

	public int getFirstRegisterCoin()
	{
		return firstRegisterCoin;
	}

	public void setFirstRegisterCoin(int firstRegisterCoin)
	{
		this.firstRegisterCoin = firstRegisterCoin;
	}

	public int getInviteFriendCoin()
	{
		return inviteFriendCoin;
	}

	public void setInviteFriendCoin(int inviteFriendCoin)
	{
		this.inviteFriendCoin = inviteFriendCoin;
	}

	public int getInviteMaxFriendCount()
	{
		return inviteMaxFriendCount;
	}

	public void setInviteMaxFriendCount(int inviteMaxFriendCount)
	{
		this.inviteMaxFriendCount = inviteMaxFriendCount;
	}

	public int getInviteMaxFriendCoin()
	{
		return inviteMaxFriendCoin;
	}

	public void setInviteMaxFriendCoin(int inviteMaxFriendCoin)
	{
		this.inviteMaxFriendCoin = inviteMaxFriendCoin;
	}

	public int getSign7daysCoins()
	{
		return sign7daysCoins;
	}

	public void setSign7daysCoins(int sign7daysCoins)
	{
		this.sign7daysCoins = sign7daysCoins;
	}

	public String getDesKey()
	{
		return desKey;
	}

	public void setDesKey(String desKey)
	{
		this.desKey = desKey;
	}

	public int getIsDeviceRegisted()
	{
		return isDeviceRegisted;
	}

	public void setIsDeviceRegisted(int isDeviceRegisted)
	{
		this.isDeviceRegisted = isDeviceRegisted;
	}

	public int getIsExistRedPacket()
	{
		return isExistRedPacket;
	}

	public void setIsExistRedPacket(int isExistRedPacket)
	{
		this.isExistRedPacket = isExistRedPacket;
	}

	public String getServiceTelephone()
	{
		return serviceTelephone;
	}

	public void setServiceTelephone(String serviceTelephone)
	{
		this.serviceTelephone = serviceTelephone;
	}

	public String getQqGroupNo()
	{
		return qqGroupNo;
	}

	public void setQqGroupNo(String qqGroupNo)
	{
		this.qqGroupNo = qqGroupNo;
	}

	public String getWeixinPublicAccount()
	{
		return weixinPublicAccount;
	}

	public void setWeixinPublicAccount(String weixinPublicAccount)
	{
		this.weixinPublicAccount = weixinPublicAccount;
	}

	public String getCompanyEmail()
	{
		return companyEmail;
	}

	public void setCompanyEmail(String companyEmail)
	{
		this.companyEmail = companyEmail;
	}

	public double getExchangeRate()
	{
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate)
	{
		this.exchangeRate = exchangeRate;
	}

	public Map<Integer, LevelInfo> getLevelInfos()
	{
		return levelInfos;
	}

	public double getFriendBackRate()
	{
		return friendBackRate;
	}

	public void setFriendBackRate(double friendBackRate)
	{
		this.friendBackRate = friendBackRate;
	}
}
