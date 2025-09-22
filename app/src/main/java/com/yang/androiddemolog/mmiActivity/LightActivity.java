package com.yang.androiddemolog.mmiActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

/**
 * 光线传感器
 */
public class LightActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView tvLux;
    private TextView tvBrightness;

    private static final float MIN_LUX = 10.0f;    // 环境暗
    private static final float MAX_LUX = 100.0f; // 环境亮

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        // 初始化视图
        tvLux = findViewById(R.id.tv_lux);
        tvBrightness = findViewById(R.id.tv_brightness);

        // 1.获取传感器的Manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // 2.获取光线传感器
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(lightSensor == null){
            // 设备不支持光线传感器
            tvLux.setText("设备不支持光线传感器！");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 3.注册传感器监听器
        if(lightSensor != null){
            sensorManager.registerListener(
                    this,
                    lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 4.注销监听器
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            // 获取当前光照强度
            float lux = event.values[0];

            // 更新UI
            tvLux.setText(String.format("Lux: %.2f", lux));

            // 将lux映射到屏幕亮度
            float brightness = mapLuxToBrightness(lux);
            tvBrightness.setText(String.format("Brightness: %.2f", brightness));

            // 调整窗口亮度
            adjustWindowBrightness(brightness);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private float mapLuxToBrightness(float lux) {
        float normalizedLux = (lux - MIN_LUX) / (MAX_LUX - MIN_LUX);
        return Math.max(0.0f, Math.min(1.0f, normalizedLux));
    }

    private void adjustWindowBrightness(float tvBrightness) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.screenBrightness = tvBrightness;
        window.setAttributes(attributes);
    }
}
