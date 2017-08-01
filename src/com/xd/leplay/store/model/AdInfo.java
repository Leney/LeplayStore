package com.xd.leplay.store.model;

import android.text.TextUtils;

import com.xd.download.DownloadInfo;

import java.io.Serializable;

/**
 * 科大讯飞Banner广告类型信息
 * Created by llj on 2017/3/31.
 */
public class AdInfo implements Serializable {
    /**
     * 广告类型--->跳转类型
     */
    public static final String AD_TYPE_REDIRECT = "redirect";
    /**
     * 广告类型--->下载类型
     */
    public static final String AD_TYPE_DOWNLOAD = "download";
    /**
     * 广告类型--->品牌类型
     */
    public static final String AD_TYPE_BRAND = "brand";
    /**
     * 当为跳转类型时==>>标识跳转地址,当为下载类型时==>>下载apk的地址
     */
    private String landingUrl;
    /**
     * 广告来源标记 纯文本说明
     */
    private String adSourceMark;
    /**
     * 点击地址，需要上报用户的点击行为
     */
    private String[] clickUrls;
    /**
     * 图片广告，图片地址
     */
    private String imageUrl;

    /**
     * 图文广告，icon地址
     */
    private String icon;
    /**
     * 图文广告，右边icon地址
     */
    private String rightIconUrl;
    /**
     * 图文广告，标题
     */
    private String title;
    /**
     * 图文广告，描述
     */
    private String subTile;
    /**
     * 图文广告，点击描述
     */
    private String clickText;
    /**
     * 广告类型  redirect=跳转类型，download=下载类型，brand=品牌类型
     */
    private String adType;
    /**
     * 曝光监控数组，广告加载完成,触发上报
     */
    private String[] imprUrls;
    /**
     * 针对下载类广告，需下载应用的包名
     */
    private String packageName;
    /**
     * 针对下载类广告,安装开始上报监控
     */
    private String[] instInstallStartUrls;
    /**
     * 针对下载类广告,安装成功上报监控
     */
    private String[] instInstallSuccUrls;
    /**
     * 针对下载类广告,下载开始上报监控
     */
    private String[] instDownloadStartUrls;
    /**
     * 针对下载类广告,下载成功上报监控
     */
    private String[] instDownloadSuccUrls;
    /**
     * deepLink链接
     */
    private String deepLink;
    /**
     * 用户是否点击了此广告
     */
    private boolean isClick;

    public String getLandingUrl() {
        return landingUrl;
    }

    public void setLandingUrl(String landingUrl) {
        this.landingUrl = landingUrl;
    }

    public String getAdSourceMark() {
        return adSourceMark;
    }

    public void setAdSourceMark(String adSourceMark) {
        this.adSourceMark = adSourceMark;
    }

    public String[] getClickUrls() {
        return clickUrls;
    }

    public void setClickUrls(String[] clickUrls) {
        this.clickUrls = clickUrls;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRightIconUrl() {
        return rightIconUrl;
    }

    public void setRightIconUrl(String rightIconUrl) {
        this.rightIconUrl = rightIconUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTile() {
        return subTile;
    }

    public void setSubTile(String subTile) {
        this.subTile = subTile;
    }

    public String getClickText() {
        return clickText;
    }

    public void setClickText(String clickText) {
        this.clickText = clickText;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String[] getImprUrls() {
        return imprUrls;
    }

    public void setImprUrls(String[] imprUrls) {
        this.imprUrls = imprUrls;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String[] getInstInstallStartUrls() {
        return instInstallStartUrls;
    }

    public void setInstInstallStartUrls(String[] instInstallStartUrls) {
        this.instInstallStartUrls = instInstallStartUrls;
    }

    public String[] getInstInstallSuccUrls() {
        return instInstallSuccUrls;
    }

    public void setInstInstallSuccUrls(String[] instInstallSuccUrls) {
        this.instInstallSuccUrls = instInstallSuccUrls;
    }

    public String[] getInstDownloadStartUrls() {
        return instDownloadStartUrls;
    }

    public void setInstDownloadStartUrls(String[] instDownloadStartUrls) {
        this.instDownloadStartUrls = instDownloadStartUrls;
    }

    public String[] getInstDownloadSuccUrls() {
        return instDownloadSuccUrls;
    }

    public void setInstDownloadSuccUrls(String[] instDownloadSuccUrls) {
        this.instDownloadSuccUrls = instDownloadSuccUrls;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public DownloadInfo toDownloadInfo() {
        DownloadInfo downloadInfo = new DownloadInfo();
        if (!(TextUtils.isEmpty(icon))) {
            downloadInfo.setIconUrl(this.icon);
        } else if (!TextUtils.isEmpty(this.imageUrl)) {
            downloadInfo.setIconUrl(this.imageUrl);
        } else if (!TextUtils.isEmpty(this.rightIconUrl)) {
            downloadInfo.setIconUrl(this.rightIconUrl);
        }
        downloadInfo.setSoftId("-2");
        downloadInfo.setPackageName(this.packageName);
        downloadInfo.setSize(-1);
        downloadInfo.setName("安装包");
        downloadInfo.setUrl(this.landingUrl);
        return downloadInfo;
    }
}
