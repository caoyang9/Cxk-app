package com.yang.androiddemolog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.environmentanddebug.HelloWorldActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    private final Map<Integer, Integer> arrowToContentMap = new HashMap<>();
    private final Map<Integer, ImageView> arrowViewMap = new HashMap<>();
    private final Map<Integer, View> contentViewMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // 初始化映射关系
        initMappings();

        // 设置点击监听器
        setupClickListeners();

        // 设置底部按钮
        findViewById(R.id.btnLanguage2).setOnClickListener(v -> toggleLanguage());
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
        int[] buttonIds = {
                R.id.btn2HelloWorld, R.id.btnImage, R.id.btnTouch,
                R.id.btnPlay, R.id.btnRecord, R.id.btnVolume,
                R.id.btnWifi, R.id.btnBluetooth, R.id.btnMobileData,
                R.id.btnAccelerometer, R.id.btnGyroscope, R.id.btnCompass,
                R.id.btnGps, R.id.btnNfc, R.id.btnFingerprint
        };
        // card1 button1
        findViewById(R.id.btn2HelloWorld).setOnClickListener(v ->
                startActivity(new Intent(MainActivity2.this, HelloWorldActivity.class)));
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
}
