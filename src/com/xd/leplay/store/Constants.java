package com.xd.leplay.store;

public class Constants
{
	// /** 网络数据请求的主地址(测试服务器) */
	// public static final String APP_API_URL = "http://app.api.test.aiwan.hk/";
	//
	// /** 账户数据请求的主地址(测试服务器) */
	// public static final String UAC_API_URL = "http://uac.api.test.aiwan.hk/";
	//
	// /** 每日抽奖的URL(测试版本的抽奖地址) */
	// public static final String LOTTERY_URL =
	// "http://raffle.test.aiwan.hk/luck?uid=";
	//
	// /** 上传头像的URL(测试版本) */
	// public static final String UPLOAD_PIC_URL =
	// "http://app.api.test.aiwan.hk/upload";
	//
	// /** 上报必须采集的数据信息的URL(测试地址) */
	// public static final String REPORT_COLLECTION_DATA_URL =
	// "http://report.api.test.aiwan.hk/report";
	//
	// /** 百度推送的api_key值(测试) */
	// public static final String BAI_DU_PUSH_API_KEY_VALUE =
	// "0bjHK48s21Gm6prfcrqpjyDW";
	//
	// /** 微信app_id值(测试) */
	// public static final String WEIXIN_APP_ID = "wx026738b47d7af658";

	// /** 网络数据请求的主地址(正式版本服务器地址) */
	// public static final String APP_API_URL = "http://app.api.aiwan.hk/";
	//
	// /** 账户数据请求的主地址(正式版本服务器地址) */
	// public static final String UAC_API_URL = "http://uac.api.aiwan.hk/";
	//
	// /** 每日抽奖的URL(正式版本的抽奖地址) */
	// public static final String LOTTERY_URL =
	// "http://raffle.aiwan.hk/luck?uid=";
	//
	// /** 上传头像的URL(正式版本) */
	// public static final String UPLOAD_PIC_URL =
	// "http://app.api.aiwan.hk/upload";
	//
	// /** 上报必须采集的数据信息的URL(正式地址) */
	// public static final String REPORT_COLLECTION_DATA_URL =
	// "http://report.api.aiwan.hk/report";
	//
	// /** 百度推送的api_key值(正式) */
	// public static final String BAI_DU_PUSH_API_KEY_VALUE =
	// "cNwoSFjr5RSp1cVQVAI7cNSU";
	//
	// // /** 微信app_id值(正式) */
	// // public static final String WEIXIN_APP_ID = "wx6752da4be3086c3f";
	// /** 提现奖励网页路径 */
	// public static final String FRIEND_BACK_MONEY_URL =
	// "http://www.aiwan.hk/haoyoutixianjiangli.html";

	/** 网络数据请求的主地址(正式版本服务器地址) */
	public static String APP_API_URL = "";

	/** 账户数据请求的主地址(正式版本服务器地址) */
	public static String UAC_API_URL = "";

	/** 每日抽奖的URL(正式版本的抽奖地址) */
	public static String LOTTERY_URL = "";

	/** 上传头像的URL(正式版本) */
	public static String UPLOAD_PIC_URL = "";

	/** 上报必须采集的数据信息的URL(正式地址) */
	public static String REPORT_COLLECTION_DATA_URL = "";

	/** 百度推送的api_key值(正式) */
	public static String BAI_DU_PUSH_API_KEY_VALUE = "";

	/** 提现奖励网页路径 */
	public static String FRIEND_BACK_MONEY_URL = "";

	/** 微信app_id值(测试) */
	public static String WEIXIN_APP_ID = "";

	/** 微信app_secret值 */
	public static String WEIXIN_APP_SECRET = "";

	 /** 科大讯飞的AppId */
	 public static String KDXF_APP_ID = "58d9d690";

	// /** 大头鸟积分墙的AppKey */
	// public static String DTN_APP_KEY = "";

	/** 爱玩商店apk下载地址 */
	public static String AIWAN_APK_DOWNLOAD_URL = "";
	
	/** 领取红包后分享连接*/
	public static String RED_PACKET_SHARE_URL="";

	/** 接收到全局的软件安装成功广播之后所转发的广播 */
	public static final String ACTION_INSTALLED_SOFTWARE_SUCCESS = "com.donson.leplay.store.ACTION_INSTALLED_SOFTWARE_SUCCESS";

	/** 接收到全局的软件卸载成功广播之后所转发的广播 */
	public static final String ACTION_UNINSTALLED_SOFTWARE_SUCCESS = "com.donson.leplay.store.ACTION_UNINSTALLED_SOFTWARE_SUCCESS";

	/** 软件管理中、在接收到全局的软件安装成功的转播广告之后、处理完本地逻辑之后，再次转播的安装成功广播 */
	public static final String ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS = "com.donson.leplay.store.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS";

	/** 软件管理中、在接收到全局的软件卸载成功的转播广告之后、处理完本地逻辑之后，再次转播的卸载成功广播 */
	public static final String ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS = "com.donson.leplay.store.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS";

	/** 软件管理中、本地的所有初始化操作都已经完成的广播 */
	public static final String ACTION_SOFTWARE_MANAGER_INIT_DONE = "com.donson.leplay.store.ACTION_SOFTWARE_MANAGER_INIT_DONE";

	/** 软件管理中、从网络获取可更新应用列表已经完成的广播 */
	public static final String ACTION_SOFTWARE_MANAGER_GET_UPDATE_LIST_FROM_NETWORK_FINISH = "com.donson.leplay.store.ACTION_SOFTWARE_MANAGER_GET_UPDATE_LIST_FROM_NETWORK_FINISH";

	/** 软件管理中、当卸载软件时，如果软件下载任务列表中有关于卸载软件的下载任务、则转播此广播 */
	public static final String ACTION_UNINSTALLED_SOFTWARE_HAVE_DOWNLOADINFO = "com.donson.leplay.store.ACTION_UNINSTALLED_SOFTWARE_HAVE_DOWNLOADINFO";

	/** 软件更新界面、忽略或取消忽略应用，发送此广播 */
	public static final String ACTION_UPDATE_ACTIVITY_IGNORE_OR_CANCLE_IGNORE_APP = "com.donson.leplay.store.ACTION_UPDATE_ACTIVITY_IGNORE_OR_CANCLE_IGNORE_APP";

	/** 关于账户的信息发送变化(包括登录和登出帐号、礼包数量等)，发送此广播 */
	public static final String ACTION_ACCOUNT_HAVE_MODIFY = "com.donson.leplay.store.ACTION_ACCOUNT_HAVE_MODIFY";

	/** 登录成功，发送此广播 */
	public static final String ACTION_LOGIN_SUCCESS = "com.donson.leplay.store.ACTION_LOGIN_SUCCESS";

	/** 用户自己的信息发生改变(用户头像、昵称等所属信息发生改变)，发送此广播 */
	public static final String ACTION_LOGINED_USER_INFO_HAVE_MODIFY = "com.donson.leplay.store.ACTION_LOGINED_USER_INFO_HAVE_MODIFY";

	/** 用户需要重新登录时，发送此广播 */
	public static final String ACTION_USER_NEED_RELOGIN = "com.donson.leplay.store.ACTION_USER_NEED_RELOGIN";

	/** 当下载任务池中的下载任务条数发生改变的时候，发送此广播 */
	public static final String ACTION_DOWNLOAD_TASK_COUNT_CHANGE = "com.donson.leplay.store.ACTION_DOWNLOAD_TASK_COUNT_CHANGE";

	/** 软件更新提示的通知栏，点击 "一键更新" 按钮，发送此广播 */
	public static final String ACTION_NOTIFY_UPDATE_ALL_BTN_CLICK = "com.donson.leplay.store.ACTION_NOTIFY_UPDATE_ALL_BTN_CLICK";

	/** 无图省流量模式时，发送此广播 */
	public static final String ACTION_NO_PIC_MODEL_CHANGE = "com.donson.leplay.store.ACTION_NO_PIC_MODEL_CHANGE";

	/** 获取常量数据完成，发送此广播 */
	public static final String ACTION_GET_CONSTANT_VALUE_FINISH = "com.donson.leplay.store.ACTION_GET_CONSTANT_VALUE_FINISH";

	/** 获取公告消息数据完成，发送此广播 */
	public static final String ACTION_GET_SYSTEM_MSG_FINISH = "com.donson.leplay.store.ACTION_GET_SYSTEM_MSG_FINISH";

	/** 第三方应用发送到微信的请求处理后的响应结果,发送次广播 */
	public static final String ACTION_WEIXIN_LOGIN_RESP_CODE = "com.donson.leplay.store.ACTION_WEIXIN_LOGIN_RESP_CODE";

	/** 获取是否强制升级的结果返回,发送此广播 */
	public static final String ACTION_GET_FORCE_UPGREAD_RESULT = "com.donson.leplay.store.ACTION_GET_FORCE_UPGREAD_RESULT";

	// /** 微信登录成功,发送次广播*/
	// public static final String
	// ACTION_WEIXIN_LOGIN_RESPONSE_STATUS="com.donson.leplay.store.ACTION_WEIXIN_LOGIN_RESPONSE_STATUS";
	//
	// /** 微信绑定成功,发送次广播*/
	// public static final String ACTION_WEIXIN_BIND_RESPONSE_STATUS
	// ="com.donson.leplay.store.ACTION_WEIXIN_BIND_RESPONSE_STATUS";

	/** 下载按钮的状态值 */
	/** 下载状态：等待 */
	public static final int STATE_WAIT = 0;
	/** 下载状态：正在下载 */
	public static final int STATE_DOWNLOADING = 1;
	/** 下载状态：停止 */
	public static final int STATE_STOP = 2;
	/** 下载状态：完成 */
	public static final int STATE_FINISH = 3;
	/** 下载状态：下载出错 */
	public static final int STATE_ERROR = 4;
	/** 非下载状态：未安装 */
	public static final int STATE_NORMAL = 5;
	/** 非下载状态：已安装且有更新 */
	public static final int STATE_UPDATE = 6;
	/** 非下载状态： 已安装且无更新 */
	public static final int STATE_INSTALLED = 7;

	/** app详情的通知栏ID */
	public static final int APP_DETAILS_NOTIFY_ID = 1000001;

	/** 跳转到网页的通知栏ID */
	public static final int WEB_VIEW_NOTIFY_ID = 1000002;

	/** 商店升级通知栏ID */
	public static final int UPGREAD_NOTIFY_ID = 1000003;

	/** 登录的用户信息缓存文件的名称 */
	public static final String LOGINED_USER_INFO_CANCHE_FILE_NAME = "logined_user_info_canche_file_name";

	/** 常量信息缓存文件的名称 */
	public static final String CONSTANT_INFO_CANCHE_FILE_NAME = "constant_info_canche_file_name";

	/** 用户赞的信息缓存文件的名称 */
	public static final String PRAISE_INFO_CANCHE_FILE_NAME = "praise_info_canche_file_name";

	/** 数据采集的信息缓存文件的名称 */
	public static final String DATA_COLLECTION_INFO_CANCHE_FILE_NAME = "data_collection_info_canche_file_name";

	/** 用户下载并安装app成功之后 保存的需要上报的app信息缓存文件的名称 */
	public static final String DOWNLOAD_INSTALL_SUCCESS_APP_INFO_CANCHE_FILE_NAME = "download_install_success_info_canche_file_name";

	/** etag信息缓存文件的名称 */
	public static final String ETAG_CANCLE_FILE_NAME = "etag_cancle_file_name";

	/** 搜索历史记录缓存文件的名称 */
	public static final String SEARCH_HISTORY_CANCHE_FILE_NAME = "search_history_canche_file_name";

	/** 获取第一步的code后，请求以下链接获取access_token */
	public static String GET_WEIXIN_CODE_REQUEST_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

	/** 获取用户个人信息 url */
	public static String GET_WEIXIN_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
}
