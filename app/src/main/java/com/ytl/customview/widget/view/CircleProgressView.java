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
import android.util.AttributeSet;
import android.view.View;

import com.ytl.customview.R;

/**
 * package:com.ytl.customview.widget.view
 * description:
 * author: ytl
 * date:18.5.9  15:04.
 */


public class CircleProgressView extends View implements View.OnClickListener {

    private Context mContext;
    private Paint mTextPain;
    private Paint mBackgroundPaint;
    private Paint mBorderPaint;
    private Paint mBallPaint;

    private Path mSrcPath;
    private Path mDstPath;
    private PathMeasure mPathMeasure;

    private int mRadius = 0;
    private float mTextSize = 0;
    private int mTextColor;
    private int mBorderColor;
    private int mBackgroundColor;
    private int mBallColor;
    private float mBorderWidth;

    private ValueAnimator mAnimator;
    private int mProgress;
    private float mCurrentValue;

    private int mWidth;
    private int mHeight;


    public CircleProgressView(Context context) {
        this(context,null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    public void init(Context context,@Nullable AttributeSet attrs,int defStyle) {
        mContext = context;

        TypedArray attribute = getContext().obtainStyledAttributes(attrs,
                R.styleable.CircleProgressView,defStyle,0);
        mBackgroundColor = attribute.getColor(
                R.styleable.CircleProgressView_backgroundColor, Color.WHITE);
        mBallColor = attribute.getColor(R.styleable.CircleProgressView_ball_color,Color.BLUE);
        mBorderColor = attribute.getColor(R.styleable.CircleProgressView_border_color,Color.GREEN);
        mTextColor = attribute.getColor(R.styleable.CircleProgressView_title_color,Color.BLACK);
        mTextSize = attribute.getDimension(R.styleable.CircleProgressView_title_size,12);
        mBorderWidth = attribute.getInt(R.styleable.CircleProgressView_border_width,2);

        mWidth = (int) attribute.getDimension(R.styleable.CircleProgressView_width,40);
        mHeight = (int) attribute.getDimension(R.styleable.CircleProgressView_height,40);

        mSrcPath = new Path();
        mPathMeasure = new PathMeasure();
        mDstPath = new Path();

        initPaint();
        setOnClickListener(this);
    }

    public void initPaint(){
        mTextPain = new Paint();
        mTextPain.setAntiAlias(true);
        mTextPain.setColor(mTextColor);
        mTextPain.setStyle(Paint.Style.FILL);
        mTextPain.setTextSize(mTextSize);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);

        mBallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBallPaint.setStyle(Paint.Style.FILL);
        mBallPaint.setColor(mBallColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mWidth = measureSize(modeWidth,widthSize,mWidth);
        mHeight = measureSize(modeHeight,heightSize,mHeight);
        setMeasuredDimension(mWidth,mHeight);

    }

    public int measureSize(int mode,int srcsize,int dstsize) {
        int realSize;
        if (mode == MeasureSpec.AT_MOST) {
            realSize = Math.min(srcsize,dstsize);
        }else if (mode == MeasureSpec.EXACTLY) {
            realSize = srcsize;
        }else {
            realSize = dstsize;
        }
        return realSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void initAnimator(){
        if (null != mAnimator) {
            mAnimator.setDuration(mProgress);
            return;
        }
        mAnimator = ValueAnimator.ofFloat(0,1);
        mAnimator.setDuration(mProgress);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurrentValue = (float)valueAnimator.getAnimatedValue();
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
}
