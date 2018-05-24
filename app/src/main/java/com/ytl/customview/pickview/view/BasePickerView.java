package com.ytl.customview.pickview.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.ytl.customview.R;
import com.ytl.customview.pickview.configure.PickerOptions;
import com.ytl.customview.pickview.listener.OnDismissListener;

/**
 * package:com.ytl.customview.pickview.view
 * description:
 * author: ytl
 * date:18.5.24  14:47.
 */


public class BasePickerView {
    private Context mContext;
    ViewGroup mContainerViewGroup;
    ViewGroup mDialogView;
    ViewGroup mRootView;

    PickerOptions mPickerOptions;
    public boolean mIsDismiss;
    private OnDismissListener mDismissListener;

    private Animation mAnimationIn;
    private Animation mAnimationOut;
    public boolean mIsShow;

    int mAnimGravity = Gravity.BOTTOM;
    View mClickView;
    private Dialog mDialog;
    private boolean mIsAnim;

    public BasePickerView(Context context) {
        mContext = context;
    }

    protected void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (mIsShow) {
            mDialogView = (ViewGroup) layoutInflater.inflate(R.layout.basepickerview,null,false);
            mDialogView.setBackgroundColor(Color.TRANSPARENT);

            mContainerViewGroup = mDialogView.findViewById(R.id.content_container);
            params.rightMargin = 30;
            params.leftMargin = 30;
            mContainerViewGroup.setLayoutParams(params);

            mDialogView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } else {
            if (mPickerOptions.mDecorView != null) {
                mPickerOptions.mDecorView = (ViewGroup) ((Activity) mContext)
                        .getWindow().getDecorView();
            }
            mRootView = (ViewGroup) layoutInflater.inflate(R.layout.basepickerview,
                    mPickerOptions.mDecorView,false);
            mRootView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            if (mPickerOptions.mBackgroundId != -1) {
                mRootView.setBackgroundColor(mPickerOptions.mBackgroundId);
            }
            mContainerViewGroup = mRootView.findViewById(R.id.content_container);
            mContainerViewGroup.setLayoutParams(params);

        }
    }


    protected void initAnim(){
        mAnimationIn = getInAnimation();
        mAnimationOut = getOutAnimation();
    }


    protected void initEvent() {}


    protected Animation getInAnimation() {
        int res =-1;
        switch (mAnimGravity) {
            case Gravity.BOTTOM:
                res = R.anim.pickerview_slide_in_bottom;
                break;
            case Gravity.LEFT:
                break;
            default:
                break;
        }
        return AnimationUtils.loadAnimation(mContext,res);
    }

    protected Animation getOutAnimation() {
        int animRes = -1;
        switch (mAnimGravity) {
            case Gravity.BOTTOM:
                animRes = R.anim.pickerview_slide_out_bottom;
                break;
            case Gravity.LEFT:

                break;
            default:
                break;
        }
        return AnimationUtils.loadAnimation(mContext,animRes);
    }


}
