package com.uas.uaspda.ItemView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by LiuJie on 2015/12/29.
 */
public class InMakeNetListItemView extends LinearLayout {
    public TextView tv;

    public TextView getTv() {
        return tv;
    }

    @SuppressLint("NewApi")
    public InMakeNetListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public InMakeNetListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InMakeNetListItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        //新建布局和控件
        LinearLayout ll = new LinearLayout(context);
        tv = new TextView(context);
        //布局参数对象
        LayoutParams llParams = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,32);
        LayoutParams tvParams = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,32);
        //细节配置
        llParams.setMargins(0, 8,0,7);
        tv.setTextSize(18);
       // int i = 3;
        tv.setTag("TAG_TV");
        ll.addView(tv, tvParams);
        addView(ll,llParams);
    }
}
