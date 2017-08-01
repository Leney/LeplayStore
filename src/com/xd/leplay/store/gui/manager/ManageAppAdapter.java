package com.xd.leplay.store.gui.manager;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xd.leplay.store.R;
import com.xd.leplay.store.model.InstalledAppInfo;

/**
 * 管理应用列表Adapter
 * 
 * @author lilijun
 */
public class ManageAppAdapter extends BaseAdapter
{

	private Context mContext;

	private List<InstalledAppInfo> list = null;

	private List<InstalledAppInfo> checkedList = null;

	private Handler mHandler = null;

	public ManageAppAdapter(Context context, List<InstalledAppInfo> list,
			List<InstalledAppInfo> checkedList, Handler handler)
	{
		this.mContext = context;
		this.mHandler = handler;
		this.list = list;
		this.checkedList = checkedList;
	}

	public void setList(List<InstalledAppInfo> list)
	{
		this.list = list;
	}

	public void setCheckedList(List<InstalledAppInfo> checkedList)
	{
		this.checkedList = checkedList;
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		HolderView holderView = null;
		InstalledAppInfo appInfo = (InstalledAppInfo) getItem(position);
		if (convertView == null)
		{
			convertView = View.inflate(mContext, R.layout.manage_app_adapter,
					null);
			holderView = new HolderView();
			holderView.icon = (ImageView) convertView
					.findViewById(R.id.manage_app_icon);
			holderView.name = (TextView) convertView
					.findViewById(R.id.manage_app_name);
			holderView.size = (TextView) convertView
					.findViewById(R.id.manage_app_size);
			holderView.checkBox = (CheckBox) convertView
					.findViewById(R.id.manage_app_checkBox);
			holderView.checkBox.setOnClickListener(checkBoxOnClickListener);
			convertView.setOnClickListener(checkBoxOnClickListener);
			convertView.setTag(holderView);
		} else
		{
			holderView = (HolderView) convertView.getTag();
		}
		holderView.icon.setImageDrawable(appInfo.getIcon());
		holderView.name.setText(appInfo.getName());
		holderView.size.setText(String.format(mContext.getResources()
				.getString(R.string.installed_version_size), appInfo
				.getFormatAppSize()));
		holderView.checkBox.setChecked(appInfo.isChecked());
		convertView.setTag(R.id.manage_app_checkBox, appInfo);
		holderView.checkBox.setTag(R.id.manage_app_checkBox, appInfo);
		return convertView;
	}

	/**
	 * 条目点击事件
	 */
	private OnClickListener checkBoxOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			InstalledAppInfo appInfo = (InstalledAppInfo) v
					.getTag(R.id.manage_app_checkBox);
			if (appInfo.isChecked())
			{
				// 已选择状态 则设置成非选择状态
				appInfo.setChecked(false);
				checkedList.remove(appInfo);
			} else
			{
				// 非选择状态 则设置成选择状态
				appInfo.setChecked(true);
				checkedList.add(appInfo);
			}
			notifyDataSetChanged();
			mHandler.sendEmptyMessage(ManageAppActivity.CHECK_STATE_CHANGED_MSG);
		}
	};

	private class HolderView
	{
		ImageView icon;
		TextView name, size;
		CheckBox checkBox;
	}

}
