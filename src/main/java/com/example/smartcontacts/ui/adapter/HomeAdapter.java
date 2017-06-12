package com.example.smartcontacts.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smartcontacts.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TJTJL on 2017/6/11.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private static final String TAG = "Heda";
    private Activity activity;
    private ArrayList<String> list;

    public HomeAdapter(Activity activity, ArrayList<String> list) {
        this.activity = activity;
        this.list = list;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.item_home, parent, false);
        HomeViewHolder holder = new HomeViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final HomeViewHolder holder, final int position) {
        holder.tvText.setText(list.get(position));
        if (listener != null) {
            Log.e(TAG,"监听被触发了");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String item = list.get(position);
                    listener.onItemClick(item);
//                    int pos = holder.getLayoutPosition();
//                    listener.onItemClick(holder.itemView, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemView)
        RelativeLayout itemView;
        @BindView(R.id.tv_text)
        TextView tvText;
        private int position;

        public HomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * 设置回调接口
     */
    public interface onItemClickListener {
        void onItemClick(String item);
    }

    ;

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    private onItemClickListener listener;
}
