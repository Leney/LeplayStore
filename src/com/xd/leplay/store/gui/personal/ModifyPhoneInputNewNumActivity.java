package com.xd.leplay.store.gui.personal;

import java.util.List;

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
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqChangePhone;
import com.xd.leplay.store.model.proto.Uac.RspChangePhone;
import com.google.protobuf.ByteString;

/**
 * 更改绑定手机号码 输入新的要绑定的手机号码界面
 * 
 * @author lilijun
 *
 */
public class ModifyPhoneInputNewNumActivity extends BaseActivity
{

	private final String TAG = "ModifyPhoneInputNewNumActivity";

	private String codeToken = "";

	private EditText inputNewPhone1, inputNewPhone2;

	private Button sureBtn;

	/** 修改新的手机号码的请求TAG */
	private final String MODIFY_NEW_PHONE_REQUEST_TAG = "ReqChangePhone";

	/** 修改新的手机号码的响应TAG */
	private final String MODIFY_NEW_PHONE_RSPONSE_TAG = "RspChangePhone";

	@Override
	protected void initView()
	{
		action = DataCollectionManager
				.getAction(
						DataCollectionManager
								.getIntentDataCollectionAction(getIntent()),
						DataCollectionConstant.DATA_COLLECTION_INPUT_NEW_PHONE_NUM_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		titleView.setTitleName(getResources().getString(R.string.modify_phone));
		titleView.setRightLayVisible(false);
		titleView.setBottomLineVisible(true);

		loadingView.setVisibilyView(false);

		codeToken = getIntent().getStringExtra("codeToken");

		setCenterView(R.layout.modify_phone_input_new_phone_activity);

		inputNewPhone1 = (EditText) findViewById(R.id.modify_phone_new_phone_edit1);
		inputNewPhone2 = (EditText) findViewById(R.id.modify_phone_new_phone_edit2);
		inputNewPhone1.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		inputNewPhone2.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		sureBtn = (Button) findViewById(R.id.modify_phone_new_phone_sure_btn);
		sureBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String newPhone1 = inputNewPhone1.getText().toString().trim();
				String newPhone2 = inputNewPhone2.getText().toString().trim();
				if ("".equals(newPhone1))
				{
					Toast.makeText(ModifyPhoneInputNewNumActivity.this,
							getResources().getString(R.string.input_new_phone),
							Toast.LENGTH_SHORT).show();
					return;
				} else
				{
					if (newPhone1.length() != 11 || !newPhone1.startsWith("1"))
					{
						Toast.makeText(
								ModifyPhoneInputNewNumActivity.this,
								getResources().getString(
										R.string.input_new_phone_error),
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				if ("".equals(newPhone2))
				{
					Toast.makeText(
							ModifyPhoneInputNewNumActivity.this,
							getResources().getString(
									R.string.input_new_phone_again),
							Toast.LENGTH_SHORT).show();
					return;
				} else
				{
					if (newPhone2.length() != 11 || !newPhone2.startsWith("1"))
					{
						Toast.makeText(
								ModifyPhoneInputNewNumActivity.this,
								getResources().getString(
										R.string.input_new_phone_again_error),
								Toast.LENGTH_SHORT).show();
						return;
					}

					if (!newPhone2.equals(newPhone1))
					{
						Toast.makeText(
								ModifyPhoneInputNewNumActivity.this,
								getResources().getString(
										R.string.input_twice_phone_not_same),
								Toast.LENGTH_SHORT).show();
						return;
					}
				}

				LoginedUserInfo userInfo = LoginUserInfoManager.getInstance()
						.getLoginedUserInfo();
				// 提交更改的新的手机号码信息
				doLoadData(
						Constants.UAC_API_URL,
						new String[]
						{ MODIFY_NEW_PHONE_REQUEST_TAG },
						new ByteString[]
						{ getModifyNewPhoneRequestData(userInfo.getUserId(),
								userInfo.getUserToken(), codeToken, newPhone1) },
						"");
			}
		});

	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<String> actions = rspPacket.getActionList();
		for (String actionnet : actions)
		{
			if (MODIFY_NEW_PHONE_RSPONSE_TAG.equals(actionnet))
			{
				try
				{
					RspChangePhone rspChangePhone = RspChangePhone
							.parseFrom(rspPacket.getParams(0));
					// 0=成功,1=响应错误,2=token错误,3=sms token错误
					if (rspChangePhone.getRescode() == 0)
					{
						LoginUserInfoManager.getInstance().exitLogin();
						// 发送用户需要重新登陆的广播
						sendBroadcast(new Intent(
								Constants.ACTION_USER_NEED_RELOGIN));
						Toast.makeText(
								this,
								getResources().getString(
										R.string.modify_phone_success),
								Toast.LENGTH_SHORT).show();
						// 跳转到登录界面
						LoginActivity.startLoginActivity(
								ModifyPhoneInputNewNumActivity.this, action);
						finish();
					} else if (rspChangePhone.getRescode() == 2)
					{
						// userToken错误 重新登录
						Toast.makeText(this,
								getResources().getString(R.string.re_login),
								Toast.LENGTH_SHORT).show();
						LoginActivity.startLoginActivity(this, action);
						finish();
					} else if (rspChangePhone.getRescode() == 3)
					{
						// 如果是短信验证码有问题 则跳回获取短信验证码的界面
						Toast.makeText(
								ModifyPhoneInputNewNumActivity.this,
								getResources().getString(R.string.code_invaile),
								Toast.LENGTH_SHORT).show();
						ModifyPhoneGetCodeActivity
								.startModifyPhoneGetCodeActivity(
										ModifyPhoneInputNewNumActivity.this,
										action);
						finish();
					} else
					{
						Toast.makeText(ModifyPhoneInputNewNumActivity.this,
								rspChangePhone.getResmsg(), Toast.LENGTH_SHORT)
								.show();
					}
				} catch (Exception e)
				{
					DLog.e(TAG, "loadDataSuccess()#exception:", e);
				}
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		Toast.makeText(ModifyPhoneInputNewNumActivity.this,
				getResources().getString(R.string.modify_new_phone_failed),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		Toast.makeText(ModifyPhoneInputNewNumActivity.this,
				getResources().getString(R.string.modify_new_phone_failed),
				Toast.LENGTH_SHORT).show();
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

	public static void startModifyPhoneInputNewNumActivity(Context context,
			String codeToken, String action)
	{
		Intent intent = new Intent(context,
				ModifyPhoneInputNewNumActivity.class);
		intent.putExtra("codeToken", codeToken);
		DataCollectionManager.startActivity(context, intent, action);
	}

}
