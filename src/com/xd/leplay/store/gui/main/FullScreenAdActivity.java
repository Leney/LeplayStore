package com.xd.leplay.store.gui.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.xd.base.util.DLog;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LeplayPreferences;
import com.xd.leplay.store.model.AdInfo;
import com.xd.leplay.store.model.AdUtils;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.NetUtil;
import com.xd.leplay.store.view.AdImageView;
import com.xd.leplay.store.view.CountDownTextView;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * 全屏广告界面
 * Created by llj on 2017/4/5.
 */
public class FullScreenAdActivity extends Activity {

    private AdImageView adImg;

    private TextView closeBtn, adMark;

    private CountDownTextView countDownTextView;

    private AdInfo adInfo;

    /**
     * 超时次数
     */
    private int totalCount = 3;

    private final int COUNT_DOWN_MSG = 1;

    /**
     * 标识是否请求请成功
     */
    private boolean isSuccess = false;

//    /**
//     * 标识是否点击跳转了
//     */
//    private boolean isClick = false;

    /**
     * 标识是否已经请求广告超时
     */
    private boolean isTimeOut = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == COUNT_DOWN_MSG) {
                if (isSuccess) {
                    handler.removeMessages(COUNT_DOWN_MSG);
                    return;
                }
                if (--totalCount < 0) {
                    // 倒计时完成(请求超时)
                    handler.removeMessages(COUNT_DOWN_MSG);
                    DLog.e("llj", "倒计时完成！！！");
                    isTimeOut = true;
                    // 打开主页
                    MainActivity.startActivity(FullScreenAdActivity.this);
                    finish();
                } else {
                    // 倒计时未完成
                    sendEmptyMessageDelayed(COUNT_DOWN_MSG, 1000);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_full_screen_layout);

        adImg = (AdImageView) findViewById(R.id.full_screen_ad_img);
        adImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AdInfo adInfo = (AdInfo) view.getTag();
                if (adInfo != null) {
                    AdUtils.doClick(FullScreenAdActivity.this, adInfo, adImg.downX, adImg.downY,
                            adImg.upX, adImg.upY, new AdUtils.OnAdClickListener() {
                                @Override
                                public void onClick(String type) {
                                    if (TextUtils.equals(type, AdInfo.AD_TYPE_REDIRECT)) {
                                        // 跳转链接类型，什么都不做
                                        countDownTextView.stop();
                                        countDownTextView.setOnDoneListner(null);
                                        if (TextUtils.isEmpty(adInfo.getLandingUrl())) {
                                            MainActivity.startActivity(FullScreenAdActivity.this);
                                        }
                                        finish();
                                    } else if (TextUtils.equals(type, AdInfo.AD_TYPE_DOWNLOAD)) {
                                        // 下载类型
                                        // 跳转到主页
                                        countDownTextView.stop();
                                        countDownTextView.setOnDoneListner(null);
                                        MainActivity.startActivity(FullScreenAdActivity.this);
                                        finish();
                                    } else if (TextUtils.equals(type, AdInfo.AD_TYPE_BRAND)) {
                                        // 品牌类型
                                        countDownTextView.stop();
                                        countDownTextView.setOnDoneListner(null);
                                        finish();
                                    }
                                }
                            });
//                    isClick = true;
                }
            }
        });

        closeBtn = (TextView) findViewById(R.id.full_screen_close_ad_img);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTextView.stop();
//                finish();
                // 打开主页
                MainActivity.startActivity(FullScreenAdActivity.this);
                finish();
            }
        });
        countDownTextView = (CountDownTextView) findViewById(R.id.full_screen_ad_count_down);
        countDownTextView.setOnDoneListner(new CountDownTextView.OnCountDownDoneListener() {
            @Override
            public void onDone() {
                // 倒计时完成
//                finish();
                DLog.e("llj", "广告展示倒计时完成！！！");
//                if(isClick){
//                    finish();
//                    return;
//                }
                // 跳转到主页
                MainActivity.startActivity(FullScreenAdActivity.this);
                finish();
            }
        });

        adMark = (TextView) findViewById(R.id.full_screen_ad_mark);

        String userAgent = LeplayPreferences.getInstance(FullScreenAdActivity.this).getUserAgent();
        if (TextUtils.isEmpty(userAgent)) {
            userAgent = new WebView(FullScreenAdActivity.this).getSettings().getUserAgentString();
            LeplayPreferences.getInstance(FullScreenAdActivity.this).setUserAgent(userAgent);
            DLog.i("llj", "转义之前 userAgent----->>" + userAgent);
            String escapeUserAgent = StringEscapeUtils.escapeJava(userAgent);
            DLog.i("llj", "转义之之后 escapeUserAgent----->>" + escapeUserAgent);
            DataCollectionConstant.userAgent = escapeUserAgent;
        } else {
            DataCollectionConstant.userAgent = userAgent;
            DLog.i("llj", "userAgent不为空----->>" + userAgent);
        }

        // 请求全屏数据
        requestFullScreenAds();
        handler.sendEmptyMessageDelayed(COUNT_DOWN_MSG, 1000);


//        adInfo = (AdInfo) getIntent().getSerializableExtra("adInfo");
//        if (adInfo == null) {
//            finish();
//        }

//        if (!TextUtils.isEmpty(adInfo.getImageUrl())) {
//            ImageLoaderManager.getInstance().displayImage(adInfo.getImageUrl(), adImg,
//                    DisplayUtil.getScreenShortImageLoaderOptions());
//        } else if (!TextUtils.isEmpty(adInfo.getRightIconUrl())) {
//            ImageLoaderManager.getInstance().displayImage(adInfo.getRightIconUrl(), adImg,
//                    DisplayUtil.getScreenShortImageLoaderOptions());
//        } else if (!TextUtils.isEmpty(adInfo.getIcon())) {
//            ImageLoaderManager.getInstance().displayImage(adInfo.getIcon(), adImg, DisplayUtil
//                    .getScreenShortImageLoaderOptions());
//        }
//        adImg.setTag(adInfo);
//
//        if (adInfo.getImprUrls() != null) {
//            int length = adInfo.getImprUrls().length;
//            for (int i = 0; i < length; i++) {
//                // 加载完成  上报
//                NetUtil.requestUrl(adInfo.getImprUrls()[i], new
//                        Callback() {
//                            @Override
//                            public void onResponse(Response response) throws
//                                    IOException {
//                                DLog.i("llj", "请求一个url成功!!!!");
//                                String result = response.body().string();
//                                DLog.i("llj", "result---->>>" + result);
//                            }
//
//                            @Override
//                            public void onFailure(Request request,
//                                                  IOException e) {
//                                DLog.e("llj", "请求一个url失败!!!");
//                            }
//                        });
//            }
//        }
//        countDownTextView.start(5);
    }

    @Override
    public void onBackPressed() {
        MainActivity.startActivity(FullScreenAdActivity.this);
        super.onBackPressed();
    }

    @Override
    public void finish() {
        handler.removeMessages(COUNT_DOWN_MSG);
        countDownTextView.stop();
        countDownTextView.setOnDoneListner(null);
        super.finish();
    }

    /**
     * 请求全屏广告数据
     */
    private void requestFullScreenAds() {
//                EC515143106BACCD815BB094EA114004
//        8FD0B8613E8DA52F52110EB3B367A3FA", false
        AdUtils.requestAd(FullScreenAdActivity.this,true, "EC515143106BACCD815BB094EA114004", false,
                DataCollectionConstant.screenWidth, DataCollectionConstant.screenHeight, new
                        AdUtils.OnResponseListener() {
                            @Override
                            public void onSuccess(AdInfo adInfo) {
                                isSuccess = true;
                                if (!TextUtils.isEmpty(adInfo.getImageUrl())) {
                                    ImageLoaderManager.getInstance().displayImage(adInfo
                                                    .getImageUrl(), adImg,
                                            DisplayUtil.getScreenShortImageLoaderOptions());
                                } else if (!TextUtils.isEmpty(adInfo.getRightIconUrl())) {
                                    ImageLoaderManager.getInstance().displayImage(adInfo
                                                    .getRightIconUrl(), adImg,
                                            DisplayUtil.getScreenShortImageLoaderOptions());
                                } else if (!TextUtils.isEmpty(adInfo.getIcon())) {
                                    ImageLoaderManager.getInstance().displayImage(adInfo.getIcon
                                            (), adImg, DisplayUtil
                                            .getScreenShortImageLoaderOptions());
                                }
                                adImg.setTag(adInfo);
                                adMark.setText(adInfo.getAdSourceMark() + "|" + getResources()
                                        .getString(R.string.ad));

                                if (adInfo.getImprUrls() != null) {
                                    NetUtil.requestUrls(adInfo.getImprUrls(), new NetUtil.OnRequestMoreListener() {
                                        @Override
                                        public void onSuccess() {
                                            DLog.i("llj", "请求全屏广告曝光完成上报多个url成功!!!!");
                                        }

                                        @Override
                                        public void onError() {
                                            DLog.e("llj", "请求全屏广告曝光完成上报多个url失败!!!!");
                                        }
                                    });
//                                    int length = adInfo.getImprUrls().length;
//                                    for (int i = 0; i < length; i++) {
//                                        // 加载完成  上报
//                                        NetUtil.requestUrl(adInfo.getImprUrls()[i], new
//                                                Callback() {
//                                                    @Override
//                                                    public void onResponse(Response response)
//                                                            throws IOException {
//                                                        DLog.i("llj", "请求一个url成功!!!!");
//                                                        String result = response.body().string();
//                                                        DLog.i("llj", "result---->>>" + result);
//                                                    }
//
//                                                    @Override
//                                                    public void onFailure(Request request,
//                                                                          IOException e) {
//                                                        DLog.e("llj", "请求一个url失败!!!");
//                                                    }
//                                                });
//                                    }
                                }
                                // 移除handler消息
                                handler.removeMessages(COUNT_DOWN_MSG);
                                countDownTextView.start(5);
                            }

                            @Override
                            public void onError(String msg) {
                                // 跳转到主页
                                if (isTimeOut) {
                                    // 已经超时  已经跳转到主页  不用再跳转了
                                    return;
                                }
                                MainActivity.startActivity(FullScreenAdActivity.this);
                                finish();
                            }
                        });

//        AdUtils.requestUrl(FullScreenAdActivity.this, true, "8FD0B8613E8DA52F52110EB3B367A3FA",
//                false, DataCollectionConstant.screenWidth, DataCollectionConstant.screenHeight, new
//                        AdUtils.OnResponseListener() {
//                            @Override
//                            public void onSuccess(AdInfo adInfo) {
//                                isSuccess = true;
//                                if (!TextUtils.isEmpty(adInfo.getImageUrl())) {
//                                    ImageLoaderManager.getInstance().displayImage(adInfo
//                                                    .getImageUrl(), adImg,
//                                            DisplayUtil.getScreenShortImageLoaderOptions());
//                                } else if (!TextUtils.isEmpty(adInfo.getRightIconUrl())) {
//                                    ImageLoaderManager.getInstance().displayImage(adInfo
//                                                    .getRightIconUrl(), adImg,
//                                            DisplayUtil.getScreenShortImageLoaderOptions());
//                                } else if (!TextUtils.isEmpty(adInfo.getIcon())) {
//                                    ImageLoaderManager.getInstance().displayImage(adInfo.getIcon
//                                            (), adImg, DisplayUtil
//                                            .getScreenShortImageLoaderOptions());
//                                }
//                                adImg.setTag(adInfo);
//                                adMark.setText(adInfo.getAdSourceMark() + "|" + getResources()
//                                        .getString(R.string.ad));
//
//
//                                // 获取展示完成上报请求数据
//                                JSONObject requestObject = AdUtils.getRequestUrls("impr_url",
//                                        adInfo.getImprUrls());
//                                NetUtil.requestUrl2("http://120.76.228.105:49520/kdxf/callback",
//                                        requestObject, new Callback() {
//                                    @Override
//                                    public void onFailure(Request request, IOException e) {
//
//                                    }
//
//                                    @Override
//                                    public void onResponse(Response response) throws IOException {
//                                        DLog.i("llj", "全屏展示完成上报数据成功!!! response.body---->>>" +
//                                                response.body().string());
//                                    }
//                                });
////                                if (adInfo.getImprUrls() != null) {
////                                    int length = adInfo.getImprUrls().length;
////                                    for (int i = 0; i < length; i++) {
////                                        // 加载完成  上报
////                                        NetUtil.requestUrl(adInfo.getImprUrls()[i], new
////                                                Callback() {
////                                                    @Override
////                                                    public void onResponse(Response response)
////                                                            throws
////                                                            IOException {
////                                                        DLog.i("llj", "请求一个url成功!!!!");
////                                                        String result = response.body().string();
////                                                        DLog.i("llj", "result---->>>" + result);
////                                                    }
////
////                                                    @Override
////                                                    public void onFailure(Request request,
////                                                                          IOException e) {
////                                                        DLog.e("llj", "请求一个url失败!!!");
////                                                    }
////                                                });
////                                    }
////                                }
//                                // 移除handler消息
//                                handler.removeMessages(COUNT_DOWN_MSG);
//                                countDownTextView.start(5);
//                            }
//
//                            @Override
//                            public void onError(String msg) {
//                                // 跳转到主页
//                                if (isTimeOut) {
//                                    // 已经超时  已经跳转到主页  不用再跳转了
//                                    return;
//                                }
//                                MainActivity.startActivity(FullScreenAdActivity.this);
//                                finish();
//                            }
//                        });
    }

//    public static void startAcitivty(Context context, AdInfo adInfo) {
//        Intent intent = new Intent(context, FullScreenAdActivity.class);
//        intent.putExtra("adInfo", adInfo);
//        context.startActivity(intent);
//    }
}
