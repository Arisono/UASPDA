package com.uas.uaspda.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uas.uaspda.bean.ProductIn;
import com.uas.uaspda.bean.UnCollect;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @note:Json与Bean转化工具类
 */
public class GsonUtil {

    public static <T> T changeGsonToBean(String gsonString, Class<T> tClass){
        Gson gson = new Gson();
        T t = gson.fromJson(gsonString,tClass);
        return t;
    }


    //SCmake:工单备料，备料工单
    public static List changeGsonToList(String gsonString,Type type) {
        Gson gson = new Gson();
        List list = gson.fromJson(gsonString, type);
        return list;
    }

    //SCmake:工单备料，待采集清单
    public static List<UnCollect> changeGsonToUnCollectList(String gsonString) {
        Gson gson = new Gson();
        List<UnCollect> list = gson.fromJson(gsonString,
                new TypeToken<List<UnCollect>>() {
                }.getType());
        return list;
    }

    public static String getProdListGsonString(List localProdList){
        Gson gson = new Gson();
        String localProcRootJson = gson.toJson(localProdList);
        return localProcRootJson;
    }
    //Inmake
    public static List<ProductIn> changeGsonToProdList(String gsonString) {
        Gson gson = new Gson();
        List<ProductIn> list = gson.fromJson(gsonString,
                new TypeToken<List<ProductIn>>() {
                }.getType());
        return list;
    }


    private GsonUtil(){}
}
