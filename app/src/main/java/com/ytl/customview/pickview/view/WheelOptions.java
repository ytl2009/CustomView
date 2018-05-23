package com.ytl.customview.pickview.view;

import android.view.View;

import com.ytl.customview.R;
import com.ytl.customview.pickview.adapter.ArrayWheelAdapter;
import com.ytl.customview.pickview.listener.OnOptionsSelectChangedListener;
import com.ytl.customview.widget.listener.OnItemSelectedListenter;
import com.ytl.customview.widget.view.WheelView;

import java.util.List;

/**
 * package:com.ytl.customview.pickview.view
 * description:
 * author: ytl
 * date:18.5.23  13:56.
 */


public class WheelOptions<T> {

    private View mView;
    private WheelView mWheelView1;
    private WheelView mWheelView2;
    private WheelView mWheelView3;

    private List<T> mOptions1Item;
    private List<List<T>> mOptions2Item;
    private List<List<List<T>>> mOptions3Item;

    public boolean mIsLink = true;
    public boolean mIsRestoreItem = false;

    private OnItemSelectedListenter mWheelListener_Options1;
    private OnItemSelectedListenter mWheelListener_Options2;

    private OnOptionsSelectChangedListener mOptionsSelectChangedListener;

    private int mTextInColor;
    private int mTextOutColor;
    private int mDividerColor;

    private WheelView.DividerType mDividerType;

    private int lineSpacingMultiplier;

    public WheelOptions(View view, boolean isRestoreItem) {
        super();
        mView = view;
        mIsRestoreItem = isRestoreItem;
        mWheelView1 = view.findViewById(R.id.options1);
        mWheelView2 = view.findViewById(R.id.options2);
        mWheelView3 = view.findViewById(R.id.options3);
    }

    public void setPicker(List<T> options1,List<List<T>> options2,
                          List<List<List<T>>> options3) {
        mOptions1Item = options1;
        mOptions2Item = options2;
        mOptions3Item = options3;

        mWheelView1.setAdapter(new ArrayWheelAdapter(options1));
        mWheelView1.setSelectedPosition(0);

        if (mOptions2Item != null && mOptions2Item.size()>0) {
            mWheelView2.setAdapter(new ArrayWheelAdapter(mOptions2Item.get(0)));
        }

        mWheelView2.setSelectedPosition(mWheelView2.getSelectedPosition());

        if (mOptions3Item != null && mOptions3Item.size()>0) {
            mWheelView3.setAdapter(new ArrayWheelAdapter(mOptions3Item.get(0).get(0)));
        }
        mWheelView3.setSelectedPosition(mWheelView3.getSelectedPosition());

        mWheelView1.setOPtions(true);
        mWheelView2.setOPtions(true);
        mWheelView3.setOPtions(true);

        if (this.mOptions2Item == null) {
            mWheelView2.setVisibility(View.GONE);
        } else {
            mWheelView2.setVisibility(View.VISIBLE);
        }
        if (this.mOptions3Item == null) {
            mWheelView3.setVisibility(View.GONE);
        } else {
            mWheelView3.setVisibility(View.VISIBLE);
        }

        mWheelListener_Options1 = new OnItemSelectedListenter() {
            @Override
            public void onItemSelected(int position) {
                int opt2Select = 0;
                if (mOptions2Item == null) {//只有1级联动数据
                    if (mOptionsSelectChangedListener != null) {
                        mOptionsSelectChangedListener.onOptionsSelectChanged(mWheelView1.getSelectedPosition(), 0, 0);
                    }
                } else {
                    if (!mIsRestoreItem) {
                        opt2Select = mWheelView2.getSelectedPosition();//上一个opt2的选中位置
                        //新opt2的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                        opt2Select = opt2Select >= mOptions2Item.get(position).size() - 1 ? mOptions2Item.get(position).size() - 1 : opt2Select;
                    }
                    mWheelView2.setAdapter(new ArrayWheelAdapter(mOptions2Item.get(position)));
                    mWheelView2.setSelectedPosition(opt2Select);

                    if (mOptions3Item != null) {
                        mWheelListener_Options2.onItemSelected(opt2Select);
                    } else {//只有2级联动数据，滑动第1项回调
                        if (mOptionsSelectChangedListener != null) {
                            mOptionsSelectChangedListener.onOptionsSelectChanged(position, opt2Select, 0);
                        }
                    }
                }

            }
        };

        mWheelListener_Options2 = new OnItemSelectedListenter() {
            @Override
            public void onItemSelected(int position) {
                if (mOptions3Item != null) {
                    int opt1Select = mWheelView1.getSelectedPosition();
                    opt1Select = opt1Select >= mOptions3Item.size() - 1 ? mOptions3Item.size() - 1 : opt1Select;
                    position = position >= mOptions2Item.get(opt1Select).size() - 1 ? mOptions2Item.get(opt1Select).size() - 1 : position;
                    int opt3 = 0;
                    if (!mIsRestoreItem) {
                        // wv_option3.getCurrentItem() 上一个opt3的选中位置
                        //新opt3的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                        opt3 = mWheelView3.getSelectedPosition() >= mOptions3Item.get(opt1Select).get(position).size() - 1 ?
                                mOptions3Item.get(opt1Select).get(position).size() - 1 : mWheelView3.getSelectedPosition();
                    }
                    mWheelView3.setAdapter(new ArrayWheelAdapter(mOptions3Item.get(mWheelView1.getSelectedPosition()).get(position)));
                    mWheelView3.setSelectedPosition(opt3);

                    //3级联动数据实时回调
                    if (mOptionsSelectChangedListener != null) {
                        mOptionsSelectChangedListener.onOptionsSelectChanged(mWheelView1.getSelectedPosition(), position, opt3);
                    }
                } else {//只有2级联动数据，滑动第2项回调
                    if (mOptionsSelectChangedListener != null) {
                        mOptionsSelectChangedListener.onOptionsSelectChanged(mWheelView1.getSelectedPosition(), position, 0);
                    }
                }
            }
        };
        // 添加联动监听
        if (options1 != null && mIsLink) {
            mWheelView1.setOnItemSelectedListenter(mWheelListener_Options1);
        }
        if (options2 != null && mIsLink) {
            mWheelView2.setOnItemSelectedListenter(mWheelListener_Options2);
        }
        if (options3 != null && mIsLink && mOptionsSelectChangedListener != null) {
            mWheelView3.setOnItemSelectedListenter(new OnItemSelectedListenter() {
                @Override
                public void onItemSelected(int index) {
                    mOptionsSelectChangedListener.onOptionsSelectChanged(mWheelView1.getSelectedPosition(), mWheelView2.getInitPosition(), index);
                }
            });
        }


    }


    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }
}
