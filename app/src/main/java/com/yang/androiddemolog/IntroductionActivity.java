package com.yang.androiddemolog;

import static com.yang.constant.ChannelConstants.INTRODUCTION_CHANNEL_ID;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.snackbar.Snackbar;

public class IntroductionActivity extends AppCompatActivity {

    private VideoView videoView;
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        // 弹个提示
        showFanToast();
        createNotificationChannel();

        initViews();
        setupVideoPlayer();
    }

    private void initViews() {
        videoView = findViewById(R.id.videoView);
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // 添加三个按钮的点击事件
        Button btnToast = findViewById(R.id.btn_toast);
        Button btnDialog = findViewById(R.id.btn_dialog);
        Button btnNotification = findViewById(R.id.btn_notification);

        btnToast.setOnClickListener(v -> showCustomToast());
        btnDialog.setOnClickListener(v -> showCustomDialog());
        btnNotification.setOnClickListener(v -> sendNotification());
    }

    private void showCustomToast() {
        Toast.makeText(this, "toast：加油鸡哥！", Toast.LENGTH_SHORT).show();
    }

    private void showCustomDialog() {
        new AlertDialog.Builder(this)
                .setTitle("dialog：真爱粉提示")
                .setMessage("确认你是CXK的真爱粉吗？")
                .setPositiveButton("确认", (dialog, which) -> {
                    Toast.makeText(IntroductionActivity.this, "真爱粉认证成功！", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void sendNotification() {
        // 使用应用图标作为通知图标
        int iconResource = android.R.drawable.ic_dialog_info; // 使用系统默认图标

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, INTRODUCTION_CHANNEL_ID)
                .setSmallIcon(iconResource)
                .setContentTitle("CXK新动态")
                .setContentText("鸡你太美新专辑已发布，快来收听！")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        // 发送通知，同样的通知id会覆盖
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        Toast.makeText(this, "notification: ikun已守护", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CXK Channel";
            String description = "Channel for CXK notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(INTRODUCTION_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.deleteNotificationChannel(CHANNEL_ID);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setupVideoPlayer() {
        // 本地raw资源
        try {
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.cxk_introduction;
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
    protected void showFanToast() {
        // 获取当前Activity的根视图
        View rootView = findViewById(android.R.id.content);
        // 参数1：显示位置，参数2：文本内容，参数3：显示时长
        Snackbar snackbar = Snackbar.make(rootView, "温馨提示：我们是真爱粉", Snackbar.LENGTH_LONG);

        // 获取视图的参数并修改
        View snackbarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        // 显示在顶部，距离顶部400像素
        params.gravity = Gravity.TOP;
        params.topMargin = 400;
        snackbarView.setLayoutParams(params);

        // 可选：设置样式
        snackbarView.setBackgroundColor(Color.parseColor("#FF4081")); // 粉色
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);

        snackbar.show();
    }
}