package com.example.homework;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextClock textClock;
    private TextView textView;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //全屏显示
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("TimerDemo01");

        textView = (TextView) findViewById(R.id.textView);
        textClock = (TextClock) findViewById(R.id.textClock);

        String clockFormatter = "  HH : mm ss\n      EE            yyyy/MM/dd";
        Spannable s = new SpannableString(clockFormatter);
        s.setSpan(new RelativeSizeSpan(0.5f), 10, 11,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE); //将ss(对应秒)缩小显示
        s.setSpan(new RelativeSizeSpan(0.3f), 12, 43,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE); //将星期和日期缩小显示

        textClock.setTimeZone("GMT+8"); //设置时区
        textClock.setFormat24Hour(s);

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String hourStr = textClock.getText().toString().split(":")[0].trim();
                        int hour = Integer.parseInt(hourStr);
                        if (hour >= 0 && hour < 12) {
                            textView.setText("AM");
                        } else textView.setText("PM");
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 600);
    }
}