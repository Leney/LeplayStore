package com.xd.leplay.store;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.xd.base.util.DLog;
import com.xd.base.util.DefaultExceptionHandler;
import com.xd.base.util.PropertiesUtil;
import com.xd.download.DownloadInfo;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.control.AutoUpdateManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.EtagManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LeplayPreferences;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.control.UpgradAppManager;
import com.xd.leplay.store.util.ToolsUtil;

import java.util.Locale;
import java.util.Properties;

public class LeplayApplication extends Application {
    private static LeplayApplication instance;

    /**
     * 是否打印日志的总开关
     */
    private boolean logSwitch = false;

    private OkHttpClient client;

    private static Properties properties;

    // private static Properties properties;

    public static LeplayApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//		if (!"com.donson.leplay.store".equals(ToolsUtil
//				.getCurProcessName(getApplicationContext())))
//		{
//			// 如果不是商店应用进程
//			return;
//		}

        Log.i("lilijun", "LeplayApplication,onCreate() 启动！！！");
        // 应用内配置语言
        Resources resources = getResources();
        // 获得设置对象
        Configuration config = resources.getConfiguration();
        // 获得屏幕参数：主要是分辨率，像素等。
        DisplayMetrics dm = resources.getDisplayMetrics();
        // 简体中文
        config.locale = Locale.SIMPLIFIED_CHINESE;
        resources.updateConfiguration(config, dm);

        instance = this;
        // 全局异常监听
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(
                getApplicationContext()));

        DataCollectionConstant.screenWidth = dm.widthPixels;
        DataCollectionConstant.screenHeight = dm.heightPixels;
        // 获取手机分辨率
        DataCollectionConstant.screenResolution = DataCollectionConstant.screenWidth + "*"
                + DataCollectionConstant.screenHeight;
        if (properties == null) {
            properties = PropertiesUtil.initProperties(getApplicationContext());
        }
        // 获取配置url信息
        getConfigUrl();
        // 初始化日志
        DLog.init("xd", logSwitch);
        // 初始化下载模块
        DownloadManager.shareInstance().init(getApplicationContext());
        // 初始化自动更新
        AutoUpdateManager.getInstance().init(getApplicationContext());
        // 初始化强制升级
        UpgradAppManager.getInstance().init(getApplicationContext());
        // 初始化图片加载器
        ImageLoaderManager.getInstance().init(getApplicationContext());
        // 初始化数据采集管理器
        DataCollectionManager.getInstance().init(getApplicationContext());
        // 初始化Etag数据管理器
        EtagManager.getInstance().init(getApplicationContext());

        if (ToolsUtil.isFirstIn(getApplicationContext())) {
            DLog.i("lilijun", "是第一次启动   ， 删除下载列表中本商店的下载任务！！");
            DownloadInfo downloadInfo = DownloadManager.shareInstance()
                    .queryDownload(getApplicationContext().getPackageName());
            // 设置配置数据为第一次登录
            LeplayPreferences.getInstance(getApplicationContext()).setFirstIn(
                    true);
            // 设置未显示过升级爱玩的Dialog
            LeplayPreferences.getInstance(getApplicationContext())
                    .setShowUpgradeDialog(false);
            // 设置需要上报更新并安装了新版商店的数据
            LeplayPreferences.getInstance(getApplicationContext())
                    .setNeedRepordUpdateData(true);
            if (downloadInfo != null) {
                DownloadManager.shareInstance().deleteDownload(downloadInfo);
            }
            DownloadInfo autoDownloadInfo = AutoUpdateManager.getInstance().downloadManager
                    .queryDownload(getApplicationContext().getPackageName());
            if (autoDownloadInfo != null) {
                AutoUpdateManager.getInstance().downloadManager
                        .deleteDownload(autoDownloadInfo);
            }
            // 软件管理初始化(删除更新数据库中 应用商店本身的更新信息)
            SoftwareManager.getInstance().init(getApplicationContext(), true);
        } else {
            // 软件管理初始化(正常初始化)
            SoftwareManager.getInstance().init(getApplicationContext(), false);
        }
    }

    /***
     * 获取httpClient对象
     *
     * @return
     */
    public OkHttpClient getHttpClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    // 通过此方法传入名称，来获取此名称所对应的配置信息
    private String getProperty(String key) {
        return properties.getProperty(key, "");
    }

    /**
     * 获取配置信息
     */
    private void getConfigUrl() {
        // Constants.APP_API_URL = getProperty("APP_API_URL");
        // Constants.UAC_API_URL = getProperty("UAC_API_URL");
        // Constants.LOTTERY_URL = getProperty("LOTTERY_URL");
        // Constants.UPLOAD_PIC_URL = getProperty("UPLOAD_PIC_URL");
        // Constants.REPORT_COLLECTION_DATA_URL =
        // getProperty("REPORT_COLLECTION_DATA_URL");
        // Constants.FRIEND_BACK_MONEY_URL =
        // getProperty("FRIEND_BACK_MONEY_URL");
        // Constants.AIWAN_APK_DOWNLOAD_URL =
        // getProperty("AIWAN_APK_DOWNLOAD_URL");


        Constants.APP_API_URL = ToolsUtil
                .decryptUrl(getProperty("APP_API_URL"));
//		Constants.APP_API_URL = "http://120.76.228.105:49520/";
        Constants.UAC_API_URL = ToolsUtil
                .decryptUrl(getProperty("UAC_API_URL"));
//		Constants.UAC_API_URL = "http://120.76.228.105:49530/";
        Constants.LOTTERY_URL = ToolsUtil
                .decryptUrl(getProperty("LOTTERY_URL"));
        Constants.UPLOAD_PIC_URL = ToolsUtil
                .decryptUrl(getProperty("UPLOAD_PIC_URL"));
        Constants.REPORT_COLLECTION_DATA_URL = ToolsUtil
                .decryptUrl(getProperty("REPORT_COLLECTION_DATA_URL"));
        Constants.FRIEND_BACK_MONEY_URL = ToolsUtil
                .decryptUrl(getProperty("FRIEND_BACK_MONEY_URL"));
        Constants.AIWAN_APK_DOWNLOAD_URL = ToolsUtil
                .decryptUrl(getProperty("AIWAN_APK_DOWNLOAD_URL"));
        Constants.RED_PACKET_SHARE_URL = ToolsUtil
                .decryptUrl(getProperty("RED_PACKET_SHARE_URL"));
        // Log.i("lilijun", "Constants.APP_API_URL------------->>>"
        // + Constants.APP_API_URL);
        // Log.i("lilijun", "Constants.UAC_API_URL------------->>>"
        // + Constants.UAC_API_URL);
        // Log.i("lilijun", "Constants.LOTTERY_URL------------->>>"
        // + Constants.LOTTERY_URL);
        // Log.i("lilijun", "Constants.UPLOAD_PIC_URL------------->>>"
        // + Constants.UPLOAD_PIC_URL);
        // Log.i("lilijun",
        // "Constants.REPORT_COLLECTION_DATA_URL------------->>>"
        // + Constants.REPORT_COLLECTION_DATA_URL);
        // Log.i("lilijun", "Constants.FRIEND_BACK_MONEY_URL------------->>>"
        // + Constants.FRIEND_BACK_MONEY_URL);
        // Log.i("lilijun", "Constants.AIWAN_APK_DOWNLOAD_URL------------->>>"
        // + Constants.AIWAN_APK_DOWNLOAD_URL);

        Constants.BAI_DU_PUSH_API_KEY_VALUE = getProperty("BAI_DU_PUSH_API_KEY_VALUE");
        Constants.WEIXIN_APP_ID = getProperty("WEIXIN_APP_ID");
        Constants.WEIXIN_APP_SECRET = getProperty("WEIXIN_APP_SECRET");
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            DataCollectionConstant.versionCode = packageInfo.versionCode;
            DataCollectionConstant.versionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataCollectionConstant.channelNo = getProperty("CHANNEL_NO");
        String value = getProperty("IS_LOG");
        if ("true".equals(value)) {
            logSwitch = true;
        } else {
            logSwitch = false;
        }

        // String entryStr1 = ToolsUtil.encryptUrl(Constants.APP_API_URL);
        // Log.i("lilijun", "APP_API_URL--------->>" + entryStr1);
        // String entryStr2 = ToolsUtil.encryptUrl(Constants.UAC_API_URL);
        // Log.i("lilijun", "UAC_API_URL--------->>" + entryStr2);
        // String entryStr3 = ToolsUtil.encryptUrl(Constants.LOTTERY_URL);
        // Log.i("lilijun", "LOTTERY_URL--------->>" + entryStr3);
        // String entryStr4 = ToolsUtil.encryptUrl(Constants.UPLOAD_PIC_URL);
        // Log.i("lilijun", "UPLOAD_PIC_URL--------->>" + entryStr4);
        // String entryStr5 = ToolsUtil
        // .encryptUrl(Constants.REPORT_COLLECTION_DATA_URL);
        // Log.i("lilijun", "REPORT_COLLECTION_DATA_URL--------->>" +
        // entryStr5);
        // String entryStr6 = ToolsUtil
        // .encryptUrl(Constants.FRIEND_BACK_MONEY_URL);
        // Log.i("lilijun", "FRIEND_BACK_MONEY_URL--------->>" + entryStr6);
        // String entryStr7 = ToolsUtil
        // .encryptUrl(Constants.AIWAN_APK_DOWNLOAD_URL);
        // Log.i("lilijun", "AIWAN_APK_DOWNLOAD_URL--------->>" + entryStr7);
        //
        // Log.i("lilijun", "------------------解密后-----------------");
        // Log.i("lilijun",
        // "APP_API_URL--------->>" + ToolsUtil.decryptUrl(entryStr1));
        // Log.i("lilijun",
        // "UAC_API_URL--------->>" + ToolsUtil.decryptUrl(entryStr2));
        // Log.i("lilijun",
        // "LOTTERY_URL--------->>" + ToolsUtil.decryptUrl(entryStr3));
        // Log.i("lilijun",
        // "UPLOAD_PIC_URL--------->>" + ToolsUtil.decryptUrl(entryStr4));
        // Log.i("lilijun",
        // "REPORT_COLLECTION_DATA_URL--------->>"
        // + ToolsUtil.decryptUrl(entryStr5));
        // Log.i("lilijun",
        // "FRIEND_BACK_MONEY_URL--------->>"
        // + ToolsUtil.decryptUrl(entryStr6));
        // Log.i("lilijun",
        // "AIWAN_APK_DOWNLOAD_URL--------->>"
        // + ToolsUtil.decryptUrl(entryStr7));
    }
}
