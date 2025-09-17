package com.yang.androiddemolog.uiActivity;

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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.yang.androiddemolog.R;

public class SingActivity2 extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing);

        showFanToast();

        initViews();
        setupVideoPlayer();
    }

    private void initViews() {
        videoView = findViewById(R.id.videoView);
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupVideoPlayer() {
        // 本地raw资源
        try {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.cxk_sing;
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
