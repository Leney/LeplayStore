package com.xd.leplay.store.gui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ConstantManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.control.OnWeiXinResponsLinstener;
import com.xd.leplay.store.control.WeiXinAPIManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqLoginOrReg;
import com.xd.leplay.store.model.proto.Uac.ReqSendSMSCode;
import com.xd.leplay.store.model.proto.Uac.RspLogUser;
import com.xd.leplay.store.model.proto.Uac.RspSendSMSCode;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.CenterDialog;
import com.xd.leplay.store.view.LoadingDialog;

import java.util.HashMap;
import java.util.List;

/**
 * 注册登录界面
 * 
 * @author lilijun
 *
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
		OnWeiXinResponsLinstener
{
	private final String TAG = "RegisterActivity";
	/** 手机输入框、验证码输入框 */
	private EditText phoneInput, codeInput;
	/** 获取验证码按钮、注册按钮 */
	private Button getCodeBtn, registerBtn;
	/** 获取验证码的请求TAG */
	private final String GET_CODE_REQUEST_TAG = "ReqSendSMSCode";
	/** 获取验证码的响应TAG */
	private final String GET_CODE_RSPONSE_TAG = "RspSendSMSCode";
	/** 注册和登录的请求TAG */
	private final String REGISTER_REQUEST_TAG = "ReqLoginOrReg";
	/** 注册和登录的响应TAG */
	private final String REGISTER_RSPONSE_TAG = "RspLoginOrReg";

	/** 获取验证码的类型 1=注册,2=找回密码,3=申请提现,4=确认提现,5=更改绑定手机号码 */
	private final int GET_CODE_TYPE = 1;

	/** 开始计时的Handler消息 */
	private final int CHANGE_TIME = 0;

	private int timeCount = 60;

	/** 标识 当前网络请求是否是在获取验证码 */
	private boolean isGetCode = true;

	/** 正在注册的弹出框 */
	private LoadingDialog loadingDialog = null;

//	/** 微信登录 */
//	private LinearLayout wxLoginLay = null;

	/** 微信登录邀请码 */
	private String wxInviteCode = "";

	/** handler的倒计时线程 */
	private Runnable countDownRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			handler.sendEmptyMessage(CHANGE_TIME);
			handler.postDelayed(this, 1000);
			if (timeCount == 0)
			{
				handler.removeCallbacks(this);
			}
		}
	};

	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what == CHANGE_TIME)
			{
				if (timeCount > 0)
				{
					/*
					 * getCodeBtn.setTextColor(getResources().getColor(
					 * R.color.reget_code_text_color));
					 */
					getCodeBtn.setEnabled(false);

					getCodeBtn.setText(String.format(
							getResources().getString(R.string.reget_code),
							timeCount + ""));
					timeCount--;
				} else
				{
					/*
					 * getCodeBtn.setTextColor(getResources().getColor(
					 * R.color.list_describle_color));
					 */
					getCodeBtn.setEnabled(true);
					getCodeBtn.setText(getResources().getString(
							R.string.register_get_verification_code));
					timeCount = 60;
				}
			}
		};
	};

	@Override
	protected void initView()
	{
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_LOGIN_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		titleView.setRightLayVisible(false);
		titleView.setTitleName(getResources().getString(R.string.register));
		titleView.setBottomLineVisible(true);
		loadingView.setVisibilyView(false);
		setCenterView(R.layout.register_activity);

		loadingDialog = new LoadingDialog(this, getResources().getString(
				R.string.registting));

		phoneInput = (EditText) findViewById(R.id.register_phone_edit);
		codeInput = (EditText) findViewById(R.id.register_verification_code_edit);
		phoneInput.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		codeInput.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		getCodeBtn = (Button) findViewById(R.id.register_get_verification_code_btn);
		getCodeBtn.setOnClickListener(this);
		registerBtn = (Button) findViewById(R.id.register_register_btn);
		registerBtn.setOnClickListener(this);
//		wxLoginLay = (LinearLayout) findViewById(R.id.wx_login_lay);
//		wxLoginLay.setOnClickListener(this);

		WeiXinAPIManager.getInstance().setLinstener(this);
	}

	/**
	 * 微信登录
	 */
	private void wxLogin()
	{
		// WeiXinAPIManager.getInstance().wxLogin(new OnWeiXinResponsLinstener()
		// {
		//
		// @Override
		// public void onSuccess()
		// {
		// runOnUiThread(new Runnable()
		// {
		//
		// @Override
		// public void run()
		// {
		// loadingDialog.dismiss();
		// // 添加友盟注册成功的数据统计
		// HashMap<String, String> values = new HashMap<String, String>();
		// values.put("user_id", LoginUserInfoManager
		// .getInstance().getLoginedUserInfo().getUserId()
		// + "");
		// DataCollectionManager
		// .getInstance()
		// .addYouMengEventRecord(
		// LoginActivity.this,
		// action,
		// DataCollectionConstant.EVENT_ID_REGISTER_SUCCESS,
		// values);
		// // intent.putExtra("is_login_in",
		// // true);
		// sendBroadcast(new Intent(Constants.ACTION_LOGIN_SUCCESS));
		// finish();
		// }
		//
		// });
		// }
		//
		// @Override
		// public void onError(final String msg)
		// {
		//
		// runOnUiThread(new Runnable()
		// {
		//
		// @Override
		// public void run()
		// {
		// loadingDialog.dismiss();
		// Toast.makeText(LoginActivity.this, msg,
		// Toast.LENGTH_SHORT).show();
		// }
		// });
		//
		// }
		//
		// @Override
		// public void onSubmitData()
		// {
		// runOnUiThread(new Runnable()
		// {
		//
		// @Override
		// public void run()
		// {
		// loadingDialog.show();
		// }
		// });
		// }
		// }, "0", wxInviteCode);
		WeiXinAPIManager.getInstance().wxLogin2("0", wxInviteCode);
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<String> actions = rspPacket.getActionList();
		for (String actionStr : actions)
		{
			if (GET_CODE_RSPONSE_TAG.equals(actionStr))
			{
				String msg = parseGetCodeResult(rspPacket);
				if (!"success".equals(msg))
				{
					// 获取验证码失败
					Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT)
							.show();
					handler.removeCallbacks(countDownRunnable);
					/*
					 * getCodeBtn.setTextColor(getResources().getColor(
					 * R.color.list_describle_color));
					 */
					getCodeBtn.setEnabled(true);
					getCodeBtn.setText(getResources().getString(
							R.string.register_get_verification_code));
					timeCount = 60;
				} else if ("error".equals(msg))
				{
					// 解析获取验证码的返回结果出现异常
				} else
				{
					// 获取验证码成功 暂时不做任何操作
					codeInput.requestFocus();
				}
				isGetCode = false;
			} else if (REGISTER_RSPONSE_TAG.equals(actionStr))
			{
				String status = parseRegisterResult(rspPacket);
				loadingDialog.dismiss();
				if ("success".equals(status))
				{
					if (LoginUserInfoManager.getInstance().getLoginedUserInfo()
							.getLastLoginTime() == null
							|| "".equals(LoginUserInfoManager.getInstance()
									.getLoginedUserInfo().getLastLoginTime()
									.trim()))
					{
						DLog.i("lilijun", "注册并登录成功，发送广播！！");
						// 添加友盟注册成功的数据统计
						HashMap<String, String> values = new HashMap<String, String>();
						values.put("user_id", LoginUserInfoManager
								.getInstance().getLoginedUserInfo().getUserId()
								+ "");
						DataCollectionManager
								.getInstance()
								.addYouMengEventRecord(
										LoginActivity.this,
										action,
										DataCollectionConstant.EVENT_ID_REGISTER_SUCCESS,
										values);
						// 添加自己后台数据统计
						String registerSuccessAction = DataCollectionManager
								.getAction(
										action,
										DataCollectionConstant.DATA_COLLECTION_REGISTER_SUCCESS_VALUE);
						DataCollectionManager.getInstance().addRecord(
								registerSuccessAction);
					}
					Intent intent = new Intent(Constants.ACTION_LOGIN_SUCCESS);
					// intent.putExtra("is_login_in", true);
					sendBroadcast(intent);
					finish();
				} else if ("error".equals(status))
				{
					// 解析注册返回结果时 出现异常
					DLog.i(TAG, "解析注册返回结果时  出现异常");
				} else
				{
					DLog.i(TAG, "注册失败: " + status);
					Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		if (isGetCode)
		{
			Toast.makeText(LoginActivity.this,
					getResources().getString(R.string.get_code_failed),
					Toast.LENGTH_SHORT).show();
			isGetCode = false;
			handler.removeCallbacks(countDownRunnable);
			/*
			 * getCodeBtn.setTextColor(getResources().getColor(
			 * R.color.list_describle_color));
			 */
			getCodeBtn.setEnabled(true);
			getCodeBtn.setText(getResources().getString(
					R.string.register_get_verification_code));
			timeCount = 60;
		} else
		{
			Toast.makeText(LoginActivity.this,
					getResources().getString(R.string.login_failed),
					Toast.LENGTH_SHORT).show();
			loadingDialog.dismiss();
		}
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		if (isGetCode)
		{
			Toast.makeText(LoginActivity.this,
					getResources().getString(R.string.get_code_failed),
					Toast.LENGTH_SHORT).show();
			isGetCode = false;
			handler.removeCallbacks(countDownRunnable);
			/*
			 * getCodeBtn.setTextColor(getResources().getColor(
			 * R.color.list_describle_color));
			 */
			getCodeBtn.setEnabled(true);
			getCodeBtn.setText(getResources().getString(
					R.string.register_get_verification_code));
			timeCount = 60;
		} else
		{
			Toast.makeText(LoginActivity.this,
					getResources().getString(R.string.register_failed),
					Toast.LENGTH_SHORT).show();
			loadingDialog.dismiss();
		}
	}

	/**
	 * 得到获取手机验证码的请求参数
	 * 
	 * @param phoneNum
	 *            手机号码
	 * @param type
	 *            获取验证码的类型
	 * @return
	 */
	private ByteString getRequestGetCodeData(String phoneNum, int type)
	{
		ReqSendSMSCode.Builder builder = ReqSendSMSCode.newBuilder();
		builder.setPhoneNo(phoneNum);
		builder.setBuzType(type);
		return builder.build().toByteString();
	}

	/**
	 * 解析获取短信验证码的结果
	 * 
	 * @param rspPacket
	 * @return
	 * @throws Exception
	 */
	private String parseGetCodeResult(RspPacket rspPacket)
	{
		RspSendSMSCode rspSendSMSCode;
		try
		{
			rspSendSMSCode = RspSendSMSCode.parseFrom(rspPacket.getParams(0));
			// 0=成功,1=系统响应出错,2=未注册业务类型,3=获取短信失败
			if (rspSendSMSCode.getRescode() == 0)
			{
				return "success";
			} else
			{
				return rspSendSMSCode.getResmsg();
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "parseGetCodeResult()#Excepton:", e);
			return "error";
		}
	}

	/**
	 * 获取注册的请求参数
	 * 
	 * @param phone
	 * @param inviteCode
	 * @param smsCode
	 * @return
	 */
	private ByteString getRegisterRequestData(String phone, String inviteCode,
			int userType, String smsCode)
	{
		ReqLoginOrReg.Builder builder = ReqLoginOrReg.newBuilder();
		builder.setAccountName(phone);
		builder.setInviteCode(inviteCode);
		builder.setUserType(userType);
		builder.setSmsCode(smsCode);
		return builder.build().toByteString();
	}

	/**
	 * 获取微信登录的请求参数
	 * 
	 * @param openid
	 * @param inviteCode
	 * @param userType
	 * @param headPicUrl
	 * @param nickName
	 * @return
	 */
	private ByteString getWXRegisterRequestData(String openid,
			String inviteCode, int userType, String headPicUrl, String nickName)
	{
		ReqLoginOrReg.Builder builder = ReqLoginOrReg.newBuilder();
		builder.setAccountName(openid);
		builder.setInviteCode(inviteCode);
		builder.setUserType(userType);
		builder.setHeadPicUrl(headPicUrl);
		builder.setNickName(nickName);
		return builder.build().toByteString();
	}

	/**
	 * 解析注册成功之后 返回的登录信息
	 * 
	 * @param rspPacket
	 * @return
	 * @throws Exception
	 */
	private String parseRegisterResult(RspPacket rspPacket)
	{
		try
		{
			RspLogUser rspRegUser = RspLogUser
					.parseFrom(rspPacket.getParams(0));
			// 0=成功,1=失败,2=仅支持11位手机号注册,3=该用户已被注册
			if (rspRegUser.getRescode() == 0)
			{
				LoginedUserInfo userInfo = ToolsUtil

				.getLoginedUserInfo(rspRegUser);
				// 将登录的账户信息保存到缓存中去
				ToolsUtil.saveCachDataToFile(LoginActivity.this,
						Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME, userInfo);
				// 将登录后的用户信息保存到登录帐号信息管理类中去
				LoginUserInfoManager.getInstance().setLoginedUserInfo(userInfo);
				return "success";
			} else
			{
				return rspRegUser.getResmsg();
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "parseRegisterResult()#Excepton:", e);
			return "error";
		}

	}

	/**
	 * 跳转到登录注册界面
	 * 
	 * @param context
	 */
	public static void startLoginActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				LoginActivity.class), action);
	}

	@Override
	public void onClick(View v)
	{
		final String phone = phoneInput.getText().toString().trim();
		switch (v.getId())
		{
		case R.id.register_get_verification_code_btn:
			// 获取短信验证码
			if ("".equals(phone))
			{
				Toast.makeText(LoginActivity.this,
						getResources().getString(R.string.input_phone_num),
						Toast.LENGTH_SHORT).show();
				return;
			} else
			{
				if (phone.length() != 11)
				{
					Toast.makeText(
							LoginActivity.this,
							getResources().getString(
									R.string.input_phone_num_error),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			// 获取注册验证码
			isGetCode = true;
			doLoadData(Constants.UAC_API_URL, new String[]
			{ GET_CODE_REQUEST_TAG }, new ByteString[]
			{ getRequestGetCodeData(phone, GET_CODE_TYPE) }, "");
			handler.post(countDownRunnable);
			break;
		case R.id.register_register_btn:
			// 注册按钮
			final String code = codeInput.getText().toString().trim();

			if ("".equals(phone))
			{
				Toast.makeText(LoginActivity.this,
						getResources().getString(R.string.input_phone_num),
						Toast.LENGTH_SHORT).show();
				return;
			} else
			{
				if (phone.length() != 11 || !phone.startsWith("1"))
				{
					Toast.makeText(
							LoginActivity.this,
							getResources().getString(
									R.string.input_phone_num_error),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if ("".equals(code))
			{
				Toast.makeText(LoginActivity.this,
						getResources().getString(R.string.input_code),
						Toast.LENGTH_SHORT).show();
				return;
			} else
			{
				if (code.length() != 6)
				{
					Toast.makeText(
							LoginActivity.this,
							getResources().getString(R.string.input_code_error),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if (ConstantManager.getInstance().getConstantInfo()
					.getIsDeviceRegisted() == 0)
			{
				final CenterDialog phoneLoginInputInvitedCodeDialog = new CenterDialog(
						LoginActivity.this);
				phoneLoginInputInvitedCodeDialog.show();
				phoneLoginInputInvitedCodeDialog.setTitleName(getResources()
						.getString(R.string.invited_code));
				View dialogView = View.inflate(LoginActivity.this,
						R.layout.input_invited_code_dialog, null);
				dialogView
						.setBackgroundResource(R.drawable.loading_dialog_bottom_bg_shape);
				final EditText codeInput = (EditText) dialogView
						.findViewById(R.id.invite_code_input);
				codeInput.setHintTextColor(getResources().getColor(
						R.color.divider_color));
				Button ignoreBtn = (Button) dialogView
						.findViewById(R.id.ignore_submit_btn);
				ignoreBtn.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{

						String inviteCode = codeInput.getText().toString()
								.trim();
						phoneLoginInputInvitedCodeDialog.dismiss();
						// 登录
						doLoadData(Constants.UAC_API_URL, new String[]
						{ REGISTER_REQUEST_TAG }, new ByteString[]
						{ getRegisterRequestData(phone, inviteCode, 0, code) },
								"");
						loadingDialog.show();
					}
				});
				Button confirmBtn = (Button) dialogView
						.findViewById(R.id.confirm_name_submit_btn);
				confirmBtn.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						String inviteCode = codeInput.getText().toString()
								.trim();
						phoneLoginInputInvitedCodeDialog.dismiss();
						// 登录
						doLoadData(Constants.UAC_API_URL, new String[]
						{ REGISTER_REQUEST_TAG }, new ByteString[]
						{ getRegisterRequestData(phone, inviteCode, 0, code) },
								"");
						loadingDialog.show();
					}
				});
				phoneLoginInputInvitedCodeDialog.setCenterView(dialogView);
			} else
			{
				loadingDialog.show();
				// 登录
				doLoadData(Constants.UAC_API_URL, new String[]
				{ REGISTER_REQUEST_TAG }, new ByteString[]
				{ getRegisterRequestData(phone, "", 0, code) }, "");
				loadingDialog.show();
			}
			break;

//		case R.id.wx_login_lay:
//			// 微信登录
//			if (ConstantManager.getInstance().getConstantInfo()
//					.getIsDeviceRegisted() == 0)
//			{
//				final CenterDialog wxLoginInputInvitedCodeDialog = new CenterDialog(
//						LoginActivity.this);
//				wxLoginInputInvitedCodeDialog.show();
//				wxLoginInputInvitedCodeDialog.setTitleName(getResources()
//						.getString(R.string.invited_code));
//				View wxDialogView = View.inflate(LoginActivity.this,
//						R.layout.input_invited_code_dialog, null);
//				wxDialogView
//						.setBackgroundResource(R.drawable.loading_dialog_bottom_bg_shape);
//				final EditText wxCodeInput = (EditText) wxDialogView
//						.findViewById(R.id.invite_code_input);
//				wxCodeInput.setHintTextColor(getResources().getColor(
//						R.color.divider_color));
//				wxInviteCode = wxCodeInput.getText().toString().trim();
//				Button wxIgnoreBtn = (Button) wxDialogView
//						.findViewById(R.id.ignore_submit_btn);
//				wxIgnoreBtn.setOnClickListener(new OnClickListener()
//				{
//
//					@Override
//					public void onClick(View v)
//					{
//						loadingDialog.show();
//						wxInviteCode = wxCodeInput.getText().toString().trim();
//						wxLoginInputInvitedCodeDialog.dismiss();
//						wxLogin();
//
//					}
//				});
//
//				Button wxConfirmBtn = (Button) wxDialogView
//						.findViewById(R.id.confirm_name_submit_btn);
//				wxConfirmBtn.setOnClickListener(new OnClickListener()
//				{
//					@Override
//					public void onClick(View v)
//					{
//						loadingDialog.show();
//						wxInviteCode = wxCodeInput.getText().toString().trim();
//						wxLoginInputInvitedCodeDialog.dismiss();
//						wxLogin();
//
//					}
//
//				});
//				wxLoginInputInvitedCodeDialog.setCenterView(wxDialogView);
//			} else
//			{
//				loadingDialog.show();
//				wxLogin();
//			}
//			break;
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		loadingDialog.dismiss();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onSuccess()
	{
		try
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					loadingDialog.dismiss();
					// 添加友盟注册成功的数据统计
					HashMap<String, String> values = new HashMap<String, String>();
					values.put("user_id", LoginUserInfoManager.getInstance()
							.getLoginedUserInfo().getUserId()
							+ "");
					DataCollectionManager.getInstance().addYouMengEventRecord(
							LoginActivity.this, action,
							DataCollectionConstant.EVENT_ID_REGISTER_SUCCESS,
							values);
					// intent.putExtra("is_login_in",
					// true);
					sendBroadcast(new Intent(Constants.ACTION_LOGIN_SUCCESS));
					finish();
				}

			});
		} catch (Exception e)
		{
			DLog.e(TAG, "LoginActivity不存在了f#exception:", e);
		}
	}

	@Override
	public void onError(final String msg)
	{
		try
		{
			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					loadingDialog.dismiss();
					Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT)
							.show();
				}
			});
		} catch (Exception e)
		{
			DLog.e(TAG, "LoginActivity不存在了f#exception:", e);
		}
	}

	@Override
	public void onSubmitData()
	{

		try
		{
			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					loadingDialog.show();
				}
			});
		} catch (Exception e)
		{
			DLog.e(TAG, "LoginActivity不存在了f#exception:", e);
		}
	}
}
