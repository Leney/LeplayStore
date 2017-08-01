package com.xd.leplay.store.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xd.leplay.store.R;

/**
 * 可自定义视图的中间弹出的Dialog
 * 
 * @author lilijun
 *
 */
public class CenterDialog extends Dialog
{

	private Context mContext = null;

	private TextView title;

	private View topLine;

	private LinearLayout centerLay;

	private LinearLayout allLay;

	public CenterDialog(Context context)
	{
		// super(context);
		super(context, R.style.DialogTheme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog);
		setCanceledOnTouchOutside(true);

		allLay = (LinearLayout) findViewById(R.id.custom_dialog_all_view);
		title = (TextView) findViewById(R.id.custom_dialog_title);
		topLine = findViewById(R.id.custom_dialog_top_line);
		centerLay = (LinearLayout) findViewById(R.id.custom_dialog_center_lay);
		setCancelable(true);

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
	}

	/**
	 * 设置Dialog的标题
	 * 
	 * @param title
	 */
	public void setTitleName(String title)
	{
		this.title.setText(title);
	}

	/**
	 * 自定义Dialog中间的视图
	 * 
	 * @param view
	 */
	public void setCenterView(View view)
	{
		// view.setBackgroundResource(R.drawable.loading_dialog_bg_shape);
		this.centerLay.removeAllViews();
		this.centerLay.addView(view, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	/**
	 * 自定义Dialog中间的视图
	 * 
	 * @param layoutId
	 */
	public void setCenterView(int layoutId)
	{
		this.centerLay.removeAllViews();
		View view = View.inflate(mContext, layoutId, null);
		// view.setBackgroundResource(R.drawable.loading_dialog_bg_shape);
		this.centerLay.addView(view, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	public void setCenterView(View view, LayoutParams params)
	{
		this.centerLay.removeAllViews();
		// view.setBackgroundResource(R.drawable.loading_dialog_bg_shape);
		this.centerLay.addView(view, params);
	}

	/**
	 * 设置中间视图的背景
	 * 
	 * @param resId
	 */
	public void setCenterViewBackground(int resId)
	{
		centerLay.setBackgroundResource(resId);
	}

	public void setDialogBackgroundColor(int resId)
	{
		allLay.setBackgroundColor(resId);
	}

	/**
	 * 设置是否显示标题
	 * 
	 * @param isvisible
	 */
	public void setTitleVisible(boolean isvisible)
	{
		if (isvisible)
		{
			title.setVisibility(View.VISIBLE);
			topLine.setVisibility(View.VISIBLE);
		} else
		{
			title.setVisibility(View.GONE);
			topLine.setVisibility(View.GONE);
		}
	}
}
