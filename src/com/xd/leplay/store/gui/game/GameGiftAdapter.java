package com.xd.leplay.store.gui.game;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.download.DownloadInfo;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.details.DetailsActivity;
import com.xd.leplay.store.gui.download.adapter.DownloadExpandableBaseAdapter;
import com.xd.leplay.store.gui.download.adapter.DownloadViewHolder;
import com.xd.leplay.store.gui.game.GameGiftAdapter.GroupListViewHolder;
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.model.GiftInfo;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.model.proto.App.GameBag;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.download.DownloadOnclickListener;
import com.xd.leplay.store.view.download.DownloadProgressButton;

/**
 * 游戏列表列表的Adapter
 * 
 * @author lilijun
 *
 */
public class GameGiftAdapter extends
		DownloadExpandableBaseAdapter<GroupListViewHolder>
{

	private List<GroupListViewHolder> groupHolderList;

	private List<ListAppInfo> groupList = null;

	private List<List<GameBag>> childrenList = null;

	private Context mContext = null;

	private LoginUserInfoManager loginUserInfoManager = null;

	private Handler mHandler = null;

	private String action = "";

	public GameGiftAdapter(Context context, List<ListAppInfo> groupList,
			List<List<GameBag>> childrenList, Handler handler, String action)
	{
		super(context);
		this.mContext = context;
		this.groupList = groupList;
		this.childrenList = childrenList;
		this.loginUserInfoManager = LoginUserInfoManager.getInstance();
		this.mHandler = handler;
		this.action = action;
		setUpdateProgress(false);

	}

	/** grop 的HolderView */
	public class GroupListViewHolder extends DownloadViewHolder
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

		/** 顶部分割线 */
		public View topSplitLine;

		public void init()
		{
			icon = (ImageView) baseView
					.findViewById(R.id.game_gift_group_app_icon);
			appName = (TextView) baseView
					.findViewById(R.id.game_gift_group_app_name);
			downloadCountAndSize = (TextView) baseView
					.findViewById(R.id.game_gift_group_download_count_and_size);
			topSplitLine = baseView
					.findViewById(R.id.game_gift_group_top_split_line);
			downloadStateButton = (DownloadProgressButton) baseView
					.findViewById(R.id.game_gift_group_download_btn);
			listener = new DownloadOnclickListener(context, action);
		}

		public GroupListViewHolder(View baseView)
		{
			super(baseView);
			init();
		}
	}

	/**
	 * 子item的HolderView
	 * 
	 * @author lilijun
	 *
	 */
	public class ChildListViewHolder
	{
		/** 礼包名称 */
		public TextView giftName;

		/** 礼包描述 */
		public TextView giftDescrible;

		/** 抢礼包、复制按钮 */
		public Button getGiftBtn, copyGiftBtn;

		/** 分割线 */
		public View splitLine;

		public void init(View baseView)
		{
			giftName = (TextView) baseView
					.findViewById(R.id.game_gift_child_gift_name);
			giftDescrible = (TextView) baseView
					.findViewById(R.id.game_gift_child_gift_decrible);
			getGiftBtn = (Button) baseView
					.findViewById(R.id.game_gift_child_get_gift_btn);
			copyGiftBtn = (Button) baseView
					.findViewById(R.id.game_gift_child_copy_gift_btn);
			splitLine = baseView.findViewById(R.id.game_gift_child_split_line);
		}

		public ChildListViewHolder(View baseView)
		{
			init(baseView);
		}
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent)
	{
		GroupListViewHolder holder = null;
		ListAppInfo appInfo = (ListAppInfo) getGroup(groupPosition);
		if (convertView == null)
		{
			convertView = (LinearLayout) View.inflate(context,
					R.layout.game_gift_group_item_lay, null);
			holder = new GroupListViewHolder(convertView);
			convertView.setTag(holder);
			holder.downloadStateButton.downloadbtn
					.setOnClickListener(holder.listener);
			convertView.setOnClickListener(groupItemOnClickListener);
		} else
		{
			holder = (GroupListViewHolder) convertView.getTag();
		}
		if (groupPosition == 0)
		{
			holder.topSplitLine.setVisibility(View.GONE);
		} else
		{
			holder.topSplitLine.setVisibility(View.VISIBLE);
		}
		convertView.setTag(R.id.game_gift_group_app_name, appInfo.getSoftId());
		addToViewHolder(appInfo.getPackageName(), holder);
		imageLoader.displayImage(appInfo.getIconUrl(), holder.icon, options);
		holder.appName.setText(appInfo.getName());
		holder.downloadCountAndSize.setText(appInfo.getFormatDownloadCount()
				+ context.getResources().getString(R.string.count_download)
				+ "|" + appInfo.getFormatSize());
		holder.downloadStateButton.setInfo(appInfo.getPackageName());
		holder.listener.setDownloadListenerInfo(appInfo);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent)
	{
		ChildListViewHolder holder = null;
		ListAppInfo appInfo = (ListAppInfo) getGroup(groupPosition);
		GameBag gift = (GameBag) getChild(groupPosition, childPosition);
		if (convertView == null)
		{
			convertView = (LinearLayout) View.inflate(context,
					R.layout.game_gift_child_item_lay, null);
			holder = new ChildListViewHolder(convertView);
			convertView.setTag(holder);
			convertView.setOnClickListener(itemOnClickListener);
			holder.getGiftBtn.setOnClickListener(getCodeBtnOnClickLinstener);
			holder.copyGiftBtn.setOnClickListener(copyBtnOnClickLinstener);
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
		holder.giftName.setText(gift.getBagName());
		holder.giftDescrible.setText(gift.getBagContent());
		if (loginUserInfoManager.isHaveUserLogin())
		{
			if (!loginUserInfoManager.getLoginedUserInfo().getGiftIdList()
					.contains(gift.getBagId()))
			{
				// 显示抢礼包
				holder.getGiftBtn.setVisibility(View.VISIBLE);
				holder.copyGiftBtn.setVisibility(View.GONE);
			} else
			{
				// 显示复制按钮
				holder.getGiftBtn.setVisibility(View.GONE);
				holder.copyGiftBtn.setVisibility(View.VISIBLE);
			}
		} else
		{
			// 显示抢礼包
			holder.getGiftBtn.setVisibility(View.VISIBLE);
			holder.copyGiftBtn.setVisibility(View.GONE);
		}

		convertView.setTag(R.id.game_gift_child_gift_name, appInfo.getSoftId());
		convertView.setTag(R.id.game_gift_child_gift_decrible, gift.getBagId());

		holder.getGiftBtn.setTag(appInfo.getSoftId());
		holder.getGiftBtn.setTag(R.id.game_gift_child_get_gift_btn,
				gift.getBagId());
		holder.copyGiftBtn.setTag(gift.getBagId());

		return convertView;
	}

	private OnClickListener groupItemOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			long softId = (Long) v.getTag(R.id.game_gift_group_app_name);
			DetailsActivity.startDetailsActivityById(context, softId, action);
		}
	};

	private OnClickListener itemOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			long gameId = (Long) v.getTag(R.id.game_gift_child_gift_name);
			int giftId = (Integer) v.getTag(R.id.game_gift_child_gift_decrible);
			Message msg = new Message();
			msg.what = GameGiftFragment.HANDLER_INTENT_GIFT_DETAIL;
			Bundle data = new Bundle();
			data.putLong("game_id", gameId);
			data.putInt("show_gift_id", giftId);
			msg.setData(data);
			mHandler.sendMessage(msg);
		}
	};

	/**
	 * 抢礼包按钮点击事件
	 */
	private OnClickListener getCodeBtnOnClickLinstener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if (LoginUserInfoManager.getInstance().isHaveUserLogin())
			{
				long gameId = (Long) v.getTag();
				int giftId = (Integer) v
						.getTag(R.id.game_gift_child_get_gift_btn);
				Message msg = new Message();
				msg.what = GameGiftFragment.HANDLER_GET_GIFT;
				Bundle data = new Bundle();
				data.putLong("game_soft_id", gameId);
				data.putInt("gift_id", giftId);
				msg.setData(data);
				mHandler.sendMessage(msg);
			} else
			{
				// 跳转到登录界面
				LoginActivity.startLoginActivity(mContext, action);
			}
		}
	};
	/**
	 * 复制按钮点击事件
	 */
	private OnClickListener copyBtnOnClickLinstener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			int giftId = (Integer) v.getTag();
			GiftInfo giftInfo = LoginUserInfoManager.getInstance()
					.getLoginedUserInfo().getGiftList().get(giftId);
			// 复制到粘贴板
			boolean isCopy = ToolsUtil.copy(giftInfo.getCode(), context);
			if (isCopy)
			{
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.copy_code_success), Toast.LENGTH_SHORT)
						.show();
			} else
			{
				Toast.makeText(
						mContext,
						mContext.getResources().getString(R.string.copy_failed),
						Toast.LENGTH_SHORT).show();
			}
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
	protected List<GroupListViewHolder> getHoldersList()
	{
		groupHolderList = new ArrayList<GameGiftAdapter.GroupListViewHolder>();
		return groupHolderList;
	}

	@Override
	protected void setDownloadData(GroupListViewHolder holder, DownloadInfo info)
	{
		holder.downloadStateButton.setInfo(info.getPackageName());
		holder.listener.setDownloadListenerInfo(info);
	}

}
