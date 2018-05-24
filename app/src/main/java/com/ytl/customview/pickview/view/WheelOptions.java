package com.ytl.customview.pickview.view;

import android.graphics.Typeface;
import android.text.TextUtils;
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

    private float mLineSpacingMultiplier;

    public WheelOptions(View view, boolean isRestoreItem) {
        super();
        mView = view;
        mIsRestoreItem = isRestoreItem;
        mWheelView1 = view.findViewById(R.id.options1);
        mWheelView2 = view.findViewById(R.id.options2);
        mWheelView3 = view.findViewById(R.id.options3);
    }

    public void setPicker(List<T> options1, List<List<T>> options2,
                          List<List<List<T>>> options3) {
        mOptions1Item = options1;
        mOptions2Item = options2;
        mOptions3Item = options3;

        mWheelView1.setAdapter(new ArrayWheelAdapter(options1));
        mWheelView1.setSelectedPosition(0);

        if (mOptions2Item != null && mOptions2Item.size() > 0) {
            mWheelView2.setAdapter(new ArrayWheelAdapter(mOptions2Item.get(0)));
        }

        mWheelView2.setSelectedPosition(mWheelView2.getSelectedPosition());

        if (mOptions3Item != null && mOptions3Item.size() > 0) {
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


    public void setNPicker(List<T> options1, List<List<T>> options2,
                           List<List<List<T>>> options3) {
        mOptions1Item = options1;
        mOptions2Item = options2;
        mOptions3Item = options3;

        mWheelView1.setAdapter(new ArrayWheelAdapter(mOptions1Item));
        if (mOptions1Item != null && mOptions1Item.size() > 0) {
            mWheelView1.setSelectedPosition(0);
        }

        if (mOptions2Item != null && mOptions2Item.size() > 0) {
            mWheelView2.setAdapter(new ArrayWheelAdapter(mOptions2Item.get(0)));
        }

        mWheelView2.setSelectedPosition(mWheelView2.getSelectedPosition());

        if (mOptions3Item != null && mOptions3Item.size() > 0) {
            mWheelView3.setAdapter(new ArrayWheelAdapter(mOptions3Item.get(0).get(0)));
        }

        mWheelView3.setSelectedPosition(mWheelView3.getSelectedPosition());

        mWheelView1.setOPtions(true);
        mWheelView2.setOPtions(true);
        mWheelView3.setOPtions(true);

        if (mOptionsSelectChangedListener != null) {
            mWheelView1.setOnItemSelectedListenter(new OnItemSelectedListenter() {
                @Override
                public void onItemSelected(int position) {
                    mOptionsSelectChangedListener.onOptionsSelectChanged(position,
                            mWheelView2.getSelectedPosition(), mWheelView3.getSelectedPosition());
                }
            });
        }

        if (mOptions2Item == null) {
            mWheelView2.setVisibility(View.GONE);
        } else {
            mWheelView2.setVisibility(View.VISIBLE);
            if (mOptionsSelectChangedListener != null) {
                mWheelView2.setOnItemSelectedListenter(new OnItemSelectedListenter() {
                    @Override
                    public void onItemSelected(int position) {
                        mOptionsSelectChangedListener.onOptionsSelectChanged(mWheelView1.getSelectedPosition(),
                                position, mWheelView3.getSelectedPosition());
                    }
                });
            }
        }

        if (mOptions3Item == null) {
            mWheelView3.setVisibility(View.GONE);
        } else {
            if (mOptionsSelectChangedListener != null) {
                mWheelView3.setOnItemSelectedListenter(new OnItemSelectedListenter() {
                    @Override
                    public void onItemSelected(int position) {
                        mOptionsSelectChangedListener.onOptionsSelectChanged(mWheelView1.getSelectedPosition(),
                                mWheelView2.getSelectedPosition(), position);
                    }
                });
            }
        }

    }


    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }

    public void setTextContentSize(int textSize) {
        mWheelView1.setTextSize(textSize);
        mWheelView2.setTextSize(textSize);
        mWheelView3.setTextSize(textSize);
    }

    public void setTextInColor() {
        mWheelView1.setTextselectedInColor(mTextInColor);
        mWheelView2.setTextselectedInColor(mTextInColor);
        mWheelView3.setTextselectedInColor(mTextInColor);
    }

    public void setTextOutColor() {
        mWheelView1.setTextSelectedOutColor(mTextOutColor);
        mWheelView2.setTextSelectedOutColor(mTextOutColor);
        mWheelView3.setTextSelectedOutColor(mTextOutColor);
    }

    public void setDividerColor() {
        mWheelView1.setDividerLineColor(mDividerColor);
        mWheelView2.setDividerLineColor(mDividerColor);
        mWheelView3.setDividerLineColor(mDividerColor);
    }


    public void setDividerType() {
        mWheelView1.setDividerType(mDividerType);
        mWheelView2.setDividerType(mDividerType);
        mWheelView3.setDividerType(mDividerType);
    }


    public void setLineSpacingMultiplier() {
        mWheelView1.setDividerLineMultiplier(mLineSpacingMultiplier);
        mWheelView2.setDividerLineMultiplier(mLineSpacingMultiplier);
        mWheelView3.setDividerLineMultiplier(mLineSpacingMultiplier);
    }


    public void setLabels(String label1, String label2, String label3) {
        if (label1 != null && TextUtils.isEmpty(label1)) {
            mWheelView1.setLabel(label1);
        }
        if (label2 != null && TextUtils.isEmpty(label2)) {
            mWheelView2.setLabel(label2);
        }
        if (label2 != null && TextUtils.isEmpty(label2)) {
            mWheelView3.setLabel(label3);
        }

    }


    public void setTextOffset(int xOffset1, int xOffset2, int xOffset3) {
        mWheelView2.setTextOffset(xOffset1);
        mWheelView2.setTextOffset(xOffset2);
        mWheelView3.setTextOffset(xOffset3);
    }


    public void setCyclic(boolean iscyclic) {
        mWheelView1.setLoop(iscyclic);
        mWheelView2.setLoop(iscyclic);
        mWheelView3.setLoop(iscyclic);
    }


    public void setTypeface(Typeface font) {
        mWheelView1.setTypeface(font);
        mWheelView2.setTypeface(font);
        mWheelView3.setTypeface(font);
    }


    public void setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3) {
        mWheelView1.setLoop(cyclic1);
        mWheelView2.setLoop(cyclic2);
        mWheelView3.setLoop(cyclic3);
    }


    public int[] getCurrentItems() {
        int[] currentItems = new int[3];
        currentItems[0] = mWheelView1.getSelectedPosition();
        if (mOptions2Item != null && mOptions2Item.size() > 0) {
            currentItems[1] = mWheelView2.getSelectedPosition() > (mOptions2Item.get(currentItems[0])
                    .size() - 1) ? 0 : mWheelView2.getSelectedPosition();
        } else {
            currentItems[1] = mWheelView2.getSelectedPosition();
        }

        if (mOptions3Item != null && mOptions3Item.size() > 0) {
            currentItems[2] = mWheelView3.getSelectedPosition() > (mOptions3Item.get(currentItems[0])
                    .get(currentItems[1]).size() - 1) ? 0 : mWheelView3.getSelectedPosition();
        } else {
            currentItems[2] = mWheelView3.getSelectedPosition();
        }
        return currentItems;

    }

    public void setCurrentItems(int option1,int option2,int option3) {
        if (mIsLink) {

        } else {
            mWheelView1.setSelectedPosition(option1);
            mWheelView2.setSelectedPosition(option2);
            mWheelView3.setSelectedPosition(option3);
        }
    }

    public void onItemSelect(int option1,int option2,int option3) {
        if (mOptions1Item != null) {
            mWheelView1.setSelectedPosition(option1);
        }

        if (mOptions2Item != null) {
            mWheelView2.setAdapter(new ArrayWheelAdapter(mOptions2Item.get(option1)));
            mWheelView2.setSelectedPosition(option2);
        }

        if (mOptions3Item != null) {
            mWheelView3.setAdapter(new ArrayWheelAdapter(mOptions3Item.get(option1)
                    .get(option2)));
            mWheelView3.setSelectedPosition(option3);
        }
    }


    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        this.mLineSpacingMultiplier = lineSpacingMultiplier;
        setLineSpacingMultiplier();
    }


    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
        setDividerColor();
    }

    public void setDividerType(WheelView.DividerType dividerType) {
        this.mDividerType = dividerType;
        setDividerType();
    }


    public void setTextInColor(int textInColor) {
        this.mTextInColor = textInColor;
        setTextInColor();
    }


    public void setTextOutColor(int textOutColor) {
        this.mTextOutColor = textOutColor;
        setTextOutColor();
    }


    public void isCenterLabel(boolean isCenterLabel) {
        mWheelView1.setIsCenterLabel(isCenterLabel);
        mWheelView2.setIsCenterLabel(isCenterLabel);
        mWheelView3.setIsCenterLabel(isCenterLabel);
    }

    public void setOptionsSelectChangedListener(OnOptionsSelectChangedListener listener) {
        this.mOptionsSelectChangedListener = listener;
    }


    public void setLink(boolean isLink) {
        this.mIsLink = isLink;
    }


}
