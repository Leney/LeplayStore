package com.xd.leplay.store.gui.treasure;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xd.base.util.DLog;
import com.xd.leplay.store.R;
import com.xd.leplay.store.model.proto.Uac.WithdrawingInfo;

public class GotCashRewardAdapter extends BaseAdapter
{
	private Context mContext;

	private List<WithdrawingInfo> rewardList;

	public GotCashRewardAdapter(Context context, List<WithdrawingInfo> list)
	{
		this.mContext = context;
		this.rewardList = list;
	}

	@Override
	public int getCount()
	{
		return rewardList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return rewardList.get(position);
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
		if (convertView == null)
		{
			holderView = new HolderView();
			convertView = View.inflate(mContext,
					R.layout.got_cash_reward_adapter, null);
			holderView.date = (TextView) convertView
					.findViewById(R.id.got_cash_reward_date);
			holderView.name = (TextView) convertView
					.findViewById(R.id.got_cash_reward_name);
			holderView.money = (TextView) convertView
					.findViewById(R.id.got_cash_reward_money);
			holderView.status = (TextView) convertView
					.findViewById(R.id.got_cash_reward_status);
			holderView.describe = (TextView) convertView
					.findViewById(R.id.got_cash_reward_describe);
			holderView.emptyView = convertView
					.findViewById(R.id.got_cash_reward_adapter_empty_view);
			convertView.setTag(holderView);
		} else
		{
			holderView = (HolderView) convertView.getTag();
		}
		WithdrawingInfo reward = rewardList.get(position);
		holderView.date.setText(reward.getCreateTime());
		DLog.i("lilijun", "时间---------->>>" + reward.getCreateTime());
		holderView.name.setText(reward.getPayPlatform());
		holderView.money.setText(String.format(mContext.getResources()
				.getString(R.string.yuan), reward.getAmount() + ""));
		if (reward.getStatus() == 2)
		{
			// 审核不通过(未到账)
			holderView.status.setText(mContext.getResources().getString(
					R.string.no_money));
			holderView.status
					.setBackgroundResource(R.drawable.got_cash_status_btn_bg_shape2);
		} else if (reward.getStatus() == 1)
		{
			// 已打款
			holderView.status.setText(mContext.getResources().getString(
					R.string.got_money));
			holderView.status
					.setBackgroundResource(R.drawable.got_cash_status_btn_bg_shape1);
		} else if (reward.getStatus() == 0)
		{
			// 待审核(审核中)
			holderView.status.setText(mContext.getResources().getString(
					R.string.money_checking));
			holderView.status
					.setBackgroundResource(R.drawable.got_cash_status_btn_bg_shape2);
		}
		if ("".equals(reward.getRemark().trim()))
		{
			// 没有说明
			holderView.describe.setVisibility(View.GONE);
			holderView.emptyView.setVisibility(View.GONE);
		} else
		{
			// 有说明
			holderView.describe.setText(reward.getRemark());
			holderView.describe.setVisibility(View.VISIBLE);
			holderView.emptyView.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	class HolderView
	{
		TextView date, name, money, status, describe;
		View emptyView;
	}

}
