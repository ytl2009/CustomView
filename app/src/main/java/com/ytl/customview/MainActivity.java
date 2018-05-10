package com.ytl.customview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ytl.customview.widget.view.CircleProgressView;

public class MainActivity extends AppCompatActivity {
    private CircleProgressView mCircleProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCircleProgressView = (CircleProgressView) findViewById(R.id.circleProgress);
        mCircleProgressView.setTextSize(12f);
        mCircleProgressView.setBackgroundColor(Color.DKGRAY);
        mCircleProgressView.setBallColor(Color.GREEN);
        mCircleProgressView.setBorderColor(Color.BLUE);
        mCircleProgressView.setStartAngle(0);
        mCircleProgressView.setBorderWidth(4f);
        mCircleProgressView.setTextColor(Color.WHITE);
    }
}
