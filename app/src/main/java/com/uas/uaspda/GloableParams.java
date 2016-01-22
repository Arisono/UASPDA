package com.uas.uaspda;

// INDEX表示目录页面，包括主页面和四个菜单页面
// DETAIL:表示详细功能界面
/**
 * Created by LiuJie on 2015/12/17.
 */
public class GloableParams {
    //记录用户请求IP
    private static String IP = null;
    private static String PORT = null;
    public static String ADDRESS_CONNECT_SERVER;
    public static String ADDRESS_LOGIN_APPLY;
    public static String ADDRESS_USELOCATION_APPLY;
    public static String ADDRESS_INOUTNO_APPLY;
    public static String ADDRESS_WHCODE_APPLY;
    public static String ADDRESS_PRODINDATA_APPLY;
    public static String ADDRESS_CLEARGET_APPLY;
    public static String ADDRESS_PREPARELIST_APPLY;
    public static String ADDRESS_UNCOLLECT_APPLY;
    public static String ADDRESS_COLLECT_APPLY;
    public static String ADDRESS_WITHDRAW_APPLY;

    //连接服务器请求地址
    private static final String ADDRESSTAIL_CONNECT_SERVER = "/ERP/mobile/getAllMasters.action";
    //登录页面请求地址
    private static final String ADDRESSTAIL_LOGIN_APPLY = "/ERP/pda/login.action";
    //请求储位地址
    private static final String ADDRESSTAIL_USELOCATION_APPLY = "/ERP/pm/bom/getDescription.action";
    //请求入库单号
    private static final String ADDRESSTAIL_INOUTNO_APPLY = "/ERP/pda/fuzzySearch.action";
    //请求入库单详细信息
    private static final String ADDRESSTAIL_WHCODE_APPLY = "/ERP/pda/getWhcode.action";
    //请求入库单待采集信息
    private static final String ADDRESSTAIL_PRODINDATA_APPLY = "/ERP/pda/getProdInData.action";
    //请求重新采集
    private static final String ADDRESSTAIL_CLEARGET_APPLY = "/ERP/pda/clearGet.action";
    //请求备料单数据
    private static final String ADDRESSTAIL_PREPARELIST_APPLY = "/ERP/pda/shopFloorManage/getMpcodeList.action";
    //请求备料单未采集订单
    private static final String ADDRESSTAIL_UNCOLLECT_APPLEY="/ERP/pda/shopFloorManage/needPreparedList.action";
    //请求提交采集数据
    private static final String ADDRESSTAIL_COLLECT_APPLY = "/ERP/pda/shopFloorManage/barGet.action";
    //取消备料
    private static final String ADDRESSTAIL_WITHDRAW_APPLY = "/ERP/pda/shopFloorManage/barBack.action";
    /*界面文字和图片资源,控制后续数据一致*/
    //主界面
    public static final String GRIDNAME_INOUT_STORAGE = "出入库";
    public static final String GRIDNAME_SHOPCONTENT = "车间管理";
    public static final String GRIDNAME_STORAGE_MANAGER = "仓库管理";
    public static final String GRIDNAME_SETTING = "设置";
    public static String[] indexMainGridNames = {GRIDNAME_INOUT_STORAGE, GRIDNAME_SHOPCONTENT,
            GRIDNAME_STORAGE_MANAGER, GRIDNAME_SETTING};
    public static int[] indexMainGridImgs = {R.drawable.mainmenu_outinstorage,
            R.drawable.mainmenu_workhousemanager, R.drawable.mainmenu_storehousemanager, R.drawable.mainmenu_usersetting};
    //INDEX:出入库管理Index
    public static final String GRIDNAME_CODEBAR_COLLECT = "条码采集";
    public static final String GRIDNAME_CODEBAR_VERIFY = "条码校验";
    public static String[] inoutContentGridNames = {GRIDNAME_CODEBAR_COLLECT,GRIDNAME_CODEBAR_VERIFY};
    public static int[] inoutContentGridImgs = {R.drawable.scanner_collect,R.drawable.scanner_verify};
    //DETAIL:Inmake界面PopMenu
    public static final String POPMENU_NAME_DELETE = "删除订单";
    public static final String POPMENU_NAME_RECOLLECT = "重新采集";
    public static int[] inmakeLocalMenuImgs = {R.drawable.delete,R.drawable.recollect};
    public static String[] inmakeLocalMenuNames = {POPMENU_NAME_DELETE,POPMENU_NAME_RECOLLECT};
    //订单采集状态码
    public static final String ENAUDITSTATUS_COLLECTED = "101";
    public static final String ENAUDITSTATUS_COLLECTING = "103";
    public static final String ENAUDITSTATUS_UNCOLLECT = "102";
    //INDEX:车间管理Index
    public static final String GRIDNAME_SMTMATERIAL_ADD = "SMT上料";
    public static final String GRIDNAME_MATERIAL_PREPARE = "工单备料";
    public static final String GRIDNAME_MATERIAL_ADD = "飞达上料";
    public static final String GRIDNAME_SURPLUS_RECURRENCE = "尾料还仓";
    public static String[] workhouseGridNames = {GRIDNAME_SMTMATERIAL_ADD,GRIDNAME_MATERIAL_PREPARE,GRIDNAME_MATERIAL_ADD,GRIDNAME_SURPLUS_RECURRENCE};
    public static int[] worlhouseGridImgs = {R.drawable.workhousemenu_smt_add,R.drawable.workhousemenu_material_prepare,R.drawable.workhousemenu_fd_add,R.drawable.workhousemenu_recurrence};
    //INDEX:仓库管理Index
    public static final String GRIDNAME_GOOD_SEARCH = "货物查找";
    public static final String GRIDNAME_BATCH_OPRATION = "拆批合批";
    public static final String GRIDNAME_STORAGE_TRANSFER = "储位转移";
    public static final String GRIDNAME_MSD_MANAGER = "MSD管理";
    public static final String GRIDNAME_WORK_INVENTORY = "盘点作业";
    public static final String[] storageGridNames = {GRIDNAME_GOOD_SEARCH,GRIDNAME_BATCH_OPRATION,GRIDNAME_STORAGE_TRANSFER,GRIDNAME_MSD_MANAGER,GRIDNAME_WORK_INVENTORY};
    public static final int[] storageGridImgs = {R.drawable.storage_good_search,R.drawable.storage_bach_operation,R.drawable.storage_transfer,R.drawable.storage_msd_manager,R.drawable.storage_work_inventory};
    //DETAIL:搜索备料单号，下拉列表
    public static final String SPINNER_PREPARE_SEARCH = "搜索备料单号";
    public static final String SPINNER_MAKECODE_SEARCH = "搜索制造单号";
    public static final String SPINNER_LINECODE_SEARCH = "搜索产线线别";
    public static final String[] scmakeSpinnerNames = {SPINNER_PREPARE_SEARCH,SPINNER_MAKECODE_SEARCH,SPINNER_LINECODE_SEARCH};

    //INDEX:setting Index


    //设置IP和PORT
    public static void setUri(String pIp,String pPort){
        GloableParams.IP = pIp;
        GloableParams.PORT = pPort;
        String uriHead = "http://"+IP+":"+PORT;

        //拼接各地址
        GloableParams.ADDRESS_CONNECT_SERVER = uriHead+GloableParams.ADDRESSTAIL_CONNECT_SERVER;
        GloableParams.ADDRESS_LOGIN_APPLY = uriHead+GloableParams.ADDRESSTAIL_LOGIN_APPLY;
        GloableParams.ADDRESS_USELOCATION_APPLY = uriHead+GloableParams.ADDRESSTAIL_USELOCATION_APPLY;
        GloableParams.ADDRESS_INOUTNO_APPLY = uriHead+GloableParams.ADDRESSTAIL_INOUTNO_APPLY;
        GloableParams.ADDRESS_WHCODE_APPLY = uriHead+GloableParams.ADDRESSTAIL_WHCODE_APPLY;
        GloableParams.ADDRESS_PRODINDATA_APPLY = uriHead+GloableParams.ADDRESSTAIL_PRODINDATA_APPLY;
        GloableParams.ADDRESS_CLEARGET_APPLY = uriHead+GloableParams.ADDRESSTAIL_CLEARGET_APPLY;
        GloableParams.ADDRESS_PREPARELIST_APPLY = uriHead +GloableParams.ADDRESSTAIL_PREPARELIST_APPLY;
        GloableParams.ADDRESS_UNCOLLECT_APPLY = uriHead+GloableParams.ADDRESSTAIL_UNCOLLECT_APPLEY;
        GloableParams.ADDRESS_COLLECT_APPLY = uriHead+GloableParams.ADDRESSTAIL_COLLECT_APPLY;
        GloableParams.ADDRESS_WITHDRAW_APPLY = uriHead+GloableParams.ADDRESSTAIL_WITHDRAW_APPLY;
    }
}
