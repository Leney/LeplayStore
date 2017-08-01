package com.xd.leplay.store.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.xd.leplay.store.model.proto.Uac.SysNotify;
import com.xd.leplay.store.model.proto.Uac.UserStatus;

/**
 * 当前登录用户的账户信息类
 * 
 * @author lilijun
 *
 */
public class LoginedUserInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 用户id */
	private int userId;
	/** 登录帐号(当前为手机号，以后可能增加邮箱名称) */
	private String account;
	/** 用户性别，0=女，1=男，-1=未知 */
	private int sex = -1;
	/** 绑定邮箱号(暂时未用) */
	private String email;
	/** 绑定手机号(暂时未用) */
	private String phone;
	/** 头像地址 */
	private String iconUrl;
	/** 上一次登录时间 */
	private String lastLoginTime;
	/** 账户注册时间 */
	private String registerTime;
	/** 账户状态:UserStatus.USER_NORMAL=正常，UserStatus.USER_DISABLED=禁用 */
	private UserStatus userStatus;
	/** 推荐者id */
	private int recommenderId;
	/** 昵称 */
	private String nickName;
	/** 公众帐号返回的支付wxOpenId */
	private String wxOpenId;
	/** 客户端与公众号关联的unionId */
	private String wxUnionId;
	/** 账户登录之后服务器产生的一个token值,相当于一个用户的登录凭证(重要) */
	private String userToken;
	/** 未读消息条数 */
	private int unreadMsgNum = 0;
	/** 用户的消息列表 */
	private List<SysNotify> userMsgList = new ArrayList<SysNotify>();

	/** 登录用户的所拥有的礼包信息(我的礼包) */
	private Hashtable<Integer, GiftInfo> giftList = new Hashtable<Integer, GiftInfo>();

	/** 登录用户的所拥有的礼包的id集合 (主要是用来在游戏礼包列表中匹配当前用户是否拥有此礼包) */
	private List<Integer> giftIdList = new ArrayList<Integer>();

	/** 登录用户的财富信息 */
	private TreasureInfo treasureInfo = new TreasureInfo();

	public int getUserId()
	{
		return userId;
	}

	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	public String getAccount()
	{
		return account;
	}

	public void setAccount(String account)
	{
		this.account = account;
	}

	public int getSex()
	{
		return sex;
	}

	public void setSex(int sex)
	{
		this.sex = sex;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getIconUrl()
	{
		return iconUrl;
	}

	public void setIconUrl(String iconUrl)
	{
		this.iconUrl = iconUrl;
	}

	public String getLastLoginTime()
	{
		return lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime)
	{
		this.lastLoginTime = lastLoginTime;
	}

	public String getRegisterTime()
	{
		return registerTime;
	}

	public void setRegisterTime(String registerTime)
	{
		this.registerTime = registerTime;
	}

	public UserStatus getUserStatus()
	{
		return userStatus;
	}

	public void setUserStatus(UserStatus userStatus)
	{
		this.userStatus = userStatus;
	}

	public int getRecommenderId()
	{
		return recommenderId;
	}

	public void setRecommenderId(int recommenderId)
	{
		this.recommenderId = recommenderId;
	}

	public String getNickName()
	{
		return nickName;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	public String getOpenId()
	{
		return wxOpenId;
	}

	public void setOpenId(String wxOpenId)
	{
		this.wxOpenId = wxOpenId;
	}

	public String getWxUnionId()
	{
		return wxUnionId;
	}

	public void setWxUnionId(String wxUnionId)
	{
		this.wxUnionId = wxUnionId;
	}

	public String getUserToken()
	{
		return userToken;
	}

	public void setUserToken(String userToken)
	{
		this.userToken = userToken;
	}

	public Hashtable<Integer, GiftInfo> getGiftList()
	{
		return giftList;
	}

	public List<Integer> getGiftIdList()
	{
		return giftIdList;
	}

	public void setGiftIdList(List<Integer> giftIdList)
	{
		this.giftIdList = giftIdList;
	}

	public TreasureInfo getTreasureInfo()
	{
		return treasureInfo;
	}

	public int getUnreadMsgNum()
	{
		return unreadMsgNum;
	}

	public void setUnreadMsgNum(int unreadMsgNum)
	{
		this.unreadMsgNum = unreadMsgNum;
	}

	public List<SysNotify> getUserMsgList()
	{
		return userMsgList;
	}

	public void setUserMsgList(List<SysNotify> userMsgList)
	{
		this.userMsgList = userMsgList;
	}

}
