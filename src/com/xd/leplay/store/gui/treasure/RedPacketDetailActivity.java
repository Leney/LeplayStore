package com.xd.leplay.store.gui.treasure;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.util.DisplayUtil;

/**
 * 红包详情界面
 * 
 * @author luoxignxing
 *
 */
public class RedPacketDetailActivity extends BaseActivity implements
		OnClickListener
{
	/** 叫好友来抢红包图片按钮 */
	private ImageView pickPacket;

	/** 红包金额 */
	private TextView redPacketCoins;

	/** 登录用户信息 */
	private LoginedUserInfo userInfo = null;

	@Override
	protected void initView()
	{
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_RED_PACKET_DETAIL_VALUE);
		DataCollectionManager.getInstance().addRecord(action);
//		titleView.setTitleBackgroundColor(Color.parseColor((this.getResources()
//				.getString(R.string.red_packet_dialog_main_title))));
		titleView.setTitleName(getResources().getString(
				R.string.red_packet_password_input_title));
		titleView.setRightTextBtnName(getResources().getString(
				R.string.red_packet_record));
		titleView.setBottomLineVisible(false);
		titleView.setTitleColor(Color.WHITE);
		titleView.setRightTextBtnColor(Color.WHITE);
		titleView.setRightTextBtnOnClickListener(this);
		titleView.setBackImgRes(R.drawable.back_img);
		loadingView.setVisibilyView(false);
		setCenterView(R.layout.red_packet_detail);
		pickPacket = (ImageView) findViewById(R.id.inviteFriendPickPacket);
		pickPacket.setOnClickListener(this);
		redPacketCoins = (TextView) findViewById(R.id.redPacketDetailCoins);
		redPacketCoins.setText((getIntent().getIntExtra("redPackdetCoins", 0))
				/ 100.00 + "");
		userInfo = LoginUserInfoManager.getInstance().getLoginedUserInfo();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.child_title_right_text:
			GainCoinsRecordDetailActivity.startGainCoinsRecordDetailActivity(
					RedPacketDetailActivity.this, action, 4);
			// 数据采集
			String clickRedPacketRecord = DataCollectionManager
					.getAction(
							action,
							DataCollectionConstant.DATA_COLLECTION_CLICK_RED_PACKET_DETAIL_RECORD_VALUE);
			DataCollectionManager.getInstance().addRecord(clickRedPacketRecord);

			DataCollectionManager
					.getInstance()
					.addYouMengEventRecord(
							RedPacketDetailActivity.this,
							action,
							DataCollectionConstant.EVENT_ID_CLICK_RED_PACKET_DETAIL_RECORD,
							null);
			break;

		case R.id.inviteFriendPickPacket:
			if (userInfo != null)
			{
				String shareUrl = Constants.RED_PACKET_SHARE_URL
						+ userInfo.getUserId();
				String shareContent = String.format(
						getResources().getString(
								R.string.red_packet_password_share_content),
						RedPacketDetailActivity.this.getIntent()
								.getStringExtra("redPacketPassword"), shareUrl);
				DisplayUtil
						.showShareDialog(
								RedPacketDetailActivity.this,
								getResources().getString(
										R.string.invite_friends),
								shareContent,
								getResources()
										.getString(
												R.string.red_packet_password_share_title),
								String.format(
										getResources()
												.getString(
														R.string.red_packet_password_share_wx_content),
										RedPacketDetailActivity.this
												.getIntent().getStringExtra(
														"redPacketPassword")),
								shareUrl, action);
				// 数据采集
				String inviteFriendGetPacket = DataCollectionManager
						.getAction(
								action,
								DataCollectionConstant.DATA_COLLECTION_CLICK_INVITE_FRIEND_GET_RED_PACKET_VALUE);
				DataCollectionManager.getInstance().addRecord(
						inviteFriendGetPacket);

				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								RedPacketDetailActivity.this,
								action,
								DataCollectionConstant.EVENT_ID_CLICK_INVITE_FRIEND_GET_RED_PACKET,
								null);
			}
			break;
		}
	}

	public static void startRedPacketDetailActivity(Context context,
			String action, int redPackdetCoins, String redPacketPassword)
	{
		Intent intent = new Intent(context, RedPacketDetailActivity.class);
		intent.putExtra("redPackdetCoins", redPackdetCoins);
		intent.putExtra("redPacketPassword", redPacketPassword);
		DataCollectionManager.startActivity(context, intent, action);
	}
}
