package com.xd.leplay.store.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by yb on 2017/3/31.
 */
public class AdImageView extends ImageView {
    public float downX,downY,upX,upY;
    public AdImageView(Context context) {
        super(context);
    }

    public AdImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AdImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
