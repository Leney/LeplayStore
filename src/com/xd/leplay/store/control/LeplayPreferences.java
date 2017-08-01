package com.xd.leplay.store.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 小数据配置类
 * 
 * @author lilijun
 */
public class LeplayPreferences
{
	private static LeplayPreferences instance;

	private static Context mContext;

	/** 是否是无图省流量模式 */
	private final String IS_NO_PIC_MODEL = "is_no_pic_model";

	/** 下载安装完成之后 是否删除安装包 */
	private final String IS_DELETE_APK = "is_delete_apk";

	/** wifi下 是否自动更新 */
	private final String IS_AUTO_UPDATE = "is_auto_update";

	/** 是否接收推荐内容提醒 */
	private final String IS_RECEIVER_PUSH_MSG = "is_receiver_push_msg";

	/** 是否是第一次进入爱玩商店 */
	private final String IS_FIRST_IN = "is_first_in";

	/** 在未登录的情况下,首次点击下载按钮进行下载 */
	private final String IS_FIRST_DOWNLOAD_UNLOGING = "is_first_download_unlogin";

	/** 是否显示了升级爱玩商店的dialog */
	private final String IS_SHOW_UPGRADE_DIALOG = "is_show_upgrade_dialog";

	/** 上一次进入商店时的版本号 */
	private final String LAST_VERSION_CODE = "last_version_code";

	/** 上一次进入商店时的时间戳 */
	private final String LAST_IN_DATE_TIME = "last_in_date_time";

	/** 在商店旧版本的时候，服务器上可更新的商店高版本的软件id */
	private final String UPDATE_SOFT_ID = "update_soft_id";

	/** 在商店旧版本的时候，服务器上可更新的商店高版本的pack id */
	private final String UPDATE_PACK_ID = "update_pack_id";

	/** 在商店旧版本的时候，服务器上可更新的商店高版本的版本号 */
	private final String UPDATE_VERSION_CODE = "update_version_code";

	/** 是否需要上报更新并安装了新版商店的数据 */
	private final String IS_NEED_REPORD_UPDATE_DATA = "is_need_repord_update_data";

	/** 公告消息 */
	private final String SYSTEM_MSG = "SYSTEM_MSG";

	/** 上一次获取到的闪屏地址 */
	private final String SPLASH_IMG_URL = "SPLASH_IMG_URL";

	// /** 是否需要强制升级 */
	// private final String IS_MUST_FORCE_UPGREAD = "IS_MUST_FORCE_UPGREAD";
	//
	// /** 需要强制升级的目标版本号 */
	// private final String FORCE_UPGREAD_VERSION_CODE =
	// "FORCE_UPGREAD_VERSION_CODE";

	/** aes_key */
	private final String DES_KEY = "DES_KEY";

	public static LeplayPreferences getInstance(Context context)
	{
		mContext = context;
		if (instance == null)
		{
			synchronized (LeplayPreferences.class)
			{
				if (instance == null)
				{
					instance = new LeplayPreferences();
				}
			}
		}
		return instance;
	}

	/**
	 * 清除SharePrefrences里的数据
	 */
	public void clearSharePrefrencesData()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		preferences.edit().clear().commit();
	}

	/**
	 * 设置退出商店时版本号
	 * 
	 * @param lastVersionCode
	 */
	public void setLastVersionCode(int lastVersionCode)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putInt(LAST_VERSION_CODE, lastVersionCode);
		editor.commit();
	}

	/**
	 * 获取上一次退出商店时的版本号
	 * 
	 * @return
	 */
	public int getLastVersionCode()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getInt(LAST_VERSION_CODE, 0);
	}

	/**
	 * 设置当商店有更新时,保存的可更新版本的softId
	 * 
	 * @param softId
	 */
	public void setUpdateSoftId(long softId)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putLong(UPDATE_SOFT_ID, softId);
		editor.commit();
	}

	/**
	 * 获取当商店有更新时,保存的可更新版本的softId
	 * 
	 * @return
	 */
	public long getUpdateSoftId()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getLong(UPDATE_SOFT_ID, -1);
	}

	/**
	 * 设置当商店有更新时,保存的可更新版本的packId
	 * 
	 * @param packId
	 */
	public void setUpdatePackId(long packId)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putLong(UPDATE_PACK_ID, packId);
		editor.commit();
	}

	/**
	 * 获取当商店有更新时,保存的可更新版本的packId
	 * 
	 * @return
	 */
	public long getUpdatePackId()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getLong(UPDATE_PACK_ID, -1);
	}

	/**
	 * 设置当商店有更新时,保存的可更新版本的版本号
	 * 
	 * @param softId
	 */
	public void setUpdateVersionCode(int versionCode)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putInt(UPDATE_VERSION_CODE, versionCode);
		editor.commit();
	}

	/**
	 * 获取当商店有更新时,保存的可更新版本的版本号
	 * 
	 * @return
	 */
	public int getUpdateVersionCode()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getInt(UPDATE_VERSION_CODE, -1);
	}

	/**
	 * 设置是否需要上报更新并安装了新版商店的数据
	 * 
	 * @param isNeed
	 */
	public void setNeedRepordUpdateData(boolean isNeed)
	{

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putBoolean(IS_NEED_REPORD_UPDATE_DATA, isNeed);
		editor.commit();
	}

	/**
	 * 获取是否需要上报更新并安装了新版的商店数据
	 * 
	 * @return
	 */
	public boolean isNeedRepordUpdateData()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getBoolean(IS_NEED_REPORD_UPDATE_DATA, false);
	}

	/**
	 * 设置当前进入商店的时间戳
	 * 
	 * @param time
	 */
	public void setLastInDateTime(long time)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putLong(LAST_IN_DATE_TIME, time);
		editor.commit();
	}

	/**
	 * 获取上一次进入商店的时间戳
	 * 
	 * @return
	 */
	public long getLastInDateTime()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getLong(LAST_IN_DATE_TIME, 0);
	}

	/**
	 * 设置是否第一次进入爱玩商店
	 * 
	 * @param isShow
	 */
	public void setFirstIn(boolean first)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putBoolean(IS_FIRST_IN, first);
		editor.commit();
	}

	/**
	 * 获取是否是第一次进入爱玩商店
	 * 
	 * @return
	 */
	public boolean isFirstIn()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getBoolean(IS_FIRST_IN, true);
	}


	/**
	 * 设置未登陆时，是否是第一次点击下载按钮
	 * 
	 * @param isShow
	 */
	public void setFirstDownloadWithUnlogin(boolean first)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putBoolean(IS_FIRST_DOWNLOAD_UNLOGING, first);
		editor.commit();
	}

	/**
	 * 获取是否是在未登录的情况下第一次点击下载
	 * 
	 * @return
	 */
	public boolean isFirstDownloadWithUnlogin()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getBoolean(IS_FIRST_DOWNLOAD_UNLOGING, true);
	}

	/**
	 * 设置是否显示了升级爱玩商店的Dialog
	 * 
	 * @param isShow
	 */
	public void setShowUpgradeDialog(boolean show)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putBoolean(IS_SHOW_UPGRADE_DIALOG, show);
		editor.commit();
	}

	/**
	 * 获取是否显示了升级爱玩商店的Dialog
	 * 
	 * @return
	 */
	public boolean isShowUpgradeDialog()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getBoolean(IS_SHOW_UPGRADE_DIALOG, false);
	}

	/**
	 * 设置是否无图省流量模式
	 * 
	 * @param isShow
	 */
	public void setNoPicModel(boolean isShow)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putBoolean(IS_NO_PIC_MODEL, isShow);
		editor.commit();
	}

	/**
	 * 获取是否是无图省流量模式
	 * 
	 * @return
	 */
	public boolean isNoPicModel()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getBoolean(IS_NO_PIC_MODEL, false);
	}

	/**
	 * 设置下载安装完成之后 是否删除本地安装包
	 * 
	 * @param isDelete
	 */
	public void setDeleteApk(boolean isDelete)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putBoolean(IS_DELETE_APK, isDelete);
		editor.commit();
	}

	/**
	 * 获取 在下载安装完成之后 是否删除本地安装包
	 * 
	 * @return
	 */
	public boolean isDeleteApk()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getBoolean(IS_DELETE_APK, true);
	}

	/**
	 * Wifi下 是否自动更新
	 * 
	 * @param isUpdate
	 */
	public void setAutoUpdate(boolean isUpdate)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putBoolean(IS_AUTO_UPDATE, isUpdate);
		editor.commit();
	}

	/**
	 * 获取 Wifi下 是否自动更新
	 * 
	 * @return
	 */
	public boolean isAutoUpdate()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getBoolean(IS_AUTO_UPDATE, true);
	}

	/**
	 * 是否接收推荐内容提醒(是否接收push内容)
	 * 
	 * @param isReceiver
	 */
	public void setReceiverPushMsg(boolean isReceiver)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putBoolean(IS_RECEIVER_PUSH_MSG, isReceiver);
		editor.commit();
	}

	/**
	 * 获取是否接收内容提醒(是否接收push内容)
	 * 
	 * @return
	 */
	public boolean isReceiverPushMsg()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getBoolean(IS_RECEIVER_PUSH_MSG, true);
	}

	/**
	 * 设置公告消息
	 * 
	 */
	public void setSystemMsg(String msg)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putString(SYSTEM_MSG, msg);
		editor.commit();
	}

	/**
	 * 获取公告消息
	 * 
	 * @return
	 */
	public String getSystemMsg()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getString(SYSTEM_MSG, "");
	}

	/**
	 * 设置闪屏图片地址
	 * 
	 * @param url
	 */
	public void setSplashImgUrl(String url)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putString(SPLASH_IMG_URL, url);
		editor.commit();
	}

	/**
	 * 获取闪屏图片地址
	 * 
	 * @return
	 */
	public String getSplashImgUrl()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getString(SPLASH_IMG_URL, "");
	}


	/**
	 * 设置浏览器UserAgent
	 *
	 * @param ua
	 */
	public void setUserAgent(String ua)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putString("user_agent", ua);
		editor.commit();
	}

	/**
	 * 获取浏览器UserAgent
	 *
	 * @return
	 */
	public String getUserAgent()
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return preferences.getString("user_agent", "");
	}
}
