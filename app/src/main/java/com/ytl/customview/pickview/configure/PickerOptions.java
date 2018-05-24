package com.ytl.customview.pickview.configure;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;

import com.ytl.customview.R;
import com.ytl.customview.pickview.listener.CustomListener;
import com.ytl.customview.pickview.listener.OnOptionsSelectChangedListener;
import com.ytl.customview.pickview.listener.OnOptionsSelectListener;
import com.ytl.customview.pickview.listener.OnTimeSelectChangeListener;
import com.ytl.customview.pickview.listener.OnTimeSelectListener;
import com.ytl.customview.widget.view.WheelView;

import java.util.Calendar;

/**
 * package:com.ytl.customview.pickview.configure
 * description:
 * author: ytl
 * date:18.5.23  14:54.
 */


public class PickerOptions {

    private static final int PICKER_VIEW_BTN_COLOR_NORMAL = 0xFF057dff;
    private static final int PICKER_VIEW_BG_COLOR_TITLE = 0xFFf5f5f5;
    private static final int PICKER_VIEW_COLOR_TITLE = 0xFF000000;
    private static final int PICKER_VIEW_BG_COLOR_DEFAULT = 0xFFFFFFFF;

    public static final int TYPE_PICKER_OPTIONS = 1;
    public static final int TYPE_PICKER_TIME = 2;

    public OnOptionsSelectListener mOnOptionsSelectListener;
    public OnTimeSelectListener mTimeSelectListener;

    public OnOptionsSelectChangedListener mOptionsSelectChangedListener;
    public OnTimeSelectChangeListener mTimeSelectChangeListener;

    public CustomListener mCustomListener;

    public String mLabel1,mLabel2,mLabel3;
    public int mOptions1,mOptions2,mOptions3;
    public int mXOffset1,mXOffset2,mXOffset3;

    public boolean mIsCyclic1 = false;
    public boolean mIsCyclic2 = false;
    public boolean mIsCyclic3 = false;

    public boolean mIsRestoreItem = false;

    public boolean[] type = new boolean[] {true,true,true,false,false,false};

    public Calendar mdate;
    public Calendar mStartDate;
    public Calendar mEndDate;

    public int mStartYear;
    public int mEndYear;

    public boolean mCyclic = false;
    public boolean mIsLunarCanlendar = false;

    public String mLabel_Year,mLabel_Month,mLabel_Day,
            mLabel_Hours,mLabel_Minutes,mLabel_Seconds;
    public int mXOffset_Year,mXOffset_Month,mXOffset_Day,
            mXOffset_Hours,mXOffset_Minutes,mXOffset_Seconds;


    //******* 公有字段  ******//
    public int mLayoutRes;
    public ViewGroup mDecorView;
    public int mTextGravity = Gravity.CENTER;
    public Context mContext;

    public String mTextContentConfirm;//确定按钮文字
    public String mTextContentCancel;//取消按钮文字
    public String mTextContentTitle;//标题文字

    public int mTextColorConfirm = PICKER_VIEW_BTN_COLOR_NORMAL;//确定按钮颜色
    public int mTextColorCancel = PICKER_VIEW_BTN_COLOR_NORMAL;//取消按钮颜色
    public int mTextColorTitle = PICKER_VIEW_COLOR_TITLE;//标题颜色

    public int mBgColorWheel = PICKER_VIEW_BG_COLOR_DEFAULT;//滚轮背景颜色
    public int mBgColorTitle = PICKER_VIEW_BG_COLOR_TITLE;//标题背景颜色

    public int mTextSizeSubmitCancel = 17;//确定取消按钮大小
    public int mTextSizeTitle = 18;//标题文字大小
    public int mTextSizeContent = 18;//内容文字大小

    public int mTextColorOut = 0xFFa8a8a8; //分割线以外的文字颜色
    public int mTextColorCenter = 0xFF2a2a2a; //分割线之间的文字颜色
    public int mDividerColor = 0xFFd5d5d5; //分割线的颜色
    public int mBackgroundId = -1; //显示时的外部背景色颜色,默认是灰色

    public float mLineSpacingMultiplier = 1.6f; // 条目间距倍数 默认1.6
    public boolean mIsDialog;//是否是对话框模式

    public boolean mCancelable = true;//是否能取消
    public boolean mIsCenterLabel = false;//是否只显示中间的label,默认每个item都显示
    public Typeface mFont = Typeface.MONOSPACE;//字体样式
    public WheelView.DividerType mDividerType = WheelView.DividerType.FILL;//分隔线类型

    public PickerOptions(int buildType) {
        if (buildType == TYPE_PICKER_OPTIONS) {
            mLayoutRes = R.layout.pickview_options;
        }else {
            mLayoutRes = R.layout.pickview_time;
        }
    }
}
