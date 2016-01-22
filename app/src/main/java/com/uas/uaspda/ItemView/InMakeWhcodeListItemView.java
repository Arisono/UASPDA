package com.uas.uaspda.ItemView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by LiuJie on 2015/12/25.
 */
public class InMakeWhcodeListItemView extends LinearLayout {

    public static final String TAG_TVINOUTNO = "tvInoutno";
    public static final String TAG_TVLEFT = "tvLeft";
    public static final String TAG_TVRIGHT = "tvRight";
    private TextView tvInoutNum, tvClass, tvWhcode;


    @SuppressLint("NewApi")
    public InMakeWhcodeListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // this.tvId = tvId;
    }

    public InMakeWhcodeListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public InMakeWhcodeListItemView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);

        //新建横向布局
        LinearLayout upperLayout = new LinearLayout(context);
        LinearLayout lowerLayout = new LinearLayout(context);
        tvInoutNum = new TextView(context);
        tvClass = new TextView(context);
        tvWhcode = new TextView(context);

        //定义样式对象
        LayoutParams layoutUpperParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 32);
        LayoutParams tvUpperParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 32);
        LayoutParams layoutLowerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 32);
        LayoutParams tvLowerParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 32);
        //细节配置
        upperLayout.setOrientation(HORIZONTAL);
        layoutUpperParams.setMargins(0, 8, 0, 4);
        lowerLayout.setOrientation(HORIZONTAL);
        layoutLowerParams.setMargins(0, 0, 0, 7);
        tvLowerParams.setMargins(0, 0, 16, 0);
        TextPaint p = tvInoutNum.getPaint();
        p.setFakeBoldText(true);
        tvInoutNum.setTextSize(18);
        tvClass.setTextSize(16);
        tvWhcode.setTextSize(16);
        //设置tag
        tvInoutNum.setTag(TAG_TVINOUTNO);
        tvClass.setTag(TAG_TVLEFT);
        tvWhcode.setTag(TAG_TVRIGHT);

        //添加组件间关系
        upperLayout.addView(tvInoutNum, tvUpperParams);
        lowerLayout.addView(tvClass, tvLowerParams);
        lowerLayout.addView(tvWhcode, tvLowerParams);
        //组件文字
        tvInoutNum.setText("YS1506606");
        tvClass.setText("仓库：001");
        tvWhcode.setText("状态：未采集");

        addView(upperLayout, layoutUpperParams);
        addView(lowerLayout, layoutLowerParams);

    }

}
