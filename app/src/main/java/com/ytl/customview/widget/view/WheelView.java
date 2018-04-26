package com.ytl.customview.widget.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.View;

import com.ytl.customview.R;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * TODO: document your custom view class.
 */
public class WheelView extends View {

    public boolean mIsDebug = true;

    private static final String TAG = WheelView.class.getSimpleName();
    private float mTextSize;

    public enum DividerType{
        FILL,WRAP
    }

    public enum ActionEvent{
        CLICK,FLING,DRAGGLE
    }

    private String mExampleString = "\u661F\u671F";

    private Context mContext;
    private Handler mHandler;

    public GestureDetector mGestureDetector;

    public ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    public Future<?> mFuture ;


    private int mTextselectedInColor;
    private int mTextSelectedOutColor;
    private int mDividerLineColor;
    private float mDividerLineMultiplier = 1.6F;
    public  boolean mIsLoop = true;

    private int mGravity = Gravity.CENTER;

    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaintIn;
    private TextPaint mTextPaintOut;
    private Paint mPaintIndicator;

    private int mFirstLineY;
    private int mSecondLineY;
    private int mLabelTextY;

    private int mTotalScollY;
    private int initPosition;

    private int mSelectedPosition;
    private int mPreCurrentPosition;

    private int mChangedOffset;

    private int mVisibleItemCount = 11;

    private int mWheelViewHeight;
    private int mWheelViewWidth;

    private int mRadius;

    private int mOffset = 0;
    private float mPreviousY = 0;
    private long mStartTimer = 0;

    //change the speed by modify the param
    private static final int mVelocity_Fling_Y = 5;
    private int mWidthMeasureSpec;

    private int mDrawCenterTextStart = 0;//center content draw start position
    private int mDrawOutTextStart = 0;//divider line out content draw start position

    private static final float SCALECONTENT = 0.8F;//the out of divider line content height
    private float CENTER_CONTENT_OFFSET;//offset

    private final float DEFAULT_TEXT_TRAGET_SKEWX = 0.5f;




    private float mTextWidth;
    private float mTextHeight;

    public WheelView(Context context) {
        super(context);
        init(null, 0);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float density = displayMetrics.density;
        setCenterContentOffsetByDensity(density);

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.WheelView, defStyle, 0);

        mTextselectedInColor = a.getColor(
                R.styleable.WheelView_selected_in_text_color,0xFF2a2a2a);
        mTextSelectedOutColor = a.getColor(
                R.styleable.WheelView_selected_out_text_color,
                0xFFa8a8a8);
        mDividerLineColor = a.getColor(R.styleable.WheelView_selected_divider_line,
                0xFFd5d5d5);
        mDividerLineMultiplier = a.getFloat(R.styleable.WheelView_linespaceingMultiplier,
                mDividerLineMultiplier);

        mGravity = a.getInt(R.styleable.WheelView_gravity,Gravity.CENTER);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.

        if (a.hasValue(R.styleable.WheelView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.WheelView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        setLineSpacingMultiplier();
        initWheelView(getContext());


        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void initWheelView(Context context){
        this.mContext = context;
        mHandler = new Handler();//TODO
        mGestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener());
        mGestureDetector.setIsLongpressEnabled(false);
        mIsLoop = true;

        mTotalScollY = 0;
        mSelectedPosition = -1;
        initPaints();

    }

    private void initPaints(){
        // Set up a default TextPaint object
        mTextPaintIn = new TextPaint();
        mTextPaintIn.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaintIn.setTextAlign(Paint.Align.LEFT);
        mTextPaintIn.setColor(mTextselectedInColor);
        mTextPaintIn.setTextSize(mTextSize);

        mTextPaintOut = new TextPaint();
        mTextPaintOut.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaintOut.setColor(mTextSelectedOutColor);
        mTextPaintOut.setTextAlign(Paint.Align.LEFT);
        mTextPaintOut.setTextSize(mTextSize);

        mPaintIndicator = new Paint();
        mPaintIndicator.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaintIndicator.setColor(mDividerLineColor);

        setLayerType(LAYER_TYPE_SOFTWARE,null);

    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    private void setCenterContentOffsetByDensity(float density) {
        if (density < 1) {
            CENTER_CONTENT_OFFSET = 2.4f;
        } else if (density>=1 && density<1.5) {
            CENTER_CONTENT_OFFSET = 3.6f;
        } else if (density>=1.5 && density<2) {
            CENTER_CONTENT_OFFSET = 4.5f;
        } else if (density>=2 && density< 3) {
            CENTER_CONTENT_OFFSET = 6.0f;
        } else if (density >= 3) {
            CENTER_CONTENT_OFFSET = density * 2.5F;
        }

    }

    private void setLineSpacingMultiplier(){
        if (mDividerLineMultiplier < 1.0f) {
            mDividerLineMultiplier = 1.0f;
        } else if (mDividerLineMultiplier >4.0f) {
            mDividerLineMultiplier = 4.0f;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void remeasure(){

    }

    private void measureTextWidthAndHeight(){
        Rect rect = new Rect();
        int length;
        for (int i =0; i< length; i++ ) {
            String text = getcontentText();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
