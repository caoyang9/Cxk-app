package com.yang.androiddemolog.mmiActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class GravityActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private Sensor accelerometerSensor;

    private ImageView imageView;
    private TextView tvGravityX, tvGravityY, tvGravityZ;
    private TextView tvOrientation, tvStatus;

    // 重力常量
    private static final float EARTH_GRAVITY = 9.8f;
    private static final float GRAVITY_THRESHOLD = 5.0f;

    // 低通滤波器参数（用于平滑数据）
    private static final float ALPHA = 0.8f;
    private float[] filteredValues = new float[3];

    // 方向枚举
    private enum Orientation {
        PORTRAIT,       // 竖屏（正常）
        LANDSCAPE_LEFT, // 横屏（向左旋转）
        LANDSCAPE_RIGHT,// 横屏（向右旋转）
        REVERSE_PORTRAIT, // 反向竖屏
        FACE_UP,        // 屏幕朝上
        FACE_DOWN       // 屏幕朝下
    }

    private Orientation currentOrientation = Orientation.PORTRAIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravity);
        // 初始化视图
        initViews();
        // 初始化传感器
        initSensors();
    }

    private void initViews() {
        imageView = findViewById(R.id.iv_rotatable);
        tvGravityX = findViewById(R.id.tv_gravity_x);
        tvGravityY = findViewById(R.id.tv_gravity_y);
        tvGravityZ = findViewById(R.id.tv_gravity_z);
        tvOrientation = findViewById(R.id.tv_orientation);
        tvStatus = findViewById(R.id.tv_status);
    }

    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // 首先尝试获取重力传感器
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        if (gravitySensor != null) {
            tvStatus.setText("传感器状态: 使用重力传感器");
        }
//        else {
//            // 如果不支持重力传感器，使用加速度计作为备选
//            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//            if (accelerometerSensor != null) {
//                tvStatus.setText("传感器状态: 使用加速度计（模拟重力）");
//            } else {
//                tvStatus.setText("传感器状态: 无可用传感器");
//                tvStatus.setTextColor(0xFFFF0000);
//            }
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册传感器监听
        if (gravitySensor != null) {
            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_UI);
        }
//        else if (accelerometerSensor != null) {
//            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销传感器监听
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY ||
                event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // 使用低通滤波器平滑数据
            filteredValues[0] = ALPHA * filteredValues[0] + (1 - ALPHA) * event.values[0];
            filteredValues[1] = ALPHA * filteredValues[1] + (1 - ALPHA) * event.values[1];
            filteredValues[2] = ALPHA * filteredValues[2] + (1 - ALPHA) * event.values[2];

            float x = filteredValues[0];
            float y = filteredValues[1];
            float z = filteredValues[2];

            // 更新数据显示
            updateGravityDisplay(x, y, z);
            // 检测方向并旋转图片
            detectAndRotateImage(x, y, z);
        }
    }

    private void updateGravityDisplay(float x, float y, float z) {
        tvGravityX.setText(String.format("X轴: %.2f m/s²", x));
        tvGravityY.setText(String.format("Y轴: %.2f m/s²", y));
        tvGravityZ.setText(String.format("Z轴: %.2f m/s²", z));
    }

    /**
     * 根据x、y、z三个坐标的重力数据检测方向并旋转图片
     * @param x
     * @param y
     * @param z
     */
    private void detectAndRotateImage(float x, float y, float z) {
        // 检测设备的方向
        Orientation newOrientation = detectOrientation(x, y, z);
        // 只有当方向真正改变时才更新UI，避免频繁重绘
        if (newOrientation != currentOrientation) {
            currentOrientation = newOrientation;
            rotateImageBasedOnOrientation(newOrientation);
            updateOrientationText(newOrientation);
        }
    }

    private Orientation detectOrientation(float x, float y, float z) {
        // 检测设备方向
        if (Math.abs(z) > EARTH_GRAVITY - GRAVITY_THRESHOLD) {
            return z > 0 ? Orientation.FACE_UP : Orientation.FACE_DOWN;
        } else if (Math.abs(y) > EARTH_GRAVITY - GRAVITY_THRESHOLD) {
            if (y > 0) {
                return Orientation.PORTRAIT;
            } else {
                return Orientation.REVERSE_PORTRAIT;
            }
        } else if (Math.abs(x) > EARTH_GRAVITY - GRAVITY_THRESHOLD) {
            return x > 0 ? Orientation.LANDSCAPE_LEFT : Orientation.LANDSCAPE_RIGHT;
        }

        return currentOrientation; // 保持当前方向
    }

    private void rotateImageBasedOnOrientation(Orientation orientation) {
        float rotation = 0;

        switch (orientation) {
            case PORTRAIT:
                rotation = 0;
                break;
            case LANDSCAPE_LEFT:
                rotation = 90;
                break;
            case LANDSCAPE_RIGHT:
                rotation = -90;
                break;
            case REVERSE_PORTRAIT:
                rotation = 180;
                break;
            case FACE_UP:
            case FACE_DOWN:
                // 平放时保持当前旋转状态
                return;
        }
        // 使用属性动画实现平滑旋转
        imageView.animate()
                .rotation(rotation)
                .setDuration(300) // 300毫秒的旋转动画
                .start();
    }

    private void updateOrientationText(Orientation orientation) {
        String orientationText;
        switch (orientation) {
            case PORTRAIT:
                orientationText = "竖屏（正常）";
                break;
            case LANDSCAPE_LEFT:
                orientationText = "横屏（向左旋转）";
                break;
            case LANDSCAPE_RIGHT:
                orientationText = "横屏（向右旋转）";
                break;
            case REVERSE_PORTRAIT:
                orientationText = "反向竖屏";
                break;
            case FACE_UP:
                orientationText = "屏幕朝上";
                break;
            case FACE_DOWN:
                orientationText = "屏幕朝下";
                break;
            default:
                orientationText = "未知方向";
        }
        tvOrientation.setText("当前方向: " + orientationText);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 传感器精度变化处理
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

        tvStatus.setText("传感器状态: " + accuracyText);
    }
}
