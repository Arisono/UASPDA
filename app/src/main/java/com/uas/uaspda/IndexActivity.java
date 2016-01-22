package com.uas.uaspda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.uas.uaspda.datasource.DataSourceManager;
import com.uas.uaspda.util.VolleyUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class IndexActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ArrayList<HashMap<String, Object>> gridItemList;
    private GridView menuGridView;
    private TextView actionbarTextVeiw;
    int pageType = VolleyUtil.ACTIVITY_MENU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        //请求储位
        VolleyUtil.requestGetUseLocationOrNot(getApplicationContext(), GloableParams.ADDRESS_USELOCATION_APPLY,
                VolleyUtil.METHOD_POST,pageType);

        //获得控件
        menuGridView = (GridView) findViewById(R.id.grid_menu);
        actionbarTextVeiw = (TextView) findViewById(R.id.actionbar);
        //配置Actionbar文字
        actionbarTextVeiw.setText(getResources().getString(R.string.title_activity_menu));
        //配置Grid数据
        gridItemList = DataSourceManager.getDataSourceManager().getIndexMainGridItemList();
        //Adapter
        SimpleAdapter adapter = new SimpleAdapter(this, gridItemList, R.layout.item_grid,
                new String[]{DataSourceManager.KEY_GRID_ITEMIMG, DataSourceManager.KEY_GRID_ITEMNAME},
                new int[]{R.id.griditem_img, R.id.griditem_name});
        menuGridView.setAdapter(adapter);
        //配置监听
        menuGridView.setOnItemClickListener(this);
        menuGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //控制gridView不滚动
                if (event.getAction() == MotionEvent.ACTION_MOVE) return true;
                return false;
            }
        });


//        //添加head Fragment
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.head_container,).commit();
//        //content Fragment
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.content_container,).commit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //将选中的ItemName传递给功能Activity（FunctionActivity）
        String itemName = (String) gridItemList.get(position).get(DataSourceManager.KEY_GRID_ITEMNAME);
        Intent intent = new Intent(IndexActivity.this, FunctionActivity.class);
        intent.putExtra(DataSourceManager.KEY_GRID_ITEMNAME,itemName);
        startActivity(intent);
    }
}
