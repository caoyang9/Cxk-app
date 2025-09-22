package com.yang.androiddemolog.mmiActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yang.androiddemolog.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 音频录制界面
 */
public class MediaRecorderActivity extends AppCompatActivity {
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 100;
    private MediaRecorder mediaRecorder;
    private String outputFile;

    /**
     * 计时器
     */
    private Chronometer chronometer;
    private Button btnRecord, btnStop;
    private boolean isRecording = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediarecorder);

        chronometer = findViewById(R.id.chronometer);
        btnRecord = findViewById(R.id.btnRecord);
        btnStop = findViewById(R.id.btnStop);

        btnStop.setEnabled(false);

        btnRecord.setOnClickListener(v -> {
            if(checkPermissions()){
                if(!isRecording){
                    startRecorder();
                }else{
                    stopRecording();
                }
            }else{
                requestPermission();
            }
        });

        btnStop.setOnClickListener(v -> stopRecording());
    }

    /**
     * 权限可用校验
     * @return
     */
    private boolean checkPermissions(){
        int recordPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO);
        return recordPermission == PackageManager.PERMISSION_GRANTED &&
                storagePermission == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求权限
     */
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_MEDIA_AUDIO
        }, REQUEST_AUDIO_PERMISSION_CODE);
    }

    /**
     * 开始录音
     */
    private void startRecorder(){
        if(mediaRecorder == null){
            setupMediaRecorder();
        }
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();

            isRecording = true;
            btnRecord.setEnabled(false);
            btnStop.setEnabled(true);
        }catch (IOException ioe){
            Toast.makeText(this, "录音准备失败: " + ioe.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 停止录音
     */
    private void stopRecording(){
        if(mediaRecorder != null){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());

            isRecording = false;
            btnRecord.setText("开始录音");
            btnRecord.setEnabled(true);
            btnStop.setEnabled(false);
            Toast.makeText(this, "录音已保存: " + outputFile, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 设置资源存储目录
     */
    private void setupMediaRecorder(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        // 创建文件输出路径
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "AUDIO_" + timeStamp + ".mp3";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        outputFile = storageDir.getAbsoluteFile() + "/" + fileName;

        mediaRecorder.setOutputFile(outputFile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_AUDIO_PERMISSION_CODE){
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

}
