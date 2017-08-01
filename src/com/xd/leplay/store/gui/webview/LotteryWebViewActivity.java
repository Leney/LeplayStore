package com.xd.leplay.store.gui.webview;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.util.NetUtil;
import com.xd.leplay.store.util.ToolsUtil;

/**
 * 抽奖界面(webView)
 * 
 * @author lilijun
 *
 */
public class LotteryWebViewActivity extends BaseActivity
{
	private WebView webView;

	private String loadUrl = "";

	private String title = "";

	/** 标识是否开始抽奖了 */
	private boolean isStartLottery = false;

	@Override
	protected void initView()
	{
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_TREASURE_LOTTERY_VALUE);
		DataCollectionManager.getInstance().addRecord(action);
		webView = new WebView(this);

		titleView.setBackLinstener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!isStartLottery)
				{
					finish();
				}
			}
		});
		titleView.setRightFirstImgRes(R.drawable.refresh);
		titleView.setRightFirstImgOnclickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!isStartLottery)
				{
					webView.reload();
				}
			}
		});
		titleView.setRightFirstImgVisible(true);
		titleView.setRightSecondImgVisible(false);

		setCenterView(webView);

		loadUrl = getIntent().getStringExtra("loadUrl");
		title = getIntent().getStringExtra("title");
		titleView.setTitleName(title);
		titleView.setBottomLineVisible(true);
		webView.loadUrl(loadUrl);

		webView.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				// 返回值是true的时候就在本webView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});

		webView.setWebChromeClient(new WebChromeClient()
		{
			@Override
			public void onProgressChanged(WebView view, int newProgress)
			{
				if (newProgress == 100)
				{
					// 网页加载完成
					loadingView.setVisibilyView(false);
				} else
				{
					// 加载中
				}
			}
		});

		WebSettings settings = webView.getSettings();
		// 启用支持javascript
		settings.setJavaScriptEnabled(true);
		// // 优先使用缓存
		// settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// 不使用缓存
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		settings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		settings.setLoadWithOverviewMode(true);

		// //设置字符编码
		// settings.setDefaultTextEncodingName("GBK");
		// 是否支持网页缩放
		// settings.setBuiltInZoomControls(true);
		// settings.setSupportZoom(true);

		webView.addJavascriptInterface(new WebJavaScriptInterface(), "lottery");
	}

	@Override
	public void onBackPressed()
	{
		if (!isStartLottery)
		{
			super.onBackPressed();
		}
	}

	final class WebJavaScriptInterface
	{
		@JavascriptInterface
		public void startLottery()
		{
			DLog.i("lilijun", "开始抽奖！！！");
			isStartLottery = true;
		}

		@JavascriptInterface
		public void lotterySuccess(final String lotteryCoin)
		{
			DLog.i("lilijun", "抽奖完成返回----->>" + lotteryCoin);
			try
			{
				int coins = Integer.parseInt(lotteryCoin);
				if (coins != 0)
				{
					// 中奖了
					LoginedUserInfo userInfo = LoginUserInfoManager
							.getInstance().getLoginedUserInfo();

					if (userInfo != null)
					{
						// 加入总金币数
						userInfo.getTreasureInfo()
								.setCoinNum(
										userInfo.getTreasureInfo().getCoinNum()
												+ coins);
						// 加入今日收入
						userInfo.getTreasureInfo().setTodayCoins(
								userInfo.getTreasureInfo().getTodayCoins()
										+ coins);
						Toast.makeText(
								LotteryWebViewActivity.this,
								String.format(
										getResources().getString(
												R.string.lottery_got_it), coins
												+ ""), Toast.LENGTH_SHORT)
								.show();
						// 保存登录用户信息到本地缓存
						ToolsUtil.saveCachDataToFile(
								LotteryWebViewActivity.this,
								Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME,
								LoginUserInfoManager.getInstance()
										.getLoginedUserInfo());
						sendBroadcast(new Intent(
								Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY));
						// 请求财富信息
						NetUtil.getLoginUserGiftsAndTreasureData(LotteryWebViewActivity.this);
					}
				} else
				{
					// 没有中奖
					DLog.i("lilijun", "没有中奖！");
				}
			} catch (Exception e)
			{
				DLog.e("lilijun", "抽奖成功之后，格式化获得的金币数异常#exception：", e);
			}
			isStartLottery = false;

		}

		@JavascriptInterface
		public void lotteryFailed()
		{
			DLog.i("lilijun", "抽奖失败!!!");
			Toast.makeText(LotteryWebViewActivity.this,
					getResources().getString(R.string.lottery_failed),
					Toast.LENGTH_SHORT).show();
			isStartLottery = false;
		}

	}

	public static void startActivity(Context context, String title, String url,
			String action)
	{
		Intent intent = new Intent(context, LotteryWebViewActivity.class);
		if ("".equals(url.trim()) || "".equals(title))
		{
			return;
		}
		intent.putExtra("title", title);
		intent.putExtra("loadUrl", url);
		DataCollectionManager.startActivity(context, intent, action);
	}

}
