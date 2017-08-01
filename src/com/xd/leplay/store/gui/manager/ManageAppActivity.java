package com.xd.leplay.store.gui.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.InstalledAppInfo;
import com.xd.leplay.store.util.ToolsUtil;

/**
 * 管理应用(已安装应用列表)
 * 
 * @author lilijun
 *
 */
public class ManageAppActivity extends BaseActivity
{
	private ListView listView;

	private ManageAppAdapter adapter = null;

	/** 卸载按钮 */
	private Button uninstallBtn;

	private SoftwareManager softwareManager = null;

	private List<InstalledAppInfo> list = null;

	private Resources resources;

	/** 选择状态发生改变 */
	public static final int CHECK_STATE_CHANGED_MSG = 1;

	/** 选中的已安装应用集合 */
	private List<InstalledAppInfo> checkedList = new ArrayList<InstalledAppInfo>();

	private ManageAppReceiver receiver = null;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what == CHECK_STATE_CHANGED_MSG)
			{
				if (!checkedList.isEmpty())
				{
					long size = 0;
					for (InstalledAppInfo tempAppInfo : checkedList)
					{
						size += tempAppInfo.getAppSize();
					}
					String formatSize = ToolsUtil.formatFileSize(size);
					uninstallBtn.setEnabled(true);
					uninstallBtn
							.setTextColor(resources.getColor(R.color.white));
					uninstallBtn.setText(String.format(getResources()
							.getString(R.string.uninstall_all2),
							checkedList.size() + "", formatSize));
				} else
				{
					uninstallBtn.setEnabled(false);
					uninstallBtn.setTextColor(resources
							.getColor(R.color.button_unenable_text_color));
					uninstallBtn.setText(getResources().getString(R.string.uninstall_all));
				}

			}
		};
	};

	@Override
	protected void initView()
	{
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_INSTALLED_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		softwareManager = SoftwareManager.getInstance();
		resources = getResources();
		titleView.setTitleName(getResources().getString(R.string.manager_app));
		titleView.setBottomLineVisible(true);
		titleView.setRightLayVisible(false);

		setCenterView(R.layout.manage_app_activity);
		listView = (ListView) findViewById(R.id.manage_app_listview);
		uninstallBtn = (Button) findViewById(R.id.manage_app_uninstall_all_btn);
		uninstallBtn.setEnabled(false);
		uninstallBtn.setTextColor(resources
				.getColor(R.color.button_unenable_text_color));
		uninstallBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				for (InstalledAppInfo appInfo : checkedList)
				{
					// 卸载应用
					softwareManager.uninstallApk(ManageAppActivity.this,
							appInfo.getPackageName());
				}
			}
		});

		list = new ArrayList<InstalledAppInfo>();
		for (Entry<String, InstalledAppInfo> entry : softwareManager
				.getInstalledAppInfos().entrySet())
		{
			InstalledAppInfo info = entry.getValue();
			if (!info.isSystemApp())
			{
				// 只显示第三方应用
				list.add(info);
			}
		}
		loadingView.setVisibilyView(false);
		adapter = new ManageAppAdapter(ManageAppActivity.this, list,
				checkedList, mHandler);

		// 底部顶上来的空白视图
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT, (int) getResources()
						.getDimension(R.dimen.install_bottom_lay_height));
		View emptyView = new View(ManageAppActivity.this);
		emptyView.setLayoutParams(params);
		listView.addFooterView(emptyView);

		listView.setAdapter(adapter);

		receiver = new ManageAppReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS);
		filter.addAction(Constants.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS);
		registerReceiver(receiver, filter);
	}

	/**
	 * 本地广播类
	 * 
	 * @author lilijun
	 */
	private class ManageAppReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// if (Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS
			// .equals(intent.getAction())
			// || Constants.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS
			// .equals(intent.getAction()))
			// {
			// // 接收到软件安装成功或卸载成功的广播
			// String packageName = intent.getStringExtra("packageName");
			// DLog.i("lilijun", "已安装界面接收到软件卸载或安装成功的广播，packageName----->>>"
			// + packageName);
			//
			// list.clear();
			// checkedList.clear();
			// for (Entry<String, InstalledAppInfo> entry : softwareManager
			// .getInstalledAppInfos().entrySet())
			// {
			// list.add(entry.getValue());
			// if (entry.getValue().isChecked())
			// {
			// checkedList.add(entry.getValue());
			// }
			// }
			// DLog.i("lilijun", "list.size()----->>>" + list.size());
			// adapter.setList(list);
			// adapter.setCheckedList(checkedList);
			// mHandler.sendEmptyMessage(CHECK_STATE_CHANGED_MSG);
			// adapter.notifyDataSetChanged();
			// }

			if (Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS
					.equals(intent.getAction()))
			{
				// 安装软件成功广播
				String packageName = intent.getStringExtra("packageName");
				DLog.i("lilijun", "已安装列表接收到安装软件广播:  packageName------>>"
						+ packageName);
				if (softwareManager.getInstalledAppInfos().containsKey(
						packageName))
				{
					InstalledAppInfo installedAppInfo = softwareManager
							.getInstalledAppInfos().get(packageName);
					list.add(installedAppInfo);
					mHandler.sendEmptyMessage(CHECK_STATE_CHANGED_MSG);
					adapter.notifyDataSetChanged();
				}
			} else if (Constants.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS
					.equals(intent.getAction()))
			{
				// 卸载软件成功广播
				String packageName = intent.getStringExtra("packageName");
				for (InstalledAppInfo installedAppInfo : list)
				{
					if (packageName.equals(installedAppInfo.getPackageName()))
					{
						list.remove(installedAppInfo);
						if (checkedList.contains(installedAppInfo))
						{
							checkedList.remove(installedAppInfo);
						}
						break;
					}
				}
				mHandler.sendEmptyMessage(CHECK_STATE_CHANGED_MSG);
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void finish()
	{
		super.finish();
		// 设置软件管理中的已安装数据默认为非选择状态
		for (InstalledAppInfo tempAppInfo : checkedList)
		{
			softwareManager.getInstalledAppInfos()
					.get(tempAppInfo.getPackageName()).setChecked(false);
		}
	}

	@Override
	protected void onDestroy()
	{
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	public static void startManagerAppActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				ManageAppActivity.class), action);
	}
}
