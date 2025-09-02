package com.yang.androiddemolog;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.snackbar.Snackbar;

public class RapActivity extends AppCompatActivity {
    private VideoView videoView;

    /**
     * 点击返回键时，发出的广播动作
     */
    private static final String BACK_BUTTON_ACTION = "com.yang.rapActivity.BACK_BUTTON_PRESSED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rap);

        showFanToast();

        initViews();
        setupVideoPlayer();
    }

    private void initViews() {
        videoView = findViewById(R.id.videoView);
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            sendBackButtonBroadcast();
            finish();
        });
    }

    /**
     * 点击返回按钮发广播消息
     */
    private void sendBackButtonBroadcast() {
        Intent broadIntent = new Intent(BACK_BUTTON_ACTION);
        broadIntent.putExtra("key0", getString(R.string.broadcast_0));
        broadIntent.putExtra("key1", getString(R.string.broadcast_1));

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadIntent);
    }

    private void setupVideoPlayer() {
        try {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.cxk_rap;
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
