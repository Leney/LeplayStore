package com.xd.base.util;

import java.io.File;

import org.apache.log4j.Logger;

import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Log
 * 
 * @author 李丽均
 * 
 */
public class DLog
{
	private static boolean LOG = false;

	private static String TAG = "DLog";

	public static void init(String savePath, boolean logSwitch)
	{
		if (logSwitch)
		{
			try
			{
				boolean useFileAppender = Environment.MEDIA_MOUNTED
						.equals(Environment.getExternalStorageState());

				String path = Environment.getExternalStorageDirectory()
						+ File.separator + savePath;
				new File(path).mkdirs();
				LogConfigurator config = new LogConfigurator();
				String fileName = path + File.separator + "log4j.txt";
				config.setUseFileAppender(useFileAppender);
				config.setFileName(fileName);
				config.setMaxBackupSize(10);
				config.setMaxFileSize(10 * 1024 * 1024);
				config.setFilePattern("%d [%-5p] [%t] [%c{2}] %m%n");
				config.configure();
			} catch (Exception e)
			{
				e.printStackTrace();
			}

			LOG = logSwitch;
		}
	}

	public static boolean isLOG()
	{
		return DLog.LOG;
	}

	public static String getTAG()
	{
		return DLog.TAG;
	}

	public static void setTAG(String aTAG)
	{
		DLog.TAG = aTAG;
	}

	// TAG
	public static void v(String msg)
	{
		if (LOG)
		{
			// Log.v(TAG, msg);

			Logger log = Logger.getLogger(TAG);
			log.debug(msg);
		}
	}

	public static void v(String msg, Throwable tr)
	{
		if (LOG)
		{
			// Log.v(TAG, msg, tr);
			// tr.printStackTrace();

			Logger log = Logger.getLogger(TAG);
			log.debug(msg, tr);
		}
	}

	public static void d(String msg)
	{
		if (LOG)
		{
			// Log.d(TAG, msg);

			Logger log = Logger.getLogger(TAG);
			log.debug(msg);
		}
	}

	public static void d(String msg, Throwable tr)
	{
		if (LOG)
		{
			// Log.d(TAG, msg, tr);
			// tr.printStackTrace();

			Logger log = Logger.getLogger(TAG);
			log.debug(msg, tr);
		}
	}

	public static void i(String msg)
	{
		if (LOG)
		{
			// Log.i(TAG, msg);

			Logger log = Logger.getLogger(TAG);
			log.info(msg);
		}
	}

	public static void i(String msg, Throwable tr)
	{
		if (LOG)
		{
			// Log.i(TAG, msg, tr);
			// tr.printStackTrace();

			Logger log = Logger.getLogger(TAG);
			log.info(msg, tr);
		}
	}

	public static void w(String msg)
	{
		if (LOG)
		{
			// Log.w(TAG, msg);

			Logger log = Logger.getLogger(TAG);
			log.warn(msg);
		}
	}

	public static void w(String msg, Throwable tr)
	{
		if (LOG)
		{
			// Log.w(TAG, msg, tr);
			// tr.printStackTrace();

			Logger log = Logger.getLogger(TAG);
			log.warn(msg, tr);
		}
	}

	public static void e(String msg)
	{
		if (LOG)
		{
			// Log.e(TAG, msg);

			Logger log = Logger.getLogger(TAG);
			log.error(msg);
		}
	}

	public static void e(String msg, Throwable tr)
	{
		if (LOG)
		{
			// Log.e(TAG, msg, tr);
			// tr.printStackTrace();

			Logger log = Logger.getLogger(TAG);
			log.error(msg, tr);
		}
	}

	// TAG

	public static void v(String tag, String msg)
	{
		if (LOG)
		{
			// Log.v(tag, msg);

			Logger log = Logger.getLogger(tag);
			log.debug(msg);
		}
	}

	public static void v(String tag, String msg, Throwable tr)
	{
		if (LOG)
		{
			// Log.v(tag, msg, tr);
			// tr.printStackTrace();

			Logger log = Logger.getLogger(tag);
			log.debug(msg, tr);
		}
	}

	public static void d(String tag, String msg)
	{
		if (LOG)
		{
			// Log.d(tag, msg);

			Logger log = Logger.getLogger(tag);
			log.debug(msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr)
	{
		if (LOG)
		{
			// Log.d(tag, msg, tr);
			// tr.printStackTrace();

			Logger log = Logger.getLogger(tag);
			log.debug(msg, tr);
		}
	}

	public static void i(String tag, String msg)
	{
		if (LOG)
		{
			// Log.i(tag, msg);

			Logger log = Logger.getLogger(tag);
			log.info(msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr)
	{
		if (LOG)
		{
			// Log.i(tag, msg, tr);
			// tr.printStackTrace();

			Logger log = Logger.getLogger(tag);
			log.info(msg, tr);
		}
	}

	public static void w(String tag, String msg)
	{
		if (LOG)
		{
			// Log.w(tag, msg);

			Logger log = Logger.getLogger(tag);
			log.warn(msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr)
	{
		if (LOG)
		{
			// Log.w(tag, msg, tr);
			// tr.printStackTrace();

			Logger log = Logger.getLogger(tag);
			log.warn(msg, tr);
		}
	}

	public static void e(String tag, String msg)
	{
		if (LOG)
		{
			// Log.e(tag, msg);

			Logger log = Logger.getLogger(tag);
			log.error(msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr)
	{
		if (LOG)
		{
			// Log.e(tag, msg, tr);
			// tr.printStackTrace();

			Logger log = Logger.getLogger(tag);
			log.error(msg, tr);
		}
	}
}
