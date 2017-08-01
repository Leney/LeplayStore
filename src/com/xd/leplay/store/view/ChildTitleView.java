package com.xd.leplay.store.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xd.leplay.store.R;

/**
 * 子Title
 * 
 * @author lilijun
 *
 */
public class ChildTitleView extends RelativeLayout
{
	/** 整个标题部分 */
	private RelativeLayout tilteLay;
	/** 左边部分，右边部分 */
	private LinearLayout leftLay, rightLay;

	/** 返回图标，右边第一个按钮图标，右边第二个按钮图标 */
	private ImageView backImg, rightImg1, rightImg2;

	/** 右边的文本按钮 */
	private TextView rightTextBtn;

	/** 标题名称 */
	private TextView titleName;

	/** 底部的一条分割线 */
	private View bottomLine;

	public ChildTitleView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public ChildTitleView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public ChildTitleView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public ChildTitleView(Context context)
	{
		super(context);
		init(context);
	}

	private void init(Context context)
	{
		View view = View.inflate(context, R.layout.child_title_view, null);
		tilteLay = (RelativeLayout) view.findViewById(R.id.child_titile_lay);
		leftLay = (LinearLayout) view.findViewById(R.id.child_tilte_back_lay);
		leftLay.setOnClickListener(backListener);
		rightLay = (LinearLayout) view.findViewById(R.id.child_title_right_lay);
		backImg = (ImageView) view.findViewById(R.id.child_title_back_img);
		rightImg1 = (ImageView) view.findViewById(R.id.child_title_right_img_1);
		rightImg2 = (ImageView) view.findViewById(R.id.child_title_right_img_2);
		rightTextBtn = (TextView) view
				.findViewById(R.id.child_title_right_text);
		titleName = (TextView) view.findViewById(R.id.child_title_name);
		bottomLine = view.findViewById(R.id.child_title_bottom_line);
		addView(view);
		setBackgroundColor(getResources().getColor(R.color.title_bg2));
	}

	private OnClickListener backListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			((Activity) v.getContext()).finish();
		}
	};

	/**
	 * 设置返回的点击事件
	 * 
	 * @param listener
	 */
	public void setBackLinstener(OnClickListener listener)
	{
		leftLay.setOnClickListener(null);
		leftLay.setOnClickListener(listener);
	}

	/**
	 * 设置标题名称
	 * 
	 * @param name
	 */
	public void setTitleName(String name)
	{
		titleName.setText(name);
	}

	/**
	 * 设置右边视图是否显示
	 * 
	 * @param isVisible
	 */
	public void setRightLayVisible(boolean isVisible)
	{
		if (isVisible)
		{
			rightLay.setVisibility(View.VISIBLE);
		} else
		{
			rightLay.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置右边第一个按钮图片的点击事件
	 * 
	 * @param listener
	 */
	public void setRightFirstImgOnclickListener(OnClickListener listener)
	{
		rightImg1.setOnClickListener(listener);
	}

	/**
	 * 设置右边第二个按钮图片的点击事件
	 * 
	 * @param listener
	 */
	public void setRightSecondImgOnclickListener(OnClickListener listener)
	{
		rightImg2.setOnClickListener(listener);
	}

	/**
	 * 设置右边的文本按钮的点击事件
	 * 
	 * @param listener
	 */
	public void setRightTextBtnOnClickListener(OnClickListener listener)
	{
		rightTextBtn.setOnClickListener(listener);
	}

	/**
	 * 设置右边文本按钮的名称文本
	 * 
	 * @param text
	 */
	public void setRightTextBtnName(String text)
	{
		rightTextBtn.setVisibility(View.VISIBLE);
		rightTextBtn.setText(text);
		rightImg1.setVisibility(View.GONE);
		rightImg2.setVisibility(View.GONE);
	}

	/**
	 * 设置右边文本按钮的字体颜色
	 * 
	 * @param resId
	 */
	public void setRightTextBtnColor(int resId)
	{
		rightTextBtn.setTextColor(resId);
	}

	public void setRightTextBtnLeftDrawableResource(int resId)
	{
		Drawable drawable = getResources().getDrawable(resId);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		rightTextBtn.setCompoundDrawables(drawable, null, null, null);
	}

	/**
	 * 设置右边第一个按钮是否显示
	 * 
	 * @param isVisible
	 */
	public void setRightFirstImgVisible(boolean isVisible)
	{
		if (isVisible)
		{
			rightImg1.setVisibility(View.VISIBLE);
		} else
		{
			rightImg1.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置右边第二个按钮是否显示
	 * 
	 * @param isVisible
	 */
	public void setRightSecondImgVisible(boolean isVisible)
	{
		if (isVisible)
		{
			rightImg2.setVisibility(View.VISIBLE);
		} else
		{
			rightImg2.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置右边第一个按钮的图片资源
	 * 
	 * @param resId
	 */
	public void setRightFirstImgRes(int resId)
	{
		rightImg1.setImageResource(resId);
	}

	/**
	 * 设置右边第二个按钮的图片资源
	 * 
	 * @param resId
	 */
	public void setRightSecondImgRes(int resId)
	{
		rightImg2.setImageResource(resId);
	}

	/**
	 * 设置返回按钮资源
	 * 
	 * @param resId
	 */
	public void setBackImgRes(int resId)
	{
		backImg.setImageResource(resId);
	}

	/**
	 * 设置标题文本颜色
	 * 
	 * @param color
	 */
	public void setTitleColor(int color)
	{
		titleName.setTextColor(color);
	}

	/**
	 * 设置整个标题的背景颜色
	 * 
	 * @param color
	 */
	public void setTitleBackgroundColor(int color)
	{
		tilteLay.setBackgroundColor(color);
	}

	/**
	 * 设置整个标题的背景图片
	 * 
	 * @param res
	 */
	public void setTitleBackground(int resId)
	{
		tilteLay.setBackgroundResource(resId);
	}

	/**
	 * 设置底部的分割线是否显示
	 * 
	 * @param visible
	 */
	public void setBottomLineVisible(boolean visible)
	{
		if (visible)
		{
			bottomLine.setVisibility(View.VISIBLE);
		} else
		{
			bottomLine.setVisibility(View.GONE);
		}
	}

}
