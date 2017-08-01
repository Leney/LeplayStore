package com.xd.leplay.store.gui.application;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.FrameLayout;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.adapter.ClassifyAdapter2;
import com.xd.leplay.store.gui.main.BaseTabChildFragment;
import com.xd.leplay.store.model.proto.App;
import com.xd.leplay.store.model.proto.App.AppType;
import com.xd.leplay.store.model.proto.App.ReqAppTypes;
import com.xd.leplay.store.model.proto.App.RspAppTypes;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.view.MarketListView;
import com.google.protobuf.ByteString;

/**
 * 应用Tab中 分类子Fragment
 * 
 * @author lilijun
 *
 */
public class ApplicationClassifyFragment extends BaseTabChildFragment
{
	private static final String TAG = "ApplicationClassifyFragment";

	private MarketListView listView = null;

	private ClassifyAdapter2 adapter = null;

	private List<AppType> appTypes = null;

	/** 请求列表数据的TAG */
	private final String REQUEST_LIST_TAG = "ReqAppTypes";

	/** 响应列表数据的TAG */
	private final String RSPONSE_LIST_TAG = "RspAppTypes";

	/** 分类的类型 ,11=应用,12=游戏 */
	private final int CLASSIFY_TYPE = 11;

	/** 判断是否是第一次加载数据 */
	private boolean isFirstLoadData = true;

	private static String thisAction = "";

	/** 应用分类列表的etag标识值 */
	private String etagMark = "applicationClassifyList";

	public static ApplicationClassifyFragment getInstance(String beforeAction)
	{
		thisAction = DataCollectionManager.getAction(beforeAction,
				DataCollectionConstant.DATA_COLLECTION_APP_CLASSIFY_VALUE);
		return new ApplicationClassifyFragment();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isFirstLoadData)
		{
			action = thisAction;
			DataCollectionManager.getInstance().addRecord(action);
			isFirstLoadData = false;
		}
	}

	@Override
	protected void initView(FrameLayout view)
	{
		action = thisAction;
		listView = new MarketListView(getActivity());
		listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		setCenterView(listView);

		appTypes = new ArrayList<App.AppType>();
		adapter = new ClassifyAdapter2(getActivity(), appTypes, action,
				etagMark);

		doLoadData(Constants.APP_API_URL, new String[]
		{ REQUEST_LIST_TAG }, new ByteString[]
		{ getClassifyListRequestData(CLASSIFY_TYPE) }, etagMark);

		// 注册无图省流量模式改变广播
		IntentFilter filter = new IntentFilter(
				Constants.ACTION_NO_PIC_MODEL_CHANGE);
		getActivity().registerReceiver(receiver, filter);
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<String> actions = rspPacket.getActionList();
		for (String action : actions)
		{
			if (RSPONSE_LIST_TAG.equals(action))
			{
				// 列表数据返回
				parseClassifyListResult(rspPacket);
				if (!appTypes.isEmpty())
				{
					listView.setAdapter(adapter);
					adapter.setAppTypes(appTypes);
				}

			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		loadingView.setVisibilyView(false);
		centerViewLayout.setVisibility(View.GONE);
		errorViewLayout.setVisibility(View.VISIBLE);
		errorViewLayout.showLoadFailedLay();
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		loadingView.setVisibilyView(false);
		centerViewLayout.setVisibility(View.GONE);
		errorViewLayout.setVisibility(View.VISIBLE);
		errorViewLayout.showLoadFailedLay();
	}

	@Override
	protected void tryAgain()
	{
		super.tryAgain();
		doLoadData(Constants.APP_API_URL, new String[]
		{ REQUEST_LIST_TAG }, new ByteString[]
		{ getClassifyListRequestData(CLASSIFY_TYPE) }, etagMark);
	}

	@Override
	public void onDestroy()
	{
		getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}

	/**
	 * 获取分类的请求数据
	 * 
	 * @param type
	 * @return
	 */
	private ByteString getClassifyListRequestData(int type)
	{
		ReqAppTypes.Builder builder = ReqAppTypes.newBuilder();
		builder.setTypeClass(type);
		return builder.build().toByteString();
	}

	/**
	 * 解析分类列表返回结果
	 * 
	 * @param rspPacket
	 * @throws Exception
	 */
	private void parseClassifyListResult(RspPacket rspPacket)
	{
		try
		{
			RspAppTypes rspAppTypes = RspAppTypes.parseFrom(rspPacket
					.getParams(0));
			// 返回码：0=成功，1=失败，2=参数错误...
			if (rspAppTypes.getRescode() == 0)
			{
				List<AppType> list = rspAppTypes.getAppTypesList();
				if (list != null && !list.isEmpty())
				{
					appTypes.addAll(list);
				}
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "parseClassifyListResult()#Excepton:", e);
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (Constants.ACTION_NO_PIC_MODEL_CHANGE.equals(intent.getAction()))
			{
				adapter.notifyDataSetChanged();
			}
		}
	};
}
