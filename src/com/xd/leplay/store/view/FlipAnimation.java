package com.xd.leplay.store.view;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class FlipAnimation extends Animation
{
	// 用来实现绕Y轴旋转后透视投影
	private Camera camera;
	// 旋转开始角度
	private float fromDegree;
	// 旋转结束角度
	private float toDegree;
	// x中心点
	private float centerX;
	// y中心点
	private float centerY;
	private float depthZ;
	// 是否需要扭曲
	private boolean reverse;

	public FlipAnimation(float fromDegree, float toDegree, float centerX,
			float centerY, float depthZ, boolean reverse)
	{
		this.fromDegree = fromDegree;
		this.toDegree = toDegree;
		this.centerX = centerX;
		this.centerY = centerY;
		this.reverse = reverse;
		this.depthZ = depthZ;

		/**
		 * 设置动画的差值器，决定了时间片的时间点
		 * AccelerateDecelerateInterpolator============动画开始与结束的地方速率改变比较慢
		 * ，在中间的时候加速。
		 * AccelerateInterpolator===================动画开始的地方速率改变比较慢，然后开始加速。
		 * AnticipateInterpolator ==================开始的时候向后然后向前甩。
		 * AnticipateOvershootInterpolator=============开始的时候向后然后向前甩一定值后返回最后的值。
		 * BounceInterpolator=====================动画结束的时候弹起。
		 * CycleInterpolator======================动画循环播放特定的次数，速率改变沿着正弦曲线。
		 * DecelerateInterpolator===================在动画开始的地方快然后慢。
		 * LinearInterpolator======================以常量速率改变。
		 * OvershootInterpolator====================向前甩一定值后再回到原来位置。
		 * PathInterpolator========================新增的，就是可以定义路径坐标，然后可以按照路径坐
		 * 
		 */
		this.setInterpolator(new AccelerateDecelerateInterpolator());
	}

	/**
	 * 动画初始化函数
	 */
	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight)
	{
		super.initialize(width, height, parentWidth, parentHeight);
		camera = new Camera();
	}

	/**
	 * 动画调用的关键函数，在每一个动画时间片到达的时候调用，会调用多次，动画的差值器对它的 执行频率直接影响
	 */
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t)
	{
		// 生成中间角度
		float degree = fromDegree + (toDegree - fromDegree) * interpolatedTime;

		final Matrix matrix = t.getMatrix();// 取得当前的矩阵
		camera.save();

		if (reverse)
		{
			camera.translate(0, 0, depthZ * interpolatedTime);// 对矩阵进行平移变换操作
		} else
		{
			camera.translate(0, 0, depthZ * (1.0f - interpolatedTime));
		}
		camera.rotateY(degree);// 进行旋转
		camera.getMatrix(matrix);
		camera.restore();
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}
}
