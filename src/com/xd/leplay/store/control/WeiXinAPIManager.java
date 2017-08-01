package com.xd.leplay.store.control;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.LeplayApplication;
import com.xd.leplay.store.R;
import com.xd.leplay.store.des.Des;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqLoginOrReg;
import com.xd.leplay.store.model.proto.Uac.ReqSetUserInfo;
import com.xd.leplay.store.model.proto.Uac.RspLogUser;
import com.xd.leplay.store.model.proto.Uac.RspSetUserInfo;
import com.xd.leplay.store.util.NetUtil;
import com.xd.leplay.store.util.NetUtil.OnNetResponseLinstener;
import com.xd.leplay.store.util.ToolsUtil;
import com.google.protobuf.ByteString;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信朋友圈Api管理类
 * 
 * @author lilijun
 *
 */
public class WeiXinAPIManager
{
	private final String TAG = "WeiXinAPIManager";

	private Context mContext;

	private static WeiXinAPIManager instance;

	private IWXAPI weiXinApi = null;

	private String accessTokenUrl = "";

	// private String wxOperateType = "0";// 微信操作类型：0--登录，1--绑定
	//
	// private String inviteCode;// 微信登录邀请码

	private OnWeiXinResponsLinstener linstener;

	public static WeiXinAPIManager getInstance()
	{
		if (instance == null)
		{
			synchronized (WeiXinAPIManager.class)
			{
				instance = new WeiXinAPIManager();
			}
		}
		return instance;
	}

	public void init(Context context)
	{
		this.mContext = context;
		weiXinApi = WXAPIFactory.createWXAPI(mContext, Constants.WEIXIN_APP_ID,
				true);
		weiXinApi.registerApp(Constants.WEIXIN_APP_ID);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_WEIXIN_LOGIN_RESP_CODE);
		mContext.registerReceiver(recever, filter);
	}
	

	public void setLinstener(OnWeiXinResponsLinstener linstener)
	{
		this.linstener = linstener;
	}

	private WeiXinAPIManager()
	{
	}

	/**
	 * 获取微信Api对象
	 * 
	 * @return
	 */
	public IWXAPI getWeiXinApi()
	{
		return weiXinApi;
	}

	/**
	 * 跳转到登录微信界面
	 */
	public void wxLogin(OnWeiXinResponsLinstener linstener, final String... str)
	{
		this.linstener = linstener;
		// 0--登录，1--绑定
		if (!weiXinApi.isWXAppInstalled())
		{
			linstener.onError("请先安装微信应用");
			return;
		} else

			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					weiXinApi.registerApp(Constants.WEIXIN_APP_ID);
					SendAuth.Req req = new SendAuth.Req();
					req.scope = "snsapi_userinfo";
					req.state = "wechat_sdk_demo";
					JSONObject jsonObject = new JSONObject();
					if (str != null)
					{
						for (int i = 0; i < str.length; i++)
						{
							try
							{
								if (i == 0)
								{
									jsonObject.put("type", str[i]);
								} else if (i == 1)
								{
									jsonObject.put("inviteCode", str[i]);
								}
							} catch (Exception e)
							{
								DLog.e(TAG, "wxLogin()#exception:", e);
							}
						}

					}
					req.transaction = jsonObject.toString();

					weiXinApi.sendReq(req);
				}
			}).start();
	}

	/**
	 * 跳转到登录微信界面2
	 */
	public void wxLogin2(final String... str)
	{
		// 0--登录，1--绑定
		if (!weiXinApi.isWXAppInstalled())
		{
			linstener.onError("请先安装微信应用");
			return;
		} else

			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					weiXinApi.registerApp(Constants.WEIXIN_APP_ID);
					SendAuth.Req req = new SendAuth.Req();
					req.scope = "snsapi_userinfo";
					req.state = "wechat_sdk_demo";
					JSONObject jsonObject = new JSONObject();
					if (str != null)
					{
						for (int i = 0; i < str.length; i++)
						{
							try
							{
								if (i == 0)
								{
									jsonObject.put("type", str[i]);
								} else if (i == 1)
								{
									jsonObject.put("inviteCode", str[i]);
								}
							} catch (Exception e)
							{
								DLog.e(TAG, "wxLogin()#exception:", e);
							}
						}

					}
					req.transaction = jsonObject.toString();

					weiXinApi.sendReq(req);
				}
			}).start();
	}

	/**
	 * 获取access_token的URL（微信）
	 * 
	 * @param code
	 *            授权时，微信回调给的
	 * @return URL
	 */
	public static String getCodeRequestUrl(String code)
	{
		String result = null;
		String getCodeAppidUrl = Constants.GET_WEIXIN_CODE_REQUEST_URL.replace(
				"APPID", ToolsUtil.urlEnodeUTF8(Constants.WEIXIN_APP_ID));
		String getCodeSecretUrl = getCodeAppidUrl.replace("SECRET",
				ToolsUtil.urlEnodeUTF8(Constants.WEIXIN_APP_SECRET));
		String getUrl = getCodeSecretUrl.replace("CODE",
				ToolsUtil.urlEnodeUTF8(code));
		result = getUrl;
		return result;
	}

	/**
	 * 获取用户个人信息的URL（微信）
	 * 
	 * @param access_token
	 *            获取access_token时给的
	 * @param openid
	 *            获取access_token时给的
	 * @return URL
	 */
	public static String getUserInfoUrl(String access_token, String openid)
	{
		String result = null;
		String getTokenUrl = Constants.GET_WEIXIN_USER_INFO_URL.replace(
				"ACCESS_TOKEN", ToolsUtil.urlEnodeUTF8(access_token));
		String getOppenIdUrl = getTokenUrl.replace("OPENID",
				ToolsUtil.urlEnodeUTF8(openid));
		result = getOppenIdUrl;
		return result;
	}

	/**
	 * 获取access_token等等的信息(微信)
	 */
	public void getWxAccessTokenInfo(String acces_token, final String tag)
	{
		Request request = null;
		request = new Request.Builder().url(acces_token).build();
		LeplayApplication.getInstance().getHttpClient().newCall(request)
				.enqueue(new Callback()
				{
					@Override
					public void onFailure(final Request request,
							IOException arg1)
					{
						DLog.e(TAG, "获取微信access_token出错：" + request.toString());
						// linstener.onError(mContext.getResources().getString(
						// R.string.internet_error));
						linstenerOnError(mContext.getResources().getString(
								R.string.internet_error));
					}

					@Override
					public void onResponse(Response response)
							throws IOException
					{

						String josn = response.body().string();
						JSONObject json;
						try
						{
							json = new JSONObject(josn);
							String access_token = (String) json
									.get("access_token");
							String openid = (String) json.get("openid");
							String get_user_info_url = WeiXinAPIManager
									.getInstance().getUserInfoUrl(access_token,
											openid);
							getWxUserInfo(get_user_info_url, tag);

						} catch (JSONException e)
						{
							e.printStackTrace();
						}

					}

				});

	}

	/**
	 * 获取微信用户个人信息
	 * 
	 * @param get_user_info_url
	 *            调用URL
	 */
	private void getWxUserInfo(String get_user_info_url, final String tag)
	{
		Request request = null;
		request = new Request.Builder().url(get_user_info_url).build();
		LeplayApplication.getInstance().getHttpClient().newCall(request)
				.enqueue(new Callback()
				{

					@Override
					public void onFailure(final Request request,
							IOException arg1)
					{
						DLog.e(TAG, "获取微信个人信息出错：" + request.toString());
						// linstener.onError(mContext.getResources().getString(
						// R.string.internet_error));
						linstenerOnError(mContext.getResources().getString(
								R.string.internet_error));
					}

					@Override
					public void onResponse(Response response)
							throws IOException
					{
						final String respUserInfoJson = response.body()
								.string();

						if (respUserInfoJson != null
								&& !respUserInfoJson.contains("Error"))
						{
							try
							{
								JSONObject json = new JSONObject(
										respUserInfoJson);
								String wxNickName = (String) json
										.get("nickname");
								String wxHeadIconUrl = (String) json
										.get("headimgurl");
								String wxUnionId = (String) json.get("unionid");
								JSONObject tagObject = new JSONObject(tag);
								String type = "-1";
								String code = "-1";
								if (tagObject.has("type"))
								{
									type = tagObject.getString("type");
								}
								if (tagObject.has("inviteCode"))
								{
									code = tagObject.getString("inviteCode");
								}
								if ("-1".equals(type))
								{
									// linstener
									// .onError(mContext
									// .getResources()
									// .getString(
									// R.string.wx_authorize_failed));
									linstenerOnError(mContext
											.getResources()
											.getString(
													R.string.wx_authorize_failed));
									return;
								}
								if (Integer.parseInt(type) == 0)
								{
									wxReqLogin(wxNickName, wxHeadIconUrl,
											wxUnionId, code);

								} else if (Integer.parseInt(type) == 1)
								{
									wxReqBind(wxUnionId);
								}

							} catch (JSONException e)
							{
								e.printStackTrace();
							}
						}
					}
				});
	}

	/**
	 * 向服务器请求登录
	 */
	private void wxReqLogin(String wxNickName, String wxHeadIconUrl,
			String wxUnionId, String code)
	{
		if (Des.encrypt(wxUnionId.getBytes(), ConstantManager.getInstance()
				.getConstantInfo().getDesKey()) == null)
		{
			// linstener.onError(mContext.getResources().getString(
			// R.string.login_failed));
			linstenerOnError(mContext.getResources().getString(
					R.string.login_failed));
			DLog.i("lilijun", "---------->>>>"
					+ ConstantManager.getInstance().getConstantInfo()
							.getDesKey());
		} else
		{
			// if (linstener != null)
			// {
			// linstener.onSubmitData();
			// }
			linstenerOnSubmit();
			ReqLoginOrReg.Builder builder = ReqLoginOrReg.newBuilder();
			builder.setAccountName(Des.encrypt(wxUnionId.getBytes(),
					ConstantManager.getInstance().getConstantInfo().getDesKey()));
			// builder.setInviteCode(inviteCode);
			builder.setInviteCode(code);
			builder.setUserType(1);
			builder.setHeadPicUrl(wxHeadIconUrl);
			builder.setNickName(wxNickName);
			NetUtil.doLoadData(Constants.UAC_API_URL, new String[]
			{ "ReqLoginOrReg" }, new ByteString[]
			{ builder.build().toByteString() }, new OnNetResponseLinstener()
			{

				@Override
				public void onNetError(String[] tags)
				{
					DLog.e(TAG, "网络错误！！！");
					// linstener.onError(mContext.getResources().getString(
					// R.string.internet_error));
					linstenerOnError(mContext.getResources().getString(
							R.string.internet_error));
				}

				@Override
				public void onLoadSuccess(RspPacket rspPacket)
				{
					parseRegisterResult(rspPacket);
				}

				@Override
				public void onLoadFailed(RspPacket rspPacket)
				{
					DLog.e(TAG, "微信登录失败！msg---->>>" + rspPacket.getResmsg()
							+ "  code---->>>" + rspPacket.getRescode());
					// linstener.onError(mContext.getResources().getString(
					// R.string.login_failed));
					linstenerOnError(mContext.getResources().getString(
							R.string.login_failed));
				}
			});
		}
	}

	/**
	 * 向服务器请求绑定
	 */
	private void wxReqBind(String wxOpenId)
	{
		if (Des.encrypt(wxOpenId.getBytes(), ConstantManager.getInstance()
				.getConstantInfo().getDesKey()) == null)
		{
			// linstener.onError(mContext.getResources().getString(
			// R.string.bind_failed));
			linstenerOnError(mContext.getResources().getString(
					R.string.bind_failed));
			DLog.i("lilijun", "---------->>>>"
					+ ConstantManager.getInstance().getConstantInfo()
							.getDesKey());
		} else
		{
			// if (linstener != null)
			// {
			// linstener.onSubmitData();
			// }
			linstenerOnSubmit();
			ReqSetUserInfo.Builder builder = ReqSetUserInfo.newBuilder();
			builder.setUid(LoginUserInfoManager.getInstance()
					.getLoginedUserInfo().getUserId());
			builder.setUserToken(LoginUserInfoManager.getInstance()
					.getLoginedUserInfo().getUserToken());
			builder.setWxUnionId(Des.encrypt(wxOpenId.getBytes(),
					ConstantManager.getInstance().getConstantInfo().getDesKey()));
			// 这里传-1是为了配合后台 传入-1时 后台就不对Sex去进行处理了
			builder.setUserSex(-1);
			NetUtil.doLoadData(Constants.UAC_API_URL, new String[]
			{ "ReqSetUserInfo" }, new ByteString[]
			{ builder.build().toByteString() }, new OnNetResponseLinstener()
			{
				@Override
				public void onNetError(String[] tags)
				{
					DLog.e(TAG, "网络错误！！！");
					// linstener.onError(mContext.getResources().getString(
					// R.string.internet_error));
					linstenerOnError(mContext.getResources().getString(
							R.string.internet_error));
				}

				@Override
				public void onLoadSuccess(RspPacket rspPacket)
				{
					parseModifyUserInfoResultData(rspPacket.getParams(0));
				}

				@Override
				public void onLoadFailed(RspPacket rspPacket)
				{
					DLog.e(TAG, "微信绑定失败！msg---->>>" + rspPacket.getResmsg()
							+ "  code---->>>" + rspPacket.getRescode());
					// linstener.onError(mContext.getResources().getString(
					// R.string.bind_failed));
					linstenerOnError(mContext.getResources().getString(
							R.string.bind_failed));
				}
			});
		}
	}

	/**
	 * 解析注册成功之后 返回的登录信息
	 * 
	 * @param rspPacket
	 * @return
	 * @throws Exception
	 */
	private void parseRegisterResult(RspPacket rspPacket)
	{
		try
		{
			RspLogUser rspRegUser = RspLogUser
					.parseFrom(rspPacket.getParams(0));
			DLog.e("lilijun", "登录返回--------->>>" + rspRegUser.getRescode());
			// 0=成功,1=失败,2=仅支持11位手机号注册,3=该用户已被注册
			if (rspRegUser.getRescode() == 0)
			{
				LoginedUserInfo userInfo = ToolsUtil
						.getLoginedUserInfo(rspRegUser);
				// 将登录的账户信息保存到缓存中去
				ToolsUtil.saveCachDataToFile(mContext,
						Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME, userInfo);
				// 将登录后的用户信息保存到登录帐号信息管理类中去
				LoginUserInfoManager.getInstance().setLoginedUserInfo(userInfo);
				// linstener.onSuccess();
				linstenerOnSuccess();
			} else
			{
				// linstener.onError(rspRegUser.getResmsg());
				linstenerOnError(rspRegUser.getResmsg());
				// return rspRegUser.getResmsg();
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "parseRegisterResult()#Excepton:", e);
			// linstener.onError(mContext.getResources().getString(
			// R.string.login_failed));
			linstenerOnError(mContext.getResources().getString(
					R.string.login_failed));
		}

	}

	/**
	 * 解析修改用户信息的结果数据
	 * 
	 * @param rspPacket
	 * @return
	 * @throws Exception
	 */
	private void parseModifyUserInfoResultData(ByteString result)
	{
		try
		{

			RspSetUserInfo rspSetUserInfo = RspSetUserInfo.parseFrom(result);
			// 0=成功,1=响应错误,2=token错误,3=用户不存在或被禁用
			if (rspSetUserInfo.getRescode() == 0)
			{
				LoginedUserInfo userInfo = ToolsUtil
						.getLoginedUserInfo(rspSetUserInfo);
				// 将登录的账户信息保存到缓存中去
				ToolsUtil.saveCachDataToFile(mContext,
						Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME, userInfo);
				// 将登录后的用户信息保存到登录帐号信息管理类中去
				LoginUserInfoManager.getInstance().setLoginedUserInfo(userInfo);
				// linstener.onSuccess();
				linstenerOnSuccess();

			} else if (rspSetUserInfo.getRescode() == 2)
			{

				// 返回-1，表示userToken错误，登录信息过期，需要重新登录，调用的地方去做处理
				// linstener.onError("-1");
				linstenerOnError("-1");
			} else
			{
				DLog.i(TAG, rspSetUserInfo.getResmsg());
				// linstener.onError(rspSetUserInfo.getResmsg());
				linstenerOnError(rspSetUserInfo.getResmsg());

			}
		} catch (Exception e)
		{
			DLog.e(TAG, "parseModifyUserInfoResultData()#Excepton:", e);
		}

	}

	private BroadcastReceiver recever = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction() == Constants.ACTION_WEIXIN_LOGIN_RESP_CODE)
			{
				String code = intent.getStringExtra("wx_login_resp_code");
				String tag = intent.getStringExtra("tag");
				if (code != null)
				{
					// code返回
					/*
					 * 将你前面得到的AppID、AppSecret、code，拼接成URL
					 */
					accessTokenUrl = getCodeRequestUrl(code);
					// Thread thread = new Thread(downloadRun);
					Thread thread = new Thread(new DownLoadRun(tag));
					thread.start();
					try
					{
						thread.join();
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				} else
				{
					// linstener.onError(mContext.getResources().getString(
					// R.string.wx_authorize_failed));
					try
					{
						// 休眠100毫秒(让微信授权界面完全finish掉，显示登录界面)
						Thread.sleep(100);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					linstenerOnError(mContext.getResources().getString(
							R.string.wx_authorize_failed));
					DLog.i(TAG, "微信授权失败");
				}
			}
		}
	};

	public class DownLoadRun implements Runnable
	{
		String tag = "-1";

		public DownLoadRun(String tag)
		{
			this.tag = tag;
		}

		@Override
		public void run()
		{
			try
			{
				// 休眠100毫秒(让微信授权界面完全finish掉，显示登录界面)
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			getWxAccessTokenInfo(accessTokenUrl, tag);
		}

	}

	private void linstenerOnSuccess()
	{
		if (linstener == null)
		{
			return;
		}
		linstener.onSuccess();
	}

	private void linstenerOnError(String msg)
	{
		if (linstener == null)
		{
			return;
		}
		linstener.onError(msg);
	}

	private void linstenerOnSubmit()
	{
		if (linstener == null)
		{
			return;
		}
		linstener.onSubmitData();
	}

}
