package com.xd.leplay.store.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.LeplayApplication;
import com.xd.leplay.store.control.ConstantManager;
import com.xd.leplay.store.control.LeplayPreferences;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.control.ReportDownLoadDataManager;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.model.ConstantInfo;
import com.xd.leplay.store.model.GiftInfo;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.model.proto.App.PackInfo;
import com.xd.leplay.store.model.proto.App.ReqDownReport;
import com.xd.leplay.store.model.proto.App.ReqForceUpgrade;
import com.xd.leplay.store.model.proto.App.ReqUpdateDownTimes;
import com.xd.leplay.store.model.proto.App.RspDownReport;
import com.xd.leplay.store.model.proto.App.RspForceUpgrade;
import com.xd.leplay.store.model.proto.App.RspUpdateDownTimes;
import com.xd.leplay.store.model.proto.Packet.ReqPacket;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.GameBag;
import com.xd.leplay.store.model.proto.Uac.LevelInfo;
import com.xd.leplay.store.model.proto.Uac.ReqConstant;
import com.xd.leplay.store.model.proto.Uac.ReqMyGameBag;
import com.xd.leplay.store.model.proto.Uac.ReqSysNotify;
import com.xd.leplay.store.model.proto.Uac.ReqWealth;
import com.xd.leplay.store.model.proto.Uac.RspConstant;
import com.xd.leplay.store.model.proto.Uac.RspMyGameBag;
import com.xd.leplay.store.model.proto.Uac.RspSysNotify;
import com.xd.leplay.store.model.proto.Uac.RspWealth;
import com.xd.leplay.store.model.proto.Uac.SysNotify;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetUtil {
    private static final String TAG = "NetUtil";

    public static boolean doLoadData(String url, String[] actions,
                                     ByteString[] datas, final OnNetResponseLinstener linstener) {
        if (actions.length != datas.length) {
            DLog.e("lilijun", "BaseActivity,action数量必须和data的数量一致!");
            return false;
        }

        // /** 加到请求header里面的(上行)etag值的key */
        // String etagUpHeaderKey = "If-None-Match";
        //
        // /** 加到请求header里面的(下行)etag值的key */
        // final String etagDownHeaderKey = "Etag";
        //
        // /** 加到请求header里面的请求tag的key */
        // final String reqHeaderKey = "reqKey";

        ReqPacket.Builder packetBuilder = ReqPacket.newBuilder();
        // String reqKey = "";
        if (actions.length == 1) {
            packetBuilder.addAction(actions[0]);
            packetBuilder.addParams(datas[0]);
            // reqKey = actions[0];
        } else {
            for (int i = 0; i < actions.length; i++) {
                packetBuilder.addAction(actions[i]);
                packetBuilder.addParams(datas[i]);
                // reqKey += actions[i];
            }
        }
        // reqKey += etagMark;

        packetBuilder.setUdi(DataCollectionConstant.combinationUDI);
        packetBuilder.setClientVer(DataCollectionConstant.versionName);
        packetBuilder.setChannel(DataCollectionConstant.channelNo);
        // String str = packetBuilder.build().toByteString().toString();
        RequestBody body = RequestBody.create(
                MediaType.parse("text/x-markdown; charset=utf-8"),
                packetBuilder.build().toByteArray());

        Request request = null;
        // if (url.equals(Constants.APP_API_URL))
        // {
        // EtagInfo etagInfo = EtagManager.getInstance().getEtagMap()
        // .get(reqKey);
        // if (etagInfo != null)
        // {
        // if (etagInfo.getEtagValue() != null)
        // {
        // request = new Request.Builder()
        // .url(url)
        // .post(body)
        // .tag(actions)
        // .addHeader(etagUpHeaderKey, etagInfo.getEtagValue())
        // .addHeader(reqHeaderKey, reqKey).build();
        // } else
        // {
        // request = new Request.Builder().url(url).post(body)
        // .tag(actions).addHeader(etagUpHeaderKey, "")
        // .addHeader(reqHeaderKey, reqKey).build();
        // }
        // } else
        // {
        // request = new Request.Builder().url(url).post(body)
        // .tag(actions).addHeader(etagUpHeaderKey, "")
        // .addHeader(reqHeaderKey, reqKey).build();
        // }
        // } else
        // {
        // 请求uac地址 header什么都不加
        request = new Request.Builder().url(url).post(body).tag(actions)
                .build();
        // }

        LeplayApplication.getInstance().getHttpClient().newCall(request)
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(final Response response)
                            throws IOException {
                        // 获取请求时发送到服务器的reqKey(这个值是缓存前端数据的唯一标识值)
                        // String reqKey =
                        // response.request().header(reqHeaderKey);
                        DLog.i("lilijun", "返回响应码-------->>" + response.code());
                        // if (response.code() == 304)
                        // {
                        // // 表明没有变化 直接读取本地缓存好的数据
                        // DLog.i(TAG, "加载本地缓存好的etag数据！！");
                        // loadCancleData((String[]) response.request().tag(),
                        // reqKey, linstener);
                        // } else
                        // {
                        if (response.code() == 200) {
                            // 服务端数据有变化
                            byte[] resultBytes = response.body().bytes();
                            final RspPacket rspPacket = RspPacket
                                    .parseFrom(resultBytes);
                            DLog.e("lilijun", "rspPacket.getRescode()------>>>"
                                    + rspPacket.getRescode());
                            if (rspPacket.getRescode() == 0) {
                                DLog.i(TAG, "服务器返回正确的数据成功！");
                                // 获取到请求是url
                                // String requestUrl = response.request()
                                // .urlString();
                                // if (Constants.APP_API_URL.equals(requestUrl))
                                // {
                                // // 只在请求的是APP_API_URL时，才进行缓存etag等数据
                                // // 获取到服务器返回的Etag值
                                // String etagValue = response
                                // .header(etagDownHeaderKey);
                                // if (etagValue != null)
                                // {
                                // DLog.i(TAG, "得到新的Etag数据！！");
                                // EtagInfo etagInfo = new EtagInfo();
                                // etagInfo.setEtagValue(etagValue);
                                // etagInfo.setResponseBodyBytes(resultBytes);
                                // // 将新的返回数据缓存到本地
                                // EtagManager.getInstance().addEtagInfo(
                                // reqKey, etagInfo);
                                // }
                                // }
                                linstener.onLoadSuccess(rspPacket);
                            } else {
                                // if (reqKey != null
                                // && EtagManager.getInstance()
                                // .getEtagMap().get(reqKey) != null)
                                // {
                                // DLog.i(TAG,
                                // "能访问服务器，但服务器本身出现异常，加载本地缓存好的etag数据！！");
                                // loadCancleData((String[]) response
                                // .request().tag(), reqKey, linstener);
                                // } else
                                // {
                                Log.e(TAG, "能访问服务器，但服务器本身出现异常，加载数据失败！  失败码:"
                                        + rspPacket.getRescode());
                                linstener.onLoadFailed(rspPacket);
                                // }
                            }
                        } else {
                            DLog.e(TAG,
                                    "服务器报错，无法响应，返回码---->>>" + response.code());
                            // if (reqKey != null
                            // && EtagManager.getInstance().getEtagMap()
                            // .get(reqKey) != null)
                            // {
                            // DLog.i(TAG, "服务器报错，无法响应,加载本地缓存好的etag数据！！");
                            // loadCancleData((String[]) response.request()
                            // .tag(), reqKey, linstener);
                            // } else
                            // {
                            // 前端自己构造一个RspPacket对象 返回给子类
                            String[] requestTag = (String[]) response.request()
                                    .tag();
                            RspPacket.Builder builder = RspPacket.newBuilder();
                            for (String tag : requestTag) {
                                builder.addAction(tag);
                            }
                            builder.setRescode(-1);
                            builder.setResmsg("resonpse failed, result code = "
                                    + response.code());
                            // 这里rspPacket返回的actions 是请求TAG
                            linstener.onLoadFailed(builder.build());
                            // }
                        }
                        // }
                    }

                    @Override
                    public void onFailure(final Request request, IOException e) {
                        // 访问服务器出错，有可能是路径有问题也有可能是网络连接异常
                        // String requestKey = (String) request
                        // .header(reqHeaderKey);
                        // DLog.i("lilijun", "网络错误,requestKey--------->>>"
                        // + requestKey);
                        // if (requestKey != null
                        // && EtagManager.getInstance().getEtagMap()
                        // .get(requestKey) != null)
                        // {
                        // DLog.i(TAG, "服务器报错，无法响应,加载本地缓存好的etag数据！！");
                        // loadCancleData((String[]) request.tag(),
                        // requestKey, linstener);
                        // } else
                        // {
                        DLog.e(TAG, "访问服务器出错：" + request.toString());
                        linstener.onNetError((String[]) request.tag());

                        // }
                    }
                });
        return true;

    }

    // /**
    // * 加载缓存数据
    // *
    // * @param reqKey
    // */
    // private static void loadCancleData(final String[] tag, String reqKey,
    // OnNetResponseLinstener linstener)
    // {
    // try
    // {
    // final RspPacket rspPacket = RspPacket.parseFrom(EtagManager
    // .getInstance().getEtagMap().get(reqKey)
    // .getResponseBodyBytes());
    // linstener.onLoadSuccess(rspPacket);
    // } catch (Exception e)
    // {
    // DLog.e(TAG, "获取缓存数据发生异常#exception:", e);
    // linstener.onNetError(tag);
    // }
    // }

    public static interface OnNetResponseLinstener {
        /**
         * 加载成功(异步方法)
         */
        void onLoadSuccess(RspPacket rspPacket);

        /**
         * 加载失败(异步方法)
         */
        void onLoadFailed(RspPacket rspPacket);

        /**
         * 网络错误(异步方法)
         */
        void onNetError(String[] tags);
    }

    /**
     * 到服务器获取登录用户的礼包和财富信息
     */
    public static void getLoginUserGiftsAndTreasureData(final Context context) {
        LoginUserInfoManager userInfoManager = LoginUserInfoManager
                .getInstance();
        if (userInfoManager.getLoginedUserInfo() == null) {
            return;
        }
        ReqMyGameBag.Builder giftBuilder = ReqMyGameBag.newBuilder();
        giftBuilder.setUid(userInfoManager.getLoginedUserInfo().getUserId());
        giftBuilder.setUserToken(userInfoManager.getLoginedUserInfo()
                .getUserToken());
        ReqWealth.Builder wealthBuilder = ReqWealth.newBuilder();
        wealthBuilder.setUid(userInfoManager.getLoginedUserInfo().getUserId());
        wealthBuilder.setUserToken(userInfoManager.getLoginedUserInfo()
                .getUserToken());

        NetUtil.doLoadData(Constants.UAC_API_URL, new String[]
                        {"ReqMyGameBag", "ReqWealth"}, new ByteString[]
                        {giftBuilder.build().toByteString(),
                                wealthBuilder.build().toByteString()},
                new OnNetResponseLinstener() {
                    @Override
                    public void onNetError(String[] tags) {
                        DLog.e(TAG, "获取财富信息失败，网络错误！！");
                        // // 获取服务器广播消息
                        // getSystemAndUserMsg(context);
                    }

                    @Override
                    public void onLoadSuccess(RspPacket rspPacket) {

                        List<ByteString> byteStrings = rspPacket
                                .getParamsList();
                        for (int i = 0; i < byteStrings.size(); i++) {
                            String action = rspPacket.getAction(i);
                            if (action.equals("RspMyGameBag")) {
                                // 我的礼包响应TAG
                                RspMyGameBag rspMyGameBag = null;
                                try {
                                    rspMyGameBag = RspMyGameBag
                                            .parseFrom(byteStrings.get(i));
                                } catch (Exception e) {
                                    DLog.e(TAG, "解析礼包数据失败", e);
                                    return;
                                }
                                if (rspMyGameBag == null) {
                                    return;
                                }
                                if (rspMyGameBag.getRescode() == 0) {
                                    DLog.i(TAG, "获取财富信息成功！！");
                                    LoginUserInfoManager userInfoManager = LoginUserInfoManager
                                            .getInstance();
                                    if (userInfoManager.isHaveUserLogin()) {
                                        // 清除之前的礼包信息
                                        userInfoManager.getLoginedUserInfo()
                                                .getGiftList().clear();
                                        // 清除之前的礼包id信息
                                        userInfoManager.getLoginedUserInfo()
                                                .getGiftIdList().clear();

                                        for (GameBag gift : rspMyGameBag
                                                .getGameBagList()) {
                                            // 添加新获取到的礼包信息
                                            GiftInfo giftInfo = new GiftInfo();
                                            giftInfo.setId(gift.getBagId());
                                            giftInfo.setName(gift.getBagName());
                                            giftInfo.setEndTime(gift
                                                    .getEndTime());
                                            giftInfo.setStartTime(gift
                                                    .getStartTime());
                                            giftInfo.setCode(gift
                                                    .getRedeemCode());
                                            giftInfo.setGameIconUrl(gift
                                                    .getGameIconUrl());
                                            giftInfo.setGameId(gift.getGameId());
                                            giftInfo.setGameName(gift
                                                    .getGameName());

                                            userInfoManager
                                                    .getLoginedUserInfo()
                                                    .getGiftList()
                                                    .put(giftInfo.getId(),
                                                            giftInfo);
                                            // 将礼包的ID提取出来 然后保存到一个专门的礼包ID集合中去
                                            userInfoManager
                                                    .getLoginedUserInfo()
                                                    .getGiftIdList()
                                                    .add(giftInfo.getId());
                                        }
                                        DLog.i("lilijun", "用户我的礼包条数："
                                                + userInfoManager
                                                .getLoginedUserInfo()
                                                .getGiftList().size());

                                    }
                                } else if (rspMyGameBag.getRescode() == 2) {
                                    // userToken错误
                                    // 退出登录
                                    LoginUserInfoManager.getInstance()
                                            .exitLogin();
                                } else if (rspMyGameBag.getRescode() == 3) {
                                    // 没有礼包数据
                                } else {
                                    DLog.e(TAG, "获取账户礼包数据时，服务器出现异常#Exception:"
                                            + rspMyGameBag.getResmsg());
                                }
                            } else if (action.equals("RspWealth")) {
                                // 解析财富信息
                                RspWealth rspWealth = null;
                                try {
                                    rspWealth = RspWealth.parseFrom(rspPacket
                                            .getParams(i));
                                } catch (InvalidProtocolBufferException e) {
                                    DLog.e(TAG, "解析财富信息失败！", e);
                                    return;
                                }
                                if (rspWealth == null) {
                                    return;
                                }

                                // 0=成功,1=系统响应错误,2=user_token错误,3=用户不存在或已经被禁用
                                if (rspWealth.getRescode() == 0) {
                                    // 解析财富数据
                                    parseTreasureResult(rspWealth, context);
                                } else if (rspWealth.getRescode() == 2) {
                                    DLog.e(TAG, "获取财富信息userToken错误！！");
                                    // userToken错误
                                    // 退出帐号
                                    LoginUserInfoManager.getInstance()
                                            .exitLogin();
                                } else {
                                    // 获取财富信息失败
                                    DLog.e(TAG,
                                            "获取财富信息失败！！--->"
                                                    + rspWealth.getResmsg()
                                                    + "--->>"
                                                    + rspWealth.getRescode());
                                }
                            }
                        }
                        // // 将登录的账户信息保存到缓存中去
                        // ToolsUtil.saveCachDataToFile(context,
                        // Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME,
                        // LoginUserInfoManager.getInstance()
                        // .getLoginedUserInfo());

                        // // 发送用户信息改变广播
                        // Intent intent = new Intent(
                        // Constants.ACTION_ACCOUNT_HAVE_MODIFY);
                        // context.sendBroadcast(intent);
                    }

                    @Override
                    public void onLoadFailed(RspPacket rspPacket) {
                        DLog.e("lilijun",
                                "获取财富信息整体失败！！code--->>>"
                                        + rspPacket.getRescode()
                                        + "   errorMsg-------->>>"
                                        + rspPacket.getResmsg());
                        // // 获取服务器广播消息
                        // getSystemAndUserMsg(context);

                    }
                });
    }

    /**
     * 解析财富返回结果
     *
     * @return
     */
    private static void parseTreasureResult(RspWealth rspWealth, Context context) {
        try {
            // 成功
            LoginUserInfoManager loginUserInfoManager = LoginUserInfoManager
                    .getInstance();
            if (loginUserInfoManager.getLoginedUserInfo() == null
                    || loginUserInfoManager.getLoginedUserInfo()
                    .getTreasureInfo() == null) {
                return;
            }
            LoginedUserInfo newUserInfo = new LoginedUserInfo();
            // 将其他的用户信息赋值给新的LoginedUserInfo对象中
            LoginUserInfoManager.getInstance().setOldInfoToNewInfo(newUserInfo);
            newUserInfo.getTreasureInfo().setTodayCoins(
                    rspWealth.getTodayCornTotal());
            newUserInfo.getTreasureInfo().setCoinNum(rspWealth.getCornTotal());
            newUserInfo.getTreasureInfo().setInviteCoins(
                    rspWealth.getInviteCornTotal());
            newUserInfo.getTreasureInfo().setInviteFriendCount(
                    rspWealth.getFriendCount());
            newUserInfo.getTreasureInfo().setInviteCode(
                    rspWealth.getInviteCode());
            // 我的等级称号
            newUserInfo.getTreasureInfo().setLevalName(
                    rspWealth.getMyLevelName());
            // 我的等级称号所对应的数字编号
            newUserInfo.getTreasureInfo().setLevalNo(rspWealth.getMyLevelNo());
            // 已提现的金额
            newUserInfo.getTreasureInfo().setWithdrawMoney(
                    rspWealth.getWithdrawingAmount());
            // 邀请的好友提现返利总金额数
            newUserInfo.getTreasureInfo().setFriendBackMoney(
                    rspWealth.getFriendWithdrawing());
            // 任务下载收入总和
            newUserInfo.getTreasureInfo().setDownloadMoney(
                    rspWealth.getDownTaskSumAmount());
            // 签到抽奖收入总和
            newUserInfo.getTreasureInfo().setSignAndLotteryMoney(
                    rspWealth.getSignRaffleSumAmout());
            // 邀请奖励收入总和
            newUserInfo.getTreasureInfo().setInviteMoney(
                    rspWealth.getInviteAmount());
            // 其他奖励收入总和
            newUserInfo.getTreasureInfo().setOtherMoney(
                    rspWealth.getOtherAmount());
            // 微信openId
            newUserInfo.setOpenId(rspWealth.getWxOpenId());
            // 微信unionId
            newUserInfo.setWxUnionId(rspWealth.getWxUnionId());
            if (rspWealth.getNotifyList() != null
                    && !rspWealth.getNotifyList().isEmpty()) {
                int msgNum = 0;
                List<SysNotify> list = new ArrayList<SysNotify>();
                for (SysNotify notify : rspWealth.getNotifyList()) {
                    if (notify.getStatus() == 0) {
                        // 未读
                        msgNum++;
                    }
                    list.add(notify);
                    DLog.i("lilijun", "消息---->>>" + notify.getMessage());
                }
                // 设置有未读消息
                newUserInfo.setUnreadMsgNum(msgNum);
                newUserInfo.getUserMsgList().clear();
                newUserInfo.getUserMsgList().addAll(list);
            }
            LoginUserInfoManager.getInstance().setLoginedUserInfo(newUserInfo);

            DLog.i("lilijun", "金币总数---->>>" + rspWealth.getCornTotal());
            DLog.i("lilijun",
                    "邀请的好友提现奖励总金额数---->>>" + rspWealth.getFriendWithdrawing());
            DLog.i("lilijun",
                    "财富信息，任务下载收入---->>>" + rspWealth.getDownTaskSumAmount());
            DLog.i("lilijun",
                    "财富信息，签到抽奖收入---->>>" + rspWealth.getSignRaffleSumAmout());
            DLog.i("lilijun",
                    "财富信息，邀请奖励收入---->>>" + rspWealth.getInviteAmount());
            DLog.i("lilijun", "财富信息，其他奖励收入---->>>" + rspWealth.getOtherAmount());
            DLog.i("lilijun",
                    "财富信息，微信登录的openId---->>>" + rspWealth.getWxOpenId());

            // 将登录的账户信息保存到缓存中去
            ToolsUtil.saveCachDataToFile(context,
                    Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME,
                    LoginUserInfoManager.getInstance().getLoginedUserInfo());

            // 发送用户信息改变广播
            Intent intent = new Intent(Constants.ACTION_ACCOUNT_HAVE_MODIFY);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            DLog.e(TAG, "解析财富数据异常", e);
            // 退出帐号
            LoginUserInfoManager.getInstance().exitLogin();
        }
    }

    /**
     * 获取公告信息
     *
     */
    public static void getSystemAndUserMsg(final Context context) {
        ReqSysNotify.Builder builder = ReqSysNotify.newBuilder();
        doLoadData(Constants.UAC_API_URL, new String[]
                {"ReqSysNotify"}, new ByteString[]
                {builder.build().toByteString()}, new OnNetResponseLinstener() {

            @Override
            public void onNetError(String[] tags) {
                DLog.e("lilijun", "获取公告和用户信息失败,网络错误");
            }

            @Override
            public void onLoadSuccess(RspPacket rspPacket) {
                RspSysNotify reSysNotify = null;
                try {
                    reSysNotify = RspSysNotify.parseFrom(rspPacket.getParams(0));
                } catch (Exception e) {
                    DLog.e(TAG, "解析公告信息失败！！！", e);
                    return;
                }
                if (reSysNotify == null) {
                    return;
                }
                if (reSysNotify.getRescode() == 0) {
                    DLog.i("lilijun", "获取公告和用户信息成功");
                    List<SysNotify> notifies = reSysNotify.getNotifyList();
                    if (notifies != null && !notifies.isEmpty()) {
                        StringBuilder msgAll = new StringBuilder();
                        for (SysNotify notify : notifies) {
                            // 组合公告消息
                            msgAll.append(notify.getMessage());
                            msgAll.append("    ");
                        }

                        // 将获取到的公告信息设置到配置文件中去
                        LeplayPreferences.getInstance(context).setSystemMsg(
                                msgAll.toString());
                        context.sendBroadcast(new Intent(
                                Constants.ACTION_GET_SYSTEM_MSG_FINISH));
                    }
                } else {
                    DLog.e("lilijun",
                            "获取公告信息失败，msg---->>>" + reSysNotify.getResmsg()
                                    + "  code---->>>"
                                    + reSysNotify.getRescode());
                }
            }

            @Override
            public void onLoadFailed(RspPacket rspPacket) {
                DLog.e("lilijun", "获取公告信息失败 rspPacket.getRescode()----->>"
                        + rspPacket.getRescode());
            }
        });
    }

    /**
     * 获取常量信息
     *
     * @param context
     */
    public static void getConstantDataFromNet(final Context context) {
        ReqConstant.Builder builder = ReqConstant.newBuilder();
        doLoadData(Constants.UAC_API_URL, new String[]
                {"ReqConstant"}, new ByteString[]
                {builder.build().toByteString()}, new OnNetResponseLinstener() {
            @Override
            public void onNetError(String[] tags) {
            }

            @Override
            public void onLoadSuccess(RspPacket rspPacket) {
                RspConstant rspConstant = null;
                try {
                    rspConstant = RspConstant.parseFrom(rspPacket.getParams(0));
                } catch (Exception e) {
                    DLog.e(TAG, "解析从服务器返回的常量信息出现异常#excption:", e);
                }
                if (rspConstant.getRescode() == 0) {
                    DLog.i("lilijun", "获取常量信息成功");
                    ConstantInfo constantInfo = new ConstantInfo();
                    constantInfo.setInviteFriendCoin(rspConstant
                            .getInviteRewardCoin());
                    constantInfo.setInviteMaxFriendCount(rspConstant
                            .getInviteExtraCount());
                    constantInfo.setInviteMaxFriendCoin(rspConstant
                            .getInviteExtraReward());
                    constantInfo.setServiceTelephone(rspConstant
                            .getServiceTelNo());
                    constantInfo.setQqGroupNo(rspConstant.getQqGroupNo());
                    constantInfo.setWeixinPublicAccount(rspConstant
                            .getWeixinPublicNo());
                    constantInfo.setCompanyEmail(rspConstant.getCompanyEmail());
                    constantInfo.setExchangeRate(rspConstant.getExchangeRate());
                    constantInfo.setFirstRegisterCoin(rspConstant
                            .getRegisterRewardCorn());
                    constantInfo.setSign7daysCoins(rspConstant
                            .getSign7DayRewardCoin());
                    constantInfo.setFriendBackRate(rspConstant
                            .getFriendWithdrawingRate());
                    DLog.i("lilijun",
                            "好友提现利率值------->>>"
                                    + rspConstant.getFriendWithdrawingRate());
                    constantInfo.setDesKey(rspConstant.getDesKey());
                    constantInfo.setIsDeviceRegisted(rspConstant
                            .getDeviceRegisted());
                    DLog.i("lilijun", "deviceRegisted---------->>>>>>"
                            + rspConstant.getDeviceRegisted());
                    constantInfo.setIsExistRedPacket(rspConstant
                            .getHasRedPack());
                    DLog.i("lilijun", "isHasRedPacket---------->>>>>>>"
                            + rspConstant.getHasRedPack());
                    for (LevelInfo levelInfo : rspConstant.getLevelInfoList()) {
                        constantInfo.getLevelInfos().put(
                                levelInfo.getLevelNo(), levelInfo);
                    }
                    ConstantManager.getInstance().setConstantInfo(constantInfo);
                    // 保存至缓存文件
                    ToolsUtil.saveCachDataToFile(context,
                            Constants.CONSTANT_INFO_CANCHE_FILE_NAME,
                            ConstantManager.getInstance().getConstantInfo());

                } else {
                    Log.e("lilijun", "获取常量信息失败 resultCode------>>>"
                            + rspConstant.getRescode());
                }
            }

            @Override
            public void onLoadFailed(RspPacket rspPacket) {
            }
        });
    }

    /**
     * 更新app下载次数
     *
     * @param softId
     */
    public static void updateDownloadCount(long softId) {
        ReqUpdateDownTimes.Builder builder = ReqUpdateDownTimes.newBuilder();
        builder.setAppId(softId);

        doLoadData(Constants.APP_API_URL, new String[]
                {"ReqUpdateDownTimes"}, new ByteString[]
                {builder.build().toByteString()}, new OnNetResponseLinstener() {

            @Override
            public void onNetError(String[] tags) {
                DLog.e("lilijun", "更新下载次数失败！！！");
            }

            @Override
            public void onLoadSuccess(RspPacket rspPacket) {
                RspUpdateDownTimes rspUpdateDownTimes = null;
                try {
                    rspUpdateDownTimes = RspUpdateDownTimes.parseFrom(rspPacket
                            .getParams(0));
                } catch (Exception e) {
                    DLog.e(TAG, "解析更新下载次数返回数据失败#excption:", e);
                    return;
                }
                if (rspUpdateDownTimes == null) {
                    return;
                }
                if (rspUpdateDownTimes.getRescode() == 0) {
                    DLog.i("lilijun", "更新下载次数成功！msg------->>>"
                            + rspUpdateDownTimes.getResmsg());
                } else {
                    DLog.e("lilijun", "更新下载次数不成功！msg---->>>"
                            + rspUpdateDownTimes.getResmsg() + "  code---->>>"
                            + rspUpdateDownTimes.getRescode());
                }
            }

            @Override
            public void onLoadFailed(RspPacket rspPacket) {
                DLog.e("lilijun", "更新下载次数失败！！！");
            }
        });
    }

    /**
     * 下载安装完成之后 上报数据
     *
     * @param packInfo 可以传null值，当为null时 标识上报所有之前未上报成功的数据
     */
    public static void reportDownloadDataToNet(final Context context,
                                               final PackInfo packInfo) {
        LoginUserInfoManager userInfoManager = LoginUserInfoManager
                .getInstance();
        if (!userInfoManager.isHaveUserLogin()) {
            return;
        }
        if (packInfo == null) {
            if (ReportDownLoadDataManager.getInstance().isEmptyData()) {
                return;
            }
        }
        ReqDownReport.Builder reportBuilder = ReqDownReport.newBuilder();
        reportBuilder.setUid(userInfoManager.getLoginedUserInfo().getUserId());
        reportBuilder.setUserToken(userInfoManager.getLoginedUserInfo()
                .getUserToken());
        if (packInfo == null) {
            DLog.i("lilijun", "上报所有数据  downPackInfo.size()------>>>"
                    + ReportDownLoadDataManager.getInstance().getPackInfos()
                    .size());
            // 表示上报所有未上报的数据
            for (PackInfo downPackInfo : ReportDownLoadDataManager
                    .getInstance().getPackInfos()) {
                DLog.i("lilijun", "添加一条上报数据到请求参数里面-packId---->>>"
                        + downPackInfo.getPackId());
                reportBuilder.addPackInfo(downPackInfo);
            }
        } else {
            // 表示只单独上报一条数据
            DLog.i("lilijun", "只上报单个数据 ！！");
            reportBuilder.addPackInfo(packInfo);
        }
        doLoadData(Constants.APP_API_URL, new String[]
                {"ReqDownReport"}, new ByteString[]
                {reportBuilder.build().toByteString()}, new OnNetResponseLinstener() {
            @Override
            public void onNetError(String[] tags) {
            }

            @Override
            public void onLoadSuccess(RspPacket rspPacket) {
                RspDownReport rspDownReport = null;
                try {
                    rspDownReport = RspDownReport.parseFrom(rspPacket
                            .getParams(0));
                } catch (Exception e) {
                    DLog.e(TAG, "解析上报下载信息返回数据时失败#excption:", e);
                    return;
                }
                if (rspDownReport == null) {
                    return;
                }
                // 0=成功，1=系统响应错误，2=token错误
                if (rspDownReport.getRescode() == 0) {
                    // 提交数据成功
                    if (packInfo == null) {
                        DLog.i("lilijun", "上报所有数据成功！！！msg---->>>"
                                + rspDownReport.getResmsg());
                        // 上报所有的数据成功
                        // 清空之前缓存的所有的未上报数据
                        ReportDownLoadDataManager.getInstance().clearAllData();
                        // 去获取一次用户的财富信息
                        getLoginUserGiftsAndTreasureData(context);
                    } else {
                        DLog.i("lilijun", "上报单个数据成功！！！msg---->>>"
                                + rspDownReport.getResmsg());
                        // 上报单条数据成功
                        // 去获取一次用户的财富信息
                        getLoginUserGiftsAndTreasureData(context);
                    }
                } else {
                    // 没有提交数据成功
                    if (packInfo == null) {
                        // 上报所有的数据失败 暂不做任何操作
                        DLog.e("lilijun", "上报所有数据失败！！！  code-------->>>"
                                + rspDownReport.getRescode()
                                + "\n 总条数--->>"
                                + ReportDownLoadDataManager.getInstance()
                                .getPackInfos().size());
                    } else {
                        // 上报单条数据失败
                        // 将上传失败的数据保存下来
                        ReportDownLoadDataManager.getInstance().addPackInfo(
                                packInfo);
                        ToolsUtil
                                .saveCachDataToFile(
                                        context,
                                        Constants
                                                .DOWNLOAD_INSTALL_SUCCESS_APP_INFO_CANCHE_FILE_NAME,
                                        ReportDownLoadDataManager.getInstance()
                                                .getPackInfos());
                        DLog.e("lilijun",
                                "上报单个数据失败,保存上报失败的数据！ \n packId----->>"
                                        + packInfo.getPackId() + "\n msg--->> "
                                        + rspDownReport.getResmsg()
                                        + "\n code-------->>>"
                                        + rspDownReport.getRescode());
                    }
                }
            }

            @Override
            public void onLoadFailed(RspPacket rspPacket) {
                if (packInfo != null) {
                    DLog.e("lilijun", "reportDownloadData,上报单个数据整体失败了！！\n "
                            + rspPacket.getResmsg() + "\n code---->>"
                            + rspPacket.getRescode());
                    // 将上传失败的数据保存下来
                    ReportDownLoadDataManager.getInstance().addPackInfo(
                            packInfo);
                    ToolsUtil
                            .saveCachDataToFile(
                                    context,
                                    Constants.DOWNLOAD_INSTALL_SUCCESS_APP_INFO_CANCHE_FILE_NAME,
                                    ReportDownLoadDataManager.getInstance()
                                            .getPackInfos());
                } else {
                    DLog.e("lilijun", "reportDownloadData,上报所有数据整体失败了！！\n "
                            + rspPacket.getResmsg() + "\n code---->>"
                            + rspPacket.getRescode());
                }
            }
        });
    }

    /**
     * 获取版本是否强制升级
     */
    public static void getVersionForceState(final Context context) {
        ReqForceUpgrade.Builder builder = ReqForceUpgrade.newBuilder();
        builder.setPackName(context.getPackageName());
        builder.setVersionCode(DataCollectionConstant.versionCode);
        DLog.e("lilijun",
                "获取版本是否强制升级，packageName----->>" + context.getPackageName());
        DLog.e("lilijun", "获取版本是否强制升级，versionCode----->>"
                + DataCollectionConstant.versionCode);
        doLoadData(Constants.APP_API_URL, new String[]
                {"ReqForceUpgrade"}, new ByteString[]
                {builder.build().toByteString()}, new OnNetResponseLinstener() {
            @Override
            public void onNetError(String[] tags) {
                DLog.e(TAG, "获取是否强制升级失败，网络错误！");
            }

            @Override
            public void onLoadSuccess(RspPacket rspPacket) {
                RspForceUpgrade rspForceUpgrade = null;
                try {
                    rspForceUpgrade = RspForceUpgrade.parseFrom(rspPacket
                            .getParams(0));
                } catch (Exception e) {
                    DLog.e(TAG, "解析获取强制升级状态时失败#excption:", e);
                    return;
                }
                if (rspForceUpgrade == null) {
                    return;
                }
                DLog.e("lilijun",
                        "获取是否强制升级状态------->>" + rspForceUpgrade.getRescode()
                                + "---msg---->>>" + rspForceUpgrade.getResmsg());
                if (rspForceUpgrade.getRescode() == 0) {
                    // 需强制升级
                    DownloadInfo downloadInfo = new DownloadInfo();
                    downloadInfo.setUrl(rspForceUpgrade.getDownloadUrl());
                    downloadInfo.setIconUrl(rspForceUpgrade.getIconUrl());
                    downloadInfo.setIntegral(rspForceUpgrade.getCoin());
                    downloadInfo.setSoftId(rspForceUpgrade.getAppId() + "");
                    downloadInfo.setName(rspForceUpgrade.getAppName());
                    downloadInfo.setPackageId(rspForceUpgrade.getPackId());
                    downloadInfo.setSize(rspForceUpgrade.getPackSize());
                    downloadInfo.setPackageName(rspForceUpgrade.getPackName());
                    downloadInfo.setUpdateVersionCode(rspForceUpgrade
                            .getVersionCode());
                    SoftwareManager.getInstance().setForceDownloadInfo(
                            downloadInfo);
                    context.sendBroadcast(new Intent(
                            Constants.ACTION_GET_FORCE_UPGREAD_RESULT));
                }
            }

            @Override
            public void onLoadFailed(RspPacket rspPacket) {
                DLog.e(TAG,
                        "获取是否强制升级失败-----code---->>>" + rspPacket.getRescode()
                                + " 错误消息-------->>" + rspPacket.getResmsg());
            }
        });
    }


    /**
     * 科大讯飞广告api请求方法
     *
     * @param url
     * @param listener
     * @return
     */
    public static boolean doRequestByAds(String url, Map<String, Object> params, final
    OnAdsResponseListener listener) {
        if (params == null) {
            params = new HashMap<>();
        }

        JSONObject requestBodyJson = new JSONObject();
        try {
            if (!params.isEmpty()) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    // 将传过来的参数组进来
                    requestBodyJson.put(entry.getKey(), entry.getValue());
                }
            }
            requestBodyJson.put("tramaterialtype", "json");
            // 是否支持deepLink 0=不支持，1=支持
            requestBodyJson.put("is_support_deeplink", 1);
            // 设备类型 -1=未知，0=phone,1=pad,2=pc,3=tv, 4=wap
            requestBodyJson.put("devicetype", 0);
            // 操作系统类型
            requestBodyJson.put("os", "Android");
            // 操作系统版本号
            requestBodyJson.put("osv", DataCollectionConstant.androidVersionName);
            requestBodyJson.put("adid", DataCollectionConstant.androidId);
            requestBodyJson.put("imei", DataCollectionConstant.imei);
            requestBodyJson.put("mac", DataCollectionConstant.macAdress);
            requestBodyJson.put("density", DataCollectionConstant.density);
            requestBodyJson.put("operator", DataCollectionConstant.imsiCode);
            // userAgent
            requestBodyJson.put("ua", DataCollectionConstant.userAgent);
            requestBodyJson.put("ts", System.currentTimeMillis());
            // 设备屏幕宽度
            requestBodyJson.put("dvw", DataCollectionConstant.screenWidth);
            // 设备屏幕高度
            requestBodyJson.put("dvh", DataCollectionConstant.screenHeight);
            // 横竖屏 0=竖屏，1=横屏
            requestBodyJson.put("orientation", 0);
            // 设备生产商
            requestBodyJson.put("vendor", DataCollectionConstant.manuFacturer);
            // 设备型号
            requestBodyJson.put("model", DataCollectionConstant.model);
            // 使用语言
            requestBodyJson.put("lan", "zh-CN");
//            // 是否开屏 1=开屏，0=非开屏
//            requestBodyJson.put("isboot", 1);
            // 请求批量下发广告的数量，目前只能为”1”
            requestBodyJson.put("batch_cnt", "1");
            // appId 和讯飞后台保持一致
            requestBodyJson.put("appid", Constants.KDXF_APP_ID);
            // app名称 和讯飞后台保持一致
            requestBodyJson.put("appname", "爱玩商店");
            // app包名 和讯飞后台保持一致
            requestBodyJson.put("pkgname", "com.xd.leplay.store");

//            JSONObject debugObject = new JSONObject();
//            //用于指定下发广告的交互类型，取值范围： 0，不限制；1，跳转类； 2，下载类。不指定的话，按值为 0 处理
//            debugObject.put("action_type", 0);
//            // 用于指定下发广告的落地页类型，
//            // 0=不限制,1=包含landing_url和deep_link,2=仅包含landing_url,3=仅包含deep_link,默认0
//            debugObject.put("landing_type", 3);
//            requestBodyJson.put("debug", debugObject);

            DLog.i("llj", "请求参数--->>>" + requestBodyJson.toString());

        } catch (Exception e) {
            DLog.e(TAG, "请求广告数据出现异常#Exception\n", e);
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("text/x-markdown; charset=utf-8"),
                requestBodyJson.toString().getBytes());


        Request request = new Request.Builder().url(url).post(body).addHeader
                ("X-protocol-ver", "2.0").addHeader("Content-Type","application/json").build();


        LeplayApplication.getInstance().getHttpClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onResponse(final Response response)
                            throws IOException {
                        // 获取请求时发送到服务器的reqKey(这个值是缓存前端数据的唯一标识值)
                        // String reqKey =
                        // response.request().header(reqHeaderKey);
                        DLog.i("llj", "返回响应码-------->>" + response.code());
                        String result = response.body().string();
                        DLog.i("llj", "返回结果------>>>" + result);
                        if (response.code() == 200) {
                            // 服务端数据有变化
                            try {
                                JSONObject resultObject = new JSONObject(result);
                                if (resultObject.getInt("rc") == 70200) {
                                    // 请求广告成功、下发广告成功
                                    listener.onLoadSuccess(resultObject);
                                } else {
                                    // 连接到服务器成功，但出现一些错误，下发广告失败
                                    listener.onLoadFailed(result);
                                }
                            } catch (Exception e) {
                                DLog.e(TAG, "返回成功之后，解析数据出现异常#exception:\n", e);
                            }
                        } else {
                            DLog.e(TAG,
                                    "服务器报错，无法响应，返回码---->>>" + response.code());
                            // 前端自己构造一个RspPacket对象 返回给子类
                            String[] requestTag = (String[]) response.request()
                                    .tag();
                            // 这里rspPacket返回的actions 是请求TAG
                            listener.onLoadFailed(result);
                        }
                    }

                    @Override
                    public void onFailure(final Request request, IOException e) {
                        // 访问服务器出错，有可能是路径有问题也有可能是网络连接异常
                        DLog.e(TAG, "访问服务器出错：" + request.toString());
                        listener.onNetError("网络异常");
                    }
                });
        return true;
    }

//    /**
//     * 自己后台服务器api请求方法
//     *
//     * @param url
//     * @param listener
//     * @return
//     */
//    public static boolean doRequest(String url, Map<String, Object> params, final
//    OnAdsResponseListener listener) {
//        if (params == null) {
//            params = new HashMap<>();
//        }
//
//        JSONObject requestBodyJson = new JSONObject();
//        try {
//            if (!params.isEmpty()) {
//                for (Map.Entry<String, Object> entry : params.entrySet()) {
//                    // 将传过来的参数组进来
//                    requestBodyJson.put(entry.getKey(), entry.getValue());
//                }
//            }
//            requestBodyJson.put("tramaterialtype", "json");
//            // 是否支持deepLink 0=不支持，1=支持
//            requestBodyJson.put("is_support_deeplink", 1);
//            // 设备类型 -1=未知，0=phone,1=pad,2=pc,3=tv, 4=wap
//            requestBodyJson.put("devicetype", 0);
//            // 操作系统类型
//            requestBodyJson.put("os", "Android");
//            // 操作系统版本号
//            requestBodyJson.put("osv", DataCollectionConstant.androidVersionName);
//            requestBodyJson.put("adid", DataCollectionConstant.androidId);
//            requestBodyJson.put("imei", DataCollectionConstant.imei);
//            requestBodyJson.put("mac", DataCollectionConstant.macAdress);
//            requestBodyJson.put("density", DataCollectionConstant.density);
//            requestBodyJson.put("operator", DataCollectionConstant.imsiCode);
//            // userAgent
//            requestBodyJson.put("ua", DataCollectionConstant.userAgent);
//            requestBodyJson.put("ts", System.currentTimeMillis());
//            // 设备屏幕宽度
//            requestBodyJson.put("dvw", DataCollectionConstant.screenWidth);
//            // 设备屏幕高度
//            requestBodyJson.put("dvh", DataCollectionConstant.screenHeight);
//            // 横竖屏 0=竖屏，1=横屏
//            requestBodyJson.put("orientation", 0);
//            // 设备生产商
//            requestBodyJson.put("vendor", DataCollectionConstant.manuFacturer);
//            // 设备型号
//            requestBodyJson.put("model", DataCollectionConstant.model);
//            // 使用语言
//            requestBodyJson.put("lan", "zh-CN");
//            // 请求批量下发广告的数量，目前只能为”1”
//            requestBodyJson.put("batch_cnt", "1");
//            // appId 和讯飞后台保持一致
//            requestBodyJson.put("appid", Constants.KDXF_APP_ID);
//            // app名称 和讯飞后台保持一致
//            requestBodyJson.put("appname", "爱玩商店");
//            // app包名 和讯飞后台保持一致
//            requestBodyJson.put("pkgname", "com.xd.leplay.store");
//
////            JSONObject debugObject = new JSONObject();
////            //用于指定下发广告的交互类型，取值范围： 0，不限制；1，跳转类； 2，下载类。不指定的话，按值为 0 处理
////            debugObject.put("action_type", 0);
////            // 用于指定下发广告的落地页类型，
////            // 0=不限制,1=包含landing_url和deep_link,2=仅包含landing_url,3=仅包含deep_link,默认0
////            debugObject.put("landing_type", 3);
////            requestBodyJson.put("debug", debugObject);
//
//            DLog.i("llj", "请求参数--->>>" + requestBodyJson.toString());
//
//        } catch (Exception e) {
//            DLog.e(TAG, "请求广告数据出现异常#Exception\n", e);
//        }
//
//        RequestBody body = RequestBody.create(
//                MediaType.parse("text/x-markdown; charset=utf-8"),
//                requestBodyJson.toString().getBytes());
//
//
//        Request request = new Request.Builder().url(url).post(body).addHeader
//                ("X-protocol-ver", "2.0").build();
//
//
//        LeplayApplication.getInstance().getHttpClient().newCall(request)
//                .enqueue(new Callback() {
//                    @Override
//                    public void onResponse(final Response response)
//                            throws IOException {
//                        // 获取请求时发送到服务器的reqKey(这个值是缓存前端数据的唯一标识值)
//                        // String reqKey =
//                        // response.request().header(reqHeaderKey);
//                        DLog.i("llj", "返回响应码-------->>" + response.code());
//                        String result = response.body().string();
//                        DLog.i("llj", "返回结果------>>>" + result);
//                        if (response.code() == 200) {
//                            // 服务端数据有变化
//                            try {
//                                JSONObject resultObject = new JSONObject(result);
//                                if (resultObject.getInt("rc") == 70200) {
//                                    // 请求广告成功、下发广告成功
//                                    listener.onLoadSuccess(resultObject);
//                                } else {
//                                    // 连接到服务器成功，但出现一些错误，下发广告失败
//                                    listener.onLoadFailed(result);
//                                }
//                            } catch (Exception e) {
//                                DLog.e(TAG, "返回成功之后，解析数据出现异常#exception:\n", e);
//                            }
//                        } else {
//                            DLog.e(TAG,
//                                    "服务器报错，无法响应，返回码---->>>" + response.code());
//                            // 前端自己构造一个RspPacket对象 返回给子类
////                            String[] requestTag = (String[]) response.request()
////                                    .tag();
//                            // 这里rspPacket返回的actions 是请求TAG
//                            listener.onLoadFailed(result);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(final Request request, IOException e) {
//                        // 访问服务器出错，有可能是路径有问题也有可能是网络连接异常
//                        DLog.e(TAG, "访问服务器出错：" + request.toString());
//                        listener.onNetError("网络异常");
//                    }
//                });
//        return true;
//    }


    public interface OnAdsResponseListener {
        /**
         * 加载成功(异步方法)
         */
        void onLoadSuccess(JSONObject resultObject);

        /**
         * 加载失败(异步方法)
         */
        void onLoadFailed(String msg);

        /**
         * 网络错误(异步方法)
         */
        void onNetError(String msg);
    }

    /**
     * 只是请求一个链接
     *
     * @param url
     */
    public static void requestUrl(String url, Callback callback) {
        RequestBody body = RequestBody.create(
                MediaType.parse("text/x-markdown; charset=utf-8"), "");
        Request request = new Request.Builder().url(url).post(body).addHeader
                ("X-protocol-ver", "2.0").build();
        LeplayApplication.getInstance().getHttpClient().newCall(request)
                .enqueue(callback);
    }

    /**
     * 一次需要请求多个url链接
     *
     * @param urls
     */
    public static void requestUrls(String[] urls, final OnRequestMoreListener listener) {
        if(urls == null){
            return;
        }
        final int length = urls.length;
        DLog.i("llj", "requestUrls,总长度---length--->>>" + length);
        final Integer[] times = {0};
        for (int i = 0; i < length; i++) {
            NetUtil.requestUrl(urls[i], new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    synchronized (times[0]) {
                        times[0]++;
                        DLog.i("llj", "请求多个Url有失败的情况----->>>" + times[0]);
                        if (times[0] == length) {
                            // 如果所有请求都回调了
                            // 只要有一次到这里，就是整个多个请求失败了 只是成功了部分也是失败
                            if (listener != null) {
                                listener.onError();
                            }
                        }
                    }
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    synchronized (times[0]) {
                        times[0]++;
                        DLog.i("llj", "requestUrls,网络连接成功  times[0]----->>>" + times[0]);
                        if (times[0] == length) {
                            // 如果所有请求都回调了
                            // 全部请求都成功了
                            DLog.i("llj","全部url请求都成功了!!");
                            if (listener != null) {
                                listener.onSuccess();
                            }
                        }
                    }
                }
            });
        }
    }


//    /**
//     * 只是请求一个链接
//     *
//     * @param url
//     */
//    public static void requestUrl2(String url,JSONObject requestBodyJson, Callback callback) {
//        RequestBody body = RequestBody.create(
//                MediaType.parse("text/x-markdown; charset=utf-8"),
//                requestBodyJson.toString().getBytes());
////        Request request = new Request.Builder().url(url).post(body).addHeader
////                ("X-protocol-ver", "2.0").build();
//        Request request = new Request.Builder().url(url).post(body).build();
//        LeplayApplication.getInstance().getHttpClient().newCall(request)
//                .enqueue(callback);
//    }

    public interface OnRequestMoreListener {
        void onSuccess();

        void onError();
    }


}
