package commoncache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kedacom.share.utils.PinYinUtil;
import common.*;
import controller.Application;
import common.DeviceDbOperate;
import dao.DeviceDao;
import listener.LocationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hejiangbo on 2016/11/3.
 */
public class ComCache {
    private static  ComCache  m_ComCahce = new ComCache();                            //通用缓存
    private  ConfigCache m_ConfigCache = new ConfigCache();                           //配置缓存
    private  DeviceCache m_DeviceCache = new DeviceCache();                           //设备缓存
    DeviceDao dao = Application.myContext.getBean("deviceDao", DeviceDao.class);
    private  static final Logger m_Logger = LoggerFactory.getLogger(ComCache.class);  //日志信息

    public  ComCache()
    {
    }

    /**
     * 缓存单例对象
     * @return
     */
    public  static ComCache getInstance(){
        return  m_ComCahce;
    }


    /**
     * 获取配置项缓存数据
     * @return
     */
    public  ConfigCache getConfigCache(){
        return  m_ConfigCache;
    }

    /**
     * 获取设备缓存
     * @return
     */
    public Map getDeviceCache(){
        return m_DeviceCache.getDeviceCache();
    }

    /**
     * 初始化缓存数据
     */
    public void initCache(String type){
        Connection connection = null;
        try
        {
            DataSource dataSource = (DataSource) Application.myContext.getBean("ezViewDataSource");
            connection = dataSource.getConnection();
            if (null != connection)
            {
                switch (type){
                    case ComDefine.INIT_CONFIG:
                        m_ConfigCache.initCache(connection);
                        break;
                    case ComDefine.INIT_DEVICE:
                        m_DeviceCache.initDevice(connection);
                        break;

                }

            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (null != connection)
                {
                    connection.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * 更新设备缓存信息
     * @param updateMap 需要更新的缓存设备
     * @param operate  操作类型
     */
    public void UpdateDeviceCache(Map<String,Map> updateMap,int operate,String channelName){
        synchronized(this) {
            //更新缓存里的数据
            if (updateMap.size() > 0) {
                for (Map.Entry<String, Map> entry : updateMap.entrySet()) {
                    Map<String, Map> mapCache = ComCache.getInstance().getDeviceCache();
                    Map<String, String> updateValue = entry.getValue();
                    Map deviceCache = null;
                    if (mapCache.containsKey(entry.getKey())) {
                        deviceCache = mapCache.get(entry.getKey());
                    }
                    String sbgsz = ComConvert.toString(updateValue.get("sbgsz"));
                    switch (operate) {
                        case ComDefine.OPERA_ADD:
                            //有序放置key(按照预定的顺序在redis里发布)
                            Map insertMap = new LinkedHashMap<String, String>();
                            insertMap.put("sbbh", ComConvert.toString(updateValue.get("sbbh")));
                            insertMap.put("sbmc", ComConvert.toString(updateValue.get("sbmc")));
                            insertMap.put("sbdl", "7");
                            insertMap.put("sblx", ComConvert.toString(updateValue.get("sblx")));
                            insertMap.put("spsbbh", ComConvert.toString(updateValue.get("spsbbh")));
                            insertMap.put("ssxq", ComConvert.toString(updateValue.get("ssxq")));
                            insertMap.put("sbxh", ComConvert.toString(updateValue.get("sbxh")));
                            insertMap.put("sbyhzt", "0");
                            insertMap.put("sbsyz", ComConvert.toString(updateValue.get("sbsyz")));
                            insertMap.put("sbsyzxm", ComConvert.toString(updateValue.get("sbsyzxm")));
                            insertMap.put("sbgsz", ComConvert.toString(updateValue.get("sbgsz")));
                            insertMap.put("sbgszxm", ComConvert.toString(updateValue.get("sbgszxm")));
                            insertMap.put("gxsj", ComConvert.toString(updateValue.get("gxsj")));
                            insertMap.put("sbjd", ComConvert.toString(updateValue.get("sbjd")));
                            insertMap.put("sbwd", ComConvert.toString(updateValue.get("sbwd")));
                            insertMap.put("csmc", ComConvert.toString(updateValue.get("csmc")));
                            insertMap.put("ipdz", ComConvert.toString(updateValue.get("ipdz")));
                            insertMap.put("ydhm", ComConvert.toString(updateValue.get("ydhm")));
                            insertMap.put("rjbb", ComConvert.toString(updateValue.get("rjbb")));
                            insertMap.put("ktsj", ComConvert.toString(updateValue.get("ktsj")));
                            //查询归属者的名称
                            if (updateValue.containsKey("sbgsz")) {
                                String sbgszxm = "";
                                if (!sbgsz.isEmpty()) {
                                    sbgszxm = DeviceDbOperate.getUserName(sbgsz);
                                }
                                insertMap.put("sbsyz", sbgsz);
                                insertMap.put("sbgszxm", sbgszxm);
                                insertMap.put("sbsyzxm", sbgszxm);
                            }
                            DeviceDbOperate.updatePinYin(insertMap);
                            publishUpdateCache(operate, 7, insertMap, channelName);
                            break;
                        case ComDefine.OPERA_MOD:
                            if (null != deviceCache) {
                                //查询归属者的名称
                                if (updateValue.containsKey("sbgsz")) {
                                    String sbgszxm = "";
                                    if (!sbgsz.isEmpty()) {
                                        sbgszxm = DeviceDbOperate.getUserName(sbgsz);
                                    }
                                    updateValue.put("sbgszxm", sbgszxm);
                                }
                                String sbyhzt = ComConvert.toString(deviceCache.get("sbyhzt"));
                                //若离线则更新使用者为归属者
                                if (sbyhzt.equals("0")) {
                                    updateValue.put("sbsyz", ComConvert.toString(deviceCache.get("sbgsz")));
                                    updateValue.put("sbsyzxm", ComConvert.toString(deviceCache.get("sbgszxm")));
                                } else {
                                    String sbsyz = ComConvert.toString(deviceCache.get("sbsyz"));
                                    String sbsyzxm = "";
                                    if (!sbsyz.isEmpty()) {
                                        sbsyzxm = DeviceDbOperate.getUserName(sbsyz);
                                    }
                                    updateValue.put("sbsyzxm", sbsyzxm);
                                }
                                DeviceDbOperate.updatePinYin(deviceCache);
                                publishUpdateCache(operate, 7, updateValue, channelName);
                            }
                            break;
                        case ComDefine.OPERA_DEL:
                            if (null != deviceCache) {
                                publishUpdateCache(operate, 7, deviceCache, channelName);
                            }
                            break;
                    }
                }
                updateMap.clear();
            }
        }
    }

    /**
     * 发布更新设备redis消息
     * @param operaType  操作类型
     * @param type 设备类型
     * @param cacheContent 缓存内容
     * @param channelName
     */
    private void publishUpdateCache(int operaType, int type
            , Map cacheContent, String channelName){
        try {
            //publish 更新设备消息
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode();
            ObjectNode operaInfo = mapper.createObjectNode();
            String json = mapper.writeValueAsString(cacheContent);
            operaInfo.set("content", mapper.readTree(json));
            operaInfo.put("type", type);
            root.put("operaType", operaType);
            root.set("operaInfo", operaInfo);
            RedisUtil redisUtil = RedisUtil.getInstance();
            redisUtil.publish(channelName, ComConvert.toString(root));
            m_Logger.debug(channelName + ComConvert.toString(root));
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 更新设备信息
     * @param device     设备信息
     * @param updateType 更新类型
     */
    public void updateDevice(Map device,String updateType){
        String deviceNum = ComConvert.toString(device.get("sbbh"));
        Map<String,Map> deviceCache = ComCache.getInstance().getDeviceCache();
        switch (updateType)
        {
            case ComDefine.CACHE_YDYHZT:
                if(deviceCache.containsKey(deviceNum)){
                Map updateDevice = deviceCache.get(deviceNum);
                //若是下线则将当前设备使用者置为设备归属者
                String sbyhzt = ComConvert.toString(device.get("sbyhzt"));
                if(sbyhzt.equals("0")){
                    device.put("sbsyz",updateDevice.get("sbgsz"));
                    device.put("sbsyzxm",updateDevice.get("sbgszxm"));
                }
                //获取设备使用者姓名，以及警种类别
                Map<String,String> result = dao.getUserNameAndSbsyzlb(ComConvert.toString(device.get("sbsyz")));
                //如果是上线，需要更新sbsyzxm
                if(sbyhzt.equals("1")){
                    String userName = result.get("userName");
                    if (userName.isEmpty()) {
                        m_Logger.info("用户状态更新:查询的用户姓名为空(可能ezView人员表没有该" + device.get("sbsyz") + "用户的信息," +
                                "或者对应的名称信息为空");
                    }
                    device.put("sbsyzxm", userName);
                }
                //获取设备使用者类别，即警种
                String sbsyzlb = result.get("sbsyzlb");
                if(!sbsyzlb.isEmpty() && null != sbsyzlb) {
                    device.put("sbsyzlb", sbsyzlb);
                }
                if(dao.updateUserStatus(device)>0) {
                    device.put("sbmc",ComConvert.toString(updateDevice.get("sbmc")));
                    DeviceDbOperate.updatePinYin(device);
                    publishUpdateCache(ComDefine.OPERA_MOD,7,device,ComDefine.CACHE_YDYHZT);
                   }
                }
                else{
                    m_Logger.info("状态更新:当前缓存(设备信息表)无编号" + deviceNum +"的设备");
                }
                break;
            case ComDefine.CACHE_YDSBWZ:
                if(deviceCache.containsKey(deviceNum)){
                //检查数据的正确性
                double lon = ComConvert.toDouble(device.get("sbjd"),0);
                double lat = ComConvert.toDouble(device.get("sbwd"), 0);
                Date time = TimeHelper.StringToDate(ComConvert.toString(device.get("gxsj")));
                if(!ComConvert.IsValidGeometryData(lon, lat)) {
                    m_Logger.info("位置更新:收到设备编号:" + deviceNum + ";经纬度:" + lon + "," + lat +"[不是有效的经纬度]");
                }
                //若是在有效的时间范围内,则更新实时位置信息
                else if(!TimeHelper.isValidGpsTime(time)) {
                    m_Logger.info("位置更新:收到设备编号:" + deviceNum +";更新时间:"+ device.get("gxsj") + "[超出了当前系统设置的有效范围]");
                }
                else {
                    //判断当前时间是否大于当前时间,给出提示
                    if(TimeHelper.isBackwardTime(time)){
                        m_Logger.info("位置更新:收到设备编号:" + deviceNum + ";更新时间:"+ device.get("gxsj") + "[大于当前时间]");
                    }
                    if (dao.updateDevicePostion(device) > 0) {
                        // 发送更新位置消息
                        publishUpdateCache(ComDefine.OPERA_MOD, 7, device, ComDefine.CACHE_YDSBWZ);
                    }
                }
               if(deviceCache.get(deviceNum).containsKey("sbsyz"))
                device.put("sbsyz",deviceCache.get(deviceNum).get("sbsyz"));
                //插入历史位置信息
                 EsGpsHisData.getInstance().insert(device);
                }
                else{
                    m_Logger.info("位置更新:当前缓存(设备信息表)无编号" + deviceNum +"的设备");
                }
                break;
        }

    }
}
