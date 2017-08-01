package com.xd.leplay.store.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DownloadAdInfoManager;
import com.xd.leplay.store.gui.webview.AdWebViewActivity;
import com.xd.leplay.store.util.NetUtil;
import com.xd.leplay.store.util.ParseUtil;
import com.xd.leplay.store.util.ToolsUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 广告的工具类
 * Created by yb on 2017/4/1.
 */
public class AdUtils {

    private static final String TAG = "AdUtils";

//    /**
//     * 请求普通广告
//     *
//     * @param activity
//     * @param adUnitId
//     * @param isSupportDeepLink
//     * @param adWidth
//     * @param adHeight
//     */
//    public static void requestAd(final Activity activity,String
//            adUnitId, boolean isSupportDeepLink, int adWidth, int adHeight,final
// OnResponseListener listener) {
//        Map<String, Object> params = new HashMap<>();
////        params.put("adunitid", "E98C0619C442DDB0479B1D5E8E3D8C0B");
//        // 添加广告位id 不是appId
//        params.put("adunitid", adUnitId);
//        // 是否支持深度链接  0=不支持，1=支持，默认为0
//        params.put("is_support_deeplink", isSupportDeepLink ? 0 : 1);
//        // TODO 当前网络类型，需要在调用到时候去获取网络类型 暂时写死
//        params.put("net", 2);
//        // 当前网络ip地址，需要在调用到时候去获取ip地址
//        params.put("ip", ToolsUtil.getIPAddress(activity));
//
//        // 广告位的宽度 px为单位
//        params.put("adw", adWidth);
//        // 广告位的高度 px为单位
//        params.put("adh", adHeight);
//        NetUtil.doRequestByAds("http://ws.voiceads.cn/ad/request", params, new NetUtil
//                .OnAdsResponseListener() {
//            @Override
//            public void onLoadSuccess(final JSONObject resultObject) {
//                DLog.i("llj", "api接入banner广告成功------->>>" + resultObject.toString());
//
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        AdInfo adInfo = ParseUtil.getBannerAdInfo(resultObject);
//                        if (adInfo != null) {
////                                adsLay.setVisibility(View.VISIBLE);
//                            if(listener != null){
//                                listener.onSuccess(adInfo);
//                            }
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onLoadFailed(String msg) {
//                DLog.i("llj", "api接入banner广告失败----msg-->>>" + msg);
//                if(listener != null){
//                    listener.onError();
//                }
//            }
//
//            @Override
//            public void onNetError(String msg) {
//                DLog.i("llj", "api接入banner广告失败----网络连接异常--！！");
//                if(listener != null){
//                    listener.onError();
//                }
//            }
//        });
//    }


    /**
     * 请求普通广告
     *
     * @param activity
     * @param isBoot  是否是开屏
     * @param adUnitId
     * @param isSupportDeepLink
     * @param adWidth
     * @param adHeight
     */
    public static void requestAd(final Activity activity, boolean isBoot,String adUnitId, boolean
            isSupportDeepLink, int adWidth, int adHeight, final OnResponseListener listener) {
        Map<String, Object> params = new HashMap<>();
        // 添加广告位id 不是appId
        params.put("adunitid", adUnitId);
        // 是否支持深度链接  0=不支持，1=支持，默认为0
        params.put("is_support_deeplink", isSupportDeepLink ? 0 : 1);
        // TODO 当前网络类型，需要在调用到时候去获取网络类型 暂时写死
        params.put("net", 2);
        // 当前网络ip地址，需要在调用到时候去获取ip地址
        params.put("ip", ToolsUtil.getIPAddress(activity));
        // 广告位的宽度 px为单位
//        params.put("adw", adWidth);
        params.put("adw", adWidth);
        // 广告位的高度 px为单位
        params.put("adh", adHeight);
        // 是否开屏 1=开屏，0=非开屏
        params.put("isboot", isBoot ? 1 : 0);
        NetUtil.doRequestByAds("http://ws.voiceads.cn/ad/request", params, new NetUtil
                .OnAdsResponseListener() {
            @Override
            public void onLoadSuccess(JSONObject resultObject) {
                final AdInfo adInfo = ParseUtil.getBannerAdInfo(resultObject);
                if (adInfo != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onSuccess(adInfo);
                            }
                        }
                    });
                }
            }

            @Override
            public void onLoadFailed(final String msg) {
                DLog.e("llj", "请求广告数据失败----msg-->>>" + msg);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onError(msg);
                        }
                    }
                });
            }

            @Override
            public void onNetError(final String msg) {
                DLog.e("llj", "请求广告数据失败,网络异常----msg-->>>" + msg);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onError(msg);
                        }
                    }
                });
            }
        });
    }

//    /**
//     * 请求普通广告(非主线程回调)
//     *
//     * @param context
//     * @param adUnitId
//     * @param isSupportDeepLink
//     * @param adWidth
//     * @param adHeight
//     */
//    public static void requestAd2(Context context, String adUnitId, boolean
//            isSupportDeepLink, int adWidth, int adHeight, final OnResponseListener listener) {
//        Map<String, Object> params = new HashMap<>();
//        // 添加广告位id 不是appId
//        params.put("adunitid", adUnitId);
//        // 是否支持深度链接  0=不支持，1=支持，默认为0
//        params.put("is_support_deeplink", isSupportDeepLink ? 0 : 1);
//        // TODO 当前网络类型，需要在调用到时候去获取网络类型 暂时写死
//        params.put("net", 2);
//        // 当前网络ip地址，需要在调用到时候去获取ip地址
//        params.put("ip", ToolsUtil.getIPAddress(context));
//        // 广告位的宽度 px为单位
//        params.put("adw", adWidth);
//        // 广告位的高度 px为单位
//        params.put("adh", adHeight);
//        NetUtil.doRequestByAds("http://ws.voiceads.cn/ad/request", params, new NetUtil
//                .OnAdsResponseListener() {
//            @Override
//            public void onLoadSuccess(JSONObject resultObject) {
//                final AdInfo adInfo = ParseUtil.getBannerAdInfo(resultObject);
//                if (adInfo != null) {
//                    if (listener != null) {
//                        listener.onSuccess(adInfo);
//                    }
//                }
//            }
//
//            @Override
//            public void onLoadFailed(final String msg) {
//                DLog.e("llj", "请求广告数据失败----msg-->>>" + msg);
//                if (listener != null) {
//                    listener.onError(msg);
//                }
//            }
//
//            @Override
//            public void onNetError(final String msg) {
//                DLog.e("llj", "请求广告数据失败,网络异常----msg-->>>" + msg);
//                if (listener != null) {
//                    listener.onError(msg);
//                }
//            }
//        });
//    }

    /**
     * 普通广告点击事件处理
     *
     * @param activity
     * @param adInfo
     * @param downX
     * @param downY
     * @param upX
     * @param upY
     * @param listener
     */
    public static void doClick(final Activity activity, final AdInfo adInfo, float
            downX, float downY, float upX, float upY, final OnAdClickListener listener) {
        if (adInfo.isClick() || adInfo.getClickUrls() == null) {
            // 已经点击过广告, 或者 没有需要上报的点击链接，直接处理点击 不上报日志
            doAdByType(activity, adInfo, listener);
            return;
        }
        // 有clickUrl需要上报
        final int length = adInfo.getClickUrls().length;
//        final Integer[] times = {0};
        for (int i = 0; i < length; i++) {
            // 先组每组的点击url
            String url = ToolsUtil.replaceAdClickUrl(adInfo.getClickUrls()[i], downX, downY, upX,
                    upY);
            adInfo.getClickUrls()[i] = url;
//            NetUtil.requestUrl(url, new Callback() {
//                @Override
//                public void onFailure(Request request, IOException e) {
//                    // 一次失败，所有的都失败了  不进行广告操作
//                    times[0]++;
//                }
//
//                @Override
//                public void onResponse(Response response) throws IOException {
//                    DLog.i("llj", "请求click_url成功！！！");
//                    // 设置已经点击
//                    adInfo.setClick(true);
//                    synchronized (times) {
//                        times[0]++;
//                        if (times[0] == length) {
//                            // 成功了
//                            doAdByType(activity, adInfo, listener);
//                        }
//                    }
//                }
//            });
        }

        NetUtil.requestUrls(adInfo.getClickUrls(), new NetUtil.OnRequestMoreListener() {
            @Override
            public void onSuccess() {
                DLog.i("llj", "请求多个点击url成功！！！");
                doAdByType(activity, adInfo, listener);
            }

            @Override
            public void onError() {
                DLog.i("llj", "请求多个点击url失败！！！");
                doAdByType(activity, adInfo, listener);
                // 重新设置回未点击的状态，为了下次点击依旧会上报点击url
                adInfo.setClick(false);
            }
        });
    }


//    /**
//     * 普通广告点击事件处理（传我们服务器处理点击接口）
//     *
//     * @param activity
//     * @param adInfo
//     * @param downX
//     * @param downY
//     * @param upX
//     * @param upY
//     * @param listener
//     */
//    public static void doClick2(final Activity activity, final AdInfo adInfo, float
//            downX, float downY, float upX, float upY, final OnAdClickListener listener) {
//        if (adInfo.isClick()) {
//            // 已经点击过广告,直接处理点击 不上报日志
//            doAdByType(activity, adInfo, listener);
//            return;
//        }
////        // 有clickUrl需要上报
////        final int length = adInfo.getClickUrls().length;
////        final Integer[] times = {0};
//        JSONObject clickObject = getRequestClickUrls("url", adInfo.getClickUrls(), downX,
//                downY, upX, upY);
//        NetUtil.requestUrl2("http://120.76.228.105:49520/kdxf/callback", clickObject, new
//                Callback() {
//                    @Override
//                    public void onFailure(Request request, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Response response) throws IOException {
//                        DLog.i("llj", "上报数据成功返回 response.body---->>" + response.body().string());
//                        // 成功了
//                        doAdByType(activity, adInfo, listener);
//                    }
//                });
//    }

//    /**
//     * 获取点击上报报文
//     *
//     * @param key
//     * @param urls
//     * @param downX
//     * @param downY
//     * @param upX
//     * @param upY
//     * @return
//     */
//    private static JSONObject getRequestClickUrls(String key, String[] urls, float downX, float
//            downY, float upX, float upY) {
//        JSONObject clickObject = new JSONObject();
//        JSONArray clickArray = new JSONArray();
//        if (urls == null) {
//            return clickObject;
//        }
//        try {
//            // 有clickUrl需要上报
//            final int length = urls.length;
//            for (int i = 0; i < length; i++) {
//                String url = ToolsUtil.replaceAdClickUrl(urls[i], downX, downY,
//                        upX,
//                        upY);
//                DLog.i("llj","点击上报路径url("+i+")-------->>>"+url);
//                clickArray.put(i, url);
////                NetUtil.requestUrl(url, new Callback() {
////                    @Override
////                    public void onFailure(Request request, IOException e) {
////                        // 一次失败，所有的都失败了  不进行广告操作
////                        times[0]++;
////                    }
////
////                    @Override
////                    public void onResponse(Response response) throws IOException {
////                        DLog.i("llj", "请求click_url成功！！！");
////                        // 设置已经点击
////                        adInfo.setClick(true);
////                        synchronized (times) {
////                            times[0]++;
////                            if (times[0] == length) {
////                                // 成功了
////                                doAdByType(activity, adInfo, listener);
////                            }
////                        }
////                    }
////                });
//            }
//            clickObject.put(key, clickArray);
//        } catch (Exception e) {
//            DLog.e(TAG, "获取点击上报数据发生异常#Exception:\n", e);
//        }
//        return clickObject;
//    }

    /**
     * 获取普通上报报文
     *
     * @param key
     * @param urls
     * @return
     */
    public static JSONObject getRequestUrls(String key, String[] urls) {
        JSONObject clickObject = new JSONObject();
        JSONArray clickArray = new JSONArray();
        if (urls == null) {
            return clickObject;
        }
        try {
            // 有clickUrl需要上报
            final int length = urls.length;
            for (int i = 0; i < length; i++) {
                clickArray.put(i, urls[i]);
//                NetUtil.requestUrl(url, new Callback() {
//                    @Override
//                    public void onFailure(Request request, IOException e) {
//                        // 一次失败，所有的都失败了  不进行广告操作
//                        times[0]++;
//                    }
//
//                    @Override
//                    public void onResponse(Response response) throws IOException {
//                        DLog.i("llj", "请求click_url成功！！！");
//                        // 设置已经点击
//                        adInfo.setClick(true);
//                        synchronized (times) {
//                            times[0]++;
//                            if (times[0] == length) {
//                                // 成功了
//                                doAdByType(activity, adInfo, listener);
//                            }
//                        }
//                    }
//                });
            }
            clickObject.put("url", clickArray);
        } catch (Exception e) {
            DLog.e(TAG, "获取点击上报数据发生异常#Exception:\n", e);
        }
        return clickObject;
    }

    /**
     * 根据广告类型处理点击事件
     *
     * @param activity
     * @param adInfo
     * @param listener
     */
    private static void doAdByType(final Activity activity, final AdInfo adInfo, final
    OnAdClickListener listener) {
        // 设置已经点击
        adInfo.setClick(true);
        if (TextUtils.equals(AdInfo.AD_TYPE_REDIRECT, adInfo.getAdType())) {
            // 跳转类型
            DLog.i("llj", "跳转类型！！！");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(adInfo.getDeepLink())) {
                        // 有deepLink
                        DLog.i("llj", "有deepLink 尝试跳转到其它app！！！");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(adInfo
                                .getDeepLink()));
                        if (deviceCanHandleIntent(activity, intent)) {
                            // 可以跳转到其它app
                            activity.startActivity(intent);
                            return;
                        }
                        DLog.i("llj", "有deepLink 但跳转失败！！！");
                    }
                    DLog.i("llj", "有deepLink 但跳转失败,跳转到webView");
//                    if(TextUtils.isEmpty(adInfo.getLandingUrl())){
//                        if (listener != null) {
//                            listener.onClick(adInfo.getAdType());
//                        }
//                        return;
//                    }
                    AdWebViewActivity.startWebViewActivity(activity, activity.getResources()
                            .getString(R.string.ad), adInfo.getLandingUrl(), "");
                    if (listener != null) {
                        listener.onClick(adInfo.getAdType());
                    }
                }
            });

        } else if (TextUtils.equals(AdInfo.AD_TYPE_DOWNLOAD,
                adInfo.getAdType())) {
            // 下载类型
            DLog.i("llj", "下载类型!!!，添加到下载管理器！！！");
            if (DownloadManager.shareInstance().isDownloading
                    (adInfo.getPackageName())) {
                // 如果有正在下载的任务
                // 上报开始下载的url
//                JSONObject requestObject = AdUtils.getRequestUrls("inst_downstart_url", adInfo
//                        .getInstDownloadStartUrls());
//                NetUtil.requestUrl2("http://120.76.228.105:49520/kdxf/callback",
//                        requestObject, new Callback() {
//                            @Override
//                            public void onFailure(Request request, IOException e) {
//
//                            }
//
//                            @Override
//                            public void onResponse(Response response) throws IOException {
//                                DLog.i("llj", "开始下载上报数据成功!!! response.body---->>>" +
//                                        response.body().string());
//                            }
//                        });

                NetUtil.requestUrls(adInfo.getInstDownloadStartUrls(), new NetUtil
                        .OnRequestMoreListener() {
                    @Override
                    public void onSuccess() {
                        DLog.i("llj", "上报开始下载的信息成功！！");
                    }

                    @Override
                    public void onError() {
                        DLog.i("llj", "上报开始下载的信息失败！！");
                    }
                });
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, activity.getResources().getString(R.string
                                .begin_download_exist), Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onClick(adInfo.getAdType());
                        }
                    }
                });
            } else {
                // 没有正在下载的任务
                // 不管有没有此任务，都删掉
                DownloadManager.shareInstance().deleteDownloadByPackage(adInfo.getPackageName());
                boolean isSuccess = DownloadManager.shareInstance().addDownload(adInfo
                        .toDownloadInfo());
                if (isSuccess) {
                    // 先添加为下载类型的广告到管理器中去
                    DownloadAdInfoManager.getInstance().addDownloadAdInfo(adInfo);
                    // 上报开始下载的url
//                    JSONObject requestObject = AdUtils.getRequestUrls("inst_downstart_url", adInfo
//                            .getInstDownloadStartUrls());
//                    NetUtil.requestUrl2("http://120.76.228.105:49520/kdxf/callback",
//                            requestObject, new Callback() {
//                                @Override
//                                public void onFailure(Request request, IOException e) {
//
//                                }
//
//                                @Override
//                                public void onResponse(Response response) throws IOException {
//                                    DLog.i("llj", "开始下载上报数据成功!!! response.body---->>>" +
//                                            response.body().string());
//                                }
//                            });
                    NetUtil.requestUrls(adInfo.getInstDownloadStartUrls(), new NetUtil
                            .OnRequestMoreListener() {

                        @Override
                        public void onSuccess() {
                            DLog.i("llj", "上报开始下载的信息成功！！");
                        }

                        @Override
                        public void onError() {
                            DLog.i("llj", "上报开始下载的信息失败！！");
                        }
                    });
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity.getApplicationContext(), activity
                                    .getResources().getString(R.string.begin_download), Toast
                                    .LENGTH_SHORT).show();
                            if (listener != null) {
                                listener.onClick(adInfo.getAdType());
                            }
                        }
                    });
                }
            }
        }
    }

    public interface OnResponseListener {
        void onSuccess(AdInfo adInfo);

        void onError(String msg);
    }

    public interface OnAdClickListener {
        void onClick(String type);
    }

    /**
     * deepLink适配方法
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean deviceCanHandleIntent(final Context context, final Intent intent) {
        try {
            final PackageManager packageManager = context.getPackageManager();
            final List<ResolveInfo> activities = packageManager
                    .queryIntentActivities(intent, 0);
            return !activities.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }


//    /**
//     * 请求普通广告
//     *
//     * @param activity
//     * @param isBoot            是否是开屏
//     * @param adUnitId
//     * @param isSupportDeepLink
//     * @param adWidth
//     * @param adHeight
//     */
//    public static void requestUrl(final Activity activity, boolean isBoot, String adUnitId, boolean
//            isSupportDeepLink, int adWidth, int adHeight, final OnResponseListener listener) {
//        Map<String, Object> params = new HashMap<>();
//        // 添加广告位id 不是appId
//        params.put("adunitid", adUnitId);
//        // 是否支持深度链接  0=不支持，1=支持，默认为0
//        params.put("is_support_deeplink", isSupportDeepLink ? 0 : 1);
//        // TODO 当前网络类型，需要在调用到时候去获取网络类型 暂时写死
//        params.put("net", 2);
//        // 当前网络ip地址，需要在调用到时候去获取ip地址
//        params.put("ip", ToolsUtil.getIPAddress(activity));
//        // 广告位的宽度 px为单位
//        params.put("adw", adWidth);
//        // 广告位的高度 px为单位
//        params.put("adh", adHeight);
//        // 是否开屏 1=开屏，0=非开屏
//        params.put("isboot", isBoot ? 1 : 0);
//        NetUtil.doRequest("http://120.76.228.105:49520/kdxf/request", params, new NetUtil
//                .OnAdsResponseListener() {
//            @Override
//            public void onLoadSuccess(JSONObject resultObject) {
//                final AdInfo adInfo = ParseUtil.getBannerAdInfo(resultObject);
//                if (adInfo != null) {
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (listener != null) {
//                                listener.onSuccess(adInfo);
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onLoadFailed(final String msg) {
//                DLog.e("llj", "请求广告数据失败----msg-->>>" + msg);
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (listener != null) {
//                            listener.onError(msg);
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onNetError(final String msg) {
//                DLog.e("llj", "请求广告数据失败,网络异常----msg-->>>" + msg);
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (listener != null) {
//                            listener.onError(msg);
//                        }
//                    }
//                });
//            }
//        });
//    }
}
