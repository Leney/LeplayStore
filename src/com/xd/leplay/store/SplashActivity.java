package com.xd.leplay.store;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.widget.ImageView;

import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xd.base.util.DLog;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LeplayPreferences;
import com.xd.leplay.store.gui.application.ApplicationClassifyDetailActivity;
import com.xd.leplay.store.gui.details.DetailsActivity;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.gui.main.MainActivity;
import com.xd.leplay.store.gui.webview.WebViewActivity;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.model.proto.App;
import com.xd.leplay.store.model.proto.App.AdElement;
import com.xd.leplay.store.model.proto.App.ReqAdElements;
import com.xd.leplay.store.model.proto.App.RspAdElements;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.ToolsUtil;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 闪屏界面
 *
 * @author lilijun
 *
 */
public class SplashActivity extends BaseActivity
{
	private static final String TAG = "SplashActivity";

	private List<AdElement> adElements = null;

	private ImageView splashImg;

	// private TextView skipBtn;

	private ImageLoaderManager imageLoaderManager;

	private DisplayImageOptions options;

	private LeplayPreferences preferences;

	/** 请求广告数据的TAG */
	private final String REQUEST_ADELMENT_TAG = "ReqAdElements";

	/** 闪屏的etag标识值 */
	private String etagMark = "splashEtagMark";

	/** 上一次获得的闪屏地址 */
	private String localUrl = "";

	/** 倒计时控制值 */
	private int countTime = 2;

	/** 图片渐变动画 */
	private AlphaAnimation animation = null;

	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what == 1)
			{
				// 第一次进入直接进入引导页
				if (LeplayPreferences.getInstance(SplashActivity.this)
						.isFirstIn())
				{
					finish();
				} else
				{
					// 跳转到主页
					Intent intent = new Intent(SplashActivity.this,
							MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					SplashActivity.this.startActivity(intent);
					finish();
					return;
				}
			}
		};
	};

	private Runnable countDownThread = new Runnable()
	{
		@Override
		public void run()
		{
			if (countTime <= 0)
			{
				handler.sendEmptyMessage(1);
			} else
			{
				synchronized (this)
				{
					countTime--;
				}
				handler.postDelayed(this, 1000);
			}
		}
	};

	@Override
	protected void initView()
	{
		String userAgent = LeplayPreferences.getInstance(SplashActivity.this).getUserAgent();
		if (TextUtils.isEmpty(userAgent)) {
			userAgent = new WebView(SplashActivity.this).getSettings().getUserAgentString();
			LeplayPreferences.getInstance(SplashActivity.this).setUserAgent(userAgent);
			DLog.i("llj", "转义之前 userAgent----->>" + userAgent);
			String escapeUserAgent = StringEscapeUtils.escapeJava(userAgent);
			DLog.i("llj", "转义之之后 escapeUserAgent----->>" + escapeUserAgent);
			DataCollectionConstant.userAgent = escapeUserAgent;
		} else {
			DataCollectionConstant.userAgent = userAgent;
			DLog.i("llj", "userAgent不为空----->>" + userAgent);
		}
		// 第一次进入直接进入引导页
		if (LeplayPreferences.getInstance(SplashActivity.this).isFirstIn())
		{
			addShortcut();
			Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
			SplashActivity.this.startActivity(intent);
			SplashActivity.this.finish();
			return;
		}

		preferences = LeplayPreferences.getInstance(this);
		localUrl = preferences.getSplashImgUrl();
		if ("".equals(localUrl))
		{
			// 跳转到主页
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			SplashActivity.this.startActivity(intent);
			finish();
			return;
		}

		setTitleVisible(false);
		errorViewLayout.setVisibility(View.GONE);
		loadingView.setVisibilyView(false);
		centerViewLayout.setVisibility(View.VISIBLE);
		action = DataCollectionConstant.DATA_COLLECTION_SPLASH_VALUE;

		setCenterView(R.layout.splash_layout);
		splashImg = (ImageView) findViewById(R.id.splash_img);
		splashImg.setImageResource(R.drawable.splash_screen_img);
		// skipBtn = (TextView) findViewById(R.id.splash_skip_btn);
		// skipBtn.setOnClickListener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// handler.removeCallbacks(countDownThread);
		// // 否则跳转到主页
		// Intent intent = new Intent(SplashActivity.this,
		// MainActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// SplashActivity.this.startActivity(intent);
		// finish();
		// }
		// });

		imageLoaderManager = ImageLoaderManager.getInstance();
		options = DisplayUtil.getSplashImageLoaderOptions();
		adElements = new ArrayList<App.AdElement>();

		// 图片渐变模糊度始终
		animation = new AlphaAnimation(0.1f, 1.0f);
		// 渐变时间
		animation.setDuration(1000);

		imageLoaderManager.displayImage2(localUrl, splashImg, options,
				new ImageLoadingListener()
				{

					@Override
					public void onLoadingStarted(String arg0, View arg1)
					{
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2)
					{
						DLog.e("lilijun", "首次加载闪屏图片失败！！！");
						// 开始计时
						handler.postDelayed(countDownThread, 1000);
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2)
					{
						DLog.e("lilijun", "首次加载闪屏图片完成！！！");
						// 开始计时
						handler.postDelayed(countDownThread, 1000);
						splashImg.startAnimation(animation);
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1)
					{
					}
				});
		doLoadData(Constants.APP_API_URL, new String[]
		{ REQUEST_ADELMENT_TAG }, new ByteString[]
		{ getReqAdElements(1) }, etagMark);
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<String> actions = rspPacket.getActionList();
		for (String action : actions)
		{
			if (action.equals("RspAdElements"))
			{
				// 闪屏数据返回
				parseAdElementsResult(rspPacket);
			}
		}

		if (!adElements.isEmpty())
		{
			AdElement adElement = adElements.get(0);
			splashImg.setTag(adElement);
			splashImg.setOnClickListener(imgOnClickListener);
			// if (localUrl.equals(adElement.getAdsPicUrlPort().trim())
			// || "".equals(adElement.getAdsPicUrlPort().trim()))
			// {
			// Intent intent = new Intent(SplashActivity.this,
			// MainActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// SplashActivity.this.startActivity(intent);
			// finish();
			// return;
			// }
			if ("".equals(adElement.getAdsPicUrlPort().trim()))
			{
				// 没有loading图地址
				preferences.setSplashImgUrl("");
				Intent intent = new Intent(SplashActivity.this,
						MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				SplashActivity.this.startActivity(intent);
				finish();
				return;
			}
			// 将新的闪屏地址设置到配置文件中去
			preferences.setSplashImgUrl(adElement.getAdsPicUrlPort().trim());
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
	}

	/**
	 * 获取ReqAdElemnts的ByteString
	 *
	 * @param type
	 * @return
	 */
	private ByteString getReqAdElements(int type)
	{
		ReqAdElements.Builder adElementsBuilder = ReqAdElements.newBuilder();
		adElementsBuilder.setPosType(type);
		return adElementsBuilder.build().toByteString();
	}

	/**
	 * 解析闪屏广告的返回结果
	 *
	 * @param rspPacket
	 */
	private void parseAdElementsResult(RspPacket rspPacket)
	{
		try
		{
			RspAdElements rspAdElements = RspAdElements.parseFrom(rspPacket
					.getParams(0));
			adElements = rspAdElements.getAdElementsList();
		} catch (Exception e)
		{
			DLog.e(TAG, "parseAdElementsResult()#Excepton:", e);
		}
	}

	/**
	 * 创建快捷方式
	 *
	 */
	private void addShortcut()
	{
		Intent addShortcutIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建
		addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
		// 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
		// 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
		// 屏幕上没有空间时会提示
		// 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式

		// 名字
		addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources()
				.getString(R.string.app_name));

		// 图标
		addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(SplashActivity.this,
						R.drawable.app_icon));

		// 设置关联程序
		Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
		launcherIntent.setClass(SplashActivity.this, SplashActivity.class);
		launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		addShortcutIntent
				.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

		// 发送广播
		sendBroadcast(addShortcutIntent);
	}

	/**
	 * 图片的点击事件
	 */
	private OnClickListener imgOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			AdElement adElement = (AdElement) v.getTag();
			// 元素类型：1=跳转至应用或游戏，2=跳转指定链接，3=跳转至分类
			switch (adElement.getElemType())
			{
			case 1:
				// App详情
				handler.removeCallbacks(countDownThread);
				ListAppInfo appInfo = ToolsUtil.getListAppInfo(adElement
						.getAppInfo());
				DLog.i("lilijun", "appInfo==null----->>>" + (appInfo == null));
				DetailsActivity.startDetailsActivityByIdFromSplash(
						SplashActivity.this, appInfo.getSoftId(), action);
				finish();
				break;
			case 2:
				// webview链接
				String url = adElement.getJumpLinkUrl();
				DLog.i("lilijun", "链接地址------->>>" + url);
				DLog.i("lilijun", "显示名称------->>>" + adElement.getShowName());
				if ("".equals(adElement.getShowName()) || url.equals("#"))
				{
					return;
				}
				handler.removeCallbacks(countDownThread);
				// 如果链接不为空才会跳转
				WebViewActivity.startWebViewActivityFromSplash(
						SplashActivity.this, adElement.getShowName(), url,
						action);
				finish();
				break;
			case 3:
				// 分类列表
				handler.removeCallbacks(countDownThread);
				int appTypeId = adElement.getJumpAppTypeId();
				String appTypeName = adElement.getJumpAppTypeName();
				ApplicationClassifyDetailActivity
						.startApplicationClassifyDetailActivityFromSplash(
								SplashActivity.this, appTypeId, appTypeName,
								action, appTypeId + "");
				finish();
				break;
			}
		}
	};

}
