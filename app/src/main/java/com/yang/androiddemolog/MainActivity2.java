package com.yang.androiddemolog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.activityActivity.WhatAreYouDoingActivity2;
import com.yang.androiddemolog.environmentanddebug.HelloWorldActivity;
import com.yang.androiddemolog.environmentanddebug.LogActivity;
import com.yang.androiddemolog.interaction.BasketballActivity2;
import com.yang.androiddemolog.interaction.IntroductionActivity2;
import com.yang.androiddemolog.uiActivity.DanceActivity2;
import com.yang.androiddemolog.uiActivity.RapActivity2;
import com.yang.androiddemolog.uiActivity.SingActivity2;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    private String currentLanguage = "zh";
    private Button btn2SwitchLanguage;
    private final Map<Integer, Integer> arrowToContentMap = new HashMap<>();
    private final Map<Integer, ImageView> arrowViewMap = new HashMap<>();
    private final Map<Integer, View> contentViewMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // 获取当前应用语言
        currentLanguage = getSavedLanguage();
        // 定位语言转换按钮
        btn2SwitchLanguage = findViewById(R.id.btn2SwitchLanguage);
        // 更新按钮文本
        updateSwitchButtonText();
        // 切换应用语言逻辑
        btn2SwitchLanguage.setOnClickListener(v -> switchLanguage());

        // 初始化映射关系
        initMappings();

        // 设置点击监听器
        setupClickListeners();

        // 设置底部按钮
        findViewById(R.id.btnNext2).setOnClickListener(v -> goToNextChapter());

        setupFunctionButtons();
    }

    private void initMappings() {
        // 初始化箭头到内容的映射
        arrowToContentMap.put(R.id.arrow1, R.id.content1);
        arrowToContentMap.put(R.id.arrow2, R.id.content2);
        arrowToContentMap.put(R.id.arrow3, R.id.content3);
        arrowToContentMap.put(R.id.arrow4, R.id.content4);
        arrowToContentMap.put(R.id.arrow5, R.id.content5);
        arrowToContentMap.put(R.id.arrow6, R.id.content6);
        arrowToContentMap.put(R.id.arrow7, R.id.content7);
        arrowToContentMap.put(R.id.arrow8, R.id.content8);
        arrowToContentMap.put(R.id.arrow9, R.id.content9);
        arrowToContentMap.put(R.id.arrow10, R.id.content10);

        // 初始化箭头视图映射
        arrowViewMap.put(R.id.arrow1, findViewById(R.id.arrow1));
        arrowViewMap.put(R.id.arrow2, findViewById(R.id.arrow2));
        arrowViewMap.put(R.id.arrow3, findViewById(R.id.arrow3));
        arrowViewMap.put(R.id.arrow4, findViewById(R.id.arrow4));
        arrowViewMap.put(R.id.arrow5, findViewById(R.id.arrow5));
        arrowViewMap.put(R.id.arrow6, findViewById(R.id.arrow6));
        arrowViewMap.put(R.id.arrow7, findViewById(R.id.arrow7));
        arrowViewMap.put(R.id.arrow8, findViewById(R.id.arrow8));
        arrowViewMap.put(R.id.arrow9, findViewById(R.id.arrow9));
        arrowViewMap.put(R.id.arrow10, findViewById(R.id.arrow10));

        // 初始化内容视图映射
        contentViewMap.put(R.id.content1, findViewById(R.id.content1));
        contentViewMap.put(R.id.content2, findViewById(R.id.content2));
        contentViewMap.put(R.id.content3, findViewById(R.id.content3));
        contentViewMap.put(R.id.content4, findViewById(R.id.content4));
        contentViewMap.put(R.id.content5, findViewById(R.id.content5));
        contentViewMap.put(R.id.content6, findViewById(R.id.content6));
        contentViewMap.put(R.id.content7, findViewById(R.id.content7));
        contentViewMap.put(R.id.content8, findViewById(R.id.content8));
        contentViewMap.put(R.id.content9, findViewById(R.id.content9));
        contentViewMap.put(R.id.content10, findViewById(R.id.content10));
    }

    private void setupClickListeners() {
        // 为每个卡片设置点击监听器
        setupCardClickListener(R.id.card1, R.id.content1, R.id.arrow1);
        setupCardClickListener(R.id.card2, R.id.content2, R.id.arrow2);
        setupCardClickListener(R.id.card3, R.id.content3, R.id.arrow3);
        setupCardClickListener(R.id.card4, R.id.content4, R.id.arrow4);
        setupCardClickListener(R.id.card5, R.id.content5, R.id.arrow5);
        setupCardClickListener(R.id.card6, R.id.content6, R.id.arrow6);
        setupCardClickListener(R.id.card7, R.id.content7, R.id.arrow7);
        setupCardClickListener(R.id.card8, R.id.content8, R.id.arrow8);
        setupCardClickListener(R.id.card9, R.id.content9, R.id.arrow9);
        setupCardClickListener(R.id.card10, R.id.content10, R.id.arrow10);

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

    // 箭头点击处理方法
    private void onArrowClick(int arrowId) {
        Integer contentId = arrowToContentMap.get(arrowId);
        if (contentId != null) {
            toggleCard(contentId, arrowId);
        }
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

    private void setupFunctionButtons() {
        // 设置所有功能按钮的点击事件
        // card1
        findViewById(R.id.btn2HelloWorld).setOnClickListener(v ->
                startActivity(new Intent(MainActivity2.this, HelloWorldActivity.class)));
        findViewById(R.id.btn2Log).setOnClickListener(v ->
                startActivity(new Intent(MainActivity2.this, LogActivity.class)));
        // card2
        findViewById(R.id.btn2Play).setOnClickListener(v ->
                startActivity(new Intent(MainActivity2.this, WhatAreYouDoingActivity2.class)));
        // card3
        findViewById(R.id.btn2_3_1).setOnClickListener(v ->
                startActivity(new Intent(MainActivity2.this, SingActivity2.class)));
        findViewById(R.id.btn2_3_2).setOnClickListener(v ->
                startActivity(new Intent(MainActivity2.this, DanceActivity2.class)));
        findViewById(R.id.btn2_3_3).setOnClickListener(v ->
                startActivity(new Intent(MainActivity2.this, RapActivity2.class)));
        // card4
        findViewById(R.id.btn2_4_1).setOnClickListener(v ->
                startActivity(new Intent(MainActivity2.this, IntroductionActivity2.class)));
        findViewById(R.id.btn2_4_2).setOnClickListener(v ->
                startActivity(new Intent(MainActivity2.this, BasketballActivity2.class)));
    }

    private void toggleLanguage() {
        showToast("切换中英文");
    }

    private void goToNextChapter() {
        showToast("下一章");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取当前应用语言
     * @return
     */
    private String getSavedLanguage() {
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        return preferences.getString("language", "zh");
    }

    /**
     * 切换语言
     */
    private void switchLanguage() {
        if(currentLanguage.equals("zh")){
            setAppLanguage("en");
            currentLanguage = "en";
        }else {
            setAppLanguage("zh");
            currentLanguage = "zh";
        }
        // 重启Activity
        recreate();
    }

    /**
     * 设置应用语言
     * @param languageCode
     */
    private void setAppLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // 保存语言设置到SharedPreference
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("language", languageCode);
        edit.apply();
    }

    private void updateSwitchButtonText() {
        if (currentLanguage.equals("zh")) {
            btn2SwitchLanguage.setText(getString(R.string.trans3));
        } else {
            btn2SwitchLanguage.setText(getString(R.string.trans3));
        }
    }
}
