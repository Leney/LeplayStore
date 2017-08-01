package com.xd.leplay.store.model;

import android.graphics.drawable.Drawable;

/**
 * 已安装的应用信息
 * 
 * @author lilijun
 *
 */
public class InstalledAppInfo
{
	/** 名称 */
	private String name;

	/** 包名 */
	private String packageName;

	/** 版本名称 */
	private String versionName;

	/** 版本号 */
	private int versionCode;

	/** 图标 */
	private Drawable icon;

	/** 安装应用大小 */
	private long appSize;

	/** 换算成有单位的应用大小 */
	private String formatAppSize;

	/** 签名 */
	private String sign;

	/** 是否是系统应用 */
	private boolean isSystemApp = false;

	/** 专门为已安装列表所设置的一个属性，标识当前此应用是否被选中 */
	private boolean isChecked = false;

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

	public Drawable getIcon()
	{
		return icon;
	}

	public void setIcon(Drawable icon)
	{
		this.icon = icon;
	}

	public long getAppSize()
	{
		return appSize;
	}

	public void setAppSize(long appSize)
	{
		this.appSize = appSize;
	}

	public String getFormatAppSize()
	{
		return formatAppSize;
	}

	public void setFormatAppSize(String formatAppSize)
	{
		this.formatAppSize = formatAppSize;
	}

	public String getSign()
	{
		return sign;
	}

	public void setSign(String sign)
	{
		this.sign = sign;
	}

	public boolean isChecked()
	{
		return isChecked;
	}

	public void setChecked(boolean isChecked)
	{
		this.isChecked = isChecked;
	}

	public boolean isSystemApp()
	{
		return isSystemApp;
	}

	public void setSystemApp(boolean isSystemApp)
	{
		this.isSystemApp = isSystemApp;
	}

}
