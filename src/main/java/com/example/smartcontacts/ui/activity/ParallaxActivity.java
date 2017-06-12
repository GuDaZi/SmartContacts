package com.example.smartcontacts.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.smartcontacts.R;
import com.example.smartcontacts.ui.custom.ParallaxListView;
import com.example.smartcontacts.util.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ParallaxActivity extends AppCompatActivity {

    @BindView(R.id.parallaxLV)
    ParallaxListView parallaxLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parallax);
        ButterKnife.bind(this);

        //去掉listview边缘的蓝色阴影
        parallaxLV.setOverScrollMode(View.OVER_SCROLL_NEVER);

        //添加header
        View headerView = View.inflate(this, R.layout.parallax_header, null);
        ImageView parallaxImage = (ImageView) headerView.findViewById(R.id.parallaxImage);
        parallaxLV.addHeaderView(headerView);
        parallaxLV.setParallaxImage(parallaxImage);
        //填充数据
        parallaxLV.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.NAMES));
        
    }
}
