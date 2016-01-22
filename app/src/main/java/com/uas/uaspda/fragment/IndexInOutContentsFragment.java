package com.uas.uaspda.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.uas.uaspda.FunctionActivity;
import com.uas.uaspda.GloableParams;
import com.uas.uaspda.R;
import com.uas.uaspda.datasource.DataSourceManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @note:出入库管理
 */
public class IndexInOutContentsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ArrayList<HashMap<String, Object>> gridItemList;
    Fragment fragment;
    GridView gridView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inoutcontents,container,false);
        FunctionActivity.setTitle(getResources().getString(R.string.title_fragment_inout));
        //获取组件
        gridView = (GridView) view.findViewById(R.id.grid_menu_inoutcontents);
        //配置gridView数据源
        gridItemList = DataSourceManager.getDataSourceManager().getInoutGridItemList();
        //配置Adapter
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),gridItemList,R.layout.item_grid_first,
                new String[]{DataSourceManager.KEY_GRID_ITEMIMG,DataSourceManager.KEY_GRID_ITEMNAME},
                new int[]{R.id.first_griditem_img,R.id.first_griditem_name});
        gridView.setAdapter(adapter);
        //配置监听事件
        gridView.setOnItemClickListener(this);

        return view;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String itemName = (String) gridItemList.get(position).get(DataSourceManager.KEY_GRID_ITEMNAME);
        Fragment fragment = null;
        switch (itemName){
            //条码采集
            case GloableParams.GRIDNAME_CODEBAR_COLLECT:
                fragment = new ICInMakeMaterialFragment();
                getFragmentManager().beginTransaction().addToBackStack(null)
                        .replace(R.id.container_function_fragment, fragment,FunctionActivity.TAG_INMAKE).commit();
                break;
            //条码校验
            case GloableParams.GRIDNAME_CODEBAR_VERIFY:
                break;
        }
    }
}
