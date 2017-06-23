package common;

import controller.Application;
import dao.UnitDao;
import dao.UserDao;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.util.*;

/**
 * Created by tanhuizhen on 2017/4/25.
 */
public class VcsUtils {
    private  static final Logger m_Logger = LoggerFactory.getLogger(VcsUtils.class);
    public void initialize() {
        synchronizedOrg();
        synchronizedUsers();
        RedisChannel();


    }

    private static String MapGetString(Map map, String key) {
        if (!map.containsKey(key) || map.get(key) == null) {
            return "";
        }
        return map.get(key).toString();
    }

    private Map<String, Object> NewOrgMap(String[] params) {
        Map<String, Object> result = new HashMap<String, Object>();
        int i =0;
        result.put("xh", params[i++]);
        result.put("dwbh", params[i++]);
        result.put("dwmc", params[i++]);
        result.put("dwdz", params[i++]);
        result.put("lxdh", params[i++]);
        result.put("dwjb", params[i++]);
        result.put("dwms", params[i++]);
        result.put("sfxq", params[i++]);
        result.put("lsgx", params[i++]);
        result.put("lxr", params[i++]);
        result.put("ssxq", params[i++]);
        result.put("dwpy", params[i++]);
        result.put("dwnbbm", params[i++]);
        return result;
    }

    private Map<String, Object> NewUserMap(String[] params){
        Map<String, Object> result = new HashMap<String, Object>();
        int i =0;
        result.put("xh", params[i++]);
        result.put("dlm", params[i++]);
        result.put("dlmm", params[i++]);
        result.put("jyxm", params[i++]);
        result.put("lsdw", params[i++]);
        result.put("lxdh", params[i++]);
        result.put("yhzt", params[i++]);
        result.put("yhzcsj", params[i++]);
        result.put("yhyxq", params[i++]);
        result.put("jh", params[i++]);
        result.put("jylb", params[i++]);
        result.put("zw", params[i++]);
        result.put("sfzh", params[i++]);
        result.put("sjhm", params[i++]);
        result.put("yhms", params[i++]);
        result.put("rypy", params[i++]);
        result.put("ywzy1", params[i++]);
        result.put("ywzy2", params[i++]);
        result.put("spls", params[i++]);
        return result;
    }

    private Map<String, Object> NewBodyMap(String p1){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("",p1);
        return result;
    }

    /**
     * 同步单位信息
     */
    private void synchronizedOrg() {
        UnitDao dao = Application.myContext.getBean("unitDao", UnitDao.class);
        List<Map<String, String>> result_select = dao.getUnit();
        Map<String, Object> newBodyMap = NewBodyMap("");
        String request_result = HttpHelper.sendGet(Application.getAppConfig().getProperty("vcs.server.url") + "/rest/unit/listAll", newBodyMap);
        Map resultMap = JSONObject.fromObject(request_result);
        List data = JSONArray.fromObject(resultMap.get("data"));
        List<String> deleteList = new ArrayList<>();
        Set<String> select_set = new HashSet<String>();
        for (Map map :result_select ){
            select_set.add(MapGetString(map, "DWBH"));
        }
        for (Map map :(List<Map>)data){
            if(!select_set.contains(MapGetString(map, "dwbh"))){
                deleteList.add(MapGetString(map, "dwbh"));
            }
        }
        //HttpHelper.sendPost(Application.getAppConfig().getProperty("vcs.server.url") + "/rest/unit/deleteBatch", deleteList);
        m_Logger.info("此次同步删除单位数据:" + deleteList.size() + "条");
        List<Map> Orgs = new ArrayList<Map>();
        for(Map map : result_select) {
            String xh = MapGetString(map,"XH");
            String dwbh = MapGetString(map,"DWBH");
            String dwmc = MapGetString(map,"DWMC");
            String dwdz = MapGetString(map,"DWDZ");
            String lxdh = MapGetString(map,"LXDH");
            String dwjb = MapGetString(map,"DWJB");
            String dwms = MapGetString(map,"DWMS");
            String sfxq = MapGetString(map,"SFXQ");
            String lsgx = MapGetString(map,"LSGX");
            String lxr = MapGetString(map,"LXR");
            String ssxq = MapGetString(map,"SSXQ");
            String dwpy = MapGetString(map,"DWPY");
            String dwnbbm = MapGetString(map,"DWNBBM");
            Map<String, Object> newOrgMap = NewOrgMap(new String[]{xh, dwbh, dwmc, dwdz,lxdh,dwjb,dwms,sfxq,lsgx,lxr,ssxq,dwpy,dwnbbm});
            Orgs.add(newOrgMap);
        }
         HttpHelper.sendPost(Application.getAppConfig().getProperty("vcs.server.url") + "/rest/unit/addBatch", Orgs);
        m_Logger.info("此次同步新增或修改单位数据:" + Orgs.size() + "条");
    }

    /**
     * 同步人员信息
     */
    private void synchronizedUsers() {
        UserDao dao = Application.myContext.getBean("userDao", UserDao.class);
        List<Map<String, String>> result_select = dao.getUser();
        Map<String, Object> newBodyMap = NewBodyMap("");
        String request_result = HttpHelper.sendGet(Application.getAppConfig().getProperty("vcs.server.url") + "/rest/user/listAll", newBodyMap);
        Map resultMap = JSONObject.fromObject(request_result);
        List data = JSONArray.fromObject(resultMap.get("data"));
        List<String> deleteList = new ArrayList<>();
        Set<String> select_set = new HashSet<String>();
        for (Map map :result_select ){
            select_set.add(MapGetString(map, "DLM"));
        }
        for (Map map :(List<Map>)data){
            if(!select_set.contains(MapGetString(map, "dlm"))){
                deleteList.add(MapGetString(map, "dlm"));
            }
        }
        HttpHelper.sendPost(Application.getAppConfig().getProperty("vcs.server.url") + "/rest/user/deleteBatch", deleteList);
        m_Logger.info("此次同步删除人员数据:" + deleteList.size() + "条");
        List<Map> Users = new ArrayList<Map>();
        for(Map map : result_select){
            String xh = MapGetString(map,"XH");
            String dlm = MapGetString(map,"DLM");
            String dlmm = MapGetString(map,"DLMM");
            String jyxm = MapGetString(map,"JYXM");
            String lsdw = MapGetString(map,"LSDW");
            String lxdh = MapGetString(map,"LXDH");
            String yhzt = MapGetString(map,"YHZT");
            String yhzcsj = MapGetString(map,"YHZCSJ");
            String yhyxq = MapGetString(map,"YHYXQ");
            String jh = MapGetString(map,"JH");
            String jylb = MapGetString(map,"JYLB");
            String zw = MapGetString(map,"ZW");
            String sfzh = MapGetString(map,"SFZH");
            String sjhm = MapGetString(map,"SJHM");
            String yhms = MapGetString(map,"YHMS");
            String rypy = MapGetString(map,"RYPY");
            String ywzy1 = MapGetString(map,"YWZY1");
            String ywzy2 = MapGetString(map,"YWZY2");
            String spls = MapGetString(map,"SPLS");
            Map<String, Object> newUserMap =NewUserMap(new String[]{xh,dlm,dlmm,jyxm,lsdw,lxdh,yhzt,yhzcsj,yhyxq,jh,jylb,zw,sfzh,sjhm,yhms,rypy,ywzy1,ywzy2,spls});
            Users.add(newUserMap);
        }
        HttpHelper.sendPost(Application.getAppConfig().getProperty("vcs.server.url") + "/rest/user/addBatch", Users);
        m_Logger.info("此次同步新增或修改人员数据:" + Users.size() + "条");
    }

    /**
     * ezview客户端监听消息
     */
    private void RedisChannel() {
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
                if(handleType.equals("1")||handleType.equals("2")){
                    Map<String,String> data_map = new HashMap<String,String>();
                    List<Map> data_list = new ArrayList<Map>();
                    data_map.put("xh",content.get("xh").toString());
                    data_map.put("dlm",content.get("dlm").toString());
                    data_map.put("dlmm",content.get("dlmm").toString());
                    data_map.put("jyxm",content.get("jyxm").toString());
                    data_map.put("lsdw",content.get("lsdw").toString());
                    data_map.put("lxdh",content.get("lxdh").toString());
                    data_map.put("yhzt",content.get("yhzt").toString());
                    data_map.put("yhzcsj",content.get("yhzcsj").toString());
                    data_map.put("yhyxq",content.get("yhyxq").toString());
                    data_map.put("jh",content.get("jh").toString());
                    data_map.put("jylb",content.get("jylb").toString());
                    data_map.put("zw",content.get("zw").toString());
                    data_map.put("sfzh",content.get("sfzh").toString());
                    data_map.put("sjhm",content.get("sjhm").toString());
                    data_map.put("yhms",content.get("yhms").toString());
                    data_map.put("rypy",content.get("rypy").toString());
                    data_map.put("ywzy1",content.get("ywzy1").toString());
                    data_map.put("ywzy2",content.get("ywzy2").toString());
                    data_map.put("spls",content.get("spls").toString());
                    data_list.add(data_map);
                    HttpHelper.sendPost(Application.getAppConfig().getProperty("vcs.server.url") + "/rest/user/addBatch", data_list);
                    m_Logger.info("此次新增或修改人员数据:" + content);
                }else if(handleType.equals("3")){
                    List<String> data_list = new ArrayList<String>();
                    data_list.add(content.get("dlm").toString());
                    HttpHelper.sendPost(Application.getAppConfig().getProperty("vcs.server.url") + "/rest/user/deleteBatch",data_list);
                    m_Logger.info("此次删除人员数据:" + content.get("dlm").toString());
                }
            } else if (channel.equals("CACHE.DWXX")) {
                if(handleType.equals("1")||handleType.equals("2")){
                    Map<String,String> data_map = new HashMap<String,String>();
                    List<Map> data_list = new ArrayList<Map>();
                    data_map.put("xh",content.get("xh").toString());
                    data_map.put("dwbh",content.get("dwbh").toString());
                    data_map.put("dwmc",content.get("dwmc").toString());
                    data_map.put("dwdz",content.get("dwdz").toString());
                    data_map.put("lxdh",content.get("lxdh").toString());
                    data_map.put("dwjb",content.get("dwjb").toString());
                    data_map.put("dwms",content.get("dwms").toString());
                    data_map.put("sfxq",content.get("sfxq").toString());
                    data_map.put("lsgx",content.get("lsgx").toString());
                    data_map.put("lxr",content.get("lxr").toString());
                    data_map.put("ssxq",content.get("ssxq").toString());
                    data_map.put("dwpy",content.get("dwpy").toString());
                    data_map.put("dwnbbm",content.get("dwnbbm").toString());
                    data_list.add(data_map);
                    HttpHelper.sendPost(Application.getAppConfig().getProperty("vcs.server.url") + "/rest/unit/addBatch", data_list);
                    m_Logger.info("此次新增或修改单位数据:" + content);
                }else if(handleType.equals("3")){
                    List<String> data_list = new ArrayList<String>();
                    data_list.add(content.get("dwbh").toString());
                    HttpHelper.sendPost(Application.getAppConfig().getProperty("vcs.server.url") + "/rest/unit/deleteBatch",data_list);
                    m_Logger.info("此次删除单位数据:" + content.get("dwbh").toString());
                }
            }
        }
    }


}
