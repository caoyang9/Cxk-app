package com.yang.androiddemolog.mmiActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class MagnetometerActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor magnetometer;
    private Sensor accelerometer;

    // 显示视图
    private TextView tvX, tvY, tvZ, tvTotal, tvAzimuth, tvPitch, tvRoll, tvStatus;
    private ImageView compassView;

    // 传感器数据
    private float[] magnetValues = new float[3];
    private float[] accelValues = new float[3];
    private boolean hasAccelData = false;
    private boolean hasMagnetData = false;

    // 滤波处理
    private static final float ALPHA = 0.8f;
    private float[] filteredMagnet = new float[3];

    // 地磁常量
    private static final float EARTH_MAGNETIC_THRESHOLD = 100.0f; // μT
    private static final float STRONG_FIELD_THRESHOLD = 200.0f;   // μT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetometer);

        initViews();
        initSensors();
    }

    private void initViews() {
        tvX = findViewById(R.id.tv_x);
        tvY = findViewById(R.id.tv_y);
        tvZ = findViewById(R.id.tv_z);
        tvTotal = findViewById(R.id.tv_total);
        tvAzimuth = findViewById(R.id.tv_azimuth);
        tvPitch = findViewById(R.id.tv_pitch);
        tvRoll = findViewById(R.id.tv_roll);
        tvStatus = findViewById(R.id.tv_status);
        compassView = findViewById(R.id.iv_compass);
    }

    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (magnetometer == null) {
            tvStatus.setText("设备不支持地磁传感器");
            tvStatus.setTextColor(Color.RED);
        } else {
            tvStatus.setText("地磁传感器就绪，请进行8字形校准");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册两个传感器
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, accelValues, 0, 3);
                hasAccelData = true;
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                // 应用滤波
                for (int i = 0; i < 3; i++) {
                    filteredMagnet[i] = lowPass(event.values[i], filteredMagnet[i]);
                }
                System.arraycopy(filteredMagnet, 0, magnetValues, 0, 3);
                hasMagnetData = true;
                break;
        }

        // 当两个传感器都有数据时，计算方向
        if (hasAccelData && hasMagnetData) {
            updateMagneticDisplay();
            calculateOrientation();
            detectMagneticAnomalies();
        }
    }

    private float lowPass(float current, float last) {
        return last + ALPHA * (current - last);
    }

    private void updateMagneticDisplay() {
        runOnUiThread(() -> {
            float x = magnetValues[0];
            float y = magnetValues[1];
            float z = magnetValues[2];

            tvX.setText(String.format("X轴: %.1f μT", x));
            tvY.setText(String.format("Y轴: %.1f μT", y));
            tvZ.setText(String.format("Z轴: %.1f μT", z));

            // 计算总磁场强度
            float total = (float) Math.sqrt(x*x + y*y + z*z);
            tvTotal.setText(String.format("总磁场强度: %.1f μT", total));

            // 根据磁场强度改变颜色
            updateMagneticColors(x, y, z, total);
        });
    }

    private void updateMagneticColors(float x, float y, float z, float total) {
        int colorLevel = getColorForMagneticField(total);

        tvTotal.setTextColor(colorLevel);
        tvStatus.setTextColor(colorLevel);

        // 各轴颜色根据强度变化
        tvX.setTextColor(getColorForMagneticField(Math.abs(x)));
        tvY.setTextColor(getColorForMagneticField(Math.abs(y)));
        tvZ.setTextColor(getColorForMagneticField(Math.abs(z)));
    }

    private int getColorForMagneticField(float strength) {
        if (strength < 30) {
            return Color.parseColor("#4CAF50"); // 正常（绿色）
        } else if (strength < EARTH_MAGNETIC_THRESHOLD) {
            return Color.BLACK; // 标准地磁场
        } else if (strength < STRONG_FIELD_THRESHOLD) {
            return Color.parseColor("#FF9800"); // 中等干扰（橙色）
        } else {
            return Color.RED; // 强干扰（红色）
        }
    }

    private void calculateOrientation() {
        float[] rotationMatrix = new float[9];
        float[] inclinationMatrix = new float[9];
        float[] orientation = new float[3];

        // 计算旋转矩阵和方向
        boolean success = SensorManager.getRotationMatrix(
                rotationMatrix, inclinationMatrix, accelValues, magnetValues);

        if (success) {
            SensorManager.getOrientation(rotationMatrix, orientation);

            // 转换为角度
            float azimuth = (float) Math.toDegrees(orientation[0]); // 方位角（0=北，90=东，180=南，270=西）
            float pitch = (float) Math.toDegrees(orientation[1]);   // 俯仰角
            float roll = (float) Math.toDegrees(orientation[2]);    // 横滚角

            updateOrientationDisplay(azimuth, pitch, roll);
            updateCompass(azimuth);
        }
    }
    private void updateOrientationDisplay(float azimuth, float pitch, float roll) {
        runOnUiThread(() -> {
            // 确保方位角在0-360度范围内
            tvAzimuth.setText(String.format("方位角: %.1f° (%s)", azimuth, getDirectionName(azimuth)));
            tvPitch.setText(String.format("俯仰角: %.1f°", pitch));
            tvRoll.setText(String.format("横滚角: %.1f°", roll));
        });
    }

    private String getDirectionName(float azimuth) {
        if (azimuth >= 337.5 || azimuth < 22.5) return "北";
        if (azimuth >= 22.5 && azimuth < 67.5) return "东北";
        if (azimuth >= 67.5 && azimuth < 112.5) return "东";
        if (azimuth >= 112.5 && azimuth < 157.5) return "东南";
        if (azimuth >= 157.5 && azimuth < 202.5) return "南";
        if (azimuth >= 202.5 && azimuth < 247.5) return "西南";
        if (azimuth >= 247.5 && azimuth < 292.5) return "西";
        return "西北";
    }

    private void updateCompass(float azimuth) {
        runOnUiThread(() -> {
            // 旋转指南针指针（负号是因为旋转方向相反）
            compassView.setRotation(-azimuth);

            // 根据方向准确性改变颜色
            float accuracy = calculateDirectionAccuracy();
            if (accuracy > 0.8f) {
                compassView.setColorFilter(Color.parseColor("#4CAF50"));
            } else if (accuracy > 0.5f) {
                compassView.setColorFilter(Color.parseColor("#FF9800"));
            } else {
                compassView.setColorFilter(Color.RED);
            }
        });
    }

    private float calculateDirectionAccuracy() {
        // 简单的方向准确性评估
        float totalField = (float) Math.sqrt(
                magnetValues[0]*magnetValues[0] +
                        magnetValues[1]*magnetValues[1] +
                        magnetValues[2]*magnetValues[2]);

        // 地球磁场通常在25-65μT范围内
        if (totalField > 20 && totalField < 70) {
            return 1.0f - Math.abs(totalField - 45) / 25; // 45μT是典型值
        }
        return 0.3f; // 磁场异常，准确性低
    }

    private void detectMagneticAnomalies() {
        float totalField = (float) Math.sqrt(
                magnetValues[0]*magnetValues[0] +
                        magnetValues[1]*magnetValues[1] +
                        magnetValues[2]*magnetValues[2]);

        runOnUiThread(() -> {
            if (totalField > STRONG_FIELD_THRESHOLD) {
                tvStatus.setText("⚠️ 检测到强磁场干扰！");
                tvStatus.setTextColor(Color.RED);
            } else if (totalField < 10) {
                tvStatus.setText("🔒 磁场强度过低（可能被屏蔽）");
                tvStatus.setTextColor(Color.parseColor("#FF9800"));
            } else if (totalField > 20 && totalField < 70) {
                tvStatus.setText("✅ 地磁场正常，方向准确");
                tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            } else {
                tvStatus.setText("⚡ 地磁场异常，需要校准");
                tvStatus.setTextColor(Color.parseColor("#FF9800"));
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        runOnUiThread(() -> {
            String accuracyText;
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    accuracyText = "不可靠 - 请进行8字形校准";
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    accuracyText = "低精度 - 缓慢移动设备校准";
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    accuracyText = "中等精度";
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    accuracyText = "高精度";
                    break;
                default:
                    accuracyText = "未知";
            }

            Toast.makeText(this, "地磁传感器精度: " + accuracyText, Toast.LENGTH_SHORT).show();
        });
    }
}
