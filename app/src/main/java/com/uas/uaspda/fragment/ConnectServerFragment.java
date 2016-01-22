package com.uas.uaspda.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uas.uaspda.GloableParams;
import com.uas.uaspda.R;
import com.uas.uaspda.customview.ClearableEditText;
import com.uas.uaspda.customview.LoadingView;
import com.uas.uaspda.datasource.DataSourceManager;
import com.uas.uaspda.util.RegexUtil;
import com.uas.uaspda.util.SharedPreUtil;
import com.uas.uaspda.util.VolleyUtil;

import java.util.List;
import java.util.Map;


/**
 * @note:连接服务器Fragment
 */
public class ConnectServerFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    public static final String KEY_URI = "key_uri";

    private TextView noticeTextView;
    private Button submitButton;
    private EditText ipEditText, portEditText;
    Handler ipHandler;
    View view;
    ClearableEditText ipClearText;
    LoadingView loadingView;
    int pageType = -1;
    //格式正确
    boolean uriRight = false;
    String cacheUri = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_connectserver, container, false);
        pageType = VolleyUtil.FRAGMENT_CONNECTSERVER;
        //获取组件
        submitButton = (Button) view.findViewById(R.id.btn_submit_conncetserver_fragment);
        ipEditText = (EditText) view.findViewById(R.id.et_ip_connectserver_fragment);
        portEditText = (EditText) view.findViewById(R.id.et_port_connectserver_fragment);
        noticeTextView = (TextView) view.findViewById(R.id.textview_notice);

        //Loading页面
        loadingView = new LoadingView(getActivity());
        //添加焦点
//        submitButton.setFocusable(true);
//        submitButton.setFocusableInTouchMode(true);
        //添加监听事件
        submitButton.setOnClickListener(this);
        ipEditText.setOnFocusChangeListener(this);
        portEditText.setOnFocusChangeListener(this);


        ipHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                loadingView.dismiss();
                switch (msg.what) {
                    //连接成功
                    case VolleyUtil.SUCCESS_SUCCESS:
                        String ip = ipEditText.getText().toString().trim();
                        String port = portEditText.getText().toString().trim();
                        //添加缓存
                        saveCache(ip,port);
                        //清除缓存
                       // SharedPreUtil.removeString(getActivity(), SharedPreUtil.KEY_URI);
                        //跳转
                        gotoLoginFragment(ip, port);
                        break;
                    //连接失败
                    case VolleyUtil.FAILED_FAILED:
                        String notice = (String) msg.obj;
                        showNotice(notice);
                        Toast.makeText(getActivity(),notice,Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        };
        //加载缓存
        loadCache();
//        VolleyUtil.setVolleyHandler(new VolleyUtil.VolleyHandler() {
//            @Override
//            public void setHandler() {
//            }
//            @Override
//            public void postMessenge() {
//                handler.getMessageName();
//            }
//        });
        return view;
    }

    //添加缓存
    private void saveCache(String ip, String port) {
        cacheUri = DataSourceManager.getDataSourceManager().addUriItem(ip, port);
      //  Toast.makeText(getActivity(),cacheUri,Toast.LENGTH_SHORT).show();
        SharedPreUtil.saveString(getActivity(), SharedPreUtil.KEY_URI, cacheUri);
    }

    //加载缓存
    private void loadCache() {
        cacheUri = SharedPreUtil.getString(getActivity(), SharedPreUtil.KEY_URI);
        //Toast.makeText(getActivity(),cacheUri,Toast.LENGTH_SHORT).show();
        //当缓存不为空
        if (cacheUri != null) {
            //将缓存数据加载到数据源UriList
            DataSourceManager.getDataSourceManager().setUriList(cacheUri);
            List<Map<String, String>> list = DataSourceManager.getDataSourceManager().getUriList();
            String tmpIp = list.get(0).get(DataSourceManager.KEY_IP);
            Log.e("JsonUri",tmpIp);
            String tmpPort = list.get(0).get(DataSourceManager.KEY_PORT);

            Log.e("JsonUri",tmpPort);
            ipEditText.setText(tmpIp);
            portEditText.setText(tmpPort);
            uriRight = true;
        }
    }


    @Override
    public void onClick(View v) {
        //获得焦点，使TextView失去焦点
//        v.requestFocus();
//        v.requestFocusFromTouch();
    //    Toast.makeText(getActivity(),"1"+uriRight,Toast.LENGTH_SHORT).show();

        String ip = ipEditText.getText().toString().trim();
        String port = portEditText.getText().toString().trim();

        Log.e("ConnectIp",ip);
        Log.e("ConnectPort",port);
        //IP为空
        if (ip.equals("") || ip == null) {
            showNotice(getResources().getString(R.string.notice_serverconnect_ip_blank));
            return;
        }
        //PORT为空
        if (port.equals("") || port == null) {
            showNotice(getResources().getString(R.string.notice_serverconnect_port_blank));
            return;
        }

        //IP和Port格式验证通过
        if (uriRight) {
            loadingView.show();
            //连接服务器
            connect(ip, port);
        }

    //    Toast.makeText(getActivity(),"2"+uriRight,Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onPause() {
//        DataSourceManager.getDataSourceManager().
//    }

    private void connect(String ip, String port) {
        String url = getIPAddress(ip, port);
        //传递Handler对象给Volley
        VolleyUtil.setVolleyHandler(ipHandler);
        //连接服务器
        VolleyUtil.getVolleyUtil().requestConnectServer(getActivity(), url, VolleyUtil.METHOD_POST, pageType);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText et = (EditText) v;
        //如果获得了焦点
        if (hasFocus) {
            et.setTextColor(getResources().getColor(R.color.gray));
        }
        //失去了焦点
        else {
            String tempStr = et.getText().toString().trim();
            //当没有字的时候
            if (tempStr.equals("") || tempStr == null) {
                switch (et.getId()) {
                    case R.id.et_ip_connectserver_fragment:
                        et.setHint(getResources().getString(R.string.hint_fragment_serverconnect_ip));
                        break;
                    case R.id.et_port_connectserver_fragment:
                        et.setHint(getResources().getString(R.string.hint_fragment_serverconnect_port));
                        break;
                }
            }
            //有字的时候
            else {
                switch (et.getId()) {
                    case R.id.et_ip_connectserver_fragment:
                        isRightIp();
                        break;
                    case R.id.et_port_connectserver_fragment:
                        isRightPort();
                        break;
                }
            }
        }
    }

    //判断是否是正确URI
    private void isRightIp(){
        String ip = ipEditText.getText().toString().trim();
        //判断是否是正确的IP地址
        boolean ipRight = RegexUtil.checkString(ip, RegexUtil.IP_FORMAT);
        Log.e("ConnectFragment","ipRight"+ipRight);
        if (!ipRight) {
            //IP不正确，文字变红
            ipEditText.setTextColor(getResources().getColor(R.color.red));
            uriRight = false;
            //显示输入错误图片
            ((ClearableEditText) ipEditText).setWarnIconVisible();
            showNotice(getResources().getString(R.string.notice_serverconnect_ip_error));
        } else {
            uriRight = true;
        }
    }
    private void isRightPort(){
        String port = portEditText.getText().toString().trim();

        //判断是否是正确的port地址
        boolean portRight = RegexUtil.checkString(port, RegexUtil.PORT_FORMAT);

        if (!portRight) {
            //Prot不正确，文字变红
            portEditText.setTextColor(getResources().getColor(R.color.red));
            uriRight = false;
            ((ClearableEditText) portEditText).setWarnIconVisible();
        } else {
            uriRight = true;
        }
    }
    //拼接IP地址
    private String getIPAddress(String ip, String port) {
        //将IP和port保存
        GloableParams.setUri(ip,port);
        /*
        * http://IP+端口/ERP/请求路径
        * http://192.168.253.111:8090/ERP/oa/info/getPagingRelease.action*/
     //   String applyAddr = "http://" + GloableParams.IP + ":" + GloableParams.PORT + GloableParams.ADDRESS_CONNECT_SERVER;
        return GloableParams.ADDRESS_CONNECT_SERVER;
    }

    private void gotoLoginFragment(String ip, String port) {
        //跳转到LoginFragment
        Fragment loginFragment = new LoginFragment();
        String uri = ip+":"+port;
        Bundle b =new Bundle();
        b.putString(KEY_URI,uri);
        //将Uri地址传入LoginFragment
        loginFragment.setArguments(b);
        getFragmentManager().beginTransaction()
                .replace(R.id.container_login_fragment, loginFragment)
                .commitAllowingStateLoss();
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
}
