package com.yang.androiddemolog.mmiActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

/**
 * MMI Activity：一个新的界面，包含MMI中的实践
 */
public class MMIActivity extends AppCompatActivity {

    // 定义模拟亮度调节按钮，图片管理按钮，触摸移动按钮
    private Button brightnessBtn, imageBtn, touchBtn;

    // 定义按键、闪光灯、振动按钮
    private Button vibratorBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmi);

        // 初始化亮度调节按钮，图片管理按钮，触摸移动按钮，绑定点击事件
        initBrightnessBtn();
    }

    private void initBrightnessBtn(){
        brightnessBtn = findViewById(R.id.btn_brightness);
        imageBtn = findViewById(R.id.btn_image);
        touchBtn = findViewById(R.id.btn_touchToArea);

        vibratorBtn = findViewById(R.id.btn_vibrator);

        brightnessBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, BrightnessActivity.class)));
        imageBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, ImageActivity.class)));
        touchBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, TouchActivity.class)));

        vibratorBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, VibratorActivity.class)));
    }
}
