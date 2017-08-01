package com.xd.base.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

/**
 * 监控全局的异常类
 * 
 * @author lilijun
 * 
 */
public class DefaultExceptionHandler implements UncaughtExceptionHandler
{
	private static final String TAG = "DefaultExceptionHandler";

	private Context context;

	public DefaultExceptionHandler(Context context)
	{
		this.context = context;
	}

	private File getLogDirectory(Context context)
	{
		File appLogDir = null;
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
		{
			appLogDir = getExternalCacheDir(context);
		}
		if (appLogDir == null)
		{
			appLogDir = context.getCacheDir();
		}
		return appLogDir;
	}

	private File getExternalCacheDir(Context context)
	{
		File appLogDir = new File(new File(
				Environment.getExternalStorageDirectory(), "donson"), "log");
		if (!appLogDir.exists())
		{
			if (!appLogDir.mkdirs())
			{
				DLog.w(TAG, "Unable to create directory");
				return null;
			}
			try
			{
				new File(appLogDir, ".nomedia").createNewFile();
			} catch (IOException e)
			{
			}
		}
		return appLogDir;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex)
	{
		// 收集异常信息，写入到sd卡
		restoreCrash(thread, ex);

		// 发送Crash信息到服务器
		sendCrashReport(thread, ex);

		// 处理异常
		handleException(thread, ex);

		// 退出
		killProcess();
	}

	protected void restoreCrash(Thread thread, Throwable ex)
	{
		// 收集异常信息，写入到sd卡
		try
		{
			File path = getLogDirectory(context);
			java.text.DateFormat format1 = new java.text.SimpleDateFormat(
					"yyyyMMddhhmmss");
			String name = format1.format(new Date(System.currentTimeMillis()))
					+ ".log";
			PrintStream err = new PrintStream(new File(path, name));
			ex.printStackTrace(err);
		} catch (Exception e)
		{
			DLog.e(TAG, "sendCrashReport Exception=", e);
		}
	}

	protected void sendCrashReport(Thread thread, Throwable ex)
	{
		// TODO 发送Crash信息到服务器
	}

	protected void handleException(Thread thread, Throwable ex)
	{
		DLog.e(TAG, "handleException#threadId=" + thread.getId()
				+ ", threadName=" + thread.getName() + ", exception=", ex);

		// 正式版本崩溃不提示用户
		if (!DLog.isLOG())
		{
			return;
		}

		// 提示用户程序崩溃了
		new Thread()
		{
			@Override
			public void run()
			{
				// 必须这样实现才能显示提示
				Looper.prepare();
				Toast.makeText(context, "程序出现异常,烦请报告开发人员，谢谢!",
						Toast.LENGTH_LONG).show();
				Looper.loop();
				if (Looper.myLooper() != null)
				{
					Looper.myLooper().quit();
				}
			}
		}.start();

		// 间隔3秒，让用户看到提示
		try
		{
			Thread.sleep(3000);
		} catch (InterruptedException e)
		{
			DLog.d(TAG, "handleException# Thread.sleep exception=", e);
		}
	}

	protected void killProcess()
	{
		// 杀死程序
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}
}
