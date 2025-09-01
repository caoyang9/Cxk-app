package com.yang.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.yang.androiddemolog.R;
import com.yang.constant.ChannelConstants;

import java.util.Objects;

public class MyForegroundService extends Service {

    private static final String TAG = "MyForegroundService";
    private static final int NOTIFICATION_ID = 1;

    private Handler handler;
    private Runnable notificationRunnable;
    private int notificationCount = 0;

    private final IBinder binder = new ForegroundBinder();

    public class ForegroundBinder extends Binder {
        public MyForegroundService getService(){
            return MyForegroundService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() - 服务创建");

        // Looper.getMainLooper() 获得主线程的Looper
        handler = new Handler(Looper.getMainLooper());
        setupNotificationTask();
    }

    /**
     * 生命周期方法：每次startService()时调用
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() - 启动命令收到, startId: " + startId + ", flags: " + flags);
        // 启动前台服务
        startForegroundService();

        // 返回START_STICKY，确保服务被杀死后会自动重启
        return START_STICKY;
    }

    /**
     * 生命周期方法：绑定服务时调用
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() - 服务被绑定");
        // 返回null表示这是一个纯粹的启动服务，不支持绑定
        return binder;
    }

    /**
     * 生命周期方法：解绑服务时调用
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() - 服务解绑");
        return super.onUnbind(intent);
    }

    /**
     * 生命周期方法：服务重新绑定时调用
     */
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind() - 服务重新绑定");
    }

    /**
     * 生命周期方法：服务销毁时调用
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() - 服务销毁");

        // 停止发送通知的任务
        if (handler != null && notificationRunnable != null) {
            handler.removeCallbacks(notificationRunnable);
        }
    }

    /**
     * 启动前台服务
     */
    private void startForegroundService() {
        // 创建通知
        Notification notification = createNotification("服务正在运行中", "服务已启动");

        // 启动前台服务，且必须提供一个有效的Notification实例
        try {
            startForeground(NOTIFICATION_ID, notification);
        }catch (Exception e){
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        // 开始每10秒发送一次通知
        startNotificationTask();
    }

    /**
     * 创建通知
     * @param title
     * @param content
     * @return
     */
    private Notification createNotification(String title, String content) {
        return new NotificationCompat.Builder(this, ChannelConstants.SERVICE_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_notification) // 需要添加通知图标
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }

    /**
     * 设置通知任务
     */
    private void setupNotificationTask() {
        notificationRunnable = new Runnable() {
            @Override
            public void run() {
                notificationCount++;
                String content = "蔡徐坤给你发了 " + notificationCount + " 条私信";

                // 更新通知
                Notification notification = createNotification("前台服务运行中", content);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.notify(NOTIFICATION_ID, notification);

                Log.d(TAG, "发送通知: " + content);

                // 10秒后再次执行
                if (handler != null) {
                    handler.postDelayed(this, 10000);
                }
            }
        };
    }

    /**
     * 开始发送通知
     */
    private void startNotificationTask() {
        if (handler != null && notificationRunnable != null) {
            // 先移除之前的任务（如果有）
            handler.removeCallbacks(notificationRunnable);
            // 立即发送第一条通知，然后每10秒发送一次
            handler.post(notificationRunnable);
        }
    }
}
