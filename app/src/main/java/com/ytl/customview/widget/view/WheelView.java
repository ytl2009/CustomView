package com.ytl.customview.widget.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
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

import static android.R.attr.radius;

/**
 * package:com.ytl.customview.widget.timer
 * description:
 * author: ytl
 * date:18.5.12  8:48.
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
    private float mTotalScrollY;
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
        a.recycle();

        setLineSpacingMultiplier();
        initWheelView(getContext());
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

        mTotalScrollY = 0;
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
    * calculate the width and height by content
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
            mOffset =  (int) ((mTotalScrollY % mItemHeight + mItemHeight) % mItemHeight);
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
        mTotalScrollY = 0;
        invalidate();
    }

    public boolean isOptions() {
        return mIsOptions;
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

    public boolean isLoop() {
        return mIsLoop;
    }

    public float getTotalScrollY() {
        return mTotalScrollY;
    }


    public void setTotalScrollY(float totalScollY) {
        mTotalScrollY = totalScollY;
    }

    public float getItemHeight() {
        return mItemHeight;
    }


    public void setItemHeight(float itemHeight) {
        mItemHeight = itemHeight;
    }

    public int getInitPosition() {
        return initPosition;
    }


    public void setInitPosition(int initPosition) {
        this.initPosition = initPosition;
    }

    @Override
    public Handler getHandler() {
        return mHandler;
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

        mChangedOffset = (int) (mTotalScrollY / mItemHeight);

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

        float itemOffsetHeight = mTotalScrollY % mItemHeight;

        int counter = 0;
        while (counter < mVisibleItemCount) {

            int index = mPreCurrentPosition - (mVisibleItemCount / 2 - counter);//索引值，即当前在控件中间的item看作数据源的中间，计算出相对源数据源的index值
            if (mIsLoop) { //判断是否循环，如果是循环数据源也使用相对循环的position获取对应的item值，如果不是循环则超出数据源范围使用""空白字符串填充，在界面上形成空白无数据的item项

                index = getLoopMappingIndex(index);
                visibles[counter] = mAdapter.getItem(index);
            } else if (index < 0) {
                visibles[counter] = "";
            } else if (index > mAdapter.getItemCount() - 1) {
                visibles[counter] = "";
            } else {
                visibles[counter] = mAdapter.getItem(index);
            }

            counter ++;
        }

        //绘制中间两条横线
        if (mDividerType == DividerType.WRAP) {
            float startX,endX;

            if (TextUtils.isEmpty(mLabel)) {//无label
                startX = (mWheelViewWidth - mMaxTextWidth) / 2 - 12;
            } else {
                startX = (mWheelViewWidth - mMaxTextWidth) / 4 - 12;
            }

            if (startX <= 0) {//超出宽度
                startX = 10;
            }
            endX = mWheelViewWidth - startX;
            canvas.drawLine(startX,mFirstLineY,endX,mFirstLineY,mPaintIndicator);
            canvas.drawLine(startX,mSecondLineY,endX,mSecondLineY,mPaintIndicator);
        } else {
            canvas.drawLine(0,mFirstLineY,mWheelViewWidth,mFirstLineY,mPaintIndicator);
            canvas.drawLine(0,mSecondLineY,mWheelViewWidth,mSecondLineY,mPaintIndicator);
        }

        if (!TextUtils.isEmpty(mLabel) && mIsCenterLabel) {
            int labelStart = mWheelViewWidth - getTextWidth(mTextPaintIn,mLabel);
            canvas.drawText(mLabel,labelStart - CENTER_CONTENT_OFFSET,mLabelTextY,mTextPaintIn);
        }

        counter = 0;

        while (counter < mVisibleItemCount) {
            canvas.save();
            // 弧长 L = itemHeight * counter - itemHeightOffset
            // 求弧度 α = L / r  (弧长/半径) [0,π]
            double radian = ((mItemHeight * counter - itemOffsetHeight)) / radius;
            // 弧度转换成角度(把半圆以Y轴为轴心向右转90度，使其处于第一象限及第四象限
            // angle [-90°,90°]
            float angle = (float) (90D - (radian / Math.PI) * 180D);//item第一项,从90度开始，逐渐递减到 -90度

            // 计算取值可能有细微偏差，保证负90°到90°以外的不绘制
            if (angle >= 90F || angle <= -90F) {
                canvas.restore();
            } else {
                // 根据当前角度计算出偏差系数，用以在绘制时控制文字的 水平移动 透明度 倾斜程度
                float offsetCoefficient = (float) Math.pow(Math.abs(angle) / 90f, 2.2);
                //获取内容文字
                String contentText;

                //如果是label每项都显示的模式，并且item内容不为空、label 也不为空
                if (!mIsCenterLabel && !TextUtils.isEmpty(mLabel) && !TextUtils.isEmpty(getContentText(visibles[counter]))) {
                    contentText = getContentText(visibles[counter]) + mLabel;
                } else {
                    contentText = getContentText(visibles[counter]);
                }

                reMeasureTextSize(contentText);
                //计算开始绘制的位置
                measuredCenterContentStart(contentText);
                measuredOutContentStart(contentText);
                float translateY = (float) (radius - Math.cos(radian) * radius - (Math.sin(radian) * mMaxTextHeight) / 2D);
                //根据Math.sin(radian)来更改canvas坐标系原点，然后缩放画布，使得文字高度进行缩放，形成弧形3d视觉差
                canvas.translate(0.0F, translateY);

                if (translateY <= mFirstLineY && mMaxTextHeight + translateY >= mFirstLineY) {
                    // 条目经过第一条线
                    canvas.save();
                    canvas.clipRect(0, 0, mWheelViewWidth, mFirstLineY - translateY);
                    canvas.scale(1.0F, (float) Math.sin(radian) * SCALECONTENT);
                    canvas.drawText(contentText, mDrawOutTextStart, mMaxTextHeight, mTextPaintOut);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, mFirstLineY - translateY, mWheelViewWidth, (int) (mItemHeight));
                    canvas.scale(1.0F, (float) Math.sin(radian) * 1.0F);
                    canvas.drawText(contentText, mDrawCenterTextStart, mMaxTextHeight - CENTER_CONTENT_OFFSET, mTextPaintIn);
                    canvas.restore();
                } else if (translateY <= mSecondLineY && mMaxTextHeight + translateY >= mSecondLineY) {
                    // 条目经过第二条线
                    canvas.save();
                    canvas.clipRect(0, 0, mWheelViewWidth, mSecondLineY - translateY);
                    canvas.scale(1.0F, (float) Math.sin(radian) * 1.0F);
                    canvas.drawText(contentText, mDrawCenterTextStart, mMaxTextHeight - CENTER_CONTENT_OFFSET, mTextPaintIn);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, mSecondLineY - translateY, mWheelViewWidth, (int) (mItemHeight));
                    canvas.scale(1.0F, (float) Math.sin(radian) * SCALECONTENT);
                    canvas.drawText(contentText, mDrawOutTextStart, mMaxTextHeight, mTextPaintOut);
                    canvas.restore();
                } else if (translateY >= mFirstLineY && mMaxTextHeight + translateY <= mSecondLineY) {
                    // 中间条目
                    canvas.clipRect(0, 0, mWheelViewWidth, mMaxTextHeight);
                    //让文字居中
                    float Y = mMaxTextHeight - CENTER_CONTENT_OFFSET;//因为圆弧角换算的向下取值，导致角度稍微有点偏差，加上画笔的基线会偏上，因此需要偏移量修正一下
                    canvas.drawText(contentText, mDrawCenterTextStart, Y, mTextPaintIn);

                    //设置选中项
                    mSelectedPosition = mPreCurrentPosition - (mVisibleItemCount / 2 - counter);
                } else {
                    // 其他条目
                    canvas.save();
                    canvas.clipRect(0, 0, mWheelViewWidth, (int) (mItemHeight));
                    canvas.scale(1.0F, (float) Math.sin(radian) * SCALECONTENT);
                    // 控制文字倾斜角度
                    mTextPaintOut.setTextSkewX((mTextOffset == 0 ? 0 : (mTextOffset > 0 ? 1 : -1)) * (angle > 0 ? -1 : 1) * DEFAULT_TEXT_TRAGET_SKEWX * offsetCoefficient);
                    // 控制透明度
                    mTextPaintOut.setAlpha((int) ((1 - offsetCoefficient) * 255));
                    // 控制文字水平偏移距离
                    canvas.drawText(contentText, mDrawOutTextStart + mTextOffset * offsetCoefficient, mMaxTextHeight, mTextPaintOut);
                    canvas.restore();
                }
                canvas.restore();
                mTextPaintIn.setTextSize(mTextSize);
            }
            counter++;
        }

    }

    /**
     * reset the size of the text Let it can fully display
     *
     * @param content item text content.
     */
    private void reMeasureTextSize(String content) {
        Rect rect = new Rect();
        mTextPaintIn.getTextBounds(content,0,content.length(),rect);
        int width = rect.width();
        int size = (int) mTextSize;
        while (width > mWheelViewWidth) {
            size --;
            mTextPaintIn.setTextSize(size);
            mTextPaintIn.getTextBounds(content,0,content.length(),rect);
            width = rect.width();
        }
        mTextPaintOut.setTextSize(size);
    }


    private void measuredCenterContentStart(String content) {
        Rect rect = new Rect();
        mTextPaintIn.getTextBounds(content, 0, content.length(), rect);
        switch (mGravity) {
            case Gravity.CENTER://显示内容居中
                if (mIsOptions || mLabel == null || mLabel.equals("") || !mIsCenterLabel) {
                    mDrawCenterTextStart = (int) ((mWheelViewWidth - rect.width()) * 0.5);
                } else {//只显示中间label时，时间选择器内容偏左一点，留出空间绘制单位标签
                    mDrawCenterTextStart = (int) ((mWheelViewWidth - rect.width()) * 0.25);
                }
                break;
            case Gravity.LEFT:
                mDrawCenterTextStart = 0;
                break;
            case Gravity.RIGHT://添加偏移量
                mDrawCenterTextStart = mWheelViewWidth - rect.width() - (int) CENTER_CONTENT_OFFSET;
                break;
        }
    }


    private void measuredOutContentStart(String content) {
        Rect rect = new Rect();
        mTextPaintOut.getTextBounds(content,0,content.length(),rect);
        switch (mGravity) {
            case Gravity.CENTER:
                if (mIsOptions || mLabel == null || mLabel == "" || !mIsCenterLabel) {
                    mDrawOutTextStart = (int) ((mWheelViewWidth - rect.width()) * 0.5);
                } else {
                    mDrawOutTextStart = (int) ((mWheelViewWidth - rect.width()) * 0.25);
                }
                break;
            case Gravity.LEFT:
                mDrawOutTextStart = 0;
                break;
            case Gravity.RIGHT:
                mDrawOutTextStart = (int) (mWheelViewWidth - rect.width() - CENTER_CONTENT_OFFSET);
                break;
        }

    }




    //递归计算出对应的index
    private int getLoopMappingIndex(int index) {
        if (index < 0) {
            index = index + mAdapter.getItemCount();
            index = getLoopMappingIndex(index);
        } else if (index > mAdapter.getItemCount() - 1) {
            index = index - mAdapter.getItemCount();
            index = getLoopMappingIndex(index);
        }
        return index;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = mGestureDetector.onTouchEvent(event);
        boolean isIgnore = false;

        float top = - initPosition * mItemHeight;
        float bottom = (mAdapter.getItemCount() - 1 - initPosition) * mItemHeight;
        float ratio = 0.25f;

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartTimer = System.currentTimeMillis();
                cancelFuture();
                mPreviousY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_MOVE:
                float dy = mPreviousY - event.getRawY();
                mPreviousY = event.getRawY();
                mTotalScrollY = mTotalScrollY + dy;

                // 非循环模式下，边界处理。
                if (!mIsLoop) {
                    if ((mTotalScrollY - mItemHeight *  ratio< top && dy < 0)
                            || (mTotalScrollY + mItemHeight * ratio > bottom && dy > 0)) {
                        //快滑动到边界了，设置已滑动到边界的标志
                        mTotalScrollY -= dy;
                        isIgnore = true;
                    }/* else if (totalScrollY + itemHeight * ratio > bottom && dy > 0) {
                        totalScrollY -= dy;
                        isIgnore = true;
                    } */else {
                        isIgnore = false;
                    }
                }
                break;
            default:
                if (!eventConsumed) {//未消费掉事件

                    /**
                     *@describe <关于弧长的计算>
                     *
                     * 弧长公式： L = α*R
                     * 反余弦公式：arccos(cosα) = α
                     * 由于之前是有顺时针偏移90度，
                     * 所以实际弧度范围α2的值 ：α2 = π/2-α    （α=[0,π] α2 = [-π/2,π/2]）
                     * 根据正弦余弦转换公式 cosα = sin(π/2-α)
                     * 代入，得： cosα = sin(π/2-α) = sinα2 = (R - y) / R
                     * 所以弧长 L = arccos(cosα)*R = arccos((R - y) / R)*R
                     */

                    float y = event.getY();
                    double L = Math.acos((radius - y) / radius) * radius;
                    //item0 有一半是在不可见区域，所以需要加上 itemHeight / 2
                    int circlePosition = (int) ((L + mItemHeight / 2) / mItemHeight);
                    float extraOffset = (mTotalScrollY % mItemHeight + mItemHeight) % mItemHeight;
                    //已滑动的弧长值
                    mOffset = (int) ((circlePosition - mVisibleItemCount / 2) * mItemHeight - extraOffset);

                    if ((System.currentTimeMillis() - mStartTimer) > 120) {
                        // 处理拖拽事件
                        smoothScroll(ActionEvent.DRAGGLE);
                    } else {
                        // 处理条目点击事件
                        smoothScroll(ActionEvent.CLICK);
                    }
                }

                break;

        }
        if (!isIgnore && event.getAction() != MotionEvent.ACTION_DOWN) {
            invalidate();
        }

        return true;
    }
}
