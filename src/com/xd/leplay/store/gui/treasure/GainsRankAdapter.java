package com.xd.leplay.store.gui.treasure;

import java.util.List;

import com.xd.leplay.store.R;
import com.xd.leplay.store.model.proto.Uac.RankInfo;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 收入排行Adapter
 * 
 * @author lilijun
 *
 */
public class GainsRankAdapter extends BaseAdapter
{
	private Context mContext;

	private List<RankInfo> rankInfos;

	private RankInfo userRankInfo;

	public GainsRankAdapter(Context context, List<RankInfo> list)
	{
		this.mContext = context;
		this.rankInfos = list;
	}

	public void setUserRankInfo(RankInfo userRankInfo)
	{
		this.userRankInfo = userRankInfo;
	}

	@Override
	public int getCount()
	{
		return rankInfos.size();
	}

	@Override
	public Object getItem(int position)
	{
		return rankInfos.get(position);
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
		RankInfo rankInfo = (RankInfo) getItem(position);
		if (convertView == null)
		{
			holderView = new HolderView();
			convertView = View.inflate(mContext, R.layout.gain_rank_adapter,
					null);
			holderView.itemLay = (LinearLayout) convertView
					.findViewById(R.id.gain_rank_adapter_item_lay);
			holderView.userRankNum = (TextView) convertView
					.findViewById(R.id.gain_rank_adapter_rank_num);
			holderView.userName = (TextView) convertView
					.findViewById(R.id.gain_rank_adapter_user_name);
			holderView.userMoney = (TextView) convertView
					.findViewById(R.id.gain_rank_adapter_user_money);
			convertView.setTag(holderView);
		} else
		{
			holderView = (HolderView) convertView.getTag();
		}
		// holderView.userRankNum.setText(position + 4 + "");
		holderView.userRankNum.setText(rankInfo.getRankNo() + "");
		holderView.userName.setText(rankInfo.getUserName());
		holderView.userMoney.setText(rankInfo.getWithdrawing() + "");
		if (position % 2 == 0)
		{
			holderView.itemLay.setBackgroundColor(mContext.getResources()
					.getColor(R.color.white));
		} else
		{
			holderView.itemLay.setBackgroundColor(mContext.getResources()
					.getColor(R.color.gray_btn_normal_color));
		}
		if (userRankInfo != null)
		{
			if (rankInfo.getRankNo() == userRankInfo.getRankNo())
			{
				holderView.userRankNum
						.setTextColor(Color.parseColor("#e83636"));
				holderView.userName.setTextColor(Color.parseColor("#e83636"));
				holderView.userMoney.setTextColor(Color.parseColor("#e83636"));
			} else
			{
				holderView.userRankNum
						.setTextColor(Color.parseColor("#666666"));
				holderView.userName.setTextColor(Color.parseColor("#666666"));
				holderView.userMoney.setTextColor(Color.parseColor("#666666"));
			}
		} else
		{
			holderView.userRankNum.setTextColor(Color.parseColor("#666666"));
			holderView.userName.setTextColor(Color.parseColor("#666666"));
			holderView.userMoney.setTextColor(Color.parseColor("#666666"));
		}
		return convertView;
	}

	class HolderView
	{
		LinearLayout itemLay;
		TextView userRankNum, userName, userMoney;
	}

}
