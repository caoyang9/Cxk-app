package com.yang.androiddemolog.mmiActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class OTGActivity extends AppCompatActivity {
    private TextView tvConnectionStatus;
    private TextView tvStorageInfo;
    private ListView lvFiles;
    private TextView tvEmpty;

    private StorageManager storageManager;
    private UsbManager usbManager;
    private List<StorageVolume> storageVolumes;
    private ArrayAdapter<String> fileAdapter;
    private List<String> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otg);

        initViews();
        initManagers();
        checkOTGConnection();
    }

    private void initViews() {
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus);
        tvStorageInfo = findViewById(R.id.tvStorageInfo);
        lvFiles = findViewById(R.id.lvFiles);
        tvEmpty = findViewById(R.id.tvEmpty);

        fileList = new ArrayList<>();
        fileAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        lvFiles.setAdapter(fileAdapter);
    }

    private void initManagers() {
        storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    private void checkOTGConnection() {
        // 检查USB设备连接:cite[5]
        if (usbManager != null && usbManager.getDeviceList() != null) {
            boolean hasUsbDevice = false;
            StringBuilder usbInfo = new StringBuilder();

            for (UsbDevice device : usbManager.getDeviceList().values()) {
                hasUsbDevice = true;
                usbInfo.append("USB设备: ")
                        .append(device.getDeviceName())
                        .append(", 厂商ID: ")
                        .append(device.getVendorId())
                        .append(", 产品ID: ")
                        .append(device.getProductId())
                        .append("\n");
            }

            if (hasUsbDevice) {
                tvConnectionStatus.setText("OTG状态: 已连接");
                scanStorageVolumes();
            } else {
                tvConnectionStatus.setText("OTG状态: 未检测到设备");
                tvStorageInfo.setText("存储设备信息: 无");
                showEmptyView();
            }
        } else {
            tvConnectionStatus.setText("OTG状态: 不支持或未连接");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void scanStorageVolumes() {
        if (storageManager == null) return;

        storageVolumes = storageManager.getStorageVolumes();
        StringBuilder storageInfo = new StringBuilder();
        boolean foundOTGVolume = false;

        for (StorageVolume volume : storageVolumes) {
            // 检测可移动存储设备（U盘/SD卡）:cite[6]
            if (volume.isRemovable()) {
                foundOTGVolume = true;
                storageInfo.append("描述: ").append(volume.getDescription(this))
                        .append("\n状态: ").append(volume.getState())
                        .append("\n可移动: ").append(volume.isRemovable())
                        .append("\n主存储: ").append(volume.isPrimary())
                        .append("\nUUID: ").append(volume.getUuid())
                        .append("\n\n");

                // 尝试列出文件
                listFilesInVolume(volume);
            }
        }

        if (foundOTGVolume) {
            tvStorageInfo.setText("存储设备信息:\n" + storageInfo.toString());
        } else {
            tvStorageInfo.setText("存储设备信息: 未找到可移动存储");
            showEmptyView();
        }
    }

    private void listFilesInVolume(StorageVolume volume) {
        try {
            // 使用反射获取StorageVolume的路径:cite[9]
            @SuppressLint("SoonBlockedPrivateApi") Method getPathMethod = StorageVolume.class.getDeclaredMethod("getPath");
            getPathMethod.setAccessible(true);
            String volumePath = (String) getPathMethod.invoke(volume);

            if (volumePath != null) {
                File rootDir = new File(volumePath);
                listFilesInDirectory(rootDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 备用方案：尝试通过Environment类获取:cite[3]
            try {
                File[] externalFilesDirs = getExternalFilesDirs(null);
                for (File dir : externalFilesDirs) {
                    if (dir != null && dir.getAbsolutePath().contains("USB") ||
                            dir.getAbsolutePath().contains("otg")) {
                        listFilesInDirectory(dir.getParentFile());
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(this, "无法访问存储设备", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void listFilesInDirectory(File directory) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            showEmptyView();
            return;
        }

        File[] files = directory.listFiles();
        fileList.clear();

        if (files != null && files.length > 0) {
            for (File file : files) {
                String fileInfo = file.getName();
                if (file.isDirectory()) {
                    fileInfo += "/ [目录]";
                } else {
                    fileInfo += " [" + file.length() + " 字节]";
                }
                fileList.add(fileInfo);
            }

            fileAdapter.notifyDataSetChanged();
            lvFiles.setVisibility(android.view.View.VISIBLE);
            tvEmpty.setVisibility(android.view.View.GONE);
        } else {
            showEmptyView();
        }
    }

    private void showEmptyView() {
        lvFiles.setVisibility(android.view.View.GONE);
        tvEmpty.setVisibility(android.view.View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 重新检查连接状态
        checkOTGConnection();
    }
}
