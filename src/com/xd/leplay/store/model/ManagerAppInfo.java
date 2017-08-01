package com.xd.leplay.store.model;

public class ManagerAppInfo
{
	private InstalledAppInfo installedAppInfo;

	private UpdateAppInfo updateAppInfo;

	private boolean isUpdateInfo;

	public InstalledAppInfo getInstalledAppInfo()
	{
		return installedAppInfo;
	}

	public void setInstalledAppInfo(InstalledAppInfo installedAppInfo)
	{
		this.installedAppInfo = installedAppInfo;
	}

	public UpdateAppInfo getUpdateAppInfo()
	{
		return updateAppInfo;
	}

	public void setUpdateAppInfo(UpdateAppInfo updateAppInfo)
	{
		this.updateAppInfo = updateAppInfo;
	}

	public boolean isUpdateInfo()
	{
		return isUpdateInfo;
	}

	public void setUpdateInfo(boolean isUpdateInfo)
	{
		this.isUpdateInfo = isUpdateInfo;
	}

}
