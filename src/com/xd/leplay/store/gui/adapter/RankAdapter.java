package com.xd.leplay.store.gui.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.download.DownloadInfo;
import com.xd.leplay.store.R;
import com.xd.leplay.store.gui.details.DetailsActivity;
import com.xd.leplay.store.gui.download.adapter.DownloadBaseAdapter;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.view.MarketListView;
import com.xd.leplay.store.view.download.DownloadOnclickListener;
import com.xd.leplay.store.view.download.DownloadProgressButton;

import java.util.ArrayList;

/**
 * 排行列表Adapter
 *
 * @author lilijun
 */
public class RankAdapter extends DownloadBaseAdapter<MarketListView> {
    private Context mContext;

    private String action;

    public RankAdapter(Context context, MarketListView listView,
                       ArrayList<ListAppInfo> appInfos, String action) {
        super(context, listView, appInfos);
        this.mContext = context;
        this.action = action;
        // this.datainfo = datainfo;
        setUpdateProgress(false);
    }

    public class SoftListViewHolder {
        /**
         * 做任务标识
         */
        public TextView taskText;

        /**
         * 应用图标
         */
        public ImageView icon;

        /**
         * 应用名称
         */
        public TextView appName;

        /**
         * 排行数字
         */
        public TextView rankNum;

        /**
         * 下载次数和软件大小
         */
        public TextView downloadCountAndSize;

        /**
         * 下载状态按钮
         */
        public DownloadProgressButton downloadStateButton;

        /**
         * 下载监听器
         */
        public DownloadOnclickListener listener;

        public void init(View baseView) {
            taskText = (TextView) baseView
                    .findViewById(R.id.rank_list_do_task_text);
            icon = (ImageView) baseView.findViewById(R.id.rank_list_app_icon);
            appName = (TextView) baseView.findViewById(R.id.rank_list_app_name);
            rankNum = (TextView) baseView.findViewById(R.id.rank_list_rank_num);
            downloadCountAndSize = (TextView) baseView
                    .findViewById(R.id.rank_list_download_count_and_size);
            downloadStateButton = (DownloadProgressButton) baseView
                    .findViewById(R.id.rank_list_download_btn);
            listener = new DownloadOnclickListener(context, action);
        }

        public SoftListViewHolder(View baseView) {
            init(baseView);
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SoftListViewHolder holder = null;
        ListAppInfo info = appInfos.get(position);
        if (convertView == null) {
            convertView = (LinearLayout) View.inflate(context,
                    R.layout.rank_list_adapter, null);
            holder = new SoftListViewHolder(convertView);
            convertView.setTag(holder);
            holder.downloadStateButton.downloadbtn
                    .setOnClickListener(holder.listener);
            convertView.setOnClickListener(itemClickListener);
        } else {
            holder = (SoftListViewHolder) convertView.getTag();
        }

        convertView.setTag(R.id.soft_list_app_icon, info);

        // datainfo.setPosition(position + "");
        holder.appName.setText(info.getName());
        holder.downloadCountAndSize.setText(info.getFormatDownloadCount()
                + mContext.getResources().getString(R.string.count_download)
                + " | " + info.getFormatSize());
        if (position + 1 <= 99) {
            holder.rankNum.setText((position + 4) + "");
            holder.rankNum.setVisibility(View.VISIBLE);
        } else {
            holder.rankNum.setVisibility(View.INVISIBLE);
        }
        // if (position == 0)
        // {
        // holder.rankNum.setTextColor(mContext.getResources().getColor(
        // R.color.rank1_color));
        // } else if (position == 1)
        // {
        // holder.rankNum.setTextColor(mContext.getResources().getColor(
        // R.color.rank2_color));
        // } else if (position == 2)
        // {
        // holder.rankNum.setTextColor(mContext.getResources().getColor(
        // R.color.rank3_color));
        // } else
        // {
        // holder.rankNum.setTextColor(mContext.getResources().getColor(
        // R.color.rank4_color));
        // }
        holder.rankNum.setTextColor(mContext.getResources().getColor(
                R.color.rank4_color));
        holder.downloadStateButton.setInfo(info.getPackageName());
        holder.listener.setDownloadListenerInfo(info);
        imageLoader.displayImage(info.getIconUrl(), holder.icon, options);
        if (info.getTaskId() <= 0) {
            // 没有任务
            holder.downloadStateButton.setVisibility(View.VISIBLE);
            holder.taskText.setVisibility(View.GONE);
        } else {
            // 有任务
            holder.downloadStateButton.setVisibility(View.GONE);
            holder.taskText.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

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
}
