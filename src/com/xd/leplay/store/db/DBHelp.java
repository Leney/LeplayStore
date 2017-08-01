package com.xd.leplay.store.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.xd.base.util.DLog;

public class DBHelp extends SQLiteOpenHelper
{

	private static final String TAG = "DBHelp";

	// 数据库名称
	private static final String DATABASE_NAME = "SoftwaresDB";

	// 数据库版本
	private static final int DATABASE_VERSION = 2;

	/** 创建软件管理表的sql语句 */
	private static final String CREATE_SOFTWARES_TABLE = "create table if not exists "
			+ Columns.TABLE_NAME
			+ " ("
			+ Columns._ID
			+ " integer primary key autoincrement, "
			+ Columns.NAME
			+ " text, "
			+ Columns.PACKAGE_NAME
			+ " text, "
			+ Columns.SOFT_ID
			+ " text, "
			+ Columns.LOCAL_VERSION_CODE
			+ " integer, "
			+ Columns.PACK_ID
			+ " long, "
			+ Columns.LOCAL_VERSION_NAME
			+ " text,"
			+ Columns.UPDATE_VERSION_NAME
			+ " text,"
			+ Columns.ICON_URL
			+ " text,"
			+ Columns.DOWNLOAD_URL
			+ " text,"
			+ Columns.UPDATE_PERCENT
			+ " text,"
			+ Columns.UPDATE_DESCRIBE
			+ " text,"
			+ Columns.PUBLISH_DATA
			+ " text,"
			+ Columns.UPDATE_SOFT_SIZE + " integer,"
			// + Columns.UPDATE_APK_SIZE
			// + " integer,"
			// + Columns.CUR_STATE
			// + " integer,"
			// + Columns.SAME_SIGN
			// + " bit,"
			// + Columns.IS_INLAY
			// + " bit,"
			// + Columns.IS_SHOW_INSTALLED_LIST
			// + " bit,"
			// + Columns.SIGN
			// + " text,"
			// + Columns.REL_APK_URL
			// + " text,"
			// + Columns.DIFFERENCE_UPGRADE
			// + " bit,"
			+ Columns.PROMPT_UPGREADE + " bit);";

	/** 删除软件管理表的sql语句 */
	private static final String DELETE_SOFTWARE_TABLE = "drop table "
			+ Columns.TABLE_NAME;

	// /** 创建更新软件数量的表 */
	// private static final String CREATE_UPDATE_COUNT_TABLE =
	// "create table if not exists "
	// + "miss_infos "
	// + "("
	// + Columns._ID
	// + " integer primary key autoincrement," + " miss integer);";

	/** 删除更新软件数量的表sql语句 */
	private static final String DELETE_UPDATE_COUNT_TABLE = "drop table miss_infos;";

	public DBHelp(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_SOFTWARES_TABLE);
		// db.execSQL(CREATE_UPDATE_COUNT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(DELETE_SOFTWARE_TABLE);
		db.execSQL(CREATE_SOFTWARES_TABLE);

		try
		{
			db.execSQL(DELETE_UPDATE_COUNT_TABLE);
		} catch (Exception e)
		{
			DLog.i("lilijun", "要删除的表不存在！！");
			DLog.e(TAG, "onUpgrade()#exception", e);
		}
		// DLog.i("lilijun", "创建了更新数量表！！");
		// db.execSQL(CREATE_UPDATE_COUNT_TABLE);

		DLog.i(TAG, "oldVersion=" + oldVersion + ", newVersion=" + newVersion);
	}

	public static class Columns implements BaseColumns
	{
		/** 表名称 */
		public static final String TABLE_NAME = "update_infos_tab";

		/** 软件名称 */
		public static final String NAME = "name";

		/** 软件包名 */
		public static final String PACKAGE_NAME = "package_name";

		/** 可更新应用在服务器的SoftId */
		public static final String SOFT_ID = "soft_id";

		/** 当前本地安装的版本号 */
		public static final String LOCAL_VERSION_CODE = "local_version_code";

		/** 当前本地安装的版本名称 */
		public static final String LOCAL_VERSION_NAME = "local_version_name";

		/** 服务器可更新的版本名称 */
		public static final String UPDATE_VERSION_NAME = "update_version_name";

		/** 有更新的软件图标地址 */
		public static final String ICON_URL = "icon_url";

		/** 有更新的软件下载地址 */
		public static final String DOWNLOAD_URL = "download_url";

		/** 服务器上更新软件的大小 */
		public static final String UPDATE_SOFT_SIZE = "update_soft_size";

		// /** 软件状态 */
		// public static final String CUR_STATE = "cur_state";

		/** 是否提示升级(忽略) */
		public static final String PROMPT_UPGREADE = "prompt_upgreade";

		// /** 本地应用签名是否与服务器应用签名一致 */
		// public static final String SAME_SIGN = "same_sign";

		// /** 签名 */
		// public static final String SIGN = "sign";

		// /** 是否是内置应用 */
		// public static final String IS_INLAY = "is_inlay";

		// /** 是否在已安装列表显示 */
		// public static final String IS_SHOW_INSTALLED_LIST =
		// "is_show_installed_list";

		// /** 服务器上可更新的软件apk文件大小 */
		// public static final String UPDATE_APK_SIZE = "update_apk_size";

		// /** 是否可差分升级 */
		// public static final String DIFFERENCE_UPGRADE = "difference_upgrade";

		// /** 服务器上可更新软件的apk的下载地址(与是否有差分包无关) */
		// public static final String REL_APK_URL = "rel_apk_url";

		/** 更新此应用的人数百分比 */
		public static final String UPDATE_PERCENT = "update_percent";

		/** 新版特性 */
		public static final String UPDATE_DESCRIBE = "update_describe";

		/** 发布时间 */
		public static final String PUBLISH_DATA = "publish_data";

		/** 更新应用在服务器的安装包Id */
		public static final String PACK_ID = "pack_id";
	}

}
