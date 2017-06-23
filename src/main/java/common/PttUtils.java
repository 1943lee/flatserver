package common;

import com.kedacom.basic.common.bean.ModifyInfo;
import com.kedacom.basic.common.bean.PageableInfo;
import com.kedacom.basic.common.bean.PageableResult;
import com.kedacom.basic.common.bean.SingleReq;
import com.kedacom.basic.common.vo.DeviceVo;
import com.kedacom.basic.common.vo.OrgVo;
import com.kedacom.basic.common.vo.UserVo;
import com.kedacom.uc.sdk.api.IDataListener;
import com.kedacom.uc.sdk.api.IListener;
import com.kedacom.uc.sdk.api.pojo.StatusType;
import com.kedacom.uc.sdk.api.impl.OpenAPIImpl;
import com.kedacom.uc.sdk.api.pojo.UserInfo;
import com.kedacom.uc.sdk.api.pojo.UserPositionInfo;
import commoncache.ComCache;
import com.kedacom.ptt.rest.pojo.LoginReq;
import controller.Application;
import dao.DeviceDao;
import dao.UnitDao;
import dao.UserDao;
import model.Unit;
import model.User;
import net.sf.ezmorph.MorphException;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPubSub;
import sync.UpdateDeviceThreadTask;

import java.util.*;

/**
 * Created by hejiangbo on 2016/11/3.
 * PTT工具类
 */
public class PttUtils {

    // ptt设备类型、设备型号前缀，属于那边的字典项，一般不会改
    private static final String dict_device_model_code_prefix = "devicemodel1";
    private static final String dict_device_type_code_prefix = "devicetypel1";

    private  OpenAPIImpl m_OpenAPI = null;                                                               //视信通接口类
    private  UpdateDeviceThreadTask deviceUpdateTask = UpdateDeviceThreadTask.newInstance();             //设备更新线程池
    private  static final Logger m_Logger = LoggerFactory.getLogger(PttUtils.class);                     //日志信息
    private  String isNeedUpdate = Application.getAppConfig().getProperty("enableUpdatePttDevice");
    public PttUtils() {
        m_OpenAPI = OpenAPI_Single.getSingle().getM_OpenAPI();
    }

    public void initialize() {
        //连接服务
        initOpenAPIImpl();
        //登陆
        authLogin();
        //同步用户状态信息
        synchronizedUserStatus();
        // 设置监听
        setListener();
        //接受redis通知
        RedisChannel();
    }

    public void synchronizedTimer() {
        //同步单位信息
        synchronizedOrg();
        //同步人员信息
        synchronizedUser();
        //同步设备信息
        synchronizedDevice();
    }

    /**
     * 初始化PTT服务接口
     */
    private void initOpenAPIImpl() {
        if (null == m_OpenAPI) {
            String pttServer = ComCache.getInstance().getConfigCache().getPzxx("pttserver");
            String pttServerIp = "";
            String pttServerPort = "";
            String[] temp = pttServer.split(":");
            if (temp.length == 2) {
                pttServerIp = temp[0];
                pttServerPort = temp[1];
            }
            if (!pttServerIp.isEmpty() && !pttServerPort.isEmpty())
            {
                OpenAPI_Single.getSingle().setM_OpenAPI(new OpenAPIImpl(pttServerIp, Integer.parseInt(pttServerPort)));
                m_OpenAPI = OpenAPI_Single.getSingle().getM_OpenAPI();
            }
        }
    }

    /**
     * 登陆位置服务平台
     */
    public void authLogin() {
        if (null == m_OpenAPI)
            return;
        String userCode = "ezviewbianpinghua";
        LoginReq loginReq = new LoginReq();
        loginReq.setUserCode(userCode);
        RandomGUID mdGuid = new RandomGUID(true);
        loginReq.setUserPassword(mdGuid.valueAfterMD5);
        loginReq.setDeviceImei(mdGuid.valueBeforeMD5);
        m_OpenAPI.login(loginReq);
    }

    /**
     * 转换部分字段数据
     */
    public String csmcConvert(String v, int type) {
        if (v == null || v.equals("")) {
            return "";
        }
        List<ValueConvert> valueList = new ArrayList<ValueConvert>();
        valueList.add(new ValueConvert("1", "科达"));
        valueList.add(new ValueConvert("2", "海棠"));
        valueList.add(new ValueConvert("3", "德士喜"));
        String result = ValueConvert.convertStr(type, valueList, v);
        if (result == null) {
            m_Logger.error("csmc要转换的值" + v + "或者转换类型" + type + "出错!!!");
            return "";
        } else {
            return result;
        }
    }

    public String jylbConvert(String v, int type) {
/*        if (v == null || v.equals("")) {
            return "";
        }*/
        List<ValueConvert> valueList = new ArrayList<ValueConvert>();
        valueList.add(new ValueConvert("putongyuangong", "9"));
        valueList.add(new ValueConvert("zhihuizhongxinrenyuan", "10"));
        valueList.add(new ValueConvert("putongyuangong", "1"));//警察
        valueList.add(new ValueConvert("putongyuangong", "2"));
        valueList.add(new ValueConvert("putongyuangong", "3"));
        valueList.add(new ValueConvert("putongyuangong", "4"));
        valueList.add(new ValueConvert("putongyuangong", "5"));
        valueList.add(new ValueConvert("putongyuangong", "6"));
        valueList.add(new ValueConvert("putongyuangong", "7"));
        valueList.add(new ValueConvert("putongyuangong", "8"));
        valueList.add(new ValueConvert("putongyuangong", "77"));
        valueList.add(new ValueConvert("admin", ""));
        String result = ValueConvert.convertStr(type, valueList, v);
        if (result == null) {
            m_Logger.error("jylb要转换的值" + v + "或者转换类型" + type + "出错!!!");
            return "";
        } else {
            return result;
        }
    }

    public String handleTypeConvert(String v, int type) {
        if (v == null || v.equals("")) {
            return "";
        }
        List<ValueConvert> valueList = new ArrayList<ValueConvert>();
        valueList.add(new ValueConvert("1", "0"));
        valueList.add(new ValueConvert("2", "1"));
        valueList.add(new ValueConvert("3", "2"));
        String result = ValueConvert.convertStr(type, valueList, v);
        if (result == null) {
            m_Logger.error("handleType要转换的值" + v + "或者转换类型" + type + "出错!!!");
            return "";
        } else {
            return result;
        }
    }
    /**
     * 设置监听器
     */
    private void setListener() {
        if(null == m_OpenAPI)
            return;
        m_OpenAPI.setDataListener(new IDataListener() {
            /**
             * 接收人员单位设备更新
             */
            public void dataNotice(ModifyInfo dataModifyInfo) {
                List infoList = dataModifyInfo.getModifyInfoList();
                int handleType = dataModifyInfo.getHandleType();
                /**
                 * 接收人员信息更新
                 */
                int objectType = dataModifyInfo.getObjectType();
                if (objectType == 0) {//数据类型为用户
                    for (int i = 0; i < infoList.size(); i++) {
                        MorphDynaBean userVo = (MorphDynaBean) infoList.get(i);
                        m_Logger.info("需要更新人员信息：DLM=" + userVo.get("userCode") + ";handleType:" + dataModifyInfo.getHandleType());
                        User user = new User();
                        UserDao dao = Application.myContext.getBean("userDao", UserDao.class);
                        int result;
                        String jylb = "";
                        user.setDlm(ComConvert.toString(userVo.get("userCode")));//用户登录名
                        user.setJyxm(ComConvert.toString(userVo.get("userName")));//警员姓名
                        user.setJh(ComConvert.toString(userVo.get("userCode")));//警号
                        user.setYhzt(ComConvert.toString(userVo.get("dictStatus")));//用户状态
                        if(!ComConvert.toString(userVo.get("userPassword")).isEmpty()) {
                            user.setDlmm(ComConvert.toString(userVo.get("userPassword")));//用户密码
                        }
                        user.setLsdw(ComConvert.toString(userVo.get("orgCode")));//单位编号
                        if (ComConvert.toString(userVo.get("roleCode")).equals("zhihuizhongxinrenyuan")) {
                            jylb = "10";
                        } else if (ComConvert.toString(userVo.get("roleCode")).equals("putongyuangong")) {
                            jylb = "1";
                        } else {
                            jylb = jylbConvert(ComConvert.toString(userVo.get("roleCode")), 1);
                        }
                        user.setJylb(jylb);//警员类别
                        switch (handleType) {
                            case 0:
                                result = dao.insertUser(user);
                                m_Logger.info("新增用户数量：" + result);
                                break;//create
                            case 1:
                                result = dao.updateUser(user);
                                m_Logger.info("修改用户数量：" + result);
                                break;//update
                            case 2:
                                result = dao.deleteUser(user);
                                m_Logger.info("删除用户数量：" + result);
                                break;//delete
                            default:
                                break;
                        }
                    }
                }

                /**
                 * 接收设备信息更新
                 */
                else if (objectType == 1) {//数据类型为设备
                    //若忽略更新
                    if(handleType == 1 && "false".equalsIgnoreCase(isNeedUpdate))
                        return;
                    Map<String, Map> addMap = new HashMap<String, Map>();
                    Map<String, Map> updateMap = new HashMap<String, Map>();
                    Map<String, Map> deleteMap = new HashMap<String, Map>();
                    for (int i = 0; i < infoList.size(); i++) {
                        MorphDynaBean deviceVo = (MorphDynaBean) infoList.get(i);
                        m_Logger.info("需要更新设备信息：设备编号=" + deviceVo.get("deviceCode") + ";handleType:" + dataModifyInfo.getHandleType());
                        Map<String, String> device = new HashMap<>();
                        DeviceDao dao = Application.myContext.getBean("deviceDao", DeviceDao.class);
                        int result;
                        device.put("sbbh", ComConvert.toString(deviceVo.get("deviceCode")));//设备编号
                        if (handleType != 2) {
                            String SBXH = "";
                            String sbxhStr = ComConvert.toString(deviceVo.get("dictDeviceModelCode"));//设备型号编号
                            if (sbxhStr.length() == 1) {
                                SBXH = "devicemodel10" + sbxhStr;
                            } else if (sbxhStr.length() == 2) {
                                SBXH = "devicemodel1" + sbxhStr;
                            }
                            device.put("sbxh", SBXH);
                            if (isNotNull(handleType, deviceVo.get("deviceOwnerCode"))) {
                                device.put("sbgsz", ComConvert.toString(deviceVo.get("deviceOwnerCode")));//设备拥有者编号
                            }
                            if (isNotNull(handleType,deviceVo.get("orgCode"))) {
                                device.put("ssxq", ComConvert.toString(deviceVo.get("orgCode")));//单位编号
                            }
                            if (isNotNull(handleType,deviceVo.get("deviceName"))) {
                                device.put("sbmc", ComConvert.toString(deviceVo.get("deviceName")));//设备名称
                            }
                            String sblxStr = ComConvert.toString(deviceVo.get("dictDeviceTypeCode"));//设备类型
                            if (sblxStr.isEmpty()) {
                                device.put("sblx", "");//设备类型编号
                            } else {
                                String sblx = sblxStr.substring(sblxStr.length() - 1, sblxStr.length());
                                device.put("sblx", sblx);//设备类型编号
                            }

                            device.put("csmc", ComConvert.toString(deviceVo.get("dictDeviceFactory")));//设备厂商名称
                            if (isNotNull(handleType,deviceVo.get("gbbh"))) {
                                String gbbh = null;
                                try {
                                    gbbh = ComConvert.toString(deviceVo.get("gbbh"));
                                } catch (MorphException e) {
                                    gbbh = null;
                                }
                                if (gbbh != null) {
                                    List<String> gbbhList = (List<String>) deviceVo.get("gbbh");
                                    String gbbhStr = "";
                                    for (int j = 0; j < gbbhList.size(); j++) {
                                        if (j != gbbhList.size() - 1) {
                                            gbbhStr += gbbhList.get(j) + ",";
                                        } else {
                                            gbbhStr += gbbhList.get(j);
                                        }
                                    }
                                    device.put("spsbbh", gbbhStr);//国标编号
                                } else {
                                    device.put("spsbbh", "");
                                }
                            }
                        }
                        switch (handleType) {
                            case 0:
                                result = dao.insertDevice(device);
                                m_Logger.info("新增设备数量：" + result);
                                addMap.put(device.get("sbbh"), device);
                                break;//create
                            case 1:
                                result = dao.updateDevice(device);
                                m_Logger.info("修改设备数量：" + result);
                                updateMap.put(device.get("sbbh"), device);
                                break;//update
                            case 2:
                                result = dao.deleteDevices(device);
                                m_Logger.info("删除设备数量：" + result);
                                deleteMap.put(device.get("sbbh"), device);
                                break;//delete
                            default:
                                break;

                        }
                    }
                    ComCache.getInstance().UpdateDeviceCache(addMap, ComDefine.OPERA_ADD, ComDefine.CACHE_YDSBXX);
                    ComCache.getInstance().UpdateDeviceCache(updateMap, ComDefine.OPERA_MOD, ComDefine.CACHE_YDSBXX);
                    ComCache.getInstance().UpdateDeviceCache(deleteMap, ComDefine.OPERA_DEL, ComDefine.CACHE_YDSBXX);
                }

                /**
                 * 接收单位信息更新
                 */
                else if (objectType == 2) { //数据类型为单位
                    UnitDao dao = Application.myContext.getBean("unitDao", UnitDao.class);
                    List<Map<String, String>> localOrg = dao.getUnit();

                    for (int i = 0; i < infoList.size(); i++) {
                        MorphDynaBean orgVo = (MorphDynaBean) infoList.get(i);
                        m_Logger.info("需要更新单位信息：单位编号=" + orgVo.get("orgCode") + ";handleType:" + dataModifyInfo.getHandleType());
                        Unit unit = new Unit();
                        int result;
                        unit.setDwbh(ComConvert.toString(orgVo.get("orgCode")));//单位编号
                        unit.setDwmc(ComConvert.toString(orgVo.get("orgName")));//单位名称
                        if (handleType == 0 || handleType == 1) {
                            unit.setSsxq(getSSXQ(ComConvert.toString(orgVo.get("orgParentCode")), localOrg));
                        }
                        String[] NBBMS = null;
                        NBBMS = (ComConvert.toString(orgVo.get("orgInnerCode")).isEmpty() ? "" : ComConvert.toString(orgVo.get("orgInnerCode"))).split("\\,");
                        List<String> NbbmList = new ArrayList<String>();
                        NbbmList.add("1");
                        for (int j = NBBMS.length - 2; j >= 0; j--) {
                            NbbmList.add(NBBMS[j]);
                        }
                        unit.setDwnbbm(StringUtils.collectionToDelimitedString(NbbmList, "."));//单位内部编码
                        unit.setLsgx(ComConvert.toString(orgVo.get("orgParentCode")));//隶属关系

                        switch (handleType) {
                            case 0:
                                result = dao.insertUnit(unit);
                                m_Logger.info("新增单位数量：" + result);
                                break;//create
                            case 1:
                                result = dao.updateUnit(unit);
                                m_Logger.info("修改单位数量：" + result);
                                break;//update
                            case 2:
                                result = dao.deleteUnit(unit);
                                m_Logger.info("删除单位数量：" + result);
                                break;//delete

                        }
                    }
                }
            }

            public Map<String, String> getParent(String parentCode, List<Map<String, String>> datas) {
                for (Map<String, String> data : datas) {
                    if (data.get("DWBH").equals(parentCode)) {
                        return data;
                    }
                }
                return null;
            }

            public List<Map<String, String>> getChildren(String code, List<Map<String, String>> datas) {
                List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                for (Map<String, String> data : datas) {
                    if (data.containsKey("LSGX") && data.get("LSGX").equals(code)) {
                        result.add(data);
                    }
                }
                return result;
            }

            public String getCurrentLevelSSXQ(Map<String, String> parentNode, List<Map<String, String>> datas) {
                /*if (parentNode.get("LSGX").equals("-1")) {
                    return parentNode.get("DWBH");
                }*/
                List<Map<String, String>> currentLevels = getChildren(parentNode.get("DWBH"), datas);
                for (Map<String, String> data : currentLevels) {
                    if (data.containsKey("SSXQ") && !data.get("SSXQ").equals("")) {
                        return data.get("SSXQ");
                    }
                }
                return "";
            }

            public String getParentLevelSSXQ(Map<String, String> parentNode, List<Map<String, String>> datas) {
                if (parentNode.get("LSGX").equals("-1")) {
                    return parentNode.get("DWBH");
                }
                Map<String, String> parent = getParent(parentNode.get("DWBH"), datas);
                if (parent.containsKey("SSXQ") && !parent.get("SSXQ").equals("")) {
                    return parent.get("SSXQ");
                }
                return getParentLevelSSXQ(parent, datas);
            }

            public String getSSXQ(String parentCode, List<Map<String, String>> datas) {
                Map<String, String> parentNode = getParent(parentCode, datas);
                String result = getCurrentLevelSSXQ(parentNode, datas);
                return result.equals("") ? getParentLevelSSXQ(parentNode, datas) : result;
            }


            /**
             * 接收用户状态更新
             */
            public void statusReceived(UserInfo data) {
                // TODO Auto-generated method stub
            }

            /**
             * 接收设备位置更新（当前用户）
             */
            public void positionReceived(UserPositionInfo userPositionInfo) {
                // TODO Auto-generated method stub
            }
        });

        m_OpenAPI.setListener(new IListener() {
            public void statusReceived(UserInfo data) {
                // 读取UserCode
                String[] userCodes = data.getUserCode().split(":");
                if (userCodes.length != 2) {
                    m_Logger.info("用户状态:收到UserCode:" + data.getUserCode() + ";无效用户信息");
                    return;
                }
                data.setUserCode(userCodes[1]);

                // DeviceID没有数据，直接返回
                String deviceID = data.getDeviceCode();
                if (null == deviceID || deviceID.equals("")) {
                    m_Logger.info("用户状态:收到UserCode:" + userCodes[1] + "设备编号:" + deviceID + ";无效设备编号");
                    return;
                }
                Map device = new HashMap<String, String>();
                String deviceNum = data.getDeviceCode();
                String deviceUser = data.getUserCode();
                // 设置sbsyz
                StatusType status = data.getStatus();
                device.put("sbbh", deviceNum);
                device.put("sbsyz", deviceUser);
                // 下线通知
                if (status == StatusType.OFF_LINE) {
                    device.put("sbyhzt", "0");
                } else {
                    device.put("sbyhzt", "1");
                }
                deviceUpdateTask.addTask(device, ComDefine.CACHE_YDYHZT);
            }

            public void positionReceived(UserPositionInfo userPositionInfo) {
                Date time = TimeHelper.timeStamp2Date(userPositionInfo.getSysDateTime());
                Map device = new HashMap<String, String>();
                String deviceNum = userPositionInfo.getDeviceId();
                String longtitude = ComConvert.toString(userPositionInfo.getLongitude());
                String lattitude = ComConvert.toString(userPositionInfo.getLatitude());
                String updateTime = TimeHelper.formatTime(time, "yyyy-MM-dd HH:mm:ss");
                device.put("sbbh", deviceNum);
                device.put("sbjd", longtitude);
                device.put("sbwd", lattitude);
                device.put("gxsj", updateTime);
                deviceUpdateTask.addTask(device, ComDefine.CACHE_YDSBWZ);
            }
        });
    }


    /**
     * 获取ptt单位数据同步数据库
     */
    public void synchronizedOrg() {
        if (null == m_OpenAPI)
            return;
        UnitDao dao = Application.myContext.getBean("unitDao", UnitDao.class);
        PageableInfo<OrgVo> orgInfoReq = new PageableInfo<OrgVo>();
        orgInfoReq.setQueryBean(new OrgVo());
        orgInfoReq.setLimit(Integer.MAX_VALUE);
        PageableResult result = m_OpenAPI.queryOrgsInfo(orgInfoReq);
        List<OrgVo> orgs = result.getData();

        List<OrgVo> update_orgs = new ArrayList<OrgVo>();
        List<OrgVo> insert_orgs = new ArrayList<OrgVo>();
        List<Map<String, String>> result_select = dao.getUnit();

        Set<String> s = new HashSet<String>();

        for (OrgVo v : orgs) {
            s.add(v.getOrgCode());
        }


        for (Map<String, String> m : result_select) {
            OrgVo orgVo = new OrgVo();
            orgVo.setOrgCode(m.get("DWBH"));    //单位编号
            orgVo.setOrgName(m.get("DWMC"));    //单位名称
            String[] NBBMS = null;
            NBBMS = (m.get("DWNBBM") == null ? "" : m.get("DWNBBM")).split("\\.");
            List<String> NbbmList = new ArrayList<String>();
            for (int i = NBBMS.length - 1; i > 0; i--) {
                NbbmList.add(NBBMS[i]);
            }
            NbbmList.add("000000000000");
            orgVo.setOrgInnerCode(StringUtils.collectionToDelimitedString(NbbmList, ","));//单位内部编码
            if (m.get("LSGX").equals("-1")) {
                orgVo.setOrgParentCode("000000000000");
            } else {
                orgVo.setOrgParentCode(m.get("LSGX"));    //隶属关系
            }
            orgVo.setOrgSpell(m.get("DWPY"));       //单位拼音
            if (!s.contains(ComConvert.toString(m.get("DWBH")))) {
                insert_orgs.add(orgVo);
            } else {
                update_orgs.add(orgVo);
            }
        }
        //调接口 传list 和 handletype
        SingleReq<ModifyInfo<OrgVo>> orgModifyInfoReq = new SingleReq<ModifyInfo<OrgVo>>();
        if (insert_orgs.size() != 0) {
            ModifyInfo<OrgVo> modifyinfo_insert = new ModifyInfo<OrgVo>();
            modifyinfo_insert.setHandleType(0);//设置操作类型,0insert
            modifyinfo_insert.setModifyInfoList(insert_orgs);
            orgModifyInfoReq.setT(modifyinfo_insert);
            //调用sdk接口
            m_Logger.info(ComConvert.toString(m_OpenAPI.modifyOrgsInfo(orgModifyInfoReq)));
            m_Logger.info("此次同步新增数据:" + insert_orgs.size() + "条");
        } else {
            m_Logger.info("此次同步新增数据:" + insert_orgs.size() + "条");
        }
        if (update_orgs.size() != 0) {
            ModifyInfo<OrgVo> modifyinfo_update = new ModifyInfo<OrgVo>();
            modifyinfo_update.setHandleType(1);//设置操作类型,1update
            modifyinfo_update.setModifyInfoList(update_orgs);
            orgModifyInfoReq.setT(modifyinfo_update);
            //调用sdk接口
            m_Logger.info(ComConvert.toString(m_OpenAPI.modifyOrgsInfo(orgModifyInfoReq)));
            m_Logger.info("此次同步修改数据:" + update_orgs.size() + "条");
        } else {
            m_Logger.info("此次同步修改数据:" + update_orgs.size() + "条");
        }

    }



    /**
     * 获取ptt人员数据同步数据库
     */

    public void synchronizedUser() {
        if (null == m_OpenAPI)
            return;
        UserDao dao = Application.myContext.getBean("userDao", UserDao.class);
        PageableInfo<UserVo> userInfoReq = new PageableInfo<UserVo>();
        userInfoReq.setQueryBean(new UserVo());
        userInfoReq.setLimit(Integer.MAX_VALUE);
        PageableResult result = m_OpenAPI.queryUsersInfo(userInfoReq);
        List<UserVo> users = result.getData();

        List<UserVo> update_users = new ArrayList<UserVo>();
        List<UserVo> insert_users = new ArrayList<UserVo>();
        List<Map<String, String>> result_select = dao.getUser();//bendi

        Set<String> s = new HashSet<String>();

        for (UserVo u : users) {
            s.add(u.getUserCode());
        }


        for (Map<String, String> m : result_select) {
            UserVo userVo = new UserVo();
            if(m.get("DLM") == null || m.get("DLM").trim().isEmpty()){
                continue;
            }
            String jylb = "";
            userVo.setUserCode(m.get("DLM"));//用户登录名
            userVo.setUserName(m.get("JYXM"));//警员姓名
            if (m.get("DLMM") != null) {
                userVo.setUserPassword(m.get("DLMM"));//用户密码
            } else {
                String md5 = Security.MD5(m.get("DLM"));
                userVo.setUserPassword(md5);
            }
            userVo.setOrgCode(m.get("LSDW"));//单位编号
            userVo.setOrgName(m.get("DWMC"));//单位名称
            userVo.setDictStatus(m.get("YHZT"));//用户状态
            if(!m.containsKey("JYLB")|| ComConvert.toString(m.get("JYLB")).equals("")  ){
                jylb = "admin";
            }else {
                String[] jylbStrArray = ComConvert.toString(m.get("JYLB")).split(",");
                if (jylbStrArray.length == 1) {
                    if (m.get("DLM").equals("admin")) {
                        jylb = "admin";
                    } else {
                        jylb = jylbConvert(ComConvert.toString(m.get("JYLB")), 2);
                    }
                } else {
                    jylb = "putongyuangong";
                    for (int i = 0; i < jylbStrArray.length; i++) {
                        if (jylbStrArray[i].equals("10")) {
                            jylb = "zhihuizhongxinrenyuan";
                            break;
                        }
                    }
                }
            }
            userVo.setRoleCode(jylb);//警员类别
            if (!s.contains(ComConvert.toString(m.get("DLM")))) {//如果外部数据没有本地数据的DLM,insert
                insert_users.add(userVo);
            } else if (s.contains(ComConvert.toString(m.get("DLM")))) {//如果外部数据有本地数据的DLM,update
                update_users.add(userVo);
            }
        }
        //调接口 传list 和 handletype
        SingleReq<ModifyInfo<UserVo>> userModifyInfoReq = new SingleReq<ModifyInfo<UserVo>>();

        if (insert_users.size() != 0) {
            ModifyInfo<UserVo> modifyinfo_insert = new ModifyInfo<UserVo>();
            modifyinfo_insert.setHandleType(0);//设置操作类型,0insert
            modifyinfo_insert.setModifyInfoList(insert_users);
            userModifyInfoReq.setT(modifyinfo_insert);
            //调用sdk接口
            m_Logger.info(ComConvert.toString(m_OpenAPI.modifyUsersInfo(userModifyInfoReq)));
            m_Logger.info("此次同步新增数据:" + insert_users.size() + "条");
        } else {
            m_Logger.info("此次同步新增数据:" + insert_users.size() + "条");
        }

        if (update_users.size() != 0) {
            ModifyInfo<UserVo> modifyinfo_update = new ModifyInfo<UserVo>();
            modifyinfo_update.setHandleType(1);//设置操作类型,1update
            modifyinfo_update.setModifyInfoList(update_users);
            userModifyInfoReq.setT(modifyinfo_update);
            //调用sdk接口
            m_Logger.info(ComConvert.toString(m_OpenAPI.modifyUsersInfo(userModifyInfoReq)));
            m_Logger.info("此次同步修改数据:" + update_users.size() + "条");
        } else {
            m_Logger.info("此次同步修改数据:" + update_users.size() + "条");
        }

    }


    /**
     * 获取ptt设备数据同步数据库
     */

    public void synchronizedDevice() {
        if (null == m_OpenAPI)
            return;
        DeviceDao dao = Application.myContext.getBean("deviceDao", DeviceDao.class);
        PageableInfo<DeviceVo> deviceInfoReq = new PageableInfo<DeviceVo>();
        deviceInfoReq.setQueryBean(new DeviceVo());
        deviceInfoReq.setLimit(Integer.MAX_VALUE);
        PageableResult result = m_OpenAPI.queryDevicesInfo(deviceInfoReq);
        List<DeviceVo> devices = result.getData();
        List<Map<String, String>> result_select = dao.getDevice();//bendi

        Map<String, Map> addMap = new HashMap<String, Map>();
        Map<String, Map> updateMap = new HashMap<String, Map>();

        Set<String> s = new HashSet<String>();

        for (Map m : result_select) {
            s.add(ComConvert.toString(m.get("SBBH")));
        }
        int insertNum = 0;
        int updateNum = 0;
        for (DeviceVo d : devices) {
            Map<String, String> device = new HashMap<>();
            device.put("sbbh", d.getDeviceCode());//设备编号

            device.put("sbxh", d.getDictDeviceModelCode());//设备型号
            device.put("sbgsz", d.getDeviceOwnerCode());//设备拥有者编号
            device.put("ssxq", d.getOrgCode());//单位编号
            device.put("sbmc", d.getDeviceName());//设备名称
            String[] sblxStr = d.getDictDeviceTypeCode().split(dict_device_type_code_prefix);//设备类型，需要去掉前缀
            if (2 != sblxStr.length || sblxStr[1].isEmpty()) {
                device.put("sblx", "");//设备类型编号
            } else {
                device.put("sblx", sblxStr[1]);//设备类型编号
            }
            device.put("csmc", d.getDictDeviceFactory());//设备厂商名称
            device.put("spsbbh", org.apache.commons.lang.StringUtils.join(d.getGbbh(),','));//国标编号
            if (!s.contains(d.getDeviceCode())) {
                dao.insertDevice(device);
                insertNum++;
                addMap.put(device.get("sbbh"), device);

            } else {
                if("true".equalsIgnoreCase(isNeedUpdate)) {
                    dao.updateDevice(device);
                    updateNum++;
                    updateMap.put(device.get("sbbh"), device);
                }

            }
        }
        m_Logger.info("此次同步新增数据:" + insertNum + "条");
        m_Logger.info("此次同步修改数据:" + updateNum + "条");
        ComCache.getInstance().UpdateDeviceCache(addMap, ComDefine.OPERA_ADD, ComDefine.CACHE_YDSBXX);
        ComCache.getInstance().UpdateDeviceCache(updateMap, ComDefine.OPERA_MOD, ComDefine.CACHE_YDSBXX);

    }


    /**
     * 同步ptt用户状态到数据库
     */
    private void synchronizedUserStatus() {
        if (null == m_OpenAPI)
            return;
        //清空设备使用者
        DeviceDao dao = Application.myContext.getBean("deviceDao", DeviceDao.class);
        dao.clearUserStatus();

        //开始同步用户状态
        List<UserInfo> userInfos = m_OpenAPI.getUserInfos();
        for (int i = 0; i < userInfos.size() - 1; i++) {
            UserInfo userInfo = userInfos.get(i);
            StatusType statusType = userInfo.getStatus();
            String deviceCode = userInfo.getDeviceCode();
            //只更新在线的设备
            if (null != deviceCode && !deviceCode.isEmpty() && statusType == StatusType.ON_LINE) {
                Map device = new HashMap<String, String>();
                String[] userCodes = deviceCode.split(":");
                if (userCodes.length != 2) {
                    m_Logger.info("用户状态:收到UserCode:" + deviceCode + ";无效用户信息");
                    continue;
                }
                device.put("sbsyz", userCodes[1]);
                device.put("sbyhzt", "1");
                dao.updateUserStatus(device);
            }
        }
    }


    /**
     * ezview客户端监听消息
     */
    private void RedisChannel() {
        if(null == m_OpenAPI)
            return;
        RedisUtil redisUtil = RedisUtil.getInstance();
        redisUtil.subscribe(new MyListener(), new String[]{"CACHE.RYXX", "CACHE.DWXX"});
    }

    public class MyListener extends JedisPubSub {
        // 取得订阅的消息后的处理
        public void onMessage(String channel, String message) {
            System.out.println(channel + "=" + message);
            JSONObject jsonObject = JSONObject.fromObject(message);
            String handleType = jsonObject.getString("operaType");
            JSONObject operaInfo = jsonObject.getJSONObject("operaInfo");
            JSONObject content = operaInfo.getJSONObject("content");
            if (channel.equals("CACHE.RYXX")) {
                UserVo user = new UserVo();
                UnitDao dao = Application.myContext.getBean("unitDao", UnitDao.class);
                String jylb = "";
                user.setUserCode(content.getString("dlm"));//用户登录名
                user.setUserName(content.getString("jyxm"));//警员姓名
                user.setUserPassword(content.getString("dlmm"));//用户密码
                user.setOrgCode(content.getString("lsdw"));//单位编号
                user.setOrgCode(content.getString("lsdw"));//单位编号
                Map result_m = dao.getDwmcByDwbh(content.getString("lsdw"));
                String DwmcStr="";
                if(result_m !=null && result_m.containsKey("DWMC")) {
                    DwmcStr = dao.getDwmcByDwbh(content.getString("lsdw")).get("DWMC").toString();
                }
                user.setOrgName(DwmcStr);//单位名称
                user.setDictStatus(content.getString("yhzt"));//用户状态
                String[] jylbStrArray = ComConvert.toString(content.getString("jylb")).split(",");
                if (jylbStrArray.length == 1) {
                    jylb = jylbConvert(ComConvert.toString(content.getString("jylb")), 2);
                } else {
                    jylb = "putongyuangong";
                    for (int i = 0; i < jylbStrArray.length; i++) {
                        if (jylbStrArray[i].equals("10")) {
                            jylb = "zhihuizhongxinrenyuan";
                            break;
                        }
                    }
                }
                user.setRoleCode(jylb);//警员类别
                SingleReq<ModifyInfo<UserVo>> userModifyInfoReq = new SingleReq<ModifyInfo<UserVo>>();
                ModifyInfo<UserVo> modifyinfo = new ModifyInfo<UserVo>();
                List<UserVo> list = new ArrayList<UserVo>();
                list.add(user);
                m_Logger.info(user.toString());
                modifyinfo.setHandleType(Integer.parseInt(handleTypeConvert(handleType, 1)));//设置操作类型
                modifyinfo.setModifyInfoList(list);
                userModifyInfoReq.setT(modifyinfo);
                //调用sdk接口
                m_Logger.info(m_OpenAPI.modifyUsersInfo(userModifyInfoReq).toString());

            } else if (channel.equals("CACHE.SBXX")) {
                DeviceVo deviceVo = new DeviceVo();
                deviceVo.setDeviceCode(content.getString("sbbh"));//设备编号
                deviceVo.setDeviceName(content.getString("sbmc"));//设备名称
                String CSMC = csmcConvert(content.getString("csmc"), 2);//厂商名称code
                deviceVo.setDictDeviceFactoryCode(CSMC);
                deviceVo.setDictDeviceFactory(content.getString("csmc"));//厂商名称
                deviceVo.setDictDeviceTypeCode(content.getString("sblx"));//设备类型
                if (content.getString("sblx") == null) {
                    deviceVo.setDictDeviceModelCode("");
                } else {
                    //设备型号
                    int SBXH = Integer.parseInt(content.getString("sbxh").substring(content.getString("sbxh").length() - 2, content.getString("sbxh").length()));
                    deviceVo.setDictDeviceModelCode(String.valueOf(SBXH));
                }
                deviceVo.setDeviceOwnerCode(content.getString("sbgsz"));//设备归属者
                deviceVo.setOrgCode(content.getString("ssxq"));//所属辖区单位编号
                deviceVo.setGbbh(Arrays.asList(content.getString("spsbbh").split(",")));////国标spsbbh
                //调接口 传list 和 handletype
                SingleReq<ModifyInfo<DeviceVo>> deviceModifyInfoReq = new SingleReq<ModifyInfo<DeviceVo>>();
                ModifyInfo<DeviceVo> modifyinfo = new ModifyInfo<DeviceVo>();
                List<DeviceVo> list = new ArrayList<DeviceVo>();
                list.add(deviceVo);
                m_Logger.info(deviceVo.toString());
                modifyinfo.setHandleType(Integer.parseInt(handleTypeConvert(handleType, 1)));//设置操作类型
                modifyinfo.setModifyInfoList(list);
                deviceModifyInfoReq.setT(modifyinfo);
                //调用sdk接口
                m_Logger.info(m_OpenAPI.modifyDevicesInfo(deviceModifyInfoReq).toString());

            } else if (channel.equals("CACHE.DWXX")) {
                OrgVo orgVo = new OrgVo();
                orgVo.setOrgCode(content.getString("dwbh"));    //单位编号
                orgVo.setOrgName(content.getString("dwmc"));    //单位名称
                String[] NBBMS = null;
                NBBMS = (content.getString("dwnbbm") == null ? "" : content.getString("dwnbbm")).split("\\.");
                List<String> NbbmList = new ArrayList<String>();
                for (int i = NBBMS.length - 1; i > 0; i--) {
                    NbbmList.add(NBBMS[i]);
                }
                NbbmList.add("000000000000");
                orgVo.setOrgInnerCode(StringUtils.collectionToDelimitedString(NbbmList, ","));//单位内部编码
                if (content.getString("lsgx").equals("-1")) {
                    orgVo.setOrgParentCode("000000000000");
                } else {
                    orgVo.setOrgParentCode(content.getString("lsgx"));    //隶属关系
                }
                orgVo.setOrgSpell(content.getString("dwpy"));       //单位拼音

                //调接口 传list 和 handletype
                SingleReq<ModifyInfo<OrgVo>> orgModifyInfoReq = new SingleReq<ModifyInfo<OrgVo>>();
                ModifyInfo<OrgVo> modifyinfo = new ModifyInfo<OrgVo>();
                List<OrgVo> list = new ArrayList<OrgVo>();
                list.add(orgVo);
                m_Logger.info(ComConvert.toString(orgVo));
                modifyinfo.setHandleType(Integer.parseInt(handleTypeConvert(handleType, 1)));//设置操作类型
                modifyinfo.setModifyInfoList(list);
                orgModifyInfoReq.setT(modifyinfo);
                //调用sdk接口
                m_Logger.info(m_OpenAPI.modifyOrgsInfo(orgModifyInfoReq).toString());
            }
        }

    }

    public boolean isNotNull(int type,Object value){
        String str = ComConvert.toString(value);
        if((type ==1 && str.isEmpty()))
            return  false;
        else
            return  true;
    }
}
