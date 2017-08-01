package com.xd.leplay.store.gui.login;

import java.util.List;

import android.content.Context;
import android.content.Intent;
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
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqFindPwd;
import com.xd.leplay.store.model.proto.Uac.ReqSendSMSCode;
import com.xd.leplay.store.model.proto.Uac.RspFindPwd;
import com.xd.leplay.store.model.proto.Uac.RspSendSMSCode;
import com.xd.leplay.store.view.LoadingDialog;
import com.google.protobuf.ByteString;

/**
 * 忘记密码获取验证码界面
 * 
 * @author lilijun
 *
 */
public class LostPwdGetCodeActivity extends BaseActivity
{
	private final String TAG = "LostPwdGetCodeActivity";

	private EditText phoneInput;

	private EditText codeInput;

	private Button getCodeBtn, submitBtn;

	/** 获取验证码的请求TAG */
	private final String GET_CODE_REQUEST_TAG = "ReqSendSMSCode";
	/** 获取验证码的响应TAG */
	private final String GET_CODE_RSPONSE_TAG = "RspSendSMSCode";

	/** 找回密码时 提交验证码的请求TAG */
	private final String SUBMIT_CODE_REQUEST_TAG = "ReqFindPwd";

	/** 找回密码时 提交验证码的响应TAG */
	private final String SUBMIT_CODE_RSPONSE_TAG = "RspFindPwd";

	/** 获取验证码的类型 1=注册,2=找回密码,3=申请提现,4=确认提现,5=更改绑定手机号码 */
	private final int GET_CODE_TYPE = 2;

	/** 开始计时的Handler消息 */
	private final int CHANGE_TIME = 0;

	private int timeCount = 60;

	/** 正在提交的弹出框 */
	private LoadingDialog loadingDialog = null;

	/** 提交验证码之后 服务器会返回一个是否接收到获取更改手机的验证码tokend值 在下一页更改手机号的时候需要这个值 */
	private String codeToken = "";

	private String phone = "";

	/** 标识 当前网络请求是否是在获取验证码 */
	private boolean isGetCode = true;

	/** 提交更改信息密码等信息成功之后 服务器会返回一个账户的userId */
	private int userId;

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
					getCodeBtn.setTextColor(getResources().getColor(
							R.color.reget_code_text_color));
					getCodeBtn.setEnabled(false);

					getCodeBtn.setText(String.format(
							getResources().getString(R.string.reget_code),
							timeCount + ""));
					timeCount--;
				} else
				{
					getCodeBtn.setTextColor(getResources().getColor(
							R.color.list_describle_color));
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
						DataCollectionConstant.DATA_COLLECTION_LOST_PWD_GET_VER_CODE_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		titleView.setTitleName(getResources().getString(R.string.lost_pwd));
		titleView.setBottomLineVisible(true);
		titleView.setRightLayVisible(false);
		loadingView.setVisibilyView(false);

		setCenterView(R.layout.lost_pwd_get_code_activity);

		loadingDialog = new LoadingDialog(LostPwdGetCodeActivity.this,
				getResources().getString(R.string.submitting));

		phoneInput = (EditText) findViewById(R.id.lost_pwd_phone_text);
		codeInput = (EditText) findViewById(R.id.lost_pwd_code_edit);
		phoneInput.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		codeInput.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		
		getCodeBtn = (Button) findViewById(R.id.lost_pwd_get_code_btn);
		submitBtn = (Button) findViewById(R.id.lost_pwd_submit_btn);

		getCodeBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				phone = phoneInput.getText().toString().trim();
				if ("".equals(phone))
				{
					Toast.makeText(LostPwdGetCodeActivity.this,
							getResources().getString(R.string.input_phone_num),
							Toast.LENGTH_SHORT).show();
					return;
				} else
				{
					if (phone.length() != 11 || !phone.startsWith("1"))
					{
						Toast.makeText(
								LostPwdGetCodeActivity.this,
								getResources().getString(
										R.string.input_phone_num_error),
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				// 获取更换手机号码的验证码
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
				String phone = phoneInput.getText().toString().trim();
				String code = codeInput.getText().toString().trim();

				if ("".equals(phone))
				{
					Toast.makeText(LostPwdGetCodeActivity.this,
							getResources().getString(R.string.input_phone_num),
							Toast.LENGTH_SHORT).show();
					return;
				} else
				{
					if (phone.length() != 11 || !phone.startsWith("1"))
					{
						Toast.makeText(
								LostPwdGetCodeActivity.this,
								getResources().getString(
										R.string.input_phone_num_error),
								Toast.LENGTH_SHORT).show();
						return;
					}
				}

				if ("".equals(code))
				{
					Toast.makeText(LostPwdGetCodeActivity.this,
							getResources().getString(R.string.input_code),
							Toast.LENGTH_SHORT).show();
					return;
				} else
				{
					if (code.length() != 6)
					{
						Toast.makeText(
								LostPwdGetCodeActivity.this,
								getResources().getString(
										R.string.input_code_error),
								Toast.LENGTH_SHORT).show();
						return;
					}
				}

				// 提交验证码
				doLoadData(Constants.UAC_API_URL, new String[]
				{ SUBMIT_CODE_REQUEST_TAG }, new ByteString[]
				{ getSubmitCodeRequestData(phone, code) }, "");
				loadingDialog.show();

			}
		});
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
						Toast.makeText(LostPwdGetCodeActivity.this, msg,
								Toast.LENGTH_SHORT).show();
						handler.removeCallbacks(countDownRunnable);
						getCodeBtn.setTextColor(getResources().getColor(
								R.color.list_describle_color));
						getCodeBtn.setEnabled(true);
						getCodeBtn.setText(getResources().getString(
								R.string.register_get_verification_code));
						timeCount = 60;
					} else
					{
						// 获取验证码成功 暂时不做任何操作
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
						// 跳转到填写新密码的界面
						if (!"".equals(codeToken))
						{
							LostPwdInputNewPwdActivity
									.startLostPwdInputNewPwdActivity(
											LostPwdGetCodeActivity.this,
											userId, codeToken, action);
							finish();
						} else
						{
							Toast.makeText(
									LostPwdGetCodeActivity.this,
									getResources().getString(
											R.string.submit_find_pwd_failed),
									Toast.LENGTH_SHORT).show();
						}
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
			Toast.makeText(LostPwdGetCodeActivity.this,
					getResources().getString(R.string.get_code_failed),
					Toast.LENGTH_SHORT).show();
			isGetCode = false;
			handler.removeCallbacks(countDownRunnable);
			getCodeBtn.setTextColor(getResources().getColor(
					R.color.list_describle_color));
			getCodeBtn.setEnabled(true);
			getCodeBtn.setText(getResources().getString(
					R.string.register_get_verification_code));
			timeCount = 60;
		} else
		{
			Toast.makeText(LostPwdGetCodeActivity.this,
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
			Toast.makeText(LostPwdGetCodeActivity.this,
					getResources().getString(R.string.get_code_failed),
					Toast.LENGTH_SHORT).show();
			isGetCode = false;
			handler.removeCallbacks(countDownRunnable);
			getCodeBtn.setTextColor(getResources().getColor(
					R.color.list_describle_color));
			getCodeBtn.setEnabled(true);
			getCodeBtn.setText(getResources().getString(
					R.string.register_get_verification_code));
			timeCount = 60;
		} else
		{
			Toast.makeText(LostPwdGetCodeActivity.this,
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
		// 0=成功,1=系统响应出错,2=未注册业务类型,3=获取短信失败
		if (rspSendSMSCode.getRescode() == 0)
		{
			return "success";
		} else
		{
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
	private ByteString getSubmitCodeRequestData(String account, String code)
	{
		ReqFindPwd.Builder builder = ReqFindPwd.newBuilder();
		builder.setAccountName(account);
		builder.setVerifyCode(code);
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
		RspFindPwd rspFindPwd = RspFindPwd.parseFrom(result);
		// 0=成功,1=响应出错,2=验证码错误,3=用户不存在或已被禁用
		if (rspFindPwd.getRescode() == 0)
		{
			codeToken = rspFindPwd.getSmsCodeToken();
			userId = rspFindPwd.getUid();
			// sendBroadcast(new Intent(Constants.ACTION_ACCOUNT_HAVE_MODIFY));
		} else
		{
			Toast.makeText(LostPwdGetCodeActivity.this, rspFindPwd.getResmsg(),
					Toast.LENGTH_SHORT).show();
		}
		return rspFindPwd.getRescode();
	}

	/**
	 * 跳转到LostPwdGetCodeActivity界面
	 * 
	 * @param context
	 */
	public static void startLostPwdGetCodeActivity(Context context,
			String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				LostPwdGetCodeActivity.class), action);
	}

}
