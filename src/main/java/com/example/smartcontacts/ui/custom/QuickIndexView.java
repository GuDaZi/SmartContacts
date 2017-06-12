package com.example.smartcontacts.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.smartcontacts.R;

/**
 * Created by dance on 2017/5/19.
 */

public class QuickIndexView extends View {
    private String[] letterArr = {"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"};

    public QuickIndexView(Context context) {
        this(context, null);
    }

    public QuickIndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    Paint paint;
    int ColorDefault = Color.WHITE;
    int ColorPressed = Color.BLACK;

    public QuickIndexView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);//设置开启抗锯齿
        paint.setColor(ColorDefault);
        int textSize = getResources().getDimensionPixelSize(R.dimen.text_size);
        paint.setTextSize(textSize);
        //由于文字绘制的起点默认是左下角，
        paint.setTextAlign(Paint.Align.CENTER);//设置起点为底边的中心，
    }

    //一定用float来保证精度
    float cellHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellHeight = getMeasuredHeight() * 1f / letterArr.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //遍历数组绘制26个字母
        for (int i = 0; i < letterArr.length; i++) {
            String text = letterArr[i];
            float x = getMeasuredWidth() / 2;//当前view宽度的一半
            //文字高度一半+格子高度一半+ i*格子高度
            float y = getTextHeight(text) / 2 + cellHeight / 2 + i * cellHeight;

//            paint.setColor(ColorPressed);
            //重绘的时候，判断当前正在绘制的字母是否是按下的那个字母，如果是则将颜色设置为黑色
            paint.setColor(i==index?ColorPressed : ColorDefault);

            canvas.drawText(text, x, y, paint);
        }

    }
    int index = -1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                //使用y坐标除以格子的高，得到的是字母的索引
                int current = (int) (event.getY() /cellHeight);
                if(current!=index){
                    index = current;

                    //对代码进行安全性的检查
                    if(index>=0 && index<letterArr.length){
                        String letter = letterArr[index];
                        if(listener!=null){
                            listener.onLetterChange(letter);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //抬起的时候需要重置
                index = -1;
                if(listener!=null){
                    listener.onRelease();
                }

                break;
        }
        //重绘，就是调用onDraw
        invalidate();

        return true;
    }

    /**
     * 获取文字的高度
     *
     * @param text
     * @return
     */
    public int getTextHeight(String text) {
        Rect bounds = new Rect();
        //该方法执行完毕，bounds
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }
    private OnLetterChangeListener listener;

    public void setOnLetterChangeListener(OnLetterChangeListener listener) {
        this.listener = listener;
    }

    public interface OnLetterChangeListener{
        void onLetterChange(String letter);
        //当抬起的时候执行
        void onRelease();
    }
}
