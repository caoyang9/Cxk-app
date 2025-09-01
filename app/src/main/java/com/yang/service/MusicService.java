package com.yang.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.yang.androiddemolog.R;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    private final IBinder binder = new MusicBinder();

    public class MusicBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Music Service", "onBind()");
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Music Service", "onCreate()");

        // 初始化MediaPlayer，但不立即播放
        initializeMediaPlayer();
    }

    private void initializeMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.whatareyoudoingdj);
        mediaPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Music Service", "onStartCommand");
        // 确保服务在后台持续运行
        return START_STICKY;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    /**
     * 播放音乐
     */
    public void playMusic(){
        if (mediaPlayer == null) {
            initializeMediaPlayer();
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pauseMusic(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    public void stopMusic(){
        if(mediaPlayer != null){
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            // 重置MediaPlayer到初始状态，但不释放资源
            mediaPlayer.reset();
            initializeMediaPlayer(); // 重新初始化，准备下一次播放
//            // 停止自身服务
//            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Music Service", "onDestroy");
        // 释放MediaPlayer资源
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}