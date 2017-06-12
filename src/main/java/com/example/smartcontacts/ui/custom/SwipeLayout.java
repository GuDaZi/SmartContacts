package com.example.smartcontacts.ui.custom;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * Created by dance on 2017/5/17.
 */

public class SwipeLayout extends FrameLayout {

    private View content;
    private View delete;
    ViewDragHelper dragHelper = null;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        dragHelper = ViewDragHelper.create(this, cb);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        content = getChildAt(0);
        delete = getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
        content.layout(0, 0, content.getMeasuredWidth(), content.getMeasuredHeight());
        int delLeft = content.getMeasuredWidth();
        delete.layout(delLeft, 0, delLeft+delete.getMeasuredWidth(), delete.getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = dragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    float downX,downY;
    long downTime;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //让ViewDragHelper帮我们处理触摸事件
        dragHelper.processTouchEvent(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                float dx = moveX - downX;
                float dy = moveY - downY;
                if(Math.abs(dx) > Math.abs(dy)){
                    //说明用户滑动的方向偏向于水平，则认为用户向滑动条目，那么则请求listview不要拦截事件
                    requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                //1.计算按下抬起的时间
                long duration = System.currentTimeMillis() - downTime;
                //2.计算按下抬起的距离
                float deltaX = event.getX() - downX;
                float deltaY = event.getY() - downY;
                float distance = (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
                if(duration< ViewConfiguration.getLongPressTimeout() && distance<ViewConfiguration.getTouchSlop()){
                    //说明是达到的点击条件
                    performClick();
                }

                break;
        }

        return true;
    }

    ViewDragHelper.Callback cb = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }
        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        /**
         * 控制水平滑动的
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //限制content
            if(child==content){
                if(left>0){
                    left = 0;
                }else if(left<-delete.getMeasuredWidth()){
                    left = -delete.getMeasuredWidth();
                }
            }else if(child==delete){
                //限制delete
                if(left>content.getMeasuredWidth()){
                    left = content.getMeasuredWidth();
                }else if(left< (content.getMeasuredWidth()-delete.getMeasuredWidth())){
                    left = (content.getMeasuredWidth()-delete.getMeasuredWidth());
                }
            }
            return left;
        }

        /**
         * 当View位置改变的时候调用
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //如果当前改变的content，那么让delete进行伴随移动
            if(content==changedView){
//                delete.offsetLeftAndRight(dx);//默认的offsetLeftAndRight有bug，会少刷新一次
                ViewCompat.offsetLeftAndRight(delete, dx);
            }else if(changedView==delete){
                //让content进行伴随移动
//                content.offsetLeftAndRight(dx);
                ViewCompat.offsetLeftAndRight(content, dx);
            }

            //回调接口方法
            if(listener!=null){
                //说明是打开的
                if(content.getLeft()==-delete.getMeasuredWidth()){
                    listener.onOpen(SwipeLayout.this);
                }else if(content.getLeft()==0){
                    listener.onClose(SwipeLayout.this);
                }
            }

        }
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if(content.getLeft()> -delete.getMeasuredWidth()/2){
                //关闭
                dragHelper.smoothSlideViewTo(content, 0, 0);
                ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
            }else {
                //打开
                dragHelper.smoothSlideViewTo(content, -delete.getMeasuredWidth(), 0);
                ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
            }
        }
    };

    /**
     * 关闭的方法
     */
    public void close(){
        dragHelper.smoothSlideViewTo(content, 0, 0);
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(dragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        }
    }
    private OnSwipeListener listener;

    public void setOnSwipeListener(OnSwipeListener listener) {
        this.listener = listener;
    }

    public interface OnSwipeListener{
        void onOpen(SwipeLayout layout);
        void onClose(SwipeLayout layout);
    }
}
