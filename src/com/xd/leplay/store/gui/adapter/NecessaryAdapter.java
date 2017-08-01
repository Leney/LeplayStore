package com.xd.leplay.store.gui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.leplay.store.R;
import com.xd.leplay.store.gui.adapter.NecessaryAdapter.ChildListViewHolder;
import com.xd.leplay.store.gui.details.DetailsActivity;
import com.xd.leplay.store.gui.download.adapter.DownloadExpandableBaseAdapter;
import com.xd.leplay.store.gui.download.adapter.DownloadViewHolder;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.view.download.DownloadOnclickListener;
import com.xd.leplay.store.view.download.DownloadProgressButton;

public class NecessaryAdapter extends
		DownloadExpandableBaseAdapter<ChildListViewHolder>
{

	private List<ChildListViewHolder> childHolderList;

	private List<String> groupList = null;

	private List<List<ListAppInfo>> childrenList = null;

	private String action = "";

	public NecessaryAdapter(Context context, List<String> groupList,
			List<List<ListAppInfo>> childrenList, String action)
	{
		super(context);
		this.groupList = groupList;
		this.childrenList = childrenList;
		this.action = action;
		setUpdateProgress(false);

	}

	/** grop 的HolderView */
	public class GroupListViewHolder
	{

		/** 名称 */
		public TextView groupTitle;

		public void init(View baseView)
		{
			groupTitle = (TextView) baseView
					.findViewById(R.id.necessary_group_item_name);
		}

		public GroupListViewHolder(View baseView)
		{
			init(baseView);
		}
	}

	/**
	 * 子item的HolderView
	 * 
	 * @author lilijun
	 *
	 */
	public class ChildListViewHolder extends DownloadViewHolder
	{
		/** 应用图标 */
		public ImageView icon;

		/** 应用名称 */
		public TextView appName;

		/** 下载次数和软件大小 */
		public TextView downloadCountAndSize;

		/** 下载状态按钮 */
		public DownloadProgressButton downloadStateButton;

		/** 下载监听器 */
		public DownloadOnclickListener listener;

		/** 分割线 */
		public View splitLine;

		public void init()
		{
			icon = (ImageView) baseView
					.findViewById(R.id.necessary_child_app_icon);
			appName = (TextView) baseView
					.findViewById(R.id.necessary_child_app_name);
			downloadCountAndSize = (TextView) baseView
					.findViewById(R.id.necessary_child_download_count_and_size);
			splitLine = baseView.findViewById(R.id.necessary_child_split_line);
			downloadStateButton = (DownloadProgressButton) baseView
					.findViewById(R.id.necessary_child_download_btn);
			listener = new DownloadOnclickListener(context, action);
		}

		public ChildListViewHolder(View baseView)
		{
			super(baseView);
			init();
		}
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent)
	{
		GroupListViewHolder holder = null;
		if (convertView == null)
		{
			convertView = (FrameLayout) View.inflate(context,
					R.layout.necessary_goup_item_lay, null);
			holder = new GroupListViewHolder(convertView);
			convertView.setTag(holder);
		} else
		{
			holder = (GroupListViewHolder) convertView.getTag();
		}

		holder.groupTitle.setText(groupList.get(groupPosition));
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent)
	{
		ChildListViewHolder holder = null;
		ListAppInfo appInfo = (ListAppInfo) getChild(groupPosition,
				childPosition);
		if (convertView == null)
		{
			convertView = (LinearLayout) View.inflate(context,
					R.layout.necessary_child_item_lay, null);
			holder = new ChildListViewHolder(convertView);
			convertView.setTag(holder);
			holder.downloadStateButton.downloadbtn
					.setOnClickListener(holder.listener);
			convertView.setOnClickListener(itemClickListener);
		} else
		{

			holder = (ChildListViewHolder) convertView.getTag();
		}
		if (childPosition == 0)
		{
			holder.splitLine.setVisibility(View.GONE);
		} else
		{
			holder.splitLine.setVisibility(View.VISIBLE);
		}
		if (appInfo.getName().equals("火辣健身"))
		{
			DLog.e("lilijun", "softId------>>>" + appInfo.getSoftId());
			DLog.e("lilijun",
					"packageName--------->>>" + appInfo.getPackageName());
		}

		convertView.setTag(R.id.necessary_child_app_icon, appInfo.getSoftId());

		addToViewHolder(appInfo.getPackageName(), holder);
		imageLoader.displayImage(appInfo.getIconUrl(), holder.icon, options);
		holder.appName.setText(appInfo.getName());
		holder.downloadCountAndSize.setText(appInfo.getFormatDownloadCount()
				+ context.getResources().getString(R.string.count_download)
				+ " | " + appInfo.getFormatSize());

		holder.downloadStateButton.setInfo(appInfo.getPackageName());
		holder.listener.setDownloadListenerInfo(appInfo);

		return convertView;
	}

	/**
	 * 整个item的点击事件
	 */
	private OnClickListener itemClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			long id = (Long) v.getTag(R.id.necessary_child_app_icon);
			DLog.e("lilijun", "点击id------>>>" + id);
			DetailsActivity.startDetailsActivityById(context, id, action);
		}
	};

	@Override
	public Object getChild(int groupPosition, int childPosition)
	{
		return childrenList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		if (childrenList == null)
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
	public int getGroupCount()
	{
		return groupList.size();
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return false;
	}

	@Override
	protected List<ChildListViewHolder> getHoldersList()
	{
		childHolderList = new ArrayList<NecessaryAdapter.ChildListViewHolder>();
		return childHolderList;
	}

	@Override
	protected void setDownloadData(ChildListViewHolder holder, DownloadInfo info)
	{
		holder.downloadStateButton.setInfo(info.getPackageName());
		holder.listener.setDownloadListenerInfo(info);
	}
}
