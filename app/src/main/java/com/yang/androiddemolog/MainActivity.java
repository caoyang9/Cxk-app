package com.yang.androiddemolog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.constant.ChannelConstants;
import com.yang.service.MyForegroundService;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnSing, btnDance, btnRap, btnBasketball;

    private Button btnIntroduction, btnWhatAreYouDoing, btnWhatAreYouDoingDJ, btnJustBecause;

    private Button btnSwitchLanguage;

    private String currentLanguage = "zh";

    private static final int REQUEST_CODE = 1;
    private TextView tvResult;

    private static final String TAG = "MainActivity";

    private MyForegroundService foregroundService;
    private boolean serviceBound = false;

    private NetworkChangeReceiver networkReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化广播接收器和过滤器
        networkReceiver = new NetworkChangeReceiver();
        intentFilter = new IntentFilter();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        }

        // 创建ServiceChannel
        createNotificationChannel();
        // 初始化4个ServiceButton
        initServiceButtons();

        // 初始化识别按钮并绑定点击事件
        initViews();
        setupClickListeners();

        // 数据发送
        tvResult = findViewById(R.id.tv_result);
        Button btnOpen = findViewById(R.id.btn_open);

        btnOpen.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TargetActivity.class);
            intent.putExtra("message", getString(R.string.trans0));
            startActivityForResult(intent, REQUEST_CODE);
        });

        // 获取当前应用语言
        currentLanguage = getSavedLanguage();
        // 定位语言转换按钮
        btnSwitchLanguage = findViewById(R.id.btn_switch_language);
        // 更新按钮文本
        updateSwitchButtonText();
        // 切换应用语言逻辑
        btnSwitchLanguage.setOnClickListener(v -> switchLanguage());
    }

    /**
     * 接收返回结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String result = data.getStringExtra("result");
                tvResult.setText(getString(R.string.trans1) + result);
            } else {
                tvResult.setText(getString(R.string.trans2));
            }
        }
    }

    /**
     * 获取当前应用语言
     * @return
     */
    private String getSavedLanguage() {
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        return preferences.getString("language", "zh");
    }

    /**
     * 更新切换语言上的按钮文本
     */
    private void updateSwitchButtonText() {
        if (currentLanguage.equals("zh")) {
            btnSwitchLanguage.setText(getString(R.string.trans3));
        } else {
            btnSwitchLanguage.setText(getString(R.string.trans3));
        }
    }

    /**
     * 该方法在onCreate前被调用，确保在Activity前配置好语言环境
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        // 应用保存的语言设置
        SharedPreferences preferences = newBase.getSharedPreferences("AppSettings", MODE_PRIVATE);
        String language = preferences.getString("language", "zh");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }

    /**
     * 切换语言
     */
    private void switchLanguage() {
        if(currentLanguage.equals("zh")){
            setAppLanguage("en");
            currentLanguage = "en";
        }else {
            setAppLanguage("zh");
            currentLanguage = "zh";
        }
        // 重启Activity
        recreate();
    }

    /**
     * 设置应用语言
     * @param languageCode
     */
    private void setAppLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // 保存语言设置到SharedPreference
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("language", languageCode);
        edit.apply();
    }

    private void initViews() {
        btnSing = findViewById(R.id.btn_sing);
        btnDance = findViewById(R.id.btn_dance);
        btnRap = findViewById(R.id.btn_rap);
        btnBasketball = findViewById(R.id.btn_basketball);
        btnIntroduction = findViewById(R.id.btn_introduction);
        btnWhatAreYouDoing = findViewById(R.id.btn_whatAreYouDoing);
        btnJustBecause = findViewById(R.id.btn_justBecause);
        btnWhatAreYouDoingDJ = findViewById(R.id.btn_whatAreYouDoingDJ);
    }

    /**
     * 跳转到新的Activity
     */
    private void setupClickListeners() {
        btnSing.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SingActivity.class)));
        btnDance.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DanceActivity.class)));
        btnRap.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RapActivity.class)));
        btnBasketball.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BasketballActivity.class)));
        btnIntroduction.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, IntroductionActivity.class)));
        btnWhatAreYouDoing.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WhatAreYouDoingActivity.class)));
        btnJustBecause.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, JustBecauseActivity.class)));
        btnWhatAreYouDoingDJ.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WhatAreYouDoingDJActivity.class)));
    }

    /**
     * 创建Service的NotificationChannel
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyForegroundService";
            String description = "Channel for foreground service notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(ChannelConstants.SERVICE_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Service连接回调
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected - 服务连接成功");
            MyForegroundService.ForegroundBinder foregroundBinder = (MyForegroundService.ForegroundBinder) service;
            foregroundService = foregroundBinder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected - 服务连接断开");
            serviceBound = false;
        }
    };

    /**
     * 启动服务
     */
    private void startService() {
        Log.d(TAG, "点击startService按钮");
        Intent intent = new Intent(this, MyForegroundService.class);
        startService(intent);
        Toast.makeText(this, getString(R.string.trans4), Toast.LENGTH_SHORT).show();
    }

    /**
     * 停止服务
     */
    private void stopService() {
        Log.d(TAG, "点击stopService按钮");
        Intent intent = new Intent(this, MyForegroundService.class);
        stopService(intent);
        Toast.makeText(this, getString(R.string.trans5), Toast.LENGTH_SHORT).show();
    }

    /**
     * 绑定服务
     */
    private void bindService() {
        Log.d(TAG, "点击bindService按钮");
        if (!serviceBound) {
            Intent intent = new Intent(this, MyForegroundService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            Toast.makeText(this, getString(R.string.trans6), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.trans7), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 解绑服务
     */
    private void unbindService() {
        Log.d(TAG, "点击unbindService按钮");
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
            Toast.makeText(this, getString(R.string.trans8), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.trans9), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 为按钮设置点击事件
     */
    private void initServiceButtons() {
        findViewById(R.id.btn_start_service).setOnClickListener(v -> startService());
        findViewById(R.id.btn_stop_service).setOnClickListener(v -> stopService());
        findViewById(R.id.btn_bind_service).setOnClickListener(v -> bindService());
        findViewById(R.id.btn_unbind_service).setOnClickListener(v -> unbindService());
    }

    /**
     * 内部广播接收器类
     */
    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            String message;
            if (activeNetwork != null && activeNetwork.isConnected()) {
                // 网络已连接
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    message = getString(R.string.toast_net0);
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    message = getString(R.string.toast_net1);;
                } else {
                    message = getString(R.string.toast_net2);;
                }
            } else {
                // 网络断开连接
                message = getString(R.string.toast_net3);;
            }

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Lifecycle", "MainActivity-onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Lifecycle", "MainActivity-onResume()");
        // 注册广播接收器
        registerReceiver(networkReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Lifecycle", "MainActivity-onPause()");
        // 取消注册广播接收器，避免内存泄漏
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Lifecycle", "MainActivity-onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("Lifecycle", "MainActivity-onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Lifecycle", "MainActivity-onDestroy()");
        // 避免内存泄漏
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }
}