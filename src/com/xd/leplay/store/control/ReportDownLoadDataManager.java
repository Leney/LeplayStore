package com.xd.leplay.store.control;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.xd.leplay.store.Constants;
import com.xd.leplay.store.model.proto.App.PackInfo;
import com.xd.leplay.store.util.ToolsUtil;

/**
 * 上报下载并安装成功的管理类
 * 
 * @author lilijun
 *
 */
public class ReportDownLoadDataManager
{
	private static ReportDownLoadDataManager instance = null;

	private List<PackInfo> packInfos = null;

	private Context mContext = null;

	public static ReportDownLoadDataManager getInstance()
	{
		if (instance == null)
		{
			synchronized (ReportDownLoadDataManager.class)
			{
				instance = new ReportDownLoadDataManager();
			}
		}
		return instance;
	}

	public void init(Context context)
	{
		packInfos = new ArrayList<PackInfo>();
		mContext = context;
		@SuppressWarnings("unchecked")
		List<PackInfo> datas = (List<PackInfo>) ToolsUtil.getCacheDataFromFile(
				context,
				Constants.DOWNLOAD_INSTALL_SUCCESS_APP_INFO_CANCHE_FILE_NAME);
		if (datas != null)
		{
			packInfos.addAll(datas);
		}
	}

	/**
	 * 加入一条数据到集合中去
	 * 
	 * @param packInfo
	 */
	public void addPackInfo(PackInfo packInfo)
	{
		packInfos.add(packInfo);
	}

	/**
	 * 清空所有未上报的数据
	 */
	public void clearAllData()
	{
		packInfos.clear();
		ToolsUtil.clearCacheDataToFile(mContext,
				Constants.DOWNLOAD_INSTALL_SUCCESS_APP_INFO_CANCHE_FILE_NAME);
	}

	/**
	 * 判断集合中是否有数据 true=没有数据 ，false=有数据
	 */
	public boolean isEmptyData()
	{
		return packInfos.isEmpty();
	}

	public List<PackInfo> getPackInfos()
	{
		return packInfos;
	}
}
