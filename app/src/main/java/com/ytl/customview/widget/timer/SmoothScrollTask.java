package com.ytl.customview.widget.timer;

import com.ytl.customview.widget.view.WheelView;

import java.util.TimerTask;

/**
 * package:com.ytl.customview.widget.timer
 * description:
 * author: ytl
 * date:18.5.2  8:48.
 */


public final class SmoothScrollTask extends TimerTask {

    private int mRealTotalOffset;
    private int mOffset;
    private int mRealOffset;
    private WheelView mWheelView;

    public SmoothScrollTask(WheelView wheelView,int offset) {

        mWheelView = wheelView;
        mRealOffset = 0;
        mRealTotalOffset = Integer.MAX_VALUE;
        mOffset = offset;
    }


    @Override
    public void run() {
        if (mRealTotalOffset == Integer.MAX_VALUE) {
            mRealTotalOffset = mOffset;
        }

        mRealOffset = (int) (mRealTotalOffset * 0.1F);

        if (mRealOffset == 0){
            if (mRealTotalOffset < 0) {
                mRealOffset =-1;
            }else {
                mRealOffset = 1;
            }
        }

        if (Math.abs(mRealOffset) <=1 ) {
            mWheelView.cancelFuture();
            mWheelView.getHandler().sendEmptyMessage(MessageHandler.ITEM_SELECTED);
        } else {

        }

    }
}
