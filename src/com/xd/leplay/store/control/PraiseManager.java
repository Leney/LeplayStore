package com.xd.leplay.store.control;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;

import com.xd.leplay.store.Constants;
import com.xd.leplay.store.util.ToolsUtil;

/**
 * 赞的管理类
 * 
 * @author lilijun
 *
 */
public class PraiseManager
{
	private static PraiseManager instance = null;

	private Set<Long> praiseList = new HashSet<Long>();

	private Context mContext;

	public static PraiseManager getInstance()
	{
		if (instance == null)
		{
			synchronized (PraiseManager.class)
			{
				instance = new PraiseManager();
			}
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public void init(Context context)
	{
		this.mContext = context;

		Set<Long> praiseSet = (Set<Long>) ToolsUtil.getCacheDataFromFile(
				context, Constants.PRAISE_INFO_CANCHE_FILE_NAME);
		if (praiseSet != null)
		{
			praiseList.addAll(praiseSet);
		}
	}

	/**
	 * 判断是否之前赞过此应用
	 * 
	 * @param softId
	 * @return
	 */
	public boolean isPraised(long softId)
	{
		return praiseList.contains(softId);
	}

	/**
	 * 添加赞
	 * 
	 * @param softId
	 */
	public void addPraiseApp(long softId)
	{
		praiseList.add(softId);
		ToolsUtil.saveCachDataToFile(mContext,
				Constants.PRAISE_INFO_CANCHE_FILE_NAME, praiseList);
	}

	/**
	 * 取消赞
	 * 
	 * @param softId
	 */
	public void removePraiseApp(long softId)
	{
		praiseList.remove(softId);
		ToolsUtil.saveCachDataToFile(mContext,
				Constants.PRAISE_INFO_CANCHE_FILE_NAME, praiseList);
	}

	/**
	 * 清除所有赞的数据
	 */
	public void clearAllPraise()
	{
		praiseList.clear();
		ToolsUtil.saveCachDataToFile(mContext,
				Constants.PRAISE_INFO_CANCHE_FILE_NAME, praiseList);
	}
}
