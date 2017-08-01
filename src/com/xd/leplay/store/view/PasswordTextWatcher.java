package com.xd.leplay.store.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class PasswordTextWatcher implements TextWatcher
{
	private String regex = "";
	private boolean mIsMatch;
	private CharSequence mResult;
	private int mSelectionStart;
	private int mSelectionEnd;
	private EditText mPasswordEditText;

	public PasswordTextWatcher()
	{
	};

	public PasswordTextWatcher(EditText editText, String password_regex)
	{
		mPasswordEditText = editText;
		regex = password_regex;
	};

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after)
	{
		mSelectionStart = mPasswordEditText.getSelectionStart();
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		CharSequence charSequence = "";
		if ((mSelectionStart + count) <= s.length())
		{
			charSequence = s.subSequence(mSelectionStart, mSelectionStart
					+ count);
		}
		mIsMatch = pswFilter(charSequence);
		if (!mIsMatch)
		{
			String temp = s.toString();
			mResult = temp.replace(charSequence, "");
			mSelectionEnd = start;
		}
	}

	@Override
	public void afterTextChanged(Editable s)
	{
		if (!mIsMatch)
		{
			mPasswordEditText.setText(mResult);
			mPasswordEditText.setSelection(mSelectionEnd);
		}
	}

	/**
	 * pswFilter: if the param folow the password match rule<br/>
	 *
	 * @author yanbb
	 * @param s
	 * @return
	 * @since MT 1.0
	 */
	private boolean pswFilter(CharSequence s)
	{
		if (TextUtils.isEmpty(s))
		{
			return true;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		if (matcher.matches())
		{
			return true;
		}
		return false;
	}

	public EditText getPasswordEditText()
	{
		return mPasswordEditText;
	}

	public void setPasswordEditText(EditText passwordEditText)
	{
		this.mPasswordEditText = passwordEditText;
	}
}