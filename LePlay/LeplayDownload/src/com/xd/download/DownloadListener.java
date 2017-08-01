package com.xd.download;
/**
 * 下载状态监听器
 * 
 * @author lilijun
 * 
 */
public interface DownloadListener
{
	/**
	 * 通知下载任务状态改变
	 * 
	 * @param state
	 *            状态
	 * @param info
	 *            下载任务
	 */
	void onStateChange(int state, DownloadInfo info);

	/**
	 * 通知下载任务进度变化，进度的更新频率为1s+,任务这期间的进度事件会被丢弃。
	 * 
	 * @param percent
	 *            百分比
	 * @param info
	 *            下载任务
	 */
	void onProgress(int percent, DownloadInfo info);

	/**
	 * 通知下载任务项数发生了变化
	 * 
	 * @param state
	 *            变换状态
	 * @param info
	 *            下载任务
	 */
	void onTaskCountChanged(int state, DownloadInfo info);
}
