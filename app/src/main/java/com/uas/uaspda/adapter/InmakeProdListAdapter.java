package com.uas.uaspda.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uas.uaspda.GloableParams;
import com.uas.uaspda.bean.ProductIn;
import com.uas.uaspda.ItemView.InMakeProdListItemView;

import java.util.List;

/**
 * Created by LiuJie on 2015/12/30.
 */
public class InmakeProdListAdapter extends BaseAdapter{
    InMakeProdListItemView inMakeView;
    List<ProductIn> items;
    Context context;

    public InmakeProdListAdapter(Context context, List<ProductIn> items) {
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
        inMakeView = new InMakeProdListItemView(context);
        TextView tvInoutno = (TextView) inMakeView.findViewWithTag(InMakeProdListItemView.TAG_TVINOUTNO);
        TextView tvClass = (TextView) inMakeView.findViewWithTag(InMakeProdListItemView.TAG_TVLEFT);
        TextView tvWhcode = (TextView) inMakeView.findViewWithTag(InMakeProdListItemView.TAG_TVRIGHT);
        TextView tvMark = (TextView) inMakeView.findViewWithTag(InMakeProdListItemView.TAG_TVMARK);

        ProductIn tmpWhcode = items.get(position);
        String strInoutno = tmpWhcode.getTarget().get(0).getPi_inoutno();
        String strClass = tmpWhcode.getTarget().get(0).getPi_class();
        String strWhcode = tmpWhcode.getTarget().get(0).getPi_whcode();
        String strMarkCode = tmpWhcode.getTarget().get(0).getEnauditstatus();

        tvInoutno.setText(strInoutno);
        tvClass.setText(strClass);
        tvWhcode.setText(strWhcode);
        tvMark.setText(getStatus(strMarkCode));
        return inMakeView;
    }

    private String getStatus(String code){
        switch (code){
            //已采集
            case GloableParams.ENAUDITSTATUS_COLLECTED:
                return "已采集";
            //采集中
            case GloableParams.ENAUDITSTATUS_COLLECTING:
                return "采集中";
            //未采集
            case GloableParams.ENAUDITSTATUS_UNCOLLECT:
                return "未采集";
        }
        return "无状态";
    }
}
