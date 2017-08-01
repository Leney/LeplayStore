package com.xd.leplay.store.gui.treasure;

import android.content.Context;
import android.content.Intent;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqSetNotifyReaded;
import com.xd.leplay.store.model.proto.Uac.RspSetNotifyReaded;
import com.xd.leplay.store.view.MarketListView;
import com.google.protobuf.ByteString;

/**
 * 我的消息界面
 * 
 * @author lilijun
 *
 */
public class MyMessageActivity extends BaseActivity
{
	private MarketListView listview;

	private MyMsgAdapter adapter;

	@Override
	protected void initView()
	{
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_MY_MSGS_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		titleView.setTitleName(getResources().getString(R.string.msg_notify));
		if (LoginUserInfoManager.getInstance().getLoginedUserInfo()
				.getUserMsgList() == null
				|| LoginUserInfoManager.getInstance().getLoginedUserInfo()
						.getUserMsgList().isEmpty())
		{
			// 没有消息
			errorViewLayout.setErrorView(R.drawable.no_msgs, getResources()
					.getString(R.string.no_msgs), "");
			errorViewLayout.setRefrushOnClickListener(null);
			showErrorView();
		} else
		{
			// 有消息
			listview = new MarketListView(this);
			listview.setDividerHeight(0);
			listview.setSelector(R.drawable.item_nothing_bg_selector);
			setCenterView(listview);

			adapter = new MyMsgAdapter(LoginUserInfoManager.getInstance()
					.getLoginedUserInfo().getUserMsgList(), this);
			listview.setAdapter(adapter);
			showContentView();

			// 通知服务器已读消息
			ReqSetNotifyReaded.Builder builder = ReqSetNotifyReaded
					.newBuilder();
			builder.setUid(LoginUserInfoManager.getInstance()
					.getLoginedUserInfo().getUserId());
			doLoadData(Constants.UAC_API_URL, new String[]
			{ "ReqSetNotifyReaded" }, new ByteString[]
			{ builder.build().toByteString() }, "");
		}
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		for (String action : rspPacket.getActionList())
		{
			if (action.equals("RspSetNotifyReaded"))
			{
				// 设置用户已读消息状态返回
				try
				{
					RspSetNotifyReaded rspSetNotifyReaded = RspSetNotifyReaded
							.parseFrom(rspPacket.getParams(0));
					if (rspPacket.getRescode() == 0)
					{
						// 设置消息已读成功
						LoginUserInfoManager.getInstance().getLoginedUserInfo()
								.getUserMsgList().clear();
						LoginUserInfoManager.getInstance().getLoginedUserInfo()
								.getUserMsgList()
								.addAll(rspSetNotifyReaded.getNotifyList());
						// 设置未读信息为0
						LoginUserInfoManager.getInstance().getLoginedUserInfo()
								.setUnreadMsgNum(0);
					} else
					{
						// 设置消息已读失败
					}
				} catch (Exception e)
				{
					e.printStackTrace();
					DLog.e("MyMessageActivity", "loadDataSuccess()#Excepton:",
							e);
				}
			}
		}

		// 发送用户账户所属信息发生改变广播
		sendBroadcast(new Intent(Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY));
	}

	public static void startMyMessageActivity(Context context, String action)
	{
		Intent intent = new Intent(context, MyMessageActivity.class);
		DataCollectionManager.startActivity(context, intent, action);
	}
}
