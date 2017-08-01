package com.xd.leplay.store.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.leplay.store.R;
import com.xd.leplay.store.util.ToolsUtil;

/**
 * 页面网络错误视图
 * 
 * @author lilijun
 *
 */
public class NetErrorView extends LinearLayout
{
	/** 加载失败视图 */
	private LinearLayout loadFailedLay;

	private ImageView errorImg;

	private TextView errorText;

	/** 设置网络按钮 */
	private TextView setNetBtn;

	public NetErrorView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public NetErrorView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public NetErrorView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public NetErrorView(Context context)
	{
		super(context);
		init(context);
	}

	private void init(Context context)
	{
		View view = View.inflate(context, R.layout.net_error_layout, null);
		loadFailedLay = (LinearLayout) view.findViewById(R.id.net_error_lay);
		errorImg = (ImageView) view.findViewById(R.id.net_error_img);
		errorText = (TextView) view.findViewById(R.id.net_error_text);
		setNetBtn = (TextView) view.findViewById(R.id.net_error_set_net_btn);

		setNetBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ToolsUtil.startSettingActivity(getContext());
			}
		});

		setGravity(Gravity.CENTER);
		addView(view);
	}

	/**
	 * 设置视图显示资源
	 * 
	 * @param imgResId
	 * @param errorText
	 * @param btnText
	 */
	public void setErrorView(int imgResId, String showText, String btnText)
	{
		errorImg.setImageResource(imgResId);
		errorText.setText(showText);
		setNetBtn.setText(btnText);
		if (btnText.trim().equals(""))
		{
			setBtnVisible(false);
		} else
		{
			setBtnVisible(true);
		}
	}

	/**
	 * 设置按钮的点击事件
	 * 
	 * @param listener
	 */
	public void setBtnOnclickLinstener(OnClickListener listener)
	{
		setNetBtn.setOnClickListener(null);
		setNetBtn.setOnClickListener(listener);
	}

	/**
	 * 设置按钮是否显示
	 * 
	 * @param isVisible
	 */
	public void setBtnVisible(boolean isVisible)
	{
		if (isVisible)
		{
			setNetBtn.setVisibility(View.VISIBLE);
		} else
		{
			setNetBtn.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置显示加载失败视图
	 */
	public void showLoadFailedLay()
	{
		loadFailedLay.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置重试点击事件
	 * 
	 * @param listener
	 */
	public void setRefrushOnClickListener(OnClickListener listener)
	{
		loadFailedLay.setOnClickListener(listener);
	}

}
