package com.uas.uaspda.customview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.uas.uaspda.R;

/**
 * Created by LiuJie on 2015/12/28.
 */
public class SearchableButton extends AppCompatButton {
    Context context;
    Drawable searchIcon;
    public SearchableButton(Context context) {
        super(context);
        init(context);
    }

    public SearchableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context pContext){
        context = pContext;

        //获取资源图片
        Drawable searchDraw = ContextCompat.getDrawable(context, R.drawable.search_24);
        Drawable wrappedDrawable = DrawableCompat.wrap(searchDraw);
        searchIcon = wrappedDrawable;
        searchIcon.setBounds(0,0,searchIcon.getIntrinsicHeight(),searchIcon.getIntrinsicWidth());

        searchIcon.setVisible(true,false);
        final Drawable[] compondDrawable = getCompoundDrawables();
        setCompoundDrawables(compondDrawable[0],
                compondDrawable[1],
                compondDrawable[2],
                searchIcon);

    }
}
