package com.xd.download;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.text.DecimalFormat;


/**
 * 
 * @author lilijun
 */
public class Util
{

	private final static String NONETWORK = "NONETWORK";

	private final static String NET = "NET";

	private final static String WAP = "WAP";

	private final static String WIFI = "WIFI";

	private static final int NONETWORK_INT = -1;

	private static final int WAP_INT = 0;

	private static final int NET_INT = 1;

	public static final int WIFI_INT = 2;

	// 当前的APN
	private static final Uri PREFERRED_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");

	// private static String[] projection =
	// { "_id", "apn", "type", "current", "proxy", "port" };
	private static String[] projection =
	{ "apn" };

	public static boolean isNetworkAvailable(Context c)
	{
		ConnectivityManager connectivity = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity != null)
		{
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null)
			{
				if (info.getState() == NetworkInfo.State.CONNECTED)
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取网络连接方式
	 * 
	 * @param context
	 * @return -1 无网络 0 wap网络 1 net网络 2 wifi网络
	 */
	public static int getNetType(Context context)
	{
		String noteType = getNetMode(context);
		int notetype = NONETWORK_INT;

		if (noteType.equals(NONETWORK))
		{
			notetype = NONETWORK_INT;
		} else if (noteType.equals(WAP))
		{
			notetype = WAP_INT;
		} else if (noteType.equals(NET))
		{
			notetype = NET_INT;
		} else if (noteType.equals(WIFI))
		{
			notetype = WIFI_INT;
		}

		return notetype;
	}

	/**
	 * 获取网络连接方式
	 * 
	 * @param aContext
	 * @return
	 */
	public static String getNetMode(Context aContext)
	{
		String noteType = NONETWORK;

		ConnectivityManager connManager = (ConnectivityManager) aContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		if (info != null)
		{
			if (info.getTypeName().toUpperCase().equals("WIFI"))
			{
				noteType = WIFI;
			} else
			{
				if (info.getExtraInfo() != null)
				{
					if (info.getExtraInfo().toUpperCase().indexOf("WAP") != -1)
					{
						noteType = WAP;
					} else
					{
						noteType = NET;
					}
				} else
				{
					String apninfo = null;
					Cursor cursor = getApnCursor(aContext);
					if (cursor != null && cursor.moveToFirst())
					{
						apninfo = cursor
								.getString(cursor.getColumnIndex("apn"));
						cursor.close();
					}
					if (apninfo != null)
					{
						if (apninfo.toUpperCase().indexOf("WAP") != -1)
						{
							noteType = WAP;
						} else
						{
							noteType = NET;
						}
					} else
					{
						noteType = NONETWORK;
					}
				}
			}
		} else
		{
			noteType = NONETWORK;
		}

		return noteType;
	}

	private static Cursor getApnCursor(Context context)
	{
		ContentResolver resoler = context.getContentResolver();
		Cursor cur = resoler.query(PREFERRED_APN_URI, projection, null, null,
				null);
		return cur;
	}

	public static final String getFormatSize(long size)
	{
		if(size < 0){
			return "未知";
		}
		DecimalFormat df = new DecimalFormat("#.0");
		String fileSizeString = "";
		if (size < 1024)
		{
			if (size == 0)
			{
				fileSizeString = "0B";
			} else
			{
				fileSizeString = df.format((double) size) + "B";
			}

		} else if (size < 1048576)
		{
			fileSizeString = df.format((double) size / 1024) + "KB";
		} else if (size < 1073741824)
		{
			fileSizeString = df.format((double) size / 1048576) + "MB";
		} else
		{
			fileSizeString = df.format((double) size / 1073741824) + "GB";
		}
		return fileSizeString;
	}

}
