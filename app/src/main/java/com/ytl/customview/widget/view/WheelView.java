package com.ytl.customview.widget.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.View;

import com.ytl.customview.R;
import com.ytl.customview.widget.adapter.IWheelViewAdapter;
import com.ytl.customview.widget.interfaces.IWheelViewDataListener;
import com.ytl.customview.widget.listener.OnItemSelectedListenter;
import com.ytl.customview.widget.listener.WheelViewGestrueListener;
import com.ytl.customview.widget.timer.InertialTimerTask;
import com.ytl.customview.widget.timer.MessageHandler;
import com.ytl.customview.widget.timer.SmoothScrollTask;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private DividerType mDividerType;

    private String mExampleString = "\u661F\u671F";

    private Context mContext;
    private Handler mHandler;
    private IWheelViewAdapter mAdapter;

    public GestureDetector mGestureDetector;
    private OnItemSelectedListenter mOnItemSelectedListenter;

    public ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    public Future<?> mFuture ;

    public boolean mIsOptions = true;
    public boolean mIsCenterLabel = true;


    private int mTextselectedInColor;
    private int mTextSelectedOutColor;
    private int mDividerLineColor;
    private float mDividerLineMultiplier = 1.6F;
    public  boolean mIsLoop = true;

    private int mGravity = Gravity.CENTER;

    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private String mLabel;// unit text of item selected

    // the selected text paint
    private TextPaint mTextPaintIn;
    // the out of text paint
    private TextPaint mTextPaintOut;
    // the divider line paint
    private Paint mPaintIndicator;

    //the first line ox of y
    private float mFirstLineY;
    //the second line ox of y
    private float mSecondLineY;
    //label position by draw
    private float mLabelTextY;

    // the total scroll y
    private int mTotalScollY;
    // the init position
    private int initPosition;

    //the selected position of item
    private int mSelectedPosition;
    // the pre-selected of item position
    private int mPreCurrentPosition;

    // the change of position
    private int mChangedOffset;

    // the visible of item count
    private int mVisibleItemCount = 11;

    // the wheelview of height
    private int mWheelViewHeight;
    // the wheelview of width
    private int mWheelViewWidth;

    // the wheelview radius
    private int mRadius;

    // the offset of selected item
    private int mOffset = 0;
    //the pre-item of y
    private float mPreviousY = 0;
    //
    private long mStartTimer = 0;

    //change the speed by modify the param
    private static final int mVelocity_Fling_Y = 5;
    private int mWidthMeasureSpec;

    private int mDrawCenterTextStart = 0;//center content draw start position
    private int mDrawOutTextStart = 0;//divider line out content draw start position

    private static final float SCALECONTENT = 0.8F;//the out of divider line content height
    private float CENTER_CONTENT_OFFSET;//offset

    private final float DEFAULT_TEXT_TRAGET_SKEWX = 0.5f;

    //the width of show text
    private float mTextWidth;
    // the height of show text
    private float mTextHeight;

    // the max width of text
    private float mMaxTextWidth = 0;
    // the max height of text
    private float mMaxTextHeight = 0;
    // the caculate of item height
    private float mItemHeight;
    // the text offset
    private int mTextOffset;
    // set font type face
    private Typeface mTypeface = Typeface.MONOSPACE;

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


    /*
    * init the wheel view
    * */
    private void initWheelView(Context context){
        this.mContext = context;
        mHandler = new MessageHandler(this);//TODO
        mGestureDetector = new GestureDetector(context,new WheelViewGestrueListener(this));
        mGestureDetector.setIsLongpressEnabled(false);
        mIsLoop = true;

        mTotalScollY = 0;
        mSelectedPosition = -1;
        initPaints();

    }


    /*
    * init the many paints
    * */
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
        mTextPaintIn.setTextSize(mTextSize);
        mTextPaintIn.setColor(mTextselectedInColor);
        mTextWidth = mTextPaintIn.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaintIn.getFontMetrics();
        mTextHeight = fontMetrics.bottom;

        mTextPaintOut.setTextSize(mTextSize);
        mTextPaintOut.setColor(mTextSelectedOutColor);

    }


    /*
    *
    * set the offset by different device
    * */
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


    /*
    * set the Line space distance
    *
    * */
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
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        remeasure();
        mWheelViewWidth += getPaddingLeft() + getPaddingRight();
        mWheelViewHeight += getPaddingBottom() + getPaddingTop();
        mWheelViewWidth = measureSize(modeWidth,widthSize,mWheelViewHeight);
        mWheelViewHeight = measureSize(modeHeight,heightSize,mWheelViewHeight);
        setMeasuredDimension(mWheelViewWidth,mWheelViewHeight);
    }


    /*
    * measure the view width and height
    *
    * */
    private void remeasure(){
        if (mAdapter == null) {
            return;
        }

        measureTextWidthAndHeight();

        int halfCircumference = (int) (mItemHeight * (mVisibleItemCount - 1));
        mWheelViewHeight = (int) (halfCircumference *2 / Math.PI);
        mRadius = (int) (halfCircumference/Math.PI);

        //计算两条横线 和 选中项画笔的基线Y位置
        mFirstLineY = (mWheelViewHeight - mItemHeight) / 2.0F;
        mSecondLineY = (mWheelViewHeight + mItemHeight) / 2.0F;
        mLabelTextY = mSecondLineY - (mItemHeight - mMaxTextHeight) / 2.0F - CENTER_CONTENT_OFFSET;

        if (mSelectedPosition == -1) {
            if (mIsLoop) {
                mSelectedPosition = (mAdapter.getItemCount()+1) / 2;
            } else {
                mSelectedPosition = 0;
            }
        }

        mPreCurrentPosition = mSelectedPosition;

    }

    /*
    * calpulate the width and height by content
    *
    * */
    private void measureTextWidthAndHeight(){
        Rect rect = new Rect();
        int length = mAdapter.getItemCount();
        for (int i =0; i< length; i++ ) {
            String text = getContentText(mAdapter.getItem(i));
            mTextPaintIn.getTextBounds(text,0,text.length(),rect);

            int textWidth = rect.width();
            if (textWidth > mMaxTextWidth) {
                mMaxTextWidth = textWidth;
            }
            mTextPaintIn.getTextBounds("\\u661F\\u671F",0,2,rect);

            int textHeight = rect.height();
            if (textHeight > mMaxTextHeight) {
                mMaxTextHeight = textHeight+2;
            }
        }

        mItemHeight = mDividerLineMultiplier * mMaxTextHeight;
    }


    /*
    * measure size of wheelView by mode
    *
    * */
    private int measureSize(int mode,int exceptSize,int resultSize){
        int measureSize = 0;

        if (mode == MeasureSpec.EXACTLY) {
            measureSize = exceptSize;
        }else if (mode == MeasureSpec.AT_MOST) {
            measureSize = Math.min(exceptSize,resultSize);
        } else {
            measureSize = resultSize;
        }

        return measureSize;

    }


    /*
    * get the text by input the item info
    *
    * */
    private String getContentText(Object item){
        if (item == null) {
            return "";
        } else if (item instanceof IWheelViewDataListener) {
            return ((IWheelViewDataListener) item).getSelectedData();
        }else if (item instanceof Integer) {
            return String.format(Locale.getDefault(), "%02d", (int) item);
        }

        return item.toString();
    }


    public void smoothScroll(ActionEvent actionEvent){
        cancelFuture();
        if (actionEvent== ActionEvent.FLING || actionEvent == ActionEvent.DRAGGLE) {
            mOffset =  (int) ((mTotalScollY % mItemHeight + mItemHeight) % mItemHeight);
            if (mOffset > mItemHeight / 2.0F) {
                mOffset = (int) (mItemHeight - mOffset);
            } else {
                mOffset = -mOffset;
            }
        }

        mFuture = mExecutorService.scheduleWithFixedDelay(new SmoothScrollTask(this,mOffset),
                0,10, TimeUnit.MILLISECONDS);

    }


    public final void scrollBy(float velocityY){
        cancelFuture();
        mFuture = mExecutorService.scheduleWithFixedDelay(new InertialTimerTask(this,velocityY),
                0,10,TimeUnit.MILLISECONDS);

    }




    public int getItemCount() {
        return mAdapter==null ? 0 : mAdapter.getItemCount();
    }


    public void setLabel(String label) {
        this.mLabel = label;
    }


    public void setIsCenterLabel(boolean isCenterLabel) {
         this.mIsCenterLabel = isCenterLabel;
    }


    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    public int getTextWidth(Paint paint,String text) {

        int width = 0;
        if (text != null && text.length()>0) {
            int length = text.length();
            float [] widths = new float[length];
            paint.getTextWidths(text,widths);
            for (int i=0;i<length;i++) {
                width += (int)Math.ceil(widths[i]);
            }
        }
        return width;
    }


    public void setOPtions(boolean isOptions) {
        this.mIsOptions = isOptions;
    }

    public void setTextselectedInColor (int textselectedInColor) {
        this.mTextselectedInColor = textselectedInColor;
        mTextPaintIn.setColor(mTextselectedInColor);
    }

    public void setTextSelectedOutColor (int textSelectedOutColor) {
        this.mTextSelectedOutColor = textSelectedOutColor;
        mTextPaintOut.setColor(mTextSelectedOutColor);
    }


    public void setTextOffset(int offset) {
        this.mTextOffset = offset;
        mTextPaintIn.setTextScaleX(1.0f);
    }

    public void setDividerLineColor(int dividerLineColor) {
        this.mDividerLineColor = dividerLineColor;
        mPaintIndicator.setColor(mDividerLineColor);
    }

    public void setDividerType(DividerType dividerType) {
        this.mDividerType = dividerType;
    }




    //if loop is true set cycle scroll or not
    public void setLoop(boolean loop) {
        mIsLoop = loop;
    }

    // set typeface of font
    public void setTypeface(Typeface typeface) {
        mTypeface = typeface;
        mTextPaintIn.setTypeface(mTypeface);
        mPaintIndicator.setTypeface(mTypeface);
    }

    public void setTextSize(float textSize) {
        if (textSize > 0.0F) {
            mTextSize = mContext.getResources().getDisplayMetrics().density * textSize;
            mTextPaintIn.setTextSize(mTextSize);
            mTextPaintOut.setTextSize(mTextSize);
        }

    }


    public void setSelectedPosition(int selectedPosition) {
        mSelectedPosition = selectedPosition;
        initPosition = selectedPosition;
        mTotalScollY = 0;
        invalidate();
    }


    public void setOnItemSelectedListenter(OnItemSelectedListenter onItemSelectedListenter) {
        mOnItemSelectedListenter = onItemSelectedListenter;
    }


    public void setAdapter(IWheelViewAdapter adapter) {
        mAdapter = adapter;
        remeasure();
        invalidate();
    }


    public IWheelViewAdapter getAdapter() {
        return mAdapter;
    }


    public int getSelectedPosition() {
        return mSelectedPosition;
    }


    public void onItemSelected() {
        if (mOnItemSelectedListenter != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOnItemSelectedListenter.onItemSelected(getSelectedPosition());
                }
            },100);
        }
    }


    public void cancelFuture(){
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAdapter == null) {
            return;
        }

        initPosition = Math.min(Math.max(0,initPosition),mAdapter.getItemCount() - 1);

        Object visibles[] = new Object[mVisibleItemCount];

        mChangedOffset = (int) (mTotalScollY / mItemHeight);

        try {
            mPreCurrentPosition = initPosition + mChangedOffset % mAdapter.getItemCount();
        } catch (ArithmeticException e) {

        }

        if (!mIsLoop) {
            if (mPreCurrentPosition < 0) {
                mPreCurrentPosition = 0;
            }
            if (mPreCurrentPosition > mAdapter.getItemCount() - 1) {
                mPreCurrentPosition = mAdapter.getItemCount() - 1;
            }
        }else {
            if (mPreCurrentPosition < 0) {
                mPreCurrentPosition = mPreCurrentPosition + mAdapter.getItemCount();
            }
            if (mPreCurrentPosition > mAdapter.getItemCount() -1) {
                mPreCurrentPosition = mPreCurrentPosition - mAdapter.getItemCount();
            }
        }

        float itemOffsetHeight = mTotalScollY % mItemHeight;

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
                mTextPaintIn);

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
