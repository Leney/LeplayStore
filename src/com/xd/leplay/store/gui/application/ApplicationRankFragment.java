package com.xd.leplay.store.gui.application;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.gui.adapter.RankAdapter;
import com.xd.leplay.store.gui.details.DetailsActivity;
import com.xd.leplay.store.gui.details.view.DetailDownloadProgressButton;
import com.xd.leplay.store.gui.main.BaseTabChildFragment;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.model.proto.App;
import com.xd.leplay.store.model.proto.App.ReqRecommAppList;
import com.xd.leplay.store.model.proto.App.RspRecommAppList;
import com.xd.leplay.store.model.proto.Packet.RspPacket;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.LoadMoreScrollListener;
import com.xd.leplay.store.view.LoadMoreScrollListener.setOnScrollToEndListener;
import com.xd.leplay.store.view.LoadMoreView;
import com.xd.leplay.store.view.MarketListView;
import com.xd.leplay.store.view.download.DownloadOnclickListener;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用Tab中 排行子Fragment
 *
 * @author lilijun
 */
public class ApplicationRankFragment extends BaseTabChildFragment implements
        OnClickListener {
    private static final String TAG = "ApplicationRankFragment";

    private MarketListView listView = null;

    private RankAdapter adapter = null;

    private ArrayList<ListAppInfo> appInfos = null;

    /**
     * 列表一次加载的长度
     */
    private static final int LOAD_DATA_SIZE = 10;

    /**
     * 标识是否还有下一页列表数据
     */
    private boolean isHaveNextPageData = true;

    private LoadMoreView loadMoreView = null;

    /**
     * 获取列表的类型的标识值
     * 1=首页推荐,2=首页新品应用首发,3=首页新品游戏首发,4=首页玩赚金币(应用),5=首页玩赚金币(游戏)，6=应用精品，
     * 7=应用排行,8=应用必备,9=游戏精品,10=游戏排行
     */
    private final int LIST_TYPE = 7;

    /**
     * 请求列表数据的TAG
     */
    private final String REQUEST_LIST_TAG = "ReqRecommAppList";

    /**
     * 响应列表数据的TAG
     */
    private final String RSPONSE_LIST_TAG = "RspRecommAppList";

    /**
     * 判断是否是第一次加载数据
     */
    private boolean isFirstLoadData = true;

    private static String thisAction = "";

    /**
     * 应用分类列表的etag标识值
     */
    private String etagMark = "applicationRankList";

    private View headerView;

    /**
     * 排行列表顶部view前三名应用图标
     */
    private ImageView rankCenterImage, rankLeftImage, rankRightImage;

    /**
     * 排行列表顶部view前三名应用名称
     */
    private TextView rankCenterName, rankLeftName, rankRightName;

    /**
     * 排行列表顶部view前三名应用下载次数
     */
    private TextView rankCenterDownloadCounts, rankLeftDownloadCounts,
            rankRightDownloadCounts;

    /**
     * 排行列表顶部view下载状态按钮
     */
    private DetailDownloadProgressButton downloadStateLeftButton,
            downloadStateCenterButton, downloadStateRightButton;

    /**
     * 排行列表顶部view做任务标识按钮
     */
    private TextView rankListTaskTextLeft, rankListTaskTextCenter,
            rankListTaskTextRight;
    /**
     * 下载监听器
     */
    private DownloadOnclickListener listenerLeft, listenerCenter,
            listenerRight;

    /**
     * 下载列表加载图片的配置
     */
    private DisplayImageOptions options = null;

    /**
     * 图片加载器
     */
    private ImageLoaderManager imageLoader = null;

    /**
     * 排名前三的应用
     */
    private ListAppInfo[] top3;

    private RelativeLayout rankListLayLeft, rankListLayCenter,
            rankListLayRight;

    public static ApplicationRankFragment getInstance(String beforeAction) {
        thisAction = DataCollectionManager.getAction(beforeAction,
                DataCollectionConstant.DATA_COLLECTION_APP_RANK_VALUE);
        return new ApplicationRankFragment();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirstLoadData) {
            action = thisAction;
            DataCollectionManager.getInstance().addRecord(action);
            isFirstLoadData = false;
        }
    }

    @Override
    protected void initView(FrameLayout view) {
        action = thisAction;
        listView = new MarketListView(getActivity());
        setCenterView(listView);
        imageLoader = ImageLoaderManager.getInstance();
        options = DisplayUtil.getListIconImageLoaderOptions();
        appInfos = new ArrayList<ListAppInfo>();
        adapter = new RankAdapter(getActivity(), listView, appInfos, action);
        listView.setOnScrollListener(new LoadMoreScrollListener(ImageLoader
                .getInstance(), true, true, new setOnScrollToEndListener() {
            @Override
            public void loadMoreWhenScrollToEnd() {
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
                R.layout.soft_list_rank_header_view, null);
        rankListTaskTextLeft = (TextView) headerView
                .findViewById(R.id.rank_list_do_task_text_left);
        rankListTaskTextCenter = (TextView) headerView
                .findViewById(R.id.rank_list_do_task_text_center);
        rankListTaskTextRight = (TextView) headerView
                .findViewById(R.id.rank_list_do_task_text_right);

        listenerLeft = new DownloadOnclickListener(getActivity(), action);
        listenerCenter = new DownloadOnclickListener(getActivity(), action);
        listenerRight = new DownloadOnclickListener(getActivity(), action);
        rankListLayLeft = (RelativeLayout) headerView
                .findViewById(R.id.rank_list_lay_left);
        rankListLayCenter = (RelativeLayout) headerView
                .findViewById(R.id.rank_list_lay_center);
        rankListLayRight = (RelativeLayout) headerView
                .findViewById(R.id.rank_list_lay_right);
        rankListLayLeft.setOnClickListener(this);
        rankListLayCenter.setOnClickListener(this);
        rankListLayRight.setOnClickListener(this);
        rankCenterImage = (ImageView) headerView
                .findViewById(R.id.soft_list_app_icon_center);
        rankLeftImage = (ImageView) headerView
                .findViewById(R.id.soft_list_app_icon_left);
        rankRightImage = (ImageView) headerView
                .findViewById(R.id.soft_list_app_icon_right);
        rankCenterName = (TextView) headerView
                .findViewById(R.id.rank_list_app_name_center);
        rankLeftName = (TextView) headerView
                .findViewById(R.id.rank_list_app_name_left);
        rankRightName = (TextView) headerView
                .findViewById(R.id.rank_list_app_name_right);

        rankCenterDownloadCounts = (TextView) headerView
                .findViewById(R.id.rank_list_total_download_center);
        rankLeftDownloadCounts = (TextView) headerView
                .findViewById(R.id.rank_list_total_download_left);
        rankRightDownloadCounts = (TextView) headerView
                .findViewById(R.id.rank_list_total_download_right);
        downloadStateLeftButton = (DetailDownloadProgressButton) headerView
                .findViewById(R.id.rank_list_download_btn_left);
        downloadStateLeftButton.downloadbtn.setTextSize(13);
        downloadStateCenterButton = (DetailDownloadProgressButton) headerView
                .findViewById(R.id.rank_list_download_btn_center);
        downloadStateCenterButton.downloadbtn.setTextSize(13);
        downloadStateRightButton = (DetailDownloadProgressButton) headerView
                .findViewById(R.id.rank_list_download_btn_right);
        downloadStateRightButton.downloadbtn.setTextSize(13);

        downloadStateCenterButton.downloadbtn
                .setOnClickListener(listenerCenter);
        downloadStateLeftButton.downloadbtn.setOnClickListener(listenerLeft);
        downloadStateRightButton.downloadbtn.setOnClickListener(listenerRight);

        listView.addHeaderView(headerView);
        loadMoreView = new LoadMoreView(getActivity());
        loadMoreView.setNetErrorViewOnClickLinstener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreView.setLoadingVisible(true);
                loadMoreData();
            }
        });
        listView.addFooterView(loadMoreView);

        doLoadData(
                Constants.APP_API_URL,
                new String[]
                        {REQUEST_LIST_TAG},
                new ByteString[]
                        {getReqRecommAppList(((appInfos.size() / LOAD_DATA_SIZE) + 1),
                                LOAD_DATA_SIZE)}, etagMark);
    }

    @Override
    protected void loadDataSuccess(RspPacket rspPacket) {
        super.loadDataSuccess(rspPacket);
        List<String> actions = rspPacket.getActionList();
        for (String action : actions) {
            if (RSPONSE_LIST_TAG.equals(action)) {
                // 列表数据返回
                parseListResult(rspPacket);
                if (!isHaveNextPageData) {
                    listView.setOnScrollListener(null);
                    listView.removeFooterView(loadMoreView);
                    listView.showEndView();
                }
                if (top3.length == 3) {
                    imageLoader.displayImage(top3[0].getIconUrl(),
                            rankCenterImage, options);
                    imageLoader.displayImage(top3[1].getIconUrl(),
                            rankLeftImage, options);
                    imageLoader.displayImage(top3[2].getIconUrl(),
                            rankRightImage, options);

                    rankCenterName.setText(top3[0].getName());
                    rankLeftName.setText(top3[1].getName());
                    rankRightName.setText(top3[2].getName());

                    rankCenterDownloadCounts.setText(top3[0]
                            .getFormatDownloadCount()
                            + getActivity().getResources().getString(
                            R.string.count_download));
                    rankLeftDownloadCounts.setText(top3[1]
                            .getFormatDownloadCount()
                            + getActivity().getResources().getString(
                            R.string.count_download));
                    rankRightDownloadCounts.setText(top3[2]
                            .getFormatDownloadCount()
                            + getActivity().getResources().getString(
                            R.string.count_download));

                    downloadStateLeftButton.setInfo(top3[1].getPackageName());
                    downloadStateCenterButton.setInfo(top3[0].getPackageName());
                    downloadStateRightButton.setInfo(top3[2].getPackageName());
                    listenerLeft.setDownloadListenerInfo(top3[1]);
                    listenerCenter.setDownloadListenerInfo(top3[0]);
                    listenerRight.setDownloadListenerInfo(top3[2]);

                    if (top3[1].getTaskId() <= 0) {
                        // 没有任务
                        downloadStateLeftButton.setVisibility(View.VISIBLE);
                        rankListTaskTextLeft.setVisibility(View.GONE);
                    } else {
                        // 有任务
                        downloadStateLeftButton.setVisibility(View.GONE);
                        rankListTaskTextLeft.setVisibility(View.VISIBLE);
                    }

                    if (top3[0].getTaskId() <= 0) {
                        // 没有任务
                        downloadStateCenterButton.setVisibility(View.VISIBLE);
                        rankListTaskTextCenter.setVisibility(View.GONE);
                    } else {
                        // 有任务
                        downloadStateCenterButton.setVisibility(View.GONE);
                        rankListTaskTextCenter.setVisibility(View.VISIBLE);
                    }

                    if (top3[2].getTaskId() <= 0) {
                        // 没有任务
                        downloadStateRightButton.setVisibility(View.VISIBLE);
                        rankListTaskTextRight.setVisibility(View.GONE);
                    } else {
                        // 有任务
                        downloadStateRightButton.setVisibility(View.GONE);
                        rankListTaskTextRight.setVisibility(View.VISIBLE);
                    }
                }
                if (!appInfos.isEmpty()) {
                    if (listView.getAdapter() == null) {
                        listView.setAdapter(adapter);
                    } else {
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
        }
    }

    @Override
    protected void loadDataFailed(RspPacket rspPacket) {
        super.loadDataFailed(rspPacket);
        if (!appInfos.isEmpty()) {
            loadMoreView.setNetErrorVisible(true);
        } else {
            loadingView.setVisibilyView(false);
            centerViewLayout.setVisibility(View.GONE);
            errorViewLayout.setVisibility(View.VISIBLE);
            errorViewLayout.showLoadFailedLay();
        }
    }

    @Override
    protected void netError(String[] actions) {
        super.netError(actions);
        if (!appInfos.isEmpty()) {
            loadMoreView.setNetErrorVisible(true);
        } else {
            loadingView.setVisibilyView(false);
            centerViewLayout.setVisibility(View.GONE);
            errorViewLayout.setVisibility(View.VISIBLE);
            errorViewLayout.showLoadFailedLay();
        }
    }

    @Override
    protected void tryAgain() {
        super.tryAgain();
        doLoadData(
                Constants.APP_API_URL,
                new String[]
                        {REQUEST_LIST_TAG},
                new ByteString[]
                        {getReqRecommAppList(((appInfos.size() / LOAD_DATA_SIZE) + 1),
                                LOAD_DATA_SIZE)}, etagMark);
    }

    /**
     * 加载列表下一页数据
     */
    private void loadMoreData() {
        doLoadData(
                Constants.APP_API_URL,
                new String[]
                        {REQUEST_LIST_TAG,},
                new ByteString[]
                        {getReqRecommAppList(((appInfos.size() / LOAD_DATA_SIZE) + 1),
                                LOAD_DATA_SIZE)}, etagMark
                        + (appInfos.size() / LOAD_DATA_SIZE) + 1);
    }

    /**
     * 获取ReqRecommAppList的ByteString
     *
     * @param index
     * @param size
     * @return
     */
    private ByteString getReqRecommAppList(int index, int size) {
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
     * @throws Exception
     */
    private void parseListResult(RspPacket rspPacket) {
        try {
            RspRecommAppList rspRecommAppList = RspRecommAppList
                    .parseFrom(rspPacket.getParams(0));
            List<App.AppInfo> infos = rspRecommAppList.getAppInfosList();
            if (infos.size() == 0 || infos.isEmpty()
                    || infos.size() < LOAD_DATA_SIZE) {
                // 没有下一页数据了
                isHaveNextPageData = false;
            }

            if (appInfos.size() < LOAD_DATA_SIZE) {
                // 第一页
                List<ListAppInfo> emptyAppInfos = new ArrayList<ListAppInfo>();
                for (App.AppInfo info : infos) {
                    emptyAppInfos.add(ToolsUtil.getListAppInfo(info));
                    // appInfos.add(ToolsUtil.getListAppInfo(info));
                }
                top3 = new ListAppInfo[3];
                // 第一页
                for (int i = 0; i < emptyAppInfos.size(); i++) {
                    if (i < 3) {
                        top3[i] = emptyAppInfos.get(i);
                    } else {
                        appInfos.add(emptyAppInfos.get(i));
                    }
                }
            } else {
                for (App.AppInfo info : infos) {
                    appInfos.add(ToolsUtil.getListAppInfo(info));
                }
            }

        } catch (Exception e) {
            DLog.e(TAG, "parseListResult()#Excepton:", e);
        }
    }

    @Override
    public void onDestroy() {
        if (adapter != null) {
            adapter.unRegisterListener();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rank_list_lay_left:
                // 没有积分任务(跳转到应用详情界面)
                DetailsActivity.startDetailsActivityById(getActivity(),
                        top3[1].getSoftId(), action);

                break;

            case R.id.rank_list_lay_center:
                // 没有积分任务(跳转到应用详情界面)
                DetailsActivity.startDetailsActivityById(getActivity(),
                        top3[0].getSoftId(), action);
                break;
            case R.id.rank_list_lay_right:
                // 没有积分任务(跳转到应用详情界面)
                DetailsActivity.startDetailsActivityById(getActivity(),
                        top3[2].getSoftId(), action);
                break;
        }
    }

}
