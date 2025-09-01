package com.yang.androiddemolog;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WhatAreYouDoingActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isLooping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatareyoudoing);

        // 初始化按钮
        View btnLoop = findViewById(R.id.btn_loop);
        View btnPause = findViewById(R.id.btn_pause);
        View btnBack = findViewById(R.id.btn_back);

        // 初始化媒体播放器
        initializeMediaPlayer();

        // 循环播放
        btnLoop.setOnClickListener(v -> toggleLoopPlayback());
        // 暂停
        btnPause.setOnClickListener(v -> togglePause());
        // 返回
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * 初始化播放器
     */
    private void initializeMediaPlayer() {
        try {
            // 创建MediaPlayer实例
            mediaPlayer = MediaPlayer.create(this, R.raw.whatareyoudoing);

            if (mediaPlayer == null) {
                Toast.makeText(this, "音频文件加载失败", Toast.LENGTH_SHORT).show();
                return;
            }

            // 设置播放完成监听器
            mediaPlayer.setOnCompletionListener(mp -> {
                if (isLooping) {
                    // 如果是循环模式，重新开始播放
                    mediaPlayer.start();
                    Toast.makeText(WhatAreYouDoingActivity.this, "循环播放中...", Toast.LENGTH_SHORT).show();
                }
            });

            // 开始播放
            mediaPlayer.start();
            Toast.makeText(this, "开始播放音频", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "音频播放失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 循环播放声音
     */
    private void toggleLoopPlayback() {
        if (mediaPlayer != null) {
            isLooping = !isLooping;
            if (isLooping) {
                Toast.makeText(this, "已开启循环播放", Toast.LENGTH_SHORT).show();
                // 如果当前暂停了，重新开始播放
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            } else {
                Toast.makeText(this, "已关闭循环播放", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 暂停播放声音
     */
    private void togglePause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                Toast.makeText(this, "已暂停", Toast.LENGTH_SHORT).show();
            } else {
                mediaPlayer.start();
                Toast.makeText(this, "继续播放", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 当Activity不可见时暂停播放
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 当Activity恢复时继续播放（如果不是暂停状态）
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && isLooping) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放MediaPlayer资源
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
