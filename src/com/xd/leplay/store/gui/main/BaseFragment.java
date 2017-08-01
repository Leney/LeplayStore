package com.xd.leplay.store.gui.main;

import java.io.IOException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.xd.leplay.store.view.LoadingView;
import com.xd.leplay.store.view.NetErrorView;
import com.google.protobuf.ByteString;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * Fragment 基类
 * 
 * @author lilijun
 *
 */
public abstract class BaseFragment extends Fragment
{
	private static final String TAG = "BaseFragment";

	/** 提交到服务器数据类型 */
	private static MediaType MEDIA_TYPE_MARKDOWN = MediaType
			.parse("text/x-markdown; charset=utf-8");

	public LinearLayout baseView = null;

	protected RelativeLayout centerViewLayout = null;

	protected NetErrorView errorViewLayout = null;

	protected LoadingView loadingView = null;

	protected ChildTitleView titleView = null;

	/** 是否正在加载数据 */
	protected boolean isLoadingData = false;

	protected OkHttpClient okHttpClient;

	/** 数据采集的界面所对应的action */
	protected String action = "";

	private EtagManager etagManager = null;

	/** 加到请求header里面的(上行)etag值的key */
	private String etagUpHeaderKey = "If-None-Match";

	/** 加到请求header里面的(下行)etag值的key */
	private String etagDownHeaderKey = "Etag";

	/** 加到请求header里面的请求tag的key */
	private String reqHeaderKey = "reqKey";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		okHttpClient = LeplayApplication.getInstance().getHttpClient();
		etagManager = EtagManager.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// 初始化基本视图
		baseView = (LinearLayout) inflater.inflate(R.layout.base_fragment_lay,
				container, false);

		titleView = (ChildTitleView) baseView
				.findViewById(R.id.base_fragment_title_view);
		centerViewLayout = (RelativeLayout) baseView
				.findViewById(R.id.base_fragment_center_lay);
		errorViewLayout = (NetErrorView) baseView
				.findViewById(R.id.base_fragment_error_lay);
		loadingView = (LoadingView) baseView
				.findViewById(R.id.base_fragment_loading_view);
		centerViewLayout.setVisibility(View.VISIBLE);
		errorViewLayout.setVisibility(View.GONE);
		errorViewLayout.setRefrushOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				tryAgain();
			}
		});
		initView(baseView);
		return baseView;
	}

	/**
	 * 初始化中间视图
	 * 
	 * @param inflater
	 * @return
	 */
	protected abstract void initView(LinearLayout view);

	/**
	 * 开始加载网络数据
	 * 
	 * @param url
	 *            请求api的网络地址
	 * 
	 * @param body
	 * @param tag
	 *            接口名
	 */
	protected void doLoadData(String url, String[] actions, ByteString[] datas,
			String etagMark)
	{

		if (actions.length != datas.length)
		{
			DLog.e("lilijun", "BaseFragment,action数量必须和data的数量一致!");
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

		Log.i(TAG, "网络请求---->>>" + request.httpUrl());

		okHttpClient.newCall(request).enqueue(new Callback()
		{

			@Override
			public void onResponse(final Response response) throws IOException
			{
				// 获取请求时发送到服务器的reqKey(这个值是缓存前端数据的唯一标识值)
				String reqKey = response.request().header(reqHeaderKey);
				DLog.e("lilijun", "返回响应码-------->>" + response.code());
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
						if (rspPacket.getRescode() == 0)
						{
							Log.i(TAG, "服务器返回正确的数据成功！");
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

							if (getActivity() != null)
							{
								getActivity().runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										// 返回正确数据成功
										if (loadingView != null)
										{
											loadingView.setVisibilyView(false);
										}
										errorViewLayout
												.setVisibility(View.GONE);
										if (centerViewLayout != null)
										{
											centerViewLayout
													.setVisibility(View.VISIBLE);
										}
										loadDataSuccess(rspPacket);
									}
								});
							}
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
								if (getActivity() != null)
								{
									getActivity().runOnUiThread(new Runnable()
									{
										public void run()
										{
											// 服务器出现异常 没有返回正确数据
											Log.e(TAG,
													"能访问服务器，但服务器本身出现异常，加载数据失败！  失败码:"
															+ rspPacket
																	.getRescode());
											// 这里rspPacket返回的actions 是请求TAG
											loadDataFailed(rspPacket);
										}
									});
								}
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
							if (getActivity() != null)
							{
								getActivity().runOnUiThread(new Runnable()
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
			}

			@Override
			public void onFailure(final Request request, IOException e)
			{
				// 判断本地是否缓存了数据
				isLoadingData = false;
				String requestKey = (String) request.header(reqHeaderKey);
				DLog.i("lilijun", "网络错误,requestKey--------->>>" + requestKey);
				if (requestKey != null
						&& etagManager.getEtagMap().get(requestKey) != null)
				{
					DLog.i(TAG, "服务器报错，无法响应,加载本地缓存好的etag数据！！");
					loadCancleData((String[]) request.tag(), requestKey);
				} else
				{
					if (getActivity() != null)
					{
						getActivity().runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								netError((String[]) request.tag());
								Log.e(TAG, "访问服务器出错：" + request.toString());
							}
						});
					}
				}
			}
		});
		isLoadingData = true;
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
			if (getActivity() != null)
			{
				getActivity().runOnUiThread(new Runnable()
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
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "获取缓存数据发生异常#exception:", e);
			if (getActivity() != null)
			{
				getActivity().runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						netError(tag);
					}
				});
			}
		}
	}

	/**
	 * 设置中间试图
	 * 
	 * @param layoutId
	 */
	protected void setCenterView(int layoutId)
	{
		View view = View.inflate(getActivity(), layoutId, null);
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
	 * 加载数据失败(能连接到服务器，但服务器那边报错，返回异常状态码)
	 * 
	 * @param rspPacket
	 */
	protected void loadDataFailed(RspPacket rspPacket)
	{

	}

	/**
	 * 加载数据失败(网络错误，无法连接到服务器，UI线程执行)
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
	}

}
