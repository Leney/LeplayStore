package com.xd.leplay.store.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.util.ToolsUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 数据采集管理类
 * 
 * @author lilijun
 *
 */
public class DataCollectionManager
{
	private final String TAG = "DataCollectionManager";

	private static DataCollectionManager instance = null;

	/** 跳转Activity时 通过Intent传过去的数据采集action名称 */
	public static final String DATA_COLLECTION_ACTION = "data_collection_action";

	private Context mContext = null;

	/** 采集的数据存放的列表 */
	private List<String> dataList = new ArrayList<String>();

	/** 软件id */
	public static final String SOFT_ID = "soft_id";

	/** 包id */
	public static final String PACK_ID = "pack_id";

	/** 分类id */
	public static final String CLASSFIY_ID = "classfiy_id";

	/** 渠道号 */
	public static final String CHANNEL_NO = "channel_no";

	/** 用户的user id */
	private final String USER_ID = "user_id";

	public static DataCollectionManager getInstance()
	{
		if (instance == null)
		{
			synchronized (DataCollectionManager.class)
			{
				instance = new DataCollectionManager();
			}
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public void init(Context context)
	{
		this.mContext = context;

		List<String> datas = (List<String>) ToolsUtil.getCacheDataFromFile(
				context, Constants.DATA_COLLECTION_INFO_CANCHE_FILE_NAME);
		if (datas != null)
		{
			dataList.addAll(datas);
		}
	}

	/**
	 * 添加一条采集到的数据到采集数据列表
	 * 
	 * @param action
	 */
	public void addRecord(String action, String... params)
	{
		if (!DLog.isLOG())
		{
			// 只在关闭日志的时候才采集数据
			JSONObject jsonObject = null;
			try
			{
				jsonObject = new JSONObject();
				jsonObject.put("record_url", action);
				jsonObject.put(SOFT_ID, "");
				jsonObject.put(PACK_ID, "");
				jsonObject.put(CLASSFIY_ID, "");
				jsonObject.put(USER_ID, "");
				for (int i = 0; i < params.length; i++)
				{
					if (i % 2 != 0)
					{
						// 是value
						jsonObject.put(params[i - 1], params[i]);
					}
				}
				if (LoginUserInfoManager.getInstance().isHaveUserLogin())
				{
					jsonObject.put(USER_ID, LoginUserInfoManager.getInstance()
							.getLoginedUserInfo().getUserId());
				}
			} catch (Exception e)
			{
				jsonObject = null;
				DLog.e(TAG, "组合采集到的json数据时发生异常#exception：", e);
			}
			if (jsonObject != null)
			{
				dataList.add(jsonObject.toString());
			}
			DLog.i("lilijun", "添加一条采集数据------>>" + jsonObject.toString());
			if (dataList.size() >= 10)
			{
				// 满10条数据，就上报数据一次
				DLog.i("lilijun", "满10条数据，就上报数据一次!!");
				uploadData();
				dataList.clear();
			}

			ToolsUtil.saveCachDataToFile(mContext,
					Constants.DATA_COLLECTION_INFO_CANCHE_FILE_NAME, dataList);
		}
	}

	/**
	 * 添加友盟的自定义是事件
	 * 
	 * @param action
	 *            我们自己定义的界面跳转路径的action
	 * @param eventId
	 *            事件id
	 * @param values
	 *            所携带的参数
	 */
	public void addYouMengEventRecord(Context context, String action,
			String eventId, HashMap<String, String> values)
	{
		if (values == null)
		{
			values = new HashMap<String, String>();
		}
		values.put("action", action);
		values.put("version_name", DataCollectionConstant.versionName);
		if (LoginUserInfoManager.getInstance().isHaveUserLogin())
		{
			values.put(USER_ID, LoginUserInfoManager.getInstance()
					.getLoginedUserInfo().getUserId()
					+ "");
		}
		MobclickAgent.onEvent(context, eventId, values);
	}

	public List<String> getDataList()
	{
		return dataList;
	}

	/**
	 * 获取一个Action
	 * 
	 * @param oldAction
	 * @param newAction
	 * @return
	 */
	public static String getAction(String oldAction, String newAction)
	{
		if ("".equals(oldAction) || oldAction == null)
		{
			return newAction;
		} else
		{
			return oldAction + "-" + newAction;
		}
	}

	/**
	 * 跳转到Activity
	 * 
	 * @param context
	 * @param intent
	 * @param action
	 *            传到下一界面的数据采集Action
	 */
	public static void startActivity(Context context, Intent intent,
			String action)
	{
		intent.putExtra(DATA_COLLECTION_ACTION, action);
		context.startActivity(intent);
	}

	/**
	 * 得到从上一个界面传过来的数据采集action
	 * 
	 * @param intent
	 * @return
	 */
	public static String getIntentDataCollectionAction(Intent intent)
	{
		return intent.getStringExtra(DATA_COLLECTION_ACTION);
	}

	/**
	 * 上报数据到服务器
	 */
	public void uploadData()
	{
		try
		{
			JSONObject jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			jsonObject.put("mac", DataCollectionConstant.macAdress);
			jsonObject.put("imei", DataCollectionConstant.imei);
			jsonObject.put("versionCode", DataCollectionConstant.versionCode);
			jsonObject.put("versionName", DataCollectionConstant.versionName);
			jsonObject.put("resolution",
					DataCollectionConstant.screenResolution);
			jsonObject.put("channelNo", DataCollectionConstant.channelNo);
			jsonObject.put("model", DataCollectionConstant.model);
			jsonObject.put("brand", DataCollectionConstant.brand);
			jsonObject.put("imsi", DataCollectionConstant.imsi);
			jsonObject.put("androidVersion",
					DataCollectionConstant.androidVersionName);
			jsonObject.put("mf", DataCollectionConstant.manuFacturer);

			for (String jsonStr : dataList)
			{
				jsonArray.put(new JSONObject(jsonStr));
			}
			jsonObject.put("specil_data", jsonArray);

			if (jsonObject != null)
			{
				DLog.i("lilijun",
						"json.toString--------->>>" + jsonObject.toString());
				// 上报必须采集的数据
				ToolsUtil.repordDataCollection(jsonObject);
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "组合上报数据采集的json数据发生异常#exception：", e);
		}
	}

}
