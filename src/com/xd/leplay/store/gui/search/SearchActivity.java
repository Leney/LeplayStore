package com.xd.leplay.store.gui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.xd.leplay.store.DataCollectionConstant;
import com.xd.leplay.store.R;
import com.xd.leplay.store.control.DataCollectionManager;
import com.xd.leplay.store.util.DisplayUtil;
import com.umeng.analytics.MobclickAgent;

public class SearchActivity extends AppCompatActivity implements
		OnStartSearchLinstener
{
	private FragmentManager fragmentManager = null;

	/** 返回按钮 */
	private ImageView backImg;

	/** 输入框 */
	private EditText inputText;

	/** 搜索按钮 、清除文本按钮 */
	private ImageView searchImg, clearImg;

	/** 2个fragment */
	private Fragment[] fragments = new Fragment[2];

	/** 当前所在tab的标识值 */
	private int curTab = 0;

	private String action = "";

	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);

		action = DataCollectionManager.getAction(DataCollectionManager
				.getIntentDataCollectionAction(getIntent()),
				DataCollectionConstant.DATA_COLLECTION_SEARCH_VALUE);

		fragmentManager = getSupportFragmentManager();

		fragments[0] = SearchKeysFragment.getInstance(action);
		((SearchKeysFragment) fragments[0]).setOnStartSearchLinstener(this);

		backImg = (ImageView) findViewById(R.id.search_titile_back_img);
		backImg.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		inputText = (EditText) findViewById(R.id.search_titile_input_edit);
		inputText.setHintTextColor(getResources().getColor(
				R.color.divider_color));
		inputText.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
				if (s != null && s.length() != 0)
				{
					clearImg.setVisibility(View.VISIBLE);
				} else
				{
					clearImg.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s)
			{
				// TODO Auto-generated method stub

			}
		});

		inputText.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event)
			{
				if (actionId == EditorInfo.IME_ACTION_SEARCH)
				{
					if (fragments[1] == null)
					{
						fragments[1] = SearchResultFragment
								.getSearchResultFragment(inputText.getText()
										.toString(), action);
					} else
					{
						((SearchResultFragment) fragments[1])
								.startSearch(inputText.getText().toString());
					}
					if (curTab != 1)
					{
						changePage(fragments[1]);
					}
					curTab = 1;
					// 隐藏软键盘
					DisplayUtil.hideInput(SearchActivity.this, inputText);
					SearchHistoryManager.getInstance().addSearchHistory(
							inputText.getText().toString());
				}
				return false;
			}
		});

		clearImg = (ImageView) findViewById(R.id.search_title_clear_img);
		clearImg.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				inputText.setText("");
				clearImg.setVisibility(View.GONE);
			}
		});
		searchImg = (ImageView) findViewById(R.id.search_titile_search_btn);
		searchImg.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				DataCollectionManager
						.getInstance()
						.addRecord(
								DataCollectionManager
										.getAction(
												action,
												DataCollectionConstant.DATA_COLLECTION_SEARCH_USER_INPUT_SEARCH_VALUE));
				DataCollectionManager
						.getInstance()
						.addYouMengEventRecord(
								SearchActivity.this,
								action,
								DataCollectionConstant.EVENT_ID_CLICK_USER_INPUT_SEARCH_BTN,
								null);
				if (fragments[1] == null)
				{
					fragments[1] = SearchResultFragment
							.getSearchResultFragment(inputText.getText()
									.toString(), action);
				} else
				{
					((SearchResultFragment) fragments[1]).startSearch(inputText
							.getText().toString());
				}
				if (curTab != 1)
				{
					changePage(fragments[1]);
				}
				curTab = 1;
				// 隐藏软键盘
				DisplayUtil.hideInput(SearchActivity.this, inputText);
				SearchHistoryManager.getInstance().addSearchHistory(
						inputText.getText().toString());
			}
		});

		// 设置进入应用时 默认跳转的tab
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.add(R.id.search_fragment_lay, fragments[0]);
		transaction.commitAllowingStateLoss();
		curTab = 0;
	}

	private void changePage(Fragment fragment)
	{
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.hide(fragments[curTab]);
		if (!fragment.isAdded())
		{
			// 如果没有被添加过
			transaction.add(R.id.search_fragment_lay, fragment);
		} else if (fragment.isHidden())
		{
			transaction.show(fragment);
		}
		transaction.commitAllowingStateLoss();
	}

	@Override
	public void finish()
	{
		if (curTab == 0)
		{
			super.finish();
		} else
		{
			changePage(fragments[0]);
			curTab = 0;
		}
	}

	@Override
	public void onStartSearch(String key)
	{
		if (fragments[1] == null)
		{
			fragments[1] = SearchResultFragment.getSearchResultFragment(key,
					action);
		} else
		{
			((SearchResultFragment) fragments[1]).startSearch(key);
		}
		if (curTab != 1)
		{
			changePage(fragments[1]);
		}
		curTab = 1;
		inputText.setText(key);
		// 隐藏软键盘
		DisplayUtil.hideInput(SearchActivity.this, inputText);
		SearchHistoryManager.getInstance().addSearchHistory(key);
	}

	public static void startSearchActivity(Context context, String action)
	{
		DataCollectionManager.startActivity(context, new Intent(context,
				SearchActivity.class), action);
	}
}
