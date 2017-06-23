package com.keda.patrol.service;

import com.keda.patrol.dao.PatrolConfigMapper;
import com.keda.patrol.dao.PatrolRtTaskMapper;
import com.keda.patrol.model.PatrolConfig;
import com.keda.patrol.model.PatrolRtTask;
import common.ComConvert;
import common.ComDefine;
import common.RedisUtil;
import commoncache.ComCache;
import controller.Application;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by hejiangbo on 2017/2/8.
 */
@Service
public class PatrolLineManager {

    private RedisUtil m_RedisUtil = RedisUtil.getInstance();                                            //redis工具类
    private PatrolAlarmDetectionTask patrolAlarmDetectionTask = PatrolAlarmDetectionTask.getInstance(); //巡逻报警检测任务



    public PatrolLineManager(){
    }

    /**
     * 初始化
     */
    public void initialize(){
        getAllRtTask();
        subscribeRedis();
    }

    /**
     * 初始化巡逻任务列表
     */
    private void getAllRtTask(){
        PatrolRtTaskMapper patrolRtTaskMapper = Application.myContext.getBean(PatrolRtTaskMapper.class);
        List<PatrolRtTask> patrolRtTaskList = patrolRtTaskMapper.selectAll();
        for(int i = 0;i < patrolRtTaskList.size();i++){
            String taskId = ComConvert.toString(patrolRtTaskList.get(i).getRwbh());
            addTaskToQueue(taskId);
        }
    }


    /**
     * 添加新任务至列表
     */
    private void addTaskToQueue(String taskId){
        Map<String, Map> patrolTaskList = PatrolLineTask.getIntance().getPatrolTaskList();
        Map<String, String> patrolTask = new HashMap<>();
        PatrolRtTaskMapper patrolRtTaskMapper = Application.myContext.getBean(PatrolRtTaskMapper.class);
        PatrolRtTask patrolRtTask = patrolRtTaskMapper.selectByPrimaryKey(taskId);
        if (null == patrolRtTask)
            return;
        //巡逻编号
        String patrolNumber = ComConvert.toString(patrolRtTask.getXlbh());
        //巡逻设备编号
        String deviceNumber = ComConvert.toString(patrolRtTask.getSbbh());
        //巡逻配置
        PatrolConfigMapper patrolConfigMapper = Application.myContext.getBean(PatrolConfigMapper.class);

        PatrolConfig patrolConfig = patrolConfigMapper.selectByPrimaryKey(patrolNumber);
        if (null == patrolConfig)
            return;
        //巡逻路线
        String patrolLine = ComConvert.toString(patrolConfig.getXllx());
        //巡逻路线偏移距离
        String offsetDistance = ComConvert.toString(patrolConfig.getPyjl());
        //巡逻路线偏移时长
        String offsetDuration = ComConvert.toString(patrolConfig.getPysc());

        String[] deviceNumbers = deviceNumber.split(",");
        for (int i = 0; i < deviceNumbers.length; i++) {
            String sigleDeviceNumber = deviceNumbers[i];
            patrolTask.put("xlbh", patrolNumber);
            patrolTask.put("sbbh", sigleDeviceNumber);
            patrolTask.put("xllx", patrolLine);
            patrolTask.put("pyjl", offsetDistance);
            patrolTask.put("pysc", offsetDuration);
            patrolTask.put("rwbh", taskId);
            //默认没有偏离预定轨迹
            patrolTask.put("sfpl", "0"); //是否偏离
            //标示是否报过警
            patrolTask.put("bjzt", "0"); //是否报警
            patrolTaskList.put(sigleDeviceNumber, patrolTask);
        }
    }

    /**
     * 订阅
     */
    private void subscribeRedis(){
        m_RedisUtil.subscribe(new JedisPubSub() {
            public void onMessage(String channel, String message) {
                JSONObject jsonObject = JSONObject.fromObject(message);
                int operaType = ComConvert.toInteger(jsonObject.get("operaType"), 0);
                JSONObject operaInfo = jsonObject.getJSONObject("operaInfo");
                JSONObject contentJson = operaInfo.getJSONObject("content");
                Map<String, Map> patrolTaskList = PatrolLineTask.getIntance().getPatrolTaskList();
                if (!message.isEmpty()) {
                    if(channel.equals(ComDefine.PATROL_TASK)) {
                        String taskId = ComConvert.toString(contentJson.getString("rwbh"));
                        switch (operaType) {
                            //新增巡逻任务
                            case ComDefine.OPERA_ADD:
                                addTaskToQueue(taskId);
                                break;
                            //删除巡逻任务
                            case ComDefine.OPERA_DEL:
                                Iterator iterator = patrolTaskList.keySet().iterator();
                                while (iterator.hasNext()) {
                                    if (patrolTaskList.get(iterator.next()).get("rwbh").equals(taskId)) {
                                        iterator.remove();
                                    }
                                }
                                break;
                        }
                    }
                    if(channel.equals(ComDefine.CACHE_YDSBWZ)){
                        String deviceNum = ComConvert.toString(contentJson.getString("sbbh"));
                        Map<String,String> device = new HashMap();
                        Iterator keys = contentJson.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            device.put(key, contentJson.getString(key));
                        }
                        Map<String,Map> deviceCache = ComCache.getInstance().getDeviceCache();
                        device.put("sbsyz", ComConvert.toString(deviceCache.get(deviceNum).get("sbsyz")));
                        patrolAlarmDetectionTask.addTask(device);
                    }
                }
            }
        }, new String[]{ComDefine.PATROL_TASK,ComDefine.CACHE_YDSBWZ});

    }
}
