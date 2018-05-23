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
            mWheelView.setTotalScrollY(mWheelView.getTotalScrollY() + mRealOffset);

            //这里如果不是循环模式，则点击空白位置需要回滚，不然就会出现选到－1 item的 情况
            if (!mWheelView.isLoop()) {
                float itemHeight = mWheelView.getItemHeight();
                float top = (float) (-mWheelView.getInitPosition()) * itemHeight;
                float bottom = (float) (mWheelView.getItemCount() - 1 - mWheelView.getInitPosition()) * itemHeight;
                if (mWheelView.getTotalScrollY() <= top || mWheelView.getTotalScrollY() >= bottom) {
                    mWheelView.setTotalScrollY(mWheelView.getTotalScrollY() - mRealOffset);
                    mWheelView.cancelFuture();
                    mWheelView.getHandler().sendEmptyMessage(MessageHandler.ITEM_SELECTED);
                    return;
                }
            }
            mWheelView.getHandler().sendEmptyMessage(MessageHandler.INVALIDATE_WHEEL_VIEW);
            mRealTotalOffset = mRealTotalOffset - mRealOffset;
        }

    }
}
