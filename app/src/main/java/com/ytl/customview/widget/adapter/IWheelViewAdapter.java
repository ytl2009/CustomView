package com.ytl.customview.widget.adapter;

/**
 * package:com.ytl.customview.widget.adapter
 * description:
 * author: ytl
 * date:18.4.27  9:24.
 */


public interface IWheelViewAdapter<T> {

    int getItemCount();

    T getItem(int position);

    int indexOf(T object);

}
