package com.xd.leplay.store;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.xd.leplay.store.gui.main.MainActivity;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 引导界面
 * 
 * @author luoxingxing
 *
 */
public class GuideActivity extends AppCompatActivity
{

	private ViewPager viewPager;

	/** 引导页图片集合 */
	private List<ImageView> viewContainter;

	private float fromx = 0;

	private float tox = 0;

	private Handler handler;

	private TimerThread timerThread;

	/** 引导页集合填充图片资源 */
	private int[] images =
	{ R.drawable.loading1, R.drawable.loading2, R.drawable.loading3 };

	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onPageStart("GuideActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPageEnd("GuideActivity");
		MobclickAgent.onPause(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_layout);
		handler = new Handler();
		timerThread = new TimerThread();
		viewContainter = new ArrayList<ImageView>();

		// 图片压缩处理防溢出
		BitmapFactory.Options opt = new BitmapFactory.Options();

		opt.inPreferredConfig = Bitmap.Config.RGB_565;

		opt.inPurgeable = true;

		opt.inInputShareable = true;

		for (int i = 0; i < images.length; i++)
		{
			InputStream is = getResources().openRawResource(images[i]);

			Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);

			ImageView imageLoad = new ImageView(this);

			imageLoad.setImageBitmap(bitmap);

			imageLoad.setScaleType(ScaleType.FIT_XY);

			viewContainter.add(imageLoad);

		}
		viewContainter.get(2).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				startMainActivity();
			}
		});
		viewContainter.get(2).setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					fromx = event.getX();
				} else if (event.getAction() == MotionEvent.ACTION_MOVE)
				{
					tox = event.getX();
					if (tox - fromx < -10)
					{
						startMainActivity();
					}
				}
				return false;
			}
		});

		viewPager = (ViewPager) findViewById(R.id.guidePager);
		viewPager.setAdapter(new PagerAdapter()
		{

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object)
			{
				((ViewPager) container).removeView(viewContainter.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position)
			{
				((ViewPager) container).addView(viewContainter.get(position));
				return viewContainter.get(position);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1)
			{
				return arg0 == arg1;
			}

			@Override
			public int getItemPosition(Object object)
			{
				return super.getItemPosition(object);
			}

			@Override
			public int getCount()
			{
				return viewContainter.size();
			}
		});
		viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int arg0)
			{
				if (arg0 == viewContainter.size() - 1)
				{

					handler.postDelayed(timerThread, 2000);

				} else

					handler.removeCallbacks(timerThread);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{

			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{

			}
		});

	}

	private void startMainActivity()
	{
		Intent intent = new Intent(GuideActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		GuideActivity.this.startActivity(intent);
		GuideActivity.this.finish();
	}

	class TimerThread implements Runnable
	{

		@Override
		public void run()
		{
			startMainActivity();
		}

	}
}
