package com.example.smartcontacts.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.smartcontacts.R;
import com.example.smartcontacts.ui.adapter.HomeAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private ArrayList<String> list = new ArrayList<>();
    private static final String TAG = "Heda";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        initData();
        initToolbar();
        initView();
    }

    private void initData() {
        list.add("SlideMenu");
        list.add("QuickIndex");
        list.add("Parallax");
        list.add("SwipeMenu");
        list.add("GooAnimation");
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        HomeAdapter adapter = new HomeAdapter(this, list);
        //重写Adapter中的回调方法
        adapter.setOnItemClickListener(new HomeAdapter.onItemClickListener() {
            @Override
            public void onItemClick(String item) {
                Log.e(TAG, "点击事件终于执行了");
                if (item.equals("SlideMenu")) {
                    Intent intent = new Intent(HomeActivity.this, SlideMenuActivity.class);
                    startActivity(intent);
                }
                if (item.equals("QuickIndex")) {
                    Intent intent = new Intent(HomeActivity.this, SlideMenuActivity.class);
                    startActivity(intent);
                }
                if (item.equals("Parallax")) {
                    Intent intent = new Intent(HomeActivity.this, ParallaxActivity.class);
                    startActivity(intent);
                }
                if (item.equals("SwipeMenu")) {
                    Intent intent = new Intent(HomeActivity.this, SwipeMenuActivity.class);
                    startActivity(intent);
                }
                if (item.equals("GooAnimation")) {
                    Intent intent = new Intent(HomeActivity.this, GooActivity.class);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
    }
}
