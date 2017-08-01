package com.xd.leplay.store.gui.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.base.util.DLog;
import com.xd.leplay.store.Constants;
import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.ConstantManager;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.gui.main.BaseActivity;
import com.xd.leplay.store.gui.webview.WebViewActivity;
import com.xd.leplay.store.gui.webview.WeixinPublicAccountWebViewActivity;
import com.xd.leplay.store.model.ConstantInfo;
import com.xd.leplay.store.util.ToolsUtil;

/**
 * 关于我们界面
 * 
 * @author lilijun
 *
 */
public class AboutUsActivity extends BaseActivity implements OnClickListener
{
	private final String TAG = "AboutUsActivity";

	// /** 版本名称、发布日期 */
	// private TextView version, publishDate;
	/** 版本名称 */
	private TextView version;

	/** 客服电话、用户粉丝群、微信公众帐号、邮箱地址 */
//	private LinearLayout telephoneLay, fansLay, weixinLay, emailLay;
	private LinearLayout telephoneLay, weixinLay, emailLay;

	/** 客服电话，QQ粉丝群，微信公众帐号，公司邮箱 */
//	private TextView phone, qqFansGroup, weixinPublicAccount, email;
	private TextView phone, weixinPublicAccount, email;

	private ConstantManager constantManager = null;

	@Override
	protected void initView()
	{
		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_MANAGER_ABOUT_US_VALUE);
		DataCollectionManager.getInstance().addRecord(action);

		constantManager = ConstantManager.getInstance();

		titleView.setTitleName(getResources().getString(R.string.about_us));
		titleView.setRightLayVisible(true);
		titleView.setBottomLineVisible(true);
		titleView.setRightTextBtnName(getResources().getString(
				R.string.use_deal));
		titleView.setRightTextBtnOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// "使用协议"的点击事件
				WebViewActivity
						.startWebViewActivity(
								AboutUsActivity.this,
								getResources().getString(R.string.use_deal),
								Constants.UAC_API_URL + "/protocal",
								DataCollectionManager
										.getAction(
												action,
												DataCollectionConstant.DATA_COLLECTION_MANAGER_ABOUT_US_USE_DEAL_VALUE));
			}
		});
		loadingView.setVisibilyView(false);

		setCenterView(R.layout.about_us_activity);

		version = (TextView) findViewById(R.id.about_us_name_and_version);
		// publishDate = (TextView) findViewById(R.id.about_us_publish_date);
		telephoneLay = (LinearLayout) findViewById(R.id.about_us_telephone_lay);
		telephoneLay.setOnClickListener(this);
//		fansLay = (LinearLayout) findViewById(R.id.about_us_fans_qq_group_lay);
//		fansLay.setOnClickListener(this);
		weixinLay = (LinearLayout) findViewById(R.id.about_us_weixin_public_account_lay);
		weixinLay.setOnClickListener(this);
		emailLay = (LinearLayout) findViewById(R.id.about_us_aiwan_email_lay);
		emailLay.setOnClickListener(this);

		phone = (TextView) findViewById(R.id.about_us_tel_num);
//		qqFansGroup = (TextView) findViewById(R.id.about_us_fans_qq_num);
		weixinPublicAccount = (TextView) findViewById(R.id.about_us_weixin_public_account);
		email = (TextView) findViewById(R.id.about_us_aiwan_email);

		// if (constantManager.isHaveConstantInfo())
		// {
		ConstantInfo constantInfo = constantManager.getConstantInfo();
//		phone.setText(constantInfo.getServiceTelephone());
		// 临时数据  志辉电话
		phone.setText("13556850292");

//		qqFansGroup.setText(constantInfo.getQqGroupNo());
		weixinPublicAccount.setText(constantInfo.getWeixinPublicAccount());
//		email.setText(constantInfo.getCompanyEmail());
		// 临时数据  志辉QQ邮箱
		email.setText("30727042@qq.com");
		// } else
		// {
		// phone.setText(ConstantManager.SERVICE_TELEPHONE);
		// qqFansGroup.setText(ConstantManager.QQ_GROUP_NO);
		// weixinPublicAccount.setText(ConstantManager.WEIXIN_PUBLIC_ACCOUNT);
		// email.setText(ConstantManager.COMPANY_EMAIL);
		// }

		try
		{
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			version.setText(getResources().getString(R.string.app_name) + " "
					+ packageInfo.versionName);
		} catch (Exception e)
		{
			DLog.e(TAG, "获取VersionName发生异常#exception:", e);
		}
		// publishDate
		// .setText(getResources().getString(R.string.app_publish_date));
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.about_us_telephone_lay:
			// 客服电话
			String phoneStr = phone.getText().toString().trim();
			// 截取电话 只是拨打主机号码
//			String mainPhone = phoneStr.substring(0, phoneStr.indexOf(" "));
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ phoneStr));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
//		case R.id.about_us_fans_qq_group_lay:
//			// 用户粉丝群
//			if (ToolsUtil.copy(qqFansGroup.getText().toString().trim(),
//					AboutUsActivity.this))
//			{
//				Toast.makeText(
//						AboutUsActivity.this,
//						getResources().getString(
//								R.string.copy_qq_group_num_success),
//						Toast.LENGTH_SHORT).show();
//			} else
//			{
//				Toast.makeText(AboutUsActivity.this,
//						getResources().getString(R.string.copy_failed),
//						Toast.LENGTH_SHORT).show();
//			}
//
//			break;
		case R.id.about_us_weixin_public_account_lay:
			// 爱玩商店微信公众帐号
			WeixinPublicAccountWebViewActivity.startActivity(this,
					getResources().getString(R.string.attention_aiwan),
					Constants.APP_API_URL + "/concern", action);
			break;
		case R.id.about_us_aiwan_email_lay:
			// 爱玩商店邮箱帐号
			if (ToolsUtil.copy(email.getText().toString().trim(),
					AboutUsActivity.this))
			{
				Toast.makeText(
						AboutUsActivity.this,
						getResources().getString(
								R.string.copy_email_account_success),
						Toast.LENGTH_SHORT).show();
			} else
			{
				Toast.makeText(AboutUsActivity.this,
						getResources().getString(R.string.copy_failed),
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	public static void startAboutUsActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				AboutUsActivity.class), action);
	}

}
