package com.xd.leplay.store.gui.manager.update;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.TextViewCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.R;
import com.xd.leplay.store.model.UpdateAppInfo;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.MarketListView;
import com.xd.leplay.store.view.download.DownloadOnclickListener;
import com.xd.leplay.store.view.download.DownloadProgressButton;

/**
 * 更新列表Adapter
 * 
 * @author lilijun
 *
 */
public class UpdateAdapter extends UpdateDownloadBaseAdapter<MarketListView>
{
	private Context mContext;

	private Handler mHandler = null;

	private String action = "";

	public UpdateAdapter(Context context, MarketListView listView,
			ArrayList<UpdateAppInfo> appInfos, Handler handler, String action)
	{
		super(context, listView, appInfos);
		this.mContext = context;
		this.mHandler = handler;
		this.action = action;
		setUpdateProgress(false);
	}

	public class SoftListViewHolder
	{
		/** 应用图标 */
		public ImageView icon;

		/** 应用名称 */
		public TextView appName;

		/** 版本名称和软件大小 */
		public TextView codeNameAndSize;

		/** 更新人数百分比 */
		public TextView percent;

		/** 更新说明 */
		public TextView updateDecrible;

		/** 发布时间 */
		public TextView publishDate;

		/** 忽略应用 */
		public TextView ignoreApp;

		/** 收缩/展开指示图片 */
		public ImageView pointImg;

		/** 更新说明整个部分(包括指示图片)、发布时间和忽略应用部分 */
		public LinearLayout updateDesLay, publishAndIgnoreLay;

		/** 下载状态按钮 */
		public DownloadProgressButton downloadStateButton;

		/** 下载监听器 */
		public DownloadOnclickListener listener;

		public void init(View baseView)
		{
			icon = (ImageView) baseView.findViewById(R.id.update_app_icon);
			appName = (TextView) baseView.findViewById(R.id.update_app_name);
			codeNameAndSize = (TextView) baseView
					.findViewById(R.id.update_codename_and_size_text);
			percent = (TextView) baseView
					.findViewById(R.id.update_percent_text);
			updateDecrible = (TextView) baseView
					.findViewById(R.id.update_decrible);
			publishDate = (TextView) baseView
					.findViewById(R.id.update_publish_date);
			ignoreApp = (TextView) baseView
					.findViewById(R.id.update_ignore_app);
			pointImg = (ImageView) baseView.findViewById(R.id.update_point_img);
			updateDesLay = (LinearLayout) baseView
					.findViewById(R.id.update_decrible_lay);
			publishAndIgnoreLay = (LinearLayout) baseView
					.findViewById(R.id.update_publish_and_ignore_lay);

			downloadStateButton = (DownloadProgressButton) baseView
					.findViewById(R.id.update_download_btn);
			listener = new DownloadOnclickListener(context, action);
		}

		public SoftListViewHolder(View baseView)
		{
			init(baseView);
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		SoftListViewHolder holder = null;
		UpdateAppInfo info = appInfos.get(position);
		if (convertView == null)
		{
			convertView = (LinearLayout) View.inflate(context,
					R.layout.update_adapter, null);
			holder = new SoftListViewHolder(convertView);
			convertView.setTag(holder);
			holder.downloadStateButton.downloadbtn
					.setOnClickListener(holder.listener);
			holder.updateDesLay
					.setOnClickListener(updateDescribleLayClickListener);
			holder.ignoreApp.setOnClickListener(ignoreAppOnClickListener);
		} else
		{
			holder = (SoftListViewHolder) convertView.getTag();
		}
		holder.updateDesLay.setTag(holder.updateDecrible);
		holder.updateDesLay.setTag(R.id.update_point_img, holder.pointImg);

		holder.ignoreApp.setTag(info.getPackageName());

		imageLoader.displayImage(info.getIconUrl(), holder.icon, options);
		holder.appName.setText(info.getName());
		holder.codeNameAndSize.setText(info.getLocalVersionName() + "-->"
				+ info.getUpdateVersionName() + "				"
				+ ToolsUtil.getFormatSize(info.getUpdateSoftSize()));
		holder.percent.setText(info.getUpdatePercent()
				+ mContext.getResources().getString(R.string.update_percent));
		holder.updateDecrible.setText(mContext.getResources().getString(
				R.string.update_describe)
				+ info.getUpdateDescribe());
		holder.publishDate.setText(mContext.getResources().getString(
				R.string.publish_date)
				+ info.getPublishDate());

		holder.downloadStateButton.setInfo(info.getPackageName());
		holder.listener.setDownloadListenerInfo(info);
		return convertView;
	}

	@Override
	protected void refreshData(DownloadInfo info, View view)
	{
		SoftListViewHolder viewHolder = (SoftListViewHolder) view.getTag();
		if (viewHolder != null)
		{
			viewHolder.downloadStateButton.setInfo(info.getPackageName());
			viewHolder.listener.setDownloadListenerInfo(info);
		}
	}

	/**
	 * 更新说明整个区域的点击事件
	 */
	private OnClickListener updateDescribleLayClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			TextView describle = (TextView) v.getTag();
			ImageView pointImg = (ImageView) v.getTag(R.id.update_point_img);
			int maxLines = TextViewCompat.getMaxLines(describle);
			DLog.i("lilijun", "maxLines----------->>>" + maxLines);
			if (maxLines == 100)
			{
				describle.setMaxLines(1);
				pointImg.setImageResource(R.drawable.down_point);
			} else
			{
				describle.setMaxLines(100);
				pointImg.setImageResource(R.drawable.up_point);
			}
		}
	};

	/**
	 * 忽略应用 的点击事件
	 */
	public OnClickListener ignoreAppOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			String packageName = (String) v.getTag();
			softwareManager.getUpdateAppInfos().get(packageName)
					.setPromptUpgreade(false);
			// 更新数据库中保存的更新信息数据
			softwareManager.updateUpdateInfoToDB(softwareManager
					.getUpdateAppInfos().get(packageName));
			mHandler.sendEmptyMessage(UpdateFragment.REGET_LIST_DATA);
			// 发送忽略软件更新的广播
			mContext.sendBroadcast(new Intent(
					Constants.ACTION_UPDATE_ACTIVITY_IGNORE_OR_CANCLE_IGNORE_APP));
		}
	};

}
