package com.uas.uaspda.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uas.uaspda.GloableParams;
import com.uas.uaspda.R;
import com.uas.uaspda.adapter.AdapterFactory;
import com.uas.uaspda.adapter.ScMakeUncollectListAdapter;
import com.uas.uaspda.bean.Mpcode;
import com.uas.uaspda.customview.ClearableEditText;
import com.uas.uaspda.customview.LoadingView;
import com.uas.uaspda.datasource.DataSourceManager;
import com.uas.uaspda.util.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @note:备料料卷采集页面
 */
public class SCMakePrepareGetFragment extends Fragment implements
        DataSourceManager.NotifyData, View.OnClickListener {

    //待采集的备料单对象
    Mpcode.Message selectMessage;
    //页面
    LoadingView loadingView;
    TextView tvNotice;
    ClearableEditText etCollect;
    TextView btnCollect;
    Button btnTitleCancel;
    //innerNotice显示界面
    RelativeLayout rlInnerMsg;
    TextView tvMsgTitle,tvLocation,tvQty,tvBarcode,tvProdcode,tvDetail;
    //待采集页面
    ListView unCollectListView;
    List unCollectList;
    ScMakeUncollectListAdapter uncollectListAdapter;
    RelativeLayout nullUnCollectView;
    LinearLayout ll;
    Context ct;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmnet_scmake_prepareget,container,false);
        ct=getActivity();
        activity=getActivity();
        //获取组件
        ll = (LinearLayout) view.findViewById(R.id.ll_todis);
        etCollect = (ClearableEditText) view.findViewById(R.id.et_collect_scmake);
        loadingView = new LoadingView(getActivity());

        tvNotice = (TextView) view.findViewById(R.id.tv_innernotice);
        View headView = view.findViewById(R.id.head_view);
        View tailView = view.findViewById(R.id.loadmore_view);
        nullUnCollectView = (RelativeLayout) view.findViewById(R.id.rl_nulldata);
        btnCollect = (TextView) view.findViewById(R.id.btn_collect_scmake);
        tvMsgTitle = (TextView) view.findViewById(R.id.tv_innernotice_msgtitle);
        tvLocation = (TextView) view.findViewById(R.id.tv_innernotice_location);
        tvQty = (TextView) view.findViewById(R.id.tv_innernotice_qty);
        tvBarcode = (TextView) view.findViewById(R.id.tv_innernotice_barcode);
        tvProdcode = (TextView) view.findViewById(R.id.tv_innernotice_prodcode);
        tvDetail = (TextView) view.findViewById(R.id.tv_innernotice_detail);
        rlInnerMsg = (RelativeLayout) view.findViewById(R.id.rl_innernotice);
        btnTitleCancel = (Button) getActivity().findViewById(R.id.btn_actionbar_right);
        parentView = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        rlInnerMsg.setVisibility(View.GONE);
        //接口回调：提醒数据源发生变化
        DataSourceManager.getDataSourceManager().setNotifyData(this);
        VolleyUtil.setVolleyHandler(handler);
        // etCollect.setEtFocus(this);
        //获取数据
        selectMessage = (Mpcode.Message) getArguments().getSerializable("message");

        loadingView.show();
        requestUncollectList();
        //List
        unCollectListView = (ListView) view.findViewById(R.id.content_view_scmake_collect);
        //Adapter
        uncollectListAdapter = AdapterFactory.getAdapterFactory().createScMakeUnCollectListAdapter(getActivity(),unCollectList);
        unCollectListView.setAdapter(uncollectListAdapter);

        //监听
        etCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("showKeyboard", "click");
               editTextGetFocus();
                showKeyboard();
            }
        });
        btnCollect.setOnClickListener(this);
        btnTitleCancel.setOnClickListener(this);
        etCollect.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //回车事件
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    disposeApplyCollectData();
                    return true;
                }
                return false;
            }
        });
        //界面配置
        //->标题
        String title = getString(R.string.title_fragment_collect_scmake) + "\n"+"("+selectMessage.getMp_code()+")";
        title = title.replace("\\n","\n");
        ((TextView)getActivity().findViewById(R.id.tv_actionbar_withback)).setText(title);
        //->右上角图标
        (getActivity().findViewById(R.id.btn_actionbar_right)).setBackgroundResource(R.drawable.cleancollect_48);

        //->待采集界面
        headView.setVisibility(View.GONE);

        showNullItemImg(unCollectList, nullUnCollectView);
       //isCollectInvalidate();

        //ll.setVisibility(View.GONE);
        //->输入框
       // editTextGetFocus();

        return view;
    }
    InputMethodManager inputManager;
    //打开键盘
    private void showKeyboard() {
        inputManager = (InputMethodManager) ct.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(etCollect, InputMethodManager.SHOW_FORCED);
//        Log.e("SHOW", "显示键盘");
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                inputManager = (InputMethodManager) ct.getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputManager.showSoftInput(etCollect, InputMethodManager.SHOW_FORCED);
//            }
//        }, 200);
    }
    //请求未采集备料List
    private void requestUncollectList(){
        unCollectList = DataSourceManager.getDataSourceManager().getUncollectList(getActivity(), selectMessage.getMp_id());
    }

    //请求服务器采集数据
    private void disposeApplyCollectData(){
        String barCode = etCollect.getText().toString().trim();
        //如果输入框为空,提示错误
        if(barCode.equals("") || barCode == null){
            etCollect.setWarnIconVisible();
        }
        //输入框不为空，请求数据
        else{
            int mpid = selectMessage.getMp_id();
            requestType = VolleyUtil.FRAGMENT_SCMAKE_COLLECT;
            VolleyUtil.getVolleyUtil().requestCollectPrepare(getActivity(), GloableParams.ADDRESS_COLLECT_APPLY, VolleyUtil.METHOD_POST,
                    requestType, barCode, mpid);
        }
    }
    @Override
    public void onDestroy() {
        //销毁配置
        clearAll();
        super.onDestroy();
    }

    /*===========================取消备料页面=========================================*/
    PopupWindow withdrawPopWin;
    View parentView;
    ClearableEditText etWithdraw;
    TextView tvMsgWithDraw;
    //显示取消备料界面
    private void showWithdrawCollectPopWin(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.popwin_scmake_withdraw,null,false);
        withdrawPopWin = new PopupWindow(view, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

        //获取组件
        Button btnOk = (Button) view.findViewById(R.id.btn_ok_withdraw_scmake);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel_withdraw_scmake);
        etWithdraw = (ClearableEditText) view.findViewById(R.id.et_withdraw_scmake);
        tvMsgWithDraw = (TextView) view.findViewById(R.id.tv_message_withdraw);
        //添加监听
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        etWithdraw.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //回车事件
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    disposeWithdrawSingle();
                    return true;
                }
                return false;
            }
        });
        //popwin捕获焦点
        withdrawPopWin.setFocusable(true);
        withdrawPopWin.setBackgroundDrawable(new BitmapDrawable());
        withdrawPopWin.showAtLocation(parentView, Gravity.CENTER, 0, 0);
    }

    //隐藏取消备料界面
    private void dismissWithdrawCollectPopwin(){
        withdrawPopWin.dismiss();
    }

    //回退单条备料
    private void disposeWithdrawSingle(){
        String barCode = etWithdraw.getText().toString().trim();
        //如果输入框没有输入
        if(barCode.equals("") || barCode == null){
            etWithdraw.setWarnIconVisible();
            return;
        }
        Log.e("SCMake","barcode:"+barCode+"mpid:"+selectMessage.getMp_id());
        requestType = VolleyUtil.FRAGMENT_SCMAKE_WITHDRAW;
        //请求回退一条
        VolleyUtil.getVolleyUtil().requestWithdrawSingle(getActivity(),GloableParams.ADDRESS_WITHDRAW_APPLY,VolleyUtil.METHOD_GET,
                requestType,barCode,selectMessage.getMp_id());
    }

    /*===========================Handler=========================================*/
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case VolleyUtil.FAILED_FAILED:
                    rlInnerMsg.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    String errorMsg = (String) msg.obj;
                    tvNotice.setText(errorMsg);
                    if(tvMsgWithDraw != null) tvMsgWithDraw.setText(errorMsg);
                    loadingView.dismiss();
                    break;
                case VolleyUtil.SUCCESS_FAILED:
                    tvNotice.setText(R.string.notice_collected_fragment_scmake);
                    break;
                case VolleyUtil.SUCCESS_SUCCESS:
                    //采集成功
                    String result = (String) msg.obj;
                    disposeShowMessage(result);
                    break;
            }
        }
    };

    //请求类型
    int requestType;
    //显示采集请求的返回信息
    private void disposeShowCollectMessage(String result){
        Log.e("SCMAKE","showmsg"+result);
        rlInnerMsg.setVisibility(View.VISIBLE);
        int md_qty = -1;
        boolean isFinish = false;
        String md_location = "无",pr_detail = "无";
        String md_prodcode = "无",pr_spec = "无",md_barcode = "无";
        try {
            JSONObject resultJson = new JSONObject(result);
            JSONObject msgObject = resultJson.getJSONObject("message");
            //获取message中的内容
            md_qty = msgObject.getInt("md_qty");
            pr_detail = msgObject.getString("pr_detail");
            md_location = msgObject.getString("md_location");
            md_prodcode = msgObject.getString("md_prodcode");
            pr_spec = msgObject.getString("pr_spec");
            md_barcode = msgObject.getString("md_barcode");
            isFinish = msgObject.getBoolean("finish");
            Log.e("SCMAKE:finish", "isFinish" + isFinish);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(tvLocation == null){
            Log.e("SCMAKE","tvLocation is null");
        }
        if(md_location == null){
            Log.e("SCMAKE","md_location is null");
        }
        if(isFinish){
            tvMsgTitle.setText("备料完成");
        }
        else{
            tvMsgTitle.setText("备料成功");
        }
        tvLocation.setText("站位："+md_location);
        tvQty.setText("数量："+md_qty);
        tvBarcode.setText("料卷号:"+md_barcode);
        tvProdcode.setText("料号:"+md_prodcode);
        String detailStr = "名称规格："+pr_detail+" || 尺寸："+pr_spec;
        tvDetail.setText(detailStr);
    }
    //显示回退请求的返回信息
    private void disposeShowWithdrawMessage(String result){
        String md_location = "无";
        Log.e("SCMAKE", "回退" + md_location);
        try {
            JSONObject resultJson = new JSONObject(result);
            JSONObject msgJson = resultJson.getJSONObject("message");
            md_location = msgJson.getString("md_location");
            Log.e("SCMAKE","回退"+md_location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvMsgWithDraw.setText("回退成功！回退站位："+md_location);
        tvNotice.setText("回退成功！回退站位："+md_location);
        rlInnerMsg.setVisibility(View.GONE);
    }
    //显示服务器传递的采集信息
    private void disposeShowMessage(String result){
        Log.e("SCMAKEShow","requestTyepe"+requestType);
        //如果是采集请求的返回数据
        if(requestType == VolleyUtil.FRAGMENT_SCMAKE_COLLECT){
            disposeShowCollectMessage(result);
        }
        //如果是回退请求的返回数据
        else{
            disposeShowWithdrawMessage(result);
        }
        //刷新待采集列表
        requestUncollectList();
    }
    /*===========================公用方法=========================================*/

    //EditText获取焦点
    // @Override
//    public void setFoucus() {
//       // editTextGetFocus();
//    }

    //“没有订单”界面
    private void showNullItemImg( List sourseList, RelativeLayout layout) {
        if (sourseList.size() == 0) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
    }
    //数据源发生变化
    @Override
    public void NotifyDataChanged(int noticeType) {
        if(uncollectListAdapter == null ){
            Log.e("SCMake:notify", "uncollectListAdapter is null");
            return;
        }
        uncollectListAdapter.notifyDataSetChanged();
        loadingView.dismiss();
        //如果服务器端没有返回数据,不可搜索
        isCollectInvalidate();
        showNullItemImg(unCollectList, nullUnCollectView);
    }

    //清空
    private void clearAll(){
        btnTitleCancel.setBackgroundResource(R.drawable.search_48);
        SCMakePrepareListFragment.isLoadingData = false;
        unCollectList.removeAll(unCollectList);

        tvNotice.setText("");
    }

    //输入框获取焦点
    private void editTextGetFocus(){
        etCollect.setFocusable(true);
        etCollect.setFocusableInTouchMode(true);
        etCollect.setEnabled(true);
        etCollect.requestFocus();
        etCollect.requestFocusFromTouch();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //是否可采集
    private void isCollectInvalidate(){
        Log.e("SCMake","num"+unCollectList.size());
        //如果服务器返回没有数据
        if(unCollectList.size() == 0){
            Log.e("SCMake", "num" + unCollectList.size());
            //搜索框和搜索按钮不可用
            btnCollect.setClickable(false);
            etCollect.setFocusable(false);
            etCollect.setFocusableInTouchMode(false);
            etCollect.clearFocus();
            etCollect.invalidate();
            tvNotice.setText(R.string.notice_collected_fragment_scmake);
        }
        //如果有数据
        else{
            btnCollect.setClickable(true);
            editTextGetFocus();
            tvNotice.setText("请采集");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //采集按钮
            case R.id.btn_collect_scmake:
                disposeApplyCollectData();
                break;
            //显示取消备料界面按钮
            case R.id.btn_actionbar_right:
                showWithdrawCollectPopWin();
                break;
            //取消备料——OK
            case R.id.btn_ok_withdraw_scmake:
                disposeWithdrawSingle();
                break;
            //取消备料——Cancel
            case R.id.btn_cancel_withdraw_scmake:
                dismissWithdrawCollectPopwin();
                break;
        }
    }

}
