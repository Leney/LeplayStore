package com.xd.leplay.store.util;

import android.text.TextUtils;

import com.xd.base.util.DLog;
import com.xd.leplay.store.model.AdInfo;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 解析数据工具类
 * Created by llj on 2017/3/31.
 */
public class ParseUtil {
    private static final String TAG = "ParseUtil";

    /**
     * 获取科大讯飞banner广告数据
     *
     * @param resultObject
     * @return
     */
    public static AdInfo getBannerAdInfo(JSONObject resultObject) {
        AdInfo adInfo = null;
        try {
            JSONArray batchMaArray = resultObject.getJSONArray("batch_ma");
            adInfo = new AdInfo();
            int length = batchMaArray.length();
            String adType = resultObject.getString("adtype");
            for (int i = 0; i < length; i++) {
                JSONObject batchMaObject = batchMaArray.getJSONObject(i);
                adInfo.setAdType(adType);

                // 默认未点击
                adInfo.setClick(false);
                if(batchMaObject.has("ad_source_mark")){
                    adInfo.setAdSourceMark(batchMaObject.getString("ad_source_mark"));
                }
                if(batchMaObject.has("landing_url")){
                    adInfo.setLandingUrl(batchMaObject.getString("landing_url"));
                }
                if (batchMaObject.has("image")) {
                    adInfo.setImageUrl(batchMaObject.getString("image"));
                }
                if (batchMaObject.has("icon")) {
                    adInfo.setIcon(batchMaObject.getString("icon"));
                }
                if (batchMaObject.has("title")) {
                    adInfo.setTitle(batchMaObject.getString("title"));
                    DLog.i("llj","解析广告数据有titile---->>>"+adInfo.getTitle());
                }
                if (batchMaObject.has("sub_title")) {
                    adInfo.setSubTile(batchMaObject.getString("sub_title"));
                    DLog.i("llj","解析广告数据有sub_title---->>>"+adInfo.getSubTile());
                }
                if (batchMaObject.has("right_icon_url")) {
                    adInfo.setRightIconUrl(batchMaObject.getString("right_icon_url"));
                }
                if(batchMaObject.has("deep_link")){
                    adInfo.setDeepLink(batchMaObject.getString("deep_link"));
                }

                JSONArray clickUrlArray = batchMaObject.getJSONArray("click_url");
                int clickLength = clickUrlArray.length();
                adInfo.setClickUrls(new String[clickLength]);
                for (int j = 0; j < clickLength; j++) {
                    // 获取click_url数组具体值
                    adInfo.getClickUrls()[j] = clickUrlArray.getString(j);
                }

                JSONArray imprUrlArray = batchMaObject.getJSONArray("impr_url");
                int imprLength = imprUrlArray.length();
                adInfo.setImprUrls(new String[imprLength]);
                for (int j = 0; j < imprLength; j++) {
                    // 获取impr_url数组具体值
                    adInfo.getImprUrls()[j] = imprUrlArray.getString(j);
                }

                if (TextUtils.equals(AdInfo.AD_TYPE_DOWNLOAD, adType)) {
                    // 如果是下载类型的广告
                    adInfo.setPackageName(batchMaObject.getString("package_name"));

                    // 开始安装时上报的url
                    JSONArray installStartArray = batchMaObject.getJSONArray
                            ("inst_installstart_url");
                    int installStartLength = installStartArray.length();
                    adInfo.setInstInstallStartUrls(new String[installStartLength]);
                    for (int j = 0; j < installStartLength; j++) {
                        adInfo.getInstInstallStartUrls()[j] = installStartArray.getString(j);
                    }

                    // 安装成功时上报的url
                    JSONArray installSuccArray = batchMaObject.getJSONArray
                            ("inst_installsucc_url");
                    int installSuccessLength = installSuccArray.length();
                    adInfo.setInstInstallSuccUrls(new String[installSuccessLength]);
                    for (int j = 0; j < installSuccessLength; j++) {
                        adInfo.getInstInstallSuccUrls()[j] = installSuccArray.getString(j);
                    }

                    // 开始下载时上报的url
                    JSONArray downloadStartArray = batchMaObject.getJSONArray
                            ("inst_downstart_url");
                    int downloadStartLength = downloadStartArray.length();
                    adInfo.setInstDownloadStartUrls(new String[downloadStartLength]);
                    for (int j = 0; j < downloadStartLength; j++) {
                        adInfo.getInstDownloadStartUrls()[j] = downloadStartArray.getString(j);
                    }

                    // 下载成功时上报的url
                    JSONArray downloadSuccArray = batchMaObject.getJSONArray
                            ("inst_downsucc_url");
                    int downloadSuccLength = downloadSuccArray.length();
                    adInfo.setInstDownloadSuccUrls(new String[downloadSuccLength]);
                    for (int j = 0; j < downloadSuccLength; j++) {
                        adInfo.getInstDownloadSuccUrls()[j] = downloadSuccArray.getString(j);
                    }
                }
            }
        } catch (Exception e) {
            DLog.e(TAG, "解析Banner广告数据发生异常#Exception:\n", e);
            adInfo = null;
        }
        return adInfo;
    }

}
