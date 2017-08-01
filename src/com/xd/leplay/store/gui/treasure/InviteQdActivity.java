package com.xd.leplay.store.gui.treasure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xd.leplay.store.Constants;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.control.LoginUserInfoManager;
import com.xd.leplay.store.model.LoginedUserInfo;
import com.xd.leplay.store.util.ToolsUtil;
import com.xd.leplay.store.view.ChildTitleView;

/**
 * 邀请二维码界面
 * 
 * @author lilijun
 *
 */
public class InviteQdActivity extends Activity
{
	private ImageView qdImg;

	private TextView inviteCode;

	private LoginedUserInfo userInfo;

	private ChildTitleView titleView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		userInfo = LoginUserInfoManager.getInstance().getLoginedUserInfo();
		if (userInfo == null)
		{
			finish();
			return;
		}
		setContentView(R.layout.invite_qd_activity);
		titleView = (ChildTitleView) findViewById(R.id.invite_qd_title);
		titleView.setTitleName(getResources().getString(R.string.qd_invite));
		qdImg = (ImageView) findViewById(R.id.invite_qd_img);
		inviteCode = (TextView) findViewById(R.id.invite_qd_invite_code);

		if ("".equals(userInfo.getPhone()))
		{
			// 没有绑定手机号码
			inviteCode.setText(getResources().getString(R.string.invite_code)
					+ userInfo.getTreasureInfo().getInviteCode() + "");
		} else
		{
			// 有绑定手机号码
			String result = getResources().getString(R.string.invite_code)
					+ userInfo.getTreasureInfo().getInviteCode() + " "
					+ getResources().getString(R.string.or) + " "
					+ userInfo.getPhone();
			inviteCode.setText(result);
		}

		ViewTreeObserver vto = qdImg.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				qdImg.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				String shareUrl = Constants.UAC_API_URL + "invite/new/"
						+ userInfo.getUserId() + "?from=qrcode";
				// 得到二维码bitmap
				Bitmap qdBitmap = ToolsUtil.createQRImage(shareUrl,
						qdImg.getWidth(), qdImg.getHeight());
				if (qdBitmap != null)
				{
					Bitmap centerImg = BitmapFactory.decodeResource(
							getResources(), R.drawable.app_icon_mid);
					// 在二维码上绘图
					ToolsUtil.createQRCodeBitmapWithPortrait(qdImg.getWidth(),
							qdImg.getHeight(), qdBitmap, centerImg);
					qdImg.setImageBitmap(qdBitmap);
				}
			}
		});
	}

	public static void startActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				InviteQdActivity.class), action);
	}
}
