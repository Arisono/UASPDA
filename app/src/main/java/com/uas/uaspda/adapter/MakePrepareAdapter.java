package com.uas.uaspda.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uas.uaspda.bean.Mpcode;
import com.uas.uaspda.ItemView.ScMakePrepareListItemView;

import java.util.List;

/**
 * @note:工单备料localListAdapter
 */
public class MakePrepareAdapter extends BaseAdapter {
    ScMakePrepareListItemView makeView;
    List<Mpcode.Message> itemList;
    Context context;

    public MakePrepareAdapter(Context context, List itemList) {
        this.itemList = itemList;
        this.context = context;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tvTitle, tvMakecode, tvLinecode;
        makeView = new ScMakePrepareListItemView(context);

        tvTitle = (TextView) makeView.findViewWithTag(ScMakePrepareListItemView.TAG_TVTITLE);
        tvMakecode = (TextView) makeView.findViewWithTag(ScMakePrepareListItemView.TAG_TVMAKECODE);
        tvLinecode = (TextView) makeView.findViewWithTag(ScMakePrepareListItemView.TAG_TVLINECODE);

        Mpcode.Message message = itemList.get(position);

        tvTitle.setText("备料单："+message.getMp_code());
        tvMakecode.setText("制造单："+message.getMp_makecode());
        tvLinecode.setText("产线："+message.getMp_linecode());
        return makeView;
    }
}
