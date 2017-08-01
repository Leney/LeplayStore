package com.xd.leplay.store.gui.download;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xd.download.DownloadInfo;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.gui.details.DetailsActivity;
import com.xd.leplay.store.gui.download.DownloadManagerAdapter.ChildListViewHolder;
import com.xd.leplay.store.gui.download.adapter.DownloadExpandableBaseAdapter;
import com.xd.leplay.store.gui.download.adapter.DownloadViewHolder;
import com.xd.leplay.store.view.download.DownloadButton;
import com.xd.leplay.store.view.download.DownloadOnclickListener;

public class DownloadManagerAdapter extends
		DownloadExpandableBaseAdapter<ChildListViewHolder>
{
	private List<ChildListViewHolder> childHolderList;

	private List<String> groupList = null;

	private List<List<DownloadInfo>> childrenList = null;

	private DownloadManager downloadManager = null;

	/** 展开的Item的groupPosition(即:显示 "取消" 和 "详情" 按钮部分的item) */
	private int curExpandableGroupPosition = -1;

	/** 展开的Item的childPosition(即:显示 "取消" 和 "详情" 按钮部分的item) */
	private int curExpandableChildPosition = -1;

	/** 当前展开的Item的ChildListViewHolder(即:显示 "取消" 和 "详情" 按钮部分的item) */
	private ChildListViewHolder curExpandableChildListViewHolder = null;

	private String action = "";

	private Handler handler = null;

	public DownloadManagerAdapter(Context context, List<String> groupList,
			List<List<DownloadInfo>> childrenList, Handler handler,
			String action)
	{
		super(context);
		this.groupList = groupList;
		this.childrenList = childrenList;
		this.downloadManager = DownloadManager.shareInstance();
		this.handler = handler;
		this.action = action;
		setUpdateProgress(true);
	}

	/**
	 * 子item的HolderView
	 * 
	 * @author lilijun
	 *
	 */
	public class ChildListViewHolder extends DownloadViewHolder
	{
		/** 显示信息部分、按钮部分 */
		public LinearLayout infoLay, btnLay;

		/** 下载图标 */
		public ImageView icon;

		/** 应用名称 */
		public TextView appName;

		/** 大小 */
		public TextView size;

		/** 下载进度条 */
		public ProgressBar progressBar;

		/** 下拉/上拉图片 */
		public ImageView pullImg;

		/** 取消按钮、详情按钮 */
		public TextView cancle, details;

		/** 下载状态按钮 */
		public DownloadButton downloadButton;

		/** 底部分割线(当心显示 取消、详情 按钮的时候显示 否则隐藏) */
		public View splitLine;

		/** 下载监听器 */
		public DownloadOnclickListener listener;

		public void init()
		{
			infoLay = (LinearLayout) baseView
					.findViewById(R.id.download_soft_list_info_lay);
			btnLay = (LinearLayout) baseView
					.findViewById(R.id.download_soft_list_btn_lay);
			icon = (ImageView) baseView
					.findViewById(R.id.download_soft_list_app_icon);
			appName = (TextView) baseView
					.findViewById(R.id.download_soft_list_app_name);
			size = (TextView) baseView
					.findViewById(R.id.download_soft_list_download_size);
			progressBar = (ProgressBar) baseView
					.findViewById(R.id.download_soft_list_progress);
			pullImg = (ImageView) baseView
					.findViewById(R.id.download_soft_list_pull_img);
			cancle = (TextView) baseView
					.findViewById(R.id.download_soft_list_cancle_btn);
			details = (TextView) baseView
					.findViewById(R.id.download_soft_list_details_btn);
			splitLine = baseView
					.findViewById(R.id.download_soft_list_bottom_split_line);
			downloadButton = (DownloadButton) baseView
					.findViewById(R.id.download_soft_list_download_btn);
			listener = new DownloadOnclickListener(context, action);
		}

		public ChildListViewHolder(View baseView)
		{
			super(baseView);
			init();
		}

	}

	public class GroupListViewHolder
	{

		/** 名称 */
		public TextView groupTitle, groupBtn;

		public void init(View baseView)
		{
			groupTitle = (TextView) baseView
					.findViewById(R.id.download_group_item_name);
			groupBtn = (TextView) baseView
					.findViewById(R.id.download_group_item_btn);
		}

		public GroupListViewHolder(View baseView)
		{
			init(baseView);
		}

	}

	@Override
	protected List<ChildListViewHolder> getHoldersList()
	{
		childHolderList = new ArrayList<DownloadManagerAdapter.ChildListViewHolder>();
		return childHolderList;
	}

	@Override
	protected void setDownloadData(ChildListViewHolder holder, DownloadInfo info)
	{
		holder.downloadButton.setInfo(info.getPackageName());
		holder.listener.setDownloadListenerInfo(info);
		// 设置item的进度条
		holder.progressBar.setProgress(info.getPercent());
		// 设置item的当前下载文件大小
		holder.size.setText(info.getFormatDownloadSize() + "/"
				+ info.getFormatSize());
	}

	@Override
	public int getGroupCount()
	{
		if (groupList == null)
		{
			return 0;
		}
		return groupList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		if (childrenList == null || childrenList.isEmpty())
		{
			return 0;
		}
		return childrenList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return groupList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition)
	{
		return childrenList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent)
	{
		GroupListViewHolder holder = null;
		if (convertView == null)
		{
			convertView = (FrameLayout) View.inflate(context,
					R.layout.download_manager_goup_item_lay, null);
			holder = new GroupListViewHolder(convertView);
			holder.groupBtn.setOnClickListener(installAllOnClickListener);
			convertView.setTag(holder);
		} else
		{
			holder = (GroupListViewHolder) convertView.getTag();
		}
		holder.groupTitle.setText(groupList.get(groupPosition));
		if (groupPosition == 0)
		{
			holder.groupBtn.setVisibility(View.GONE);
		} else if (groupPosition == 1)
		{
			holder.groupBtn.setVisibility(View.VISIBLE);
			holder.groupBtn.setText(context.getResources().getString(
					R.string.install_all));
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent)
	{
		ChildListViewHolder holder = null;
		if (convertView == null)
		{
			convertView = (LinearLayout) View.inflate(context,
					R.layout.download_manager_child_item_lay, null);
			holder = new ChildListViewHolder(convertView);
			convertView.setTag(holder);
			holder.downloadButton.setOnClickListener(holder.listener);
			holder.infoLay.setOnClickListener(infoLayOnClickListener);
			holder.cancle.setOnClickListener(cancleOnClickListener);
			holder.details.setOnClickListener(detailsOnClickListener);
		} else
		{
			holder = (ChildListViewHolder) convertView.getTag();
		}
		if (groupPosition == curExpandableGroupPosition
				&& childPosition == curExpandableChildPosition)
		{
			holder.btnLay.setVisibility(View.VISIBLE);
			holder.pullImg.setImageResource(R.drawable.up_point);
		} else
		{
			holder.btnLay.setVisibility(View.GONE);
			holder.pullImg.setImageResource(R.drawable.down_point);
		}

		addToViewHolder(childrenList.get(groupPosition).get(childPosition)
				.getPackageName(), holder);

		DownloadInfo info = childrenList.get(groupPosition).get(childPosition);
		holder.appName.setText(info.getName());
		holder.size.setText(info.getFormatDownloadSize() + " | "
				+ info.getFormatSize());
		holder.downloadButton.setInfo(info.getPackageName());
		holder.progressBar.setProgress(info.getPercent());
		holder.listener.setDownloadListenerInfo(info);
		imageLoader.displayImage(info.getIconUrl(), holder.icon, options);
		// holder.listener.setAnimationViewInfo(info.getIconUrl(), holder.icon);

		holder.infoLay.setTag(holder);
		holder.infoLay.setTag(R.id.download_soft_list_app_icon, groupPosition);
		holder.infoLay.setTag(R.id.download_soft_list_app_name, childPosition);

		holder.cancle.setTag(info);
		holder.details.setTag(info.getSoftId());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * 下载信息显示区域的点击事件
	 */
	private OnClickListener infoLayOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			ChildListViewHolder holder = (ChildListViewHolder) v.getTag();
			int groupPosition = (Integer) v
					.getTag(R.id.download_soft_list_app_icon);
			int childPosition = (Integer) v
					.getTag(R.id.download_soft_list_app_name);
			if (curExpandableGroupPosition != groupPosition
					|| curExpandableChildPosition != childPosition)
			{
				if (curExpandableChildListViewHolder != null)
				{
					curExpandableChildListViewHolder.btnLay
							.setVisibility(View.GONE);
					curExpandableChildListViewHolder.pullImg
							.setImageResource(R.drawable.down_point);
				}
			}
			if (holder.btnLay.getVisibility() == View.VISIBLE)
			{
				holder.btnLay.setVisibility(View.GONE);
				holder.splitLine.setVisibility(View.GONE);
				holder.pullImg.setImageResource(R.drawable.down_point);
				curExpandableGroupPosition = -1;
				curExpandableChildPosition = -1;
				curExpandableChildListViewHolder = null;
			} else
			{
				holder.btnLay.setVisibility(View.VISIBLE);
				holder.splitLine.setVisibility(View.VISIBLE);
				holder.pullImg.setImageResource(R.drawable.up_point);
				curExpandableGroupPosition = groupPosition;
				curExpandableChildPosition = childPosition;
				curExpandableChildListViewHolder = holder;
			}
		}
	};

	/**
	 * 取消按钮点击事件
	 */
	private OnClickListener cancleOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			downloadManager.deleteDownload((DownloadInfo) v.getTag(), true);
			curExpandableChildListViewHolder = null;
			curExpandableGroupPosition = -1;
			curExpandableChildPosition = -1;
			handler.sendEmptyMessage(DownloadManagerActivity.REGET_LIST_DATA);
		}
	};

	/**
	 * 详情点击事件
	 */
	private OnClickListener detailsOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			long id = Long.valueOf((String) v.getTag());
			DetailsActivity.startDetailsActivityById(context, id, action);
		}
	};

	/**
	 * 全部安装点击事件
	 */
	private OnClickListener installAllOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			for (DownloadInfo downloadInfo : childrenList.get(1))
			{
				SoftwareManager.getInstance().installApkByDownloadInfo(
						downloadInfo);
			}
		}
	};
}
