package com.uas.uaspda.fragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uas.uaspda.FunctionActivity;
import com.uas.uaspda.GloableParams;
import com.uas.uaspda.Listener.InoutMakeListListener;
import com.uas.uaspda.R;
import com.uas.uaspda.adapter.MakePrepareAdapter;
import com.uas.uaspda.bean.Mpcode;
import com.uas.uaspda.customview.LoadingView;
import com.uas.uaspda.customview.PullToRefreshLayout;
import com.uas.uaspda.datasource.DataSourceManager;
import com.uas.uaspda.util.VolleyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
//SC:shop content

/**
 * @note:工单备料:备料单列表页面
 */
public class SCMakePrepareListFragment extends Fragment implements DataSourceManager.NotifyData, View.OnClickListener, TextWatcher {
    public static boolean isLoadingData = true;
    //prepare列表
    PullToRefreshLayout ptrl;
    ListView prepareList;
    List<Mpcode.Message> prepareDataList;
    MakePrepareAdapter prepareAdapter;
    LoadingView loadingView;
    RelativeLayout nullItemLayoutPrepare;
    Button btnShowSearch, btnBack;
    //EditText etSearch;
    View parentView;
    Context ct;
    // TextView tvTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("SCMake", "onCreate");
    }

    /*========================搜索界面：SearchPopWin====================================*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("SCMake", "onCreateView");
        FunctionActivity.setTitle(GloableParams.GRIDNAME_MATERIAL_PREPARE);
        View view = inflater.inflate(R.layout.fragment_scmakeprepare, container, false);
         ct=getActivity();
        Log.i("SCMake","activity:o"+ct);
        //接口回调
        DataSourceManager.getDataSourceManager().setNotifyData(this);
        VolleyUtil.setVolleyHandler(handler);
        //获取数据
        prepareDataList = DataSourceManager.getDataSourceManager().getPrepareList();
        //获取组件
        ptrl = (PullToRefreshLayout) view.findViewById(R.id.refresh_view_makeprepare);
        prepareList = (ListView) view.findViewById(R.id.content_list_makeprepare);
        nullItemLayoutPrepare = (RelativeLayout) view.findViewById(R.id.rl_nulldata);
        btnShowSearch = (Button) getActivity().findViewById(R.id.btn_actionbar_right);
        btnBack = (Button) getActivity().findViewById(R.id.btn_actionbar_withback);
        parentView = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);

        //设置监听
        btnShowSearch.setOnClickListener(this);
        ptrl.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                getPrepareData(true);
                // 刷新完毕,通知下拉头消失
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });
        //初始化界面、List
        initListView();
        btnShowSearch.setVisibility(View.VISIBLE);
        showNullItemImg(prepareDataList,nullItemLayoutPrepare);
        //加载数据
        loadingView = new LoadingView(getActivity());
        //获取初始数据

        getPrepareData(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("SCMake", "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("SCMake", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("SCMake", "onStop()");
    }

    @Override
    public void onDestroy() {
        Log.i("SCMake", "onDestroy");
        //右上角搜索按钮不可见
        isLoadingData = true;
        btnShowSearch.setVisibility(View.GONE);
        //将结果保存到缓存
        DataSourceManager.getDataSourceManager().saveLocalPrepareListCache(prepareDataList, getActivity());
        //tvTitle.setVisibility(View.VISIBLE);
//        etSearch.clearAnimation();
//        etSearch.setVisibility(View.GONE);
        super.onDestroy();
    }

    //向服务器请求备料工单
    private void getPrepareData( boolean isRefresh) {
        //如果是用户下拉刷新操作,请求网络数据
         if(isRefresh){
            VolleyUtil.getVolleyUtil().requestPrepareList(getActivity(), GloableParams.ADDRESS_PREPARELIST_APPLY, VolleyUtil.METHOD_POST, VolleyUtil.FRAGMENT_SHOP_PREPARELIST);
            return;
        }
        //如果是页面加载操作
        else{
            //如果需要加载数据：是从主界面跳转过来，就向服务器请求数据；如果是采集页面返回跳转到此页面，不向服务器请求数据。
            if(isLoadingData){

                //取缓存数据
                DataSourceManager.getDataSourceManager().getLocalPrepareListCache(ct);
                Log.i("SCMake", "activity:p" + getActivity());
                //如果从缓存未取到数据,就从网络加载数据
                Log.i("SCMake", "数据是否为空：" + prepareDataList.size());
                loadingView.show();
                if(prepareDataList.size() == 0){

                    Log.e("SCMAKE:GETDATA"," no prepareList cache");
                    VolleyUtil.getVolleyUtil().requestPrepareList(getActivity(), GloableParams.ADDRESS_PREPARELIST_APPLY, VolleyUtil.METHOD_POST, VolleyUtil.FRAGMENT_SHOP_PREPARELIST);
                }
                else {
                    Log.e("SCMAKE","fromCache:"+prepareDataList.toString());
                    loadingView.dismiss();
                };
            }

        }
    }

    //ListView初始化方法
    private void initListView() {
        //prepareAdapter = AdapterFactory.getAdapterFactory().createMakePrepareAdapter(getActivity(),prepareDataList);
        prepareAdapter = new MakePrepareAdapter(getActivity(), prepareDataList);
        prepareList.setAdapter(prepareAdapter);
        prepareList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                //显示操作Menu
                // showPopupMenuLocal(view, position, id);
                return true;
            }
        });
        prepareList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //跳转至采集信息页面
                gotoCollectPrepareFragment(prepareDataList.get(position));
                //hideKeyboard(searchEditText);
            }
        });
    }


    /*========================搜索界面：SearchPopWin====================================*/

    public static boolean SEARCH_WIN_ISSHOWING = false;
    PopupWindow popupWindow;
    Button btnClose, btnSpinnerDown;
    TextView btnSearch;
    EditText searchEditText;
    RelativeLayout nullItemLayoutSearch;
    View refreshView;
    PullToRefreshLayout ptrlSearch;
    ListView searchList;
    LinearLayout searchLayout, upperLayout;

    //用户选择的item
    String searchSelectedItemId;
    //显示搜索界面
    private void showMakePrepareSearchPopWin() {
        SEARCH_WIN_ISSHOWING = true;
        View searchHeadView;

        //获取布局资源
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popupwin_inmake_search, null, false);
        popupWindow = new PopupWindow(view, ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);

        //获取组件
        btnClose = (Button) view.findViewById(R.id.btn_close_search);
        btnSearch = (TextView) view.findViewById(R.id.btn_search);
        btnSpinnerDown = (Button) view.findViewById(R.id.btn_spinner_inmake);
        searchEditText = (EditText) view.findViewById(R.id.et_search_inmake);
        nullItemLayoutSearch = (RelativeLayout) view.findViewById(R.id.rl_nulldata);
        refreshView = view.findViewById(R.id.refresh_view);
        searchHeadView = view.findViewById(R.id.head_view);
        searchLayout = (LinearLayout) view.findViewById(R.id.ll_search_inmake);
        upperLayout = (LinearLayout) view.findViewById(R.id.ll_upperlayout_inmake);
        //List
        ptrlSearch = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
        ptrlSearch.setOnRefreshListener(new InoutMakeListListener());
        searchList = (ListView) view.findViewById(R.id.content_view);

        //配置监听
        btnClose.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnSpinnerDown.setOnClickListener(this);
        searchEditText.addTextChangedListener(this);

        //页面配置，禁止下拉
        searchHeadView.setVisibility(View.GONE);

        //popwin捕获焦点
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        //初始化界面
        btnSpinnerDown.setVisibility(View.VISIBLE);
        searchEditText.setHint(getString(R.string.hint_search_prepare_fragment_scmake));
        initSearchListView();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // DataSourceManager.getDataSourceManager().saveLocalProcCache(getActivity());
                //searchItemList.removeAll(searchItemList);
            }
        });
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        showKeyboard(searchEditText);
    }

    //搜索结果Adapter
    MakePrepareAdapter searchAdapter;
    //SearchListView初始化方法
    private void initSearchListView() {
        if (searchItemList == null) {
            searchItemList = new ArrayList();
        }
        showNullItemImg(searchItemList, nullItemLayoutSearch);
        searchAdapter = new MakePrepareAdapter(getActivity(), searchItemList);
        searchList.setAdapter(searchAdapter);
        searchList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Toast.makeText(
                        getActivity(),
                        "LongClick on "
                                + parent.getAdapter().getItemId(position),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //isValidTectChanged = false;
                //按钮不可用
                btnSearch.setClickable(false);
                //隐藏键盘
                hideKeyboard(searchEditText);
                //处理所点击的某一个Item
                disposeSearchListItemClick(position);
            }
        });
    }

    //处理用户点击的搜索到的列表，SearchItem
    private void disposeSearchListItemClick(int position){
        //获取点击的备料单/制造单/产线ID
        Mpcode.Message searchSelectedItem = searchItemList.get(position);
        switch (searchType){
            //备料单
            case SEARCH_TYPE_PREPARE:
                searchSelectedItemId = searchSelectedItem.getMp_code();
                break;
            //制造单
            case SEARCH_TYPE_MAKECODE:
                searchSelectedItemId = searchSelectedItem.getMp_makecode();
                break;
            //产线
            case SEARCH_TYPE_LINECODE:
                searchSelectedItemId = searchSelectedItem.getMp_linecode();
                break;
        }
        searchEditText.setText(searchSelectedItemId);
        //跳转到采集页面
        gotoCollectPrepareFragment(searchSelectedItem);
    }

    //跳转到采集页面
    private void gotoCollectPrepareFragment(Mpcode.Message message){
        Log.e("SCMake","gotoCollect");
        Fragment fragment = new SCMakePrepareGetFragment();
        Bundle bundle = new Bundle();
        //将用户选择的数据传递到待采集Fragmnet
        bundle.putSerializable("message",message);
        fragment.setArguments(bundle);
        closeSearchWin();
        getFragmentManager().beginTransaction().addToBackStack(null)
                .replace(R.id.container_function_fragment, fragment).commit();
    }
    //关闭Search界面
    public void closeSearchWin() {
        if(popupWindow == null){
            return;
        }
        SEARCH_WIN_ISSHOWING = false;
        popupWindow.dismiss();
    }

    /*========================查找方式下拉框====================================*/
    PopupWindow spinnerPopWin;
    List spinnerNameList;
    //搜索结果集（单项，备料单号、产线或者是制造单号）
    List<Mpcode.Message> searchItemList;
    //用户选择的搜索类别,默认为备料单号
    int searchType = 1;
    final int SEARCH_TYPE_PREPARE = 1;
    final int SEARCH_TYPE_MAKECODE = 2;
    final int SEARCH_TYPE_LINECODE = 3;

    //处理点击下拉事件：弹出输入框的下拉框
    @SuppressLint("NewApi")
    private void showSearchTypeSpinnerDialog(View marginLeftView, View marginTopView) {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.popwin_select_spinner_scmake, null);
        //获取组件
        ListView spinnerListView = (ListView) contentView.findViewById(R.id.lv_spinner_searchtype_scmake);
        //数据
        spinnerNameList = DataSourceManager.getDataSourceManager().getPrepareSpinnerItemList();
        //Adapter
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner_searchtype, spinnerNameList);
        spinnerListView.setAdapter(adapter);

        //配置Dialog样式
        //注意：getY方法是获取视图和父视图之间的距离，由于布局中，输入框和父布局有8个像素的margin,所以Dialog的y是marginTop
        int x = (int) marginLeftView.getX();
        LinearLayout.LayoutParams tp = (LinearLayout.LayoutParams) marginTopView.getLayoutParams();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) marginLeftView.getLayoutParams();
        int y = tp.topMargin + marginTopView.getHeight();
        int w = (int) (marginLeftView.getWidth() + getResources().getDimension(R.dimen.space_left_8));
        int h = marginTopView.getHeight();
        Log.e("SCMAKE", "w" + w);
        Log.e("SCMAKE", "h" + h);

        //配置监听
        spinnerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                disposeOnsearchTypeSpinnerItemClick(position, id);
                dismissSpinnerDialog();
            }
        });

        showSpinnerDialog(contentView, x, y, w, h);
    }

    //选择搜索类型下拉框列表点击事件
    private void disposeOnsearchTypeSpinnerItemClick(int position, long id) {
        String selectedSpinnerItem = spinnerNameList.get(position).toString().trim();
        searchEditText.setText("");
        switch (selectedSpinnerItem) {
            //搜索备料单
            case GloableParams.SPINNER_PREPARE_SEARCH:
                searchType = SEARCH_TYPE_PREPARE;
                searchEditText.setHint(getString(R.string.hint_search_prepare_fragment_scmake));
                showKeyboard(searchEditText);
                break;
            //搜索制造单
            case GloableParams.SPINNER_MAKECODE_SEARCH:
                searchType = SEARCH_TYPE_MAKECODE;
                searchEditText.setHint(getString(R.string.hint_search_makecode_fragment_scmake));
                showKeyboard(searchEditText);
                break;
            //搜索产线
            case GloableParams.SPINNER_LINECODE_SEARCH:
                searchType = SEARCH_TYPE_LINECODE;
                searchEditText.setHint(getString(R.string.hint_search_linecode_fragment_scmake));
                showKeyboard(searchEditText);
                break;
        }
    }

    //查找本地备料单
    private void findPrepareInvoice(String aimStr,int size) {
        Log.e("SCMake:find","findtype:"+searchType);
        Log.e("SCMake:find","size:"+size);
        searchItemList.removeAll(searchItemList);
        String tmpSourceMp;
        switch (searchType) {
            //根据备料单号搜索
            case SEARCH_TYPE_PREPARE:
                for (Mpcode.Message m : prepareDataList) {
                    //转换成小写比对
                    tmpSourceMp = m.getMp_code().substring(0,size).toLowerCase();
                    Log.e("SCMake:find","substring:"+tmpSourceMp);
                    if (tmpSourceMp.equals(aimStr)) {
                        Log.e("SCMake:find","find:"+aimStr);
                        searchItemList.add(m);
                    }
                }
                break;
            //根据制造单号搜索
            case SEARCH_TYPE_MAKECODE:
                for (Mpcode.Message m : prepareDataList) {
                    //转换成小写比对
                    //源数据字符串长度<查找长度？
                    String tmpMC = m.getMp_makecode();
                    if(tmpMC.length()<size){
                        Log.e("SCMake:find","源数据字符串长度<查找长度？");
                        continue;
                    }
                    tmpSourceMp = tmpMC.substring(0,size).toLowerCase();
                    Log.e("SCMake:find","substring:"+tmpSourceMp);
                    if (tmpSourceMp.equals(aimStr)) {
                        Log.e("SCMake:find","find:"+aimStr);
                        searchItemList.add(m);
                    }
                }
                break;
            //根据产线线别搜索
            case SEARCH_TYPE_LINECODE:
                for (Mpcode.Message m : prepareDataList) {
                    //转换成小写比对
                    tmpSourceMp = m.getMp_linecode().substring(0,size).toLowerCase();
                    Log.e("SCMake:find","substring:"+tmpSourceMp);
                    if (tmpSourceMp.equals(aimStr)) {
                        Log.e("SCMake:find","find:"+aimStr);
                        searchItemList.add(m);
                    }
                }
                break;
        }
        //通知数据源发生变化
        NotifyDataChanged(NOTICE_SEARCH_ADAPTER);
    }
    /*========================公用方法====================================*/


    /*------数据源发生变化------*/
    public static final int NOTICE_PREPARE_ADAPTER = 1;
    public static final int NOTICE_SEARCH_ADAPTER = 2;
    @Override
    public void NotifyDataChanged(int notifyType) {
        switch (notifyType){
            //网络加载的备料单列表发生变化
            case NOTICE_PREPARE_ADAPTER:
                if (prepareAdapter != null) {
                    prepareAdapter.notifyDataSetChanged();
                    Log.i("Inmake:NotifyItem", prepareDataList.toString());
                    showNullItemImg(prepareDataList, nullItemLayoutPrepare);
                }

                loadingView.dismiss();
                break;
            //搜索的备料单列表发生变化
            case NOTICE_SEARCH_ADAPTER:
                Log.e("Inmake:NotifyItem", "searchItemList"+searchItemList.toString());
                if (searchAdapter != null) {
                    searchAdapter.notifyDataSetChanged();
                    Log.e("Inmake:NotifyItem", searchItemList.toString());
                    showNullItemImg(searchItemList, nullItemLayoutSearch);
                }
                loadingView.dismiss();
                break;

        }
    }

    /*------弹出框------*/
    Dialog dialog;
    //弹出框界面
    public void showSpinnerDialog(View contentView, int x, int y, int w, int h) {
        //新建对话框
        dialog = new Dialog(getActivity(), R.style.SearchSpinnerDialogStyle);
        dialog.setContentView(contentView);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //设置坐标
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        params.x = x;
        params.y = y;
        //设置宽高
        params.width = w;
        // params.height = h;
        window.setAttributes(params);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Log.e("SCMAKE", "w" + dialog.getWindow().getAttributes().width);
        Log.e("SCMAKE", "h" + dialog.getWindow().getAttributes().height);
    }

    //弹出框消失
    public void dismissSpinnerDialog() {
        dialog.dismiss();
    }
    //无界面
    private static void showNullItemImg(List sourseList, RelativeLayout layout) {
        String str = "没有备料工单";
        TextView textView = (TextView) layout.findViewById(R.id.tv_nulldata);
        textView.setText(str);
        if (sourseList.size() == 0) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    /*------键盘------*/
    InputMethodManager inputManager;
    //打开键盘
    private void showKeyboard(final EditText view) {
        Log.e("SHOW", "显示键盘");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(view, 0);
            }
        }, 200);
    }

    //隐藏键盘
    private void hideKeyboard(final EditText view) {
        //获取键盘管理对象
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case VolleyUtil.FAILED_FAILED:
                    Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    loadingView.dismiss();
                    break;
            }
        }
    };
    /*========================点击事件====================================*/

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //关闭搜索界面
            case R.id.btn_close_search:
                closeSearchWin();
                break;
            //ActionBar右侧按钮
            case R.id.btn_actionbar_right:
                showMakePrepareSearchPopWin();
//                float fromX = btnSearch.getX();
//                float toX = etSearch.getX();
//                etSearch.setVisibility(View.VISIBLE);
//                TranslateAnimation animation = new TranslateAnimation(fromX, toX,0, 0);
//                animation.setDuration(500);//设置动画持续时间
//                animation.setRepeatCount(0);//设置重复次数
//                animation.setFillAfter(true);
//                etSearch.setAnimation(animation);
//                etSearch.setFocusable(true);
//                etSearch.setFocusableInTouchMode(true);
//                etSearch.requestFocus();
//                showKeyboard(etSearch);
                break;
            //搜索框下拉按钮
            case R.id.btn_spinner_inmake:
                showSearchTypeSpinnerDialog(searchLayout, upperLayout);
                break;
        }
    }

    /*========================文字变化事件====================================*/
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int strLength = s.length();
        //当字数超过3个
        if (strLength > 2) {
            //  loadingView.show();
            String str = s.toString();
            String lowStr = str.toLowerCase();
            Log.e("SCMake:textchange", lowStr);
            findPrepareInvoice(lowStr, strLength);
        }
        //当输入框没有文字
        if (strLength == 0) {
            searchItemList.removeAll(searchItemList);
            Log.e("SCMake:textchange", "searchItemList" + searchItemList.toString());
            Log.e("SCMake:textchange", "len" + strLength);
            NotifyDataChanged(SCMakePrepareListFragment.NOTICE_SEARCH_ADAPTER);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {}
}
