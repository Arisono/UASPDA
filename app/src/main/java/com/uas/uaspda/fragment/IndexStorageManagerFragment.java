package com.uas.uaspda.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.uas.uaspda.FunctionActivity;
import com.uas.uaspda.R;
import com.uas.uaspda.datasource.DataSourceManager;

import java.util.List;
import java.util.Map;

/**
 * @note:仓库管理
 */
public class IndexStorageManagerFragment extends Fragment{
    GridView gridView;
    List<Map<String,Object>> gridItemList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FunctionActivity.setTitle(getResources().getString(R.string.title_fragment_storage_index));
        View view = inflater.inflate(R.layout.fragment_index_storagemanager,container,false);
        //获取组件
        gridView = (GridView) view.findViewById(R.id.grid_menu_storage_index);
        //配置数据
        gridItemList = DataSourceManager.getDataSourceManager().getStorageMenuList();
        //配置Adapter
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),gridItemList,R.layout.item_grid_first,
                new String[]{DataSourceManager.KEY_GRID_ITEMIMG,DataSourceManager.KEY_GRID_ITEMNAME},
                new int[]{R.id.first_griditem_img,R.id.first_griditem_name});
        gridView.setAdapter(adapter);
        return view;
    }
}
