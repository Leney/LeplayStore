package com.xd.leplay.store.gui.details;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.details.view.ScreenShortDetailBanner;
import com.xd.leplay.store.gui.main.BaseActivity;

/**
 * 全屏显示截屏的界面
 * 
 * @author lilijun
 *
 */
public class ScreenShortDetailActivity extends BaseActivity
{

	private ScreenShortDetailBanner bannerView;

	private int position = 0;

	private List<String> urls;

	@Override
	protected void initView()
	{
		titleView.setVisibility(View.GONE);
		loadingView.setVisibilyView(false);

		setCenterView(R.layout.screenshort_detail_activity);
		urls = new ArrayList<String>();
		position = getIntent().getIntExtra("position", 0);
		urls = getIntent().getStringArrayListExtra("urls");
		action = DataCollectionManager
				.getAction(
						DataCollectionManager
								.getIntentDataCollectionAction(getIntent()),
						DataCollectionConstant.DATA_COLLECTION_CP_DETAIL_FULL_CREEN_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		bannerView = (ScreenShortDetailBanner) findViewById(R.id.screenshort_bannerview);
		bannerView.initData(urls);
		bannerView.initView(this);
		bannerView.setCurrentItem(position);
	}

	public static void startScreenShortDetailActivity(Context context,
			int position, List<String> urls, String action)
	{
		Intent intent = new Intent(context, ScreenShortDetailActivity.class);
		intent.putExtra("position", position);
		intent.putStringArrayListExtra("urls", (ArrayList<String>) urls);
		DataCollectionManager.startActivity(context, intent, action);
	}

}
