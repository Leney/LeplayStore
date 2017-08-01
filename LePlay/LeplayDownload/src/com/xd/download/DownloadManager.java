package com.xd.download;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Proxy;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.TextUtils;
import android.widget.Toast;

import com.xd.base.util.DLog;

/**
 * 下载管理器
 *
 * @author lilijun
 */
public class DownloadManager {
    public static final int DATABASE_VERSION = 8;

    public static final String ERROR_NO_SD_CARD = "未检测到存储卡,请先插入存储卡!";

    public static final String ERROR_STORAGE_SPACE_TOO_LOW = "存储卡已满，建议清理存储器相关内容！";

    public static final String ERROR_STORAGE_ERROR = "请检查存储器连接是否正常，或换一张存储卡！";

    public static final String ERROR_STORAGE_UNMOUNT = "存储卡被电脑占用，请尝试断开USB接口！";

    public static final String ERROR_NO_INTENET = "网络错误，请重新设置网络!";

    public static final String ERROR_DOWNLOAD_FAIL = "%s下载失败, 原因: %s";

    public static final int NOTIFICATION_ID_DOWNLOADING = 100000000;

    public static final int NOTIFICATION_ID_DOWNLOADED = 100000001;

    public static final int NOTIFICATION_ID_DOWNLOADED_FAILED = 100000002;

    public static final int FLAG_NOTIFICATION_ADD = 1;

    public static final int FLAG_NOTIFICATION_DELETE = 1 << 1;

    public static final int FLAG_NOTIFICATION_SUCCESS = 1 << 2;

    public static final int FLAG_NOTIFICATION_FAIL = 1 << 3;

    class TaskOperation {
        public static final int OPERATION_STOP = 0;

        public static final int OPERATION_DELETE = 1;

        public static final int OPERATION_DB_INSERT_OR_UPDATE = 2;

        public static final int OPERATION_DB_DELETE = 3;

        public static final int FLAG_NO_DELETE_FILE_WHEN_DELETE_TASK = 1;

        int operation_type;

        int flag; //

        DownloadInfo info;
    }

    class ConnectionChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.this.bNetworkValid = Util
                    .isNetworkAvailable(context);
            DLog.d(Constants.TAG, "DownloadManager.this.bNetworkValid = "
                    + DownloadManager.this.bNetworkValid);
        }
    }

    public static DownloadManager shareInstance() {
        if (defaultDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (defaultDownloadManager == null) {
                    defaultDownloadManager = new DownloadManager();
                }
            }
        }
        return defaultDownloadManager;
    }

    public DownloadManager() {
        allDownloadList = new ArrayList<DownloadInfo>();
        allDownloadMap = new HashMap<String, DownloadInfo>();
        allDownloadTaskMap = new HashMap<String, DownloadTask>();
        downloadListenerList = new ArrayList<DownloadListener>();
        allNotificationMap = new HashMap<String, Notification>();

        allDownloadFutureMap = new HashMap<String, Future<?>>();

        theThreadPool = Executors.newFixedThreadPool(3); //
        bInited = new AtomicBoolean(false);
        lstDownloading = new ArrayList<String>();
        lstDownloaded = new ArrayList<String>();

        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();

        theThreadPool.submit(new Runnable() {

            public void run() {
                while (true) {
                    try {
                        TaskOperation oper = DownloadManager.this.operationQueue
                                .take();
                        if (oper.operation_type == TaskOperation.OPERATION_STOP) {
                            DownloadManager.this.stopDownloadOperate(oper.info);
                        } else if (oper.operation_type == TaskOperation.OPERATION_DELETE) {
                            DownloadManager.this
                                    .deleteDownloadOperate(
                                            oper.info,
                                            oper.flag == TaskOperation
                                                    .FLAG_NO_DELETE_FILE_WHEN_DELETE_TASK ? false
                                                    : true);
                        } else if (oper.operation_type == TaskOperation
                                .OPERATION_DB_INSERT_OR_UPDATE) {
                            if (DownloadManager.this.db != null) {
                                DownloadManager.this.db
                                        .updateOrInsert(oper.info);
                            }
                        }
                    } catch (Exception e) {
                        DLog.e(Constants.TAG, "DownloadManager# Exception=", e);
                    }
                }
            }
        });

    }

    private void wateforInited() {

    }

    public void init(Context aContext) {
        if (this == defaultDownloadManager) {
            this.init(aContext, null, null);
        } else {
            throw new IllegalStateException(
                    "new instance need a database path and download dir path");
        }
    }

    /**
     * 只有第一次调用有效. 共享实例不使用传入的aDbPath和downloadDir参数.
     * <p/>
     * 初始化，负责设置上下文环境和加载数据库. 侦听网络变化，创建handle对象.
     *
     * @param aContext
     * @param aDbPath     数据库文件名
     * @param downloadDir 下载文件目录名
     */
    public void init(Context aContext, String aDbPath, String downloadDir) {

        if (this.context != null) { // 仅第一次调用有效.
            DLog.d(Constants.TAG, "init have already invoked.");
            // throw new IllegalStateException("init have already invoked.");
            return;
        }

        if (aContext == null) {
            throw new IllegalArgumentException("context is null.");
        }

        if (this != defaultDownloadManager) {
            if (aDbPath == null || downloadDir == null) {
                throw new IllegalArgumentException(
                        "dbpath is null or download dir is null.");
            }
        }

        // if(!bInited.compareAndSet(false, true)){
        // return;//线程安全需要
        // }

        this.context = aContext.getApplicationContext();

        if (aDbPath != null) {
            default_db_filename = aDbPath;
        }

        if (downloadDir != null) {
            default_download_dir = downloadDir;
        }

        db = new DownloadDB(context, default_db_filename, DATABASE_VERSION);

        List<DownloadInfo> lst = db.queryAll();
        DLog.d(Constants.TAG, "task list size from db is " + lst.size());
        allDownloadList.addAll(lst);

        for (DownloadInfo info : lst) {
            DLog.d(Constants.TAG,
                    info.getName() + "when load status " + info.getState());

            allDownloadMap.put(info.getPackageName(), info);
            // info.reset();
            info.setCanceled(false);
            info.setDeleted(false);
            info.setError(DownloadInfo.ERROR_NA);
            if (info.getState() != DownloadInfo.STATE_FINISH) {
                info.setState(DownloadInfo.STATE_STOP);
            }
        }

        IntentFilter in = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        br = new ConnectionChangeReceiver();
        context.registerReceiver(br, in);

        manager = (NotificationManager) context
                .getSystemService(Activity.NOTIFICATION_SERVICE);
        // clear 通知
        if (manager != null) {
            manager.cancel(NOTIFICATION_ID_DOWNLOADING);
            manager.cancel(NOTIFICATION_ID_DOWNLOADED);
            manager.cancel(NOTIFICATION_ID_DOWNLOADED_FAILED);
        }

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                messageBoxInMainThread(msg.obj.toString());
                // super.handleMessage(msg);
            }
        };
        bNetworkValid = Util.isNetworkAvailable(context);
        lstValidPath = getWritableVolumePaths(aContext);

        // notifitionTaskDownloading = new Notification(getResourceId(aContext,
        // "icon_notification", "drawable", aContext.getPackageName()),
        // "正在下载任务数", System.currentTimeMillis());
        // notifitionTaskDownloaded = new Notification(getResourceId(aContext,
        // "icon_notification", "drawable", aContext.getPackageName()),
        // "下载完成任务数", System.currentTimeMillis());
        // notifitionTaskDownloadFailed = new
        // Notification(getResourceId(aContext,
        // "icon_notification", "drawable", aContext.getPackageName()),
        // "下载失败任务数", System.currentTimeMillis());

        // notifitionTaskDownloaded = new NotificationCompat.Builder(aContext);
        // notifitionTaskDownloaded.setSmallIcon(getResourceId(aContext,
        // "icon_notification", "drawable", aContext.getPackageName()));
        // notifitionTaskDownloaded.setAutoCancel(false);
        // notifitionTaskDownloaded.setWhen(System.currentTimeMillis());
        // notifitionTaskDownloaded.setLargeIcon(BitmapFactory.decodeResource(
        // aContext.getResources(),
        // getResourceId(aContext, "icon_notification_update", "drawable",
        // aContext.getPackageName())));
        // notifitionTaskDownloaded.setTicker("下载完成任务数");

        // notifitionTaskDownloadFailed = new
        // NotificationCompat.Builder(aContext);
        // notifitionTaskDownloadFailed.setSmallIcon(getResourceId(aContext,
        // "icon_notification", "drawable", aContext.getPackageName()));
        // notifitionTaskDownloadFailed.setAutoCancel(false);
        // notifitionTaskDownloadFailed.setWhen(System.currentTimeMillis());
        // notifitionTaskDownloadFailed.setLargeIcon(BitmapFactory.decodeResource(
        // aContext.getResources(),
        // getResourceId(aContext, "icon_notification_update", "drawable",
        // aContext.getPackageName())));
        // notifitionTaskDownloadFailed.setTicker("下载失败任务数");

        // notifitionTaskDownloading = new NotificationCompat.Builder(aContext);
        // notifitionTaskDownloading.setSmallIcon(getResourceId(aContext,
        // "icon_notification", "drawable", aContext.getPackageName()));
        // notifitionTaskDownloading.setAutoCancel(false);
        // notifitionTaskDownloading.setWhen(System.currentTimeMillis());
        // notifitionTaskDownloading.setLargeIcon(BitmapFactory.decodeResource(
        // aContext.getResources(),
        // getResourceId(aContext, "icon_notification_update", "drawable",
        // aContext.getPackageName())));
        // notifitionTaskDownloading.setTicker("正在下载任务数");

        ACTION_DOWNLOAD_MANAGER_ACTIVITY = context.getPackageName()
                + ".downloadManager";

        bInited.set(true);
    }

    /**
     * 取消未完成的任务，删除通知 设置handle。 取消广播侦听.
     */
    public void destory() {
        for (Future<?> f : allDownloadFutureMap.values()) {
            f.cancel(false);
        }

        if (manager != null) {
            manager.cancel(NOTIFICATION_ID_DOWNLOADED);
            manager.cancel(NOTIFICATION_ID_DOWNLOADED_FAILED);
            manager.cancel(NOTIFICATION_ID_DOWNLOADING);
        }
        context.unregisterReceiver(br);
        handler = null;
        theThreadPool.shutdown();
    }

    /**
     * 添加一个下载任务到下载队列,同步执行. 可能的失败原因. 1、网络无效 2、SD卡被占用或不存在
     * 3、SD卡空间不足,根据info.getSize()来判断. 4、参数无效
     *
     * @param info
     * @return 添加成功返回true，否则返回false.
     */
    public boolean addDownload(DownloadInfo info) {
        DLog.d(Constants.TAG, "download task add begin.");
        wateforInited();
        writeLock.lock();
        try {
            // 检查输入参数
            if (info == null || (info.getPackageName() == null)
                    || (info.getUrl() == null)) {
                DLog.i("llj", "info==null------->>>" + (info == null));
                DLog.i("llj", "packageName------->>>" + info.getPackageName());
                DLog.i("llj", "url------->>>" + info.getUrl());
                DLog.d(Constants.TAG, "download task add  arguement error.");
                return false;
            }

            if (isHttpUrl(info)) {

            } else {
                DLog.d(Constants.TAG, "无效的url:" + info.getPackageName() + "|"
                        + info.getUrl());
                if (bShowToast) {
                    messageBox("无效的url:" + info.getName() + "|" + info.getUrl());
                }
                return false;
            }

            // 检查任务存在。
            DownloadInfo tempInfo = allDownloadMap.get(info.getPackageName());
            if (tempInfo != null) {
                if ((tempInfo.getState() == DownloadInfo.STATE_WAIT)
                        || tempInfo.getState() == DownloadInfo.STATE_DOWNLOADING) {
                    DLog.d(Constants.TAG, "download task has exist.");

                    return false;
                }

                if ((tempInfo.getState() == DownloadInfo.STATE_FINISH)) {
                    // softId 不是-2的时候才检查，-2是广告的softId
                    File f = new File(tempInfo.getPath());
                    if (f != null && f.exists()
                            && f.length() == tempInfo.getContentLength()
                            && tempInfo.getContentLength() != 0) {
                        return false;
                    } else {// 文件不存在，或不完整
                        tempInfo.setState(DownloadInfo.STATE_WAIT);
                    }
                }

                // FIXME 根据传进来的downloadInfo信息,更新本地信息. 如是否提示.
            } else {
                tempInfo = info;
            }

            // 检测SD卡
            // 1.1 SD卡是否被占用
            DLog.d(Constants.TAG, "add task " + tempInfo.getName() + "|"
                    + tempInfo.getPackageName() + "|" + tempInfo.getUrl() + "|"
                    + tempInfo.getPath());
            if (Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_SHARED)) {
                if (bShowToast || tempInfo.isShowNotifiction()) {
                    messageBox(ERROR_STORAGE_UNMOUNT);
                }
                return false;
            }

            // 1.2 SD卡是否存在
            if (!Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
                if (bShowToast || tempInfo.isShowNotifiction()) {
                    messageBox(ERROR_NO_SD_CARD);
                }
                return false;
            }

            // 1.3 SD卡是否有足够空间。
            if (!isSDCardHasEnoughSpace(tempInfo)) {
                DLog.d(Constants.TAG, "sd card full.");
                if (bShowToast || tempInfo.isShowNotifiction()) {
                    messageBox(ERROR_STORAGE_SPACE_TOO_LOW);
                }
                return false;
            }

            // 检查网络
            if (!bNetworkValid) {
                DLog.d(Constants.TAG, "no network.");
                return false;
            }

            tempInfo.reset();

            // 创建下载任务
            DownloadTask task = new DownloadTask(tempInfo, this);
            Future<?> f = theThreadPool.submit(task);
            allDownloadMap.put(info.getPackageName(), tempInfo);
            allDownloadTaskMap.put(info.getPackageName(), task);
            if (allDownloadList.indexOf(tempInfo) < 0) {
                allDownloadList.add(tempInfo);
            }

            allDownloadFutureMap.put(info.getPackageName(), f);

            // 通知listener任务数变化
            taskCountChanged(DownloadInfo.TASK_EVENT_ADD, info);

            // 添加数据库更新任务
            TaskOperation oper = new TaskOperation();
            oper.operation_type = TaskOperation.OPERATION_DB_INSERT_OR_UPDATE;
            oper.info = tempInfo;
            operationQueue.add(oper);

            // 更新状态栏任务数变化通知消息
            if (!lstDownloading.contains(info.getPackageName())) {
                lstDownloading.add(info.getPackageName());
            }
            updateDownloadStatusNotification(FLAG_NOTIFICATION_ADD);

            DLog.d(Constants.TAG, "download task add OK.");

            // Intent addintent = new Intent();
            // addintent.setAction("com.gionee.aora.market.download.count.change");
            // context.sendBroadcast(addintent);
        } catch (Exception e) {
            DLog.e(Constants.TAG, "addDownload", e);
        } finally {
            DLog.d(Constants.TAG, "download task add unlock");
            writeLock.unlock();
        }

        return true;
    }

    private boolean isHttpUrl(DownloadInfo info) {
        if (info.getUrl().length() <= 8) {

            return false;
        }

        String strStart = info.getUrl().substring(0, 8).toLowerCase();

        return strStart.startsWith("http://")
                || strStart.startsWith("https://");
    }

    public boolean reDownloadLostedFinishedTask(DownloadInfo info) {
        writeLock.lock();
        try {
            DownloadInfo temp = allDownloadMap.get(info.getPackageName());
            if (temp != null) {
                if ((temp.getState() == DownloadInfo.STATE_FINISH)
                        && !(new File(temp.getPath()).exists())) {
                    temp.setState(DownloadInfo.STATE_STOP);
                    lstDownloaded.remove(info.getPackageName());
                }
            }
        } catch (Exception e) {
            DLog.e(Constants.TAG, "reDownloadLostedFinishedTask", e);
        } finally {
            writeLock.unlock();
        }

        return addDownload(info);
    }

    /**
     * 停止一个正在下载的任务. 提交一个取消操作到操作队列后，立刻返回.
     *
     * @param info
     */
    public void stopDownload(DownloadInfo info) {
        wateforInited();
        TaskOperation oper = new TaskOperation();
        oper.operation_type = TaskOperation.OPERATION_STOP;
        oper.info = info;
        try {
            operationQueue.put(oper);
        } catch (InterruptedException e) {
            DLog.e(Constants.TAG, "stopDownload# Exception=", e);
        }
    }

    private void stopDownloadOperate(DownloadInfo info) {
        DLog.d(Constants.TAG, "download task stop begin." + info.getName()
                + "|" + info.getPackageName() + "|" + info.getPath() + "|"
                + info.getUrl());
        writeLock.lock();
        try {
            Future<?> f = allDownloadFutureMap.get(info.getPackageName());
            if (f != null) {
                f.cancel(false);
            }
            allDownloadFutureMap.remove(info.getPackageName());

            DownloadTask task = allDownloadTaskMap.get(info.getPackageName());
            if (task != null) {
                task.cancel();
                allDownloadTaskMap.remove(info.getPackageName());
            }
            if (db != null) {
                db.updateOrInsert(info);
            }

            lstDownloaded.remove(info.getPackageName());
            updateDownloadStatusNotification(FLAG_NOTIFICATION_DELETE);
        } catch (Exception e) {
            DLog.e(Constants.TAG, "stopDownloadOperate", e);
        } finally {
            DLog.d(Constants.TAG, "download task stop unlock.");
            writeLock.unlock();
        }

        DLog.d(Constants.TAG, "download task stop OK.");
    }

    /**
     * 删除下载中或已下载完成的任务, 提交一个删除操作到操作队列后，立刻返回.
     *
     * @param info
     */
    public void deleteDownload(DownloadInfo info) {
        deleteDownload(info, true);
    }

    /**
     * 根据包名删除任务
     *
     * @param packageName
     */
    public void deleteDownloadByPackage(String packageName) {
        DownloadInfo tempInfo = allDownloadMap.get(packageName);
        if (tempInfo != null) {
            deleteDownload(tempInfo);
        }
    }

    /**
     * 根据包名查找 当前是否有正在下载的任务
     *
     * @param packageName
     * @return
     */
    public boolean isDownloading(String packageName) {
        DownloadInfo tempInfo = allDownloadMap.get(packageName);
        if (tempInfo != null) {
            if (tempInfo.getState() == DownloadInfo.STATE_DOWNLOADING || tempInfo.getState() ==
                    DownloadInfo.STATE_WAIT) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除下载中或已下载完成的任务, 提交一个删除操作到操作队列后，立刻返回.
     *
     * @param info
     * @param bDeleteFile 删除任务时是否删除图片.
     */
    public void deleteDownload(DownloadInfo info, boolean bDeleteFile) {
        wateforInited();
        DLog.v(Constants.TAG, "bDeleteFile =" + bDeleteFile);
        TaskOperation oper = new TaskOperation();
        oper.operation_type = TaskOperation.OPERATION_DELETE;
        oper.info = info;
        if (!bDeleteFile) {
            oper.flag = TaskOperation.FLAG_NO_DELETE_FILE_WHEN_DELETE_TASK;
        }
        try {
            operationQueue.put(oper);
        } catch (InterruptedException e) {
            DLog.e(Constants.TAG, "deleteDownload# Exception=", e);
        }

        Intent delintent = new Intent();
        delintent.setAction("com.gionee.aora.market.download.count.change");
        context.sendBroadcast(delintent);
    }

    /**
     * 删除一个任务. 任务可能处于以下状态. 0. 以前下载完成的任务.本次应用启动后没有添加这个任务.直接删除文件. 缓存中没找到task对象 1.
     * 任务添加，处在等来队列. future.cancel 取消下载任务, 缓存中找到task对象 2。任务下载中, //task类负责删除文件逻辑
     * 3. 任务已完成. //task类负责删除文件逻辑 4. 任务被取消. //task类负责删除文件逻辑 5. 任务被删除.
     *
     * @param info
     */
    private void deleteDownloadOperate(DownloadInfo info, boolean bDeleteFile) {
        DLog.d(Constants.TAG, "download task delete begin." + info.getName()
                + "|" + info.getPackageName() + "|" + info.getPath() + "|"
                + info.getUrl());
        writeLock.lock();

        try {
            DownloadInfo tempInfo = allDownloadMap.get(info.getPackageName());
            allDownloadList.remove(info);
            allDownloadList.remove(tempInfo);
            // 更新数据库.
            if (db != null) {
                db.delete(tempInfo);
            }

            Future<?> f = allDownloadFutureMap.get(info.getPackageName());
            if (f != null) // 任务可能还在等待队里
            {
                f.cancel(false);
            }
            allDownloadFutureMap.remove(info.getPackageName());

            DownloadTask task = allDownloadTaskMap.get(info.getPackageName());
            if (task != null) {
                task.delete(bDeleteFile);
                allDownloadTaskMap.remove(info.getPackageName());
            } else { // 删除本次启动时已完成的任务
                if (info.getPath() != null) {
                    if (bDeleteFile) {
                        boolean bRet = DownloadTask
                                .deleteDownloadInfoFile(info);
                        ;
                        DLog.d(Constants.TAG, Thread.currentThread().getName()
                                + info.getName() + " 0 delete ret " + bRet);
                    }
                }
            }

            if (tempInfo != null) {
                allDownloadMap.remove(info.getPackageName());
                tempInfo.setCanceled(true);
                tempInfo.setDeleted(true);

                info.setCanceled(true);
                info.setDeleted(true);
            }

            taskCountChanged(DownloadInfo.TASK_EVENT_DELETE, tempInfo);

            // 发送通知状态
            lstDownloading.remove(info.getPackageName());
            lstDownloaded.remove(info.getPackageName());
            updateDownloadStatusNotification(FLAG_NOTIFICATION_DELETE);
        } catch (Exception e) {
            DLog.e(Constants.TAG, "deleteDownloadOperate", e);
        } finally {
            DLog.d(Constants.TAG, "download task delete unlock.");
            writeLock.unlock();
        }
        DLog.d(Constants.TAG, "download task delete OK.");
    }

    public DownloadInfo queryDownload(String packageName) {
        wateforInited();
        readLock.lock();
        DownloadInfo info = null;
        try {
            info = allDownloadMap.get(packageName);
        } catch (Exception e) {
            DLog.e(Constants.TAG, "queryDownload", e);
        } finally {
            readLock.unlock();
        }
        return info;
    }

    public void registerDownloadListener(DownloadListener listener) {
        if (listener == null)
            return;
        writeLock.lock();
        try {
            downloadListenerList.add(listener);
        } catch (Exception e) {
            DLog.e(Constants.TAG, "registerDownloadListener", e);
        } finally {
            writeLock.unlock();
        }
    }

    public void unregisterDownloadListener(DownloadListener listener) {
        writeLock.lock();
        try {
            downloadListenerList.remove(listener);
        } catch (Exception e) {
            DLog.e(Constants.TAG, "unregisterDownloadListener", e);
        } finally {
            writeLock.unlock();
        }
    }

    void downloadEventOccur(int event, DownloadInfo info) {
        writeLock.lock();
        try {
            if (db != null) {
                db.updateOrInsert(info);
            }

            DLog.d(Constants.TAG, "downloadEventOccur" + info.getName()
                    + " status " + info.getState() + " event" + event);

            if (event == DownloadInfo.STATE_FINISH
                    || event == DownloadInfo.STATE_ERROR
                    || event == DownloadInfo.STATE_STOP) {
                if (event == DownloadInfo.STATE_FINISH) {
                    if (!lstDownloaded.contains(info.getPackageName())) {
                        lstDownloaded.add(info.getPackageName());
                    }
                    allDownloadFutureMap.remove(info.getPackageName());
                    allDownloadTaskMap.remove(info.getPackageName());
                }
                lstDownloading.remove(info.getPackageName());
                updateDownloadStatusNotification((event == DownloadInfo.STATE_FINISH) ?
                        FLAG_NOTIFICATION_SUCCESS
                        : ((event == DownloadInfo.STATE_ERROR) ? FLAG_NOTIFICATION_FAIL
                        : FLAG_NOTIFICATION_DELETE));
                deleteNodification(info);
            }

            for (DownloadListener listener : downloadListenerList) {
                // FIXME 需要使用handle来处理
                listener.onStateChange(event, info);
            }

            updateDownloadStatusNotification(FLAG_NOTIFICATION_ADD);
        } catch (Exception e) {
            DLog.e(Constants.TAG, "downloadEventOccur", e);
        } finally {
            writeLock.unlock();
        }
    }

    private void showDownloadError(DownloadInfo info) {
        if (bShowToast || info.isShowNotifiction()) {
            String reason = "未知错误.";
            if (!bNetworkValid) {
                reason = ERROR_NO_INTENET;
            }

            switch (info.getError()) {
                case DownloadInfo.ERROR_HTTP_FAILD:
                case DownloadInfo.ERROR_NO_CONNECTION:
                case DownloadInfo.ERROR_RUNTIME_EXCEPTION:
                    reason = "软件包不完整，请重新下载！";
                    break;
                case DownloadInfo.ERROR_NO_NETWORK:
                    reason = ERROR_NO_INTENET;
                    break;
                case DownloadInfo.ERROR_STORAGE_FULL:
                    reason = ERROR_STORAGE_SPACE_TOO_LOW;
                    break;
                case DownloadInfo.ERROR_WRITE_FAILD:
                    if (isSDCardHasEnoughSpace(info)) {
                        reason = ERROR_STORAGE_ERROR;
                    } else {
                        reason = ERROR_STORAGE_SPACE_TOO_LOW;
                    }
                    break;
                default:
                    reason = ERROR_NO_INTENET;
                    break;
            }

            messageBox(String.format(ERROR_DOWNLOAD_FAIL, info.getName(),
                    reason));
        }
    }

    void downloadProgressEventOccur(int percent, DownloadInfo info) {
        readLock.lock();
        try {
            addNodification(info);
            for (DownloadListener listener : downloadListenerList) {
                // FIXME 需要使用handle来处理
                listener.onProgress(percent, info);
            }
        } catch (Exception e) {
            DLog.e(Constants.TAG, "downloadProgressEventOccur", e);
        } finally {
            readLock.unlock();
        }
    }

    void taskCountChanged(int event, DownloadInfo info) {
        readLock.lock();
        try {
            for (DownloadListener listener : downloadListenerList) {
                // FIXME 需要使用handle来处理
                listener.onTaskCountChanged(event, info);
            }
        } catch (Exception e) {
            DLog.e(Constants.TAG, "taskCountChanged", e);
        } finally {
            readLock.unlock();
        }
    }

    public List<DownloadInfo> getAllTaskInfo() {
        wateforInited();
        readLock.lock();
        List<DownloadInfo> tempList = null;
        try {
            tempList = new ArrayList<DownloadInfo>(allDownloadList.size());
            tempList.addAll(allDownloadList);
        } catch (Exception e) {
            DLog.e(Constants.TAG, "getAllTaskInfo", e);
        } finally {
            readLock.unlock();
        }

        return tempList;
    }

    public List<DownloadInfo> getSortedAllTaskInfo() {
        return getSortedAllTaskInfo(new Comparator<DownloadInfo>() {

            @Override
            public int compare(DownloadInfo lhs, DownloadInfo rhs) {
                if (lhs.getState() == rhs.getState()) {
                    return 0;
                }
                if (lhs.getState() == DownloadInfo.STATE_FINISH) {
                    return 1;
                }
                if (rhs.getState() == DownloadInfo.STATE_FINISH) {
                    return -1;
                }
                return 0;
            }
        });
    }

    public List<DownloadInfo> getSortedAllTaskInfo(
            Comparator<DownloadInfo> comparator) {
        wateforInited();
        readLock.lock();
        List<DownloadInfo> tempList = null;
        try {
            tempList = new ArrayList<DownloadInfo>(allDownloadList.size());
            tempList.addAll(allDownloadList);
            Collections.sort(tempList, comparator);
        } catch (Exception e) {
            DLog.e(Constants.TAG, "getSortedAllTaskInfo", e);
        } finally {
            readLock.unlock();
        }

        return tempList;
    }

    public boolean isShowNotifiction() {
        return showNotifiction;
    }

    public void setShowNotifiction(boolean showNotifiction) {
        this.showNotifiction = showNotifiction;
        if (!showNotifiction) {
            if (manager != null) {
                manager.cancel(NOTIFICATION_ID_DOWNLOADING);
                manager.cancel(NOTIFICATION_ID_DOWNLOADED);
            }
        }
    }

    boolean isbShowToast() {
        return bShowToast;
    }

    void setbShowToast(boolean bShowToast) {
        this.bShowToast = bShowToast;
    }

    public HttpHost getHttpProxyHost() {
        if (context == null) {
            return null;
        }

        // return NetUtil.getDefaultProxy(context);
        return getDefaultProxy();
    }

    public String getDefault_download_dir() {
        return default_download_dir;
    }

    public File getDownloadDirectory(DownloadInfo info) {
        if (info == null)
            return null;
        String str = getSavePathForDownloadInfo(info);
        if (str == null) {
            return null;
        }

        File f = new File(new File(str, "donson"), default_download_dir);
        // File f = new File(str, default_download_dir);
        f.mkdirs();
        if (!f.exists()) {
            DLog.e(Constants.TAG, "create dir fail:" + f.getPath());
            return null;
        }
        return f;
    }

    private boolean isSDCardHasEnoughSpace(DownloadInfo info) {
        boolean bEnough = false;
        long lBegin = System.currentTimeMillis();
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            if (info.getPath() == null) {
                if (lstValidPath.size() == 0) {
                    lstValidPath = getWritableVolumePaths(context);
                }
                long aLneedSpaceSize = info.getSize();
                for (String path : lstValidPath) {
                    StatFs stat = new StatFs(path);
                    long blockSize = stat.getBlockSize();
                    long availableBlocks = stat.getAvailableBlocks();

                    long spaces = blockSize * availableBlocks;

                    if (aLneedSpaceSize <= 0) {
                        aLneedSpaceSize = 10 * 1024 * 1024;
                    }
                    bEnough = spaces > aLneedSpaceSize;
                    DLog.d(Constants.TAG, path + " has valid space:" + spaces
                            + "and need " + aLneedSpaceSize);
                    if (bEnough)
                        break;

                }
            } else {
                String fileDir = info.getPath().substring(0,
                        info.getPath().indexOf(default_download_dir) - 1);
                long aLneedSpaceSize = info.getSize() - info.getContentLength()
                        * info.getPercent();
                StatFs stat = new StatFs(fileDir);
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();

                long spaces = blockSize * availableBlocks;

                if (aLneedSpaceSize <= 0) {
                    aLneedSpaceSize = 10 * 1024 * 1024;
                }
                bEnough = spaces > aLneedSpaceSize;
                DLog.d(Constants.TAG, fileDir + " has valid space:" + spaces
                        + "and need " + aLneedSpaceSize);

            }
        }
        DLog.d(Constants.TAG,
                "isSDCardHasEnoughSpace last:"
                        + (System.currentTimeMillis() - lBegin));
        return bEnough;
    }

    String getSavePathForDownloadInfo(DownloadInfo info) {
        String savePath = null;
        if (info != null) {
            long aLneedSpaceSize = info.getSize();
            if (Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
                if (lstValidPath.size() == 0) {
                    lstValidPath = getWritableVolumePaths(context);
                }
                for (String path : lstValidPath) {
                    StatFs stat = new StatFs(path);
                    long blockSize = stat.getBlockSize();
                    long availableBlocks = stat.getAvailableBlocks();

                    long spaces = blockSize * availableBlocks;
                    DLog.d(Constants.TAG, "valid space = " + spaces
                            + ", need size" + aLneedSpaceSize);
                    if (aLneedSpaceSize <= 0) {
                        aLneedSpaceSize = 10 * 1024 * 1024;
                    }
                    DLog.d(Constants.TAG, path + "valid space = " + spaces
                            + ", need size" + aLneedSpaceSize);
                    if (spaces > aLneedSpaceSize) {
                        savePath = path;
                        break;
                    }
                }
            }

        }
        return savePath;
    }

    public boolean isSdCardValid() {
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    void messageBox(String errMsg) {
        if (bShowToast) {
            if (context != null && errMsg != null) {

                handler.sendMessage(handler.obtainMessage(0, errMsg));
            }
        }
    }

    private void messageBoxInMainThread(String errMsg) {
        Toast t = Toast.makeText(this.context, errMsg, Toast.LENGTH_SHORT);
        t.show();
    }

    private void addNodification(DownloadInfo info) {
        if (!info.isShowNotifiction())
            return;
        if (manager != null) {
            readLock.lock();
            try {
                Notification notification = allNotificationMap.get(info
                        .getPackageName());
                if (notification == null) {
                    notification = new Notification(getResourceId(context,
                            "icon_notification", "drawable",
                            context.getPackageName()), info.getName() + "正在下载",
                            System.currentTimeMillis());

                    allNotificationMap.put(info.getPackageName(), notification);
                }
                // 4.0的机子上跑是没有问题的，在2.3、2.2上就会出现异常。
                // 所以还是要设置contentIntent，只是Intents设置不同的动作，contentIntent不可以设为空！！！
                PendingIntent contentIntent = PendingIntent.getActivity(
                        context, (int) info.getId(), new Intent(), 0);
                notification.flags = Notification.FLAG_NO_CLEAR;
                notification.setLatestEventInfo(context, info.getName(),
                        info.getPercent() + "%", contentIntent);

                manager.notify((int) info.getId(), notification);
            } catch (Exception e) {
                DLog.e(Constants.TAG, "addNodification", e);
            } finally {
                readLock.unlock();
            }

        }
    }

    private void deleteNodification(DownloadInfo info) {
        if (manager != null) {
            manager.cancel((int) info.getId());
            allNotificationMap.remove(info.getPackageName());
        }
    }

    private void updateDownloadStatusNotification(int flag) {
        if (!showNotifiction)
            return;
        if (manager == null)
            return;

        // 这个锁的范围有点大.
        readLock.lock();
        try {

            int taskCount = 0, taskDownloadingCount = 0, taskWaitCount = 0, taskFailCount = 0;
            for (DownloadInfo info : allDownloadList) {
                switch (info.getState()) {
                    case DownloadInfo.STATE_ERROR:
                        // taskCount++;
                        taskFailCount++;
                        break;
                    case DownloadInfo.STATE_WAIT:
                        taskCount++;
                        taskWaitCount++;
                        break;
                    case DownloadInfo.STATE_DOWNLOADING:
                        taskCount++;
                        taskDownloadingCount++;
                        break;
                }
            }
            if (taskCount <= 0) {
                DLog.d(Constants.TAG, Thread.currentThread().getName()
                        + " cancel downloading notification.");
                manager.cancel(NOTIFICATION_ID_DOWNLOADING);
            } else {
                Intent intent = new Intent(ACTION_DOWNLOAD_MANAGER_ACTIVITY);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent contentIntent = PendingIntent.getActivity(
                        context, NOTIFICATION_ID_DOWNLOADING, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                // notifitionTaskDownloading.flags = Notification.FLAG_NO_CLEAR;
                String msgTitle = String.format("%d个下载任务", taskCount);
                String msgContent = String.format("%d个下载中,%d个等待中",
                        taskDownloadingCount, taskWaitCount);
                // notifitionTaskDownloading.tickerText = msgTitle + msgContent;
                // notifitionTaskDownloading.when = System.currentTimeMillis();
                // notifitionTaskDownloading.setLatestEventInfo(context,
                // msgTitle,
                // msgContent, contentIntent);

                // notifitionTaskDownloading.setTicker(msgTitle + msgContent);
                // notifitionTaskDownloading.setContentTitle(msgTitle);
                // notifitionTaskDownloading.setContentText(msgContent);
                // notifitionTaskDownloading.setContentIntent(contentIntent);
                // Notification downloadingNotify = notifitionTaskDownloading
                // .build();
                // Utils.hideNotifySmallIcon(context, downloadingNotify);
                // manager.notify(NOTIFICATION_ID_DOWNLOADING,
                // downloadingNotify);

                // 弹出下载中的通知 TODO
                // showSystemStyleNotifiy(context, msgTitle, msgContent,
                // msgTitle
                // + msgContent, contentIntent,
                // NOTIFICATION_ID_DOWNLOADING, manager);
            }

            if (taskFailCount <= 0) {
                manager.cancel(NOTIFICATION_ID_DOWNLOADED_FAILED);
            } else {
                if ((flag & FLAG_NOTIFICATION_FAIL) == FLAG_NOTIFICATION_FAIL) {
                    Intent intent = new Intent(ACTION_DOWNLOAD_MANAGER_ACTIVITY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent contentIntent = PendingIntent.getActivity(
                            context, NOTIFICATION_ID_DOWNLOADED_FAILED, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    // notifitionTaskDownloadFailed.flags =
                    // Notification.FLAG_AUTO_CANCEL;
                    String msgTitle = String.format("%d个下载任务失败", taskFailCount);
                    // notifitionTaskDownloadFailed.tickerText = msgTitle;
                    // notifitionTaskDownloadFailed.when = System
                    // .currentTimeMillis();
                    // notifitionTaskDownloadFailed.setLatestEventInfo(context,
                    // msgTitle, "点击进入下载管理.", contentIntent);

                    // notifitionTaskDownloadFailed.setTicker(msgTitle);
                    // notifitionTaskDownloadFailed.setContentTitle(msgTitle);
                    // notifitionTaskDownloadFailed.setContentText("点击进入下载管理.");
                    // notifitionTaskDownloadFailed
                    // .setContentIntent(contentIntent);
                    // Notification downloadFailedNotify =
                    // notifitionTaskDownloadFailed
                    // .build();
                    // Utils.hideNotifySmallIcon(context, downloadFailedNotify);
                    // manager.notify(NOTIFICATION_ID_DOWNLOADED_FAILED,
                    // downloadFailedNotify);

                    // 弹出下载失败的通知 TODO
                    // showSystemStyleNotifiy(context, msgTitle, "点击进入下载管理",
                    // msgTitle, contentIntent,
                    // NOTIFICATION_ID_DOWNLOADED_FAILED, manager);
                }

            }

            int cntDownlonded = lstDownloaded.size();
            if (cntDownlonded <= 0) {
                DLog.d(Constants.TAG, Thread.currentThread().getName()
                        + " cancel downloaded... notification.");
                manager.cancel(NOTIFICATION_ID_DOWNLOADED);
            } else {
                if ((flag & FLAG_NOTIFICATION_SUCCESS) == FLAG_NOTIFICATION_SUCCESS) {
                    Intent intent = new Intent(ACTION_DOWNLOAD_MANAGER_ACTIVITY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    PendingIntent contentIntent = PendingIntent.getActivity(
                            context, NOTIFICATION_ID_DOWNLOADED, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    // notifitionTaskDownloaded.flags =
                    // Notification.FLAG_AUTO_CANCEL;
                    // notifitionTaskDownloaded.tickerText = cntDownlonded
                    // + "下载任务已完成";
                    // notifitionTaskDownloaded.when =
                    // System.currentTimeMillis();
                    // notifitionTaskDownloaded.setLatestEventInfo(context,
                    // notifitionTaskDownloaded.tickerText, "点击进入下载管理.",
                    // contentIntent);

                    // notifitionTaskDownloaded.setTicker(cntDownlonded
                    // + "下载任务已完成");
                    // notifitionTaskDownloaded.setContentTitle(cntDownlonded
                    // + "下载任务已完成");
                    // notifitionTaskDownloaded.setContentText("点击进入下载管理.");
                    // notifitionTaskDownloaded.setContentIntent(contentIntent);
                    // Notification downloadedNotify = notifitionTaskDownloaded
                    // .build();
                    // Utils.hideNotifySmallIcon(context, downloadedNotify);
                    // manager.notify(NOTIFICATION_ID_DOWNLOADED,
                    // downloadedNotify);

                    // 弹出下载失败的通知 TODO
                    // showSystemStyleNotifiy(context, cntDownlonded +
                    // "下载任务已完成",
                    // "点击进入下载管理", cntDownlonded + "下载任务已完成",
                    // contentIntent, NOTIFICATION_ID_DOWNLOADED, manager);
                }
            }

            // DLog.d(Constants.TAG, Thread.currentThread().getName()
            // + " updateDownloadStatusNotification " + cntDownlonding
            // + "/ " + cntDownlonded);
        } catch (Exception e) {
            DLog.e(Constants.TAG, "updateDownloadStatusNotification", e);
        } finally {
            readLock.unlock();
        }
    }

    public boolean isbNetworkValid() {
        return bNetworkValid;
    }

    void updateTaskProgress(DownloadInfo info) {
        if (db != null) {
            db.updateOrInsert(info);
        }
    }

    /**
     * @param context
     * @return
     */
    List<String> getWritableVolumePaths(Context context) {
        Object mStorageManager;
        Method mMethodGetPaths;
        List<String> l = new ArrayList<String>();
        if (context != null
                && Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)
                && bSupport2SdCard) {
            mStorageManager = context.getSystemService("storage");// Context.STORAGE_SERVICE
            try {
                mMethodGetPaths = mStorageManager.getClass().getMethod(
                        "getVolumePaths");
                String[] paths = (String[]) mMethodGetPaths
                        .invoke(mStorageManager);

                for (String str : paths) {
                    File f = new File(str);
                    if (f.exists() && f.canRead() && f.canWrite()) {
                        l.add(str);
                    }

                }
            } catch (Exception e) {
                DLog.e(Constants.TAG, "getWritableVolumePaths error", e);
                bSupport2SdCard = false;
                if (Environment.getExternalStorageState().equals(
                        android.os.Environment.MEDIA_MOUNTED)) {
                    l.add(Environment.getExternalStorageDirectory().getPath());
                }
            }
        } else {// 不支持2SD卡，系统不存在StorageManager.getVolumePaths方法。
            DLog.i(Constants.TAG,
                    "not support 2 sdcard, no StorageManager.getVolumePaths method.");
            if (Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
                l.add(Environment.getExternalStorageDirectory().getPath());
            }
        }
        return l;
    }

    private List<DownloadInfo> allDownloadList;

    private Map<String, DownloadInfo> allDownloadMap;

    private Map<String, DownloadTask> allDownloadTaskMap;

    private Map<String, Future<?>> allDownloadFutureMap;

    private Map<String, Notification> allNotificationMap;

    private List<DownloadListener> downloadListenerList;

    private ReadWriteLock lock;

    Lock readLock, writeLock;
    ;// task类会使用

    private ExecutorService theThreadPool;

    Context context;

    private DownloadDB db;

    private NotificationManager manager = null;

    private static DownloadManager defaultDownloadManager;

    private LinkedBlockingQueue<TaskOperation> operationQueue = new
            LinkedBlockingQueue<DownloadManager.TaskOperation>();

    private boolean bNetworkValid;

    private boolean bShowToast = true;

    private String default_db_filename = "downloadApk.db";

    // private String default_download_dir = "machine" + File.separator
    // + "downloadApk";
    private String default_download_dir = "downloadApk";

    private AtomicBoolean bInited;

    // private AtomicInteger aiTaskDownloading, aiTaskDownloaded;

    private List<String> lstDownloading, lstDownloaded;

    // private NotificationCompat.Builder notifitionTaskDownloading,
    // notifitionTaskDownloaded, notifitionTaskDownloadFailed;

    /**
     * 下载应用是否显示Notifiction和Toast提示
     */
    private boolean showNotifiction = true;

    List<String> lstValidPath; // 所有可写的卡的目录。

    /**
     * 纪录库启动时间.
     */
    // private long lLaunchTime = System.currentTimeMillis();

    Handler handler;

    private ConnectionChangeReceiver br;

    private boolean bSupport2SdCard = true;

    private boolean downloadOnlyInWifi = false;

    private String ACTION_DOWNLOAD_MANAGER_ACTIVITY = "com.gionee.aora.market.downloadManager";

    /**
     * 通过资源获取id
     *
     * @param context
     * @param name
     * @param type
     * @param packageName
     * @return
     */
    private int getResourceId(Context context, String name, String type,
                              String packageName) {
        Resources themeResources = null;
        PackageManager pm = context.getPackageManager();
        try {
            themeResources = pm.getResourcesForApplication(packageName);
            return themeResources.getIdentifier(name, type, packageName);
        } catch (NameNotFoundException e) {

            e.printStackTrace();
        }
        return 0;
    }

    // private void cancelAllWaitingTask(){
    //
    // }

    public boolean isDownloadOnlyInWifi() {
        return downloadOnlyInWifi;
    }

    public void setDownloadOnlyInWifi(boolean downloadOnlyInWifi) {
        this.downloadOnlyInWifi = downloadOnlyInWifi;
    }

    // /**
    // * 跟系统通知栏样式一样的自定义通知栏
    // *
    // * @param context
    // * @param title
    // * 标题
    // * @param content
    // * 文本
    // * @param pendingIntent
    // * 跳转Intent
    // * @param notifyId
    // * 通知栏Id
    // * @param notificationManager
    // * 通知管理器
    // */
    // public static void showSystemStyleNotifiy(Context context, String title,
    // String content, String tickerText, PendingIntent pendingIntent,
    // int notifyId, NotificationManager notificationManager)
    // {
    // Date date = new Date(System.currentTimeMillis());
    // SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    // String time = sdf.format(date);
    //
    // NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
    // context);
    // mBuilder.setSmallIcon(R.drawable.download_icon_notification);
    // mBuilder.setAutoCancel(true);
    // mBuilder.setWhen(System.currentTimeMillis());
    // RemoteViews contentView = new RemoteViews(context.getPackageName(),
    // R.layout.download_system_type_notify_lay);
    // contentView.setTextViewText(R.id.installed_notification_title, title);
    // contentView.setTextViewText(R.id.installed_notification_content,
    // content);
    // contentView.setTextViewText(R.id.installed_notification_content_time,
    // time);
    // mBuilder.setContent(contentView);
    // // 通知栏滚动消息
    // mBuilder.setTicker(tickerText);
    // mBuilder.setContentIntent(pendingIntent);
    // // NotificationManager mnotiManager = (NotificationManager) context
    // // .getSystemService(Context.NOTIFICATION_SERVICE);
    // Notification mNotification = mBuilder.build();
    // mNotification.contentView = contentView;
    // notificationManager.notify(notifyId, mNotification);
    // }

    private HttpHost getDefaultProxy() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String host = Proxy.getDefaultHost();
        int port = Proxy.getDefaultPort();
        if (host != null && !host.equals("") && port > 0) {
            return new HttpHost(host, port);
        }
        return null;
    }

}
