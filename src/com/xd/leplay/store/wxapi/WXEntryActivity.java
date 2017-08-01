package com.xd.leplay.store.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.control.WeiXinAPIManager;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler
{
	private IWXAPI weiXinApi;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		weiXinApi = WeiXinAPIManager.getInstance().getWeiXinApi();
		weiXinApi.handleIntent(getIntent(), this);

	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req)
	{
		finish();
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp)
	{
		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH)
		{
			Intent intent = new Intent(Constants.ACTION_WEIXIN_LOGIN_RESP_CODE);
			intent.putExtra("wx_login_resp_code", ((SendAuth.Resp) resp).code);
			intent.putExtra("tag", resp.transaction);
			DLog.e("lilijun", "传过来的tag----->>>" + resp.transaction);
			sendBroadcast(intent);
		}
		finish();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		setIntent(intent);
		weiXinApi.handleIntent(intent, this);
		finish();
	}

}
