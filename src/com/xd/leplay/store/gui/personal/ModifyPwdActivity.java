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
import com.xd.leplay.store.model.proto.Uac.ReqSetPwd;
import com.xd.leplay.store.model.proto.Uac.RspSetPwd;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.LoadingDialog;
import com.google.protobuf.ByteString;

/**
 * 修改密码界面(获取手机验证码)
 * 
 * @author lilijun
 *
 */
public class ModifyPwdActivity extends BaseActivity
{

	private final String TAG = "ModifyPwdActivity";

	/** 旧密码输入框、新密码输入框、新密码确认输入框 */
	private EditText oldPwdInput, newPwdInput1, newPwdInput2;

	/** 提交按钮 */
	private Button submitBtn;

	/** 修改密码的请求TAG */
	private final String MODIFY_PWD_REQUEST_TAG = "ReqSetPwd";

	/** 修改密码的响应TAG */
	private final String MODIFY_PWD_RSPONSE_TAG = "RspSetPwd";

	private LoadingDialog dialog = null;

	@Override
	protected void initView()
	{
		action = DataCollectionManager
				.getAction(
						DataCollectionManager
								.getIntentDataCollectionAction(getIntent()),
						DataCollectionConstant.DATA_COLLECTION_MODIFY_PWD_GET_CODE_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		titleView.setTitleName(getResources().getString(R.string.change_pwd));
		titleView.setRightLayVisible(false);
		titleView.setBottomLineVisible(true);
		loadingView.setVisibilyView(false);
		setCenterView(R.layout.modify_pwd_get_code_activity);

		dialog = new LoadingDialog(this, getResources().getString(
				R.string.modifing));

		oldPwdInput = (EditText) findViewById(R.id.modify_pwd_old_pwd_edit);
		newPwdInput1 = (EditText) findViewById(R.id.modify_pwd_new_pwd_edit);
		newPwdInput2 = (EditText) findViewById(R.id.modify_pwd_new_pwd_affirm_edit);
		oldPwdInput.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		newPwdInput1.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		newPwdInput2.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		submitBtn = (Button) findViewById(R.id.modify_pwd_submit_btn);
		submitBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String oldPwd = oldPwdInput.getText().toString().trim();
				String newPwd1 = newPwdInput1.getText().toString().trim();
				String newPwd2 = newPwdInput2.getText().toString().trim();

				if ("".equals(oldPwd))
				{
					Toast.makeText(ModifyPwdActivity.this,
							getResources().getString(R.string.input_old_pwd),
							Toast.LENGTH_SHORT).show();
					return;
				} else
				{
					if (oldPwd.length() < 4 || oldPwd.length() > 16)
					{
						Toast.makeText(
								ModifyPwdActivity.this,
								getResources().getString(
										R.string.input_old_pwd_error),
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				if ("".equals(newPwd1))
				{
					Toast.makeText(ModifyPwdActivity.this,
							getResources().getString(R.string.input_new_pwd),
							Toast.LENGTH_SHORT).show();
					return;
				} else
				{
					if (newPwd1.length() < 4 || newPwd1.length() > 16)
					{
						Toast.makeText(
								ModifyPwdActivity.this,
								getResources().getString(
										R.string.input_new_pwd_error),
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				if (!newPwd1.equals(newPwd2))
				{
					Toast.makeText(
							ModifyPwdActivity.this,
							getResources().getString(
									R.string.input_new_pwd_affim_error),
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (oldPwd.equals(newPwd1))
				{
					Toast.makeText(
							ModifyPwdActivity.this,
							getResources().getString(R.string.old_new_pwd_same),
							Toast.LENGTH_SHORT).show();
					return;
				}

				String oldPwdMd5 = ToolsUtil.getMD5ByString(oldPwd);
				String newPwdMd5 = ToolsUtil.getMD5ByString(newPwd1);
				LoginedUserInfo userInfo = LoginUserInfoManager.getInstance()
						.getLoginedUserInfo();
				if (userInfo != null)
				{
					doLoadData(
							Constants.UAC_API_URL,
							new String[]
							{ MODIFY_PWD_REQUEST_TAG },
							new ByteString[]
							{ getModifyPwdRequestData(userInfo.getUserId(),
									userInfo.getUserToken(), oldPwdMd5,
									newPwdMd5) }, "");
				}

				dialog.show();
			}
		});

	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<String> actions = rspPacket.getActionList();
		for (String action : actions)
		{
			if (MODIFY_PWD_RSPONSE_TAG.equals(action))
			{
				int resultCode = parseModifyPwdResult(rspPacket.getParams(0));
				dialog.dismiss();
				// 0=成功,1=相应错误,2=旧密码错误,3=新密码不合法
				if (resultCode == 0)
				{
					finish();
				}
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		Toast.makeText(this,
				getResources().getString(R.string.modify_pwd_failed),
				Toast.LENGTH_SHORT).show();
		dialog.dismiss();
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		Toast.makeText(this,
				getResources().getString(R.string.modify_pwd_failed),
				Toast.LENGTH_SHORT).show();
		dialog.dismiss();
	}

	/**
	 * 获取修改密码的请求数据
	 * 
	 * @param userId
	 * @param userToken
	 * @param oldPwd
	 * @param newPwd
	 */
	private ByteString getModifyPwdRequestData(int userId, String userToken,
			String oldPwd, String newPwd)
	{
		ReqSetPwd.Builder builder = ReqSetPwd.newBuilder();
		builder.setUid(userId);
		builder.setOldPwd(oldPwd);
		builder.setNewPwd(newPwd);
		builder.setUserToken(userToken);
		return builder.build().toByteString();
	}

	/**
	 * 解析更改密码的返回数据
	 * 
	 * @param result
	 * @return
	 * @throws Exception
	 */
	private int parseModifyPwdResult(ByteString result)
	{
		try
		{
			RspSetPwd rspSetPwd = RspSetPwd.parseFrom(result);
			// 0=成功,1=响应错误,2=旧密码错误,3=新密码不合法,4=用户不存在或已被禁用,5=token错误
			if (rspSetPwd.getRescode() == 0)
			{
				LoginUserInfoManager.getInstance().getLoginedUserInfo()
						.setUserToken(rspSetPwd.getUserToken());
				ToolsUtil
						.saveCachDataToFile(ModifyPwdActivity.this,
								Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME,
								LoginUserInfoManager.getInstance()
										.getLoginedUserInfo());
				ModifyPwdActivity.this.sendBroadcast(new Intent(
						Constants.ACTION_ACCOUNT_HAVE_MODIFY));
				Toast.makeText(ModifyPwdActivity.this,
						getResources().getString(R.string.modify_pwd_success),
						Toast.LENGTH_SHORT).show();
			} else if (rspSetPwd.getRescode() == 5)
			{
				// userToken错误 重新登录
				Toast.makeText(this,
						getResources().getString(R.string.re_login),
						Toast.LENGTH_SHORT).show();
				LoginActivity.startLoginActivity(this, action);
			} else
			{
				Toast.makeText(ModifyPwdActivity.this, rspSetPwd.getResmsg(),
						Toast.LENGTH_SHORT).show();
			}
			return rspSetPwd.getRescode();
		} catch (Exception e)
		{
			DLog.e(TAG, "parseModifyPwdResult()#exception:", e);
			return -1;
		}
	}

	/**
	 * 启动修改密码界面
	 * 
	 * @param context
	 */
	public static void startModifyPwdActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				ModifyPwdActivity.class), action);
	}

}
