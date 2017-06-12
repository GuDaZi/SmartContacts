package com.example.smartcontacts.ui.custom;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.smartcontacts.R;
import com.example.smartcontacts.util.GeometryUtil;

/**
 * Created by dance on 2017/5/19.
 */

public class GooView extends View {
    public GooView(Context context) {
        this(context, null);
    }

    public GooView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    Paint paint;
    FloatEvaluator floatEval = new FloatEvaluator();
    public GooView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
    }

    float dragRadius = 20;//drag圆的半径
    float stickyRadius = 20;//sticky圆的半径
    PointF dragCenter = new PointF(400, 300); //drag圆的圆心坐标
    PointF stickyCenter = new PointF(400, 300); //stickyCenter的圆心坐标
    PointF controlPoint = new PointF(350, 300);
    PointF[] stickyPoints = {new PointF(400, 280), new PointF(400, 320)};
    PointF[] dragPoints = {new PointF(300, 280), new PointF(300, 320)};

    double lineK;//斜率,就是正切值
    @Override
    protected void onDraw(Canvas canvas) {
        //动态计算stickyRadius
        stickyRadius = cauculateRadius();

        //a.计算正切值，也就是斜率
        float dy = dragCenter.y - stickyCenter.y;
        float dx = dragCenter.x - stickyCenter.x;
        if(dx!=0){
            lineK = dy / dx;
        }
        //b.根据正切值，计算圆上面的附着点
        dragPoints = GeometryUtil.getIntersectionPoints(dragCenter, dragRadius, lineK);
        stickyPoints = GeometryUtil.getIntersectionPoints(stickyCenter, stickyRadius, lineK);
        //3.计算控制点
        controlPoint = GeometryUtil.getPointByPercent(dragCenter, stickyCenter, 0.618f);


        //1.绘制2个圆
        canvas.drawCircle(dragCenter.x, dragCenter.y, dragRadius, paint);
        if(!isOutOfRange){
            canvas.drawCircle(stickyCenter.x, stickyCenter.y, stickyRadius, paint);

            //2.使用贝塞尔曲线绘制2圆链接的部分
            Path path = new Path();
            //指定第一条贝塞尔曲线的起点
            path.moveTo(stickyPoints[0].x, stickyPoints[0].y);
            path.quadTo(controlPoint.x, controlPoint.y, dragPoints[0].x, dragPoints[0].y);
            //移到第二个曲线的起点
            path.lineTo(dragPoints[1].x, dragPoints[1].y);
            path.quadTo(controlPoint.x, controlPoint.y, stickyPoints[1].x, stickyPoints[1].y);
            path.close();//会自动闭合

            //把path绘制出啦
            canvas.drawPath(path, paint);
        }

        //绘制保护圈
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(stickyCenter.x, stickyCenter.y, maxDistance, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    float maxDistance = 150;
    /**
     * 根据2圆圆心距离去计算半径
     * @return
     */
    private float cauculateRadius() {
        //当前圆心距离
        float distance = GeometryUtil.getDistanceBetween2Points(dragCenter, stickyCenter);
        //计算当前拖拽了多少百分比
        float fraction = distance / maxDistance;
        return floatEval.evaluate(fraction,20,4);
    }

    boolean isOutOfRange = false;//是否超出范围
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                dragCenter.set(event.getX(), event.getY());

                //动态计算2圆的圆心距离
                float distance = GeometryUtil.getDistanceBetween2Points(dragCenter, stickyCenter);
                isOutOfRange = distance > maxDistance;
                break;
            case MotionEvent.ACTION_UP:
                //抬起的时候去判断是在范围内还是在范围外
                if(isOutOfRange){
                    //播放爆炸动画
                    execBoomAnim(dragCenter.x, dragCenter.y);
                    //让drag圆归位
                    dragCenter.set(stickyCenter);
                }else {
                    //在范围内抬起，应该回弹到sticky的位置
                    final PointF start = new PointF(dragCenter.x, dragCenter.y);
                    ValueAnimator animator = ValueAnimator.ofFloat(1,5);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            //获取动画执行的百分比进度
                            float fraction = animation.getAnimatedFraction();
                            //根据百分比计算对应的点
                            PointF p = GeometryUtil.getPointByPercent(start, stickyCenter, fraction);
                            //将点设置给drag圆
                            dragCenter.set(p);
                            invalidate();//重绘
                        }
                    });
                    animator.setInterpolator(new OvershootInterpolator(4));
                    animator.setDuration(400).start();
                }
                break;
        }
        //重绘
        invalidate();
        return true;
    }

    /**
     * 在指定的位置执行爆炸动画
     * @param x
     * @param y
     */
    private void execBoomAnim(float x, float y) {
        //1.在x，y的位置添加一个ImageView
        final ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new FrameLayout.LayoutParams(70,70));
        imageView.setBackgroundResource(R.drawable.anim);
        final ViewGroup parent = (ViewGroup) getParent();
        parent.addView(imageView);
        //设置imageView的位置
        imageView.setTranslationX(x-35);
        imageView.setTranslationY(y-35);

        //2.让这个View执行帧动画
        AnimationDrawable animDrawable = (AnimationDrawable) imageView.getBackground();
        animDrawable.start();

        //3.动画执行完毕，需要移除ImageView
        new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        parent.removeView(imageView);
                    }
                },601);

    }
}
