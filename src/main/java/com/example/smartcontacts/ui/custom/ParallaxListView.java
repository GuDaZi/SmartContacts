package com.example.smartcontacts.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.smartcontacts.R;
import com.example.smartcontacts.presenter.HeightAnim;

/**
 * Created by dance on 2017/5/18.
 */

public class ParallaxListView extends ListView {
    public ParallaxListView(Context context) {
        this(context, null);
    }

    public ParallaxListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    ImageView parallaxImage;
    int maxHeight;//设定IMageView的最大高度
    int originalHeight;//最初的高度，就是那个120dp
    public void setParallaxImage(ImageView image){
        parallaxImage = image;

        //最大高度为图片本身高度的2倍
        maxHeight = parallaxImage.getDrawable().getIntrinsicHeight() * 2;
        originalHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_UP){
            //抬起手指，那么则需要让ImageView从当前的高度恢复到最初的那个120dp
//            ValueAnimator animator = ValueAnimator.ofInt( parallaxImage.getHeight(), originalHeight);
//            //监听动画值改变的进度，自己去实现动画逻辑
//            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    int value = (int) animation.getAnimatedValue();
//                    //我们应该讲这个值设置为ImageView的高度
//                    ViewGroup.LayoutParams params = parallaxImage.getLayoutParams();
//                    params.height = value;
//                    parallaxImage.setLayoutParams(params);
//                }
//            });
//            animator.setDuration(500).start();

            HeightAnim heightAnim = new HeightAnim(parallaxImage, parallaxImage.getHeight(), originalHeight);
            heightAnim.start(500);
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 当listview滑动到头的时候继续滑动的话就会调用
     *
     * @param deltaX
     * @param deltaY         表示手指继续滑动的距离,负值就是顶部到头，正值就是底部带头
     * @param maxOverScrollX
     * @param maxOverScrollY 表示listview滚动到头后可以继续滚动的最大距离
     * @param isTouchEvent   表示是否是手动拖动到头的，true就是，false表示靠惯性滚动到头
     * @return
     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                   int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY,
                                   boolean isTouchEvent) {

//        Log.d("tag", "deltaY: "+ deltaY + " isTouchEvent: "+isTouchEvent);
        //如果listview是手指拖动到头，并且是顶部到头的，那么就可以让ImageView继续增高
        if(isTouchEvent && deltaY<0){
            //计算新高度
            int newHeight = parallaxImage.getHeight() + Math.abs(deltaY)/3;
            //限制newHeight
            if(newHeight>maxHeight){
                newHeight = maxHeight;
            }

            //将新高度设置给ImageVIew
            ViewGroup.LayoutParams params = parallaxImage.getLayoutParams();
            params.height = newHeight;
            parallaxImage.setLayoutParams(params);
        }


        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
                maxOverScrollX, maxOverScrollY, isTouchEvent);
    }
}
