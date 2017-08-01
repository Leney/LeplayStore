package com.xd.leplay.store.control;

import com.xd.leplay.store.model.ConstantInfo;

/**
 * 常量管理类
 * 
 * @author lilijun
 *
 */
public class ConstantManager
{
	private static ConstantManager instance = null;

	private ConstantInfo constantInfo = null;

	/** 首次登录注册所送金币(当从网络没有获取到常量数据的时候 所使用的默认写死的数据) */
	public static int FIRST_REGISTER_COIN = 100;

	/** 邀请一个好友并成功注册之后所奖励的金币数量(当从网络没有获取到常量数据的时候 所使用的默认写死的数据) */
	public static int INVITE_FRIEND_COIN = 100;

	/** 成功邀请多少好友 成为商店代言人 (当从网络没有获取到常量数据的时候 所使用的默认写死的数据) */
	public static int INVITE_MAX_FRIEND_COUNT = 100;

	/** 成功邀请一定好友，成为商店代言人之后，所奖励的金币数(当从网络没有获取到常量数据的时候 所使用的默认写死的数据) */
	public static int INVITE_MAX_FRIEND_COIN = 10000;

	/** 连续签到7天所额外奖励的金币数(当从网络没有获取到常量数据的时候 所使用的默认写死的数据) */
	public static int SIGN_7_DAYS_COINS = 50;

	/** 客服电话 (当从网络没有获取到常量数据的时候 所使用的默认写死的数据) */
	public static String SERVICE_TELEPHONE = "0755-88602100 转 8192";

	/** 粉丝QQ群号 (当从网络没有获取到常量数据的时候 所使用的默认写死的数据) */
	public static String QQ_GROUP_NO = "175732183";

	/** 微信公众帐号(当从网络没有获取到常量数据的时候 所使用的默认写死的数据) */
	public static String WEIXIN_PUBLIC_ACCOUNT = "aiwanshangdian";

	/** 公司邮箱 (当从网络没有获取到常量数据的时候 所使用的默认写死的数据) */
	public static String COMPANY_EMAIL = "service@aiwan.hk";

	/** des加密key */
	public static String DES_KEY = "z#!.~ykZ";
	
	/** 设备是否被注册过 */
	public static int DEVICE_REGISTER=0;
	
	/** 是否有红包活动(0:无红包 1：有红包)*/
    public static int IS_EXIST_RED_PACKET=0;
	// /** 用户级别信息(当从网络没有获取到常量数据的时候 所使用的默认写死的数据) */
	// public static Map<Integer, LevelInfo> LEVEL_INFOS = new HashMap<Integer,
	// LevelInfo>();

	/**
	 * 金币和人民币之间的兑换汇率(金币兑换人民币 例如:0.01 即100金币= 1元RMB) (当从网络没有获取到常量数据的时候
	 * 所使用的默认写死的数据)
	 */
	public static double EXCHANGE_RATE = 0.01d;

	/** 邀请的好友提现，返利利率 (例:0.05=5%) */
	public static double FRIEND_BACK_RATE = 0.05d;

	public static ConstantManager getInstance()
	{
		if (instance == null)
		{
			synchronized (ConstantManager.class)
			{
				instance = new ConstantManager();
			}
		}
		return instance;
	}

	public ConstantInfo getConstantInfo()
	{
		return constantInfo;
	}

	public void setConstantInfo(ConstantInfo constantInfo)
	{
		this.constantInfo = constantInfo;
	}

	// /**
	// * 获取是否有从网络获取到的常量信息
	// *
	// * @return
	// */
	// public boolean isHaveConstantInfo()
	// {
	// if (constantInfo == null)
	// {
	// return false;
	// }
	// return true;
	// }

}
