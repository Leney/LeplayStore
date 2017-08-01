package com.xd.leplay.store.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.base.util.DLog;
import com.xd.leplay.store.R;

/**
 * 从底部弹出的自定义Dialog
 * 
 * @author lilijun
 *
 */
public class BottomDialog extends Dialog
{

	private Context mContext;

	/** 标题、默认中间显示的文本内容 */
	private TextView title, centerMsg;

	/** 显示中间视图的布局 */
	private FrameLayout centerLayout;

	/** 底部按钮部分 */
	private LinearLayout bottomBtnsLay, bottomBtnsLay1;

	/** 左边按钮、右边按钮、底部按钮 */
	private TextView leftBtn, rightBtn, bottomBtn;

	/** 底部两个按钮之间的分隔线 */
	private View emptyView;

	// public BottomDialog(Context context, boolean cancelable,
	// OnCancelListener cancelListener)
	// {
	// super(context, cancelable, cancelListener);
	// this.mContext = context;
	// }
	//
	// public BottomDialog(Context context, int theme)
	// {
	// super(context, theme);
	// this.mContext = context;
	// }

	public BottomDialog(Context context)
	{
		super(context, R.style.DialogTheme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_bottom_dialog);
		setCanceledOnTouchOutside(true);

		title = (TextView) findViewById(R.id.base_bottom_dialog_title);
		centerMsg = (TextView) findViewById(R.id.base_bottom_dialog_content_text);
		centerLayout = (FrameLayout) findViewById(R.id.base_bottom_dialog_center_view);
		bottomBtnsLay = (LinearLayout) findViewById(R.id.base_bottom_dialog_btn_lay);
		bottomBtnsLay1 = (LinearLayout) findViewById(R.id.base_bottom_dialog_left_btn1);
		leftBtn = (TextView) findViewById(R.id.base_bottom_dialog_left_btn);
		rightBtn = (TextView) findViewById(R.id.base_bottom_dialog_right_btn);
		bottomBtn = (TextView) findViewById(R.id.base_bottom_dialog_bottom_btn);
		emptyView = findViewById(R.id.base_bottom_dialog_empty_line);

		// Window window = getWindow();
		// WindowManager.LayoutParams windowparams = window.getAttributes();
		// window.setGravity(Gravity.BOTTOM);
		// Rect rect = new Rect();
		// View view1 = window.getDecorView();
		// view1.getWindowVisibleDisplayFrame(rect);
		// WindowManager windowManager = ((Activity)
		// mContext).getWindowManager();
		// Display display = windowManager.getDefaultDisplay();
		// windowparams.width = display.getWidth();
		// window.setWindowAnimations(R.style.bottomDialogStyle);
		// window.setBackgroundDrawableResource(android.R.color.transparent);
		// window.setAttributes((android.view.WindowManager.LayoutParams)
		// windowparams);

		Resources resources = getContext().getResources();
		// 获得屏幕参数：主要是分辨率，像素等。
		DisplayMetrics dm = resources.getDisplayMetrics();
		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.bottomDialogStyle);
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = dm.widthPixels;
		getWindow().setAttributes(params);
	}

	/**
	 * 设置Dialog的标题
	 * 
	 * @param title
	 */
	public void setTitleName(String title)
	{
		DLog.i("lilijun", "BottomDialog,setTitleName()   !!!");
		this.title.setText(title);
	}

	/**
	 * 设置默认的中间的内容文本
	 * 
	 * @param msg
	 */
	public void setCenterMsg(String msg)
	{
		this.centerMsg.setText(msg);
	}

	/**
	 * 设置默认的中间的内容文本
	 * 
	 * @param text
	 */
	public void setCenterMsg(CharSequence text)
	{
		this.centerMsg.setText(text);
	}

	/**
	 * 自定义Dialog中间的视图
	 * 
	 * @param view
	 */
	public void setCenterView(View view)
	{
		this.centerLayout.removeAllViews();
		this.centerLayout.addView(view);
	}

	/**
	 * 自定义Dialog中间的视图
	 * 
	 * @param layoutId
	 */
	public void setCenterView(int layoutId)
	{
		this.centerLayout.removeAllViews();
		this.centerLayout.addView(View.inflate(mContext, layoutId, null));
	}

	/**
	 * 设置底部按钮部分是否显示
	 * 
	 * @param visible
	 */
	public void setBottomBtnsLayVisible(boolean visible)
	{
		if (visible)
		{
			this.bottomBtnsLay.setVisibility(View.VISIBLE);
		} else
		{
			this.bottomBtnsLay.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置左边按钮的文本
	 * 
	 * @param text
	 */
	public void setLeftBtnText(String text)
	{
		this.leftBtn.setText(text);
	}

	/**
	 * 设置右边按钮的文本
	 * 
	 * @param text
	 */
	public void setRightBtnText(String text)
	{
		this.rightBtn.setText(text);
	}

	/**
	 * 设置左边按钮是否显示
	 * 
	 * @param isvisible
	 */
	public void setLeftBtnVisible(boolean isvisible)
	{
		if (isvisible)
		{
			this.leftBtn.setVisibility(View.VISIBLE);
		} else
		{
			this.leftBtn.setVisibility(View.GONE);
			this.emptyView.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置右边按钮是否显示
	 * 
	 * @param isvisible
	 */
	public void setRightBtnVisible(boolean isvisible)
	{
		if (isvisible)
		{
			this.rightBtn.setVisibility(View.VISIBLE);
		} else
		{
			this.rightBtn.setVisibility(View.GONE);
			this.emptyView.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置左边按钮的点击事件
	 * 
	 * @param listener
	 */
	public void setLeftBtnOnclickLinstener(
			android.view.View.OnClickListener listener)
	{
		this.leftBtn.setOnClickListener(listener);
	}

	/**
	 * 设置右边按钮的点击事件
	 * 
	 * @param listener
	 */
	public void setRightOnclickLinstener(
			android.view.View.OnClickListener listener)
	{
		this.rightBtn.setOnClickListener(listener);
	}

	/**
	 * 设置最底部按钮部分是否显示
	 * 
	 * @param visible
	 */
	public void setBottomBtnsLay1Visible(boolean visible)
	{
		if (visible)
		{
			this.bottomBtnsLay1.setVisibility(View.VISIBLE);
		} else
		{
			this.bottomBtnsLay1.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置最底部按钮的文本
	 * 
	 * @param text
	 */
	public void setBottomBtnText(String text)
	{
		this.bottomBtn.setText(text);
	}

	/**
	 * 设置底部按钮的点击事件
	 * 
	 * @param listener
	 */
	public void setBottomBtnOnclickLinstener(
			android.view.View.OnClickListener listener)
	{
		this.bottomBtn.setOnClickListener(listener);
	}
}
