package com.xd.leplay.store.gui.webview;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.util.ToolsUtil;

/**
 * 关注爱玩详情界面(webView)
 * 
 * @author lilijun
 *
 */
public class WeixinPublicAccountWebViewActivity extends BaseActivity
{
	private WebView webView;

	private String loadUrl = "";

	private String title = "";

	@Override
	protected void initView()
	{
		action = DataCollectionManager
				.getAction(
						DataCollectionManager
								.getIntentDataCollectionAction(getIntent()),
						DataCollectionConstant.DATA_COLLECTION_ATTENTION_AIWAN_DETAIL_VALUE);
		DataCollectionManager.getInstance().addRecord(action);
		webView = new WebView(this);
		setCenterView(webView);

		loadUrl = getIntent().getStringExtra("loadUrl");
		title = getIntent().getStringExtra("title");
		titleView.setTitleName(title);
		titleView.setBottomLineVisible(true);
		titleView.setRightLayVisible(false);
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

		webView.addJavascriptInterface(new WebJavaScriptInterface(), "android");
	}

	@Override
	public void onBackPressed()
	{
		if (webView.canGoBack())
		{
			// 返回上一页面
			webView.goBack();
		} else
		{
			super.onBackPressed();
		}
	}

	final class WebJavaScriptInterface
	{
		@JavascriptInterface
		public void copy(final String account)
		{
			DLog.i("lilijun", "微信公众帐号点击复制了account---->>" + account);
			boolean isCopy = ToolsUtil.copy(account,
					WeixinPublicAccountWebViewActivity.this);
			if (isCopy)
			{
				Toast.makeText(
						WeixinPublicAccountWebViewActivity.this,
						getResources().getString(
								R.string.copy_public_account_account_success),
						Toast.LENGTH_SHORT).show();
				// 打开微信
				boolean isToWeiXin = ToolsUtil.openSoftware(
						WeixinPublicAccountWebViewActivity.this,
						"com.tencent.mm");
				if (!isToWeiXin)
				{
					Toast.makeText(
							WeixinPublicAccountWebViewActivity.this,
							getResources().getString(
									R.string.no_installed_weixin),
							Toast.LENGTH_SHORT).show();
				}
			} else
			{
				Toast.makeText(WeixinPublicAccountWebViewActivity.this,
						getResources().getString(R.string.copy_failed),
						Toast.LENGTH_SHORT).show();
				// 打开微信
				boolean isToWeiXin = ToolsUtil.openSoftware(
						WeixinPublicAccountWebViewActivity.this,
						"com.tencent.mm");
				if (!isToWeiXin)
				{
					Toast.makeText(
							WeixinPublicAccountWebViewActivity.this,
							getResources().getString(
									R.string.no_installed_weixin),
							Toast.LENGTH_SHORT).show();
				}
			}

		}
	}

	public static void startActivity(Context context, String title, String url,
			String action)
	{
		Intent intent = new Intent(context,
				WeixinPublicAccountWebViewActivity.class);
		if ("".equals(url.trim()) || "".equals(title))
		{
			return;
		}
		intent.putExtra("title", title);
		intent.putExtra("loadUrl", url);
		DataCollectionManager.startActivity(context, intent, action);
	}

}
