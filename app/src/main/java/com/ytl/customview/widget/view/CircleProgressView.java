package com.ytl.customview.widget.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.ytl.customview.R;
import com.ytl.customview.util.Utils;

/**
 * package:com.ytl.customview.widget.view
 * description:
 * author: ytl
 * date:18.5.9  15:04.
 */


public class CircleProgressView extends View implements View.OnClickListener {

    private Context mContext;
    private Paint mTextPaint;
    private Paint mBackgroundPaint;
    private Paint mBorderPaint;
    private Paint mBallPaint;
    private Paint mProgressPaint;

    private Path mSrcPath;
    private Path mDstPath;
    private PathMeasure mPathMeasure;

    private float mRadius = 0;
    private float mTextSize = 12f;
    private int mTextColor = Color.WHITE;
    private int mBorderColor = Color.BLUE;
    private int mBackgroundColor= Color.DKGRAY;
    private int mBallColor = Color.GREEN;
    private float mBorderWidth = 4f;
    private boolean mIsBall;
    private float mBallWidth;

    private ValueAnimator mAnimator;
    private int mProgress;
    private float mCurrentValue = 0;

    private int mWidth;
    private int mHeight;

    private int mCanvasWidth;
    private int mCanvasHeight;
    private float mLength;
    private int mStartAngle = 0;
    private float[] mPos;
    private float[] mTan;
    private String mDisplayFormat = "%f%%";
    private String mDisplayText;


    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void init(Context context, @Nullable AttributeSet attrs, int defStyle) {
        mContext = context;

        TypedArray attribute = getContext().obtainStyledAttributes(attrs,
                R.styleable.CircleProgressView, defStyle, 0);
        mBackgroundColor = attribute.getColor(
                R.styleable.CircleProgressView_backgroundColor, Color.BLACK);
        mBallColor = attribute.getColor(R.styleable.CircleProgressView_ball_color, mBallColor);
        mBorderColor = attribute.getColor(R.styleable.CircleProgressView_border_color, mBorderColor);
        mTextColor = attribute.getColor(R.styleable.CircleProgressView_title_color, mTextColor);
        mTextSize = attribute.getDimension(R.styleable.CircleProgressView_title_size, Utils.dp2px(mContext,mTextSize));
        mBorderWidth = attribute.getInt(R.styleable.CircleProgressView_border_width, Utils.dp2px(mContext,mBorderWidth));
        mIsBall = attribute.getBoolean(R.styleable.CircleProgressView_isBall,true);


        mWidth = (int) attribute.getDimension(R.styleable.CircleProgressView_view_width, mWidth);
        mHeight = (int) attribute.getDimension(R.styleable.CircleProgressView_view_height, mHeight);

        mStartAngle = (attribute.getInteger(R.styleable.CircleProgressView_start_angle,mStartAngle)+270) % 360;
        mSrcPath = new Path();
        mPathMeasure = new PathMeasure();
        mDstPath = new Path();

        mPos = new float[2];
        mTan = new float[2];
        initPaint();
        setOnClickListener(this);
    }

    public void initPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mBallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBallPaint.setStyle(Paint.Style.FILL);
        mBallPaint.setColor(mBallColor);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setColor(Color.YELLOW);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCanvasHeight = h >> 1;
        mCanvasWidth = w >> 1;
        calculateRadius();

    }

    public void calculateRadius() {

        if (mIsBall) {
            mRadius = mCanvasWidth - Math.max(mBorderWidth,mBallWidth);
        }else {
            mRadius = mCanvasWidth - mBorderWidth;
        }
        mSrcPath.reset();
        mSrcPath.addCircle(0,0,mRadius, Path.Direction.CW);
        mPathMeasure.setPath(mSrcPath,false);

        mLength = mPathMeasure.getLength();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mWidth = measureSize(modeWidth, widthSize, mWidth);
        mHeight = measureSize(modeHeight, heightSize, mHeight);
        setMeasuredDimension(mWidth, mHeight);

    }

    public int measureSize(int mode, int srcsize, int dstsize) {
        int realSize;
        if (mode == MeasureSpec.AT_MOST) {
            realSize = Math.min(srcsize, dstsize);
        } else if (mode == MeasureSpec.EXACTLY) {
            realSize = srcsize;
        } else {
            realSize = dstsize;
        }
        return realSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getWidth()>>1,getHeight()>>1);
        canvas.rotate(mStartAngle);
        canvas.drawCircle(0,0,mRadius,mBackgroundPaint);
        canvas.drawPath(mSrcPath,mBackgroundPaint);

        mDstPath.reset();
        mDstPath.lineTo(0,0);

        float stop = mLength * mCurrentValue;
        mPathMeasure.getSegment(0,stop,mDstPath,true);

        canvas.drawPath(mDstPath,mProgressPaint);

        mPathMeasure.getPosTan(mCurrentValue * mLength,mPos,mTan);
        if (mIsBall) {
            canvas.drawCircle(mPos[0], mPos[1], mBorderWidth,mBallPaint);
        }
        if (mDisplayFormat.contains("%")) {

            mDisplayText = String.format(mDisplayFormat,(1 - mCurrentValue) / 1000);
        }

        if (!TextUtils.isEmpty(mDisplayText)) {
            float middle = mTextPaint.measureText(mDisplayText);
            canvas.rotate(-mStartAngle);
            canvas.drawText(mDisplayText,0 - (middle/2), 0- ((mTextPaint.descent() + mTextPaint.ascent())/2),mTextPaint);
        }

        canvas.restore();
    }

    public void initAnimator() {
        if (null != mAnimator) {
            mAnimator.setDuration(mProgress);
            return;
        }
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(mProgress);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurrentValue = (float) valueAnimator.getAnimatedValue();
                invalidate();

            }
        });

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }


    @Override
    public void onClick(View view) {

    }


    public void setTextSize(float textSize) {
        mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        mBackgroundPaint.setColor(mBackgroundColor);
        invalidate();
    }

    public void setBallColor(int ballColor) {
        mBallColor = ballColor;
        mBallPaint.setColor(mBallColor);
        invalidate();
    }

    public void setBorderWidth(float borderWidth) {
        mBorderWidth = borderWidth;
        mBorderPaint.setStrokeWidth(mBorderWidth);
        calculateRadius();
        invalidate();
    }

    public void setStartAngle(int startAngle) {
        startAngle = (startAngle + 270) % 360;
        mStartAngle = startAngle;
        invalidate();
    }
}
