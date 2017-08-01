package com.xd.leplay.store.util;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class TextUtil {
	
	
	
	
	/** 获取文字的高度 */
	public static int getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.ascent) + 2;
	}

	/** 获取表情的高度 */
	public static int getEmojiHeight(float fontSize, Context context) {
		float density = context.getResources().getDisplayMetrics().density;
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		int height = (int) ((Math.ceil(fm.descent - fm.ascent) + 2) * density);
		return height;
	}

	/** 计算出该TextView中文字的长度(像素) */
	public static float getTextViewLength(TextView textView, String text) {
		TextPaint paint = textView.getPaint();
		// 得到使用该paint写上text的时候,像素为多少
		float textLength = paint.measureText(text);
		return textLength;
	}
	
	/**
	 * 字符串格式化工具
	 * @param list
	 * @return
	 */
	public static String getFormatString(ArrayList<String> list){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++)
		{
			sb.append(list.get(i));
			if (i%2==0)
			{
				sb.append("　　");
			}else {
				sb.append("\n");
				
			}
		}
		if (sb.length()>1)
		{
			sb = sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	
	/***
	 * 是否为空
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str){
		if (str==null||"".equals(str))
		{
			return true;
		}
		return false;
	}
	/***
	 * 设置textview指定文字颜色
	 * @param
	 */
	public static SpannableStringBuilder setTextPartialColor(String str,int startIndex,int  endIndex,int color){
		SpannableStringBuilder style=new SpannableStringBuilder(str); 
	    style.setSpan(new ForegroundColorSpan(color),startIndex,endIndex+1,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		return style; 
	}
}
