package com.example.smartcontacts.ui.custom;

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
import android.widget.Scroller;

/**
 * Created by dance on 2017/5/17.
 */

public class SlideMenu extends FrameLayout {
    public SlideMenu(Context context) {
        this(context, null);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    FloatEvaluator floatEval = new FloatEvaluator();//浮点计算器
    ArgbEvaluator argbEval = new ArgbEvaluator();
    int maxLeft;//main最大left值
    View main, menu;
    ViewDragHelper dragHelper;
    public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        dragHelper = ViewDragHelper.create(this, callback);
    }

    /**
     * 当完成xml布局填充的时候调用，所以该方法执行的时候，当前View已经知道自己有几个字View了
     * 注意：该方法调用的时候，还没有进行测量和布局呢，所以不能再该方法中获取view的宽高
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menu = getChildAt(0);
        main = getChildAt(1);
    }

    /**
     * 当onMeasuere执行完毕之后就会执行这个方法，所以可以在这个方法中获取任意View的宽高了
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxLeft = (int) (getMeasuredWidth()*0.6f);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        //让ViewDragHelper帮助我们判断是否应该拦截
        boolean result = dragHelper.shouldInterceptTouchEvent(ev);

        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将event交给ViewDragHelper来解析处理，我们自己就不用解析了
        dragHelper.processTouchEvent(event);

        return true;
    }

    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 尝试去捕获监视View的触摸事件
         * @param child     当前触摸的子View
         * @param pointerId     触摸点的索引，一般用不到
         * @return   true就监视，false就会忽略
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child==main || child==menu;
        }

        /**
         * 看起来好像是获取View水平方向的拖拽范围的，实际上并木有这个用。目前这个方法主要是用来判断你是否想强制横向滑动
         * ，如果你想强制横向滑动，就返回大于0的值，否则就返回不大于0的值
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        /**
         * 控制和修正View水平方向的位置
         * @param child     当前触摸的子View
         * @param left     表示ViewDragHelper帮我们计算好的最新的left值, left=child.getLeft()+dx
         * @param dx    水平方向移动距离
         * @return  返回的值表示我们想让view的left变成的值，也就是最终返回的值才决定了child的left
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //只限制main的移动，不限制menu
            if(child==main){
                left = fixLeft(left);
            }

            return left;
        }
        /**
         * 控制和修正View垂直方向的位置
         * @param child     当前触摸的子View
         * @param top     表示ViewDragHelper帮我们计算好的最新的top值,top=child.getTop()+dy
         * @param dy    垂直方向移动距离
         * @return  返回的值表示我们想让view的top变成的值，也就是最终返回的值才决定了child的top
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }

        /**
         * 当View位置改变的时候调用
         * @param changedView   当前位置改变了的View
         * @param left  当前view最新的left
         * @param top   当前view最新的top
         * @param dx    水平移动的距离
         * @param dy    垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //如果当前移动的是menu，那么就让main进行伴随移动
            if(changedView==menu){
                //一直让menu固定在原地位置不动
                menu.layout(0,0,menu.getMeasuredWidth(),menu.getBottom());

                int newLeft = main.getLeft() + dx;
                newLeft = fixLeft(newLeft);

                main.layout(newLeft, main.getTop(), newLeft+main.getMeasuredWidth(),main.getBottom());
            }

            //由于要执行一系列动画，我们先算出手指拖动的百分比
            float fraction = main.getLeft()*1f / maxLeft;
            //根据百分比执行动画
            execAnim(fraction);
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
         * 当View松开或则释放的时候执行
         * @param releasedChild     在那个View上松开
         * @param xvel  x方向的滑动速度
         * @param yvel  y方向滑动的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if(main.getLeft()>maxLeft/2){
                //需要打开
//                scroller.startScroll();
//                ViewCompat.postInvalidateOnAnimation(SlideMenu.this);

                open();
            }else {
                //需要关闭
                close();
            }
        }
    };

    public void open(){
        dragHelper.smoothSlideViewTo(main, maxLeft, 0);
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    public void close(){
        dragHelper.smoothSlideViewTo(main, 0, 0);
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    /**
     * 切换开与关的方法
     */
    public void toggle(){
        if(main.getLeft()==0){
            //点击的时候应该打开
            open();
        }else if(main.getLeft()==maxLeft){
            close();
        }
    }

    /**
     * 根据拖动百分比执行动画
     * @param fraction
     */
    private void execAnim(float fraction) {
        //fraction: 0 -> 1
        //main缩放：1 -> 0.8
        main.setScaleX(floatEval.evaluate(fraction, 1f,0.8f));
        main.setScaleY(floatEval.evaluate(fraction, 1f,0.8f));

        //给menu进行放大效果
        menu.setScaleX(floatEval.evaluate(fraction, 0.3f, 1f));
        menu.setScaleY(floatEval.evaluate(fraction, 0.3f, 1f));
        //让menu进行移动效果
        menu.setTranslationX(floatEval.evaluate(fraction, -menu.getMeasuredWidth()/2, 0));

        //让SlideMenu的背景变暗
        Drawable background = getBackground();
        if(background!=null){
            //给background添加一个阴影遮罩效果
            background.setColorFilter((Integer) argbEval.evaluate(fraction, Color.BLACK, Color.TRANSPARENT),
                    PorterDuff.Mode.SRC_OVER);
        }

        //1.find child
        //2.animate child view

    }

    private float evaluateValue(float start, float end, float fraction){
        return start + (end-start)*fraction;
    }

    Scroller scroller = null;
    @Override
    public void computeScroll() {
        super.computeScroll();
        //判断动画有没有执行完
//        if(scroller.computeScrollOffset()){
//            //获取scroller模拟的值，自己实现滚动
//            scrollTo(scroller.getCurrX(), scroller.getCurrY());
//            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
//        }

        //ViewDragHelper的写法
        Log.e("DDD","这里走没走");
        if(dragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    /**
     * 限制left
     * @param newLeft
     * @return
     */
    private int fixLeft(int newLeft) {
        if(newLeft>maxLeft){
            newLeft = maxLeft;
        }else if(newLeft<0){
            newLeft = 0;
        }
        return newLeft;
    }

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
}
