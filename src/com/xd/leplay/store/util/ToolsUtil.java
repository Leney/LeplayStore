package com.xd.leplay.store.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.qd.croe.BarcodeFormat;
import com.qd.croe.EncodeHintType;
import com.qd.croe.WriterException;
import com.qd.croe.common.BitMatrix;
import com.qd.croe.qrcode.QRCodeWriter;
import com.qd.croe.qrcode.decoder.ErrorCorrectionLevel;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.LeplayApplication;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.LeplayPreferences;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.control.WeiXinAPIManager;
import com.xd.leplay.store.model.CommentInfo;
import com.xd.leplay.store.model.DetailAppInfo;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.model.UpdateAppInfo;
import com.xd.leplay.store.model.proto.App.AppDetail;
import com.xd.leplay.store.model.proto.App.AppInfo;
import com.xd.leplay.store.model.proto.App.AppUpdate;
import com.xd.leplay.store.model.proto.Uac.RecommentContent;
import com.xd.leplay.store.model.proto.Uac.RspLogUser;
import com.xd.leplay.store.model.proto.Uac.RspSetUserInfo;
import com.xd.leplay.store.model.proto.Uac.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ToolsUtil {

    private static final String TAG = "ToolsUtil";

    /**
     * 获取文件大小
     *
     * @param length
     * @return
     */
    public static String formatFileSize(long length) {
        String result = null;
        int sub_string = 0;
        if (length >= 1073741824) {
            sub_string = String.valueOf((float) length / 1073741824).indexOf(
                    ".");
            result = ((float) length / 1073741824 + "000").substring(0,
                    sub_string + 3) + "GB";
        } else if (length >= 1048576) {
            sub_string = String.valueOf((float) length / 1048576).indexOf(".");
            result = ((float) length / 1048576 + "000").substring(0,
                    sub_string + 3) + "MB";
        } else if (length >= 1024) {
            sub_string = String.valueOf((float) length / 1024).indexOf(".");
            result = ((float) length / 1024 + "000").substring(0,
                    sub_string + 3) + "KB";
        } else if (length < 1024)
            result = Long.toString(length) + "B";
        return result;
    }

    /**
     * 将文件长度转换成字符串有单位的形式
     *
     * @param size
     * @return
     */
    public static final String getFormatSize(long size) {
        DecimalFormat df = new DecimalFormat("#.0");
        String fileSizeString = "";
        if (size < 1024) {
            if (size == 0) {
                fileSizeString = "0B";
            } else {
                fileSizeString = df.format((double) size) + "B";
            }

        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "KB";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 将下载次数转换成有单位的形式
     *
     * @param count
     * @return
     */
    public static String getFormatDownloadCount(long count) {
        DecimalFormat df = new DecimalFormat("0.0");
        if (count < 10000) {
            return count + "";
        } else if (count <= 99999999) {
            // 最高9999.9万
            return df.format(count / 10000d) + "万";
        } else {
            // 最高9999.9亿
            return df.format(count / 100000000d) + "亿";
        }
    }

    /**
     * 将赞的次数转换成有单位的形式
     *
     * @param count
     * @return
     */
    public static String getFormatPraiseCount(int count) {
        DecimalFormat df = new DecimalFormat("0.0");
        if (count < 10000) {
            return count + "";
        } else if (count <= 99999999) {
            // 最高9999.9万
            return df.format(count / 10000d) + "万";
        } else {
            // 最高9999.9亿
            return df.format(count / 100000000d) + "亿";
        }
    }

    /**
     * 检查当前网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean checkNetworkValid(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            DLog.e(TAG, "checkNetworkValid()#exception:", e);
        }
        DisplayUtil.showNetErrorDialog(context);
        return false;
    }

    /**
     * 检查当前网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean checkNetwork(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            DLog.e(TAG, "checkNetworkValid()#exception:", e);
        }
        return false;
    }

    /**
     * 根据包名打开软件
     *
     * @param context
     * @param packageName 软件包名
     */
    public static boolean openSoftware(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            Intent intent = context.getPackageManager()
                    .getLaunchIntentForPackage(packageName);
            intent.putExtra("IS_FROME_MARKET", true);
            if (intent != null) {
                context.startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            DLog.e(TAG, "openSoftware()#Exception=", e);
        }
        return false;
    }

    /**
     * 检查手机内部存储空间小于20M
     *
     * @param context
     */
    public static boolean checkMemorySize(Context context, DownloadInfo info) {
        File path = Environment.getDataDirectory(); // 获取数据目录
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        // if ((blockSize * availableBlocks) < (20 * 1024 * 1024))
        // if ((blockSize * availableBlocks) < info.getSize())
        if ((blockSize * availableBlocks) < info.getSize()) {
            Toast.makeText(
                    context,
                    context.getResources()
                            .getString(R.string.not_enough_memory),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 跳转到网络设置界面
     *
     * @param context
     */
    public static void startSettingActivity(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 判断当前网络是否是Wifi网络
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State
                .CONNECTED ? true
                : false;
        // boolean isGprsConnected = cm.getNetworkInfo(
        // ConnectivityManager.TYPE_MOBILE).getState() ==
        // NetworkInfo.State.CONNECTED ? true
        // : false;
    }

    /**
     * 将从服务器获取到的列表AppInfo转换成本地的ListAppInfo对象
     *
     * @param info
     * @return
     */
    public static ListAppInfo getListAppInfo(AppInfo info) {
        ListAppInfo appInfo = new ListAppInfo();
        appInfo.setSoftId(info.getAppId());
        appInfo.setPackageId(info.getPackId());
        appInfo.setName(info.getShowName());
        appInfo.setRecommendDescribe(info.getRecommWord());
        appInfo.setIconUrl(info.getIconUrl());
        appInfo.setDownlaodUrl(info.getPackUrl());
        appInfo.setPackageName(info.getPackName());
        appInfo.setSize(info.getPackSize());
        appInfo.setFormatSize(ToolsUtil.getFormatSize(appInfo.getSize()));
        appInfo.setCoin(info.getRewardCorn());
        appInfo.setStarLevel(info.getRecommLevel());
        appInfo.setDownloadCount(info.getDownTimes());
        appInfo.setFormatDownloadCount(ToolsUtil.getFormatDownloadCount(appInfo
                .getDownloadCount()));
        appInfo.setTaskId(info.getTaskId());
        // 首发
        appInfo.setFirst((info.getRecommFlag() & (int) Math.pow(2, 0)) != 0);
        // 礼包
        appInfo.setHaveGift((info.getRecommFlag() & (int) Math.pow(2, 1)) != 0);
        // 热门
        appInfo.setHot((info.getRecommFlag() & (int) Math.pow(2, 2)) != 0);
        // 精品
        appInfo.setBoutique((info.getRecommFlag() & (int) Math.pow(2, 3)) != 0);
        appInfo.setAd(false);
        return appInfo;
    }

    /**
     * 将从服务器获取到的详情AppDetail 转换成本地的DetailAppInfo对象
     *
     * @param info
     * @return
     */
    public static DetailAppInfo getDetailAppInfo(AppDetail info) {
        DetailAppInfo appInfo = new DetailAppInfo();
        appInfo.setSoftId(info.getAppId());
        appInfo.setPackageId(info.getPackId());
        appInfo.setName(info.getShowName());
        appInfo.setRecommendDescribe(info.getRecommWord());
        appInfo.setDeveloper(info.getDevName());
        appInfo.setDownloadCount(info.getDownTimes());
        appInfo.setFormatDownloadCount(ToolsUtil.getFormatDownloadCount(info
                .getDownTimes()));
        appInfo.setStarLevel(info.getRecommLevel());
        appInfo.setIconUrl(info.getIconUrl());
        if (!info.getAppPicUrlList().isEmpty()) {
            appInfo.setScreenUrls(info.getAppPicUrlList());
        }
        // else
        // {
        // appInfo.getScreenUrls()
        // .add("http://a.hiphotos.bdimg
        // .com/wisegame/pic/item/2234970a304e251f0a7264cfa686c9177f3e5304.jpg");
        // appInfo.getScreenUrls()
        // .add("http://e.hiphotos.bdimg
        // .com/wisegame/pic/item/3aee3d6d55fbb2fb1fb818304c4a20a44623dc05.jpg");
        // appInfo.getScreenUrls()
        // .add("http://b.hiphotos.bdimg
        // .com/wisegame/pic/item/df315c6034a85edf249a5aeb48540923dc5475e0.jpg");
        // appInfo.getScreenUrls()
        // .add("http://e.hiphotos.bdimg
        // .com/wisegame/pic/item/ff82d158ccbf6c8165037b7bbd3eb13533fa4069.jpg");
        // }
        appInfo.setDownlaodUrl(info.getPackUrl());
        appInfo.setPackageName(info.getPackName());
        appInfo.setSize(info.getPackSize());
        appInfo.setFormatSize(ToolsUtil.getFormatSize(appInfo.getSize()));
        appInfo.setVersionCode(info.getVerCode());
        appInfo.setVersionName(info.getVerName());
        appInfo.setDescribe(info.getAppDesc());
        appInfo.setUpdateDescribe(info.getUpdateDesc());
        appInfo.setPublishDate(info.getPublishTime());
        appInfo.setSign(info.getSignCode());
        appInfo.setPraiseCount(info.getLikeCount());
        appInfo.setFormatPraiseCount(ToolsUtil.getFormatPraiseCount(appInfo
                .getPraiseCount()));
        appInfo.setCoin(info.getRewardCorn());
        // 首发
        appInfo.setFirst((info.getRecommFlag() & (int) Math.pow(2, 0)) != 0);
        // 礼包
        appInfo.setHaveGift((info.getRecommFlag() & (int) Math.pow(2, 1)) != 0);
        // 热门
        appInfo.setHot((info.getRecommFlag() & (int) Math.pow(2, 2)) != 0);
        // 精品
        appInfo.setBoutique((info.getRecommFlag() & (int) Math.pow(2, 3)) != 0);
        // 官方
        appInfo.setOfficial((info.getRecommFlag() & (int) Math.pow(2, 4)) != 0);
        // 安全
        appInfo.setSafety((info.getRecommFlag() & (int) Math.pow(2, 5)) != 0);
        // 无广告
        appInfo.setNoAd((info.getRecommFlag() & (int) Math.pow(2, 6)) != 0);
        return appInfo;
    }

    /**
     * 将一个AppInfo的集合转换成ListAppInfo的集合
     *
     * @return
     */
    public static List<ListAppInfo> getListAppInfosByAppInfoList(
            List<AppInfo> appInfos) {
        List<ListAppInfo> list = new ArrayList<ListAppInfo>();
        for (AppInfo appInfo : appInfos) {
            list.add(ToolsUtil.getListAppInfo(appInfo));
        }
        return list;
    }

    /**
     * 将一个AppDetail的集合转换成DetailAppInfo的集合
     *
     * @param appDetails
     * @return
     */
    public static Hashtable<String, UpdateAppInfo> getUpdateAppInfosByAppDetailList(
            List<AppUpdate> appUpdates) {
        Hashtable<String, UpdateAppInfo> table = new Hashtable<String, UpdateAppInfo>();
        for (AppUpdate appUpdate : appUpdates) {
            UpdateAppInfo updateAppInfo = getUpdateAppInfo(appUpdate);
            table.put(updateAppInfo.getPackageName(), updateAppInfo);
        }
        return table;
    }

    /**
     * 将从服务器获取到的详情AppUpdate 转换成本地的UpdateAppInfo对象
     *
     * @param appUpdate
     * @return
     */
    public static UpdateAppInfo getUpdateAppInfo(AppUpdate appUpdate) {
        UpdateAppInfo updateAppInfo = new UpdateAppInfo();
        updateAppInfo.setPackageId(appUpdate.getPackId());
        updateAppInfo.setSoftId(appUpdate.getAppId());
        updateAppInfo.setName(appUpdate.getAppName());
        updateAppInfo.setPackageName(appUpdate.getPackName());
        updateAppInfo.setLocalVersionCode(appUpdate.getLocalVerCode());
        updateAppInfo.setLocalVersionName(appUpdate.getLocalVerName());
        updateAppInfo.setUpdateVersionCode(appUpdate.getVerCode());
        updateAppInfo.setUpdateVersionName(appUpdate.getVerName());
        updateAppInfo.setIconUrl(appUpdate.getIconUrl());
        updateAppInfo.setDownloadUrl(appUpdate.getPackUrl());
        updateAppInfo.setUpdateSoftSize(appUpdate.getPackSize());
        updateAppInfo.setPublishDate(appUpdate.getPublishTime());
        updateAppInfo.setSign(appUpdate.getSignCode());
        updateAppInfo.setCoins(appUpdate.getRewardCorn());
        if (!"".equals(appUpdate.getUpdatePercent())) {
            updateAppInfo.setUpdatePercent(appUpdate.getUpdatePercent());
        } else {
            // 如果后台给到的百分比是空的 则取80-99之间的随机数
            updateAppInfo.setUpdatePercent(getRandomNum(80, 19) + "%");
        }
        updateAppInfo.setUpdateDescribe(appUpdate.getUpdateDesc());
        return updateAppInfo;
    }

    /**
     * 将从服务器获取到的RspLogUser 转换成本地的LoginedUserInfo对象
     *
     * @param info
     * @return
     */
    public static LoginedUserInfo getLoginedUserInfo(RspLogUser rspInfo) {
        LoginedUserInfo userInfo = new LoginedUserInfo();
        userInfo.setUserToken(rspInfo.getUserToken());

        UserInfo info = rspInfo.getUserInfo();
        userInfo.setUserId(info.getUid());
        userInfo.setAccount(info.getAccountName());
        userInfo.setSex(info.getUserSex());
        userInfo.setEmail(info.getUserMail());
        userInfo.setPhone(info.getUserMobile());
        userInfo.setIconUrl(info.getHeadPicUrl());
        userInfo.setLastLoginTime(info.getLastLogTime());
        userInfo.setRegisterTime(info.getRegTime());
        userInfo.setUserStatus(info.getUserStatus());
        userInfo.setRecommenderId(info.getRecommenderId());
        userInfo.setNickName(info.getShowName());
        userInfo.setOpenId(info.getWxOpenId());
        userInfo.setWxUnionId(info.getWxUnionId());
        return userInfo;
    }

    /**
     * 将从服务器获取到的RecommnetContent对象转换成CommentInfo对象
     *
     * @param content
     * @return
     */
    public static CommentInfo getCommentInfo(RecommentContent content) {
        CommentInfo commentInfo = new CommentInfo();
        commentInfo.setCommentContent(content.getContent());
        commentInfo.setCommentTime(content.getCreateTime());
        commentInfo.setStarLevel(content.getLevel());
        commentInfo.setUserIcon(content.getHeadPic());
        commentInfo.setUserName(content.getUserName());
        commentInfo.setUserId(content.getUid());
        return commentInfo;
    }

    /**
     * 将从服务器获取到的RspSetUserInfo 转换成本地的LoginedUserInfo对象
     *
     * @param info
     * @return
     */
    public static LoginedUserInfo getLoginedUserInfo(RspSetUserInfo rspInfo) {
        LoginedUserInfo userInfo = null;
        if (LoginUserInfoManager.getInstance().isHaveUserLogin()) {
            userInfo = LoginUserInfoManager.getInstance().getLoginedUserInfo();
        } else {
            userInfo = new LoginedUserInfo();
            userInfo.setUserToken("");
        }

        UserInfo info = rspInfo.getUserInfo();
        userInfo.setUserId(info.getUid());
        userInfo.setAccount(info.getAccountName());
        userInfo.setSex(info.getUserSex());
        userInfo.setEmail(info.getUserMail());
        userInfo.setPhone(info.getUserMobile());
        userInfo.setIconUrl(info.getHeadPicUrl());
        userInfo.setLastLoginTime(info.getLastLogTime());
        userInfo.setRegisterTime(info.getRegTime());
        userInfo.setUserStatus(info.getUserStatus());
        userInfo.setRecommenderId(info.getRecommenderId());
        userInfo.setNickName(info.getShowName());
        userInfo.setIconUrl(info.getHeadPicUrl());
        userInfo.setOpenId(info.getWxOpenId());
        userInfo.setWxUnionId(info.getWxUnionId());
        return userInfo;
    }

    /**
     * 根据字符串获取md5值
     */
    public static String getMD5ByString(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }

    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }

    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        String[] strDigits =
                {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
                        "e", "f"};
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 返回形式只为数字
    private static String byteToNum(byte bByte) {
        int iRet = bByte;
        System.out.println("iRet1=" + iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }

    /**
     * 保存缓存数据到文件
     *
     * @param <T>
     * @param context
     * @param list
     * @return
     */
    public static boolean saveCachDataToFile(Context context, String fileName,
                                             Object object) {
        if (object == null) {
            return false;
        }
        try {
            // 需要一个文件输出流和对象输出流；文件输出流用于将字节输出到文件，对象输出流用于将对象输出为字节
            FileOutputStream fout = context.openFileOutput(fileName + ".ser",
                    Activity.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(object);
            out.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "saveCachDataToFile()#Exception:", e);
        }
        return false;
    }

    /**
     * 从文件里面获取缓存数据
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Object getCacheDataFromFile(Context context, String fileName) {
        Object object = null;
        try {
            FileInputStream fin = context.openFileInput(fileName + ".ser");
            ObjectInputStream in = new ObjectInputStream(fin);
            object = in.readObject();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "getCacheDataToFile()#Exception:", e);
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return object;
    }

    /**
     * 清除指定文件名的缓存
     *
     * @param context
     * @param fileName
     */
    public static void clearCacheDataToFile(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName + ".ser");
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 获取两个数值之间的随机数
     *
     * @param max
     * @param min
     * @return
     */
    public static int getRandomNum(int min, int poor) {
        return (int) (min + Math.random() * poor);
    }

    /**
     * 实现文本复制功能
     *
     * @param content
     */
    public static boolean copy(String content, Context context) {
        if (content.trim().equals("")) {
            return false;
        }
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        return true;
    }

    /**
     * 设置一段文本其中的某一段文字不同颜色
     *
     * @param text
     * @param start    不同颜色文本开始时的index值
     * @param end      不同颜色文本结束时的index值
     * @param colorHex 颜色的16进制值 例如"#ff0000"
     * @return
     */
    public static SpannableStringBuilder getFormatTextColor(String text,
                                                            int start, int end, String colorHex) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(
                Color.parseColor(colorHex));
        // Spannable.SPAN_INCLUSIVE_EXCLUSIVE 指示的显示特别颜色的的文本index的范围是：前面包括，后面不包括
        builder.setSpan(colorSpan, start, end,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 将字符串转换成Bitmap类型
     *
     * @param string
     * @return
     */
    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 将Bitmap转换成字符串
     *
     * @param bitmap
     * @return
     */

    public static String bitmapToString(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    /**
     * 字符串压缩为字节数组
     */
    public static byte[] compressToByte(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes("utf-8"));
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * 字符串压缩为字节数组
     */
    public static byte[] compressToByte(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * 字节数组解压缩后返回字符串
     */
    public static String uncompressToString(byte[] b) {
        if (b == null || b.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(b);

        try {
            GZIPInputStream gunzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    /**
     * 字节数组解压缩后返回字符串
     */
    public static String uncompressToString(byte[] b, String encoding) {
        if (b == null || b.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(b);

        try {
            GZIPInputStream gunzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString(encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 压缩
    public static String compress(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
            gzip.close();
            return out.toString("ISO-8859-1");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 解压缩
    public static String uncompress(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(
                    str.getBytes("ISO-8859-1"));
            GZIPInputStream gunzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            // toString()使用平台默认编码，也可以显式的指定如toString("GBK")
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 获取bitmap的大小
     *
     * @param bitmap
     * @return
     */
    public static long getBitmapSize(Bitmap bitmap) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();

    }

    /**
     * 分享到某一个平台
     *
     * @param type 包名
     */
    private void initShareIntent(Context context, String type) {
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        // gets the list of intentsthat can be loaded.
        List<ResolveInfo> resInfo = context.getPackageManager()
                .queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type)
                        || info.activityInfo.name.toLowerCase().contains(type)) {
                    // share.putExtra(Intent.EXTRA_SUBJECT, "subject");
                    share.putExtra(Intent.EXTRA_TEXT, "your text");

                    // share.putExtra(Intent.EXTRA_STREAM,
                    // Uri.fromFile(newFile(myPath))); // Optional, just
                    // // if you wanna
                    // // share an
                    // // image.
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return;
            context.startActivity(Intent.createChooser(share, "Select"));
        }
    }

    /**
     * 只显示指定的分享平台
     */
    public static void share2Spec(Context context, String packageName,
                                  String shareTitle, String shareContent) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        List<ResolveInfo> resInfo = context.getPackageManager()
                .queryIntentActivities(intent, 0);
        if (!resInfo.isEmpty()) {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            for (ResolveInfo info : resInfo) {
                Intent targeted = new Intent(Intent.ACTION_SEND);
                targeted.setType("text/plain");
                ActivityInfo activityInfo = info.activityInfo;

                // judgments :activityInfo.packageName, activityInfo.name, etc.
                // if (activityInfo.packageName.contains("com.sina.weibo")
                // || activityInfo.name.contains("tencent"))
                // {
                // targeted.putExtra(Intent.EXTRA_TEXT, "分享内容");
                // targeted.setPackage(activityInfo.packageName);
                // targetedShareIntents.add(targeted);
                // }
                if (activityInfo.packageName.contains(packageName)) {
                    targeted.putExtra(Intent.EXTRA_TEXT, shareContent);
                    targeted.setPackage(activityInfo.packageName);
                    targetedShareIntents.add(targeted);
                }

            }
            if (!targetedShareIntents.isEmpty()) {
                Intent chooserIntent = Intent.createChooser(
                        targetedShareIntents.remove(0), shareTitle);
                if (chooserIntent == null) {
                    return;
                }
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                        targetedShareIntents.toArray(new Parcelable[]
                                {}));
                try {
                    context.startActivity(chooserIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context,
                            "Can't find sharecomponent to share",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(
                        context,
                        context.getResources().getString(
                                R.string.no_installed_app), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * 分享到微信
     *
     * @param flag 0=分享给朋友，1=分享到朋友圈
     */
    public static void share2weixin(Context context, int flag, String title,
                                    String description, String url) {
        // Bitmap bmp = BitmapFactory.decodeResource(getResources(),
        // R.drawable.weixin_share);
        IWXAPI weiXinApi = WeiXinAPIManager.getInstance().getWeiXinApi();
        if (!weiXinApi.isWXAppInstalled()) {
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.no_installed_app),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);

        msg.title = title;
        msg.description = description;
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.app_icon);
        msg.setThumbImage(thumb);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag;
        weiXinApi.sendReq(req);
    }

    private void shareToFriend(Context context) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, "测试数据分享到朋友圈");
        // intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(intent);
    }

    private void shareToTimeLine(Context context, File file) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, "测试数据分享到朋友圈？？？");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(intent);
    }

    /**
     * 判断是否是第一次启动
     *
     * @param context
     * @return
     */
    public static boolean isFirstIn(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            int currentVersion = info.versionCode;
            int lastVersion = LeplayPreferences.getInstance(context)
                    .getLastVersionCode();
            if (currentVersion > lastVersion) {
                // 如果当前版本大于上次版本，该版本属于第一次启动
                // 清除之前sharedPreferences中保存的所有数据
                LeplayPreferences.getInstance(context)
                        .clearSharePrefrencesData();
                // 将当前版本写入preference中，则下次启动的时候，据此判断，不再为首次启动
                LeplayPreferences.getInstance(context).setLastVersionCode(
                        currentVersion);
                return true;
            }
            return false;
        } catch (Exception e) {
            DLog.e(TAG, "isFirstIn#exception:", e);
            return false;
        }
    }

    /**
     * 获取手机分辨率
     *
     * @return
     */
    public static String getScreenResolution(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return screenWidth + "*" + screenHeight;
    }

    // /**
    // * 获取Ip地址
    // */
    // public static String getLocalIpAddress()
    // {
    // try
    // {
    // String ipv4;
    // List<NetworkInterface> nilist = Collections.list(NetworkInterface
    // .getNetworkInterfaces());
    // for (NetworkInterface ni : nilist)
    // {
    // List<InetAddress> ialist = Collections.list(ni
    // .getInetAddresses());
    // for (InetAddress address : ialist)
    // {
    // if (!address.isLoopbackAddress()
    // && InetAddressUtils.isIPv4Address(ipv4 = address
    // .getHostAddress()))
    // {
    // return ipv4;
    // }
    // }
    // }
    // } catch (Exception e)
    // {
    // Log.e(TAG, "getLocalIpAddress()#获取ip地址异常", e);
    // }
    // return "";
    // }

    /**
     * 获取mac 地址
     *
     * @return
     */
    public static String getLocalMacAddress() {
        String Mac = "";
        try {
            String path = "sys/class/net/wlan0/address";
            if ((new File(path)).exists()) {
                FileInputStream fis = new FileInputStream(path);
                byte[] buffer = new byte[8192];
                int byteCount = fis.read(buffer);
                if (byteCount > 0) {
                    Mac = new String(buffer, 0, byteCount, "utf-8");
                }
            }
            if (Mac == null || Mac.length() == 0) {
                path = "sys/class/net/eth0/address";
                FileInputStream fis_name = new FileInputStream(path);
                byte[] buffer_name = new byte[8192];
                int byteCount_name = fis_name.read(buffer_name);
                if (byteCount_name > 0) {
                    Mac = new String(buffer_name, 0, byteCount_name, "utf-8");
                }
            }

            if (Mac.length() == 0 || Mac == null) {
                return "";
            }
        } catch (Exception e) {
            DLog.e(TAG, "getLocalMacAddress()#获取Mac地址异常：", e);
            Mac = "";
        }

        return Mac.trim();
    }

    /**
     * 获取手机imei和imsi号
     *
     * @param context
     * @return
     */
    public static void getImeiAndImsi(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            DataCollectionConstant.imei = telephonyManager.getDeviceId();
            String imsi = telephonyManager.getSubscriberId();
            if(TextUtils.isEmpty(DataCollectionConstant.imei)){
                DataCollectionConstant.imei = "";
            }
            if(!TextUtils.isEmpty(imsi)){
                DataCollectionConstant.imsi = imsi;
            }

            DLog.i("llj","DataCollectionConstant.imsi--------->>>"+DataCollectionConstant.imsi);
            if (imsi != null) {
                if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
                    // 中国移动
                    //因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
                    DataCollectionConstant.imsiCode = "46000";
                } else if (imsi.startsWith("46001")) {
                    //中国联通
                    DataCollectionConstant.imsiCode = "46001";
                } else if (imsi.startsWith("46003")) {
                    //中国电信
                    DataCollectionConstant.imsiCode = "46003";
                }
            }else {
                // 测试的时候先写死
                DataCollectionConstant.imsiCode = "46000";
            }
        } catch (Exception e) {
            DLog.e(TAG, "获取Imei号发生异常#exception：", e);
        }
    }

    /**
     * 上报必须采集的数据给服务器
     *
     * @param json
     */
    public static void repordDataCollection(JSONObject json) {
        final MediaType JSON = MediaType
                .parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json.toString());
        // 构建请求
        Request request = new Request.Builder()
                .url(Constants.REPORT_COLLECTION_DATA_URL).post(body)// 添加请求体
                .build();
        LeplayApplication.getInstance().getHttpClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onResponse(Response response)
                            throws IOException {
                        if (response.code() == 200) {
                            DLog.i("lilijun", "上报必须采集的数据成功！！");
                        } else {
                            DLog.e("lilijun",
                                    "上报必须采集的数据失败！！  返回码：" + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Request request, IOException arg1) {
                        DLog.e("lilijun", "上报必须采集的数据失败！！");
                    }
                });
    }

    // 序列化对象为String字符串，先对序列化后的结果进行BASE64编码，否则不能直接进行反序列化
    public static String writeObject(Object o) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(o);
        oos.flush();
        oos.close();
        bos.close();
        // return new BASE64Encoder().encode(bos.toByteArray());
        return new String(bos.toByteArray(), "ISO-8859-1");
    }

    /**
     * 对象序列化为字符串
     */
    public static String serialize(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            String serStr = byteArrayOutputStream.toString("ISO-8859-1");// 必须是ISO-8859-1
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8");// 编码后字符串不是乱码（不编也不影响功能）
            DLog.i("lilijun", "对象obj：【" + obj + "】序列化serStr：【" + serStr + "】");
            objectOutputStream.close();
            byteArrayOutputStream.close();
            return serStr;
        } catch (Exception e) {
            DLog.e(TAG, "序列化发生异常#exception：", e);
        }
        return "";
    }

    public static String toJSON(Object obj) {
        JSONStringer js = new JSONStringer();
        serialize(js, obj);
        return js.toString();
    }

    /**
     * 序列化为JSON
     **/
    private static void serialize(JSONStringer js, Object o) {
        if (isNull(o)) {
            try {
                js.value(null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        Class<?> clazz = o.getClass();
        if (isObject(clazz)) { // 对象
            serializeObject(js, o);
        } else if (isArray(clazz)) { // 数组
            serializeArray(js, o);
        } else if (isCollection(clazz)) { // 集合
            Collection<?> collection = (Collection<?>) o;
            serializeCollect(js, collection);
        } else { // 单个值
            try {
                js.value(o);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 序列化数组
     **/
    private static void serializeArray(JSONStringer js, Object array) {
        try {
            js.array();
            for (int i = 0; i < Array.getLength(array); ++i) {
                Object o = Array.get(array, i);
                serialize(js, o);
            }
            js.endArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 序列化集合
     **/
    private static void serializeCollect(JSONStringer js,
                                         Collection<?> collection) {
        try {
            js.array();
            for (Object o : collection) {
                serialize(js, o);
            }
            js.endArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 序列化对象
     **/
    private static void serializeObject(JSONStringer js, Object obj) {
        try {
            js.object();
            for (Field f : obj.getClass().getFields()) {
                Object o = f.get(obj);
                js.key(f.getName());
                serialize(js, o);
            }
            js.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断对象是否为空
     **/
    private static boolean isNull(Object obj) {
        if (obj instanceof JSONObject) {
            return JSONObject.NULL.equals(obj);
        }
        return obj == null;
    }

    /**
     * 判断是否是值类型
     **/
    private static boolean isSingle(Class<?> clazz) {
        return isBoolean(clazz) || isNumber(clazz) || isString(clazz);
    }

    /**
     * 是否布尔值
     **/
    public static boolean isBoolean(Class<?> clazz) {
        return (clazz != null)
                && ((Boolean.TYPE.isAssignableFrom(clazz)) || (Boolean.class
                .isAssignableFrom(clazz)));
    }

    /**
     * 是否数值
     **/
    public static boolean isNumber(Class<?> clazz) {
        return (clazz != null)
                && ((Byte.TYPE.isAssignableFrom(clazz))
                || (Short.TYPE.isAssignableFrom(clazz))
                || (Integer.TYPE.isAssignableFrom(clazz))
                || (Long.TYPE.isAssignableFrom(clazz))
                || (Float.TYPE.isAssignableFrom(clazz))
                || (Double.TYPE.isAssignableFrom(clazz)) || (Number.class
                .isAssignableFrom(clazz)));
    }

    /**
     * 判断是否是字符串
     **/
    public static boolean isString(Class<?> clazz) {
        return (clazz != null)
                && ((String.class.isAssignableFrom(clazz))
                || (Character.TYPE.isAssignableFrom(clazz)) || (Character.class
                .isAssignableFrom(clazz)));
    }

    /**
     * 判断是否是对象
     **/
    private static boolean isObject(Class<?> clazz) {
        return clazz != null && !isSingle(clazz) && !isArray(clazz)
                && !isCollection(clazz);
    }

    /**
     * 判断是否是数组
     **/
    public static boolean isArray(Class<?> clazz) {
        return clazz != null && clazz.isArray();
    }

    /**
     * 判断是否是集合
     **/
    public static boolean isCollection(Class<?> clazz) {
        return clazz != null && Collection.class.isAssignableFrom(clazz);
    }

    /**
     * 获取当前进程名称
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static void deleteAllFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteAllFile(f);
            }
            file.delete();
        }
    }

    public static String urlEnodeUTF8(String str) {
        String result = str;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 加密url
     *
     * @param url
     * @return
     */
    public static String encryptUrl(String url) {
        StringBuilder builder = new StringBuilder(Base64.encodeToString(
                url.getBytes(), Base64.DEFAULT));
        char[] chars =
                {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        Integer[] indexs = new Integer[5];
        for (int i = 0; i < indexs.length; i++) {
            // 获取0-35之间的随机数
            indexs[i] = getRandomNum(0, 35);
            // 查找随机数所对应位置的值并append到结果中去
            builder.append(chars[indexs[i]]);
        }
        return builder.toString();
    }

    /**
     * 解密url
     *
     * @param encrytUrl
     * @return
     */
    public static String decryptUrl(String encrytUrl) {
        // 截取真正的加密值
        String realUrl = encrytUrl.substring(0, encrytUrl.length() - 5);
        try {
            return new String(Base64.decode(realUrl, Base64.DEFAULT), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 生成二维码
     *
     * @param url    网络链接地址或者文本
     * @param width  二维码宽度
     * @param height 二维码高度
     * @return
     */
    public static Bitmap createQRImage(String url, int width, int height) {
        int QR_WIDTH = width;
        int QR_HEIGHT = height;
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            // 用于设置QR二维码参数
            Hashtable<EncodeHintType, Object> qrParam = new Hashtable<EncodeHintType, Object>();
            // 设置QR二维码的纠错级别——这里选择最高H级别
            qrParam.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 设置编码方式
            qrParam.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            // Hashtable<EncodeHintType, String> hints = new
            // Hashtable<EncodeHintType, String>();
            // hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, qrParam);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 在二维码上绘制头像
     */
    public static void createQRCodeBitmapWithPortrait(int qecode_w,
                                                      int qecode_h, Bitmap qr, Bitmap portrait) {
        // 头像图片的大小
        int portrait_W = portrait.getWidth();
        int portrait_H = portrait.getHeight();

        // 设置头像要显示的位置，即居中显示
        int left = (qecode_w - portrait_W) / 2;
        int top = (qecode_h - portrait_H) / 2;
        int right = left + portrait_W;
        int bottom = top + portrait_H;
        Rect rect1 = new Rect(left, top, right, bottom);

        // 取得qr二维码图片上的画笔，即要在二维码图片上绘制我们的头像
        Canvas canvas = new Canvas(qr);

        // 设置我们要绘制的范围大小，也就是头像的大小范围
        Rect rect2 = new Rect(0, 0, portrait_W, portrait_H);
        // 开始绘制
        canvas.drawBitmap(portrait, rect2, rect1, null);
    }

    /**
     * 获取设备的Android id
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        return android.provider.Settings.Secure.getString(context.getContentResolver(), android
                .provider
                .Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取屏幕密度
     *
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.density;
//        Log.d(TAG,"Density is "+displayMetrics.density+" densityDpi is "+displayMetrics
// .densityDpi+" height: "+displayMetrics.heightPixels+
//                " width: "+displayMetrics.widthPixels);
    }


    /**
     * 获取当前网络类型
     *
     * @return 0=未知，1=ethernet(数据网络),2=wifi,3=蜂窝网络，未知代，4=2G,5=蜂窝网络(3G),6=蜂窝网络(4G)
     */
    public static int getNetworkType(Context context) {
        int strNetworkType = 0;

        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = 2;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        // 2G网络
                        strNetworkType = 4;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        // 3G网络
                        strNetworkType = 5;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        // 4G网络
                        strNetworkType = 6;
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName
                                .equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase
                                ("CDMA2000")) {
                            // 联通3G网络
                            strNetworkType = 5;
                        } else {
                            // 未知网络
                            strNetworkType = 0;
                        }
                        break;
                }
            }
        }

        return strNetworkType;
    }


    /**
     * 获取id地址
     *
     * @param context
     * @return
     */
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces
                            (); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                             enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof
                                    Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context
                        .WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 替换科大讯飞中的click_url
     *
     * @param url
     * @param downX
     * @param downY
     * @param upX
     * @param upY
     * @return
     */
    public static String replaceAdClickUrl(String url, float downX, float downY, float upX, float
            upY) {
        url = url.replace("IT_CLK_PNT_DOWN_X", String.valueOf(downX));
        url = url.replace("IT_CLK_PNT_DOWN_Y", String.valueOf(downY));
        url = url.replace("IT_CLK_PNT_UP_X", String.valueOf(upX));
        url = url.replace("IT_CLK_PNT_UP_Y", String.valueOf(upY));
        return url;
    }

}
