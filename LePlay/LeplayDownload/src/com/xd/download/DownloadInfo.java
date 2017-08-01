package com.xd.download;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 封装下载任务信息
 * 
 * @author lilijun
 * 
 */
/**
 * 封装下载任务信息
 * 
 * @author lilijun
 *
 */
public class DownloadInfo implements Serializable
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6014828057845754048L;

	/** 下载状态：创建一个下载任务的初始状态 */
	public static final int STATE_WAIT = 0;

	/** 下载状态：正在下载 */
	public static final int STATE_DOWNLOADING = 1;

	/** 下载状态：停止 */
	public static final int STATE_STOP = 2;

	/** 下载状态：完成 */
	public static final int STATE_FINISH = 3;

	/** 下载状态：下载出错 */
	public static final int STATE_ERROR = 4;

	public static final int TASK_EVENT_ADD = 100;

	public static final int TASK_EVENT_DELETE = 101;

	/** 下载过程中错误状态：无错误 */
	public static final int ERROR_NA = 0;

	/** 下载过程中错误状态：无网络 */
	public static final int ERROR_NO_NETWORK = 1;

	/** 下载过程中错误状态：无法连接到服务器 */
	public static final int ERROR_NO_CONNECTION = 2;

	/** 下载过程中错误状态：存储器已满 */
	public static final int ERROR_STORAGE_FULL = 3;

	/** 下载过程中错误状态：磁盘写入错误 */
	public static final int ERROR_WRITE_FAILD = 4;

	/** 下载过程中错误状态：服务器端错误 */
	public static final int ERROR_HTTP_FAILD = 5;

	/** 下载过程中错误状态：服务器端错误 */
	public static final int ERROR_RUNTIME_EXCEPTION = 6;

	/** 下载过程中错误状态：不符合要求的网络连接 */
	public static final int ERROR_NOT_ALLOWED_NETWORK_TYPE = 7;

	/** 下载应用数据库记录ID */
	private long id;

	/** 下载应用名称 */
	private String name;

	/** 下载应用包名 */
	private String packageName;

	/** 下载应用URL */
	private String url;

	/** 下载应用ICON */
	private String iconUrl;

	/** 下载应用大小，网络接口返回的大小 */
	private long size;

	/** 下载应用长度，HTTP响应报文返回的大小 */
	private long contentLength;

	/** 下载应用已下载大小 */
	private long downloadSize;

	/** 下载应用存储路径 */
	private String path;

	/** 下载应用已下载百分比 */
	private int percent;

	/** 下载状态 */
	private int state;

	/** 下载过程中错误状态 */
	private int error;

	/** 下载应用是否手动取消 */
	private boolean canceled;

	/** 下载应用是否删除 */
	private boolean deleted;

	private int hashcode;

	/** 下载应用下载大小 */
	private String formatSize;

	/** 下载应用已下载大小 */
	private String formatDownloadSize = Util.getFormatSize(0);

	/** 下载应用是否显示Notifiction和Toast提示 */
	private boolean showNotifiction;

	/** 应用id */
	private String softId = "";

	/** 应用积分 */
	private int integral = 0;

	/** 真正的apk下载地址，不管有无差分升级，都能下到的apk包路径 */
	private String relApkUrl;

	/** 真正的apk包大小，完整的apk包大小 */
	private long relApkSize;

	/** 如果是更新的DownloadInfo,此属性为其本地应用的软件版本名称 */
	private String localVersionName;

	/** 服务器上面的应用版本名称 */
	private String updateVersionName;

	/** 服务器上面的应用版本号 */
	private int updateVersionCode;

	/** 下载的安装包的id */
	private long packageId;

	/** 任务id */
	private int taskId;

	/** 数据采集的action */
	private String action;

	private final int LOCK_NONE = 0;

	private final int LOCK_DELETE = 1;

	private final int LOCK_INSTALL = 2;

	private boolean installAfterDownload = true;

	private AtomicInteger lockDeleteAndInstall = new AtomicInteger(LOCK_NONE);

	public DownloadInfo()
	{
	}

	public DownloadInfo(String name, String packageName, String url,
			String iconUrl, long size, String softId, int integral)
	{

		this(-1, name, packageName, url, iconUrl, size, 0, 0, null, 0,
				STATE_WAIT, ERROR_NA, false, false, softId, integral);

	}

	public DownloadInfo(int id, String name, String packageName, String url,
			String iconUrl, long size, long contentLength, long downloadSize,
			String path, int percent, int state, int error, boolean cancel,
			boolean showNotifiction, String softId, int integral)
	{
		if (packageName == null || url == null)
		{
			throw new IllegalArgumentException(
					"package name is null or url is null.");
		}

		this.id = id;
		this.name = name;
		this.packageName = packageName;
		this.url = url;
		this.iconUrl = iconUrl;
		this.size = size;
		this.contentLength = contentLength;
		this.downloadSize = downloadSize;
		this.path = path;
		this.percent = percent;
		this.state = state;
		this.error = error;
		this.canceled = cancel;
		this.showNotifiction = showNotifiction;
		this.formatSize = Util.getFormatSize(size);
		this.softId = softId;
		this.integral = integral;
	}

	void reset()
	{
		state = STATE_WAIT;
		error = ERROR_NA;
		canceled = false;
		deleted = false;
	}

	public boolean isInstallAfterDownload()
	{
		return installAfterDownload;
	}

	public void setInstallAfterDownload(boolean installAfterDownload)
	{
		this.installAfterDownload = installAfterDownload;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getIconUrl()
	{
		return iconUrl;
	}

	public void setIconUrl(String iconUrl)
	{
		this.iconUrl = iconUrl;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
		setFormatSize(Util.getFormatSize(size));
	}

	public String getFormatSize()
	{
		return formatSize;
	}

	public void setFormatSize(String formatSize)
	{
		this.formatSize = formatSize;
	}

	public String getFormatDownloadSize()
	{
		return formatDownloadSize;
	}

	public void setFormatDownloadSize(String formatDownloadSize)
	{
		this.formatDownloadSize = formatDownloadSize;
	}

	public long getContentLength()
	{
		return contentLength;
	}

	void setContentLength(long contentLength)
	{
		this.contentLength = contentLength;
	}

	public long getDownloadSize()
	{
		return downloadSize;
	}

	void setDownloadSize(long downloadSize)
	{
		if (contentLength != 0)
		{
			this.percent = (int) (downloadSize * 100 / contentLength);
		}
		this.downloadSize = downloadSize;
		setFormatDownloadSize(Util.getFormatSize(downloadSize));
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public int getPercent()
	{
		return percent;
	}

	public void setPercent(int percent)
	{
		this.percent = percent;
	}

	public int getState()
	{
		return state;
	}

	void setState(int state)
	{
		this.state = state;
	}

	public int getError()
	{
		return error;
	}

	void setError(int error)
	{
		this.error = error;
	}

	public boolean isCanceled()
	{
		return canceled;
	}

	void setCanceled(boolean cancel)
	{
		this.canceled = cancel;
	}

	public boolean isDeleted()
	{
		return deleted;
	}

	void setDeleted(boolean deleted)
	{
		this.deleted = deleted;
	}

	public boolean isShowNotifiction()
	{
		return showNotifiction;
	}

	public void setShowNotifiction(boolean showNotifiction)
	{
		this.showNotifiction = showNotifiction;
	}

	public String getSoftId()
	{
		return softId;
	}

	public void setSoftId(String softId)
	{
		this.softId = softId;
	}

	public int getIntegral()
	{
		return integral;
	}

	public void setIntegral(int integral)
	{
		this.integral = integral;
	}

	public String getRelApkUrl()
	{
		return relApkUrl;
	}

	public void setRelApkUrl(String relApkUrl)
	{
		this.relApkUrl = relApkUrl;
	}

	public long getRelApkSize()
	{
		return relApkSize;
	}

	public void setRelApkSize(long relApkSize)
	{
		this.relApkSize = relApkSize;
	}

	public String getLocalVersionName()
	{
		return localVersionName;
	}

	public void setLocalVersionName(String localVersionName)
	{
		this.localVersionName = localVersionName;
	}

	public String getUpdateVersionName()
	{
		return updateVersionName;
	}

	public void setUpdateVersionName(String updateVersionName)
	{
		this.updateVersionName = updateVersionName;
	}

	public int getUpdateVersionCode()
	{
		return updateVersionCode;
	}

	public void setUpdateVersionCode(int updateVersionCode)
	{
		this.updateVersionCode = updateVersionCode;
	}

	public long getPackageId()
	{
		return packageId;
	}

	public void setPackageId(long packageId)
	{
		this.packageId = packageId;
	}
	
	public int getTaskId()
	{
		return taskId;
	}

	public void setTaskId(int taskId)
	{
		this.taskId = taskId;
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;

		if (!(o instanceof DownloadInfo))
		{
			return false;
		}

		DownloadInfo obj = (DownloadInfo) o;
		return obj.getUrl().equals(url)
				&& obj.getPackageName().equals(packageName);
	}

	@Override
	public int hashCode()
	{
		if (hashcode == 0)
		{
			hashcode = (packageName + "|" + url).hashCode();
		}
		return hashcode;
	}

	boolean lockForDelete()
	{
		return lockDeleteAndInstall.compareAndSet(LOCK_NONE, LOCK_DELETE);
	}

	boolean releaseDeleteLock()
	{
		return lockDeleteAndInstall.compareAndSet(LOCK_DELETE, LOCK_NONE);
	}

	public boolean lockForInstall()
	{
		return lockDeleteAndInstall.compareAndSet(LOCK_NONE, LOCK_INSTALL);
	}

	public boolean releaseInstallLock()
	{
		return lockDeleteAndInstall.compareAndSet(LOCK_INSTALL, LOCK_NONE);
	}

}
