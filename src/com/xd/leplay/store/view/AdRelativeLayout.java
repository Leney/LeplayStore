package com.xd.leplay.store.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by yb on 2017/4/7.
 */
public class AdRelativeLayout extends RelativeLayout {
    public float downX,downY,upX,upY;
    public AdRelativeLayout(Context context) {
        super(context);
    }

    public AdRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AdRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            downX = event.getX();
            downY = event.getY();
        }else if (event.getAction() == MotionEvent.ACTION_UP){
            upX = event.getX();
            upY = event.getY();
        }
        return super.onTouchEvent(event);
    }
}
