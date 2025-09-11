package com.yang.androiddemolog.mmiActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class BrightnessActivity extends AppCompatActivity {

    private Button lowestBrightness, gradientBrightness, highestBright;

    private Handler brightnessHandler;
    private Runnable brightnessRunnable;

    // 亮度百分比
    private final float[] brightness = {1.0f, 0.95f, 0.9f, 0.85f, 0.8f, 0.75f,0.7f, 0.65f, 0.6f,
            0.55f, 0.5f, 0.45f, 0.4f, 0.35f, 0.3f, 0.25f, 0.2f, 0.15f, 0.1f, 0.05f};
    private int currentLevelIndex = 0;

    // 每次亮度变化的间隔时间
    private static final long DELAY_MILLIS = 200;

    // 定义请求码
    private static final int REQUEST_CODE_WRITE_SETTINGS = 100;

    /**
     * 检查并请求 WRITE_SETTINGS 权限
     */
    private void checkAndRequestWriteSettingsPermission() {
        // 对于 Android M (API 23) 及以上版本才需要特殊处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查是否已有权限
            if (Settings.System.canWrite(this)) {
                // 已有权限，执行需要权限的操作
                performSystemBrightnessChange();
            } else {
                // 没有权限，引导用户去设置页面
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                // 使用 startActivityForResult 以便知道用户返回后的结果
                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
            }
        } else {
            // 低版本直接操作它
            performSystemBrightnessChange();
        }
    }

    private void performSystemBrightnessChange() {
        // 设置系统亮度
        int targetBrightness = 10; // 0-255
        Settings.System.putInt(
                getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                targetBrightness
        );
        // 修改系统设置后，通常需要重启或触发系统更新才能生效，一种触发方式是改变当前窗口的亮度
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = targetBrightness / 255.0f;
        window.setAttributes(layoutParams);

        Toast.makeText(this,
                getSystemBrightness() > 0 ? "系统亮度修改成功" : "系统亮度修改失败",
                Toast.LENGTH_SHORT).show();
    }

    private int getSystemBrightness(){
        int systemBrightness;
        try {
            // 获取的是 0-255 的整数值
            systemBrightness = Settings.System.getInt(
                    getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS
            );
            Log.d("Brightness", "获取到系统亮度为: " + systemBrightness);
            return systemBrightness;
        } catch (Exception e) {
            Log.e("Brightness", "获取系统亮度失败：" + e.getMessage());
        }
        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            // 用户从 WRITE_SETTINGS 授权界面返回
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(this)) {
                    // 用户授予了权限
                    performSystemBrightnessChange();
                } else {
                    // 用户拒绝或未授予权限
                    Toast.makeText(this, "权限被拒绝，无法修改系统设置", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brightness);

        initBtn();

        getSystemBrightness();

        checkAndRequestWriteSettingsPermission();

        brightnessHandler = new Handler(Looper.getMainLooper());
        setupBrightCycle();
    }

    private void setupBrightCycle() {
        // 获取当前窗口
        Window window = getWindow();

        brightnessRunnable = new Runnable() {
            @Override
            public void run() {
                // 获取当前需要设置的亮度
                float curBrightness = brightness[currentLevelIndex];

                // 设置窗口亮度
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.screenBrightness = curBrightness;
                window.setAttributes(attributes);

                // 更新索引，准备下一个待变化的亮度值
                currentLevelIndex = (currentLevelIndex + 1) % brightness.length;

                // 延迟两秒
                brightnessHandler.postDelayed(this, DELAY_MILLIS);
            }
        };
        brightnessHandler.post(brightnessRunnable);
    }

    private void initBtn() {
        lowestBrightness = findViewById(R.id.btn_lowestBrightness);
        gradientBrightness = findViewById(R.id.btn_gradientBrightness);
        highestBright = findViewById(R.id.btn_highestBright);

        lowestBrightness.setOnClickListener(v -> toLowestBrightness());
        gradientBrightness.setOnClickListener(v -> toGradientBrightness());
        highestBright.setOnClickListener(v -> toHighestBright());
    }

    private void toLowestBrightness() {
        if(brightnessHandler != null && brightnessRunnable != null){
            brightnessHandler.removeCallbacks(brightnessRunnable);
        }
        // 获取当前窗口
        Window window = getWindow();
        // 获取当前需要设置的亮度
        float curBrightness = brightness[brightness.length - 1];
        // 设置窗口亮度
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.screenBrightness = curBrightness;
        window.setAttributes(attributes);
    }

    private void toGradientBrightness() {
        setupBrightCycle();
    }

    private void toHighestBright() {
        if(brightnessHandler != null && brightnessRunnable != null){
            brightnessHandler.removeCallbacks(brightnessRunnable);
        }
        // 获取当前窗口
        Window window = getWindow();
        // 获取当前需要设置的亮度
        float curBrightness = brightness[0];
        // 设置窗口亮度
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.screenBrightness = curBrightness;
        window.setAttributes(attributes);
    }

    /**
     * 销毁Activity时，恢复窗口的亮度为系统默认
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止brightnessHandler任务
        if(brightnessHandler != null && brightnessRunnable != null){
            brightnessHandler.removeCallbacks(brightnessRunnable);
        }

        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        window.setAttributes(attributes);
    }
}
