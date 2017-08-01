package com.xd.leplay.store.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.xd.download.DownloadInfo;

/**
 * 应用详情
 * 
 * @author lilijun
 *
 */
public class DetailAppInfo implements Serializable
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

	/** 赞次数 */
	private int praiseCount;

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

	/** 详情描述(详情中) */
	private String describe;

	/** 开发者 */
	private String developer;

	/** 更新描述说明 */
	private String updateDescribe;

	/** 版本发布时间 */
	private String publishDate;

	/** 签名 */
	private String sign;

	/** 是否官方 */
	private boolean isOfficial;

	/** 是否安全 */
	private boolean isSafety;

	/** 是否有广告 */
	private boolean isNoAd;

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

	/** 截屏图片地址集合 */
	private List<String> screenUrls = new ArrayList<String>();

	/** 当应用为游戏时，游戏所拥有的礼包信息列表 */
	private List<DetailGiftInfo> detailGiftInfos = new ArrayList<DetailGiftInfo>();

	public DownloadInfo toDownloadInfo()
	{
		DownloadInfo downloadInfo = new DownloadInfo(name, packageName,
				downlaodUrl, iconUrl, size, softId + "", coin);
		downloadInfo.setPackageId(packageId);
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

	public int getPraiseCount()
	{
		return praiseCount;
	}

	public void setPraiseCount(int praiseCount)
	{
		this.praiseCount = praiseCount;
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

	public String getDescribe()
	{
		return describe;
	}

	public void setDescribe(String describe)
	{
		this.describe = describe;
	}

	public boolean isOfficial()
	{
		return isOfficial;
	}

	public void setOfficial(boolean isOfficial)
	{
		this.isOfficial = isOfficial;
	}

	public boolean isSafety()
	{
		return isSafety;
	}

	public void setSafety(boolean isSafety)
	{
		this.isSafety = isSafety;
	}

	public boolean isNoAd()
	{
		return isNoAd;
	}

	public void setNoAd(boolean isNoAd)
	{
		this.isNoAd = isNoAd;
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

	public List<String> getScreenUrls()
	{
		return screenUrls;
	}

	public void setScreenUrls(List<String> screenUrls)
	{
		this.screenUrls = screenUrls;
	}

	public List<DetailGiftInfo> getDetailGiftInfos()
	{
		return detailGiftInfos;
	}

	public void setGiftInfos(List<DetailGiftInfo> detailGiftInfos)
	{
		this.detailGiftInfos = detailGiftInfos;
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

	public String getSign()
	{
		return sign;
	}

	public void setSign(String sign)
	{
		this.sign = sign;
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

	public String getDeveloper()
	{
		return developer;
	}

	public void setDeveloper(String developer)
	{
		this.developer = developer;
	}

	public String getUpdateDescribe()
	{
		return updateDescribe;
	}

	public void setUpdateDescribe(String updateDescribe)
	{
		this.updateDescribe = updateDescribe;
	}

	public String getPublishDate()
	{
		return publishDate;
	}

	public void setPublishDate(String publishDate)
	{
		this.publishDate = publishDate;
	}
}
