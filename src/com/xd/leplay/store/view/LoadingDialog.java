package com.xd.leplay.store.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

import com.xd.leplay.store.R;

/**
 * 加载的弹出框
 * 
 * @author lilijun
 *
 */
public class LoadingDialog extends Dialog
{
	/** 圆形转圈的加载进度条 */
	private CircleProgressView progressView;

	/** 显示的文本 */
	private TextView msgText;

	private String msg;

	private Context mContext;

	public LoadingDialog(Context context, String msg)
	{
		super(context, R.style.DialogTheme);
		this.mContext = context;
		this.msg = msg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_dialog);

		progressView = (CircleProgressView) findViewById(R.id.loading_dialog_circlProgress);
		msgText = (TextView) findViewById(R.id.loading_dialog_text);

		msgText.setText(msg);

		// getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		// Window window = getWindow();
		// WindowManager.LayoutParams windowparams = window.getAttributes();
		// Rect rect = new Rect();
		// View view1 = window.getDecorView();
		// view1.getWindowVisibleDisplayFrame(rect);
		// WindowManager windowManager = ((Activity)
		// mContext).getWindowManager();
		// Display display = windowManager.getDefaultDisplay();
		// windowparams.width = (int) (display.getWidth() * 0.95);
		// window.setBackgroundDrawableResource(android.R.color.transparent);
		// window.setAttributes((android.view.WindowManager.LayoutParams)
		// windowparams);

		Resources resources = getContext().getResources();
		// 获得屏幕参数：主要是分辨率，像素等。
		DisplayMetrics dm = resources.getDisplayMetrics();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = (int) (dm.widthPixels * 0.95);
		getWindow().setAttributes(params);

		setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				progressView.setVisibilyView(false);
			}
		});

		setOnShowListener(new OnShowListener()
		{
			@Override
			public void onShow(DialogInterface dialog)
			{
				progressView.setVisibilyView(true);
			}
		});
	}

	/**
	 * 设置加载的提示文本
	 * 
	 * @param msg
	 */
	public void setMessage(String msg)
	{
		this.msgText.setText(msg);
	}

}
