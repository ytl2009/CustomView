package com.ytl.customview.widget.timer;

import com.ytl.customview.widget.view.WheelView;

import java.util.TimerTask;

/**
 * package:com.ytl.customview.widget.timer
 * description:
 * author: ytl
 * date:18.5.2  9:12.
 */


public final class InertialTimerTask extends TimerTask {

    private float mCurrentVelocityY;
    private final float mFirstVelocityY ;
    private WheelView mWheelView;



    public InertialTimerTask(WheelView wheelView,float velocity_Y) {
        super();
        this.mWheelView = wheelView;
        mFirstVelocityY = velocity_Y;
        mCurrentVelocityY = Integer.MAX_VALUE;
    }

    @Override
    public void run() {

    }
}
