package com.xd.leplay.store.gui.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.gui.application.ApplicationClassifyDetailActivity;
import com.xd.leplay.store.model.proto.App.AppType;
import com.xd.leplay.store.util.DisplayUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * 分类适配器(一行一个条目)
 * 
 * @author lilijun
 */
public class ClassifyAdapter2 extends BaseAdapter
{

	private Context mContext = null;

	private List<AppType> appTypes = null;

	private ImageLoaderManager imageLoaderManager = null;

	private DisplayImageOptions options = null;

	private String action = "";

	private String etagMark = "";

	public ClassifyAdapter2(Context context, List<AppType> list, String action,
			String etagMark)
	{
		this.mContext = context;
		this.appTypes = list;
		this.imageLoaderManager = ImageLoaderManager.getInstance();
		this.options = DisplayUtil.getListIconImageLoaderOptions();
		this.action = action;
		this.etagMark = etagMark;
	}

	public void setAppTypes(List<AppType> appTypes)
	{
		this.appTypes = appTypes;
	}

	@Override
	public int getCount()
	{
		return appTypes.size();
	}

	@Override
	public Object getItem(int position)
	{
		return appTypes.get(position);
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
		AppType appType = (AppType) getItem(position);
		if (convertView == null)
		{
			convertView = View.inflate(mContext,
					R.layout.classify_list_adapter2, null);
			holderView = new HolderView();
			holderView.icon = (ImageView) convertView
					.findViewById(R.id.classify_adapter2_icon);
			holderView.name = (TextView) convertView
					.findViewById(R.id.classify_adapter2_name);
			convertView.setOnClickListener(itemOncliClickListener);
			convertView.setTag(holderView);
		} else
		{
			holderView = (HolderView) convertView.getTag();
		}
		convertView.setTag(R.id.classify_adapter2_icon, appType.getTypeId());
		convertView.setTag(R.id.classify_adapter2_name, appType.getTypeName());
		imageLoaderManager.displayImage(appType.getTypePicUrl(),
				holderView.icon, options);
		holderView.name.setText(appType.getTypeName());
		return convertView;
	}

	class HolderView
	{
		ImageView icon;
		TextView name;
	}

	/**
	 * item的点击事件
	 */
	private OnClickListener itemOncliClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// 类别Id
			int classifyId = (Integer) v.getTag(R.id.classify_adapter2_icon);
			String classifyName = (String) v
					.getTag(R.id.classify_adapter2_name);
			String tempEtagMark = etagMark + classifyId + "";
			ApplicationClassifyDetailActivity
					.startApplicationClassifyDetailActivity(mContext,
							classifyId, classifyName, action, tempEtagMark);
		}
	};

}
