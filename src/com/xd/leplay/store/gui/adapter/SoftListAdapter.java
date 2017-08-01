package com.xd.leplay.store.gui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.xd.base.util.DLog;
import com.xd.download.DownloadInfo;
import com.xd.leplay.store.R;
import com.xd.leplay.store.gui.details.DetailsActivity;
import com.xd.leplay.store.gui.download.adapter.DownloadBaseAdapter;
import com.xd.leplay.store.model.AdInfo;
import com.xd.leplay.store.model.AdUtils;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.view.AdLinearLayout;
import com.xd.leplay.store.view.AdRelativeLayout;
import com.xd.leplay.store.view.MarketListView;
import com.xd.leplay.store.view.download.DownloadOnclickListener;
import com.xd.leplay.store.view.download.DownloadProgressButton;

import java.util.ArrayList;

/**
 * 普通列表通用Adapter
 *
 * @author lilijun
 */
public class SoftListAdapter extends DownloadBaseAdapter<MarketListView> {
    // private DataCollectInfo datainfo = null;

    // public SoftListAdapter(Context context, MarketListView listView,
    // ArrayList<AppInfo> appInfos, DataCollectInfo datainfo)
    // {
    // super(context, listView, appInfos);
    // this.datainfo = datainfo;
    // setUpdateProgress(true);
    // }

    private Context mContext;

    private String action = "";

    private OnRequestAdListener listener;

    public SoftListAdapter(Context context, MarketListView listView,
                           ArrayList<ListAppInfo> appInfos, String action) {
        super(context, listView, appInfos);
        this.mContext = context;
        this.action = action;
        // this.datainfo = datainfo;
        setUpdateProgress(false);
    }

    public void setOnRequestAdListener(OnRequestAdListener listener) {
        this.listener = listener;
    }

    public class SoftListViewHolder {
        /**
         * 下载图标
         */
        public ImageView icon;

        /**
         * 应用名称
         */
        public TextView appName;

        // /** 下载次数和软件大小 */
        // public TextView downloadCountAndSize;

        /**
         * 星级
         */
        public RatingBar star;

        /**
         * 标签1、标签2
         */
        public TextView label1, label2;

//        /**
//         * 赠送金币数量
//         */
//        public TextView coin;

        /**
         * 小编推荐
         */
        public TextView decribe;

        /**
         * 下载状态按钮
         */
        public DownloadProgressButton downloadStateButton;

        /**
         * 下载监听器
         */
        public DownloadOnclickListener listener;

        public void init(View baseView) {
            icon = (ImageView) baseView.findViewById(R.id.soft_list_app_icon);
            appName = (TextView) baseView.findViewById(R.id.soft_list_app_name);
            // downloadCountAndSize = (TextView) baseView
            // .findViewById(R.id.soft_list_app_download_count_and_size);
            star = (RatingBar) baseView.findViewById(R.id.soft_list_app_star);
            label1 = (TextView) baseView.findViewById(R.id.soft_list_lab1);
            label2 = (TextView) baseView.findViewById(R.id.soft_list_lab2);
//            coin = (TextView) baseView.findViewById(R.id.soft_list_coin_text);
            decribe = (TextView) baseView
                    .findViewById(R.id.soft_list_describe_text);
            downloadStateButton = (DownloadProgressButton) baseView
                    .findViewById(R.id.soft_list_download_btn);
            listener = new DownloadOnclickListener(context, action);
        }

        public SoftListViewHolder(View baseView) {
            init(baseView);
        }

    }

    public class AdViewHolder {

        public ImageView icon;
        /**
         * 主标题
         */
        public TextView mainTitle;

        /**
         * 描述(副标题)
         */
        public TextView describle;
        /**
         * 广告标记
         */
        public TextView adMark;

        public AdRelativeLayout itemLay;

        public void init(View baseView) {
            icon = (ImageView) baseView.findViewById(R.id.ad_list_icon);
            mainTitle = (TextView) baseView.findViewById(R.id.ad_list_main_tilte);
            describle = (TextView) baseView.findViewById(R.id.ad_list_sec_title);
            adMark = (TextView) baseView.findViewById(R.id.ad_list_ad_mark);
            itemLay = (AdRelativeLayout) baseView.findViewById(R.id.soft_list_item_lay);
            itemLay.setOnClickListener(adItemClickListenr);
        }

        public AdViewHolder(View baseView) {
            init(baseView);
        }

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // 0 = 正常app信息列表视图  1=广告视图
        return appInfos.get(position).isAd() ? 1 : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SoftListViewHolder holder = null;
        AdViewHolder adViewHolder = null;
        ListAppInfo info = appInfos.get(position);
        int viewType = getItemViewType(position);
        if (convertView == null) {
            if (viewType == 0) {
                // 正常视图
                convertView = View.inflate(context,
                        R.layout.soft_list_adapter, null);
                holder = new SoftListViewHolder(convertView);
                holder.downloadStateButton.downloadbtn
                        .setOnClickListener(holder.listener);
                convertView.setOnClickListener(itemClickListener);
                convertView.setTag(holder);
            } else if (viewType == 1) {
                // 广告视图
                convertView = View.inflate(context, R.layout.ad_item, null);
                adViewHolder = new AdViewHolder(convertView);
                convertView.setTag(adViewHolder);
//                convertView.setOnClickListener(adItemClickListenr);
            }
        } else {
            if (viewType == 0) {
                holder = (SoftListViewHolder) convertView.getTag();
            } else if (viewType == 1) {
                adViewHolder = (AdViewHolder) convertView.getTag();
            }
        }

        if (viewType == 0) {
            // 正常视图
            convertView.setTag(R.id.soft_list_app_icon, info);

            holder.appName.setText(info.getName());
            holder.star.setRating(info.getStarLevel());
            holder.decribe.setText(info.getRecommendDescribe());

            holder.label1.setVisibility(View.GONE);
            holder.label2.setVisibility(View.GONE);
            if (info.isHaveGift()) {
                holder.label1.setText(mContext.getResources().getString(
                        R.string.gift));
                // holder.label1.setBackground(mContext.getResources().getDrawable(
                // R.drawable.lab_gift_bg_shape));
                holder.label1.setBackgroundResource(R.drawable.lab_gift_bg_shape);
                holder.label1.setVisibility(View.VISIBLE);
            }
            if (info.isFirst()) {
                if (holder.label1.getVisibility() == View.VISIBLE) {
                    holder.label2.setText(mContext.getResources().getString(
                            R.string.first));
                    // holder.label2.setBackground(mContext.getResources()
                    // .getDrawable(R.drawable.lab_first_bg_shape));
                    holder.label2
                            .setBackgroundResource(R.drawable.lab_first_bg_shape);
                    holder.label2.setVisibility(View.VISIBLE);
                } else {
                    holder.label1.setText(mContext.getResources().getString(
                            R.string.first));
                    // holder.label1.setBackground(mContext.getResources()
                    // .getDrawable(R.drawable.lab_first_bg_shape));
                    holder.label1
                            .setBackgroundResource(R.drawable.lab_first_bg_shape);
                    holder.label1.setVisibility(View.VISIBLE);
                }
            }

            if (holder.label1.getVisibility() == View.VISIBLE) {
                // 第一个标签已经被设置了值
                if (holder.label2.getVisibility() == View.GONE) {
                    // 第二个标签还未设置
                    if (info.isHot()) {
                        holder.label2.setText(mContext.getResources().getString(
                                R.string.hot));
                        // holder.label2.setBackground(mContext.getResources()
                        // .getDrawable(R.drawable.lab_hot_bg_shape));
                        holder.label2
                                .setBackgroundResource(R.drawable.lab_hot_bg_shape);
                        holder.label2.setVisibility(View.VISIBLE);
                    }
                    if (info.isBoutique()) {
                        if (holder.label2.getVisibility() == View.GONE) {
                            holder.label2.setText(mContext
                                    .getString(R.string.boutique));
                            // holder.label2.setBackground(mContext.getResources()
                            // .getDrawable(R.drawable.lab_boutique_bg_shape));
                            holder.label2
                                    .setBackgroundResource(R.drawable.lab_boutique_bg_shape);
                            holder.label2.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else {
                // 第一个标签还没有设置值 则证明 没有礼包和不是首发
                if (info.isHot()) {
                    holder.label1.setText(mContext.getResources().getString(
                            R.string.hot));
                    // holder.label1.setBackground(mContext.getResources()
                    // .getDrawable(R.drawable.lab_hot_bg_shape));
                    holder.label1
                            .setBackgroundResource(R.drawable.lab_hot_bg_shape);
                    holder.label1.setVisibility(View.VISIBLE);
                }
                if (info.isBoutique()) {
                    if (holder.label1.getVisibility() == View.VISIBLE) {
                        // 如果第一个标签被设置了 则设置第二个标签
                        holder.label2.setText(mContext.getResources().getString(
                                R.string.boutique));
                        // holder.label2.setBackground(mContext.getResources()
                        // .getDrawable(R.drawable.lab_boutique_bg_shape));
                        holder.label2
                                .setBackgroundResource(R.drawable.lab_boutique_bg_shape);
                        holder.label2.setVisibility(View.VISIBLE);
                    } else {
                        // 如果第一个标签没有被设置
                        holder.label1.setText(mContext.getResources().getString(
                                R.string.boutique));
                        // holder.label1.setBackground(mContext.getResources()
                        // .getDrawable(R.drawable.lab_boutique_bg_shape));
                        holder.label1
                                .setBackgroundResource(R.drawable.lab_boutique_bg_shape);
                        holder.label1.setVisibility(View.VISIBLE);
                        holder.label2.setVisibility(View.GONE);
                    }
                }
            }

            holder.downloadStateButton.setInfo(info.getPackageName());
            holder.listener.setDownloadListenerInfo(info);

            imageLoader.displayImage(info.getIconUrl(), holder.icon, options);
        } else if (viewType == 1) {
            // 广告视图
            if(!info.isFullAd()){
                // 广告视图还没有被广告数据填充
                if(listener != null){
                    DLog.i("llj","广告宽度-----getMeasuredWidth->>>"+adViewHolder.itemLay.getMeasuredWidth());
                    DLog.i("llj","广告高度-----getMeasuredHeight->>>"+adViewHolder.itemLay.getMeasuredHeight());

                    DLog.i("llj","广告宽度----getWidth-->>>"+adViewHolder.itemLay.getWidth());
                    DLog.i("llj","广告高度----getHeight-->>>"+adViewHolder.itemLay.getHeight());
                    listener.onRequestAd(position);
                }
            }else {
                // 被填充了
                adViewHolder.itemLay.setTag(info.getAdInfo());
                if (!TextUtils.isEmpty(info.getAdInfo().getImageUrl())) {
                    imageLoader.displayImage(info.getAdInfo().getImageUrl(), adViewHolder.icon,
                            DisplayUtil.getScreenShortImageLoaderOptions());
                } else if (!TextUtils.isEmpty(info.getAdInfo().getRightIconUrl())) {
                    imageLoader.displayImage(info.getAdInfo().getRightIconUrl(), adViewHolder.icon,
                            DisplayUtil.getScreenShortImageLoaderOptions());
                } else if (!TextUtils.isEmpty(info.getAdInfo().getIcon())) {
                    imageLoader.displayImage(info.getAdInfo().getIcon(), adViewHolder.icon,
                            DisplayUtil.getScreenShortImageLoaderOptions());
                }
//            imageLoader.displayImage(info.getIconUrl(), adViewHolder.icon, options);
                adViewHolder.mainTitle.setText(info.getAdInfo().getTitle());
                adViewHolder.describle.setText(info.getAdInfo().getSubTile());
                adViewHolder.adMark.setText(info.getAdInfo().getAdSourceMark() + "|" + parent.getResources().getString(R.string.ad));
            }
        }
        return convertView;
    }

    /**
     * 整个item的点击事件
     */
    private OnClickListener itemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ListAppInfo info = (ListAppInfo) v.getTag(R.id.soft_list_app_icon);
            // 没有积分任务(跳转到应用详情界面)
            DetailsActivity.startDetailsActivityById(mContext,
                    info.getSoftId(), action);
        }
    };

    /**
     * 广告条目点击事件
     */
    private OnClickListener adItemClickListenr = new OnClickListener() {
        @Override
        public void onClick(View view) {
            AdInfo adInfo = (AdInfo) view.getTag();
            if (adInfo == null) {
                return;
            }
            AdRelativeLayout adRelativeLayout = (AdRelativeLayout) view;
            DLog.i("llj","downX---->>"+adRelativeLayout.downX);
            DLog.i("llj","downY---->>"+adRelativeLayout.downY);
            DLog.i("llj","upX---->>"+adRelativeLayout.upX);
            DLog.i("llj","upY---->>"+adRelativeLayout.upY);
            AdUtils.doClick((Activity) context, adInfo, adRelativeLayout.downX, adRelativeLayout.downY,
                    adRelativeLayout.upX, adRelativeLayout.upY, null);
        }
    };

    @Override
    protected void refreshData(DownloadInfo info, View view) {
        SoftListViewHolder viewHolder = (SoftListViewHolder) view.getTag();
        if (viewHolder != null) {
            viewHolder.downloadStateButton.setInfo(info.getPackageName());
            viewHolder.listener.setDownloadListenerInfo(info);
        }
        // else
        // {
        // Toast.makeText(context, "SoftListViewHolder = null",
        // Toast.LENGTH_SHORT).show();
        // }

    }

    /**
     * 请求广告的监听器
     */
    public interface OnRequestAdListener{
        void onRequestAd(int position);
    }
}
