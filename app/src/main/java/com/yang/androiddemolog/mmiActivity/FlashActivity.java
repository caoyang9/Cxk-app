package com.yang.androiddemolog.mmiActivity;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class FlashActivity extends AppCompatActivity {
    private Button btnToggle;
    private ImageView ivFlashlight;
    private TextView tvStatus;

    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashlightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);

        initViews();
        initCamera();
    }

    private void initViews() {
        btnToggle = findViewById(R.id.btn_toggle);
        ivFlashlight = findViewById(R.id.iv_flashlight);
        tvStatus = findViewById(R.id.tv_status);

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFlashlight();
            }
        });
    }

    // 初始化相机管理器
    private void initCamera() {
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

            // 尝试获取相机ID
            String[] cameraIds = cameraManager.getCameraIdList();
            for (String id : cameraIds) {
                cameraId = id; // 使用第一个可用的相机
                break;
            }

        } catch (Exception e) {
            showError("初始化相机失败");
        }
    }

    // 切换闪光灯状态
    private void toggleFlashlight() {
        try {
            if (isFlashlightOn) {
                turnOffFlashlight();
            } else {
                turnOnFlashlight();
            }
        } catch (Exception e) {
            showError("操作失败: " + e.getMessage());
        }
    }

    // 开启闪光灯
    private void turnOnFlashlight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true);
                isFlashlightOn = true;
                updateUI();
            } else {
                // 对于Android 6.0以下的版本，使用传统方法
                showError("需要Android 6.0以上系统");
            }
        } catch (Exception e) {
            showError("无法开启闪光灯");
        }
    }

    // 关闭闪光灯
    private void turnOffFlashlight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false);
                isFlashlightOn = false;
                updateUI();
            }
        } catch (Exception e) {
            showError("无法关闭闪光灯");
        }
    }

    // 更新UI状态
    private void updateUI() {
        if (isFlashlightOn) {
            ivFlashlight.setImageResource(R.drawable.ic_flashlight_on);
            tvStatus.setText("闪光灯已开启");
            btnToggle.setText("关闭闪光灯");
            btnToggle.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            ivFlashlight.setImageResource(R.drawable.ic_flashlight_off);
            tvStatus.setText("闪光灯已关闭");
            btnToggle.setText("开启闪光灯");
            btnToggle.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    // 显示错误信息
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 当Activity不可见时自动关闭闪光灯
        if (isFlashlightOn) {
            turnOffFlashlight();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 确保闪光灯被关闭
        if (isFlashlightOn) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cameraManager.setTorchMode(cameraId, false);
                }
            } catch (Exception e) {
                // 忽略错误
            }
        }
    }
}
