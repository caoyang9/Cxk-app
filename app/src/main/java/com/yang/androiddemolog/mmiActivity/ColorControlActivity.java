package com.yang.androiddemolog.mmiActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class ColorControlActivity extends AppCompatActivity {
    private TextView tvColorDisplay;
    private TextView tvColorInfo;

    private int baseColor = Color.YELLOW; // 基础颜色（亮黄色）
    private float brightness = 1.0f; // 亮度值（0.0 - 1.0）
    private static final float BRIGHTNESS_STEP = 0.1f; // 每次调整的步长

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_control);

        initViews();
        updateColorDisplay();
    }

    private void initViews() {
        tvColorDisplay = findViewById(R.id.tv_color_display);
        tvColorInfo = findViewById(R.id.tv_color_info);
    }

    // 更新颜色显示
    private void updateColorDisplay() {
        // 根据亮度调整颜色
        int adjustedColor = adjustColorBrightness(baseColor, brightness);

        // 设置背景颜色
        tvColorDisplay.setBackgroundColor(adjustedColor);

        // 更新颜色信息
        String hexColor = String.format("#%06X", (0xFFFFFF & adjustedColor));
        int brightnessPercent = (int) (brightness * 100);

        String colorInfo = String.format("当前颜色: %s\n亮度: %d%%", hexColor, brightnessPercent);
        tvColorInfo.setText(colorInfo);
    }

    // 调整颜色亮度
    private int adjustColorBrightness(int color, float brightness) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = brightness; // 设置亮度值（0-1）
        return Color.HSVToColor(hsv);
    }

    // 增加亮度
    private void increaseBrightness() {
        if (brightness < 1.0f) {
            brightness += BRIGHTNESS_STEP;
            if (brightness > 1.0f) {
                brightness = 1.0f;
            }
            updateColorDisplay();
        } else {
            Toast.makeText(this, "已达到最大亮度", Toast.LENGTH_SHORT).show();
        }
    }

    // 降低亮度
    private void decreaseBrightness() {
        if (brightness > 0.1f) {
            brightness -= BRIGHTNESS_STEP;
            if (brightness < 0.1f) {
                brightness = 0.1f;
            }
            updateColorDisplay();
        } else {
            Toast.makeText(this, "已达到最小亮度", Toast.LENGTH_SHORT).show();
        }
    }

    // 显示亮度变化提示
    private void showBrightnessToast(String message) {
        int brightnessPercent = (int) (brightness * 100);
        Toast.makeText(this, message + ": " + brightnessPercent + "%", Toast.LENGTH_SHORT).show();
    }

    // 重置颜色到最亮
    private void resetColor() {
        brightness = 1.0f;
        updateColorDisplay();
        Toast.makeText(this, "颜色已重置为最亮", Toast.LENGTH_SHORT).show();
    }

    // 重写按键事件处理，监听音量键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                // 增加亮度
                increaseBrightness();
                return true; // 消费该事件，防止默认音量调整

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                // 降低亮度
                decreaseBrightness();
                return true; // 消费该事件，防止默认音量调整

            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    // 可选：也处理音量键释放事件
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true; // 消费这些事件
            default:
                return super.onKeyUp(keyCode, event);
        }
    }
}
