//package com.example.smartcontacts.ui.adapter;
//
//import android.content.Context;
//
//import com.example.smartcontacts.model.bean.Friend;
//import com.zhy.adapter.abslistview.CommonAdapter;
//import com.zhy.adapter.abslistview.ViewHolder;
//
//import java.util.List;
//
///**
// * Created by dance on 2017/5/19.
// */
//
//public class FriendAdapter extends CommonAdapter<Friend>{
//
//    public FriendAdapter(Context context, int layoutId, List<Friend> datas) {
//        super(context, layoutId, datas);
//    }
//
//    @Override
//    protected void convert(ViewHolder viewHolder, Friend item, int position) {
//        //获取当前的首字母
//        String frLetter = item.pinyin.charAt(0)+"";
//        if(position>0){
//            //获取上一个首字母
//            String last = mDatas.get(position-1).pinyin.charAt(0)+"";
//            if(frLetter.equals(last)){
//                //说明等于了，那么要将当前的textView隐藏
//                viewHolder.setVisible(R.id.tv_letter, false);
//            }else {
//                //需要手动显示出来
//                viewHolder.setVisible(R.id.tv_letter, true);
//                //说明不等于上一个，显示当前的首字母
//                viewHolder.setText(R.id.tv_letter, frLetter);
//            }
//        }else {
//            //说明当前是第0个条目,不需要获取上一个，则直接显示当前的拼音即可
//            viewHolder.setVisible(R.id.tv_letter, true);
//            viewHolder.setText(R.id.tv_letter, frLetter);
//        }
//        viewHolder.setText(R.id.tv_name, item.name);
//    }
//
//}
