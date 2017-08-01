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
import com.xd.leplay.store.model.proto.Uac.DownloadContent;
import com.xd.leplay.store.model.proto.Uac.DownloadDetail;

/**
 * 获取下载应用列表的Adapter
 * 
 * @author luoxingxing
 *
 */
public class DownloadRewardRecordAdapter extends BaseAdapter
{
	private Context mContext;

	private List<DownloadDetail> recordList = null;

	private LayoutParams params = null;

	public DownloadRewardRecordAdapter(Context context, List<DownloadDetail> list)
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
		DownloadDetail detail = (DownloadDetail) getItem(position);
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
		for (DownloadContent content : detail.getContentList())
		{
			View view = View.inflate(mContext, R.layout.gain_coin_record_item,
					null);
			TextView appName = (TextView) view
					.findViewById(R.id.gain_record_item_des);
			TextView gainCoin = (TextView) view
					.findViewById(R.id.gain_record_item_coin);
			appName.setText(content.getAppName());
			if (position == 0)
			{
				appName.setTextColor(mContext.getResources().getColor(
						R.color.list_name_color));
			} else
			{
				appName.setTextColor(mContext.getResources().getColor(
						R.color.list_soft_describe_color));
			}
			gainCoin.setVisibility(View.GONE);
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
