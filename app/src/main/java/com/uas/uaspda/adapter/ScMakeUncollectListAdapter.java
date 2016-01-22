package com.uas.uaspda.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uas.uaspda.ItemView.ScMakeUncollectListItemView;
import com.uas.uaspda.bean.UnCollect;

import java.util.List;

/**
 * Created by LiuJie on 2016/1/18.
 */
public class ScMakeUncollectListAdapter extends BaseAdapter{
    ScMakeUncollectListItemView itemView;
    List<UnCollect> itemList;
    Context context;

    public ScMakeUncollectListAdapter(Context context, List itemList) {
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
        itemView = new ScMakeUncollectListItemView(context);
        //序号
        TextView tvMdDetno = (TextView) itemView.findViewWithTag(ScMakeUncollectListItemView.TAG_TVMDDETNO);;
        //料号
        TextView tvMdProduct = (TextView) itemView.findViewWithTag(ScMakeUncollectListItemView.TAG_TVMDPRODUCT);
        //替代料
        TextView tvMdRepCode = (TextView) itemView.findViewWithTag(ScMakeUncollectListItemView.TAG_TVMDREPCODE);;
        //储位
        TextView tvPrWiplocation = (TextView) itemView.findViewWithTag(ScMakeUncollectListItemView.TAG_TVPRWIPLOCATION);
        //数量
        TextView tvMdNeedQty = (TextView) itemView.findViewWithTag(ScMakeUncollectListItemView.TAG_TVMDNEEDQTY);

        UnCollect message = itemList.get(position);

        int no = position+1;
        tvMdDetno.setText(""+no);
        tvMdProduct.setText("料号："+message.getMd_prodcode());
        tvMdRepCode.setText("替代料："+message.getMd_repcode());
        tvPrWiplocation.setText("储位："+message.getPr_wiplocation());
        tvMdNeedQty.setText("数量："+message.getMd_needqty());
        return itemView;
    }
}
