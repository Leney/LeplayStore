package com.xd.leplay.store.control;

import java.util.Hashtable;
import java.util.Map;

import android.content.Context;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.model.EtagInfo;
import com.xd.leplay.store.util.ToolsUtil;

/**
 * 网络请求Etag数据管理类
 * 
 * @author lilijun
 *
 */
public class EtagManager
{

	private Context mContext = null;

	private static EtagManager instance = null;

	/**
	 * 存储网络请求的返回数据
	 * ，key=请求服务器时的请求TAG(当多条一起请求时，则是多条的请求tag值的组合值)，value=所对应EtagInfo对象
	 */
	private Map<String, EtagInfo> etagMap = null;

	private LeplayPreferences preferences = null;

	public static EtagManager getInstance()
	{
		if (instance == null)
		{
			synchronized (EtagManager.class)
			{
				instance = new EtagManager();
			}
		}
		return instance;
	}

	public void init(Context context)
	{
		this.mContext = context;
		etagMap = new Hashtable<String, EtagInfo>();

		preferences = LeplayPreferences.getInstance(context);
		long passTime = 4 * 60 * 60 * 1000;
		long curTime = System.currentTimeMillis();
		if (Math.abs(curTime - preferences.getLastInDateTime()) >= passTime)
		{
			// 距离上一次进入商店的时间已经过去4个小时
			// 清除之前缓存的etagInfo对象
			DLog.i("lilijun", "距离上一次进入商店的时间已经过去4个小时");
			ToolsUtil.clearCacheDataToFile(context,
					Constants.ETAG_CANCLE_FILE_NAME);
		} else
		{
			// 距离上一次进入商店的时间 还没有超过4小时
			DLog.i("lilijun", "距离上一次进入商店的时间还没超过4个小时");
			Map<String, EtagInfo> tempEtags = (Map<String, EtagInfo>) ToolsUtil
					.getCacheDataFromFile(context,
							Constants.ETAG_CANCLE_FILE_NAME);
			if (tempEtags != null && !tempEtags.isEmpty())
			{
				etagMap.putAll(tempEtags);
			}
			DLog.i("lilijun", "etagMap.size()--------->>>" + etagMap.size());
		}
		preferences.setLastInDateTime(curTime);
	}

	/**
	 * 添加一条etagInfo到map集合中去
	 * 
	 * @param key
	 * @param value
	 */
	public void addEtagInfo(String key, EtagInfo value)
	{
		etagMap.put(key, value);
		// 保存信息到缓存
		ToolsUtil.saveCachDataToFile(mContext, Constants.ETAG_CANCLE_FILE_NAME,
				etagMap);
	}

	public Map<String, EtagInfo> getEtagMap()
	{
		return etagMap;
	}

}
