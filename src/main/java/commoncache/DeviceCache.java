package commoncache;

import com.keda.patrol.service.PatrolLineTask;
import common.ComConvert;
import common.ComDefine;
import common.JsonUtils;
import common.RedisUtil;
import net.sf.json.JSONObject;
import redis.clients.jedis.JedisPubSub;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hejiangbo on 2016/11/8.
 */
public class DeviceCache {

    /**
     * 设备信息缓存
     */
    private Map m_DeviceMap = new HashMap<String, Map>();

    /**
     * 获取已有设备缓存
     * @return
     */
    public  Map getDeviceCache(){
        return  m_DeviceMap;
    }

    /**
     * 初始化设备相关信息
     */
    public void initDevice(Connection connection){
        // 初始化设备缓存
        initCache(connection);
        // 订阅设备更新通知
        subscribeRedis();


    }

    /**
     *订阅redis设备更新信息
     */
    private void subscribeRedis(){
        RedisUtil.getInstance().subscribe(new JedisPubSub() {
            public void onMessage(String channel, String message) {
                if (!message.isEmpty()) {
                JSONObject jsonObject = JSONObject.fromObject(message);
                JSONObject operaInfo = jsonObject.getJSONObject("operaInfo");
                JSONObject contentJson = operaInfo.getJSONObject("content");
                    String deviceNum = ComConvert.toString(contentJson.getString("sbbh"));
                    Map<String, Map> deviceCahce = ComCache.getInstance().getDeviceCache();
                    switch (channel) {
                        // 设备信息更新
                        case ComDefine.CACHE_YDSBXX:
                            int operaType = ComConvert.toInteger(jsonObject.get("operaType"), 0);
                            // 新增设备
                            if (operaType == ComDefine.OPERA_ADD) {
                                if (!deviceCahce.containsKey(deviceNum)) {
                                    Map<String, String> device = new HashMap<String, String>();
                                    updateMap(device,contentJson);
                                    deviceCahce.put(deviceNum, device);
                                }
                            }
                            if (deviceCahce.containsKey(deviceNum)) {
                                // 更新设备
                                if (operaType == ComDefine.OPERA_MOD) {
                                    updateMap(deviceCahce.get(deviceNum),contentJson);
                                }
                                // 删除设备
                                if (operaType == ComDefine.OPERA_DEL) {
                                    deviceCahce.remove(deviceNum);
                                }
                            }
                            break;
                        // 用户状态更新
                        case ComDefine.CACHE_YDYHZT:
                            if (deviceCahce.containsKey(deviceNum)) {
                                updateMap(deviceCahce.get(deviceNum),contentJson);
                            }
                            break;
                    }
                }
            }
        }, new String[]{ComDefine.CACHE_YDSBXX, ComDefine.CACHE_YDYHZT});
    }

    /**
     * 要更新的设备map
     * @param deviceMap
     */
    private void updateMap(Map<String,String> deviceMap, JSONObject jsonObject){
        Iterator keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            deviceMap.put(key, jsonObject.getString(key));
        }
    }



    /**
     * 获取移动设备数据
     */
    private void initCache(Connection connection){

        m_DeviceMap.clear();

        String sqlStatement = "SELECT "
                + "			a.SBBH			  AS sbbh, "
                + "         a.SBMC            AS sbmc, "
                + "         7                 AS sbdl,"
                + "         a.SBLX            AS sblx, "
                + "         a.SBXH            AS sbxh, "
                + "         a.SPSBBH          AS spsbbh, "
                + "         a.SSXQ            AS ssxq, "
                + "         a.SBYHZT          AS sbyhzt,"
                + "         a.SBSYZ           AS sbsyz,"
                + "         b.JYXM            AS sbsyzxm,"
                + "         a.SBGSZ           AS sbgsz,"
                + "         d.JYXM            AS sbgszxm,"
                + "         a.GXSJ            AS gxsj,"
                + "         a.SBJD            AS sbjd,"
                + "         a.SBWD            AS sbwd,"
                + "         a.CSMC            AS csmc, "
                + "         a.IPDZ            AS ipdz, "
                + "         a.YDHM            AS ydhm, "
                + "         a.RJBB            AS rjbb, "
                + "         a.KTSJ            AS ktsj  "
                + "FROM"
                + "			B_YDSB_SBXX	a LEFT  JOIN B_RS_RY b " +
                "   on a.sbsyz = b.dlm  LEFT JOIN B_RS_RY d " +
                "   on a.sbgsz = d.dlm ";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
             preparedStatement = connection.prepareStatement(sqlStatement);
             resultSet = preparedStatement.executeQuery();
             ResultSetMetaData metaData = resultSet.getMetaData();
             while (resultSet.next()) {
                Map map = new LinkedHashMap<String,String>();
                for (int i = 1;i <= metaData.getColumnCount();i++){
                    map.put(metaData.getColumnName(i).toLowerCase(),ComConvert.toString(resultSet.getObject(i)));
                }
                String deviceNum = ComConvert.toString(resultSet.getObject("sbbh"));
                if(!m_DeviceMap.containsKey(deviceNum))
                m_DeviceMap.put(deviceNum, map);
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
                    connection.close();
                if(null != resultSet)
                resultSet.close();
                if(null != preparedStatement)
                preparedStatement.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    }
