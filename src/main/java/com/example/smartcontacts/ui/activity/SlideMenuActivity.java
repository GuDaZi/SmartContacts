package com.example.smartcontacts.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartcontacts.R;
import com.example.smartcontacts.ui.custom.SlideMenu;
import com.example.smartcontacts.util.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SlideMenuActivity extends AppCompatActivity {

    @BindView(R.id.menu_listview)
    ListView menuListview;
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.main_listview)
    ListView mainListview;
    @BindView(R.id.slideMenu)
    SlideMenu slideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        ButterKnife.bind(this);

        //填充数据
        mainListview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1
                , Constant.NAMES));
        menuListview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1
                , Constant.sCheeseStrings) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });


        //给SlideMneu设置滑动监听器
        slideMenu.setOnSlideListener(new SlideMenu.OnSlideListener() {
            @Override
            public void onDragging(float fraction) {
                ivHead.setRotation(720 * fraction);
            }

            @Override
            public void onOpen() {
                Toast.makeText(SlideMenuActivity.this, "芝麻开门", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClose() {
                Toast.makeText(SlideMenuActivity.this, "芝麻关门!!!!", Toast.LENGTH_SHORT).show();
                ViewCompat.animate(ivHead).translationX(40)
                        .setInterpolator(new CycleInterpolator(4))
//                        .setInterpolator(new OvershootInterpolator(4))
//                        .setInterpolator(new BounceInterpolator())
                        .setDuration(800).start();
            }
        });
    }

    @OnClick(R.id.iv_head)
    public void onViewClicked() {
        slideMenu.toggle();
    }
}
