package com.xd.leplay.store.gui.game;

import java.util.List;

import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.model.GiftInfo;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.ToolsUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 我的礼包列表适配器
 * 
 * @author lilijun
 *
 */
public class MyGiftAdapter extends BaseAdapter
{
	private List<GiftInfo> giftInfos;

	private Context mContext;

	private ImageLoaderManager imageLoaderManager = null;

	private DisplayImageOptions options = null;

	private String action = "";

	public MyGiftAdapter(Context context, List<GiftInfo> list, String action)
	{
		this.mContext = context;
		this.giftInfos = list;
		this.action = action;
		this.imageLoaderManager = ImageLoaderManager.getInstance();
		this.options = DisplayUtil.getListIconImageLoaderOptions();
	}

	@Override
	public int getCount()
	{
		return giftInfos.size();
	}

	@Override
	public Object getItem(int position)
	{
		return giftInfos.get(position);
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
		GiftInfo giftInfo = (GiftInfo) getItem(position);
		if (convertView == null)
		{
			holderView = new HolderView();
			convertView = View
					.inflate(mContext, R.layout.my_gift_adapter, null);
			holderView.appIcon = (ImageView) convertView
					.findViewById(R.id.my_gift_app_icon);
			holderView.appName = (TextView) convertView
					.findViewById(R.id.my_gift_app_name);
			holderView.giftName = (TextView) convertView
					.findViewById(R.id.my_gift_gift_name);
			holderView.giftCode = (TextView) convertView
					.findViewById(R.id.my_gift_gift_code);
			holderView.copyBtn = (Button) convertView
					.findViewById(R.id.my_gift_copy_code_btn);
			holderView.copyBtn.setOnClickListener(copyBtnOnClickListener);
			convertView.setOnClickListener(itemOnClickListener);
			convertView.setTag(holderView);
		} else
		{
			holderView = (HolderView) convertView.getTag();
		}

		imageLoaderManager.displayImage(giftInfo.getGameIconUrl(),
				holderView.appIcon, options);
		holderView.appName.setText(giftInfo.getGameName());
		holderView.giftName.setText(giftInfo.getName());
		holderView.giftCode.setText(mContext.getResources().getString(
				R.string.redeem_code)
				+ giftInfo.getCode());
		holderView.copyBtn.setTag(giftInfo.getCode());
		convertView.setTag(R.id.my_gift_gift_name, giftInfo);
		return convertView;
	}

	class HolderView
	{
		ImageView appIcon;
		TextView appName, giftName, giftCode;
		Button copyBtn;
	}

	private OnClickListener copyBtnOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String code = (String) v.getTag();
			// 复制到粘贴板
			boolean isCopy = ToolsUtil.copy(code, mContext);
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

	private OnClickListener itemOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			GiftInfo giftInfo = (GiftInfo) v.getTag(R.id.my_gift_gift_name);
			GiftDetailActivity2.startGiftDetailActivity2(mContext,
					giftInfo.getGameId(), giftInfo.getId(), action);
		}
	};

}
