package com.xd.leplay.store.model;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

import com.xd.download.DownloadInfo;

/**
 * 更新应用的AppInfo对象
 * 
 * @author lilijun
 *
 */
public class UpdateAppInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 新版本服务器的软件id */
	private long softId;
	/** 新版本软件名称 */
	private String name;
	/** 新版本软件包名 */
	private String packageName;
	/** 本地旧版本的版本号 */
	private int localVersionCode;
	/** 本地旧版本的版本名称 */
	private String localVersionName;
	/** 新版本的版本号 */
	private int updateVersionCode;
	/** 新版本的版本名称 */
	private String updateVersionName;
	/** 新版本图标路径 */
	private String iconUrl;
	/** 新版本下载地址 */
	private String downloadUrl;
	/** 新版本应用大小 */
	private long updateSoftSize;
	/** 新版本更新人数的百分比 */
	private String updatePercent;
	/** 新版本特性描述 */
	private String updateDescribe;
	/** 当前更新信息是否要提示升级 */
	private boolean isPromptUpgreade = true;
	/** 发布时间 */
	private String publishDate;
	/** 服务器安装包所对应packageId */
	private long packageId;
	/** 服务器的更新包签名 */
	private String sign;
	/** 送的金币数 */
	private int coins;
	/** 图标 */
	private Drawable icon;

	public DownloadInfo toDownloadInfo()
	{
		DownloadInfo downloadInfo = new DownloadInfo(name, packageName,
				downloadUrl, iconUrl, updateSoftSize, softId + "", coins);
		downloadInfo.setPackageId(packageId);
		downloadInfo.setUpdateVersionCode(updateVersionCode);
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public int getLocalVersionCode()
	{
		return localVersionCode;
	}

	public void setLocalVersionCode(int localVersionCode)
	{
		this.localVersionCode = localVersionCode;
	}

	public String getLocalVersionName()
	{
		return localVersionName;
	}

	public void setLocalVersionName(String localVersionName)
	{
		this.localVersionName = localVersionName;
	}

	public int getUpdateVersionCode()
	{
		return updateVersionCode;
	}

	public void setUpdateVersionCode(int updateVersionCode)
	{
		this.updateVersionCode = updateVersionCode;
	}

	public String getUpdateVersionName()
	{
		return updateVersionName;
	}

	public void setUpdateVersionName(String updateVersionName)
	{
		this.updateVersionName = updateVersionName;
	}

	public String getIconUrl()
	{
		return iconUrl;
	}

	public void setIconUrl(String iconUrl)
	{
		this.iconUrl = iconUrl;
	}

	public String getDownloadUrl()
	{
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl)
	{
		this.downloadUrl = downloadUrl;
	}

	public long getUpdateSoftSize()
	{
		return updateSoftSize;
	}

	public void setUpdateSoftSize(long updateSoftSize)
	{
		this.updateSoftSize = updateSoftSize;
	}

	public String getUpdatePercent()
	{
		return updatePercent;
	}

	public void setUpdatePercent(String updatePercent)
	{
		this.updatePercent = updatePercent;
	}

	public String getUpdateDescribe()
	{
		return updateDescribe;
	}

	public void setUpdateDescribe(String updateDescribe)
	{
		this.updateDescribe = updateDescribe;
	}

	public boolean isPromptUpgreade()
	{
		return isPromptUpgreade;
	}

	public void setPromptUpgreade(boolean isPromptUpgreade)
	{
		this.isPromptUpgreade = isPromptUpgreade;
	}

	public String getPublishDate()
	{
		return publishDate;
	}

	public void setPublishDate(String publishDate)
	{
		this.publishDate = publishDate;
	}

	public long getPackageId()
	{
		return packageId;
	}

	public void setPackageId(long packageId)
	{
		this.packageId = packageId;
	}

	public String getSign()
	{
		return sign;
	}

	public void setSign(String sign)
	{
		this.sign = sign;
	}

	public int getCoins()
	{
		return coins;
	}

	public void setCoins(int coins)
	{
		this.coins = coins;
	}

	public Drawable getIcon()
	{
		return icon;
	}

	public void setIcon(Drawable icon)
	{
		this.icon = icon;
	}

}
