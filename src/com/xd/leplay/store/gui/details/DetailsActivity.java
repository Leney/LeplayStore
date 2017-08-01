package com.xd.leplay.store.gui.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.control.PraiseManager;
import com.xd.leplay.store.gui.details.view.DetailCommentListView;
import com.xd.leplay.store.gui.details.view.DetailDownloadProgressButton;
import com.xd.leplay.store.gui.details.view.ScreenShortHorizontalScrollView;
import com.xd.leplay.store.gui.login.LoginActivity;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.gui.main.MainActivity;
import com.xd.leplay.store.model.CommentInfo;
import com.xd.leplay.store.model.DetailAppInfo;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.model.proto.App;
import com.xd.leplay.store.model.proto.App.ReqAppDetail;
import com.xd.leplay.store.model.proto.App.ReqSetLikeCount;
import com.xd.leplay.store.model.proto.App.RspAppDetail;
import com.xd.leplay.store.model.proto.App.RspSetLikeCount;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.model.proto.Uac.RecommentContent;
import com.xd.leplay.store.model.proto.Uac.ReqRecomment;
import com.xd.leplay.store.model.proto.Uac.ReqRecommentList;
import com.xd.leplay.store.model.proto.Uac.RspRecomment;
import com.xd.leplay.store.model.proto.Uac.RspRecommentList;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.CenterDialog;
import com.xd.leplay.store.view.LoadMoreScrollListener2;
import com.xd.leplay.store.view.LoadMoreScrollListener2.setOnScrollToEndListener;
import com.xd.leplay.store.view.LoadMoreView;
import com.xd.leplay.store.view.LoadingView;
import com.xd.leplay.store.view.NetErrorView;
import com.xd.leplay.store.view.download.DownloadOnclickListener;
import com.xd.leplay.store.view.download.DownloadProgressButton;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 详情界面
 * 
 * @author lilijun
 *
 */
public class DetailsActivity extends BaseActivity implements OnClickListener
{
	private static final String TAG = "DetailsActivity";
	/** 请求应用详情的TAG */
	private final String REQUEST_APP_DETAIL_ACTION = "ReqAppDetail";
	/** 响应应用详情的TAG */
	private final String RSPONSE_APP_DETAIL_ACTION = "RspAppDetail";

	/** 获取评论列表的请求TAG */
	private final String GET_COMMENT_LIST_REQUEST_TAG = "ReqRecommentList";
	/** 获取评论列表的响应TAG */
	private final String GET_COMMENT_LIST_RSPONSE_TAG = "RspRecommentList";

	/** 提交评论的请求TAG */
	private final String COMMIT_COMMENT_REQUEST_TAG = "ReqRecomment";
	/** 提交评论的响应TAG */
	private final String COMMIT_COMMENT_RSPONSE_TAG = "RspRecomment";

	/** 赞的请求TAG */
	private final String PRAISE_APP_REQUEST_TAG = "ReqSetLikeCount";
	/** 赞的响应TAG */
	private final String PRAISE_APP_RSPONSE_TAG = "RspSetLikeCount";

	private long softId = -1;

	private String packageName = "";

	/** 是否是从闪屏页跳转过来 */
	private boolean isFromSplash = false;

	private DetailAppInfo appInfo;

	/** 列表显示评论的真正数据源集合 */
	private List<CommentInfo> commentInfos;

	/** 存放的评论数据(当点击详情时 不需要显示评论数据) */
	private List<CommentInfo> saveCommentInfos;

	/** 评论列表一次加载的长度 */
	private static final int LOAD_DATA_SIZE = 10;

	private View headerView;

	/** 图标 */
	private ImageView headerIcon;
	/** 名称 */
	private TextView headerName;
	/** 星级 */
	private RatingBar headerStar;
	/** 标签1、标签2 */
	private TextView headerLab1, headerLab2;
	/** 下载次数和软件大小 */
	private TextView headerDownloadCountAndSize;
	/** 官方标签、安全标签、无广告标签 */
	private TextView headerOfficalLab, headerSafetyLab, headerNoAdLab;
	/** 进度条下载状态按钮 */
	private DownloadProgressButton headerDownloadProgressButton;
	/** 顶部不动的整个Tab部分、中间的整个Tab部分 */
	private LinearLayout topTabLay, headerCenterTabLay;

	/** 顶部不动的Tab详情按钮部分、顶部不动的Tab评论按钮部分、中间的Tab详情按钮部分、中间的Tab评论按钮部分 */
	private FrameLayout topTabDetailLay, topTabCommentLay,
			headerCenterTabDetailLay, headerCenterTabCommentLay;
	/** 顶部不动的Tab详情文本、顶部不动的Tab评论文本、中间的Tab详情文本、中间的Tab评论文本 */
	private TextView topTabDetailText, topTabCommentText,
			headerCenterTabDetailText, headerCenterTabCommentText;
	/** 顶部不动的Tab详情底部条、顶部不动的Tab评论底部条、中间的Tab详情底部条、中间的Tab评论底部条 */
	private View topTabDetailBottomLine, topTabCommentBottomLine,
			headerCenterTabDetailBottomLine, headerCenterTabCommentBottomLine;

	/** 点击详情时 显示的详情信息部分 */
	private LinearLayout headerDetailsLay;
	/** 领取礼包部分 */
	private TextView headerGiftLay;
	/** 截图显示部分 */
	private ScreenShortHorizontalScrollView headerScreenShortView;
	/** 应用描述标题部分、更新说明标题部分 */
	private FrameLayout headerAppDescribeTitleLay,
			headerUpdateDescribeTitleLay;
	/** 应用描述标题右边展开和收缩显示的文本、更新说明标题右边展开和收缩显示的文本 */
	private TextView headerAppDescribeStatusText,
			headerUpdateDescribeStatusText;
	/** 更新说明的分割线 */
	private View headerUpdateDesSpltLine, headerUpdateDesTopSplitLine;
	/** 应用描述显示部分 、更新说明显示部分 */
	private TextView headerAppDescribe, headerUpdateDescribe;
	/** 评论列表 */
	private DetailCommentListView listView;
	/** 评论列表的Adapter */
	private CommentAdapter commentAdapter = null;
	/** 没有评论、正在加载评论、加载评论失败的显示区域 */
	private FrameLayout headerCommentErrorLay;
	/** 评论加载视图 */
	private LoadingView headerCommentLoadingView;
	/** 评论错误视图(或者 无评论数据的视图) */
	private NetErrorView headerCommentErrorView;
	/** 加载更多评论数据的列表底部视图 */
	private LoadMoreView loadMoreView = null;
	/** 底部下载按钮 */
	private DetailDownloadProgressButton bottomDownloadBtn;
	/** 底部"我要评论"按钮 */
	private Button commentBtn;
	/** 底部"赞"按钮、底部"分享"按钮 */
	private TextView praiseBtn, shareBtn;

	/** 顶部显示应用基本信息的部分 */
	private RelativeLayout headerDetailTopLay;

	/** 整个底部按钮的部分 */
	private LinearLayout bottomLay;

	/** 顶部的下载按钮点击事件对象 */
	private DownloadOnclickListener downloadTopOnclickListener = null;

	/** 底部的下载按钮点击事件对象 */
	private DownloadOnclickListener downloadBottomOnclickListener = null;

	private ImageLoaderManager imageLoaderManager = null;

	private DisplayImageOptions iconOptions = null;

	/** 收起图标 */
	private Drawable upProint = null;
	/** 展开图标 */
	private Drawable downProint = null;

	/** 需要向上滑动的总距离(滑动这个距离之后 显示顶部的Tab视图) */
	private int needScrollHeight = 0;

	/** 标识当前是否显示详情界面(即:没有评论数据) true=显示详情界面，false=显示评论界面 */
	private boolean isShowDetail = true;

	/** 标识评论数据是否还有下一页数据 */
	private boolean isHaveNextPage = false;

	/** 获取评论数据失败 */
	private boolean isGetCommentFailed = false;

	private int height = 0;

	/** 是否展开详情信息、是否展开更新信息 */
	private boolean isExplandDetail = false, isExplandUpdate = false;

	/** 赞过的图片、没赞过的图片 */
	private Drawable likePraiseIcon, unLikePraiseIcon;

	/** 应用详情数据的etag标识值 */
	private String etagMark = "detailData";

	@SuppressWarnings("deprecation")
	@Override
	protected void initView()
	{
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_CP_DETAIL_VALUE);

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		height = wm.getDefaultDisplay().getHeight();

		titleView.setTitleName(getResources().getString(R.string.app_details));
		titleView.setRightLayVisible(false);
		softId = getIntent().getLongExtra("softId", -1);
		packageName = getIntent().getStringExtra("packageName");
		isFromSplash = getIntent().getBooleanExtra("isFromSplash", false);

		imageLoaderManager = ImageLoaderManager.getInstance();
		iconOptions = DisplayUtil.getListIconImageLoaderOptions();

		setCenterView(R.layout.details_activity);
		centerViewLayout.setVisibility(View.GONE);
		appInfo = new DetailAppInfo();
		commentInfos = new ArrayList<CommentInfo>();
		saveCommentInfos = new ArrayList<CommentInfo>();

		listView = (DetailCommentListView) findViewById(R.id.details_listView);
		listView.setDividerHeight(0);
		bottomDownloadBtn = (DetailDownloadProgressButton) findViewById(R.id.details_bottom_download_btn);
		topTabLay = (LinearLayout) findViewById(R.id.details_top_tab_lay);
		topTabLay.setVisibility(View.GONE);

		topTabDetailLay = (FrameLayout) findViewById(R.id.details_top_details_btn_lay);
		topTabDetailLay.setOnClickListener(this);
		topTabDetailText = (TextView) findViewById(R.id.details_top_details_text);
		topTabDetailBottomLine = findViewById(R.id.details_top_details_line);

		topTabCommentLay = (FrameLayout) findViewById(R.id.details_top_comment_btn_lay);
		topTabCommentLay.setOnClickListener(this);
		topTabCommentText = (TextView) findViewById(R.id.details_top_comment_text);
		topTabCommentBottomLine = findViewById(R.id.details_top_comment_line);

		bottomLay = (LinearLayout) findViewById(R.id.details_bottom_lay);
		commentBtn = (Button) findViewById(R.id.details_bottom_comment_btn);
		commentBtn.setOnClickListener(this);
		praiseBtn = (TextView) findViewById(R.id.details_praise_btn);
		praiseBtn.setOnClickListener(this);
		shareBtn = (TextView) findViewById(R.id.details_share_btn);
		shareBtn.setOnClickListener(this);

		commentAdapter = new CommentAdapter(DetailsActivity.this, commentInfos);
		listView.setOnScrollListener(new LoadMoreScrollListener2(ImageLoader
				.getInstance(), true, true, new setOnScrollToEndListener()
		{
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount)
			{
				if (view.getChildAt(0) != null)
				{
					int scrollY = -view.getChildAt(0).getTop()
							+ view.getFirstVisiblePosition() * view.getHeight();
					if (scrollY < needScrollHeight)
					{
						topTabLay.setVisibility(View.GONE);
					} else
					{
						topTabLay.setVisibility(View.VISIBLE);
					}
				}
			}

			@Override
			public void loadMoreWhenScrollToEnd()
			{
				if (!isShowDetail && isHaveNextPage)
				{
					// 只在显示评论时 和 在已经有评论的数据情况下才去加载更多
					loadMoreCommentData();
				}
			}
		}));

		loadMoreView = new LoadMoreView(DetailsActivity.this);
		loadMoreView.setNetErrorViewOnClickLinstener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				loadMoreView.setLoadingVisible(true);
				loadMoreCommentData();
			}
		});
		listView.addFooterView(loadMoreView);
		// 默认不显示底部加载更多视图
		loadMoreView.setVisibleView(false);

		likePraiseIcon = getResources().getDrawable(R.drawable.like);
		unLikePraiseIcon = getResources().getDrawable(R.drawable.unlike);
		likePraiseIcon.setBounds(0, 0, likePraiseIcon.getMinimumWidth(),
				likePraiseIcon.getMinimumHeight());
		unLikePraiseIcon.setBounds(0, 0, unLikePraiseIcon.getMinimumWidth(),
				unLikePraiseIcon.getMinimumHeight());

		headerView = View.inflate(DetailsActivity.this,
				R.layout.details_header_view, null);
		headerDetailTopLay = (RelativeLayout) headerView
				.findViewById(R.id.details_top_header_lay);
		headerIcon = (ImageView) headerView.findViewById(R.id.details_app_icon);
		headerName = (TextView) headerView.findViewById(R.id.details_app_name);
		headerStar = (RatingBar) headerView.findViewById(R.id.details_app_star);
		headerLab1 = (TextView) headerView.findViewById(R.id.details_lab1);
		headerLab2 = (TextView) headerView.findViewById(R.id.details_lab2);
		headerLab1.setVisibility(View.GONE);
		headerLab2.setVisibility(View.GONE);
		headerDownloadCountAndSize = (TextView) headerView
				.findViewById(R.id.details_download_count_and_size);
		headerOfficalLab = (TextView) headerView
				.findViewById(R.id.details_officaial_text);
		headerSafetyLab = (TextView) headerView
				.findViewById(R.id.details_safety_text);
		headerNoAdLab = (TextView) headerView
				.findViewById(R.id.details_no_ad_text);
		headerDownloadProgressButton = (DownloadProgressButton) headerView
				.findViewById(R.id.details_top_download_btn);
		headerCenterTabLay = (LinearLayout) headerView
				.findViewById(R.id.details_center_tab_lay);
		headerCenterTabLay.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener()
				{
					@Override
					public void onGlobalLayout()
					{
						if (needScrollHeight <= 0)
						{
							int[] locationCenter = new int[2];
							headerCenterTabLay
									.getLocationOnScreen(locationCenter);
							int statusBarHeight = DisplayUtil
									.getStatusBarHeight(DetailsActivity.this);
							needScrollHeight = locationCenter[1]
									- statusBarHeight
									- titleView.getLayoutParams().height;

							// 设置评论加载失败的视图高度
							LinearLayout.LayoutParams params = (LayoutParams) headerCommentErrorLay
									.getLayoutParams();
							params.height = height
									- headerDetailTopLay.getHeight()
									- titleView.getHeight()
									- bottomLay.getHeight() - statusBarHeight;
							headerCommentErrorLay.setLayoutParams(params);
						} else
						{
							headerCenterTabLay.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
						}
					}
				});

		headerCenterTabDetailLay = (FrameLayout) headerView
				.findViewById(R.id.details_center_details_btn_lay);
		headerCenterTabDetailLay.setOnClickListener(this);
		headerCenterTabDetailText = (TextView) headerView
				.findViewById(R.id.details_center_details_text);
		headerCenterTabDetailBottomLine = headerView
				.findViewById(R.id.details_center_details_line);

		headerCenterTabCommentLay = (FrameLayout) headerView
				.findViewById(R.id.details_center_comment_btn_lay);
		headerCenterTabCommentLay.setOnClickListener(this);
		headerCenterTabCommentText = (TextView) headerView
				.findViewById(R.id.details_center_comment_text);
		headerCenterTabCommentBottomLine = headerView
				.findViewById(R.id.details_center_comment_line);

		headerDetailsLay = (LinearLayout) headerView
				.findViewById(R.id.detials_details_lay);
		headerGiftLay = (TextView) headerView
				.findViewById(R.id.details_gift_lay);
		headerGiftLay.setVisibility(View.GONE);
		// headerGiftLay.setOnClickListener(this);
		headerScreenShortView = (ScreenShortHorizontalScrollView) headerView
				.findViewById(R.id.details_screenshort_lay);
		headerScreenShortView.setAction(action);

		upProint = getResources().getDrawable(R.drawable.up_point);
		downProint = getResources().getDrawable(R.drawable.down_point);
		downProint.setBounds(0, 0, downProint.getMinimumWidth(),
				downProint.getMinimumHeight());
		upProint.setBounds(0, 0, upProint.getMinimumWidth(),
				upProint.getMinimumHeight());

		headerAppDescribeTitleLay = (FrameLayout) headerView
				.findViewById(R.id.details_app_describe_title_lay);
		headerAppDescribeTitleLay.setOnClickListener(this);
		headerUpdateDescribeTitleLay = (FrameLayout) headerView
				.findViewById(R.id.details_update_describe_title_lay);
		headerUpdateDescribeTitleLay.setOnClickListener(this);
		headerAppDescribeStatusText = (TextView) headerView
				.findViewById(R.id.details_app_describle_status_text);
		headerUpdateDescribeStatusText = (TextView) headerView
				.findViewById(R.id.details_update_describle_status_text);
		headerUpdateDesSpltLine = headerView
				.findViewById(R.id.details_update_describle_split_line);
		headerUpdateDesTopSplitLine = headerView
				.findViewById(R.id.details_update_describe_top_split_line);
		headerAppDescribe = (TextView) headerView
				.findViewById(R.id.details_app_describe);
		headerUpdateDescribe = (TextView) headerView
				.findViewById(R.id.details_update_describe);
		headerCommentErrorLay = (FrameLayout) headerView
				.findViewById(R.id.details_comment_lay);
		headerCommentLoadingView = (LoadingView) headerView
				.findViewById(R.id.details_comment_loading_view);
		headerCommentErrorView = (NetErrorView) headerView
				.findViewById(R.id.details_comment_error_lay);

		headerCommentErrorLay.setVisibility(View.GONE);

		// 默认显示评论加载视图，隐藏评论评论列表和评论加载错的视图
		headerCommentLoadingView.setVisibilyView(true);
		headerCommentErrorView.setVisibility(View.GONE);

		headerCommentErrorView.setRefrushOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// 设置加载实刷新按钮的点击事件
				headerCommentLoadingView.setVisibilyView(true);
				headerCommentErrorView.setVisibility(View.GONE);
				headerCommentErrorLay.setVisibility(View.GONE);
				// 获取评论列表数据
				doLoadData(
						Constants.UAC_API_URL,
						new String[]
						{ GET_COMMENT_LIST_REQUEST_TAG },
						new ByteString[]
						{ getCommentListRequestData(appInfo.getSoftId(),
								((commentInfos.size() / LOAD_DATA_SIZE) + 1),
								LOAD_DATA_SIZE) }, "");
			}
		});
		listView.addHeaderView(headerView);
		listView.setAdapter(commentAdapter);

		if (softId != -1)
		{
			etagMark += softId + "";
			doLoadData(Constants.APP_API_URL, new String[]
			{ REQUEST_APP_DETAIL_ACTION }, new ByteString[]
			{ getReqAppDetailById(softId) }, etagMark);
		} else
		{
			etagMark += packageName;
			doLoadData(Constants.APP_API_URL, new String[]
			{ REQUEST_APP_DETAIL_ACTION }, new ByteString[]
			{ getReqAppDetailByPackageName(packageName) }, etagMark);
		}
	}

	@Override
	protected void loadDataSuccess(RspPacket rspPacket)
	{
		super.loadDataSuccess(rspPacket);
		List<String> actions = rspPacket.getActionList();
		for (String actionNet : actions)
		{
			if (RSPONSE_APP_DETAIL_ACTION.equals(actionNet))
			{
				// 详情数据返回
				try
				{
					RspAppDetail rspAppDetail = RspAppDetail
							.parseFrom(rspPacket.getParams(0));

					// 0=成功，1=失败，2=参数错误，3=应用不存在或已删除
					if (rspAppDetail.getRescode() == 0)
					{
						App.AppDetail info = rspAppDetail.getAppInfo();
						DLog.i("lilijun","appId---->>>"+info.getAppId());
						appInfo = ToolsUtil.getDetailAppInfo(info);
						setDataToView();
						// 获取评论列表数据
						doLoadData(
								Constants.UAC_API_URL,
								new String[]
								{ GET_COMMENT_LIST_REQUEST_TAG },
								new ByteString[]
								{ getCommentListRequestData(
										appInfo.getSoftId(),
										((commentInfos.size() / LOAD_DATA_SIZE) + 1),
										LOAD_DATA_SIZE) }, "");
						DataCollectionManager.getInstance().addRecord(action,
								DataCollectionManager.SOFT_ID,
								appInfo.getSoftId() + "",
								DataCollectionManager.PACK_ID,
								appInfo.getPackageId() + "");
						// 添加友盟的自定义事件的数据采集
						HashMap<String, String> values = new HashMap<String, String>();
						values.put(DataCollectionManager.SOFT_ID,
								appInfo.getSoftId() + "");
						values.put(DataCollectionManager.PACK_ID,
								appInfo.getPackageId() + "");
						DataCollectionManager
								.getInstance()
								.addYouMengEventRecord(
										this,
										action,
										DataCollectionConstant.EVENT_ID_GET_DETAILS_SUCCESS,
										values);
					} else
					{
						DLog.e("lilijun", "获取详情数据失败，code-------->>"
								+ rspAppDetail.getRescode() + "---msg----->>>"
								+ rspAppDetail.getResmsg());
						showErrorView();
					}
				} catch (Exception e)
				{
					DLog.e(TAG, "解析详情数据时发生异常#Excepton:", e);
					showErrorView();
				}
			} else if (GET_COMMENT_LIST_RSPONSE_TAG.equals(actionNet))
			{
				DLog.i("lilijun", "评论数据返回！！！@！！");
				// 评论数据返回
				try
				{
					RspRecommentList rspRecommentList = RspRecommentList
							.parseFrom(rspPacket.getParams(0));
					// 0=成功,1=相应错误,2=没有评论数据
					if (rspRecommentList.getRescode() == 0)
					{
						// 获取评论数据成功
						isGetCommentFailed = false;
						List<RecommentContent> list = rspRecommentList
								.getRecommentContentList();
						if (list != null)
						{
							if (list.isEmpty() || list.size() < LOAD_DATA_SIZE)
							{
								// 没有下一页数据了
								isHaveNextPage = false;
								// listView.setOnScrollListener(null);
								listView.removeFooterView(loadMoreView);
								if (!isShowDetail)
								{
									listView.showEndView();
								}
							} else
							{
								isHaveNextPage = true;
							}
							for (RecommentContent recommentContent : list)
							{
								// 添加到保存评论数据到集合中去
								saveCommentInfos.add(ToolsUtil
										.getCommentInfo(recommentContent));
							}
							commentInfos.clear();
							if (!isShowDetail)
							{
								// 当前显示评论界面
								commentInfos.addAll(saveCommentInfos);
								if (!commentInfos.isEmpty())
								{
									// 有评论数据
									commentAdapter.notifyDataSetChanged();
									// 隐藏错误视图
									headerCommentErrorLay
											.setVisibility(View.GONE);
									headerCommentLoadingView
											.setVisibilyView(false);
									headerCommentErrorView
											.setVisibility(View.GONE);

								}
							} else
							{
								// 当前显示详情界面
							}
						}
					} else if (rspRecommentList.getRescode() == 2)
					{
						// 获取评论数据成功
						isGetCommentFailed = false;
						// 没有评论数据
						DLog.i("lilijun", "没有评论数据");
						if (!isShowDetail)
						{
							// 显示没评论数据的视图
							headerCommentErrorLay.setVisibility(View.VISIBLE);
						} else
						{
							// 隐藏没评论数据的视图
							headerCommentErrorLay.setVisibility(View.GONE);
						}
						headerCommentLoadingView.setVisibilyView(false);
						headerCommentErrorView.setErrorView(
								R.drawable.no_comment_img, getResources()
										.getString(R.string.no_comments), "");
						headerCommentErrorView.setRefrushOnClickListener(null);
						headerCommentErrorView.setVisibility(View.VISIBLE);

					} else
					{
						// 获取评论数据失败
						isGetCommentFailed = true;
						DLog.e(TAG, "获取评论数据失败:" + rspRecommentList.getResmsg());
						loadMoreView.setVisibleView(false);
						headerCommentErrorView.showLoadFailedLay();
						headerCommentErrorView.setVisibility(View.VISIBLE);
						headerCommentLoadingView.setVisibilyView(false);
					}
				} catch (Exception e)
				{
					DLog.e(TAG, "解析评论数据时发生异常#Excepton:", e);
				}
			} else if (COMMIT_COMMENT_RSPONSE_TAG.equals(actionNet))
			{
				try
				{
					RspRecomment rspRecomment = RspRecomment
							.parseFrom(rspPacket.getParams(0));
					// 0=成功,1=相应错误,2=token错误,3=评论内容不合法
					if (rspRecomment.getRescode() == 0)
					{
						// 得到用户评论的相关信息 展示到评论列表去
						DLog.i("lilijun", "评论成功！！！！");
						CommentInfo commentInfo = ToolsUtil
								.getCommentInfo(rspRecomment.getContent());
						saveCommentInfos.add(0, commentInfo);
						if (!isShowDetail)
						{
							if (commentInfos.isEmpty())
							{
								headerCommentErrorLay.setVisibility(View.GONE);
								loadMoreView.setVisibleView(false);
							}
							commentInfos.add(0, commentInfo);
							commentAdapter.notifyDataSetChanged();
							if (!isHaveNextPage)
							{
								listView.showEndView();
							}
						}
					} else if (rspRecomment.getRescode() == 2)
					{
						// userToken错误
						// 退出登录
						LoginUserInfoManager.getInstance().exitLogin();
						Toast.makeText(DetailsActivity.this,
								getResources().getString(R.string.re_login),
								Toast.LENGTH_SHORT).show();
						LoginActivity.startLoginActivity(DetailsActivity.this,
								action);
					} else if (rspRecomment.getRescode() == 3)
					{
						// 评论内容有敏感字符
						Toast.makeText(
								DetailsActivity.this,
								getResources().getString(
										R.string.comment_content_problem),
								Toast.LENGTH_SHORT).show();
					} else
					{
						Toast.makeText(DetailsActivity.this,
								rspRecomment.getResmsg(), Toast.LENGTH_SHORT)
								.show();
					}
				} catch (Exception e)
				{
					DLog.e(TAG, "解析提交评论数据时发生异常#Excepton:", e);
				}
			} else if (PRAISE_APP_RSPONSE_TAG.equals(actionNet))
			{
				try
				{
					RspSetLikeCount rspSetLikeCount = RspSetLikeCount
							.parseFrom(rspPacket.getParams(0));
					if (rspSetLikeCount.getRescode() == 0)
					{
						// 提交赞数据成功
						DLog.i("lilijun", "提交赞数据成功！！！");
						appInfo.setPraiseCount(rspSetLikeCount.getLikeCount());
						appInfo.setFormatPraiseCount(ToolsUtil
								.getFormatPraiseCount(appInfo.getPraiseCount()));
						praiseBtn.setText(String.format(getResources()
								.getString(R.string.praise), appInfo
								.getFormatPraiseCount()));
					} else
					{
						// 提交赞数据失败
						DLog.i("lilijun",
								"提交赞数据失败   --->" + rspSetLikeCount.getRescode());
					}
				} catch (Exception e)
				{
					DLog.e(TAG, "解析提交赞数据时发生异常#Excepton:", e);
				}
			}
		}
	}

	@Override
	protected void loadDataFailed(RspPacket rspPacket)
	{
		super.loadDataFailed(rspPacket);
		if (rspPacket.getActionCount() != 1)
		{
			for (String action : rspPacket.getActionList())
			{
				if (action.equals(REQUEST_APP_DETAIL_ACTION))
				{
					// 获取详情信息失败
					showErrorView();
				} else if (action.equals(GET_COMMENT_LIST_REQUEST_TAG))
				{
					// 获取评论数据失败
					isGetCommentFailed = true;
					// 获取评论数据失败
					if (!saveCommentInfos.isEmpty())
					{
						// 这里只管显示出来loadMoreView的网络错误视图
						// 只要在切换到时候去控制loadMoreView这个是否显示或者隐藏即可
						loadMoreView.setNetErrorVisible(true);
					}
					// 获取评论信息失败
					if (!isShowDetail)
					{
						if (saveCommentInfos.isEmpty())
						{
							// 显示获取评论失败的视图
							headerCommentErrorLay.setVisibility(View.VISIBLE);
							// headerCommentErrorView.showLoadFailedLay();
							headerCommentErrorView.setVisibility(View.VISIBLE);
							headerCommentLoadingView.setVisibilyView(false);
						}
					}
				} else if (action.equals(COMMIT_COMMENT_RSPONSE_TAG))
				{
					// 提交评论失败
					Toast.makeText(DetailsActivity.this,
							getResources().getString(R.string.comment_failed),
							Toast.LENGTH_SHORT).show();
				}
			}

		} else
		{
			if (rspPacket.getAction(0).equals(REQUEST_APP_DETAIL_ACTION))
			{
				// 获取详情信息失败
				showErrorView();
			} else if (rspPacket.getAction(0).equals(
					GET_COMMENT_LIST_REQUEST_TAG))
			{
				// 获取评论数据失败
				isGetCommentFailed = true;
				// 获取评论数据失败
				if (!saveCommentInfos.isEmpty())
				{
					// 这里只管显示出来loadMoreView的网络错误视图
					// 只要在切换到时候去控制loadMoreView这个是否显示或者隐藏即可
					loadMoreView.setNetErrorVisible(true);
				}
				// 获取评论信息失败
				if (!isShowDetail)
				{
					if (saveCommentInfos.isEmpty())
					{
						// 显示获取评论失败的视图
						headerCommentErrorLay.setVisibility(View.VISIBLE);
						// headerCommentErrorView.showLoadFailedLay();
						headerCommentErrorView.setVisibility(View.VISIBLE);
						headerCommentLoadingView.setVisibilyView(false);
					}
				}
			} else if (rspPacket.getAction(0)
					.equals(COMMIT_COMMENT_RSPONSE_TAG))
			{
				// 提交评论失败
				Toast.makeText(DetailsActivity.this,
						getResources().getString(R.string.comment_failed),
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
			if (action.equals(REQUEST_APP_DETAIL_ACTION))
			{
				// 获取详情信息失败
				showErrorView();
			} else if (action.equals(GET_COMMENT_LIST_REQUEST_TAG))
			{
				// 获取评论数据失败
				isGetCommentFailed = true;
				// 获取评论数据失败
				if (!saveCommentInfos.isEmpty())
				{
					// 这里只管显示出来loadMoreView的网络错误视图
					// 只要在切换到时候去控制loadMoreView这个是否显示或者隐藏即可
					loadMoreView.setNetErrorVisible(true);
				}
				// 获取评论信息失败
				if (!isShowDetail)
				{
					if (saveCommentInfos.isEmpty())
					{
						// 显示获取评论失败的视图
						headerCommentErrorLay.setVisibility(View.VISIBLE);
						// headerCommentErrorView.showLoadFailedLay();
						headerCommentErrorView.setVisibility(View.VISIBLE);
						headerCommentLoadingView.setVisibilyView(false);
					}
				}
			} else if (action.equals(COMMIT_COMMENT_RSPONSE_TAG))
			{
				// 提交评论失败
				Toast.makeText(DetailsActivity.this,
						getResources().getString(R.string.comment_failed),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void tryAgain()
	{
		super.tryAgain();
		if (softId != -1)
		{
			doLoadData(Constants.APP_API_URL, new String[]
			{ REQUEST_APP_DETAIL_ACTION }, new ByteString[]
			{ getReqAppDetailById(softId) }, etagMark);
		} else
		{
			doLoadData(Constants.APP_API_URL, new String[]
			{ REQUEST_APP_DETAIL_ACTION }, new ByteString[]
			{ getReqAppDetailByPackageName(packageName) }, etagMark);
		}
	}

	/**
	 * 设置数据到视图
	 */
	@SuppressWarnings("deprecation")
	private void setDataToView()
	{
		if (PraiseManager.getInstance().isPraised(appInfo.getSoftId()))
		{
			// 赞过
			praiseBtn.setCompoundDrawables(null, likePraiseIcon, null, null);
			if (appInfo.getPraiseCount() == 0)
			{
				// 如果用户赞过 但服务器那边给到的数据为0 则人为的加上1
				appInfo.setPraiseCount(1);
				appInfo.setFormatPraiseCount(1 + "");
				praiseBtn.setText(String.format(
						getResources().getString(R.string.praise), "1"));
			}
		} else
		{
			// 没赞过
			praiseBtn.setCompoundDrawables(null, unLikePraiseIcon, null, null);
		}
		// 设置赞的次数
		praiseBtn.setText(String.format(
				getResources().getString(R.string.praise),
				appInfo.getFormatPraiseCount()));

		downloadTopOnclickListener = new DownloadOnclickListener(
				this,
				appInfo,
				DataCollectionManager
						.getAction(
								action,
								DataCollectionConstant.DATA_COLLECTION_CP_DETAIL_CLICK_TOP_DOWNLOAD_BTN_VALUE));
		downloadBottomOnclickListener = new DownloadOnclickListener(
				this,
				appInfo,
				DataCollectionManager
						.getAction(
								action,
								DataCollectionConstant.DATA_COLLECTION_CP_DETAIL_CLICK_BOTTOM_DOWNLOAD_BTN_VALUE));
		headerDownloadProgressButton.downloadbtn
				.setOnClickListener(downloadTopOnclickListener);
		bottomDownloadBtn.downloadbtn
				.setOnClickListener(downloadBottomOnclickListener);

		headerDownloadProgressButton.setInfo(appInfo.getPackageName());
		bottomDownloadBtn.setInfo(appInfo.getPackageName());

		imageLoaderManager.displayImage(appInfo.getIconUrl(), headerIcon,
				iconOptions);
		headerName.setText(appInfo.getName());
		headerStar.setRating(appInfo.getStarLevel());
		headerDownloadCountAndSize.setText(appInfo.getFormatDownloadCount()
				+ getResources().getString(R.string.count_download) + " | "
				+ appInfo.getFormatSize());
		headerAppDescribe.setText(appInfo.getDescribe());
		if ("".equals(appInfo.getUpdateDescribe().trim()))
		{
			headerUpdateDescribeTitleLay.setVisibility(View.GONE);
			headerUpdateDescribe.setVisibility(View.GONE);
			headerUpdateDesSpltLine.setVisibility(View.GONE);
			headerUpdateDesTopSplitLine.setVisibility(View.GONE);
		} else
		{
			headerUpdateDescribe.setText(appInfo.getUpdateDescribe());
			headerUpdateDescribeTitleLay.setVisibility(View.VISIBLE);
			headerUpdateDescribe.setVisibility(View.VISIBLE);
			headerUpdateDesSpltLine.setVisibility(View.VISIBLE);
			headerUpdateDesTopSplitLine.setVisibility(View.VISIBLE);
		}

		if (appInfo.isOfficial())
		{
			headerOfficalLab.setVisibility(View.VISIBLE);
		} else
		{
			headerOfficalLab.setVisibility(View.GONE);
		}
		if (appInfo.isSafety())
		{
			headerSafetyLab.setVisibility(View.VISIBLE);
		} else
		{
			headerSafetyLab.setVisibility(View.GONE);
		}
		if (appInfo.isNoAd())
		{
			headerNoAdLab.setVisibility(View.VISIBLE);
		} else
		{
			headerNoAdLab.setVisibility(View.GONE);
		}
		if (appInfo.isHaveGift())
		{
			// 因没有礼包功能，故隐藏
			headerGiftLay.setVisibility(View.GONE);
			// headerGiftLay.setVisibility(View.VISIBLE);
			headerLab1.setText(this.getResources().getString(R.string.gift));
			headerLab1.setBackgroundResource(R.drawable.lab_gift_bg_shape);
			headerLab1.setVisibility(View.VISIBLE);
		} else
		{
			headerGiftLay.setVisibility(View.GONE);
		}

		if (appInfo.isFirst())
		{
			if (headerLab1.getVisibility() == View.VISIBLE)
			{
				headerLab2.setText(this.getResources()
						.getString(R.string.first));
				// headerLab2.setBackground(this.getResources().getDrawable(
				// R.drawable.lab_first_bg_shape));
				headerLab2.setBackgroundResource(R.drawable.lab_first_bg_shape);
				headerLab2.setVisibility(View.VISIBLE);
			} else
			{
				headerLab1.setText(this.getResources()
						.getString(R.string.first));
				// headerLab1.setBackground(this.getResources().getDrawable(
				// R.drawable.lab_first_bg_shape));
				headerLab1.setBackgroundResource(R.drawable.lab_first_bg_shape);
				headerLab1.setVisibility(View.VISIBLE);
			}
		}

		if (headerLab1.getVisibility() == View.VISIBLE)
		{
			// 第一个标签已经被设置了值
			if (headerLab2.getVisibility() == View.GONE)
			{
				// 第二个标签还未设置
				if (appInfo.isHot())
				{
					headerLab2.setText(this.getResources().getString(
							R.string.hot));
					// headerLab2.setBackground(this.getResources().getDrawable(
					// R.drawable.lab_hot_bg_shape));
					headerLab2
							.setBackgroundResource(R.drawable.lab_hot_bg_shape);
					headerLab2.setVisibility(View.VISIBLE);
				}
				if (appInfo.isBoutique())
				{
					if (headerLab2.getVisibility() == View.GONE)
					{
						headerLab2.setText(this.getResources().getString(
								R.string.boutique));
						// headerLab2.setBackground(this.getResources()
						// .getDrawable(R.drawable.lab_boutique_bg_shape));
						headerLab2
								.setBackgroundResource(R.drawable.lab_boutique_bg_shape);
						headerLab2.setVisibility(View.VISIBLE);
					}
				}
			}
		} else
		{
			// 第一个标签还没有设置值 则证明 没有礼包和不是首发
			if (appInfo.isHot())
			{
				headerLab1.setText(this.getResources().getString(R.string.hot));
				// headerLab1.setBackground(this.getResources().getDrawable(
				// R.drawable.lab_hot_bg_shape));
				headerLab1.setBackgroundResource(R.drawable.lab_hot_bg_shape);
				headerLab1.setVisibility(View.VISIBLE);
			}
			if (appInfo.isBoutique())
			{
				if (headerLab1.getVisibility() == View.VISIBLE)
				{
					// 如果第一个标签被设置了 则设置第二个标签
					headerLab2.setText(this.getResources().getString(
							R.string.boutique));
					// headerLab2.setBackground(this.getResources().getDrawable(
					// R.drawable.lab_boutique_bg_shape));
					headerLab2
							.setBackgroundResource(R.drawable.lab_boutique_bg_shape);
					headerLab2.setVisibility(View.VISIBLE);
				} else
				{
					// 如果第一个标签没有被设置
					headerLab1.setText(this.getResources().getString(
							R.string.boutique));
					// headerLab1.setBackground(this.getResources().getDrawable(
					// R.drawable.lab_boutique_bg_shape));
					headerLab1
							.setBackgroundResource(R.drawable.lab_boutique_bg_shape);
					headerLab1.setVisibility(View.VISIBLE);
					headerLab2.setVisibility(View.GONE);
				}
			}
		}
		headerScreenShortView.setUrls(appInfo.getScreenUrls(), listView);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.details_app_describe_title_lay:
			// 应用描述部分
			// if (headerAppDescribe.getMaxLines() == 150)
			if (isExplandDetail)
			{
				headerAppDescribe.setMaxLines(4);
				headerAppDescribeStatusText.setText(getResources().getString(
						R.string.expand));
				headerAppDescribeStatusText.setCompoundDrawables(null, null,
						downProint, null);
				isExplandDetail = false;
			} else
			{
				headerAppDescribe.setMaxLines(150);
				headerAppDescribeStatusText.setText(getResources().getString(
						R.string.pack_up));
				headerAppDescribeStatusText.setCompoundDrawables(null, null,
						upProint, null);
				isExplandDetail = true;
			}
			break;
		case R.id.details_update_describe_title_lay:
			// 更新说明部分
			// if (headerUpdateDescribe.getMaxLines() == 100)
			if (isExplandUpdate)
			{
				headerUpdateDescribe.setMaxLines(4);
				headerUpdateDescribeStatusText.setText(getResources()
						.getString(R.string.expand));
				headerUpdateDescribeStatusText.setCompoundDrawables(null, null,
						downProint, null);
				isExplandUpdate = false;
			} else
			{
				headerUpdateDescribe.setMaxLines(100);
				headerUpdateDescribeStatusText.setText(getResources()
						.getString(R.string.pack_up));
				headerUpdateDescribeStatusText.setCompoundDrawables(null, null,
						upProint, null);
				isExplandUpdate = true;
			}
			break;
		case R.id.details_top_details_btn_lay:
			// 顶部固定不动Tab的详情部分(显示详情部分、隐藏评论列表)
			bottomDownloadBtn.setVisibility(View.VISIBLE);
			commentBtn.setVisibility(View.GONE);

			topTabDetailText.setTextColor(getResources().getColor(
					R.color.indicator_select_text_color));
			topTabCommentText.setTextColor(getResources().getColor(
					R.color.indicator_normal_text_color));
			topTabDetailBottomLine.setVisibility(View.VISIBLE);
			topTabCommentBottomLine.setVisibility(View.GONE);

			headerCenterTabDetailText.setTextColor(getResources().getColor(
					R.color.indicator_select_text_color));
			headerCenterTabCommentText.setTextColor(getResources().getColor(
					R.color.indicator_normal_text_color));
			headerCenterTabCommentBottomLine.setVisibility(View.GONE);
			headerCenterTabDetailBottomLine.setVisibility(View.VISIBLE);

			headerCommentErrorLay.setVisibility(View.GONE);
			headerDetailsLay.setVisibility(View.VISIBLE);
			loadMoreView.setVisibleView(false);
			listView.hideEndView();
			// 清空adapter的数据源
			commentInfos.clear();
			commentAdapter.notifyDataSetChanged();
			loadMoreView.setVisibleView(false);
			listView.hideEndView();
			isShowDetail = true;
			break;
		case R.id.details_top_comment_btn_lay:
			// 顶部固定不动Tab的评价部分(显示评论列表、隐藏显示详情部分)
			commentBtn.setVisibility(View.VISIBLE);
			bottomDownloadBtn.setVisibility(View.GONE);

			headerCenterTabDetailText.setTextColor(getResources().getColor(
					R.color.indicator_normal_text_color));
			headerCenterTabCommentText.setTextColor(getResources().getColor(
					R.color.indicator_select_text_color));
			headerCenterTabCommentBottomLine.setVisibility(View.VISIBLE);
			headerCenterTabDetailBottomLine.setVisibility(View.GONE);

			topTabDetailText.setTextColor(getResources().getColor(
					R.color.indicator_normal_text_color));
			topTabCommentText.setTextColor(getResources().getColor(
					R.color.indicator_select_text_color));
			topTabDetailBottomLine.setVisibility(View.GONE);
			topTabCommentBottomLine.setVisibility(View.VISIBLE);

			headerCommentErrorLay.setVisibility(View.VISIBLE);
			headerDetailsLay.setVisibility(View.GONE);
			commentInfos.clear();
			// 将之前保存的评论数据添加到adapter的数据源中去
			commentInfos.addAll(saveCommentInfos);
			commentAdapter.notifyDataSetChanged();
			DLog.i("lilijun", "saveCommentInfos.size()--------->>"
					+ saveCommentInfos.size());
			if (commentInfos.isEmpty())
			{
				loadMoreView.setVisibleView(false);
			} else
			{
				if (isHaveNextPage)
				{
					loadMoreView.setVisibleView(true);
				} else
				{
					loadMoreView.setVisibleView(false);
					listView.showEndView();
				}
				headerCommentErrorLay.setVisibility(View.GONE);
			}
			if (isGetCommentFailed)
			{
				// 加载评论失败
				headerCommentErrorLay.setVisibility(View.VISIBLE);
				headerCommentErrorView.setVisibility(View.VISIBLE);
				headerCommentLoadingView.setVisibility(View.GONE);
			}
			isShowDetail = false;
			break;
		case R.id.details_center_details_btn_lay:
			// 中间Tab的详情部分(显示详情部分、隐藏评论列表)
			bottomDownloadBtn.setVisibility(View.VISIBLE);
			commentBtn.setVisibility(View.GONE);

			topTabDetailText.setTextColor(getResources().getColor(
					R.color.indicator_select_text_color));
			topTabCommentText.setTextColor(getResources().getColor(
					R.color.indicator_normal_text_color));
			topTabDetailBottomLine.setVisibility(View.VISIBLE);
			topTabCommentBottomLine.setVisibility(View.GONE);

			headerCenterTabDetailText.setTextColor(getResources().getColor(
					R.color.indicator_select_text_color));
			headerCenterTabCommentText.setTextColor(getResources().getColor(
					R.color.indicator_normal_text_color));
			headerCenterTabCommentBottomLine.setVisibility(View.GONE);
			headerCenterTabDetailBottomLine.setVisibility(View.VISIBLE);

			headerCommentErrorLay.setVisibility(View.GONE);
			headerDetailsLay.setVisibility(View.VISIBLE);
			loadMoreView.setVisibleView(false);
			listView.hideEndView();
			// 清空adapter的数据源
			commentInfos.clear();
			commentAdapter.notifyDataSetChanged();
			loadMoreView.setVisibleView(false);
			listView.hideEndView();
			isShowDetail = true;
			break;
		case R.id.details_center_comment_btn_lay:
			// 中间Tab的评价部分(显示评论列表、隐藏显示详情部分)
			commentBtn.setVisibility(View.VISIBLE);
			bottomDownloadBtn.setVisibility(View.GONE);

			headerCenterTabDetailText.setTextColor(getResources().getColor(
					R.color.indicator_normal_text_color));
			headerCenterTabCommentText.setTextColor(getResources().getColor(
					R.color.indicator_select_text_color));
			headerCenterTabCommentBottomLine.setVisibility(View.VISIBLE);
			headerCenterTabDetailBottomLine.setVisibility(View.GONE);

			topTabDetailText.setTextColor(getResources().getColor(
					R.color.indicator_normal_text_color));
			topTabCommentText.setTextColor(getResources().getColor(
					R.color.indicator_select_text_color));
			topTabDetailBottomLine.setVisibility(View.GONE);
			topTabCommentBottomLine.setVisibility(View.VISIBLE);

			headerCommentErrorLay.setVisibility(View.VISIBLE);
			headerDetailsLay.setVisibility(View.GONE);
			commentInfos.clear();
			// 将之前保存的评论数据添加到adapter的数据源中去
			commentInfos.addAll(saveCommentInfos);
			commentAdapter.notifyDataSetChanged();
			if (commentInfos.isEmpty())
			{
				loadMoreView.setVisibleView(false);
			} else
			{
				if (isHaveNextPage)
				{
					loadMoreView.setVisibleView(true);
				} else
				{
					loadMoreView.setVisibleView(false);
					listView.showEndView();
				}
				headerCommentErrorLay.setVisibility(View.GONE);
			}
			if (isGetCommentFailed)
			{
				// 加载评论失败
				headerCommentErrorLay.setVisibility(View.VISIBLE);
				headerCommentErrorView.setVisibility(View.VISIBLE);
				headerCommentLoadingView.setVisibility(View.GONE);
			}
			isShowDetail = false;
			break;
		// case R.id.details_gift_lay:
		// // 礼包部分
		// GiftDetailActivity2.startGiftDetailActivity2(DetailsActivity.this,
		// appInfo.getSoftId(), action);
		// break;
		case R.id.details_bottom_comment_btn:
			// 底部我要评论按钮
			if (LoginUserInfoManager.getInstance().isHaveUserLogin())
			{
				final CenterDialog commentDialog = new CenterDialog(
						DetailsActivity.this);
				commentDialog.show();
				commentDialog.setTitleVisible(false);
				View view = View.inflate(DetailsActivity.this,
						R.layout.comment_dialog, null);
				view.setBackgroundResource(R.drawable.loading_dialog_bg_shape);
				commentDialog.setCenterView(view);
				final TextView ratingDes = (TextView) view
						.findViewById(R.id.comment_dialog_star_describle);
				final RatingBar ratingBar = (RatingBar) view
						.findViewById(R.id.comment_dialog_star);
				ratingBar
						.setOnRatingBarChangeListener(new OnRatingBarChangeListener()
						{
							@Override
							public void onRatingChanged(RatingBar ratingBar,
									float rating, boolean fromUser)
							{
								if (rating == 1f)
								{
									ratingDes.setText(getResources().getString(
											R.string.comment_rating_1));
								} else if (rating == 2f)
								{
									ratingDes.setText(getResources().getString(
											R.string.comment_rating_2));
								} else if (rating == 3f)
								{
									ratingDes.setText(getResources().getString(
											R.string.comment_rating_3));
								} else if (rating == 4f)
								{
									ratingDes.setText(getResources().getString(
											R.string.comment_rating_4));
								} else if (rating == 5f)
								{
									ratingDes.setText(getResources().getString(
											R.string.comment_rating_5));
								}
							}
						});
				final EditText commentInput = (EditText) view
						.findViewById(R.id.comment_dialog_comment_input);
				commentInput.setHintTextColor(getResources().getColor(
						R.color.divider_color));
				Button cancleBtn = (Button) view
						.findViewById(R.id.comment_dialog_cancle_btn);
				cancleBtn.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						commentDialog.dismiss();
					}
				});
				Button okBtn = (Button) view
						.findViewById(R.id.comment_dialog_ok_btn);
				okBtn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// 提交评论信息
						String comment = commentInput.getText().toString()
								.trim();
						if ("".equals(comment))
						{
							comment = ratingDes.getText().toString().trim();
						}

						LoginedUserInfo userInfo = LoginUserInfoManager
								.getInstance().getLoginedUserInfo();
						DLog.i("lilijun", "提交评论内容---------->>>" + comment);
						DLog.i("lilijun", "appInfo.getSoftId()--------->>>"
								+ appInfo.getSoftId());
						// 提交评论信息
						doLoadData(
								Constants.UAC_API_URL,
								new String[]
								{ COMMIT_COMMENT_REQUEST_TAG },
								new ByteString[]
								{ getCommitCommentRequestData(
										userInfo.getUserId(),
										userInfo.getUserToken(),
										appInfo.getSoftId(),
										(int) ratingBar.getRating(), comment) },
								"");
						commentDialog.dismiss();
					}
				});
			} else
			{
				LoginActivity.startLoginActivity(DetailsActivity.this, action);
			}

			break;
		case R.id.details_praise_btn:
			// 底部 赞 按钮
			if (PraiseManager.getInstance().isPraised(appInfo.getSoftId()))
			{
				// 取消赞
				PraiseManager.getInstance()
						.removePraiseApp(appInfo.getSoftId());
				appInfo.setPraiseCount(appInfo.getPraiseCount() - 1);
				appInfo.setFormatPraiseCount(ToolsUtil
						.getFormatPraiseCount(appInfo.getPraiseCount()));
				praiseBtn.setText(String.format(
						getResources().getString(R.string.praise),
						appInfo.getFormatPraiseCount()));
				praiseBtn.setCompoundDrawables(null, unLikePraiseIcon, null,
						null);
				doLoadData(Constants.APP_API_URL, new String[]
				{ PRAISE_APP_REQUEST_TAG }, new ByteString[]
				{ getPraiseRequestData(appInfo.getSoftId(), false) }, "");
			} else
			{
				// 赞
				PraiseManager.getInstance().addPraiseApp(appInfo.getSoftId());
				appInfo.setPraiseCount(appInfo.getPraiseCount() + 1);
				appInfo.setFormatPraiseCount(ToolsUtil
						.getFormatPraiseCount(appInfo.getPraiseCount()));
				praiseBtn.setText(String.format(
						getResources().getString(R.string.praise),
						appInfo.getFormatPraiseCount()));
				praiseBtn
						.setCompoundDrawables(null, likePraiseIcon, null, null);
				doLoadData(Constants.APP_API_URL, new String[]
				{ PRAISE_APP_REQUEST_TAG }, new ByteString[]
				{ getPraiseRequestData(appInfo.getSoftId(), true) }, "");
			}

			break;
		case R.id.details_share_btn:
			// 底部 分享 按钮
			DataCollectionManager
					.getInstance()
					.addRecord(
							DataCollectionManager
									.getAction(
											action,
											DataCollectionConstant.DATA_COLLECTION_CP_DETAIL_CLICK_SHARE_BTN_VALUE),
							DataCollectionManager.SOFT_ID,
							appInfo.getSoftId() + "",
							DataCollectionManager.PACK_ID,
							appInfo.getPackageId() + "");

			// 添加友盟的自定义事件的数据采集
			HashMap<String, String> values = new HashMap<String, String>();
			values.put(DataCollectionManager.SOFT_ID, appInfo.getSoftId() + "");
			values.put(DataCollectionManager.PACK_ID, appInfo.getPackageId()
					+ "");
			DataCollectionManager.getInstance().addYouMengEventRecord(this,
					action,
					DataCollectionConstant.EVENT_ID_CLICK_DETAIL_SHARE_BTN,
					values);

			String shareUrl = Constants.APP_API_URL + "/app?appid="
					+ appInfo.getSoftId();
			String shareContent = String.format(
					getResources().getString(R.string.share_app_content),
					appInfo.getName()) + shareUrl;
			String shareWeixinContent = String
					.format(getResources().getString(
							R.string.share_app_weixin_content),
							appInfo.getName());
			DisplayUtil.showShareDialog(DetailsActivity.this, getResources()
					.getString(R.string.app_name), shareContent, getResources()
					.getString(R.string.share_app_weixin_title),
					shareWeixinContent, shareUrl, action);
			// Intent intent = new Intent(Intent.ACTION_SEND);
			// intent.setType("text/plain");
			// intent.putExtra(Intent.EXTRA_SUBJECT,
			// getResources().getString(R.string.share_app_title));
			// intent.putExtra(Intent.EXTRA_TEXT, shareContent);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// startActivity(Intent.createChooser(intent, getTitle()));

			break;
		}
	}

	/**
	 * 加载评论列表下一页数据
	 */
	private void loadMoreCommentData()
	{
		doLoadData(
				Constants.UAC_API_URL,
				new String[]
				{ GET_COMMENT_LIST_REQUEST_TAG, },
				new ByteString[]
				{ getCommentListRequestData(appInfo.getSoftId(),
						((commentInfos.size() / LOAD_DATA_SIZE) + 1),
						LOAD_DATA_SIZE) }, "");
	}

	@Override
	public void finish()
	{
		if (isFromSplash)
		{
			// 跳转到主页
			Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
			startActivity(intent);
		}
		super.finish();
	}

	@Override
	protected void onDestroy()
	{
		headerDownloadProgressButton.onDestory();
		bottomDownloadBtn.onDestory();
		super.onDestroy();
	}

	private ByteString getReqAppDetailById(long id)
	{
		ReqAppDetail.Builder builder = ReqAppDetail.newBuilder();
		builder.setAppId(id);
		ReqAppDetail reqAppDetail = builder.build();
		ByteString byteString = reqAppDetail.toByteString();
		return byteString;
	}

	private ByteString getReqAppDetailByPackageName(String packageName)
	{
		ReqAppDetail.Builder builder = ReqAppDetail.newBuilder();
		builder.setPackName(packageName);
		ReqAppDetail reqAppDetail = builder.build();
		ByteString byteString = reqAppDetail.toByteString();
		return byteString;
	}

	/**
	 * 获取应用的评论列表的请求参数
	 * 
	 * @param softId
	 * @param index
	 * @param size
	 * @return
	 */
	private ByteString getCommentListRequestData(long softId, int index,
			int size)
	{
		ReqRecommentList.Builder builder = ReqRecommentList.newBuilder();
		builder.setGameId(softId);
		builder.setPageIndex(index);
		builder.setPageSize(size);
		return builder.build().toByteString();
	}

	/**
	 * 获取提交评论的请求数据
	 * 
	 * @param userId
	 * @param userToken
	 * @param softId
	 * @param starLevel
	 * @param commentContent
	 * @return
	 */
	private ByteString getCommitCommentRequestData(int userId,
			String userToken, long softId, int starLevel, String commentContent)
	{
		ReqRecomment.Builder builder = ReqRecomment.newBuilder();
		builder.setUid(userId);
		builder.setUserToken(userToken);
		builder.setGameId(softId);
		builder.setRecommLevel(starLevel);
		builder.setContent(commentContent);
		return builder.build().toByteString();
	}

	/**
	 * 获取点赞/取消赞的请求数据
	 * 
	 * @param softId
	 *            软件id
	 * @param isPraise
	 *            是否是赞 ,true=赞，false=取消赞
	 * @return
	 */
	private ByteString getPraiseRequestData(long softId, boolean isPraise)
	{
		ReqSetLikeCount.Builder builder = ReqSetLikeCount.newBuilder();
		builder.setAppId(softId);
		if (isPraise)
		{
			builder.setLikeCount(1);
		} else
		{
			builder.setLikeCount(-1);
		}
		return builder.build().toByteString();
	}

	/**
	 * 根据id跳转到详情界面
	 * 
	 * @param softId
	 *            软件id
	 */
	public static void startDetailsActivityById(Context context, long softId,
			String action)
	{
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra("softId", softId);
		DataCollectionManager.startActivity(context, intent, action);
	}

	/**
	 * 根据id从闪屏处跳转到详情界面
	 * 
	 * @param softId
	 *            软件id
	 */
	public static void startDetailsActivityByIdFromSplash(Context context,
			long softId, String action)
	{
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra("softId", softId);
		// 是否是从闪屏处跳转过来
		intent.putExtra("isFromSplash", true);
		DataCollectionManager.startActivity(context, intent, action);
	}

	/**
	 * 根据跳转到详情界面
	 * 
	 *            包名
	 */
	public static void startDetailsActivityByPackageName(Context context,
			String packageName, String action)
	{
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra("packageName", packageName);
		DataCollectionManager.startActivity(context, intent, action);
	}
}
