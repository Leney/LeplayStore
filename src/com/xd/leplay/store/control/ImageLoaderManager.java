package com.xd.leplay.store.control;

import java.io.File;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import android.widget.ImageView;

import com.xd.base.util.DLog;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 图片加载管理类
 * 
 * @author lilijun
 * 
 */
public class ImageLoaderManager
{

	private static ImageLoaderManager imageLoaderManager = null;

	private LeplayPreferences preferences = null;

	private ImageLoader imageLoader = null;

	// private enum NetWorkState
	// {
	// /** wifi */
	// WIFI,
	// /** 速度快的移动网络，例如3g、4g */
	// FAST_MOBILE,
	// /** 速度慢的移动网络，例如GPRS */
	// SLOW_MOBILE,
	// /** 网络不可用 */
	// NONE;
	// }

	// private NetWorkState nowWorkState = NetWorkState.NONE;

	// 监听网络变化广播
	// private CheckIsWifeModeReceiver checkIsWifeModeReceiver;

	// /**
	// * 设置图片加载配置
	// *
	// * @return
	// */
	// public DisplayImageOptions getImageLoaderOptions()
	// {
	// DisplayImageOptions options = new DisplayImageOptions.Builder()
	// .showImageOnLoading(R.drawable.default_icon)
	// .showImageForEmptyUri(R.drawable.default_icon)
	// .showImageOnFail(R.drawable.default_icon).cacheInMemory(true)
	// .cacheInMemory(true).build();
	// return options;
	// }

	// /**
	// * 设置是否显示
	// *
	// * @param isShowImage
	// */
	// public void setShowImage(boolean isShowImage)
	// {
	// if (preferences != null)
	// {
	// this.isShowImage = isShowImage;
	// preferences.setNoPicModel(isShowImage);
	// }
	//
	// }

	// /**
	// * 设置是否显示
	// *
	// * @param getShowImage
	// */
	// public boolean getShowImage()
	// {
	// // if (preferences != null)
	// // {
	// // if (nowWorkState == NetWorkState.WIFI)
	// // {
	// // // 如果是wifi网络环境下 则直接显示
	// // return true;
	// // } else if (nowWorkState == NetWorkState.FAST_MOBILE)
	// // {
	// // return preferences.isShowIcon();
	// // }
	// // }
	// if (preferences != null)
	// {
	// return preferences.isNoPicModel();
	// }
	// return true;
	// }

	/**
	 * 获取单实例
	 * 
	 * @param context
	 * @return
	 */
	public static ImageLoaderManager getInstance()
	{
		if (imageLoaderManager == null)
		{
			imageLoaderManager = new ImageLoaderManager();
		}
		return imageLoaderManager;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context)
	{
		// initImageLoader2(context);
		ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(context));
		preferences = LeplayPreferences.getInstance(context);
		imageLoader = ImageLoader.getInstance();
		// isNoPicModel = preferences.isNoPicModel();
		// initNetWorkState(context);

		// 注册监听网络变化广播
		// checkIsWifeModeReceiver = new CheckIsWifeModeReceiver();
		// context.registerReceiver(checkIsWifeModeReceiver, new IntentFilter(
		// "android.net.conn.CONNECTIVITY_CHANGE"));
	}

	/**
	 * 初始化图片加载器
	 * 
	 * @param context
	 */
	private void initImageLoader(Context context)
	{
		int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);

		MemoryCache memoryCache;
		if (Build.VERSION.SDK_INT >= 9)
		{
			memoryCache = new LruMemoryCache(memoryCacheSize);
		} else
		{
			memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
		}

		// File cacheDir = getCacheDirectory(context);
		// ImageLoaderConfiguration config = new
		// ImageLoaderConfiguration.Builder(
		// context)
		// .threadPriority(Thread.NORM_PRIORITY - 2)
		// // 线程池内加载的数量
		// .threadPoolSize(3)
		// // 内存缓存的最大值
		// .memoryCacheSize(2 * 1024 * 1024)
		// .memoryCache(memoryCache)
		// // 超时时间
		// .imageDownloader(
		// new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
		// .denyCacheImageMultipleSizesInMemory()
		// // 自定义缓存路径
		// .diskCache(new UnlimitedDiscCache(cacheDir))
		// .tasksProcessingOrder(QueueProcessingType.LIFO).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// 线程池内加载的数量
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// 内存缓存的最大值
				.memoryCacheSize(2 * 1024 * 1024).memoryCache(memoryCache)
				.build();
		ImageLoader.getInstance().init(config);
	}

	private void initImageLoader2(Context paramContext)
	{
		int i = (int) (Runtime.getRuntime().maxMemory() / 8L);
		if (Build.VERSION.SDK_INT >= 9)
			;
		for (Object localObject = new LruMemoryCache(i);; localObject = new LRULimitedMemoryCache(
				i))
		{
			// File localFile = Util.getCacheDirectory(paramContext);
			// DLog.v("lung", localFile + "");
			ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(
					paramContext).threadPriority(3)
					.denyCacheImageMultipleSizesInMemory()
					.tasksProcessingOrder(QueueProcessingType.LIFO).build();
			ImageLoader.getInstance().init(localImageLoaderConfiguration);
			return;
		}
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView
	 * when it's turn. NOTE: init(ImageLoaderConfiguration) method must be
	 * called before this method call
	 * 
	 * @param uri
	 *            Image URI (i.e. "http://site.com/image.png",
	 *            "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            ImageView which should display image
	 */
	public void displayImage(String uri, ImageView imageView)
	{
		displayImage(uri, imageView, null, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView
	 * when it's turn. NOTE: init(ImageLoaderConfiguration) method must be
	 * called before this method call
	 * 
	 * @param uri
	 *            Image URI (i.e. "http://site.com/image.png",
	 *            "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            ImageView which should display image
	 * @param option
	 *            Display image options for image displaying. If null - default
	 *            display image options from configuration will be used.
	 */
	public void displayImage(String uri, ImageView imageView,
			DisplayImageOptions options)
	{
		displayImage(uri, imageView, options, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView
	 * when it's turn. NOTE: init(ImageLoaderConfiguration) method must be
	 * called before this method call
	 * 
	 * @param uri
	 *            Image URI (i.e. "http://site.com/image.png",
	 *            "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            ImageView which should display image
	 * @param listener
	 *            Listener for image loading process. Listener fires events on
	 *            UI thread.
	 */
	public void displayImage(String uri, ImageView imageView,
			ImageLoadingListener listener)
	{
		displayImage(uri, imageView, null, listener);
	}


	/**
	 * 显示drawable中数据
	 * @param resId
	 * @param imageView
     */
	public void displayImageRes(String resId,ImageView imageView){
		imageLoader.displayImage("drawable://" + resId,imageView);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView
	 * when it's turn. NOTE: init(ImageLoaderConfiguration) method must be
	 * called before this method call
	 * 
	 * @param uri
	 *            Image URI (i.e. "http://site.com/image.png",
	 *            "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            ImageView which should display image
	 * @param option
	 *            Display image options for image displaying. If null - default
	 *            display image options from configuration will be used.
	 * @param listener
	 *            Listener for image loading process. Listener fires events on
	 *            UI thread.
	 */
	public void displayImage(String uri, ImageView imageView,
			DisplayImageOptions option, ImageLoadingListener listener)
	{
		// if (nowWorkState == NetWorkState.WIFI)
		// {
		// imageLoader.displayImage(uri, imageView, option, listener);
		// } else if (isShowImage && nowWorkState == NetWorkState.FAST_MOBILE)
		// {
		// imageLoader.displayImage(uri, imageView, option, listener);
		// } else
		// {
		// imageLoader.displayImage(null, imageView, option, listener);
		// }

		if (preferences.isNoPicModel())
		{
			imageLoader.displayImage(null, imageView, option, listener);
		} else
		{
			imageLoader.displayImage(uri, imageView, option, listener);
		}
	}

	public void loadImage(String uri, DisplayImageOptions option,
			ImageLoadingListener listener)
	{
		// if (nowWorkState == NetWorkState.WIFI)
		// {
		// imageLoader.loadImage(uri, option, listener);
		// } else if (isShowImage && nowWorkState == NetWorkState.FAST_MOBILE)
		// {
		// imageLoader.loadImage(uri, option, listener);
		// } else
		// {
		// imageLoader.loadImage(null, option, listener);
		// }

		if (preferences.isNoPicModel())
		{
			imageLoader.loadImage(null, option, listener);
		} else
		{
			imageLoader.loadImage(uri, option, listener);
		}
	}

	/**
	 * 此方法不受是否显示图片设置限制 始终都会显示图片
	 * 
	 * @param uri
	 * @param imageView
	 * @param option
	 * @param listener
	 */
	public void displayImage2(String uri, ImageView imageView,
			DisplayImageOptions option, ImageLoadingListener listener)
	{
		imageLoader.displayImage(uri, imageView, option, listener);
	}

	/**
	 * 获取bitmap对象
	 * 
	 * @param uri
	 * @return
	 */
	public Bitmap loadImageSync(String uri)
	{
		return imageLoader.loadImageSync(uri);
	}

	public class CheckIsWifeModeReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{

			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent
					.getAction()))
			{
				// initNetWorkState(context);
			}
		}

	}

	// private void initNetWorkState(Context context)
	// {
	// ConnectivityManager connManager = (ConnectivityManager) context
	// .getSystemService(Context.CONNECTIVITY_SERVICE);
	// NetworkInfo info = connManager.getActiveNetworkInfo();
	// if (info == null)
	// {
	// nowWorkState = NetWorkState.NONE;
	// return;
	// }
	// boolean isNetworkValid = info.isConnected();
	//
	// if (isNetworkValid)
	// {
	// // 如果是wifi
	// if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI)
	// {
	// nowWorkState = NetWorkState.WIFI;
	// } else if (isFastMobileNetwork(context))
	// {
	// nowWorkState = NetWorkState.FAST_MOBILE;
	// } else
	// {
	// nowWorkState = NetWorkState.SLOW_MOBILE;
	// }
	// } else
	// {
	// nowWorkState = NetWorkState.NONE;
	// }
	// }

	// /**
	// * @Title: isFastMobileNetwork
	// * @Description:是否是快速移动网络
	// * @param @param context
	// * @param @return
	// * @return boolean
	// * @throws
	// */
	// private static boolean isFastMobileNetwork(Context context)
	// {
	// return true;
	// // TelephonyManager telTephonyManager = (TelephonyManager) context
	// // .getSystemService(Context.TELEPHONY_SERVICE);
	// // int netType = telTephonyManager.getNetworkType();
	// // switch (netType)
	// // {
	// // case TelephonyManager.NETWORK_TYPE_1xRTT:
	// // return false; // ~ 50-100 kbps
	// // case TelephonyManager.NETWORK_TYPE_CDMA:
	// // return false; // ~ 14-64 kbps
	// // case TelephonyManager.NETWORK_TYPE_EDGE:
	// // return false; // ~ 50-100 kbps
	// // case TelephonyManager.NETWORK_TYPE_EVDO_0:
	// // return true; // ~ 400-1000 kbps
	// // case TelephonyManager.NETWORK_TYPE_EVDO_A:
	// // return true; // ~ 600-1400 kbps
	// // case TelephonyManager.NETWORK_TYPE_GPRS:
	// // return false; // ~ 100 kbps
	// // case TelephonyManager.NETWORK_TYPE_HSDPA:
	// // return true; // ~ 2-14 Mbps
	// // case TelephonyManager.NETWORK_TYPE_HSPA:
	// // return true; // ~ 700-1700 kbps
	// // case TelephonyManager.NETWORK_TYPE_HSUPA:
	// // return true; // ~ 1-23 Mbps
	// // case TelephonyManager.NETWORK_TYPE_UMTS:
	// // return true; // ~ 400-7000 kbps
	// // case TelephonyManager.NETWORK_TYPE_EHRPD:
	// // return true; // ~ 1-2 Mbps
	// // case TelephonyManager.NETWORK_TYPE_EVDO_B:
	// // return true; // ~ 5 Mbps
	// // case TelephonyManager.NETWORK_TYPE_HSPAP:
	// // return true; // ~ 10-20 Mbps
	// // case TelephonyManager.NETWORK_TYPE_IDEN:
	// // return false; // ~25 kbps
	// // case TelephonyManager.NETWORK_TYPE_LTE:
	// // return true; // ~ 10+ Mbps
	// // case TelephonyManager.NETWORK_TYPE_UNKNOWN:
	// // return false;
	// // default:
	// // return false;
	// // }
	// }

	/**
	 * @param context
	 * @return
	 */
	public static File getCacheDirectory(Context context)
	{
		File appCacheDir = null;
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
		{
			appCacheDir = getExternalCacheDir(context);
		}
		if (appCacheDir == null)
		{
			appCacheDir = context.getCacheDir();
		}
		return appCacheDir;
	}

	/**
	 * @param context
	 * @return
	 */
	private static File getExternalCacheDir(Context context)
	{
		File appCacheDir = new File(new File(
				Environment.getExternalStorageDirectory(), "donson"),
				"imgcache");
		// File appCacheDir = new File(new File(dataDir,
		// context.getPackageName()), "cache");
		if (!appCacheDir.exists())
		{
			if (!appCacheDir.mkdirs())
			{
				DLog.i("Unable to create external cache directory");
				return null;
			}
			try
			{
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException e)
			{
				DLog.i("Can't create \".nomedia\" file in application external cache directory");
			}
		}
		return appCacheDir;
	}

	/**
	 * 清除缓存图片资源
	 */
	public void clearMemoryCache()
	{
		imageLoader.clearMemoryCache();
		imageLoader.clearDiskCache();
	}
}
