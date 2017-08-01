package com.xd.download;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.xd.base.util.DLog;

/**
 * 下载数据库
 *
 * @author lilijun
 *
 */
public class DownloadDB
{
	private static final String TAG = "PushDownloadDB";

	private DBHelp dbHelp;

	public DownloadDB(Context context, String databaseName, int databaseVersion)
	{
		dbHelp = new DBHelp(context, databaseName, databaseVersion);
	}

	/**
	 * 查询最近的待下载项
	 *
	 * @param msgType
	 *            消息类型，为0时，表示不限制类型
	 * @return 最近的待下载项
	 */
	public synchronized DownloadInfo queryByPackageName(String packageName)
	{
		DownloadInfo info = null;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String selection = null;
		if (packageName != null && !packageName.equals(""))
		{
			selection = Columns.APP_PACKAGE_NAME + " = ' " + packageName
					+ " ' ";
		}
		try
		{
			db = dbHelp.getReadableDatabase();
			cursor = db.query(Columns.TABLE_NAME, null, selection, null, null,
					null, Columns.ORDER_DEFAULT);
			if (cursor.moveToFirst())
			{
				info = getDownloadInfo(cursor);
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "queryByPackageName# Exception=", e);
			// e.printStackTrace();
		} finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
			if (db != null)
			{
				db.close();
			}
		}
		return info;
	}

	public synchronized List<DownloadInfo> queryAll()
	{
		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try
		{
			db = dbHelp.getReadableDatabase();
			cursor = db.query(Columns.TABLE_NAME, null, null, null, null, null,
					null);
			while (cursor.moveToNext())
			{
				list.add(getDownloadInfo(cursor));
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "queryAll# Exception=", e);
			// e.printStackTrace();
		} finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
			if (db != null)
			{
				db.close();
			}
		}
		return list;
	}

	private DownloadInfo getDownloadInfo(Cursor cursor)
	{
		DownloadInfo info = new DownloadInfo("", "", "", "", -1, "", 0);
		info.setId(cursor.getInt(cursor.getColumnIndex(Columns._ID)));
		info.setName(cursor.getString(cursor.getColumnIndex(Columns.APP_NAME)));
		info.setPackageName(cursor.getString(cursor
				.getColumnIndex(Columns.APP_PACKAGE_NAME)));
		info.setUrl(cursor.getString(cursor.getColumnIndex(Columns.APP_URL)));
		info.setIconUrl(cursor.getString(cursor
				.getColumnIndex(Columns.APP_ICON_URL)));
		info.setSize(cursor.getLong(cursor.getColumnIndex(Columns.APP_SIZE)));
		info.setContentLength(cursor.getLong(cursor
				.getColumnIndex(Columns.CONTENT_LENGTH)));
		info.setDownloadSize(cursor.getLong(cursor
				.getColumnIndex(Columns.ALREADY_DOWN)));
		info.setPath(cursor.getString(cursor.getColumnIndex(Columns.APK_PATH)));
		// info.setPercent(cursor.getInt(cursor.getColumnIndex(Columns.APK_DOWN_PERCENT)));
		info.setState(cursor.getInt(cursor
				.getColumnIndex(Columns.APK_DOWN_STATE)));
		info.setError(cursor.getInt(cursor
				.getColumnIndex(Columns.APK_DOWN_ERROR)));
		info.setCanceled(cursor.getInt(cursor
				.getColumnIndex(Columns.APK_DOWN_CANCEL)) == 1 ? true : false);
		info.setDeleted(cursor.getInt(cursor
				.getColumnIndex(Columns.APK_DOWN_DELETE)) == 1 ? true : false);
		info.hashCode();// setId(cursor.getInt(cursor.getColumnIndex(Columns.HASH_CODE)));
		info.setShowNotifiction(cursor.getInt(cursor
				.getColumnIndex(Columns.APK_DOWN_NOTIFY)) == 1 ? true : false);
		info.setSoftId(cursor.getString(cursor
				.getColumnIndex(Columns.APK_SOFT_ID)));
		info.setPackageId(cursor.getLong(cursor
				.getColumnIndex(Columns.PACKAGE_ID)));
		info.setUpdateVersionName(cursor.getString(cursor
				.getColumnIndex(Columns.VERSION_NAME)));
		info.setUpdateVersionCode(cursor.getInt(cursor
				.getColumnIndex(Columns.VERSION_CODE)));
		info.setTaskId(cursor.getInt(cursor.getColumnIndex(Columns.TASK_ID)));
		info.setAction(cursor.getString(cursor.getColumnIndex(Columns.ACTION)));
		info.setIntegral(cursor.getInt(cursor
				.getColumnIndex(Columns.APK_INTEGRAL)));
		info.setRelApkUrl(cursor.getString(cursor
				.getColumnIndex(Columns.REL_APK_URL)));
		info.setRelApkSize(cursor.getLong(cursor
				.getColumnIndex(Columns.REL_APK_SIZE)));
		return info;
	}

	// public long insert(DownloadInfo info)
	// {
	// SQLiteDatabase db = dbHelp.getWritableDatabase();
	// long id = db.insert(Columns.TABLE_NAME, null, getContentValues(info));
	// info.setId(id);
	// db.close();
	// return id;
	// }

	public synchronized int delete(DownloadInfo info)
	{
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		int count = db.delete(Columns.TABLE_NAME, Columns.APP_PACKAGE_NAME
				+ " = '" + info.getPackageName() + "'", null);
		db.close();
		return count;
	}

	/**
	 * 插入或更新均用词方法
	 * 
	 * @param info
	 * @return
	 */
	public synchronized int updateOrInsert(DownloadInfo info)
	{
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		int count = db
				.update(Columns.TABLE_NAME,
						getContentValues(info),
						Columns.APP_PACKAGE_NAME + " = '"
								+ info.getPackageName() + "'", null);

		if (count < 1)
		{
			long id = db.insert(Columns.TABLE_NAME, null,
					getContentValues(info));
			info.setId(id);
			count++;
		}
		db.close();
		return count;
	}

	private ContentValues getContentValues(DownloadInfo info)
	{
		ContentValues values = new ContentValues();
		// values.put(Columns._ID, info.getId());
		values.put(Columns.APP_NAME, info.getName());
		values.put(Columns.APP_PACKAGE_NAME, info.getPackageName());
		values.put(Columns.APP_URL, info.getUrl());
		values.put(Columns.APP_ICON_URL, info.getIconUrl());
		values.put(Columns.APP_SIZE, info.getSize());
		values.put(Columns.CONTENT_LENGTH, info.getContentLength());
		values.put(Columns.ALREADY_DOWN, info.getDownloadSize());
		values.put(Columns.APK_PATH, info.getPath());
		values.put(Columns.APK_DOWN_PERCENT, info.getPercent());
		values.put(Columns.APK_DOWN_STATE, info.getState());
		values.put(Columns.APK_DOWN_ERROR, info.getError());
		values.put(Columns.APK_DOWN_CANCEL, info.isCanceled() ? 1 : 0);
		values.put(Columns.APK_DOWN_DELETE, info.isDeleted() ? 1 : 0);
		values.put(Columns.HASH_CODE, info.hashCode());
		values.put(Columns.APK_DOWN_NOTIFY, info.isShowNotifiction() ? 1 : 0);
		values.put(Columns.APK_SOFT_ID, info.getSoftId());
		values.put(Columns.PACKAGE_ID, info.getPackageId());
		values.put(Columns.VERSION_NAME, info.getUpdateVersionName());
		values.put(Columns.VERSION_CODE, info.getUpdateVersionCode());
		values.put(Columns.TASK_ID, info.getTaskId());
		values.put(Columns.ACTION, info.getAction());
		values.put(Columns.APK_INTEGRAL, info.getIntegral());
		values.put(Columns.REL_APK_URL, info.getRelApkUrl());
		values.put(Columns.REL_APK_SIZE, info.getRelApkSize());
		return values;
	}

	public void close()
	{
		dbHelp.close();
	}

	private static class DBHelp extends SQLiteOpenHelper
	{
		private static final String CREATE_DOWNLOAD_TABLE = "create table if not exists "
				+ Columns.TABLE_NAME
				+ " ("
				+ Columns._ID
				+ " integer primary key autoincrement, "
				+ Columns.APP_NAME
				+ " text, "
				+ Columns.APP_PACKAGE_NAME
				+ " text, "
				+ Columns.APP_URL
				+ "  text, "
				+ Columns.APP_ICON_URL
				+ " text, "
				+ Columns.APP_SIZE
				+ " long, "
				+ Columns.CONTENT_LENGTH
				+ " long, "
				+ Columns.ALREADY_DOWN
				+ " long, "
				+ Columns.APK_PATH
				+ " text, "
				+ Columns.APK_DOWN_PERCENT
				+ " int, "
				+ Columns.APK_DOWN_STATE
				+ " int, "
				+ Columns.APK_DOWN_ERROR
				+ " int, "
				+ Columns.APK_DOWN_CANCEL
				+ " int(1), "
				+ Columns.APK_DOWN_DELETE
				+ " int(1), "
				+ Columns.HASH_CODE
				+ " int, "
				+ Columns.APK_DOWN_NOTIFY
				+ " int(1) ,"
				+ Columns.APK_SOFT_ID
				+ " text, "
				+ Columns.VERSION_NAME
				+ " text, "
				+ Columns.VERSION_CODE
				+ " int, "
				+ Columns.TASK_ID
				+ " int, "
				+ Columns.PACKAGE_ID
				+ " long, "
				+ Columns.ACTION
				+ " text, "
				+ Columns.REL_APK_URL
				+ " text, "
				+ Columns.REL_APK_SIZE
				+ " long, "
				+ Columns.APK_INTEGRAL
				+ " int );";

		private static final String DELETE_DOWNLOAD_TABLE = "drop table "
				+ Columns.TABLE_NAME;

		public DBHelp(Context context, String databaseName, int databaseVersion)
		{
			super(context.getApplicationContext(), databaseName, null,
					databaseVersion);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(CREATE_DOWNLOAD_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL(DELETE_DOWNLOAD_TABLE);
			db.execSQL(CREATE_DOWNLOAD_TABLE);
			DLog.i(TAG, "oldVersion=" + oldVersion + ", newVersion="
					+ newVersion);
		}
	}

	public static class Columns implements BaseColumns
	{
		/** 表名称 */
		public static final String TABLE_NAME = "downloads";

		/** 默认排序 */
		public static final String ORDER_DEFAULT = "_id DESC";

		/** 应用名,DownloadInfo类中的name变量 text */
		public static final String APP_NAME = "app_name";

		/** 包名,DownloadInfo类中的packageName变量 text */
		public static final String APP_PACKAGE_NAME = "app_pkg_name";

		/** 下载的URL,DownloadInfo类中的url变量 text */
		public static final String APP_URL = "url";

		/** 图标url,DownloadInfo类中的iconUrl变量 text */
		public static final String APP_ICON_URL = "app_icon_url";

		/** 应用大小(单位byte),DownloadInfo类中的size变量 long */
		public static final String APP_SIZE = "app_size";

		/** 下载应用长度，HTTP响应报文返回的大小,DownloadInfo类中的contentLength变量 long */
		public static final String CONTENT_LENGTH = "content_length";

		/** 应用已经下载了的大小,DownloadInfo类中的downloadSize变量 long */
		public static final String ALREADY_DOWN = "already_downed";

		/** 下载应用存储路径,DownloadInfo类中的path变量 text */
		public static final String APK_PATH = "apk_path";

		/** apk已经下载的百分比,DownloadInfo类中的percent变量 integer */
		public static final String APK_DOWN_PERCENT = "apk_down_percent";

		/** apk下载状态,DownloadInfo类中的state变量 integer */
		public static final String APK_DOWN_STATE = "apk_down_state";

		/** apk下载过程错误状态,DownloadInfo类中的error变量 integer */
		public static final String APK_DOWN_ERROR = "apk_down_error";

		/** apk下载是否手动取消,DownloadInfo类中的cancel变量 integer 1:true 0:false */
		public static final String APK_DOWN_CANCEL = "apk_down_cancel";

		/** apk下载是否手动删除,DownloadInfo类中的deleted变量 integer 1:true 0:false */
		public static final String APK_DOWN_DELETE = "apk_down_delete";

		/** hashcode,DownloadInfo类中的hashcode变量 integer */
		public static final String HASH_CODE = "hash_code";

		/**
		 * 应用下载是否显示toast和notify提示,DownloadInfo类中的showNotifiction变量 integer
		 * 1:true 0:false
		 */
		public static final String APK_DOWN_NOTIFY = "apk_down_notify";

		/**
		 * 应用的softId
		 */
		public static final String APK_SOFT_ID = "apk_soft_id";

		/**
		 * 应用的积分数量
		 */
		public static final String APK_INTEGRAL = "apk_integral";

		/** 服务器上可更新软件的apk的下载地址(与是否有差分包无关) */
		public static final String REL_APK_URL = "rel_apk_url";

		/** 服务器上完整的apk包大小 */
		public static final String REL_APK_SIZE = "rel_apk_size";

		/** 包id */
		public static final String PACKAGE_ID = "package_id";

		/** 版本名称 */
		public static final String VERSION_NAME = "version_name";

		/** 版本号 */
		public static final String VERSION_CODE = "version_code";

		/** 任务id */
		public static final String TASK_ID = "task_id";

		/** 数据采集action */
		public static final String ACTION = "action";

	}
}
