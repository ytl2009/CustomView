package com.ytl.customview.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ytl.customview.R;
import com.ytl.customview.util.Utils;
import com.ytl.customview.widget.listener.OnProgressBarListener;

/**
 * package:com.ytl.customview.widget.view
 * description:
 * author: ytl
 * date:18.5.14  10:10.
 */


public class NumberProgressView extends View {

    private int mMaxValue = 100;

    private int mCurrentValue = 0;

    private int mBgColor ;

    private int mProgressColor;

    private int mTextColor;

    private float mTextSize;

    private float mBgBarHeight;

    private float mProgressBarHeight;

    private String mFormat = "%d%%";




    private float mDrawTextWidth;

    private float mDrawTextStart;

    private float mDrawTextEnd;

    private String mCurrentText;

    private Paint mBgPaint;

    private Paint mProgressPaint;

    private Paint mTextPaint;

    private OnProgressBarListener mOnProgressBarListener;


    /**
     * For save and restore instance of progressbar.
     */

    private static final String STATE_KEY = "saved";
    private static final String TEXT_COLOR_KEY= "text_color";
    private static final String TEXT_SIZE_KEY = "text_size";
    private static final String PROGRESS_HEIGHT_KEY = "progress_height";
    private static final String PROGRESS_COLOR_KEY = "progress_color";
    private static final String PROGRESS_MAX_KEY = "progress_max";
    private static final String PROGRESS_VALUE_KEY = "progress_value";
    private static final String PROGRESS_BACKGROUND_COLOR_KEY = "progress_bg_color";
    private static final String SUFFIX_KEY = "suffix";
    private static final String PREFIX_KEY = "prefix";
    private static final String TEXT_VISIBILE_KEY = "text_visible";

    private static final int PROGRESS_TEXT_VISIBLE = 0;

    private int mWidth;
    private int mHeight;

    private RectF mReachedRectF = new RectF(0,0,0,0);
    private RectF mUnReachedRectF = new RectF(0,0,0,0);
    private float mOffset = 0;


    private enum ProgressTextVisible {
        Visible,Invisible
    }

    private int mTextVisible;


    public NumberProgressView(Context context) {
        this(context,null);
    }

    public NumberProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.styleable.Themes_numberProgressBarStyle);
    }

    public NumberProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray attribute = context.getResources().obtainAttributes(attrs,R.styleable.NumberProgressView);

        mBgBarHeight = attribute.getDimension(R.styleable.NumberProgressView_progress_bg_height,
                Utils.dp2px(context,2f));
        mBgColor = attribute.getColor(R.styleable.NumberProgressView_progress_background_color,
                mBgColor);
        mCurrentValue = attribute.getInt(R.styleable.NumberProgressView_progress_current,mCurrentValue);
        mMaxValue = attribute.getInt(R.styleable.NumberProgressView_progress_max,mMaxValue);
        mProgressBarHeight = attribute.getDimension(R.styleable.NumberProgressView_progress_height,
                Utils.dp2px(context,2.0f));
        mProgressColor = attribute.getColor(R.styleable.NumberProgressView_progress_reached_color,mProgressColor);
        mTextColor = attribute.getColor(R.styleable.NumberProgressView_progress_text_color,mTextColor);
        mTextSize = attribute.getDimension(R.styleable.NumberProgressView_progress_text_size,mTextSize);
        mTextVisible = attribute.getInt(R.styleable.NumberProgressView_progress_text_visibility,PROGRESS_TEXT_VISIBLE);
        mOffset = attribute.getDimension(R.styleable.NumberProgressView_progress_text_offset,mOffset);
        attribute.recycle();

        initPaint();

    }


    public void initPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mTextColor);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mBgColor);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setStrokeWidth(mBgBarHeight);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressBarHeight);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mWidth = getSuggestedMinimumWidth() + getPaddingLeft() +getPaddingRight();
        mHeight = getSuggestedMinimumHeight() + getPaddingBottom() + getPaddingTop();

        mWidth = measureSize(modeWidth,width,mWidth);
        mHeight = measureSize(modeHeight,height,mHeight);
        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return (int) Math.max(mTextSize,Math.max(mBgBarHeight,mProgressBarHeight));
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) mTextSize;
    }

    public int measureSize(int mode, int srcSize, int dstSize) {

        int resultSize = 0;

        if (mode == MeasureSpec.EXACTLY) {
            resultSize = srcSize;
        } else if (mode == MeasureSpec.AT_MOST) {
            resultSize = Math.min(srcSize,dstSize);
        } else {
            resultSize = dstSize;
        }
        return resultSize;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mTextVisible == PROGRESS_TEXT_VISIBLE) {
            calculateDrawRectF();
        } else {
            calculateDrawRectFWithoutProgressText();
        }

        canvas.drawRect(mUnReachedRectF,mBgPaint);
        canvas.drawRect(mReachedRectF,mProgressPaint);
        if (mTextVisible == PROGRESS_TEXT_VISIBLE) {
            canvas.drawText(mCurrentText,mDrawTextStart,mDrawTextEnd,mTextPaint);
        }

    }

    public void calculateDrawRectF(){
        mCurrentText = String.format(mFormat,getProgress()*100 / getMaxValue());
        mDrawTextWidth = mTextPaint.measureText(mCurrentText);
        if (getProgress() == 0) {
            mDrawTextStart = getPaddingLeft();
        } else {
            mReachedRectF.left = getPaddingLeft();
            mReachedRectF.top = getHeight() /2.0f - mProgressBarHeight / 2.0f;
            mReachedRectF.right = (getWidth() - getPaddingLeft()
                    - getPaddingRight()) / (getMaxValue()*1.0f) * getProgress()
                    - mOffset + getPaddingLeft();
            mReachedRectF.bottom = getHeight() / 2.0f + mProgressBarHeight /2.0f;
            mDrawTextStart = (mReachedRectF.right + mOffset);
        }

        mDrawTextEnd = (getHeight() / 2.0f) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2.0f);

        if ((mDrawTextStart + mDrawTextWidth) >= getWidth() - getPaddingRight()) {
            mDrawTextStart = getWidth() - getPaddingRight() - mDrawTextWidth;
            mReachedRectF.right = mDrawTextStart - mOffset;
        }

        float unReachedBarStart = mDrawTextStart + mDrawTextWidth + mOffset;
        if (unReachedBarStart <= getWidth() - getPaddingRight()) {
            mUnReachedRectF.left = unReachedBarStart;
            mUnReachedRectF.right = getWidth() - getPaddingRight()-10;
            mUnReachedRectF.top = getHeight() /2.0f - mBgBarHeight / 2.0f;
            mUnReachedRectF.bottom = getHeight() / 2.0f + mBgBarHeight /2.0f;
        }

    }


    private void calculateDrawRectFWithoutProgressText() {

        mReachedRectF.left = getPaddingLeft();
        mReachedRectF.top = getHeight() / 2.0f - mProgressBarHeight / 2.0f;
        mReachedRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (getMaxValue()* 1.0f) * getProgress() + getPaddingLeft();
        mReachedRectF.bottom = getHeight() / 2.0f + mProgressBarHeight / 2.0f;

        mUnReachedRectF.left = mReachedRectF.right;
        mUnReachedRectF.right = getWidth() - getPaddingRight();
        mUnReachedRectF.top = getHeight() / 2.0f - mBgBarHeight / 2.0f;
        mUnReachedRectF.bottom = getHeight() / 2.0f + mBgBarHeight / 2.0f;
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_KEY,super.onSaveInstanceState());
        bundle.putInt(PROGRESS_COLOR_KEY,mProgressColor);
        bundle.putInt(TEXT_COLOR_KEY,mTextColor);
        bundle.putFloat(PROGRESS_HEIGHT_KEY,mProgressBarHeight);
        bundle.putInt(PROGRESS_MAX_KEY,mMaxValue);
        bundle.putInt(PROGRESS_VALUE_KEY,mCurrentValue);
        bundle.putInt(PROGRESS_BACKGROUND_COLOR_KEY,mBgColor);

        return bundle;
    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            mProgressColor = ((Bundle) state).getInt(PROGRESS_COLOR_KEY);
            mTextColor = ((Bundle) state).getInt(TEXT_COLOR_KEY);
            mMaxValue = ((Bundle) state).getInt(PROGRESS_MAX_KEY);
            mCurrentValue = ((Bundle) state).getInt(PROGRESS_VALUE_KEY);
            mProgressBarHeight = ((Bundle) state).getFloat(PROGRESS_HEIGHT_KEY);
            mBgColor = ((Bundle) state).getInt(PROGRESS_BACKGROUND_COLOR_KEY);

            initPaint();
            super.onRestoreInstanceState(((Bundle) state).getParcelable(STATE_KEY));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public int getProgress() {
        return mCurrentValue;
    }

    public int getBgColor() {
        return mBgColor;
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setProgress(int currentValue) {
        if (currentValue <= getMaxValue() && currentValue>=0) {
            mCurrentValue = currentValue;
            postInvalidate();
        }
    }

    public void setBgColor(int bgColor) {
        mBgColor = bgColor;
        mBgPaint.setColor(mBgColor);
        invalidate();
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
        mProgressPaint.setColor(progressColor);
        invalidate();
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public void setBgBarHeight(float bgBarHeight) {
        mBgBarHeight = bgBarHeight;
    }

    public void setProgressBarHeight(float progressBarHeight) {
        mProgressBarHeight = progressBarHeight;
    }

    public void setFormat(String format) {
        mFormat = format;
    }
}
