package com.uas.uaspda.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by LiuJie on 2016/1/13.
 */
public class AdapterFactory {
    private static AdapterFactory factory = null;

    //工单备料，备料单列表
    public MakePrepareAdapter createMakePrepareAdapter(Context pContext, List pList){
        return new MakePrepareAdapter(pContext,pList);
    }
    //工单备料，备料采集
    public ScMakeUncollectListAdapter createScMakeUnCollectListAdapter(Context pContext, List pList){
        return new ScMakeUncollectListAdapter(pContext,pList);
    }

    private AdapterFactory(){}

    public static AdapterFactory getAdapterFactory(){
        if(factory == null){
            factory = new AdapterFactory();
        }
        return factory;
    }
}
