package com.xd.leplay.store.model;

import java.io.Serializable;

/**
 * 单个的礼包信息对象(将App.GameBag和Uac.GameBag整合起来的一个礼包对象)
 * 
 * @author lilijun
 *
 */
public class GiftInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 礼包ID */
	private int id;
	/** 礼包名称 */
	private String name;
	/** 礼包内容 */
	private String content;
	/** 领取资格 */
	private String eligibility;
	/** 领取方式 */
	private String howPickup;
	/** 开始时间 */
	private String startTime;
	/** 结束时间 */
	private String endTime;
	/** 使用方法 */
	private String usage;
	/** 兑换码 */
	private String code;

	/** 礼包所对应的游戏的软件id */
	private long gameId;
	/** 礼包所对应的游戏的名称 */
	private String gameName;
	/** 礼包所对应的游戏图标路径 */
	private String gameIconUrl;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getEligibility()
	{
		return eligibility;
	}

	public void setEligibility(String eligibility)
	{
		this.eligibility = eligibility;
	}

	public String getHowPickup()
	{
		return howPickup;
	}

	public void setHowPickup(String howPickup)
	{
		this.howPickup = howPickup;
	}

	public String getStartTime()
	{
		return startTime;
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}

	public String getEndTime()
	{
		return endTime;
	}

	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}

	public String getUsage()
	{
		return usage;
	}

	public void setUsage(String usage)
	{
		this.usage = usage;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public long getGameId()
	{
		return gameId;
	}

	public void setGameId(long gameId)
	{
		this.gameId = gameId;
	}

	public String getGameName()
	{
		return gameName;
	}

	public void setGameName(String gameName)
	{
		this.gameName = gameName;
	}

	public String getGameIconUrl()
	{
		return gameIconUrl;
	}

	public void setGameIconUrl(String gameIconUrl)
	{
		this.gameIconUrl = gameIconUrl;
	}
	
	

}
