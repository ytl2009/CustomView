package com.ytl.customview.pickview.builder;

import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;

import com.ytl.customview.pickview.configure.PickerOptions;
import com.ytl.customview.pickview.listener.OnOptionsSelectChangedListener;
import com.ytl.customview.pickview.listener.OnOptionsSelectListener;
import com.ytl.customview.widget.view.WheelView;

/**
 * package:com.ytl.customview.pickview.builder
 * description:
 * author: ytl
 * date:18.5.24  10:46.
 */


public class PickerOptionsBuilder {
    private PickerOptions mPickerOptions;

    public PickerOptionsBuilder(Context context, OnOptionsSelectListener optionsSelectListener) {
        mPickerOptions = new PickerOptions(PickerOptions.TYPE_PICKER_OPTIONS);
        mPickerOptions.mContext = context;
        mPickerOptions.mOnOptionsSelectListener = optionsSelectListener;
    }

    public PickerOptionsBuilder setConfirmText(String confirmText) {
        mPickerOptions.mTextContentConfirm = confirmText;
        return this;
    }

    public PickerOptionsBuilder setCancelText(String cancelText) {
        mPickerOptions.mTextContentCancel = cancelText;
        return this;
    }

    public PickerOptionsBuilder setTitleText(String titleText) {
        mPickerOptions.mTextContentTitle = titleText;
        return this;
    }

    public PickerOptionsBuilder setTextColorCenter(int color) {
        mPickerOptions.mTextColorCenter = color;
        return this;
    }

    public PickerOptionsBuilder setTextOutColor(int color) {
        mPickerOptions.mTextColorOut = color;
        return this;
    }


    public PickerOptionsBuilder setDividerType(WheelView.DividerType dividerType) {
        mPickerOptions.mDividerType = dividerType;
        return this;
    }


    public PickerOptionsBuilder setDividerColor(int dividerColor) {
        mPickerOptions.mDividerColor = dividerColor;
        return this;
    }


    public PickerOptionsBuilder setLineSpacingMultiplier(int lineSpacingMultiplier) {
        mPickerOptions.mLineSpacingMultiplier = lineSpacingMultiplier;
        return this;
    }


    public PickerOptionsBuilder setDialog(boolean isDialog) {
        mPickerOptions.mIsDialog = isDialog;
        return this;
    }

    public PickerOptionsBuilder setBackground(int background) {
        mPickerOptions.mBackgroundId = background;
        return this;
    }


    public PickerOptionsBuilder setDecorView(ViewGroup decorView) {
        mPickerOptions.mDecorView = decorView;
        return this;
    }


    public PickerOptionsBuilder setLayoutRes(int layoutRes) {
        mPickerOptions.mLayoutRes = layoutRes;
        return this;
    }


    public PickerOptionsBuilder setBackgroundColor(int bgcolor) {
        mPickerOptions.mBgColorWheel = bgcolor;
        return this;
    }


    public PickerOptionsBuilder setBackgroundColorTitle(int colorTitle){
        mPickerOptions.mBgColorTitle = colorTitle;
        return this;
    }

    public PickerOptionsBuilder setTitleColor (int titleColor) {
        mPickerOptions.mTextColorTitle = titleColor;
        return this;
    }


    public PickerOptionsBuilder setTextSizeCancel(int textSizeCancel) {
        mPickerOptions.mTextSizeSubmitCancel = textSizeCancel;
        return this;
    }


    public PickerOptionsBuilder setTextSizeContent(int textSizeContent) {
        mPickerOptions.mTextSizeContent = textSizeContent;
        return this;
    }


    public PickerOptionsBuilder setTitleSize(int titleSize) {
        mPickerOptions.mTextSizeTitle = titleSize;
        return this;
    }


    public PickerOptionsBuilder setOutsideCancelable(boolean cancelable) {
        mPickerOptions.mCancelable = cancelable;
        return this;
    }


    public PickerOptionsBuilder setLabels(String label1,String label2,String label3) {
        mPickerOptions.mLabel1 = label1;
        mPickerOptions.mLabel2 = label2;
        mPickerOptions.mLabel3 = label3;
        return this;
    }


    public PickerOptionsBuilder setTypeFace(Typeface font) {
        mPickerOptions.mFont = font;
        return this;
    }


    public PickerOptionsBuilder setCyclics(boolean cyclic1,boolean cyclic2,boolean cyclic3) {
        mPickerOptions.mIsCyclic1 = cyclic1;
        mPickerOptions.mIsCyclic2 = cyclic2;
        mPickerOptions.mIsCyclic3 = cyclic3;
        return this;
    }


    public PickerOptionsBuilder setSelectOptions(int option1) {
        mPickerOptions.mOptions1 = option1;
        return this;
    }


    public PickerOptionsBuilder setSelectOptions(int option1,int option2) {
        mPickerOptions.mOptions1 = option1;
        mPickerOptions.mOptions2 = option2;
        return this;
    }


    public PickerOptionsBuilder setSelectOptions(int option1,int option2,int option3){
        mPickerOptions.mOptions1 = option1;
        mPickerOptions.mOptions2 = option2;
        mPickerOptions.mOptions3 = option3;
        return this;
    }


    public PickerOptionsBuilder setIsCenterLabel(boolean isCenterLabel) {
        mPickerOptions.mIsCenterLabel = isCenterLabel;
        return this;
    }


    public PickerOptionsBuilder setRestoreItem(boolean isRestoreItem) {
        mPickerOptions.mIsRestoreItem = isRestoreItem;
        return this;
    }


    public PickerOptionsBuilder setOnOptionSelectChangedListener(
            OnOptionsSelectChangedListener optionChangeListener) {
        mPickerOptions.mOptionsSelectChangedListener = optionChangeListener;
        return this;
    }


    public PickerOptionsBuilder setTextOffset(int xOffset1,int xOffset2,int xOffset3) {
        mPickerOptions.mXOffset1 = xOffset1;
        mPickerOptions.mXOffset2 = xOffset2;
        mPickerOptions.mXOffset3 = xOffset3;
        return this;
    }






}
