package com.yang.androiddemolog.mmiActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class GyroBallActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor gyroscope;

    // UI组件
    private ImageView ball;

    // 小球移动参数
    private float ballX = 0f; // 小球X坐标
    private float ballY = 0f; // 小球Y坐标
    private float ballVelocityX = 0f; // X轴速度
    private float ballVelocityY = 0f; // Y轴速度

    // 屏幕尺寸
    private int screenWidth, screenHeight;
    private int ballWidth, ballHeight;

    // 物理参数
    private static final float FRICTION = 0.95f; // 摩擦系数
    private static final float MAX_VELOCITY = 30f; // 最大速度
    private static final float SENSITIVITY = 0.5f; // 控制灵敏度

    // 陀螺仪数据滤波
    private static final float ALPHA = 0.8f;
    private float[] filteredValues = new float[3];

    // 动画循环
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            updateBallPosition();
            handler.postDelayed(this, 16); // 约60FPS
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyro_ball);

        initViews();
        initSensors();
        startAnimation();
    }

    private void initViews() {
        ball = findViewById(R.id.ball);

        // 获取屏幕尺寸（在视图布局完成后）
        ball.post(new Runnable() {
            @Override
            public void run() {
                screenWidth = findViewById(R.id.container).getWidth();
                screenHeight = findViewById(R.id.container).getHeight();
                ballWidth = ball.getWidth();
                ballHeight = ball.getHeight();

                // 初始化小球位置到屏幕中心
                ballX = (screenWidth - ballWidth) / 2f;
                ballY = (screenHeight - ballHeight) / 2f;
                updateBallViewPosition();
            }
        });
    }

    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    private void startAnimation() {
        handler.post(animationRunnable);
    }

    private void stopAnimation() {
        handler.removeCallbacks(animationRunnable);
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
        stopAnimation();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // 应用低通滤波器
            filteredValues[0] = lowPass(event.values[0], filteredValues[0]);
            filteredValues[1] = lowPass(event.values[1], filteredValues[1]);
            filteredValues[2] = lowPass(event.values[2], filteredValues[2]);

            float x = filteredValues[0]; // 横滚 - 控制Y轴移动
            float y = filteredValues[1]; // 俯仰 - 控制X轴移动
            float z = filteredValues[2]; // 偏航 - 用于显示

            // 根据陀螺仪数据更新小球速度
            updateBallVelocity(x, y);

            // 检测剧烈晃动（重置小球位置）
            detectShake(x, y, z);
        }
    }

    private float lowPass(float current, float last) {
        return last + ALPHA * (current - last);
    }

    private int getColorForVelocity(float velocity) {
        if (velocity < 10) return 0xFFE0E0E0;    // 灰色 - 静止
        if (velocity < 45) return 0xFF4CAF50;    // 绿色 - 慢速
        if (velocity < 90) return 0xFFFF9800;    // 橙色 - 中速
        return 0xFFFF4444;                       // 红色 - 快速
    }

    private void updateBallVelocity(float gyroX, float gyroY) {
        // 将陀螺仪数据转换为小球速度
        // 注意：陀螺仪的X轴对应设备的横滚（控制小球Y轴移动）
        //       陀螺仪的Y轴对应设备的俯仰（控制小球X轴移动）

        // 转换为度/秒并应用灵敏度
        float velocityX = (float) Math.toDegrees(gyroY) * SENSITIVITY;
        float velocityY = (float) Math.toDegrees(gyroX) * SENSITIVITY;

        // 限制最大速度
        ballVelocityX = clamp(velocityX, -MAX_VELOCITY, MAX_VELOCITY);
        ballVelocityY = clamp(velocityY, -MAX_VELOCITY, MAX_VELOCITY);
    }

    private void updateBallPosition() {
        // 应用速度
        ballX += ballVelocityX;
        ballY += ballVelocityY;

        // 应用摩擦力（逐渐减速）
        ballVelocityX *= FRICTION;
        ballVelocityY *= FRICTION;

        // 边界碰撞检测
        handleBoundaryCollision();

        // 更新小球视图位置
        updateBallViewPosition();
    }

    private void handleBoundaryCollision() {
        // 左边界
        if (ballX < 0) {
            ballX = 0;
            ballVelocityX = -ballVelocityX * 0.7f; // 反弹并损失能量
        }
        // 右边界
        else if (ballX > screenWidth - ballWidth) {
            ballX = screenWidth - ballWidth;
            ballVelocityX = -ballVelocityX * 0.7f;
        }

        // 上边界
        if (ballY < 0) {
            ballY = 0;
            ballVelocityY = -ballVelocityY * 0.7f;
        }
        // 下边界
        else if (ballY > screenHeight - ballHeight) {
            ballY = screenHeight - ballHeight;
            ballVelocityY = -ballVelocityY * 0.7f;
        }

        // 如果速度很小，停止运动以避免微小抖动
        if (Math.abs(ballVelocityX) < 0.1f) ballVelocityX = 0;
        if (Math.abs(ballVelocityY) < 0.1f) ballVelocityY = 0;
    }

    private void updateBallViewPosition() {
        runOnUiThread(() -> {
            ball.setX(ballX);
            ball.setY(ballY);

            // 根据速度改变小球颜色（可选效果）
            updateBallAppearance();
        });
    }

    private void updateBallAppearance() {
        // 根据速度大小改变小球透明度或颜色（可选效果）
        float speed = (float) Math.sqrt(ballVelocityX * ballVelocityX + ballVelocityY * ballVelocityY);
        float alpha = 0.7f + (speed / MAX_VELOCITY) * 0.3f; // 0.7 - 1.0
        ball.setAlpha(alpha);

        // 可以添加缩放效果
        float scale = 1.0f + (speed / MAX_VELOCITY) * 0.2f; // 1.0 - 1.2
        ball.setScaleX(scale);
        ball.setScaleY(scale);
    }

    private void detectShake(float x, float y, float z) {
        // 检测剧烈晃动（用于重置小球位置）
        float totalRotation = (float) Math.sqrt(x*x + y*y + z*z);
        float totalDegrees = (float) Math.toDegrees(totalRotation);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAnimation();
        handler.removeCallbacksAndMessages(null);
    }
}
