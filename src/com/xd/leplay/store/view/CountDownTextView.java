package com.xd.leplay.store.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 倒计时控件
 * Created by lilijun on 2016/9/8.
 */
public class CountDownTextView extends TextView {
    private final int COUNT_DOWN_MSG = 1;
    /**
     * 倒计时总次数
     */
    private int totalCount = 60;
    /**
     * 默认的显示文本
     */
    private String defaultText;

    private OnCountDownDoneListener listener;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == COUNT_DOWN_MSG) {
                if (--totalCount < 0) {
                    // 倒计时完成
                    stop();
                    if (listener != null) {
                        listener.onDone();
                    }
                } else {
                    // 倒计时未完成
                    setText(totalCount + "s");
                    sendEmptyMessageDelayed(COUNT_DOWN_MSG, 1000);
                }
            }
        }
    };

    public CountDownTextView(Context context) {
        super(context);
        setEnabled(false);
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEnabled(false);
    }

    public CountDownTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setEnabled(false);
    }

    public CountDownTextView(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setEnabled(false);
    }


    /**
     * 设置监听倒计时完成广播
     *
     * @param listner
     */
    public void setOnDoneListner(OnCountDownDoneListener listner) {
        this.listener = listner;
    }

    // 开始倒计时
    public void start(int totalCount) {
        setVisibility(VISIBLE);
        this.totalCount = totalCount;
        this.defaultText = getText().toString();
        setText(totalCount + "s");
        handler.sendEmptyMessageDelayed(COUNT_DOWN_MSG, 1000);
    }

    public void stop() {
        setVisibility(GONE);
        handler.removeMessages(COUNT_DOWN_MSG);
    }

    /**
     * 倒计时完成接口
     */
    public interface OnCountDownDoneListener {
        void onDone();
    }
}
