package com.uas.uaspda.customview;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.uas.uaspda.R;

/**
 * Created by LiuJie on 2016/1/8.
 */
public class ConfirmDialog implements View.OnClickListener{
    LayoutInflater inflater;
    Context context;
    View layoutView;
    TextView textView;
    Button btnOk,btnCancel;
    LinearLayout llLayout;
    //弹出框
    PopupWindow popupWindow;
    //渐进/出动画
    private AlphaAnimation alphaAnimationIn,alphaAnimationOut;

    private final boolean TAG_OK = true;
    private final boolean TAG_CANCEL = false;

    public void show(String noticeStr){
        initAnimation();
        //获取组件
        layoutView = inflater.inflate(R.layout.popwin_dialog_confirm,null);
        textView = (TextView) layoutView.findViewById(R.id.tv_dialog_confirm_notice);
        btnOk = (Button) layoutView.findViewById(R.id.btn_dialog_confirm_ok);
        btnCancel = (Button) layoutView.findViewById(R.id.btn_dialog_confirm_cancel);
        llLayout = (LinearLayout) layoutView.findViewById(R.id.ll_dialog_confirm);
        //配置细节
        textView.setText(noticeStr);
        btnOk.setTag(TAG_OK);
        btnCancel.setTag(TAG_CANCEL);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        alphaAnimationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llLayout.clearAnimation();
                popupWindow.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //popwin
        popupWindow = new PopupWindow(layoutView, ActionBar.LayoutParams.FILL_PARENT,ActionBar.LayoutParams.FILL_PARENT);
        View parentView = ((Activity)context).getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        popupWindow.showAtLocation(parentView, Gravity.CENTER,0,0);
        llLayout.startAnimation(alphaAnimationIn);
    }


    public void dismiss(){
        if(popupWindow!= null && popupWindow.isShowing()){
//            llLayout.clearAnimation();
//            popupWindow.dismiss();
            llLayout.startAnimation(alphaAnimationOut);
        }
    }

    Handler handler;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private void initAnimation(){
        //渐进动画
        alphaAnimationIn = new AlphaAnimation(0f,1f);
        alphaAnimationIn.setFillAfter(true);
        alphaAnimationIn.setDuration(200);
        alphaAnimationIn.setInterpolator(new LinearInterpolator());

        //渐出动画
        alphaAnimationOut = new AlphaAnimation(1f,0f);
        alphaAnimationOut.setFillAfter(true);
        alphaAnimationOut.setDuration(200);
        alphaAnimationOut.setInterpolator(new LinearInterpolator());

        //动画结束时，隐藏
        alphaAnimationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public ConfirmDialog(Context pContext) {
        context = pContext;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public void onClick(View v) {
        boolean select = false;
        //如果选择了OK
        if((boolean)v.getTag()){
            select = TAG_OK;
        }
        //如果选择了Cancel
        else {
            select = TAG_CANCEL;
        }

        Message msg = Message.obtain();
        msg.obj = select;
        handler.sendMessage(msg);
    }
}
