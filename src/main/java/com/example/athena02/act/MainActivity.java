package com.example.athena02.act;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.athena02.R;
import com.example.athena02.custom.SlidingLayout;
import com.example.athena02.utils.Content;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.fraction;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.menu_listview)
    ListView menuListview;
    @BindView(R.id.main_listview)
    ListView mainListview;
    @BindView(R.id.slidingMenu)
    SlidingLayout slidingMenu;
    private ImageView imgHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        menuListview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Content.sCheeseStrings) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });
        mainListview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Content.NAMES));

//        imgHead = (ImageView) mainListview.findViewById(R.id.iv_head);
        imgHead = (ImageView) findViewById(R.id.iv_head);
        imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });

        //设置slidingMenu的监听
        slidingMenu.setOnSlideListener(new SlidingLayout.OnSlideListener() {
            @Override
            public void onDragging(float fraction) {

                imgHead.setRotationY(720 * fraction);
            }

            @Override
            public void onOpen() {
                Toast.makeText(MainActivity.this, "达到最大位置", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClose() {
                Toast.makeText(MainActivity.this, "回到原位置", Toast.LENGTH_SHORT).show();
                ViewCompat.animate(imgHead).translationX(40)
                        .setInterpolator(new CycleInterpolator(4))
                        .setDuration(800).start();
            }
        });
    }

}
