package com.yang.androiddemolog.mmiActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

import java.util.HashMap;
import java.util.Map;

/**
 * MMI Activity：一个新的界面，包含MMI中的实践
 */
public class MMIActivity extends AppCompatActivity {

    private final Map<Integer, Integer> arrowToContentMap = new HashMap<>();
    private final Map<Integer, ImageView> arrowViewMap = new HashMap<>();
    private final Map<Integer, View> contentViewMap = new HashMap<>();

    // 定义模拟亮度调节按钮，图片管理按钮，触摸移动按钮
    private Button brightnessBtn, imageBtn, touchBtn;

    // 定义按键、闪光灯、振动按钮
    private Button vibratorBtn, keyBtn, flashBtn;

    private Button takePhotoBtn;

    private Button audioMusic, recordingBtn;

    private Button lightSensorBtn, proximitySensorBtn, gravitySensorBtn, accelerationSensorBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmi2);

        initMappings();
        // 设置点击监听器
        setupClickListeners();

        // 初始化亮度调节按钮，图片管理按钮，触摸移动按钮，绑定点击事件
        initBrightnessBtn();
    }

    private void initBrightnessBtn(){
        findViewById(R.id.btnPrePage).setOnClickListener(v -> finish());

        brightnessBtn = findViewById(R.id.btn_brightness);
        imageBtn = findViewById(R.id.btn_image);
        touchBtn = findViewById(R.id.btn_touchToArea);

        vibratorBtn = findViewById(R.id.btn_vibrator);
        keyBtn = findViewById(R.id.btn_key);
        flashBtn = findViewById(R.id.btn_flash);

        takePhotoBtn = findViewById(R.id.btn_take_photo);

        audioMusic = findViewById(R.id.btn_audio_music);
        recordingBtn = findViewById(R.id.btn2_audio_recording);

        lightSensorBtn = findViewById(R.id.btn_sensor_1);
        proximitySensorBtn = findViewById(R.id.btn_sensor_2);
        gravitySensorBtn = findViewById(R.id.btn_sensor_3);

        brightnessBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, BrightnessActivity.class)));
        imageBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, ImageActivity.class)));
        touchBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, TouchActivity.class)));

        vibratorBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, VibratorActivity.class)));
        keyBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, ColorControlActivity.class)));
        flashBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, FlashActivity.class  )));

        takePhotoBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, TakePhotoActivity.class)));

        audioMusic.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, AudioMusicActivity.class)));
        recordingBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, MediaRecorderActivity.class)));

        lightSensorBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, LightActivity.class)));
        proximitySensorBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, ProximityActivity.class)));
        gravitySensorBtn.setOnClickListener(v -> startActivity(new Intent(MMIActivity.this, GravityActivity.class)));
    }

    private void initMappings() {
        // 初始化箭头到内容的映射
        arrowToContentMap.put(R.id.arrowMMI1, R.id.contentMMI1);
        arrowToContentMap.put(R.id.arrowMMI2, R.id.contentMMI2);
        arrowToContentMap.put(R.id.arrowMMI3, R.id.contentMMI3);
        arrowToContentMap.put(R.id.arrowMMI4, R.id.contentMMI4);
        arrowToContentMap.put(R.id.arrowMMI5, R.id.contentMMI5);
//        arrowToContentMap.put(R.id.arrowMMI55, R.id.contentMMI55);

        // 初始化箭头视图映射
        arrowViewMap.put(R.id.arrowMMI1, findViewById(R.id.arrowMMI1));
        arrowViewMap.put(R.id.arrowMMI2, findViewById(R.id.arrowMMI2));
        arrowViewMap.put(R.id.arrowMMI3, findViewById(R.id.arrowMMI3));
        arrowViewMap.put(R.id.arrowMMI4, findViewById(R.id.arrowMMI4));
        arrowViewMap.put(R.id.arrowMMI5, findViewById(R.id.arrowMMI5));
//        arrowViewMap.put(R.id.arrowMMI55, findViewById(R.id.arrowMMI55));

        // 初始化内容视图映射
        contentViewMap.put(R.id.contentMMI1, findViewById(R.id.contentMMI1));
        contentViewMap.put(R.id.contentMMI2, findViewById(R.id.contentMMI2));
        contentViewMap.put(R.id.contentMMI3, findViewById(R.id.contentMMI3));
        contentViewMap.put(R.id.contentMMI4, findViewById(R.id.contentMMI4));
        contentViewMap.put(R.id.contentMMI5, findViewById(R.id.contentMMI5));
//        contentViewMap.put(R.id.contentMMI55, findViewById(R.id.contentMMI55));
    }

    private void setupClickListeners() {
        // 为每个卡片设置点击监听器
        setupCardClickListener(R.id.cardMMI1, R.id.contentMMI1, R.id.arrowMMI1);
        setupCardClickListener(R.id.cardMMI2, R.id.contentMMI2, R.id.arrowMMI2);
        setupCardClickListener(R.id.cardMMI3, R.id.contentMMI3, R.id.arrowMMI3);
        setupCardClickListener(R.id.cardMMI4, R.id.contentMMI4, R.id.arrowMMI4);
        setupCardClickListener(R.id.cardMMI5, R.id.contentMMI5, R.id.arrowMMI5);
//        setupCardClickListener(R.id.cardMMI55, R.id.contentMMI55, R.id.arrowMMI55);

        // 为每个箭头设置点击监听器
        for (Integer arrowId : arrowToContentMap.keySet()) {
            ImageView arrow = findViewById(arrowId);
            arrow.setOnClickListener(v -> onArrowClick(arrowId));
        }
    }
    private void setupCardClickListener(int cardId, int contentId, int arrowId) {
        View card = findViewById(cardId);
        card.setOnClickListener(v -> toggleCard(contentId, arrowId));
    }

    // 卡片点击处理方法
    private void toggleCard(int contentId, int arrowId) {
        View content = contentViewMap.get(contentId);
        ImageView arrow = arrowViewMap.get(arrowId);

        if (content != null && arrow != null) {
            boolean isVisible = content.getVisibility() == View.VISIBLE;

            if (isVisible) {
                content.setVisibility(View.GONE);
                arrow.animate().rotation(0).setDuration(300).start();
            } else {
                content.setVisibility(View.VISIBLE);
                arrow.animate().rotation(180).setDuration(300).start();
            }
        }
    }
    // 箭头点击处理方法
    private void onArrowClick(int arrowId) {
        Integer contentId = arrowToContentMap.get(arrowId);
        if (contentId != null) {
            toggleCard(contentId, arrowId);
        }
    }
}
