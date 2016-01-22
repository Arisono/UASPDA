package com.uas.uaspda.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uas.uaspda.ItemView.InMakeNetListItemView;

import java.util.List;

/**
 * Created by LiuJie on 2015/12/29.
 */
public class InmakeNetListAdapter extends BaseAdapter {
    List<String> items;
    Context context;

    public InmakeNetListAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inMakeView = new InMakeNetListItemView(context);
        TextView tv = (TextView) inMakeView.findViewWithTag("TAG_TV");
        tv.setText(items.get(position));
        return inMakeView;
    }

    InMakeNetListItemView inMakeView;
}
