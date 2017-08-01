package com.xd.leplay.store.gui.treasure;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.ToolsUtil;

/**
 * 提现成功之后,分享界面
 * 
 * @author lilijun
 *
 */
public class GainMonneySuccessShareActivity extends BaseActivity
{
	public static void startGainMonneySuccessShareActivity(Context context,
			String money, String action)
	{
		Intent intent = new Intent(context,
				GainMonneySuccessShareActivity.class);
		intent.putExtra("money", money);
		DataCollectionManager.startActivity(context, intent, action);
	}

	@Override
	protected void initView()
	{
		action = DataCollectionManager
				.getAction(
						DataCollectionManager
								.getIntentDataCollectionAction(getIntent()),
						DataCollectionConstant.DATA_COLLECTION_GOT_CASH_SUCCESS_SHARE_VALUE);
		DataCollectionManager.getInstance().addRecord(action);
		DataCollectionManager
				.getInstance()
				.addYouMengEventRecord(
						GainMonneySuccessShareActivity.this,
						action,
						DataCollectionConstant.EVENT_ID_CLICK_GAIN_MONEY_SUCCESS_SHARE_VALUE,
						null);
		final String money = getIntent().getStringExtra("money");

		titleView.setTitleName(getResources().getString(
				R.string.share_red_packet));
		setCenterView(R.layout.gain_money_success_share_activity);

		TextView detailTextView = (TextView) findViewById(R.id.gain_money_share_text);
		String result = getResources()
				.getString(R.string.share_red_packet_text);
		int start = result.indexOf("10");
		detailTextView.setText(ToolsUtil.getFormatTextColor(result, start,
				start + 2, "#de3a50"));
		Button shareBtn = (Button) findViewById(R.id.gain_money_share_now_btn);
		shareBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// 旧的分享链接
				// String shareUrl = Constants.UAC_API_URL
				// + "/invite?code="
				// + LoginUserInfoManager.getInstance()
				// .getLoginedUserInfo().getTreasureInfo()
				// .getInviteCode();
				// 新的分享链接
				String shareUrl = Constants.UAC_API_URL
						+ "withdraw_share/"
						+ LoginUserInfoManager.getInstance()
								.getLoginedUserInfo().getUserId();

				String msgContent = String.format(
						getResources().getString(
								R.string.share_red_packet_content), money)
						+ "\n" + shareUrl;

				DisplayUtil.showShareDialog(
						GainMonneySuccessShareActivity.this,
						getResources().getString(R.string.app_name),
						msgContent,
						getResources().getString(
								R.string.share_app_weixin_title), String
								.format(getResources().getString(
										R.string.share_red_packet_content),
										money), shareUrl, action);
			}

		});

		showContentView();
	}
}
