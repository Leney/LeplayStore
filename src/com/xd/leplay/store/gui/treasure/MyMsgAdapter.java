package com.xd.leplay.store.gui.treasure;

import java.util.List;

import com.xd.base.util.DLog;
import com.xd.leplay.store.R;
import com.xd.leplay.store.model.proto.Uac.SysNotify;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 我的消息Adapter
 * 
 * @author lilijun
 *
 */
public class MyMsgAdapter extends BaseAdapter
{

	private List<SysNotify> msgList;

	private Context mContext;

	public MyMsgAdapter(List<SysNotify> list, Context context)
	{
		this.mContext = context;
		this.msgList = list;
	}

	@Override
	public int getCount()
	{
		return msgList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return msgList.get(position);
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
			convertView = View.inflate(mContext, R.layout.my_msg_adapter, null);
			holderView.date = (TextView) convertView
					.findViewById(R.id.my_msg_adapter_date);
			holderView.content = (TextView) convertView
					.findViewById(R.id.my_msg_adapter_content);
			convertView.setTag(holderView);
		} else
		{
			holderView = (HolderView) convertView.getTag();
		}
		SysNotify msg = msgList.get(position);
		DLog.i("lilijun", "消息显示的单挑消息--------->>>" + msg.getMessage());
		holderView.date.setText(msg.getCreateTime());
		holderView.content.setText(msg.getMessage());
		if (msg.getStatus() == 0)
		{
			// 未读
			holderView.content.setTextColor(Color.parseColor("#333333"));
			holderView.content
					.setBackgroundResource(R.drawable.incon_list_bg_shape);
		} else
		{
			// 已读
			holderView.content.setTextColor(Color.parseColor("#999999"));
			holderView.content
					.setBackgroundResource(R.drawable.incon_list_bg_shape_2);
		}
		return convertView;
	}

	class HolderView
	{
		/** 时间、正文文本 */
		TextView date, content;
	}

}
