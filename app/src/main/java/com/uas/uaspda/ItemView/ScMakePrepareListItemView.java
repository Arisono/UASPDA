package com.uas.uaspda.ItemView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 备料单Item布局
 */
public class ScMakePrepareListItemView extends LinearLayout {

    public static final String TAG_TVTITLE = "tvtitle";
    public static final String TAG_TVMAKECODE = "tvmakecode";
    public static final String TAG_TVLINECODE = "tvlinecode";
    private TextView tvTitle,tvMakecode,tvLinecode;

    public ScMakePrepareListItemView(Context context) {
        super(context);
        initView(context);
    }

    public ScMakePrepareListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    @SuppressLint("NewApi")
    public ScMakePrepareListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        setOrientation(VERTICAL);
        //新建layout对象和组件
        LinearLayout upperLayout = new LinearLayout(context);
        LinearLayout lowerLayout = new LinearLayout(context);
        tvTitle = new TextView(context);
        tvMakecode = new TextView(context);
        tvLinecode = new TextView(context);

        //新建样式对象
        LayoutParams layoutUpperParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,32);
        LayoutParams layoutLowerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams tvUpperParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,32);
        LayoutParams tvLowerParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,32);

        //细节配置
        upperLayout.setOrientation(VERTICAL);
        layoutUpperParams.setMargins(0, 8, 0, 4);
        lowerLayout.setOrientation(VERTICAL);
        layoutLowerParams.setMargins(0, 0, 0, 7);
        tvLowerParams.setMargins(0, 0, 16, 0);
        TextPaint p = tvTitle.getPaint();
        p.setFakeBoldText(true);
        tvTitle.setTextSize(18);
        tvMakecode.setTextSize(16);
        tvLinecode.setTextSize(16);

        //设置tag
        tvTitle.setTag(TAG_TVTITLE);
        tvMakecode.setTag(TAG_TVMAKECODE);
        tvLinecode.setTag(TAG_TVLINECODE);

        tvTitle.setText("title");
        tvMakecode.setText("makecode");
        tvLinecode.setText("linecode");

        //添加组件间关系
        upperLayout.addView(tvTitle, tvUpperParams);
        lowerLayout.addView(tvMakecode, tvLowerParams);
        lowerLayout.addView(tvLinecode, tvLowerParams);

        addView(upperLayout, layoutUpperParams);
        addView(lowerLayout, layoutLowerParams);
    }
}
