package com.example.liangmutian.bitmapwaveview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    CoustomView coustomView;
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
                        coustomView.setCurrent(progress, progress + "%");
                        sendEmptyMessageDelayed(one, 50);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coustomView = (CoustomView) findViewById(R.id.radio);
        handler.sendEmptyMessageDelayed(one, 2000);
    }

}