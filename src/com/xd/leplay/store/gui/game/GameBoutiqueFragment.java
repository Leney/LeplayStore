package com.xd.leplay.store.gui.game;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.FrameLayout;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.gui.adapter.SoftListAdapter;
import com.xd.leplay.store.gui.application.ApplicationClassifyDetailActivity;
import com.xd.leplay.store.gui.details.DetailsActivity;
import com.xd.leplay.store.gui.main.BaseTabChildFragment;
import com.xd.leplay.store.gui.webview.WebViewActivity;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.model.proto.App;
import com.xd.leplay.store.model.proto.App.AdElement;
import com.xd.leplay.store.model.proto.App.ReqAdElements;
import com.xd.leplay.store.model.proto.App.ReqRecommAppList;
import com.xd.leplay.store.model.proto.App.RspAdElements;
import com.xd.leplay.store.model.proto.App.RspRecommAppList;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.BannerView;
import com.xd.leplay.store.view.LoadMoreScrollListener;
import com.xd.leplay.store.view.LoadMoreScrollListener.setOnScrollToEndListener;
import com.xd.leplay.store.view.LoadMoreView;
import com.xd.leplay.store.view.MarketListView;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 游戏Tab中 精品子Fragment
 * 
 * @author lilijun
 *
 */
public class GameBoutiqueFragment extends BaseTabChildFragment
{
	private static final String TAG = "GameBoutiqueFragment";

	private MarketListView listView = null;

	private SoftListAdapter adapter = null;

	private ArrayList<ListAppInfo> appInfos = null;

	/** banner 广告对象 */
	private List<AdElement> bannerAdElements = null;

	/** banner下面图片广告对象 */
	private List<AdElement> imgAdElements = null;

	/** 列表一次加载的长度 */
	private static final int LOAD_DATA_SIZE = 10;

	/** 标识是否还有下一页列表数据 */
	private boolean isHaveNextPageData = true;

	private View headerView = null;

	private BannerView bannerView = null;

	/** 头部 左边广告图片、头部右边广告图片 */
	//private ImageView headerLeftAdImg, headerRightAdImg;

	private LoadMoreView loadMoreView = null;

	/**
	 * 获取列表的类型的标识值
	 * 1=首页推荐,2=首页新品应用首发,3=首页新品游戏首发,4=首页玩赚金币(应用),5=首页玩赚金币(游戏)，6=应用精品，
	 * 7=应用排行,8=应用必备,9=游戏精品,10=游戏排行
	 */
	private final int LIST_TYPE = 9;

	/**
	 * 获取广告元素(Banner)的类型标识值
	 * 
	 * 1=闪屏，2=首页轮播,3=应用轮播，4=游戏轮播，5=应用广告图片,6=游戏广告图片
	 */
	private final int AD_ELEMENTS_BANNER_TYPE = 4;

	/**
	 * 获取banner下面广告图片的
	 */
	private final int AD_ELEMENTS_IMG_TYPE = 6;

	/** 请求列表数据的TAG值 */
	private final String REQUEST_LIST_TAG = "ReqRecommAppList";

	/** 请求广告数据的TAG值 */
	private final String REQUEST_ADELMENT_TAG = "ReqAdElements";

	/** 响应列表数据的TAG值 */
	private final String RSPONSE_LIST_TAG = "RspRecommAppList";

	/** 响应广告数据的TAG值 */
	private final String RSPONSE_ADELMENT_TAG = "RspAdElements";

	private ImageLoaderManager imageLoaderManager = null;

	private DisplayImageOptions adImgOptions = null;

	/** 判断是否是第一次加载数据 */
	private boolean isFirstLoadData = true;

	private static String thisAction = "";

	/** 游戏精品列表的etag标识值 */
	private String etagMark = "gameBoutiqueList";

	public static GameBoutiqueFragment getInstance(String beforeAction)
	{
		thisAction = DataCollectionManager.getAction(beforeAction,
				DataCollectionConstant.DATA_COLLECTION_GAME_BOUTIQUE_VALUE);
		return new GameBoutiqueFragment();
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
		imageLoaderManager = ImageLoaderManager.getInstance();
		adImgOptions = DisplayUtil.getAdImgImageLoaderOptions();

		listView = new MarketListView(getActivity());
		listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		setCenterView(listView);

		appInfos = new ArrayList<ListAppInfo>();
		bannerAdElements = new ArrayList<App.AdElement>();
		imgAdElements = new ArrayList<App.AdElement>();
		adapter = new SoftListAdapter(getActivity(), listView, appInfos, action);
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

		headerView = View.inflate(getActivity(),
				R.layout.application_boutique_header_view, null);
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
		listView.addHeaderView(headerView);
		listView.addFooterView(loadMoreView);

		bannerView = (BannerView) headerView
				.findViewById(R.id.applicaition_boutique_banner_view);
		bannerView
				.setAction(DataCollectionManager
						.getAction(
								action,
								DataCollectionConstant.DATA_COLLECTION_GAME_BOUTIQUE_CLICK_TOP_BANNER_VALUE));
//		headerLeftAdImg = (ImageView) headerView
//				.findViewById(R.id.application_boutique_left_ad_img);
//		headerLeftAdImg
//				.setImageResource(R.drawable.application_header_right_lay);
//		headerRightAdImg = (ImageView) headerView
//				.findViewById(R.id.application_boutique_right_ad_img);
//		headerLeftAdImg.setOnClickListener(new OnClickListener()
//		{
//
//			@Override
//			public void onClick(View v)
//			{
//				if (LoginUserInfoManager.getInstance().isHaveUserLogin())
//				{
//					// 下载赚自己后台数据采集
//					String headerRightAdImgAction = DataCollectionManager
//							.getAction(
//									action,
//									DataCollectionConstant.DATA_COLLECTION_CLICK_DOWNLOAD_GAIN_VALUE);
//					DataCollectionManager.getInstance().addRecord(
//							headerRightAdImgAction);
//					DataCollectionManager
//							.getInstance()
//							.addYouMengEventRecord(
//									getActivity(),
//									action,
//									DataCollectionConstant.EVENT_ID_IN_DOWNLOAD_GET_CONINS,
//									null);
//					// 跳转到我的下载
//					DownloadRewardActivity.startDownloadRewardActivity(
//							getActivity(),
//							DataCollectionManager
//									.getAction(
//											action,
//											DataCollectionConstant.DATA_COLLECTION_TREASURE_DOWNLOAD_REWARD_VALUE));
//				} else
//				{
//					LoginActivity.startLoginActivity(getActivity(), action);
//				}
//			}
//		});
//		headerRightAdImg
//				.setOnClickListener(headerLeftAndRightAdImgOnClickListener);

		doLoadData(
				Constants.APP_API_URL,
				new String[]
				{ REQUEST_LIST_TAG, REQUEST_ADELMENT_TAG, REQUEST_ADELMENT_TAG },
				new ByteString[]
				{
						getReqRecommAppList(
								((appInfos.size() / LOAD_DATA_SIZE) + 1),
								LOAD_DATA_SIZE),
						getReqAdElements(AD_ELEMENTS_BANNER_TYPE),
						getReqAdElements(AD_ELEMENTS_IMG_TYPE) }, etagMark);

		// 注册无图省流量模式改变广播
		IntentFilter filter = new IntentFilter(
				Constants.ACTION_NO_PIC_MODEL_CHANGE);
		getActivity().registerReceiver(receiver, filter);
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<ByteString> byteList = rspPacket.getParamsList();
		for (int i = 0; i < byteList.size(); i++)
		{
			String action = rspPacket.getAction(i);
			if (action.equals(RSPONSE_LIST_TAG))
			{
				parseListResult(byteList.get(i));
				if (!isHaveNextPageData)
				{
					listView.setOnScrollListener(null);
					listView.removeFooterView(loadMoreView);
					listView.showEndView();
				}
			} else if (action.equals(RSPONSE_ADELMENT_TAG))
			{
				parseAdElmentResult(byteList.get(i));
			}
		}

		if (!appInfos.isEmpty())
		{
			if (listView.getAdapter() == null)
			{
				listView.setAdapter(adapter);
			} else
			{
				adapter.notifyDataSetChanged();
			}
			// if (appInfos.size() <= LOAD_DATA_SIZE)
			// {
			// // 表明是第一次加载列表数据
			// listView.setAdapter(adapter);
			// } else
			// {
			// // 表明是加载下一页返回了数据
			// adapter.notifyDataSetChanged();
			// }
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		if (!appInfos.isEmpty())
		{
			loadMoreView.setNetErrorVisible(true);
		} else
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
		if (!appInfos.isEmpty())
		{
			loadMoreView.setNetErrorVisible(true);
		} else
		{
			loadingView.setVisibilyView(false);
			centerViewLayout.setVisibility(View.GONE);
			errorViewLayout.setVisibility(View.VISIBLE);
			errorViewLayout.showLoadFailedLay();
		}
	}

	@Override
	protected void tryAgain()
	{
		super.tryAgain();
		doLoadData(
				Constants.APP_API_URL,
				new String[]
				{ REQUEST_LIST_TAG, REQUEST_ADELMENT_TAG, REQUEST_ADELMENT_TAG },
				new ByteString[]
				{
						getReqRecommAppList(
								((appInfos.size() / LOAD_DATA_SIZE) + 1),
								LOAD_DATA_SIZE),
						getReqAdElements(AD_ELEMENTS_BANNER_TYPE),
						getReqAdElements(AD_ELEMENTS_IMG_TYPE) }, "");
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
				{ getReqRecommAppList(((appInfos.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, etagMark
						+ (appInfos.size() / LOAD_DATA_SIZE) + 1);
	}

	/**
	 * 获取ReqRecommAppList的ByteString
	 * 
	 * @param type
	 * @param index
	 * @param size
	 * @return
	 */
	private ByteString getReqRecommAppList(int index, int size)
	{
		ReqRecommAppList.Builder builder = ReqRecommAppList.newBuilder();
		builder.setRecommType(LIST_TYPE);
		builder.setPageIndex(index);
		builder.setPageSize(size);
		ReqRecommAppList reqRecommAppList = builder.build();
		ByteString byteString = reqRecommAppList.toByteString();
		return byteString;
	}

	/**
	 * 解析列表返回结果
	 * 
	 * @param rspPacket
	 */
	private void parseListResult(ByteString byteString)
	{
		try
		{
			RspRecommAppList rspRecommAppList = RspRecommAppList
					.parseFrom(byteString);
			List<App.AppInfo> infos = rspRecommAppList.getAppInfosList();
			if (infos.size() == 0 || infos.isEmpty()
					|| infos.size() < LOAD_DATA_SIZE)
			{
				// 没有下一页数据了
				isHaveNextPageData = false;
			}

			for (App.AppInfo info : infos)
			{
				appInfos.add(ToolsUtil.getListAppInfo(info));
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "parseListResult()#Excepton:", e);
		}
	}

	/**
	 * 获取ReqAdElemnts的ByteString
	 * 
	 * @param type
	 * @return
	 */
	private ByteString getReqAdElements(int type)
	{
		ReqAdElements.Builder adElementsBuilder = ReqAdElements.newBuilder();
		adElementsBuilder.setPosType(type);
		return adElementsBuilder.build().toByteString();
	}

	/**
	 * 解析广告数据
	 * 
	 * @param byteString
	 */
	private void parseAdElmentResult(ByteString byteString)
	{
		try
		{
			RspAdElements rspAdElements = RspAdElements.parseFrom(byteString);
			List<AdElement> adElementList = rspAdElements.getAdElementsList();
			if (!adElementList.isEmpty())
			{
				if (adElementList.get(0).getPosType() == AD_ELEMENTS_BANNER_TYPE)
				{
					// 应用精品banner轮播
					bannerAdElements.addAll(adElementList);

					bannerView.initData(bannerAdElements);
					bannerView.initView(getActivity());
					bannerView.setScrollTime(300);
					// 将自动刷新开关打开
					bannerView.setCancleAutoRefresh(false);
					// 开始自动刷新
					bannerView.startAutoRefresh();

				} else if (adElementList.get(0).getPosType() == AD_ELEMENTS_IMG_TYPE)
				{
					// 应用banner下面的广告图片
					imgAdElements.addAll(adElementList);

					for (int i = 0; i < imgAdElements.size(); i++)
					{
						// if (i == 0)
						// {
						// imageLoaderManager.displayImage(imgAdElements
						// .get(i).getAdsPicUrlLand(),
						// headerLeftAdImg, adImgOptions);
						// headerLeftAdImg.setTag(imgAdElements.get(i));
						// } else
//						if (i == 1)
//						{
//							imageLoaderManager.displayImage(imgAdElements
//									.get(i).getAdsPicUrlLand(),
//									headerRightAdImg, adImgOptions);
//							headerRightAdImg.setTag(imgAdElements.get(i));
//							break;
//						}
					}
				}
			}
		} catch (Exception e)
		{
			DLog.e(TAG, "parseAdElmentResult()#Excepton:", e);
		}
	}

	/**
	 * banner下面的两张广告图片的点击事件
	 */
	private OnClickListener headerLeftAndRightAdImgOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			AdElement adElement = (AdElement) v.getTag();
			// 元素类型：1=跳转至应用或游戏，2=跳转指定链接，3=跳转至分类
			switch (adElement.getElemType())
			{
			case 1:
				// App详情
				ListAppInfo appInfo = ToolsUtil.getListAppInfo(adElement
						.getAppInfo());
				DLog.i("lilijun", "appInfo==null----->>>" + (appInfo == null));
				DetailsActivity
						.startDetailsActivityById(
								getActivity(),
								appInfo.getSoftId(),
								DataCollectionManager
										.getAction(
												action,
												DataCollectionConstant.DATA_COLLECTION_GAME_BOUTIQUE_CLICK_LEFT_BANNER_VALUE));
				break;
			case 2:
				// webview链接
				String url = adElement.getJumpLinkUrl();
				DLog.i("lilijun", "链接地址------->>>" + url);
				WebViewActivity
						.startWebViewActivity(
								getActivity(),
								adElement.getShowName(),
								url,
								DataCollectionManager
										.getAction(
												action,
												DataCollectionConstant.DATA_COLLECTION_GAME_BOUTIQUE_CLICK_LEFT_BANNER_VALUE));
				break;
			case 3:
				// 分类列表
				int appTypeId = adElement.getJumpAppTypeId();
				String appTypeName = adElement.getJumpAppTypeName();
				String etagMark = "gameBoutiqueBannerClassify" + appTypeId;
				ApplicationClassifyDetailActivity
						.startApplicationClassifyDetailActivity(
								getActivity(),
								appTypeId,
								appTypeName,
								DataCollectionManager
										.getAction(
												action,
												DataCollectionConstant.DATA_COLLECTION_GAME_BOUTIQUE_CLICK_LEFT_BANNER_VALUE),
								etagMark);
				break;
			}
		}
	};

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			for (int i = 0; i < imgAdElements.size(); i++)
			{
				// if (i == 0)
				// {
				// imageLoaderManager.displayImage(imgAdElements.get(i)
				// .getAdsPicUrlLand(), headerLeftAdImg, adImgOptions);
				// headerLeftAdImg.setTag(imgAdElements.get(i));
				// } else
				// if (i == 1)
				// {
				// imageLoaderManager
				// .displayImage(imgAdElements.get(i)
				// .getAdsPicUrlLand(), headerRightAdImg,
				// adImgOptions);
				// headerRightAdImg.setTag(imgAdElements.get(i));
				// break;
				// }
			}
		}
	};

	@Override
	public void onDestroy()
	{
		getActivity().unregisterReceiver(receiver);
		if (adapter != null)
		{
			adapter.unRegisterListener();
		}
		super.onDestroy();
	}

}
