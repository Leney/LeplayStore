package com.xd.leplay.store.gui.webview;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.main.BaseActivity;

/**
 * 网页类 webview(普通的网页类)
 *
 * @author lilijun
 */
public class WebViewActivity extends BaseActivity {
    private WebView webView;

    private String loadUrl = "";

    private String title = "";

    /**
     * 是否从闪屏跳转过来的
     */
    private boolean isFromSplash = false;

    @Override
    protected void initView() {
        action = DataCollectionManager.getAction(DataCollectionManager
                        .getIntentDataCollectionAction(getIntent()),
                DataCollectionConstant.DATA_COLLECTION_NORMAL_WEBVIEW_VALUE);
        DataCollectionManager.getInstance().addRecord(action);
        webView = new WebView(this);
        setCenterView(webView);

        loadUrl = getIntent().getStringExtra("loadUrl");
        title = getIntent().getStringExtra("title");
        isFromSplash = getIntent().getBooleanExtra("isFromSplash", false);
        titleView.setTitleName(title);
        titleView.setBottomLineVisible(true);
        titleView.setRightLayVisible(false);
        // loadUrl = "http://www.baidu.com";
        webView.loadUrl(loadUrl);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候就在本webView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    loadingView.setVisibilyView(false);
                } else {
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

//		webView.addJavascriptInterface(new DemoJavaScriptInterface(), "android");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            // 返回上一页面
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        if (isFromSplash) {
//			// 跳转到主页
//			Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
//			startActivity(intent);
        }
        super.finish();
    }

//	final class DemoJavaScriptInterface
//	{
//		public void onClick()
//		{
//			DLog.i("lilijun", "界面测试点击了...");
//		}
//	}

    public static void startWebViewActivity(Context context, String title,
                                            String url, String action) {
        Intent intent = new Intent(context, WebViewActivity.class);
        if ("".equals(url.trim()) || "".equals(title)) {
            return;
        }
        intent.putExtra("title", title);
        intent.putExtra("loadUrl", url);
        DataCollectionManager.startActivity(context, intent, action);
    }

    public static void startWebViewActivityFromSplash(Context context,
                                                      String title, String url, String action) {
        Intent intent = new Intent(context, WebViewActivity.class);
        if ("".equals(url.trim()) || "".equals(title)) {
            return;
        }
        intent.putExtra("title", title);
        intent.putExtra("loadUrl", url);
        // 是否是从闪屏处跳转过来
        intent.putExtra("isFromSplash", true);
        DataCollectionManager.startActivity(context, intent, action);
    }

}
