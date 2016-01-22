package com.uas.uaspda.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uas.uaspda.bean.Whcode;
import com.uas.uaspda.ItemView.InMakeWhcodeListItemView;

import java.util.List;

/**
 * Created by LiuJie on 2015/12/30.
 */
public class InmakeWhcodeListAdapter extends BaseAdapter{

    InMakeWhcodeListItemView inMakeView;
    List<Whcode> items;
    Context context;

    public InmakeWhcodeListAdapter(Context context, List<Whcode> items) {
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
        inMakeView = new InMakeWhcodeListItemView(context);
        TextView tvInoutno = (TextView) inMakeView.findViewWithTag(InMakeWhcodeListItemView.TAG_TVINOUTNO);
        TextView tvClass = (TextView) inMakeView.findViewWithTag(InMakeWhcodeListItemView.TAG_TVLEFT);
        TextView tvWhcode = (TextView) inMakeView.findViewWithTag(InMakeWhcodeListItemView.TAG_TVRIGHT);

        Whcode tmpWhcode = items.get(position);
        String strInoutno = tmpWhcode.getPiInoutno();
        String strClass = tmpWhcode.getPiClass();
        String strWhcode = tmpWhcode.getPdWhcode();

        tvInoutno.setText(strInoutno);
        tvClass.setText(strClass);
        tvWhcode.setText("仓库号："+strWhcode);

        return inMakeView;
    }
}
