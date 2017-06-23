package com.keda.patrol.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hejiangbo on 2017/2/13.
 */
public class PatrolLineTask {

    private static PatrolLineTask patrolLineTask = new PatrolLineTask();                      //巡逻任务单例
    private Map<String,Map> m_PatrolTaskList = new HashMap<>();                               //巡逻任务列表


    public static PatrolLineTask getIntance(){
        return patrolLineTask;
    }

    /**
     * 获取巡逻任务列表
     * @return
     */
    public Map<String,Map> getPatrolTaskList(){
       return patrolLineTask.m_PatrolTaskList;
    }

    /**
     * 设置巡逻任务列表
     * @return
     */
    public void addPatrolTask(String deviceNumver,Map<String,Object>patrolMap){
        if(!m_PatrolTaskList.containsKey(deviceNumver)){
            m_PatrolTaskList.put(deviceNumver,patrolMap);
        }
    }

    /**
     * 移除巡逻任务
     * @param deviceNumver
     */
    public void removePatrolTask(String deviceNumver){
        if(m_PatrolTaskList.containsKey(deviceNumver)){
            m_PatrolTaskList.remove(deviceNumver);
        }
    }

}
