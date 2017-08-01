package com.xd.leplay.store.gui.treasure;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.model.proto.Uac.DownloadRank;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.CustomImageView;
import com.xd.leplay.store.view.MarketListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 普通列表通用Adapter
 * 
 * @author luoxingxing
 *
 */
public class DownLoadRewardRankAdapter extends BaseAdapter
{

	private Context mContext;

	private String action = "";
	private ArrayList<DownloadRank> downloadRankInfos;
	/** 下载列表加载图片的配置 */
	private DisplayImageOptions options = null;

	/** 图片加载器 */
	private ImageLoaderManager imageLoader = null;
	public DownLoadRewardRankAdapter(Context context, MarketListView listView,
			ArrayList<DownloadRank> downloadRankInfos, String action)
	{
		this.downloadRankInfos=downloadRankInfos;
		this.mContext = context;
		this.action = action;
		imageLoader = ImageLoaderManager.getInstance();
		options = DisplayUtil.getUserIconImageLoaderOptions();
	}

	public class DownLoadRankViewHolder
	{
		/** 下载用户图标 */
		public CustomImageView userIcon;

		/**名次排名图标*/
		public ImageView rankIcon;
		
		/** 名次序号*/
		public TextView rankNum;
		/** 用户昵称 */
		public TextView userName;

		/** 下载应用数 */
		public TextView decribe;

		/** 下载金币数 */
		public TextView gainCoins;

		

		public void init(View baseView)
		{
			userIcon = (CustomImageView) baseView.findViewById(R.id.download_reward_rank_user_icon);
			rankIcon=(ImageView) baseView.findViewById(R.id.download_reward_rank_icon);
			rankNum=(TextView)baseView.findViewById(R.id.download_reward_rank_num);
			userName = (TextView) baseView.findViewById(R.id.download_reward_rank_user_name);
			decribe = (TextView) baseView
					.findViewById(R.id.download_reward_rank_app_sum);
			gainCoins=(TextView) baseView
					.findViewById(R.id.download_reward_rank_gain_coins);
		}

		public DownLoadRankViewHolder(View baseView)
		{
			init(baseView);
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		DownLoadRankViewHolder holder = null;
		DownloadRank info = downloadRankInfos.get(position);
		if (convertView == null)
		{
			convertView = (LinearLayout) View.inflate(mContext,
					R.layout.download_reward_rank_list_adapter, null);
			holder = new DownLoadRankViewHolder(convertView);
			convertView.setTag(holder);
		} else
		{
			holder = (DownLoadRankViewHolder) convertView.getTag();
		}
        
		holder.userName.setText(info.getUserName());
		holder.decribe.setText(String.format(
				mContext.getResources().getString(R.string.download_reward_rank_app_sum), " "
						+ ToolsUtil.getFormatPraiseCount(info.getDownloadApps()) + " "));
		holder.gainCoins.setText(String.format(
				mContext.getResources().getString(R.string.download_reward_rank_gain_coins), " "
						+ ToolsUtil.getFormatPraiseCount(info.getDownloadCoin()) + " "));

		imageLoader.displayImage(info.getPicUrl(), holder.userIcon, options);
		Log.i("luoxingxing", "no"+info.getRankNo());
		if(position==0){
			holder.rankNum.setVisibility(View.GONE);
			holder.rankIcon.setVisibility(View.VISIBLE);
			holder.rankIcon.setImageResource(R.drawable.download_reward_rank_first);
		}else if(position==1){
			holder.rankNum.setVisibility(View.GONE);
			holder.rankIcon.setVisibility(View.VISIBLE);
			holder.rankIcon.setImageResource(R.drawable.download_reward_rank_second);
		}else if(position==2){
			holder.rankNum.setVisibility(View.GONE);
			holder.rankIcon.setVisibility(View.VISIBLE);
			holder.rankIcon.setImageResource(R.drawable.download_reward_rank_third);
		}else{
			holder.rankIcon.setVisibility(View.GONE);
		    holder.rankNum.setVisibility(View.VISIBLE);
		    holder.rankNum.setText(position+1+"");
		}
		
		return convertView;
	}

	

	@Override
	public int getCount() {
		return downloadRankInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return downloadRankInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
}
