package com.xd.base.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 获取配置信息的帮助类
 * 
 * @author lilijun
 *
 */
public class PropertiesUtil
{
	/**
	 * 获取配置url
	 * 
	 * @param context
	 * @return
	 */
	public static Properties initProperties(Context context)
	{
		Properties properties = new Properties();
		InputStream is = null;
		try
		{
			String packageName = context.getPackageName();
			// PackageManager pm = context.getPackageManager();
			// PackageInfo pi = pm.getPackageInfo(packageName, 0);

			// String versionCode = pi.versionCode + "";
			// if (versionCode.endsWith("0"))
			// {
			AssetManager am = context.getAssets();
			is = am.open(packageName + ".ini");
			// } else
			// {
			// File file = new File(Environment.getExternalStorageDirectory()
			// + "/" + packageName + ".ini");
			// is = new BufferedInputStream(new FileInputStream(file));
			// }
			properties.load(is);
		} catch (Exception e)
		{
			DLog.e("PropertiesUtil#init. System Properties init faild. Exception=",
					e);
			throw new RuntimeException(
					"PropertiesUtil#init. System Properties init faild.");
		} finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				} catch (IOException e)
				{
					DLog.e("PropertiesUtil#init. System Properties init faild. IOException=",
							e);
					throw new RuntimeException(
							"PropertiesUtil#init. System Properties init faild.");
				}
			}
		}
		return properties;
	}

}
