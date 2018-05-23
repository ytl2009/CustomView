package com.ytl.customview.pickview.adapter;

import com.ytl.customview.widget.adapter.IWheelViewAdapter;

import java.util.List;

/**
 * package:com.ytl.customview.pickview.adapter
 * description:
 * author: ytl
 * date:18.5.23  17:11.
 */


public class ArrayWheelAdapter<T> implements IWheelViewAdapter {

    private List<T> mItems;

    public ArrayWheelAdapter(List<T> items) {
        this.mItems = items;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >=0 && position<mItems.size()) {
            return mItems.get(position);
        }
        return "";
    }

    @Override
    public int indexOf(Object object) {
        return mItems.indexOf(object);
    }
}
