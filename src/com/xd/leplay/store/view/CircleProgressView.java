package com.xd.leplay.store.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xd.leplay.store.R;

/**
 * 原地旋转的加载进度视图
 * 
 * @author lilijun
 * 
 */
public class CircleProgressView extends FrameLayout
{
	private RotateAnimation animation_rotate;

	private ImageView bottomImg, rotateImg;

	public CircleProgressView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	public CircleProgressView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public CircleProgressView(Context context)
	{
		super(context);
		init(context);
	}

	// @Override
	// protected void onAttachedToWindow()
	// {
	// super.onAttachedToWindow();
	// // 开始动画
	// startAnimation(animation_rotate);
	// }

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		// 停止动画
		clearAnimation();
	}

	private void init(Context context)
	{
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// setBackgroundResource(R.drawable.loading_view_shape);
		animation_rotate = new RotateAnimation(-45 // 动画起始时的旋转角度
				, 314 // 动画旋转到的角度
				, RotateAnimation.RELATIVE_TO_SELF // 动画在X轴相对于物件位置类型
				, 0.5f // 动画相对于物件的X坐标的开始位置
				, RotateAnimation.RELATIVE_TO_SELF // 动画在Y轴相对于物件位置类型
				, 0.5f // 动画相对于物件的Y坐标的开始位置
		);

		animation_rotate.setRepeatCount(-1); // 设置重复动画
		animation_rotate.setDuration(1000); // 设置旋转一次所需时间
		animation_rotate.setInterpolator(new LinearInterpolator()); // 设置为匀速旋转

		bottomImg = new ImageView(context);
		bottomImg.setImageResource(R.drawable.loading_bottom_img);

		rotateImg = new ImageView(context);
		rotateImg.setImageResource(R.drawable.loading_rotate_img);
		
		bottomImg.setLayoutParams(params);
		rotateImg.setLayoutParams(params);

		addView(bottomImg);
		addView(rotateImg);

		// 开始动画
		rotateImg.startAnimation(animation_rotate);

		// setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT));
	}

	public void setVisibilyView(boolean visibily)
	{
		if (visibily)
		{
			if (getVisibility() != View.VISIBLE)
			{
				setVisibility(View.VISIBLE);
			}
			if (getAnimation() == null)
			{
				rotateImg.startAnimation(animation_rotate);
			}
		} else
		{
			setVisibility(View.GONE);
			rotateImg.clearAnimation();
		}
	}

}
