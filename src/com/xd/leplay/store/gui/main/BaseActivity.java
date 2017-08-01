package com.xd.leplay.store.gui.main;

import java.io.IOException;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.LeplayApplication;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.EtagManager;
import com.xd.leplay.store.model.EtagInfo;
import com.xd.leplay.store.model.proto.Packet.ReqPacket;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.view.ChildTitleView;
import com.xd.leplay.store.view.LoadingView2;
import com.xd.leplay.store.view.NetErrorView;
import com.google.protobuf.ByteString;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.umeng.analytics.MobclickAgent;

/**
 * Activity 基类
 * 
 * @author lilijun
 * 
 */
public abstract class BaseActivity extends AppCompatActivity
{
	private static final String TAG = "BaseActivity";

	/** 提交到服务器数据类型 */
	private static MediaType MEDIA_TYPE_MARKDOWN = MediaType
			.parse("text/x-markdown; charset=utf-8");

	protected RelativeLayout centerViewLayout = null;

	protected NetErrorView errorViewLayout = null;

	protected LoadingView2 loadingView = null;

	protected ChildTitleView titleView = null;

	/** 数据采集的界面所对应的action */
	protected String action = "";

	protected OkHttpClient okHttpClient;

	private EtagManager etagManager = null;

	/** 加到请求header里面的(上行)etag值的key */
	private String etagUpHeaderKey = "If-None-Match";

	/** 加到请求header里面的(下行)etag值的key */
	private String etagDownHeaderKey = "Etag";

	/** 加到请求header里面的请求tag的key */
	private String reqHeaderKey = "reqKey";

	/** 当前页面的标识值 */
	private String currentAction = "-1";

	/** 是否需要友盟进行数据采集 */
	protected boolean isNeedCollection = true;

	public BaseActivity()
	{
		super();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (isNeedCollection)
		{
			int lastIndex = action.lastIndexOf("-");
			if (lastIndex != -1)
			{
				// 不是一级界面
				currentAction = action.substring(lastIndex + 1);
			} else
			{
				// 只是一级界面
				currentAction = action;
			}
			MobclickAgent.onPageStart(currentAction);
			MobclickAgent.onResume(this);
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (isNeedCollection)
		{
			MobclickAgent.onPageEnd(currentAction);
			MobclickAgent.onPause(this);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.base_activity);
		titleView = (ChildTitleView) findViewById(R.id.base_activity_title_view);
		centerViewLayout = (RelativeLayout) findViewById(R.id.base_activity_center_lay);
		errorViewLayout = (NetErrorView) findViewById(R.id.base_activity_error_lay);
		loadingView = (LoadingView2) findViewById(R.id.base_activity_loading_view);
		errorViewLayout.setVisibility(View.GONE);
		centerViewLayout.setVisibility(View.VISIBLE);
		errorViewLayout.setRefrushOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				tryAgain();
			}
		});

		okHttpClient = LeplayApplication.getInstance().getHttpClient();
		etagManager = EtagManager.getInstance();
		initView();
		readBundle(savedInstanceState);
	}

	/**
	 * 开始加载网络数据
	 * 
	 * @param url
	 *            请求api的网络地址
	 * @param etagMark
	 *            etag标识值(用来区分请求时 同一个的请求接口tag名称的不同数据,可以传 "" 当请求uac时，都为"")
	 */
	protected void doLoadData(String url, String[] actions, ByteString[] datas,
			String etagMark)
	{
		if (actions.length != datas.length)
		{
			DLog.e("lilijun", "BaseActivity,action数量必须和data的数量一致!");
			return;
		}
		ReqPacket.Builder packetBuilder = ReqPacket.newBuilder();
		String reqKey = "";
		if (actions.length == 1)
		{
			packetBuilder.addAction(actions[0]);
			packetBuilder.addParams(datas[0]);
			reqKey = actions[0];
		} else
		{
			for (int i = 0; i < actions.length; i++)
			{
				packetBuilder.addAction(actions[i]);
				packetBuilder.addParams(datas[i]);
				reqKey += actions[i];
			}
		}
		reqKey += etagMark;

		packetBuilder.setUdi(DataCollectionConstant.combinationUDI);
		packetBuilder.setClientVer(DataCollectionConstant.versionName);
		packetBuilder.setChannel(DataCollectionConstant.channelNo);
		// String str = packetBuilder.build().toByteString().toString();
		RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN,
				packetBuilder.build().toByteArray());

		Request request = null;
		if (url.equals(Constants.APP_API_URL))
		{
			EtagInfo etagInfo = etagManager.getEtagMap().get(reqKey);
			if (etagInfo != null)
			{
				if (etagInfo.getEtagValue() != null)
				{
					request = new Request.Builder()
							.url(url)
							.post(body)
							.tag(actions)
							.addHeader(etagUpHeaderKey, etagInfo.getEtagValue())
							.addHeader(reqHeaderKey, reqKey).build();
				} else
				{
					request = new Request.Builder().url(url).post(body)
							.tag(actions).addHeader(etagUpHeaderKey, "")
							.addHeader(reqHeaderKey, reqKey).build();
				}
			} else
			{
				request = new Request.Builder().url(url).post(body)
						.tag(actions).addHeader(etagUpHeaderKey, "")
						.addHeader(reqHeaderKey, reqKey).build();
			}
		} else
		{
			// 请求uac地址 header什么都不加
			request = new Request.Builder().url(url).post(body).tag(actions)
					.build();
		}

		DLog.i(TAG, "网络请求---->>>" + request.httpUrl());
		okHttpClient.newCall(request).enqueue(new Callback()
		{

			@Override
			public void onResponse(final Response response) throws IOException
			{
				// final RspPacket rspPacket =
				// RspPacket.parseFrom(response.body()
				// .bytes());
				// runOnUiThread(new Runnable()
				// {
				// @Override
				// public void run()
				// {
				// if (rspPacket.getRescode() == 0)
				// {
				// // 返回正确数据成功
				// DLog.i(TAG, "服务器返回正确的数据成功！");
				// if (loadingView != null)
				// {
				// loadingView.setVisibilyView(false);
				// }
				// errorViewLayout.setVisibility(View.GONE);
				// if (centerViewLayout != null)
				// {
				// centerViewLayout.setVisibility(View.VISIBLE);
				// }
				// loadDataSuccess(rspPacket);
				// } else
				// {
				// // 服务器出现异常 没有返回正确数据
				// DLog.e(TAG, "能访问服务器，但服务器本身出现异常，加载数据失败！  失败码:"
				// + rspPacket.getRescode());
				// // 这里rspPacket返回的actions 是请求TAG
				// loadDataFailed(rspPacket);
				// }
				// isLoadingData = false;
				// }
				// });

				// 获取请求时发送到服务器的reqKey(这个值是缓存前端数据的唯一标识值)
				String reqKey = response.request().header(reqHeaderKey);
				DLog.i("lilijun", "返回响应码-------->>" + response.code());
				if (response.code() == 304)
				{
					// 表明没有变化 直接读取本地缓存好的数据
					DLog.i(TAG, "加载本地缓存好的etag数据！！");
					loadCancleData((String[]) response.request().tag(), reqKey);
				} else
				{
					if (response.code() == 200)
					{
						// 服务端数据有变化
						byte[] resultBytes = response.body().bytes();
						final RspPacket rspPacket = RspPacket
								.parseFrom(resultBytes);
						DLog.e("lilijun", "rspPacket.getRescode()------>>>"
								+ rspPacket.getRescode());
						if (rspPacket.getRescode() == 0)
						{
							DLog.i(TAG, "服务器返回正确的数据成功！");
							// 获取到请求是url
							String requestUrl = response.request().urlString();
							if (Constants.APP_API_URL.equals(requestUrl))
							{
								// 只在请求的是APP_API_URL时，才进行缓存etag等数据
								// 获取到服务器返回的Etag值
								String etagValue = response
										.header(etagDownHeaderKey);
								if (etagValue != null)
								{
									DLog.i(TAG, "得到新的Etag数据！！");
									EtagInfo etagInfo = new EtagInfo();
									etagInfo.setEtagValue(etagValue);
									etagInfo.setResponseBodyBytes(resultBytes);
									// 将新的返回数据缓存到本地
									etagManager.addEtagInfo(reqKey, etagInfo);
								}
							}
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									// 返回正确数据成功
									if (loadingView != null)
									{
										loadingView.setVisibilyView(false);
									}
									errorViewLayout.setVisibility(View.GONE);
									if (centerViewLayout != null)
									{
										centerViewLayout
												.setVisibility(View.VISIBLE);
									}
									loadDataSuccess(rspPacket);
								}
							});
						} else
						{
							if (reqKey != null
									&& etagManager.getEtagMap().get(reqKey) != null)
							{
								DLog.i(TAG,
										"能访问服务器，但服务器本身出现异常，加载本地缓存好的etag数据！！");
								loadCancleData((String[]) response.request()
										.tag(), reqKey);
							} else
							{
								runOnUiThread(new Runnable()
								{
									public void run()
									{
										// 服务器出现异常 没有返回正确数据
										DLog.e(TAG,
												"能访问服务器，但服务器本身出现异常，加载数据失败！  失败码:"
														+ rspPacket
																.getRescode());
										// 这里rspPacket返回的actions 是请求TAG
										loadDataFailed(rspPacket);
									}
								});
							}
						}
					} else
					{
						DLog.e(TAG, "服务器报错，无法响应，返回码---->>>" + response.code());
						if (reqKey != null
								&& etagManager.getEtagMap().get(reqKey) != null)
						{
							DLog.i(TAG, "服务器报错，无法响应,加载本地缓存好的etag数据！！");
							loadCancleData((String[]) response.request().tag(),
									reqKey);
						} else
						{
							runOnUiThread(new Runnable()
							{
								public void run()
								{
									// 前端自己构造一个RspPacket对象 返回给子类
									String[] requestTag = (String[]) response
											.request().tag();
									RspPacket.Builder builder = RspPacket
											.newBuilder();
									for (String tag : requestTag)
									{
										builder.addAction(tag);
									}
									builder.setRescode(-1);
									builder.setResmsg("resonpse failed, result code = "
											+ response.code());
									// 这里rspPacket返回的actions 是请求TAG
									loadDataFailed(builder.build());
								}
							});
						}
					}
				}
			}

			@Override
			public void onFailure(final Request request, IOException e)
			{
				// 访问服务器出错，有可能是路径有问题也有可能是网络连接异常
				String requestKey = (String) request.header(reqHeaderKey);
				DLog.i("lilijun", "网络错误,requestKey--------->>>" + requestKey);
				if (requestKey != null
						&& etagManager.getEtagMap().get(requestKey) != null)
				{
					DLog.i(TAG, "服务器报错，无法响应,加载本地缓存好的etag数据！！");
					loadCancleData((String[]) request.tag(), requestKey);
				} else
				{
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							netError((String[]) request.tag());
							DLog.e(TAG, "访问服务器出错：" + request.toString());
						}
					});
				}
			}
		});
	}

	/**
	 * 加载缓存数据
	 * 
	 * @param reqKey
	 */
	private void loadCancleData(final String[] tag, String reqKey)
	{
		try
		{
			final RspPacket rspPacket = RspPacket.parseFrom(etagManager
					.getEtagMap().get(reqKey).getResponseBodyBytes());
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (loadingView != null)
					{
						loadingView.setVisibilyView(false);
					}
					errorViewLayout.setVisibility(View.GONE);
					if (centerViewLayout != null)
					{
						centerViewLayout.setVisibility(View.VISIBLE);
					}
					loadDataSuccess(rspPacket);
				}
			});
		} catch (Exception e)
		{
			DLog.e(TAG, "获取缓存数据发生异常#exception:", e);
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					netError(tag);
				}
			});
		}
	}

	/**
	 * 初始化中间视图
	 * 
	 * @param inflater
	 * @return
	 */
	protected void readBundle(Bundle savedInstanceState)
	{
	};

	protected abstract void initView();

	/**
	 * 设置中间试图
	 * 
	 * @param layoutId
	 */
	protected void setCenterView(int layoutId)
	{
		View view = View.inflate(this, layoutId, null);
		setCenterView(view);
	}

	/**
	 * 设置中间试图
	 * 
	 * @param view
	 */
	protected void setCenterView(View view)
	{
		setCenterView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	/**
	 * 设置中间试图
	 * 
	 * @param view
	 * @param params
	 */
	protected void setCenterView(View view, ViewGroup.LayoutParams params)
	{
		centerViewLayout.addView(view, params);
	}

	/**
	 * 设置是否显示标题
	 *
	 * @param visible
	 */
	protected void setTitleVisible(boolean visible)
	{
		if (visible)
		{
			titleView.setVisibility(View.VISIBLE);
		} else
		{
			titleView.setVisibility(View.GONE);
		}
	}

	/**
	 * 如果第一次加载失败，点击错误视图重新加载所调用的函数
	 */
	protected void tryAgain()
	{
		loadingView.setVisibilyView(true);
		errorViewLayout.setVisibility(View.GONE);
		centerViewLayout.setVisibility(View.GONE);
	}

	/**
	 * 加载数据成功，UI线程操作
	 * 
	 * @param requestType
	 */
	protected void loadDataSuccess(RspPacket rspPacket)
	{

		// centerViewLayout.setVisibility(View.VISIBLE);
		// errorViewLayout.setVisibility(View.GONE);
		// loadingView.setVisibilyView(false);
	};

	/**
	 * 加载数据失败，UI线程操作(能连接到服务器，但服务器那边报错，返回异常状态码)
	 * 
	 * @param rspPacket
	 */
	protected void loadDataFailed(RspPacket rspPacket)
	{

	}

	/**
	 * 加载数据失败(网络错误，无法连接到服务器，UI线程操作)
	 * 
	 * @param actions
	 */
	protected void netError(String[] actions)
	{

	}

	/**
	 * 显示加载失败视图
	 * 
	 * @param request
	 */
	protected void showErrorView()
	{
		errorViewLayout.setVisibility(View.VISIBLE);
		loadingView.setVisibilyView(false);
		centerViewLayout.setVisibility(View.GONE);
	};

	/**
	 * 显示正文视图
	 */
	protected void showContentView()
	{
		centerViewLayout.setVisibility(View.VISIBLE);
		loadingView.setVisibilyView(false);
		errorViewLayout.setVisibility(View.GONE);
	}

	/**
	 * 显示正在加载视图
	 */
	protected void showLoadingView()
	{
		loadingView.setVisibilyView(true);
		centerViewLayout.setVisibility(View.GONE);
		errorViewLayout.setVisibility(View.GONE);
	}
}
