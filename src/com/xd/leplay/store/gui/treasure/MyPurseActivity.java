package com.xd.leplay.store.gui.treasure;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ConstantManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqWithdrawingWeixin;
import com.xd.leplay.store.model.proto.Uac.RspWithdrawingWeixin;
import com.xd.leplay.store.util.NetUtil;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.CenterDialog;
import com.google.protobuf.ByteString;

/**
 * 我的钱包界面
 * 
 * @author lilijun
 *
 */
public class MyPurseActivity extends BaseActivity implements OnClickListener
{
	private static final String TAG = "MyPurseActivity";
	/** 我的金币 */
	private TextView myCoinNum;

	/** 申请提现按钮 */
	private Button applyCashBtn;

	/** 金币兑换人民币规则 */
	private TextView exchangeRule;

	/** 当前可提现金额 */
	private TextView curMoney;

	/** 已提现金额 */
	private TextView gotMoney;

	/** 收入类型明细列表Lay */
	private RelativeLayout[] incomeTypeLays = new RelativeLayout[4];

	/** 收入明细具体值 */
	private TextView[] incomeMoneys = new TextView[4];

	/** 根据当前用户的所拥有的金币数算出的最大可提现的金额 */
	private double maxMoney;

	// /** 用户提现所有金额所花费的最多的金币数量 */
	// private int maxCoin;

	/** 1块钱人民币所花费的金币数量 */
	private int exchangeCoin;

	private ConstantManager constantManager = null;

	private LoginUserInfoManager userInfoManager = null;

	// private LoadingDialog loadingDialog = null;

	/** 用户提现成功时所花费的金币数 */
	private int takeCoins;

	/** 提现成功的金额 */
	private String takeMoney;

	/** 是否跳转到微信 */
	private boolean isToWeiXin = false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null)
		{
			isToWeiXin = savedInstanceState.getBoolean("isToWeiXin", false);
			DLog.i("lilijun",
					"onCreate(),有跳转保存的savedInstanceState,isToWeiXin----->"
							+ isToWeiXin);
		}
	}

	@Override
	protected void initView()
	{
		if (!LoginUserInfoManager.getInstance().isHaveUserLogin())
		{
			finish();
			return;
		}
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_TREASURE_MY_PURSE_VALUE);
		DataCollectionManager.getInstance().addRecord(action);
		// titleView.setTitleBackground(R.drawable.my_purse_top_bg);
		titleView.setTitleName(getResources().getString(
				R.string.treasure_my_purse));
		// titleView.setBackImgRes(R.drawable.back_img);
		// titleView.setTitleColor(getResources().getColor(R.color.white));
		titleView.setBottomLineVisible(false);
		titleView
				.setRightTextBtnLeftDrawableResource(R.drawable.my_purse_cash_history);
		titleView.setRightTextBtnName(getResources().getString(
				R.string.get_cash_history));
		titleView.setRightTextBtnColor(Color.parseColor("#bd6940"));
		titleView.setRightTextBtnOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// 提现记录点击事件
				GotCashRewardActivity.startGotCashRewardActivity(
						MyPurseActivity.this, action);
			}
		});
		titleView.setRightFirstImgVisible(false);
		titleView.setRightSecondImgVisible(false);
		loadingView.setVisibilyView(false);

		constantManager = ConstantManager.getInstance();
		userInfoManager = LoginUserInfoManager.getInstance();

		// if (constantManager.isHaveConstantInfo())
		// {
		// // 得到用户当前最多能兑换的金额
		// double money = userInfoManager.getLoginedUserInfo().getTreasureInfo()
		// .getCoinNum()
		// * constantManager.getConstantInfo().getExchangeRate();
		// maxMoney = Math.floor(money * 10d) / 10;
		// maxCoin = (int) (maxMoney / constantManager.getConstantInfo()
		// .getExchangeRate());
		exchangeCoin = (int) (1 / constantManager.getConstantInfo()
				.getExchangeRate());
		// } else
		// {
		// double money = userInfoManager.getLoginedUserInfo()
		// .getTreasureInfo().getCoinNum()
		// * ConstantManager.EXCHANGE_RATE;
		// maxMoney = Math.floor(money * 10d) / 10;
		// maxCoin = (int) (maxMoney / ConstantManager.EXCHANGE_RATE);
		// exchangeCoin = (int) (1 / ConstantManager.EXCHANGE_RATE);
		// DLog.i("lilijun", "maxMoney 2-------->>" + maxMoney);
		// DLog.i("lilijun", "exchangeCoin(服务器)-------->>" + exchangeCoin);
		// DLog.i("lilijun", "换算汇率(本地的)-------->>"
		// + ConstantManager.EXCHANGE_RATE);
		// }

		// loadingDialog = new LoadingDialog(this, getResources().getString(
		// R.string.loading));
		// loadingDialog.setCanceledOnTouchOutside(false);

		setCenterView(R.layout.my_purse_activity);

		exchangeRule = (TextView) findViewById(R.id.my_purse_exchange_rule);
		exchangeRule.setText(String.format(
				getResources().getString(R.string.exchange_rule), exchangeCoin
						+ ""));

		myCoinNum = (TextView) findViewById(R.id.my_purse_my_coins);
		// myCoinNum.setText(LoginUserInfoManager.getInstance()
		// .getLoginedUserInfo().getTreasureInfo().getCoinNum()
		// + "");

		curMoney = (TextView) findViewById(R.id.my_purse_cur_money);
		// curMoney.setText(String.format(getResources().getString(R.string.yuan),
		// maxMoney + ""));
		gotMoney = (TextView) findViewById(R.id.my_purse_got_money);
		gotMoney.setText(String.format(getResources().getString(R.string.yuan),
				LoginUserInfoManager.getInstance().getLoginedUserInfo()
						.getTreasureInfo().getWithdrawMoney()
						+ ""));
		setChangeData();

		applyCashBtn = (Button) findViewById(R.id.my_pursh_apply_cash_btn);
		applyCashBtn.setOnClickListener(this);

		incomeTypeLays[0] = (RelativeLayout) findViewById(R.id.my_purse_income_type_1);
		incomeTypeLays[0].setOnClickListener(this);
		incomeTypeLays[1] = (RelativeLayout) findViewById(R.id.my_purse_income_type_2);
		incomeTypeLays[1].setOnClickListener(this);
		incomeTypeLays[2] = (RelativeLayout) findViewById(R.id.my_purse_income_type_3);
		incomeTypeLays[2].setOnClickListener(this);
		incomeTypeLays[3] = (RelativeLayout) findViewById(R.id.my_purse_income_type_4);
		incomeTypeLays[3].setOnClickListener(this);

		incomeMoneys[0] = (TextView) findViewById(R.id.my_purse_income_money_1);
		incomeMoneys[1] = (TextView) findViewById(R.id.my_purse_income_money_2);
		incomeMoneys[2] = (TextView) findViewById(R.id.my_purse_income_money_3);
		incomeMoneys[3] = (TextView) findViewById(R.id.my_purse_income_money_4);

		incomeMoneys[0].setText(String.format(
				getResources().getString(R.string.yuan), LoginUserInfoManager
						.getInstance().getLoginedUserInfo().getTreasureInfo()
						.getDownloadAndTaskMoney()
						+ ""));
		incomeMoneys[1].setText(String.format(
				getResources().getString(R.string.yuan), LoginUserInfoManager
						.getInstance().getLoginedUserInfo().getTreasureInfo()
						.getSignAndLotteryMoney()
						+ ""));
		incomeMoneys[2].setText(String.format(
				getResources().getString(R.string.yuan), LoginUserInfoManager
						.getInstance().getLoginedUserInfo().getTreasureInfo()
						.getInviteMoney()
						+ ""));
		incomeMoneys[3].setText(String.format(
				getResources().getString(R.string.yuan), LoginUserInfoManager
						.getInstance().getLoginedUserInfo().getTreasureInfo()
						.getOtherMoney()
						+ ""));

		IntentFilter filter = new IntentFilter(
				Constants.ACTION_ACCOUNT_HAVE_MODIFY);
		registerReceiver(receiver, filter);
	}

	/**
	 * 设置可变的数据
	 */
	private void setChangeData()
	{
		myCoinNum.setText(LoginUserInfoManager.getInstance()
				.getLoginedUserInfo().getTreasureInfo().getCoinNum()
				+ "");
		// 得到用户当前最多能兑换的金额
		double money = userInfoManager.getLoginedUserInfo().getTreasureInfo()
				.getCoinNum()
				* constantManager.getConstantInfo().getExchangeRate();
		maxMoney = Math.floor(money * 10d) / 10;
		curMoney.setText(String.format(getResources().getString(R.string.yuan),
				maxMoney + ""));
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		// loadingDialog.dismiss();
		List<String> actions = rspPacket.getActionList();
		for (String actionNet : actions)
		{
			if (actionNet.equals("RspWithdrawingWeixin"))
			{
				try
				{
					RspWithdrawingWeixin rspWithdrawingWeixin = RspWithdrawingWeixin
							.parseFrom(rspPacket.getParams(0));
					// 0=成功,1=系统响应错误,2=
					// 用户不存在或已经被禁用,3=token错误,4=兑换金币不足,5=未达到最低提取额度,6=还未绑定微信账号
					if (rspWithdrawingWeixin.getRescode() == 0)
					{
						// 成功,减去金币数
						userInfoManager
								.getLoginedUserInfo()
								.getTreasureInfo()
								.setCoinNum(
										userInfoManager.getLoginedUserInfo()
												.getTreasureInfo().getCoinNum()
												- takeCoins);
						ToolsUtil.saveCachDataToFile(MyPurseActivity.this,
								Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME,
								userInfoManager.getLoginedUserInfo());
						setChangeData();

						takeCoins = 0;
						takeMoney = "";
						// 发送用户的相关信息发生了改变的广播
						sendBroadcast(new Intent(
								Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY));
						GainMonneySuccessShareActivity
								.startGainMonneySuccessShareActivity(
										MyPurseActivity.this, takeMoney,
										actionNet);
						Toast.makeText(
								MyPurseActivity.this,
								getResources().getString(
										R.string.apply_cash_successful),
								Toast.LENGTH_SHORT).show();
						finish();
					} else if (rspWithdrawingWeixin.getRescode() == 3)
					{
						// userToken错误
						Toast.makeText(MyPurseActivity.this,
								getResources().getString(R.string.re_login),
								Toast.LENGTH_SHORT).show();
						userInfoManager.exitLogin();
						finish();
					} else
					{
						// 其他错误
						Toast.makeText(MyPurseActivity.this,
								rspWithdrawingWeixin.getResmsg(),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e)
				{
					DLog.e(TAG, "解析获取短信验证码时发生异常#Excepton:", e);
				}
				takeCoins = 0;
				takeMoney = "";
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		// loadingDialog.dismiss();
		takeCoins = 0;
		takeMoney = "";
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (isToWeiXin)
		{
			// 之前从此界面曾经跳转到过微信
			// 再次去获取财富信息
			NetUtil.getLoginUserGiftsAndTreasureData(MyPurseActivity.this);
			isToWeiXin = false;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putBoolean("isToWeiXin", isToWeiXin);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy()
	{
		try
		{
			unregisterReceiver(receiver);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		super.onDestroy();
	};

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.my_pursh_apply_cash_btn:
			// 申请提现按钮
			// 申请提现 按钮的点击事件
			DataCollectionManager
					.getInstance()
					.addRecord(
							DataCollectionManager
									.getAction(
											action,
											DataCollectionConstant.DATA_COLLECTION_TREASURE_MY_PURSE_CLICK_APPLY_FOR_CASH_BTN_VALUE));
			DataCollectionManager
					.getInstance()
					.addYouMengEventRecord(
							MyPurseActivity.this,
							action,
							DataCollectionConstant.EVENT_ID_TREASURE_MY_PURSE_CLICK_APPLY_FOR_CASH_BTN,
							null);

			if ("".equals(LoginUserInfoManager.getInstance()
					.getLoginedUserInfo().getOpenId().trim())
					|| LoginUserInfoManager.getInstance().getLoginedUserInfo()
							.getOpenId() == null)
			{
				// 未绑定微信
				showBindWexinDialog();
			} else
			{
				// 绑定了微信
				showInputMoneyDialog();
			}
			break;
		case R.id.my_purse_income_type_1:
			// 任务下载收入
			GainCoinsRecordDetailActivity.startGainCoinsRecordDetailActivity(
					MyPurseActivity.this, action, 0);
			break;
		case R.id.my_purse_income_type_2:
			// 签到抽奖收入
			GainCoinsRecordDetailActivity.startGainCoinsRecordDetailActivity(
					MyPurseActivity.this, action, 1);
			break;
		case R.id.my_purse_income_type_3:
			// 邀请奖励收入
			GainCoinsRecordDetailActivity.startGainCoinsRecordDetailActivity(
					MyPurseActivity.this, action, 2);
			break;
		case R.id.my_purse_income_type_4:
			// 其他奖励收入
			GainCoinsRecordDetailActivity.startGainCoinsRecordDetailActivity(
					MyPurseActivity.this, action, 3);
			break;
		}
	}

	/**
	 * 显示绑定微信的Dialog
	 */
	private void showBindWexinDialog()
	{
		// 绑定微信Dialog
		final CenterDialog bindingWeixinDialog = new CenterDialog(
				MyPurseActivity.this);
		bindingWeixinDialog.show();
		bindingWeixinDialog.setTitleVisible(false);
		bindingWeixinDialog.setCenterView(R.layout.binding_weixin_dialog);
		Button bingdingWeixinBtn = (Button) bindingWeixinDialog
				.findViewById(R.id.my_purse_binding_weixin_btn);
		TextView bindWxNotice = (TextView) bindingWeixinDialog
				.findViewById(R.id.bindWxNotice);
		bindWxNotice.setText(MyPurseActivity.this.getResources().getString(R.string.first_got_money_knows));
		bingdingWeixinBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// // 立即绑定微信
				// WeiXinAPIManager.getInstance().wxLogin(
				// new OnWeiXinResponsLinstener()
				// {
				//
				// @Override
				// public void onSuccess()
				// {
				// runOnUiThread(new Runnable()
				// {
				// @Override
				// public void run()
				// {
				// // // 发送用户的相关信息发生了改变的广播
				// // sendBroadcast(new Intent(
				// // Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY));
				//
				// Toast.makeText(
				// MyPurseActivity.this,
				// getResources().getString(
				// R.string.bind_success),
				// Toast.LENGTH_SHORT).show();
				// loadingDialog.dismiss();
				// showInputMoneyDialog();
				// }
				// });
				// }
				//
				// @Override
				// public void onError(final String msg)
				// {
				// runOnUiThread(new Runnable()
				// {
				// @Override
				// public void run()
				// {
				// Toast.makeText(MyPurseActivity.this,
				// msg, Toast.LENGTH_SHORT).show();
				// loadingDialog.dismiss();
				//
				// }
				// });
				// }
				// }, "1");
				// bindingWeixinDialog.dismiss();
				// loadingDialog.show();

				// 前往微信
				bindingWeixinDialog.dismiss();
				// 打开微信
				isToWeiXin = ToolsUtil.openSoftware(MyPurseActivity.this,
						"com.tencent.mm");
				if (!isToWeiXin)
				{
					Toast.makeText(
							MyPurseActivity.this,
							getResources().getString(
									R.string.no_installed_weixin),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 显示输入金额的dialog
	 */
	private void showInputMoneyDialog()
	{
		// 输入申请金额Dialog
		final CenterDialog inputMoneyDialog = new CenterDialog(
				MyPurseActivity.this);
		inputMoneyDialog.show();
		inputMoneyDialog.setTitleVisible(false);
		inputMoneyDialog.setCenterView(R.layout.input_money_dialog);
		// 提现金额输入框
		final EditText moneyInput = (EditText) inputMoneyDialog
				.findViewById(R.id.my_purse_exchange_money_input);
		moneyInput.setHintTextColor(Color.parseColor("#dddddd"));
		if (maxMoney >= 5)
		{
			// 最大提取金额必须大于5元
			moneyInput.setHint(String.format(
					getResources().getString(R.string.max_apply_cash), maxMoney
							+ ""));
		} else
		{
			// 您还未达到5元的最低提现金额
			moneyInput.setHint(String.format(
					getResources().getString(R.string.can_not_apply_cash),
					maxMoney + ""));
		}

		moneyInput.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
				String input = moneyInput.getText().toString();
				int decStart = input.indexOf(".");
				if (decStart >= 0)
				{
					// 有小数点了
					if (input.length() - 1 > decStart + 1)
					{
						DLog.i("lilijun", "已经输入了小数点,且超过小数点后一位了！");
						// 超过小数点后一位了
						moneyInput.setText(input.substring(0, decStart + 2));
						moneyInput.setSelection(decStart + 2);
					}
					// if (input.indexOf(".", input.indexOf(".") + 1) > 0)
					// {
					// DLog.i("lilijun", "已经输入了小数点！！！！");
					// moneyInput.setText(input.substring(0, input.toString()
					// .length() - 1));
					// moneyInput.setSelection(input.length());
					// }

				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s)
			{
				// TODO Auto-generated method stub

			}
		});

		// 提交按钮
		Button submitBtn = (Button) inputMoneyDialog
				.findViewById(R.id.my_purse_submit_btn);
		submitBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (maxMoney < 5)
				{
					// 最大金额
					Toast.makeText(
							MyPurseActivity.this,
							getResources().getString(
									R.string.can_not_apply_cash1),
							Toast.LENGTH_SHORT).show();
				} else
				{
					String moneyStr = moneyInput.getText().toString().trim();
					if ("".equals(moneyStr))
					{
						Toast.makeText(
								MyPurseActivity.this,
								getResources().getString(
										R.string.input_apply_cash_money),
								Toast.LENGTH_SHORT).show();
						return;
					}
					double inputMoney = Math.floor(Double.parseDouble(moneyStr) * 10d) / 10;
					if (inputMoney < 5)
					{
						// 每次提现最低为5元
						Toast.makeText(
								MyPurseActivity.this,
								getResources().getString(
										R.string.can_not_apply_cash2),
								Toast.LENGTH_SHORT).show();
						return;
					}
					if (inputMoney > maxMoney)
					{
						// 如果输入的金额大于最多金额
						Toast.makeText(
								MyPurseActivity.this,
								String.format(
										getResources().getString(
												R.string.max_apply_cash),
										maxMoney + ""), Toast.LENGTH_SHORT)
								.show();
						return;
					} else
					{
						// if (constantManager.isHaveConstantInfo())
						// {
						// 花费的金币数
						// takeCoin = (int) (inputMoney /
						// constantManager
						// .getConstantInfo().getExchangeRate());
						// } else
						// {
						// // 花费的金币数
						// takeCoin = (int) (inputMoney /
						// ConstantManager.EXCHANGE_RATE);
						// }
						// DLog.i("lilijun", "takeCoin------->>>"
						// + takeCoin);
						// ApplyCashActivity.startApplyCashActivity(
						// MyPurseActivity.this,
						// inputMoney,
						// takeCoin,
						// DataCollectionManager
						// .getAction(
						// action,
						// DataCollectionConstant.DATA_COLLECTION_TREASURE_MY_PURSE_CLICK_APPLY_FOR_CASH_BTN_VALUE));
						// finish();

						// 提交提现金额
						takeCoins = (int) (inputMoney / constantManager
								.getConstantInfo().getExchangeRate());
						takeMoney = inputMoney + "";
						DLog.i("lilijun", "提交提现金额,金额---------->>" + inputMoney
								+ "  花费金币----->>>" + takeCoins);
						ReqWithdrawingWeixin.Builder builder = ReqWithdrawingWeixin
								.newBuilder();
						builder.setUid(userInfoManager.getLoginedUserInfo()
								.getUserId());
						builder.setUserToken(userInfoManager
								.getLoginedUserInfo().getUserToken());
						builder.setCorn(takeCoins);
						doLoadData(Constants.UAC_API_URL, new String[]
						{ "ReqWithdrawingWeixin" }, new ByteString[]
						{ builder.build().toByteString() }, "");
						// loadingDialog.show();
						inputMoneyDialog.dismiss();
					}
				}
			}
		});
	}

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (Constants.ACTION_ACCOUNT_HAVE_MODIFY.equals(intent.getAction()))
			{
				if (!LoginUserInfoManager.getInstance().isHaveUserLogin())
				{
					// 登录信息已过期
					Toast.makeText(
							context,
							context.getResources().getString(R.string.re_login),
							Toast.LENGTH_SHORT).show();
					// 如果没登录 就直接finish掉当前界面
					finish();
				}
			}
		}
	};

	public static void startMyPurseActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				MyPurseActivity.class), action);
	}

}
