package com.ytl.customview.widget.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.ytl.customview.widget.view.WheelView;

/**
 * package:com.ytl.customview.widget.listener
 * description:
 * author: ytl
 * date:18.4.27  8:51.
 */


public class WheelViewGestrueListener extends GestureDetector.SimpleOnGestureListener {

    private WheelView mWheelView;

    public WheelViewGestrueListener(WheelView wheelView) {
        mWheelView = wheelView;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return super.onFling(e1, e2, velocityX, velocityY);
    }
}
