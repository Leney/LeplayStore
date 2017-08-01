package com.xd.leplay.store.gui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.leplay.store.R;
import com.xd.leplay.store.gui.details.DetailsActivity;
import com.xd.leplay.store.gui.download.adapter.DownloadBaseAdapter;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.view.MarketListView;
import com.xd.leplay.store.view.download.DownloadOnclickListener;
import com.xd.leplay.store.view.download.DownloadProgressButton;

/**
 * 搜索结果Adapter
 * 
 * @author lilijun
 *
 */
public class SearchResultAdapter extends DownloadBaseAdapter<MarketListView>
{
	// private DataCollectInfo datainfo = null;

	// public SoftListAdapter(Context context, MarketListView listView,
	// ArrayList<AppInfo> appInfos, DataCollectInfo datainfo)
	// {
	// super(context, listView, appInfos);
	// this.datainfo = datainfo;
	// setUpdateProgress(true);
	// }

	private Context mContext;

	private String action = "";

	public SearchResultAdapter(Context context, MarketListView listView,
			ArrayList<ListAppInfo> appInfos, String action)
	{
		super(context, listView, appInfos);
		this.mContext = context;
		this.action = action;
		// this.datainfo = datainfo;
		setUpdateProgress(false);
	}

	public class SoftListViewHolder
	{
		/** 下载图标 */
		public ImageView icon;

		/** 应用名称 */
		public TextView appName;

		/** 下载次数和软件大小 */
		public TextView downloadCountAndSize;

		/** 星级 */
		public RatingBar star;

		/** 赠送金币数量 */
		public TextView coin;

		/** 小编推荐 */
		public TextView decribe;

		/** 下载状态按钮 */
		public DownloadProgressButton downloadStateButton;

		/** 下载监听器 */
		public DownloadOnclickListener listener;

		public void init(View baseView)
		{
			icon = (ImageView) baseView
					.findViewById(R.id.search_result_list_app_icon);
			appName = (TextView) baseView
					.findViewById(R.id.search_result_list_app_name);
			downloadCountAndSize = (TextView) baseView
					.findViewById(R.id.search_result_download_count_and_size_text);
			star = (RatingBar) baseView
					.findViewById(R.id.search_result_list_app_star);
			coin = (TextView) baseView
					.findViewById(R.id.search_result_list_coin_text);
			decribe = (TextView) baseView
					.findViewById(R.id.search_result_list_describe_text);
			downloadStateButton = (DownloadProgressButton) baseView
					.findViewById(R.id.search_result_list_download_btn);
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
		ListAppInfo info = appInfos.get(position);
		if (convertView == null)
		{
			convertView = (LinearLayout) View.inflate(context,
					R.layout.search_result_adapter, null);
			holder = new SoftListViewHolder(convertView);
			convertView.setTag(holder);
			holder.downloadStateButton.downloadbtn
					.setOnClickListener(holder.listener);
			convertView.setOnClickListener(itemClickListener);
		} else
		{
			holder = (SoftListViewHolder) convertView.getTag();
		}

		convertView.setTag(R.id.soft_list_app_icon, info.getSoftId());

		// datainfo.setPosition(position + "");
		holder.appName.setText(info.getName());
		holder.star.setRating(info.getStarLevel());
		holder.downloadCountAndSize.setText(info.getFormatDownloadCount()
				+ context.getResources().getString(R.string.count_download)
				+ " | " + info.getFormatSize());
		holder.decribe.setText(info.getRecommendDescribe());
		holder.downloadStateButton.setInfo(info.getPackageName());

		holder.listener.setDownloadListenerInfo(info);
		imageLoader.displayImage(info.getIconUrl(), holder.icon, options);
		if (info.getCoin() > 0)
		{
			holder.coin.setText(String.format(mContext.getResources()
					.getString(R.string.award_coins), info.getCoin() + ""));
			holder.coin.setVisibility(View.VISIBLE);
		} else
		{
			holder.coin.setVisibility(View.GONE);
		}
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
			DLog.i("lilijun", "点击了列表条目！！！");
			long id = (Long) v.getTag(R.id.soft_list_app_icon);
			DetailsActivity.startDetailsActivityById(mContext, id, action);
		}
	};

	@Override
	protected void refreshData(DownloadInfo info, View view)
	{
		SoftListViewHolder viewHolder = (SoftListViewHolder) view.getTag();
		if (viewHolder != null)
		{
			viewHolder.downloadStateButton.setInfo(info.getPackageName());
			viewHolder.listener.setDownloadListenerInfo(info);
		}
		// else
		// {
		// Toast.makeText(context, "SoftListViewHolder = null",
		// Toast.LENGTH_SHORT).show();
		// }

	}
}
