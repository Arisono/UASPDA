package com.uas.uaspda.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.uas.uaspda.GloableParams;
import com.uas.uaspda.IndexActivity;
import com.uas.uaspda.R;
import com.uas.uaspda.bean.Master;
import com.uas.uaspda.customview.LoadingView;
import com.uas.uaspda.datasource.DataSourceManager;
import com.uas.uaspda.util.SharedPreUtil;
import com.uas.uaspda.util.VolleyUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by LiuJie on 2015/12/10.
 */
public class LoginFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnFocusChangeListener, View.OnClickListener {

    private TextView noticeTextView;
    EditText userEditText,pwdEditText;
    Button btnLogin;
    CheckBox rmbCheckBox;
    Spinner masterSpinner;
    LoadingView loadingView;
    List<String> masterFuncList;
    List<Master> masterList;
    static ArrayAdapter adapter;
    int pageType = -1;
    Handler loginHandler;
    Intent intent;

    String maName = null;
    String userName = null,password = null;
    String cacheInfo;
    String keyUri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        pageType = VolleyUtil.FRAGMENT_LOGIN;
        //获取组件
        masterSpinner = (Spinner) view.findViewById(R.id.sp_login);
        userEditText = (EditText) view.findViewById(R.id.et_user_login);
        pwdEditText = (EditText) view.findViewById(R.id.et_pwd_login);
        btnLogin = (Button) view.findViewById(R.id.button_login_login);
        loadingView = new LoadingView(getActivity());
        noticeTextView = (TextView) view.findViewById(R.id.textview_notice);
        rmbCheckBox = (CheckBox) view.findViewById(R.id.chbx_login);
        //获取ConnectServerFragment传递的uriKey以作为SharedPreference保存登录名的key值
        Bundle b = getArguments();
        keyUri = b.getString(ConnectServerFragment.KEY_URI);

        //获取MasterList集合
        masterList = DataSourceManager.getDataSourceManager().getMasterList();

        //绑定Master下拉框数据
        masterFuncList = DataSourceManager.getDataSourceManager().getMasterFuncList();
        adapter = new ArrayAdapter(getActivity(),R.layout.item_spinner,R.id.text_spinner,masterFuncList);
        masterSpinner.setAdapter(adapter);

//        btnLogin.setFocusable(true);
//        btnLogin.setFocusableInTouchMode(true);

        //Handler
        loginHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                loadingView.dismiss();
                String tip = (String) msg.obj;
                switch (msg.what){
                    case VolleyUtil.SUCCESS_SUCCESS:
                        String userName = userEditText.getText().toString().trim();
                        String pwd = null;
                        if(rmbCheckBox.isChecked()){
                            pwd = pwdEditText.getText().toString().trim();
                        }
                        saveCache(userName,pwd);
                        intent= new Intent(getActivity(), IndexActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        break;
                    case VolleyUtil.SUCCESS_FAILED:
                        showNotice(getResources().getString(R.string.notice_login_error)+" "+tip);
                     //   Toast.makeText(getActivity(),"登录失败:"+tip,Toast.LENGTH_SHORT).show();
                        break;
                    case VolleyUtil.FAILED_FAILED:
                        showNotice(getResources().getString(R.string.notice_login_error));
                      //  Toast.makeText(getActivity(),tip,Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        //监听事件
        masterSpinner.setOnItemSelectedListener(this);
        userEditText.setOnFocusChangeListener(this);
        pwdEditText.setOnFocusChangeListener(this);
        btnLogin.setOnClickListener(this);

        //加载缓存
        loadCache();
        return view;
    }

    /**
     * @注释:保存缓存，根据服务器uri来获取缓存*/
    private void saveCache(String pUserName, String pPwd){
        cacheInfo = DataSourceManager.getDataSourceManager().addInfoItem(pUserName,pPwd);
        SharedPreUtil.saveString(getActivity(),keyUri,cacheInfo);
    }

    /**
     * @注释:加载缓存*/
    private void loadCache(){
        //通过ipport获得登录名缓存
        cacheInfo = SharedPreUtil.getString(getActivity(),keyUri);
        if(cacheInfo != null){
            DataSourceManager.getDataSourceManager().setInfoList(cacheInfo);
            List<Map<String,String>> tmpList = DataSourceManager.getDataSourceManager().getInfoList();
            String tmpUserName = tmpList.get(0).get(DataSourceManager.KEY_USERNAME).toString();
            String tmpPwd = tmpList.get(0).get(DataSourceManager.KEY_PASSWORD).toString();
            userEditText.setText(tmpUserName);
            //当密码不等于“/t”，说明用户选择了保存密码
            if(!tmpPwd.equals(DataSourceManager.NO_PWD)){
                pwdEditText.setText(tmpPwd);
                rmbCheckBox.setChecked(true);
            }
        }
    }



    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText et = (EditText) v;
        if(hasFocus){

        }
        //失去焦点
        else{
            String text = et.getText().toString().trim();
            switch (v.getId()){
                case R.id.et_user_login:
                    if(text.equals("") || text == null){
                        userEditText.setHint(R.string.hint_fragment_login_user);
                    }
                    else{
                        userName = text;
                    }
                    break;
                case R.id.et_pwd_login:
                    if(text.equals("")|| text == null){
                        userEditText.setHint(R.string.hint_fragment_login_pwd);
                    }
                    else{
                        password = text;
                    }
                    break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //获得master的name
        maName = masterList.get(position).getMaName();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        userName = userEditText.getText().toString().trim();
        password = pwdEditText.getText().toString().trim();
      //  v.requestFocus();
      //  v.requestFocusFromTouch();
        if(maName == null || maName.equals("")){
            showNotice(getResources().getString(R.string.notice_login_master));
            return;
        }
        if(userName == null || userName.equals("")){
            showNotice(getResources().getString(R.string.notice_login_user));
            return;
        }
        if(password == null || password.equals("")){
            showNotice(getResources().getString(R.string.notice_login_pwd));
            return;
        }
        //当master、username、password都不为空时
        if(maName!= null && userName !=null && password != null){
            loadingView.show();
            Log.e("JsonListener",maName+"+"+userName+"+"+password);
            connect();
        }
    }



    /**
     * @note:连接服务器请求登录*/
    private void connect(){
        //传递Handler
        VolleyUtil.setVolleyHandler(loginHandler);
        //Volley请求服务器
        VolleyUtil.requestUserLogin(getActivity(), GloableParams.ADDRESS_LOGIN_APPLY, VolleyUtil.METHOD_POST,
                pageType, maName, userName, password);
    }

    /**
     * @注释:显示错误提示
     */
    private void showNotice(String noticeMsg) {
        noticeTextView.setText(noticeMsg);
        Animation noticeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.notice_in);
        final Animation noticeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.notice_out);
        //控件保存在动画结束的状态
        noticeIn.setFillAfter(true);
        noticeOut.setFillAfter(true);
        noticeTextView.setVisibility(View.VISIBLE);
        noticeTextView.startAnimation(noticeIn);

        new AsyncTask<String, Void, Float>() {
            @Override
            protected Float doInBackground(String... params) {
                try {
                    //notice显示1.5s
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Float aFloat) {
                //notice退出
                noticeTextView.startAnimation(noticeOut);
                //  noticeTextView.setVisibility(View.GONE);
            }
        }.execute();
    }
    /*提醒数据发生变化*/
    public static void NotifySpinnerDataChanged(){
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }
}
