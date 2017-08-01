package com.xd.leplay.store.gui.recommend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ConstantManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.adapter.SoftListAdapter;
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.main.BaseTabChildFragment;
import com.xd.leplay.store.gui.treasure.RedPacketPasswordInputActivity;
import com.xd.leplay.store.model.AdInfo;
import com.xd.leplay.store.model.AdUtils;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.model.proto.App;
import com.xd.leplay.store.model.proto.App.AdElement;
import com.xd.leplay.store.model.proto.App.ReqAdElements;
import com.xd.leplay.store.model.proto.App.ReqRecommAppList;
import com.xd.leplay.store.model.proto.App.RspAdElements;
import com.xd.leplay.store.model.proto.App.RspRecommAppList;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.NetUtil;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.AdImageView;
import com.xd.leplay.store.view.BannerView;
import com.xd.leplay.store.view.CenterDialog;
import com.xd.leplay.store.view.LoadMoreScrollListener;
import com.xd.leplay.store.view.LoadMoreView;
import com.xd.leplay.store.view.MarketListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐Tab Fragment
 *
 * @author lilijun
 */
public class RecommendFragment extends BaseTabChildFragment implements OnClickListener {
    private static final String TAG = "RecommendFragment";

    private MarketListView listView = null;

    private SoftListAdapter adapter = null;

    private ArrayList<ListAppInfo> appInfos = null;

    /**
     * banner 广告对象
     */
    private List<AdElement> adElements = null;

    /**
     * 列表一次加载的长度
     */
    private static final int LOAD_DATA_SIZE = 6;

    /**
     * 标识是否还有下一页列表数据
     */
    private boolean isHaveNextPageData = true;

    private View headerView = null;

    private BannerView bannerView = null;

//	/** 新品首发、玩赚金币 */
//	private ImageView headerLeftLay, headerRightLay, headerLeftLay2,
//			headerRightLay2;

    private LoadMoreView loadMoreView = null;

//    /**
//     * 跑马灯
//     */
//    private CustomMarqueeTextView marqueeText;

    /**
     * 是否跳转到微信
     */
    private boolean isToWeiXin = false;
    /**
     * 获取列表的类型的标识值
     * 1=首页推荐,2=首页新品应用首发,3=首页新品游戏首发,4=首页玩赚金币(应用),5=首页玩赚金币(游戏)，6=应用精品，
     * 7=应用排行,8=应用必备,9=游戏精品,10=游戏排行
     */
    private final int LIST_TYPE = 1;

    /**
     * 获取广告元素(Banner)的类型标识值
     * <p>
     * 1=闪屏，2=首页轮播,3=应用轮播，4=游戏轮播，5=应用广告图片,6=游戏广告图片
     */
    private final int AD_ELEMENTS_TYPE = 2;

    /**
     * 请求列表数据的TAG
     */
    private final String REQUEST_LIST_TAG = "ReqRecommAppList";

    /**
     * 响应列表数据的TAG
     */
    private final String RSPONSE_LIST_TAG = "RspRecommAppList";

    /**
     * 请求广告数据的TAG
     */
    private final String REQUEST_ADELMENT_TAG = "ReqAdElements";

    /**
     * 推荐页列表和广告Banner的etag标识值
     */
    private String etagMark = "recommendListAndTopBanner";


//    /**
//     * 科大讯飞banner 广告
//     */
//    private IFLYBannerAd bannerAd;

//    private FrameLayout bannerAdsLay;

    private FrameLayout leftAdLay;

    private AdImageView leftAd;

    private TextView adMark;

    private int adWidth, adHeight;

    // 信息流广告位展示高度
    private int infoAdHeight;
//    private LinearLayout adsLay;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            isToWeiXin = savedInstanceState.getBoolean("isToWeiXin", false);
            DLog.i("lilijun",
                    "onCreate(),有跳转保存的savedInstanceState,isToWeiXin----->"
                            + isToWeiXin);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isToWeiXin) {
            // 之前从此界面曾经跳转到过微信
            // 再次去获取财富信息
            NetUtil.getLoginUserGiftsAndTreasureData(getActivity());
            isToWeiXin = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isToWeiXin", isToWeiXin);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void initView(FrameLayout view) {

        action = DataCollectionConstant.DATA_COLLECTION_RECOMMENT_VALUE;
        DataCollectionManager.getInstance().addRecord(action);

        infoAdHeight = DisplayUtil.dip2px(getActivity(),getResources().getDimension(R.dimen.dp100));

        listView = new MarketListView(getActivity());
        listView.setCacheColorHint(Color.parseColor("#00000000"));
        setCenterView(listView);
        // 红包悬浮窗是否显示 1：显示
        if (ConstantManager.getInstance().getConstantInfo()
                .getIsExistRedPacket() == 1) {
            setRedPacketFloat();
        }

        appInfos = new ArrayList<ListAppInfo>();
        adElements = new ArrayList<App.AdElement>();
        adapter = new SoftListAdapter(getActivity(), listView, appInfos, action);
        adapter.setOnRequestAdListener(new SoftListAdapter.OnRequestAdListener() {
            @Override
            public void onRequestAd(int position) {
                // 请求数据流广告
                requestAdInfo(position);
            }
        });
        listView.setOnScrollListener(new LoadMoreScrollListener(ImageLoader
                .getInstance(), true, true, new LoadMoreScrollListener.setOnScrollToEndListener() {
            @Override
            public void loadMoreWhenScrollToEnd() {
                // if (isHaveNextPageData)
                // {
                loadMoreData();
                // } else
                // {
                // listView.setOnScrollListener(null);
                // listView.removeFooterView(loadMoreView);
                // listView.showEndView();
                // }
            }
        }));

        headerView = View.inflate(getActivity(),
                R.layout.recommend_header_view, null);
        loadMoreView = new LoadMoreView(getActivity());
        loadMoreView.setNetErrorViewOnClickLinstener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreView.setLoadingVisible(true);
                loadMoreData();
            }
        });
        listView.addHeaderView(headerView);
        listView.addFooterView(loadMoreView);

        bannerView = (BannerView) headerView
                .findViewById(R.id.recommed_banner_view);
        bannerView
                .setAction(DataCollectionManager
                        .getAction(
                                action,
                                DataCollectionConstant.DATA_COLLECTION_RECOMMENT_TOP_BANNER_VALUE));

//        bannerAdsLay = (FrameLayout) headerView.findViewById(R.id.recomend_ads_layout);
//        // 设置科大讯飞banner广告
//        setAds();

//        adsLay = (LinearLayout) headerView.findViewById(R.id.recommend_ads_lay);
//        adsLay.setVisibility(View.GONE);
        leftAdLay = (FrameLayout) headerView.findViewById(R.id.recommend_left_banner_ad_lay);
        leftAd = (AdImageView) headerView.findViewById(R.id.recommend_left_banner_ad_img);
        leftAd.setOnClickListener(this);
        adMark = (TextView) headerView.findViewById(R.id.recommend_left_banner_ad_mark);
//        rightAd = (ImageView) headerView.findViewById(R.id.recommend_right_banner_ad_img);
//        rightAd.setOnClickListener(this);


//		headerLeftLay = (ImageView) headerView
//				.findViewById(R.id.recommend_header_left_lay);
//		headerRightLay = (ImageView) headerView
//				.findViewById(R.id.recommend_header_right_lay);
//		headerLeftLay2 = (ImageView) headerView
//				.findViewById(R.id.recommend_header_left_lay2);
//		headerRightLay2 = (ImageView) headerView
//				.findViewById(R.id.recommend_header_right_lay2);N

//        marqueeText = (CustomMarqueeTextView) headerView
//                .findViewById(R.id.custom_marquee_textview);
//        marqueeText.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GainsRankActivity.startGainsRankActivity(getActivity(), action);
//            }
//        });

        doLoadData(
                Constants.APP_API_URL,
                new String[]
                        {REQUEST_LIST_TAG, REQUEST_ADELMENT_TAG},
                new ByteString[]
                        {
                                getReqRecommAppList(
                                        ((appInfos.size() / LOAD_DATA_SIZE) + 1),
                                        LOAD_DATA_SIZE),
                                getReqAdElements(AD_ELEMENTS_TYPE)}, etagMark);

//        IntentFilter filter = new IntentFilter(
//                Constants.ACTION_GET_SYSTEM_MSG_FINISH);
//        getActivity().registerReceiver(new RecommentRecevier(), filter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adWidth <= 0 || adHeight <= 0) {
            leftAd.post(new Runnable() {
                @Override
                public void run() {
                    requestBannerAd();
                }
            });
        }
    }

    /**
     * 设置显示红包悬浮窗
     */
    private void setRedPacketFloat() {
        ImageView image = new ImageView(getActivity());
        image.setImageResource(R.drawable.red_packet_float);// 设置图片
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);// 与父容器的左侧对齐
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);// 与父容器的上侧对齐
        lp.rightMargin = 40;
        lp.bottomMargin = 40;
        image.setLayoutParams(lp);// 设置布局参数
        image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 点击红包浮窗后台数据采集
                if (LoginUserInfoManager.getInstance().isHaveUserLogin()) {
                    if (LoginUserInfoManager.getInstance().getLoginedUserInfo()
                            .getOpenId() != null
                            && !"".equals(LoginUserInfoManager.getInstance()
                            .getLoginedUserInfo().getOpenId().trim())) {
                        String clickRedPacketFloat = DataCollectionManager
                                .getAction(
                                        action,
                                        DataCollectionConstant
                                                .DATA_COLLECTION_CLICK_RED_PACKET_FLOAT_VALUE);
                        DataCollectionManager.getInstance().addRecord(
                                clickRedPacketFloat);

                        DataCollectionManager
                                .getInstance()
                                .addYouMengEventRecord(
                                        getActivity(),
                                        action,
                                        DataCollectionConstant.EVENT_ID_CLICK_RED_PACKET_FLOAT,
                                        null);
                        RedPacketPasswordInputActivity
                                .startRedPacketPasswordInputActivity(
                                        getActivity(), action);
                    } else
                        showBindWexinDialog();
                } else {
                    LoginActivity.startLoginActivity(getActivity(), action);
                }
            }
        });
        centerViewLayout.addView(image);
    }

    @Override
    protected void loadDataSuccess(RspPacket rspPacket) {
        super.loadDataSuccess(rspPacket);
        List<String> actions = rspPacket.getActionList();
        for (String action : actions) {
            if (RSPONSE_LIST_TAG.equals(action)) {
                // 列表数据返回
                parseListResult(rspPacket);
                DLog.i("lilijun", "获取到推荐列表数据:appInfos.size()------>>>"
                        + appInfos.size());
                if (!isHaveNextPageData) {
                    listView.setOnScrollListener(null);
                    listView.removeFooterView(loadMoreView);
                    listView.showEndView();
                }
                if (!appInfos.isEmpty()) {
                    if (listView.getAdapter() == null) {
                        listView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }

                    // if (appInfos.size() <= LOAD_DATA_SIZE)
                    // {
                    // // 表明是第一次加载列表数据
                    // listView.setAdapter(adapter);
                    // } else
                    // {
                    // // 表明是加载下一页返回了数据
                    // adapter.notifyDataSetChanged();
                    // }
                }
            } else if ("RspAdElements".equals(action)) {
                // banner数据返回
//                parseAdElementsResult(rspPacket);

                // 组临时写死数据
                // 百度浏览器
                App.AdElement.Builder baiduiBuilder = App.AdElement.newBuilder();
                // 设置成App详情类型
                baiduiBuilder.setElemType(1);
                baiduiBuilder.setElemId(1442312968);
                baiduiBuilder.setPosType(2);
                baiduiBuilder.setIconUrl("-1");
                baiduiBuilder.setAdsPicUrlLand(R.drawable.banner_baidu + "");

                // 全民奇迹
                App.AdElement.Builder quanminBuilder = App.AdElement.newBuilder();
                // 设置成App详情类型
                quanminBuilder.setElemType(1);
                quanminBuilder.setElemId(-11158374);
                quanminBuilder.setPosType(2);
                quanminBuilder.setIconUrl("-1");
                quanminBuilder.setAdsPicUrlLand(R.drawable.banner_quanmin + "");

                // 天天爱消除
                App.AdElement.Builder xiaochuBuilder = App.AdElement.newBuilder();
                // 设置成App详情类型
                xiaochuBuilder.setElemType(1);
                xiaochuBuilder.setElemId(1442459031);
                xiaochuBuilder.setPosType(2);
                xiaochuBuilder.setIconUrl("-1");
                xiaochuBuilder.setAdsPicUrlLand(R.drawable.banner_xiaochu + "");


                RspAdElements.Builder builder = RspAdElements.newBuilder();
                builder.addAdElements(quanminBuilder.build());
                builder.addAdElements(baiduiBuilder.build());
                builder.addAdElements(xiaochuBuilder.build());
                builder.setRescode(0);
                builder.setResmsg("");
                // 获取
                adElements = builder.build().getAdElementsList();

//                adElements.add(adElement.build());

                bannerView.initData(adElements);
                bannerView.initView(getActivity());
                bannerView.setScrollTime(300);
                // 将自动刷新开关打开
                bannerView.setCancleAutoRefresh(false);
                // 开始自动刷新
                bannerView.startAutoRefresh();

//                setLeftAds();
            }
        }

//        // 设置跑马灯信息
//        setSystemMsg();
    }

    @Override
    protected void loadDataFailed(RspPacket rspPacket) {
        super.loadDataFailed(rspPacket);
        if (!appInfos.isEmpty()) {
            loadMoreView.setNetErrorVisible(true);
        } else {
            loadingView.setVisibilyView(false);
            centerViewLayout.setVisibility(View.GONE);
            errorViewLayout.setVisibility(View.VISIBLE);
            errorViewLayout.showLoadFailedLay();
        }
    }

    @Override
    protected void netError(String[] actions) {
        super.netError(actions);
        if (!appInfos.isEmpty()) {
            loadMoreView.setNetErrorVisible(true);
        } else {
            loadingView.setVisibilyView(false);
            centerViewLayout.setVisibility(View.GONE);
            errorViewLayout.setVisibility(View.VISIBLE);
            errorViewLayout.showLoadFailedLay();
        }
    }

    @Override
    protected void tryAgain() {
        super.tryAgain();
        doLoadData(
                Constants.APP_API_URL,
                new String[]
                        {REQUEST_LIST_TAG, REQUEST_ADELMENT_TAG},
                new ByteString[]
                        {
                                getReqRecommAppList(
                                        ((appInfos.size() / LOAD_DATA_SIZE) + 1),
                                        LOAD_DATA_SIZE),
                                getReqAdElements(AD_ELEMENTS_TYPE)}, etagMark);
    }

    /**
     * 加载列表下一页数据
     */
    private void loadMoreData() {
        doLoadData(
                Constants.APP_API_URL,
                new String[]
                        {REQUEST_LIST_TAG,},
                new ByteString[]
                        {getReqRecommAppList(((appInfos.size() / LOAD_DATA_SIZE) + 1),
                                LOAD_DATA_SIZE)}, etagMark
                        + (appInfos.size() / LOAD_DATA_SIZE) + 1);
    }

    /**
     * 获取ReqRecommAppList的ByteString
     * <p>
     * 列表数据类型
     *
     * @param index
     * @param size
     * @return
     */
    private ByteString getReqRecommAppList(int index, int size) {
        ReqRecommAppList.Builder builder = ReqRecommAppList.newBuilder();
        builder.setRecommType(LIST_TYPE);
        builder.setPageIndex(index);
        builder.setPageSize(size);
        ReqRecommAppList reqRecommAppList = builder.build();
        ByteString byteString = reqRecommAppList.toByteString();
        return byteString;
    }

    /**
     * 解析列表返回结果
     *
     * @param rspPacket
     */
    private void parseListResult(RspPacket rspPacket) {
        try {
            RspRecommAppList rspRecommAppList = RspRecommAppList
                    .parseFrom(rspPacket.getParams(0));
            List<App.AppInfo> infos = rspRecommAppList.getAppInfosList();
            if (infos.size() == 0 || infos.isEmpty()
                    || infos.size() < LOAD_DATA_SIZE) {
                // 没有下一页数据了
                isHaveNextPageData = false;
            }

            int time = 0;
            for (App.AppInfo info : infos) {
                time ++;
                appInfos.add(ToolsUtil.getListAppInfo(info));
                if(time == 1){
                    // 在第一条数据后面添加一个广告数据(第二条数据)
                    ListAppInfo appInfo = new ListAppInfo();
                    // 设置为广告类型
                    appInfo.setAd(true);
                    // 设置视图未被填充
                    appInfo.setFullAd(false);
                    appInfos.add(appInfo);
                }
            }

        } catch (Exception e) {
            DLog.e(TAG, "parseListResult()#Excepton:", e);
        }
    }

    /**
     * 获取ReqAdElemnts的ByteString
     *
     * @param type
     * @return
     */
    private ByteString getReqAdElements(int type) {
        ReqAdElements.Builder adElementsBuilder = ReqAdElements.newBuilder();
        adElementsBuilder.setPosType(type);
        return adElementsBuilder.build().toByteString();
    }

    /**
     * 解析Banner的返回结果
     *
     * @param rspPacket
     */
    private void parseAdElementsResult(RspPacket rspPacket) {
        try {
            RspAdElements rspAdElements = RspAdElements.parseFrom(rspPacket
                    .getParams(1));
            adElements = rspAdElements.getAdElementsList();
        } catch (Exception e) {
            DLog.e(TAG, "parseAdElementsResult()#Excepton:", e);
        }
    }

//    /**
//     * 本类广播接收类
//     *
//     * @author dell
//     */
//    private class RecommentRecevier extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(
//                    Constants.ACTION_GET_SYSTEM_MSG_FINISH)) {
//                // 接收到 从服务器获取到了系统广播和用户消息改变的广播
//                setSystemMsg();
//            }
//        }
//
//    }

//    /**
//     * 设置跑马灯消息
//     */
//    private void setSystemMsg() {
//        String msg = LeplayPreferences.getInstance(getActivity())
//                .getSystemMsg();
//        if (!"".equals(msg)) {
//            marqueeText.setVisibility(View.VISIBLE);
//            marqueeText.setText(msg);
//            marqueeText.requestFocus();
//            marqueeText.setFocusable(true);
//        } else {
//            marqueeText.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onDestroy() {
        if (adapter != null) {
            adapter.unRegisterListener();
        }
        super.onDestroy();
    }

    /**
     * 显示绑定微信的Dialog
     */
    private void showBindWexinDialog() {
        // 绑定微信Dialog
        final CenterDialog bindingWeixinDialog = new CenterDialog(getActivity());
        bindingWeixinDialog.show();
        bindingWeixinDialog.setTitleVisible(false);
        bindingWeixinDialog.setCenterView(R.layout.binding_weixin_dialog);
        Button bingdingWeixinBtn = (Button) bindingWeixinDialog
                .findViewById(R.id.my_purse_binding_weixin_btn);
        TextView bindWxNotice = (TextView) bindingWeixinDialog
                .findViewById(R.id.bindWxNotice);
        bindWxNotice.setText(getActivity().getResources().getString(
                R.string.get_red_packet_notice));
        bingdingWeixinBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 前往微信
                bindingWeixinDialog.dismiss();
                // 打开微信
                isToWeiXin = ToolsUtil.openSoftware(getActivity(),
                        "com.tencent.mm");
                if (!isToWeiXin) {
                    Toast.makeText(
                            getActivity(),
                            getResources().getString(
                                    R.string.no_installed_weixin),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


//    private void setAds() {
//        bannerAd = IFLYBannerAd.createBannerAd(getActivity(), "E98C0619C442DDB0479B1D5E8E3D8C0B");
//        DLog.i("lilijun","bannerAd == null---->>>"+(bannerAd == null));
//        bannerAd.setAdSize(IFLYAdSize.BANNER); //设置广告尺寸
//        bannerAd.setParameter(AdKeys.DEBUG_MODE, "true");//设置为调试模式
//        bannerAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");//下载广告前，弹窗提示
//        //请求广告，添加监听器
//        bannerAd.loadAd(mAdListener);
//        FrameLayout adsLay = (FrameLayout) headerView.findViewById(R.id.recomend_ads_layout);
//        adsLay.removeAllViews();
//        adsLay.addView(bannerAd);
//    }

//    private IFLYAdListener mAdListener = new IFLYAdListener() {
//        @Override
//        public void onAdReceive() {
//            //成功接收广告，调用广告展示接口。 注意： 该接口在回调中才能生效
//            DLog.i("lilijun", "banner 广告成功！！！");
//            bannerAd.showAd();
//        }
//
//        @
//                Override
//        public void onAdFailed(AdError error) {
//            // 广告请求失败
//            // error.getErrorCode():错误码， error.getErrorDescription()：错误描述
//            DLog.e("lilijun", "banner 广告失败！！！\n" + error.getErrorCode() + "----->" + error
//                    .getErrorDescription());
//        }
//
//        @Override
//        public void onAdClick() {
//            // 广告被点击
//        }
//
//        @Override
//        public void onAdClose() {
//            // 广告被关闭
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recommend_left_banner_ad_img:
                // 左边banner广告点击事件
                DLog.i("llj", "点击事件 dx------>>>" + leftAd.downX);
                DLog.i("llj", "点击事件 dy------>>>" + leftAd.downY);
                DLog.i("llj", "点击事件 upx------>>>" + leftAd.upX);
                DLog.i("llj", "点击事件 upy------>>>" + leftAd.upY);

                final AdInfo adInfo = (AdInfo) view.getTag();
                if (adInfo != null && adInfo.getClickUrls() != null) {
                    AdUtils.doClick(getActivity(), adInfo, leftAd.downX, leftAd.downY,
                            leftAd.upX, leftAd.upY, null);
                }
                break;
//            case R.id.recommend_right_banner_ad_img:
//                // 右边banner广告点击事件
//                break;
        }
    }


    /**
     * 请求科大讯飞 banner广告
     */
    private void requestBannerAd() {
        if (adWidth <= 0 || adHeight <= 0) {
            adWidth = leftAd.getMeasuredWidth();
            adHeight = leftAd.getMeasuredHeight();
        }
//        E98C0619C442DDB0479B1D5E8E3D8C0B
        // 45F8203725A8967FD66117CBACD43F10
        AdUtils.requestAd(getActivity(),false, "99148236196BB9EB0DFCE13F211F6AB3",
                false, adWidth, adHeight, new AdUtils.OnResponseListener() {
                    @Override
                    public void onSuccess(AdInfo adInfo) {
                        DLog.i("llj", "图片显示路径---->>>" + adInfo.getImageUrl());
                        leftAdLay.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(adInfo.getImageUrl())) {
                            ImageLoaderManager.getInstance().displayImage(adInfo
                                    .getImageUrl(), leftAd, DisplayUtil
                                    .getScreenShortImageLoaderOptions());
                        } else if (!TextUtils.isEmpty(adInfo.getRightIconUrl())) {
                            ImageLoaderManager.getInstance().displayImage(adInfo
                                    .getRightIconUrl(), leftAd, DisplayUtil
                                    .getScreenShortImageLoaderOptions());
                        } else if (!TextUtils.isEmpty(adInfo.getIcon())) {
                            ImageLoaderManager.getInstance().displayImage(adInfo
                                    .getIcon(), leftAd, DisplayUtil
                                    .getScreenShortImageLoaderOptions());
                        }
                        leftAd.setTag(adInfo);
                        adMark.setText(adInfo.getAdSourceMark() + "|" + getResources()
                                .getString(R.string.ad));

                        if (adInfo.getImprUrls() != null) {
                            NetUtil.requestUrls(adInfo.getImprUrls(), new NetUtil.OnRequestMoreListener() {
                                @Override
                                public void onSuccess() {
                                    DLog.i("llj", "请求banner曝光完成上报多个url成功!!!!");
                                }

                                @Override
                                public void onError() {
                                    DLog.e("llj", "请求banner曝光完成上报多个url失败!!!!");
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
                    }

                    @Override
                    public void onError(String msg) {
                        leftAdLay.setVisibility(View.GONE);
                    }
                });

//        AdUtils.requestUrl(getActivity(), false, "45F8203725A8967FD66117CBACD43F10",
//                false, adWidth, adHeight, new AdUtils.OnResponseListener() {
//                    @Override
//                    public void onSuccess(AdInfo adInfo) {
//                        DLog.i("llj", "图片显示路径---->>>" + adInfo.getImageUrl());
//                        if (!TextUtils.isEmpty(adInfo.getImageUrl())) {
//                            ImageLoaderManager.getInstance().displayImage(adInfo
//                                    .getImageUrl(), leftAd, DisplayUtil
//                                    .getScreenShortImageLoaderOptions());
//                        } else if (!TextUtils.isEmpty(adInfo.getRightIconUrl())) {
//                            ImageLoaderManager.getInstance().displayImage(adInfo
//                                    .getRightIconUrl(), leftAd, DisplayUtil
//                                    .getScreenShortImageLoaderOptions());
//                        } else if (!TextUtils.isEmpty(adInfo.getIcon())) {
//                            ImageLoaderManager.getInstance().displayImage(adInfo
//                                    .getIcon(), leftAd, DisplayUtil
//                                    .getScreenShortImageLoaderOptions());
//                        }
//                        leftAd.setTag(adInfo);
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
//                                                DLog.i(TAG, "请求一个url成功!!!!");
//                                                String result = response.body().string();
//                                                DLog.i("llj", "result---->>>" + result);
//                                            }
//
//                                            @Override
//                                            public void onFailure(Request request,
//                                                                  IOException e) {
//                                                DLog.e(TAG, "请求一个url失败!!!");
//                                            }
//                                        });
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(String msg) {
//
//                    }
//                });
    }


    /**
     * 请求信息流广告
     * @param position 需要填充的广告在列表中的position
     */
    private void requestAdInfo(final int position){
//        AdUtils.requestUrl(getActivity(), false, "5D86B056669F6C6F86F3648FB7AC462A",
//                false, DataCollectionConstant.screenWidth, infoAdHeight, new AdUtils.OnResponseListener() {
//                    @Override
//                    public void onSuccess(AdInfo adInfo) {
//                        DLog.i("llj", "图片显示路径---->>>" + adInfo.getImageUrl());
//                        ListAppInfo appInfo = appInfos.get(position);
//                        if(appInfo == null){
//                            return;
//                        }
//                        appInfo.setAd(true);
//                        // 设置视图被填充了
//                        appInfo.setFullAd(true);
//                        appInfo.setAdInfo(adInfo);
////                        // 添加信息流广告数据到请求时的列表最后一个数据位置
////                        appInfos.add(position,appInfo);
//                        adapter.notifyDataSetChanged();
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
//                                                DLog.i(TAG, "请求一个url成功!!!!");
//                                                String result = response.body().string();
//                                                DLog.i("llj", "result---->>>" + result);
//                                            }
//
//                                            @Override
//                                            public void onFailure(Request request,
//                                                                  IOException e) {
//                                                DLog.e(TAG, "请求一个url失败!!!");
//                                            }
//                                        });
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(String msg) {
//
//                    }
//                });

        AdUtils.requestAd(getActivity(), false, "5D86B056669F6C6F86F3648FB7AC462A",
                false, DataCollectionConstant.screenWidth, infoAdHeight, new AdUtils.OnResponseListener() {
                    @Override
                    public void onSuccess(AdInfo adInfo) {
                        DLog.i("llj", "图片显示路径---->>>" + adInfo.getImageUrl());
                        ListAppInfo appInfo = appInfos.get(position);
                        if(appInfo == null){
                            return;
                        }
                        appInfo.setAd(true);
                        // 设置视图被填充了
                        appInfo.setFullAd(true);
                        appInfo.setAdInfo(adInfo);
//                        // 添加信息流广告数据到请求时的列表最后一个数据位置
//                        appInfos.add(position,appInfo);
                        adapter.notifyDataSetChanged();

                        if (adInfo.getImprUrls() != null) {
                            NetUtil.requestUrls(adInfo.getImprUrls(), new NetUtil.OnRequestMoreListener() {
                                @Override
                                public void onSuccess() {
                                    DLog.i("llj", "请求信息流曝光完成上报多个url成功!!!!");
                                }

                                @Override
                                public void onError() {
                                    DLog.e("llj", "请求信息流曝光完成上报多个url失败!!!!");
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
//                                                DLog.i(TAG, "请求一个url成功!!!!");
//                                                String result = response.body().string();
//                                                DLog.i("llj", "result---->>>" + result);
//                                            }
//
//                                            @Override
//                                            public void onFailure(Request request,
//                                                                  IOException e) {
//                                                DLog.e(TAG, "请求一个url失败!!!");
//                                            }
//                                        });
//                            }
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        appInfos.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                DLog.e("llj", "监听到一分钟时间变化！！！");
                requestBannerAd();
            }
        }
    };

}
