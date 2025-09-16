package com.yang.androiddemolog.mmiActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class VibratorActivity extends AppCompatActivity {
    private Vibrator vibrator;
    private Button btnStart, btnStop;
    private TextView tvDuration;

    private int vibrationDuration = 500; // 默认振动时长500ms
    private boolean isVibrating = false;
    private static final int MIN_DURATION = 100; // 最小振动时长100ms
    private static final int MAX_DURATION = 10000; // 最大振动时长2000ms
    private static final int DURATION_STEP = 100; // 每次调整的步长100ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibration);

        // 初始化视图
        initViews();

        // 获取振动器服务
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // 检查设备是否支持振动
        if (vibrator == null || !vibrator.hasVibrator()) {
            Toast.makeText(this, "设备不支持振动功能", Toast.LENGTH_SHORT).show();
            btnStart.setEnabled(false);
            btnStop.setEnabled(false);
        }
    }

    private void initViews() {
        btnStart = findViewById(R.id.btn_start_vibration);
        btnStop = findViewById(R.id.btn_stop_vibration);
        tvDuration = findViewById(R.id.tv_vibration_duration);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVibration();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopVibration();
            }
        });

        // 初始状态
        updateDurationDisplay();
        btnStop.setEnabled(false);
    }

    private void startVibration() {
        if (vibrator == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect effect = VibrationEffect.createOneShot(
                    vibrationDuration,
                    VibrationEffect.DEFAULT_AMPLITUDE
            );
            vibrator.vibrate(effect);
        } else {
            // 兼容旧版本
            vibrator.vibrate(vibrationDuration);
        }

        isVibrating = true;
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);

        Toast.makeText(this, "振动已启动", Toast.LENGTH_SHORT).show();
    }

    private void stopVibration() {
        if (vibrator != null) {
            vibrator.cancel();
        }

        isVibrating = false;
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        Toast.makeText(this, "振动已停止", Toast.LENGTH_SHORT).show();
    }

    private void updateDurationDisplay() {
        tvDuration.setText("振动时长: " + vibrationDuration + "ms");
    }

    private void increaseDuration() {
        if (vibrationDuration < MAX_DURATION) {
            vibrationDuration += DURATION_STEP;
            if (vibrationDuration > MAX_DURATION) {
                vibrationDuration = MAX_DURATION;
            }
            updateDurationDisplay();

            // 如果正在振动，重新启动振动以应用新时长
            if (isVibrating) {
                stopVibration();
                startVibration();
            }
        } else {
            Toast.makeText(this, "已达到最大振动时长", Toast.LENGTH_SHORT).show();
        }
    }

    private void decreaseDuration() {
        if (vibrationDuration > MIN_DURATION) {
            vibrationDuration -= DURATION_STEP;
            if (vibrationDuration < MIN_DURATION) {
                vibrationDuration = MIN_DURATION;
            }
            updateDurationDisplay();

            // 如果正在振动，重新启动振动以应用新时长
            if (isVibrating) {
                stopVibration();
                startVibration();
            }
        } else {
            Toast.makeText(this, "已达到最小振动时长", Toast.LENGTH_SHORT).show();
        }
    }

    // 重写按键事件处理，监听音量键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                // 增加振动时长
                increaseDuration();
                return true; // 消费该事件，防止默认音量调整

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                // 减少振动时长
                decreaseDuration();
                return true; // 消费该事件，防止默认音量调整

            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 当Activity不可见时停止振动
        stopVibration();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放资源
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
