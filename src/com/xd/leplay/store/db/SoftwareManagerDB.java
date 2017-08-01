package com.xd.leplay.store.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xd.base.util.DLog;
import com.xd.leplay.store.db.DBHelp.Columns;
import com.xd.leplay.store.model.UpdateAppInfo;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class SoftwareManagerDB
{
	private static final String TAG = "SoftwareManagerDB";

	private DBHelp dbHelp;

	public SoftwareManagerDB(Context context)
	{
		dbHelp = new DBHelp(context.getApplicationContext());
	}

	/**
	 * 得到所有安装的有更新的本地软件信息
	 * 
	 * @return UpdateAppInfo数据集合
	 */
	public Hashtable<String, UpdateAppInfo> queryAll()
	{
		Hashtable<String, UpdateAppInfo> map = new Hashtable<String, UpdateAppInfo>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try
		{
			db = dbHelp.getReadableDatabase();
			cursor = db.query(Columns.TABLE_NAME, null, null, null, null, null,
					null);
			while (cursor.moveToNext())
			{
				UpdateAppInfo updateAppInfo = getUpdateAppInfo(cursor);
				map.put(updateAppInfo.getPackageName(), updateAppInfo);
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "queryAll()#Exception=", e);
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
		return map;
	}

	/**
	 * 从查询结果中获取出一条数据
	 * 
	 * @param cursor
	 * @return 一条数据 AppInfo
	 */
	private UpdateAppInfo getUpdateAppInfo(Cursor cursor)
	{
		UpdateAppInfo updateAppInfo = new UpdateAppInfo();
		updateAppInfo.setSoftId(cursor.getInt(cursor
				.getColumnIndex(Columns.SOFT_ID)));
		updateAppInfo.setName(cursor.getString(cursor
				.getColumnIndex(Columns.NAME)));
		updateAppInfo.setPackageName(cursor.getString(cursor
				.getColumnIndex(Columns.PACKAGE_NAME)));
		updateAppInfo.setLocalVersionCode(cursor.getInt(cursor
				.getColumnIndex(Columns.LOCAL_VERSION_CODE)));
		updateAppInfo.setLocalVersionName(cursor.getString(cursor
				.getColumnIndex(Columns.LOCAL_VERSION_NAME)));
		updateAppInfo.setUpdateVersionName(cursor.getString(cursor
				.getColumnIndex(Columns.UPDATE_VERSION_NAME)));
		updateAppInfo.setIconUrl(cursor.getString(cursor
				.getColumnIndex(Columns.ICON_URL)));
		updateAppInfo.setDownloadUrl(cursor.getString(cursor
				.getColumnIndex(Columns.DOWNLOAD_URL)));
		updateAppInfo.setUpdateSoftSize(cursor.getInt(cursor
				.getColumnIndex(Columns.UPDATE_SOFT_SIZE)));
		updateAppInfo.setUpdatePercent(cursor.getString(cursor
				.getColumnIndex(Columns.UPDATE_PERCENT)));
		updateAppInfo.setUpdateDescribe(cursor.getString(cursor
				.getColumnIndex(Columns.UPDATE_DESCRIBE)));
		updateAppInfo.setPublishDate(cursor.getString(cursor
				.getColumnIndex(Columns.PUBLISH_DATA)));
		updateAppInfo.setPackageId(cursor.getLong(cursor
				.getColumnIndex(Columns.PACK_ID)));
		int prompt = cursor.getInt(cursor
				.getColumnIndex(Columns.PROMPT_UPGREADE));
		if (prompt == 0)
		{
			// 提示更新
			updateAppInfo.setPromptUpgreade(true);
		} else
		{
			// 不提示更新(忽略更新)
			updateAppInfo.setPromptUpgreade(false);
		}

		return updateAppInfo;
	}

	/**
	 * 将AppInfo对象转换成ContentValues对象
	 * 
	 * @param updateAppInfo
	 * @return
	 */
	private ContentValues getContentValues(UpdateAppInfo updateAppInfo)
	{
		ContentValues values = new ContentValues();
		values.put(Columns.NAME, updateAppInfo.getName());
		values.put(Columns.PACKAGE_NAME, updateAppInfo.getPackageName());
		values.put(Columns.SOFT_ID, updateAppInfo.getSoftId());
		values.put(Columns.LOCAL_VERSION_CODE,
				updateAppInfo.getLocalVersionCode());
		values.put(Columns.LOCAL_VERSION_NAME,
				updateAppInfo.getLocalVersionName());
		values.put(Columns.UPDATE_VERSION_NAME,
				updateAppInfo.getUpdateVersionName());
		values.put(Columns.ICON_URL, updateAppInfo.getIconUrl());
		values.put(Columns.DOWNLOAD_URL, updateAppInfo.getDownloadUrl());
		values.put(Columns.UPDATE_SOFT_SIZE, updateAppInfo.getUpdateSoftSize());
		values.put(Columns.UPDATE_PERCENT, updateAppInfo.getUpdatePercent());
		values.put(Columns.UPDATE_DESCRIBE, updateAppInfo.getUpdateDescribe());
		values.put(Columns.PUBLISH_DATA, updateAppInfo.getPublishDate());
		values.put(Columns.PACK_ID, updateAppInfo.getPackageId());
		if (updateAppInfo.isPromptUpgreade())
		{
			// 提示升级
			values.put(Columns.PROMPT_UPGREADE, 0);
		} else
		{
			// 不提示升级(忽略)
			values.put(Columns.PROMPT_UPGREADE, 1);
		}
		return values;
	}

	/**
	 * 向软件更新管理表中插入一条数据
	 * 
	 * @param info
	 * @return 所插入的数据在软件管理表中的id
	 */
	public long insert(UpdateAppInfo info)
	{
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		long id = db.insert(Columns.TABLE_NAME, null, getContentValues(info));
		db.close();
		return id;
	}

	/**
	 * 向软件更新管理表中插入一组数据 UpdateAppInfo集合
	 * 
	 * @param appInfos
	 * @return 所插入数据在管理表中的id数组
	 */
	public void insertList(Map<String, UpdateAppInfo> appInfos)
	{
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		Iterator<Entry<String, UpdateAppInfo>> it = appInfos.entrySet()
				.iterator();
		while (it.hasNext())
		{
			Entry<String, UpdateAppInfo> entry = it.next();
			UpdateAppInfo updateAppInfo = entry.getValue();
			db.insert(Columns.TABLE_NAME, null, getContentValues(updateAppInfo));
		}
		db.close();
	}

	// /**
	// * 删除软件管理表中的某条数据
	// * @param info
	// * @return
	// */
	// public int delete(AppInfo info)
	// {
	// SQLiteDatabase db = dbHelp.getWritableDatabase();
	// int count = db.delete(Columns.TABLE_NAME,
	// Columns._ID + "=" + info.getId(), null);
	// db.close();
	// return count;
	// }

	/**
	 * 根据包名删除数据
	 * 
	 * @param packageName
	 */
	public void delete(String packageName)
	{
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		db.delete(Columns.TABLE_NAME, Columns.PACKAGE_NAME + "='" + packageName
				+ "'", null);
		db.close();
	}

	/**
	 * 更新软件管理表中的某条数据
	 * 
	 * @param info
	 * @return
	 */
	public int update(UpdateAppInfo info)
	{
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		int count = db.update(Columns.TABLE_NAME, getContentValues(info),
				Columns.PACKAGE_NAME + "= '" + info.getPackageName() + "'",
				null);
		db.close();
		return count;
	}

	// /**
	// * 查找通过包名查找数据
	// * @return
	// */
	// public boolean queryIsHavaData(String packageName){
	// SQLiteDatabase db=dbHelp.getReadableDatabase();
	// Cursor cursor=db.query(Columns.TABLE_NAME, null,
	// Columns.PACKAGE_NAME+"='"+packageName+"'", null, null, null, null);
	// if(cursor.getCount()>0){
	// db.close();
	// //数据库中有记录
	// return true;
	// }
	// db.close();
	// return false;
	// }

	/**
	 * 查找数据库中已忽略更新的数据
	 * 
	 * @return
	 */
	public HashMap<String, UpdateAppInfo> queryIgnoreAppInfos()
	{
		SQLiteDatabase db = dbHelp.getReadableDatabase();
		HashMap<String, UpdateAppInfo> ignores = new HashMap<String, UpdateAppInfo>();
		Cursor cursor = db.query(Columns.TABLE_NAME, null,
				Columns.PROMPT_UPGREADE + "=" + 1, null, null, null, null);
		while (cursor.moveToNext())
		{
			UpdateAppInfo info = getUpdateAppInfo(cursor);
			ignores.put(info.getPackageName(), info);
		}
		db.close();
		return ignores;
	}

	/**
	 * 查找数据库中可更新的数据
	 * 
	 * @return
	 */
	public HashMap<String, UpdateAppInfo> queryUpdateAppInfos()
	{
		SQLiteDatabase db = dbHelp.getReadableDatabase();
		HashMap<String, UpdateAppInfo> updates = new HashMap<String, UpdateAppInfo>();
		Cursor cursor = db.query(Columns.TABLE_NAME, null,
				Columns.PROMPT_UPGREADE + "=" + 0, null, null, null, null);
		while (cursor.moveToNext())
		{
			UpdateAppInfo info = getUpdateAppInfo(cursor);
			updates.put(info.getPackageName(), info);
		}
		db.close();
		return updates;
	}

	/**
	 * 更新软件更新管理表中的集合数据
	 * 
	 * @return 更新数据条数
	 */
	public int updateAppInfos(Hashtable<String, UpdateAppInfo> infos)
	{
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		int count = 0;
		for (Entry<String, UpdateAppInfo> entry : infos.entrySet())
		{
			db.update(Columns.TABLE_NAME, getContentValues(entry.getValue()),
					Columns.PACKAGE_NAME + "= '"
							+ entry.getValue().getPackageName() + "'", null);
			count++;
		}
		db.close();
		return count;
	}

	/**
	 * 更新软件信息表的cur_version_code列
	 *
	 * @param packageName
	 *            包名
	 * @param versionCode
	 *            软件版本CODE
	 */
	public void updateVersionCode(String packageName, int versionCode)
	{
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Columns.LOCAL_VERSION_CODE, versionCode);
		db.update(Columns.TABLE_NAME, values, Columns.PACKAGE_NAME + "= '"
				+ packageName + "' ", null);
		db.close();
	}

	/**
	 * 删除表中所有数据
	 */
	public void clearFeedTable()
	{
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		String sql = "DELETE FROM " + Columns.TABLE_NAME + ";";
		db.execSQL(sql);
		revertSeq(db);
		db.close();

	}

	/**
	 * 让自增长还原
	 */
	private void revertSeq(SQLiteDatabase db)
	{
		if (db != null)
		{
			String sql = "update sqlite_sequence set seq=0 where name='"
					+ Columns.TABLE_NAME + "'";
			db.execSQL(sql);
		}
	}

	public void close()
	{
		dbHelp.close();
	}

	/**
	 * 向更新数量表中插入数据(插入更新条数数据)
	 */
	public void insertDataToMissInfosTable(int count)
	{
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("miss", count);
		db.insert("miss_infos", null, values);
		DLog.i("lilijun", "插入了数据,miss----->>>" + count);
		db.close();
	}

	/** 查找是否有创建过表 并且查入过数据 */
	public boolean queryMissInfos()
	{
		SQLiteDatabase db = dbHelp.getReadableDatabase();
		Cursor cursor = db.query("miss_infos", null, null, null, null, null,
				null);
		if (cursor.getCount() == 0)
		{
			return false;
		}
		db.close();
		return true;
	}

	/**
	 * 更新软件管理表中的集合数据
	 * 
	 * @param count
	 *            更新条数
	 */
	public void updateMissInfosTable(int count)
	{
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("miss", count);
		db.update("miss_infos", values, null, null);
		DLog.i("lilijun", "更新了   更新条数表中的数据！！！");
		db.close();
	}

	// private static class DBHelp extends SQLiteOpenHelper
	// {
	// // 数据库名称
	// private static final String DATABASE_NAME = "SoftwaresDB";
	//
	// // 数据库版本
	// private static final int DATABASE_VERSION = 6;
	//
	// /** 创建软件管理表的sql语句 */
	// private static final String CREATE_SOFTWARES_TABLE =
	// "create table if not exists "
	// + Columns.TABLE_NAME
	// + " ("
	// + Columns._ID
	// + " integer primary key autoincrement, "
	// + Columns.NAME
	// + " text, "
	// + Columns.PACKAGE_NAME
	// + " text, "
	// + Columns.CUR_VERSION_CODE
	// + " integer, "
	// + Columns.CUR_VERSION_NAME
	// + " text,"
	// + Columns.UPDATE_VERSION_NAME
	// + " text,"
	// + Columns.ICON_URL
	// + " text,"
	// + Columns.DOWNLOAD_URL
	// + " text,"
	// + Columns.UPDATE_SOFT_SIZE
	// + " integer,"
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
	// + Columns.PROMPT_UPGREADE
	// + " bit);";
	//
	// /** 删除软件管理表的sql语句 */
	// private static final String DELETE_SOFTWARE_TABLE = "drop table "
	// + Columns.TABLE_NAME;
	//
	// public DBHelp(Context context)
	// {
	// super(context, DATABASE_NAME, null, DATABASE_VERSION);
	// }
	//
	// public void onCreate(SQLiteDatabase db)
	// {
	// db.execSQL(CREATE_SOFTWARES_TABLE);
	// }
	//
	// public void onUpgrade(SQLiteDatabase db, int oldVersion, int
	// newVersion)
	// {
	// db.execSQL(DELETE_SOFTWARE_TABLE);
	// db.execSQL(CREATE_SOFTWARES_TABLE);
	// DLog.i(TAG, "oldVersion=" + oldVersion + ", newVersion="
	// + newVersion);
	// }
	// }

	// public static class Columns implements BaseColumns
	// {
	// /** 表名称 */
	// public static final String TABLE_NAME = "softwares";
	//
	// /** 软件名称 */
	// public static final String NAME = "name";
	//
	// /** 软件包名 */
	// public static final String PACKAGE_NAME = "package_name";
	//
	// /** 当前本地安装的版本号 */
	// public static final String CUR_VERSION_CODE = "cur_version_code";
	//
	// /** 当前本地安装的版本名称 */
	// public static final String CUR_VERSION_NAME = "cur_version_name";
	//
	// /** 服务器可更新的版本名称 */
	// public static final String UPDATE_VERSION_NAME =
	// "update_version_name";
	//
	// /** 有更新的软件图标地址 */
	// public static final String ICON_URL = "icon_url";
	//
	// /** 有更新的软件下载地址 */
	// public static final String DOWNLOAD_URL = "download_url";
	//
	// /** 服务器上更新软件的大小 */
	// public static final String UPDATE_SOFT_SIZE = "update_soft_size";
	//
	// /** 软件状态 */
	// public static final String CUR_STATE = "cur_state";
	//
	// /** 是否提示升级 */
	// public static final String PROMPT_UPGREADE = "prompt_upgreade";
	//
	// /** 本地应用签名是否与服务器应用签名一致 */
	// public static final String SAME_SIGN = "same_sign";
	//
	// /** 签名 */
	// public static final String SIGN = "sign";
	//
	// /** 是否是内置应用 */
	// public static final String IS_INLAY = "is_inlay";
	//
	// /** 是否在已安装列表显示*/
	// public static final String IS_SHOW_INSTALLED_LIST =
	// "is_show_installed_list";
	//
	// /** 服务器上可更新的软件apk文件大小 */
	// public static final String UPDATE_APK_SIZE = "update_apk_size";
	//
	// /** 是否可差分升级*/
	// public static final String DIFFERENCE_UPGRADE="difference_upgrade";
	//
	// /** 服务器上可更新软件的apk的下载地址(与是否有差分包无关)*/
	// public static final String REL_APK_URL="rel_apk_url";
	// }

}
