package com.xd.leplay.store.model;

import com.xd.download.DownloadInfo;

import java.io.Serializable;

/**
 * 应用详情
 * 
 * @author lilijun
 *
 */
public class ListAppInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 软件Id */
	private long softId;

	/** 所对应的服务器中的包名id */
	private long packageId;

	/** 软件名称 */
	private String name;

	/** 包名 */
	private String packageName;

	/** 版本名称 */
	private String versionName;

	/** 版本号 */
	private int versionCode;

	/** 软件大小 long型 */
	private long size;

	/** 格式化成字符串之后的软件大小长度 */
	private String formatSize;

	/** 图标路径 */
	private String iconUrl;

	/** 下载次数 */
	private long downloadCount;

	/** 格式化成字符串之后的软件下载次数 */
	private String formatDownloadCount;

	/** 格式化成字符串之后的赞的次数 */
	private String formatPraiseCount;

	/** 星级 */
	private float starLevel;

	/** 奖励金币数量 */
	private int coin;

	/** 应用下载地址 */
	private String downlaodUrl;

	/** 一句话描述(列表中) */
	private String recommendDescribe;

	/** 是否首发 */
	private boolean isFirst;

	/** 是否精品 */
	private boolean isBoutique;

	/** 是否热门 */
	private boolean isHot;

	/** 是否有礼包 */
	private boolean isHaveGift;

	/** 是否是应用 */
	private boolean isApp;

	/** 是否是游戏 */
	private boolean isGame;

	/** 任务id(积分墙任务Id（大于零时候才有效）) */
	private int taskId;

	/**
	 * 广告数据
	 */
	private AdInfo adInfo;

	/**
	 * 标识是否是广告数据(讯飞信息流类型广告)
	 */
	private boolean isAd = false;

	/** 标识是否填充了广告*/
	private boolean isFullAd = false;

	public DownloadInfo toDownloadInfo()
	{
		DownloadInfo downloadInfo = new DownloadInfo(name, packageName,
				downlaodUrl, iconUrl, size, softId + "", coin);
		downloadInfo.setPackageId(packageId);
		downloadInfo.setTaskId(taskId);
		return downloadInfo;
	}

	public long getSoftId()
	{
		return softId;
	}

	public void setSoftId(long softId)
	{
		this.softId = softId;
	}

	public long getPackageId()
	{
		return packageId;
	}

	public void setPackageId(long packageId)
	{
		this.packageId = packageId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}

	public String getFormatSize()
	{
		return formatSize;
	}

	public void setFormatSize(String formatSize)
	{
		this.formatSize = formatSize;
	}

	public String getIconUrl()
	{
		return iconUrl;
	}

	public void setIconUrl(String iconUrl)
	{
		this.iconUrl = iconUrl;
	}

	public long getDownloadCount()
	{
		return downloadCount;
	}

	public void setDownloadCount(long downloadCount)
	{
		this.downloadCount = downloadCount;
	}

	public String getFormatDownloadCount()
	{
		return formatDownloadCount;
	}

	public void setFormatDownloadCount(String formatDownloadCount)
	{
		this.formatDownloadCount = formatDownloadCount;
	}

	public String getFormatPraiseCount()
	{
		return formatPraiseCount;
	}

	public void setFormatPraiseCount(String formatPraiseCount)
	{
		this.formatPraiseCount = formatPraiseCount;
	}

	public float getStarLevel()
	{
		return starLevel;
	}

	public void setStarLevel(float starLevel)
	{
		this.starLevel = starLevel;
	}

	public int getCoin()
	{
		return coin;
	}

	public void setCoin(int coin)
	{
		this.coin = coin;
	}

	public String getDownlaodUrl()
	{
		return downlaodUrl;
	}

	public void setDownlaodUrl(String downlaodUrl)
	{
		this.downlaodUrl = downlaodUrl;
	}

	public String getRecommendDescribe()
	{
		return recommendDescribe;
	}

	public void setRecommendDescribe(String recommendDescribe)
	{
		this.recommendDescribe = recommendDescribe;
	}

	public boolean isFirst()
	{
		return isFirst;
	}

	public void setFirst(boolean isFirst)
	{
		this.isFirst = isFirst;
	}

	public boolean isBoutique()
	{
		return isBoutique;
	}

	public void setBoutique(boolean isBoutique)
	{
		this.isBoutique = isBoutique;
	}

	public boolean isHot()
	{
		return isHot;
	}

	public void setHot(boolean isHot)
	{
		this.isHot = isHot;
	}

	public boolean isHaveGift()
	{
		return isHaveGift;
	}

	public void setHaveGift(boolean isHaveGift)
	{
		this.isHaveGift = isHaveGift;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public String getVersionName()
	{
		return versionName;
	}

	public void setVersionName(String versionName)
	{
		this.versionName = versionName;
	}

	public int getVersionCode()
	{
		return versionCode;
	}

	public void setVersionCode(int versionCode)
	{
		this.versionCode = versionCode;
	}

	public boolean isApp()
	{
		return isApp;
	}

	public void setApp(boolean isApp)
	{
		this.isApp = isApp;
	}

	public boolean isGame()
	{
		return isGame;
	}

	public void setGame(boolean isGame)
	{
		this.isGame = isGame;
	}

	public int getTaskId()
	{
		return taskId;
	}

	public void setTaskId(int taskId)
	{
		this.taskId = taskId;
	}

	public AdInfo getAdInfo() {
		return adInfo;
	}

	public void setAdInfo(AdInfo adInfo) {
		this.adInfo = adInfo;
	}

	public boolean isAd() {
		return isAd;
	}

	public void setAd(boolean ad) {
		isAd = ad;
	}

	public boolean isFullAd() {
		return isFullAd;
	}

	public void setFullAd(boolean fullAd) {
		isFullAd = fullAd;
	}
}
