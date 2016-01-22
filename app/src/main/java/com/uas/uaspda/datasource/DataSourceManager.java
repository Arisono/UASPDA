package com.uas.uaspda.datasource;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.uas.uaspda.GloableParams;
import com.uas.uaspda.bean.Master;
import com.uas.uaspda.bean.Mpcode;
import com.uas.uaspda.bean.ProductIn;
import com.uas.uaspda.bean.UnCollect;
import com.uas.uaspda.bean.Whcode;
import com.uas.uaspda.exception.NullListException;
import com.uas.uaspda.fragment.ICInMakeMaterialFragment;
import com.uas.uaspda.fragment.LoginFragment;
import com.uas.uaspda.fragment.SCMakePrepareListFragment;
import com.uas.uaspda.util.GsonUtil;
import com.uas.uaspda.util.SharedPreUtil;
import com.uas.uaspda.util.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @注释：数据源管理者类，单例模式
 */
public class DataSourceManager {
    private static  DataSourceManager manager = null;
    //服务器返回的所有JSON格式数据 顶层关键字都是data
    private static final String KEY_RESULE = "data";


    /*=============================Detail页面 工单备料======================================*/
    //prepareList数据
    List<Mpcode.Message> prepareList = null;
    /*********
     * 服务器请求到的工单备料
     ***************/
    public void setPrepareList(String s,Context pContext) {
        if(prepareList == null){
            prepareList = new ArrayList<>();
        }
        prepareList.removeAll(prepareList);
        Mpcode mpcode;
        try {
            JSONObject jsonObject = new JSONObject(s);
            //如果返回的是空，查询无结果
            String msgStr = jsonObject.getString("message").trim();
            if ( msgStr.equals("null")||msgStr.equals("") || msgStr == null) {
                //提示查询无结果
                Log.e("DataSource:Prepare","查询无结果");
            }
            //查询结果有值
            else {
                mpcode = GsonUtil.changeGsonToBean(s, Mpcode.class);
                prepareList.addAll(mpcode.getMessage());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //通知数据源发生变化
        notifyData.NotifyDataChanged(SCMakePrepareListFragment.NOTICE_PREPARE_ADAPTER);
    }
    public List<Mpcode.Message> getPrepareList() {
        if (prepareList == null) {
            prepareList = new ArrayList<Mpcode.Message>();
        }
        return prepareList;
    }
    //保存缓存
    public void saveLocalPrepareListCache(List resultList,Context pContext){
        //将List对象转换成String
        String listValue = GsonUtil.getProdListGsonString(resultList);
        Log.e("DATASOURCE","cache"+listValue);
        //存入缓存
        SharedPreUtil.saveString(pContext, SharedPreUtil.KEY_SCMAKE_PREPARE, listValue);
    }
    //获取缓存
    public void getLocalPrepareListCache(Context pContext){
        if(prepareList==null) {
            Log.e("datasource","prepareList is null");
            prepareList = new ArrayList<>();
        }
       // SharedPreUtil.removeString(pContext,SharedPreUtil.KEY_SCMAKE_PREPARE);
        String listValue = SharedPreUtil.getString(pContext, SharedPreUtil.KEY_SCMAKE_PREPARE);
        List tmpList= GsonUtil.changeGsonToList(listValue,
                new TypeToken<List<Mpcode.Message>>() {
                }.getType());
        prepareList.removeAll(prepareList);
        //如果缓存中有值
        if(tmpList != null){
            prepareList.addAll(tmpList);
        }
        //通知数据源发生变化
        notifyData.NotifyDataChanged(SCMakePrepareListFragment.NOTICE_PREPARE_ADAPTER);
    }

    /*********
     * 待采集备料单
     ***************/
    List<UnCollect> unCollectList = null;
    public void setUnCollectList(String result){
        if(unCollectList == null){
            unCollectList = new ArrayList<>();
        }
        try {
            unCollectList.removeAll(unCollectList);
            JSONObject resultJson = new JSONObject(result);
            String message = resultJson.get("message").toString();
            if(message.equals("null") || message == null){
                Log.e("DataSource:unCollect",""+null);
                //顶顶顶顶
            }
            else{
                Log.e("DataSource:unCollect","unCollectListdata:"+message);
                List tmpList = GsonUtil.changeGsonToUnCollectList(message);
                unCollectList.addAll(tmpList);
                Log.e("DataSource:unCollect","unCollectList:"+message.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyData.NotifyDataChanged(0);
    }
    public List getUncollectList(Context pContext, int pParams){
        if(unCollectList == null){
            unCollectList =  new ArrayList();
        }
        VolleyUtil.getVolleyUtil().requestUncollectList(pContext, GloableParams.ADDRESS_UNCOLLECT_APPLY,
                VolleyUtil.METHOD_GET, VolleyUtil.FRAGMENT_SCMAKE_UNCOLLECT, pParams);
        return unCollectList;
    }

    /*=============================SpinnerList数据=======================================*/
    private ArrayList spinnerItemList;
    public static final String KEY_SPINNER_ITEMNAME = "SpinneritemName";

    //获取SCMAKE:Search下拉EditText数据
    public ArrayList getPrepareSpinnerItemList(){
        spinnerItemList = new ArrayList<>();
        for( String s: GloableParams.scmakeSpinnerNames){
            spinnerItemList.add(s);
        }
        return spinnerItemList;
    }


    /*====================================请求入库单=======================================*/
    private static final String KEY_INMAKE_SEARCH = "pi_inoutno";
    //入库单号List，搜索popWin的List
    List<String> netInmakeList = new ArrayList<>();
    //详细入库单信息List
    List<Whcode> netWhcodeList;
    //入库单待采集信息List
    List<ProductIn> localProdList;
    //popMenuList
    List<Map<String, Object>> menuNameList;
    public static final String KEY_MENU_IMG = "menuImg";
    public static final String KEY_MENU_NAME = "menuName";

    /*********
     * popMenuLocal显示数据源List
     ***************/
    @SuppressLint("NewApi")
    public List<Map<String, Object>> getMenuList(Context pContext, String pMark) {
        // if(menuNameList == null){
        menuNameList = new ArrayList();
        //  }
        //  if(pMark.equals(GloableParams.ENAUDITSTATUS_COLLECTED))
        String[] menuNames = GloableParams.inmakeLocalMenuNames;
        int[] menuImgs = GloableParams.inmakeLocalMenuImgs;
        Map tmpMap;
        for (int i = 0; i < menuNames.length; i++) {
            tmpMap = new HashMap();
            tmpMap.put(KEY_MENU_IMG, menuImgs[i]);
            tmpMap.put(KEY_MENU_NAME, menuNames[i]);
            menuNameList.add(tmpMap);
        }
        return menuNameList;
    }

    /*********
     * 服务器请求到的入库单：模糊查询List显示的数据用于获取WhcodeList
     ***************/
    public void setNetInmakeList(JSONObject jsonResult) {
        JSONArray inmakeJsonArray = null;
        //清空原先存在的入库单list
        netInmakeList.removeAll(netInmakeList);
        try {
            inmakeJsonArray = jsonResult.getJSONArray(KEY_RESULE);
            for (int i = 0; i < inmakeJsonArray.length(); i++) {
                JSONObject tmpJson = inmakeJsonArray.getJSONObject(i);
                netInmakeList.add(tmpJson.getString(KEY_INMAKE_SEARCH));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //提醒数据源变化
        notifyData.NotifyDataChanged(ICInMakeMaterialFragment.NOTICE_SEARCH_ADAPTER);
    }

    public List getNetInMakeList() {
        return netInmakeList;
    }

    /*********
     * 服务器请求到的入库详细单：用于根据（入库单号+仓库号）来选择某一入库单
     ***************/
    public void setNetWhcodeList(String result) {
        netWhcodeList = new ArrayList();
        try {
            JSONObject whcodeJson = new JSONObject(result);
            JSONArray whcodeJsonArray = whcodeJson.getJSONArray(KEY_RESULE);
            for (int i = 0; i < whcodeJsonArray.length(); i++) {
                JSONObject tmpJson = whcodeJsonArray.getJSONObject(i);
                String tmpInoutno = tmpJson.getString("pi_inoutno");
                String tmpClass = tmpJson.getString("pi_class");
                String tmpWhcode = tmpJson.getString("pd_whcode");
                String tmpId = tmpJson.getString("pi_id");
                Whcode tmpWhcodeBeen = new Whcode(tmpInoutno, tmpClass, tmpWhcode, tmpId);
                netWhcodeList.add(tmpWhcodeBeen);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Whcode> getNetWhcodeList() {
        return netWhcodeList;
    }

    /*********
     * 服务器请求到的入库单待采集信息：入库单的采集信息，存缓存信息
     ***************/
    //添加一条 服务器请求入库单待采集信息 记录,因为每次用户只能点击List中的一条，所以add不是set
    public boolean addProcInmakeList(String result) {
        boolean flag = false;
        Log.e("DataMan:addProList", result);
        ProductIn proc = GsonUtil.changeGsonToBean(result, ProductIn.class);
        //将入库单待采集信息添加到List中
        for (int i = 0; i < localProdList.size(); i++) {
            Log.e("DataMan:addProList", "p " + localProdList.get(i).getTarget().get(0).getPi_inoutno());
        }
        localProdList.add(proc);
        for (int i = 0; i < localProdList.size(); i++) {
            Log.e("DataMan:addProList", localProdList.get(i).getTarget().get(0).getPi_inoutno());
        }
        notifyData.NotifyDataChanged(ICInMakeMaterialFragment.NOTICE_LOCAL_ADAPTER);
        flag = true;
        return flag;
    }

    //缓存加载入库单待采集信息
    public List<ProductIn> getLocalProcList(Context pContext) {
        if (localProdList == null) {
            localProdList = new ArrayList<ProductIn>();
        }
        //  SharedPreUtil.removeString(pContext, SharedPreUtil.KEY_INMAKE_LOCALPROD);
        localProdList.removeAll(localProdList);
        //获取缓存Json
        String localStr = SharedPreUtil.getString(pContext, SharedPreUtil.KEY_INMAKE_LOCALPROD);

        if (localStr != null) {
            //将缓存转换为对象List
            //Log.e("DataSource:localStr",localStr);
            localProdList = GsonUtil.changeGsonToProdList(localStr);
        }
        return localProdList;
    }

    //将proc信息存入缓存
    public void saveLocalProcCache(Context pContext) {
        //将对象List转变为JsonString，添加至缓存
        String localProcRootJson = GsonUtil.getProdListGsonString(localProdList);
        SharedPreUtil.saveString(pContext, SharedPreUtil.KEY_INMAKE_LOCALPROD, localProcRootJson);
    }

    /*=======================================账套=======================================*/
    //保存了所有账套信息
    private List<Master> masterList;
    //只保存账套名称，用于list控件绑定的数据源list
    private List<String> masterFuncList;

    /**
     * @注释:配置masterList数据源
     * @param:从服务器获取的json数据
     */
    public void setMasterList(String masterListJson) throws NullListException {

        masterList = new ArrayList<Master>();
        JSONObject rootMaster = null;
        try {
            /*将获取的Json数据转换成List对象，配置masterList*/
            rootMaster = new JSONObject(masterListJson);
            JSONArray masterJsonArray = rootMaster.getJSONArray("masters");
            for (int i = 0; i < masterJsonArray.length(); i++) {
                JSONObject masterJson = masterJsonArray.getJSONObject(i);
                String maId = masterJson.getString("ma_id");
                String maName = masterJson.getString("ma_name");
                String maFunction = masterJson.getString("ma_function");
                Master masterTmp = new Master(maId, maName, maFunction);
                if (masterList == null) {
                    throw new NullListException();
                }
                masterList.add(masterTmp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //每次masterList更新后，同步更新masterFuncList
        setMasterFuncList();
    }

    public List<Master> getMasterList() {
        return masterList;
    }

    private void setMasterFuncList() throws NullListException {
        masterFuncList = new ArrayList<String>();
        if (masterList == null) throw new NullListException();
        for (int i = 0; i < masterList.size(); i++) {
            masterFuncList.add(masterList.get(i).getMaFunction());
        }
        //通知数据源发送变化
        LoginFragment.NotifySpinnerDataChanged();
    }

    public List<String> getMasterFuncList() {
        return masterFuncList;
    }


    /*=======================================URI缓存=======================================*/
    //Uri缓存
    public static final String KEY_IP = "ip";
    public static final String KEY_PORT = "port";
    public static final String KEY_URIARRAY = "uriArray";
    private List<Map<String, String>> uriList;
    private List<String> uriIpList;
    JSONArray uriJsonArray;

    /**
     * @注释:配置uri缓存的数据源
     */
    public void setUriList(String uriJsonString) {
        uriList = new ArrayList<>();
        try {
            JSONObject rootJson = new JSONObject(uriJsonString);
            uriJsonArray = rootJson.getJSONArray(KEY_URIARRAY);
            for (int i = 0; i < uriJsonArray.length(); i++) {
                JSONObject tmpJson = uriJsonArray.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put(KEY_IP, tmpJson.getString(KEY_IP));
                map.put(KEY_PORT, tmpJson.getString(KEY_PORT));
                uriList.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Uri缓存更新，同步更新ipList
        setUriIpList();
    }

    private void setUriIpList() {
        uriIpList = new ArrayList<>();
        for (int i = 0; i < uriList.size(); i++) {
            uriIpList.add(uriList.get(i).get(KEY_IP));
        }
    }

    //添加一个Uri记录到缓存Json数组
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String addUriItem(String ip, String port) {
        JSONObject tmpJson = new JSONObject(), rootJson = new JSONObject();
        try {
            if (uriJsonArray == null) {
                uriJsonArray = new JSONArray();
            }

            //将当前元素添加到第一个位置
            tmpJson.put(KEY_IP, ip);
            tmpJson.put(KEY_PORT, port);
            uriJsonArray.put(0, tmpJson);
            rootJson.put(KEY_URIARRAY, uriJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootJson.toString();
    }

    public List<Map<String, String>> getUriList() {
        return uriList;
    }

    public List<String> getUriIpList() {
        return uriIpList;
    }


    /*=======================================登录info=======================================*/

    //登录info缓存
    public static final String KEY_USERNAME = "key_username";
    public static final String KEY_PASSWORD = "key_password";
    public static final String KEY_INFOARRAY = "key_infoArray";
    public static final String NO_PWD = "\\t";
    private List<Map<String, String>> infoList;
    JSONArray infoJsonArray;

    /**
     * @注释:配置登录info缓存的数据源
     */
    public void setInfoList(String infoJsonString) {
        infoList = new ArrayList<>();
        try {
            JSONObject rootJson = new JSONObject(infoJsonString);
            infoJsonArray = rootJson.getJSONArray(KEY_INFOARRAY);
            for (int i = 0; i < infoJsonArray.length(); i++) {
                JSONObject tmpJson = infoJsonArray.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put(KEY_USERNAME, tmpJson.getString(KEY_USERNAME));
                map.put(KEY_PASSWORD, tmpJson.getString(KEY_PASSWORD));
                infoList.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List getInfoList() {
        return infoList;
    }

    //添加info缓存
    public String addInfoItem(String pUsername, String pPwd) {
        JSONObject tmpJson = new JSONObject(), rootJson = new JSONObject();
        if (pPwd == null) {
            pPwd = NO_PWD;
        }
        try {
            if (infoJsonArray == null) {
                infoJsonArray = new JSONArray();
            }
            tmpJson.put(KEY_USERNAME, pUsername);
            tmpJson.put(KEY_PASSWORD, pPwd);
            infoJsonArray.put(0, tmpJson);
            rootJson.put(KEY_INFOARRAY, infoJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootJson.toString();
    }

    /*=============================Index页面 Grid数据=======================================*/
    //主菜单
    private ArrayList<HashMap<String, Object>> gridItemList;
    //GridView绑定数据源的key
    public static final String KEY_GRID_ITEMNAME = "itemName";
    public static final String KEY_GRID_ITEMIMG = "itemImg";

    //IndexMain
    public ArrayList getIndexMainGridItemList() {
        gridItemList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> tmpMap;
        for (int i = 0; i < GloableParams.indexMainGridNames.length; i++) {
            tmpMap = new HashMap<>();
            tmpMap.put(KEY_GRID_ITEMNAME, GloableParams.indexMainGridNames[i]);
            tmpMap.put(KEY_GRID_ITEMIMG, GloableParams.indexMainGridImgs[i]);
            gridItemList.add(tmpMap);
        }
        return gridItemList;
    }

    //IndexInoutContent
    public ArrayList getInoutGridItemList() {
        gridItemList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> tmpMap;
        for (int i = 0; i < GloableParams.inoutContentGridNames.length; i++) {
            tmpMap = new HashMap<>();
            tmpMap.put(KEY_GRID_ITEMNAME, GloableParams.inoutContentGridNames[i]);
            tmpMap.put(KEY_GRID_ITEMIMG, GloableParams.inoutContentGridImgs[i]);
            gridItemList.add(tmpMap);
        }
        return gridItemList;
    }

    //IndexWorkhouseManager
    public ArrayList getWorkhouseMenuList() {
        String[] tmpNames = GloableParams.workhouseGridNames;
        int[] tmpImgs = GloableParams.worlhouseGridImgs;
        gridItemList = new ArrayList<>();
        HashMap<String, Object> tmpMap;
        for (int i = 0; i < GloableParams.workhouseGridNames.length; i++) {
            tmpMap = new HashMap();
            tmpMap.put(KEY_GRID_ITEMIMG, tmpImgs[i]);
            tmpMap.put(KEY_GRID_ITEMNAME, tmpNames[i]);
            gridItemList.add(tmpMap);
        }
        return gridItemList;
    }

    //IndexStorageManager
    public ArrayList getStorageMenuList() {
        String[] tmpNames = GloableParams.storageGridNames;
        int[] tmpImgs = GloableParams.storageGridImgs;
        gridItemList = new ArrayList<>();
        HashMap<String, Object> tmpMap;
        for (int i = 0; i < tmpNames.length; i++) {
            tmpMap = new HashMap();
            tmpMap.put(KEY_GRID_ITEMIMG, tmpImgs[i]);
            tmpMap.put(KEY_GRID_ITEMNAME, tmpNames[i]);
            gridItemList.add(tmpMap);
        }
        return gridItemList;
    }

    //IndexSetting
    /*==========================DataSoutceManager对象配置函数=================================*/

    /**
     * @note:获得DataSourceManager对象
     */
    //私有构造函数
    private DataSourceManager() {
    }

    public static synchronized DataSourceManager  getDataSourceManager() {
        if (manager == null) {
            manager = new DataSourceManager();
        }
        return manager;
    }

    //提醒其它界面数据变化
    NotifyData notifyData;

    public synchronized void  setNotifyData(NotifyData pNotifyData) {
        notifyData = pNotifyData;
    }

    public interface NotifyData {
        void NotifyDataChanged(int noticeType);
    }
}
