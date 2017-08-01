package com.xd.leplay.store.view;

import java.util.Map.Entry;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xd.download.DownloadManager;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.control.SoftwareManager;
import com.xd.leplay.store.gui.download.DownloadManagerActivity;
import com.xd.leplay.store.gui.manager.ManagerCenterActivity;
import com.xd.leplay.store.gui.search.SearchActivity;
import com.xd.leplay.store.gui.webview.WebViewActivity;
import com.xd.leplay.store.model.UpdateAppInfo;
import com.xd.leplay.store.util.DisplayUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class MainTitleView extends RelativeLayout implements OnClickListener
{
	/** 搜索区域 */
	private LinearLayout searchEditLay;
	// /** 下载按钮图标部分、管理按钮图标部分 */
	// private FrameLayout downlaodIconLay, manageIconLay;
	/** 下载按钮图标部分 */
	private FrameLayout downlaodIconLay;
	// /** 下载任务个数、管理个数(可更新应用个数) */
	// private TextView downloadNum, managerNum;
	/** 下载秘籍 */
	private ImageView downloadCheats;
	/** 下载任务个数 */
	private TextView downloadNum;

	private CustomImageView userIcon;

	private View redProint;

	private LoginUserInfoManager userInfoManager = null;

	private ImageLoaderManager imageLoaderManager = null;

	private DisplayImageOptions options = null;

	@SuppressLint("NewApi")
	public MainTitleView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public MainTitleView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public MainTitleView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public MainTitleView(Context context)
	{
		super(context);
		init(context);
	}

	private void init(Context context)
	{
		userInfoManager = LoginUserInfoManager.getInstance();
		imageLoaderManager = ImageLoaderManager.getInstance();
		options = DisplayUtil.getUserIconImageLoaderOptions();

		View view = View.inflate(context, R.layout.main_title_view, null);
		userIcon = (CustomImageView) view
				.findViewById(R.id.main_titile_user_icon);
		// userIcon.setOuterRingData("", 0);
		userIcon.setOnClickListener(this);
		redProint = view.findViewById(R.id.main_title_red_proint);
		searchEditLay = (LinearLayout) view
				.findViewById(R.id.main_title_search_edit_lay);
		searchEditLay.setOnClickListener(this);
		downlaodIconLay = (FrameLayout) view
				.findViewById(R.id.main_titile_download_btn);
		downlaodIconLay.setOnClickListener(this);
		// manageIconLay = (FrameLayout) view
		// .findViewById(R.id.main_titile_manage_btn);
		// manageIconLay.setOnClickListener(this);
		downloadCheats = (ImageView) view
				.findViewById(R.id.main_titile_download_cheats);
		downloadCheats.setOnClickListener(this);
		downloadNum = (TextView) view
				.findViewById(R.id.main_title_download_num);
		downloadNum.setVisibility(View.GONE);
		// managerNum = (TextView)
		// view.findViewById(R.id.main_title_manage_num);
		// managerNum.setVisibility(View.GONE);
		addView(view);

		if (userInfoManager.isHaveUserLogin())
		{
			imageLoaderManager.displayImage(userInfoManager
					.getLoginedUserInfo().getIconUrl(), userIcon, options);
		} else
		{
			imageLoaderManager.displayImage("", userIcon, options);
		}

		setDownloadNum(DownloadManager.shareInstance().getAllTaskInfo().size());
		int number = 0;
		for (Entry<String, UpdateAppInfo> entry : SoftwareManager.getInstance()
				.getUpdateAppInfos().entrySet())
		{
			if (entry.getValue().isPromptUpgreade())
			{
				number++;
			}
		}
		// setUpdateNum(number);
		setRedProintVisible(number);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_DOWNLOAD_TASK_COUNT_CHANGE);
		filter.addAction(Constants.ACTION_SOFTWARE_MANAGER_GET_UPDATE_LIST_FROM_NETWORK_FINISH);
		filter.addAction(Constants.ACTION_UPDATE_ACTIVITY_IGNORE_OR_CANCLE_IGNORE_APP);
		filter.addAction(Constants.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS);
		filter.addAction(Constants.ACTION_ACCOUNT_HAVE_MODIFY);
		filter.addAction(Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY);
		filter.addAction(Constants.ACTION_NO_PIC_MODEL_CHANGE);
		context.registerReceiver(receiver, filter);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.main_titile_user_icon:
			// 管理中心
			ManagerCenterActivity.startManagerCenterActivity(getContext(), "");
			// // 个人中心
			// if (LoginUserInfoManager.getInstance().isHaveUserLogin())
			// {
			// // 跳转到个人中心
			// PersonalCenterActivity.startPersonalActivity(getContext(), "");
			// } else
			// {
			// // 跳转到登录界面
			// LoginActivity.startLoginActivity(getContext(), "");
			// }
			break;
		case R.id.main_title_search_edit_lay:
			// 搜索编辑框
			SearchActivity.startSearchActivity(getContext(), "");
			break;
		case R.id.main_titile_download_btn:
			// 下载按钮部分
			DownloadManagerActivity.startDownloadManagerActivity(getContext(),
					"");
			break;
		case R.id.main_titile_download_cheats:
			// 下载秘籍
			DataCollectionManager.getInstance().addYouMengEventRecord(
					getContext(), "",
					DataCollectionConstant.EVENT_ID_IN_JINBIMIJI, null);
			WebViewActivity
					.startWebViewActivity(
							getContext(),
							getResources().getString(R.string.coins_details),
							Constants.UAC_API_URL + "corn",
							DataCollectionManager
									.getAction(
											"",
											DataCollectionConstant.DATA_COLLECTION_TREASURE_GAIN_COINS_CLICK_CHEATS_VALUE));
			break;
		// case R.id.main_titile_manage_btn:
		// // 管理中心
		// ManagerCenterActivity.startManagerCenterActivity(getContext(), "");
		// break;
		}
	}

	/**
	 * 设置下载任务条数的数字角标值
	 * 
	 * @param number
	 */
	public void setDownloadNum(int number)
	{
		if (number <= 99)
		{
			downloadNum.setText(number + "");
		} else
		{
			downloadNum.setText("99");
		}
		if (number != 0)
		{
			downloadNum.setVisibility(View.VISIBLE);
		} else
		{
			downloadNum.setVisibility(View.GONE);
		}
	}

	// /**
	// * 设置更新应用条数的数字角标值
	// *
	// * @param number
	// */
	// public void setUpdateNum(int number)
	// {
	// if (number <= 99)
	// {
	// managerNum.setText(number + "");
	// } else
	// {
	// managerNum.setText("99");
	// }
	// if (number != 0)
	// {
	// managerNum.setVisibility(View.VISIBLE);
	// } else
	// {
	// managerNum.setVisibility(View.GONE);
	// }
	// }

	/**
	 * 设置有更新的指示红点是否显示
	 * 
	 * @param number
	 */
	public void setRedProintVisible(int number)
	{
		if (number > 0)
		{
			redProint.setVisibility(View.VISIBLE);
		} else
		{
			redProint.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDetachedFromWindow()
	{
		if (receiver != null)
		{
			getContext().unregisterReceiver(receiver);
		}
		super.onDetachedFromWindow();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (Constants.ACTION_DOWNLOAD_TASK_COUNT_CHANGE.equals(intent
					.getAction()))
			{
				// 下载任务池中的下载任务条数发生改变
				setDownloadNum(DownloadManager.shareInstance().getAllTaskInfo()
						.size());
			} else if (Constants.ACTION_SOFTWARE_MANAGER_GET_UPDATE_LIST_FROM_NETWORK_FINISH
					.equals(intent.getAction())
					|| Constants.ACTION_UPDATE_ACTIVITY_IGNORE_OR_CANCLE_IGNORE_APP
							.equals(intent.getAction())
					|| Constants.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS
							.equals(intent.getAction()))
			{
				// 从网络获取可更新应用数据完成
				int number = 0;
				for (Entry<String, UpdateAppInfo> entry : SoftwareManager
						.getInstance().getUpdateAppInfos().entrySet())
				{
					if (entry.getValue().isPromptUpgreade())
					{
						number++;
					}
				}
				// setUpdateNum(number);
				setRedProintVisible(number);
			} else if (Constants.ACTION_ACCOUNT_HAVE_MODIFY.equals(intent
					.getAction())
					|| Constants.ACTION_LOGINED_USER_INFO_HAVE_MODIFY
							.equals(intent.getAction()))
			{
				if (userInfoManager.isHaveUserLogin())
				{
					imageLoaderManager.displayImage(userInfoManager
							.getLoginedUserInfo().getIconUrl(), userIcon,
							options);
				} else
				{
					imageLoaderManager.displayImage("", userIcon, options);
				}
			} else if (Constants.ACTION_NO_PIC_MODEL_CHANGE.equals(intent
					.getAction()))
			{
				if (userInfoManager.isHaveUserLogin())
				{
					imageLoaderManager.displayImage(userInfoManager
							.getLoginedUserInfo().getIconUrl(), userIcon,
							options);
				} else
				{
					imageLoaderManager.displayImage("", userIcon, options);
				}
			}
		}
	};
}
