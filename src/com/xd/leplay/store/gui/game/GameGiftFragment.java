package com.xd.leplay.store.gui.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.main.BaseTabChildFragment;
import com.xd.leplay.store.model.DetailGiftInfo;
import com.xd.leplay.store.model.GiftInfo;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.model.proto.App;
import com.xd.leplay.store.model.proto.App.Game;
import com.xd.leplay.store.model.proto.App.GameBag;
import com.xd.leplay.store.model.proto.App.ReqGameBag;
import com.xd.leplay.store.model.proto.App.RspGameBag;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.ReqPickupGameBag;
import com.xd.leplay.store.model.proto.Uac.RspPickupGameBag;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.CenterDialog;
import com.xd.leplay.store.view.LoadMoreScrollListener;
import com.xd.leplay.store.view.LoadMoreScrollListener.setOnScrollToEndListener;
import com.xd.leplay.store.view.LoadMoreView;
import com.xd.leplay.store.view.LoadingDialog;
import com.xd.leplay.store.view.MarketExpandableListView;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 应用Tab中 必备子Fragment
 * 
 * @author lilijun
 *
 */
public class GameGiftFragment extends BaseTabChildFragment
{
	private static final String TAG = "GameGiftFragment";

	private MarketExpandableListView listView = null;

	private GameGiftAdapter adapter = null;

	private List<ListAppInfo> groups = null;

	private List<List<GameBag>> childs = null;

	/** 礼包详情的数据集合 */
	private Map<Long, DetailGiftInfo> detailGiftInfoMap = null;

	/** Game对象的集合 */
	private List<Game> games = null;

	/** 列表一次加载的长度 */
	private static final int LOAD_DATA_SIZE = 10;

	/** 标识是否还有下一页列表数据 */
	private boolean isHaveNextPageData = true;

	private LoadMoreView loadMoreView = null;

	/** 请求列表数据的TAG */
	private final String REQUEST_LIST_TAG = "ReqGameBag";

	/** 响应列表数据的TAG */
	private final String RSPONSE_LIST_TAG = "RspGameBag";

	/** 抢礼包的请求TAG */
	private final String GET_GIFT_REQUEST_TAG = "ReqPickupGameBag";

	/** 抢礼包的响应TAG */
	private final String GET_GIFT_RSPONSE_TAG = "RspPickupGameBag";

	/** 抢礼包的Handler消息 */
	public static final int HANDLER_GET_GIFT = 1;

	/** 点击礼包单个条目进入礼包详情的Handler消息 */
	public static final int HANDLER_INTENT_GIFT_DETAIL = 2;

	/** 正在获取礼包的Dialog */
	private LoadingDialog loadingDialog = null;

	/** 游戏礼包列表的etag标识值 */
	private String etagMark = "gameGiftList";

	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what == HANDLER_GET_GIFT)
			{
				Bundle data = msg.getData();
				long gameId = data.getLong("game_soft_id");
				int giftId = data.getInt("gift_id");

				loadingDialog.show();
				// 抢礼包
				doLoadData(Constants.UAC_API_URL, new String[]
				{ GET_GIFT_REQUEST_TAG }, new ByteString[]
				{ getPickupGiftRequestData(gameId, giftId) }, "");
			} else if (msg.what == HANDLER_INTENT_GIFT_DETAIL)
			{
				Bundle data = msg.getData();
				long gameId = data.getLong("game_id");
				int giftId = data.getInt("show_gift_id");
				// 跳转到礼包详情
				GiftDetailActivity.startGiftDetailActivity(getActivity(),
						detailGiftInfoMap.get(gameId), giftId, action);
			}
		};
	};

	/** 判断是否是第一次加载数据 */
	private boolean isFirstLoadData = true;

	private static String thisAction = "";

	public static GameGiftFragment getInstance(String beforeAction)
	{
		thisAction = DataCollectionManager.getAction(beforeAction,
				DataCollectionConstant.DATA_COLLECTION_GAME_GIFT_VALUE);
		return new GameGiftFragment();
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
		listView = new MarketExpandableListView(getActivity());
		setCenterView(listView);

		loadingDialog = new LoadingDialog(getActivity(), getResources()
				.getString(R.string.getting_gift));

		games = new ArrayList<Game>();
		groups = new ArrayList<ListAppInfo>();
		childs = new ArrayList<List<GameBag>>();

		detailGiftInfoMap = new HashMap<Long, DetailGiftInfo>();

		adapter = new GameGiftAdapter(getActivity(), groups, childs, handler,
				action);
		listView.setOnScrollListener(new LoadMoreScrollListener(ImageLoader
				.getInstance(), true, true, new setOnScrollToEndListener()
		{
			@Override
			public void loadMoreWhenScrollToEnd()
			{
				// if (isHaveNextPageData)
				// {
				loadMoreData();
				// } else
				// {
				// listView.setOnScrollListener(null);
				// listView.removeFooterView(loadMoreView);
				// listView.showEndView();
				// }
			}
		}));

		loadMoreView = new LoadMoreView(getActivity());
		loadMoreView.setNetErrorViewOnClickLinstener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				loadMoreView.setLoadingVisible(true);
				loadMoreData();
			}
		});
		listView.addFooterView(loadMoreView);

		IntentFilter filter = new IntentFilter(
				Constants.ACTION_ACCOUNT_HAVE_MODIFY);
		getActivity().registerReceiver(receiver, filter);

		doLoadData(
				Constants.APP_API_URL,
				new String[]
				{ REQUEST_LIST_TAG },
				new ByteString[]
				{ getGameGiftList(((games.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, etagMark);
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<String> actions = rspPacket.getActionList();
		for (String actionNet : actions)
		{
			if (RSPONSE_LIST_TAG.equals(actionNet))
			{
				// 游戏礼包列表数据返回
				parseGameGiftListResult(rspPacket);
				if (!isHaveNextPageData)
				{
					listView.setOnScrollListener(null);
					listView.removeFooterView(loadMoreView);
					listView.showEndView();
				}
				if (!games.isEmpty())
				{
					if (games.size() <= LOAD_DATA_SIZE)
					{
						// 表明是第一次加载列表数据
						listView.setAdapter(adapter);
					} else
					{
						// 表明是加载下一页返回了数据
						adapter.notifyDataSetChanged();
					}
					// 展开所有item
					for (int i = 0; i < adapter.getGroupCount(); i++)
					{
						listView.expandGroup(i);
					}
				} else
				{
					showErrorView();
					errorViewLayout.setRefrushOnClickListener(null);
					errorViewLayout
							.setErrorView(
									R.drawable.no_gifts_icon,
									getResources().getString(
											R.string.no_gifts_for_now), "");
				}
			} else if (GET_GIFT_RSPONSE_TAG.equals(actionNet))
			{
				// 抢礼包数据返回
				try
				{
					RspPickupGameBag rspPickupGameBag = RspPickupGameBag
							.parseFrom(rspPacket.getParams(0));
					if (rspPickupGameBag.getRescode() == 0)
					{
						String redeemCode = rspPickupGameBag.getRedeemCode();
						// 找到之前列表获取到并存放到本地的礼包数据
						GiftInfo giftInfo = detailGiftInfoMap
								.get(rspPickupGameBag.getGameId())
								.getGiftInfoMap()
								.get(rspPickupGameBag.getBagId());
						giftInfo.setCode(redeemCode);
						// 将得到的礼包信息添加到用户的我的礼包列表中去
						LoginUserInfoManager.getInstance().getLoginedUserInfo()
								.getGiftList().put(giftInfo.getId(), giftInfo);
						LoginUserInfoManager.getInstance().getLoginedUserInfo()
								.getGiftIdList().add(giftInfo.getId());

						// 将登录的账户信息保存到缓存中去
						ToolsUtil.saveCachDataToFile(getActivity(),
								Constants.LOGINED_USER_INFO_CANCHE_FILE_NAME,
								LoginUserInfoManager.getInstance()
										.getLoginedUserInfo());

						// 发送用户信息改变广播
						getActivity()
								.sendBroadcast(
										new Intent(
												Constants.ACTION_ACCOUNT_HAVE_MODIFY));
						loadingDialog.dismiss();
						// 显示领取礼包成功的Dialog
						showGetGiftSuccessDialog(giftInfo);

					} else if (rspPickupGameBag.getRescode() == 2)
					{
						// userToken错误 重新登录
						Toast.makeText(getActivity(),
								getResources().getString(R.string.re_login),
								Toast.LENGTH_SHORT).show();
						LoginActivity.startLoginActivity(getActivity(), action);
					} else if (rspPickupGameBag.getRescode() == 3)
					{
						// 礼包已被抢完
						Toast.makeText(getActivity(),
								getResources().getString(R.string.no_gift),
								Toast.LENGTH_SHORT).show();
					} else if (rspPickupGameBag.getRescode() == 4)
					{
						// 已领取过
						Toast.makeText(
								getActivity(),
								getResources().getString(
										R.string.already_get_gift),
								Toast.LENGTH_SHORT).show();
					} else
					{
						DLog.e("lilijun",
								"领取礼包失败--失败码-->>>"
										+ rspPickupGameBag.getRescode());
						Toast.makeText(getActivity(),
								rspPickupGameBag.getResmsg(),
								Toast.LENGTH_SHORT).show();
					}
					loadingDialog.dismiss();
				} catch (Exception e)
				{
					DLog.e(TAG, "抢礼包发生异常#Excepton:", e);
				}
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		for (String action : rspPacket.getActionList())
		{
			if (action.equals(REQUEST_LIST_TAG))
			{
				// 请求列表数据出错
				if (!games.isEmpty())
				{
					loadMoreView.setNetErrorVisible(true);
				} else
				{
					loadingView.setVisibilyView(false);
					centerViewLayout.setVisibility(View.GONE);
					errorViewLayout.setVisibility(View.VISIBLE);
					errorViewLayout.showLoadFailedLay();
				}
			} else if (action.equals(GET_GIFT_REQUEST_TAG))
			{
				// 抢礼包出错
				loadingDialog.dismiss();
				Toast.makeText(getActivity(),
						getResources().getString(R.string.get_gift_failed),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void netError(String[] actions)
	{
		super.netError(actions);
		for (String action : actions)
		{
			if (action.equals(REQUEST_LIST_TAG))
			{
				// 请求列表数据出错
				if (!games.isEmpty())
				{
					loadMoreView.setNetErrorVisible(true);
				} else
				{
					loadingView.setVisibilyView(false);
					centerViewLayout.setVisibility(View.GONE);
					errorViewLayout.setVisibility(View.VISIBLE);
					errorViewLayout.showLoadFailedLay();
				}
			} else if (action.equals(GET_GIFT_REQUEST_TAG))
			{
				// 抢礼包出错
				loadingDialog.dismiss();
				Toast.makeText(getActivity(),
						getResources().getString(R.string.get_gift_failed),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void tryAgain()
	{
		super.tryAgain();
		doLoadData(
				Constants.APP_API_URL,
				new String[]
				{ REQUEST_LIST_TAG },
				new ByteString[]
				{ getGameGiftList(((games.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, etagMark);
	}

	/**
	 * 加载列表下一页数据
	 */
	private void loadMoreData()
	{
		doLoadData(
				Constants.APP_API_URL,
				new String[]
				{ REQUEST_LIST_TAG, },
				new ByteString[]
				{ getGameGiftList(((games.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, etagMark
						+ (games.size() / LOAD_DATA_SIZE) + 1);
	}

	/**
	 * 获取游戏礼包列表的请求数据
	 * 
	 * @param type
	 * @param index
	 * @param size
	 * @return
	 */
	private ByteString getGameGiftList(int index, int size)
	{
		ReqGameBag.Builder builder = ReqGameBag.newBuilder();
		builder.setPageIndex(index);
		builder.setPageSize(size);
		return builder.build().toByteString();
	}

	/**
	 * 解析礼包列表返回结果
	 * 
	 * @param rspPacket
	 * @throws Exception
	 */
	private void parseGameGiftListResult(RspPacket rspPacket)
	{
		try
		{
			RspGameBag rspGameBag = RspGameBag
					.parseFrom(rspPacket.getParams(0));

			// 返回码：0=成功,1=失败,2=暂无数据
			if (rspGameBag.getRescode() == 0)
			{
				// 成功
				List<Game> infos = rspGameBag.getGameList();

				if (infos == null || infos.isEmpty()
						|| infos.size() < LOAD_DATA_SIZE)
				{
					// 没有下一页数据了
					isHaveNextPageData = false;
				}
				if (infos != null)
				{
					games.addAll(infos);
					for (Game game : infos)
					{
						DetailGiftInfo detailGiftInfo = new DetailGiftInfo();
						ListAppInfo listAppInfo = new ListAppInfo();
						listAppInfo.setSoftId(game.getGameId());
						listAppInfo.setName(game.getGameName());
						listAppInfo.setDownloadCount(game.getDownTimes());
						listAppInfo.setFormatDownloadCount(ToolsUtil
								.getFormatDownloadCount(listAppInfo
										.getDownloadCount()));
						listAppInfo.setSize(game.getPackSize());
						listAppInfo.setFormatSize(ToolsUtil
								.getFormatSize(listAppInfo.getSize()));
						listAppInfo.setPackageName(game.getPackName());
						listAppInfo.setDownlaodUrl(game.getPackUrl());
						listAppInfo.setIconUrl(game.getGameIconUrl());
						groups.add(listAppInfo);
						childs.add(game.getGameBagList());

						// 添加数据到礼包详情数据集合中去
						detailGiftInfo.setListAppInfo(listAppInfo);
						for (App.GameBag gift : game.getGameBagList())
						{
							GiftInfo giftInfo = new GiftInfo();
							giftInfo.setId(gift.getBagId());
							giftInfo.setName(gift.getBagName());
							giftInfo.setContent(gift.getBagContent());
							giftInfo.setEligibility(gift.getEligibility());
							giftInfo.setEndTime(gift.getEndTime());
							giftInfo.setHowPickup(gift.getHowPickup());
							giftInfo.setStartTime(gift.getStartTime());
							giftInfo.setUsage(gift.getUsage());
							giftInfo.setGameIconUrl(listAppInfo.getIconUrl());
							giftInfo.setGameId(listAppInfo.getSoftId());
							giftInfo.setGameName(listAppInfo.getName());
							detailGiftInfo.getGiftInfoMap().put(
									giftInfo.getId(), giftInfo);
						}
						detailGiftInfoMap.put(listAppInfo.getSoftId(),
								detailGiftInfo);
					}
				}
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "parseGameGiftListResult()#Excepton:", e);
		}
	}

	/**
	 * 获取领取礼包的请求数据
	 * 
	 * @param gift
	 * @return
	 */
	private ByteString getPickupGiftRequestData(long gameId, int giftId)
	{
		DetailGiftInfo detailGiftInfo = detailGiftInfoMap.get(gameId);
		ListAppInfo appInfo = detailGiftInfo.getListAppInfo();
		GiftInfo gift = detailGiftInfo.getGiftInfoMap().get(giftId);

		LoginedUserInfo loginedUserInfo = LoginUserInfoManager.getInstance()
				.getLoginedUserInfo();
		ReqPickupGameBag.Builder builder = ReqPickupGameBag.newBuilder();
		builder.setUid(loginedUserInfo.getUserId());
		builder.setUserToken(loginedUserInfo.getUserToken());
		builder.setGameId(appInfo.getSoftId());
		builder.setGameName(appInfo.getName());
		builder.setGameIconUrl(appInfo.getIconUrl());
		builder.setBagId(gift.getId());
		builder.setBagName(gift.getName());
		builder.setStartTime(gift.getStartTime());
		builder.setEndTime(gift.getEndTime());
		return builder.build().toByteString();
	}

	/**
	 * 礼包领取成功的Dialog
	 * 
	 * @param giftInfo
	 */
	private void showGetGiftSuccessDialog(final GiftInfo giftInfo)
	{
		final CenterDialog getGiftSuccessDialog = new CenterDialog(
				getActivity());
		getGiftSuccessDialog.show();
		getGiftSuccessDialog.setTitleName(getResources().getString(
				R.string.get_gift_success));
		View giftDialogView = View.inflate(getActivity(),
				R.layout.get_gift_success_dialog, null);
		TextView useWay = (TextView) giftDialogView
				.findViewById(R.id.gift_dialog_use_way);
		TextView redeemCode = (TextView) giftDialogView
				.findViewById(R.id.gift_dialog_redeem_code);
		Button copyCodeBtn = (Button) giftDialogView
				.findViewById(R.id.gift_dialog_copy_redeem_code);
		String useWayStr = getResources().getString(R.string.use_way)
				+ giftInfo.getUsage();
		useWay.setText(ToolsUtil.getFormatTextColor(useWayStr, 0, 5, "#666666"));
		redeemCode.setText(giftInfo.getCode());
		copyCodeBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				boolean isCopy = ToolsUtil.copy(giftInfo.getCode(),
						getActivity());
				if (isCopy)
				{
					Toast.makeText(
							getActivity(),
							getActivity().getResources().getString(
									R.string.copy_code_success),
							Toast.LENGTH_SHORT).show();
				} else
				{
					Toast.makeText(
							getActivity(),
							getActivity().getResources().getString(
									R.string.copy_failed), Toast.LENGTH_SHORT)
							.show();
				}
				getGiftSuccessDialog.dismiss();
			}
		});
		getGiftSuccessDialog.setCenterView(giftDialogView);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (Constants.ACTION_ACCOUNT_HAVE_MODIFY.equals(intent.getAction()))
			{
				adapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void onDestroy()
	{
		// if (adapter != null)
		// {
		// adapter.unRegisterListener();
		// }
		super.onDestroy();
	}

}
