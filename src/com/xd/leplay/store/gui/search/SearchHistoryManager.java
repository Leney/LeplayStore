package com.xd.leplay.store.gui.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.xd.leplay.store.Constants;
import com.xd.leplay.store.util.ToolsUtil;

/**
 * 搜索历史记录管理类
 * 
 * @author lilijun
 *
 */
public class SearchHistoryManager
{
	private static SearchHistoryManager instance = null;

	private Context context;

	/** 搜索历史记录保存集合 */
	private List<String> historyDataList = new ArrayList<String>();

	private OnAddedSearchHistoryLinstener addedLinstener;

	public static SearchHistoryManager getInstance()
	{
		if (instance == null)
		{
			synchronized (SearchHistoryManager.class)
			{
				instance = new SearchHistoryManager();
			}
		}
		return instance;
	}

	public void setOnAddedSearchHistoryLinstener(
			OnAddedSearchHistoryLinstener linstener)
	{
		this.addedLinstener = linstener;
	}

	public void init(Context context)
	{
		this.context = context;
		List<String> cancheList = (List<String>) ToolsUtil
				.getCacheDataFromFile(context,
						Constants.SEARCH_HISTORY_CANCHE_FILE_NAME);
		if (cancheList != null)
		{
			historyDataList.addAll(cancheList);
		}
	}

	/**
	 * 添加一条搜索历史记录
	 * 
	 * @param history
	 */
	public void addSearchHistory(String history)
	{
		if (history.trim().equals("") || history == null)
		{
			return;
		}
		if (!historyDataList.contains(history.trim()))
		{
			historyDataList.add(0, history.trim());
			if (historyDataList.size() > 5)
			{
				// 移除最早一条数据
				historyDataList.remove(historyDataList.size() - 1);
			}
			ToolsUtil.saveCachDataToFile(context,
					Constants.SEARCH_HISTORY_CANCHE_FILE_NAME, historyDataList);
			addedLinstener.onHistoryAdded();
		}
	}

	/**
	 * 清除所有的搜索历史记录
	 */
	public void clearAllHistory()
	{
		historyDataList.clear();
		ToolsUtil.saveCachDataToFile(context,
				Constants.SEARCH_HISTORY_CANCHE_FILE_NAME, historyDataList);
	}

	public boolean isEmpty()
	{
		return historyDataList.isEmpty();
	}

	public List<String> getHistoryList()
	{
		return historyDataList;
	}
}
