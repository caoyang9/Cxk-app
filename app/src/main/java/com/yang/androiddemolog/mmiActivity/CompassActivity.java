package com.yang.androiddemolog.mmiActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor magnetometer;
    private Sensor accelerometer;

    // UI组件
    private ImageView compassNeedle;

    // 传感器数据
    private float[] magnetValues = new float[3];
    private float[] accelValues = new float[3];
    private boolean hasAccelData = false;
    private boolean hasMagnetData = false;

    // 方向计算
    private float currentAzimuth = 0f;
    private float previousAzimuth = 0f;

    // 滤波和平滑处理
    private static final float ALPHA = 0.2f; // 低通滤波系数
    private float[] filteredAccel = new float[3];
    private float[] filteredMagnet = new float[3];

    // 校准相关
    private boolean isCalibrated = false;
    private int calibrationCount = 0;
    private static final int CALIBRATION_THRESHOLD = 10;

    // 动画平滑处理
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable compassUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateCompassDisplay();
            handler.postDelayed(this, 50); // 20Hz更新频率
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        initViews();
        initSensors();
        startCompassUpdate();
    }

    private void initViews() {
        compassNeedle = findViewById(R.id.iv_compass_needle);
    }

    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void startCompassUpdate() {
        handler.post(compassUpdateRunnable);
    }

    private void stopCompassUpdate() {
        handler.removeCallbacks(compassUpdateRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (magnetometer != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        stopCompassUpdate();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // 应用低通滤波
                for (int i = 0; i < 3; i++) {
                    filteredAccel[i] = lowPass(event.values[i], filteredAccel[i]);
                }
                System.arraycopy(filteredAccel, 0, accelValues, 0, 3);
                hasAccelData = true;
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                // 应用低通滤波
                for (int i = 0; i < 3; i++) {
                    filteredMagnet[i] = lowPass(event.values[i], filteredMagnet[i]);
                }
                System.arraycopy(filteredMagnet, 0, magnetValues, 0, 3);
                hasMagnetData = true;
                break;
        }

        // 当两个传感器都有数据时，计算方向
        if (hasAccelData && hasMagnetData) {
            calculateDirection();
        }
    }

    private float lowPass(float current, float last) {
        return last + ALPHA * (current - last);
    }

    private void calculateDirection() {
        float[] rotationMatrix = new float[9];
        float[] inclinationMatrix = new float[9];
        float[] orientation = new float[3];

        // 计算旋转矩阵
        boolean success = SensorManager.getRotationMatrix(
                rotationMatrix, inclinationMatrix, accelValues, magnetValues);

        if (success) {
            // 获取方向数据
            SensorManager.getOrientation(rotationMatrix, orientation);

            // 获取方位角（弧度），并转换为度
            float azimuth = (float) Math.toDegrees(orientation[0]);

            // 确保方位角在0-360度范围内
            if (azimuth < 0) {
                azimuth += 360;
            }

            // 平滑处理，避免指针跳动
            currentAzimuth = smoothAzimuth(azimuth);

            // 标记为已校准（如果数据稳定）
            if (isDataStable()) {
                calibrationCount++;
                if (calibrationCount >= CALIBRATION_THRESHOLD && !isCalibrated) {
                    isCalibrated = true;
                }
            }
        }
    }

    private float smoothAzimuth(float newAzimuth) {
        // 处理0度/360度边界情况
        if (Math.abs(newAzimuth - previousAzimuth) > 180) {
            if (newAzimuth > previousAzimuth) {
                newAzimuth -= 360;
            } else {
                newAzimuth += 360;
            }
        }

        // 应用平滑滤波
        float smoothed = previousAzimuth + ALPHA * (newAzimuth - previousAzimuth);
        previousAzimuth = smoothed;

        // 确保在0-360度范围内
        if (smoothed < 0) smoothed += 360;
        if (smoothed >= 360) smoothed -= 360;

        return smoothed;
    }

    private boolean isDataStable() {
        // 检查磁场数据是否稳定（用于校准判断）
        float totalField = calculateTotalMagneticField();
        return totalField > 20 && totalField < 70; // 正常地磁场范围
    }

    private void updateCompassDisplay() {
        runOnUiThread(() -> {
            if (isCalibrated) {
                // 旋转指南针指针（负号是因为旋转方向相反）
                compassNeedle.setRotation(-currentAzimuth);

                // 更新方向信息显示
                updateDirectionInfo();
            }
        });
    }

    private void updateDirectionInfo() {
        // 获取方向名称
        String directionName = getDirectionName(currentAzimuth);
    }

    private String getDirectionName(float azimuth) {
        // 将方位角转换为方向名称
        String[] directions = {"北", "东北", "东", "东南", "南", "西南", "西", "西北"};
        int index = (int) ((azimuth + 22.5) % 360) / 45;
        return directions[index % 8];
    }

    private float calculateTotalMagneticField() {
        return (float) Math.sqrt(
                magnetValues[0] * magnetValues[0] +
                        magnetValues[1] * magnetValues[1] +
                        magnetValues[2] * magnetValues[2]
        );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCompassUpdate();
        handler.removeCallbacksAndMessages(null);
    }
}
