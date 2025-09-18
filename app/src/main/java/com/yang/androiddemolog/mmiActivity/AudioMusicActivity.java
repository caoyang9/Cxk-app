package com.yang.androiddemolog.mmiActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class AudioMusicActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Button btnPlayPause, btnStop, btnSwitchOutput;
    private boolean isPlaying = false;
    private boolean isSpeakerMode = true; // 默认使用喇叭播放

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiomusic);

        // 初始化视图
        initViews();

        // 初始化MediaPlayer
        initMediaPlayer();

        // 设置按钮点击监听器
        setupButtonListeners();
    }

    private void initViews() {
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnStop = findViewById(R.id.btn_stop);
        btnSwitchOutput = findViewById(R.id.btn_switch_output);
    }

    private void initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.whatareyoudoingdj);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(false);

        // 添加播放完成监听器
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 音乐播放完成时调用
                isPlaying = false;
                btnPlayPause.setText("播放");
            }
        });

        // 添加错误监听器
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                resetMediaPlayer();
                return true;
            }
        });
    }

    private void setupButtonListeners() {
        // 播放/暂停按钮
        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                pauseMusic();
            } else {
                playMusic();
            }
        });
        // 停止按钮
        btnStop.setOnClickListener(v -> stopMusic());
        // 切换输出设备按钮
        btnSwitchOutput.setOnClickListener(v -> switchAudioOutput());
    }

    private void playMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.start();
                isPlaying = true;
                btnPlayPause.setText("暂停");
            } catch (IllegalStateException e) {
                // 如果MediaPlayer状态异常，重新初始化
                resetMediaPlayer();
                playMusic();
            }
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlayPause.setText("播放");
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            isPlaying = false;
            btnPlayPause.setText("播放");
            // 重置MediaPlayer以便重新播放
            resetMediaPlayer();
        }
    }

    private void resetMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        initMediaPlayer();
    }

    private void switchAudioOutput() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        if (isSpeakerMode) {
            // 切换到听筒模式
            int mode = audioManager.getMode();
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            mode = audioManager.getMode();
            audioManager.setSpeakerphoneOn(false);
            isSpeakerMode = false;
            btnSwitchOutput.setText("切换到喇叭");

            // 暂停当前播放（如果需要）
            if (isPlaying) {
                pauseMusic();
            }

            // 重新设置音频流类型
            resetMediaPlayerWithStreamType(AudioManager.STREAM_VOICE_CALL);

        } else {
            // 切换到喇叭模式
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            isSpeakerMode = true;
            btnSwitchOutput.setText("切换到听筒");

            // 暂停当前播放（如果需要）
            if (isPlaying) {
                pauseMusic();
            }

            // 重新设置音频流类型
            resetMediaPlayerWithStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    private void resetMediaPlayerWithStreamType(int streamType) {
        // 保存当前播放位置
        int currentPosition = 0;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
        }

        // 重置MediaPlayer
        resetMediaPlayer();
        mediaPlayer.setAudioStreamType(streamType);

        // 如果之前正在播放，重新开始播放
        if (isPlaying) {
            try {
                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
            } catch (IllegalStateException e) {
                Toast.makeText(this, "切换输出设备失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 当Activity失去焦点时暂停播放
        if (isPlaying) {
            pauseMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();

        // 恢复音频设置
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
