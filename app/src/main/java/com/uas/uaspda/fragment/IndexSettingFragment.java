package com.uas.uaspda.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uas.uaspda.FunctionActivity;
import com.uas.uaspda.R;

/**
 * @note:设置Fragment
 */
public class IndexSettingFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FunctionActivity.setTitle(getResources().getString(R.string.title_fragment_storage_index));
        View view = inflater.inflate(R.layout.fragment_setting,container,false);
        return view;
    }
}
