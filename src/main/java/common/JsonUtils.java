package common;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Device;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * Created by hejiangbo on 2016/12/8.
 */
public class JsonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * bean转json
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> String bean2Json(T bean) {
        try {
            return objectMapper.writeValueAsString(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * map转json
     * @param map
     * @return
     */
    public static String map2Json(Map map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * list转json
     * @param list
     * @return
     */
    public static String list2Json(List list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * json转bean
     * @param json
     * @param beanClass
     * @param <T>
     * @return
     */
    public static <T> T json2Bean(String json, Class<T> beanClass) {
        try {
            return objectMapper.readValue(json, beanClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json转list
     * @param json
     * @param beanClass
     * @param <T>
     * @return
     */
    public static <T> List<T> json2List(String json, Class<T> beanClass) {
        try {

            return (List<T>)objectMapper.readValue(json, getCollectionType(List.class, beanClass));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json转map
     * @param json
     * @return
     */
    public static Map json2Map(String json) {
        try {

            return (Map)objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /***
     * json转map集合
     * @param json
     * @return
     */
    public static Map<String,Map> json2MapDic(String json,String markKey){
        try {
            JSONArray jsonArr = JSONArray.fromObject(json);
            Map  dics =new HashMap<String,Map<String,String>>();
            for (int i = 0; i < jsonArr.size(); i++) {
                Map map = new LinkedHashMap<String, String>();
                JSONObject obj = JSONObject.fromObject(jsonArr.get(i));
                Iterator it = obj.keys();
                while (it.hasNext()) {
                    String key = String.valueOf(it.next());
                    String value = (String) obj.get(key);
                    map.put(key, value);
                }
                if(map.containsKey(markKey)) {
                    String markValue = String.valueOf(map.get(markKey));
                    if (!markValue.isEmpty() && !dics.containsKey(markValue))
                        dics.put(markValue, map);
                }
            }
            return dics;
        }

        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String [] args){
        Device device1 = new Device();
        device1.setSbbh("123");
        device1.setSbmc("测试123");
        Device device2 = new Device();
        device2.setSbbh("456");
        device2.setSbmc("测试456");
        List<Device> deviceList = new ArrayList<Device>();
        deviceList.add(device1);
        deviceList.add(device2);
        String json = list2Json(deviceList);

        //String json = "[{\"sbbh\":\"123\",\"sbmc\":\"测试123\",\"sbbm\":\"\",\"sblx\":\"\",\"sbdl\":\"\",\"sbxh\":\"\",\"sbspzt\":\"\",\"sbyhzt\":\"\",\"sbsmzt\":\"\",\"spsbbh\":\"\",\"sbms\":\"\",\"ssxq\":\"\",\"csmc\":\"\",\"ipdz\":\"\",\"sbsyz\":\"\",\"sbgsz\":\"\",\"ydhm\":\"\",\"rjbb\":\"\",\"sbjd\":\"\",\"sbwd\":\"\",\"ktsj\":\"\",\"gxsj\":\"\",\"sd\":\"\",\"gd\":\"\",\"jd\":\"\",\"fx\":\"\",\"py\":\"\",\"ylzd1\":\"\",\"ylzd2\":\"\",\"ylzd3\":\"\",\"sbsyzxm\":\"\",\"sbsyzdw\":\"\",\"sbgszxm\":\"\",\"syzjh\":\"\",\"syzsjh\":\"\",\"syzptzh\":\"\"}]";

        Map map1 = json2MapDic(json,"sbbh");

    }


}
