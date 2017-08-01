package com.xd.leplay.store.view;

import com.xd.leplay.store.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class OvalView extends View
{
	/* 绘制画布颜色**/
	private int canvasColor;
	
	/* 绘制画笔颜色**/
	private int paintColor;
	
	/* 画笔粗细**/
	private int paintStroke;
	
	public OvalView(Context context, AttributeSet attrs)
	{
		  this(context, attrs, 0);  
	}

	public OvalView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		  /** 
         * 获得我们所定义的自定义样式属性 
         */  
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.OvalView, defStyle, 0);  
        int n = a.getIndexCount();  
        for (int i = 0; i < n; i++)  
        {  
            int attr = a.getIndex(i);  
            switch (attr)  
            {  
            
            case R.styleable.OvalView_canvasColor:  
                // 默认颜色设置为黑色  
            	canvasColor = a.getColor(attr, Color.BLACK);  
                break;  
            case R.styleable.OvalView_paintColor:  
            	paintColor= a.getColor(attr, Color.BLACK);  
                break;  
            case R.styleable.OvalView_paintStroke:  
                // 默认设置为16sp，TypeValue也可以把sp转化为px  
            	paintStroke = a.getDimensionPixelSize(attr, 0);  
                break;  
            }  
  
        }  
        a.recycle();  
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
	    super.onDraw(canvas);  
	    Paint paint=new Paint();
	    paint.setAntiAlias(true);                       //设置画笔为无锯齿  
	    paint.setColor(paintColor);                    //设置画笔颜色  
	    canvas.drawColor(canvasColor);                  //画布背景  
	    paint.setStyle(Style.FILL); 
	    paint.setStrokeWidth(paintStroke);
	      
	    RectF oval=new RectF();                     //RectF对象   
	    oval.left=0;                              //左边  
	    oval.top=0;                                   //上边  
	    oval.right=getMeasuredWidth();                             //右边  
	    oval.bottom=getMeasuredHeight();                                //下边  
	    canvas.drawArc(oval, 0, 180, true, paint);    //绘制圆弧 
	    canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight()/2, paint);// 长方形 
		  
		 
	}

}
