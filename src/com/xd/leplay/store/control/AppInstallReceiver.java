package com.xd.leplay.store.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xd.leplay.store.Constants;

/**
 * 接收全局的软件安装、替换、卸载广播类
 * 
 * @author lilijun
 */
public class AppInstallReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED))
		{
			String packageName = intent.getData().getSchemeSpecificPart();
			Intent intent2 = new Intent();
			intent2.putExtra("packageName", packageName);
			intent2.setAction(Constants.ACTION_INSTALLED_SOFTWARE_SUCCESS);
			context.sendBroadcast(intent2);
		}
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED))
		{
			String packageName = intent.getData().getSchemeSpecificPart();
			Intent intent2 = new Intent();
			intent2.putExtra("packageName", packageName);
			intent2.setAction(Constants.ACTION_UNINSTALLED_SOFTWARE_SUCCESS);
			context.sendBroadcast(intent2);
		}
		// if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED))
		// {
		// String packageName = intent.getData().getSchemeSpecificPart();
		// Toast.makeText(context, "替换成功" + packageName, Toast.LENGTH_LONG)
		// .show();
		// DLog.i("lilijun", "packageName---->>>" + packageName);
		// Intent intent2 = new Intent();
		// intent2.putExtra("packageName", packageName);
		// intent2.setAction(Constants.ACTION_REPLACED_SOFTWARE_SUCCESS);
		// context.sendBroadcast(intent2);
		// }
	}
}
