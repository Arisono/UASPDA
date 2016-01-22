package com.uas.uaspda.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

import com.uas.uaspda.R;

/**
 * Created by LiuJie on 2015/12/28.
 */
public class SearchButton extends Button {

    private int resourceId = 0;
    private Bitmap bitmap;

    public SearchButton(Context context) {
        super(context);
        init();
    }

    public SearchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        resourceId = R.drawable.search_16;
        bitmap = BitmapFactory.decodeResource(getResources(),resourceId);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        int x = this.getMeasuredWidth()/2 - bitmap.getWidth()-4;
        int y = (this.getMeasuredHeight() - bitmap.getHeight())/2;
        canvas.drawBitmap(bitmap,x,y,null);
        canvas.translate(bitmap.getWidth(),0);
        super.onDraw(canvas);
    }
}
