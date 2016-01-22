package com.uas.uaspda.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uas.uaspda.bean.Whcode;
import com.uas.uaspda.datasource.DataSourceManager;
import com.uas.uaspda.exception.NullListException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @note:Volley操作类
 */
public class VolleyUtil {
    private static final String TAG_StrListener = "VolleyUtil:StrListener";
    private static final String TAG_JSONRESPONSE = "VolleyUtil:JsonResponse";
    //请求方式
    public static final int METHOD_POST = 1;
    public static final int METHOD_GET = 2;
    //请求标识
    public static final int FRAGMENT_LOGIN = 21;
    public static final int FRAGMENT_CONNECTSERVER = 22;
    public static final int ACTIVITY_MENU = 23;
    public static final int FRAGMENT_INMAKE_LIST = 241;
    public static final int FRAGMENT_INMAKE_WHCODE = 242;
    public static final int FRAGMENT_INMAKE_PRODINDATA = 243;
    public static final int FRAGMENT_INMAKE_RECOLLECT = 244;
    public static final int FRAGMENT_SHOP_PREPARELIST = 251;
    public static final int FRAGMENT_SCMAKE_UNCOLLECT = 252;
    public static final int FRAGMENT_SCMAKE_COLLECT = 253;
    public static final int FRAGMENT_SCMAKE_WITHDRAW = 254;

    //3被用在InmakeFragment
    private static Context context;
    private static VolleyUtil volleyUtil = null;
    private static RequestQueue requestQueue = null;
    private static JsonObjectRequest jsonRequest;
    private static JsonResponseListener jsonListener;
    private static ResponseErrorListener errorListener;
    private static StringRequest stringRequest;
    private static StringResponseListener stringListener;

    Message msg;
    public static Handler volleyHandler;
    private static int requestType = -1;
    static String cookies;

    /*===========================外部获取函数================================*/
    /**
     * @注释:取消单条备料采集数据
     * */
    public void requestWithdrawSingle(Context pContext, String pUrl, int method, int pRequestType, final String pBarcode, final int pMpid){
        context = pContext;
        requestType = pRequestType;
        requestQueue = Volley.newRequestQueue(pContext);
        Log.e("VOLLEYURI",pUrl);
        if(method == METHOD_GET){
            stringRequest = new StringRequest(Request.Method.POST,pUrl,stringListener,errorListener){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return setCookies();
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map paramsMap = new HashMap();
                    paramsMap.put("barcode",pBarcode);
                    paramsMap.put("mpid",""+pMpid);
                    return paramsMap;
                }
            };
        }
        else {}
        requestQueue.add(stringRequest);
    }
    /**
     * @注释:采集数据
     * */
    public void requestCollectPrepare(Context pContext, String pUrl, int method, int pRequestType,final String pBarCode, final int pMpid){
        context = pContext;
        requestType = pRequestType;
        requestQueue = Volley.newRequestQueue(pContext);
        if(method == METHOD_GET){
            String tmpUri = pUrl+"?data={barcode:3335,mpid:4769,maid:1,whcode:'001'}";
            Log.e("VOLLEYURI",tmpUri);
            stringRequest = new StringRequest(Request.Method.GET,pUrl,stringListener,errorListener){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return setCookies();
                }
            };
        }
        else{
            Log.e("VOLLEYURI", pUrl);
            Log.e("VOLLEYDATA","barcode"+pBarCode+"mpid"+pMpid);
            JSONObject tmpJson = new JSONObject();
            try {
                tmpJson.put("barcode",pBarCode);
                tmpJson.put("mpid",pMpid);
                tmpJson.put("maid",1);
                tmpJson.put("whcode","01");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonRequest = new JsonObjectRequest(Request.Method.POST,pUrl,tmpJson,jsonListener,errorListener){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return setCookies();
                }
            };
        }
        requestQueue.add(jsonRequest);
    }
    /**
     * @注释:请求工单备料的未采集订单
     * */
    public void requestUncollectList(Context pContext, String pUrl, int method, int pRequestType, final int pParams){
        context = pContext;
        requestType = pRequestType;
        requestQueue = Volley.newRequestQueue(context);
        if(method == METHOD_GET){
            String getUrl = pUrl+ "?mp_id="+pParams;
            Log.e("VOLLEYURI",""+getUrl);
           // 请求对象
            stringRequest = new StringRequest(Request.Method.GET,getUrl,
                    stringListener,errorListener){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return setCookies();
                }
            };
        }
        else{}
        requestQueue.add(stringRequest);
    }
    /**
     * @注释:ShopContent:请求备料单
     * */
    public void requestPrepareList(Context pContext, String pUrl, int method, int pRequestType){
        context = pContext;
        requestType = pRequestType;
        requestQueue = Volley.newRequestQueue(pContext);
        if(method == METHOD_GET){}
        else{
            stringRequest = new StringRequest(Request.Method.POST,pUrl,stringListener,errorListener){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return setCookies();
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map paramsMap = new HashMap();
                    paramsMap.put("type","barcode");
                    return paramsMap;
                }
            };
        }
        requestQueue.add(stringRequest);
    }
    /**
     * @注释：重新采集
     * */
    public void requestClearGet(Context pContext,String pUrl, int method, int pRequestType, final int pPiId, final String pWhcode){
        requestType = pRequestType;
        context = pContext;
        boolean isConnect = isNetworkAvailable();
        if(!isConnect){
            sendHandler(FAILED_FAILED,"网络连接不可用");
            return;
        }
        requestQueue = Volley.newRequestQueue(pContext);
        if(method == METHOD_GET){}
        else {
//            JSONObject tmpJson = new JSONObject();
//            try {
//                tmpJson.put("id", pPiId);
//                tmpJson.put("whcode",pWhcode);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Log.e("URILIST", pUrl);
//            Log.e("PARAMS", tmpJson.toString());
//            jsonRequest = new JsonObjectRequest(Request.Method.POST,pUrl,tmpJson,jsonListener,errorListener){
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    return setCookies();
//                }
//            };
            stringRequest = new StringRequest(Request.Method.POST,pUrl,
                    stringListener,errorListener){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return setCookies();
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> paramMap = new HashMap<>();
                    paramMap.put("id",""+pPiId);
                    paramMap.put("whcode",pWhcode);
                    return paramMap;
                }
            };
        }
        requestQueue.add(stringRequest);
    }
    /**
     * @注释：获取入库单待采集信息
     */
    public void requestGetProdInData(Context pContext,String url, int method,int pRequestType, final Whcode pParam){
        requestType = pRequestType;
        context = pContext;
        requestQueue = Volley.newRequestQueue(pContext);
        boolean isConnect = isNetworkAvailable();
        if(!isConnect){
            sendHandler(FAILED_FAILED,"网络连接不可用");
            return;
        }
        if(method == METHOD_GET){}
        else {
            Log.e("URILIST", url);
            stringRequest = new StringRequest(Request.Method.POST,url,
                    stringListener,errorListener){
                //Cookie
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return setCookies();
                }
                //参数
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> paramMap = new HashMap<>();
                    paramMap.put("inoutNo",pParam.getPiInoutno());
                    paramMap.put("whcode",pParam.getPdWhcode());
                    paramMap.put("pi_id",pParam.getPiId());
                    return paramMap;
                }
            };
            requestQueue.add(stringRequest);
        }
    }

    /**
     * @注释：获取入库单详细信息
     */
    public void requestGetWhcode(Context pContext,String url, int method, int pRequestType, final String pParams){
        requestType = pRequestType;
        context = pContext;
        requestQueue = Volley.newRequestQueue(pContext);
        if(method == METHOD_GET){}
        else{
            Log.e("URILIST", url);
            stringRequest = new StringRequest(Request.Method.POST,url,
                    stringListener,errorListener){
                //获取Cookie
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> paramHead = new HashMap<>();
                    paramHead.put("Cookie",cookies);
                    return paramHead;
                }
                //获取参数
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> paramMap = new HashMap<>();
                    paramMap.put("inoutNo",pParams);
                    return paramMap;
                }
            };
        }
        requestQueue.add(stringRequest);
    }
    /**
     * @注释：获取入库单
     */
    public void requestInoutNumber(Context pContext,String url, int method, int pRequestType, final String params){
        requestType = pRequestType;
        context = pContext;
        requestQueue = Volley.newRequestQueue(pContext);
        JSONObject paramJson = new JSONObject();
        try {
            paramJson.put("inoutNo",params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(method == METHOD_GET){}
        //POST及其他方式
        else{
            Log.e("URILIST",url);
            jsonRequest = new JsonObjectRequest(Request.Method.POST,url,paramJson,
                    jsonListener,
                    errorListener){
                //传递Cookie
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Cookie", cookies);
                    headers.put("Content-Type","application/json");
                    return headers;
                }
            };
        }
        requestQueue.add(jsonRequest);
    }
    /**
     * @注释：连接服务器
     */
    public void requestConnectServer(Context pContext, String url, int method, int pRequestType) {
        requestType = pRequestType;
        context = pContext;
        requestQueue = Volley.newRequestQueue(pContext);
        boolean isConnect = isNetworkAvailable();
        if(!isConnect){
            sendHandler(FAILED_FAILED,"网络连接不可用");
            return;
        }
        //get方式请求
        if (method == METHOD_GET) {
            jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    jsonListener,
                    errorListener);
        }
        //post方式
        else {
            Log.e("URILIST", url);
            jsonRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                    jsonListener,
                    errorListener);
        }
        requestQueue.add(jsonRequest);
    }

    /**
     * @注释:登录：登录
     */
    public static void requestUserLogin(Context pContext, String pUrl, int pMethod, int pRequestType,
                                        final String pMaster, final String pUserName, final String pPassword) {
        //通知连接页面类型
        requestType = pRequestType;
        context = pContext;
        getVolleyUtil();
        //get方式
        if (pMethod == METHOD_GET) {
            stringRequest = new StringRequest(Request.Method.GET, pUrl,
                    stringListener, errorListener);
        }
        //post
        else {
            stringRequest = new StringRequest(Request.Method.POST, pUrl,
                    stringListener, errorListener) {
                //重写parseNetworkResponse，获得服务器返回的cookies，修改字符编码
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String str = null;
                    try {
                        Map<String, String> responseHeaders = response.headers;
                        //保存cookies
                        cookies = responseHeaders.get("Set-Cookie");
                        cookies = StringUtil.splitCookieString(cookies);
                        Log.e("!!!CookiesResponseSplit", cookies);
                        //修改字符编码
                        str = new String(response.data, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> dataMap = new HashMap();
                    dataMap.put("j_username", pUserName);
                    dataMap.put("j_password", pPassword);
                    dataMap.put("master", pMaster);
                    Log.e("Jsondata", dataMap.toString());
                    return dataMap;
                }
            };
        }
        requestQueue.add(stringRequest);
    }

    /**
     * @注释:主菜单：主菜单,Menu页面获取储位
     */
    public static void requestGetUseLocationOrNot(Context pContext, String pUrl, int pMethod, int pRequestType) {
        requestType = pRequestType;
        requestQueue = Volley.newRequestQueue(pContext);
        if (pMethod == VolleyUtil.METHOD_GET) {
        } else {
            stringRequest = new StringRequest(Request.Method.POST, pUrl,
                    stringListener, errorListener) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("caller", "BarCodeSetting");
                    params.put("tablename", "configs");
                    params.put("field", "data");
                    params.put("condition", "code='UseLocationOrNot' and caller='BarCodeSetting'");
                    return params;
                }

                //传递Cookie
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Cookie", cookies);
                    return headers;
                }
            };
        }
        requestQueue.add(stringRequest);
    }

    public static void getUseLocationString(Context pContext, String pUrl, int pMethod, int pRequestType) {
        if (pMethod == VolleyUtil.METHOD_GET) {

        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(pContext);
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, pUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {

                            Toast.makeText(context, jsonObject.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("UseLocation", "Loa成功" + jsonObject.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(context, volleyError.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("UseLocation", "Loa失败" + volleyError.toString());
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Cookie", cookies);
                    headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                    return headers;
                }


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("caller", "BarCodeSetting");
                    params.put("tablename", "configs");
                    params.put("field", "data");
                    params.put("condition", "code='UseLocationOrNot' and caller='BarCodeSetting'");
                    return params;
                }
            };
            requestQueue.add(req);
        }

    }

/*=======================Volley监听类Listener==============================*/
    public static final String TAG_JSONLISTENER = "Volley:JsonListener";
    /**
     * @注释：Volley响应成功Listener_String格式
     */
    private class StringResponseListener implements Response.Listener<String> {
        @Override
        public void onResponse(String s) {
            Log.e(TAG_StrListener, "响应成功"+s);
            switch (requestType) {
                //如果页面是登录页面
                case VolleyUtil.FRAGMENT_LOGIN:
                    Log.e(TAG_StrListener, "登录响应成功");
                    disposeLogin(s);
                    requestType = -1;
                    break;
                //如果页面是主菜单页面
                case VolleyUtil.ACTIVITY_MENU:
                    Log.e(TAG_StrListener, "储位响应成功");
                    disposeUseLocationOrNot(s);
                    requestType = -1;
                    break;
                //如果是请求入库单详细信息
                case VolleyUtil.FRAGMENT_INMAKE_WHCODE:
                    Log.e(TAG_StrListener, "入库单详细信息响应成功");
                    disposeInmakeWhcode(s);
                    requestType = -1;
                    break;
                case VolleyUtil.FRAGMENT_INMAKE_PRODINDATA:
                    Log.e(TAG_StrListener, "入库单待采集信息响应成功");
                    disposeInmakeProd(s);
                    break;
                //请求备料单
                case VolleyUtil.FRAGMENT_SHOP_PREPARELIST:
                    Log.e(TAG_StrListener, "请求备料单响应成功");
                    disposePrepareList(s);
                    break;
                //请求备料单待采集订单
                case VolleyUtil.FRAGMENT_SCMAKE_UNCOLLECT:
                    Log.e(TAG_StrListener, "请求备料单待采集订单响应成功");
                    disposeUncollectList(s);
                    break;
                //取消一条备料采集
                case VolleyUtil.FRAGMENT_SCMAKE_WITHDRAW:
                    Log.e(TAG_StrListener, "请求取消一条备料采集响应成功");
                    disposeWithdraw(s);
                    break;
            }
        }
    }

    /**
     * @注释：Volley响应成功Listener_JSON格式
     */
    private class JsonResponseListener implements com.android.volley.Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e("TAG_JSONRESPONSE", "JSON响应success" + jsonObject.toString());
            switch (requestType) {
                //如果页面是连接服务器页面
                case VolleyUtil.FRAGMENT_CONNECTSERVER:
                    disposeConnectServer(jsonObject);
                    requestType = -1;
                    break;
                //获取模糊搜索订单列表
                case VolleyUtil.FRAGMENT_INMAKE_LIST:
                    Log.e(TAG_JSONLISTENER, "获取订单成功" + jsonObject.toString());
                    disposeGetInMakeNumber(jsonObject);
                    requestType = -1;
                    break;
                //重新采集入库单
                case VolleyUtil.FRAGMENT_INMAKE_RECOLLECT:
                    disposeRecollect();
                    break;
                //采集备料单
                case VolleyUtil.FRAGMENT_SCMAKE_COLLECT:
                    disposeCollect(jsonObject);
                    break;
            }
        }
    }

    /**
     * @注释:Volley响应失败Listener
     */
    private class ResponseErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Log.e("ErrorListener", "!volleyError" + volleyError.getMessage() +":"+ volleyError.toString());
            //当连接不上服务器时
            String errorMsg = "连接服务器失败";
            //服务器返回的错误信息
            if(volleyError.networkResponse != null){
                byte[] htmlBodyBytes = volleyError.networkResponse.data;
                String errorInfo = new String(htmlBodyBytes);
                Log.e("ErrorListener","!volleyInfo"+ errorInfo);
                //服务器传递的错误信息
                try {
                    JSONObject errorJson = new JSONObject(errorInfo);
                    errorMsg = errorJson.getString("exceptionInfo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            switch (requestType){
//                case FRAGMENT_INMAKE_PRODINDATA:
//                    //errorMsg = "获取入库单待采集信息失败";
//                    break;
//                case FRAGMENT_INMAKE_WHCODE:
//                    //errorMsg = "获取入库单信息失败";
//                    break;
//                default:
//                    //errorMsg = "连接失败";
//                    break;
//            }
            sendHandler(FAILED_FAILED,errorMsg);
//            if (volleyHandler != null) {
//                getNewMessage();
//                msg.what = VolleyUtil.FAILED_FAILED;
//                volleyHandler.sendMessage(msg);
//            }
        }
    }

    /*========================服务器响应成功处理函数==============================*/
    private static final String TAG_DPSLOCATION = "VolleyUtil:disLocation";

    /**
     * @note:取消一条备料采集
     */
    private void disposeWithdraw(String result){
        sendHandler(SUCCESS_SUCCESS,result);
    }
    /**
     * @note:采集备料
     */
    private void disposeCollect(JSONObject resultJson){
        Log.e("VolleyUtil:Collect",resultJson.toString());
        sendHandler(SUCCESS_SUCCESS,resultJson.toString());
    }
    /**
     * @note:处理待采集函数
     */
    private void disposeUncollectList(String result){
        DataSourceManager.getDataSourceManager().setUnCollectList(result);
    }
    /**
     * @note:处理请求入库单函数
     */
    private void disposePrepareList(String result){
        DataSourceManager.getDataSourceManager().setPrepareList(result,context);
    }
    /**
     * @note:处理重新采集信息事件
     */
    private void disposeRecollect(){
        sendHandler(SUCCESS_INMAKE_RECOLLECT,null);
    }
    /**
     * @note:处理入库单待采集信息事件
     */
    private void disposeInmakeProd(String result){
        Log.e("Volley:InmakeProd",result);
        DataSourceManager.getDataSourceManager().addProcInmakeList(result);
        sendHandler(SUCCESS_INMAKE_PRODUCE,null);
    }
    /**
     * @note:处理请求订单详细信息事件
     */
    private void disposeInmakeWhcode(String result){
        DataSourceManager.getDataSourceManager().setNetWhcodeList(result);
        //提醒InMakeMaterialFragment数据完成
        sendHandler(SUCCESS_SUCCESS,null);
    }
    /**
     * @note:处理请求订单事件
     */
    private void disposeGetInMakeNumber(JSONObject jsonObject){
        DataSourceManager.getDataSourceManager().setNetInmakeList(jsonObject);
    }
    /**
     * @note:处理储位事件
     */
    private void disposeUseLocationOrNot(String s) {
        try {
            JSONObject tmpJson = new JSONObject(s);
            String description = tmpJson.getString("description");
            boolean success = tmpJson.getBoolean("success");
            Log.e(TAG_DPSLOCATION, description + "%" + success);
            SharedPreUtil.saveString(context, SharedPreUtil.KEY_USELOCATION, description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @note:处理登录事件
     */
    private void disposeLogin(String s) {
        //登录成功
        boolean isSuccess = false;
        try {
            JSONObject jsonObject = new JSONObject(s);
            isSuccess = jsonObject.getBoolean("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isSuccess || s.equals("")) {
            Log.e("ResponseListener", "success" + s);
            //发送跳转命令
            if (volleyHandler != null) sendHandler(SUCCESS_SUCCESS, null);
            releaseHandler();
            requestType = -1;
        }
        //登录失败
        else {
            //  Toast.makeText(context,"登录失败"+ s,Toast.LENGTH_SHORT).show();
            //发送登录失败命令
            if (volleyHandler != null) sendHandler(SUCCESS_FAILED, s);
            Log.e("ResponseListener", "failed" + s);
        }
    }

    /**
     * @note:处理连接服务器事件
     */
    private void disposeConnectServer(JSONObject jsonObject) {
        //将获得的数据传递给DataManager
        try {
            DataSourceManager.getDataSourceManager().setMasterList(jsonObject.toString());
            requestType = -1;
            if (volleyHandler != null) sendHandler(SUCCESS_SUCCESS, null);
            releaseHandler();
        } catch (NullListException e) {
            e.printStackTrace();
        }
    }

/*======================Handler操作===============================================*/
    //连接服务器结果
    public static final int SUCCESS_SUCCESS = 11;
    public static final int SUCCESS_FAILED = 12;
    public static final int FAILED_FAILED = 13;

    //获入库单取详细信息成功
    public static final int SUCCESS_INMAKE_PRODUCE = 14;
    //重新采集请求成功
    public static final int SUCCESS_INMAKE_RECOLLECT = 15;

    //传递Handler
    public static void setVolleyHandler(Handler pHandler) {
        volleyHandler = pHandler;
    }

    private static void releaseHandler() {
        volleyHandler = null;
    }

    private void sendHandler(int flag, String errorMsg) {
        //通知页面连接服务器成功
        getNewMessage();
        switch (flag) {
            case SUCCESS_SUCCESS:
                msg.obj = errorMsg;
                break;
            case SUCCESS_FAILED:
                msg.obj = errorMsg;
                break;
            case FAILED_FAILED:
                Log.e("VolleyHandler","volleyHandler.sendFailedMessage(msg)");
                msg.obj = errorMsg;
                break;
            case SUCCESS_INMAKE_PRODUCE:
                break;
            case SUCCESS_INMAKE_RECOLLECT:
                break;
        }
        msg.what = flag;
        if(volleyHandler == null) {
            Log.e("VolleyHandler",""+null);
            return;}
        Log.e("VolleyHandler","volleyHandler.sendFailedMessage(msg)");
        volleyHandler.sendMessage(msg);
    }

    private Message getNewMessage() {
        msg = new Message();
        //如果多次发送msg要进行判断，不能多次发送相同的msg
        if (volleyHandler != null && volleyHandler.obtainMessage(msg.what, msg.obj) != null) {
            Message _msg = new Message();
            _msg.what = msg.what;
            _msg.obj = msg.obj;
            msg = _msg;
        }
        return msg;
    }

    /*=====================公用方法==============================================*/

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @return true 表示网络可用
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
                else{
                    sendHandler(FAILED_FAILED,"网络连接不可用");
                    return false;
                }
            }
        }
        return false;
    }

    //request携带cookies
    private Map<String,String>  setCookies() {
        Map<String,String> paramHead = new HashMap<>();
        paramHead.put("Cookie",cookies);
        Log.e("VolleyUtil:Cookie",cookies);
        return paramHead;
    }

    /*=====================Volley配置函数==============================================*/
    private VolleyUtil() {
        //创建请求队列
        jsonListener = new JsonResponseListener();
        errorListener = new ResponseErrorListener();
        stringListener = new StringResponseListener();
        //获取Message对象
        msg = Message.obtain();
    }

    public static VolleyUtil getVolleyUtil() {
        if (volleyUtil == null) {
            volleyUtil = new VolleyUtil();
        }
        return  volleyUtil;
    }
}
