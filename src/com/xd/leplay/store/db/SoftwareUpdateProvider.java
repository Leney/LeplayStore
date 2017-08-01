package com.xd.leplay.store.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.xd.base.util.DLog;

public class SoftwareUpdateProvider extends ContentProvider
{

	private DBHelp dbHelp;

	// 常量UriMatcher.NO_MATCH表示不匹配任何路径的返回码
	private static final UriMatcher MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	// private static final int UPDATES = 1;

	// private static final int UPDATE = 2;

	/** 软件管理表中的可更新数据 */
	public static final int SOFTWARE_UPDATES = 3;
	static
	{
		// // 如果match()方法匹配content://com.gionee.aora.market/user路径，返回匹配码为1
		// MATCHER.addURI("com.gionee.aora.market", "miss_infos", UPDATES);
		// // 如果match()方法匹配content://com.gionee.aora.market/user/123路径，返回匹配码为2
		// MATCHER.addURI("com.gionee.aora.market", "miss_infos/#", UPDATE);//
		// #号为通配符

		MATCHER.addURI("com.donson.leplay.store", DBHelp.Columns.TABLE_NAME,
				SOFTWARE_UPDATES);
	}

	@Override
	public boolean onCreate()
	{
		dbHelp = new DBHelp(this.getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder)
	{
		DLog.i("lilijun", "系统查询了可更新数量！！！");
		SQLiteDatabase db = dbHelp.getReadableDatabase();
		Cursor cursor = null;
		switch (MATCHER.match(uri))
		{
		// case UPDATES:
		// cursor = db.query("miss_infos", projection, selection,
		// selectionArgs, null, null, sortOrder);
		// break;
		// case UPDATE:
		// String id = uri.getPathSegments().get(1);
		// cursor = db.query("miss_infos", projection, "miss"
		// + "="
		// + id
		// + (!TextUtils.isEmpty(selection) ? "AND(" + selection + ')'
		// : ""), selectionArgs, null, null, sortOrder);
		case SOFTWARE_UPDATES:
			// 查询当前可提示更新的数量(不包括忽略更新的条目)
			cursor = db.query(DBHelp.Columns.TABLE_NAME, projection,
					DBHelp.Columns.PROMPT_UPGREADE + "=" + 0, selectionArgs,
					null, null, null);
			break;

		default:
			DLog.d("lilijun", "Unknown URI" + uri);
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
		return cursor;
	}

	@Override
	public String getType(Uri uri)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
