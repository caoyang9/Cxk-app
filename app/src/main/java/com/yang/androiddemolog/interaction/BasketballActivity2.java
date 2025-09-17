package com.yang.androiddemolog.interaction;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.yang.androiddemolog.R;

public class BasketballActivity2 extends AppCompatActivity {
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basketball);

        showFanToast();

        initViews();
        setupVideoPlayer();

        // 接收数据
        Intent intent = getIntent();
        boolean fromRap = intent.getBooleanExtra("来自rap的自动跳转", false);
        String welcomeMsg = intent.getStringExtra("欢迎：");

        // 显示数据
        TextView textView = findViewById(R.id.textView1);
        if (fromRap && welcomeMsg != null) {
            textView.setText("来自Rap: " + welcomeMsg);
        }
    }

    private void initViews() {
        videoView = findViewById(R.id.videoView);
        Button btnBack = findViewById(R.id.btn_back);

        // 长按点击事件
        btnBack.setOnLongClickListener(v -> {
            // 长按逻辑：显示确认对话框或直接返回
            showLongPressDialog();
            return true; // 返回true表示事件已处理
        });
    }

    private void showLongPressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认返回")
                .setMessage("确定要返回主界面吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    finish(); // 确认后关闭当前Activity
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss(); // 取消对话框
                })
                .show();
    }

    private void setupVideoPlayer() {
        try {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.cxk_basketball;
            Uri uri = Uri.parse(videoPath);
            videoView.setVideoURI(uri);

            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);

            videoView.start();
        } catch (Exception e) {
            Toast.makeText(this, "视频加载失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    /**
     * 温馨提示
     */
    protected void showFanToast(){
        View rootView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, "温馨提示：我们是真爱粉", Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.topMargin = 400; // 调整这个值来控制位置
        snackbarView.setLayoutParams(params);

        // 可选：设置样式
        snackbarView.setBackgroundColor(Color.parseColor("#FF4081")); // 粉色
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);

        snackbar.show();
    }
}
