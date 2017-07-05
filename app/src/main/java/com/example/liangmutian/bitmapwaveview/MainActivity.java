package com.example.liangmutian.bitmapwaveview;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    WaveLoadingView coustomView;
    private int progress;
    private static final int one = 0X0001;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progress++;
            switch (msg.what) {
                case one:
                    if (progress <= 100) {
                        coustomView.setCurrent(progress);
                        sendEmptyMessageDelayed(one, 200);
                        if (progress == 98) {
                            PropertyValuesHolder px = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f,1f,1.2f,1f);
                            PropertyValuesHolder py = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f,1f,1.2f,1f);
                            ObjectAnimator.ofPropertyValuesHolder(coustomView, px, py).setDuration(1000).start();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coustomView = (WaveLoadingView) findViewById(R.id.radio);
        handler.sendEmptyMessageDelayed(one, 2000);
        coustomView.setOnLoadinFinishListener(new WaveLoadingView.OnLoadinFinishListener() {
            @Override
            public void LoadingComplete() {
            }
        });
    }

}