package com.xd.leplay.store.gui.treasure;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.leplay.store.R;
import com.xd.leplay.store.model.proto.Uac.Detail;
import com.xd.leplay.store.model.proto.Uac.DetailContent;

/**
 * 赚取金币记录列表的Adapter
 * 
 * @author luoxingxing
 *
 */
public class GainCoinRecordDetailAdapter extends BaseExpandableListAdapter
{

	private Context mContext;

	private List<Detail> groupList;

	private List<List<DetailContent>> childrenList;

	private String type;

	public GainCoinRecordDetailAdapter(Context context, List<Detail> groupList,
			List<List<DetailContent>> childrenList, String type)
	{
		this.mContext = context;
		this.groupList = groupList;
		this.childrenList = childrenList;
		this.type = type;
	}

	/** grop 的HolderView */
	public class GroupListViewHolder
	{
		/** 创建时间 */
		public TextView createDate;

		/** 分割线 */
		public View topSplitLine;

		public void init(View baseView)
		{
			createDate = (TextView) baseView
					.findViewById(R.id.coin_record_detail_adapter_date);
			topSplitLine = baseView
					.findViewById(R.id.coin_record_detail_group_top_split_line);

		}

		public GroupListViewHolder(View baseView)
		{
			init(baseView);
		}

	}

	/** 子item的HolderView */

	public class ChildListViewHolder
	{
		/** 奖励详情描述 */
		public TextView rewardDetail;

		/** 奖励收入 */
		public TextView rewardNum;

		/** 分割线 */
		public View splitLine;

		public void init(View baseView)
		{
			rewardDetail = (TextView) baseView
					.findViewById(R.id.gain_record_item_detail_des);
			rewardNum = (TextView) baseView
					.findViewById(R.id.gain_record_item_detail_coin);
			splitLine = baseView
					.findViewById(R.id.gain_record_item_detail_child_split_line);
		}

		public ChildListViewHolder(View baseView)
		{
			init(baseView);
		}
	}

	@Override
	public int getGroupCount()
	{
		return groupList.size();
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
		Detail detail = (Detail) getGroup(groupPosition);
		if (convertView == null)
		{
			convertView = (LinearLayout) View.inflate(mContext,
					R.layout.gain_coin_record_detail_adapter, null);
			holder = new GroupListViewHolder(convertView);
			convertView.setTag(holder);
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

		holder.createDate.setText(detail.getCreateTime());
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent)
	{
		ChildListViewHolder holder = null;
		DetailContent detailContent = (DetailContent) getChild(groupPosition,
				childPosition);
		if (convertView == null)
		{
			convertView = (LinearLayout) View.inflate(mContext,
					R.layout.gain_coin_record_detail_item, null);
			holder = new ChildListViewHolder(convertView);
			convertView.setTag(holder);

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
		holder.rewardDetail.setText(detailContent.getSource());
		if ("13".equals(type))
		{
			holder.rewardNum.setText("+" + " " + (detailContent.getCorn())
					/ 100.00+"元");
		} else
			holder.rewardNum.setText("+" + " " + detailContent.getCorn());

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return false;
	}

}
