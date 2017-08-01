package com.xd.leplay.store.gui.details;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.model.CommentInfo;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.view.CustomImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 评论列表的Adapter
 * 
 * @author lilijun
 *
 */
public class CommentAdapter extends BaseAdapter
{
	private List<CommentInfo> commentInfos;

	private Context mContext;

	private ImageLoaderManager imageLoaderManager = null;

	private DisplayImageOptions options = null;

	public CommentAdapter(Context context, List<CommentInfo> list)
	{
		this.mContext = context;
		this.commentInfos = list;
		this.imageLoaderManager = ImageLoaderManager.getInstance();
		this.options = DisplayUtil.getUserIconImageLoaderOptions();
	}

	@Override
	public int getCount()
	{
		return commentInfos.size();
	}

	@Override
	public Object getItem(int position)
	{
		return commentInfos.get(position);
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
		CommentInfo commentInfo = (CommentInfo) getItem(position);
		if (convertView == null)
		{
			holderView = new HolderView();
			convertView = View
					.inflate(mContext, R.layout.comment_adapter, null);
			holderView.commentContent = (TextView) convertView
					.findViewById(R.id.comment_adapter_comment_content);
			holderView.commentTime = (TextView) convertView
					.findViewById(R.id.comment_adapter_time);
			holderView.starLevel = (RatingBar) convertView
					.findViewById(R.id.comment_adapter_comment_star);
			holderView.userIcon = (CustomImageView) convertView
					.findViewById(R.id.comment_adapter_user_icon);
			holderView.userName = (TextView) convertView
					.findViewById(R.id.comment_adapter_user_name);
			convertView.setTag(holderView);
		} else
		{
			holderView = (HolderView) convertView.getTag();
		}
		imageLoaderManager.displayImage(commentInfo.getUserIcon(),
				holderView.userIcon, options);
		holderView.commentContent.setText(commentInfo.getCommentContent());
		holderView.commentTime.setText(commentInfo.getCommentTime());
		holderView.starLevel.setRating(commentInfo.getStarLevel());
		holderView.userName.setText(commentInfo.getUserName());
		return convertView;
	}

	class HolderView
	{
		// RoundImageView userIcon;
		CustomImageView userIcon;
		TextView userName, commentTime, commentContent;
		RatingBar starLevel;
	}

}
