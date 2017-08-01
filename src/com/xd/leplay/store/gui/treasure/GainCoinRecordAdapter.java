package com.xd.leplay.store.gui.treasure;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xd.leplay.store.R;
import com.xd.leplay.store.model.proto.Uac.Detail;
import com.xd.leplay.store.model.proto.Uac.DetailContent;

/**
 * 赚取金币记录列表的Adapter
 * 
 * @author lilijun
 *
 */
public class GainCoinRecordAdapter extends BaseAdapter
{
	private Context mContext;

	private List<Detail> recordList = null;

	private LayoutParams params = null;

	public GainCoinRecordAdapter(Context context, List<Detail> list)
	{
		this.mContext = context;
		this.recordList = list;
		this.params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
	}

	@Override
	public int getCount()
	{
		return recordList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return recordList.get(position);
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
		Detail detail = (Detail) getItem(position);
		if (convertView == null)
		{
			holderView = new HolderView();
			convertView = View.inflate(mContext,
					R.layout.gain_coin_record_adapter, null);
			holderView.topLine = (FrameLayout) convertView
					.findViewById(R.id.coin_record_top_line);
			holderView.date = (TextView) convertView
					.findViewById(R.id.coin_record_adapter_date);
			holderView.contentLay = (LinearLayout) convertView
					.findViewById(R.id.coin_record_adapter_content_lay);
			convertView.setTag(holderView);
		} else
		{
			holderView = (HolderView) convertView.getTag();
		}
		holderView.contentLay.removeAllViews();
		if (position == 0)
		{
			holderView.date
					.setBackgroundResource(R.drawable.blue_round_corner_bg_shape);
			holderView.date.setTextColor(mContext.getResources().getColor(
					R.color.white));
		} else
		{
			holderView.date
					.setBackgroundResource(R.drawable.gray_round_corner_bg_shape);
			holderView.date.setTextColor(mContext.getResources().getColor(
					R.color.list_soft_describe_color));
		}
		holderView.date.setText(detail.getCreateTime());
		for (DetailContent content : detail.getContentList())
		{
			View view = View.inflate(mContext, R.layout.gain_coin_record_item,
					null);
			TextView gainCoinDes = (TextView) view
					.findViewById(R.id.gain_record_item_des);
			TextView gainCoin = (TextView) view
					.findViewById(R.id.gain_record_item_coin);
			gainCoinDes.setText(content.getSource());
			if (position == 0)
			{
				gainCoinDes.setTextColor(mContext.getResources().getColor(
						R.color.list_name_color));
			} else
			{
				gainCoinDes.setTextColor(mContext.getResources().getColor(
						R.color.list_soft_describe_color));
			}
			if (content.getCorn() >= 0)
			{
				// 获得金币
				gainCoin.setTextColor(mContext.getResources().getColor(
						R.color.redeem_code_text_color));
				gainCoin.setText("+ " + content.getCorn());
			} else
			{
				// 支出金币
				gainCoin.setTextColor(mContext.getResources().getColor(
						R.color.list_soft_describe_color));
				gainCoin.setText("- " + Math.abs(content.getCorn()));
			}
			holderView.contentLay.addView(view, params);
		}
		return convertView;
	}

	class HolderView
	{
		FrameLayout topLine;
		TextView date;
		LinearLayout contentLay;
	}

}
