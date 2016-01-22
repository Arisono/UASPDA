package com.uas.uaspda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uas.uaspda.datasource.DataSourceManager;
import com.uas.uaspda.fragment.ICInMakeMaterialFragment;
import com.uas.uaspda.fragment.IndexInOutContentsFragment;
import com.uas.uaspda.fragment.IndexSettingFragment;
import com.uas.uaspda.fragment.IndexShopContentsFragment;
import com.uas.uaspda.fragment.IndexStorageManagerFragment;
import com.uas.uaspda.fragment.SCMakePrepareListFragment;

public class FunctionActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnBack;
    static TextView actionBarTextView;

    public static final String TAG_INMAKE = "inmakeFragment";
    public static final String TAG_SCMAKE = "scmakeFragment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);

        //获取组件
        btnBack = (Button) findViewById(R.id.btn_actionbar_withback);
        actionBarTextView = (TextView) findViewById(R.id.tv_actionbar_withback);
        //添加监听事件
        btnBack.setOnClickListener(this);

        //获取用户选择的功能名称
        Intent intent = getIntent();
        String funName = intent.getStringExtra(DataSourceManager.KEY_GRID_ITEMNAME);
        Fragment fragment = null;
        //加载对应的Fragment
        switch (funName){
            //出入库
            case GloableParams.GRIDNAME_INOUT_STORAGE:
                fragment = new IndexInOutContentsFragment();
                break;
            //车间管理
            case GloableParams.GRIDNAME_SHOPCONTENT:
                fragment = new IndexShopContentsFragment();
                break;
            //仓库管理
            case GloableParams.GRIDNAME_STORAGE_MANAGER:
                fragment = new IndexStorageManagerFragment();
                break;
            //设置
            case GloableParams.GRIDNAME_SETTING:
                fragment = new IndexSettingFragment();
                break;
        }
        //添加Fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_function_fragment,fragment).commitAllowingStateLoss();

    }

    //设置Actionbar标题
    public static void setTitle( String  title){
        actionBarTextView.setText(title);
    }

    @Override
    public void onClick(View v) {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            finish();
        }
        else{
            getSupportFragmentManager().popBackStack();
        }
    }
    //键盘后退
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("Functivity","1onKeyDown");
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            //入库单页面：重新采集：确认对话框
            if(ICInMakeMaterialFragment.CONFIRM_DIALOG_ISSHOWING){
                ICInMakeMaterialFragment.CONFIRM_DIALOG_ISSHOWING = false;
                ICInMakeMaterialFragment fragment = (ICInMakeMaterialFragment) getSupportFragmentManager().findFragmentByTag(TAG_INMAKE);
                fragment.closeConfirmDialog();
                return true;
            }
            //入库单页面的搜索PopWin
            if(ICInMakeMaterialFragment.SEARCH_WIN_ISSHOWING){
                ICInMakeMaterialFragment.SEARCH_WIN_ISSHOWING = false;
                ICInMakeMaterialFragment fragment = (ICInMakeMaterialFragment) getSupportFragmentManager().findFragmentByTag(TAG_INMAKE);
                fragment.closeSearchWin();
                return true;
            }
            //备料单页面的搜索PopWin
            if(SCMakePrepareListFragment.SEARCH_WIN_ISSHOWING){
                SCMakePrepareListFragment.SEARCH_WIN_ISSHOWING = false;
                SCMakePrepareListFragment fragment = (SCMakePrepareListFragment) getSupportFragmentManager().findFragmentByTag(TAG_SCMAKE);
                fragment.closeSearchWin();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
