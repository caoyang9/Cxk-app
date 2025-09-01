package com.yang.androiddemolog;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yang.service.MusicService;


public class WhatAreYouDoingDJActivity extends AppCompatActivity {

    private MusicService musicService;
//    private boolean serviceBoundFlag = false;
    private Button btnPlayPause, btnStop;

    private ImageView ivMusicDisc; // 播放音乐封面的圆形图片
    private Animation rotationAnimation; // 旋转动画

    // 定义链接Service的回调
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 连接成功，获取 Service 实例
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
//            serviceBoundFlag = true;
            updatePlaybackState(); // 更新播放状态（包括旋转）
            Log.d("WhatAreYouDoingDJActivity", "onServiceConnected");
            Toast.makeText(WhatAreYouDoingDJActivity.this, "服务已连接", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
//            serviceBoundFlag = false;
            musicService = null;
            stopDiscRotation(); // 服务断开时停止旋转
        }
    };

    // 根据播放状态更新按钮文字
    private void updatePlaybackState() {
        if (musicService != null && musicService.isPlaying()) {
            btnPlayPause.setText("暂停");
            startDiscRotation();
        } else {
            btnPlayPause.setText("播放");
            stopDiscRotation();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatareyoudoingdj);

        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnStop = findViewById(R.id.btn_stop);
        ivMusicDisc  = findViewById(R.id.iv_music_disc);

        // 加载旋转动画
        rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);

        // 1. 启动Service（后台运行）
        Intent startIntent = new Intent(this, MusicService.class);
        startService(startIntent);
        Log.d("WhatAreYouDoingDJActivity", "startService");

        // 2. 绑定Service（获得控制它的接口）
        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
        Log.d("WhatAreYouDoingDJActivity", "bindService");

        // 播放/暂停按钮点击事件
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService != null) {
                    if (musicService.isPlaying()) {
                        musicService.pauseMusic();
                    } else {
                        musicService.playMusic();
                    }
                    updatePlaybackState();
                } else {
                    Toast.makeText(WhatAreYouDoingDJActivity.this, "服务未连接", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 停止按钮点击事件
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService != null) {
                    musicService.stopMusic();
//                    serviceBoundFlag = false;
                    updatePlaybackState();
                    Toast.makeText(WhatAreYouDoingDJActivity.this, "音乐已停止", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 开始图片旋转
    private void startDiscRotation() {
        if (ivMusicDisc != null) {
            ivMusicDisc.startAnimation(rotationAnimation);
        }
    }

    // 停止图片旋转
    private void stopDiscRotation() {
        if (ivMusicDisc != null) {
            ivMusicDisc.clearAnimation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        // 如果服务未绑定，尝试重新绑定
//        if (!serviceBoundFlag) {
//            Intent bindIntent = new Intent(this, MusicService.class);
//            bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 当Activity进入后台时，不解除绑定，保持服务运行
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止旋转
        stopDiscRotation();
        // 解除与 Service 的绑定
//        if (serviceBoundFlag) {
//            unbindService(serviceConnection);
//            serviceBoundFlag = false;
//        }
        unbindService(serviceConnection);
        // 停止服务，确保应用被清除时服务也停止
        Intent stopIntent = new Intent(this, MusicService.class);
        stopService(stopIntent);
    }
}