package com.xd.leplay.store.control;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.download.DownloadListener;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.db.SoftwareManagerDB;
import com.xd.leplay.store.gui.details.view.DetailAppStateConstants;
import com.xd.leplay.store.gui.manager.update.UpdateAppActivity;
import com.xd.leplay.store.gui.search.SearchHistoryManager;
import com.xd.leplay.store.model.AdInfo;
import com.xd.leplay.store.model.AdUtils;
import com.xd.leplay.store.model.ConstantInfo;
import com.xd.leplay.store.model.InstalledAppInfo;
import com.xd.leplay.store.model.UpdateAppInfo;
import com.xd.leplay.store.model.proto.App.LocalAppVer;
import com.xd.leplay.store.model.proto.App.PackInfo;
import com.xd.leplay.store.model.proto.App.ReqAppsUpdate;
import com.xd.leplay.store.model.proto.App.RspAppsUpdate;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.LevelInfo;
import com.xd.leplay.store.util.NetUtil;
import com.xd.leplay.store.util.NetUtil.OnNetResponseLinstener;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.download.AppStateConstants;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

/**
 * 软件管理类
 *
 * @author lilijun
 */
public class SoftwareManager {
    private static final String TAG = "SoftwareManager";

    private static final String DOWNLOAD_INFO = "downloadInfo";

    /**
     * 软件状态，未安装
     */
    public static final int STATE_NO_INSTALLED = 0;
    /**
     * 软件状态，已安装 且 没有更新
     */
    public static final int STATE_INSTALLED = 1;
    /**
     * 软件状态，已安装 且 有更新
     */
    public static final int STATE_UPDATE = 2;

    private static SoftwareManager instance = null;

    private Context mContext = null;

    /**
     * 本地安装的所有应用信息列表
     */
    private Hashtable<String, InstalledAppInfo> installedAppInfos = new Hashtable<String,
            InstalledAppInfo>();

    /**
     * 本地应用所有有更新的应用信息列表
     */
    private Hashtable<String, UpdateAppInfo> updateAppInfos = new Hashtable<String,
            UpdateAppInfo>();

    /**
     * 更新应用信息数据库管理类对象
     */
    private SoftwareManagerDB db = null;

    /**
     * 本地所有的已安装应用总个数(包括系统应用和第三方应用)
     */
    private int allAppCount = 0;

    /**
     * 获取已安装应用信息时 当前已经获取到的应用信息个数
     */
    private int alreadyGetAppCount = 0;

    /**
     * 获取到的本地已安装应用的LocalAppVer集合对象
     */
    private List<LocalAppVer> localAppVers = new ArrayList<LocalAppVer>();

    /**
     * 强制更新升级的DownloadInfo对象
     */
    private DownloadInfo forceDownloadInfo = null;

    public static SoftwareManager getInstance() {
        if (instance == null) {
            synchronized (SoftwareManager.class) {
                instance = new SoftwareManager();
            }
        }
        return instance;
    }

    /**
     * 初始化一些软件管理的本地操作
     *
     * @param context
     * @param isDeleteSelf 是否删除数据库中 应用商店本身的更新信息
     */
    public void init(Context context, boolean isDeleteSelf) {
        this.mContext = context;

        // 创建/初始化数据库
        db = new SoftwareManagerDB(context);
        if (isDeleteSelf) {
            DLog.i("lilijun", "SoftwareManager,删除数据库中商店本身的更新信息！");
            // 删除更新数据库中 应用商店本身的更新信息数据
            deleteUpdateInfoToDB(context.getPackageName());
        }
        // 获取需要采集的一些参数信息
        getCollectionConstantData();

        // // 设置友盟统计进入测试模式
        // MobclickAgent.setDebugMode(true);

        // 初始化个人登录信息
        LoginUserInfoManager.getInstance().init(context);

        // 初始化全局的下载监听器
        initDownloadLinstener();
        // 获取本地所有的已安装应用信息列表
        getInstalledAppInfos(context);

        // 获取常量数据
        getConstantData();

        // 初始化之前下载并安装应用成功之后 是否有未上报成功的数据
        ReportDownLoadDataManager.getInstance().init(mContext);

        // 获取是否强制升级
        NetUtil.getVersionForceState(context);

//		// 初始化多盟积分墙api
//		DOW.getInstance(context).init();
//
//		// 初始化贝多积分墙api
//		BeiduoPlatform.setAppId(context, "14370", "15388a50ab81113");

//		// 初始化爱玩自有积分墙
//		AWApi.init(context, "c4ca4238a0b923820dcc509a6f75849b",
//				"c4ca4238a0b923820dcc509a6f75849b", true);

//		// 初始化点入积分墙api
//		DRSdk.initialize(context, false, "");
        if (LoginUserInfoManager.getInstance().isHaveUserLogin()) {
//			// 设置点乐积分墙的用户id
//			DevInit.setCurrentUserID(context, LoginUserInfoManager
//					.getInstance().getLoginedUserInfo().getUserId()
//					+ "");

            // 友盟数据统计 登入账户
            MobclickAgent.onProfileSignIn(LoginUserInfoManager.getInstance()
                    .getLoginedUserInfo().getAccount());

//			// 设置有米积分墙的用户信息
//			OffersManager.getInstance(context).setCustomUserId(
//					LoginUserInfoManager.getInstance().getLoginedUserInfo()
//							.getUserId()
//							+ "");
//
//			// 设置贝多积分墙用户信息
//			BeiduoPlatform.setUserId(LoginUserInfoManager.getInstance()
//					.getLoginedUserInfo().getUserId()
//					+ "");
//
//			// 设置多盟积分墙用户信息
//			DOW.getInstance(context).setUserId(
//					LoginUserInfoManager.getInstance().getLoginedUserInfo()
//							.getUserId()
//							+ "");

//			// 设置爱玩积分墙用户信息
//			AWApi.setClientUserId(LoginUserInfoManager.getInstance()
//					.getLoginedUserInfo().getUserId()
//					+ "");

//			// 设置点入积分墙用户信息
//			DRSdk.setUserId(LoginUserInfoManager.getInstance()
//					.getLoginedUserInfo().getUserId()
//					+ "");

            // 如果有登录信息
            // 则去获取此登录用户的财富信息(必须在积分墙的初始化信息之后再去获取财富信息，因为有可能获取财富信息的时候userToken错误，退出了登录)
            NetUtil.getLoginUserGiftsAndTreasureData(mContext);

            // 上报之前未上报成功的所有数据
            NetUtil.reportDownloadDataToNet(mContext, null);

        }
        // 获取服务器广播消息
        NetUtil.getSystemAndUserMsg(context);

        AppStateConstants.init(mContext);
        DetailAppStateConstants.init(mContext);

        // 初始化赞的数据
        PraiseManager.getInstance().init(context);
        // 初始化搜索历史记录信息
        SearchHistoryManager.getInstance().init(context);

        // 初始化分享到微信朋友圈api
        WeiXinAPIManager.getInstance().init(context);

//		// 初始化大头鸟积分墙api
//		DTNIntegralManager.getInstance().init(context);

        // 设置友盟统计里的渠道号
        AnalyticsConfig.setChannel(DataCollectionConstant.channelNo);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_INSTALLED_SOFTWARE_SUCCESS);
        filter.addAction(Constants.ACTION_UNINSTALLED_SOFTWARE_SUCCESS);
        filter.addAction(Constants.ACTION_LOGIN_SUCCESS);
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(softwareReceiver, filter);
    }

    /**
     * 异步操作、初始化操作软件更新
     */
    public void initAll() {
        NetUtil.doLoadData(Constants.APP_API_URL, new String[]
                        {"ReqAppsUpdate"}, new ByteString[]
                        {getUpdateAppsRequestData(localAppVers)},
                new OnNetResponseLinstener() {
                    @Override
                    public void onNetError(String[] tags) {
                        doCheckNetAppUpdateFinish();
                    }

                    @Override
                    public void onLoadSuccess(RspPacket rspPacket) {
                        // 返回数据成功
                        Log.i(TAG, "从服务器获取更新应用信息已成功返回！");
                        RspAppsUpdate rspAppsUpdate = null;
                        try {
                            rspAppsUpdate = RspAppsUpdate.parseFrom(rspPacket
                                    .getParams(0));
                        } catch (InvalidProtocolBufferException e) {
                            DLog.e(TAG, "解析应用更新返回数据异常", e);
                            return;
                        }
                        if (rspAppsUpdate == null) {
                            return;
                        }
                        if (rspAppsUpdate.getRescode() == 0) {
                            // 获取更新应用成功
                            Hashtable<String, UpdateAppInfo> table = ToolsUtil
                                    .getUpdateAppInfosByAppDetailList(rspAppsUpdate
                                            .getAppInfosList());
                            DLog.i("lilijun", "获取更新应用，table.size()------>>>"
                                    + table.size());
                            for (Entry<String, UpdateAppInfo> entry : table
                                    .entrySet()) {
                                if (updateAppInfos.containsKey(entry.getKey())) {
                                    // 将之前保存的是否提示升级(是否忽略)信息保存下来
                                    entry.getValue().setPromptUpgreade(
                                            updateAppInfos.get(entry.getKey())
                                                    .isPromptUpgreade());
                                }
                            }
                            if (!table.isEmpty()) {
                                updateAppInfos.clear();
                                updateAppInfos.putAll(table);
                                // 清除数据库表中所有数据
                                db.clearFeedTable();
                                // 将更新信息重新再次插入表中
                                db.insertList(updateAppInfos);
                            } else {
                                // 说明 经过从网络审查 用户手机当前没有需要更新的应用信息
                                // 则清除当前已获取的更新信息
                                updateAppInfos.clear();
                                // 清除数据库表中所有数据
                                db.clearFeedTable();
                            }
                        } else {
                            DLog.e("lilijun",
                                    "获取更新应用列表失败！！！rspAppsUpdate.getRescode()--->>"
                                            + rspAppsUpdate.getRescode()
                                            + "----rspAppsUpdate.getResmsg()------>>>"
                                            + rspAppsUpdate.getResmsg());
                        }
                        doCheckNetAppUpdateFinish();
                    }

                    @Override
                    public void onLoadFailed(RspPacket rspPacket) {
                        DLog.e(TAG,
                                "软件更新，访问服务器出现异常#Exception:"
                                        + rspPacket.getResmsg());
                        doCheckNetAppUpdateFinish();
                    }
                });
    }

    /**
     * 从网络获取更新App数据之后(成功或失败)，所做操作
     */
    private void doCheckNetAppUpdateFinish() {
        if (LeplayPreferences.getInstance(mContext).isAutoUpdate()) {
            // 自动更新
            AutoUpdateManager.getInstance().autoUpdate();
        }
        // 显示更新通知栏
        showUpdateNotification();
        // 发送从网络获取到更新应用列表完成的广播
        mContext.sendBroadcast(new Intent(
                Constants.ACTION_SOFTWARE_MANAGER_GET_UPDATE_LIST_FROM_NETWORK_FINISH));
        if (LeplayPreferences.getInstance(mContext).isNeedRepordUpdateData()) {
            if (LoginUserInfoManager.getInstance().isHaveUserLogin()) {
                InstalledAppInfo installedAppInfo = installedAppInfos
                        .get(mContext.getPackageName());
                if (installedAppInfo != null) {
                    if (installedAppInfo.getVersionCode() == LeplayPreferences
                            .getInstance(mContext).getUpdateVersionCode()) {
                        // 如果本地安装的商店版本号 和
                        // 之前保存到配置文件中的有更新版本商店的版本号相同
                        PackInfo.Builder builder = PackInfo.newBuilder();
                        builder.setAppId(LeplayPreferences
                                .getInstance(mContext).getUpdateSoftId());
                        builder.setPackId(LeplayPreferences.getInstance(
                                mContext).getUpdatePackId());
                        // 上报数据
                        NetUtil.reportDownloadDataToNet(mContext,
                                builder.build());
                        LeplayPreferences.getInstance(mContext)
                                .setNeedRepordUpdateData(false);
                    }
                }
            }
        }
    }

    private ByteString getUpdateAppsRequestData(List<LocalAppVer> localAppVers) {
        ReqAppsUpdate.Builder builder = ReqAppsUpdate.newBuilder();
        for (LocalAppVer localAppVer : localAppVers) {
            builder.addLocalAppVer(localAppVer);
        }
        return builder.build().toByteString();
    }

    /**
     * 获取常量数据
     */
    private void getConstantData() {
        // 从缓存文件中先获取之前的缓存过的常量信息
        ConstantManager.getInstance().setConstantInfo(
                (ConstantInfo) ToolsUtil.getCacheDataFromFile(mContext,
                        Constants.CONSTANT_INFO_CANCHE_FILE_NAME));
        if (ConstantManager.getInstance().getConstantInfo() != null) {
            ConstantManager.getInstance().getConstantInfo()
                    .setIsExistRedPacket(0);
        }
        if (ConstantManager.getInstance().getConstantInfo() == null) {
            ConstantInfo constantInfo = new ConstantInfo();
            constantInfo.setCompanyEmail(ConstantManager.COMPANY_EMAIL);
            constantInfo.setExchangeRate(ConstantManager.EXCHANGE_RATE);
            constantInfo
                    .setFirstRegisterCoin(ConstantManager.FIRST_REGISTER_COIN);
            constantInfo
                    .setInviteFriendCoin(ConstantManager.INVITE_FRIEND_COIN);
            constantInfo
                    .setInviteMaxFriendCoin(ConstantManager.INVITE_MAX_FRIEND_COIN);
            constantInfo
                    .setInviteMaxFriendCount(ConstantManager.INVITE_MAX_FRIEND_COUNT);
            constantInfo.setQqGroupNo(ConstantManager.QQ_GROUP_NO);
            constantInfo.setServiceTelephone(ConstantManager.SERVICE_TELEPHONE);
            constantInfo
                    .setWeixinPublicAccount(ConstantManager.WEIXIN_PUBLIC_ACCOUNT);
            constantInfo.setSign7daysCoins(ConstantManager.SIGN_7_DAYS_COINS);
            constantInfo.setFriendBackRate(ConstantManager.FRIEND_BACK_RATE);
            constantInfo.setDesKey(ConstantManager.DES_KEY);
            constantInfo.setIsDeviceRegisted(ConstantManager.DEVICE_REGISTER);
            constantInfo
                    .setIsExistRedPacket(ConstantManager.IS_EXIST_RED_PACKET);
            ConstantManager.getInstance().setConstantInfo(constantInfo);
        }
        if (ConstantManager.getInstance().getConstantInfo().getLevelInfos()
                .isEmpty()) {
            // 如果没有级别信息 则添加默认的级别信息
            LevelInfo.Builder builder1 = LevelInfo.newBuilder();
            builder1.setLevelName(mContext.getResources().getString(
                    R.string.level_name_1));
            builder1.setStartAmount(0);
            builder1.setEndAmount(99);
            builder1.setLevelNo(1);
            ConstantManager.getInstance().getConstantInfo().getLevelInfos()
                    .put(1, builder1.build());

            LevelInfo.Builder builder2 = LevelInfo.newBuilder();
            builder2.setLevelName(mContext.getResources().getString(
                    R.string.level_name_2));
            builder2.setStartAmount(100);
            builder2.setEndAmount(299);
            builder2.setLevelNo(2);
            ConstantManager.getInstance().getConstantInfo().getLevelInfos()
                    .put(2, builder2.build());

            LevelInfo.Builder builder3 = LevelInfo.newBuilder();
            builder3.setLevelName(mContext.getResources().getString(
                    R.string.level_name_3));
            builder3.setStartAmount(300);
            builder3.setEndAmount(499);
            builder3.setLevelNo(3);
            ConstantManager.getInstance().getConstantInfo().getLevelInfos()
                    .put(3, builder3.build());

            LevelInfo.Builder builder4 = LevelInfo.newBuilder();
            builder4.setLevelName(mContext.getResources().getString(
                    R.string.level_name_4));
            builder4.setStartAmount(500);
            builder4.setEndAmount(999);
            builder4.setLevelNo(4);
            ConstantManager.getInstance().getConstantInfo().getLevelInfos()
                    .put(4, builder4.build());

            LevelInfo.Builder builder5 = LevelInfo.newBuilder();
            builder5.setLevelName(mContext.getResources().getString(
                    R.string.level_name_5));
            builder5.setStartAmount(1000);
            builder5.setEndAmount(1999);
            builder5.setLevelNo(5);
            ConstantManager.getInstance().getConstantInfo().getLevelInfos()
                    .put(5, builder5.build());

            LevelInfo.Builder builder6 = LevelInfo.newBuilder();
            builder6.setLevelName(mContext.getResources().getString(
                    R.string.level_name_6));
            builder6.setStartAmount(2000);
            // -1 表示无限大 即:>1999
            builder6.setEndAmount(-1);
            builder6.setLevelNo(6);
            ConstantManager.getInstance().getConstantInfo().getLevelInfos()
                    .put(6, builder6.build());
        }

        NetUtil.getConstantDataFromNet(mContext);
    }

    /**
     * 全局的下载监听器
     */
    private void initDownloadLinstener() {
        final Intent intent = new Intent(
                Constants.ACTION_DOWNLOAD_TASK_COUNT_CHANGE);
        DownloadListener listener = new DownloadListener() {

            @Override
            public void onTaskCountChanged(int state, DownloadInfo info) {
                // 发送任务池中 任务条数发送改变
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onStateChange(int state, DownloadInfo info) {
                if (state == DownloadInfo.STATE_FINISH) {
                    DLog.i("llj", "下载完成！！！" + info.getPackageName());
                    AdInfo adInfo = DownloadAdInfoManager.getInstance().getAdInfoMap().get
                            (info.getPackageName());
                    DLog.i("llj", "下载完成！！！adInfo==null---->>>" + (adInfo == null));
                    if (adInfo != null) {
                        // 如果存在的下载类型的广告对象
                        // 上报下载完成的信息
//                        JSONObject requestObject = AdUtils.getRequestUrls("inst_downsucc_url",
//                                adInfo.getInstDownloadStartUrls());
//                        NetUtil.requestUrl2("http://120.76.228.105:49520/kdxf/callback",
//                                requestObject, new Callback() {
//                                    @Override
//                                    public void onFailure(Request request, IOException e) {
//
//                                    }
//
//                                    @Override
//                                    public void onResponse(Response response) throws IOException {
//                                        DLog.i("llj", "下载完成上报数据成功!!! response.body---->>>" +
//                                                response.body().string());
//                                    }
//                                });
                        NetUtil.requestUrls(adInfo.getInstDownloadSuccUrls(), new NetUtil
                                .OnRequestMoreListener() {
                            @Override
                            public void onSuccess() {
                                DLog.i("llj", "上报下载完成的信息成功！！！");
                            }

                            @Override
                            public void onError() {
                                DLog.i("llj", "上报下载完成的信息失败！！！");
                            }
                        });
                    }
                    installApkByDownloadInfo(info);

                    if (adInfo != null) {
                        // 上报开始安装的信息
//                        JSONObject requestObject = AdUtils.getRequestUrls("inst_installstart_url",
//                                adInfo.getInstDownloadStartUrls());
//                        NetUtil.requestUrl2("http://120.76.228.105:49520/kdxf/callback",
//                                requestObject, new Callback() {
//                                    @Override
//                                    public void onFailure(Request request, IOException e) {
//
//                                    }
//
//                                    @Override
//                                    public void onResponse(Response response) throws IOException {
//                                        DLog.i("llj", "开始安装上报数据成功!!! response.body---->>>" +
//                                                response.body().string());
//                                    }
//                                });
                        NetUtil.requestUrls(adInfo.getInstInstallStartUrls(), new NetUtil
                                .OnRequestMoreListener() {

                            @Override
                            public void onSuccess() {
                                DLog.i("llj", "上报开始安装的信息成功！！！");
                            }

                            @Override
                            public void onError() {
                                DLog.i("llj", "上报开始安装的信息失败！！！");
                            }
                        });
                    }
                    String actionValue = DataCollectionManager
                            .getAction(
                                    info.getAction(),
                                    DataCollectionConstant.DATA_COLLECTION_DOWNLOAD_FINISH);
                    DataCollectionManager.getInstance().addRecord(actionValue,
                            DataCollectionManager.SOFT_ID, info.getSoftId(),
                            DataCollectionManager.PACK_ID,
                            info.getPackageId() + "");
                    // 添加友盟数据统计
                    HashMap<String, String> values = new HashMap<String, String>();
                    values.put(DataCollectionManager.SOFT_ID, info.getSoftId());
                    values.put(DataCollectionManager.PACK_ID,
                            info.getPackageId() + "");
                    DataCollectionManager
                            .getInstance()
                            .addYouMengEventRecord(
                                    mContext,
                                    actionValue,
                                    DataCollectionConstant.EVENT_ID_DOWNLOAD_TASK_FINISH,
                                    values);
                } else if (state == DownloadInfo.STATE_DOWNLOADING) {
                    DLog.i("llj", "开始下载！！！" + info.getPackageName());
                    DLog.e("lilijun",
                            "添加一个新的下载任务,action----------->>>"
                                    + info.getAction());
                    if (info.getAction()
                            .contains(
                                    DataCollectionConstant
                                            .DATA_COLLECTION_CP_DETAIL_CLICK_TOP_DOWNLOAD_BTN_VALUE)) {
                        // 通过点击详情顶部下载按钮进行下载的
                        // 我们自身是数据采集先不做 只做友盟的数据统计
                        // 添加友盟数据统计
                        HashMap<String, String> values = new HashMap<String, String>();
                        values.put(DataCollectionManager.SOFT_ID,
                                info.getSoftId());
                        values.put(DataCollectionManager.PACK_ID,
                                info.getPackageId() + "");
                        DataCollectionManager
                                .getInstance()
                                .addYouMengEventRecord(
                                        mContext,
                                        info.getAction(),
                                        DataCollectionConstant
                                                .EVENT_ID_CLICK_DETAIL_TOP_DOWNLOAD_BTN_FOR_ADD_NEW_TASK,
                                        values);
                    } else if (info
                            .getAction()
                            .contains(
                                    DataCollectionConstant
                                            .DATA_COLLECTION_CP_DETAIL_CLICK_BOTTOM_DOWNLOAD_BTN_VALUE)) {
                        // 通过点击详情底部下载按钮进行下载的
                        // 我们自身是数据采集先不做 只做友盟的数据统计
                        // 添加友盟数据统计
                        HashMap<String, String> values = new HashMap<String, String>();
                        values.put(DataCollectionManager.SOFT_ID,
                                info.getSoftId());
                        values.put(DataCollectionManager.PACK_ID,
                                info.getPackageId() + "");
                        DataCollectionManager
                                .getInstance()
                                .addYouMengEventRecord(
                                        mContext,
                                        info.getAction(),
                                        DataCollectionConstant
                                                .EVENT_ID_CLICK_DETAIL_BOTTOM_DOWNLOAD_BTN_FOR_ADD_NEW_TASK,
                                        values);
                    } else {
                        // 通过点击列表下载按钮进行下载的
                        // 我们自身是数据采集先不做 只做友盟的数据统计
                        // 添加友盟数据统计
                        HashMap<String, String> values = new HashMap<String, String>();
                        values.put(DataCollectionManager.SOFT_ID,
                                info.getSoftId());
                        values.put(DataCollectionManager.PACK_ID,
                                info.getPackageId() + "");
                        DataCollectionManager
                                .getInstance()
                                .addYouMengEventRecord(
                                        mContext,
                                        info.getAction(),
                                        DataCollectionConstant
                                                .EVENT_ID_CLICK_LIST_DOWNLOAD_BTN_FOR_ADD_NEW_TASK,
                                        values);
                    }
                }
            }

            @Override
            public void onProgress(int percent, DownloadInfo info) {
            }
        };

        DownloadManager.shareInstance().registerDownloadListener(listener);
    }

    /**
     * 获取所有的本地已安装的第三方应用信息
     *
     * @param context
     */
    private void getInstalledAppInfos(Context context) {
        // List<PackageInfo> packages = context.getPackageManager()
        // .getInstalledPackages(0);
        List<PackageInfo> packages = context.getPackageManager()
                .getInstalledPackages(PackageManager.GET_SIGNATURES);
        allAppCount = packages.size();
        PackageManager packageManager = context.getPackageManager();

		/*
         * 保存当前已安装的应用的包名 因为在获取本地安装应用大小的时候是异步线程、为了尽快显示数据库中之前保存的可更新的应用信息
		 * 所以先用此集合临时保存所有的已安装应用的包名 以供后面过滤更新信息使用
		 */
        List<String> installedPackgeNameList = new ArrayList<String>();
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageInfo.versionCode > 0 && packageInfo.versionName != null
                    && packageInfo.packageName != null) {
                InstalledAppInfo tmpInfo = new InstalledAppInfo();
                LocalAppVer.Builder builder = LocalAppVer.newBuilder();
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    // 第三方应用
                    tmpInfo.setSystemApp(false);
                    // 只保存第三方应用的图标(系统级应用就不保存图标了，因为也不会在已安装列表显示出来)
                    tmpInfo.setIcon(packageInfo.applicationInfo
                            .loadIcon(packageManager));
                } else {
                    // 系统应用
                    tmpInfo.setSystemApp(true);
                }
                tmpInfo.setName(packageInfo.applicationInfo.loadLabel(
                        packageManager).toString());
                tmpInfo.setPackageName(packageInfo.packageName);
                // 将包名保存到一个临时的已安装包名集合中去
                installedPackgeNameList.add(tmpInfo.getPackageName());
                tmpInfo.setVersionCode(packageInfo.versionCode);
                tmpInfo.setVersionName(packageInfo.versionName);

                // tmpInfo.setSign(getSign(packageInfo.signatures));
                tmpInfo.setSign("");

                if (tmpInfo.getPackageName().equals(context.getPackageName())) {
                    // 如果是商店本身
                    DataCollectionConstant.versionCode = tmpInfo
                            .getVersionCode();
                    DataCollectionConstant.versionName = tmpInfo
                            .getVersionName();
                }

                getAppSizeFromAllApps(packageManager, tmpInfo);

                // 添加相关属性到 LocalAppVer
                builder.setPackName(tmpInfo.getPackageName());
                builder.setVerName(tmpInfo.getVersionName());
                builder.setVerCode(tmpInfo.getVersionCode());
                // builder.setSignCode(tmpInfo.getSign());
                localAppVers.add(builder.build());
            } else {
                synchronized (this) {
                    allAppCount--;
                }
                if (i == packages.size() - 1) {
                    // 说明已经循环完毕 去更新
                    // 异步从网络获取可更新的应用信息
                    initAll();
                }
            }
        }

        // 获取数据库中保存的有更新的应用信息列表
        getUpdateAppInfosFromDB(installedPackgeNameList);
    }

    // /**
    // * 获取应用签名
    // *
    // * @param signature
    // */
    // private String getSign(Signature[] signatures)
    // {
    // String sign = "";
    // try
    // {
    // CertificateFactory certFactory = CertificateFactory
    // .getInstance("X.509");
    // for (int i = 0; i < signatures.length; i++)
    // {
    //
    // X509Certificate cert = (X509Certificate) certFactory
    // .generateCertificate(new ByteArrayInputStream(
    // signatures[i].toByteArray()));
    // String pubKey = getPublicKeyFormatString(cert);
    // sign += pubKey;
    // }
    // // if (!"".equals(sign))
    // // {
    // // sign = com.aora.base.util.Util.getMD5(sign);
    // // }
    // } catch (Exception e)
    // {
    // DLog.e(TAG, "getSign()#exception", e);
    // return "";
    // }
    // return sign;
    // }

    // /**
    // * 对publicKey进行统一格式化
    // *
    // * @param c
    // * @return
    // */
    // private String getPublicKeyFormatString(Certificate c)
    // {
    // String str = "";
    // PublicKey pk = c.getPublicKey();
    // if (pk instanceof RSAPublicKey)
    // {
    // RSAPublicKey rsaKey = (RSAPublicKey) pk;
    // str = rsaKey.getModulus() + "|" + rsaKey.getPublicExponent();
    // } else if (pk instanceof DSAPublicKey)
    // {
    // DSAPublicKey dsaKey = (DSAPublicKey) pk;
    // str = dsaKey.getY() + "";
    // }
    // return str;
    // }

    /**
     * 根据包名获取一个InstalledAppInfo对象
     */
    private void getInstalledAppInfoByPackageName(Context context,
                                                  String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            InstalledAppInfo tmpInfo = new InstalledAppInfo();
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 第三方应用
                tmpInfo.setSystemApp(false);
                // 只保存第三方应用的图标(系统级应用就不保存图标了，因为也不会在已安装列表显示出来)
                tmpInfo.setIcon(packageInfo.applicationInfo
                        .loadIcon(packageManager));
            } else {
                // 系统级应用
                tmpInfo.setSystemApp(true);
            }
            tmpInfo.setName(packageInfo.applicationInfo.loadLabel(
                    packageManager).toString());
            tmpInfo.setPackageName(packageInfo.packageName);
            tmpInfo.setVersionCode(packageInfo.versionCode);
            tmpInfo.setVersionName(packageInfo.versionName);
            // tmpInfo.setSign(getSign(packageInfo.signatures));
            tmpInfo.setSign("");
            // Signature[] signs = packageInfo.signatures;
            // tmpInfo.setSign(getPublicKey(signs[0].toByteArray()));
            // DLog.i("lilijun", "tmpInfo.getSign()------->>>" +
            // tmpInfo.getSign());
            getSigleAppSize(packageManager, tmpInfo);

        } catch (NameNotFoundException e) {
            DLog.e(TAG,
                    "getInstalledAppInfoByPackageName()#NameNotFoundException:",
                    e);
        }
    }

    // public String getPublicKey(byte[] signature)
    // {
    // try
    // {
    // CertificateFactory certFactory = CertificateFactory
    // .getInstance("X.509");
    // X509Certificate cert = (X509Certificate) certFactory
    // .generateCertificate(new ByteArrayInputStream(signature));
    // String signNumber = cert.getSerialNumber().toString();
    // DLog.i("lilijun", "signNumber------>" + signNumber);
    // return cert.getPublicKey().toString();
    // } catch (Exception e)
    // {
    // DLog.e(TAG, "getPublicKey()#Exception:", e);
    // return "";
    // }
    // }

    /**
     * 初始化时 获取所有已安装app信息中的其中一个的应用大小
     *
     * @param packageManager
     * @param appInfo
     */
    private void getAppSizeFromAllApps(PackageManager packageManager,
                                       InstalledAppInfo appInfo) {
        try {
            Method getPackageSizeInfo = packageManager.getClass().getMethod(
                    "getPackageSizeInfo", String.class,
                    IPackageStatsObserver.class);
            getPackageSizeInfo.invoke(packageManager, appInfo.getPackageName(),
                    new GetAllPkgSizeObserver(appInfo));
        } catch (Exception e) {
            DLog.e(TAG, "getAppSizeFromAllApps()#exception:", e);
        }
    }

    class GetAllPkgSizeObserver extends IPackageStatsObserver.Stub {
        private InstalledAppInfo appInfo;

        public GetAllPkgSizeObserver(InstalledAppInfo appInfo) {
            this.appInfo = appInfo;
        }

        /**
         * 异步方法
         */
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {
            appInfo.setAppSize(pStats.codeSize);
            appInfo.setFormatAppSize(ToolsUtil.formatFileSize(appInfo
                    .getAppSize()));
            installedAppInfos.put(appInfo.getPackageName(), appInfo);
            alreadyGetAppCount++;
            if (alreadyGetAppCount == allAppCount) {
                // 表示已经获取完本地的所有已安装的App信息(即 也代表SoftwareManager本地初始化操作已经完成)
                // 发送初始化完成的广播
                mContext.sendBroadcast(new Intent(
                        Constants.ACTION_SOFTWARE_MANAGER_INIT_DONE));
                // 异步从网络获取可更新的应用信息
                initAll();
            }
        }
    }

    /**
     * 获取已安装应用的大小
     *
     * @param packageManager 是否是根据包名获取单个已安装应用的大小
     */
    private void getSigleAppSize(PackageManager packageManager,
                                 InstalledAppInfo appInfo) {
        try {
            Method getPackageSizeInfo = packageManager.getClass().getMethod(
                    "getPackageSizeInfo", String.class,
                    IPackageStatsObserver.class);
            getPackageSizeInfo.invoke(packageManager, appInfo.getPackageName(),
                    new PkgSizeObserver(appInfo));
        } catch (Exception e) {
            DLog.e(TAG, "getAppSize()#exception:", e);
        }
    }

    class PkgSizeObserver extends IPackageStatsObserver.Stub {
        private InstalledAppInfo appInfo;

        public PkgSizeObserver(InstalledAppInfo appInfo) {
            this.appInfo = appInfo;
        }

        /**
         * 异步方法
         */
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {
            appInfo.setAppSize(pStats.codeSize);
            appInfo.setFormatAppSize(ToolsUtil.formatFileSize(appInfo
                    .getAppSize()));
            installedAppInfos.put(appInfo.getPackageName(), appInfo);
            if (updateAppInfos.containsKey(appInfo.getPackageName())) {
                updateAppInfos.remove(appInfo.getPackageName());
            }
            // 因为本方法是异步方法、所以当在广播中第二次转播广播时，在获取应用大小时完成时第二次转播可能已经转播出去，无法达到实时刷新数据的目的
            // 处理完本地逻辑之后、再次转播软件安装成功的本地广播
            Intent intent2 = new Intent();
            intent2.putExtra("packageName", appInfo.getPackageName());
            intent2.setAction(Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS);
            mContext.sendBroadcast(intent2);
        }
    }

    /**
     * 从数据库中获取更新应用信息列表
     *
     * @param installedPackageNameList 为当前所有的已安装的包名信息集合
     */
    private void getUpdateAppInfosFromDB(List<String> installedPackageNameList) {
        Hashtable<String, UpdateAppInfo> cancleInfos = db.queryAll();
        for (Entry<String, UpdateAppInfo> entry : cancleInfos.entrySet()) {
            if (!installedPackageNameList.contains(entry.getKey())) {
                // 如果不存在 表示用户已经卸载此软件 则需要清除之前已经保存在数据库中的软件信息
                db.delete(entry.getKey());
            } else {
                // 将还存在的更新信息保存到更新数据集合中去
                updateAppInfos.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 获得本地已安装应用列表
     *
     * @return
     */
    public Hashtable<String, InstalledAppInfo> getInstalledAppInfos() {
        return installedAppInfos;
    }

    /**
     * 获得有更新的应用信息列表
     *
     * @return
     */
    public Hashtable<String, UpdateAppInfo> getUpdateAppInfos() {
        return updateAppInfos;
    }

    /**
     * 获取强制升级的DownloadInfo对象
     */
    public DownloadInfo getForceDownloadInfo() {
        return forceDownloadInfo;
    }

    /**
     * 设置强制升级的DownloadInfo对象
     *
     * @param forceDownloadInfo
     */
    public void setForceDownloadInfo(DownloadInfo forceDownloadInfo) {
        this.forceDownloadInfo = forceDownloadInfo;
    }

    /**
     * 删除更新数据库中的数据
     *
     * @param packageName
     */
    private void deleteUpdateInfoToDB(String packageName) {
        db.delete(packageName);
    }

    /**
     * 更新数据库中的指定数据
     *
     * @param updateAppInfo
     */
    public void updateUpdateInfoToDB(UpdateAppInfo updateAppInfo) {
        db.update(updateAppInfo);
    }

    /**
     * 卸载应用
     *
     * @param context
     * @param packageName
     */
    public void uninstallApk(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);// 获取删除包名的URI
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }

    /**
     * 根据包名获取到App当前状态
     *
     * @param packageName
     * @return
     */
    public int getStateByPackageName(String packageName) {
        if (updateAppInfos.containsKey(packageName)) {
            return STATE_UPDATE;
        }
        if (installedAppInfos.containsKey(packageName)) {
            return STATE_INSTALLED;
        }
        return STATE_NO_INSTALLED;
    }

    /**
     * 安装apk
     *
     * @param info
     */
    public void installApkByDownloadInfo(DownloadInfo info) {
        File file = new File(info.getPath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    /**
     * 获取更新的Info所对应的DownloadInfo对象
     *
     * @param packageName
     * @return
     */
    public DownloadInfo getUpdateDownloadInfoByPackageName(String packageName) {
        UpdateAppInfo updateAppInfo = updateAppInfos.get(packageName);
        if (updateAppInfo != null) {
            return updateAppInfo.toDownloadInfo();
        }
        return null;
    }

    /**
     * 有更新应用的通知栏
     */
    public void showUpdateNotification() {
        if (updateAppInfos.isEmpty()) {
            return;
        }
        List<UpdateAppInfo> promptUpdateInfoList = new ArrayList<UpdateAppInfo>();
        for (Entry<String, UpdateAppInfo> entry : updateAppInfos.entrySet()) {
            if (entry.getValue().isPromptUpgreade()) {
                // 添加有更新的应用 所对应的 用户本地安装应用
                promptUpdateInfoList.add(updateAppInfos.get(entry.getKey()));
            }
            if (entry.getKey().equals(mContext.getPackageName())) {
                // 如果当前可更新的应用集合中有爱玩商店
                // 设置可更新版本的本地保存的数据
                LeplayPreferences.getInstance(mContext).setUpdatePackId(
                        entry.getValue().getPackageId());
                LeplayPreferences.getInstance(mContext).setUpdateSoftId(
                        entry.getValue().getSoftId());
                LeplayPreferences.getInstance(mContext).setUpdateVersionCode(
                        entry.getValue().getUpdateVersionCode());
            }
        }
        if (promptUpdateInfoList.isEmpty()) {
            return;
        }

        ImageLoaderManager imageLoaderManager = ImageLoaderManager
                .getInstance();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);
        mBuilder.setSmallIcon(R.drawable.app_icon_small);
        mBuilder.setAutoCancel(true);
        mBuilder.setWhen(System.currentTimeMillis());
        // mBuilder.setLargeIcon(BitmapFactory.decodeResource(
        // mContext.getResources(), R.drawable.icon_notification_large));

        RemoteViews contentView = new RemoteViews(mContext.getPackageName(),
                R.layout.update_notification_lay);
        String formatStr = String.format("您有  %s 款应用可以更新啦",
                promptUpdateInfoList.size() + "");
        // contentView.setTextViewText(
        // R.id.update_notification_text,
        // setFormatStrColor(formatStr, update_softwaresMap.size() + "�?,
        // "#2e975d"));
        contentView.setTextViewText(R.id.update_notification_content_title,
                formatStr);
        int size = promptUpdateInfoList.size();
        if (size > 3) {
            // 所有图片都显示 包括更多图片
            contentView.setViewVisibility(R.id.update_notification_icon1,
                    View.VISIBLE);
            contentView.setViewVisibility(R.id.update_notification_icon2,
                    View.VISIBLE);
            contentView.setViewVisibility(R.id.update_notification_icon3,
                    View.VISIBLE);
            contentView.setViewVisibility(R.id.update_notification_icon_more,
                    View.VISIBLE);

            contentView.setImageViewBitmap(R.id.update_notification_icon1,
                    imageLoaderManager.loadImageSync(promptUpdateInfoList
                            .get(0).getIconUrl()));
            contentView.setImageViewBitmap(R.id.update_notification_icon2,
                    imageLoaderManager.loadImageSync(promptUpdateInfoList
                            .get(1).getIconUrl()));
            contentView.setImageViewBitmap(R.id.update_notification_icon3,
                    imageLoaderManager.loadImageSync(promptUpdateInfoList
                            .get(2).getIconUrl()));

            contentView.setImageViewBitmap(
                    R.id.update_notification_icon_more,
                    ((BitmapDrawable) mContext.getResources().getDrawable(
                            R.drawable.update_notification_more_img))
                            .getBitmap());
        } else {
            switch (size) {
                case 1:
                    contentView.setViewVisibility(R.id.update_notification_icon1,
                            View.VISIBLE);
                    contentView.setViewVisibility(R.id.update_notification_icon2,
                            View.GONE);
                    contentView.setViewVisibility(R.id.update_notification_icon3,
                            View.GONE);
                    contentView.setViewVisibility(
                            R.id.update_notification_icon_more, View.GONE);

                    contentView.setImageViewBitmap(R.id.update_notification_icon1,
                            imageLoaderManager.loadImageSync(promptUpdateInfoList
                                    .get(0).getIconUrl()));
                    break;
                case 2:
                    contentView.setViewVisibility(R.id.update_notification_icon1,
                            View.VISIBLE);
                    contentView.setViewVisibility(R.id.update_notification_icon2,
                            View.VISIBLE);
                    contentView.setViewVisibility(R.id.update_notification_icon3,
                            View.GONE);
                    contentView.setViewVisibility(
                            R.id.update_notification_icon_more, View.GONE);

                    contentView.setImageViewBitmap(R.id.update_notification_icon1,
                            imageLoaderManager.loadImageSync(promptUpdateInfoList
                                    .get(0).getIconUrl()));
                    contentView.setImageViewBitmap(R.id.update_notification_icon2,
                            imageLoaderManager.loadImageSync(promptUpdateInfoList
                                    .get(1).getIconUrl()));
                    break;
                case 3:
                    contentView.setViewVisibility(R.id.update_notification_icon1,
                            View.VISIBLE);
                    contentView.setViewVisibility(R.id.update_notification_icon2,
                            View.VISIBLE);
                    contentView.setViewVisibility(R.id.update_notification_icon3,
                            View.VISIBLE);
                    contentView.setViewVisibility(
                            R.id.update_notification_icon_more, View.GONE);

                    contentView.setImageViewBitmap(R.id.update_notification_icon1,
                            imageLoaderManager.loadImageSync(promptUpdateInfoList
                                    .get(0).getIconUrl()));
                    contentView.setImageViewBitmap(R.id.update_notification_icon2,
                            imageLoaderManager.loadImageSync(promptUpdateInfoList
                                    .get(1).getIconUrl()));
                    contentView.setImageViewBitmap(R.id.update_notification_icon3,
                            imageLoaderManager.loadImageSync(promptUpdateInfoList
                                    .get(2).getIconUrl()));
                    break;
            }
        }

        Intent updateAllIntent = new Intent(
                Constants.ACTION_NOTIFY_UPDATE_ALL_BTN_CLICK);
        PendingIntent updateAllPendingIntent = PendingIntent.getBroadcast(
                mContext, 0, updateAllIntent, 0);
        contentView
                .setOnClickPendingIntent(
                        R.id.update_notification_update_all_btn,
                        updateAllPendingIntent);

        mBuilder.setContent(contentView);
        // 通知栏滚动消息
        mBuilder.setTicker("您有" + promptUpdateInfoList.size() + "款应用有更新啦");

        // Intent intent = new Intent(UPDATE_ACTION);
        Intent intent = new Intent();
        intent.setClass(mContext, UpdateAppActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                intent, 0);
        mBuilder.setContentIntent(contentIntent);
        NotificationManager mnotiManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification mNotification = mBuilder.build();
        mNotification.contentView = contentView;

        if (mnotiManager == null || mNotification == null) {
            return;
        }
        // mnotiManager.notify(UPDATE_NOTIFICATION_ID, mNotification);
        try {
            mnotiManager.notify(112, mNotification);
        } catch (Exception e) {
            DLog.e(TAG, "showUpdateNotification()#exception:", e);
        }

    }

    /**
     * 获取数据采集必需的数据
     */
    private void getCollectionConstantData() {
        ToolsUtil.getImeiAndImsi(mContext);
        DataCollectionConstant.macAdress = ToolsUtil.getLocalMacAddress();
        DataCollectionConstant.brand = Build.BRAND;
        DataCollectionConstant.model = Build.MODEL;
        DataCollectionConstant.manuFacturer = Build.MANUFACTURER;
        DataCollectionConstant.androidVersionName = Build.VERSION.RELEASE;
        DataCollectionConstant.androidId = ToolsUtil.getAndroidId(mContext);
        DataCollectionConstant.density = ToolsUtil.getDensity(mContext);

        if (DataCollectionConstant.imei == null) {
            DataCollectionConstant.imei = "";
        }
        if (DataCollectionConstant.imsi == null) {
            DataCollectionConstant.imsi = "";
        }
        if (DataCollectionConstant.brand == null) {
            DataCollectionConstant.brand = "";
        }
        if (DataCollectionConstant.model == null) {
            DataCollectionConstant.model = "";
        }
        if (DataCollectionConstant.manuFacturer == null) {
            DataCollectionConstant.manuFacturer = "";
        }
        if (DataCollectionConstant.androidVersionName == null) {
            DataCollectionConstant.androidVersionName = "";
        }

        // 组合起来的设备唯一编号
        DataCollectionConstant.combinationUDI = "mf="
                + DataCollectionConstant.manuFacturer + "&branch="
                + DataCollectionConstant.brand + "&model="
                + DataCollectionConstant.model + "&imsi="
                + DataCollectionConstant.imsi + "&imei="
                + DataCollectionConstant.imei + "&mac="
                + DataCollectionConstant.macAdress;
    }

    /**
     * 软件管理广播类
     *
     * @author lilijun
     */
    private BroadcastReceiver softwareReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getStringExtra("packageName");
            if (Constants.ACTION_INSTALLED_SOFTWARE_SUCCESS.equals(intent
                    .getAction())) {
                // 软件安装成功
                DLog.i("lilijun", "SoftwareManager,接收到转播软件安装成功的广播！！");
                // 获取安装成功的apk的InstalledAppInfo信息，并判断是否是第三方应用，如果是则添加到已安装列表集合中去
                getInstalledAppInfoByPackageName(context, packageName);

                DownloadInfo downloadInfo = DownloadManager.shareInstance()
                        .queryDownload(packageName);
                if (downloadInfo != null) {
                    // 如果用户有登录并且刚刚下载的任务有送积分
                    if (LoginUserInfoManager.getInstance().isHaveUserLogin())
                    /** 修改为下载有无金币都会上传 */
                    // && downloadInfo.getIntegral() > 0)
                    {
                        DLog.i("lilijun", "开始上报数据！！");
                        // 上报下载数据{"record_url":"1-11-12-10","soft_id":"","pack_id":"",
                        // "user_id":10000001}
                        PackInfo.Builder builder = PackInfo.newBuilder();
                        builder.setAppId(Long.parseLong(downloadInfo
                                .getSoftId()));
                        builder.setPackId(downloadInfo.getPackageId());
                        builder.setAppName(downloadInfo.getName());
                        builder.setCoin(downloadInfo.getIntegral());
                        NetUtil.reportDownloadDataToNet(mContext,
                                builder.build());
                    }
                    String actionValue = DataCollectionManager
                            .getAction(
                                    downloadInfo.getAction(),
                                    DataCollectionConstant.DATA_COLLECTION_DOWNLOAD_FINISH
                                            + "-"
                                            + DataCollectionConstant
                                            .DATA_COLLECTION_DOWNLOAD_AND_INSTALLED);
                    DataCollectionManager.getInstance().addRecord(actionValue,
                            DataCollectionManager.SOFT_ID,
                            downloadInfo.getSoftId(),
                            DataCollectionManager.PACK_ID,
                            downloadInfo.getPackageId() + "");

                    // 添加友盟数据统计
                    HashMap<String, String> values = new HashMap<String, String>();
                    values.put(DataCollectionManager.SOFT_ID,
                            downloadInfo.getSoftId());
                    values.put(DataCollectionManager.PACK_ID,
                            downloadInfo.getPackageId() + "");
                    DataCollectionManager
                            .getInstance()
                            .addYouMengEventRecord(
                                    context,
                                    actionValue,
                                    DataCollectionConstant
                                            .EVENT_ID_DOWNLOAD_TASK_AND_INSTALL_SUCCESS,
                                    values);

                    // 删除下载任务列表中的任务
                    DownloadManager.shareInstance().deleteDownload(
                            downloadInfo,
                            LeplayPreferences.getInstance(mContext)
                                    .isDeleteApk());
                }
                DownloadInfo autoupdateDownloadInfo = AutoUpdateManager
                        .getInstance().downloadManager
                        .queryDownload(packageName);
                if (autoupdateDownloadInfo != null) {
                    // 如果用户有登录并且刚刚下载的任务有送积分
                    if (LoginUserInfoManager.getInstance().isHaveUserLogin())
                    /** 修改为下载有无金币都会上传 */
                    // && autoupdateDownloadInfo.getIntegral() > 0)
                    {
                        DLog.i("lilijun", "开始上报自动更新的数据！！");
                        // 上报下载数据
                        PackInfo.Builder builder = PackInfo.newBuilder();
                        builder.setAppId(Long.parseLong(autoupdateDownloadInfo
                                .getSoftId()));
                        builder.setPackId(autoupdateDownloadInfo.getPackageId());
                        builder.setAppName(autoupdateDownloadInfo.getName());
                        builder.setCoin(autoupdateDownloadInfo.getIntegral());
                        NetUtil.reportDownloadDataToNet(context,
                                builder.build());
                    }
                    String actionValue = DataCollectionManager
                            .getAction(
                                    autoupdateDownloadInfo.getAction(),
                                    DataCollectionConstant.DATA_COLLECTION_DOWNLOAD_FINISH
                                            + "-"
                                            + DataCollectionConstant
                                            .DATA_COLLECTION_DOWNLOAD_AND_INSTALLED);
                    DataCollectionManager.getInstance().addRecord(actionValue,
                            DataCollectionManager.SOFT_ID,
                            autoupdateDownloadInfo.getSoftId(),
                            DataCollectionManager.PACK_ID,
                            autoupdateDownloadInfo.getPackageId() + "");
                    // 添加友盟数据统计
                    HashMap<String, String> values = new HashMap<String, String>();
                    values.put(DataCollectionManager.SOFT_ID,
                            autoupdateDownloadInfo.getSoftId());
                    values.put(DataCollectionManager.PACK_ID,
                            autoupdateDownloadInfo.getPackageId() + "");
                    DataCollectionManager
                            .getInstance()
                            .addYouMengEventRecord(
                                    context,
                                    actionValue,
                                    DataCollectionConstant
                                            .EVENT_ID_AUTO_DOWNLOAD_TASK_AND_INSTALL_SUCCESS,
                                    values);
                    // 删除自动下载列表中的下载任务
                    AutoUpdateManager.getInstance().downloadManager
                            .deleteDownload(autoupdateDownloadInfo);
                }


                final AdInfo adInfo = DownloadAdInfoManager.getInstance().getAdInfoMap().get
                        (packageName);
                if (adInfo != null) {
                    // 上报安装完成的数据
//                    JSONObject requestObject = AdUtils.getRequestUrls("inst_installsucc_url",
//                            adInfo.getInstDownloadStartUrls());
//                    NetUtil.requestUrl2("http://120.76.228.105:49520/kdxf/callback",
//                            requestObject, new Callback() {
//                                @Override
//                                public void onFailure(Request request, IOException e) {
//
//                                }
//
//                                @Override
//                                public void onResponse(Response response) throws IOException {
//                                    DLog.i("llj", "安装完成上报数据成功!!! response.body---->>>" +
//                                            response.body().string());
//                                }
//                            });
                    NetUtil.requestUrls(adInfo.getInstInstallSuccUrls(), new NetUtil
                            .OnRequestMoreListener() {
                        @Override
                        public void onSuccess() {
                            DLog.i("llj", "上报安装成功的信息成功！！！");
                            // 移除保存的下载类型广告信息
                            DownloadAdInfoManager.getInstance().getAdInfoMap().remove(adInfo);
                        }

                        @Override
                        public void onError() {
                            DLog.i("llj", "上报安装成功的信息失败！！！");
                        }
                    });
                }

                // // 处理完本地逻辑之后、再次转播软件安装成功的本地广播
                // Intent intent2 = new Intent();
                // intent2.putExtra("packageName", packageName);
                // intent2.setAction(Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS);
                // context.sendBroadcast(intent2);
            } else if (Constants.ACTION_UNINSTALLED_SOFTWARE_SUCCESS
                    .equals(intent.getAction())) {
                // 软件卸载成功
                DLog.i("lilijun", "SoftwareManager,接收到转播软件卸载成功的广播！！");
                if (!packageName.equals(mContext.getPackageName())) {
                    installedAppInfos.remove(packageName);
                    if (updateAppInfos.containsKey(packageName)) {
                        // 删除更新集合中的数据
                        updateAppInfos.remove(packageName);
                        // 删除更新数据库中的数据
                        deleteUpdateInfoToDB(packageName);
                    }
                }
                // 处理完本地逻辑之后、再次转播软件卸载成功的本地广播
                Intent intent2 = new Intent();
                intent2.putExtra("packageName", packageName);
                intent2.setAction(Constants.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS);
                context.sendBroadcast(intent2);
                DownloadInfo downloadInfo = DownloadManager.shareInstance()
                        .queryDownload(packageName);
                if (downloadInfo != null) {
                    // 下载任务列表中有此任务
                    Intent intent3 = new Intent();
                    intent3.putExtra(DOWNLOAD_INFO, downloadInfo);
                    intent3.setAction(Constants.ACTION_UNINSTALLED_SOFTWARE_HAVE_DOWNLOADINFO);
                    context.sendBroadcast(intent3);
                }
            } else if (Constants.ACTION_LOGIN_SUCCESS
                    .equals(intent.getAction())) {
                // 登录成功 重新设置大头鸟的用户账户Id
//				DTNIntegralManager.getInstance().reSetClientUserId(
//						LoginUserInfoManager.getInstance().getLoginedUserInfo()
//								.getUserId()
//								+ "");

//				// 登录成功 设置点乐积分墙的用户id
//				DevInit.setCurrentUserID(context, LoginUserInfoManager
//						.getInstance().getLoginedUserInfo().getUserId()
//						+ "");

                // 友盟数据统计 登入账户
                MobclickAgent.onProfileSignIn(LoginUserInfoManager
                        .getInstance().getLoginedUserInfo().getAccount());

//				// 设置有米积分墙的用户信息
//				OffersManager.getInstance(context).setCustomUserId(
//						LoginUserInfoManager.getInstance().getLoginedUserInfo()
//								.getUserId()
//								+ "");
//				// 重新设置有米帐号之后 需要调用此方法来重新设置帐号
//				OffersManager.getInstance(context).onAppLaunch();
//
//				// 设置贝多积分墙用户信息
//				BeiduoPlatform.setUserId(LoginUserInfoManager.getInstance()
//						.getLoginedUserInfo().getUserId()
//						+ "");
//
//				// 设置多盟积分墙用户信息
//				DOW.getInstance(context).setUserId(
//						LoginUserInfoManager.getInstance().getLoginedUserInfo()
//								.getUserId()
//								+ "");

//				// 设置爱玩积分墙用户信息
//				AWApi.setClientUserId(LoginUserInfoManager.getInstance()
//						.getLoginedUserInfo().getUserId()
//						+ "");

//				// 设置点入积分墙用户信息
//				DRSdk.setUserId(LoginUserInfoManager.getInstance()
//						.getLoginedUserInfo().getUserId()
//						+ "");
                // 当用户登录了
                // 则去获取我的礼包和财富数据(必须在积分墙的初始化信息之后再去获取财富信息，因为有可能获取财富信息的时候userToken错误，退出了登录)
                NetUtil.getLoginUserGiftsAndTreasureData(context);

            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent
                    .getAction())) {
                DLog.i("lilijun", "网络改变广播！！！");
                // 网络状态改变广播
                if (ToolsUtil.checkNetwork(context)) {
                    // 获取是否强制升级
                    NetUtil.getVersionForceState(context);
                    // 获取常量数据
                    NetUtil.getConstantDataFromNet(context);
                    // 获取财富消息
                    NetUtil.getLoginUserGiftsAndTreasureData(context);
                    // 上报未上报成功的下载数据
                    NetUtil.reportDownloadDataToNet(context, null);
                }
            }
        }
    };
}
