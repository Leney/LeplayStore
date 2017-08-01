package com.xd.leplay.store.gui.treasure;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ConstantManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.main.BaseTabChildFragment;
import com.xd.leplay.store.gui.personal.PersonalCenterActivity;
import com.xd.leplay.store.gui.webview.LotteryWebViewActivity;
import com.xd.leplay.store.gui.webview.WeixinPublicAccountWebViewActivity;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqSign;
import com.xd.leplay.store.model.proto.Uac.ReqSignList;
import com.xd.leplay.store.model.proto.Uac.RspSign;
import com.xd.leplay.store.model.proto.Uac.RspSignList;
import com.xd.leplay.store.model.proto.Uac.SignInfo;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.NetUtil;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.CenterDialog;
import com.xd.leplay.store.view.CustomImageView;
import com.xd.leplay.store.view.LoadingDialog;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 财富主Fragment
 * 
 * @author lilijun
 *
 */
public class TreasureFragment extends BaseTabChildFragment implements
		OnClickListener
{
	private final String TAG = "TreasureFragment";

	/** 顶部用户信息部分 */
	private RelativeLayout userInfoLay;
	/** 用户头像 */
	private CustomImageView userIcon;

	/** 用户名称、金币数量、今日收入、签到天数 */
	private TextView userName, coinNum, todayCoins, signDays;

	/** 我的金币部分、我的礼包部分 */
	private LinearLayout myCoinsLay, myGiftsLay;

	/** 消息部分 */
	private LinearLayout msgLay;

	/** 消息数量 */
	private TextView msgNum;

	/** 一周的名称显示控件(0-6==>周一到星周日) */
	private TextView[] weekName = new TextView[7];

	/** 一周的签到获得的金币数量显示控件(0-6==>周一到星周日) */
	private TextView[] weekSignCoins = new TextView[7];

	/** "每日签到" 按钮 */
	private Button siginBtn;

	/** 下面按钮部分的视图数组 */
	private LinearLayout[] buttonLays;

	private LoginUserInfoManager userInfoManager = null;

	private LoginedUserInfo userInfo = null;

	/** 获取用户签到的历史记录列表的请求TAG */
	private static final String GET_USER_SINGN_HISTORY_REQUEST_TAG = "ReqSignList";

	/** 获取用户签到的历史记录列表的响应TAG */
	private static final String GET_USER_SINGN_HISTORY_RSPONSE_TAG = "RspSignList";

	/** 签到的请求TAG */
	private static final String SIGN_REQUEST_TAG = "ReqSign";

	/** 签到的响应TAG */
	private static final String SIGN_RSPONSE_TAG = "RspSign";

	/** 签到的LoadingDialog */
	private LoadingDialog loadingDialog = null;

	// /** 刷新金币的Dialog */
	// private LoadingDialog refreshDialog = null;

	/** 签到中 金币的图片资源 */
	private Drawable drawable = null;

	/** 我的消息 没有未读消息图片资源、有未读消息图片资源 */
	private Drawable normallMsgImg, unreadMsgImg;

	private ImageLoaderManager imageLoaderManager = null;

	private DisplayImageOptions options = null;

	/** 签到成功Dialog,连续签到7天的Dialog */
	private CenterDialog signSuccessDialog, sign7daysDialog;

	/** 让签到dialog消失的Hander.what */
	private final int DISMISS_SIGN_DIALOG_HANDER = 1;

	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what == DISMISS_SIGN_DIALOG_HANDER)
			{
				if (signSuccessDialog != null)
				{
					signSuccessDialog.dismiss();
				}
				if (sign7daysDialog != null)
				{
					sign7daysDialog.dismiss();
				}
			}
		};
	};

	@Override
	protected void initView(FrameLayout view)
	{
		action = DataCollectionConstant.DATA_COLLECTION_TREASURE_VALUE;
		DataCollectionManager.getInstance().addRecord(action);

		userInfoManager = LoginUserInfoManager.getInstance();
		userInfo = userInfoManager.getLoginedUserInfo();
		imageLoaderManager = ImageLoaderManager.getInstance();
		options = DisplayUtil.getUserIconImageLoaderOptions();

		centerViewLayout.setVisibility(View.GONE);
		loadingView.setVisibilyView(true);

		loadingDialog = new LoadingDialog(getActivity(), getResources()
				.getString(R.string.signing));
		// refreshDialog = new LoadingDialog(getActivity(), getResources()
		// .getString(R.string.refreshing));
		drawable = getResources().getDrawable(R.drawable.treasure_history_coin);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		normallMsgImg = getResources().getDrawable(R.drawable.msg_icon);
		normallMsgImg.setBounds(0, 0, normallMsgImg.getMinimumWidth(),
				normallMsgImg.getMinimumHeight());
		unreadMsgImg = getResources().getDrawable(R.drawable.msg_icon_2);
		unreadMsgImg.setBounds(0, 0, unreadMsgImg.getMinimumWidth(),
				unreadMsgImg.getMinimumHeight());
		setCenterView(R.layout.treasure_fragment);

		userInfoLay = (RelativeLayout) view
				.findViewById(R.id.treasure_user_info_lay);
		userInfoLay.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (userInfoManager.isHaveUserLogin())
				{
					// 跳转到个人中心
					PersonalCenterActivity.startPersonalActivity(getActivity(),
							action);
					String personalCenterAction = DataCollectionManager
							.getAction(
									action,
									DataCollectionConstant.DATA_COLLECTION_CLICK_TREASURE_PERSONAL_CENTER_VALUE);
					// 自己后台的数据采集
					DataCollectionManager.getInstance().addRecord(
							personalCenterAction);
					// 添加友盟的事件点击数据采集
					DataCollectionManager
							.getInstance()
							.addYouMengEventRecord(
									getActivity(),
									personalCenterAction,
									DataCollectionConstant.EVENT_ID_CLICK_TREASURE_PERSONAL_CENTER_TAB,
									null);
				} else
				{
					// 跳转到登录界面
					LoginActivity.startLoginActivity(getActivity(), action);
				}
			}
		});
		userIcon = (CustomImageView) view.findViewById(R.id.treasure_user_icon);

		userName = (TextView) view.findViewById(R.id.treasure_user_name);
		coinNum = (TextView) view.findViewById(R.id.treasure_coin_num);
		todayCoins = (TextView) view
				.findViewById(R.id.treasure_user_today_income);
		signDays = (TextView) view
				.findViewById(R.id.treasure_sign_in_day_count);

		myCoinsLay = (LinearLayout) view
				.findViewById(R.id.treasure_my_coins_lay);
		myCoinsLay.setOnClickListener(this);
		myGiftsLay = (LinearLayout) view
				.findViewById(R.id.treasure_my_gifts_lay);
		myGiftsLay.setOnClickListener(this);
		msgLay = (LinearLayout) view.findViewById(R.id.treasure_my_msgs_lay);
		msgLay.setOnClickListener(this);
		msgNum = (TextView) view.findViewById(R.id.treasure_msg_num);

		weekName[0] = (TextView) view
				.findViewById(R.id.treasure_sign_in_monday);
		weekName[1] = (TextView) view
				.findViewById(R.id.treasure_sign_in_tuesday);
		weekName[2] = (TextView) view
				.findViewById(R.id.treasure_sign_in_wednesday);
		weekName[3] = (TextView) view
				.findViewById(R.id.treasure_sign_in_thursday);
		weekName[4] = (TextView) view
				.findViewById(R.id.treasure_sign_in_friday);
		weekName[5] = (TextView) view
				.findViewById(R.id.treasure_sign_in_sarturday);
		weekName[6] = (TextView) view
				.findViewById(R.id.treasure_sign_in_sunday);

		weekSignCoins[0] = (TextView) view
				.findViewById(R.id.treasure_sign_in_monday_coin);
		weekSignCoins[1] = (TextView) view
				.findViewById(R.id.treasure_sign_in_tuesday_coin);
		weekSignCoins[2] = (TextView) view
				.findViewById(R.id.treasure_sign_in_wednesday_coin);
		weekSignCoins[3] = (TextView) view
				.findViewById(R.id.treasure_sign_in_thursday_coin);
		weekSignCoins[4] = (TextView) view
				.findViewById(R.id.treasure_sign_in_friday_coin);
		weekSignCoins[5] = (TextView) view
				.findViewById(R.id.treasure_sign_in_sarturday_coin);
		weekSignCoins[6] = (TextView) view
				.findViewById(R.id.treasure_sign_in_sunday_coin);

		siginBtn = (Button) view.findViewById(R.id.treasure_sign_in_btn);
		siginBtn.setOnClickListener(this);

		buttonLays = new LinearLayout[6];
		buttonLays[0] = (LinearLayout) view
				.findViewById(R.id.treasure_button_lay_1);
		buttonLays[1] = (LinearLayout) view
				.findViewById(R.id.treasure_button_lay_2);
		buttonLays[2] = (LinearLayout) view
				.findViewById(R.id.treasure_button_lay_3);
		buttonLays[3] = (LinearLayout) view
				.findViewById(R.id.treasure_button_lay_4);
		buttonLays[4] = (LinearLayout) view
				.findViewById(R.id.treasure_button_lay_5);
		buttonLays[5] = (LinearLayout) view
				.findViewById(R.id.treasure_button_lay_6);
		buttonLays[0].setOnClickListener(this);
		buttonLays[1].setOnClickListener(this);
		buttonLays[2].setOnClickListener(this);
		buttonLays[3].setOnClickListener(this);
		buttonLays[4].setOnClickListener(this);
		buttonLays[5].setOnClickListener(this);
		// lotteryLay = (LinearLayout) view
		// .findViewById(R.id.treasure_lottery_lay);
		// lotteryLay.setOnClickListener(this);
		// incomeRankLay = (LinearLayout) view
		// .findViewById(R.id.treasure_income_rank_lay);
		// incomeRankLay.setOnClickListener(this);
		// myPurseLay = (LinearLayout) view
		// .findViewById(R.id.treasure_my_purse_lay);
		// myPurseLay.setOnClickListener(this);

		// setDefaultData();
		if (userInfoManager.isHaveUserLogin())
		{
			setUserInfoData();
			// 获取用户签到历史记录列表数据
			DLog.i("lilijun",
					"获取用户签到历史记录列表数据,userId--------->>>" + userInfo.getUserId());
			DLog.i("lilijun",
					"获取用户签到历史记录列表数据,userToken--------->>>"
							+ userInfo.getUserToken());
			doLoadData(
					Constants.UAC_API_URL,
					new String[]
					{ GET_USER_SINGN_HISTORY_REQUEST_TAG },
					new ByteString[]
					{ getUserSignHistoryRequestData(userInfo.getUserId(),
							userInfo.getUserToken()) }, "");
		} else
		{
			// 未登录 不用去服务器获取数据了
			loadingView.setVisibilyView(false);
			errorViewLayout.setVisibility(View.GONE);
			centerViewLayout.setVisibility(View.VISIBLE);
			// 设置默认数据(用户未登录)
			setDefaultData();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_ACCOUNT_HAVE_MODIFY);
		filter.addAction(Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY);
		filter.addAction(Constants.ACTION_NO_PIC_MODEL_CHANGE);
		getActivity().registerReceiver(treasureReceiver, filter);
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		for (String action : rspPacket.getActionList())
		{
			if (action.equals(GET_USER_SINGN_HISTORY_RSPONSE_TAG))
			{
				// 获取到用户签到历史记录信息
				try
				{
					RspSignList rspSignList = RspSignList.parseFrom(rspPacket
							.getParams(0));
					// 0=成功,1=系统响应错误,2=token错误
					if (rspSignList.getRescode() == 0)
					{
						DLog.i("lilijun", "获取签到历史记录成功！！！！");
						if (rspSignList.getIsSigned() == 0)
						{
							userInfoManager.getLoginedUserInfo()
									.getTreasureInfo().setSignIn(false);
							siginBtn.setEnabled(true);
							siginBtn.setText(getResources().getString(
									R.string.sign_in_for_everyday));
							siginBtn.setTextColor(getResources().getColor(
									R.color.white));
						} else
						{
							userInfoManager.getLoginedUserInfo()
									.getTreasureInfo().setSignIn(true);
							siginBtn.setEnabled(false);
							siginBtn.setText(getResources().getString(
									R.string.already_sign));
							siginBtn.setTextColor(getResources().getColor(
									R.color.list_describle_color));
						}
						userInfoManager.getLoginedUserInfo().getTreasureInfo()
								.setCoinNum(rspSignList.getTotalCorn());
						userInfoManager
								.getLoginedUserInfo()
								.getTreasureInfo()
								.setContinuSignDays(
										rspSignList.getContinuedDay());
						userInfoManager.getLoginedUserInfo().getTreasureInfo()
								.setTodayCoins(rspSignList.getTodayCorn());
						coinNum.setText(ToolsUtil
								.getFormatPraiseCount(userInfoManager
										.getLoginedUserInfo().getTreasureInfo()
										.getCoinNum())
								+ "");
						signDays.setText(" "
								+ userInfoManager.getLoginedUserInfo()
										.getTreasureInfo().getContinuSignDays()
								+ " ");
						// 设置今日收入
						todayCoins.setText(String.format(getResources()
								.getString(
										R.string.today_coins,
										userInfoManager.getLoginedUserInfo()
												.getTreasureInfo()
												.getTodayCoins())));
						List<SignInfo> signInfos = rspSignList
								.getSignInfoList();
						// 设置签到历史记录信息数据
						for (SignInfo signInfo : signInfos)
						{
							// 设置签到历史记录
							if (signInfo.getRewardCorn() == 0)
							{
								// 表示这一天为已经过去的一天 并且这一天没有签到
								weekName[signInfo.getWeekNo()]
										.setTextColor(getResources().getColor(
												R.color.list_describle_color));
								weekSignCoins[signInfo.getWeekNo()]
										.setText(">_<");
								weekSignCoins[signInfo.getWeekNo()]
										.setTextColor(getResources().getColor(
												R.color.list_describle_color));
								weekSignCoins[signInfo.getWeekNo()]
										.setCompoundDrawables(null, null, null,
												null);
							} else if (signInfo.getRewardCorn() == -1)
							{
								// 表示这一天还没到(或者为当天还没签到)
								weekName[signInfo.getWeekNo()]
										.setTextColor(getResources().getColor(
												R.color.title_name_color2));
								weekSignCoins[signInfo.getWeekNo()]
										.setText(signInfo.getDate());
								weekSignCoins[signInfo.getWeekNo()]
										.setTextColor(getResources().getColor(
												R.color.title_name_color2));
								weekSignCoins[signInfo.getWeekNo()]
										.setCompoundDrawables(null, null, null,
												null);
							} else
							{
								// 表示这一天已经过去了 并且这一天有签到
								weekName[signInfo.getWeekNo()]
										.setTextColor(getResources().getColor(
												R.color.title_name_color2));
								weekSignCoins[signInfo.getWeekNo()]
										.setText(signInfo.getRewardCorn() + "");
								weekSignCoins[signInfo.getWeekNo()]
										.setTextColor(getResources().getColor(
												R.color.redeem_code_text_color));
								weekSignCoins[signInfo.getWeekNo()]
										.setCompoundDrawables(null, null,
												drawable, null);
							}
						}

						centerViewLayout.setVisibility(View.VISIBLE);
						errorViewLayout.setVisibility(View.GONE);
						loadingView.setVisibilyView(false);
						// 保存登录用户信息到本地缓存
						ToolsUtil.saveCachDataToFile(getActivity(),
								Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME,
								userInfoManager.getLoginedUserInfo());
						DLog.i("lilijun", "处理完获取签到列表之后的操作！！！");
					} else if (rspSignList.getRescode() == 2)
					{
						// userToken错误 重新登录
						DLog.i("lilijun", "获取用户签到历史记录列表数据,userToken错误！！！");
						// 退出登录
						LoginUserInfoManager.getInstance().exitLogin();
					} else
					{
						DLog.i("lilijun",
								"获取签到历史记录失败，返回码---->>>"
										+ rspSignList.getRescode());
						DLog.i("lilijun",
								"失败消息---->>" + rspSignList.getResmsg());
						showErrorView();
					}
				} catch (Exception e)
				{
					DLog.e(TAG, "解析获取用户的签到历史记录信息发生异常#exception:", e);
					showErrorView();
				}
			} else if (action.equals(SIGN_RSPONSE_TAG))
			{
				// 签到返回
				try
				{
					loadingDialog.dismiss();
					RspSign rspSign = RspSign.parseFrom(rspPacket.getParams(0));
					// 0=成功,1=系统响应错误 2 = 今天已经签到了
					if (rspSign.getRescode() == 0)
					{
						// 签到成功
						Toast.makeText(
								getActivity(),
								getResources().getString(R.string.sign_success),
								Toast.LENGTH_SHORT).show();
						// 将连续签到的天数加 1
						userInfo.getTreasureInfo()
								.setContinuSignDays(
										userInfo.getTreasureInfo()
												.getContinuSignDays() + 1);
						signDays.setText(" "
								+ userInfo.getTreasureInfo()
										.getContinuSignDays() + " ");
						SignInfo signInfo = rspSign.getSignInfo();
						weekName[signInfo.getWeekNo()]
								.setTextColor(getResources().getColor(
										R.color.title_name_color2));
						weekSignCoins[signInfo.getWeekNo()].setText(signInfo
								.getRewardCorn() + "");
						weekSignCoins[signInfo.getWeekNo()]
								.setTextColor(getResources().getColor(
										R.color.redeem_code_text_color));
						weekSignCoins[signInfo.getWeekNo()]
								.setCompoundDrawables(null, null, drawable,
										null);
						// 设置为今天已签到
						userInfoManager.getLoginedUserInfo().getTreasureInfo()
								.setSignIn(true);
						// 追加总的金币数
						userInfoManager
								.getLoginedUserInfo()
								.getTreasureInfo()
								.setCoinNum(
										userInfoManager.getLoginedUserInfo()
												.getTreasureInfo().getCoinNum()
												+ signInfo.getRewardCorn());
						// 追加今日收入金币数
						userInfo.getTreasureInfo().setTodayCoins(
								userInfo.getTreasureInfo().getTodayCoins()
										+ signInfo.getRewardCorn());

						// 设置总得金币数量
						coinNum.setText(ToolsUtil
								.getFormatPraiseCount(userInfoManager
										.getLoginedUserInfo().getTreasureInfo()
										.getCoinNum())
								+ "");

						// 设置今日收入金币数
						todayCoins.setText(String.format(getResources()
								.getString(
										R.string.today_coins,
										userInfoManager.getLoginedUserInfo()
												.getTreasureInfo()
												.getTodayCoins())));

						siginBtn.setEnabled(false);
						siginBtn.setText(getResources().getString(
								R.string.already_sign));
						siginBtn.setTextColor(getResources().getColor(
								R.color.list_describle_color));

						if (LoginUserInfoManager.getInstance()
								.getLoginedUserInfo().getTreasureInfo()
								.getContinuSignDays() % 7 != 0)
						{
							// 弹签到成功的Dialog
							ConstantManager constantManager = ConstantManager
									.getInstance();
							double maxMoney = 0;
							// if (constantManager.isHaveConstantInfo())
							// {
							// 得到用户当前最多能兑换的金额
							double money = userInfoManager.getLoginedUserInfo()
									.getTreasureInfo().getCoinNum()
									* constantManager.getConstantInfo()
											.getExchangeRate();
							maxMoney = Math.floor(money * 10d) / 10;
							// } else
							// {
							// double money = userInfoManager
							// .getLoginedUserInfo().getTreasureInfo()
							// .getCoinNum()
							// * ConstantManager.EXCHANGE_RATE;
							// maxMoney = Math.floor(money * 10d) / 10;
							// }

							int surplus = 7 - LoginUserInfoManager
									.getInstance().getLoginedUserInfo()
									.getTreasureInfo().getContinuSignDays() % 7;

							showSignSuccessDialog(getActivity(),
									signInfo.getRewardCorn(),
									LoginUserInfoManager.getInstance()
											.getLoginedUserInfo()
											.getTreasureInfo().getCoinNum(),
									maxMoney, surplus, ConstantManager
											.getInstance().getConstantInfo()
											.getSign7daysCoins());
						} else
						{
							// 连续签到7天
							// 弹出连续签到7天的Dialog
							ConstantManager constantManager = ConstantManager
									.getInstance();
							double maxMoney = 0;
							// if (constantManager.isHaveConstantInfo())
							// {
							// 得到用户当前最多能兑换的金额
							double money = userInfoManager.getLoginedUserInfo()
									.getTreasureInfo().getCoinNum()
									* constantManager.getConstantInfo()
											.getExchangeRate();
							maxMoney = Math.floor(money * 10d) / 10;
							// } else
							// {
							// double money = userInfoManager
							// .getLoginedUserInfo().getTreasureInfo()
							// .getCoinNum()
							// * ConstantManager.EXCHANGE_RATE;
							// maxMoney = Math.floor(money * 10d) / 10;
							// }

							showSignTo7Dialog(getActivity(), ConstantManager
									.getInstance().getConstantInfo()
									.getSign7daysCoins(), LoginUserInfoManager
									.getInstance().getLoginedUserInfo()
									.getTreasureInfo().getCoinNum(), maxMoney);
						}

					} else if (rspSign.getRescode() == 2)
					{
						// 已签到
						siginBtn.setEnabled(false);
						siginBtn.setText(getResources().getString(
								R.string.already_sign));
						siginBtn.setTextColor(getResources().getColor(
								R.color.list_describle_color));
						Toast.makeText(
								getActivity(),
								getResources().getString(R.string.already_sign),
								Toast.LENGTH_SHORT).show();
					} else
					{
						DLog.i("lilijun",
								"签到失败，失败码----->>" + rspSign.getRescode());
						// 签到失败
						Toast.makeText(getActivity(), rspSign.getResmsg(),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e)
				{
					DLog.i(TAG, "解析签到返回数据发生异常#exception:", e);
				}
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		for (String action : rspPacket.getActionList())
		{
			if (action.equals(GET_USER_SINGN_HISTORY_REQUEST_TAG))
			{
				showErrorView();
			} else if (action.equals(SIGN_REQUEST_TAG))
			{
				loadingDialog.dismiss();
				Toast.makeText(getActivity(),
						getResources().getString(R.string.sign_failed),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		for (String action : actions)
		{
			if (action.equals(GET_USER_SINGN_HISTORY_REQUEST_TAG))
			{
				showErrorView();
			} else if (action.equals(SIGN_REQUEST_TAG))
			{
				loadingDialog.dismiss();
				Toast.makeText(getActivity(),
						getResources().getString(R.string.sign_failed),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void tryAgain()
	{
		super.tryAgain();
		if (userInfoManager.isHaveUserLogin())
		{
			setUserInfoData();
			// 获取用户签到历史记录列表数据
			doLoadData(
					Constants.UAC_API_URL,
					new String[]
					{ GET_USER_SINGN_HISTORY_REQUEST_TAG },
					new ByteString[]
					{ getUserSignHistoryRequestData(userInfo.getUserId(),
							userInfo.getUserToken()) }, "");
		}
	}

	/**
	 * 设置默认数据(用户未登录)
	 */
	private void setDefaultData()
	{
		userName.setText(getResources().getString(R.string.please_login));
		coinNum.setText(0 + "");
		// giftNum.setText(0 + "");TODO
		msgNum.setText(0 + "");
		msgNum.setCompoundDrawables(normallMsgImg, null, null, null);

		signDays.setText(" " + 0 + " ");

		// 设置今日收入金币数
		todayCoins.setText(String.format(getResources().getString(
				R.string.today_coins, "0")));

		// 设置签到一周表中所有数据为未签到状态
		for (int i = 0; i < weekSignCoins.length; i++)
		{
			weekSignCoins[i].setText(">_<");
			weekSignCoins[i].setCompoundDrawables(null, null, null, null);
			weekSignCoins[i].setTextColor(getResources().getColor(
					R.color.list_describle_color));
			weekName[i].setTextColor(getResources().getColor(
					R.color.list_describle_color));
		}
		siginBtn.setEnabled(true);
		siginBtn.setText(getResources()
				.getString(R.string.sign_in_for_everyday));
		siginBtn.setTextColor(getResources().getColor(R.color.white));
		imageLoaderManager.displayImage("", userIcon, options);
	}

	/**
	 * 当有用户登录了 则去设置用户的相关信息
	 */
	private void setUserInfoData()
	{
		if (!"".equals(userInfo.getNickName()))
		{
			userName.setText(userInfo.getNickName());
		} else
		{
			userName.setText(userInfo.getAccount());
		}
		// 设置我的礼包数量
		// giftNum.setText(userInfo.getGiftList().size() + "");TODO
		if (userInfo.getUnreadMsgNum() > 0)
		{
			// 有未读消息
			msgNum.setText(userInfo.getUnreadMsgNum() + "");
			msgNum.setCompoundDrawables(unreadMsgImg, null, null, null);
		} else
		{
			// 没有未读消息
			msgNum.setText(0 + "");
			msgNum.setCompoundDrawables(normallMsgImg, null, null, null);
		}
		coinNum.setText(ToolsUtil.getFormatPraiseCount(userInfo
				.getTreasureInfo().getCoinNum()) + "");
		todayCoins.setText(String.format(getResources().getString(
				R.string.today_coins,
				userInfoManager.getLoginedUserInfo().getTreasureInfo()
						.getTodayCoins())));
		imageLoaderManager.displayImage(userInfo.getIconUrl(), userIcon,
				options);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.treasure_button_lay_1:
			// 邀请好友
			if (LoginUserInfoManager.getInstance().isHaveUserLogin())
			{
				InviteFriendsActivity.startInviteFriendsActivity(getActivity(),
						action);
			} else
			{
				LoginActivity.startLoginActivity(getActivity(), action);
			}
			break;
		case R.id.treasure_button_lay_2:
			// 每日抽奖
			if (userInfoManager.isHaveUserLogin())
			{
				String lotteryUrl = Constants.LOTTERY_URL
						+ userInfoManager.getLoginedUserInfo().getUserId()
						+ "&v=" + ToolsUtil.getRandomNum(0, 1000);
				LotteryWebViewActivity.startActivity(getActivity(),
						getResources().getString(R.string.treasure_lottery),
						lotteryUrl, action);
			} else
			{
				LoginActivity.startLoginActivity(getActivity(), action);
			}
			break;
		case R.id.treasure_button_lay_3:
			// 我的钱包
			if (userInfoManager.isHaveUserLogin())
			{
				MyPurseActivity.startMyPurseActivity(getActivity(), action);
				String myPurseAction = DataCollectionManager
						.getAction(
								action,
								DataCollectionConstant.DATA_COLLECTION_CLICK_TREASURE_MY_PURSE_VALUE);
				// 自己后台的数据采集
				DataCollectionManager.getInstance().addRecord(myPurseAction);
				// 添加友盟的事件点击数据采集
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								getActivity(),
								action,
								DataCollectionConstant.EVENT_ID_CLICK_TREASURE_MY_PURSE_VALUE,
								null);

			} else
			{
				LoginActivity.startLoginActivity(getActivity(), action);
			}
			break;
		case R.id.treasure_button_lay_4:
			// 关注爱玩
			WeixinPublicAccountWebViewActivity.startActivity(getActivity(),
					getResources().getString(R.string.attention_aiwan),
					Constants.APP_API_URL + "/concern", action);
			break;
		case R.id.treasure_button_lay_5:
			// 收入排行
			GainsRankActivity.startGainsRankActivity(getActivity(), action);
			break;
		case R.id.treasure_button_lay_6:
			// 赚取记录
			if (LoginUserInfoManager.getInstance().isHaveUserLogin())
			{
				GainCoinsRecordActivity.startGainCoinsRecordActivity(
						getActivity(), action);
			} else
			{
				LoginActivity.startLoginActivity(getActivity(), action);
			}
			break;
		case R.id.treasure_my_coins_lay:
			// 我的金币部分
			if (userInfoManager.isHaveUserLogin())
			{
				MyPurseActivity.startMyPurseActivity(getActivity(), action);
				String coinsAction = DataCollectionManager
						.getAction(
								action,
								DataCollectionConstant.DATA_COLLECTION_CLICK_TREASURE_COINS_COUNT_VALUE);
				// 自己后台的数据采集
				DataCollectionManager.getInstance().addRecord(coinsAction);
				// 添加友盟的事件点击数据采集
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								getActivity(),
								action,
								DataCollectionConstant.EVENT_ID_CLICK_TREASURE_COINS_COUNT,
								null);
			} else
			{
				LoginActivity.startLoginActivity(getActivity(), action);
			}
			break;
		case R.id.treasure_my_gifts_lay:
			// // 我的礼包部分
			// if (userInfoManager.isHaveUserLogin())
			// {
			// MyGiftsActivity.startMyGiftsActivity(getActivity(), action);
			// } else
			// {
			// LoginActivity.startLoginActivity(getActivity(), action);
			// }
			// // 赚取记录
			// if (LoginUserInfoManager.getInstance().isHaveUserLogin())
			// {
			// GainCoinsRecordActivity.startGainCoinsRecordActivity(
			// getActivity(), action);
			// } else
			// {
			// LoginActivity.startLoginActivity(getActivity(), action);
			// }
			// 刷新金币
			if (LoginUserInfoManager.getInstance().isHaveUserLogin())
			{
				// refreshDialog.show();
				NetUtil.getLoginUserGiftsAndTreasureData(getActivity());
				String refreshAction = DataCollectionManager
						.getAction(
								action,
								DataCollectionConstant.DATA_COLLECTION_CLICK_TREASURE_REFRESH_VALUE);
				// 自己后台的数据采集
				DataCollectionManager.getInstance().addRecord(refreshAction);
				// 添加友盟的事件点击数据采集
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								getActivity(),
								action,
								DataCollectionConstant.EVENT_ID_CLICK_TREASURE_REFRESH_VALUE,
								null);
			} else
			{
				LoginActivity.startLoginActivity(getActivity(), action);
			}
			break;
		case R.id.treasure_my_msgs_lay:
			// 我的消息
			if (LoginUserInfoManager.getInstance().isHaveUserLogin())
			{
				MyMessageActivity.startMyMessageActivity(getActivity(), action);
			} else
			{
				LoginActivity.startLoginActivity(getActivity(), action);
			}
			break;
		case R.id.treasure_sign_in_btn:
			// 签到按钮
			if (userInfoManager.isHaveUserLogin())
			{
				loadingDialog.show();
				userInfo = userInfoManager.getLoginedUserInfo();
				doLoadData(
						Constants.UAC_API_URL,
						new String[]
						{ SIGN_REQUEST_TAG },
						new ByteString[]
						{ getSignRequestData(userInfo.getUserId(),
								userInfo.getUserToken()) }, "");
				String signAction = DataCollectionManager
						.getAction(
								action,
								DataCollectionConstant.DATA_COLLECTION_CLICK_TREASURE_SIGN_VALUE);
				// 自己后台的数据采集
				DataCollectionManager.getInstance().addRecord(signAction);
				// 添加友盟的事件点击数据采集
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								getActivity(),
								action,
								DataCollectionConstant.EVENT_ID_CLICK_TREASURE_SIGN_VALUE,
								null);
			} else
			{
				LoginActivity.startLoginActivity(getActivity(), action);
			}
			break;
		// case R.id.treasure_get_conin_lay:
		// // 赚取金币
		// GainCoinsActivity.startGainCoinsActivity(getActivity(), action);
		// break;
		// case R.id.treasure_lottery_lay:
		// // 每日抽奖
		// if (userInfoManager.isHaveUserLogin())
		// {
		// String lotteryUrl = Constants.LOTTERY_URL
		// + userInfoManager.getLoginedUserInfo().getUserId()
		// + "&v=" + ToolsUtil.getRandomNum(0, 1000);
		// DLog.i("lilijun", "lotteryUrl-------->>>" + lotteryUrl);
		// LotteryWebViewActivity.startActivity(getActivity(),
		// getResources().getString(R.string.treasure_lottery),
		// lotteryUrl, action);
		// } else
		// {
		// LoginActivity.startLoginActivity(getActivity(), action);
		// }
		// break;
		// case R.id.treasure_income_rank_lay:
		// // 收入排行
		// GainsRankActivity.startGainsRankActivity(getActivity(), action);
		// break;
		// case R.id.treasure_my_purse_lay:
		// // 我的钱包
		// if (userInfoManager.isHaveUserLogin())
		// {
		// MyPurseActivity.startMyPurseActivity(getActivity(), action);
		// } else
		// {
		// LoginActivity.startLoginActivity(getActivity(), action);
		// }
		// break;
		}
	}

	/**
	 * 获取用户签到历史记录信息的请求参数
	 * 
	 * @param userId
	 * @param userToken
	 * @return
	 */
	private ByteString getUserSignHistoryRequestData(int userId,
			String userToken)
	{
		ReqSignList.Builder builder = ReqSignList.newBuilder();
		builder.setUid(userId);
		builder.setUserToken(userToken);
		return builder.build().toByteString();
	}

	/**
	 * 获取签到的请求数据
	 * 
	 * @param userId
	 * @param userToken
	 * @return
	 */
	private ByteString getSignRequestData(int userId, String userToken)
	{
		ReqSign.Builder builder = ReqSign.newBuilder();
		builder.setUid(userId);
		builder.setUserToken(userToken);
		return builder.build().toByteString();
	}

	/**
	 * 显示签到成功的Dialog
	 * 
	 * @param signCoin
	 *            签到获得的金币数量
	 * @param curCoin
	 *            当前用户的总金币数
	 * @param money
	 *            当前用户的总金币可换取的金钱数量
	 * @param surplusContinueDay
	 *            再继续坚持多少天可以额外得到奖励
	 * @param extraMoney
	 *            额外奖励的金币数
	 */
	private void showSignSuccessDialog(Context context, int signCoin,
			int curCoin, double money, int surplusContinueDay, int extraCoin)
	{
		signSuccessDialog = new CenterDialog(context);
		signSuccessDialog.show();
		View view = View.inflate(context, R.layout.sign_success_dialog, null);
		view.setBackgroundColor(context.getResources().getColor(
				R.color.transparency));
		signSuccessDialog.setCenterView(view, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		signSuccessDialog.setTitleVisible(false);

		TextView topCoin = (TextView) view
				.findViewById(R.id.sign_success_top_coin);
		TextView contentMsg = (TextView) view
				.findViewById(R.id.sign_success_content_msg);
		TextView curCoinText = (TextView) view
				.findViewById(R.id.sign_success_bottom_text_1);
		TextView curMoney = (TextView) view
				.findViewById(R.id.sign_success_bottom_text_2);
		TextView surplusDays = (TextView) view
				.findViewById(R.id.sign_success_bottom_text_3);
		TextView extraCoins = (TextView) view
				.findViewById(R.id.sign_success_bottom_text_4);
		topCoin.setText(signCoin + "");
		String str = topCoin.getText().toString();
		contentMsg.setText(ToolsUtil.getFormatTextColor(
				String.format(
						context.getResources().getString(
								R.string.sign_success_content_msg), signCoin
								+ ""), 5, 5 + str.length(), "#fff719"));
		curCoinText.setText(String.format(
				context.getResources().getString(
						R.string.sign_success_cur_coins), curCoin + ""));
		curMoney.setText(String.format(
				context.getResources().getString(
						R.string.sign_success_cur_money), money + ""));
		surplusDays.setText(String.format(
				context.getResources().getString(
						R.string.sign_success_surplus_sign_days),
				surplusContinueDay + ""));
		extraCoins.setText(String.format(
				context.getResources().getString(
						R.string.sign_success_extra_coins), extraCoin + ""));

		// 3秒之后发送让Dialog消失的handler消息
		handler.sendEmptyMessageDelayed(DISMISS_SIGN_DIALOG_HANDER, 3000);
	}

	/**
	 * 连续签到7天 弹出的dialog
	 * 
	 * @param getCoin
	 *            获得的额外金币
	 * @param curCoin
	 *            当前总金币
	 * @param money
	 *            当前总金币可以兑换的钱
	 */
	private void showSignTo7Dialog(Context context, int getCoin, int curCoin,
			double money)
	{
		sign7daysDialog = new CenterDialog(context);
		sign7daysDialog.show();
		View view = View.inflate(context, R.layout.sign_to_7_dialog, null);
		view.setBackgroundColor(context.getResources().getColor(
				R.color.transparency));
		sign7daysDialog.setCenterView(view, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		sign7daysDialog.setTitleVisible(false);

		TextView topCoin = (TextView) view
				.findViewById(R.id.sign_to_7_top_coin);
		TextView contentMsg = (TextView) view
				.findViewById(R.id.sign_to_7_content_msg);
		TextView curCoinText = (TextView) view
				.findViewById(R.id.sign_to_7_bottom_text_1);
		TextView curMoney = (TextView) view
				.findViewById(R.id.sign_to_7_bottom_text_2);
		topCoin.setText(getCoin + "");
		String str = topCoin.getText().toString();
		contentMsg.setText(ToolsUtil.getFormatTextColor(
				String.format(
						context.getResources().getString(
								R.string.other_get_coins), getCoin + ""), 7,
				9 + str.length(), "#fff719"));
		curCoinText.setText(String.format(
				context.getResources().getString(
						R.string.sign_success_cur_coins), curCoin + ""));
		curMoney.setText(String.format(
				context.getResources().getString(
						R.string.sign_success_cur_money), money + ""));

		// 3秒之后发送让Dialog消失的handler消息
		handler.sendEmptyMessageDelayed(DISMISS_SIGN_DIALOG_HANDER, 3000);
	}

	private BroadcastReceiver treasureReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(Constants.ACTION_ACCOUNT_HAVE_MODIFY))
			{
				// // 不管有没有登录 都将刷新金币的dialog消失掉
				// refreshDialog.dismiss();
				// 帐号信息发生变化
				if (userInfoManager.isHaveUserLogin())
				{
					userInfo = userInfoManager.getLoginedUserInfo();
					setUserInfoData();
					// 获取用户签到历史记录列表数据
					doLoadData(
							Constants.UAC_API_URL,
							new String[]
							{ GET_USER_SINGN_HISTORY_REQUEST_TAG },
							new ByteString[]
							{ getUserSignHistoryRequestData(
									userInfo.getUserId(),
									userInfo.getUserToken()) }, "");
				} else
				{
					setDefaultData();
					// 显示中间视图(默认数据)
					loadingView.setVisibilyView(false);
					errorViewLayout.setVisibility(View.GONE);
					centerViewLayout.setVisibility(View.VISIBLE);
					boolean isFromGetTreasure = intent.getBooleanExtra(
							"is_from_get_treasure", false);
					if (isFromGetTreasure)
					{
						// 是财富信息获取完成(失败)之后发送过来的广播
						Toast.makeText(
								getActivity(),
								getActivity().getResources().getString(
										R.string.login_failed),
								Toast.LENGTH_SHORT).show();
					}
				}
			} else if (intent.getAction().equals(
					Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY))
			{
				// 用户信息发生变化
				if (userInfoManager.isHaveUserLogin())
				{
					userInfo = userInfoManager.getLoginedUserInfo();
					setUserInfoData();
				} else
				{
					setDefaultData();
					// 显示中间视图(默认数据)
					loadingView.setVisibilyView(false);
					errorViewLayout.setVisibility(View.GONE);
					centerViewLayout.setVisibility(View.VISIBLE);
				}
			} else if (intent.getAction().equals(
					Constants.ACTION_NO_PIC_MODEL_CHANGE))
			{
				if (userInfoManager.isHaveUserLogin())
				{
					userInfo = userInfoManager.getLoginedUserInfo();
					imageLoaderManager.displayImage(userInfo.getIconUrl(),
							userIcon, options);
				} else
				{
					imageLoaderManager.displayImage("", userIcon, options);
				}
			}
			// else if (intent.getAction().equals(
			// Constants.ACTION_GET_SYSTEM_AND_USER_MSG_FINISH))
			// {
			// // 重新获得了用户消息广播
			// if (userInfoManager.isHaveUserLogin())
			// {
			// userInfo = userInfoManager.getLoginedUserInfo();
			// setUserInfoData();
			// } else
			// {
			// setDefaultData();
			// // 显示中间视图(默认数据)
			// loadingView.setVisibilyView(false);
			// errorViewLayout.setVisibility(View.GONE);
			// centerViewLayout.setVisibility(View.VISIBLE);
			// }
			// }
		}
	};

}
