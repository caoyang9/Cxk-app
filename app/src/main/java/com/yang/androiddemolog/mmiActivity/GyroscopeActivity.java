package com.yang.androiddemolog.mmiActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class GyroscopeActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor gyroscope;

    // 显示视图
    private TextView tvX, tvY, tvZ, tvAngleX, tvAngleY, tvAngleZ, tvStatus;
    private ImageView rotationIndicator;

    // 数据处理
    private static final float ALPHA = 0.9f; // 滤波
    private float[] filteredValues = new float[3];

    // 角度积分
    private float[] angles = new float[3]; // 横滚, 俯仰, 偏航
    private long previousTimestamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        initViews();
        initSensor();
    }

    private void initViews() {
        tvX = findViewById(R.id.tv_x);
        tvY = findViewById(R.id.tv_y);
        tvZ = findViewById(R.id.tv_z);
        tvAngleX = findViewById(R.id.tv_angle_x);
        tvAngleY = findViewById(R.id.tv_angle_y);
        tvAngleZ = findViewById(R.id.tv_angle_z);
        tvStatus = findViewById(R.id.tv_status);
        rotationIndicator = findViewById(R.id.iv_rotation_indicator);
    }

    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscope == null) {
            tvStatus.setText("设备不支持陀螺仪");
            tvStatus.setTextColor(Color.RED);
        } else {
            tvStatus.setText("陀螺仪已就绪，请旋转设备");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // 1. 滤波处理
            applyFilter(event.values);

            // 2. 更新实时显示
            updateRealtimeDisplay();

            // 3. 角度积分计算
            integrateAngles(event);

            // 4. 更新视觉反馈
            updateVisualFeedback();
        }
    }

    private void applyFilter(float[] rawValues) {
        for (int i = 0; i < 3; i++) {
            filteredValues[i] = lowPass(rawValues[i], filteredValues[i]);
        }
    }

    private float lowPass(float current, float last) {
        return last + ALPHA * (current - last);
    }

    private void updateRealtimeDisplay() {
        runOnUiThread(() -> {
            // 转换为度/秒显示（更直观）
            float xDeg = (float) Math.toDegrees(filteredValues[0]);
            float yDeg = (float) Math.toDegrees(filteredValues[1]);
            float zDeg = (float) Math.toDegrees(filteredValues[2]);

            tvX.setText(String.format("X轴(横滚): %.1f °/s", xDeg));
            tvY.setText(String.format("Y轴(俯仰): %.1f °/s", yDeg));
            tvZ.setText(String.format("Z轴(偏航): %.1f °/s", zDeg));

            // 根据角速度大小改变颜色
            updateVelocityColors(xDeg, yDeg, zDeg);
        });
    }

    private void updateVelocityColors(float xDeg, float yDeg, float zDeg) {
        int colorX = getColorForVelocity(Math.abs(xDeg));
        int colorY = getColorForVelocity(Math.abs(yDeg));
        int colorZ = getColorForVelocity(Math.abs(zDeg));

        tvX.setTextColor(colorX);
        tvY.setTextColor(colorY);
        tvZ.setTextColor(colorZ);
    }

    private int getColorForVelocity(float velocity) {
        if (velocity < 10) return Color.BLACK;        // 静止
        if (velocity < 45) return Color.parseColor("#4CAF50"); // 慢速（绿色）
        if (velocity < 90) return Color.parseColor("#FF9800"); // 中速（橙色）
        return Color.RED;                             // 快速（红色）
    }

    private void integrateAngles(SensorEvent event) {
        if (previousTimestamp == 0) {
            previousTimestamp = event.timestamp;
            return;
        }

        // 计算时间间隔（纳秒→秒）
        float deltaTime = (event.timestamp - previousTimestamp) * 1.0e-9f;
        previousTimestamp = event.timestamp;

        // 简单的欧拉积分：角度 = 角速度 × 时间
        for (int i = 0; i < 3; i++) {
            angles[i] += filteredValues[i] * deltaTime;
        }
        // 更新角度显示
        updateAngleDisplay();
    }

    private void updateAngleDisplay() {
        runOnUiThread(() -> {
            // 转换为度并限制在0-360范围内
            float roll = ((float) Math.toDegrees(angles[0]) % 360 + 360) % 360;
            float pitch = ((float) Math.toDegrees(angles[1]) % 360 + 360) % 360;
            float yaw = ((float) Math.toDegrees(angles[2]) % 360 + 360) % 360;

            tvAngleX.setText(String.format("横滚角: %.1f°", roll));
            tvAngleY.setText(String.format("俯仰角: %.1f°", pitch));
            tvAngleZ.setText(String.format("偏航角: %.1f°", yaw));
        });
    }

    private void updateVisualFeedback() {
        runOnUiThread(() -> {
            // 根据偏航角（Z轴）旋转指示器
            float yawDegrees = (float) Math.toDegrees(angles[2]) % 360;
            rotationIndicator.setRotation(yawDegrees);

            // 根据旋转速度改变指示器颜色
            float maxSpeed = Math.max(
                    Math.abs(filteredValues[0]),
                    Math.max(Math.abs(filteredValues[1]), Math.abs(filteredValues[2]))
            );
            float degSpeed = (float) Math.toDegrees(maxSpeed);

            if (degSpeed > 90) {
                rotationIndicator.setColorFilter(Color.RED);
            } else if (degSpeed > 45) {
                rotationIndicator.setColorFilter(Color.parseColor("#FF9800"));
            } else if (degSpeed > 10) {
                rotationIndicator.setColorFilter(Color.parseColor("#4CAF50"));
            } else {
                rotationIndicator.setColorFilter(Color.parseColor("#666666"));
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        runOnUiThread(() -> {
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    tvStatus.setText("陀螺仪数据不可靠，请校准");
                    tvStatus.setTextColor(Color.RED);
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    tvStatus.setText("陀螺仪精度较低");
                    tvStatus.setTextColor(Color.parseColor("#FF9800"));
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    tvStatus.setText("陀螺仪精度中等");
                    tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    tvStatus.setText("陀螺仪精度高");
                    tvStatus.setTextColor(Color.parseColor("#2196F3"));
                    break;
            }
        });
    }
}
