package com.xd.leplay.store.gui.treasure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqRedpack;
import com.xd.leplay.store.model.proto.Uac.RspRedpack;
import com.xd.leplay.store.view.CenterDialog;
import com.xd.leplay.store.view.FlipAnimation;
import com.xd.leplay.store.view.LoadingDialog;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 红包口令输入界面
 * 
 * @author luoxingxing
 *
 */
public class RedPacketPasswordInputActivity extends BaseActivity implements
		OnClickListener
{
	/** 红包口令输入框 */
	private EditText inputRedPacketPassword;

	/** 领取红包按钮 */
	private Button gainRedPacketBtn;

	/** 拆开红包图片 */
	private ImageView openRedPacket;

	/** 中间红包弹出框 */
	private CenterDialog redPacketDialog;

	/** 关闭弹出框 */
	private RelativeLayout shutDownDialogLay;

	/** 正在加载弹出框 */
	private LoadingDialog loadingDialog;

	/** 红包口令请求信息TAG */
	private static String GET_RED_PACKET_INFO_REQUEST_TAG = "ReqRedpack";

	/** 红包口令返回信息TAG */
	private static String GET_RED_PACKET_INFO_RESPONSE_TAG = "RspRedpack";

	/** 登录用户信息 */
	private LoginedUserInfo userInfo = null;

	/** 红包金币数量 */
	private int coins = 0;

	/** 红包口令 */
	private String redPacketPassword;

	@SuppressLint("ResourceAsColor")
	@Override
	protected void initView()
	{
		action = DataCollectionManager
				.getAction(
						DataCollectionManager
								.getIntentDataCollectionAction(getIntent()),
						DataCollectionConstant.DATA_COLLECTION_RED_PACKET_PASSWORD_INPUT_VALUE);
		DataCollectionManager.getInstance().addRecord(action);
		titleView.setTitleBackgroundColor(Color.parseColor("#dc5a4d"));
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
		setCenterView(R.layout.red_packet_password_input);
		loadingDialog = new LoadingDialog(this, getResources().getString(
				R.string.red_packet_password_loading));
		inputRedPacketPassword = (EditText) findViewById(R.id.input_red_packet_password);
		gainRedPacketBtn = (Button) findViewById(R.id.gain_red_packet_btn);
		gainRedPacketBtn.setOnClickListener(this);
		userInfo = LoginUserInfoManager.getInstance().getLoginedUserInfo();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.gain_red_packet_btn:

			if (inputRedPacketPassword.getText().toString().trim().equals(""))
			{
				Toast.makeText(
						RedPacketPasswordInputActivity.this,
						RedPacketPasswordInputActivity.this.getResources()
								.getString(R.string.red_packet_password_empty),
						Toast.LENGTH_SHORT).show();
			} else
			{
				loadingDialog.show();
				doLoadData(Constants.UAC_API_URL, new String[]
				{ GET_RED_PACKET_INFO_REQUEST_TAG }, new ByteString[]
				{ getRedPacketRequestData() }, "");
			}
			// 数据采集
			String clickGetRedPacket = DataCollectionManager
					.getAction(
							action,
							DataCollectionConstant.DATA_COLLECTION_CLICK_GET_RED_PACKET_VALUE);
			DataCollectionManager.getInstance().addRecord(clickGetRedPacket);

			DataCollectionManager.getInstance().addYouMengEventRecord(
					RedPacketPasswordInputActivity.this, action,
					DataCollectionConstant.EVENT_ID_CLICK_GET_RED_PACKET, null);
			break;

		case R.id.child_title_right_text:
			GainCoinsRecordDetailActivity.startGainCoinsRecordDetailActivity(
					RedPacketPasswordInputActivity.this, action, 4);
			// 数据采集
			String clickRedPacketRecord = DataCollectionManager
					.getAction(
							action,
							DataCollectionConstant.DATA_COLLECTION_CLICK_RED_PACKET_RECORD_VALUE);
			DataCollectionManager.getInstance().addRecord(clickRedPacketRecord);

			DataCollectionManager.getInstance().addYouMengEventRecord(
					RedPacketPasswordInputActivity.this, action,
					DataCollectionConstant.EVENT_ID_CLICK_RED_PACKET_RECORD,
					null);
			break;
		case R.id.openRedPacket:
			Animation animation;
			animation = new FlipAnimation(0, 1080,
					(float) openRedPacket.getWidth() / 2.0f,
					(float) openRedPacket.getHeight() / 2.0f, 67.5f, false);
			animation.setAnimationListener(new AnimationListener()
			{

				@Override
				public void onAnimationStart(Animation animation)
				{

				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{

				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					// 放大缩小跳转
					RedPacketDetailActivity.startRedPacketDetailActivity(
							RedPacketPasswordInputActivity.this, action, coins,
							redPacketPassword);
					overridePendingTransition(R.anim.scale, R.anim.scale);

				}
			});
			animation.setDuration(1200);
			animation.setFillAfter(true);
			animation.setInterpolator(new AccelerateInterpolator());
			openRedPacket.setImageResource(R.drawable.red_packet_coin);
			openRedPacket.startAnimation(animation);
			break;
		case R.id.shutDownDialogLay:
			redPacketDialog.dismiss();
			break;
		}
	}

	/*
	 * 获取红包请求数据
	 */
	private ByteString getRedPacketRequestData()
	{
		ReqRedpack.Builder builder = ReqRedpack.newBuilder();
		builder.setUid(userInfo.getUserId());
		builder.setUserToken(userInfo.getUserToken());
		builder.setWords(inputRedPacketPassword.getText().toString());
		return builder.build().toByteString();
	}

	public static void startRedPacketPasswordInputActivity(Context context,
			String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				RedPacketPasswordInputActivity.class), action);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (redPacketDialog != null)
		{
			redPacketDialog.dismiss();
		}
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		try
		{
			for (String action : rspPacket.getActionList())
			{
				if (action.equals(GET_RED_PACKET_INFO_RESPONSE_TAG))
				{
					RspRedpack rspRedpackInfo = RspRedpack.parseFrom(rspPacket
							.getParams(0));
					loadingDialog.dismiss();
					parseRedPacketRspResult(rspRedpackInfo);
				}
			}
		} catch (InvalidProtocolBufferException e)
		{
			e.printStackTrace();
		}

	}

	/*
	 * 解析红包口令请求返回结果
	 */
	private void parseRedPacketRspResult(RspRedpack rspRedpackInfo)
	{
		if (rspRedpackInfo.getRescode() == 0)
		{
			// 获取红包信息成功
			coins = rspRedpackInfo.getCoin();
			redPacketPassword = rspRedpackInfo.getWords();
			redPacketDialog = new CenterDialog(
					RedPacketPasswordInputActivity.this);
			redPacketDialog.show();
			redPacketDialog.setTitleVisible(false);
			View redPacketDialogView = View.inflate(
					RedPacketPasswordInputActivity.this,
					R.layout.red_packet_dialog, null);
			redPacketDialog.setCenterView(redPacketDialogView);
			String title = rspRedpackInfo.getTitle();
			int index = title.indexOf("_");
			if(index!=-1){
			TextView mainTitle = (TextView) redPacketDialogView
					.findViewById(R.id.red_packet_main_title);
			TextView secondTitle = (TextView) redPacketDialogView
					.findViewById(R.id.red_packet_second_titile);
			mainTitle.setText(title.substring(0, index));
			secondTitle.setText(title.substring(index + 1, title.length()));
			}
			Resources resources = getResources();
			DisplayMetrics dm = resources.getDisplayMetrics();
			WindowManager.LayoutParams params = redPacketDialog.getWindow()
					.getAttributes();
			params.width = (int) (dm.widthPixels * 0.85);
			redPacketDialog.getWindow().setAttributes(params);

			openRedPacket = (ImageView) redPacketDialogView
					.findViewById(R.id.openRedPacket);
			shutDownDialogLay = (RelativeLayout) redPacketDialogView
					.findViewById(R.id.shutDownDialogLay);
			shutDownDialogLay
					.setOnClickListener(RedPacketPasswordInputActivity.this);
			openRedPacket
					.setOnClickListener(RedPacketPasswordInputActivity.this);
		} else if (rspRedpackInfo.getRescode() == 3)
		{
			// userToken错误
			// 退出登录
			LoginUserInfoManager.getInstance().exitLogin();
			Toast.makeText(RedPacketPasswordInputActivity.this,
					getResources().getString(R.string.re_login),
					Toast.LENGTH_SHORT).show();
			LoginActivity.startLoginActivity(
					RedPacketPasswordInputActivity.this, action);
			RedPacketPasswordInputActivity.this.finish();
		} else if (rspRedpackInfo.getRescode() == 4)
		{
			// 领取过红包了直接跳转到红包详情界面
			DLog.i("lilijun",
					"-------->>>>>>>>password" + rspRedpackInfo.getWords());
			RedPacketDetailActivity.startRedPacketDetailActivity(
					RedPacketPasswordInputActivity.this, action,
					rspRedpackInfo.getCoin(), rspRedpackInfo.getWords());
		} else
		{
			Toast.makeText(RedPacketPasswordInputActivity.this,
					rspRedpackInfo.getResmsg(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		loadingDialog.dismiss();
		Toast.makeText(RedPacketPasswordInputActivity.this,
				getResources().getString(R.string.internet_error),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		loadingDialog.dismiss();
		Toast.makeText(
				RedPacketPasswordInputActivity.this,
				RedPacketPasswordInputActivity.this.getResources().getString(
						R.string.red_packet_password_error), Toast.LENGTH_SHORT)
				.show();

	}

}
