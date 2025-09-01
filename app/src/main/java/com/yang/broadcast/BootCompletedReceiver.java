package com.yang.broadcast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.yang.androiddemolog.MainActivity;
import com.yang.androiddemolog.R;
import com.yang.constant.ChannelConstants;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Log.i(TAG, "Boot completed received.");
            // 创建需要启动Activity的Intent
//            Intent activityIntent = new Intent(context, MainActivity.class);
//            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(activityIntent);
            createNotificationChannel(context);

            // 创建点击通知后启动Activity的Intent
            Intent activityIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // 构建通知
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ChannelConstants.SERVICE_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification) // 必须设置，否则通知无效
                    .setContentTitle("App Started")
                    .setContentText("Device boot completed. Tap to open the app.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent) // 设置点击意图
                    .setAutoCancel(true); // 点击后自动移除通知

            // 获取NotificationManager并显示通知
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1001, builder.build()); // 1001是一个唯一的通知ID
        }
    }

    private void createNotificationChannel(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Boot Completed";
            String description = "Notification when device finishes booting";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // 创建通知管道对象
            NotificationChannel channel = new NotificationChannel(ChannelConstants.SERVICE_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager systemService = context.getSystemService(NotificationManager.class);
            systemService.createNotificationChannel(channel);
        }
    }
}
