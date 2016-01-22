package com.uas.uaspda.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.uas.uaspda.ItemView.InMakeProdListItemView;

import java.util.List;

public class ListViewAdapter extends BaseAdapter {
    List<String> itemList;
    Context context;

    public ListViewAdapter(Context context, List<String> items) {
        this.context = context;
        this.itemList = items;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        RelativeLayout view = new RelativeLayout(context);
//        tv.setText("444444");
//        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(60,30);
//
//        view.addView(tv,p);

//        View view = LayoutInflater.from(context).inflate(
//                R.layout.list_item_layout, null);
        View view = new InMakeProdListItemView(context);
        return view;
    }

}
