package com.uas.uaspda.fragment;
//其它地方车间管理备注为workhouseManager
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

import java.util.List;
import java.util.Map;

/**
 * @note:车间管理
 */
public class IndexShopContentsFragment extends Fragment implements AdapterView.OnItemClickListener {
    GridView gridView;
    List<Map<String,Object>> gridItemList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FunctionActivity.setTitle(getResources().getString(R.string.title_fragment_workhouse_index));
        View view = inflater.inflate(R.layout.fragment_index_workhouse,container,false);
        //获取组件
        gridView = (GridView) view.findViewById(R.id.grid_menu_workhouse_index);
        //配置数据
        gridItemList = DataSourceManager.getDataSourceManager().getWorkhouseMenuList();
        //配置Adapter
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),gridItemList,R.layout.item_grid_first,
                new String[]{DataSourceManager.KEY_GRID_ITEMIMG,DataSourceManager.KEY_GRID_ITEMNAME},
                new int[]{R.id.first_griditem_img,R.id.first_griditem_name});
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String itemName = (String) gridItemList.get(position).get(DataSourceManager.KEY_GRID_ITEMNAME);
        Fragment fragment = null;
        switch (itemName){
            //SMT上料
            case GloableParams.GRIDNAME_SMTMATERIAL_ADD:
                break;
            //工单备料
            case GloableParams.GRIDNAME_MATERIAL_PREPARE:
                fragment = new SCMakePrepareListFragment();
                getFragmentManager().beginTransaction().addToBackStack(null)
                        .replace(R.id.container_function_fragment, fragment,FunctionActivity.TAG_SCMAKE).commit();
                break;
            //飞达上料
            case GloableParams.GRIDNAME_MATERIAL_ADD:
                break;
            //尾料还仓
            case GloableParams.GRIDNAME_SURPLUS_RECURRENCE:
                break;
        }
    }
}
