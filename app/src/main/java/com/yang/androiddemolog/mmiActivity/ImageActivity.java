package com.yang.androiddemolog.mmiActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class ImageActivity extends AppCompatActivity {

    private ImageView mImageView;

    // 当前屏幕显示图片的索引
    private int curImageIndex = 0;

    // 图片资源的id数组
    private final int[] mImageResourceArray = {
            R.drawable.cxk1, // 方方的图
            R.drawable.cxk2, // 长长的图
            R.drawable.cxk3, // 瘦高的图
            R.drawable.cxk4, // 头像图
            R.drawable.cxk5 // 正常图
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mImageView = findViewById(R.id.fullscreen_imageview);

        // 初始化第一张图片
        showImage(curImageIndex);
    }

    /**
     * 初始化第一张图片
     * @param curImageIndex 当前展示图片在资源数组中的索引
     */
    private void showImage(int curImageIndex) {
        int imageId = mImageResourceArray[curImageIndex];

        // 获取图片的尺寸信息
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 仅获取图片的边界信息
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), imageId, options);

        int outHeight = options.outHeight;
        int outWidth = options.outWidth;

        // 根据图片的宽高比，选择scaleType
        ImageView.ScaleType scaleType = getOptimalScaleType(outHeight, outWidth);
        // 正常的图片用FIT_CENTER就行
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mImageView.setImageResource(imageId);
    }

    /**
     * 根据图片的宽高比，为图片设置合适的scaleType
     * @param outHeight 图片的高
     * @param outWidth 图片的宽
     */
    private ImageView.ScaleType getOptimalScaleType(int outHeight, int outWidth) {
        // 计算图片的宽高比例
        float aspectRatio = (float) outWidth / outHeight;

        // 获取屏幕尺寸
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        if (aspectRatio > 1.5f) {
            // 很宽的图片（如横幅、风景图）：用centerCrop保证填满宽度，裁剪上下
            return ImageView.ScaleType.CENTER_CROP;
        } else if (aspectRatio < 0.7f) {
            // 很高的图片（如人像、海报）：用centerCrop保证填满高度，裁剪左右
            return ImageView.ScaleType.CENTER_CROP;
        } else if (Math.abs(aspectRatio - 1.0f) < 0.1f) {
            // 近似正方形图片（如头像、Logo）：用centerCrop
            return ImageView.ScaleType.CENTER_CROP;
        } else if (outWidth < screenWidth / 2 || outHeight < screenHeight / 2) {
            // 小图片：用center保持原大小，避免模糊
            return ImageView.ScaleType.CENTER;
        } else {
            // 正常比例图片：用fitCenter保持完整显示
            return ImageView.ScaleType.FIT_CENTER;
        }
    }

    /**
     * 监听音量按键事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            showPreImage();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            showNextImage();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示上一张图片
     */
    private void showNextImage() {
        curImageIndex--;
        if(curImageIndex < 0){
            curImageIndex = mImageResourceArray.length - 1;
        }
        showImage(curImageIndex);
    }

    /**
     * 显示下一张图片
     */
    private void showPreImage() {
        curImageIndex++;
        if(curImageIndex >= mImageResourceArray.length){
            curImageIndex = 0;
        }
        showImage(curImageIndex);
    }
}
