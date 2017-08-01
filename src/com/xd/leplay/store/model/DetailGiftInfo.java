package com.xd.leplay.store.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 游戏礼包详情
 * 
 * @author lilijun
 *
 */
public class DetailGiftInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 游戏对象 */
	private ListAppInfo listAppInfo;

	/** 游戏对象所拥有的礼包数据 */
	private Map<Integer, GiftInfo> giftInfoMap = new HashMap<Integer, GiftInfo>();

	public ListAppInfo getListAppInfo()
	{
		return listAppInfo;
	}

	public void setListAppInfo(ListAppInfo listAppInfo)
	{
		this.listAppInfo = listAppInfo;
	}

	public Map<Integer, GiftInfo> getGiftInfoMap()
	{
		return giftInfoMap;
	}

	public void setGiftInfoMap(Map<Integer, GiftInfo> giftInfoMap)
	{
		this.giftInfoMap = giftInfoMap;
	}
	
	
}
