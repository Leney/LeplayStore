package com.xd.leplay.store.gui.search;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.main.BaseTabChildFragment;
import com.xd.leplay.store.gui.search.view.SearchItemsLayout;
import com.xd.leplay.store.model.proto.App.ReqHotSearchKeys;
import com.xd.leplay.store.model.proto.App.RspHotSearchKeys;
import com.xd.leplay.store.model.proto.App.SearchKey;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.google.protobuf.ByteString;

/**
 * 搜索首页Fragment
 * 
 * @author lilijun
 *
 */
public class SearchKeysFragment extends BaseTabChildFragment implements
		OnAddedSearchHistoryLinstener
{
	private final String TAG = "SearchKeysFragment";

	private OnStartSearchLinstener startSearchLinstener;

	private SearchItemsLayout historyItems;

	private TextView clearHistory;

	private FrameLayout historyLay;

	/** 应用keys部分、游戏keys部分 */
	private LinearLayout appKeysLay, gameKeysLay;

	/** 换一批按钮 */
	private FrameLayout changeOthersBtn;

	/** 应用标题 */
	private TextView appTitleText;

	/** 页数 默认第一页 */
	private int page = 1;

	/** 每次获取key值的条数 */
	private final int SIZE = 6;

	/** 应用Keys集合 */
	private List<SearchKey> appKeys = null;

	/** 游戏Keys集合 */
	private List<SearchKey> gameKeys = null;

	/** 应用热搜词类型 */
	private final int APP_TYPE = 1;

	/** 游戏热搜词 */
	private final int GAME_TYPE = 2;

	/** 判断是否是第一次加载数据 */
	private boolean isFirstLoadData = true;

	private static String thisAction = "";

	/** 搜索关键字列表的etag标识值 */
	private String etagMark = "searchKeyList";

	private SearchHistoryManager searchHistoryManager;

	public static SearchKeysFragment getInstance(String beforeAction)
	{
		thisAction = DataCollectionManager.getAction(beforeAction,
				DataCollectionConstant.DATA_COLLECTION_SEARCH_KEY_VALUE);
		return new SearchKeysFragment();
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
		searchHistoryManager = SearchHistoryManager.getInstance();
		searchHistoryManager.setOnAddedSearchHistoryLinstener(this);
		centerViewLayout.setVisibility(View.GONE);
		appKeys = new ArrayList<SearchKey>();
		gameKeys = new ArrayList<SearchKey>();
		setCenterView(R.layout.search_keys_fragment);
		historyItems = (SearchItemsLayout) view
				.findViewById(R.id.search_history_items);
		historyLay = (FrameLayout) view
				.findViewById(R.id.search_keys_history_lay);
		clearHistory = (TextView) view
				.findViewById(R.id.search_history_clear_text);
		clearHistory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				searchHistoryManager.clearAllHistory();
				historyLay.setVisibility(View.GONE);
				historyItems.removeAllViews();
				historyItems.setVisibility(View.GONE);
			}
		});
		appTitleText = (TextView) view
				.findViewById(R.id.search_keys_app_title_text);
		appKeysLay = (LinearLayout) view
				.findViewById(R.id.search_keys_app_keys_lay);
		gameKeysLay = (LinearLayout) view
				.findViewById(R.id.search_keys_game_keys_lay);
		changeOthersBtn = (FrameLayout) view
				.findViewById(R.id.search_keys_change_others_lay);
		changeOthersBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// 换一批 按钮点击事件
				DataCollectionManager
						.getInstance()
						.addRecord(
								DataCollectionManager
										.getAction(
												action,
												DataCollectionConstant.DATA_COLLECTION_SEARCH_CLICK_CHANGE_OTHERS_BTN_VALUE));

				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								getActivity(),
								action,
								DataCollectionConstant.EVENT_ID_SEARCH_CLICK_CHANGE_OTHERS_BTN,
								null);
				page++;
				doLoadData(Constants.APP_API_URL, new String[]
				{ "ReqHotSearchKeys", "ReqHotSearchKeys" }, new ByteString[]
				{ getReqHotSearchKeys(page, SIZE, APP_TYPE),
						getReqHotSearchKeys(page, SIZE, GAME_TYPE) }, etagMark);
			}
		});
		DLog.i("lilijun", "搜索历史记录列表---------》》》"
				+ searchHistoryManager.getHistoryList().size());
		if (searchHistoryManager.isEmpty())
		{
			historyLay.setVisibility(View.GONE);
			historyItems.setVisibility(View.GONE);
		} else
		{
			historyLay.setVisibility(View.VISIBLE);
			historyItems.setVisibility(View.VISIBLE);
			historyItems.setAdapter(searchHistoryManager.getHistoryList(),
					R.drawable.search_history_btn_bg_selector,
					startSearchLinstener);
		}
		doLoadData(Constants.APP_API_URL, new String[]
		{ "ReqHotSearchKeys", "ReqHotSearchKeys" },
				new ByteString[]
				{ getReqHotSearchKeys(page, SIZE, APP_TYPE),
						getReqHotSearchKeys(page, SIZE, GAME_TYPE) }, etagMark);
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		for (ByteString param : rspPacket.getParamsList())
		{
			try
			{
				RspHotSearchKeys rspHotSearchKeys = RspHotSearchKeys
						.parseFrom(param);
				List<SearchKey> list = rspHotSearchKeys.getSearchKeyList();
				if (!list.isEmpty())
				{
					if (list.get(0).getKeyType() == 1)
					{
						appKeys.clear();
						appKeys.addAll(list);
					} else if (list.get(0).getKeyType() == 2)
					{
						gameKeys.clear();
						gameKeys.addAll(list);
					}
				}
			} catch (Exception e)
			{
				DLog.e(TAG, "loadDataSuccess()#Excepton:", e);
			}
		}

		float textSize = appTitleText.getTextSize();
		// LayoutParams params = new LayoutParams(
		// (LayoutParams) appTitleText.getLayoutParams());
		LayoutParams titleParams = (LayoutParams) appTitleText
				.getLayoutParams();
		LayoutParams params = new LayoutParams(titleParams.width,
				titleParams.height);
		params.width = 0;
		params.weight = 1;
		params.rightMargin = (int) (textSize / 2);
		params.bottomMargin = (int) (textSize / 2);

		if (!appKeys.isEmpty())
		{
			appKeysLay.removeAllViews();
			// 向上取整数
			int lineCount = (int) Math.ceil(appKeys.size() / 3d);
			LinearLayout[] lineLayouts = new LinearLayout[lineCount];
			for (int i = 0; i < lineLayouts.length; i++)
			{
				lineLayouts[i] = new LinearLayout(getActivity());
				for (int j = 0; j < 3; j++)
				{
					if (((3 * i) + j) >= appKeys.size())
					{
						break;
					}
					TextView textView = new TextView(getActivity());
					textView.setText(appKeys.get(((3 * i) + j)).getKeyName());
					textView.setTextColor(getActivity().getResources()
							.getColor(R.color.search_app_text_color));
					textView.setGravity(Gravity.CENTER);
					textView.setSingleLine();
					textView.setEllipsize(TruncateAt.END);
					textView.setBackgroundResource(R.drawable.search_app_key_item_bg_selector);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
					textView.setOnClickListener(keyAppItemOnClickListener);
					textView.setLayoutParams(params);
					lineLayouts[i].addView(textView);
				}
				appKeysLay.addView(lineLayouts[i]);
			}
		}

		if (!gameKeys.isEmpty())
		{
			gameKeysLay.removeAllViews();
			// 向上取整数
			int lineCount = (int) Math.ceil(gameKeys.size() / 3d);
			LinearLayout[] lineLayouts = new LinearLayout[lineCount];
			for (int i = 0; i < lineLayouts.length; i++)
			{
				lineLayouts[i] = new LinearLayout(getActivity());
				for (int j = 0; j < 3; j++)
				{
					if (((3 * i) + j) >= gameKeys.size())
					{
						break;
					}
					TextView textView = new TextView(getActivity());
					textView.setText(gameKeys.get(((3 * i) + j)).getKeyName());
					textView.setTextColor(getActivity().getResources()
							.getColor(R.color.search_game_text_color));
					textView.setGravity(Gravity.CENTER);
					textView.setSingleLine();
					textView.setEllipsize(TruncateAt.END);
					textView.setBackgroundResource(R.drawable.search_game_key_item_bg_selector);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
					textView.setOnClickListener(keyGameItemOnClickListener);
					textView.setLayoutParams(params);
					lineLayouts[i].addView(textView);
				}
				gameKeysLay.addView(lineLayouts[i]);
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		if (appKeys.isEmpty() && gameKeys.isEmpty())
		{
			loadingView.setVisibilyView(false);
			centerViewLayout.setVisibility(View.GONE);
			errorViewLayout.setVisibility(View.VISIBLE);
			errorViewLayout.showLoadFailedLay();
		}
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		if (appKeys.isEmpty() && gameKeys.isEmpty())
		{
			loadingView.setVisibilyView(false);
			centerViewLayout.setVisibility(View.GONE);
			errorViewLayout.setVisibility(View.VISIBLE);
			errorViewLayout.showLoadFailedLay();
		}
	}

	/**
	 * key app item点击事件
	 */
	private OnClickListener keyAppItemOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			DataCollectionManager
					.getInstance()
					.addRecord(
							DataCollectionManager
									.getAction(
											action,
											DataCollectionConstant.DATA_COLLECTION_SEARCH_CLICK_APP_KEY_SEARCH_VALUE));
			DataCollectionManager
					.getInstance()
					.addYouMengEventRecord(
							getActivity(),
							action,
							DataCollectionConstant.EVENT_ID_SEARCH_CLICK_APP_KEY_SEARCH,
							null);
			startSearchLinstener.onStartSearch(((TextView) v).getText()
					.toString());
		}
	};
	/**
	 * key 游戏 item点击事件
	 */
	private OnClickListener keyGameItemOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			DataCollectionManager
					.getInstance()
					.addRecord(
							DataCollectionManager
									.getAction(
											action,
											DataCollectionConstant.DATA_COLLECTION_SEARCH_CLICK_GAME_KEY_VALUE));
			DataCollectionManager
					.getInstance()
					.addYouMengEventRecord(
							getActivity(),
							action,
							DataCollectionConstant.EVENT_ID_SEARCH_CLICK_GAME_KEY,
							null);
			startSearchLinstener.onStartSearch(((TextView) v).getText()
					.toString());
		}
	};

	/** 设置开始搜索的监听对象 */
	public void setOnStartSearchLinstener(OnStartSearchLinstener linstener)
	{
		this.startSearchLinstener = linstener;
	}

	/**
	 * 获取关键词的请求数据
	 * 
	 * @param page
	 *            页数
	 * @param size
	 *            每页获取的数据条数
	 * @param type
	 *            1= 应用热搜词,2=游戏热搜词
	 * @return
	 */
	private ByteString getReqHotSearchKeys(int page, int size, int type)
	{
		ReqHotSearchKeys.Builder builder = ReqHotSearchKeys.newBuilder();
		builder.setPageIndex(page);
		builder.setPageSize(size);
		builder.setKeyType(type);
		return builder.build().toByteString();
	}

	@Override
	public void onHistoryAdded()
	{
		// 当有新的搜索历史记录被添加的时候 进行刷新显示历史记录的视图
		historyLay.setVisibility(View.VISIBLE);
		historyItems.setVisibility(View.VISIBLE);
		historyItems.removeAllViews();
		historyItems
				.setAdapter(searchHistoryManager.getHistoryList(),
						R.drawable.search_history_btn_bg_selector,
						startSearchLinstener);
	}
}
