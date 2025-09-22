package com.yang.androiddemolog.mmiActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class ProximityActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private Window window;
    private boolean isNear = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity);

        window = getWindow();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            float maxRange = event.sensor.getMaximumRange();
            // 检测状态变化
            boolean nowNear = (distance < maxRange);

            if (nowNear != isNear) {
                isNear = nowNear;
                if (isNear) {
                    // 物体靠近：关闭屏幕
                    turnScreenOff();
                } else {
                    // 物体远离：打开屏幕
                    turnScreenOn();
                }
            }
        }
    }
    private void turnScreenOff() {
        runOnUiThread(() -> {
            // 添加屏幕变暗的效果
            WindowManager.LayoutParams params = window.getAttributes();
            params.screenBrightness = 0.0f; // 几乎完全黑暗
            window.setAttributes(params);

            // 禁用触摸（防止误触）
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            );
        });
    }

    private void turnScreenOn() {
        runOnUiThread(() -> {
            // 恢复屏幕亮度
            WindowManager.LayoutParams params = window.getAttributes();
            params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
            window.setAttributes(params);
            // 启用触摸
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
