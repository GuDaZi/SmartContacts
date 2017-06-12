package com.example.smartcontacts.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartcontacts.R;
import com.example.smartcontacts.ui.custom.SwipeLayout;
import com.example.smartcontacts.util.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SwipeMenuActivity extends AppCompatActivity {

    @BindView(R.id.listview)
    ListView listview;
    ArrayList<String> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipemenu);
        ButterKnife.bind(this);

        listview.setAdapter(new MyAdapter());

        //给listview设置滚动监听器
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //关闭已经打开的
                if(openSwipeLayout!=null){
                    openSwipeLayout.close();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        //设置条目点击
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(SwipeMenuActivity.this, ""+position, Toast.LENGTH_SHORT).show();
//            }
//        });

        for (int i = 0; i < Constant.NAMES.length; i++) {
            list.add(Constant.NAMES[i]);
        }
    }

    //用来记录当前已经打开的SwipeLayout
    SwipeLayout openSwipeLayout = null;
    class MyAdapter extends BaseAdapter implements SwipeLayout.OnSwipeListener{
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MyHolder holder  = null;
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.adapter_list, null);
                holder = new MyHolder(convertView);
                convertView.setTag(holder);
            }else {
               holder = (MyHolder) convertView.getTag();
            }

            //设置数据
            holder.tvName.setText(list.get(position));
            //给swipeLayout设置滑动监听器
            holder.swipeLayout.setOnSwipeListener(this);

            holder.swipeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SwipeMenuActivity.this, list.get(position), Toast.LENGTH_SHORT).show();
                }
            });
            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(SwipeMenuActivity.this,"点击了delete", Toast.LENGTH_SHORT).show();
                    //关闭布局
                    openSwipeLayout.close();
                    //移除数据
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }

        @Override
        public void onOpen(SwipeLayout layout) {
            Log.d("tag","open");
            if(openSwipeLayout!=null && openSwipeLayout!=layout){
                //先关闭open的，再记录
                openSwipeLayout.close();
            }
            //再记录新的
            openSwipeLayout = layout;
        }

        @Override
        public void onClose(SwipeLayout layout) {
            Log.d("tag","onClose");
            if(openSwipeLayout==layout){
                openSwipeLayout = null;
            }

        }
    }

    static class MyHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_delete)
        TextView tvDelete;
        @BindView(R.id.swipeLayout)
        SwipeLayout swipeLayout;

        MyHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
