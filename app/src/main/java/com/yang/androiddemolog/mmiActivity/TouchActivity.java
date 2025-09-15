package com.yang.androiddemolog.mmiActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

/**
 * 拖动图片Activity
 */
public class TouchActivity extends AppCompatActivity {

    /**
     * 图片资源
     */
    private ImageView draggableImage;

    /**
     * 图片的目标放置区域
     */
    private FrameLayout dropArea;

    /**
     * 根视图
     */
    private ViewGroup rootView;

    /**
     * 图片的初始位置
     */
    private float initX, initY;

    /**
     * 图片是否在目标区域内
     */
    private boolean inDropArea;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch);

        draggableImage = findViewById(R.id.draggableImage);
        dropArea = findViewById(R.id.dropArea);
        rootView = findViewById(android.R.id.content);

        // 设置触摸监听器
        draggableImage.setOnTouchListener(new View.OnTouchListener() {
            private float dX, dY;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        // 记录初始位置
                        initX = view.getX();
                        initY = view.getY();

                        // 计算触点与左上角的偏移量
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();

                        // 将图片移到根容器，确保在最上层
                        if (view.getParent() != rootView) {
                            ((ViewGroup) view.getParent()).removeView(view);
                            rootView.addView(view);
                            // 设置位置
                            view.setX(initX);
                            view.setY(initY);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 移动图片
                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        // 检查是否放置在区域内
                        checkIfInDropArea(view);
                        break;
                    case MotionEvent.ACTION_UP:
                        // 如果不在放置区域内，返回原位置
                        if (!inDropArea) {
                            view.animate()
                                    .x(initX)
                                    .y(initY)
                                    .setDuration(300)
                                    .start();
                        }else {
                            // 在放置区域内，将图片添加到放置区域
                            if (view.getParent() != dropArea) {
                                ((ViewGroup) view.getParent()).removeView(view);
                                dropArea.addView(view);
                                // 居中显示
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                        FrameLayout.LayoutParams.WRAP_CONTENT,
                                        FrameLayout.LayoutParams.WRAP_CONTENT);
                                params.gravity = Gravity.CENTER;
                                view.setLayoutParams(params);

                                // 禁用继续拖拽
                                view.setOnTouchListener(null);

                                Toast.makeText(TouchActivity.this,
                                        "图片已成功放置!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });


    }

    /**
     * 检查图片是否在放置区域内
     * @param view
     */
    private void checkIfInDropArea(View view) {
        // 获取图片的位置
        int[] imageLocation = new int[2];
        view.getLocationOnScreen(imageLocation);

        // 获取放置区域的位置
        int[] dropAreaLocation = new int[2];
        dropArea.getLocationOnScreen(dropAreaLocation);

        // 计算图片中心点
        float imageCenterX = imageLocation[0] + view.getWidth() / 2f;
        float imageCenterY = imageLocation[1] + view.getHeight() / 2f;

        // 检查中心点是否在放置区域内
        boolean isInsideX = imageCenterX >= dropAreaLocation[0] &&
                imageCenterX <= dropAreaLocation[0] + dropArea.getWidth();
        boolean isInsideY = imageCenterY >= dropAreaLocation[1] &&
                imageCenterY <= dropAreaLocation[1] + dropArea.getHeight();

        inDropArea = isInsideX && isInsideY;

        // 改变放置区域背景色以提供视觉反馈
        if (inDropArea) {
            dropArea.setBackgroundResource(R.drawable.drop_area_border_highlight);
        } else {
            dropArea.setBackgroundResource(R.drawable.drop_area_border);
        }
    }
}
