package com.ytl.customview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ytl.customview.widget.view.CircleProgressView;
import com.ytl.customview.widget.view.NumberProgressView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private CircleProgressView mCircleProgressView;
    private NumberProgressView mNumberProgressView;
    private int mCurrentProgress = 1;

    private ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCircleProgressView = (CircleProgressView) findViewById(R.id.circleProgress);
        mCircleProgressView.setBackgroundColor(Color.WHITE);
        mCircleProgressView.setBallColor(Color.GREEN);
        mCircleProgressView.setBorderColor(Color.BLUE);
        mCircleProgressView.setStartAngle(0);
        mCircleProgressView.setBorderWidth(8f);
        mCircleProgressView.setTextColor(Color.RED);

        mNumberProgressView = (NumberProgressView) findViewById(R.id.numberProgress);



        mExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                while (mCurrentProgress<100) {
                    mCurrentProgress +=1;
                    final int progress = mCurrentProgress;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mCircleProgressView.setProgress(progress);
                    mNumberProgressView.setProgress(progress);
                }
            }
        }, 0, 10, TimeUnit.SECONDS);

//        new Thread(new ProgressRunnable()).start();


    }

    class ProgressRunnable implements Runnable {

        @Override
        public void run() {
            while (mCurrentProgress < 100){
                mCurrentProgress +=1;
                mCircleProgressView.setProgress(mCurrentProgress);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
