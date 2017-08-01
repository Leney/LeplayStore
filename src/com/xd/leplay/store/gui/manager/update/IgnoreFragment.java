package com.xd.leplay.store.gui.manager.update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.gui.main.BaseTabChildFragment;
import com.xd.leplay.store.model.UpdateAppInfo;
import com.xd.leplay.store.view.MarketListView;

/**
 * 
 * 忽略的Fragment
 * 
 * @author lilijun
 *
 */
public class IgnoreFragment extends BaseTabChildFragment
{
	private MarketListView listView = null;

	private IgnoreAdapter adapter = null;

	/** 全部取消忽略按钮 */
	private Button unIgnoreAllBtn = null;

	/**
	 * 用户手动忽略的可更新的应用集合
	 */
	private ArrayList<UpdateAppInfo> ignoreUpdateAppInfos = new ArrayList<UpdateAppInfo>();

	/** 重新获取列表数据，并刷新 */
	public static final int REGET_LIST_DATA = 1;

	private OnPromptUpgreadeChangeListener changeListener = null;

	private UpdateAppActivity parentActivity = null;

	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what == REGET_LIST_DATA)
			{
				refresh();
				changeListener.onPromptUpgreadeChange(1);
			}
		};
	};

	/** 判断是否是第一次加载数据 */
	private boolean isFirstLoadData = true;

	private static String thisAction = "";

	public static IgnoreFragment getInstance(String beforeAction)
	{
		thisAction = DataCollectionManager
				.getAction(
						beforeAction,
						DataCollectionConstant.DATA_COLLECTION_UPDATE_LIST_IGNORE_VALUE);
		return new IgnoreFragment();
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
		loadingView.setVisibilyView(false);

		changeListener = (UpdateAppActivity) getActivity();
		parentActivity = (UpdateAppActivity) getActivity();

		setCenterView(R.layout.ignore_layout);
		listView = (MarketListView) view.findViewById(R.id.ignore_listview);
		unIgnoreAllBtn = (Button) view.findViewById(R.id.ignore_all_btn);
		unIgnoreAllBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// 全部取消忽略
				for (UpdateAppInfo updateAppInfo : ignoreUpdateAppInfos)
				{
					SoftwareManager.getInstance().getUpdateAppInfos()
							.get(updateAppInfo.getPackageName())
							.setPromptUpgreade(true);
					// 更新数据库
					SoftwareManager.getInstance().updateUpdateInfoToDB(
							SoftwareManager.getInstance().getUpdateAppInfos()
									.get(updateAppInfo.getPackageName()));
				}
				refresh();
				changeListener.onPromptUpgreadeChange(1);
				// 发送取消忽略的广播
				getActivity()
						.sendBroadcast(
								new Intent(
										Constants.ACTION_UPDATE_ACTIVITY_IGNORE_OR_CANCLE_IGNORE_APP));
			}
		});

		adapter = new IgnoreAdapter(getActivity(), listView,
				ignoreUpdateAppInfos, handler, action);
		listView.setAdapter(adapter);
		refresh();

		IntentFilter filter = new IntentFilter(
				Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS);
		getActivity().registerReceiver(receiver, filter);
	}

	/**
	 * 获取列表显示数据
	 */
	private void getListData()
	{
		List<UpdateAppInfo> list = new ArrayList<UpdateAppInfo>();

		for (Entry<String, UpdateAppInfo> entry : SoftwareManager.getInstance()
				.getUpdateAppInfos().entrySet())
		{
			if (!entry.getValue().isPromptUpgreade())
			{
				list.add(entry.getValue());
			}
		}
		ignoreUpdateAppInfos.clear();
		ignoreUpdateAppInfos.addAll(list);
	}

	/**
	 * 重新刷新界面
	 */
	public void refresh()
	{
		getListData();
		if (ignoreUpdateAppInfos.isEmpty())
		{
			parentActivity.setCurPage(0);
			errorViewLayout.setErrorView(R.drawable.no_update_and_no_ignore,
					getResources().getString(R.string.no_ignore_apps), "");
			errorViewLayout.setBtnVisible(false);
			errorViewLayout.setVisibility(View.VISIBLE);
			errorViewLayout.setRefrushOnClickListener(null);
			centerViewLayout.setVisibility(View.GONE);
		} else
		{
			errorViewLayout.setVisibility(View.GONE);
			centerViewLayout.setVisibility(View.VISIBLE);
			adapter.notifyDataSetChanged();
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(
					Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS))
			{
				refresh();
			}
		}
	};

	@Override
	public void onDestroy()
	{
		if (adapter != null)
		{
			adapter.onDestory();
		}
		if (getActivity() != null)
		{
			getActivity().unregisterReceiver(receiver);
		}
		super.onDestroy();
	}
}
