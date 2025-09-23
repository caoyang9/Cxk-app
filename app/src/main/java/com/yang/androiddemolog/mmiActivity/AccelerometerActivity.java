package com.yang.androiddemolog.mmiActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView tvX, tvY, tvZ, tvTotal, tvStatus;
    private TextView tvMaxX, tvMaxY, tvMaxZ, tvMaxTotal;
    private Button btnResetMax, btnStartRecord, btnPauseRecord;

    // 低通滤波器参数
    private static final float ALPHA = 0.8f;
    private float[] filteredValues = new float[3];

    // 最大加速度记录
    private float maxX = 0f;
    private float maxY = 0f;
    private float maxZ = 0f;
    private float maxTotal = 0f;

    // 记录控制
    private boolean isRecording = true;

    // 摇一摇检测参数
    private static final float SHAKE_THRESHOLD = 15.0f;
    private long lastShakeTime = 0;
    private static final int SHAKE_INTERVAL = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        initViews();
        initSensor();
        setupClickListeners();
    }

    private void initViews() {
        // 当前加速度显示
        tvX = findViewById(R.id.tv_x);
        tvY = findViewById(R.id.tv_y);
        tvZ = findViewById(R.id.tv_z);
        tvTotal = findViewById(R.id.tv_total);
        tvStatus = findViewById(R.id.tv_status);

        // 最大加速度显示
        tvMaxX = findViewById(R.id.tv_max_x);
        tvMaxY = findViewById(R.id.tv_max_y);
        tvMaxZ = findViewById(R.id.tv_max_z);
        tvMaxTotal = findViewById(R.id.tv_max_total);

        // 按钮
        btnResetMax = findViewById(R.id.btn_reset_max);
        btnStartRecord = findViewById(R.id.btn_start_record);
        btnPauseRecord = findViewById(R.id.btn_pause_record);

        // 初始化最大值为0
        updateMaxDisplay();
    }

    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer == null) {
            Toast.makeText(this, "设备不支持加速度传感器", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupClickListeners() {
        btnResetMax.setOnClickListener(v -> resetMaxRecords());

        btnStartRecord.setOnClickListener(v -> {
            isRecording = true;
            updateRecordingStatus();
            Toast.makeText(this, "开始记录最大加速度", Toast.LENGTH_SHORT).show();
        });

        btnPauseRecord.setOnClickListener(v -> {
            isRecording = false;
            updateRecordingStatus();
            Toast.makeText(this, "暂停记录最大加速度", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateRecordingStatus() {
        if (isRecording) {
            tvStatus.setText("状态: 记录中...");
            tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            btnStartRecord.setEnabled(false);
            btnPauseRecord.setEnabled(true);
        } else {
            tvStatus.setText("状态: 已暂停");
            tvStatus.setTextColor(Color.parseColor("#FF9800"));
            btnStartRecord.setEnabled(true);
            btnPauseRecord.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        updateRecordingStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 应用低通滤波器
            filteredValues[0] = lowPass(event.values[0], filteredValues[0]);
            filteredValues[1] = lowPass(event.values[1], filteredValues[1]);
            filteredValues[2] = lowPass(event.values[2], filteredValues[2]);

            float x = filteredValues[0];
            float y = filteredValues[1];
            float z = filteredValues[2];

            // 更新当前加速度显示
            updateCurrentDisplay(x, y, z);

            // 记录最大加速度（如果正在记录）
            if (isRecording) {
                updateMaxRecords(x, y, z);
            }

            // 检测动作
            detectShake(x, y, z);
            detectExtremeAcceleration(x, y, z);
        }
    }

    private float lowPass(float current, float last) {
        return last + ALPHA * (current - last);
    }

    private void updateCurrentDisplay(float x, float y, float z) {
        runOnUiThread(() -> {
            tvX.setText(String.format("X轴: %.2f m/s²", x));
            tvY.setText(String.format("Y轴: %.2f m/s²", y));
            tvZ.setText(String.format("Z轴: %.2f m/s²", z));

            float total = (float) Math.sqrt(x*x + y*y + z*z);
            tvTotal.setText(String.format("总加速度: %.2f m/s²", total));

            // 根据加速度大小改变颜色提示
            updateValueColors(x, y, z, total);
        });
    }

    private void updateValueColors(float x, float y, float z, float total) {
        // X轴颜色（红色系）
        int xColor = getColorForValue(Math.abs(x), 5f, 10f, 20f);
        tvX.setTextColor(xColor);

        // Y轴颜色（绿色系）
        int yColor = getColorForValue(Math.abs(y), 5f, 10f, 20f);
        tvY.setTextColor(yColor);

        // Z轴颜色（蓝色系）
        int zColor = getColorForValue(Math.abs(z), 5f, 10f, 20f);
        tvZ.setTextColor(zColor);

        // 总加速度颜色
        int totalColor = getColorForValue(total, 10f, 15f, 25f);
        tvTotal.setTextColor(totalColor);
    }

    private int getColorForValue(float value, float low, float medium, float high) {
        if (value < low) {
            return Color.BLACK; // 正常
        } else if (value < medium) {
            return Color.parseColor("#FF9800"); // 橙色警告
        } else if (value < high) {
            return Color.parseColor("#FF5722"); // 红色警告
        } else {
            return Color.RED; // 严重警告
        }
    }

    private void updateMaxRecords(float x, float y, float z) {
        boolean updated = false;

        // 更新各轴最大值（取绝对值比较）
        if (Math.abs(x) > Math.abs(maxX)) {
            maxX = x;
            updated = true;
        }
        if (Math.abs(y) > Math.abs(maxY)) {
            maxY = y;
            updated = true;
        }
        if (Math.abs(z) > Math.abs(maxZ)) {
            maxZ = z;
            updated = true;
        }

        // 更新总加速度最大值
        float total = (float) Math.sqrt(x*x + y*y + z*z);
        if (total > maxTotal) {
            maxTotal = total;
            updated = true;
        }

        // 如果最大值有更新，刷新显示
        if (updated) {
            runOnUiThread(this::updateMaxDisplay);
        }
    }

    private void updateMaxDisplay() {
        tvMaxX.setText(String.format("X轴最大: %.2f m/s²", maxX));
        tvMaxY.setText(String.format("Y轴最大: %.2f m/s²", maxY));
        tvMaxZ.setText(String.format("Z轴最大: %.2f m/s²", maxZ));
        tvMaxTotal.setText(String.format("总加速度最大: %.2f m/s²", maxTotal));

        // 高亮显示最大值
        highlightMaxValues();
    }

    private void highlightMaxValues() {
        // 重置所有颜色
        tvMaxX.setTextColor(Color.BLACK);
        tvMaxY.setTextColor(Color.BLACK);
        tvMaxZ.setTextColor(Color.BLACK);
        tvMaxTotal.setTextColor(Color.BLACK);

        // 找出当前最大的轴并高亮显示
        float absMaxX = Math.abs(maxX);
        float absMaxY = Math.abs(maxY);
        float absMaxZ = Math.abs(maxZ);

        if (absMaxX >= absMaxY && absMaxX >= absMaxZ) {
            tvMaxX.setTextColor(Color.RED);
        } else if (absMaxY >= absMaxX && absMaxY >= absMaxZ) {
            tvMaxY.setTextColor(Color.RED);
        } else {
            tvMaxZ.setTextColor(Color.RED);
        }
    }

    private void resetMaxRecords() {
        maxX = 0f;
        maxY = 0f;
        maxZ = 0f;
        maxTotal = 0f;

        updateMaxDisplay();
        Toast.makeText(this, "加速度记录已重置", Toast.LENGTH_SHORT).show();
    }

    private void detectShake(float x, float y, float z) {
        float linearZ = z - 9.8f;
        float totalLinear = (float) Math.sqrt(x*x + y*y + linearZ*linearZ);

        long currentTime = System.currentTimeMillis();
        if (totalLinear > SHAKE_THRESHOLD && (currentTime - lastShakeTime) > SHAKE_INTERVAL) {
            lastShakeTime = currentTime;
            onShakeDetected();
        }
    }

    private void onShakeDetected() {
        runOnUiThread(() -> {
            tvStatus.setText("检测到摇一摇！");
            tvStatus.setTextColor(Color.RED);

            // 振动反馈
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }

            new Handler().postDelayed(() -> {
                if (isRecording) {
                    tvStatus.setText("状态: 记录中...");
                    tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                } else {
                    tvStatus.setText("状态: 已暂停");
                    tvStatus.setTextColor(Color.parseColor("#FF9800"));
                }
            }, 2000);
        });
    }

    private void detectExtremeAcceleration(float x, float y, float z) {
        float total = (float) Math.sqrt(x*x + y*y + z*z);

        if (total > 30f) {
            runOnUiThread(() -> {
                tvStatus.setText("检测到极高加速度！");
                tvStatus.setTextColor(Color.RED);
            });
        } else if (total < 2.0f) {
            runOnUiThread(() -> {
                tvStatus.setText("可能处于自由落体！");
                tvStatus.setTextColor(Color.parseColor("#FF9800"));
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 精度变化处理
        String accuracyText;
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                accuracyText = "高精度";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                accuracyText = "中等精度";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                accuracyText = "低精度";
                break;
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                accuracyText = "数据不可靠";
                break;
            default:
                accuracyText = "未知";
        }

        runOnUiThread(() -> {
            Toast.makeText(this, "传感器精度: " + accuracyText, Toast.LENGTH_SHORT).show();
        });
    }
}
