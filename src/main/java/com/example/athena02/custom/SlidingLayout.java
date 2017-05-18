package com.example.athena02.custom;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by TJTJL on 2017/5/17.
 */

public class SlidingLayout extends FrameLayout {

    private View menu;
    private View main;
    private ViewDragHelper dragHelper;
    private int maxLeft;
    FloatEvaluator floatEval = new FloatEvaluator();
    ArgbEvaluator argbEval = new ArgbEvaluator();

    public SlidingLayout(Context context) {
        this(context, null);
    }

    public SlidingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //利用ViewDragHelper来捕捉触碰事件
        dragHelper = ViewDragHelper.create(this,callback);
    }

    /**
     * 当完成xml布局填充的时候调用,可以在此方法中找到view对象,在onMeasure方法之前执行
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menu = getChildAt(0);
        main = getChildAt(1);
    }
    /**
     *  在onMeasure方法后执行,可以在这个方法中获得view的宽高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxLeft = (int) (main.getMeasuredWidth()*0.6f);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = dragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }
    /**
     *
     */
    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        private float fraction;

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == main || child == menu;
        }

        /**
         * 是否绑定屏幕?
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
//            Log.e("heda","这个Log打印的真是傻逼");
        }
        /**
         * 是否强制横向移动,返回大于1的值则强制横向移动
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        /**
         * 操控横向移动位置
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == main) {
                if (left>maxLeft){
                    left = maxLeft;
                }else if (left<0) {
                    left = 0;
                }
            }
            return left;
        }
        /**
         * 操控竖直移动位置,不移动
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }
        /**
         * 当View位置改变的时候执行
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            if (changedView == menu) {
                menu.layout(0,0,menu.getMeasuredWidth(),menu.getMeasuredHeight());

                int newLeft = main.getLeft() + dx;

                newLeft = fixPosition(newLeft);

                main.layout(newLeft,0,newLeft+main.getMeasuredWidth(),main.getBottom());
            }

            //在执行动画前,先要获取移动的百分比percent
            fraction = main.getLeft()*1f/maxLeft;

            execAnimation(fraction);

            //回调接口的方法
            if(listener!=null){
                listener.onDragging(fraction);
                if(main.getLeft()==maxLeft){
                    //回调打开的方法
                    listener.onOpen();
                }else if(main.getLeft()==0){
                    //回调关闭的方法
                    listener.onClose();
                }
            }
        }

        /**
         * 在View松开或者释放的时候执行
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            if (main.getLeft() > (main.getLeft()/2)) {
                open();
            }else{
                close();
            }
        }
    };


    /**
     * 判断动画走没走完,不明白什么意思
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (dragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SlidingLayout.this);
        }
    }

    /**
     * 执行动画,利用setScaleX/Y进行缩放,这里要使用浮点计算器类
     */
    private void execAnimation(float fraction) {
        //main的缩放
        main.setScaleX(floatEval.evaluate(fraction,1f,0.8f));
        main.setScaleY(floatEval.evaluate(fraction,1f,0.8f));
        //menu的缩放
        menu.setScaleX(floatEval.evaluate(fraction,0.3f,1f));
        menu.setScaleY(floatEval.evaluate(fraction,0.3f,1f));
        //menu的移动
        menu.setTranslationX(floatEval.evaluate(fraction, -menu.getMeasuredWidth()/2, 0));

//        Log.e("DDD","我是要走的大哥");
        //让slideMenu的背景变暗??
        Drawable background = getBackground();
        if (background != null) {
//            Log.e("DDD","改变背景的动画走没走");
            background.setColorFilter((Integer) argbEval.evaluate
                    (fraction, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
        }
    }

    /**
     * 修正滑动menu时导致的main的位置越界
     */
    private int fixPosition(int newLeft) {
        if (newLeft > maxLeft){
            newLeft = maxLeft;
        }else if (newLeft < 0) {
            newLeft = 0;
        }
        return newLeft;
    }
    /**
     * 设置回调,将百分数fraction暴露出去
     */
    private OnSlideListener listener;

    public void setOnSlideListener(OnSlideListener listener){
        this.listener = listener;
    }


    public interface OnSlideListener{
        //拖拽中回调
        void onDragging(float fraction);
        //打开的回调
        void onOpen();
        //关闭的回调
        void onClose();
    }

    /**
     * 滑动到一定幅度后,view自动移动到指定位置
     */
    private void open() {
        dragHelper.smoothSlideViewTo(main,maxLeft,0);
        ViewCompat.postInvalidateOnAnimation(SlidingLayout.this);
    }

    private void close() {
        dragHelper.smoothSlideViewTo(main,0,0);
        ViewCompat.postInvalidateOnAnimation(SlidingLayout.this);
    }

    /**
     * 点击图片,快速切换view的两种状态
     */
    public void toggle() {
        if (main.getLeft() > (main.getLeft()/2)){
            close();
        }else{
            open();
        }
    }
}
