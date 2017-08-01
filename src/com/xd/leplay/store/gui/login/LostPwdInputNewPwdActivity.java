package com.xd.leplay.store.gui.login;

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
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqFindSetPwd;
import com.xd.leplay.store.model.proto.Uac.RspFindSetPwd;
import com.xd.leplay.store.util.ToolsUtil;
import com.google.protobuf.ByteString;

/**
 * 忘记密码 输入新的密码界面
 * 
 * @author lilijun
 *
 */
public class LostPwdInputNewPwdActivity extends BaseActivity
{

	private final String TAG = "LostPwdInputNewPwdActivity";

	private int userId;

	private String codeToken = "";

	private EditText inputNewPwd1, inputNewPwd2;

	private Button sureBtn;

	/** 修改密码的请求TAG */
	private final String MODIFY_PWD_REQUEST_TAG = "ReqFindSetPwd";

	/** 修改密码的响应TAG */
	private final String MODIFY_PWD_RSPONSE_TAG = "RspFindSetPwd";

	@Override
	protected void initView()
	{
		action = DataCollectionManager
				.getAction(
						DataCollectionManager
								.getIntentDataCollectionAction(getIntent()),
						DataCollectionConstant.DATA_COLLECTION_LOST_PWD_INPUT_NEW_PWD_VALUE);
		DataCollectionManager.getInstance().addRecord(action);
		titleView.setTitleName(getResources().getString(R.string.lost_pwd));
		titleView.setRightLayVisible(false);
		titleView.setBottomLineVisible(true);

		loadingView.setVisibilyView(false);

		userId = getIntent().getIntExtra("userId", -1);
		codeToken = getIntent().getStringExtra("codeToken");

		setCenterView(R.layout.lost_pwd_input_new_pwd_activity);

		inputNewPwd1 = (EditText) findViewById(R.id.lost_pwd_new_pwd_edit1);
		inputNewPwd2 = (EditText) findViewById(R.id.lost_pwd_new_pwd_edit2);
		inputNewPwd1.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		inputNewPwd2.setHintTextColor(getResources().getColor(
				R.color.divider_color));

		sureBtn = (Button) findViewById(R.id.lost_pwd_new_pwd_sure_btn);
		sureBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String newPwd1 = inputNewPwd1.getText().toString().trim();
				String newPwd2 = inputNewPwd2.getText().toString().trim();

				if ("".equals(newPwd1))
				{
					Toast.makeText(
							LostPwdInputNewPwdActivity.this,
							getResources().getString(
									R.string.loast_pwd_input_new_pwd),
							Toast.LENGTH_SHORT).show();
					return;
				} else
				{
					if (newPwd1.length() < 4 || newPwd1.length() > 16)
					{
						Toast.makeText(
								LostPwdInputNewPwdActivity.this,
								getResources().getString(
										R.string.input_new_pwd_error),
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				if ("".equals(newPwd2))
				{
					Toast.makeText(
							LostPwdInputNewPwdActivity.this,
							getResources().getString(
									R.string.loast_pwd_input_new_pwd_again),
							Toast.LENGTH_SHORT).show();
					return;
				} else
				{
					if (!newPwd2.equals(newPwd1))
					{
						Toast.makeText(
								LostPwdInputNewPwdActivity.this,
								getResources().getString(
										R.string.input_new_pwd_affim_error),
								Toast.LENGTH_SHORT).show();
						return;
					}
				}

				String newPwdMd5 = ToolsUtil.getMD5ByString(newPwd1);
				// 提交更改的密码信息
				doLoadData(Constants.UAC_API_URL, new String[]
				{ MODIFY_PWD_REQUEST_TAG }, new ByteString[]
				{ getModifyNewPwdRequestData(userId, newPwdMd5, codeToken) },
						"");
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
				try
				{
					RspFindSetPwd rspFindSetPwd = RspFindSetPwd
							.parseFrom(rspPacket.getParams(0));
					// 0=成功,1=相应错误,2=用户不存在,3=token错误
					if (rspFindSetPwd.getRescode() == 0)
					{
						finish();
					} else if (rspFindSetPwd.getRescode() == 3)
					{
						// 如果是短信验证码有问题 则跳回获取短信验证码的界面
						Toast.makeText(
								LostPwdInputNewPwdActivity.this,
								getResources().getString(R.string.code_invaile),
								Toast.LENGTH_SHORT).show();
						LostPwdGetCodeActivity.startLostPwdGetCodeActivity(
								LostPwdInputNewPwdActivity.this, action);
						finish();
					} else
					{
						Toast.makeText(LostPwdInputNewPwdActivity.this,
								rspFindSetPwd.getResmsg(), Toast.LENGTH_SHORT)
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
		Toast.makeText(LostPwdInputNewPwdActivity.this,
				getResources().getString(R.string.change_new_pwd_failed),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		Toast.makeText(LostPwdInputNewPwdActivity.this,
				getResources().getString(R.string.change_new_pwd_failed),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * 获得更改新密码的请求参数
	 * 
	 * @param userId
	 * @param newPwd
	 * @param codeToken
	 * @return
	 */
	private ByteString getModifyNewPwdRequestData(int userId, String newPwd,
			String codeToken)
	{
		ReqFindSetPwd.Builder builder = ReqFindSetPwd.newBuilder();
		builder.setUid(userId);
		builder.setNewPwd(newPwd);
		builder.setSmsCodeToken(codeToken);
		return builder.build().toByteString();
	}

	public static void startLostPwdInputNewPwdActivity(Context context,
			int userId, String codeToken, String action)
	{
		Intent intent = new Intent(context, LostPwdInputNewPwdActivity.class);
		intent.putExtra("userId", userId);
		intent.putExtra("codeToken", codeToken);
		DataCollectionManager.startActivity(context, intent, action);
	}

}
