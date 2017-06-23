package common;

import commoncache.ComCache;
import controller.Application;
import dao.DeviceDao;
import model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sync.UpdateDeviceThreadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hejiangbo on 2016/12/7.
 */
public class DeviceDbOperate {

    private static final Logger m_Logger = LoggerFactory.getLogger(DeviceDbOperate.class);                     //日志信息
    private static final DeviceDao dao = Application.myContext.getBean("deviceDao", DeviceDao.class);          //数据库操作
    private static final Map<String,Device> m_DeiceCache = ComCache.getInstance().getDeviceCache();            //设备缓存信息
    private static final UpdateDeviceThreadTask deviceUpdateTask = UpdateDeviceThreadTask.newInstance();       //设备更新线程池

    /***
     * 根据usercode获取username
     * @param userCode
     * @return
     */
    public static String getUserName(String userCode){
        Map map = dao.getUserName(userCode);
        return map ==null? "" : ComConvert.toString(map.get("userName"));
    }


    /**
     * 新增设备接口
     * @param devices
     * @return
     */
    public static void batchInsetInfo(Map<String,Map> devices){
        Map<String,Map>insertMaps = new HashMap<>();
        for(Map.Entry<String,Map> entry : devices.entrySet()){
            String deviceNum = entry.getKey();
              int result = dao.insertDevice(entry.getValue());
              String msg = "失败";
              if(result > 0){
                  msg = "成功";
                  insertMaps.put(deviceNum,entry.getValue());
                  //更新缓存信息
                  ComCache.getInstance().UpdateDeviceCache(insertMaps,ComDefine.OPERA_ADD,ComDefine.CACHE_YDSBXX);
                  insertMaps.clear();
               }
               m_Logger.info("新增设备:" + entry.getValue().get("sbbh") + "设备更新" + msg);
        }
    }

    /***
     * 更新设备接口
     * @param maps
     */
    public static void batchUpdateInfo(Map<String,Map>maps){
        Map<String,Map>insertMaps = new HashMap<>();
        Map<String,Map>updateMaps = new HashMap<>();
        for(Map.Entry<String,Map> entry :maps.entrySet()){
            String deviceNum = entry.getKey();
            //如果在缓存中,则更新
            if(m_DeiceCache.containsKey(deviceNum)) {
                Map map = entry.getValue();
                String sbjd = "";
                String sbwd = "";
                if(map.containsKey("sbjd") && map.containsKey("sbwd")){
                    sbjd =ComConvert.toString(map.get("sbjd"));
                    sbwd =ComConvert.toString(map.get("sbwd"));
                }
                //更新位置信息
                if(!sbjd.isEmpty() && !sbwd.isEmpty()){
                    deviceUpdateTask.addTask(map,ComDefine.CACHE_YDSBWZ);
                }
                //更新设备基本信息
                else{
                    int result = dao.updateDevice(entry.getValue());
                    String msg = "失败";
                    if (result > 0) {
                        msg = "成功";
                        updateMaps.put(deviceNum, entry.getValue());
                        //更新缓存信息
                        ComCache.getInstance().UpdateDeviceCache(updateMaps, ComDefine.OPERA_MOD, ComDefine.CACHE_YDSBXX);
                        updateMaps.clear();
                    }
                    m_Logger.info("更新设备:" + entry.getValue().get("sbbh") + "设备更新" + msg);
                }
            }
            //否则新增设备
            else
            {
                insertMaps.put(deviceNum,entry.getValue());
            }
        }
        if(insertMaps.keySet().size() > 0)
            batchInsetInfo(insertMaps);
    }

    /**
     * 删除设备接口
     * @param maps
     */
    public static void batchDeleteInfo(Map<String,Map>maps){
        Map<String,Map>deleteMaps = new HashMap<>();
        for(Map.Entry<String,Map> entry :maps.entrySet()){
            String deviceNum = entry.getKey();
            //如果在缓存中,则更新
            if (m_DeiceCache.containsKey(deviceNum)) {
                String msg = "失败";
                int result = dao.deleteDevice(deviceNum);
                if(result >0){
                    deleteMaps.put(deviceNum,entry.getValue());
                    msg = "成功";
                    ComCache.getInstance().UpdateDeviceCache(deleteMaps,ComDefine.OPERA_DEL,ComDefine.CACHE_YDSBXX);
                    deleteMaps.clear();
                }
                m_Logger.info("删除设备:" + deviceNum + "设备删除" + msg);
            }
            else
            {
                m_Logger.info("删除设备:"+ deviceNum + "设备不在缓存中");
            }
        }
    }

    /**
     * 更新设备拼音信息
     */
    public static void updatePinYin(Map updateMap){
        Map pinyinMap = new HashMap<String,String>();
        pinyinMap.put("sbbh",ComConvert.toString(updateMap.get("sbbh")));
        String sbmc = ComConvert.toString(updateMap.get("sbmc"));
        String sbsyz = ComConvert.toString(updateMap.get("sbsyz"));
        String sbsyzxm = ComConvert.toString(updateMap.get("sbsyzxm"));
        String pinyin = "";
        if(!sbsyz.isEmpty()){
            pinyin = sbsyz;
        }
        if(!sbsyzxm.isEmpty())
        {
            if(pinyin.isEmpty()) {
                pinyin = sbsyzxm;
            }
            else{
                pinyin +="," + sbsyzxm;
            }
        }
        if(!sbmc.isEmpty()){
            if(pinyin.isEmpty()) {
                pinyin = sbmc;
            }
            else{
                pinyin +="," + sbmc;
            }
        }
        pinyinMap.put("py", Pinyin4jUtil.convertToSpellAll(pinyin,4000));
        dao.updateDevice(pinyinMap);

    }

}
