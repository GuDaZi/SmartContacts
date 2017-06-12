package com.example.smartcontacts.presenter;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

/**
 * Created by dance on 2017/5/18.
 * 改变一个View的高度的动画
 */

public class HeightAnim {

    private final ValueAnimator animator;

    public HeightAnim(final View target, int startVal, int endVal){
        animator = ValueAnimator.ofInt(startVal, endVal);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                //我们应该讲这个值设置为ImageView的高度
                ViewGroup.LayoutParams params = target.getLayoutParams();
                params.height = value;
                target.setLayoutParams(params);
            }
        });
    }

    /**
     * 开启动画
     */
    public void start(int duration){
        animator.setInterpolator(new OvershootInterpolator());
        animator.setDuration(duration).start();
    }
}
