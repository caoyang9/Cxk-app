package com.yang.androiddemolog.mmiActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.yang.androiddemolog.R;

import java.util.HashMap;


/**
 * USB
 */
public class USBActivity extends AppCompatActivity {

    private TextView statusText;
    private UsbManager usbManager;

    // 定义USB相关动作
    private static final String[] USB_ACTIONS = {
            UsbManager.ACTION_USB_DEVICE_ATTACHED,
            UsbManager.ACTION_USB_DEVICE_DETACHED
    };

    // USB状态广播接收器
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                updateStatus("USB已连接", true);
                showToast("USB设备已连接");

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                updateStatus("USB已断开", false);
                showToast("USB设备已断开");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb);

        // 初始化视图
        statusText = findViewById(R.id.statusText);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        // 注册广播接收器
        registerUSBReceiver();
        // 检查初始USB状态
        checkInitialUSBState();
    }

    private void registerUSBReceiver() {
        IntentFilter filter = new IntentFilter();
        for (String action : USB_ACTIONS) {
            filter.addAction(action);
        }
        registerReceiver(usbReceiver, filter);
    }

    private void checkInitialUSBState() {
        // 检查当前已连接的USB设备
        if (usbManager != null) {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            if (deviceList != null && !deviceList.isEmpty()) {
                updateStatus("USB已连接", true);
            } else {
                updateStatus("USB已断开", false);
            }
        }
    }

    private void updateStatus(String status, boolean isConnected) {
        if (statusText != null) {
            statusText.setText(status);
            statusText.setTextColor(isConnected ?
                    getResources().getColor(android.R.color.holo_green_dark) :
                    getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册广播接收器
        if (usbReceiver != null) {
            unregisterReceiver(usbReceiver);
        }
    }
}
