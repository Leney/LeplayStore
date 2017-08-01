package com.xd.leplay.store.gui.manager;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqFeedback;
import com.xd.leplay.store.model.proto.Uac.RspFeedback;
import com.xd.leplay.store.view.LoadingDialog;
import com.google.protobuf.ByteString;

/**
 * 意见反馈界面
 * 
 * @author lilijun
 *
 */
public class FeedBackActivivty extends BaseActivity
{
	private final String TAG = "FeedBackActivivty";

	private EditText feedInput;

	private Button submitBtn;

	/** 提交意见反馈数据的请求TAG */
	private final String SUBMIT_FEEDBACK_REQUEST_TAG = "ReqFeedback";

	private LoadingDialog loadingDialog = null;

	@Override
	protected void initView()
	{
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_MANAGER_FEED_BACK_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		titleView.setTitleName(getResources().getString(R.string.feedback));
		titleView.setRightLayVisible(false);
		titleView.setBottomLineVisible(true);

		loadingView.setVisibilyView(false);

		loadingDialog = new LoadingDialog(FeedBackActivivty.this,
				getResources().getString(R.string.feed_submitting));
		setCenterView(R.layout.feed_back_activity);

		feedInput = (EditText) findViewById(R.id.feed_back_input);
		feedInput.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		submitBtn = (Button) findViewById(R.id.feed_back_submit_btn);
		submitBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String feed = feedInput.getText().toString().trim();
				if (!"".equals(feed))
				{
					// 提交意见数据
					doLoadData(Constants.UAC_API_URL, new String[]
					{ SUBMIT_FEEDBACK_REQUEST_TAG }, new ByteString[]
					{ getSubmitFeedBackRequestData(feed) }, "");
					loadingDialog.show();
				} else
				{
					Toast.makeText(FeedBackActivivty.this,
							getResources().getString(R.string.feed_back_empty),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		loadingDialog.dismiss();
		try
		{
			RspFeedback rspFeedback = RspFeedback.parseFrom(rspPacket
					.getParams(0));
			if (rspFeedback.getRescode() == 0)
			{
				// 提交意见成功
				Toast.makeText(FeedBackActivivty.this,
						getResources().getString(R.string.feed_submit_success),
						Toast.LENGTH_SHORT).show();
				feedInput.setText("");
			} else
			{
				Toast.makeText(FeedBackActivivty.this, rspFeedback.getResmsg(),
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "解析提交反馈信息时发生异常#exception：", e);
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		loadingDialog.dismiss();
		Toast.makeText(FeedBackActivivty.this,
				getResources().getString(R.string.feed_submit_failed),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		loadingDialog.dismiss();
		Toast.makeText(FeedBackActivivty.this,
				getResources().getString(R.string.feed_submit_failed),
				Toast.LENGTH_SHORT).show();
	}

	private ByteString getSubmitFeedBackRequestData(String content)
	{
		ReqFeedback.Builder builder = ReqFeedback.newBuilder();
		if (LoginUserInfoManager.getInstance().isHaveUserLogin())
		{
			builder.setUid(LoginUserInfoManager.getInstance()
					.getLoginedUserInfo().getUserId()
					+ "");
		}
		builder.setContent(content);
		return builder.build().toByteString();
	}

	public static void startFeedBackActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				FeedBackActivivty.class), action);
	}

}
