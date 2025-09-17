package com.yang.androiddemolog.activityActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class WhatAreYouDoingActivity2 extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isLooping = false;
    private static final String TAG = "AudioPlayer"; // 定义日志标签

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatareyoudoing);

        Log.d(TAG, "onCreate: Activity创建开始");

        // 初始化按钮
        View btnLoop = findViewById(R.id.btn_loop);
        View btnPause = findViewById(R.id.btn_pause);
        View btnBack = findViewById(R.id.btn_back);

        // 初始化媒体播放器
        initializeMediaPlayer();

        // 循环播放
        btnLoop.setOnClickListener(v -> {
            Log.d(TAG, "循环按钮被点击");
            toggleLoopPlayback();
        });

        // 暂停
        btnPause.setOnClickListener(v -> {
            Log.d(TAG, "暂停按钮被点击");
            togglePause();
        });

        // 返回
        btnBack.setOnClickListener(v -> {
            Log.i(TAG, "返回按钮被点击，结束Activity");
            finish();
        });

        Log.d(TAG, "onCreate: Activity创建完成");
    }

    /**
     * 初始化播放器
     */
    private void initializeMediaPlayer() {
        try {
            // 创建MediaPlayer实例
            mediaPlayer = MediaPlayer.create(this, R.raw.whatareyoudoing);
            Log.d(TAG, "MediaPlayer实例创建完成");

            if (mediaPlayer == null) {
                Toast.makeText(this, "音频文件加载失败", Toast.LENGTH_SHORT).show();
                return;
            }

            // 设置播放完成监听器
            mediaPlayer.setOnCompletionListener(mp -> {
                Log.d(TAG, "音频播放完成，当前循环模式: " + isLooping);
                if (isLooping) {
                    // 如果是循环模式，重新开始播放
                    mediaPlayer.start();
                    Log.i(TAG, "循环播放重新开始");
                    Toast.makeText(WhatAreYouDoingActivity2.this, "循环播放中...", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "单次播放结束，停止播放");
                }
            });

            // 开始播放
            mediaPlayer.start();
            Log.i(TAG, "音频开始播放");
            Toast.makeText(this, "开始播放音频", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "音频播放失败: " + e.getMessage(), e);
            Toast.makeText(this, "音频播放失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        Log.d(TAG, "initializeMediaPlayer: 媒体播放器初始化完成");
    }

    /**
     * 循环播放声音
     */
    private void toggleLoopPlayback() {
        if (mediaPlayer != null) {
            isLooping = !isLooping;
            if (isLooping) {
                Log.i(TAG, "循环模式已开启");
                Toast.makeText(this, "已开启循环播放", Toast.LENGTH_SHORT).show();
                // 如果当前暂停了，重新开始播放
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    Log.d(TAG, "循环模式开启后重新开始播放");
                }
            } else {
                Log.i(TAG, "循环模式已关闭");
                Toast.makeText(this, "已关闭循环播放", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "toggleLoopPlayback: mediaPlayer为null，无法切换循环模式");
        }
    }

    /**
     * 暂停播放声音
     */
    private void togglePause() {
        Log.d(TAG, "togglePause: 切换播放/暂停状态");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                Log.i(TAG, "音频已暂停");
                Toast.makeText(this, "已暂停", Toast.LENGTH_SHORT).show();
            } else {
                mediaPlayer.start();
                Log.i(TAG, "音频继续播放");
                Toast.makeText(this, "继续播放", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "togglePause: mediaPlayer为null，无法切换播放状态");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity进入暂停状态");

        // 当Activity不可见时暂停播放
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Log.i(TAG, "Activity暂停，音频已暂停");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity恢复显示");

        // 当Activity恢复时继续播放（如果不是暂停状态）
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && isLooping) {
            mediaPlayer.start();
            Log.i(TAG, "Activity恢复，音频继续播放");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity销毁开始");

        // 释放MediaPlayer资源
        if (mediaPlayer != null) {
            Log.d(TAG, "开始释放MediaPlayer资源");
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    Log.d(TAG, "MediaPlayer停止播放");
                }
                mediaPlayer.release();
                Log.i(TAG, "MediaPlayer资源已释放");
                mediaPlayer = null;
            } catch (Exception e) {
                Log.e(TAG, "释放MediaPlayer资源时出错: " + e.getMessage(), e);
            }
        }

        Log.d(TAG, "onDestroy: Activity销毁完成");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Activity开始可见");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity停止可见");
    }
}
