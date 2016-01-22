package com.uas.uaspda.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.uas.uaspda.FunctionActivity;
import com.uas.uaspda.GloableParams;
import com.uas.uaspda.Listener.InoutMakeListListener;
import com.uas.uaspda.R;
import com.uas.uaspda.adapter.InmakeNetListAdapter;
import com.uas.uaspda.adapter.InmakeProdListAdapter;
import com.uas.uaspda.adapter.InmakeWhcodeListAdapter;
import com.uas.uaspda.bean.ProductIn;
import com.uas.uaspda.bean.Whcode;
import com.uas.uaspda.customview.ConfirmDialog;
import com.uas.uaspda.customview.LoadingView;
import com.uas.uaspda.customview.PullToRefreshLayout;
import com.uas.uaspda.datasource.DataSourceManager;
import com.uas.uaspda.util.VolleyUtil;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @note:出入库单据Fragment
 */
public class ICInMakeMaterialFragment extends Fragment implements View.OnClickListener, TextWatcher,
        DataSourceManager.NotifyData {

    //local界面
    private static String TAG_INMAKE = "INMAKE_TEXTCHANGE";

    TextView titleTextView;
    private ListView localList;
    private PullToRefreshLayout ptrlLocal;
    RelativeLayout nullItemLocal;
    Button btnShowSearch;
    //search界面
    List<String> searchItemList;
    private InmakeNetListAdapter searchAdapter;
    private ListView searchList;
    PullToRefreshLayout ptrlSearch;
    PopupWindow popupWindow;
    View parentView;
    int requestType = VolleyUtil.FRAGMENT_INMAKE_LIST;
    View refreshView;
    RelativeLayout nullItemSearch;
    View headView, loadView;
    EditText searchEditText;
    //select界面
    private ListView selectList;
    PullToRefreshLayout ptrlSelect;

    /*==========================缓存展示页面====================================*/
    //缓存订单
    List<ProductIn> localProcList;
    InmakeProdListAdapter localAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inmake, container, false);
        FunctionActivity.setTitle(getResources().getString(R.string.title_fragment_invoice));

        //获得组件
        btnShowSearch = (Button) view.findViewById(R.id.btn_showsearch_inmake);
        parentView = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        loadingView = new LoadingView(getActivity());
        nullItemLocal = (RelativeLayout) view.findViewById(R.id.rl_nulldata);
        //List
        ptrlLocal = ((PullToRefreshLayout) view.findViewById(R.id.refresh_view));
        ptrlLocal.setOnRefreshListener(new InoutMakeListListener());
        localList = (ListView) view.findViewById(R.id.content_view);
        headView = view.findViewById(R.id.head_view);
        loadView = view.findViewById(R.id.loadmore_view);

        //数据变化回调
        DataSourceManager.getDataSourceManager().setNotifyData(this);

        //获取本地缓存List
        localProcList = DataSourceManager.getDataSourceManager().getLocalProcList(getActivity());
        initListView(localProcList);
        //添加监听
        btnShowSearch.setOnClickListener(this);
        //配置Handler
        VolleyUtil.setVolleyHandler(handler);

        showNullItemImg(localProcList,nullItemLocal);
        return view;
    }

    //ListView初始化方法
    private void initListView( List initList ) {
        localAdapter = new InmakeProdListAdapter(getActivity(), initList);
        localList.setAdapter(localAdapter);
        localList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                //显示操作Menu
                showPopupMenuLocal(view, position, id);
                return true;
            }
        });
        localList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getActivity(),
                        " Click on " + parent.getAdapter().getItemId(position),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //OnPause的时候将数据写入缓存，只能在onPause中执行，系统紧急时，onStop和onDestory不被执行
    @Override
    public void onPause() {
        super.onPause();
        //将订单信息保存到缓存
        DataSourceManager.getDataSourceManager().saveLocalProcCache(getActivity());
    }

    /*==========================LocalMenu页面====================================*/
    ListView menuLocalListView;
    ConfirmDialog confirmDialog;
    List<Map<String,Object>> localMenuList;
    PopupWindow menuPopWindow;
    boolean isDelete = true;
    int confirmSurPosition;
    //确认对话框的显示状态
    public static boolean CONFIRM_DIALOG_ISSHOWING = false;

    /**
     * @note:显示操作菜单界面
     * @param:surPosition_记录选中的localProdList位置号*/
    @SuppressLint("NewApi")
    private void showPopupMenuLocal(View surView, final int surPosition, long surId) {
        //surView.setBackgroundResource(R.color.red);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.popupwin_menu_inmake_local, null);
        //获取数据
        String mark = localProcList.get(surPosition).getTarget().get(0).getEnauditstatus();
        localMenuList = DataSourceManager.getDataSourceManager().getMenuList(getActivity(),mark);

        //获取组件
        menuLocalListView = (ListView) contentView.findViewById(R.id.lv_menu_inmake_local);
        //Adapter
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),localMenuList, R.layout.item_popmenu_inmakelocal,
                new String[]{DataSourceManager.KEY_MENU_IMG,DataSourceManager.KEY_MENU_NAME},
                new int[]{R.id.iv_item_popmenu_inmakelocal,R.id.tv_item_popmenu_inmakelocal});
           //配置操作ListView
        menuLocalListView.setAdapter(adapter);
        //Item监听
        menuLocalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                disposePopMenuItemClick(view,position,id,surPosition);
            }
        });
        //PopupWindow
        menuPopWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        menuPopWindow.setBackgroundDrawable(new BitmapDrawable());
        menuPopWindow.setFocusable(true);
        menuPopWindow.setOutsideTouchable(true);

        //当pop菜单超出屏幕时，平移位置
        //item
        float itemY = surView.getY();
        float itemHeight = surView.getHeight();
        //locallist
        float listY = localList.getY();
        float listHeight = localList.getHeight();
        //popWin的宽高
        menuPopWindow.getContentView().measure(0,0);
        float popHeight = menuPopWindow.getContentView().getMeasuredHeight();

        int scaleY = 0;
        if((itemY+itemHeight+popHeight) > listHeight){
            scaleY = (int)(itemHeight+popHeight*2.0f-15);
        }
//        Log.e("Inmake:Y","-----------------------------");
//        Log.e("Inmake:Y","iY:"+itemY);
//        Log.e("Inmake:Y","iH:"+itemHeight);
//        Log.e("Inmake:Y","lY:"+listY);
//        Log.e("Inmake:Y","lH:"+listHeight);
//        Log.e("Inmake:Y","pH:"+popHeight);
//        Log.e("Inmake:Y","-----------------------------");
        //以view的左下角为原点
        menuPopWindow.showAsDropDown(surView, 0, -scaleY);
    }
    //popMenu点击事件,surPosition为选择的订单（localProcList）位置坐标
    private void disposePopMenuItemClick(View view, int position, long id,int surPosition){
        //选中的列表项
        confirmSurPosition = surPosition;
        Log.e("Inmake:PopMenuClick", "PopMenuClick");
        String name = localMenuList.get(position).get(DataSourceManager.KEY_MENU_NAME).toString();
        Log.e("Inmake:PopMenuClick", " " + name);
        switch (name){
            //删除订单
            case GloableParams.POPMENU_NAME_DELETE:
                //删除选择的订单
                localProcList.remove(surPosition);
                menuPopWindow.dismiss();
                NotifyDataChanged(NOTICE_LOCAL_ADAPTER);
                break;
            //重新采集
            case GloableParams.POPMENU_NAME_RECOLLECT:
                //弹出确认对话框
                menuPopWindow.dismiss();
                confirmDialog = new ConfirmDialog(getActivity());
                confirmDialog.setHandler(popHandler);
                CONFIRM_DIALOG_ISSHOWING = true;
//                menuPopWindow.setFocusable(false);
//                boolean is = menuPopWindow.isFocusable();
//                Log.e("ICINMAKE",""+is);
                confirmDialog.show(getResources().getString(R.string.notice_recollect_popwin_inmake));
                break;
        }
    }
    //重新采集：对话框交互Handler，判断用户选择的是确定还是取消
    Handler popHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            isDelete = (boolean) msg.obj;
            //如果用户选择确认删除
            if(isDelete){
                //如果确认删除
                getAgain(confirmSurPosition);
            }
            closeConfirmDialog();
        }
    };
    //请求服务器清空采集的信息
    private void getAgain(int surPosition){
        ProductIn.TargetItem tmpTar = localProcList.get(surPosition).getTarget().get(0);
        int piId = tmpTar.getPi_id();
        String whcode = tmpTar.getPi_whcode();
        loadingView.show();
        VolleyUtil.getVolleyUtil().requestClearGet(getActivity(), GloableParams.ADDRESS_CLEARGET_APPLY,
                VolleyUtil.METHOD_POST, VolleyUtil.FRAGMENT_INMAKE_RECOLLECT, piId, whcode);
    }
    //关闭确认对话框
    public void closeConfirmDialog(){
        CONFIRM_DIALOG_ISSHOWING = false;
        confirmDialog.dismiss();
        //menuPopWindow.setFocusable(true);
    }
    /*==========================搜索页面====================================*/
    //选中的模糊搜索订单
    String searchSelectedItemId;
    TextView btnSearch;
    static Button btnClose;
    //搜索对话框的显示状态
    public static boolean SEARCH_WIN_ISSHOWING = false;
    //全屏搜索界面
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showPopupWindow() {
        SEARCH_WIN_ISSHOWING = true;
        View searchHeadView;

        //获取布局资源
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popupwin_inmake_search, null, false);
        popupWindow = new PopupWindow(view, ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);

        //获取组件
        btnClose = (Button) view.findViewById(R.id.btn_close_search);
        btnSearch = (TextView) view.findViewById(R.id.btn_search);
        searchEditText = (EditText) view.findViewById(R.id.et_search_inmake);
        nullItemSearch = (RelativeLayout) view.findViewById(R.id.rl_nulldata);
        refreshView = view.findViewById(R.id.refresh_view);
        searchHeadView = view.findViewById(R.id.head_view);
        //List
        ptrlSearch = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
        ptrlSearch.setOnRefreshListener(new InoutMakeListListener());
        searchList = (ListView) view.findViewById(R.id.content_view);

        //配置监听
        btnClose.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        searchEditText.addTextChangedListener(this);

        //页面配置，禁止下拉
        searchHeadView.setVisibility(View.GONE);

        //popwin捕获焦点
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        //初始化界面
        initSearchListView();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                // DataSourceManager.getDataSourceManager().saveLocalProcCache(getActivity());
                searchItemList.removeAll(searchItemList);
            }
        });
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        showKeyboard();
    }

    //SearchListView初始化方法
    private void initSearchListView() {
        searchItemList = DataSourceManager.getDataSourceManager().getNetInMakeList();
        showNullItemImg(searchItemList,nullItemSearch);
        searchAdapter = new InmakeNetListAdapter(getActivity(), searchItemList);
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
                isValidTectChanged = false;
                //按钮不可用
                btnSearch.setClickable(false);
                //隐藏键盘
                hideKeyboard();
                //处理所点击的某一个Item
                disposeSearchItemClick(view, position, id);
            }
        });
    }

    //选择模糊查询订单号列表的某一列
    private void disposeSearchItemClick(View view, int position, long id) {
        //获取点击的入库单ID
        searchSelectedItemId = searchItemList.get(position);
        searchEditText.setText(searchSelectedItemId);
        //从服务器请求订单详细信息
        applyWhcodeFromServer();
    }

    //处理从服务器获取的订单详细信息
    private void disposeWhcode() {
        //恢复搜索按钮点击
        btnSearch.setClickable(true);
        whcodeList = DataSourceManager.getDataSourceManager().getNetWhcodeList();
        for (int i = 0; i < whcodeList.size(); i++) {
            Log.e("InmakeFrag:disWhcode", whcodeList.get(i).getPiInoutno());
        }
        //如果一个单号有多个详细订单
        if (whcodeList.size() > 1) {
            //显示选择界面，获取用户选择的订单
            showSelectPopWindow();
        }
        //如果一个单号只有一个详细订单
        else {
            selectedWhcode = whcodeList.get(0);
            //添加选择的订单
            addLocalProcList();
        }
    }

    //获取入库单待采集信息
    private void getProdcode() {
        VolleyUtil.getVolleyUtil().requestGetProdInData(getActivity(),
                GloableParams.ADDRESS_PRODINDATA_APPLY, VolleyUtil.METHOD_POST,
                VolleyUtil.FRAGMENT_INMAKE_PRODINDATA, selectedWhcode);
    }

    //关闭Search界面
    public void closeSearchWin() {
        SEARCH_WIN_ISSHOWING = false;
        popupWindow.dismiss();
        //页面配置
        headView.setVisibility(View.VISIBLE);
    }

    //根据输入字母请求订单号,模糊搜索
    private void applyInoutNumFromServer(String params) {
        showInnerLoading();
        VolleyUtil.getVolleyUtil().requestInoutNumber(getActivity(), GloableParams.ADDRESS_INOUTNO_APPLY,
                VolleyUtil.METHOD_POST, requestType, params);
    }

    //搜索按钮:向服务器请求入库单详细信息
    private void applyWhcodeFromServer() {
        searchSelectedItemId = searchEditText.getText().toString().trim();
        //如果输入框为空
        if (searchSelectedItemId.equals("") || searchSelectedItemId == null) {
            return;
        }
        Log.e("searchBtn", searchSelectedItemId);
        VolleyUtil.setVolleyHandler(handler);
        VolleyUtil.getVolleyUtil().requestGetWhcode(getActivity(), GloableParams.ADDRESS_WHCODE_APPLY,
                VolleyUtil.METHOD_POST, VolleyUtil.FRAGMENT_INMAKE_WHCODE, searchSelectedItemId);
    }

    /*==========================选择同一单号多个订单页面====================================*/
    PopupWindow selectPopWin;
    //同一单号多个入库单详细信息列表
    List<Whcode> whcodeList;
    //选中的订单
    Whcode selectedWhcode = null;

    //显示搜索界面
    private void showSelectPopWindow() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.popupwin_inmake_select, null, false);
        View searchHeadView = view.findViewById(R.id.head_view);
        View searchLoadView = view.findViewById(R.id.loadmore_view);
        //显示选择界面
        selectPopWin = new PopupWindow(view, ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);

        //List
        ptrlSelect = (PullToRefreshLayout) view.findViewById(R.id.select_refresh_view);
        ptrlSelect.setOnRefreshListener(new InoutMakeListListener());
        selectList = (ListView) view.findViewById(R.id.select_content_view);
        //页面配置,禁止下拉和加载
        searchHeadView.setVisibility(View.GONE);
        searchLoadView.setVisibility(View.GONE);

        //popwin捕获焦点
        selectPopWin.setFocusable(true);
        selectPopWin.setBackgroundDrawable(new BitmapDrawable());
        initSelectListView();
        selectPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        selectPopWin.showAtLocation(parentView, Gravity.CENTER, 0, 0);
    }

    //同一单号多个订单下，最终选择的订单
    private void disposeSelectedWhcodeItem(View view, int position, long id) {
        selectedWhcode = whcodeList.get(position);
        //关闭选择弹出框
        closeSelectWin();
        //添加选择的订单
        addLocalProcList();
    }

    //添加选择的订单
    private void addLocalProcList() {
        //如果缓存中已经存在该条入库单信息
        for (int i = 0; i < localProcList.size(); i++) {
            ProductIn.TargetItem tmpPro = localProcList.get(i).getTarget().get(0);
            String whCode = tmpPro.getPi_whcode().trim();
            String piInoutNo = tmpPro.getPi_inoutno().trim();
            //如果localProcList中存在入库单号和仓库号都一样
            if (selectedWhcode.getPdWhcode().equals(whCode) && selectedWhcode.getPiInoutno().equals(piInoutNo)) {
                Log.e("Inmake:addLocal", "该单已经存在");
                Toast.makeText(getActivity(), "该单已经存在", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        loadingView.show();
        //获取订单待采集信息
        getProdcode();
    }
    //SelectListView初始化方法
    private void initSelectListView() {
        InmakeWhcodeListAdapter selectAdapter = new InmakeWhcodeListAdapter(getActivity(), whcodeList);
        selectList.setAdapter(selectAdapter);
        selectList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

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
        selectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                disposeSelectedWhcodeItem(view, position, id);
            }
        });
    }

    private void closeSelectWin() {
        selectPopWin.dismiss();
    }

    /*===========================共用方法====================================*/
    InputMethodManager inputManager;
    private static LoadingView loadingView;
    RelativeLayout rlLoading;

    public static final int NOTICE_SEARCH_ADAPTER = 1;
    public static final int NOTICE_SELECT_ADAPTER = 2;
    public static final int NOTICE_LOCAL_ADAPTER = 3;

    @Override
    public void afterTextChanged(Editable s) {
        isValidTectChanged = true;
    }

    //提醒模糊搜索数据发生变化
    @Override
    public void NotifyDataChanged(int noticeType) {
        switch (noticeType) {
            //本地缓存
            case NOTICE_LOCAL_ADAPTER:
                if (localAdapter != null) {
                    localAdapter.notifyDataSetChanged();
                    for (int i = 0; i < localProcList.size(); i++) {
                        Log.e("Inmake:NotifyItem", localProcList.get(i).getTarget().get(0).toString());
                    }
                    showNullItemImg(localProcList,nullItemLocal);
                }
                loadingView.dismiss();
                break;
            //模糊搜索PopWin
            case NOTICE_SEARCH_ADAPTER:
                if (searchAdapter != null) {
                    searchAdapter.notifyDataSetChanged();
                    Log.e("NotifyItem", searchItemList.toString());
                    showNullItemImg(searchItemList,nullItemSearch);
                }
                dismissLoading();
                break;
            //同一单号不同订单
            case NOTICE_SELECT_ADAPTER:
                break;
        }
    }
    //“没有订单”界面
    private void showNullItemImg( List sourseList, RelativeLayout layout) {
        if (sourseList.size() == 0) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
    }
    //打开键盘
    private void showKeyboard() {
        Log.e("SHOW", "显示键盘");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                inputManager = (InputMethodManager) searchEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(searchEditText, 0);
            }
        }, 200);
    }
    //隐藏键盘
    private void hideKeyboard() {
        //获取键盘管理对象
        inputManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }
    //内部Loading
    private void showInnerLoading(){
        RotateAnimation rotateAnimation;
        rotateAnimation = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(2000);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setInterpolator(new LinearInterpolator());

        btnClose.setBackgroundResource(R.drawable.close_loading);
        btnClose.startAnimation(rotateAnimation);

    }
    private static void dismissLoading(){
        btnClose.setBackgroundResource(R.drawable.close);
        btnClose.clearAnimation();
    }
    /*============================Handler====================================*/

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //如果失败
                case VolleyUtil.FAILED_FAILED:
                    String notice = (String) msg.obj;
                    Toast.makeText(getActivity(), notice, Toast.LENGTH_SHORT).show();
                    loadingView.dismiss();
                    break;
                //如果从服务器获取数据成功
                case VolleyUtil.SUCCESS_SUCCESS:
                    disposeWhcode();
                    break;
                //添加入库单成功
                case VolleyUtil.SUCCESS_INMAKE_PRODUCE:
                    Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
                    break;
                case VolleyUtil.SUCCESS_INMAKE_RECOLLECT:
                    loadingView.dismiss();
                    Toast.makeText(getActivity(), "重新采集成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /*==============================按钮点击事件=====================================*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //显示搜索界面
            case R.id.btn_showsearch_inmake:
                showPopupWindow();
                break;
            //搜索
            case R.id.btn_search:
                applyWhcodeFromServer();
                break;
            //关闭搜索界面
            case R.id.btn_close_search:
                closeSearchWin();
                break;
        }
    }

    /*==========================输入框文字变化事件====================================*/
    //区别是否是有效的文字变化事件，如果是点击List获得的文字导致的文字变化是无效的文字变化
    boolean isValidTectChanged = true;
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!isValidTectChanged){
            return;
        }
        //当字数超过3个
        if (s.length() > 2) {
          //  loadingView.show();
            String str = s.toString();
            String lowStr = str.toLowerCase();
            Log.e(TAG_INMAKE, lowStr);
            applyInoutNumFromServer(lowStr);
        }
        //当输入框没有文字
        if (s.length() == 0) {
            searchItemList.removeAll(searchItemList);
            NotifyDataChanged(ICInMakeMaterialFragment.NOTICE_SEARCH_ADAPTER);
        }
    }
}
