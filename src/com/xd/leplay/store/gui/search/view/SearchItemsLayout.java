package com.xd.leplay.store.gui.search.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.leplay.store.R;
import com.xd.leplay.store.gui.search.OnStartSearchLinstener;

public class SearchItemsLayout extends LinearLayout
{
	// // 控件与屏幕最左边的间隔距离,控件与屏幕最右边的间隔距离
	// private int leftPadding, rightPadding;

	// 临时存储当前行所占用的宽度
	private int emptyWidth;

	private Resources resources;

	private Context context;

	// 屏幕宽度
	private int width;

	// 每一个按钮与其右边的按钮之间的间距(按钮与按钮之间的间距)
	private int itemPaddingRight;

	// 行与行之间的间距
	private int linePaddingBottom;

	private OnStartSearchLinstener onStartSearchLinstener;

	public SearchItemsLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	public SearchItemsLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public SearchItemsLayout(Context context)
	{
		super(context);
		init(context);
	}

	private void init(Context context)
	{
		this.context = context;
		setOrientation(LinearLayout.VERTICAL);
		resources = context.getResources();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		itemPaddingRight = (int) resources.getDimension(R.dimen.dp12);
		linePaddingBottom = (int) resources.getDimension(R.dimen.dp12);
	}

	/**
	 *
	 * @param itemPadding
	 *            每一个按钮与其右边的按钮之间的间距(按钮与按钮之间的间距)
	 * @param linePadding
	 *            行与行之间的间距
	 * @param items
	 *            显示按钮名称集合
	 * @param btnBgRes
	 *            按钮背景资源
	 */
	public void setAdapter(List<String> items, int btnBgRes,
			OnStartSearchLinstener linstener)
	{
		this.onStartSearchLinstener = linstener;
		// 总共行数的集合
		List<LinearLayout> linearLayouts = new ArrayList<LinearLayout>();
		int measuresWidth = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int measureHeight = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);

		int leftWidth = getPaddingLeft();
		emptyWidth = leftWidth;
		LayoutParams itemParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		itemParams.rightMargin = itemPaddingRight;

		LayoutParams LineParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		LineParams.bottomMargin = linePaddingBottom;

		// if (items.size() / 2 < 1)
		// {
		// LinearLayout linearLayout = new LinearLayout(context);
		// linearLayout.setLayoutParams(LineParams);
		// linearLayouts.add(linearLayout);
		// } else
		// {
		// for (int i = 0; i < items.size() / 2; i++)175 231 183
		for (int i = 0; i < items.size(); i++)
		{
			// 为了防止数组越界，行数最多为总共长度的一半
			LinearLayout linearLayout = new LinearLayout(context);
			linearLayout.setLayoutParams(LineParams);
			linearLayouts.add(linearLayout);
		}
		// }

		// 行的位置position
		int linePosition = 0;

		for (int i = 0; i < items.size(); i++)
		{

			TextView button = new TextView(context);
			button.setSingleLine();
			button.setText(items.get(i));
			// button.setHeight(itemParams.height);
			button.setEllipsize(TruncateAt.END);
			button.setTextColor(resources
					.getColor(R.color.list_soft_describe_color));
			button.setOnClickListener(onKeyButtonClickListener);
			button.setTag(items.get(i));
			button.setLayoutParams(itemParams);
			button.setBackgroundResource(btnBgRes);
			button.setPadding(itemPaddingRight, 8, itemPaddingRight, 8);

			button.measure(measuresWidth, measureHeight);
			int textWidth = button.getMeasuredWidth() + itemPaddingRight;
			// 如果还有下一个数据
			if (width - emptyWidth > textWidth)
			{
				// 如果所剩长度还可以容纳此条数据
				linearLayouts.get(linePosition).addView(button);
				emptyWidth += textWidth;
			} else
			{
				// 如果所剩长度不可以容纳此条数据了 则显示到下行
				emptyWidth = leftWidth + textWidth;
				linePosition++;
				if (linePosition < linearLayouts.size())
				{
					linearLayouts.get(linePosition).addView(button);
				}
			}

		}

		// for (int j = 0; j < linePosition + 1; j++)
		// {
		// addView(linearLayouts.get(j));
		// }
		for (int j = 0; j < linePosition; j++)
		{
			addView(linearLayouts.get(j));
		}
	}

	/** 关键字监听器 */
	private OnClickListener onKeyButtonClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String key = (String) v.getTag();
			onStartSearchLinstener.onStartSearch(key);
		}
	};

}
