package com.xd.leplay.store.gui.manager.update;

import java.io.File;
import java.util.List;

import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.AutoUpdateManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.model.ManagerAppInfo;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.download.AppStateConstants;
import com.xd.leplay.store.view.download.DownloadProgressButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ManageListAdapter extends BaseAdapter
{
	private List<ManagerAppInfo> manageAppInfos;

	private Context context;

	/** 下载管理器 */
	protected DownloadManager manager = null;

	/** 软件管理 */
	protected SoftwareManager softwareManager = null;

	/** 图片加载器 */
	protected ImageLoaderManager imageLoader = null;

	/** 下载列表加载图片的配置 */
	protected DisplayImageOptions options = null;

	public ManageListAdapter(Context mContext, List<ManagerAppInfo> appInfos)
	{
		this.manageAppInfos = appInfos;
		this.context = mContext;
		manager = DownloadManager.shareInstance();
		softwareManager = SoftwareManager.getInstance();
		imageLoader = ImageLoaderManager.getInstance();
		options = DisplayUtil.getListIconImageLoaderOptions();
	}

	@Override
	public int getCount()
	{
		return manageAppInfos.size();
	}

	@Override
	public Object getItem(int position)
	{
		return manageAppInfos.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		ManagerAppInfo info = manageAppInfos.get(position);
		if (convertView == null)
		{
			convertView = View.inflate(context, R.layout.manage_list_adapter,
					null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);

		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (info.isUpdateInfo())
		{
			imageLoader.displayImage(info.getUpdateAppInfo().getIconUrl(),
					viewHolder.icon, options);
			viewHolder.codeNameAndSize.setText(info.getUpdateAppInfo()
					.getLocalVersionName()
					+ "-->"
					+ info.getUpdateAppInfo().getUpdateVersionName()
					+ "|"
					+ ToolsUtil.getFormatSize(info.getUpdateAppInfo()
							.getUpdateSoftSize()));
			viewHolder.downloadStateButton.downloadbtn.setText(context
					.getResources().getString(R.string.update));
			viewHolder.downloadStateButton.downloadbtn
					.setBackgroundResource(R.drawable.list_download_btn_bg1_shape);
			viewHolder.downloadStateButton.downloadbtn
					.setTextColor(Color.parseColor(AppStateConstants
							.getAppStateTextColor(AppStateConstants.APP_NO_INSTALL)));
			viewHolder.appName.setText(info.getUpdateAppInfo().getName());
			viewHolder.downloadStateButton.downloadbtn.setTag(info
					.getUpdateAppInfo().getPackageName());
			viewHolder.downloadStateButton.downloadbtn
					.setOnClickListener(updateOnClick);
		} else
		{
			viewHolder.icon.setImageDrawable(info.getInstalledAppInfo()
					.getIcon());
			viewHolder.codeNameAndSize.setText(ToolsUtil.getFormatSize(info
					.getInstalledAppInfo().getAppSize()) + "");
			viewHolder.downloadStateButton.downloadbtn.setText(context
					.getResources().getString(R.string.uninstall_app));
			viewHolder.downloadStateButton.downloadbtn
					.setBackgroundResource(R.drawable.list_download_btn_bg3_shape);
			viewHolder.downloadStateButton.downloadbtn
					.setTextColor(Color.parseColor(AppStateConstants
							.getAppStateTextColor(AppStateConstants.APP_INSTALLED)));
			viewHolder.appName.setText(info.getInstalledAppInfo().getName());
			viewHolder.downloadStateButton.downloadbtn.setTag(info
					.getInstalledAppInfo().getPackageName());
			viewHolder.downloadStateButton.downloadbtn
					.setOnClickListener(unInstallOnClick);
		}
		return convertView;
	}

	public class ViewHolder
	{
		/** 应用图标 */
		public ImageView icon;
		/** 应用名称 */
		public TextView appName;

		/** 版本名称和软件大小 */
		public TextView codeNameAndSize;
		/** 下载状态按钮 */
		public DownloadProgressButton downloadStateButton;

		public ViewHolder(View baseView)
		{
			initView(baseView);
		}

		public void initView(View baseView)
		{
			icon = (ImageView) baseView.findViewById(R.id.update_app_icon);
			appName = (TextView) baseView.findViewById(R.id.update_app_name);
			codeNameAndSize = (TextView) baseView
					.findViewById(R.id.update_codename_and_size_text);
			downloadStateButton = (DownloadProgressButton) baseView
					.findViewById(R.id.update_download_btn);
		}
	}

	public OnClickListener updateOnClick = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String packageName = (String) v.getTag();
			DLog.i("lilijun", "------------>>>>>>packageName" + packageName);
			DownloadInfo info = DownloadManager.shareInstance().queryDownload(
					packageName);
			if (info != null)
			{
				File file = new File(info.getPath());
				if (file.exists())
				{
					SoftwareManager.getInstance()
							.installApkByDownloadInfo(info);
				}
			} else
			{
				DownloadInfo updateDownloadInfo = AutoUpdateManager
						.getInstance().downloadManager
						.queryDownload(packageName);
				if (updateDownloadInfo != null
						&& updateDownloadInfo.getState() == DownloadInfo.STATE_FINISH)
				{
					File file = new File(updateDownloadInfo.getPath());
					if (file.exists())
					{
						softwareManager
								.installApkByDownloadInfo(updateDownloadInfo);
					}
				}
			}
		}
	};
	public OnClickListener unInstallOnClick = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String packageName = (String) v.getTag();
			DLog.i("lilijun", "------------>>>>>>packageName" + packageName);
			softwareManager.uninstallApk(context, packageName);
		}
	};
}
