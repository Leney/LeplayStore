package com.xd.leplay.store.gui.personal;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
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
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqChangePhone;
import com.xd.leplay.store.model.proto.Uac.ReqReChangePhone;
import com.xd.leplay.store.model.proto.Uac.ReqSendSMSCode;
import com.xd.leplay.store.model.proto.Uac.RspReChangePhone;
import com.xd.leplay.store.model.proto.Uac.RspSendSMSCode;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.LoadingDialog;
import com.google.protobuf.ByteString;

/**
 * 修改手机号码 获取验证码界面
 * 
 * @author lilijun
 *
 */
public class ModifyPhoneGetCodeActivity extends BaseActivity
{
	private final String TAG = "ModifyPhoneGetCodeActivity";

	// private TextView phoneText;

	private EditText codeInput, phoneInput;

	private Button getCodeBtn, submitBtn;

	private String phone = "";

	/** 获取验证码的请求TAG */
	private final String GET_CODE_REQUEST_TAG = "ReqSendSMSCode";
	/** 获取验证码的响应TAG */
	private final String GET_CODE_RSPONSE_TAG = "RspSendSMSCode";

	/** 更改手机号时 提交验证码的请求TAG */
	private final String SUBMIT_CODE_REQUEST_TAG = "ReqReChangePhone";

	/** 更改手机号时 提交验证码的响应TAG */
	private final String SUBMIT_CODE_RSPONSE_TAG = "RspReChangePhone";

	/** 获取验证码的类型 1=注册,2=找回密码,3=申请提现,4=确认提现,5=更改绑定手机号码 */
	private final int GET_CODE_TYPE = 5;

	/** 开始计时的Handler消息 */
	private final int CHANGE_TIME = 0;

	private int timeCount = 60;

	private LoginUserInfoManager loginUserInfoManager = null;

	/** 正在提交的弹出框 */
	private LoadingDialog loadingDialog = null;

	/** 提交验证码之后 服务器会返回一个是否接收到获取更改手机的验证码tokend值 在下一页更改手机号的时候需要这个值 */
	private String codeToken = "";

	/** 标识 当前网络请求是否是在获取验证码 */
	private boolean isGetCode = true;

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
		action = DataCollectionManager
				.getAction(
						DataCollectionManager
								.getIntentDataCollectionAction(getIntent()),
						DataCollectionConstant.DATA_COLLECTION_MODIFY_PHONE_GET_VER_CODE_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		titleView.setTitleName(getResources().getString(R.string.modify_phone));
		titleView.setBottomLineVisible(true);
		titleView.setRightLayVisible(false);
		loadingView.setVisibilyView(false);

		loginUserInfoManager = LoginUserInfoManager.getInstance();

		setCenterView(R.layout.modify_phone_get_code_activity);

		loadingDialog = new LoadingDialog(ModifyPhoneGetCodeActivity.this,
				getResources().getString(R.string.submitting));

		// phoneText = (TextView) findViewById(R.id.modify_phone_text);
		phoneInput = (EditText) findViewById(R.id.input_phone_edit);
		phoneInput.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		if (loginUserInfoManager.getLoginedUserInfo().getPhone() == null
				|| loginUserInfoManager.getLoginedUserInfo().getPhone()
						.equals(""))
		{
			phoneInput.setHint(getResources().getString(R.string.toBind_phone));
			titleView.setTitleName(getResources().getString(
					R.string.bound_phone));
		} else
		{
			phoneInput.setHint(getResources()
					.getString(R.string.toChange_phone));
			titleView.setTitleName(getResources().getString(
					R.string.modify_phone));
		}

		codeInput = (EditText) findViewById(R.id.modify_phone_code_edit);
		codeInput.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		getCodeBtn = (Button) findViewById(R.id.modify_get_code_btn);
		submitBtn = (Button) findViewById(R.id.modify_submit_btn);

		phone = LoginUserInfoManager.getInstance().getLoginedUserInfo()
				.getPhone();
		// phoneText.setText(phone);
		getCodeBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				String phone = phoneInput.getText().toString().trim();
				if ("".equals(phone))
				{
					Toast.makeText(ModifyPhoneGetCodeActivity.this,
							getResources().getString(R.string.input_phone_num),
							Toast.LENGTH_SHORT).show();
					return;
				} else
				{
					if (phone.length() != 11 || !phone.startsWith("1"))
					{
						Toast.makeText(
								ModifyPhoneGetCodeActivity.this,
								getResources().getString(
										R.string.input_phone_num_error),
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				// 绑定手机号码的验证码
				isGetCode = true;
				doLoadData(Constants.UAC_API_URL, new String[]
				{ GET_CODE_REQUEST_TAG }, new ByteString[]
				{ getRequestGetCodeData(phone, GET_CODE_TYPE) }, "");
				handler.post(countDownRunnable);
			}
		});

		submitBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				String code = codeInput.getText().toString().trim();
				if ("".equals(code))
				{
					Toast.makeText(ModifyPhoneGetCodeActivity.this,
							getResources().getString(R.string.input_code),
							Toast.LENGTH_SHORT).show();
					return;
				} else
				{
					if (code.length() != 6)
					{
						Toast.makeText(
								ModifyPhoneGetCodeActivity.this,
								getResources().getString(
										R.string.input_code_error),
								Toast.LENGTH_SHORT).show();
						return;
					}
				}

				// 提交验证码并修改
				doLoadData(
						Constants.UAC_API_URL,
						new String[]
						{ SUBMIT_CODE_REQUEST_TAG },
						new ByteString[]
						{ getSubmitCodeRequestDataByWX(loginUserInfoManager
								.getLoginedUserInfo().getUserId(),
								loginUserInfoManager.getLoginedUserInfo()
										.getUserToken(), code, phoneInput
										.getText().toString().trim()) }, "");

				loadingDialog.show();

			}
		});
		IntentFilter filter = new IntentFilter(
				Constants.ACTION_ACCOUNT_HAVE_MODIFY);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		try
		{
			List<String> actions = rspPacket.getActionList();
			for (String actionNet : actions)
			{
				if (GET_CODE_RSPONSE_TAG.equals(actionNet))
				{
					String msg = parseGetCodeResult(rspPacket);
					if (!"success".equals(msg))
					{
						// 获取验证码失败
						Toast.makeText(ModifyPhoneGetCodeActivity.this, msg,
								Toast.LENGTH_SHORT).show();
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
						// 获取验证码成功 暂时不做任何操作
						codeInput.requestFocus();
					}
					isGetCode = false;
				} else if (SUBMIT_CODE_RSPONSE_TAG.equals(actionNet))
				{
					int resultCode = parseSubmitCodeResult(rspPacket
							.getParams(0));
					loadingDialog.dismiss();
					// 0=成功,1=响应出错,2=token失效,3=用户不存在或已被禁用,4=验证码错误
					if (resultCode == 0)
					{
						// 发送用户的相关信息发生了改变的广播
						sendBroadcast(new Intent(
								Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY));
						Toast.makeText(
								this,
								getResources().getString(
										R.string.modify_user_info_success),
								Toast.LENGTH_SHORT).show();
						finish();
					}

				}
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "loadDataSuccess()#Excepton:", e);
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		if (isGetCode)
		{
			Toast.makeText(ModifyPhoneGetCodeActivity.this,
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
			Toast.makeText(ModifyPhoneGetCodeActivity.this,
					getResources().getString(R.string.submit_data_failed),
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
			Toast.makeText(ModifyPhoneGetCodeActivity.this,
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
			Toast.makeText(ModifyPhoneGetCodeActivity.this,
					getResources().getString(R.string.submit_data_failed),
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
	private String parseGetCodeResult(RspPacket rspPacket) throws Exception
	{
		RspSendSMSCode rspSendSMSCode = RspSendSMSCode.parseFrom(rspPacket
				.getParams(0));
		DLog.i("lilijun", "解析获取验证码返回数据.....");
		// 0=成功,1=系统响应出错,2=未注册业务类型,3=获取短信失败
		if (rspSendSMSCode.getRescode() == 0)
		{
			DLog.i("lilijun", "获取验证码成功！！！");
			return "success";
		} else
		{
			DLog.i("lilijun", "获取验证码失败！！！");
			return rspSendSMSCode.getResmsg();
		}
	}

	/**
	 * 得到获取提交验证码的请求参数
	 * 
	 * @param userId
	 * @param userToken
	 * @param code
	 *            验证码
	 * @return
	 */
	private ByteString getSubmitCodeRequestData(int userId, String userToken,
			String code)
	{
		ReqReChangePhone.Builder builder = ReqReChangePhone.newBuilder();
		builder.setUid(userId);
		builder.setUserToken(userToken);
		builder.setVerifyCode(code);
		return builder.build().toByteString();
	}

	/**
	 * 微信登录时得到获取提交验证码的请求参数
	 * 
	 * @param userId
	 * @param userToken
	 * @param code
	 *            验证码
	 * @return
	 */
	private ByteString getSubmitCodeRequestDataByWX(int userId,
			String userToken, String code, String phoneNo)
	{
		ReqReChangePhone.Builder builder = ReqReChangePhone.newBuilder();
		builder.setUid(userId);
		builder.setUserToken(userToken);
		builder.setVerifyCode(code);
		builder.setPhoneNo(phoneNo);
		return builder.build().toByteString();
	}

	/**
	 * 解析提交验证码的返回数据
	 * 
	 * @param result
	 * @return
	 * @throws Exception
	 */
	private int parseSubmitCodeResult(ByteString result) throws Exception
	{
		RspReChangePhone rspReChangePhone = RspReChangePhone.parseFrom(result);
		// 0=成功,1=响应出错,2=token失效,3=用户不存在或已被禁用,4=验证码错误
		if (rspReChangePhone.getRescode() == 0)
		{
			loginUserInfoManager.getLoginedUserInfo().setUserToken(
					rspReChangePhone.getUserToken());
			loginUserInfoManager.getLoginedUserInfo().setPhone(
					phoneInput.getText().toString().trim());
			loginUserInfoManager.getLoginedUserInfo().setAccount(
					phoneInput.getText().toString().trim());
			ToolsUtil.saveCachDataToFile(ModifyPhoneGetCodeActivity.this,
					Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME,
					loginUserInfoManager.getLoginedUserInfo());
			codeToken = rspReChangePhone.getSmsCodeToken();
			sendBroadcast(new Intent(Constants.ACTION_ACCOUNT_HAVE_MODIFY));
		} else if (rspReChangePhone.getRescode() == 2)
		{
			// userToken错误
			// 退出
			LoginUserInfoManager.getInstance().exitLogin();
			Toast.makeText(ModifyPhoneGetCodeActivity.this,
					getResources().getString(R.string.re_login),
					Toast.LENGTH_SHORT).show();
			LoginActivity.startLoginActivity(ModifyPhoneGetCodeActivity.this,
					action);
			finish();
		} else
		{
			Toast.makeText(ModifyPhoneGetCodeActivity.this,
					rspReChangePhone.getResmsg(), Toast.LENGTH_SHORT).show();
		}
		return rspReChangePhone.getRescode();
	}

	/**
	 * 跳转到ModifyPhoneGetCodeActivity界面
	 * 
	 * @param context
	 */
	public static void startModifyPhoneGetCodeActivity(Context context,
			String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				ModifyPhoneGetCodeActivity.class), action);
	}

	/**
	 * 获得更改新的手机号的请求参数
	 * 
	 * @param userId
	 * @param userToken
	 * @param codeToken
	 * @param newPhone
	 * @return
	 */
	private ByteString getModifyNewPhoneRequestData(int userId,
			String userToken, String codeToken, String newPhone)
	{
		ReqChangePhone.Builder builder = ReqChangePhone.newBuilder();
		builder.setUid(userId);
		builder.setUserToken(userToken);
		builder.setSmsCodeToken(codeToken);
		builder.setNewPhoneNo(newPhone);
		return builder.build().toByteString();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(Constants.ACTION_ACCOUNT_HAVE_MODIFY))
			{
				if (!LoginUserInfoManager.getInstance().isHaveUserLogin())
				{
					// 接收到用户需要重新登录的广播后 直接finish掉此界面(在用户更换了手机号之后需要重新登录)
					finish();
				}
			}
		};
	};
}
