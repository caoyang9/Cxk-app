package com.yang.androiddemolog.mmiActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brightness);

        initBtn();

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
