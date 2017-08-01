package com.xd.leplay.store.control;

import android.text.TextUtils;

import com.xd.leplay.store.model.AdInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 下载类型的AdInfo管理器
 * Created by llj on 2017/3/31.
 */
public class DownloadAdInfoManager {
    private static DownloadAdInfoManager instance;

    /** 添加AdInfo的集合*/
    private Map<String,AdInfo> adInfoMap = new HashMap<>();

    private DownloadAdInfoManager(){}
    public static DownloadAdInfoManager getInstance(){
        if(instance == null){
            synchronized(DownloadAdInfoManager.class){
                if(instance == null){
                    instance = new DownloadAdInfoManager();
                }
            }
        }
        return instance;
    }

    public Map<String, AdInfo> getAdInfoMap() {
        return adInfoMap;
    }

    /**
     * 添加AdInfo
     * @param adInfo
     */
    public void addDownloadAdInfo(AdInfo adInfo){
        if(adInfo != null && !TextUtils.isEmpty(adInfo.getPackageName())){
            adInfoMap.put(adInfo.getPackageName(),adInfo);
        }
    }
}
