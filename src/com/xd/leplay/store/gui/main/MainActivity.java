package com.xd.leplay.store.gui.main;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LeplayPreferences;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.gui.application.ApplicationFragment;
import com.xd.leplay.store.gui.recommend.RecommendFragment;
import com.xd.leplay.store.gui.treasure.TreasureFragment;
import com.xd.leplay.store.model.AdInfo;
import com.xd.leplay.store.model.AdUtils;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.NetUtil;
import com.xd.leplay.store.view.AdImageView;
import com.xd.leplay.store.view.MainTitleView;

public class MainActivity extends AppCompatActivity implements
        OnCheckedChangeListener {
    private FragmentManager fragmentManager = null;
    /**
     * 当前所在tab的标识值
     */
    private int curTab = 0;

    private RadioGroup radioGroup;

    /**
     * 4个tab的fragment
     */
    private Fragment[] fragments = new Fragment[4];

    /**
     * 主标题
     */
    private MainTitleView mainTitleView = null;

    private long mExitTime = 0;

//    /**
//     * 科大讯飞插屏广告对象
//     */
//    private IFLYInterstitialAd interstitialAd;
//
//    /**
//     * 科大讯飞全屏广告对象
//     */
//    private IFLYFullScreenAd fullScreenAd;

    private Dialog adDialog;

//    private Dialog screenFullDialog;

    //    private ImageView adImg, closeAdImg;
    private AdImageView adImg;
    private ImageView adIcon, closeAdImg;
    private TextView adMark;

//    private FrameLayout showAdLay;

    private TextView adDescrible;

    /**
     * 记录一下进入此界面有多少次了
     */
    private int time = 0;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

//        if (adDialog == null) {
//            adDialog = new Dialog(MainActivity.this);
//            adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            adDialog.setContentView(R.layout.ad_table_layout);
//            adDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
//            showAdLay = (FrameLayout) adDialog.findViewById(R.id.show_ad_lay);
//            adImg = (AdImageView) adDialog.findViewById(R.id.show_ad_img);
//            adImg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    AdInfo adInfo = (AdInfo) view.getTag();
//                    if (adInfo != null) {
//                        AdUtils.doClick(MainActivity.this, adInfo, adImg.downX, adImg.downY,
//                                adImg.upX, adImg.upY);
//                        adDialog.dismiss();
//                    }
//                }
//            });
//            closeAdImg = (ImageView) adDialog.findViewById(R.id.close_ad_img);
//            closeAdImg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    adDialog.dismiss();
//                }
//            });
//            adDescrible = (TextView) adDialog.findViewById(R.id.show_ad_text);
//            adClickBtn = (TextView) adDialog.findViewById(R.id.show_ad_btn);
//            adIcon = (ImageView) adDialog.findViewById(R.id.show_ad_icon);
//        } else {
//            if (!LeplayPreferences.getInstance(MainActivity.this).isFirstIn()) {
        time++;
        if (time % 3 == 0) {
            // 请求显示插屏广告
            requestTableScreenAds();
        }

//            }
//        }
//        if (!LeplayPreferences.getInstance(MainActivity.this).isFirstIn()) {
//            // 如果不是第一次进入商店就
//            setTableScreenAds();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // //设置友盟统计是否是测试模式
        // MobclickAgent.setDebugMode(true);

        setContentView(R.layout.main_tab_activity);
        fragmentManager = getSupportFragmentManager();

        mainTitleView = (MainTitleView) findViewById(R.id.main_tab_title_view);

        radioGroup = (RadioGroup) findViewById(R.id.main_tab_radioGroup);
        radioGroup.setOnCheckedChangeListener(this);

        fragments[0] = new RecommendFragment();
        // fragments[1] = new ApplicationFragment();
//		fragments[1] = new TabTotalTaskListFragment();
        fragments[1] = new ApplicationFragment();
        fragments[2] = new TreasureFragment();

        // 设置进入应用时 默认跳转的tab
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_tab_fragment_lay, fragments[0]);
        transaction.commitAllowingStateLoss();
        curTab = 0;

        IntentFilter filter = new IntentFilter(
                Constants.ACTION_SOFTWARE_MANAGER_GET_UPDATE_LIST_FROM_NETWORK_FINISH);
        filter.addAction(Constants.ACTION_GET_FORCE_UPGREAD_RESULT);
        registerReceiver(receiver, filter);

        adDialog = new Dialog(MainActivity.this);
        adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        adDialog.setContentView(R.layout.ad_table_layout);
        adDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
//        showAdLay = (FrameLayout) adDialog.findViewById(R.id.show_ad_lay);
        adImg = (AdImageView) adDialog.findViewById(R.id.show_ad_img);
        adImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdInfo adInfo = (AdInfo) view.getTag();
                if (adInfo != null) {
                    AdUtils.doClick(MainActivity.this, adInfo, adImg.downX, adImg.downY,
                            adImg.upX, adImg.upY,null);
                    adDialog.dismiss();
                }
            }
        });
        adMark = (TextView) adDialog.findViewById(R.id.show_ad_mark);
        closeAdImg = (ImageView) adDialog.findViewById(R.id.close_ad_img);
        closeAdImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adDialog.dismiss();
            }
        });
        adDescrible = (TextView) adDialog.findViewById(R.id.show_ad_text);
        adIcon = (ImageView) adDialog.findViewById(R.id.show_ad_icon);


//		if (SoftwareManager.getInstance().getForceDownloadInfo() != null)
//		{
//			// 需要强制升级
//			// DownloadInfo downloadInfo = new DownloadInfo("升级更新爱玩商店",
//			// "com.donson.leplay.store",
//			// "http://yun.aiwan.hk/1452072158.apk",
//			// "http://yun.aiwan.hk/icon_1452072158.png", 4118662, "-1", 0);
//			DLog.e("lilijun", "initView()，需要强制更新！！！！");
//			DisplayUtil.showForceUpgreadDialog(SoftwareManager.getInstance()
//					.getForceDownloadInfo(), MainActivity.this);
//		} else
//		{
//        // 不需要强制升级
//        if (LeplayPreferences.getInstance(MainActivity.this).isFirstIn()
//                && !LoginUserInfoManager.getInstance().isHaveUserLogin()) {
//            // 第一次进入商店并且未登录时弹出
//            DisplayUtil.showFirstInDialog(MainActivity.this,
//                    ConstantManager.FIRST_REGISTER_COIN);
//        }
//		}
        LeplayPreferences.getInstance(MainActivity.this).setFirstIn(false);
        // // 获取手机分辨率
        //
        // DataCollectionConstant.screenResolution = ToolsUtil
        // .getScreenResolution(this);

        // 进入爱玩商店的数据采集
        DataCollectionManager.getInstance().addRecord(
                DataCollectionConstant.DATA_COLLECTION_IN_APP);
        // 上报数据(只要重新开始进入爱玩商店就进行一次数据上报)
        DataCollectionManager.getInstance().uploadData();

        // 设置科大讯飞全屏广告
//        setFullScreenAds();

//        String userAgent = LeplayPreferences.getInstance(MainActivity.this).getUserAgent();
//        if (TextUtils.isEmpty(userAgent)) {
//            userAgent = new WebView(MainActivity.this).getSettings().getUserAgentString();
//            LeplayPreferences.getInstance(MainActivity.this).setUserAgent(userAgent);
//            DLog.i("llj", "转义之前 userAgent----->>" + userAgent);
//            String escapeUserAgent = StringEscapeUtils.escapeJava(userAgent);
//            DLog.i("llj", "转义之之后 escapeUserAgent----->>" + escapeUserAgent);
//            DataCollectionConstant.userAgent = escapeUserAgent;
//        } else {
//            DataCollectionConstant.userAgent = userAgent;
//            DLog.i("llj", "userAgent不为空----->>" + userAgent);
//        }

//        // 设置科大讯飞全屏广告
//        requestFullScreenAds();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.main_tab_1:
                if (curTab != 0) {
                    changePage(fragments[0]);
                    mainTitleView.setVisibility(View.VISIBLE);
                    curTab = 0;
                }
                break;
            case R.id.main_tab_2:
                if (curTab != 1) {
                    changePage(fragments[1]);
                    mainTitleView.setVisibility(View.VISIBLE);
                    curTab = 1;
                    // 自己后台的数据采集
                    DataCollectionManager
                            .getInstance()
                            .addRecord(
                                    DataCollectionConstant
                                            .DATA_COLLECTION_CLICK_APPLICATION_TAB_VALUE);
                    // 添加友盟的事件点击数据采集
                    DataCollectionManager
                            .getInstance()
                            .addYouMengEventRecord(
                                    MainActivity.this,
                                    DataCollectionConstant
                                            .DATA_COLLECTION_CLICK_APPLICATION_TAB_VALUE,
                                    DataCollectionConstant.EVENT_ID_CLICK_APPLICATION_TAB,
                                    null);
                }
                break;
            case R.id.main_tab_3:
                if (curTab != 2) {
                    changePage(fragments[2]);
                    mainTitleView.setVisibility(View.VISIBLE);
                    curTab = 2;
                    // 自己后台的数据采集
                    DataCollectionManager
                            .getInstance()
                            .addRecord(
                                    DataCollectionConstant.DATA_COLLECTION_CLICK_GAME_TAB_VALUE);
                    // 添加友盟的事件点击数据采集
                    DataCollectionManager
                            .getInstance()
                            .addYouMengEventRecord(
                                    MainActivity.this,
                                    DataCollectionConstant.DATA_COLLECTION_CLICK_GAME_TAB_VALUE,
                                    DataCollectionConstant.EVENT_ID_CLICK_APPLICATION_TAB,
                                    null);
                }
                break;
        }
        radioGroup.check(checkedId);
    }

    private void changePage(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(fragments[curTab]);
        if (!fragment.isAdded()) {
            // 如果没有被添加过
            transaction.add(R.id.main_tab_fragment_lay, fragment);
        } else if (fragment.isHidden()) {
            transaction.show(fragment);
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this,
                    getResources().getString(R.string.again_sure_exit),
                    Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            // 保存友盟统计数据
            MobclickAgent.onKillProcess(this);
            // 有米积分墙退出
//			OffersManager.getInstance(MainActivity.this).onAppExit();

            finish();
            System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.ACTION_SOFTWARE_MANAGER_GET_UPDATE_LIST_FROM_NETWORK_FINISH
                    .equals(intent.getAction())) {
                if (SoftwareManager.getInstance().getForceDownloadInfo() == null) {
                    // 提升爱玩商店升级
                    DisplayUtil.showUpgradeDialog(context);
                }
        } else if (Constants.ACTION_GET_FORCE_UPGREAD_RESULT.equals(intent
                    .getAction())) {
                // 获取是否强制升级的数据返回
                if (SoftwareManager.getInstance().getForceDownloadInfo() != null) {
                    DLog.e("lilijun", "receiver，需要强制更新！！！！");
                    // 需要强制升级
                    DisplayUtil.showForceUpgreadDialog(SoftwareManager
                                    .getInstance().getForceDownloadInfo(),
                            MainActivity.this);
                }
            }
        }
    };


    private void requestTableScreenAds() {
//        74CCE27164A64DA85ECE7E8889D89D07
        //012A3BC314EE24D9EE5275B038EC3BBE
        AdUtils.requestAd(MainActivity.this,false, "57B762C1C64C663F552F3412F6B762BC",
                false, adImg.getWidth(), adImg.getHeight(), new AdUtils
                        .OnResponseListener() {
                    @Override
                    public void onSuccess(AdInfo adInfo) {
                        if (!TextUtils.isEmpty(adInfo.getIcon()) && !TextUtils.isEmpty
                                (adInfo.getTitle())) {
                            // 是图文广告
                            adDescrible.setVisibility(View.VISIBLE);
                            adIcon.setVisibility(View.VISIBLE);
                            adDescrible.setText(adInfo.getTitle() + "\n" + adInfo
                                    .getSubTile());
                            if (!TextUtils.isEmpty(adInfo.getIcon())) {
                                ImageLoaderManager.getInstance().displayImage
                                        (adInfo.getIcon(), adIcon, DisplayUtil
                                                .getListIconImageLoaderOptions());
                            }
                        } else {
                            // 是图片广告
                            adDescrible.setVisibility(View.GONE);
                            adIcon.setVisibility(View.GONE);
                        }

                        if (!TextUtils.isEmpty(adInfo.getImageUrl())) {
                            ImageLoaderManager.getInstance().displayImage(adInfo
                                    .getImageUrl(), adImg, DisplayUtil
                                    .getScreenShortImageLoaderOptions());
                        } else if (!TextUtils.isEmpty(adInfo.getRightIconUrl())) {
                            ImageLoaderManager.getInstance().displayImage(adInfo
                                    .getRightIconUrl(), adImg, DisplayUtil
                                    .getScreenShortImageLoaderOptions());
                        } else if (!TextUtils.isEmpty(adInfo.getIcon())) {
                            ImageLoaderManager.getInstance().displayImage(adInfo
                                    .getIcon(), adImg, DisplayUtil
                                    .getScreenShortImageLoaderOptions());
                        }
                        adImg.setTag(adInfo);
                        adMark.setText(adInfo.getAdSourceMark() + "|" + getResources()
                                .getString(R.string.ad));

                        if (adInfo.getImprUrls() != null) {
                            NetUtil.requestUrls(adInfo.getImprUrls(), new NetUtil.OnRequestMoreListener() {
                                @Override
                                public void onSuccess() {
                                    DLog.i("llj", "请求插屏广告曝光完成上报多个url成功!!!!");
                                }

                                @Override
                                public void onError() {
                                    DLog.e("llj", "请求插屏广告曝光完成上报多个url失败!!!!");
                                }
                            });
//                            int length = adInfo.getImprUrls().length;
//                            for (int i = 0; i < length; i++) {
//                                // 加载完成  上报
//                                NetUtil.requestUrl(adInfo.getImprUrls()[i], new
//                                        Callback() {
//                                            @Override
//                                            public void onResponse(Response response) throws
//                                                    IOException {
//                                                DLog.i("llj", "请求一个url成功!!!!");
//                                                String result = response.body().string();
//                                                DLog.i("llj", "result---->>>" + result);
//                                            }
//
//                                            @Override
//                                            public void onFailure(Request request,
//                                                                  IOException e) {
//                                                DLog.e("llj", "请求一个url失败!!!");
//                                            }
//                                        });
//                            }
                        }
//                        showAdLay.getLayoutParams().width = (int) (DataCollectionConstant
//                                .screenWidth * 0.8);
//                        showAdLay.getLayoutParams().height = showAdLay.getLayoutParams().width;
                        adDialog.show();
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });

//        AdUtils.requestUrl(MainActivity.this,false, "012A3BC314EE24D9EE5275B038EC3BBE",
//                false, adImg.getWidth(), adImg.getHeight(), new AdUtils
//                        .OnResponseListener() {
//                    @Override
//                    public void onSuccess(AdInfo adInfo) {
//                        if (!TextUtils.isEmpty(adInfo.getIcon()) && !TextUtils.isEmpty
//                                (adInfo.getTitle())) {
//                            // 是图文广告
//                            adDescrible.setVisibility(View.VISIBLE);
//                            adIcon.setVisibility(View.VISIBLE);
//                            adDescrible.setText(adInfo.getTitle() + "\n" + adInfo
//                                    .getSubTile());
//                            if (!TextUtils.isEmpty(adInfo.getIcon())) {
//                                ImageLoaderManager.getInstance().displayImage
//                                        (adInfo.getIcon(), adIcon, DisplayUtil
//                                                .getListIconImageLoaderOptions());
//                            }
//                        } else {
//                            // 是图片广告
//                            adDescrible.setVisibility(View.GONE);
//                            adIcon.setVisibility(View.GONE);
//                        }
//
//                        if (!TextUtils.isEmpty(adInfo.getImageUrl())) {
//                            ImageLoaderManager.getInstance().displayImage(adInfo
//                                    .getImageUrl(), adImg, DisplayUtil
//                                    .getScreenShortImageLoaderOptions());
//                        } else if (!TextUtils.isEmpty(adInfo.getRightIconUrl())) {
//                            ImageLoaderManager.getInstance().displayImage(adInfo
//                                    .getRightIconUrl(), adImg, DisplayUtil
//                                    .getScreenShortImageLoaderOptions());
//                        } else if (!TextUtils.isEmpty(adInfo.getIcon())) {
//                            ImageLoaderManager.getInstance().displayImage(adInfo
//                                    .getIcon(), adImg, DisplayUtil
//                                    .getScreenShortImageLoaderOptions());
//                        }
//                        adImg.setTag(adInfo);
//                        adMark.setText(adInfo.getAdSourceMark() + "|" + getResources()
//                                .getString(R.string.ad));
//
//                        if (adInfo.getImprUrls() != null) {
//                            int length = adInfo.getImprUrls().length;
//                            for (int i = 0; i < length; i++) {
//                                // 加载完成  上报
//                                NetUtil.requestUrl(adInfo.getImprUrls()[i], new
//                                        Callback() {
//                                            @Override
//                                            public void onResponse(Response response) throws
//                                                    IOException {
//                                                DLog.i("llj", "请求一个url成功!!!!");
//                                                String result = response.body().string();
//                                                DLog.i("llj", "result---->>>" + result);
//                                            }
//
//                                            @Override
//                                            public void onFailure(Request request,
//                                                                  IOException e) {
//                                                DLog.e("llj", "请求一个url失败!!!");
//                                            }
//                                        });
//                            }
//                        }
////                        showAdLay.getLayoutParams().width = (int) (DataCollectionConstant
////                                .screenWidth * 0.8);
////                        showAdLay.getLayoutParams().height = showAdLay.getLayoutParams().width;
//                        adDialog.show();
//                    }
//
//                    @Override
//                    public void onError(String msg) {
//
//                    }
//                });
    }


//    /**
//     * 请求全屏广告数据
//     */
//    private void requestFullScreenAds() {
//        AdUtils.requestAd(MainActivity.this, "EC515143106BACCD815BB094EA114004", false,
//                DataCollectionConstant.screenWidth, DataCollectionConstant.screenHeight, new
//                        AdUtils.OnResponseListener() {
//                            @Override
//                            public void onSuccess(AdInfo adInfo) {
//                                FullScreenAdActivity.startAcitivty(MainActivity.this, adInfo);
//                            }
//
//                            @Override
//                            public void onError(String msg) {
//
//                            }
//                        });
//    }


    public static void startActivity(Context context){
        // 跳转到主页
        Intent intent = new Intent(context,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }


//    /**
//     * 设置科大讯飞插屏广告
//     */
//    private void setTableScreenAds() {
//        //创建插屏广告： adId：开发者在广告平台(http://www.voiceads.cn/)申请的广告位 ID
//        interstitialAd = IFLYInterstitialAd.createInterstitialAd(MainActivity.this,
//                "74CCE27164A64DA85ECE7E8889D89D07");
//        //设置广告尺寸
//        interstitialAd.setAdSize(IFLYAdSize.INTERSTITIAL);
//        //点击手机后退键， 是否销毁广告： "true":销毁， "false":不销毁，默认销毁
//        interstitialAd.setParameter(AdKeys.BACK_KEY_ENABLE, "true");
//        //添加监听器，请求广告
//        interstitialAd.loadAd(tableScreenAdsListener);
//    }
//
//    /**
//     * 插屏广告监听器
//     */
//    IFLYAdListener tableScreenAdsListener = new IFLYAdListener() {
//        @Override
//        public void onAdReceive() {
//            //成功接收广告，调用广告展示接口。 注意： 该接口在回调中才能生效
//            interstitialAd.showAd();
//            DLog.i("lilijun", "接收到科大讯飞插屏广告信息成功！！！");
//        }
//
//        @Override
//        public void onAdFailed(AdError error) {
//            // 广告请求失败
//            // error.getErrorCode():错误码， error.getErrorDescription()：错误描述
//            DLog.e("lilijun", "获取插屏广告失败！错误码----->" + error.getErrorCode() + "--错误描述---->>>" +
//                    error.getErrorDescription());
//        }
//
//        @Override
//        public void onAdClick() {
//
//        }
//
//        @Override
//        public void onAdClose() {
//            // 关闭广告
//        }
//
//        @Override
//        public void onAdExposure() {
//            // 广告曝光
//        }
//
//        @Override
//        public void onCancel() {
//            // 下载类广告， 下载提示框取消
//        }
//
//        @Override
//        public void onConfirm() {
//            // 下载类广告， 下载提示框确认
//        }
//    };
//
//
//    /**
//     * 设置科大讯飞全屏广告
//     */
//    private void setFullScreenAds() {
//        //创建全屏广告： adId：开发者在广告平台(http://www.voiceads.cn/)申请的广告位 ID
//        fullScreenAd = IFLYFullScreenAd.createFullScreenAd(MainActivity.this,
//                "EC515143106BACCD815BB094EA114004");
//        //设置广告尺寸
//        fullScreenAd.setAdSize(IFLYAdSize.FULLSCREEN);
//        //设置全屏广告展示时间，单位为 ms。默认广告展示 5000ms 后消失
//        fullScreenAd.setParameter(AdKeys.SHOW_TIME_FULLSCREEN, "6000");
//        //添加监听器，请求广告
//        fullScreenAd.loadAd(fullScreenAdListener);
//    }
//
//    private IFLYAdListener fullScreenAdListener = new IFLYAdListener() {
//        @Override
//        public void onAdReceive() {
//            //成功接收广告，调用广告展示接口。 注意： 该接口在回调中才能生效
//            fullScreenAd.showAd();
//            DLog.i("lilijun", "接收到科大讯飞全屏广告信息成功！！！");
//        }
//
//        @Override
//        public void onAdFailed(AdError error) {
//            // 广告请求失败
//            // error.getErrorCode():错误码， error.getErrorDescription()：错误描述
//
//            DLog.e("lilijun", "获取全屏广告失败！错误码----->" + error.getErrorCode() + "--错误描述---->>>" +
//                    error.getErrorDescription());
//        }
//
//        @Override
//        public void onAdClick() {
//            // 广告被点击
//        }
//
//        @Override
//        public void onAdClose() {
//            // 关闭广告
//        }
//
//        @Override
//        public void onAdExposure() {
//            // 广告曝光
//        }
//
//        @Override
//        public void onCancel() {
//            // 下载类广告， 下载提示框取消
//        }
//
//        @Override
//        public void onConfirm() {
//            // 下载类广告， 下载提示框确认
//        }
//    };
}
