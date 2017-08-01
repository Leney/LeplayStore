package com.xd.leplay.store.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xd.base.util.DLog;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.ImageLoaderManager;
import com.xd.leplay.store.gui.application.ApplicationClassifyDetailActivity;
import com.xd.leplay.store.gui.details.DetailsActivity;
import com.xd.leplay.store.gui.webview.WebViewActivity;
import com.xd.leplay.store.model.ListAppInfo;
import com.xd.leplay.store.model.proto.App.AdElement;
import com.xd.leplay.store.util.DisplayUtil;
import com.xd.leplay.store.util.ToolsUtil;

import java.lang.reflect.Field;
import java.util.List;

public class BannerView extends RelativeLayout {
    private ViewPager vp;
    /**
     * 底部指示器显示区域
     */
    private LinearLayout indicatorLay;
    private LayoutParams lp;
    private List<AdElement> showResults;
    /**
     * 标记是否自动刷新
     */
    private boolean isAutoRefresh = true;
    /**
     * 标记是否关闭自动刷新(是否自动刷新的总开关)
     */
    private boolean isCancleAutoRefresh = true;
    private int currentItem = 0; // 当前图片的索引号
    /**
     * 当前真正的viewPager position
     */
    private int curRealItem = 0;
    /**
     * 底部圆形指示点
     */
    private View[] circlePointViews = null;

    private ImageLoaderManager imageLoaderManager = null;

    private String action = "";

    private Context mContext;

    public void initData(List<AdElement> pResult) {
        showResults = pResult;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @SuppressLint("NewApi")
    public BannerView(Context context, AttributeSet attrs, int defStyleAttr,
                      int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BannerView(Context context) {
        super(context);
    }

    public void initView(Context context) {
        this.mContext = context;
        imageLoaderManager = ImageLoaderManager.getInstance();

        // WindowManager wm = (WindowManager) getContext().getSystemService(
        // Context.WINDOW_SERVICE);
        // int height = wm.getDefaultDisplay().getHeight();
        // 整个显示banner的Viewpager
        // lp = new LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.px2dip(
        // context, 300));
        // lp = new LayoutParams(LayoutParams.MATCH_PARENT,
        // (int) (0.15625 * height));
        vp = new ViewPager(context);
        // vp.setLayoutParams(lp);

        // 指示器
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(
                context, 30));
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.bottomMargin = (int) context.getResources().getDimension(
                R.dimen.banner_proint_bottom_margin);
        if (showResults.size() > 1) {
            indicatorLay = new LinearLayout(context);
            indicatorLay.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            indicatorLay.setLayoutParams(lp);
            // 真正的整个指示器部分
            LinearLayout realIndicatorLay = new LinearLayout(context);
            LinearLayout.LayoutParams indicatorLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            realIndicatorLay.setLayoutParams(indicatorLayoutParams);
            circlePointViews = new View[showResults.size()];

            // 指示器大小
            LinearLayout.LayoutParams indicatorItemParams = new LinearLayout.LayoutParams(
                    (int) context.getResources().getDimension(
                            R.dimen.banner_proint_width), (int) context
                    .getResources().getDimension(
                            R.dimen.banner_proint_height));
            indicatorItemParams.leftMargin = DisplayUtil.dip2px(context, 2);
            indicatorItemParams.rightMargin = DisplayUtil.dip2px(context, 2);
            for (int i = 0; i < circlePointViews.length; i++) {
                circlePointViews[i] = new View(context);
                // view.setLayoutParams(circleLayoutParams);
                if (i == 0) {
                    // 初始化时 将第一个设置为选择状态
                    circlePointViews[i]
                            .setBackgroundResource(R.drawable.banner_proint_select_bg_shape);
                } else {
                    circlePointViews[i]
                            .setBackgroundResource(R.drawable.banner_proint_normal_bg_shape);
                }
                circlePointViews[i].setLayoutParams(indicatorItemParams);
                realIndicatorLay.addView(circlePointViews[i]);
            }
            indicatorLay.addView(realIndicatorLay);
        }
        adapter = new ViewPagerAdapter(context, showResults);
        vp.setAdapter(adapter);
        vp.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        stopAutoRefresh();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        stopAutoRefresh();
                        break;
                    case MotionEvent.ACTION_UP:
                        startAutoRefresh();
                        break;
                    // case MotionEvent.ACTION_CANCEL:
                    // Log.i("lilijun", "MotionEvent.ACTION_CANCEL....");
                    // break;
                }
                return false;
            }
        });
        vp.setOnPageChangeListener(new MyPageChangeListener());
        addView(vp);
        // addView(centerLayout);
        if (showResults.size() > 1) {
            addView(indicatorLay);
        }

    }

    /**
     * 设置viewpager滑动速率 (滑动速率值 必须要小于自动刷新一次的时间值)
     *
     * @param time
     */
    public void setScrollTime(int time) {
        if (showResults.size() > 1) {
            try {
                Field field = ViewPager.class.getDeclaredField("mScroller");
                field.setAccessible(true);
                // 设置速率对象 DecelerateInterpolator：动画从开始到结束，变化率是一个减速的过程
                BannerScroller scroller = new BannerScroller(vp.getContext(),
                        new DecelerateInterpolator());
                field.set(vp, scroller);
                scroller.setmDuration(time);
            } catch (Exception e) {
                Log.e("lilijun", "", e);
            }
        }
    }

    private Handler viewPagerHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // vp.setCurrentItem(currentItem % showResults.size());// 切换当前显示的图片
            // if((currentItem+1)>showResults.size()){
            // //说明当前显示的图pains
            // }
            if (isCancleAutoRefresh) {
                return;
            }
            if (!isAutoRefresh) {
                return;
            }
            vp.setCurrentItem(curRealItem + 1);// 切换当前显示的图片
            viewPagerHandler.sendEmptyMessageDelayed(0, 3000);
        }

        ;
    };
    public ViewPagerAdapter adapter;

    /**
     * 开始自动刷新
     */
    public void startAutoRefresh() {
        if (showResults.size() > 1) {
            isAutoRefresh = true;
            viewPagerHandler.sendEmptyMessageDelayed(0, 3000);
        }
    }

    /**
     * 停止自动刷新
     */
    public void stopAutoRefresh() {
        if (showResults.size() > 1) {
            isAutoRefresh = false;
            viewPagerHandler.removeMessages(0);
        }
    }

    /**
     * 当ViewPager中页面的状态发生改变时调用
     */
    private class MyPageChangeListener implements OnPageChangeListener {
        // private int oldPosition = 0;

        public void onPageSelected(int position) {
            // 获取新的位置
            curRealItem = position;
            int newPosition = position % showResults.size();
            currentItem = newPosition;

            if (currentItem == 0) {
                // 如果是第一个
                if (circlePointViews.length - 1 > 0) {
                    circlePointViews[circlePointViews.length - 1]
                            .setBackgroundResource(R.drawable.banner_proint_normal_bg_shape);
                }
                if (currentItem + 1 < circlePointViews.length) {
                    circlePointViews[currentItem + 1]
                            .setBackgroundResource(R.drawable.banner_proint_normal_bg_shape);
                }

            } else if (currentItem == circlePointViews.length - 1) {
                // 如果是最后一个
                if (circlePointViews.length != 1) {
                    circlePointViews[0]
                            .setBackgroundResource(R.drawable.banner_proint_normal_bg_shape);
                }
                if (currentItem - 1 > 0) {
                    circlePointViews[currentItem - 1]
                            .setBackgroundResource(R.drawable.banner_proint_normal_bg_shape);
                }
            } else {
                circlePointViews[currentItem - 1]
                        .setBackgroundResource(R.drawable.banner_proint_normal_bg_shape);
                circlePointViews[currentItem + 1]
                        .setBackgroundResource(R.drawable.banner_proint_normal_bg_shape);
            }
            circlePointViews[currentItem]
                    .setBackgroundResource(R.drawable.banner_proint_select_bg_shape);
        }

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    public boolean isCancleAutoRefresh() {
        return isCancleAutoRefresh;
    }

    public void setCancleAutoRefresh(boolean isCancleAutoRefresh) {
        this.isCancleAutoRefresh = isCancleAutoRefresh;
    }

    private class ViewPagerAdapter extends PagerAdapter {

        List<AdElement> lists;
        Context ct;
        private ImageView imgView;
        private int currentPosition = 0;
        private DisplayImageOptions options;

        private RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        public ViewPagerAdapter(Context ct, List<AdElement> mList) {
            lists = mList;
            this.ct = ct;
            options = DisplayUtil.getBannerImageLoaderOptions();
        }

        /**
         * 获得页面的总数
         */
        public int getCount() {
            if (lists.size() > 1) {
                return Integer.MAX_VALUE;
            } else {
                return lists.size();
            }
        }

        /**
         * 获得相应位置上的view container view的容器
         */
        public Object instantiateItem(ViewGroup container, final int position) {
            imgView = new ImageView(ct);
            imgView.setScaleType(ImageView.ScaleType.FIT_XY);
            imgView.setOnClickListener(itemOnClickListener);
            // imgView.setLayoutParams(params);
            if (lists.size() == 0) {

            } else {
                // 写自己的逻辑
                currentPosition = position % lists.size();
//                imageLoaderManager.displayImage(lists.get(currentPosition)
//                        .getAdsPicUrlLand(), imgView, options);
                // 临时写死的  显示本地图片
                if (TextUtils.equals("-1", lists.get(currentPosition).getIconUrl())) {
                    // 表示是写死的本地banner图片，则显示本地的图片
                    imageLoaderManager.displayImageRes(lists.get(currentPosition)
                                    .getAdsPicUrlLand(),
                            imgView);
                } else {
                    // 表示是显示网络图片
                    imageLoaderManager.displayImage(lists.get(currentPosition)
                            .getAdsPicUrlLand(), imgView, options);
                }
                imgView.setTag(lists.get(currentPosition));
            }

            // 给 container 添加一个view
            container.addView(imgView);
            container.setLayoutParams(params);
            // 返回一个和该view相对的object
            return imgView;
        }

        /**
         * 判断 view和object的对应关系
         */
        public boolean isViewFromObject(View view, Object object) {
            if (view == object) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * 销毁对应位置上的object
         */
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View) object);
            object = null;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

    }

    /**
     * 图片的点击事件
     */
    private OnClickListener itemOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            AdElement adElement = (AdElement) v.getTag();
            // 元素类型：1=跳转至应用或游戏，2=跳转指定链接，3=跳转至分类
            DLog.i("lilijun",
                    "adElement.getElemType()--------->>>"
                            + adElement.getElemType());
            DataCollectionManager.getInstance().addRecord(action,
                    DataCollectionManager.CLASSFIY_ID,
                    adElement.getElemId() + "");
            switch (adElement.getElemType()) {
                case 1:
                    // App详情
//                    ListAppInfo appInfo = ToolsUtil.getListAppInfo(adElement
//                            .getAppInfo());
                    // 跳转到应用详情
//                    DetailsActivity.startDetailsActivityById(getContext(),
//                            appInfo.getSoftId(), action);


                    // 临时写死的
                    if (TextUtils.equals("-1", adElement.getIconUrl())) {
                        DetailsActivity.startDetailsActivityById(getContext(), adElement
                                        .getElemId(),
                                action);
                    } else {
                        ListAppInfo appInfo = ToolsUtil.getListAppInfo(adElement.getAppInfo());
                        // 跳转到应用详情
                        DetailsActivity.startDetailsActivityById(getContext(),
                                appInfo.getSoftId(), action);
                    }
                    break;
                case 2:
                    // webview链接
                    String url = adElement.getJumpLinkUrl();
                    DLog.i("lilijun", "链接地址------->>>" + url);
                    WebViewActivity.startWebViewActivity(getContext(),
                            adElement.getShowName(), url, action);
                    break;
                case 3:
                    // 分类列表
                    int appTypeId = adElement.getJumpAppTypeId();
                    String appTypeName = adElement.getJumpAppTypeName();
                    ApplicationClassifyDetailActivity
                            .startApplicationClassifyDetailActivity(getContext(),
                                    appTypeId, appTypeName, action, appTypeId + "");
                    break;
            }
        }
    };
}
