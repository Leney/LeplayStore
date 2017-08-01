package com.xd.leplay.store.gui.webview;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.gui.main.MainActivity;

/**
 * 网页类 webview(普通的网页类)
 *
 * @author lilijun
 */
public class AdWebViewActivity extends BaseActivity {
    private WebView webView;

    private String loadUrl = "";

    private String title = "";

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
        titleView.setTitleName(title);
//        titleView.setBottomLineVisible(true);
//        titleView.setRightLayVisible(false);
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
                    centerViewLayout.setVisibility(View.VISIBLE);
                } else {
                    // 加载中
                }
                super.onProgressChanged(view, newProgress);
            }
        });

//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                if (newProgress == 100) {
//                    // 加载完成
//                    setAddViewVisible(false);
//                    showCenterView();
//                } else {
//                    // 加载中
//                    bar.setProgress(newProgress);
//                }
//                super.onProgressChanged(view, newProgress);
//            }
//        });

        WebSettings settings = webView.getSettings();
        // 启用支持javascript
        settings.setJavaScriptEnabled(true);
        // // 优先使用缓存
        // settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        // 不使用缓存
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

//        settings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        settings.setLoadWithOverviewMode(true);

        //设置字符编码
        settings.setDefaultTextEncodingName("uft-8");
        // 是否支持网页缩放
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm
                    .TEXT_AUTOSIZING);
        } else {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

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
        try {
            // 反射方法 暂停webView
            webView.getClass().getMethod("onPause").invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();

        // 打开主页
        MainActivity.startActivity(AdWebViewActivity.this);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.setVisibility(View.GONE);
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
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
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(title)) {
            Toast.makeText(context.getApplicationContext(), context.getResources().getString(R
                    .string.invaild_url), Toast
                    .LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(context, AdWebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("loadUrl", url);
        DataCollectionManager.startActivity(context, intent, action);
    }
}
