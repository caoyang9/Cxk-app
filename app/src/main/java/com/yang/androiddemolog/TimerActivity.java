package com.yang.androiddemolog;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * 计时器Activity
 */
public class TimerActivity extends AppCompatActivity {

    private TextView tvTimer;
    private Button btnStart, btnStop, btnReset;

    private TimerThread timerThread;

    private long startTime = 0;

    private long elapsedTime = 0;

    private boolean running = false;

    private TimerHandler timeHandler;

    private static class TimerHandler extends Handler {
        // 使用静态内部类+弱引用避免内存泄露
        private final WeakReference<TimerActivity> activityReference;

        TimerHandler(TimerActivity activity){
            super(Looper.getMainLooper());
            activityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            long threadId = Thread.currentThread().getId();
            Log.d("ThreadInfo", "当前线程ID: " + threadId);

            TimerActivity timerActivity = activityReference.get();
            if(timerActivity != null){
                timerActivity.updateTimerDisplay(msg.getData().getLong("elapsedTime"));
            }
        }
    }

    private void updateTimerDisplay(long time) {
        long threadId = Thread.currentThread().getId();
        Log.d("ThreadInfo", "当前线程ID: " + threadId);
        long milliseconds = time % 1000;
        long seconds = (time / 1000) % 60;
        long minutes = (time / (1000 * 60)) % 60;
        long hours = (time / (1000 * 60 * 60));

        String timeFormatted = String.format(Locale.getDefault(),
                "%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds);
        tvTimer.setText(timeFormatted);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        long threadId = Thread.currentThread().getId();
        Log.d("ThreadInfo", "当前线程ID: " + threadId);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        initViews();
        timeHandler = new TimerHandler(this);
    }

    private void initViews() {
        long threadId = Thread.currentThread().getId();
        Log.d("ThreadInfo", "当前线程ID: " + threadId);
        tvTimer = findViewById(R.id.tv_timer);
        btnStart = findViewById(R.id.btn_time_start);
        btnStop = findViewById(R.id.btn_time_stop);
        btnReset = findViewById(R.id.btn_time_reset);
        btnStart.setOnClickListener(view -> {
            long threadId1 = Thread.currentThread().getId();
            Log.d("ThreadInfo", "当前线程ID: " + threadId1);
            startTimer();
        });
        btnStop.setOnClickListener(view -> stopTimer());
        btnReset.setOnClickListener(view -> resetTimer());

        // 设置初始状态
        updateBtnState();
    }

    private void startTimer() {
        long threadId = Thread.currentThread().getId();
        Log.d("ThreadInfo", "当前线程ID: " + threadId);
        if(!running){
            if(elapsedTime == 0){
                startTime = System.currentTimeMillis();
            }else{
                startTime = System.currentTimeMillis() - elapsedTime;
            }
            running = true;
            timerThread = new TimerThread();
            timerThread.start();
            updateBtnState();
        }
    }

    private void stopTimer() {
        if(running){
            running = false;
            if(timerThread != null){
                timerThread.interrupt();
                timerThread = null;
            }
            updateBtnState();
        }
    }

    private void resetTimer() {
        stopTimer();
        elapsedTime = 0;
        startTime = 0;
        updateTimerDisplay(0);
        updateBtnState();
    }

    private void updateBtnState() {
        long threadId = Thread.currentThread().getId();
        Log.d("ThreadInfo", "当前线程ID: " + threadId);
        btnStart.setEnabled(!running);
        btnStop.setEnabled(running);
        btnReset.setEnabled(!running && elapsedTime > 0);
    }

    // 计时器线程
    private class TimerThread extends Thread{
        @Override
        public void run() {
            long threadId = Thread.currentThread().getId();
            Log.d("ThreadInfo", "当前线程ID: " + threadId);
            super.run();
            while(running && !isInterrupted()){
                elapsedTime = System.currentTimeMillis() - startTime;

                // 使用Handler发送消息到主线程更新UI
                Message message = timeHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putLong("elapsedTime", elapsedTime);
                message.setData(bundle);
                timeHandler.sendMessage(message);
                try {
                    Thread.sleep(50); // 每50毫秒更新一次，实现毫秒级显示
                } catch (InterruptedException e) {
                    interrupt();
                    break;
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        // 移除所有未处理的消息，防止内存泄漏
        if (timeHandler != null) {
            timeHandler.removeCallbacksAndMessages(null);
        }
    }
}
