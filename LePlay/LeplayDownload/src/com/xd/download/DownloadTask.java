package com.xd.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipFile;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import com.xd.base.util.DLog;

//import org.apache.http.client.methods.HttpGet;

/**
 * 下载任务，子线程下载，实现HTTP下载、断点续传
 * 
 * @author lilijun
 * 
 */
public class DownloadTask implements Runnable
{
	private static final String TAG = "DownloadTask";

	/** 下载任务信息 */
	private DownloadInfo downloadInfo;

	private DownloadManager downloadManager;

	/** 下载任务是否运行 */
	private boolean running;

	private boolean canceled; // 任务是否被取消或删除了

	private boolean deleted; // 任务是否被删除了

	private boolean deleteFile;// 删除任务时是否删除文件

	// private Lock lock = new ReentrantLock();

	private InputStream in;

	private RandomAccessFile fout;

	private int error; // 纪录下载过程中的错误，数值参考DownloafInfo中的话错误值.

	private HttpClient httpClient;

	private HttpGet httpGet;

	private boolean bHaveSendTaskOverNotification; // 是否已经发送过任务结束通知。只能发送一次任务结束通知。

	public DownloadTask(DownloadInfo downloadInfo, DownloadManager dm)
	{
		super();
		this.downloadInfo = downloadInfo;

		downloadManager = dm;
		running = false;
	}

	@Override
	public void run()
	{
		try
		{
			traceTaskBegin();
			running = true;
			if (canceled)
			{
				return;
			}

			traceSetPath();

			setDownloadFilePath();

			if (canceled)
			{
				return;
			}

			sendTaskDownloadingNotification(DownloadInfo.STATE_DOWNLOADING);
			int tryCount = 3;
			int step = 0;
			while (tryCount > 0)
			{

				error = DownloadInfo.ERROR_NA;
				step = 0;
				do
				{
					if (canceled)
						break;

					if (downloadInfo.getPath() == null)
					{
						error = DownloadInfo.ERROR_STORAGE_FULL;
						DLog.d(Constants.TAG, "硬盘满！");
						break;
					}

					File f = new File(downloadInfo.getPath());
					long filesize = 0;
					if (f.exists())
					{
						filesize = f.length();
					}
					downloadInfo.setDownloadSize(filesize);

					// 如果文件已下载完毕，则没必要发起网络请求.
					if (filesize != 0 && filesize == downloadInfo.getSize())
					{
						downloadInfo.setContentLength(filesize);
						downloadInfo.setDownloadSize(filesize);
						sendTaskProgressNotification();
						DLog.d(Constants.TAG,
								Thread.currentThread().getName() + " "
										+ downloadInfo.getName()
										+ " run 文件已下载完毕:" + "filesize="
										+ filesize + ", contentSize="
										+ downloadInfo.getContentLength());
						break;
					} else
					{
						DLog.d(Constants.TAG,
								Thread.currentThread().getName() + " "
										+ downloadInfo.getName()
										+ "准备下载 filesize=" + filesize
										+ ", contentSize="
										+ downloadInfo.getContentLength());
					}

					if (canceled)
						break;

					try
					{
						setUpHttpConnection(filesize);

						if (canceled)
							break;
						step = 1;
						DLog.d(Constants.TAG, Thread.currentThread().getName()
								+ " " + downloadInfo.getName() + " 网络类型:"
								+ Util.getNetMode(downloadManager.context));
						if (!isAllowedNetwork())
						{

							error = DownloadInfo.ERROR_NOT_ALLOWED_NETWORK_TYPE;
							DLog.d(Constants.TAG,
									Thread.currentThread().getName()
											+ " "
											+ downloadInfo.getName()
											+ " 不允许使用的网络类型:"
											+ Util.getNetMode(downloadManager.context));
							break;
						}

						HttpResponse resp = httpClient.execute(httpGet);

						if (canceled)
							break;

						int statusCode = resp.getStatusLine().getStatusCode();

						DLog.d(Constants.TAG, Thread.currentThread().getName()
								+ " " + downloadInfo.getName()
								+ " run step2.4.1 connect to server http code "
								+ statusCode);
						if (statusCode == 206)
						{
						} else if (statusCode == 200)
						{
							filesize = 0;
						} else
						{
							error = DownloadInfo.ERROR_HTTP_FAILD;
						}

						if (error != DownloadInfo.ERROR_NA)
							break;

						downloadManager.readLock.lock();
						try
						{
							if (!bHaveSendTaskOverNotification)
							{
								downloadInfo.setDownloadSize(filesize);
							}
						} catch (Exception e)
						{
							DLog.d(Constants.TAG, "setDownloadSize", e);
						} finally
						{
							downloadManager.readLock.unlock();
						}

						if (canceled)
							break;

						long totalLength = 0;
						long tempContentLength = 0;
						Header[] headers = resp.getHeaders("Content-Length");
						resp.getAllHeaders();

						try
						{
							tempContentLength = Long.valueOf(headers[0]
									.getValue());
						} catch (Exception e)
						{
							// e.printStackTrace();
							// 异常可能是空指针异常，数组越界异常，不是整数值异常.
							DLog.e(TAG,
									Thread.currentThread().getName()
											+ " "
											+ downloadInfo.getName()
											+ "DownloadTask.run# Content-Length值不合法 Exception=",
									e);
							error = DownloadInfo.ERROR_HTTP_FAILD;
							break;
						}

						totalLength = filesize + tempContentLength;

						DLog.d(Constants.TAG, Thread.currentThread().getName()
								+ " " + downloadInfo.getName() + " contentLen "
								+ tempContentLength + ", totalLen"
								+ totalLength);
						headers = resp.getAllHeaders();
						if (headers != null)
						{
							for (int i = 0; i < headers.length; i++)
							{
								DLog.e(TAG, Thread.currentThread().getName()
										+ " " + downloadInfo.getName()
										+ headers[i].getName() + "|"
										+ headers[i].getValue());
							}
						}

						// //
						// // 因为代理的原因，有些代理需要登陆，未登录时会返回一个http页面.
						// // 导致安装失败.
						// // 所以假定大小在10k以上的apk才是合法的 。
						// if ((statusCode == 200) && (totalLength < 10 * 1024))
						// {
						// error = DownloadInfo.ERROR_HTTP_FAILD;
						// DLog.d(Constants.TAG,
						// Thread.currentThread().getName()
						// + " "
						// + downloadInfo.getName()
						// +
						// " run step2.4.2.0  strange response maybe caused by proxy.......");
						// break;
						// }

						headers = resp.getHeaders("Content-Type");
						if (headers != null && headers.length >= 1)
						{
							if (!headers[0].getValue().equals(
									"application/vnd.android.package-archive")
									&& !headers[0].getValue().equals(
											"application/octet-stream")
									&& !headers[0].getValue().equals(
											"application/zip"))
							{

								error = DownloadInfo.ERROR_HTTP_FAILD;
								DLog.d(Constants.TAG,
										Thread.currentThread().getName()
												+ " "
												+ downloadInfo.getName()
												+ "strange response with wrong Content-Type("
												+ headers[0].getValue()
												+ ") maybe caused by proxy.......");
								break;
							}
						}

						downloadManager.readLock.lock();
						try
						{
							if (!bHaveSendTaskOverNotification)
							{
								downloadInfo.setContentLength(totalLength);
								downloadInfo
										.setState(DownloadInfo.STATE_DOWNLOADING);
							}
						} catch (Exception e)
						{
							DLog.d(Constants.TAG, "setContentLength|setState",
									e);
						} finally
						{
							downloadManager.readLock.unlock();
						}

						if (canceled)
							break;

						DLog.d(Constants.TAG,
								Thread.currentThread().getName()
										+ " "
										+ downloadInfo.getName()
										+ " run step2.4.2 connect to server http content length "
										+ totalLength + " save to file  "
										+ downloadInfo.getPath());
						// in = resp.getEntity().getContent();
						step = 2;
						DLog.d(Constants.TAG, Thread.currentThread().getName()
								+ " " + downloadInfo.getName()
								+ " write file from " + filesize);
						// if (DLog.isDLog()) XXX
						// {
						Header[] allHeaders = resp.getAllHeaders();
						if (allHeaders != null)
						{
							for (int i = 0; i < allHeaders.length; i++)
							{
								DLog.d(Constants.TAG,
										Thread.currentThread().getName() + " "
												+ downloadInfo.getName()
												+ " header " + i
												+ allHeaders[i].getName() + "="
												+ allHeaders[i].getValue());
							}
							// }
						}
						writeFile(filesize, resp);
					} catch (IOException e)
					{
						if (error == DownloadInfo.ERROR_NA)
						{
							if (step == 1)
							{// 联网失败
								error = DownloadInfo.ERROR_NO_CONNECTION;
							} else if (step == 2)
							{ // 读取数据失败.
								error = DownloadInfo.ERROR_NO_NETWORK;
							} else
							{ // 未知情况。
								error = DownloadInfo.ERROR_NO_NETWORK;
							}
						}
						DLog.d(Constants.TAG, Thread.currentThread().getName()
								+ " " + downloadInfo.getName()
								+ " run step2.4.2.2 " + e.toString());
					}
					DLog.d(Constants.TAG, Thread.currentThread().getName()
							+ " " + downloadInfo.getName()
							+ "run step2.4.4 download operation ret = " + error);
				} while (false);// do...while中的代码只需执行一次，并可能从中间跳出来，使用这种写法实现

				DLog.d(Constants.TAG, Thread.currentThread().getName() + " "
						+ downloadInfo.getName() + " write file end at "
						+ downloadInfo.getDownloadSize());
				if (canceled)
				{
					break;
				}
				if ((error == DownloadInfo.ERROR_NA)
						&& (downloadInfo.getContentLength() == downloadInfo
								.getDownloadSize()))
				{
					break;
				} else
				{
					error = DownloadInfo.ERROR_HTTP_FAILD;
				}

				if (!isAllowedNetwork())
				{
					DLog.d(Constants.TAG,
							Thread.currentThread().getName() + " "
									+ downloadInfo.getName()
									+ "not allowed network type"
									+ Util.getNetMode(downloadManager.context));
					break;
				}

				tryCount--;
				DLog.d(Constants.TAG, Thread.currentThread().getName() + " "
						+ downloadInfo.getName() + " run step2.5 try again ");
			}

			if (canceled)
			{
				if (deleted && downloadInfo.isDeleted() && deleteFile)
				{
					if (downloadInfo.lockForDelete())
					{
						boolean bRet = DownloadTask
								.deleteDownloadInfoFile(downloadInfo);
						DLog.d(Constants.TAG, Thread.currentThread().getName()
								+ " " + downloadInfo.getName() + " "
								+ downloadInfo.getPath()
								+ " 1 file is deleted. ret = " + bRet);
					} else
					{
						DLog.d(Constants.TAG, Thread.currentThread().getName()
								+ downloadInfo.getName() + " 未被删除因为无法获得删除锁。");
					}
				}
			} else
			{
				if ((error == DownloadInfo.ERROR_NA) && !checkFileIntegrity())
				{
					error = DownloadInfo.ERROR_HTTP_FAILD;
					// 删除不完整的文件. 不完整的文件可能有部分文件损毁。
					DLog.d(Constants.TAG, Thread.currentThread().getName()
							+ " " + downloadInfo.getName() + "文件不完整:"
							+ downloadInfo.getPath() + " contentSize="
							+ downloadInfo.getContentLength()
							+ " downloadSize=" + downloadInfo.getDownloadSize()
							+ "|" + System.currentTimeMillis());
					new File(downloadInfo.getPath()).renameTo(new File(
							downloadInfo.getPath() + System.currentTimeMillis()
									+ ".err"));
				}

				if (error == DownloadInfo.ERROR_NA)
				{
					// if (DLog.isDLog())
					// {
					// copyFile(downloadInfo.getPath(),
					// downloadInfo.getPath() + System.currentTimeMillis() +
					// ".bak");
					// }
					sendTaskOverNotification(DownloadInfo.STATE_FINISH);
					DLog.d(Constants.TAG, Thread.currentThread().getName()
							+ " " + downloadInfo.getName()
							+ " run step3.0 downliad ok ");
				} else
				{

					sendTaskOverNotification(DownloadInfo.STATE_ERROR);
					DLog.d(Constants.TAG, Thread.currentThread().getName()
							+ "   " + downloadInfo.getName()
							+ " run step3.0 downliad fail. reason=" + error);
				}
			}

			closeSocketStream();
		} catch (RuntimeException e)
		{
			// e.printStackTrace();
			DLog.e(TAG, "DownloadTask.run# Exception=", e);
			DLog.d(Constants.TAG, Thread.currentThread().getName() + " "
					+ downloadInfo.getName() + " " + e);

			downloadManager.readLock.lock();
			try
			{
				downloadInfo.setError(DownloadInfo.ERROR_RUNTIME_EXCEPTION);
			} catch (Exception e1)
			{
				DLog.d(Constants.TAG, "setError", e1);
			} finally
			{
				downloadManager.readLock.unlock();
			}

			sendTaskOverNotification(DownloadInfo.STATE_ERROR);
		}
		httpGet = null;
		httpClient = null;
		// downloadInfo = null;
		// downloadManager = null;
	}

	private void traceSetPath()
	{
		DLog.d(Constants.TAG,
				Thread.currentThread().getName() + downloadInfo.getName()
						+ " | " + "设置文件路径");
	}

	private void traceTaskBegin()
	{
		DLog.d(Constants.TAG,
				Thread.currentThread().getName() + " run begin "
						+ downloadInfo.getName() + " | "
						+ downloadInfo.getUrl() + " | "
						+ downloadInfo.getPackageName() + "|"
						+ downloadInfo.getPath());
	}

	private boolean checkFileIntegrity()
	{
		if (downloadInfo.getContentLength() != downloadInfo.getDownloadSize())
		{
			return false;
		}

		// if (downloadInfo.getContentLength() < 10 * 1024)
		// {
		// return false;
		// }

		try
		{
			ZipFile zip = new ZipFile(downloadInfo.getPath());
			zip.close();
		} catch (IOException e)
		{
			// 文件不合法,
			DLog.e(Constants.TAG,
					Thread.currentThread().getName() + "   "
							+ downloadInfo.getName() + " 不合法的zip文件:"
							+ downloadInfo.getPath());
			return false;
		}

		return true;
	}

	private int readData(InputStream in, byte buf[]) throws IOException
	{
		int ret = -1;
		int remindCnt = buf.length;
		int offset = 0;

		while ((ret = in.read(buf, offset, remindCnt)) != -1)
		{
			offset += ret;
			remindCnt -= ret;
			if (offset == buf.length)
			{
				break;
			}

		}

		return offset > 0 ? offset : -1;
	}

	private void writeFile(long filesize, HttpResponse resp) throws IOException
	{
		in = resp.getEntity().getContent();
		long recvLenght = downloadInfo.getDownloadSize();
		if (fout != null)
		{
			try
			{
				fout.close();
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				// e.printStackTrace();
				DLog.e(TAG, "writeFile# Exception=", e);
			}
		}

		fout = new RandomAccessFile(downloadInfo.getPath(), "rw");
		fout.seek(filesize);
		DLog.i(Constants.TAG, Thread.currentThread().getName() + " "
				+ downloadInfo.getName() + "文件位置:" + fout.getFilePointer());

		byte buf[] = new byte[1024 * 32];

		int rLen = readData(in, buf);// in.read(buf);

		int nextUpdateProgress = downloadInfo.getPercent() + 1;

		while (rLen != -1)
		{
			if (recvLenght == 0)
			{
				// 忽略了rLen等于1的情况
				if (rLen >= 2 && (buf[0] != 'P' || buf[1] != 'K'))
				{
					DLog.e(Constants.TAG, Thread.currentThread().getName()
							+ " " + downloadInfo.getName() + "前两个字节PK");
				}
			}

			DLog.d(Constants.TAG, Thread.currentThread().getName() + " "
					+ downloadInfo.getName() + "run step2.4.3 recv data len "
					+ rLen);
			recvLenght += rLen;
			try
			{
				// if (DLog.isDLog()) TODO
				// {
				// if (rLen > 5)
				// {
				// DLog.d(Constants.TAG,
				// Thread.currentThread().getName()
				// + " "
				// + downloadInfo.getName()
				// + " at location"
				// + fout.getFilePointer()
				// + " is "
				// + String.format("%02x%02x%02x%02x%02x",
				// buf[0], buf[1], buf[2], buf[3],
				// buf[4]));
				// }
				// }
				fout.write(buf, 0, rLen);
			} catch (Exception e)
			{
				error = DownloadInfo.ERROR_WRITE_FAILD;
				throw new IOException(e.toString());
			}

			downloadManager.readLock.lock();
			try
			{
				if (!bHaveSendTaskOverNotification)
				{
					downloadInfo.setDownloadSize(recvLenght);

					if (downloadInfo.getPercent() >= nextUpdateProgress)
					{

						sendTaskProgressNotification();

						nextUpdateProgress = downloadInfo.getPercent() + 1;
						downloadManager.updateTaskProgress(downloadInfo);
						DLog.d(Constants.TAG, Thread.currentThread().getName()
								+ " " + downloadInfo.getName()
								+ " update progress into db with percent"
								+ downloadInfo.getPercent());
					}

					// if (DLog.isDLog()) TODO
					// {
					// DLog.d(Constants.TAG,
					// Thread.currentThread().getName() + " "
					// + downloadInfo.getName()
					// + " run step2.4.3.1 save data len "
					// + rLen + " progress "
					// + downloadInfo.getPercent() + ","
					// + recvLenght + ","
					// + downloadInfo.getContentLength());
					// DLog.d(Constants.TAG,
					// Thread.currentThread().getName() + " "
					// + downloadInfo.getName() + "文件位置:"
					// + fout.getFilePointer());
					// }
				}
			} catch (Exception e1)
			{
				DLog.d(Constants.TAG, "writeFile", e1);
			} finally
			{
				downloadManager.readLock.unlock();
			}

			if (canceled)
				break;

			rLen = readData(in, buf);

		}

		DLog.d(Constants.TAG, Thread.currentThread().getName() + " "
				+ downloadInfo.getName() + "读取结束 rLen=" + rLen);
		buf = null;
		fout.close();
		in.close();

	}

	private void setUpHttpConnection(long filesize)
			throws MalformedURLException, IOException
	{
		httpGet = new HttpGet(downloadInfo.getUrl());
		if (filesize > 0)
		{
			httpGet.addHeader("Range", "bytes=" + filesize + "-");
		}
		httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 120000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				120000);

		HttpHost host = downloadManager.getHttpProxyHost();
		if (host != null)
		{
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					host);
		}

		DLog.d(Constants.TAG, Thread.currentThread().getName() + " "
				+ downloadInfo.getName() + " run step2.4 connect to server ");
	}

	private void setDownloadFilePath()
	{
		if (downloadInfo.getPath() != null)
		{
			File f = new File(downloadInfo.getPath());
			if (!f.exists())
			{
				downloadInfo.setPath(null);
			}
		}
		if (downloadInfo.getPath() == null)
		{

			downloadInfo.setPath(getFileName());
		}
	}

	private void closeSocketStream()
	{

		if (httpClient != null)
		{
			httpClient.getConnectionManager().shutdown();
		}

		if (in != null)
		{
			try
			{
				in.close();
			} catch (Exception e) // httpClient会产生数组越界异常。
			{
				// e.printStackTrace();
				DLog.e(TAG, "closeSocketStream# Exception=", e);
			}
		}

		if (fout != null)
		{
			try
			{
				fout.close();
			} catch (IOException e)
			{
				// e.printStackTrace();
				DLog.e(TAG, "closeSocketStream# Exception=", e);
			}

		}
	}

	/**
	 * 取消下载
	 */
	public void cancel()
	{
		if (canceled)
			return;
		DLog.i(Constants.TAG,
				"user canceled download task." + downloadInfo.getName());

		canceled = true;
		if (downloadInfo.isDeleted())
		{
			deleted = true;

			if (!running)
			{
				boolean bRet = DownloadTask
						.deleteDownloadInfoFile(downloadInfo);
				DLog.d(Constants.TAG, Thread.currentThread().getName()
						+ downloadInfo.getName() + " 2 delete ret " + bRet);
			}
		}
		downloadInfo.setCanceled(true);
		sendTaskOverNotification(DownloadInfo.STATE_STOP);

		closeSocketStream();
	}

	public void delete(boolean bDeleteFile)
	{
		if (deleted)
			return;
		DLog.i(Constants.TAG,
				"user delete download task." + downloadInfo.getName());
		canceled = true;
		deleted = true;
		if (bDeleteFile)
		{
			deleteFile = true;
		}
		downloadInfo.setDeleted(true);
		downloadInfo.setCanceled(true);

		if ((!running || bHaveSendTaskOverNotification) && bDeleteFile)
		{
			if (downloadInfo.lockForDelete())
			{
				boolean bRet = DownloadTask
						.deleteDownloadInfoFile(downloadInfo);
				DLog.d(Constants.TAG, Thread.currentThread().getName()
						+ downloadInfo.getName() + " 2 delete ret " + bRet);
			} else
			{
				DLog.d(Constants.TAG, Thread.currentThread().getName()
						+ downloadInfo.getName() + " 未被删除因为无法获得删除锁。");
			}
		}

		sendTaskOverNotification(DownloadInfo.STATE_STOP);
		closeSocketStream();

	}

	// private String getDefaultDownloadDir(){
	// return downloadManager.getDownloadDirectory().getAbsolutePath();
	// }

	private String getFileName()
	{
		if (downloadInfo == null)
			throw new IllegalArgumentException("downloadInfo is null.");

		File dir = downloadManager.getDownloadDirectory(downloadInfo);
		if (dir == null)
			return null;

		String url = downloadInfo.getUrl();
		if (url == null)
			throw new IllegalArgumentException("download url is null.");
		String tempFilename = "";
		String lowerUrl = url.toLowerCase();

		// TODO 因为对接豌豆荚，文件名太长和包含特殊字符创建不了文件注释掉
		// if (lowerUrl.endsWith(".apk") || lowerUrl.endsWith(".zip"))
		// {
		// tempFilename = url.substring(url.lastIndexOf('/') + 1);
		// // FIXME tempFilename 有可能不是一个有效的文件名
		// }
		// else
		// {
		String endFilename = ".apk";
		if (lowerUrl.endsWith(".zip"))
		{
			endFilename = ".zip";
		}
		DLog.d("denglihua", "文件存储后缀名" + endFilename);
		try
		{
			MessageDigest dig = MessageDigest.getInstance("md5");
			dig.update(downloadInfo.getPackageName().getBytes());
			dig.update("|".getBytes());
			dig.update(url.getBytes());

			byte res[] = dig.digest();
			tempFilename = downloadInfo.getPackageName()
					+ String.format(
							"%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x"
									+ endFilename, res[0], res[1], res[2],
							res[3], res[4], res[5], res[6], res[7], res[8],
							res[9], res[10], res[11], res[12], res[13],
							res[14], res[15]);
		} catch (NoSuchAlgorithmException e)
		{
			// e.printStackTrace();
			DLog.e(TAG, "getFileName# Exception=", e);
		}
		// }
		DLog.e(TAG, downloadInfo.getName() + "| path=" + dir.getAbsolutePath()
				+ File.separator + tempFilename);
		return dir.getAbsolutePath() + File.separator + tempFilename;
	}

	private void sendTaskOverNotification(int event)
	{
		// 该方法总调用downloadManager方法会同步。
		downloadManager.writeLock.lock();
		try
		{
			if (!bHaveSendTaskOverNotification)
			{
				bHaveSendTaskOverNotification = true;

				downloadInfo.setState(event);
				downloadInfo.setError(error);
				if (event == DownloadInfo.STATE_STOP && deleted)
				{ // 任务被删除不发送通知，因为任务可能是已经完成的。

				} else
				{
					downloadManager.downloadEventOccur(event, downloadInfo);
				}
			}
		} catch (Exception e1)
		{
			DLog.d(Constants.TAG, "sendTaskOverNotification", e1);
		} finally
		{
			downloadManager.writeLock.unlock();
		}
	}

	private void sendTaskProgressNotification()
	{
		downloadManager.readLock.lock();
		try
		{
			if (!bHaveSendTaskOverNotification)
			{
				downloadManager.downloadProgressEventOccur(
						downloadInfo.getPercent(), downloadInfo);
			}
		} catch (Exception e1)
		{
			DLog.d(Constants.TAG, "sendTaskProgressNotification", e1);
		} finally
		{
			downloadManager.readLock.unlock();
		}
	}

	private void sendTaskDownloadingNotification(int event)
	{
		downloadManager.writeLock.lock();
		try
		{
			if (!bHaveSendTaskOverNotification)
			{
				downloadInfo.setState(event);
				downloadManager.downloadEventOccur(event, downloadInfo);
			}
		} catch (Exception e1)
		{
			DLog.d(Constants.TAG, "sendTaskDownloadingNotification", e1);
		} finally
		{
			downloadManager.writeLock.unlock();
		}

	}

	public static void copyFile(String f1, String f2)
	{
		try
		{
			int length = 8 * 1024;
			FileInputStream in = new FileInputStream(f1);
			FileOutputStream out = new FileOutputStream(f2);
			byte[] buffer = new byte[length];
			while (true)
			{
				int ins = in.read(buffer);
				if (ins == -1)
				{
					in.close();
					out.flush();
					out.close();
				} else
					out.write(buffer, 0, ins);
			}
		} catch (Exception e)
		{
			//
		}
	}

	private boolean isAllowedNetwork()
	{
		if (downloadManager == null)
			return false;

		if (downloadManager.isDownloadOnlyInWifi()
				&& (Util.getNetType(downloadManager.context) != Util.WIFI_INT))
		{
			return false;
		}

		return true;
	}

	// static boolean deleteDownloadInfoFile(DownloadInfo aDownloadInfo){
	// if(aDownloadInfo == null) return false;
	// DLog.d(Constants.TAG, "delete task state"+ aDownloadInfo.getState());
	// if(aDownloadInfo.getState() == DownloadInfo.STATE_FINISH){
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	// new File(aDownloadInfo.getPath()).renameTo(new
	// File(aDownloadInfo.getPath() + sdf.format(new Date()) + "del"));
	// DLog.d(Constants.TAG, "not delete and just rename the finished task "+
	// aDownloadInfo.getName() + "|" + aDownloadInfo.getPackageName());
	// }else {
	// DLog.d(Constants.TAG, "delete unfinished task "+ aDownloadInfo.getName()
	// + "|" + aDownloadInfo.getPackageName());
	// return new File(aDownloadInfo.getPath()).delete();
	// }
	// return true;
	// }

	static boolean deleteDownloadInfoFile(DownloadInfo aDownloadInfo)
	{
		if (aDownloadInfo.getPath() != null)
		{
			return new File(aDownloadInfo.getPath()).delete();
		}
		return false;
	}

}
