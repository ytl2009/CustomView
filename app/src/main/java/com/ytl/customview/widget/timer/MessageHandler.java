package com.ytl.customview.widget.timer;

import android.os.Handler;
import android.os.Message;

import com.ytl.customview.widget.view.WheelView;

/**
 * package:com.ytl.customview.widget.timer
 * description:
 * author: ytl
 * date:18.4.27  8:58.
 */


public class MessageHandler extends Handler {
    private WheelView mWheelView;

    public static final int INVALIDATE_WHEEL_VIEW = 1000;
    public static final int SMOOTH_FLING = 2000;
    public static final int ITEM_SELECTED = 3000;

    public MessageHandler(WheelView wheelView) {
        mWheelView = wheelView;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case INVALIDATE_WHEEL_VIEW:
                mWheelView.invalidate();

                break;
            case SMOOTH_FLING:
                mWheelView.smoothScroll(WheelView.ActionEvent.FLING);

                break;
            case ITEM_SELECTED:
                mWheelView.onItemSelected();
                break;
        }
    }
}
