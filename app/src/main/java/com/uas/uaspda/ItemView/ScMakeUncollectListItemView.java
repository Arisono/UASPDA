package com.uas.uaspda.ItemView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 待采集订单Item布局
 */
public class ScMakeUncollectListItemView extends LinearLayout {

    public static final String TAG_TVMDDETNO = "tvMdDetno";
    public static final String TAG_TVMDPRODUCT = "tvMdProduct";
    public static final String TAG_TVMDREPCODE = "tvMdRepCode";
    public static final String TAG_TVPRWIPLOCATION = "tvPrWiplocation";
    public static final String TAG_TVMDNEEDQTY = "tvMdNeedQty";
    //序号
    private TextView tvMdDetno;
    //料号
    private TextView tvMdProduct;
    //替代料
    private TextView tvMdRepCode;
    //储位
    private TextView tvPrWiplocation;
    //数量
    private TextView tvMdNeedQty;

    public ScMakeUncollectListItemView(Context context) {
        super(context);
        initView(context);
    }

    public ScMakeUncollectListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    @SuppressLint("NewApi")
    public ScMakeUncollectListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        //新建layout对象和组件
        LinearLayout leftLayout = new LinearLayout(context);
        LinearLayout rightLayout = new LinearLayout(context);
        LinearLayout rUpperLayout = new LinearLayout(context);
        LinearLayout rLowerLayout = new LinearLayout(context);
        tvMdDetno = new TextView(context);
        tvMdProduct = new TextView(context);
        tvMdRepCode = new TextView(context);
        tvPrWiplocation = new TextView(context);
        tvMdNeedQty = new TextView(context);

        //新建样式对象
        //leftLayout.setBackgroundResource(R.color.red);
        LayoutParams layoutRightParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams layoutLeftParams = new LayoutParams(48, ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutParams tvLeftParams = new LayoutParams(48, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams tvUpperParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,32);
        LayoutParams tvLowerParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,32);

        setOrientation(HORIZONTAL);
        //left
        leftLayout.setOrientation(HORIZONTAL);
        leftLayout.setGravity(Gravity.CENTER);
        tvLeftParams.setMargins(8, 8, 8, 7);

        //right
        rightLayout.setOrientation(VERTICAL);
        rUpperLayout.setOrientation(VERTICAL);
        tvUpperParams.setMargins(0, 8, 8, 0);
        rLowerLayout.setOrientation(HORIZONTAL);
        tvLowerParams.setMargins(0, 8, 16, 7);

        //细节配置
        TextPaint p = tvMdDetno.getPaint();
        p.setFakeBoldText(true);
        p = tvMdProduct.getPaint();
        p.setFakeBoldText(true);
        tvMdDetno.setTextSize(18);
        tvMdProduct.setTextSize(18);
        tvMdRepCode.setTextSize(16);
        tvPrWiplocation.setTextSize(16);
        tvMdNeedQty.setTextSize(16);

        //设置tag
        tvMdDetno.setTag(TAG_TVMDDETNO);
        tvMdProduct.setTag(TAG_TVMDPRODUCT);
        tvMdRepCode.setTag(TAG_TVMDREPCODE);
        tvPrWiplocation.setTag(TAG_TVPRWIPLOCATION);
        tvMdNeedQty.setTag(TAG_TVMDNEEDQTY);

        //添加组件间关系
        leftLayout.addView(tvMdDetno,tvLeftParams);
        rightLayout.addView(rUpperLayout);
        rightLayout.addView(rLowerLayout);
        rUpperLayout.addView(tvMdProduct,tvUpperParams);
        rUpperLayout.addView(tvMdRepCode,tvUpperParams);
        rLowerLayout.addView(tvPrWiplocation,tvLowerParams);
        rLowerLayout.addView(tvMdNeedQty,tvLowerParams);

        addView(leftLayout, layoutLeftParams);
        addView(rightLayout, layoutRightParams);
    }
}
